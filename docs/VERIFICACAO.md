# Revisão de 28/10/2026

- Compilação das classes Java com Eclipse JDT 3.33.0 incluído nas dependências: aprovada.
- 20 verificações HTTP no Tomcat com banco de teste separado: aprovadas. Detalhes em resultados-http.json.
- Interface desktop revisada em navegador: página inicial, grade, detalhes, cadastro, edição, busca e confirmação de exclusão.
- Sete capturas reais JPEG em prints/.
- Relatório de 20 páginas renderizado e revisado, com sumário correspondente.
- Testes JUnit originais: sete aprovados na versão anterior; resultado original incluído. Não reexecutados nesta revisão por indisponibilidade do ambiente Maven/JDK completo.
- O Maven e o comando javac não estavam disponíveis para compilação convencional completa neste ambiente; a distribuição foi recompilada com JDT e o WAR empacotado a partir das classes e páginas atuais.
- Não foi validada execução em dispositivos móveis físicos, Windows/macOS, carga concorrente ou Tomcat externo. O usuário já iniciou a versão anterior em Windows; isso não substitui o teste desta distribuição atualizada naquele sistema.

Para repetir localmente com JDK e Maven: mvn clean package. Para os testes HTTP, com o servidor aberto: python docs/teste_http.py http://localhost:8080.

## Atualização das capas

Pôsteres dos oito filmes vinculados por título e ano, com fallback neutro para títulos não reconhecidos. Os 20 cenários HTTP foram reexecutados e aprovados. As oito capas distintas e seus arquivos foram verificados por HTTP e no navegador. Não houve alteração no esquema do banco.

## Tema escuro e ficha de filme

JSPs e CSS revisados com base na referência visual escolhida. Vinte cenários HTTP reexecutados e aprovados, além da verificação de 14 links de ordenação e gêneros. Páginas de catálogo, detalhes, cadastro, edição, confirmação e busca revisadas no navegador desktop e capturadas. Nenhuma alteração no banco.
