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
  String pageCode = "document_move";
  String op_over = "op_over";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.person.B_Document_move documentBean = engine.erp.person.B_Document_move.getInstance(request);
   engine.project.LookUp personBean = LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_ALL);
  boolean isCanSearch=loginBean.hasLimits(pageCode,op_search);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanDel=loginBean.hasLimits(pageCode, op_delete);

  RowMap m_RowInfo = documentBean.getMasterRowinfo();   //行到主表的一行信息
  LookUp personNativeBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_NATIVE);
  LookUp personEducationBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_EDUCATION);

  String typeClass = "class=edbox";
  String readonly = "";
  //boolean isCanEdit = true;
  boolean isCanDelete = true;
  EngineDataSet dsDWTX = documentBean.getMaterTable();

  String retu = documentBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();

  ArrayList opkey = new ArrayList(); opkey.add("0"); opkey.add("2");
   boolean is_histoty =documentBean.isHistory;
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
  location.href='document_move_edit.jsp';
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
    <TD NOWRAP align="center">公文流转</TD>
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
       EngineDataSet tmp=documentBean.getMaterTable();
       pageContext.setAttribute(key, tmp);
       int iPage = loginBean.getPageSize();
       String pageSize = ""+iPage;
     %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"></pc:dbNavigator>
     </td>
      <td class="td" align="right">
        <%if(loginBean.hasLimits(pageCode,op_add)) { String qu = "showFixedQuery()";%>
        <%if(isCanSearch)%><input name="search" type="button" title = "查询"  class="button" onClick="showFixedQuery()" value=" 查询(Q) "><pc:shortcut key="q" script='<%=qu%>'/><%}%>
        <%if(loginBean.hasLimits(pageCode,op_add)) { String ret = "location.href='("+documentBean.retuUrl+",-1)";%>
        <input name="button2" type="button" title = "返回" class="button" onClick="location.href='<%=documentBean.retuUrl%>'" value=" 返回(C) "></td><pc:shortcut key="c" script='<%=ret%>'/><%}%>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>
      <%if(loginBean.hasLimits(pageCode,op_add)&&!is_histoty)  {String add = "sumitForm("+Operate.ADD+",-1)";%>
     <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0"><pc:shortcut key="a" script='<%=add%>'/>
     <%}%>
     <%if(!is_histoty){%>
     <input name="image" class="img" type="image" title="查看已读收件历史记录" onClick="sumitForm(<%=documentBean.ShowHistry%>,-1)" src="../images/select_prod.gif" border="0">
      <%}%>
     </td>

      <td nowrap>发件日期</td>
      <td nowrap>发件部门</td>
      <td nowrap>发件人</td>
      <td nowrap>收发类型</td>

      <td nowrap>主题</td>
      <td nowrap>标题</td>

      <td nowrap>文件类型</td>
      <td nowrap>优先级</td>


     </tr>
    <%//documentBean.fetchData(loginBean.getRowMin(request), loginBean.getRowMax(request), true);

      EngineDataSet list = documentBean.getMaterTable();
      deptBean.regData(list,"deptid");
      int i=0;
      list.first();
      for(; i < list.getRowCount(); i++) {

    %>
    <tr onDblClick="sumitForm(<%=documentBean.VIEW_DETAIL%>,<%=i%>)" >
      <td class="td" nowrap align="center">

     <img style='cursor:hand'  title="查看联系人"  src='../images/person.gif' border=0 onClick="sumitForm(<%=documentBean.VIEW_PERSON%>,<%=i%>)">

      <input name="image2" class="img" type="image" title="查看"  onClick="sumitForm(<%=documentBean.VIEW_DETAIL%>,<%=i%>)" src="../images/edit.gif" border="0">
       <%if(loginBean.hasLimits(pageCode,op_delete)){%>
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=i %>)" src="../images/del.gif" border="0">
      <%}%>

     </td>


      <td class="td" nowrap><%=list.getValue("senddate")%></td>
     <td class="td" nowrap><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
       <td class="td" nowrap><%=list.getValue("sendperson")%></td>
     <td class="td" nowrap><%=list.getValue("inout_type").equals("0")?"发文件":"收文件"%></td>
     <td class="td" nowrap><%=list.getValue("topic")%></td>
       <td class="td" nowrap><%=list.getValue("caption")%></td>
     <td class="td" nowrap><%=list.getValue("file_type")%></td>
     <td class="td" nowrap>
     <%=list.getValue("filelevel").equals("2")?"紧急":(list.getValue("filelevel").equals("1")?"缓慢":"普通")%>
    </td>

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
              <td noWrap  class="td">收发类型</td>
             <td width="80" class="td">
              <%String inout_type = documentBean.getFixedQueryValue("inout_type");%>
              <pc:select name="inout_type" style="width:80" addNull="1" value="<%=inout_type%>" >
               <pc:option value="0">发文件</pc:option>
               <pc:option value="1">收文件</pc:option>
              </pc:select>
            </td>
            <td noWrap  class="td">文件类型</td>
             <td width="80" class="td">
              <%String file_type = documentBean.getFixedQueryValue("file_type");%>
              <pc:select name="file_type" style="width:80" addNull="1" combox="1" value="<%=file_type%>" >
               <pc:option value="请示类">请示类</pc:option>
               <pc:option value="通知类">通知类</pc:option>
               <pc:option value="文件类">文件类</pc:option>

              </pc:select>
            </td>
             </TR>
              <TR>
              <td noWrap class="td" align="left"> &nbsp;主题&nbsp;</td>
              <td noWrap class="td"><input type="text" name="topic" value='<%=documentBean.getFixedQueryValue("topic")%>' maxlength='50' style="width:80" class="edbox"></td>
              <td noWrap class="td">优先级</td>
              <td width="80" class="td">
             <%String filelevel = documentBean.getFixedQueryValue("filelevel"); %>
              <pc:select name="filelevel" style="width:80" addNull="1" value="<%=filelevel%>" >
               <pc:option value="0">普通</pc:option>
               <pc:option value="1">缓慢</pc:option>
               <pc:option value="2">紧急</pc:option>
              </pc:select>

   </td>
             </TR>
               <TR>
              <td noWrap class="td" align="left"> &nbsp;标题&nbsp;</td>
              <td noWrap class="td" colspan="3"><input type="text" name="caption" value='<%=documentBean.getFixedQueryValue("caption")%>' maxlength='50' style="width:215" class="edbox"></td>

             </TR>



            <TR>
             <TD nowrap colspan=4 height=30 align="center">
                <div align="center">
                  <INPUT class="button" title = "查询" onClick="sumitFixedQuery(1066)" type="button" value=" 查询(Q) " name="button" onKeyDown="return getNextElement();">
                  <script language='javascript'>GetShortcutControl(81,"sumitFixedQuery(1066,-1)");</script>
                    <INPUT class="button" title = "关闭" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(C) " name="button2" onKeyDown="return getNextElement();">
                  <script language='javascript'>GetShortcutControl(67,"hideFrame('fixedQuery')");</script>
                </div>

            </TR>
          </TABLE></TD>
        </TR>
      </TABLE>
    </DIV>
  </form>
<%out.print(retu);%>
</body>
</html>


