<%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal,java.util.ArrayList,engine.html.*,engine.project.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
  String op_cancel = "op_cancel";
%>
<%
  engine.erp.sale.B_LadingInit b_LadingInitBean = engine.erp.sale.B_LadingInit.getInstance(request);
  String pageCode = "lading_init";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
    synchronized(b_LadingInitBean){
  String retu = b_LadingInitBean.doService(request, response);

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
<script language="javascript" src="../scripts/frame.js"></script>
<script language="javascript">
function toDetail(){
  // 转到主从明细
  parent.location.href='lading_init_edit.jsp';
  return;
}
function toTihd(){
  // 转到提货单
  parent.location.href='lading_init_edit.jsp';
  return;
}
function toTweihd(){
  // 转到退货单
  parent.location.href='back_lading_bill_edit.jsp';
  return;
}
function toInitTD(){
  // 转到初始化提货单
  parent.location.href='lading_init_edit.jsp';
  return;
}
function showDetail(masterRow){
  //单击主表的一行激活的操作
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='lading_init_buttom.jsp?operate=<%=b_LadingInitBean.SHOW_DETAIL%>&rownum='+masterRow;
  unlockScreenWait();
  return;
}
function sumitForm(oper, row)
{
  //新增或修改
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
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
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>

<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">销售货物初始化管理</TD>
  </TR>
</TABLE>
<%
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);//部门
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  EngineDataSet list = b_LadingInitBean.getMaterTable();//主表数据集
  HtmlTableProducer table = b_LadingInitBean.masterProducer;//主表数据的表格打印
  String curUrl = request.getRequestURL().toString();
  //RowMap m_RowInfo =b_LadingInitBean.getMasterRowinfo();//主表一行数据
  //提单类型
  ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("-1");
  ArrayList opval = new ArrayList(); opval.add("提货单"); opval.add("退货单");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
          <%
            String key = "datasetlist";
            pageContext.setAttribute(key, list);
            int iPage = loginBean.getPageSize();
            String pageSize = String.valueOf(iPage);
          %>
     <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
     </td>
      <td class="td" nowrap align="right">
        <%
          if(loginBean.hasLimits(pageCode, op_add)){
            String add = "sumitForm("+Operate.ADD+",-1)";
            String th = "sumitForm("+b_LadingInitBean.TD_RETURN_ADD+",-1)";
            String addinit = "sumitForm("+b_LadingInitBean.LADING_INIT+",-1)";
            String initOver = "if(confirm('完成后不能再修改,确实要完成吗？'))sumitForm("+b_LadingInitBean.LADING_INIT_OVER+","+list.getRow()+")";
        %>
              <%if(b_LadingInitBean.sys_init.equals("0")||true){%>
              <input name="addtd" class="button" type="button" value="完成初始化(V)" onClick="<%=initOver%>" onKeyDown="return getNextElement();">&nbsp;&nbsp;
              <pc:shortcut key="v" script="<%=initOver%>" />

              <input name="addtd" class="button" type="button" value="新增初始数据(I)" onClick="sumitForm(<%=b_LadingInitBean.LADING_INIT%>,-1)" onKeyDown="return getNextElement();">&nbsp;&nbsp;
              <pc:shortcut key="i" script="<%=addinit%>" /><%}%>
        <%}if(hasSearchLimit){%>
         <input name="search2" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
         <pc:shortcut key="q" script="showFixedQuery()" />
        <%
        }
          if(b_LadingInitBean.retuUrl!=null){
            String s = "parent.location.href='"+b_LadingInitBean.retuUrl+"'";
         %>
        <input name="button22" type="button" class="button" onClick="parent.location.href='<%=b_LadingInitBean.retuUrl%>'" value=" 返回(C) " onKeyDown="return getNextElement();">
         <pc:shortcut key="c" script="<%=s%>" />
         <%}%>
      </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
    <td>&nbsp;&nbsp;</td>
    <%table.printTitle(pageContext, "height='20'");%><!--打印表头-->
    </tr>
    <%
    BigDecimal t_zsl = new BigDecimal(0), t_zje = new BigDecimal(0);
      int count = list.getRowCount();//行数
      int i=0;
      list.first();
      for(; i<count; i++)   {
        boolean isInit = false;
        //设置表格行的状态
        String zsl = list.getValue("zsl");
        if(b_LadingInitBean.isDouble(zsl))
          t_zsl = t_zsl.add(new BigDecimal(zsl));
        String zje = list.getValue("zje");
        if(b_LadingInitBean.isDouble(zje))
          t_zje = t_zje.add(new BigDecimal(zje));
        String rowClass =list.getValue("zt");//0.初始化,1.已开提单(已经审核),9.出库
        if(rowClass.equals("0")){
          isInit = true;
          rowClass ="class=td";
        }
        boolean hasReferenced = b_LadingInitBean.hasReferenced(list.getValue("tdid"));
        rowClass = engine.action.BaseAction.getStyleName(hasReferenced?"-1":rowClass);
        String loginId=b_LadingInitBean.loginId;
        String sprid=list.getValue("sprid");
        String rowzt =list.getValue("zt");
        boolean isCancer=rowzt.equals("1") && loginId.equals(sprid)&&b_LadingInitBean.isCanCancer(list.getValue("tdid"));
        boolean isCanOver = b_LadingInitBean.isCanOver(list.getValue("tdid"));

        boolean cansubmit=false;
        boolean submitType = b_LadingInitBean.submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
        String czyid = list.getValue("czyid");
        if(submitType&&czyid.equals(loginId))
          cansubmit=true;
        else if(!submitType)
          cansubmit=true;
        String isinit = list.getValue("isinit");
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>);" onClick="showDetail(<%=list.getRow()%>)">
      <td <%=rowClass%> align="center" nowrap>
   <%--        <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
       <%if(isInit&&cansubmit){%>
           <input name="image3" class="img" type="image" title='提交审批' onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
       <%if(isCancer){%>
           <input name="image3" class="img" type="image"  title='取消审批' onClick="if(confirm('确实要取消审批吗？'))sumitForm(<%=b_LadingInitBean.CANCER_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
       <%if((rowzt.equals("1"))){%>
         <input name="image3" class="img" type="image" title='出库确认' onClick="if(confirm('确认完成出库吗？'))sumitForm(<%=b_LadingInitBean.LADING_OUT%>,<%=list.getRow()%>)" src="../images/edit.old.gif" border="0"><%}%>

         <%if((rowzt.equals("0"))&&(isinit.equals("1"))){%>
         <input name="image3" class="img" type="image" title='完成初始化' onClick="if(confirm('完成后不能再修改,确实要完成吗？'))sumitForm(<%=b_LadingInitBean.LADING_INIT_OVER%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%>--%>
     </td>
       <%table.printCells(pageContext, rowClass);%><%--打印主表数据行--%>
    </tr>
    <%  list.next();
      }
      i=count+1;
      %>
      <tr>
      <td class="tdTitle" nowrap>本页合计</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zje%>' readonly></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      </tr>
      <tr>
      <td class="tdTitle" nowrap>总合计</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=b_LadingInitBean.getZsl()%>' readonly></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=b_LadingInitBean.getZje()%>' readonly></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      </tr>
      <%
      for(; i < iPage; i++){
        out.print("<tr><td>&nbsp;</td>");
        table.printBlankCells(pageContext, "class=td");//打印空的格子
        out.print("</tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
<%
  if(!b_LadingInitBean.masterIsAdd()){
    int row = b_LadingInitBean.getSelectedRow();//得到所选择的行号
    if(row >= 0)
      out.print("showSelected('tr_"+ row +"');");//打印标识颜色
  }
%>
//------------------------------以下与查询相关
function sumitFixedQuery(oper)
{
  //执行查询,提交表单
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery(){
  //点击查询按钮打开查询对话框
  <%if(hasSearchLimit){%>
   showFrame('fixedQuery', true, "", true);//显示层
   <%}%>
}
</SCRIPT>
<%if(hasSearchLimit){%><%--查询权限--%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD nowrap class="td">提单编号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="tdbh" name="tdbh$a" value='<%=b_LadingInitBean.getFixedQueryValue("tdbh$a")%>' maxlength='16' onKeyDown="return getNextElement();"></TD>
              <TD nowrap class="td">--</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="tdbh" name="tdbh$b" value='<%=b_LadingInitBean.getFixedQueryValue("tdbh$b")%>' maxlength='16' onKeyDown="return getNextElement();"></TD>
              </tr>
              <tr>
              <TD align="center" nowrap class="td">往来单位</TD>
              <td nowrap class="td" colspan=3>
              <input type="text" class="edbox" style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=b_LadingInitBean.dwdm%>' onchange="customerCodeSelect(this)" >
              <input type="text" name="dwmc"  style="width:260" value='<%=b_LadingInitBean.dwmc%>' class="edbox"  onchange="customerNameSelect(this)" >
              <INPUT TYPE="HIDDEN" NAME="dwtxid" value='<%=b_LadingInitBean.getFixedQueryValue("dwtxid")%>'>
              <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',fixedQueryform.dwtxid.value)">
              <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value='';">
              </td>
            </TR>
            <TR>
              <TD nowrap class="td">提单日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="tdrq$a" value='<%=b_LadingInitBean.getFixedQueryValue("tdrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(tdrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="tdrq$b" value='<%=b_LadingInitBean.getFixedQueryValue("tdrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(tdrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD nowrap class="td">回款日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="hkrq$a" value='<%=b_LadingInitBean.getFixedQueryValue("hkrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(hkrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="hkrq$b" value='<%=b_LadingInitBean.getFixedQueryValue("hkrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(hkrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:160">
              <%=deptBean.getList(b_LadingInitBean.getFixedQueryValue("deptid"))%></pc:select>
              </TD>
              <TD class="td" nowrap>业务员</TD>
              <TD class="td" nowrap>
                <pc:select name="personid" addNull="1" style="width:100">
                  <%=personBean.getList(b_LadingInitBean.getFixedQueryValue("personid"))%>
                </pc:select>
             </TD>

             </TR>
              <tr>
              <TD class="td" nowrap>制单人</TD>
              <TD class="td" nowrap>
              <INPUT class="edbox" style="WIDTH:160" id="czy" name="czy" value='<%=b_LadingInitBean.getFixedQueryValue("czy")%>' maxlength='16' onKeyDown="return getNextElement();">
             </TD>
              <TD class="td" nowrap>提单类型</TD>
              <TD nowrap class="td">
                <pc:select name="djlx" addNull="1" style="width:130" >
                <%=b_LadingInitBean.listToOption(lists, opkey.indexOf(b_LadingInitBean.getFixedQueryValue("djlx")))%>
               </pc:select>
              </TD>
             </tr>
            <tr>
             <td noWrap  class="td">客户类型</td>
             <td width="120" class="td">
              <%String khlx = b_LadingInitBean.getFixedQueryValue("khlx");%>
              <pc:select name="khlx" style="width:80" addNull="1" value="<%=khlx%>" >
               <pc:option value="A">A</pc:option> <pc:option value="C">C</pc:option>
              </pc:select>
            </td>
            </tr>
            <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%
                  String [] zt = b_LadingInitBean.zt;
                  String zt0="";
                  String zt3="";
                  for(int k=0;k<zt.length;k++)
                  {
                    if(zt[k].equals("0"))
                      zt0 = "checked";
                    else if(zt[k].equals("3"))
                      zt3 = "checked";
                  }
                %>
                <input type="checkbox" name="zt" value="0" <%=zt0%>>初始化
                <input type="checkbox" name="zt" value="3" <%=zt3%>>初始化完成
              </TD>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center">
                <% String ss = "sumitFixedQuery("+Operate.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value="查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script="<%=ss%>" />
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(X)" name="button2" onKeyDown="return getNextElement();">
                 <pc:shortcut key="x" script="hideFrame('fixedQuery')" />
              </TD>
            </TR>
          </TABLE>
       </TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<% out.print(retu);}}%>
</body>
</html>