package br.edu.cinearquivo;

import br.edu.cinearquivo.config.Database;
import br.edu.cinearquivo.dao.FilmeDAO;
import br.edu.cinearquivo.model.Filme;
import br.edu.cinearquivo.service.FilmeService;
import br.edu.cinearquivo.web.Html;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class CatalogoTest {
    @TempDir Path dir;
    Database db;FilmeDAO dao;FilmeService service;
    @BeforeEach void setup()throws Exception{
        db=new Database("jdbc:h2:file:"+dir.resolve("teste")+";DB_CLOSE_ON_EXIT=FALSE");
        db.initialize();dao=new FilmeDAO(db);service=new FilmeService(dao);
    }
    private Filme novo(){return new Filme(null,"Filme de teste","Diretora Teste",2024,"Drama","Uma sinopse válida.",4);}
    @Test void cicloCrudEBusca()throws Exception{
        Filme f=novo();assertTrue(service.salvar(f));assertNotNull(f.getId());
        assertEquals("Filme de teste",service.encontrar(f.getId()).orElseThrow().getTitulo());
        assertEquals(1,service.buscar("DIRETORA TESTE","Drama","").size());
        f.setTitulo("Título atualizado");assertTrue(service.salvar(f));
        assertEquals("Título atualizado",service.encontrar(f.getId()).orElseThrow().getTitulo());
        assertTrue(service.excluir(f.getId()));assertTrue(service.encontrar(f.getId()).isEmpty());
    }
    @Test void dadosPersistemEAmostrasNaoRetornam()throws Exception{
        for(Filme f:service.buscar("","",""))service.excluir(f.getId());
        Filme f=novo();service.salvar(f);
        Database reaberto=new Database("jdbc:h2:file:"+dir.resolve("teste")+";DB_CLOSE_ON_EXIT=FALSE");reaberto.initialize();
        assertEquals(1,new FilmeDAO(reaberto).buscar("","","").size());
    }
    @Test void buscaMaliciosaNaoViraSql()throws Exception{
        assertTrue(service.buscar("' OR 1=1 --","","").isEmpty());
        assertEquals(8,service.buscar("","","").size());
    }
    @Test void buscaTrataCuringasComoTexto()throws Exception{
        assertTrue(service.buscar("%","","").isEmpty());
        assertTrue(service.buscar("_","","").isEmpty());
    }
    @Test void validaCamposELimites(){
        Filme f=novo();f.setAno(100);f.setAvaliacao(6);f.setTitulo(" ");f.setGenero("Inválido");
        assertEquals(4,service.validar(f).size());assertThrows(IllegalArgumentException.class,()->service.salvar(f));
    }
    @Test void escapingImpedeHtmlExecutavel(){
        assertEquals("&lt;script&gt;&quot;&#39;&amp;",Html.e("<script>\"'&"));
    }
    @Test void filtroOrdenacaoEInexistentes()throws Exception{
        var filmes=service.buscar("","Ficção científica","ano");
        assertEquals(2,filmes.size());assertTrue(filmes.get(0).getAno()>=filmes.get(1).getAno());
        assertTrue(service.encontrar(999999).isEmpty());assertFalse(service.excluir(999999));
        Filme f=novo();f.setId(999999L);assertFalse(service.salvar(f));
    }
}
