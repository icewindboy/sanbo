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
  String pageCode = "package_list";
  String op_over = "op_over";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.store.B_Package b_PackageBean = engine.erp.store.B_Package.getInstance(request);
  String retu = b_PackageBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp personBean = LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_LIST);
  boolean isCanSearch=loginBean.hasLimits(pageCode,op_search);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanDel=loginBean.hasLimits(pageCode, op_delete);

  RowMap m_RowInfo = b_PackageBean.getMasterRowinfo();   //行到主表的一行信息
  LookUp personNativeBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_NATIVE);
  //engine.project.LookUp driverBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_DRIVER);
   engine.project.LookUp typeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PACKAGETYPE);//物资规格属性
  LookUp personEducationBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_EDUCATION);
  engine.project.LookUp driverBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_DRIVER);
  engine.project.LookUp CarHaoBean= engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_CAR);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  String typeClass = "class=edbox";
  String readonly = "";
  //boolean isCanEdit = true;
  boolean isCanDelete = true;
  EngineDataSet dsDWTX = b_PackageBean.getMaterTable();
  String curUrl = request.getRequestURL().toString();

  ArrayList opkey = new ArrayList(); opkey.add("0"); opkey.add("2");
typeBean.regData(dsDWTX,"funditemid");
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
  function toDetail()
  {

 parent.location.href='package_edit.jsp';

  }
  function showDetail(masterRow){
    selectRow();
    lockScreenToWait("处理中, 请稍候！");
    parent.bottom.location.href='package_bottom.jsp?operate=<%=b_PackageBean.SHOW_DETAIL%>&rownum='+masterRow;
    unlockScreenWait();
  }

  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }

//客户编码
function customerCodeSelect(obj)
{
    InsideCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
//客户名称
function customerNameSelect(obj)
{
  InsideNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD NOWRAP align="center">包装方案设置</TD>
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
       EngineDataSet tmp=b_PackageBean.getMaterTable();
       pageContext.setAttribute(key, tmp);
       int iPage = loginBean.getPageSize()-6;
       String pageSize = ""+iPage;
     %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"></pc:dbNavigator>
     </td>
      <td class="td" align="right">
        <%if(loginBean.hasLimits(pageCode,op_add)) { String qu = "showFixedQuery()";%>
        <%if(isCanSearch)%><input name="search" type="button" title = "查询"  class="button" onClick="showFixedQuery()" value=" 查询(Q) "><pc:shortcut key="q" script='<%=qu%>'/><%}%>
        <%if(loginBean.hasLimits(pageCode,op_add)) { String ret = "location.href='("+b_PackageBean.retuUrl+",-1)";%>
        <input name="button2" type="button" title = "返回" class="button" onClick="parent.location.href='<%=b_PackageBean.retuUrl%>'" value=" 返回(C) "></td><pc:shortcut key="c" script='<%=ret%>'/><%}%>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>
      <%if(loginBean.hasLimits(pageCode,op_add)){%>
     <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>)" src="../images/add_big.gif" border="0"><pc:shortcut key="a" script='sumitForm(<%=Operate.ADD%>)'/>
     <%}%>
     </td>
      <td nowrap>方案号</td>
      <td nowrap>方案名称</td>
      <td nowrap>包装类别</td>
     </tr>
    <%//b_PackageBean.fetchData(loginBean.getRowMin(request), loginBean.getRowMax(request), true);
      EngineDataSet list = b_PackageBean.getMaterTable();
      int i=0;
      list.first();
      for(; i < list.getRowCount(); i++) {
    %>
    <tr onDblClick="sumitForm(<%=b_PackageBean.VIEW_DETAIL%>,<%=i%>)" onClick="showDetail(<%=i%>)" >
      <td class="td" nowrap align="center">
      <input name="image2" class="img" type="image" title='<%=(loginBean.hasLimits(pageCode, op_edit)) ?"修改" :"查看"%>' onClick="sumitForm(<%=b_PackageBean.VIEW_DETAIL%>,<%=i%>)" src="../images/edit.gif" border="0">
       <%if(loginBean.hasLimits(pageCode,op_delete)){%>
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=i %>)" src="../images/del.gif" border="0">
      <%}%>
     </td>
      <td class="td" nowrap><%=list.getValue("package_code")%></td>
      <td class="td" nowrap><%=list.getValue("package_name")%></td>
    <td class="td" nowrap><%=typeBean.getLookupName(list.getValue("funditemid"))%></td>
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
              <td noWrap class="td" align="center"> 方案编号&nbsp;</td>

              <td noWrap class="td"><input type="text" name="package_code" value='<%=b_PackageBean.getFixedQueryValue("package_code")%>' maxlength='50' style="width:120" class="edbox"></td>

            <td noWrap class="td" align="center"> 方案名称&nbsp;</td>

              <td noWrap class="td"><input type="text" name="package_name" value='<%=b_PackageBean.getFixedQueryValue("package_name")%>' maxlength='50' style="width:120" class="edbox"></td>
           </tr>
            <TR>
              <%if(loginBean.hasLimits(pageCode,op_add))  {String qu = "sumitFixedQuery("+b_PackageBean.OPERATE_SEARCH+",-1)";%>
              <TD nowrap colspan=3 height=30 align="center">
                 <INPUT class="button" title = "查询" onClick="sumitFixedQuery(<%=b_PackageBean.OPERATE_SEARCH%>)" type="button" value=" 查询(Q) " name="button" onKeyDown="return getNextElement();"><pc:shortcut key="q" script='<%=qu%>'/><%}%>
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