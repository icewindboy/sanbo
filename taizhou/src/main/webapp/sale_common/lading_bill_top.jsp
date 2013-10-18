<%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal,java.util.ArrayList,engine.html.*,engine.project.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
  String op_cancel = "op_cancel";
  String  op_outstore = "op_outstore";
%>
<%
  engine.erp.sale.B_LadingBill b_ladingbillBean = engine.erp.sale.B_LadingBill.getInstance(request);
  String pageCode = "lading_bill";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
    synchronized(b_ladingbillBean){
  String retu = b_ladingbillBean.doService(request, response);

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

<script language="javascript">
function toDetail(operate,rownum){
  // 转到主从明细
  parent.location.href='lading_bill_edit.jsp?operate='+operate+'&rownum='+rownum;
  return;
}
function toTihd(operate,rownum){
  // 转到提货单
  parent.location.href='lading_bill_edit.jsp?operate='+operate+'&rownum='+rownum;
  return;
}
function toTweihd(operate,rownum){
  // 转到退货单
  parent.location.href='back_lading_bill_edit.jsp?operate='+operate+'&rownum='+rownum;
  return;
}
function showDetail(masterRow){
  //单击主表的一行激活的操作
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='lading_bill_buttom.jsp?operate=<%=b_ladingbillBean.SHOW_DETAIL%>&rownum='+masterRow;
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
function showFixedQuery(){
  //点击查询按钮打开查询对话框
   showFrame('fixedQuery', true, "", true);//显示层
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>

<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">销售货物管理</TD>
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
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);//引用仓库信息

  EngineDataSet list = b_ladingbillBean.getMaterListTable();//主表数据集
  HtmlTableProducer table = b_ladingbillBean.masterListProducer;//主表数据的表格打印
  String curUrl = request.getRequestURL().toString();

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
            String th = "sumitForm("+b_ladingbillBean.TD_RETURN_ADD+",-1)";
        %>
              <input name="addtd" class="button" type="button" value="新增提货单(A)" onClick="toTihd(<%=Operate.ADD%>,-1)" onKeyDown="return getNextElement();">&nbsp;&nbsp;
              <pc:shortcut key="a" script="<%=add%>" />
              <input name="addth" class="button" type="button" value="新增退货单(S)" onClick="toTweihd(<%=b_ladingbillBean.TD_RETURN_ADD%>,-1)" onKeyDown="return getNextElement();">
              <pc:shortcut key="s" script="<%=th%>" />
        <%}if(hasSearchLimit){%>
         <input name="search2" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
         <pc:shortcut key="q" script="showFixedQuery()" />
        <%
        }
          //if(b_ladingbillBean.retuUrl!=null){
            String s = "parent.location.href='../pub/main.jsp'";//+b_ladingbillBean.retuUrl+"'";
         %>
        <input name="button22" type="button" class="button" onClick="<%=s%>" value=" 返回(C) " onKeyDown="return getNextElement();">
         <pc:shortcut key="c" script="<%=s%>" />
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
        if(b_ladingbillBean.isDouble(zsl))
          t_zsl = t_zsl.add(new BigDecimal(zsl));
        String zje = list.getValue("zje");
        if(b_ladingbillBean.isDouble(zje))
          t_zje = t_zje.add(new BigDecimal(zje));
        String rowClass =list.getValue("zt");//0.初始化,1.已开提单(已经审核),9.出库
        if(rowClass.equals("0")){
          isInit = true;
          rowClass ="class=td";
        }
        boolean hasReferenced = b_ladingbillBean.hasReferenced(list.getValue("tdid"));
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        String loginId=b_ladingbillBean.loginId;
        String sprid=list.getValue("sprid");
        String rowzt =list.getValue("zt");
        boolean isCancer=rowzt.equals("1") && loginId.equals(sprid)&&b_ladingbillBean.isCanCancer(list.getValue("tdid"));
        boolean isCanOver = b_ladingbillBean.isCanOver(list.getValue("tdid"));

        boolean cansubmit=false;
        boolean submitType = b_ladingbillBean.submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
        String czyid = list.getValue("czyid");
        if(submitType&&czyid.equals(loginId))
          cansubmit=true;
        else if(!submitType)
          cansubmit=true;
        String isinit = list.getValue("isinit");
        String djlx = list.getValue("djlx");
        String dbstr = "toTihd("+Operate.EDIT+","+list.getRow()+")";
        if(djlx.equals("-1"))
          dbstr = "toTweihd("+Operate.EDIT+","+list.getRow()+")";
        String tdid = list.getValue("tdid");
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="<%=dbstr%>" onClick="showDetail(<%=list.getRow()%>)">
      <td <%=rowClass%> align="center" nowrap>
           <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
       <%if(isInit&&cansubmit&&!isinit.equals("1")){%>
           <input name="image3" class="img" type="image" title='提交审批' onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
       <%if(isCancer){%>
           <input name="image3" class="img" type="image"  title='取消审批' onClick="if(confirm('确实要取消审批吗？'))sumitForm(<%=b_ladingbillBean.CANCER_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
     <%if((rowzt.equals("1")||rowzt.equals("0"))&&loginBean.hasLimits(pageCode, op_cancel)){%>
         <input name="image3" class="img" type="image"   title='作废' onClick="if(confirm('确实要做废吗？'))sumitForm(<%=b_ladingbillBean.LADDING_CANCER%>,<%=tdid%>)" src="../images/close.gif" border="0"><%}%>
       <%if((rowzt.equals("2")&&loginBean.hasLimits(pageCode, op_outstore))){%>
         <input name="image3" class="img" type="image" title='出库确认' onClick="if(confirm('确认完成出库吗？'))sumitForm(<%=b_ladingbillBean.LADING_OUT%>,<%=tdid%>)" src="../images/edit.old.gif" border="0"><%}%>
     </td>
       <%table.printCells(pageContext, rowClass);%><%--打印主表数据行--%>
    </tr>
    <%  list.next();
      }
      i=count+2;
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
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zje%>' readonly></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
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
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=b_ladingbillBean.getZsl()%>' readonly></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(b_ladingbillBean.getZje(),"#0.00")%>' readonly></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
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
  if(!b_ladingbillBean.masterIsAdd()){
    int row = b_ladingbillBean.getSelectedRow();//得到所选择的行号
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
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="tdbh" name="tdbh$a" value='<%=b_ladingbillBean.getFixedQueryValue("tdbh$a")%>' maxlength='16' onKeyDown="return getNextElement();"></TD>
              <TD nowrap class="td">--</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="tdbh" name="tdbh$b" value='<%=b_ladingbillBean.getFixedQueryValue("tdbh$b")%>' maxlength='16' onKeyDown="return getNextElement();"></TD>
              </tr>
              <tr>
              <TD align="center" nowrap class="td">往来单位</TD>
              <td nowrap class="td" colspan=3>
              <input type="text" class="edbox" style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=b_ladingbillBean.dwdm%>' onchange="customerCodeSelect(this)" >
              <input type="text" name="dwmc"  style="width:260" value='<%=b_ladingbillBean.dwmc%>' class="edbox"  onchange="customerNameSelect(this)" >
              <INPUT TYPE="HIDDEN" NAME="dwtxid" value='<%=b_ladingbillBean.getFixedQueryValue("dwtxid")%>'>
              <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',fixedQueryform.dwtxid.value)">
              <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value='';">
              </td>
            </TR>
            <TR>
              <TD nowrap class="td">提单日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="tdrq$a" value='<%=b_ladingbillBean.getFixedQueryValue("tdrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(tdrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="tdrq$b" value='<%=b_ladingbillBean.getFixedQueryValue("tdrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(tdrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD nowrap class="td">回款日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="hkrq$a" value='<%=b_ladingbillBean.getFixedQueryValue("hkrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(hkrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="hkrq$b" value='<%=b_ladingbillBean.getFixedQueryValue("hkrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(hkrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:160">
              <%=deptBean.getList(b_ladingbillBean.getFixedQueryValue("deptid"))%></pc:select>
              </TD>
              <TD class="td" nowrap>提单类型</TD>
              <TD nowrap class="td">
                <pc:select name="djlx" addNull="1" style="width:130" >
                <%=b_ladingbillBean.listToOption(lists, opkey.indexOf(b_ladingbillBean.getFixedQueryValue("djlx")))%>
               </pc:select>
              </TD>
             </TR>
              <tr>
              <TD class="td" nowrap>制单人</TD>
              <TD class="td" nowrap>
              <INPUT class="edbox" style="WIDTH:160" id="czy" name="czy" value='<%=b_ladingbillBean.getFixedQueryValue("czy")%>' maxlength='16' onKeyDown="return getNextElement();">
             </TD>
              <TD class="td" nowrap>业务员</TD>
              <TD class="td" nowrap>
                <pc:select name="personid" addNull="1" style="width:130">
                  <%=personBean.getList(b_ladingbillBean.getFixedQueryValue("personid"))%>
                </pc:select>
             </TD>
             </tr>
             <tr>
                <td   noWrap class="td">仓库</td>
                <td  noWrap class="td">
                  <pc:select name="storeid" addNull="1" style="width:110">
                    <%=storeBean.getList(b_ladingbillBean.getFixedQueryValue("storeid"))%> </pc:select>
                  </td>
              <TD class="td" nowrap>码单号</TD>
              <TD class="td" nowrap>
              <INPUT class="edbox" style="WIDTH:160" id="djh" name="djh" value='<%=b_ladingbillBean.getFixedQueryValue("djh")%>' maxlength='16' onKeyDown="return getNextElement();">
             </TD>
             </tr>
            <TR>
              <TD class="td" nowrap>品名</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="tdid$pm" name="tdid$pm" value='<%=b_ladingbillBean.getFixedQueryValue("tdid$pm")%>' maxlength='10' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="tdid$gg" name="tdid$gg" value='<%=b_ladingbillBean.getFixedQueryValue("tdid$gg")%>' maxlength='10' onKeyDown="return getNextElement();"></TD>
            </TR>
            <tr>
             <td noWrap  class="td">客户类型</td>
             <td width="120" class="td">
              <%String khlx = b_ladingbillBean.getFixedQueryValue("khlx");%>
              <pc:select name="khlx" style="width:80" addNull="1" value="<%=khlx%>" >
               <pc:option value="A">A</pc:option>
               <pc:option value="C">C</pc:option>
              </pc:select>
            </td>
            </tr>
            <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%
                  String [] zt = b_ladingbillBean.zt;
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
                <input type="checkbox" name="zt" value="2" <%=zt2%>>已出库未确认
                <input type="checkbox" name="zt" value="8" <%=zt8%>>已全部出库
                <input type="checkbox" name="zt" value="4" <%=zt4%>>作废

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
<%out.print(retu);}} %>
</body>
</html>