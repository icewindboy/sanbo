<%
/**
 * 03.03 14:52 新增 新增编号为9的其它入库单select选项. 因为库存中又新增了一个其它入库单的功能. yjg
 */
%>
<%--收发单据类别--%><%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "store_bill_kind";
%>
<%
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.store.B_StoreBillKind b_StoreBillKindBean  =  engine.erp.store.B_StoreBillKind.getInstance(request);
  String retu = b_StoreBillKindBean.doService(request, response);
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
  function sumitFixedQuery(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    fixedQueryform.operate.value = <%=Operate.FIXED_SEARCH%>;
    fixedQueryform.operate.value = oper;
    fixedQueryform.submit();
  }
  function showFixedQuery()
  {
    showFrame('fixedQuery', true, "", true);
  }
  function showInterFrame(oper, rownum)
  {
    var url = "store_bill_kind_edit.jsp?operate="+oper+"&rownum="+rownum;
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
  //返回
  function buttonEventC()
  {
    location.href='<%=b_StoreBillKindBean.retuUrl%>';
  }
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>收发单据类别</TD>
  </TR>
</TABLE>
<%
  EngineDataSet list = b_StoreBillKindBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
  boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="90%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
    <td class="td" nowrap>
     <%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
    </td>
    <TD align="right">
       <%if (hasSearchLimit) {%>
       <input name="buttonq" type="button" align="Right" class="button" title="查询(ALT+Q)" value="查询(Q)" onClick="showFixedQuery()" border="0">
        <pc:shortcut key="q" script="showFixedQuery()"/>
      <%}%>
     <%if(b_StoreBillKindBean.retuUrl!=null){%>
        <input name="buttonc" type="button" align="Right" class="button" title="返回(ALT+C)" value="返回(C)" onClick="location.href='<%=b_StoreBillKindBean.retuUrl%>'" border="0">
        <pc:shortcut key="c" script='buttonEventC()'/>
    <%}%>
    </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td width="313" nowarp>单据性质</td>
      <td width="329" nowarp>类别名称</td>
      <td width="329" nowarp>类别编码</td>
       <td width="329" nowarp>收入类型</td><%--收入类型:1表示收入单据中须要输入正数.-1表示须要输入负数--%>
      <td nowarp width="70">
      <%if(loginBean.hasLimits(pageCode, op_add)){%><input name="image" class="img" type="image" title="新增" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0"><%}%>
      </td>
    </tr>
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
      <td class="td" nowarp>
      &nbsp;<%
      //b_StoreBillKindBean.g
       String djxz = list.getValue("djxz");
       if (djxz.equals("1"))
          out.print("采购入库单");
          else if(djxz.equals("2"))
            out.print("销售出库单");
          else if(djxz.equals("3"))
            out.print("自制入库单");
          else if(djxz.equals("4"))
            out.print("生产领料单");
          else if(djxz.equals("5"))
            out.print("外加工入库单");
          else if(djxz.equals("6"))
            out.print("外加工发料单");
          else if(djxz.equals("7"))
            out.print("报损单");
          else if(djxz.equals("8"))
            out.print("移库单");
          else if(djxz.equals("9"))
            out.print("其它入库单");//03.03 14:52 新增 新增编号为9的其它入库单select选项. 因为库存中又新增了一个其它入库单的功能. yjg
          else if(djxz.equals("10"))
            out.print("其它出库单");//09.20 15:05 新增 新增编号为11同价调拨单select选项. 因为库存中又新增了一个同价调拨单的功能. yjg
          else if(djxz.equals("11"))
            out.print("同价调拨单");
      %>
      </td>
      <td class="td" nowarp>&nbsp;<%=list.getValue("lbmc")%></td>
       <td class="td" nowarp>&nbsp;<%=list.getValue("lbbm")%></td>
      <td class="td" nowarp>&nbsp;<%=list.getValue("srlx").equals("1")?"正":"负"%></td>
      <td class="td" nowarp>
        <%if(isCanEdit){%><input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0"><%}%>
        <%if(isCanDelete){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0"><%}%>
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
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form><%out.print(retu);%>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="220" height="250" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
<table BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
      <td noWrap class="tableTitle">&nbsp;单据名称</td>
       <td noWrap class="td">

        <pc:select name="djxz" style="width:130" value='<%=b_StoreBillKindBean.getFixedQueryValue("djxz")%>'>
         <pc:option value=""></pc:option>
          <pc:option value="1">采购入库单</pc:option>
          <pc:option value="2">销售出库单</pc:option>
          <pc:option value="3">自制收货单</pc:option>
          <pc:option value="4">生产领料单</pc:option>
          <pc:option value="5">外加工入库单</pc:option>
          <pc:option value="6">外加工发料单</pc:option>
          <pc:option value="7">损溢单</pc:option>
          <pc:option value="8">移库单</pc:option>
          <pc:option value="9">其它入库单</pc:option><%--03.03 14:52 新增 新增编号为9的其它入库单select选项. 因为库存中又新增了一个其它入库单的功能. yjg--%>
          <pc:option value="10">其它出库单</pc:option>
        </pc:select>
      </td>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;类别名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="lbmc" VALUE="<%=b_StoreBillKindBean.getFixedQueryValue("lbmc")%>" style="WIDTH:130" class=edbox>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;类别编码</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="lbbm" VALUE="<%=b_StoreBillKindBean.getFixedQueryValue("lbbm")%>" style="WIDTH:130" class=edbox>
      </td>
    </tr>
    <td colspan="2" noWrap class="tableTitle">
      <input name="button" type="button" class="button" title="查询(ALT+F)"   value="查询(F)" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>);" value=" 查询 ">
      <pc:shortcut key="f" script='<%="sumitFixedQuery("+Operate.FIXED_SEARCH+")"%>'/>
      <input name="button2" type="button" class="button" title="关闭(ALT+T)" value="关闭(T)" onClick="hideFrame('fixedQuery')" value=" 关闭 ">
      <pc:shortcut key="t" script="hideFrame('fixedQuery')"/>
    </td>
    </tr>
  </table>
  </div>
</form>
</body>
</html>