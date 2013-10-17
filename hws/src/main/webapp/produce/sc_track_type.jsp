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
  String pageCode = "sc_track_type";

  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.produce.B_SC_Track_Type Track_TypeBean = engine.erp.produce.B_SC_Track_Type.getInstance(request);
   engine.project.LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);



  boolean isCanSearch=loginBean.hasLimits(pageCode,op_search);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanDel=loginBean.hasLimits(pageCode, op_delete);

  RowMap m_RowInfo = Track_TypeBean.getMasterRowinfo();   //行到主表的一行信息
  LookUp personNativeBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_NATIVE);
  LookUp personEducationBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_EDUCATION);

  String typeClass = "class=edbox";
  String readonly = "";
  //boolean isCanEdit = true;
  boolean isCanDelete = true;
  EngineDataSet dsDWTX = Track_TypeBean.getMaterTable();

  String retu = Track_TypeBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();

  ArrayList opkey = new ArrayList(); opkey.add("1");opkey.add("2");
  ArrayList opval = new ArrayList(); opval.add("膜类型"); opval.add("纸类型");
  ArrayList[] list_prop  = new ArrayList[]{opkey, opval};

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
  location.href='sc_tracktype_edit.jsp';
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
    <TD NOWRAP align="center">生产跟踪单类型</TD>
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
       EngineDataSet tmp=Track_TypeBean.getMaterTable();
       pageContext.setAttribute(key, tmp);
       int iPage = loginBean.getPageSize();
       String pageSize = ""+iPage;
     %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"></pc:dbNavigator>
     </td>
      <td class="td" align="right">
        <%if(loginBean.hasLimits(pageCode,op_add)) { String qu = "showFixedQuery()";%>
        <%if(isCanSearch)%><input name="search" type="button" title = "查询"  class="button" onClick="showFixedQuery()" value=" 查询(Q) "><pc:shortcut key="q" script='<%=qu%>'/><%}%>
        <%if(loginBean.hasLimits(pageCode,op_add)) { String ret = "location.href='("+Track_TypeBean.retuUrl+",-1)";%>
        <input name="button2" type="button" title = "返回" class="button" onClick="location.href='<%=Track_TypeBean.retuUrl%>'" value=" 返回(C) "></td><pc:shortcut key="c" script='<%=ret%>'/><%}%>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>
      <%if(loginBean.hasLimits(pageCode,op_add))  {String add = "sumitForm("+Operate.ADD+",-1)";%>
     <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0"><pc:shortcut key="a" script='<%=add%>'/>
     <%}%>
     </td>

      <td nowrap>类型编号</td>
      <td nowrap>类型名称</td>
      <td nowrap>属性</td>
     </tr>
    <%//Track_TypeBean.fetchData(loginBean.getRowMin(request), loginBean.getRowMax(request), true);
      EngineDataSet list = Track_TypeBean.getMaterTable();
      int i=0;
      list.first();
      for(; i < list.getRowCount(); i++) {

    %>
    <tr onDblClick="sumitForm(<%=Track_TypeBean.VIEW_DETAIL%>,<%=i%>)" >
      <td class="td" nowrap align="center">
      <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Track_TypeBean.VIEW_DETAIL%>,<%=i%>)" src="../images/edit.gif" border="0">
       <%if(loginBean.hasLimits(pageCode,op_delete)){%>
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=i %>)" src="../images/del.gif" border="0">
      <%}%>

     </td>


      <td class="td" nowrap><%=list.getValue("type_code")%></td>



      <td class="td" nowrap><%=list.getValue("type_name")%></td>
    <%if(list.getValue("type_prop").equals("1")){%>
      <td class="td" nowrap>A类型</td>
    <%}if(list.getValue("type_prop").equals("2")){%>
      <td class="td" nowrap>B类型</td>
    <%}if(list.getValue("type_prop").equals("")){%>
       <td class="td" nowrap></td>
       <%} %>
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
              <td noWrap class="td" align="center">&nbsp;类型编号&nbsp;</td>
              <td noWrap class="td">
              <%if((isCanEdit)){%><input type="text" name="type_code" value='<%=Track_TypeBean.getFixedQueryValue("trans_code")%>' maxlength='50' style="width:120" class="edbox"></td>
              <%}else{%><input type="text" name="type_code" value='<%=Track_TypeBean.getFixedQueryValue("trans_code")%>' maxlength='50' style="width:120" class="edline" readonly>
             <%}%>
             </td>
            <td nowrap class="tdTitle">类型名称</td>
             <td nowrap class="td">
           <%if((isCanEdit)){%>
            <input type="text" name="type_name" value='<%=Track_TypeBean.getFixedQueryValue("type_name")%>' maxlength='50' style="width:120" class="edbox"></td>
             <%}else{%>
             <input type="text" name="type_name" value='<%=Track_TypeBean.getFixedQueryValue("type_name")%>' maxlength='50' style="width:120" class="edline" readonly>
             <%}%>
          </td>
          <td nowrap class="tdTitle">属性</td>
             <td nowrap class="td">
           <%if((isCanEdit)){%>
            <pc:select name="type_prop" addNull="1" style="width:130" >
                <%=Track_TypeBean.listToOption(list_prop, opkey.indexOf(Track_TypeBean.getFixedQueryValue("type_prop")))%>
               </pc:select>
             <%}else{%>
             <input type="text" name="type_prop" value='<%=Track_TypeBean.getFixedQueryValue("type_prop")%>' maxlength='50' style="width:120" class="edline" readonly>
             <%}%>
          </td>
             </TR>


            <TR>
              <%if(loginBean.hasLimits(pageCode,op_add))  {String qu = "sumitFixedQuery("+Track_TypeBean.OPERATE_SEARCH+",-1)";%>
              <TD nowrap colspan=3 height=30 align="center">
                 <INPUT class="button" title = "查询" onClick="sumitFixedQuery(<%=Track_TypeBean.OPERATE_SEARCH%>)" type="button" value=" 查询(Q) " name="button" onKeyDown="return getNextElement();"><pc:shortcut key="q" script='<%=qu%>'/><%}%>
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