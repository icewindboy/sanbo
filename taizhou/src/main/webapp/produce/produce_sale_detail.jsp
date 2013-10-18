<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*"%>
<%@ page import="java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "produce_sale_detail";
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.produce.B_Produce_Sale_Detail b_Produce_Sale_DetailBean = engine.erp.produce.B_Produce_Sale_Detail.getInstance(request);
  LookUp areaBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_AREA);
  LookUp jsfsBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_BALANCE_MODE);
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);
  LookUp prodBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PRODUCT);

%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript">

function sumitForm(oper, row)
{
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function sumitFixedQuery(oper)
 {
   lockScreenToWait("处理中, 请稍候！");
   fixedQueryform.operate.value = oper;
   fixedQueryform.submit();
  }
function showFixedQuery()
  {
   showFrame('fixedQuery',true,"",true);
   }
function backList()
{
  location.href='Produce_detail_edit.jsp';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr>
    <td NOWRAP align="center"></td>
  </tr>
</table>
<%
  String retu = b_Produce_Sale_DetailBean.doService(request, response);
  if(retu.indexOf("backList()")>-1 || retu.indexOf("toDetail()")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();

  EngineDataSet ds_xsht_list= b_Produce_Sale_DetailBean.getxshtTable();
  EngineDataSet ds_scjh_list = b_Produce_Sale_DetailBean.getscjhTable();
  EngineDataSet ds_scrwd_list = b_Produce_Sale_DetailBean.getscrwdTable();
  EngineDataSet ds_ckd_list = b_Produce_Sale_DetailBean.getckdTable();
  EngineDataSet ds_thd_list = b_Produce_Sale_DetailBean.getthdTable();
  EngineDataSet ds_scrbb_list = b_Produce_Sale_DetailBean.getscrbbTable();
  prodBean.regData(ds_xsht_list,"cpid");
  prodBean.regData(ds_scjh_list,"cpid");
  prodBean.regData(ds_scrwd_list,"cpid");
  prodBean.regData(ds_ckd_list,"cpid");
  prodBean.regData(ds_thd_list,"cpid");
  prodBean.regData(ds_scrbb_list,"cpid");
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
<table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
<tr>
<TD align="right"><%if(loginBean.hasLimits(pageCode, op_search)){%>
        <INPUT class="button" onClick="showFixedQuery()" type="button" value=" 查询 " name="Query" onKeyDown="return getNextElement();">
        <%}%>
        <input name="button2" type="button" align="Right" class="button" onClick="location.href='<%=b_Produce_Sale_DetailBean.retuUrl%>'" value=" 返回 "border="0">
</TD>
</tr>
  <tr valign="top">
  <td width="400"><table border=0 CELLPADDING=0 CELLSPACING=0 class="table">
  <tr>
  <td  class="activeVTab">产品产销明细查询</td>

  </tr>
</table>
<table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
  <tr>
  <td>
<table cellspacing="2" cellpadding="0" border="0" width="100%" bgcolor="#f0f0f0">

     <tr>
     <td nowrap class="tdTitle">日期:<%=b_Produce_Sale_DetailBean.ksrq%> 至:<%=b_Produce_Sale_DetailBean.jsrq%><td nowrap class="tdTitle">产品:<%=prodBean.getLookupName(b_Produce_Sale_DetailBean.cpid)%></td>
     </tr>
     <tr>
     <td colspan="6" nowrap>
     <table cellspacing=0 width="100%" cellpadding=0>
     <tr>
         <td nowrap><div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_0');return false;">销售合同</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_1" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_1');return false;">生产计划</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_2" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_2');return false;">生产任务单</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_3" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_3');return false;">出入库单</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_4" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_4');return false;">提货单</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_5" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_5');return false;">生产日报表</a></div></td>

         <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a></td>
     </tr>
     </table>
     <div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:750;height:330;overflow-y:auto;overflow-x:auto;">
     <table id="tableview1" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
     <tr class="tableTitle">

           <td nowrap>日期</td>
           <td nowrap>合同号</td>
           <td nowrap>购货单位</td>
           <td nowrap>数量</td>
           <td nowrap>单价</td>
           <td nowrap>金额</td>
           <td nowrap>交货日期</td>
           <td nowrap>备注</td>
    </tr>
     <%
      ds_xsht_list.first();
      for(int i=0; i<ds_xsht_list.getRowCount(); i++)
      {
        ds_xsht_list.goToRow(i);

     %>
      <tr>

          <td class="td" align="center"><%=ds_xsht_list.getValue("rq")%></td>
          <td class="td" align="center"><%=ds_xsht_list.getValue("htbh")%></td>
          <td class="td" align="center"><%=ds_xsht_list.getValue("dwmc")%></td>
          <td class="td" align="center"><%=ds_xsht_list.getValue("sl")%></td>
          <td class="td" align="center"><%=ds_xsht_list.getValue("dj")%></td>
          <td class="td" align="center"><%=ds_xsht_list.getValue("jje")%></td>
          <td class="td" align="center"><%=ds_xsht_list.getValue("jhrq")%></td>
          <td class="td" align="center"><%=ds_xsht_list.getValue("bz")%></td>
      </tr>
      <%}%>
      </table>
      <script language="javascript">initDefaultTableRow('tableview1',1);</script>
      </div>
