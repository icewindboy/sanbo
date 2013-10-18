<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.action.Operate,engine.dataset.EngineDataSet"%><%
  engine.erp.common.B_CorpSelect corpSelectBean = engine.erp.common.B_CorpSelect.getInstance(request);
  String retu = corpSelectBean.doService(request, response);
  EngineDataSet ds = corpSelectBean.getOneTable();
  String operate = request.getParameter("operate");
  if(String.valueOf(Operate.CUST_CHANGE).equals(operate) || String.valueOf(Operate.CUST_NAME_CHANGE).equals(operate))
  {
    try{
      int count = ds.getRowCount();
      //out.print("<script language='javascript' src='../scripts/validate.js'></script>");
      out.print("<script language='javascript'>");
      if(count > 1 || count==0)
      {
        if(count==0)
        {
          String sys = loginBean.getSystemParam("SYS_PUBLIC_SELECT");
          if(sys.equals("1"))
            out.print("parent.CustSelectOpen();");
          else if(sys.equals("2"))
            out.print("alert('不存在该单位');");
        }
        else
          out.print("parent.CustSelectOpen();");
      }
      else
      {
        String[] inputName = corpSelectBean.inputName;
        String[] fieldName = corpSelectBean.fieldName;
        if(inputName != null && fieldName != null)
        {
          engine.project.LookUp areaBean
              = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_AREA);
          int length = inputName.length > fieldName.length ? fieldName.length : inputName.length;
          String prefix= "parent." + corpSelectBean.srcFrm + ".";
          ds.first();
          engine.dataset.RowMap row = areaBean.getLookupRow(ds.getValue("dqh"));
          for(int i=0; i< length; i++)
          {
            out.print(prefix); out.print(inputName[i]);
            out.print(".value='");
            String lowerName = fieldName[i].toLowerCase();
            out.print(lowerName.equals("areacode") || lowerName.equals("dqmc")?
                      row.get(lowerName) : ds.getValue(fieldName[i]));
            out.print("';");
          }
        }
        if(corpSelectBean.getMethodName() != null && count==1)
          out.print("parent."+corpSelectBean.getMethodName()+";");
      }
      out.print("</script>");
      return;
    }
    catch(Exception e){
      corpSelectBean.getLog().error("custCodeNameChange", e);
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
<frameset cols="160,*" frameborder="0" framespacing="3" border="1" bordercolor="#f0f0e0" framespacing="0" rows="*">
  <frame id="tree" name="tree" src="corpselect_left.jsp" scrolling="auto">
  <frame id="context" name="context" src="corpselect_right.jsp?init=<%=isInit ? "1" : "0"%>" scrolling="auto">
</frameset>
<noframes>
<body bgcolor="#FFFFFF" text="#000000">
</body>
</noframes>
</html>