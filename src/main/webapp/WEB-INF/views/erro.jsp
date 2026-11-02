<%@ page pageEncoding="UTF-8" %>
<%@ include file="header.jspf" %>
<section class="empty panel"><p class="eyebrow">VAMOS TENTAR DE NOVO</p><h1>Algo interrompeu a sessão.</h1><p><%=Html.e(request.getAttribute("mensagem"))%></p><a class="button" href="<%=base%>/filmes">Voltar ao catálogo</a></section>
<%@ include file="footer.jspf" %>
