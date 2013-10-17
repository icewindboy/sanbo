<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList,java.util.*,java.text.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%>
<%
  engine.erp.equipment.B_RecordCard recordCardBean=engine.erp.equipment.B_RecordCard.getInstance(request);
  String pageCode = "record_card";
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
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function backList()
{
  location.href='record_card.jsp';
}

  /*
  *日期大小比较函数
  *支持年－月－日这样的格式
  */
function compareDate(DateOne,DateTwo)
{
  var OneMonth = DateOne.substring(5,DateOne.lastIndexOf ("-"));
  var OneDay = DateOne.substring(DateOne.length,DateOne.lastIndexOf ("-")+1);
  var OneYear = DateOne.substring(0,DateOne.indexOf ("-"));
  var TwoMonth = DateTwo.substring(5,DateTwo.lastIndexOf ("-"));
  var TwoDay = DateTwo.substring(DateTwo.length,DateTwo.lastIndexOf ("-")+1);
  var TwoYear = DateTwo.substring(0,DateTwo.indexOf ("-"));
  if (Date.parse(OneMonth+"/"+OneDay+"/"+OneYear) > Date.parse(TwoMonth+"/"+TwoDay+"/"+TwoYear))
  {
    return 1;
  }
  else if (Date.parse(OneMonth+"/"+OneDay+"/"+OneYear) == Date.parse(TwoMonth+"/"+TwoDay+"/"+TwoYear))
  {
    return 0;
  }
  else if (Date.parse(OneMonth+"/"+OneDay+"/"+OneYear) < Date.parse(TwoMonth+"/"+TwoDay+"/"+TwoYear))
  {
    return -1;
  }
  else
  {
    return 2;
  }
}
</script>
<%
  String retu = recordCardBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }


  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//部门
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);//人员
  engine.project.LookUp productBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//产品编码

  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = recordCardBean.getMaterTable();
  EngineDataSet list = recordCardBean.getAppertainTable();
  EngineDataSet dblist = recordCardBean.getHeavyRepairTable();

  EngineDataSet appertainChange = recordCardBean.getAppertainChangeTable();
  HtmlTableProducer masterProducer = recordCardBean.masterProducer;
  HtmlTableProducer detailProducer = recordCardBean.detailProducer;
  RowMap masterRow = recordCardBean.getMasterRowinfo();
  RowMap[] detailRows= recordCardBean.getAppertainRowinfos();
  RowMap[] appertainChangeRows= recordCardBean.getAppertainChangeRowinfos();
  RowMap[] displaceResultRows= recordCardBean.getDisplaceResult_RowInfos();
  RowMap[] stopAnnalRows= recordCardBean.getStopAnnal_RowInfos();
  RowMap[] devolveAnnalRows= recordCardBean.getDevolveAnnal_RowInfos();
  RowMap[] rejectAnnalRows= recordCardBean.getRejectAnnal_RowInfos();
  RowMap[] heavyRepairRows= recordCardBean.getHeavyRepair_RowInfos();
  RowMap[] maintainResultRows= recordCardBean.getMaintainResult_RowInfos();

  String deptid = masterRow.get("deptid");
  deptBean.regData(ds,"deptid");
  productBean.regData(dblist,"cpid");

  boolean isCanEdit = loginBean.hasLimits(pageCode, op_edit);
  boolean isCanAdd=loginBean.hasLimits(pageCode, op_add);
  boolean isCanDelete = loginBean.hasLimits(pageCode, op_delete);
  boolean isCanChange=isCanEdit||isCanAdd;
  String edClass = isCanChange? "class=edbox":"class=edline";
  String detailClass = isCanChange? "class=edbox":"class=ednone";
  String readonly =isCanChange? "":"readonly";
