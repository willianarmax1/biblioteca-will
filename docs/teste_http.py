"""Integração HTTP sem dependências externas. Execute com servidor local ativo.
Uso: python3 docs/teste_http.py http://localhost:8080
Cria registros próprios e os remove ao concluir. Não altera os filmes existentes.
"""
import sys, urllib.request, urllib.parse, urllib.error, http.cookiejar, re, json
base=(sys.argv[1] if len(sys.argv)>1 else 'http://localhost:8080').rstrip('/')
jar=http.cookiejar.CookieJar(); client=urllib.request.build_opener(urllib.request.HTTPCookieProcessor(jar))
checks=[]
def req(path='/filmes',data=None):
    try:
        with client.open(base+path,urllib.parse.urlencode(data).encode() if data is not None else None,timeout=20) as r:
            return r.status,r.read().decode(),r.geturl()
    except urllib.error.HTTPError as e:return e.code,e.read().decode(),e.geturl()
def check(name,test):
    assert test,name
    checks.append(name)
status,home,_=req();check('JSP de listagem compilada e respondendo',status==200 and 'Minha coleção' in home)
_,form,_=req('/filmes?acao=novo');token=re.search('name="csrf" value="([^"]+)"',form).group(1)
check('Formulário e token CSRF',bool(token))
record={'acao':'salvar','csrf':token,'titulo':'HTTP teste <script>alert(1)</script>','diretor':'Direção Teste','ano':'2024','genero':'Drama','sinopse':'Sinopse de integração.','avaliacao':'4'}
status,detail,url=req(data=record);fid=re.search(r'id=(\d+)',url).group(1)
try:
    check('Cadastro e redirecionamento para detalhes',status==200 and 'adicionado à sua coleção' in detail)
    check('HTML do usuário escapado', '&lt;script&gt;alert(1)&lt;/script&gt;' in detail and '<script>alert(1)</script>' not in detail)
    status,edit,_=req('/filmes?acao=editar&id='+fid);check('JSP de edição',status==200 and 'Editar filme' in edit)
    status,result,_=req('/filmes?q=Dire%C3%A7%C3%A3o+Teste&genero=Drama');check('Busca por diretor com filtro',status==200 and 'HTTP teste' in result)
    status,result,_=req('/filmes?q=%27+OR+1%3D1+--');check('Busca SQL maliciosa sem retorno indevido',status==200 and 'Nenhum filme por aqui' in result)
    invalid=record|{'ano':'letras'};status,body,_=req(data=invalid);check('Ano inválido retorna 422',status==422 and 'número inteiro' in body)
    status,_,_=req(data=record|{'csrf':'incorreto'});check('Token incorreto bloqueado com 403',status==403)
    status,_,_=req(data={'acao':'excluir','id':fid});check('Exclusão sem token bloqueada',status==403)
    status,body,_=req('/filmes?acao=excluir&id='+fid);check('Confirmação não exclui por GET',status==200 and 'Excluir este filme?' in body)
    status,_,_=req('/filmes?acao=detalhes&id='+fid);check('Registro preservado após confirmação GET',status==200)
    status,body,_=req(data=record|{'id':fid,'titulo':'HTTP teste editado'});check('Edição persistida',status==200 and 'HTTP teste editado' in body and 'Alterações salvas' in body)
    status,body,_=req('/filmes?acao=sobre');check('JSP sobre',status==200 and 'Sobre o projeto acadêmico' in body)
    status,body,_=req('/filmes?acao=detalhes&id=999999');check('Registro ausente retorna 404',status==404)
    status,body,_=req('/filmes?acao=detalhes&id=abc');check('ID inválido retorna 400',status==400)
    status,_,_=req('/nao-existe');check('Página ausente retorna 404',status==404)
    status,css,_=req('/assets/style.css');check('CSS disponível',status==200 and '@media' in css)
finally:
    status,body,_=req(data={'acao':'excluir','id':fid,'csrf':token})
    check('Exclusão confirmada',status==200 and 'Filme excluído' in body)
status,_,_=req('/filmes?acao=detalhes&id='+fid);check('Registro excluído não é localizado',status==404)
print(json.dumps({'aprovados':len(checks),'verificacoes':checks},ensure_ascii=False,indent=2))
