<%--工人工资修改页面--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.action.Operate,engine.dataset.*, engine.common.LoginBean,engine.project.*, java.math.BigDecimal"%>
<%@ page import="java.util.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%><%String pageCode = "personal_worker_wage";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.produce.B_PersonalWage personalWageBean = engine.erp.produce.B_PersonalWage.getInstance(request);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp workGroupBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_GROUP);//通过工作组id得到工作组名称
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
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
<script language="javascript" src="../scripts/rowcontrol.js"></script>
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
    location.href='personal_worker_wage.jsp';
  }
</script>
<%
  String retu = personalWageBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  RowMap m_RowInfo = personalWageBean.getRowinfo();   //所填信息
  EngineDataSet ds= personalWageBean.getMasterTable();
  EngineDataSet dsDetail = personalWageBean.getDetailTable();
  RowMap[] d_rowinfos = personalWageBean.getDetailRowinfos();
  boolean isReport = personalWageBean.isReport;
  workGroupBean.regData(ds,"gzzid");
  boolean isEdit = !m_RowInfo.get("ztbj").equals("8") && loginBean.hasLimits(pageCode, op_edit);
  //loginBean.hasLimits(pageCode, op_delete)
  if(isEdit)
    personBean.regConditionData(ds,"deptid");
%>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr><td NOWRAP align="center">工人工资计算</td></tr>
</table>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
<table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center" width="95%">
  <tr valign="top">
    <td>
      <table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
      <tr>
      <td width="100%">
      <table cellspacing="2" cellpadding="0" border="0" width="100%" bgcolor="#f0f0f0">
      <tr>
       <td nowrap class="tdTitle">车间</td>
      <td nowrap class="td">
       <input type="hidden" name="deptid" value='<%=m_RowInfo.get("deptid")%>'>
      <input type="text" name="bmmc" value='<%=workShopBean.getLookupName(m_RowInfo.get("deptid"))%>' onKeyDown="return getNextElement();"  class='edline' readonly>
      </td>
      <td nowrap class="tdTitle">工作组</td>
      <td nowrap class="td" >
      <input type="hidden" name="gzzid" value='<%=m_RowInfo.get("gzzid")%>'>
      <input class="edline"  name="gzzmc" value='<%=workGroupBean.getLookupName(m_RowInfo.get("gzzid"))%>'  onKeyDown="return getNextElement();" readonly >
     </td>
     <td nowrap class="tdTitle">班次</td>
      <td nowrap class="td">
      <input type="text" name="bc" value='<%=m_RowInfo.get("bc")%>' onKeyDown="return getNextElement();" class='edline' readonly>
      </td>
      <td nowrap class="tdTitle">总工费</td>
      <td nowrap class="td">
      <input type="text" name="zgf" value='<%=m_RowInfo.get("zgf")%>' onKeyDown="return getNextElement();" class='edline' readonly >
      </td>
      </tr>
      <tr>
      <td nowrap class="tdTitle">收货单号</td>
      <td nowrap class="td">
      <input type="text" name="djh" value='<%=m_RowInfo.get("djh")%>' onKeyDown="return getNextElement();" class='edline' readonly >
      </td>
      <td nowrap class="tdTitle">收货日期</td>
      <td nowrap class="td"><input type="text" name="djrq" value='<%=m_RowInfo.get("djrq")%>' onKeyDown="return getNextElement();" readonly class='edline' >
      </td>
      <td nowrap class="tdTitle">经手人</td>
      <td nowrap class="td">
      <input type="text" name="handlePerson" value='<%=m_RowInfo.get("handlePerson")%>' onKeyDown="return getNextElement();" class='edline' readonly >
      </td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
      </tr>
      <tr>
      <td>&nbsp;</td>
      </tr>
      <tr>
     <td colspan="8" nowrap>
     <div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:100%;height:220;overflow-y:auto;overflow-x:auto;">
     <table id="tableview1" width="100%" hight="400px" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
     <td  align="center" nowrap>
     <%if(isEdit){%>
     <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
     <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/><%}%></td>
     <td>人员</td>
     <td>比例</td>
     <td>计件工资</td>
    </tr>
      <%BigDecimal t_je = new BigDecimal(0);
      personBean.regData(dsDetail, "personid");
      String detailClass = (isEdit && !isReport) ?  "class=edFocused" : "class=ednone";
      String detailClass_r = (isEdit && !isReport) ?  "class=edFocused_r" : "class=ednone_r";
      String readonly = (!isEdit || isReport) ? " readonly" : "";
      RowMap  detail = null;
      String cpid = null;
      int i=0;
      for(; i<d_rowinfos.length; i++)
      {
        detail = (RowMap)d_rowinfos[i];
        String jjgz = detail.get("jjgz");
        if(personalWageBean.isDouble(jjgz))
          t_je = t_je.add(new BigDecimal(jjgz));
        String personidName="personid_"+i;
      %>
    <tr>
      <td nowrap class="td" align="center">
            <%if(isEdit){%>
      <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="sumitForm(<%=Operate.DEL%>,<%=i%>)">
      <%}%>
      </td>
       <td class="td" align="center" nowrap>
      <%if(!isEdit || isReport) out.print("<input type='text' style='width:100' value='"+personBean.getLookupName(detail.get("personid"))+"' class='ednone' readonly>");
      else {%>
      <pc:select className="edFocused" name="<%=personidName%>" style='width:100' addNull="1">
      <%=personBean.getList(detail.get("personid"), "deptid", m_RowInfo.get("deptid"))%></pc:select>
      <%}%>
      </td>
      <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:100%"  id="bl_<%=i%>" name="bl_<%=i%>" value='<%=detail.get("bl")%>' maxlength='<%=dsDetail.getColumn("bl").getPrecision()%>' onKeyDown="return getNextElement();" onchange="bl_onchange(<%=i%>)" <%=readonly%>></td>
      <td class="td" nowrap><input type="text" style="width:100%"  class="ednone_r"  id="jjgz_<%=i%>" name="jjgz_<%=i%>" value='<%=detail.get("jjgz")%>' maxlength='<%=dsDetail.getColumn("jjgz").getPrecision()%>' onKeyDown="return getNextElement();" readonly></td>
    </tr>
    <%}
      for(; i < 7; i++){%>
          <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
        </tr>
      <%}%>
        <tr id="rowinfo_end">
       <td class="tdTitle" nowrap>合计</td>
       <td class="td">&nbsp;</td>
       <td class="td">&nbsp;</td>
       <td align="right" class="td"><input id="t_je" name="t_je" type="text" class="ednone_r" style="width:100%" value='<%=t_je%>' readonly></td>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',0);
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    function bl_onchange(i)
      {
        var blObj = document.all['bl_'+i];
        var zgf = form1.zgf;
        var grjjgzObj = document.all['jjgz_'+i];
        if(zgf.value=="")
          return;
        if(isNaN(zgf.value))
        {
          alert("总工费非法");
          return;
        }
        if(blObj.value=="")
          return;
        if(isNaN(blObj.value))
        {
          alert("输入的比例非法");
          blObj.focus();
          return;
        }
        if(zgf.value!="" && !isNaN(zgf.value) && blObj.value!="" ){
          grjjgzObj.value= formatPrice(parseFloat(zgf.value) * parseFloat(blObj.value));
        }
        grcal_tot('grjjgz');
  }
  function grcal_tot(type)
 {
   var tmpObj;
   var tot=0;
   for(n=0; n<<%=d_rowinfos.length%>; n++)
   {
     if(type == 'grjjgz')
       tmpObj = document.all['jjgz_'+n];
     else
       return;
     if(tmpObj.value!="" && !isNaN(tmpObj.value))
       tot += parseFloat(tmpObj.value);
   }
   if(type == 'grjjgz')
     document.all['t_je'].value = formatSum(tot);
  }

</script>
  </div>
</td>
</tr>
</table>
</table>
</table>
    <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">
      <tr>
        <td noWrap class="tableTitle"><p><br>
          <%if(isEdit && !isReport){%>
          <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>)" value='保存(S)'>
          <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
          <%if(isEdit && !isReport){%><input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=personalWageBean.MASTER_DEL%>)" value=" 删除(D)">
                <pc:shortcut key="d" script="if(confirm('是否删除该记录？'))sumitForm(<%=personalWageBean.MASTER_DEL%>)"/><%}%>
          <%if(!isReport){%><input name="btnback" type="button" class="button" onClick="location.href='personal_worker_wage.jsp'" value='返回(C)'>
         <% String back ="location.href='personal_worker_wage.jsp'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/>
        <%}%>
        </p>
        </td>
      </tr>
    </table>
</form>
</body>
</html>