<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%><%
  String corpName = request.getParameter("name");
  String corpId = request.getParameter("id");
  if(corpName == null || corpName.length() == 0){
    out.println("<script language='javascript'>alert('单位名称不能为空！');</script>");
    return;
  }
  if(!loginBean.hasLimits("corplist", request, response))
    return;
  engine.erp.baseinfo.B_Corp corpBean = engine.erp.baseinfo.B_Corp.getInstance(request);
  out.print("<script language='javascript'>");
  if(corpBean.isRepeat(corpName, corpId))
    out.print("if(confirm('单位名称重复是否保存?'))");
  out.println("parent.form1.submit();</script>");
%>