<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate, com.borland.dx.dataset.Locate,java.util.*"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.project.*,engine.erp.person.*"%>
<%@ page import="java.util.*"%>
<%
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "train_feedback";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.person.shengyu.B_TrainFeedback b_TrainFeedbackBean = engine.erp.person.shengyu.B_TrainFeedback.getInstance(request);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_BOTH);//BEAN_DEPT
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp dutyBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_DUTY);
  boolean isCanSearch=loginBean.hasLimits(pageCode,op_search);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanDel=loginBean.hasLimits(pageCode, op_delete);

  RowMap m_RowInfo = b_TrainFeedbackBean.getMasterRowinfo();   //行到主表的一行信息
  LookUp personNativeBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_NATIVE);
  LookUp personEducationBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_EDUCATION);


  String typeClass = "class=edbox";
  String readonly = "";
  //boolean isCanEdit = true;
  boolean isCanDelete = true;

  EngineDataSet dsDWTX = b_TrainFeedbackBean.getMaterTable();

%>
<%
  String retu = b_TrainFeedbackBean.doService(request, response);
if(retu.indexOf("location.href=")>-1)
{
  out.print(retu);
  return;
}
String curUrl = request.getRequestURL().toString();

//ArrayList opkey = new ArrayList(); opkey.add("0"); opkey.add("2");

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
  function toDetail()
{
  location.href='train_feedback_edit.jsp';
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
    <TD NOWRAP align="center">培训反馈信息表</TD>
</TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
     <%
       String key = "loginmanagedata";
EngineDataSet tmp=b_TrainFeedbackBean.getMaterTable();
pageContext.setAttribute(key, tmp);
int iPage = loginBean.getPageSize();
String pageSize = ""+iPage;
     %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"></pc:dbNavigator>
     </td>
      <td class="td" align="right">
        <%if(loginBean.hasLimits(pageCode,op_add)) { String qu = "showFixedQuery()";%>
        <%if(isCanSearch)%><input name="search" type="button" title = "查询"  class="button" onClick="showFixedQuery()" value=" 查询(Q) "><pc:shortcut key="q" script='<%=qu%>'/><%}%>
        <%if(loginBean.hasLimits(pageCode,op_add)) { String ret = "location.href='("+b_TrainFeedbackBean.retuUrl+",-1)";%>
        <input name="button2" type="button" title = "返回" class="button" onClick="location.href='<%=b_TrainFeedbackBean.retuUrl%>'" value=" 返回(C) "></td><pc:shortcut key="c" script='<%=ret%>'/><%}%>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>
      <%if(loginBean.hasLimits(pageCode,op_add))  {String add = "sumitForm("+Operate.ADD+",-1)";%>
     <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0"><pc:shortcut key="a" script='<%=add%>'/>
     <%}%>
     </td>
      <td nowrap>员工</td>
      <td nowrap>所属部门</td>
      <td nowrap>培训项目</td>
      <td nowrap>培训起始时间</td>
      <td nowrap>培训结束时间</td>
      <td nowrap>对教师的满意度</td>
      <td nowrap>对教材的满意度</td>
      <td nowrap>是否实现培训目标</td>
      <td nowrap>总结及建议</td>
      <td nowrap>备注</td>
      </tr>
    <%//b_TrainFeedbackBean.fetchData(loginBean.getRowMin(request), loginBean.getRowMax(request), true);
      ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("2"); opkey.add("3");opkey.add("4");opkey.add("5");
      ArrayList opval = new ArrayList(); opval.add("很满意"); opval.add("满意"); opval.add("一般");opval.add("不满意");opval.add("很差");
      ArrayList[] lists  = new ArrayList[]{opkey, opval};
      String ss=m_RowInfo.get("tech_material_ok");
      String tt=m_RowInfo.get("techcher_ok");
      int te =0;
      int ke =0;
      EngineDataSet list = b_TrainFeedbackBean.getMaterTable();
      int i=0;
      list.first();
      for(; i < list.getRowCount(); i++) {
        try{
          te=Integer.parseInt(list.getValue("tech_material_ok"))-1;
          }catch(Exception e){te=0;}

          try{
            ke=Integer.parseInt(list.getValue("techcher_ok"))-1;
            }catch(Exception e){te=0;}
    %>
    <tr onDblClick="sumitForm(<%=b_TrainFeedbackBean.VIEW_DETAIL%>,<%=i%>)" >
      <td class="td" nowrap align="center">
      <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=b_TrainFeedbackBean.VIEW_DETAIL%>,<%=i%>)" src="../images/edit.gif" border="0">
       <%if((loginBean.hasLimits(pageCode,op_delete))){%>
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=i %>)" src="../images/del.gif" border="0">
      <%}%>
      </td>
      <td class="td" nowrap><%=personBean.getLookupName(list.getValue("personid"))%></td>
      <td class="td" nowrap><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
      <td class="td" nowrap><%=list.format("train_proj")%></td>
      <td class="td" nowrap><%=list.getValue("train_start")%></td>
     <td class="td" nowrap><%=list.getValue("train_end")%></td>
     <td class="td" nowrap ><%=opval.get(te)%></td>
    <td class="td" nowrap ><%=opval.get(ke)%></td>

     <td class="td" nowrap><%=list.format("isImpl").equals("1")?"是":"否"%></td>
    <td class="td" nowrap><%=list.format("sum_up")%></td>
    <td class="td" nowrap><%=list.format("memo")%></td>
    <%--<td class="td" nowrap><%=list.format("fee_prepay")%></td>--%>
     </tr>
    <%
      list.next();
      }
      for(; i < iPage; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
     <td class="td">&nbsp;</td>
     <td class="td">&nbsp;</td>
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
  <form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;">
    <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
    <div class="queryPop" id="fixedQuery" name="fixedQuery">
      <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
      <TABLE cellspacing=3 cellpadding=0 border=0>
        <TR>
          <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
            <%--<td noWrap class="td" align="center">&nbsp;培训编号&nbsp;</td>
            <td noWrap class="td"><input type="text" name="pxbh" value='<%=b_TrainFeedbackBean.getFixedQueryValue("pxbh")%>' maxlength='50' style="width:120" class="edbox"></td>--%>
              <td noWrap class="tdtitle" align="center">&nbsp;姓名&nbsp;</td>
              <td noWrap class="td"><input type="text" id="back_ID$xm" name="back_ID$xm" value='<%=b_TrainFeedbackBean.getFixedQueryValue("back_ID$xm")%>' maxlength='50' style="width:131" class="edbox"></td>
             <%-- <td noWrap class="tdtitle" align="center">职务</td>
              <TD nowrap class="td"><pc:select name="duty" addNull="1" style="width:160">
             <%=dutyBean.getList(b_TrainFeedbackBean.getFixedQueryValue("duty"))%></pc:select>
  <%--<td noWrap class="td"><input type="text" id="pxjs" name="pxjs" value='<%=b_TrainFeedbackBean.getFixedQueryValue("pxjs")%>' maxlength='6' style="width:120" class="edbox"></td>--%>
             </TR>
            <TR>
              <TD class="tdtitle" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:130">
         <%=deptBean.getList(b_TrainFeedbackBean.getFixedQueryValue("deptid"))%></pc:select>
             <td class="tdtitle" nowrap>培训项目</td>
             <td nowrap class="td"><input type="text" id="train_proj" name="train_proj" value='<%=b_TrainFeedbackBean.getFixedQueryValue("train_proj")%>' maxlength='50' style="width:131" class="edbox"></td>
            </TR>

            <TR>
              <TD nowrap class="tdtitle">培训时间</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="train_start" style="WIDTH: 130px" name="train_start" value='<%=b_TrainFeedbackBean.getFixedQueryValue("train_start")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(train_start);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
               </TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="train_end" style="WIDTH: 130px" name="train_end" value='<%=b_TrainFeedbackBean.getFixedQueryValue("train_end")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(train_end);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
              </TD>
            </TR>
            <TR>
              <%if(loginBean.hasLimits(pageCode,op_add))  {String qu = "sumitFixedQuery("+b_TrainFeedbackBean.OPERATE_SEARCH+",-1)";%>
              <TD nowrap colspan=3 height=30 align="center">
                 <INPUT class="button" title = "查询" onClick="sumitFixedQuery(<%=b_TrainFeedbackBean.OPERATE_SEARCH%>)" type="button" value=" 查询(F) " name="button" onKeyDown="return getNextElement();"><pc:shortcut key="f" script='<%=qu%>'/><%}%>
                 <%if(loginBean.hasLimits(pageCode,op_add))  {String clo = "hideFrame('fixedQuery')";%>
                 <td class="td" nowrap align="center">
                 <INPUT class="button" title = "关闭" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X) " name="button2" onKeyDown="return getNextElement();"><pc:shortcut key="x" script='<%=clo%>'/><%}%>
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
