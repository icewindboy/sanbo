<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate, engine.html.*,com.borland.dx.dataset.Locate,java.util.* ,engine.project.*"%>
<%
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "employee_info";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.person.B_EmployeeInfo b_employeeinfoBean = engine.erp.person.B_EmployeeInfo.getInstance(request);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_LIST);
  LookUp personEducationBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_EDUCATION);
  LookUp dutyBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_DUTY);
  boolean isCanSearch=loginBean.hasLimits(pageCode,op_search);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanDel=loginBean.hasLimits(pageCode, op_delete);
  HtmlTableProducer masterProducer = b_employeeinfoBean.table;//主表数据的表格打印

  engine.common.PdfProducerFacade pdf = engine.common.PdfProducerFacade.getInstance(request);
  pdf.reportDirPath = getServletContext().getRealPath("/WEB-INF/report");

%>
<%
  String retu = b_employeeinfoBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();

  ArrayList opkey = new ArrayList(); opkey.add("0"); opkey.add("2");
  ArrayList opval = new ArrayList(); opval.add("在职"); opval.add("离职");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};

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

<script language="javascript" src="../scripts/exchangeselect.js"></script>
<script language="javascript">
  function toDetail()
  {
  location.href='employee_info_edit.jsp';
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
function productCodeSelect(obj)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}
function productNameSelect(obj,srcVars)
{
  ProdNameChange(document.all['prod'], obj.form.name, srcVars,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
  }
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD NOWRAP align="center">员工信息列表</TD>
</TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="100%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
     <%
       String key = "loginmanagedata";
       EngineDataSet tmp=b_employeeinfoBean.getMaterTable();
       pageContext.setAttribute(key, tmp);
       int iPage = loginBean.getPageSize();
       String pageSize = ""+iPage;

     %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"></pc:dbNavigator>
     </td>
      <td class="td" align="right">
        <%if(isCanSearch){%>
         <input name="search" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q) ">
         <pc:shortcut key="q" script="showFixedQuery()" />
        <%}%>
         <input name="sort" type="button" class="button" onClick="showSort()" value="排序(P)">
        <pc:shortcut key="p" script='showSort()'/>
         <% if(b_employeeinfoBean.retuUrl!=null){
             String s = "location.href='"+b_employeeinfoBean.retuUrl+"'";
         %>
      <input name="button2" type="button" class="button" onClick="location.href='<%=b_employeeinfoBean.retuUrl%>'" value=" 返回(C) ">
      <pc:shortcut key="c" script="<%=s%>" />
      <%}%>
     </td>
    </tr>
  </table>
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>
      <%if(loginBean.hasLimits(pageCode,op_add)){%>
     <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
     <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/>
     <%}%>
     </td>

       <%masterProducer.printTitle(pageContext, "height='20'");%><!--打印表头-->
       <td nowrap>姓名</td>
      <td nowrap>性别</td>
      <td nowrap>状态</td>
      <td nowrap>是否残疾</td>
      <td nowrap>下拉框中显示</td>
    </tr>
    <%//b_employeeinfoBean.fetchData(loginBean.getRowMin(request), loginBean.getRowMax(request), true);
      EngineDataSet list = b_employeeinfoBean.getMaterTable();
      int i=0;
      list.first();
      for(; i < list.getRowCount(); i++) {
        int row=list.getRow();
    %>
    <tr onDblClick="sumitForm(<%=b_employeeinfoBean.VIEW_DETAIL%>,<%=i%>)"  onclick="selectRow();">
      <td class="td" nowrap align="center">
      <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=b_employeeinfoBean.VIEW_DETAIL%>,<%=i%>)" src="../images/edit.gif" border="0">
       <%if((loginBean.hasLimits(pageCode,op_delete))&&(list.getValue("isDelete").equals("0"))){%>
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=i %>)" src="../images/del.gif" border="0">
      <%}%>
      </td>
      <%masterProducer.printCells(pageContext, "Class=td");%><%--打印主表数据行--%>
      <td class="td" nowrap><%=list.getValue("xm")%></td>
     <td class="td" nowrap><%=(list.getValue("sex").equals("1"))?"男":"女"%></td>
     <td class="td" nowrap><%=(list.getValue("isDelete").equals("2"))?"离职":"在职"%></td>
     <td class="td" nowrap align="right"><%=(list.getValue("isDeformity").equals("1"))?"是":"否"%></td>

     <td class="td" nowrap>
     <%
     if(loginBean.hasLimits(pageCode,op_add)||loginBean.hasLimits(pageCode,op_edit)){
       boolean isshow = list.getValue("isshow").equals("1");

       if(list.getValue("isDelete").equals("2")){
     %>
     <input type="radio" name="isshow_<%=row%>" value="1" onclick='sumitForm(<%=b_employeeinfoBean.OPERATE_SHOW%>,<%=row%>)' <%=isshow?" checked":""%>>显示
     <input type="radio" name="isshow_<%=row%>" value="0" onclick='sumitForm(<%=b_employeeinfoBean.OPERATE_HIDDEN%>,<%=row%>)' <%=isshow?"":" checked"%>>不显示
     <%}}%>
      </td>

    </tr>
    <%
        list.next();
      }
      for(; i < iPage; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>      
    </tr>
    <%}%>
  </table>
  </form>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
function sumitFixedQuery(oper)
  {
    lockScreenToWait("处理中, 请稍候！");
    fixedQueryform.operate.value = oper;
    fixedQueryform.submit();
  }
