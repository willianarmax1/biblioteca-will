package br.edu.cinearquivo.service;

import br.edu.cinearquivo.model.Filme;
import br.edu.cinearquivo.dao.FilmeDAO;
import java.time.Year;
import java.sql.SQLException;
import java.util.*;

/** Regras de domínio independentes de HTTP/JSP. */
public class FilmeService {
    public static final List<String> GENEROS=List.of("Ação","Animação","Aventura","Comédia","Documentário","Drama","Fantasia","Ficção científica","Romance","Suspense","Terror");
    private final FilmeDAO dao;
    public FilmeService(FilmeDAO dao){this.dao=dao;}
    public List<String> validar(Filme f){
        List<String> erros=new ArrayList<>();
        f.setTitulo(clean(f.getTitulo()));f.setDiretor(clean(f.getDiretor()));f.setSinopse(clean(f.getSinopse()));
        if(f.getTitulo().isEmpty()||f.getTitulo().length()>150)erros.add("Informe um título de até 150 caracteres.");
        if(f.getDiretor().isEmpty()||f.getDiretor().length()>120)erros.add("Informe um diretor de até 120 caracteres.");
        if(f.getAno()<1888||f.getAno()>Year.now().getValue()+5)erros.add("Informe um ano entre 1888 e "+(Year.now().getValue()+5)+".");
        if(!GENEROS.contains(f.getGenero()))erros.add("Selecione um gênero válido.");
        if(f.getSinopse().isEmpty()||f.getSinopse().length()>2000)erros.add("Escreva uma sinopse de até 2.000 caracteres.");
        if(f.getAvaliacao()<0||f.getAvaliacao()>5)erros.add("A avaliação deve ficar entre 0 e 5 estrelas.");
        return erros;
    }
    private String clean(String s){return s==null?"":s.strip();}
    public boolean salvar(Filme f) throws SQLException {
        List<String> errors=validar(f);if(!errors.isEmpty())throw new IllegalArgumentException(String.join(" ",errors));return dao.salvar(f);
    }
    public List<Filme> buscar(String termo,String genero,String ordem)throws SQLException{return dao.buscar(termo,genero,ordem);}
    public Optional<Filme> encontrar(long id)throws SQLException{return dao.encontrar(id);}
    public boolean excluir(long id)throws SQLException{return dao.excluir(id);}
}