<div id="cntDivINFO_EX_1" class="tabContent" style="display:none;width:750;height:330;overflow-y:auto;overflow-x:auto;">
    <center>
        <table id="tableview2" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
            <tr class="tableTitle">

                  <td nowrap>日期</td>
                  <td nowrap>计划号</td>
                  <td nowrap>部门</td>
                  <td nowrap>数量</td>
                  <td nowrap>开始日期</td>
                  <td nowrap>结束日期</td>
                  <td nowrap>加工要求</td>

            </tr>
     <%
       ds_scjh_list.first();
       for(int i=0; i<ds_scjh_list.getRowCount(); i++)
       {
        ds_scjh_list.goToRow(i);
     %>
            <tr>

             <td class="td" align="center"><%=ds_scjh_list.getValue("rq")%></td>
             <td class="td" align="center"><%=ds_scjh_list.getValue("jhh")%></td>
             <td class="td" align="center"><%=deptBean.getLookupName(ds_scjh_list.getValue("deptid"))%></td>
             <td class="td" align="center"><%=ds_scjh_list.getValue("sl")%></td>
             <td class="td" align="center"><%=ds_scjh_list.getValue("ksrq")%></td>
             <td class="td" align="center"><%=ds_scjh_list.getValue("wcrq")%></td>
             <td class="td" align="center"><%=ds_scjh_list.getValue("jgyq")%></td>
            </tr>
            <%}%>
        </table>
        <script language="javascript">initDefaultTableRow('tableview2',1);</script>
    </center>
</div>
<div id="cntDivINFO_EX_2" class="tabContent" style="display:none;width:750;height:330;overflow-y:auto;overflow-x:auto;">
      <center>
            <table id="tableview3" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
                <tr class="tableTitle">

                    <td nowrap>日期</td>
                    <td nowrap>任务单号</td>
                    <td nowrap>部门</td>
                    <td nowrap>数量</td>
                    <td nowrap>开始日期</td>
                    <td nowrap>完成日期</td>
                    <td nowrap>加工要求</td>

                </tr>
     <%
       ds_scrwd_list.first();
        for(int i=0; i<ds_scrwd_list.getRowCount(); i++)
        {
        ds_scrwd_list.goToRow(i);
     %>
                <tr>

                    <td class="td"  align="center"><%=ds_scrwd_list.getValue("rq")%></td>
                    <td class="td"  align="center"><%=ds_scrwd_list.getValue("rwdh")%></td>
                    <td class="td"  align="center"><%=deptBean.getLookupName(ds_scrwd_list.getValue("deptid"))%></td>
                    <td class="td"  align="center"><%=ds_scrwd_list.getValue("sl")%></td>
                    <td class="td"  align="center"><%=ds_scrwd_list.getValue("ksrq")%></td>
                    <td class="td"  align="center"><%=ds_scrwd_list.getValue("wcrq")%></td>
                    <td class="td"  align="center"><%=ds_scrwd_list.getValue("jgyq")%></td>
               </tr>
            <%}%>
            </table>
            <script language="javascript">initDefaultTableRow('tableview3',1);</script>
      </center>
