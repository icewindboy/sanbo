<%--内部结算管理--%><%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,java.math.BigDecimal,engine.action.Operate,java.util.ArrayList,engine.html.*,engine.project.*"%>
<%@ page import="engine.dataset.EngineDataSet,com.borland.dx.dataset.DataSet"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
  String op_cancel = "op_cancel";
%>
<%
engine.erp.finance.xixing.B_InCheck b_InCheckBean = engine.erp.finance.xixing.B_InCheck.getInstance(request);
String pageCode = "in_check";
if(!loginBean.hasLimits(pageCode, request, response))
  return;
engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);

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
<script language="javascript" src="../scripts/exchangeselect.js"></script>
<script language="javascript">
function toDetail(){
  // 转到主从明细
  parent.location.href='in_check_edit.jsp';
}
function masterAdd(){
  // 转到主从明细
  parent.location.href='in_check_finance.jsp';
}
function showDetail(masterRow){
  //单击主表的一行激活的操作
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='in_check_buttom.jsp?operate=<%=b_InCheckBean.SHOW_DETAIL%>&rownum='+masterRow;
  unlockScreenWait();
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
function showFixedQuery(){
  //点击查询按钮打开查询对话框
   showFrame('fixedQuery', true, "", true);//显示层
}
function showSort(){
  hideFrame('fixedQuery')
  showFrame('divOrder', true, "", true);
}
function submitSort(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  getColumnArray();
  sortform.operate.value = oper;
  sortform.submit();
}
function getColumnArray()
{
  sortform.sortColumnStr.value = fnGetOptionValue(',', sortColumns);
}
</script>
<%
  synchronized(b_InCheckBean){
  String retu = b_InCheckBean.doService(request, response);

    if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
    {
      out.print(retu);
      return;
    }
    boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
    LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);//部门
    EngineDataSet list = b_InCheckBean.getMaterTable();//主表数据集
    HtmlTableProducer masterProducer = b_InCheckBean.masterProducer;//主表数据的表格打印
    String curUrl = request.getRequestURL().toString();
    RowMap m_RowInfo =b_InCheckBean.getMasterRowinfo();
    //提单类型
    ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("-1");
    ArrayList opval = new ArrayList(); opval.add("销售收款"); opval.add("销售退款");
    ArrayList[] lists  = new ArrayList[]{opkey, opval};
    //masterProducer.setHtmlPrintCellListener(new ListCellListener());
%>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">内部结算管理</TD>
  </TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
          <%
            String key = "datasetlist";
            pageContext.setAttribute(key, list);
            int iPage = loginBean.getPageSize()-6;
            String pageSize = String.valueOf(iPage);
          %>
     <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
     </td>
      <td class="td" nowrap align="right">
        <%if(hasSearchLimit){%>
          <input name="search2" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
          <pc:shortcut key="q" script="showFixedQuery()" />
        <input name="sort" type="button" class="button" onClick="showSort()" value="排序(P)">
        <pc:shortcut key="p" script='showSort()'/>
        <%}%>
        <%if(b_InCheckBean.retuUrl!=null){String ret = "parent.location.href='"+b_InCheckBean.retuUrl+"'";%>
        <input name="button22" type="button" class="button" onClick="parent.location.href='<%=b_InCheckBean.retuUrl%>'" value="返回(C)" onKeyDown="return getNextElement();">
        <pc:shortcut key="c" script="<%=ret%>" />
       <%}%>
      </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
    <td nowrap>
     <%if(loginBean.hasLimits(pageCode, op_add)){
     String add = "sumitForm("+b_InCheckBean.MASTER_ADD+",-1)";
    %>
    <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=b_InCheckBean.MASTER_ADD%>,-1)" src="../images/add.gif" border="0">
    <pc:shortcut key="A" script="<%=add%>" />
    <%}%>
   </td>
    <%masterProducer.printTitle(pageContext, "height='20'");%><!--打印表头-->
    </tr>
    <%
     BigDecimal  t_zje = new BigDecimal(0);
     BigDecimal  t_hxje = new BigDecimal(0);
     BigDecimal  t_whxje = new BigDecimal(0);
      int count = list.getRowCount();//行数
      int i=0;
      list.first();
      for(; i<count; i++)   {
        String zje = list.getValue("je");
        if(b_InCheckBean.isDouble(zje))
          t_zje = t_zje.add(new BigDecimal(zje));
        String hxje = list.getValue("hxje");
        if(b_InCheckBean.isDouble(hxje))
          t_hxje = t_hxje.add(new BigDecimal(hxje));
        String whxje = list.getValue("whxje");
        if(b_InCheckBean.isDouble(whxje))
          t_whxje = t_whxje.add(new BigDecimal(whxje));
        boolean isInit = false;
        String zt = list.getValue("zt");
        String rowClass =list.getValue("zt");//0.初始化,1.已开提单(已经审核),9.出库
        if(rowClass.equals("0")){
          isInit = true;
          rowClass ="class=td";
        }
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        String loginid=b_InCheckBean.loginid;
        String sprid=list.getValue("sprid");
        String czyid = list.getValue("czyid");
        String rowzt =list.getValue("zt");
        boolean isCancer=rowzt.equals("1") && loginid.equals(sprid);
        boolean cancer=rowzt.equals("0")||rowzt.equals("8")||rowzt.equals("4");
        String togo = (zt.equals("8")||zt.equals("1"))?String.valueOf(Operate.EDIT):b_InCheckBean.MASTER_EDIT;

        boolean cansubmit=false;
        boolean submitType = b_InCheckBean.submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
        if(submitType&&czyid.equals(loginid))
          cansubmit=true;
        else if(!submitType)
          cansubmit=true;
        //boolean iscanover =Math.abs(Double.parseDouble(list.getValue("hxje").equals("")?"0":list.getValue("hxje")))>0;
    %>
    <tr id="tr_<%=list.getRow()%>"  onDblClick="sumitForm(<%=togo%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
       <td <%=rowClass%> align="center"  nowrap>
        <%if(rowzt.equals("1")){%><input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
        <%}else{%><input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=b_InCheckBean.MASTER_EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
        <%}%>
       <%if(isInit&&cansubmit){%>
             <input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
      <%if(isCancer){%>
            <input name="image3" class="img" type="image"  title='取消审批' onClick="sumitForm(<%=b_InCheckBean.CANCER_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
     <%if(rowzt.equals("1")&&loginBean.hasLimits(pageCode, op_over)){%>
            <input name="image3" class="img" type="image"   title='核销完成' onClick="if(confirm('完成后不能修改,确认要完成吗？'))sumitForm(<%=b_InCheckBean.BALANCE_OVER%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%>
      </td>
       <%masterProducer.printCells(pageContext, rowClass);%><%--打印主表数据行--%>
    </tr>
    <%  list.next();
      }
      i=count+1;
     %>
      <tr>
      <td class="tdTitle" nowrap>本页收款</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td align="right" class="td"></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zje%>' readonly></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_hxje%>' readonly></td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_whxje%>' readonly></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      </tr>
      <tr>
      <td class="tdTitle" nowrap>总收款</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td align="right" class="td"></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=b_InCheckBean.getZje()%>' readonly></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=b_InCheckBean.getZhxje()%>' readonly></td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=b_InCheckBean.getZwhxje()%>' readonly></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      </tr>
      <%
      for(; i < iPage; i++){
        out.print("<tr><td>&nbsp;</td>");
        masterProducer.printBlankCells(pageContext, "class=td");//打印空的格子
        out.print("</tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
<%
/*
  if(!b_InCheckBean.masterIsAdd()){
    int row = b_InCheckBean.getSelectedRow();
    if(row >= 0)
      out.print("showSelected('tr_"+ row +"');");//打印标识颜色
  }
*/
%>
//------------------------------以下与查询相关
function sumitFixedQuery(oper)
{
  //执行查询,提交表单
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}

</SCRIPT>
<%if(hasSearchLimit){%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD nowrap class="td">日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="rq$a" value='<%=b_InCheckBean.getFixedQueryValue("rq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(rq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="rq$b" value='<%=b_InCheckBean.getFixedQueryValue("rq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(rq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <td noWrap class="td" align="center">金额</td>
              <td noWrap class="td"><input type="text" name="je$a" value='<%=b_InCheckBean.getFixedQueryValue("je$a")%>' maxlength='50' style="width:131" class="edbox"></td>
              <td noWrap class="td" align="center">--</td>
              <td noWrap class="td"><input type="text" name="je$b" value='<%=b_InCheckBean.getFixedQueryValue("je$b")%>' maxlength='50' style="width:131" class="edbox"></td>
            </TR>
            <TR>
              <td noWrap class="td" align="center">内部结算单号</td>
              <td noWrap class="td"><input type="text" name="nbjsdh$a" value='<%=b_InCheckBean.getFixedQueryValue("nbjsdh$a")%>' maxlength='50' style="width:131" class="edbox"></td>
              <td noWrap class="td" align="center">--</td>
              <td noWrap class="td"><input type="text" name="nbjsdh$b" value='<%=b_InCheckBean.getFixedQueryValue("nbjsdh$b")%>' maxlength='50' style="width:131" class="edbox"></td>
            </TR>
            <TR>
              <TD class="td" nowrap>收款部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:160">
              <%=deptBean.getList(b_InCheckBean.getFixedQueryValue("deptid"))%></pc:select>
              </TD>
             </TR>
             <TD class="td" nowrap>付款部门</TD>
              <TD nowrap class="td"><pc:select name="kc__deptid" addNull="1" style="width:160">
              <%=deptBean.getList(b_InCheckBean.getFixedQueryValue("kc__deptid"))%></pc:select>
              </TD>
             </TR>
            <TR>
              <TD class="td" nowrap>业务员</TD>
              <TD class="td" nowrap>
                <pc:select name="personid" addNull="1" style="width:100">
                  <%=personBean.getList(b_InCheckBean.getFixedQueryValue("personid"))%>
                </pc:select>
             </TD>
             <td noWrap class="td" align="center">&nbsp;制单人&nbsp;</td>
              <td noWrap class="td"><input type="text" name="czy" value='<%=b_InCheckBean.getFixedQueryValue("czy")%>' maxlength='50' style="width:131" class="edbox"></td>
            </TR>
           <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%
                  String [] zt = b_InCheckBean.zt;
                  String zt0="";
                  String zt9="";
                  String zt1="";
                  String zt8="";
                  for(int k=0;k<zt.length;k++)
                  {
                    if(zt[k].equals("0"))
                      zt0 = "checked";
                    else if(zt[k].equals("9"))
                      zt9 = "checked";
                    else if(zt[k].equals("1"))
                      zt1 = "checked";
                    else if(zt[k].equals("8"))
                      zt8 = "checked";
                  }
                %>
                <input type="checkbox" name="zt" value="0" <%=zt0%>>未审
                <input type="checkbox" name="zt" value="9" <%=zt9%>>审批中
                <input type="checkbox" name="zt" value="1" <%=zt1%>>已审
                <input type="checkbox" name="zt" value="8" <%=zt8%>>完成
                </td>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center">
                <% String ss = "sumitFixedQuery("+Operate.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value="查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script="<%=ss%>" />
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(X)" name="button2" onKeyDown="return getNextElement();">
                 <pc:shortcut key="x" script="hideFrame('fixedQuery')" />              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<%}%>
<%--
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="320" height="450" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
--%>
<form name="sortform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="sortColumnStr" VALUE="">
  <div class="queryPop" id="divOrder" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('divOrder')" href="javascript:"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD><TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD colspan="4" nowrap class="td"><table cellspac="0" cellpadding="0" width="100%" border="0">
                  <tr>
                    <%java.util.List fieldCodes = b_InCheckBean.orderFieldCodes;
                      java.util.List fieldNames = b_InCheckBean.orderFieldNames;
                      java.util.List selectedCodes = b_InCheckBean.selectedOrders;
                      StringBuffer buf = new StringBuffer("<script language='javascript'>var oOption;");
                      //TOption(type, value, text, isEnable, rowClick, checkName, isChecked, checkClick)
                      for(i=0; i<fieldCodes.size(); i++)
                      {
                        String fieldCode = (String)fieldCodes.get(i);
                        if(selectedCodes.indexOf(fieldCode) >= 0)
                          continue;
                        buf.append("oOption = new TOption('none','").append(fieldCode).append("',");
                        buf.append("'").append(fieldNames.get(i)).append("',true, null);");
                        buf.append("fnAddOption(showColumns, oOption);");
                      }
                      buf.append("</script>");
                      //
                      StringBuffer sot = new StringBuffer("<script language='javascript'>");
                      for(i=0; i<selectedCodes.size(); i++)
                      {
                        String fieldCode = (String)selectedCodes.get(i);
                        int index = fieldCodes.indexOf(fieldCode);
                        if(index < 0)
                          continue;
                        sot.append("oOption = new TOption('none','").append(fieldCode).append("',");
                        sot.append("'").append(fieldNames.get(index)).append("',true,null);");
                        sot.append("fnAddOption(sortColumns, oOption);");
                      }
                      sot.append("</script>");
                    %>
                    <td><table width="100%" cellspacing="0" cellpadding="0" border="0">
                        <TR>
                          <td class=td> <table cellSpacing="0" cellPadding="0" border="0" width="100%">
                              <tr>
                                <td class="td" valign="top" nowrap height="25"><b>可选列名</b></td>
                              </tr>
                              <tr>
                                <td><div style="overflow-y: auto; width: 120; height: 160; background-color: white; border: 1 solid #777777;" onselectstart="return CancelSelect();">
                                    <table id="showColumns" name="showColumns" width="100%" cellSpacing="0" cellPadding="0" border="0" onclick="SelectOption(this, false)" ondblclick="fnExchangeSelect(showColumns, sortColumns, false)">
                                    </table>
                                    <%=buf%> </div></td>
                              </tr>
                            </table></td>
                        </tr>
                      </table></td>
                    <td><table cellSpacing="0" cellPadding="0" border="0" height="135" width="100%">
                        <tr>
                          <td class="td" valign="top" nowrap height="25">&nbsp;</td>
                        </tr>
                        <tr>
                          <td height="55"></td>
                        </tr>
                        <tr>
                          <td><input name="button3" type="button" class="edbox" style="width:20;" onclick="fnExchangeSelect(showColumns, sortColumns, false)" value="&gt;" title="添加排序列名"></td>
                        </tr>
                        <tr>
                          <td height="2"></td>
                        </tr>
                        <tr>
                          <td><input name="button3" type="button" class="edbox" style="width:20;" onclick="fnRemoveMultiOption(sortColumns)" value="&lt;" title="移去排序列名"></td>
                        </tr>
                        <tr>
                          <td height="55"></td>
                        </tr>
                      </table></td>
                    <td><table cellSpacing="0" cellPadding="0" border="0" width="100%">
                        <tr>
                          <td class="td" valign="top" nowrap height="25"><b>排序列名</b></td>
                        </tr>
                        <tr>
                          <td><div style="overflow-y: auto; width: 120; height: 160; background-color: white; border: 1 solid #777777;" onselectstart="return CancelSelect();">
                              <table id="sortColumns" name="sortColumns" width="100%" cellSpacing="0" cellPadding="0" border="0" onclick="SelectOption(this)" ondblclick="fnExchangeSelect(sortColumns, showColumns, false)">
                              </table>
                              <%=sot%> </div></td>
                        </tr>
                      </table></td>
                </table></TD>
            </tr>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="submitSort(<%=engine.action.Operate.ORDERBY%>)" type="button" value=" 排序 " name="button">
                <INPUT class="button" onClick="hideFrame('divOrder');" type="button" value=" 关闭 " name="button2">
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<iframe id="interframe1" src="" class="frameBox" width="328" height="200" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
<%out.print(retu);}%>
</body>
</html>