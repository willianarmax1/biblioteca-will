package br.edu.cinearquivo;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.Context;
import java.nio.file.*;
/** Servidor local incluído: dispensa instalar Tomcat separadamente. */
public class Main {
    public static void main(String[] args)throws Exception{
        int port=Integer.parseInt(System.getProperty("cine.port","8080"));
        Tomcat server=new Tomcat();server.setBaseDir(Path.of(".tomcat").toAbsolutePath().toString());server.setPort(port);
        server.getConnector().setProperty("address",System.getProperty("cine.host","127.0.0.1"));
        Context context=server.addWebapp("",Path.of("src/main/webapp").toAbsolutePath().toString());
        context.setParentClassLoader(Main.class.getClassLoader());
        Runtime.getRuntime().addShutdownHook(new Thread(()->{try{server.stop();server.destroy();}catch(Exception ignored){}}));
        server.start();
        if(!context.getState().isAvailable())throw new IllegalStateException("Aplicação indisponível. Consulte o erro anterior no terminal.");
        System.out.println("\nBiblioteca Will pronto: http://localhost:"+port+"/filmes\nPressione Ctrl+C para encerrar.\n");server.getServer().await();
    }
}