</div>
<div id="cntDivINFO_EX_3" class="tabContent" style="display:none;width:750;height:330;overflow-y:auto;overflow-x:auto;">
      <center>
            <table id="tableview4" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
                <tr class="tableTitle">

                    <td nowrap>日期</td>
                    <td nowrap>类型</td>
                    <td nowrap>单据号</td>
                    <td nowrap>部门</td>
                    <td nowrap>数量</td>
                    <td nowrap>单价</td>
                    <td nowrap>金额</td>
                    <td nowrap>批号</td>
                    <td nowrap>备注</td>
                </tr>
     <%
       ds_ckd_list.first();
        for(int i=0; i<ds_ckd_list.getRowCount(); i++)
        {
        ds_ckd_list.goToRow(i);
     %>
                <tr >

                <td class="td" align="center"><%=ds_ckd_list.getValue("rq")%></td>
                <td class="td" align="center"><%=ds_ckd_list.getValue("djxz")%></td>
                <td class="td" align="center"><%=ds_ckd_list.getValue("sfdjdh")%></td>
                <td class="td" align="center"><%=deptBean.getLookupName(ds_ckd_list.getValue("deptid"))%></td>
                <td class="td" align="center"><%=ds_ckd_list.getValue("sl")%></td>

                <td class="td" align="center"><%=ds_ckd_list.getValue("dj")%></td>
                <td class="td" align="center"><%=ds_ckd_list.getValue("je")%></td>
                <td class="td" align="center"><%=ds_ckd_list.getValue("ph")%></td>
                 <td class="td" align="center"><%=ds_ckd_list.getValue("bz")%></td>
                </tr>
                <%}%>
            </table>
            <script language="javascript">initDefaultTableRow('tableview4',1);</script>
      </center>
</div>
<div id="cntDivINFO_EX_4" class="tabContent" style="display:none;width:750;height:330;overflow-y:auto;overflow-x:auto;">
      <center>
            <table id="tableview5" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
                  <tr class="tableTitle">

                      <td nowrap>日期</td>
                      <td nowrap>单据号</td>
                      <td nowrap>单位名称</td>
                      <td nowrap>数量</td>
                      <td nowrap>单价</td>
                      <td nowrap>金额</td>
                      <td nowrap>部门</td>
                      <td nowrap>业务员</td>
                      <td nowrap>合同号</td>
                      <td nowrap>结算方式</td>
                  </tr>
     <%
       ds_thd_list.first();
         for(int i=0; i<ds_thd_list.getRowCount(); i++)
         {
        ds_thd_list.goToRow(i);
     %>
        <tr>

                <td class="td" align="center"><%=ds_thd_list.getValue("rq")%></td>
                <td class="td" align="center"><%=ds_thd_list.getValue("tdbh")%></td>
                <td class="td" align="center"><%=ds_thd_list.getValue("dwmc")%></td>
                <td class="td" align="center"><%=ds_thd_list.getValue("sl")%></td>
                <td class="td" align="center"><%=ds_thd_list.getValue("dj")%></td>
                <td class="td" align="center"><%=ds_thd_list.getValue("jje")%></td>
                <td class="td" align="center"><%=deptBean.getLookupName(ds_thd_list.getValue("deptid"))%></td>
                <td class="td" align="center"><%=ds_thd_list.getValue("xm")%></td>
                <td class="td" align="center"><%=ds_thd_list.getValue("htbh")%></td>
                <td class="td" align="center"><%=jsfsBean.getLookupName(ds_thd_list.getValue("jsfsid"))%></td>
        </tr>
                  <%}%>
            </table>
            <script language="javascript">initDefaultTableRow('tableview5',1);</script>
      </center>
