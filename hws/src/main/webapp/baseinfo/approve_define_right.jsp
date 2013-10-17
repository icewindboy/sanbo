<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String op_edit="op_edit";
  String pageCode = "approve_define";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.system.B_ApproveDefine b_ApproveDefineBean = engine.erp.system.B_ApproveDefine.getInstance(request);
  String result =b_ApproveDefineBean.doService(request, response);
  EngineDataSet list = b_ApproveDefineBean.getDetailTable();
  EngineDataSet splstable = b_ApproveDefineBean.getDetailSPLXTable();
  RowMap[] detailRows= b_ApproveDefineBean.getDetailRowinfos();//从表多行
  engine.project.LookUp spxmBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_APPROVE_SPECIAL);//特殊审批
  String curUrl = request.getRequestURL().toString();
  String retuUrl=b_ApproveDefineBean.retuUrl;
  splstable.first();
  String splx = splstable.getValue("splx");

%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function showInterFrame(oper, rownum){
  var url = "approve_define_edit.jsp?operate="+oper+"&rownum="+rownum;
  document.all.interframe1.src = url;
  showFrame('detailDiv',true,"",true);
}
function openwin(oper, rownum)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=300 height=400 top=100 left=100";
  var winName= "GoodsProdSelector";
  paraStr = "approve_define_edit.jsp?operate="+oper+"&rownum="+rownum;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}
function hideInterFrame(){//隐藏FRAME
  hideFrame('detailDiv');
  form1.submit();
}
function hideFrameNoFresh(){
  hideFrame('detailDiv');
  }
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">审批流程定义</TD>
  </TR>
</TABLE>
  <TABLE width="75%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
    <td noWrap class="td">
    <INPUT TYPE="radio" NAME="splx" VALUE="0" <%=(!splx.equals("1"))?"checked":""%>  >事前审批
    <INPUT TYPE="radio" NAME="splx" VALUE="1" <%=splx.equals("1")?"checked":""%>  >事后审批

    </td>
      <TD align="right">
      <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存 ">
      <input name="button2" type="button" align="Right" class="button" onClick="location.href='../blank.htm'" value=" 返回 "border="0">
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="80%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap><input name="image" class="img" type="image" title="新增" onClick="sumitForm(<%=Operate.DETAIL_ADD%>,-1)" src="../images/add_big.gif" border="0"></td>
      <td height='20' nowrap>审批等级</td>
      <td height='20' nowrap>审批名称</td>
      <td height='20' nowrap>特殊审批项目</td>
      <td height='20' nowrap>特殊值</td>
      <td height='20' nowrap>状态描述通过值</td>
      <td height='20' nowrap>状态描述不通过值</td>
      </tr>
    <%
      RowMap detail = null;
      spxmBean.regConditionData(list,"spxmID");
      int i=0;
      list.first();
      for(; i<detailRows.length; i++)   {
      detail = detailRows[i];
      String spxmtsmxID="spxmtsmxID_"+i;
    %>
    <tr>
      <td class="td" nowrap align="center">
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      <%if(!list.getValue("spxmmxID").equals("-1")){%>
     <input name="image" class="img" type="image" title="审批人" onClick="openwin(<%=b_ApproveDefineBean.SHOW_SPR%>,<%=list.getValue("spxmmxID")%>)" src="../images/person.gif" border="0">
      <%}%>
      </td>
      <td class="td" nowrap align="right"><input type="text" CLASS="edbox" style="width:60"  onKeyDown="return getNextElement();" id="spdj_<%=i%>" name="spdj_<%=i%>"     value='<%=detail.get("spdj")%>'   maxlength='<%=list.getColumn("spdj").getPrecision()%>'></td>
      <td class="td" nowrap align="right"><input type="text" CLASS="edbox" style="width:60"   onKeyDown="return getNextElement();" id="spmc_<%=i%>" name="spmc_<%=i%>" value='<%=detail.get("spmc")%>' maxlength='<%=list.getColumn("spmc").getPrecision()%>'></td>
      <td noWrap class="td">
      <pc:select name="<%=spxmtsmxID%>" addNull="1" style="width:110">
      <%=spxmBean.getList(detail.get("spxmtsmxID"),"spxmid",b_ApproveDefineBean.spxmid)%>
      </pc:select>
      </td>
      <td class="td" nowrap align="right"><input type="text" CLASS="edbox"  align="right"  style="width:80"    onKeyDown="return getNextElement();" id="tsz_<%=i%>" name="tsz_<%=i%>"     value='<%=detail.get("tsz")%>'   maxlength='<%=list.getColumn("tsz").getPrecision()%>'   ></td>
      <td class="td" nowrap align="right"><input type="text" CLASS="edbox"   onKeyDown="return getNextElement();" id="ztmstgz_<%=i%>" name="ztmstgz_<%=i%>" value='<%=detail.get("ztmstgz")%>' maxlength='<%=list.getColumn("ztmstgz").getPrecision()%>'  ></td>
      <td class="td" nowrap align="right"><input type="text" CLASS="edbox"  align="right"     onKeyDown="return getNextElement();" id="ztmsbtgz_<%=i%>" name="ztmsbtgz_<%=i%>" value='<%=detail.get("ztmsbtgz")%>' maxlength='<%=list.getColumn("ztmsbtgz").getPrecision()%>' ><input type="HIDDEN" name="URL_<%=i%>"  value='<%=detail.get("URL")%>' ></td>
      </tr>
      <%list.next();
      }
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
    </tr>
    <%}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="400" height="450" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(result);%>
</BODY>
</Html>