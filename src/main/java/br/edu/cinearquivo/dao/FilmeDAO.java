package br.edu.cinearquivo.dao;

import br.edu.cinearquivo.config.Database;
import br.edu.cinearquivo.model.Filme;
import java.sql.*;
import java.util.*;

/** Isola SQL e JDBC das regras de negócio e da interface. */
public class FilmeDAO {
    private final Database db;
    public FilmeDAO(Database db){this.db=db;}
    private Filme map(ResultSet r) throws SQLException {
        return new Filme(r.getLong("id"),r.getString("titulo"),r.getString("diretor"),r.getInt("ano"),r.getString("genero"),r.getString("sinopse"),r.getInt("avaliacao"));
    }
    /** Busca título/diretor, aplica gênero e ordenação com SQL parametrizado. */
    public List<Filme> buscar(String termo,String genero,String ordem) throws SQLException {
        String sort=switch(ordem){case "ano"->"ano DESC, titulo ASC";case "avaliacao"->"avaliacao DESC, titulo ASC";default->"titulo ASC";};
        String sql="SELECT * FROM filmes WHERE (LOWER(titulo) LIKE ? ESCAPE '!' OR LOWER(diretor) LIKE ? ESCAPE '!') AND (? = '' OR genero = ?) ORDER BY "+sort;
        String pattern="%"+termo.toLowerCase(Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        try(Connection c=db.open(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,pattern);p.setString(2,pattern);p.setString(3,genero);p.setString(4,genero);
            try(ResultSet r=p.executeQuery()){List<Filme> list=new ArrayList<>();while(r.next())list.add(map(r));return list;}
        }
    }
    /** Retorna vazio quando o identificador não existe. */
    public Optional<Filme> encontrar(long id) throws SQLException {
        try(Connection c=db.open();PreparedStatement p=c.prepareStatement("SELECT * FROM filmes WHERE id=?")){
            p.setLong(1,id);try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(map(r)):Optional.empty();}
        }
    }
    /** Insere ou atualiza; retorna false para edição de registro inexistente. */
    public boolean salvar(Filme f) throws SQLException {
        boolean novo=f.getId()==null;
        String sql=novo?"INSERT INTO filmes(titulo,diretor,ano,genero,sinopse,avaliacao) VALUES(?,?,?,?,?,?)":"UPDATE filmes SET titulo=?,diretor=?,ano=?,genero=?,sinopse=?,avaliacao=? WHERE id=?";
        try(Connection c=db.open();PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            p.setString(1,f.getTitulo());p.setString(2,f.getDiretor());p.setInt(3,f.getAno());p.setString(4,f.getGenero());p.setString(5,f.getSinopse());p.setInt(6,f.getAvaliacao());
            if(!novo)p.setLong(7,f.getId());
            if(p.executeUpdate()==0)return false;
            if(novo)try(ResultSet r=p.getGeneratedKeys()){if(r.next())f.setId(r.getLong(1));}
            return true;
        }
    }
    /** Exclusão por ID usando parâmetro JDBC. */
    public boolean excluir(long id) throws SQLException {
        try(Connection c=db.open();PreparedStatement p=c.prepareStatement("DELETE FROM filmes WHERE id=?")){p.setLong(1,id);return p.executeUpdate()>0;}
    }
}
