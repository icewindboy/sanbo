<%@page contentType="application/pdf"%><%--@ include file="../pub/init.jsp"--%><%
  request.setCharacterEncoding("UTF-8");
  session.setMaxInactiveInterval(180);
  engine.common.BaseHttpServlet.browserNoCache(response);
  engine.common.LoginBean loginBean = engine.common.LoginBean.getInstance(request);
  if(!loginBean.isLogin(request, response))
    return;

  String code = request.getParameter("code");
  engine.common.PdfProducerFacade pdf = code == null ?
                                        engine.common.PdfProducerFacade.getInstance(request) :
                                        engine.common.PdfProducerFacade.getInstance(request, code);
  pdf.reportDirPath = getServletContext().getRealPath("/WEB-INF/report");
  pdf.doService(request, response);
%>