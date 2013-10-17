<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate, com.borland.dx.dataset.Locate,java.util.*"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.project.*,engine.erp.person.*"%>
<%@ page import="java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "saler_handover";
%>
<%
  engine.erp.sale.B_SalerHandover b_SalerHandoverBean = engine.erp.sale.B_SalerHandover.getInstance(request);
  String retu = b_SalerHandoverBean.doService(request, response);
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);
  LookUp mpersonBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
  LookUp mdeptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);
  LookUp personListBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
  LookUp tdBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_SALE_LADING);
  String curUrl = request.getRequestURL().toString();
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
function sumitForm(oper, row)
{
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function backList()
{
  location.href='saler_handover.jsp';
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
function deptchange(i){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid_'+i+'.value'), '');
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" method="post" action="<%=curUrl%>"  onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
<TABLE WIDTH="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 >
  <TR>
    <TD align="center"  class="tdTitle">业务员移交</TD>
  </TR>
</TABLE>
  <TABLE WIDTH="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
    <td class="td" nowrap>&nbsp;
     <%
       String key = "tdyjadddetail";
       EngineDataSet ds = b_SalerHandoverBean.getTdyj();
       //RowMap masterRow = b_SalerHandoverBean.getMasterRowinfo();//主表
       //pageContext.setAttribute(key, ds);
       int iPage = loginBean.getPageSize();
       String pageSize = ""+iPage;



       personBean.regConditionData("deptid",new String[]{ds.getValue("beforedeptid")});

       ds.first();
       String beforeid = b_SalerHandoverBean.beforeid;
       String beforedeptid = b_SalerHandoverBean.beforedeptid;
       tdBean.regData(ds,"tdid");
     %>
      <%--pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"></pc:dbNavigator--%>
       </td>
      <td nowrap class="tdTitle" align="center">部门</td>
      <td  class="td">
      <%
        String deptChange ="sumitForm("+b_SalerHandoverBean.DEPT_CHANGE+")";
      %>
      <pc:select name="beforedeptid" addNull="1" style="width:110"  onSelect="<%=deptChange%>"  >
      <%=deptBean.getList(beforedeptid)%>
      </pc:select>
      </td>
      <td nowrap class="tdTitle">业务员</td>
      <td  class="td">
      <%String onChange ="sumitForm("+b_SalerHandoverBean.PERSON_CHANGE+",-1)";%>
      <pc:select name="beforeid" style="width:80" addNull="1"  onSelect="<%=onChange%>" >
      <%=personBean.getList(beforeid, "deptid", beforedeptid)%>
      </pc:select>
      </td>
      <TD align="right">
      <input name="button" type="button" align="Right" title = "保存返回" class="button" onClick="sumitForm(<%=b_SalerHandoverBean.NEW_POST%>,-1)" value=" 保存(S) "border="0">
      <input name="button1" type="button" align="Right" title = "删除没替换" class="button" onClick="sumitForm(<%=Operate.DEL%>,-1)" value=" 删除(D) "border="0">
      <input name="button2" type="button" align="Right" title = "返回" class="button" onClick="backList()" value=" 返回(C) "border="0">
      </TD>
   </TR>
  </TABLE>
  <table id="tableview1" WIDTH="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle" >
      <td></td>
      <td>提单编号</td>
      <td>购货单位</td>
      <td>移交后部门</td>
      <td>移交后业务员</td>
    </tr>
     <%
       int i=0;
       RowMap[] rows = b_SalerHandoverBean.getXstdRowinfos();
       int count = rows.length;
       //ds.first();
       for(; i<count; i++)
       {
         RowMap detail = rows[i];
         RowMap tdrow = tdBean.getLookupRow(detail.get("tdid"));
     %>
      <tr>
      <td class="td">
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_SalerHandoverBean.NEW_DEL%>,<%=i %>)" src="../images/del.gif" border="0">
      </td>
       <td class="td">
          <INPUT TYPE="hidden" NAME="tdyjid_<%=i%>" VALUE="<%=detail.get("tdyjid")%>" style="width:100%" class="ednone"  onKeyDown="return getNextElement();" readonly>

           <INPUT TYPE="hidden" NAME="tdid_<%=i%>" VALUE="<%=detail.get("tdid")%>" style="width:100%" class="ednone"  onKeyDown="return getNextElement();" readonly>
          <INPUT TYPE="TEXT" NAME="tdbh_<%=i%>" VALUE="<%=tdrow.get("tdbh")%>" style="width:100%"  class="ednone_r"  onKeyDown="return getNextElement();" readonly>
      </td>
       <td class="td" ><INPUT TYPE="TEXT" NAME="dwmc_<%=i%>" VALUE="<%=tdrow.get("dwmc")%>" style="width:100%" class="ednone"  onKeyDown="return getNextElement();" readonly></td>
       <td class="td">
       <%
         String dept =  "afterdeptid_"+i;
         String aterdeptChange ="sumitForm("+b_SalerHandoverBean.AFTERDEPT_CHANGE+","+i+")";
       %>
       <pc:select name="<%=dept%>" style="width:80" addNull="1"  onSelect="<%=aterdeptChange%>" >
       <%=mdeptBean.getList(detail.get("afterdeptid"))%>
       </pc:select>
     </td>
       <td class="td">
       <%
         String p =  "afterid_"+i;
         //String aterpersonChange ="sumitForm("+b_SalerHandoverBean.AFTERPERSON_CHANGE+","+i+")";
       %>
       <pc:select name="<%=p%>" style="width:80" addNull="1"   >
       <%=mpersonBean.getList(detail.get("afterid"), "deptid", detail.get("afterdeptid"))%>
       </pc:select>
     </td>
     </tr>
    <%
    //ds.next();
     }
     for(; i < iPage; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
       <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
   <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form>
<%out.print(retu);%>
</body>
</html>