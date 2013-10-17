<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*,engine.erp.person.*"%>
<%@ page import="java.util.*"%>
<%
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_approve ="op_approve";
  String pageCode = "document_move";

  engine.erp.person.B_Desktop_File b_FileBean = engine.erp.person.B_Desktop_File.getInstance(request);


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
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<script language="javascript">
function backList()
{
sumitForm(<%=b_FileBean.SMPOST%>, -1);
 location.href='../pub/main.jsp';
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
  }
</script>
<%
  String retu = b_FileBean.doService(request, response);
 if(retu.indexOf("backList()")>-1 || retu.indexOf("toDetail()")>-1)
 {
   out.print(retu);
   return;
 }
  engine.project.LookUp priceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_AREA_PRICE);
  engine.project.LookUp CarBean= engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_AREA_CAR);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp personBean =  engine.project.LookupBeanFacade.getInstance(request,  engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_ALL);

  String curUrl = request.getRequestURL().toString();
  String typeClass = "class=edline";
  EngineDataSet list = b_FileBean.getEditTable();

  EngineDataSet listfile = b_FileBean.getFilefileTable();
  EngineDataSet listperson = b_FileBean.getFilepersonTable();
%>
<BODY oncontextmenu="window.event.returnValue=true" >
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<iframe id="prod2" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>

<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr>
    <td NOWRAP align="center"></td>
 </tr>
</table>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
<table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
  <tr valign="top">
   <td width="400"><table border=0 CELLPADDING=0 CELLSPACING=0 class="table">
  <tr>
   <td  class="activeVTab">公文流转(收文件)

 </td>
  </tr>

</table>
<table class="editformbox" cellspacing=2 cellpadding=0 width="400">

  <tr>
   <td>
<table cellspacing="2" cellpadding="0" border="0" width="400" bgcolor="#f0f0f0">
  <tr>

    <td noWrap  class="tdTitle">文件类型</td>

    <td class="td" width="80" nowrap><input type="text" name="file_type" value='<%=list.getValue("file_type")%>' maxlength="10" style="width:80" <%=typeClass%> onKeyDown="return getNextElement();" readonly></td>

     <td noWrap class="tdTitle">优先级</td>
     <td class="td" width="80" nowrap><input type="text" name="filelevel" value='<%=list.getValue("filelevel").equals("2")?"紧急":(list.getValue("filelevel").equals("1")?"缓慢":"普通")%>' maxlength="10" style="width:80" <%=typeClass%> onKeyDown="return getNextElement();" readonly></td>

  </tr>
  <tr>
    <td nowrap class="tdTitle">主题</td>

   <td nowrap class="td" colspan="3"><input type="text" name="topic" value='<%=list.getValue("topic")%>' maxlength="10" style="width:300" <%=typeClass%> onKeyDown="return getNextElement();" readonly></td>

   </tr>
  <tr>
     <td nowrap class="tdTitle">标题</td>

   <td nowrap class="td" colspan="3"><input type="text" name="caption" value='<%=list.getValue("caption")%>' maxlength="10" style="width:300" <%=typeClass%> onKeyDown="return getNextElement();" readonly></td>

  </tr>
  <tr>
      <td  noWrap class="tdTitle">文件内容</td><%--其他信息--%>
       <td colspan="6" noWrap class="td"><textarea name="maintext" rows="16" onKeyDown="return getNextElement();" style="width:545"   readonly > <%=list.getValue("maintext")%></textarea></td>
        </tr>
        <tr>
            <td class="td" nowrap colspan="2"><b>发件日期:</b><%=list.getValue("senddate")%></td>
            <td class="td"></td>
            <td class="td" align="right" nowrap><b>发件人:</b><%=list.getValue("sendperson")%></td>
          </tr>
   <tr>

    <tr>


    <td nowrap class="tdTitle">已读意见</td>

   <td nowrap class="td" colspan="4"><input type="text" name="sm" value='<%=listperson.getValue("sm")%>' maxlength="20" style="width:545" class=edFocused onKeyDown="return getNextElement();" ></td>

  </tr>

   <tr>
     <td colspan="6" nowrap>
     <table cellspacing=0 width="100%" cellpadding=0>

     <td nowrap><div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="#" >附件下载</a></div></td>
     <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a></td>
  </tr>

  </table>
  <div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:600;height:100;overflow-y:auto;overflow-x:auto;">
  <table id="tableview1" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
  <tr class="tableTitle">
   <td class="td"></td>

                  <td nowrap>附件</td>



      </tr>
      <%


       listfile.first();

       for(int i=0; i<listfile.getRowCount(); i++)
       {

     %>
    <tr>
                <td class="td" align="center">


                <img style='cursor:hand'  title="下载附件"  src='../images/nextbill.gif' border=0 onClick="sumitForm(<%=b_FileBean.SHOW_FILE%>,<%=i%>)">
                </td>

            <td class="td" align="left"><INPUT TYPE="TEXT" NAME="filename_<%=i%>" VALUE="<%=listfile.getValue("filename")%>" style="width:515" MAXLENGTH="<%=listfile.getColumn("filename").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" readonly></td>

               </tr>
          <%listfile.next();}%>

      </table>
      <script language="javascript">initDefaultTableRow('tableview1',1);</script>
      </div>
      </td>
      </tr>
   </table>
  </td>
  </tr>
 </table>
</table>
    <table CELLSPACING=0 CELLPADDING=0 width="600" align="center">
      <tr>
        <td noWrap class="tableTitle">
        <%
          String ret = "backList();";
        %>
        <input name="button2" type="button" class="button" title = "已阅返回"onClick="backList();" value='已阅返回(N)'><pc:shortcut key="n" script='<%=ret%>'/>

        </td>
      </tr>
  </table>
</form>
<%out.print(retu);%>
</body>
</html>