function showFixedQuery()
  {
    showFrame('fixedQuery', true, "", true);
  }
  </SCRIPT>
  <form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
    <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
    <div class="queryPop" id="fixedQuery" name="fixedQuery">
      <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
      <TABLE cellspacing=3 cellpadding=0 border=0>
        <TR>
          <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <td noWrap class="td" align="center">员工编码</td>
              <td noWrap class="td"><input type="text" name="bm" value='<%=b_employeeinfoBean.getFixedQueryValue("bm")%>' maxlength='6' style="width:131" class="edbox"></td>
              <td noWrap class="td" align="center">&nbsp;员工姓名&nbsp;</td>
              <td noWrap class="td"><input type="text" name="xm" value='<%=b_employeeinfoBean.getFixedQueryValue("xm")%>' maxlength='50' style="width:131" class="edbox"></td>

            </TR>
            <TR>
            <td noWrap class="td" align="center">工号</td>
              <td noWrap class="td"><input type="text" name="gh" value='<%=b_employeeinfoBean.getFixedQueryValue("gh")%>' maxlength='6' style="width:131" class="edbox"></td>
           </TR>
            <TR>
              <td noWrap class="td" align="center">部门</td>
              <td noWrap class="td">
              <pc:select name="deptid"  style="width:131" addNull="1">
                  <%=deptBean.getList(b_employeeinfoBean.getFixedQueryValue("deptid"))%>
              </pc:select>
              </td>
              <td noWrap class="td" align="center">是否在职</td>
              <td noWrap class="td">
                <pc:select name="isdelete" addNull="1" style="width:131" >
                <%=b_employeeinfoBean.listToOption(lists, opkey.indexOf(b_employeeinfoBean.getFixedQueryValue("isdelete")))%>
               </pc:select>
              </td>
            </TR>
            <TR>
              <td noWrap class="td" align="center">出生日期</td>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 131px" name="date_born$a" value='<%=b_employeeinfoBean.getFixedQueryValue("date_born$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(date_born$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
              </TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="date_born$b" value='<%=b_employeeinfoBean.getFixedQueryValue("date_born$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(date_born$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
             </TD>
            </TR>
            <TR>
            <TR>
              <td noWrap class="td" align="center">进厂日期</td>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="date_in$a" value='<%=b_employeeinfoBean.getFixedQueryValue("date_in$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(date_in$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
              </TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="date_in$b" value='<%=b_employeeinfoBean.getFixedQueryValue("date_in$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(date_in$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
             </TD>
            </TR>
            <TR>
            <td nowrap class="td">职务</td>
            <td nowrap class="td">
             <pc:select name="zw" addNull="1" style="width:131">
             <%=dutyBean.getList(b_employeeinfoBean.getFixedQueryValue("zw"))%>
             </pc:select>
             </td>
              <td nowrap class="td">学历</td>
              <td nowrap class="td">
              <pc:select name="study"  addNull="1"  style="width:131">
              <%=personEducationBean.getList( b_employeeinfoBean.getFixedQueryValue("study"))%>
              </pc:select>
              </td>
            </TR>
            <TR>
              <td noWrap class="td" align="center">保险日期</td>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="bxrq$a" value='<%=b_employeeinfoBean.getFixedQueryValue("bxrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(bxrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
              </TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="bxrq$b" value='<%=b_employeeinfoBean.getFixedQueryValue("bxrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(bxrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
             </TD>
            </TR>
            <TR>
              <td nowrap class="td">性别</td>
               <td nowrap class="td">
               <%
               String man = b_employeeinfoBean.getFixedQueryValue("sex");
               %>
               <input type=RADIO name="sex" value='' <%=man.equals("") ? " checked" : ""%>>全部&nbsp;<input type=RADIO name="sex" value='1' <%=man.equals("1")? " checked" : ""%>>男&nbsp; <input type=RADIO name="sex" value='0' <%=man.equals("0") ? " checked" : " "%>>女
               </TD>
              <td nowrap class="td">是否残疾</td>
               <td nowrap class="td">
               <%
               String isDeformity = b_employeeinfoBean.getFixedQueryValue("isDeformity");
               %>
               <input type=RADIO name="isDeformity" value='' <%=isDeformity.equals("") ? " checked" : ""%>>全部&nbsp;<input type=RADIO name="isDeformity" value='1' <%=isDeformity.equals("1")? " checked" : ""%>>是&nbsp; <input type=RADIO name="isDeformity" value='0' <%=isDeformity.equals("0") ? " checked" : " "%>>否
               </TD>
            </TR>
            <TR>
              <TD nowrap colspan=3 height=30 align="center">
                <% String qu = "sumitFixedQuery("+b_employeeinfoBean.OPERATE_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=b_employeeinfoBean.OPERATE_SEARCH%>)" type="button" value="查询(K)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="k" script="<%=qu%>" />
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(X)" name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script="hideFrame('fixedQuery')" />
              </TD>
            </TR>
          </TABLE></TD>
        </TR>
      </TABLE>
    </DIV>
  </form>
  <%--排序的查询框--%>
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
                    <%java.util.List fieldCodes = b_employeeinfoBean.orderFieldCodes;
                      java.util.List fieldNames = b_employeeinfoBean.orderFieldNames;
                      java.util.List selectedCodes = b_employeeinfoBean.selectedOrders;
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
<%out.print(retu);%>
</body>
</html>