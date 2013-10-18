<%@page contentType="text/html; charset=UTF-8" %>
<%request.setCharacterEncoding("UTF-8");
  //session.setMaxInactiveInterval(900);
  response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","max-age=0");
  response.addHeader("Cache-Control","pre-check=0");
  response.addHeader("Cache-Control","post-check=0");
  response.setDateHeader("Expires", 0);
  engine.common.OtherLoginBean loginBean = engine.common.OtherLoginBean.getInstance(request);
  if(!loginBean.isLogin(request, response))
    return;
%><%@ taglib uri="/WEB-INF/pagescontrol.tld" prefix="pc"%>
<%@ page import="engine.action.Operate,engine.dataset.EngineDataSet"%><%
  engine.erp.common.OtherProductSelect productSelect = engine.erp.common.OtherProductSelect.getInstance(request);
  String retu = productSelect.doService(request, response);
  EngineDataSet ds = productSelect.getOneTable();
  String operate = request.getParameter("operate");
  if(String.valueOf(Operate.PROD_CHANGE).equals(operate)|| String.valueOf(Operate.PROD_NAME_CHANGE).equals(operate))
  {
    try{
      int count = ds.getRowCount();
      //out.print("<script language='javascript' src='../scripts/validate.js'></script>");
      out.print("<script language='javascript'>");
      if(count > 1 || count == 0)
      {
        if(count==0)
        {
          String sys = loginBean.getSystemParam("SYS_PUBLIC_SELECT");
          if(sys.equals("1"))
            out.print("parent.ProdSelectOpen();");
          else if(sys.equals("2"))
            out.print("alert('不存在该产品');");
        }
        else
          out.print("parent.ProdSelectOpen();");
      }
      else
      {
        String[] inputName = productSelect.inputName;
        String[] fieldName = productSelect.fieldName;
        if(inputName != null && fieldName != null)
        {
          int length = inputName.length > fieldName.length ? fieldName.length : inputName.length;
          String prefix= "parent." + productSelect.srcFrm + ".";
          ds.first();
          for(int i=0; i< length; i++)
          {
            out.print(prefix); out.print(inputName[i]);
            out.print(".value='");
            out.print(ds.getValue(fieldName[i]));
            out.print("';");
          }
        }
        if(productSelect.getMethodName() != null && count==1)
          out.print("parent."+productSelect.getMethodName()+";");
      }
      out.print("</script>");
      return;
    }
    catch(Exception e){
      productSelect.getLog().error("productCodeNameChange", e);
    }
  }
  boolean isInit = String.valueOf(Operate.INIT).equals(operate);
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<frameset cols="220,*" frameborder="0" framespacing="3" border="1" bordercolor="#f0f0e0" framespacing="0" rows="*">
  <frame id="tree" name="tree" src="product_left.jsp" scrolling="no">
  <frame id="context" name="context" src="product_right.jsp?init=<%=isInit ? "1" : "0"%>" scrolling="auto">
</frameset>
<noframes>
<body bgcolor="#FFFFFF" text="#000000">
</body>
</noframes>
</html>