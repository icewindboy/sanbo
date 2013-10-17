<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.*,engine.project.*,com.borland.dx.dataset.Locate"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
  String pageCode  = "letter";
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
function toDetail(){
  // 转到主从明细
  location.href='letter_edit.jsp';
}
function sumitForm(oper, row)
{
  //新增或修改
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit()
}
function showFixedQuery(){
  //点击查询按钮打开查询对话框
   showFrame('fixedQuery', true, "", true);//显示层
}
function sumitFixedQuery(oper)
{
lockScreenToWait("处理中, 请稍候！");
fixedQueryform.operate.value = oper;
fixedQueryform.submit();
}
function corpQueryCodeSelect(obj,srcVars)
{
  ProvideCodeChange(document.all['prod'], obj.form.name, srcVars,
                    'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function corpQueryNameSelect(obj,srcVars)
{
  ProvideNameChange(document.all['prod'], obj.form.name, srcVars,
                    'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
</script>
<%
  engine.erp.imports.B_Letter  b_LetterBean = engine.erp.imports.B_Letter.getInstance(request);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);//往来单位
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);//人员
  LookUp bankBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_BANK);//银行
  LookUp deptBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_DEPT);//部门
  EngineDataSet list = b_LetterBean.getOneTable();
  if(!loginBean.hasLimits("letter", request, response))
    return;
  /*将小写的字符型的金额转变成大写的字符型的汉字金额*/
  String money=request.getParameter("money");
  if(money!=null)
  {
    String cstr=b_LetterBean.c_Format(money);
    out.println("<script language=javascript>");
    out.println("parent.form1.dxje.value='"+cstr+"';");
    out.println("</script>");
    return;
  }
  /*----------------------------------------*/
  String retu = b_LetterBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
%>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">信用证</TD>
  </TR>
