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
  engine.erp.sale.xixing.B_SaleExchange b_SaleExchangeBean = engine.erp.sale.xixing.B_SaleExchange.getInstance(request);
  String pageCode = "exchange";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
    synchronized(b_SaleExchangeBean){
  String retu = b_SaleExchangeBean.doService(request, response);

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
  parent.location.href='exchange_edit.jsp';
  return;
}
function toTihd(){
  // 转到提货单
  parent.location.href='exchange_edit.jsp';
  return;
}
function showDetail(masterRow){
  //单击主表的一行激活的操作
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='exchange_buttom.jsp?operate=<%=b_SaleExchangeBean.SHOW_DETAIL%>&rownum='+masterRow;
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
//产品编码
function productCodeSelect(obj)
{
    SaleProdCodeChange(document.all['prod'], obj.form.name,'srcVar=tdid$cpid&srcVar=tdid$pm&srcVar=tdid$cpbm&srcVar=tdid$gg&issale=1','fieldVar=cpid&fieldVar=pm&fieldVar=cpbm&fieldVar=gg',obj.value);
}
//产品名称
function productNameSelect(obj)
{
    SaleProdNameChange(document.all['prod'], obj.form.name,'srcVar=tdid$cpid&srcVar=tdid$pm&srcVar=tdid$cpbm&srcVar=tdid$gg&issale=1','fieldVar=cpid&fieldVar=pm&fieldVar=cpbm&fieldVar=gg',obj.value);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>

<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">销售换货单</TD>
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

  EngineDataSet list = b_SaleExchangeBean.getMaterTable();//主表数据集
  HtmlTableProducer table = b_SaleExchangeBean.masterProducer;//主表数据的表格打印
  String curUrl = request.getRequestURL().toString();
  //RowMap m_RowInfo =b_SaleExchangeBean.getMasterRowinfo();//主表一行数据
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
        <%if(hasSearchLimit){%>
         <input name="search2" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
         <pc:shortcut key="q" script="showFixedQuery()" />
        <%
            }
            //if(b_SaleExchangeBean.retuUrl!=null){
            String s = "parent.location.href='../pub/main.jsp'";//+b_SaleExchangeBean.retuUrl+"'";
         %>
        <input name="button22" type="button" class="button" onClick="<%=s%>" value=" 返回(C) " onKeyDown="return getNextElement();">
         <pc:shortcut key="c" script="<%=s%>" />
      </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
    <td>
      <%if(loginBean.hasLimits(pageCode, op_add)){
       String add = "sumitForm("+Operate.ADD+",-1)";
       %>
        <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
        <pc:shortcut key="a" script='<%=add%>'/>
      <%}%>
      </td>
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
        if(b_SaleExchangeBean.isDouble(zsl))
          t_zsl = t_zsl.add(new BigDecimal(zsl));
        String zje = list.getValue("zje");
        if(b_SaleExchangeBean.isDouble(zje))
          t_zje = t_zje.add(new BigDecimal(zje));
        String rowClass =list.getValue("zt");//0.初始化,1.已开提单(已经审核),9.出库
        if(rowClass.equals("0")){
          isInit = true;
          rowClass ="class=td";
        }
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        String loginId=b_SaleExchangeBean.loginId;
        String sprid=list.getValue("sprid");
        String rowzt =list.getValue("zt");
        boolean isCancer=rowzt.equals("1") && loginId.equals(sprid)&&b_SaleExchangeBean.isCanCancer(list.getValue("tdid"));
        boolean isCanOver = b_SaleExchangeBean.isCanOver(list.getValue("tdid"));

        boolean cansubmit=false;
        boolean submitType = b_SaleExchangeBean.submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
        String czyid = list.getValue("czyid");
        if(submitType&&czyid.equals(loginId))
          cansubmit=true;
        else if(!submitType)
          cansubmit=true;
        String isinit = list.getValue("isinit");
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>);" onClick="showDetail(<%=list.getRow()%>)">
      <td <%=rowClass%> align="center" nowrap>
           <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
       <%if(isInit&&cansubmit&&!isinit.equals("1")){%>
           <input name="image3" class="img" type="image" title='提交审批' onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
       <%if(isCancer){%>
           <input name="image3" class="img" type="image"  title='取消审批' onClick="if(confirm('确实要取消审批吗？'))sumitForm(<%=b_SaleExchangeBean.CANCER_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
     <%if((rowzt.equals("1")||rowzt.equals("0"))&&loginBean.hasLimits(pageCode, op_cancel)){%>
         <input name="image3" class="img" type="image"   title='作废' onClick="if(confirm('确实要做废吗？'))sumitForm(<%=b_SaleExchangeBean.LADDING_CANCER%>,<%=list.getRow()%>)" src="../images/close.gif" border="0"><%}%>
       <%if((rowzt.equals("1")&&loginBean.hasLimits(pageCode, op_over))){%>
         <input name="image3" class="img" type="image" title='完成' onClick="if(confirm('确认完成吗？'))sumitForm(<%=b_SaleExchangeBean.LADING_OUT%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%>
     </td>
       <%table.printCells(pageContext, rowClass);%><%--打印主表数据行--%>
    </tr>
    <%  list.next();
      }
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
  if(!b_SaleExchangeBean.masterIsAdd()){
    int row = b_SaleExchangeBean.getSelectedRow();//得到所选择的行号
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
              <TD nowrap class="td">单据编号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="tdbh" name="tdbh" value='<%=b_SaleExchangeBean.getFixedQueryValue("tdbh")%>' maxlength='16' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>制单人</TD>
              <TD class="td" nowrap>
              <INPUT class="edbox" style="WIDTH:160" id="czy" name="czy" value='<%=b_SaleExchangeBean.getFixedQueryValue("czy")%>' maxlength='16' onKeyDown="return getNextElement();">
              </TD>
            </tr>
            <tr>
              <TD align="center" nowrap class="td">往来单位</TD>
              <td nowrap class="td" colspan=3>
              <input type="text" class="edbox" style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=b_SaleExchangeBean.dwdm%>' onchange="customerCodeSelect(this)" >
              <input type="text" name="dwmc"  style="width:260" value='<%=b_SaleExchangeBean.dwmc%>' class="edbox"  onchange="customerNameSelect(this)" >
              <INPUT TYPE="HIDDEN" NAME="dwtxid" value='<%=b_SaleExchangeBean.getFixedQueryValue("dwtxid")%>'>
              <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',fixedQueryform.dwtxid.value)">
              <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value='';">
              </td>
            </TR>
            <TR>
              <TD nowrap class="td">制单日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="czrq$a" value='<%=b_SaleExchangeBean.getFixedQueryValue("czrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(czrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="czrq$b" value='<%=b_SaleExchangeBean.getFixedQueryValue("czrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(czrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:160">
              <%=deptBean.getList(b_SaleExchangeBean.getFixedQueryValue("deptid"))%></pc:select>
              </TD>
              <TD class="td" nowrap>业务员</TD>
              <TD class="td" nowrap>
                <pc:select name="personid" addNull="1" style="width:130">
                  <%=personBean.getList(b_SaleExchangeBean.getFixedQueryValue("personid"))%>
                </pc:select>
             </TD>
             </TR>
            <%--TR>
                <TD align="center" nowrap class="td">产品代码</TD>
                <td nowrap class="td" colspan="3">
                <INPUT TYPE="HIDDEN" NAME="tdid$cpid" value="">
                <input class="edbox" style="WIDTH:80" name="tdid$cpbm" value='<%=b_SaleExchangeBean.getFixedQueryValue("tdid$cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this)" >
                <INPUT class="edbox" style="WIDTH:120" id="tdid$pm" name="tdid$pm" value='<%=b_SaleExchangeBean.getFixedQueryValue("tdid$pm")%>' maxlength='10' onKeyDown="return getNextElement();"  onchange="productNameSelect(this)">
                <INPUT class="edline" style="WIDTH:120" id="tdid$gg" name="tdid$gg" value='<%=b_SaleExchangeBean.getFixedQueryValue("tdid$gg")%>' maxlength='10' onKeyDown="return getNextElement();" readonly>
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=tdid$cpid&srcVar=tdid$pm&srcVar=tdid$cpbm&srcVar=tdid$gg&issale=1','fieldVar=cpid&fieldVar=pm&fieldVar=cpbm&fieldVar=gg')">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="tdid$cpid.value='';tdid$pm.value='';tdid$gg.value='';tdid$cpbm.value='';">
                </td>
            </tr--%>
            <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%
                  String [] zt = b_SaleExchangeBean.zt;
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