package br.edu.cinearquivo.web;
/** Escapa dados do usuário antes de inseri-los no HTML, inclusive em atributos. */
public final class Html {
    private Html(){}
    public static String e(Object v){return v==null?"":v.toString().replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");}
}
