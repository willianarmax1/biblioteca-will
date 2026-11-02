package br.edu.cinearquivo.config;

import java.sql.*;
import java.nio.file.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/** Conexões JDBC; H2 grava os registros em arquivo, mesmo após desligar o servidor. */
public final class Database {
    private final String url;
    public Database(String url) { this.url = url; }
    public static Database local() throws IOException {
        Path dir = Path.of(System.getProperty("cine.data", "data")).toAbsolutePath();
        Files.createDirectories(dir);
        return new Database("jdbc:h2:file:" + dir.resolve("catalogo") + ";DB_CLOSE_ON_EXIT=FALSE");
    }
    public Connection open() throws SQLException { return DriverManager.getConnection(url, "sa", ""); }
    /** Schema e carga de exemplo executados uma única vez, em uma transação. */
    public void initialize() throws SQLException, IOException {
        try(Connection c=open()) {
            c.setAutoCommit(false);
            try(Statement s=c.createStatement()) {
                s.execute("CREATE TABLE IF NOT EXISTS app_meta (chave VARCHAR(40) PRIMARY KEY, valor VARCHAR(40))");
                try(ResultSet r=s.executeQuery("SELECT valor FROM app_meta WHERE chave='schema'")) {
                    if(r.next()) { c.commit(); return; }
                }
                String sql;
                try(InputStream in=Database.class.getResourceAsStream("/schema.sql")) {
                    if(in==null) throw new IOException("schema.sql não encontrado");
                    sql=new String(in.readAllBytes(),StandardCharsets.UTF_8);
                }
                for(String part:sql.split(";")) if(!part.isBlank()) s.execute(part);
                s.execute("INSERT INTO app_meta VALUES ('schema','1')");
                c.commit();
            } catch(SQLException|IOException ex) { c.rollback(); throw ex; }
        }
    }
}
