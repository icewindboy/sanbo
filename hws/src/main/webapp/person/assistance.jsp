<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*"%>
<html>
<head>
<title>人员辅助信息列表</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("assistance", request, response))
    return;
  engine.erp.person.B_Assistance workbookBean = engine.erp.person.B_Assistance.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>

<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">人员辅助信息列表</TD>
  </TR></TABLE>
<%String retu = workbookBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  if(!workbookBean.dsWorkbook.isOpen())
    workbookBean.dsWorkbook.open();

  EngineDataView list = workbookBean.dsWorkbook.cloneEngineDataView();
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tablelx" width="600" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td"><%int type = workbookBean.lx;%>
      <input name="lx" type="radio" value="<%=workbookBean.PERSON_DUTY%>" onClick="lx_onchange(this)"<%=type==workbookBean.PERSON_DUTY?" checked":""%>>职务
      <input name="lx" type="radio" value="<%=workbookBean.PERSON_TYPE%>" onClick="lx_onchange(this)"<%=type==workbookBean.PERSON_TYPE?" checked":""%>>人员类别
      <input name="lx" type="radio" value="<%=workbookBean.PERSON_EDUCATION%>" onClick="lx_onchange(this)"<%=type==workbookBean.PERSON_EDUCATION?" checked":""%>>学历
      <input name="lx" type="radio" value="<%=workbookBean.PERSON_PEOPLE%>" onClick="lx_onchange(this)"<%=type==workbookBean.PERSON_PEOPLE?" checked":""%>>民族
      <input name="lx" type="radio" value="<%=workbookBean.PERSON_NATIVE_PLACE%>" onClick="lx_onchange(this)"<%=type==workbookBean.PERSON_NATIVE_PLACE?" checked":""%>>籍贯
      <input name="lx" type="radio" value="<%=workbookBean.PERSON_TECH_TITLE%>" onClick="lx_onchange(this)"<%=type==workbookBean.PERSON_TECH_TITLE?" checked":""%>>职称
      <input name="lx" type="radio" value="<%=workbookBean.PERSON_POLITY%>" onClick="lx_onchange(this)"<%=type==workbookBean.PERSON_POLITY?" checked":""%>>政治面貌
      <input name="lx" type="radio" value="<%=workbookBean.PERSON_YJXS%>" onClick="lx_onchange(this)"<%=type==workbookBean.PERSON_YJXS?" checked":""%>>移交信息
    </td>
    </tr>
  </table><br>
  <table id="tableview1" width="600" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <COLGROUP width=130 valign="middle">
    <COLGROUP valign="middle">
    <COLGROUP width=77 align="center">
    <tr class="tableTitle">
      <td>编码</td>
      <td>名称</td>
      <td><input name="image" class="img" type="image" title="新增(ALT+A)" onClick="showInterFrame(<%=workbookBean.OPERATE_ADD%>,-1)" src="../images/add.gif" border="0">
          <pc:shortcut key="a" script='<%="showInterFrame("+ workbookBean.OPERATE_ADD +",-1)"%>'/>
      </td>
    </tr>
    <%list.first();
      int i=0;
      for(int j=0; j<list.getRowCount(); j++)   {
        if(list.getBigDecimal("lx").intValue() == type){
    %>
    <tr onDblClick="showInterFrame(<%=workbookBean.OPERATE_EDIT%>,<%=list.getRow()%>)">
      <td class="td"><%=list.format("dm")%></td>
      <td class="td"><%=list.format("mc")%></td>
      <td class="td"> <input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=workbookBean.OPERATE_EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
        <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=workbookBean.OPERATE_DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      </td>
    </tr>
    <%    i++;
        }
        list.next();
      }
      for(; i < loginBean.getPageSize()-2; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
  <SCRIPT LANGUAGE="javascript">
    initDefaultTableRow('tableview1',1);
    function sumitForm(oper, row)
    {
      lockScreenToWait("处理中, 请稍候！");
      form1.operate.value = oper;
      form1.rownum.value = row;
      form1.submit();
    }
    function showInterFrame(oper, rownum){
    var url = "assistanceedit.jsp?operate="+oper+"&rownum="+rownum;
    document.all.interframe1.src = url;
    showFrame('detailDiv',true,"",true);
  }

  function hideInterFrame()//隐藏FRAME
  {
    hideFrame('detailDiv');
    form1.submit();
  }
  function hideFrameNoFresh(){
    hideFrame('detailDiv');
  }
    function lx_onchange(obj){
      if(obj.value != <%=workbookBean.lx%>)
        sumitForm(<%=workbookBean.TYPE_CHANGE%>);
    }
  </SCRIPT>
  </form>
  <div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="330" height="150" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>