package br.edu.cinearquivo.web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import br.edu.cinearquivo.service.FilmeService;
import br.edu.cinearquivo.model.Filme;

/** Controlador MVC: recebe HTTP, chama o serviço e encaminha para as JSPs. */
public class FilmeServlet extends HttpServlet {
    private FilmeService service;
    @Override public void init(){service=(FilmeService)getServletContext().getAttribute("service");}
    private String value(HttpServletRequest r,String key){String v=r.getParameter(key);return v==null?"":v.strip();}
    private long id(HttpServletRequest r){long n=Long.parseLong(value(r,"id"));if(n<=0)throw new IllegalArgumentException();return n;}
    private void view(HttpServletRequest r,HttpServletResponse s,String name)throws ServletException,IOException{r.getRequestDispatcher("/WEB-INF/views/"+name+".jsp").forward(r,s);}
    private void erro(HttpServletRequest r,HttpServletResponse s,int status,String message)throws ServletException,IOException{s.setStatus(status);r.setAttribute("mensagem",message);view(r,s,"erro");}
    /** GET só consulta dados. Nenhuma exclusão é permitida nesta operação. */
    @Override protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
        r.setCharacterEncoding("UTF-8");s.setContentType("text/html;charset=UTF-8");
        if(r.getSession().getAttribute("csrf")==null)r.getSession().setAttribute("csrf",UUID.randomUUID().toString());
        r.setAttribute("generos",FilmeService.GENEROS);
        Object flash=r.getSession().getAttribute("flash");if(flash!=null){r.setAttribute("sucesso",flash);r.getSession().removeAttribute("flash");}
        String action=value(r,"acao");
        try{
            switch(action){
                case "novo" -> {r.setAttribute("filme",new Filme());view(r,s,"formulario");}
                case "detalhes", "editar", "excluir" -> {
                    Optional<Filme> f=service.encontrar(id(r));
                    if(f.isEmpty()){erro(r,s,404,"Este filme não está no catálogo.");return;}
                    r.setAttribute("filme",f.get());view(r,s,switch(action){case "editar"->"formulario";case "excluir"->"excluir";default->"detalhes";});
                }
                case "sobre" -> view(r,s,"sobre");
                case "" -> {
                    String q=value(r,"q"),g=value(r,"genero"),o=value(r,"ordem");
                    if(q.length()>150){erro(r,s,400,"A busca deve ter até 150 caracteres.");return;}
                    r.setAttribute("filmes",service.buscar(q,g,o));r.setAttribute("total",service.buscar("","","").size());
                    r.setAttribute("q",q);r.setAttribute("genero",g);r.setAttribute("ordem",o);view(r,s,"lista");
                }
                default -> erro(r,s,404,"Página não encontrada.");
            }
        }catch(IllegalArgumentException ex){erro(r,s,400,"Identificador de filme inválido.");}
        catch(SQLException ex){getServletContext().log("Falha ao consultar catálogo",ex);erro(r,s,500,"Não foi possível consultar o catálogo. Tente novamente.");}
    }
    /** POST valida CSRF e dados, grava e redireciona para evitar reenvio do formulário. */
    @Override protected void doPost(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
        r.setCharacterEncoding("UTF-8");s.setContentType("text/html;charset=UTF-8");
        HttpSession session=r.getSession(false);
        if(session==null||!Objects.equals(session.getAttribute("csrf"),r.getParameter("csrf"))||r.getParameter("csrf")==null){erro(r,s,403,"Sua sessão expirou. Volte ao catálogo e tente novamente.");return;}
        try{
            String action=value(r,"acao");
            if(action.equals("excluir")){
                if(!service.excluir(id(r))){erro(r,s,404,"O filme já foi removido ou não existe.");return;}
                session.setAttribute("flash","Filme excluído do catálogo.");s.sendRedirect(r.getContextPath()+"/filmes");return;
            }
            if(!action.equals("salvar")){erro(r,s,400,"Operação inválida.");return;}
            Filme f=new Filme();
            if(!value(r,"id").isEmpty())f.setId(id(r));
            f.setTitulo(value(r,"titulo"));f.setDiretor(value(r,"diretor"));f.setGenero(value(r,"genero"));f.setSinopse(value(r,"sinopse"));
            List<String> errors=new ArrayList<>();
            try{f.setAno(Integer.parseInt(value(r,"ano")));}catch(NumberFormatException ex){f.setAno(0);errors.add("O ano precisa ser um número inteiro.");}
            try{f.setAvaliacao(Integer.parseInt(value(r,"avaliacao")));}catch(NumberFormatException ex){f.setAvaliacao(-1);errors.add("Selecione uma avaliação válida.");}
            errors.addAll(service.validar(f));
            if(!errors.isEmpty()){s.setStatus(422);r.setAttribute("erros",errors);r.setAttribute("filme",f);r.setAttribute("generos",FilmeService.GENEROS);view(r,s,"formulario");return;}
            boolean novo=f.getId()==null;
            if(!service.salvar(f)){erro(r,s,404,"O filme não existe mais. Volte ao catálogo.");return;}
            session.setAttribute("flash",novo?"Filme adicionado à sua coleção!":"Alterações salvas com sucesso.");
            s.sendRedirect(r.getContextPath()+"/filmes?acao=detalhes&id="+f.getId());
        }catch(IllegalArgumentException ex){erro(r,s,400,"Identificador de filme inválido.");}
        catch(SQLException ex){getServletContext().log("Falha ao gravar catálogo",ex);erro(r,s,500,"Não foi possível salvar a alteração. Seus dados anteriores foram preservados.");}
    }
}
