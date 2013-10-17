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
  String pageCode = "train_plan";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.person.shengyu.B_TrainPlan B_TrainPlanBean = engine.erp.person.shengyu.B_TrainPlan.getInstance(request);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_BOTH);//BEAN_DEPT
  boolean isCanSearch=loginBean.hasLimits(pageCode,op_search);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanDel=loginBean.hasLimits(pageCode, op_delete);

  RowMap m_RowInfo = B_TrainPlanBean.getMasterRowinfo();   //行到主表的一行信息
  LookUp personNativeBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_NATIVE);
  LookUp personEducationBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_EDUCATION);


  String typeClass = "class=edbox";
  String readonly = "";
  //boolean isCanEdit = true;
  boolean isCanDelete = true;

  EngineDataSet dsDWTX = B_TrainPlanBean.getMaterTable();

%>
<%
  String retu = B_TrainPlanBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();

  ArrayList opkey = new ArrayList(); opkey.add("0"); opkey.add("2");

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
  location.href='train_plan_edit.jsp';
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
    <TD NOWRAP align="center">培训计划</TD>
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
       EngineDataSet tmp=B_TrainPlanBean.getMaterTable();
       pageContext.setAttribute(key, tmp);
       int iPage = loginBean.getPageSize();
       String pageSize = ""+iPage;
     %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"></pc:dbNavigator>
     </td>
      <td class="td" align="right">
        <%if(loginBean.hasLimits(pageCode,op_add)) { String qu = "showFixedQuery()";%>
        <%if(isCanSearch)%><input name="search" type="button" title = "查询"  class="button" onClick="showFixedQuery()" value=" 查询(Q) "><pc:shortcut key="q" script='<%=qu%>'/><%}%>
        <%if(loginBean.hasLimits(pageCode,op_add)) { String ret = "location.href='("+B_TrainPlanBean.retuUrl+",-1)";%>
        <input name="button2" type="button" title = "返回" class="button" onClick="location.href='<%=B_TrainPlanBean.retuUrl%>'" value=" 返回(C) "></td><pc:shortcut key="c" script='<%=ret%>'/><%}%>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>
      <%if(loginBean.hasLimits(pageCode,op_add))  {String add = "sumitForm("+Operate.ADD+",-1)";%>
     <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0"><pc:shortcut key="a" script='<%=add%>'/>
     <%}%>
     </td>
      <td nowrap>培训编号</td>
      <td nowrap>计划名称</td>
      <td nowrap>负责单位</td>
      <td nowrap>负责部门</td>
      <td nowrap>负责人</td>
      <td nowrap>培训类型</td>
      <td nowrap>培训形式</td>
      <td nowrap>培训目标</td>

    </tr>
    <%//B_TrainPlanBean.fetchData(loginBean.getRowMin(request), loginBean.getRowMax(request), true);
      EngineDataSet list = B_TrainPlanBean.getMaterTable();
      int i=0;
      list.first();
      for(; i < list.getRowCount(); i++) {
    %>
    <tr onDblClick="sumitForm(<%=B_TrainPlanBean.VIEW_DETAIL%>,<%=i%>)" >
      <td class="td" nowrap align="center">
      <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=B_TrainPlanBean.VIEW_DETAIL%>,<%=i%>)" src="../images/edit.gif" border="0">
       <%if((loginBean.hasLimits(pageCode,op_delete))){%>
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=i %>)" src="../images/del.gif" border="0">
      <%}%>
      </td>
      <td class="td" nowrap><%=list.format("pxbh")%></td>
      <td class="td" nowrap><%=list.format("jhmc")%></td>
      <td class="td" nowrap><%=list.format("fzdw")%></td>
      <td class="td" nowrap><%=deptBean.getLookupName(list.getValue("fzbm"))%></td>
      <td class="td" nowrap><%=list.format("fzr")%></td>
      <td class="td" nowrap><%=list.format("pxlx")%></td>
      <td class="td" nowrap><%=list.format("pxxs")%></td>
      <td class="td" nowrap><%=list.format("pxmb")%></td>
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
              <td noWrap class="td" align="center">&nbsp;培训编号&nbsp;</td>
              <td noWrap class="td"><input type="text" name="pxbh" value='<%=B_TrainPlanBean.getFixedQueryValue("pxbh")%>' maxlength='50' style="width:120" class="edbox"></td>
              <td noWrap class="td" align="center">培训教师</td>
              <td noWrap class="td"><input type="text" name="pxjs" value='<%=B_TrainPlanBean.getFixedQueryValue("pxjs")%>' maxlength='6' style="width:120" class="edbox"></td>
             </TR>

            <TR>
              <td nowrap class="tdTitle">培训时间</td>
              <td nowrap class="td"><input type="text"  <%=typeClass%> name="pxsj" value='' maxlength="10" style="width:120"  onChange="checkDate(this)" onKeyDown="return getNextElement();" <%=readonly%>>
              <a href="#"><img src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onClick="selectDate(pxsj);"></a>
              <%--<td noWrap class="td" align="center">负责部门</td>
              <td noWrap class="td"><input type="text" name="fzbm" value='<%=B_TrainPlanBean.getFixedQueryValue("fzbm")%>' maxlength='6' style="width:120" class="edbox"></td>
               --%>
             <td nowrap class="tdTitle">负责部门</td>
             <td nowrap class="td">
             <%if((isCanEdit)){%>
             <pc:select name="deptid" addNull="1" style="width:131">
             <%=deptBean.getList(m_RowInfo.get("deptid"))%>
              </pc:select>
             <%}else{%>
             <input type="text"  value='<%=deptBean.getLookupName(m_RowInfo.get("deptid"))%>'  style="width:130" class="edline"  readonly>
             <%}%>
             </td>
            </TR>
          <TR>
              <td noWrap class="td" align="center">&nbsp;计划名称&nbsp;</td>
              <td noWrap class="td"><input type="text" name="jhmc" value='<%=B_TrainPlanBean.getFixedQueryValue("jhmc")%>' maxlength='50' style="width:120" class="edbox"></td>
              <td noWrap class="td" align="center">培训类型</td>
              <td noWrap class="td"><input type="text" name="pxlx" value='<%=B_TrainPlanBean.getFixedQueryValue("pxlx")%>' maxlength='6' style="width:120" class="edbox"></td>
             </TR>

            <TR>
              <%if(loginBean.hasLimits(pageCode,op_add))  {String qu = "sumitFixedQuery("+B_TrainPlanBean.OPERATE_SEARCH+",-1)";%>
              <TD nowrap colspan=3 height=30 align="center">
                 <INPUT class="button" title = "查询" onClick="sumitFixedQuery(<%=B_TrainPlanBean.OPERATE_SEARCH%>)" type="button" value=" 查询(Q) " name="button" onKeyDown="return getNextElement();"><pc:shortcut key="q" script='<%=qu%>'/><%}%>
                 <%if(loginBean.hasLimits(pageCode,op_add))  {String clo = "hideFrame('fixedQuery')";%>
                 <td class="td" nowrap align="center">
                 <INPUT class="button" title = "关闭" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(C) " name="button2" onKeyDown="return getNextElement();"><pc:shortcut key="c" script='<%=clo%>'/><%}%>
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