</div>
<div id="cntDivINFO_EX_5" class="tabContent" style="display:none;width:750;height:330;overflow-y:auto;overflow-x:auto;">
    <center>
        <table width="100%" border="0" cellpadding="0" cellspacing="1" class="table" id="tableview6" dwcopytype="CopyTableRow" width="100%">
              <tr class="tableTitle">

                  <td nowrap>日期</td>
                  <td nowrap>部门</td>
                  <td nowrap>员工</td>
                  <td nowrap>工艺</td>
                  <td nowrap>数量</td>
                  <td nowrap>定额</td>
              </tr>
     <%
       ds_scrbb_list.first();
           for(int i=0; i<ds_scrbb_list.getRowCount(); i++)
           {
        ds_scrbb_list.goToRow(i);
     %>
        <tr >

                <td class="td" align="center"><%=ds_scrbb_list.getValue("rq")%></td>
                <td class="td" align="center"><%=deptBean.getLookupName(ds_scrbb_list.getValue("deptid"))%></td>
                <td class="td" align="center"><%=ds_scrbb_list.getValue("xm")%></td>
                <td class="td" align="center"><%=ds_scrbb_list.getValue("gx")%></td>
                <td class="td" align="center"><%=ds_scrbb_list.getValue("sl")%></td>
                <td class="td" align="center"><%=ds_scrbb_list.getValue("de")%></td>
          </tr>
        <%}%>
        </table>
        <script language="javascript">initDefaultTableRow('tableview6',1);</script>
    </center>
</div>

  <SCRIPT LANGUAGE="javascript">INFO_EX = new TabControl('INFO_EX',0);
      AddTabItem(INFO_EX,'INFO_EX_0','tabDivINFO_EX_0','cntDivINFO_EX_0');
      AddTabItem(INFO_EX,'INFO_EX_1','tabDivINFO_EX_1','cntDivINFO_EX_1');
      AddTabItem(INFO_EX,'INFO_EX_2','tabDivINFO_EX_2','cntDivINFO_EX_2');
      AddTabItem(INFO_EX,'INFO_EX_3','tabDivINFO_EX_3','cntDivINFO_EX_3');
      AddTabItem(INFO_EX,'INFO_EX_4','tabDivINFO_EX_4','cntDivINFO_EX_4');
      AddTabItem(INFO_EX,'INFO_EX_5','tabDivINFO_EX_5','cntDivINFO_EX_5');
      AddTabItem(INFO_EX,'INFO_EX_6','tabDivINFO_EX_6','cntDivINFO_EX_6');
      AddTabItem(INFO_EX,'INFO_EX_7','tabDivINFO_EX_7','cntDivINFO_EX_7');
      AddTabItem(INFO_EX,'INFO_EX_8','tabDivINFO_EX_8','cntDivINFO_EX_8');
      AddTabItem(INFO_EX,'INFO_EX_9','tabDivINFO_EX_9','cntDivINFO_EX_9');
      if (window.top.StatFrame+''!='undefined'){ var tmp_curtab=window.top.StatFrame.GetRegisterVar('INFO_EX');if (tmp_curtab!='') {SetActiveTab(INFO_EX,tmp_curtab);}}
</SCRIPT>
</td>
</tr>
</table>

</table>
</table>
</form>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;">
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
   <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0><%b_Produce_Sale_DetailBean.table.printWhereInfo(pageContext);%>
            </TABLE>
            <TR>
              <TD colspan="4" nowrap class="td" align="center"><%if(loginBean.hasLimits(pageCode, op_search)){%><INPUT class="button" onClick="sumitFixedQuery(<%=b_Produce_Sale_DetailBean.FIXED_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
                <%}%><INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
              </TD>
            </TR>
    </TABLE>
  </DIV>
</form>
<%out.print(retu);%>
</body>
</html></font></font>