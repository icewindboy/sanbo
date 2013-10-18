<%--其他应收款--%><%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.html.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
  String op_cancel = "op_cancel";
  String op_approve = "op_approve";
  String pageCode = "other_balance";
%>
<%
  if(!loginBean.hasLimits("other_balance", request, response))
    return;
  engine.erp.sale.shengyu.B_OtherBalance otherBalanceBean  =  engine.erp.sale.shengyu.B_OtherBalance.getInstance(request);
  String retu = otherBalanceBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
  out.print(retu);
%>
<html>
<head>
<title></title>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript" src="../scripts/frame.js"></script>
<SCRIPT LANGUAGE="javascript">
  function toDetail(oper, row){
    // 转到主从明细
    lockScreenToWait("处理中, 请稍候！");
    location.href='other_balance_edit.jsp?operate='+oper+'&rownum='+row;
  }
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
  function sumitFixedQuery(oper)
 {
   lockScreenToWait("处理中, 请稍候！");
   fixedQueryform.operate.value = oper;
   fixedQueryform.submit();
  }
  function showFixedQuery()
   {
    showFrame('fixedQuery',true,"",true);
   }
 function corpCodeSelect(obj,srcVar)
{
  CustCodeChange(document.all['prod'], obj.form.name, srcVar,'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function corpNameSelect(obj,srcVar)
{
  CustNameChange(document.all['prod'], obj.form.name, srcVar,'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
//客户编码
function customerCodeSelect(obj)
{
    CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
//客户名称
function customerNameSelect(obj)
{
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
 function tobachadd(oper, row)
 {
   location.href='produce_promotion_batchadd.jsp?operate='+oper;
}
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>其他应收款</TD>
  </TR>
</TABLE>
<%
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//引用部门
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);//引用往来单位
  EngineDataSet list = otherBalanceBean.getOneTable();
  HtmlTableProducer table = otherBalanceBean.masterProducer;//主表数据的表格打印
  String curUrl = request.getRequestURL().toString();
  boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE width="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
       <td class="td" nowrap>
        <%
          String key = "productpromotion";
          pageContext.setAttribute(key, list);
          int iPage = loginBean.getPageSize();
          String pageSize = String.valueOf(iPage);
        %>
        <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
        </td>
        <td class="td" nowrap align="right">
        <input name="search2" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script="showFixedQuery()" />
        <%if(otherBalanceBean.retuUrl!=null){
          String s = "location.href='"+otherBalanceBean.retuUrl+"'";
           %>
         <input name="button22" type="button" class="button"  style="width:60"  onClick="location.href='<%=otherBalanceBean.retuUrl%>'" value=" 返回(C) " onKeyDown="return getNextElement();">
         <pc:shortcut key="c" script="<%=s%>" />
        <%}%>
        </td>
   </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td>
      <%if(isCanAdd){%>
      <input name="image" class="img" type="image"  title="新增(A)"  onClick="toDetail(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/>
      <%}%>
      </td>
    <%table.printTitle(pageContext, "height='20'");%><!--打印表头-->
    </tr>
    <%
      corpBean.regData(list, "dwtxid");
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++)
      {

        boolean isInit = false;
        String rowClass =list.getValue("zt");//0.初始化,1.已开提单(已经审核),9.出库
        if(rowClass.equals("0")){
          isInit = true;
          rowClass ="class=td";
        }
        String rowzt =list.getValue("zt");
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        String loginid=otherBalanceBean.loginid;
        String sprid=list.getValue("sprid");
        boolean isCancer=rowzt.equals("1") && loginid.equals(sprid);
        boolean cansubmit=false;
        boolean submitType = otherBalanceBean.submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
        String czyid = list.getValue("czyid");
        if(submitType&&czyid.equals(loginid))
          cansubmit=true;
        else if(!submitType)
          cansubmit=true;
        RowMap corpRow =corpBean.getLookupRow(list.getValue("dwtxid"));
        isCanDelete = isCanDelete&&czyid.equals(loginid)&&rowzt.equals("0");
    %>
    <tr <%if(isCanEdit){%>onDblClick="toDetail(<%=Operate.EDIT%>,<%=list.getRow()%>)"<%}%> onClick="selectRow()" >
      <td <%=rowClass%> align="center" nowrap>
      <%if(isCanEdit){%><input name="image2" class="img" type="image" title="修改" onClick="toDetail(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0"><%}%>
      <%--if(isCanDelete){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0"><%}--%>
      <%if(isInit&&cansubmit){%><input name="image3" class="img" type="image"  title='提交审批' onClick="if(confirm('确认要提交吗？'))sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
      <%if(isCancer){%>
         <input name="image3" class="img" type="image"  title='取消审批' onClick="if(confirm('确认要取消审批吗？'))sumitForm(<%=otherBalanceBean.CANCER_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
     <%
       if(rowzt.equals("1")&&(loginBean.hasLimits(pageCode, op_over))){//&&(loginBean.hasLimits(pageCode, op_over))
      %>
         <input name="image3" class="img" type="image"   title='完成' onClick="if(confirm('确认要完成吗？'))sumitForm(<%=otherBalanceBean.SALE_OVER%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%>
      </td>
       <%table.printCells(pageContext, rowClass);%><%--打印主表数据行--%>
    </tr>
    <%
      list.next();
      }
      for(; i < iPage; i++){
        out.print("<tr><td>&nbsp;</td>");
        table.printBlankCells(pageContext, "class=td");//打印空的格子
        out.print("</tr>");
      }
      %>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
   <div class="queryPop" id="fixedQuery" name="fixedQuery">
   <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
         <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD nowrap class="td">单据号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="otherfundno" name="otherfundno" value='<%=otherBalanceBean.getFixedQueryValue("otherfundno")%>' maxlength='16' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>制单人</TD>
              <TD class="td" nowrap>
              <INPUT class="edbox" style="WIDTH:160" id="czy" name="czy" value='<%=otherBalanceBean.getFixedQueryValue("czy")%>' maxlength='16' onKeyDown="return getNextElement();">
              </TD>
            </tr>
                <TR>
                  <TD align="center" nowrap class="td">客户名称</TD>
                  <td nowrap class="td" colspan=3>
                  <input type="text" class="edbox" style="width:100" onKeyDown="return getNextElement();" name="dwdm" value='<%=otherBalanceBean.getFixedQueryValue("dwdm")%>' onchange="customerCodeSelect(this)" >
                  <input type="text" name="dwmc"  style="width:260" value='<%=otherBalanceBean.getFixedQueryValue("dwmc")%>' class="edbox"  onchange="customerNameSelect(this)" >
                  <INPUT TYPE="HIDDEN" NAME="dwtxid" value='<%=otherBalanceBean.getFixedQueryValue("dwtxid")%>'>
                  <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',fixedQueryform.dwtxid.value)">
                  <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value='';">
                  </td>
                 </TR>
            <TR>
              <TD nowrap class="td">开单日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="otherfunddate$a" value='<%=otherBalanceBean.getFixedQueryValue("otherfunddate$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(otherfunddate$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="otherfunddate$b" value='<%=otherBalanceBean.getFixedQueryValue("otherfunddate$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(otherfunddate$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:160">
              <%=deptBean.getList(otherBalanceBean.getFixedQueryValue("deptid"))%></pc:select>
              </TD>
              <TD class="td" nowrap>业务员</TD>
              <TD class="td" nowrap>
                <pc:select name="personid" addNull="1" style="width:130">
                  <%=personBean.getList(otherBalanceBean.getFixedQueryValue("personid"))%>
                </pc:select>
             </TD>
             </TR>
            <TR>
              <TD nowrap class="td">经办人</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="jbr" name="jbr" value='<%=otherBalanceBean.getFixedQueryValue("jbr")%>' maxlength='16' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>审批人</TD>
              <TD class="td" nowrap>
              <INPUT class="edbox" style="WIDTH:160" id="sprid$xm" name="sprid$xm" value='<%=otherBalanceBean.getFixedQueryValue("sprid$xm")%>' maxlength='16' onKeyDown="return getNextElement();">
              </TD>
            </tr>
            <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%
                  String [] zt = otherBalanceBean.zt;
                  String zt0="";
                  String zt9="";
                  String zt1="";
                  String zt4="";
                  String zt8="";
                  String zt3="";
                  String zt2="";
                  String zt5="";
                  for(int k=0;k<zt.length;k++)
                  {
                    if(zt[k].equals("0"))
                      zt0 = "checked";
                    else if(zt[k].equals("9"))
                      zt9 = "checked";
                    else if(zt[k].equals("1"))
                      zt1 = "checked";
                    else if(zt[k].equals("4"))
                      zt4 = "checked";
                    else if(zt[k].equals("8"))
                      zt8 = "checked";
                    else if(zt[k].equals("2"))
                      zt2 = "checked";
                    else if(zt[k].equals("5"))
                      zt5 = "checked";
                  }
                %>
                <input type="checkbox" name="zt" value="0" <%=zt0%>>未审
                <input type="checkbox" name="zt" value="9" <%=zt9%>>审批中
                <input type="checkbox" name="zt" value="1" <%=zt1%>>已审
                <input type="checkbox" name="zt" value="8" <%=zt8%>>完成
              </TD>
            </TR>
            </TABLE>
            <TR>
              <TD colspan="4" nowrap class="td" align="center">
                <% String qu = "sumitFixedQuery("+otherBalanceBean.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=otherBalanceBean.FIXED_SEARCH%>)" type="button" value="查询(K)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="k" script='<%=qu%>'/>
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(x)" name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script="hideFrame('fixedQuery')"/>
            </td>
            </tr>
            </table>
              </TD>
            </TR>
    </TABLE>
  </DIV>
</form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="320" height="325" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(retu);%>
</body>
</html>