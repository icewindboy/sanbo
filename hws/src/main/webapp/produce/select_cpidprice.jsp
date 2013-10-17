<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.action.Operate,engine.dataset.EngineDataSet"%><%
  engine.erp.jit.Select_CpidPrice selectCpidPrice = engine.erp.jit.Select_CpidPrice.getInstance(request);
  String retu = selectCpidPrice.doService(request, response);
  EngineDataSet ds = selectCpidPrice.getOneTable();
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
        String[] inputName = selectCpidPrice.inputName;
        String[] fieldName = selectCpidPrice.fieldName;
        if(inputName != null && fieldName != null)
        {
          int length = inputName.length > fieldName.length ? fieldName.length : inputName.length;
          String prefix= "parent." + selectCpidPrice.srcFrm + ".";
          ds.first();
          for(int i=0; i< length; i++)
          {
            out.print(prefix); out.print(inputName[i]);
            out.print(".value='");
            out.print(ds.getValue(fieldName[i]));
            out.print("';");
          }
        }
        //if(selectCpidPrice.getMethodName() != null && count==1)
        //  out.print("parent."+selectCpidPrice.getMethodName()+";");
      }
      out.print("</script>");
      return;
    }
    catch(Exception e){
      selectCpidPrice.getLog().error("productCodeNameChange", e);
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