%>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0>
    <tr>
      <td align="center" height="5"></td>
    </tr>
  </table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760"><tr valign="top"><td>
    <table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
      <tr>
        <td class="activeVTab">设备履历卡</td>
      </tr>
    </table>
    <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%"><tr><td>
      <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
        <tr>
          <td noWrap class="tdTitle">设备编码</td>
          <td noWrap class="td"> <input type="text" name="equipment_code" value='<%=masterRow.get("equipment_code")%>' maxlength='32' style="width:110" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeypress="checkKeyCode()">
          </td>
          <td noWrap class="tdTitle">设备名称</td>
          <td noWrap class="td"> <input type="text" name="equipment_name" value='<%=masterRow.get("equipment_name")%>' maxlength='32' style="width:110" <%=edClass%>  <%=readonly%> onKeyDown="return getNextElement();" >
          </td>
          <TD class="tdtitle" nowrap>型号规格</TD>
          <TD nowrap class="td" colspan="3"> <input type="text" name="standard_gg" value='<%=masterRow.get("standard_gg")%>' maxlength='32' style="width:300" <%=edClass%> <%=readonly%>  onKeyDown="return getNextElement();" >
          </TD>
        </tr>
        <tr>
          <td noWrap class="tdTitle">单位</td>
          <td noWrap class="td"> <input type="text" name="unit" value='<%=masterRow.get("unit")%>' maxlength='32' style="width:110" <%=edClass%> <%=readonly%>  onKeyDown="return getNextElement();" >
          </td>
          <td noWrap class="tdTitle">制造日期</td>
          <td noWrap class="td"> <input type="text" name="manufactureDate" value='<%=masterRow.get("manufactureDate")%>' maxlength="10" style="width:85" <%=edClass%> <%=readonly%>  onChange="checkDate(this)" onKeyDown="return getNextElement();" >
          <%if(isCanChange){%>
          <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(manufactureDate);"></a>
          <%}%></td>
          <TD class="tdtitle" nowrap>制造厂</TD>
          <TD nowrap class="td" colspan="3"> <input type="text" name="manufacturer" value='<%=masterRow.get("manufacturer")%>' maxlength='32' style="width:300" <%=edClass%>  <%=readonly%> onKeyDown="return getNextElement();" >
          </TD>
        </tr>
        <tr>
          <td noWrap class="tdTitle">购买日期</td>
          <td noWrap class="td"> <input type="text" name="buy_date" value='<%=masterRow.get("buy_date")%>' maxlength="10" style="width:85" <%=edClass%> <%=readonly%>  onChange="checkDate(this)" onKeyDown="return getNextElement();" >
          <%if(isCanChange){%>
          <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(buy_date);"></a>
          <%}%></td>
          <td noWrap class="tdTitle">购置金额</td>
          <td noWrap class="td"> <input type="text" name="buy_money" value='<%=masterRow.get("buy_money")%>' maxlength='32' style="width:110" <%=edClass%> <%=readonly%>  onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');" onafterpaste="this.value=this.value.replace(/\D/g,'')">
          </td>
          <td noWrap class="tdTitle">开始使用日期</td>
          <td noWrap class="td"> <input type="text" name="use_date" value='<%=masterRow.get("use_date")%>' maxlength="10" style="width:85" <%=edClass%> <%=readonly%>  onChange="checkDate(this)" onKeyDown="return getNextElement();" >
          <%if(isCanChange){%>
           <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(use_date);"></a>
          <%}%></td>
          <td noWrap class="tdTitle">使用部门</td>
          <td noWrap class="td">
          <%if(isCanChange){%>
           <pc:select name="use_deptID"  addNull="1"  style="width:110" value='<%=masterRow.get("use_deptID")%>'>
            <%=deptBean.getList()%>
           </pc:select><%}%>
          <%if(!isCanChange){%>
           <input type="text"  value='<%=deptBean.getLookupName(masterRow.get("use_deptID"))%>' maxlength='32' style="width:110" <%=edClass%> <%=readonly%>  onKeyDown="return getNextElement();" >
          <%}%>
          </td>
        </tr>
        <tr>
          <TD class="tdtitle" nowrap>存放地点</TD>
          <TD nowrap class="td" colspan="3"> <input type="text" name="depositary" value='<%=masterRow.get("depositary")%>' maxlength='32' style="width:290" <%=edClass%> <%=readonly%>  onKeyDown="return getNextElement();" >
          </TD>
          <td noWrap class="tdTitle">购入部门</td>
          <td noWrap class="td">
           <%if(isCanChange){%>
           <pc:select name="deptid"  addNull="1"  style="width:110" value='<%=masterRow.get("deptid")%>'>
            <%=deptBean.getList()%>
           </pc:select><%}%>
           <%if(!isCanChange){%>
           <input type="text"  value='<%=deptBean.getLookupName(masterRow.get("deptid"))%>' maxlength='32' style="width:110" <%=edClass%> <%=readonly%>  onKeyDown="return getNextElement();" >
          <%}%>
        </td>
          <td noWrap class="tdTitle"></td>
          <td noWrap class="td"></td>
        </tr>
        <tr>
        <tr>
          <td colspan="9" noWrap class="td"> <table width="100%" cellpadding=0 cellspacing=0 dwcopytype="CopyTableRow">
              <tr>
                <td nowrap><div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_0');return false;">主体及附属设备</a></div></td>
                <td nowrap><div id="tabDivINFO_EX_1" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_1');return false;">主体及附属设备变更记录</a></div></td>
                <td nowrap><div id="tabDivINFO_EX_2" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_2');return false;">大修理记录</a></div></td>
                <td nowrap><div id="tabDivINFO_EX_3" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_3');return false;">保养记录</a></div></td>
                <td nowrap><div id="tabDivINFO_EX_4" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_4');return false;">转移记录</a></div></td>
                <td nowrap><div id="tabDivINFO_EX_5" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_5');return false;">停用记录</a></div></td>
                <td nowrap><div id="tabDivINFO_EX_6" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_6');return false;">调拨记录</a></div></td>
                <td nowrap><div id="tabDivINFO_EX_7" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_7');return false;">清理或报废记录</a></div></td>
                <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a></td>
              </tr>
            </table>
            <%--主体及附属设备--%>
            <% RowMap detail = null;%>
            <div style="display:block;width:750;height:260;overflow-y:auto;overflow-x:auto;" id="cntDivINFO_EX_0"  class="tabContent">
              <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                <tr class="tableTitle">
                  <td class="td" width='15'>&nbsp;</td>
                  <td nowrap width="25" style='display:<%=isCanChange?"block":"none"%>'><div align="center">
                      <input name="image222" type="image" class="img" title="添加" onClick="sumitForm(<%=recordCardBean.APPERTAIN_ADD%>)" src="../images/add.gif"  border="0">
                    </div></td>
                  <%--detailProducer.printTitle(pageContext, "height='20'");--%>
                  <td nowrap height=20>名称</td>
                  <td nowrap>型号规格</td>
                  <td nowrap>金额</td>
                </tr>
                     <%
                      for(int i=0; i<detailRows.length; i++)   {
                      detail = detailRows[i];
                      %>
                <tr>
                  <td class="td" nowrap width='15' align=center><%=i+1%> </td>
                  <td align=center style='display:<%=isCanChange?"block":"none"%>'> <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=recordCardBean.APPERTAIN_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                  </td>
                  <td> <input type="text" <%=detailClass%> <%=readonly%>   style="width:100%" name="appertain_name_01_<%=i%>" value='<%=detail.get("appertain_name")%>'>
                  </td>
                  <td> <input type="text" <%=detailClass%> <%=readonly%>  style="width:100%" name="standard_gg_01_<%=i%>" value='<%=detail.get("standard_gg")%>'>
                  </td>
                  <td> <input type="text" <%=detailClass%> <%=readonly%>   style="width:100%"  name="buy_money_<%=i%>" value='<%=detail.get("buy_money")%>'>
                  </td>
                </tr>
                <%}%>
              </table>
            </div>
            <%--主体及附属设备变更记录--%>
            <% RowMap face=null;int appertainChangeTD=0;%>
            <div id="cntDivINFO_EX_1" style="display:none;width:750;overflow-y:auto;height:260;overflow-x:auto;" class="tabContent">
              <table id="tableview2" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                <tr class="tableTitle">
                  <td class="td" nowrap width=15></td>
                  <td nowrap width="25" style='display:<%=isCanChange?"block":"none"%>' ><div align="center">
                      <input name="image222" type="image" class="img" title="添加" onClick="sumitForm(<%=recordCardBean.APPERTAINCHANGEDEL_ADD%>)" src="../images/add.gif"  border="0">
                    </div></td>
                  <td  height='20' nowrap width="100">日期</td>
                  <td  height='20' nowrap>凭证号数</td>
                  <td  nowrap width="100">名称</td>
                  <td  height='20' nowrap>型号规格</td>
                  <td  height='20' nowrap>单位</td>
                  <td  height='20' nowrap>数量</td>
                  <td  height='20' nowrap>增加金额</td>
                  <td  height='20' nowrap>减少金额</td>
                </tr>
                <%
                      for(int i=0;i<appertainChangeRows.length; i++)   {
                        face=appertainChangeRows[i];
                         appertainChangeTD=i+1;
                    %>
                <tr>
                  <td class="td" nowrap  align=center><%=i+1%> </td>
                  <td class="td" nowrap align=center style='display:<%=isCanChange?"block":"none"%>'>
                    <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=recordCardBean.APPERTAINCHANGEDEL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                  </td>
                  <td><input type="text" name="change_date_<%=i%>" value='<%=face.get("change_date")%>' maxlength="10" style="width:65" <%=detailClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();" >
                  <%if(isCanChange){%>
                   <a href='#'><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onClick="selectDate(change_date_<%=i%>);"></a></td>
                  <%}%>
                  <td class="td" nowrap> <input type="text" <%=detailClass%> <%=readonly%>  style="width:100%" name="voucher_<%=i%>" value='<%=face.get("voucher")%>'>
                  </td>
                  <td class="td" nowrap> <input type="text" <%=detailClass%> <%=readonly%>  style="width:100%" name="appertain_name_<%=i%>" value='<%=face.get("appertain_name")%>'>
                  </td>
                  <td class="td"> <input type="text" <%=detailClass%> <%=readonly%>  style="width:100%" name="standard_gg_02_<%=i%>" value='<%=face.get("standard_gg")%>'>
                  </td>
                  <td class="td"> <input type="text" <%=detailClass%> <%=readonly%>  style="width:100%" name="unit_<%=i%>" value='<%=face.get("unit")%>'>
                  </td>
                  <td class="td"> <input type="text" <%=detailClass%> <%=readonly%>  style="width:100%" name="num_<%=i%>" value='<%=face.get("num")%>'>
                  </td>
                  <td class="td"> <input type="text" <%=detailClass%> <%=readonly%>  style="width:100%" name="add_money_<%=i%>" value='<%=face.get("add_money")%>'>
                  </td>
                  <td class="td"> <input type="text" <%=detailClass%> <%=readonly%>  style="width:100%" name="reduce_money_<%=i%>" value='<%=face.get("reduce_money")%>'>
                  </td>
                </tr>
                <%}%>
              </table>
            </div>
            <!--大修理记录-->
          <% RowMap heavyRepair=null;%>
           <div id="cntDivINFO_EX_2" style="display:none;width:750;overflow-y:auto;height:260;overflow-x:auto;" class="tabContent">
              <table id="tableview3" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                <tr class="tableTitle">
                  <td nowrap width="40" ></td>
                  <td  height='20' nowrap width="100">日期</td>
                  <td  height='20' nowrap>元件编码</td>
                  <td  nowrap >元件名称</td>
                  <td  height='20' nowrap width="100">修理记录单号</td>
                  <td  height='20' nowrap>备注</td>
                </tr>
                <%
                 for(int i=0;i<heavyRepairRows.length; i++)   {
                 heavyRepair=heavyRepairRows[i];
                 RowMap productRow =productBean.getLookupRow(heavyRepair.get("cpid"));
                %>
                <tr>
                  <td class="td" nowrap>
                   <input name="image" class="img" type="image" title="货物明细" onClick="selectRow();openSelectUrl('../pub/pdf.jsp?code=kc_stocks&operate=0&src=../pub/main.jsp&a$cpid=<%=heavyRepair.get("cpid")%>')" src='../images/select_prod.gif' border="0">
                   <input name="image" class="img" type="image" title="存货收发单据明细" value="4040013" onClick="selectRow();detailRep('2063','0', '2004-10-01','2004-10-16', '')" src="../images/edit.gif" border="0">
                  </td>
                  <td class="td">
                   <input type="text" class='ednone' readonly style="width:100%" name="" value='<%=heavyRepair.get("maintain_date")%>'>
                  </td>
                  <td class="td">
                   <input type="hidden" name="cpid" value='<%=heavyRepair.get("cpid")%>'>
                   <input type="text" class='ednone' readonly style="width:100%" name="" value='<%=productRow.get("cpbm")%>'>
                  </td>
                  <td class="td">
                   <input type="text" class='ednone' readonly style="width:100%" name="" value='<%=productRow.get("product")%>'>
                  </td>
                  <td class="td">
                   <input type="text" class='ednone' readonly style="width:100%" name="" value='<%=heavyRepair.get("maintainResultNO")%>'>
                  </td>
                  <td class="td">
                   <input type="text" class='ednone' readonly style="width:100%" name="" value='<%=heavyRepair.get("memo")%>'>
                  </td>
                </tr>
                <%}%>
              </table>
            </div>
           <!--保养记录-->
           <% RowMap maintainResult=null;%>
           <div id="cntDivINFO_EX_3" style="display:none;width:750;overflow-y:auto;height:260;overflow-x:auto;" class="tabContent">
              <table id="tableview4" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                <tr class="tableTitle">
                  <td  height='20' nowrap>元件编码</td>
                  <td  height='20' nowrap>元件名称</td>
                  <td  nowrap>内容</td>
                  <td  height='20' nowrap>实际开始时间</td>
                  <td  height='20' nowrap>实际完成时间</td>
                  <td  height='20' nowrap>保养人</td>
                  <td  height='20' nowrap>完成情况</td>
                  <td  height='20' nowrap>备注</td>
                </tr>
                <%
                 for(int i=0;i<maintainResultRows.length; i++)   {
                 maintainResult=maintainResultRows[i];
                 RowMap productRow =productBean.getLookupRow(maintainResult.get("cpid"));
                 //RowMap personRow =personBean.getLookupRow(maintainResult.get("personid"));
                %>
                <tr>
                  <td class="td">
                   <input type="text" class='ednone' readonly style="width:100%" name="" value='<%=productRow.get("cpbm")%>'>
                  </td>
                  <td class="td">
                   <input type="text" class='ednone' readonly style="width:100%" name="" value='<%=productRow.get("product")%>'>
                  </td>
                  <td class="td">
                   <input type="text" class='ednone' readonly style="width:100%" name="" value='<%=maintainResult.get("content")%>'>
                  </td>
                  <td class="td">
                   <input type="text" class='ednone' readonly style="width:100%" name="" value='<%=maintainResult.get("fact_startdate")%>'>
                  </td>
                  <td class="td">
                   <input type="text" class='ednone' readonly style="width:100%" name="" value='<%=maintainResult.get("fact_finishdate")%>'>
                  </td>
                  <td class="td">
                   <input type="text" class='ednone' readonly style="width:100%" name="" value='<%=personBean.getLookupName(maintainResult.get("personid"))%>'>
                  </td>
                  <td class="td">
                   <input type="text" class='ednone' readonly style="width:100%" name="" value='<%=maintainResult.get("finish_circs")%>'>
                  </td>
                  <td class="td">
                   <input type="text" class='ednone' readonly style="width:100%" name="" value='<%=maintainResult.get("memo")%>'>
                  </td>
                </tr>
                <%}%>
              </table>
            </div>
            <!--转移记录-->
            <% RowMap displaceResult=null;%>
            <div id="cntDivINFO_EX_4" style="display:none;width:750;overflow-y:auto;height:260;overflow-x:auto;" class="tabContent">
               <table id="tableview5" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                 <tr class="tableTitle">
                  <td class="td" nowrap width=15></td>
                  <td nowrap width="25" style='display:<%=isCanChange?"block":"none"%>'><div align="center">
                      <input name="image222" type="image" class="img" title="添加" onClick="sumitForm(<%=recordCardBean.DISPLACERESULT_ADD%>)" src="../images/add.gif"  border="0">
                    </div></td>
                   <td  height='20' nowrap width="100">日期</td>
                   <td  height='20' nowrap  width="110">接管部门</td>
                   <td  nowrap>存放地点</td>
                   <td  height='20' nowrap width="100">移交部门签章</td>
                   <td  height='20' nowrap width="110">接收部门签章</td>
                 </tr>
                    <%
                      for(int i=0;i<displaceResultRows.length; i++)   {
                        displaceResult=displaceResultRows[i];
                    %>
                 <tr>
                   <td class="td" align=center><%=i+1%></td>
                   <td class="td" nowrap align=center style='display:<%=isCanChange?"block":"none"%>'>
                    <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=recordCardBean.DISPLACERESULT_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                   </td>
                   <td><input type="text" name="displace_date_<%=i%>" value='<%=displaceResult.get("displace_date")%>' maxlength="10" style="width:65" <%=detailClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();" >
                    <%if(isCanChange){%><a href='#'><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onClick="selectDate(displace_date_<%=i%>);"></a><%}%></td>
                   <td class="td">
                    <%if(!isCanChange)out.print("<input type='text' value='"+deptBean.getLookupName(displaceResult.get("take_deptID"))+"' style='width:110' class='ednone' readonly>");
                       else {%>
                     <pc:select name='<%="take_deptID_"+i%>'  addNull="1"  style="width:110" value='<%=displaceResult.get("take_deptID")%>'>
                       <%=deptBean.getList()%>
                     </pc:select>
                    <%}%>
                    </td>
                    <td class="td">
                     <input type="text" <%=detailClass%> <%=readonly%>  style="width:100%" name="depositary_<%=i%>" value='<%=displaceResult.get("depositary")%>'>
                    </td>
                   <td class="td">
                   <%if(!isCanChange)out.print("<input type='text' value='"+personBean.getLookupName(displaceResult.get("handoverPersonID"))+"' style='width:100' class='ednone' readonly>");
                       else {%>
                     <pc:select name='<%="handoverPersonID_"+i%>'  addNull="1"  style="width:100" value='<%=displaceResult.get("handoverPersonID")%>'>
                       <%=personBean.getList()%>
                     </pc:select>
                   <%}%>
                   </td>
                   <td class="td">
                   <%if(!isCanChange)out.print("<input type='text' value='"+personBean.getLookupName(displaceResult.get("takePersonID"))+"' style='width:100' class='ednone' readonly>");
                       else {%>
                     <pc:select name='<%="takePersonID_"+i%>'  addNull="1"  style="width:100" value='<%=displaceResult.get("takePersonID")%>'>
                       <%=personBean.getList()%>
                     </pc:select>
                    <%}%>
                  </td>
                 </tr>
                 <%}%>
               </table>
            </div>
            <!--停用记录-->
            <% RowMap stopAnnal=null;int stopAnnalTD=0;%>
            <div id="cntDivINFO_EX_5" style="display:none;width:750;overflow-y:auto;height:260;overflow-x:auto;" class="tabContent">
               <table id="tableview6" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                 <tr class="tableTitle">
                  <td class="td" nowrap width=15></td>
                  <td nowrap width="25" style='display:<%=isCanChange?"block":"none"%>'><div align="center">
                      <input name="image222" type="image" class="img" title="添加" onClick="sumitForm(<%=recordCardBean.STOPANNAL_ADD%>)" src="../images/add.gif"  border="0">
                    </div></td>
                   <td  height='20' nowrap width="150">开始时间</td>
                   <td  height='20' nowrap width="150">结束时间</td>
                   <td  nowrap>原因</td>
                 </tr>
                    <%
                      for(int i=0;i<stopAnnalRows.length; i++)   {
                        stopAnnal=stopAnnalRows[i];
                        stopAnnalTD=i+1;
                    %>
                 <tr>
                   <td class="td" align=center><%=i+1%></td>
                   <td class="td" nowrap align=center style='display:<%=isCanChange?"block":"none"%>'>
                    <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=recordCardBean.STOPANNAL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                   </td>
                   <td class="td">
                    <%
                    String startDate=stopAnnal.get("start_date");
                    String kshour="";
                    String ksminute="";
                    String newStartDate ="";
                    if(startDate.length()>10){
                      GregorianCalendar calendar=new GregorianCalendar();
                      Date ksdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate);
                      calendar.setTime(ksdate);
                      kshour=String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
                      if(kshour.length()<2)kshour="0"+kshour;
                      ksminute=String.valueOf(calendar.get(Calendar.MINUTE));
                      if(ksminute.length()<2)ksminute="0"+ksminute;
                      newStartDate =startDate.substring(0,10).trim();
                    }
                    else newStartDate=startDate;

                    String endDate=stopAnnal.get("end_date");
                    String endhour="";
                    String endminute="";
                    String newEndDate="";
                    if(endDate.length()>10){
                      GregorianCalendar end_calendar=new GregorianCalendar();
                      Date end_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate);
                      end_calendar.setTime(end_date);
                      endhour=String.valueOf(end_calendar.get(Calendar.HOUR_OF_DAY));
                      if(endhour.length()<2)endhour="0"+endhour;
                      endminute=String.valueOf(end_calendar.get(Calendar.MINUTE));
                      if(endminute.length()<2)endminute="0"+endminute;
                      newEndDate =endDate.substring(0,10).trim();
                    }
                    else newEndDate=endDate;
                    %>
                    <input type="text" name="start_date_<%=i%>" value='<%=newStartDate%>' maxlength='10' style="width:65" <%=detailClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
                    <%if(isCanChange){%><a href="javascript:"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(start_date_<%=i%>);"></a><%}%>
                    <input  type="text" name="start_date_hour_<%=i%>" value='<%=kshour%>' maxlength='2' style="width:20" <%=detailClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>23)this.value=this.value%24;" onafterpaste="this.value=this.value.replace(/\D/g,'')">:
                    <input type="text" name="start_date_minute_<%=i%>" value='<%=ksminute%>' maxlength='2' style="width:20" <%=detailClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>59)this.value=this.value%60;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
                   </td>
                   <td class="td">
                    <input type="text" name="end_date_<%=i%>" value='<%=newEndDate%>' maxlength='10' style="width:65" <%=detailClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
                    <%if(isCanChange){%><a href="javascript:"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(end_date_<%=i%>);"></a><%}%>
                    <input type="text" name="end_date_hour_<%=i%>" value='<%=endhour%>' maxlength='2' style="width:20" <%=detailClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>23)this.value=this.value%24;" onafterpaste="this.value=this.value.replace(/\D/g,'')">:
                    <input type="text" name="end_date_minute_<%=i%>" value='<%=endminute%>' maxlength='2' style="width:20" <%=detailClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>59)this.value=this.value%60;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
                   </td>
                   <td class="td">
                     <input type="text" <%=detailClass%> <%=readonly%>   style="width:100%" name="cause_<%=i%>" value='<%=stopAnnal.get("cause")%>'>
                   </td>
                 </tr>
                 <%}%>
               </table>
            </div>
            <!--调拨记录-->
            <% RowMap devolveAnnal=null;int devolveAnnalTD=0;%>
            <div id="cntDivINFO_EX_6" style="display:none;width:750;overflow-y:auto;height:260;overflow-x:auto;" class="tabContent">
               <table id="tableview7" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                 <tr class="tableTitle">
                  <td class="td" nowrap width=15></td>
                  <td nowrap width="25" style='display:<%=isCanChange?"block":"none"%>'><div align="center">
                      <input name="image222" type="image" class="img" title="添加" onClick="sumitForm(<%=recordCardBean.DEVOLVEANNAL_ADD%>)" src="../images/add.gif"  border="0">
                    </div></td>
                   <td  height='20' nowrap width="100">调拨日期</td>
                   <td  height='20' nowrap width="110">调入部门</td>
                   <td  nowrap width="100">调拨单号</td>
                   <td  nowrap width="100">调拨价格</td>
                   <td  nowrap>备注</td>
                 </tr>
                    <%
                      for(int i=0;i<devolveAnnalRows.length; i++)   {
                        devolveAnnal=devolveAnnalRows[i];
                        devolveAnnalTD=i+1;
                    %>
                 <tr>
                   <td class="td" align=center><%=i+1%></td>
                   <td class="td" nowrap align=center style='display:<%=isCanChange?"block":"none"%>'>
                    <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=recordCardBean.DEVOLVEANNAL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                   </td>
                    <td class="td">
                    <input type="text" name="devolve_date_<%=i%>" value='<%=devolveAnnal.get("devolve_date")%>' maxlength='10' style="width:65" <%=detailClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
                    <%if(isCanChange){%><a href="javascript:"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(devolve_date_<%=i%>);"></a><%}%>
                    </td>
                    <td class="td">
                    <%if(!isCanChange)out.print("<input type='text' value='"+deptBean.getLookupName(displaceResult.get("deptid"))+"' style='width:100' class='ednone' readonly>");
                       else {%>
                      <pc:select name='<%="devolve_deptid_"+i%>'  addNull="1"  style="width:110" value='<%=devolveAnnal.get("deptid")%>'>
                       <%=deptBean.getList()%>
                      </pc:select>
                    <%}%>
                    </td>
                    <td class="td">
                     <input type="text" <%=detailClass%> <%=readonly%>   style="width:100%" name="devolveNO_<%=i%>" value='<%=devolveAnnal.get("devolveNO")%>'>
                    </td>
                    <td class="td">
                     <input type="text" <%=detailClass%> <%=readonly%>   style="width:100%" name="devolve_price_<%=i%>" value='<%=devolveAnnal.get("devolve_price")%>'>
                    </td>
                    <td class="td">
                     <input type="text" <%=detailClass%> <%=readonly%>   style="width:100%" name="memo_<%=i%>" value='<%=devolveAnnal.get("memo")%>'>
                    </td>
                 </tr>
                 <%}%>
               </table>
            </div>
            <!--清理或报废记录-->
            <% RowMap rejectAnnal=null;%>
            <div id="cntDivINFO_EX_7" style="display:none;width:750;overflow-y:auto;height:260;overflow-x:auto;" class="tabContent">
               <table id="tableview8" width="810" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                 <tr class="tableTitle">
                  <td class="td" nowrap width=15></td>
                  <td nowrap width="25" style='display:<%=isCanChange?"block":"none"%>'><div align="center">
                      <input name="image222" type="image" class="img" title="添加" onClick="sumitForm(<%=recordCardBean.REJECTANNAL_ADD%>)" src="../images/add.gif"  border="0">
                    </div></td>
                   <td  height='20' nowrap>清理原因</td>
                   <td  height='20' nowrap width="100">转帐日期</td>
                   <td  nowrap width="60">清理费用</td>
                   <td  nowrap width="40">原价</td>
                   <td  nowrap width="60">变价收入</td>
                   <td  nowrap width="80">累计折旧</td>
                   <td  nowrap width="60">清理差额</td>
                 </tr>
                    <%
                      for(int i=0;i<rejectAnnalRows.length; i++)   {
                        rejectAnnal=rejectAnnalRows[i];
                    %>
                 <tr>
                   <td class="td" align=center><%=i+1%></td>
                   <td class="td" nowrap align=center style='display:<%=isCanChange?"block":"none"%>'>
                    <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=recordCardBean.REJECTANNAL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                   </td>
                    <td class="td">
                     <input type="text" <%=detailClass%> <%=readonly%>   style="width:100%" name="liquidate_cause_<%=i%>" value='<%=rejectAnnal.get("liquidate_cause")%>'>
                    </td>
                   <td class="td">
                    <input type="text" name="virement_date_<%=i%>" value='<%=rejectAnnal.get("virement_date")%>' maxlength='10' style="width:65" <%=detailClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
                    <%if(isCanChange){%><a href="javascript:"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(virement_date_<%=i%>);"></a><%}%>
                   </td>
                    <td class="td">
                     <input type="text" <%=detailClass%> <%=readonly%>   style="width:100%" name="liquidate_rate_<%=i%>" value='<%=rejectAnnal.get("liquidate_rate")%>'>
                    </td>
                    <td class="td">
                     <input type="text" <%=detailClass%> <%=readonly%>   style="width:100%" name="formerly_price_<%=i%>" value='<%=rejectAnnal.get("formerly_price")%>'>
                    </td>
                    <td class="td">
                     <input type="text" <%=detailClass%> <%=readonly%>   style="width:100%" name="earning_<%=i%>" value='<%=rejectAnnal.get("earning")%>'>
                    </td>
                    <td class="td">
                     <input type="text" <%=detailClass%> <%=readonly%>   style="width:100%" name="total_depreciation_<%=i%>" value='<%=rejectAnnal.get("total_depreciation")%>'>
                    </td>
                    <td class="td">
                     <input type="text" <%=detailClass%> <%=readonly%>   style="width:100%" name="liquidate_margin_<%=i%>" value='<%=rejectAnnal.get("liquidate_margin")%>'>
                    </td>
                 </tr>
                 <%}%>
               </table>
            </div>
          </td></td></tr>
      </table></td></tr>
    </table></td></tr>
    <tr>
      <td> <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">
          <tr>
            <td colspan="3" noWrap class="tableTitle">
              <%if(isCanAdd){%>
              <input name="button2"  style="width:80" type="button" class="button" onClick="if(!checkAddResult()){return;}sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存新增(N)">
              <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <%}%>
              <%if(isCanChange){%>
              <input name="btnback" type="button" class="button" onClick="if(!checkAddResult()){return;}sumitForm(<%=Operate.POST%>);" style="width:90" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
              <%}%>
              <%if(isCanDelete){%>
               <input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value=" 删除(D)">
               <pc:shortcut key="d" script='delMaster();'/>
              <%}%>
              <input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
              <pc:shortcut key="c" script='backList();'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
/*检查输入框输入的字符是否为非法字符*/
function checkKeyCode(){
var str=window.event.keyCode;
if(33<=str&&str<=47)alert('输入的字符为非法字符');
if(58<=str&&str<=64)alert('输入的字符为非法字符');
if(91<=str&&str<=96)alert('输入的字符为非法字符');
if(123<=str&&str<=126)alert('输入的字符为非法字符');
}
/*检查字符窜是否为非法字符窜
 *以下字符为非法字符：~ ` / ? > < ; : } { ) ( * & ^ % $ # @ ! "
 *返回: ture@不为非法字符;false@为非法字符
*/
function checkChar(str){
  var index=str.length;
  for(i=0;i<index;i++){
    var uniCode=str.charCodeAt(i);
    if((33<=uniCode&&uniCode<=47)||(58<=uniCode&&uniCode<=64)||(91<=uniCode&&uniCode<=96)||(123<=uniCode&&uniCode<=126))
      return false;
  }
  return true;
}
/*主体及附属设备变更记录字段检查*/
function checkMaster(){
if(form1.equipment_code.value==''){alert('设备编码不能为空');return false;}
if(!checkChar(form1.equipment_code.value)){alert('设备编码为非法字符窜');return false;}
if(form1.equipment_name.value==''){alert('设备名称不能为空');return false;}
if(form1.deptid.value==''){alert('购入部门不能为空');return false;}
if(compareDate(form1.manufactureDate.value,form1.buy_date.value)==1){alert('<购买日期>不能小于<制造日期>');return false;}
if(compareDate(form1.buy_date.value,form1.use_date.value)==1){alert('<开始使用日期>不能大小<购买日期>');return false;}
if(!checkDate(form1.manufactureDate))return false;
if(!checkDate(form1.buy_date))return false;
if(!checkDate(form1.use_date))return false;
return true;
}
function checkAppertainChange(){
    var appertainChangeTD=<%=appertainChangeTD-1%>;
    var change_date_Obj;
    for(i=0;i<appertainChangeTD+1;i++){
      change_date_Obj='change_date_'+i;
      change_date_Obj=document.all[change_date_Obj].value;
      if(change_date_Obj==''){alert('主体及附属设备变更记录：第'+(i+1)+'行记录<日期>不能为空');return false;}
    }
    return true;
}
/*停用记录字段检查*/
function checkStopAnnal(){
  var stopAnnalTD=<%=stopAnnalTD-1%>;
  var startDdateObj;
  var endDdateObj;
  for(i=0;i<stopAnnalTD+1;i++){
    startDdateObj='start_date_'+i;
    endDdateObj='end_date_'+i;
    startDateValue=document.all[startDdateObj].value;
    endDateValue=document.all[endDdateObj].value;
    if(startDateValue==''){alert('停用记录：第'+(i+1)+'行记录<开始时间>不能为空');return false;}
    if(endDateValue==''){alert('停用记录：第'+(i+1)+'行记录<结束时间>不能为空');return false;}
    if(compareDate(startDateValue,endDateValue)==1){alert('停用记录：第'+(i+1)+'行记录<结束时间>不能小于<开始时间>');return false;}
  }
  return true;
}
/*--调拨字段检查--*/
function checkDevolveAnnal(){
  var devolveAnnalTD=<%=devolveAnnalTD-1%>;
  var devolve_dateObj;
  var devolve_deptidObj;
  for(i=0;i<devolveAnnalTD+1;i++){
    devolve_dateObj='devolve_date_'+i;
    devolve_deptidObj='devolve_deptid_'+i;
    devolve_dateValue=document.all[devolve_dateObj].value;
    devolve_deptidValue=document.all[devolve_deptidObj].value;
    if(devolve_dateValue==''){alert('调拨记录：第'+(i+1)+'行记录<调拨日期>不能为空');return false;}
    if(devolve_deptidValue==''){alert('调拨记录：第'+(i+1)+'行记录<调入部门>不能为空');return false;}
  }
  return true;
}
function checkAddResult(){
  if(checkMaster()&&checkAppertainChange()&&checkStopAnnal()&&checkDevolveAnnal()) return true;
  return false;
}
 initDefaultTableRow('tableview1',1);
 initDefaultTableRow('tableview2',1);
 initDefaultTableRow('tableview3',1);
 initDefaultTableRow('tableview4',1);
 initDefaultTableRow('tableview5',1);
 initDefaultTableRow('tableview6',1);
 initDefaultTableRow('tableview7',1);
 initDefaultTableRow('tableview8',1);
 INFO_EX = new TabControl('INFO_EX',0);
 AddTabItem(INFO_EX,'INFO_EX_0','tabDivINFO_EX_0','cntDivINFO_EX_0');
 AddTabItem(INFO_EX,'INFO_EX_1','tabDivINFO_EX_1','cntDivINFO_EX_1');
 AddTabItem(INFO_EX,'INFO_EX_2','tabDivINFO_EX_2','cntDivINFO_EX_2');
 AddTabItem(INFO_EX,'INFO_EX_3','tabDivINFO_EX_3','cntDivINFO_EX_3');
 AddTabItem(INFO_EX,'INFO_EX_4','tabDivINFO_EX_4','cntDivINFO_EX_4');
 AddTabItem(INFO_EX,'INFO_EX_5','tabDivINFO_EX_5','cntDivINFO_EX_5');
 AddTabItem(INFO_EX,'INFO_EX_6','tabDivINFO_EX_6','cntDivINFO_EX_6');
 AddTabItem(INFO_EX,'INFO_EX_7','tabDivINFO_EX_7','cntDivINFO_EX_7');
 if (window.top.StatFrame+''!='undefined'){ var tmp_curtab=window.top.StatFrame.GetRegisterVar('INFO_EX');if (tmp_curtab!='') {SetActiveTab(INFO_EX,tmp_curtab);}}
</SCRIPT>
<%out.print(retu);%>
</BODY>
</Html>
