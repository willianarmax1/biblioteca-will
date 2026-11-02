package br.edu.cinearquivo.model;

/** Entidade principal. Os atributos são privados para preservar o encapsulamento. */
public class Filme {
    private Long id;
    private String titulo = "", diretor = "", genero = "", sinopse = "";
    private int ano = 2000, avaliacao = 0;
    public Filme() {}
    public Filme(Long id, String titulo, String diretor, int ano, String genero, String sinopse, int avaliacao) {
        this.id=id; this.titulo=titulo; this.diretor=diretor; this.ano=ano;
        this.genero=genero; this.sinopse=sinopse; this.avaliacao=avaliacao;
    }
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getTitulo(){return titulo;} public void setTitulo(String v){titulo=v;}
    public String getDiretor(){return diretor;} public void setDiretor(String v){diretor=v;}
    public int getAno(){return ano;} public void setAno(int v){ano=v;}
    public String getGenero(){return genero;} public void setGenero(String v){genero=v;}
    public String getSinopse(){return sinopse;} public void setSinopse(String v){sinopse=v;}
    public int getAvaliacao(){return avaliacao;} public void setAvaliacao(int v){avaliacao=v;}
    /** Pôster local associado ao título e ao ano, sem alterar os dados do banco. */
    public String getCapa() {
        String nome = java.text.Normalizer.normalize(titulo == null ? "" : titulo,
            java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "")
            .toLowerCase(java.util.Locale.ROOT).strip();
        String chave = nome + "|" + ano;
        return switch (chave) {
            case "interestelar|2014", "interstellar|2014" -> "interestelar.jpg";
            case "a viagem de chihiro|2001", "spirited away|2001" -> "chihiro.png";
            case "cidade de deus|2002", "city of god|2002" -> "cidade-de-deus.jpg";
            case "de volta para o futuro|1985", "back to the future|1985" -> "de-volta-para-o-futuro.jpg";
            case "matrix|1999", "the matrix|1999" -> "matrix.png";
            case "o grande hotel budapeste|2014", "the grand budapest hotel|2014" -> "hotel-budapeste.png";
            case "o show de truman|1998", "the truman show|1998" -> "truman.jpg";
            case "parasita|2019", "parasite|2019" -> "parasita.png";
            default -> null;
        };
    }
}
