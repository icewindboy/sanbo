<%@ page contentType="text/html; charset=UTF-8" %><%
  engine.erp.baseinfo.B_Corp corpBean = engine.erp.baseinfo.B_Corp.getInstance(request);
  corpBean.doService(request, response);
%>