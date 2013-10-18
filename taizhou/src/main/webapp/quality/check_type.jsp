<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode  = "check_type";
%>

<%if(!loginBean.hasLimits("check_type", request, response))
    return;
  //engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp kindBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT_KIND);

  engine.project.LookUp produceKind = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT_KIND);
      //BEAN_PRODUCT_KIND
  engine.erp.quality.B_CheckType  quality_CheckBean = engine.erp.quality.B_CheckType.getInstance(request);
  String retu = quality_CheckBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
%>
<html>
<head>
<title></title>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<SCRIPT LANGUAGE="javascript">
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
  function showInterFrame(oper, rownum)
  {
    var url = "check_type_edit.jsp?operate="+oper+"&rownum="+rownum;
    document.all.interframe1.src = url;
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
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>检验类型管理</TD>
  </TR>
</TABLE>
<%
  EngineDataSet list = quality_CheckBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
  boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit=loginBean.hasLimits(pageCode, op_edit);//op_edit
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="60%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
    <td class="td" nowrap>
<%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
      int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
</td> <TD align="right"><%if(quality_CheckBean.retuUrl!=null){%><input name="button2222232" type="button" align="Right"
  class="button" onClick="location.href='<%=quality_CheckBean.retuUrl%>'" value=" 返回 "border="0"><%}%></TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="60%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td width="242" nowarp>检验类型</td>
      <td width="400" nowarp>类别</td>
      <td nowarp width="70">
      <input name="image" class="img" type="image" title="新增(ALT+A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">

    </td>
    </tr>
    <%
    list.first();
    int i=0;
    for(;i<list.getRowCount();i++)
    {
    %>

    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
    </td>
     <td class="td" nowarp>&nbsp; <%
        //out.print(list.getValue("checktypeid"));
        String mytype=list.getValue("checktypeid");
       if(mytype.equals("1")) out.print("原纸");
       if(mytype.equals("2")) out.print("原膜");
       if(mytype.equals("3")) out.print("成品纸");
       if(mytype.equals("4")) out.print("成品膜");
       if(mytype.equals("5")) out.print("辅助材料");
       if(mytype.equals("6")) out.print("化工原料");
       if(mytype.equals("7")) out.print("外观");
       if(mytype.equals("8")) out.print("生产过程");
       %></td>
      <td class="td" nowarp>&nbsp;<%=kindBean.getLookupName(list.getValue("wzlbid"))%>
      </td>
      <td class="td" nowarp>
        <input name="image1" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
        <input name="image2" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      </td>
    </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form>
<%out.print(retu);%>
<div class="queryPop" id="detailDiv" name="detailDiv" style="width:240;height:240">
  <div class="queryTitleBox" align="right" style="width:240"><A onClick="hideFrame('detailDiv')" href="javascript:"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="240" height="260" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>