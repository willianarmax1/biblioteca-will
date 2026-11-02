package br.edu.cinearquivo.config;
import jakarta.servlet.*;
import br.edu.cinearquivo.dao.FilmeDAO;
import br.edu.cinearquivo.service.FilmeService;
/** Inicializa o banco antes de receber requisições. */
public class AppListener implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent event){
        try {Database db=Database.local();db.initialize();event.getServletContext().setAttribute("service",new FilmeService(new FilmeDAO(db)));}
        catch(Exception e){throw new IllegalStateException("Não foi possível iniciar o banco de dados.",e);}
    }
}
