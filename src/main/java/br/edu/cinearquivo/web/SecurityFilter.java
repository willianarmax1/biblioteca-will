package br.edu.cinearquivo.web;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
/** Cabeçalhos básicos e codificação UTF-8 para toda a aplicação. */
public class SecurityFilter implements Filter {
    @Override public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain)throws IOException,ServletException{
        request.setCharacterEncoding("UTF-8");response.setCharacterEncoding("UTF-8");
        HttpServletResponse r=(HttpServletResponse)response;
        r.setHeader("X-Content-Type-Options","nosniff");r.setHeader("X-Frame-Options","DENY");
        r.setHeader("Content-Security-Policy","default-src 'self'; img-src 'self' data:; style-src 'self'; script-src 'self'; frame-ancestors 'none'; base-uri 'self'; form-action 'self'");
        r.setHeader("Referrer-Policy","same-origin");r.setHeader("Cache-Control","no-store");chain.doFilter(request,response);
    }
}
