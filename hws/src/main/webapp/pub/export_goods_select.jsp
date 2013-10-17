<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.action.Operate,engine.dataset.EngineDataSet"%><%
  engine.erp.common.B_ExportGoodsSelect exportGoodsSelectBean = engine.erp.common.B_ExportGoodsSelect.getInstance(request);
  String retu = exportGoodsSelectBean.doService(request, response);
  EngineDataSet ds = exportGoodsSelectBean.getOneTable();
  String operate = request.getParameter("operate");
  boolean isInit = String.valueOf(Operate.INIT).equals(operate);
  if(String.valueOf(Operate.PROD_CHANGE).equals(operate) || String.valueOf(Operate.PROD_NAME_CHANGE).equals(operate))
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
            out.print("parent.SaleProdSelectOpen();");
          else if(sys.equals("2"))
            out.print("alert('不存在该产品');");
        }
        else
          out.print("parent.SaleProdSelectOpen();");
      }
      else
      {
        String[] inputName = exportGoodsSelectBean.inputName;
        String[] fieldName = exportGoodsSelectBean.fieldName;
        if(inputName != null && fieldName != null)
        {
          int length = inputName.length > fieldName.length ? fieldName.length : inputName.length;
          String prefix= "parent." + exportGoodsSelectBean.srcFrm + ".";
          ds.first();
          for(int i=0; i< length; i++)
          {
            out.print(prefix); out.print(inputName[i]);
            out.print(".value='");
            out.print(ds.getValue(fieldName[i]));
            out.print("';");
          }
        }
        if(exportGoodsSelectBean.getMethodName() != null && count==1)
          out.print("parent."+exportGoodsSelectBean.getMethodName()+";");
      }
      out.print("</script>");
      return;
    }
    catch(Exception e){
      exportGoodsSelectBean.getLog().error("saleProductCodeChange", e);
    }
  }
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<frameset cols="220,*" frameborder="0" framespacing="3" border="1" bordercolor="#f0f0e0" framespacing="0" rows="*">
  <frame id="tree" name="tree" src="export_goods_left.jsp" scrolling="no">
  <frame id="context" name="context" src="export_goods_right.jsp?init=<%=isInit ? "1" : "0"%>" scrolling="auto">
</frameset>
<noframes>
<body bgcolor="#FFFFFF" text="#000000">
</body>
</noframes>
</html>