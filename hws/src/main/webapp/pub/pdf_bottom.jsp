<%@page contentType="application/pdf" %><%--@ include file="../pub/init.jsp"--%><%
  session.setMaxInactiveInterval(600);
  //�ر�netscape���������
  response.setHeader("Pragma","no-cache");
  //�ر�IE���������
  response.setHeader("Cache-Control","no-cache");
  response.setHeader("Cache-Control","no-store");
  response.setHeader("Cache-Control","post-check=0");
  response.setHeader("Cache-Control","pre-check=0");
  response.setHeader("Cache-Control","must-revalidate");
  //ȥ������������Ļ���
  response.setHeader("Expires","0");
  //response.setDateHeader("Expires", 0);
  engine.common.LoginBean loginBean = engine.common.LoginBean.getInstance(request);
  if(!loginBean.isLogin(request, response))
    return;

  engine.common.PdfProducerFacade pdf = engine.common.PdfProducerFacade.getInstance(request);
  pdf.doService(request, response);
%>