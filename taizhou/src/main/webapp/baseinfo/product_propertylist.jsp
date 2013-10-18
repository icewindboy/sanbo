<%--物资规格属性编辑页面--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate, java.util.ArrayList, java.util.Enumeration"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%><%String pageCode = "productinfoset";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.baseinfo.PropertyEdit propertyEditBean = engine.erp.baseinfo.PropertyEdit.getInstance(request);
  String retu = propertyEditBean.doService(request, response);
  if(retu.indexOf("windows.close")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = propertyEditBean.getOneTable();
  RowMap rowinfo = propertyEditBean.getMasterRowinfo();
  EngineDataSet father = propertyEditBean.getFatherTable();
  RowMap row = propertyEditBean.getMasterRowinfo();
  boolean isEdit = loginBean.hasLimits(pageCode, op_edit);//有修改权限
  String tableClass = isEdit ? "edbox" : "edline";
%>
<html><head><title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>

<script language="javascript">
  function sumitForm(oper, row)
   {
     lockScreenToWait("处理中, 请稍候！");
     form1.operate.value = oper;
     form1.rownum.value  = row;
     form1.submit();
  }
  function showInterFrame(){
    showFrame('detailDiv',true,"",true);
  }
  function hideInterFrame()//隐藏FRAME
  {
    lockScreenToWait("处理中, 请稍候！");
    hideFrame('detailDiv');
    form1.submit();
  }
  function hideFrameNoFresh(){
    hideFrame('detailDiv');
  }
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">物资规格属性</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum"  VALUE="">
  <TABLE width="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
       <td class="td" nowrap>
     <%String key = "ppdfsgg"; pageContext.setAttribute(key, ds);
       int iPage = loginBean.getPageSize()-6; String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
</td>
      <TD align="right">
        <input name="button2" type="button" align="Right" class="button" onClick="window.close();" value=" 关闭 "border="0">
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
       <td nowrap align="center" width=45><%if(loginBean.hasLimits(pageCode, op_edit)){%><input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/><%}%></td>
      <td nowrap>规格属性</td>
    </tr>
     <%ds.first();
      int i=0;
      for(; i<ds.getRowCount(); i++)
      {
    %>
    <tr onDblClick="sumitForm(<%=Operate.EDIT%>,<%=ds.getRow()%>)">
        <td class="td"><%if(loginBean.hasLimits(pageCode, op_edit)){%><input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=ds.getRow()%>)" src="../images/edit.gif" border="0">
      <input name="image" class="img" type="image" title="删除" onClick="sumitForm(<%=Operate.DEL%>,<%=ds.getRow()%>)" src="../images/del.gif" border="0">
      <%}%></td>
      <td class="td" nowrap><%=ds.getValue("sxz")%></td>
    </tr>
    <%  ds.next();
      }
      for(; i < iPage; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
    </tr>
    <%}%>
  </table>
</form>
<script LANGUAGE="javascript">
  function submitFixedQuery(oper)
  {
    lockScreenToWait("处理中, 请稍候！");
    fixedQueryform.operate.value = oper;
    fixedQueryform.submit();
  }
</script>
<form name="fixedQueryform" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
<INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
        <table BORDER="0" cellpadding="1" cellspacing="3">
       <%int rowcount = father.getRowCount();
        boolean isAdd = propertyEditBean.isAdd;
        ArrayList keys = propertyEditBean.keys;
        Enumeration rows = rowinfo.columnNames();
        String sxmc=null, sxz=null;
        StringBuffer buf = new StringBuffer();
        buf.append("<tr>").append("<td nowrap class='td'>");
        father.first();
        if(isAdd){
          for(int j=0; j<rowcount; j++)
          {
            sxmc = father.getValue("sxmc");
            buf.append(sxmc).append(":<input class=edbox size=12 maxlength=16 name='sxmc_");
            buf.append(j).append("' value='").append("'").append(">");
            father.next();
          }
        }
        else{
          //int f= 0;
          boolean isreadonly=false;
          for(int f=0;f<keys.size();f++)
          {
            key = (String)keys.get(f);
            isreadonly = rowinfo.get(key).equals("") ? false : true;
            if(isreadonly)
              buf.append(key).append(":<input size=12 class=edline readonly maxlength=16 name='sxmc_");
            else
              buf.append(key).append(":<input size=12 class=edbox maxlength=16 name='sxmc_");
            buf.append(f).append("' value='").append(rowinfo.get(key)).append("'").append(">");
          }
        }
       out.print(buf.toString());
       out.print("</td></tr>");
    %>
    <tr>
      <td colspan="2" noWrap align="center"><br>
        <input name="button" type="button" class="button" onClick="submitFixedQuery(<%=Operate.POST%>);" value="保存(S)">
         <pc:shortcut key="s" script='<%="submitFixedQuery("+ Operate.POST +",-1)"%>'/>
        <input name="button2" type="button" class="button" onClick="hideFrame('detailDiv');" value=" 关闭 ">
      </td>
    </tr>
  </table>
</div>
</form>
<script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
</script>
<%out.print(retu);%>
</body>
</html>