</TABLE>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<%
  String curUrl = request.getRequestURL().toString();
  boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
  boolean isCanAdd = loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit = loginBean.hasLimits(pageCode, op_edit);
  boolean isCanSearch = loginBean.hasLimits(pageCode,op_search);
  corpBean.regData(list,"dwtxid");
  personBean.regData(list,"personid");//approverID
  bankBean.regData(list,"yhid");
  deptBean.regData(list,"deptid");
  RowMap row = b_LetterBean.getRowinfo();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
    <td class="td" nowrap>
      <%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
      int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
         </td> <TD class="td" align="right">
         <input name="search" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)">
         <pc:shortcut key="q" script="showFixedQuery()" />
      <%if(b_LetterBean.retuUrl!=null){%><input name="button2" type="button" align="Right"
    class="button" onClick="location.href='<%=b_LetterBean.retuUrl%>'" value=" 返回 "border="0"><%}%></TD>
   </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr id="tr_0" class="tableTitle">
      <td nowrap width=30>
      <%if(loginBean.hasLimits(pageCode, op_add)){%>
         <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
         <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/>
       <%}%></td>
      <td nowrap >信用证号码</td>
      <td nowrap >银行</td>
      <td height="18" nowrap >开证方式</td>
      <td nowrap >有效期</td>
      <td nowrap >开证地点</td>
      <td nowrap >受益人</td>
      <td nowrap >通知行</td>
      <td nowrap >金额</td>
     <!-- <td nowrap >大写金额</td> -->
      <td nowrap >分批装运</td>
      <td nowrap >转运</td>
      <td nowrap >信用证类型</td>
      <td nowrap >价格条款</td>
      <td nowrap >状态</td>
      <td nowrap >申请部门</td>
      <td nowrap >申请人</td>
      <td nowrap >申请日期</td>
    </tr>
     <%
      list.first();
      int i=0;
      for(;i<list.getRowCount();i++)
      {
          String state=list.getValue("state");
          boolean isComplete = !state.equals("8")&&loginBean.hasLimits(pageCode, op_over);//有强制完成的权限
          String title= !state.equals("8") ? "修改" : "查看";
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)">
      <td nowrap class=td>
      <%if(loginBean.hasLimits(pageCode, op_edit)){%><div align="center">
      <input name="image1" class="img" type="image" title="<%=title%>" onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <%}%>
      <%if(isComplete){%><input name="image2" class="img" type="image" title='完成' onClick="if(confirm('是否完成该纪录？'))sumitForm(<%=b_LetterBean.LADING_OVER%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%>
      </div></td>
      <td nowrap class=td><%=list.getValue("letterno")%></td><!--信用证号码-->
      <td nowrap class=td><%=bankBean.getLookupName(list.getValue("yhid"))%></td><!--银行-->
      <td nowrap class=td><%String mykzfs=list.getValue("kzfs");
         if(mykzfs.equals("1"))out.print("信开");
         if(mykzfs.equals("2"))out.print("简电开");
         if(mykzfs.equals("3"))out.print("电开");
        %></td><!--开证方式-->
      <td nowrap class=td><%=list.getValue("useful_life")%></td><!--有效期-->
      <td nowrap class=td>
      <%String myaddress=list.getValue("address");
         if(myaddress.equals("1"))out.print("在受益人所在国家");
         if(myaddress.equals("2"))out.print("在开证行柜台");
      %></td><!--开证地点-->
      <td nowrap class=td><%=corpBean.getLookupName(list.getValue("dwtxid"))%></td><!--受益人-->
      <td nowrap class=td><%=bankBean.getLookupName(list.getValue("yh_yhid"))%></td><!--通知行-->
      <td nowrap class=td align="right"><%=list.getValue("xxje")%></td><!--小写金额-->
    <!--  <td nowrap class=td><%=list.getValue("dxje")%></td>--><!--大写金额-->
      <td nowrap class=td>
      <%String myfpzy=list.getValue("fpzy");
         if(myfpzy.equals("0"))out.print("不允许");
         if(myfpzy.equals("1"))out.print("允许");
      %>
      </td><!--分批装运-->
      <td nowrap class=td>
      <%String myzy=list.getValue("zy");
           if(myzy.equals("0"))out.print("不允许");
           if(myzy.equals("1"))out.print("允许");
      %>
      </td><!--转运-->
      <td nowrap class=td>
      <%String mylettertype=list.getValue("lettertype");
         if(mylettertype.equals("1"))out.print("即期付款");
         if(mylettertype.equals("2"))out.print("承税");
         if(mylettertype.equals("3"))out.print("议付");
         if(mylettertype.equals("4"))out.print("迟期付款");
     %>
      </td><!--信用证类型-->
      <td nowrap class=td>
      <%=list.getValue("jgtk")%>
      </td><!--价格条款-->
         <td nowrap class=td>
      <%String mystate=list.getValue("state");
      if(mystate.equals("0"))out.print("未完成");
      if(mystate.equals("8"))out.print("完成");
      %>
      </td><!--状态-->
      <td nowrap class=td><%=deptBean.getLookupName(list.getValue("deptid"))%></td><!--申请部门-->
      <td nowrap class=td><%=personBean.getLookupName(list.getValue("personid"))%></td><!--申请人-->
      <td nowrap class=td><%=list.getValue("apply_date")%></td><!--申请日期-->
    </tr>
    <%list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr id="tr_2">
     <!-- <td nowrap class=tdComplete>&nbsp;</td>-->
      <td nowrap class=tdComplete>&nbsp;</td>
      <td nowrap class=tdComplete>&nbsp;</td>
      <td nowrap class=tdComplete>&nbsp;</td>
      <td nowrap class=tdComplete>&nbsp;</td>
      <td nowrap class=tdComplete>&nbsp;</td>
      <td nowrap class=tdComplete>&nbsp;</td>
      <td nowrap class=tdComplete>&nbsp;</td>
      <td nowrap class=tdComplete>&nbsp;</td>
      <td nowrap class=tdComplete>&nbsp;</td>
      <td nowrap class=tdComplete>&nbsp;</td>
      <td nowrap class=tdComplete>&nbsp;</td>
      <td nowrap class=tdComplete>&nbsp;</td>
      <td nowrap class=tdComplete>&nbsp;</td>
      <td nowrap class=tdComplete>&nbsp;</td>
      <td nowrap class=tdComplete>&nbsp;</td>
      <td nowrap class=tdComplete>&nbsp;</td>
      <td nowrap class=tdComplete>&nbsp;</td>
    </tr>
    <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
</SCRIPT>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <%b_LetterBean.table.printWhereInfo(pageContext);%>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script='<%="sumitFixedQuery("+ Operate.FIXED_SEARCH +",-1)"%>'/>
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X)" name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script="hideFrame('fixedQuery')"/>
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