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
  String pageCode = "sc_track_bill";
  String op_over = "op_over";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.produce.B_SC_TrackBill b_TrackbillBean = engine.erp.produce.B_SC_TrackBill.getInstance(request);
  engine.project.LookUp guigeBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_SPEC_PROPERTY);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_STORE_AREA);
  engine.project.LookUp dwtxBean =engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp trackBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCE_TRACK_TYPE);
  boolean isCanSearch=loginBean.hasLimits(pageCode,op_search);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanDel=loginBean.hasLimits(pageCode, op_delete);

  RowMap m_RowInfo = b_TrackbillBean.getMasterRowinfo();   //行到主表的一行信息



  String typeClass = "class=edbox";
  String readonly = "";
  //boolean isCanEdit = true;
  boolean isCanDelete = true;
  EngineDataSet dsDWTX = b_TrackbillBean.getMaterTable();

  String retu = b_TrackbillBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
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

<script language="javascript">
  function productCodeSelect(obj, i)
  {
    ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product','fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
  function toDetail()
  {
  location.href='sc_track_bill_edit.jsp';
  }
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
  function customerCodeSelect(obj)
{
    CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
//客户名称
function customerNameSelect(obj)
{
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
function showFixedQuery(){
  //点击查询按钮打开查询对话框
   showFrame('fixedQuery', true, "", true);//显示层
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD NOWRAP align="center">生产跟踪卡</TD>
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
       EngineDataSet tmp=b_TrackbillBean.getMaterTable();
       pageContext.setAttribute(key, tmp);
       int iPage = loginBean.getPageSize();
       String pageSize = ""+iPage;
     %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"></pc:dbNavigator>
     </td>
      <td class="td" align="right">
        <%if(loginBean.hasLimits(pageCode,op_add)) { String qu = "showFixedQuery()";%>
        <%if(isCanSearch)%><input name="search" type="button" title = "查询"  class="button" onClick="showFixedQuery()" value=" 查询(Q) "><pc:shortcut key="q" script='<%=qu%>'/><%}%>
        <%if(loginBean.hasLimits(pageCode,op_add)) { String ret = "location.href='("+b_TrackbillBean.retuUrl+",-1)";%>
        <input name="button2" type="button" title = "返回" class="button" onClick="location.href='<%=b_TrackbillBean.retuUrl%>'" value=" 返回(C) "></td><pc:shortcut key="c" script='<%=ret%>'/><%}%>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>
      <%if(loginBean.hasLimits(pageCode,op_add))  {String add = "sumitForm("+Operate.ADD+",-1)";%>
     <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0"><pc:shortcut key="a" script='<%=add%>'/>
     <%}%>
     </td>
     <td nowrap>生产跟踪卡号</td><td nowrap>往来单位名称</td><td nowrap>生产跟踪单类型名称</td><td nowrap>品名</td>
     <td nowrap>规格</td>
     <td nowrap>仓库</td><td nowrap>原料批号</td><td nowrap>检验员</td>
     <td nowrap>毛重</td><td nowrap>净重</td><td nowrap>检验结果</td> <td nowrap>经手人</td>
     <td nowrap>制单人</td> <td nowrap>审核人</td><td nowrap>达因面</td>
     <td nowrap>热封面</td><td nowrap>等级</td><td nowrap>总损耗</td>
     <td nowrap>总成品率</td>
     <td nowrap>正品率</td>
     <td nowrap>副品率</td>
     <td nowrap>废品率</td>
     </tr>
    <%//b_TrackbillBean.fetchData(loginBean.getRowMin(request), loginBean.getRowMax(request), true);
       EngineDataSet list = b_TrackbillBean.getMaterTable();
       trackBean.regData(list,"track_type_ID");
       dwtxBean.regData(list,"dwtxId");
       prodBean.regData(list,"cpId");
       guigeBean.regData(list,"dmsxID");
       storeBean.regData(list,"storeid");
      int i=0;
      list.first();
      for(; i < list.getRowCount(); i++) {

    %>
    <tr onDblClick="sumitForm(<%=b_TrackbillBean.VIEW_DETAIL%>,<%=i%>)" >
      <td class="td" nowrap align="center">
      <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=b_TrackbillBean.VIEW_DETAIL%>,<%=i%>)" src="../images/edit.gif" border="0">
       <%if(loginBean.hasLimits(pageCode,op_delete)){%>
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=i %>)" src="../images/del.gif" border="0">
      <%}%>
    </td>


      <td class="td" nowrap><%=list.getValue("track_bill_no")%></td>
      <td class="td" nowrap><%=dwtxBean.getLookupName(list.getValue("dwtxId"))%></td>

      <td class="td" nowrap><%=trackBean.getLookupName(list.getValue("track_type_ID"))%></td>
      <td class="td" nowrap><%=prodBean.getLookupName(list.getValue("cpId"))%></td>
      <td class="td" nowrap><%=guigeBean.getLookupName(list.getValue("dmsxID"))%></td>
          <td class="td" nowrap><%=storeBean.getLookupName(list.getValue("storeid"))%></td>
      <td class="td" nowrap><%=list.getValue("material_no")%></td>

      <td class="td" nowrap><%=list.getValue("checker")%></td>
      <td class="td" nowrap><%=list.getValue("gross_weight")%></td>
      <td class="td" nowrap><%=list.getValue("net_weight")%></td>

      <td class="td" nowrap><%=list.getValue("check_result")%></td>
      <td class="td" nowrap><%=list.getValue("handler")%></td>
      <td class="td" nowrap><%=list.getValue("creator")%></td>
      <td class="td" nowrap><%=list.getValue("approver")%></td>
      <td class="td" nowrap><%=list.getValue("dyne_side")%></td>
      <td class="td" nowrap><%=list.getValue("hot_side")%></td>
      <td class="td" nowrap><%=list.getValue("grade")%></td>
      <td class="td" nowrap><%=list.getValue("tot_ull")%></td>
      <td class="td" nowrap><%=list.getValue("tot_ratio")%></td>
      <td class="td" nowrap><%=list.getValue("good_ratio")%></td>
      <td class="td" nowrap><%=list.getValue("hypo_ratio")%></td>
      <td class="td" nowrap><%=list.getValue("waster_ratio")%></td>
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
              <td noWrap class="td" align="center">&nbsp;生产跟踪卡号&nbsp;</td>

              <td noWrap class="td"><input type="text" name="track_bill_no" value='<%=b_TrackbillBean.getFixedQueryValue("track_bill_no")%>' maxlength='50' style="width:120" class="edbox"></td>

             <TD align="center" nowrap class="td">往来单位</TD>
              <td nowrap class="td" colspan=3><input type="hidden" name="dwtxid" value='<%=b_TrackbillBean.getFixedQueryValue("dwtxid")%>'>
              <input type="text" class="edbox" style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='' onchange="customerCodeSelect(this)" >
              <input type="text" name="dwmc"  style="width:260" value='' class="edbox"  onchange="customerNameSelect(this)" >

              <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',fixedQueryform.dwtxid.value)">
              <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value='';">
              </td>
             </TR>
            <TR>
              <td noWrap class="td" align="center">跟踪单类型名称</td>
              <td noWrap class="td">
                <pc:select name="track_type_ID" style="width:131" addNull="1">
                  <%=trackBean.getList(b_TrackbillBean.getFixedQueryValue("track_type_ID"))%>
             </pc:select>
              </td>
             <TD align="center" nowrap class="td">产品</TD>
                <td nowrap class="td" colspan="3">
                <INPUT TYPE="HIDDEN" NAME="cpid" value="">
                <input class="edbox" style="WIDTH:80" name="cpid$cpbm" value='<%=b_TrackbillBean.getFixedQueryValue("cpid$cpbm")%>' onKeyDown="return getNextElement();" readonly>
                <INPUT class="edbox" style="WIDTH:220" id="cpid$product" name="cpid$product" value='<%=b_TrackbillBean.getFixedQueryValue("cpid$product")%>' maxlength='10' onKeyDown="return getNextElement();"  readonly">
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cpid&srcVar=cpid$product&srcVar=cpid$cpbm','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',fixedQueryform.cpid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="fixedQueryform.cpid.value='';fixedQueryform.cpid$product.value='';fixedQueryform.cpid$cpbm.value='';">
                </td>
           </TR>
          <TR>
          <td noWrap class="td" align="center">&nbsp;原料批号&nbsp;</td>
         <td noWrap class="td"><input type="text" name="material_no" value='<%=b_TrackbillBean.getFixedQueryValue("material_no")%>' maxlength='50' style="width:120" class="edbox"></td>
          <td noWrap class="td" align="center">&nbsp;检验员&nbsp;</td>
         <td noWrap class="td"><input type="text" name="checker" value='<%=b_TrackbillBean.getFixedQueryValue("checker")%>' maxlength='50' style="width:120" class="edbox"></td>
         <td noWrap class="td" align="center">&nbsp;审核人&nbsp;</td>
         <td noWrap class="td"><input type="text" name="approver" value='<%=b_TrackbillBean.getFixedQueryValue("approver")%>' maxlength='50' style="width:120" class="edbox"></td>
            </TR>
            <TR>
          <td noWrap class="td" align="center">&nbsp;检验结果&nbsp;</td>
         <td noWrap class="td"><input type="text" name="check_result" value='<%=b_TrackbillBean.getFixedQueryValue("check_result")%>' maxlength='50' style="width:120" class="edbox"></td>
           <td noWrap class="td" align="center">&nbsp;经手人&nbsp;</td>
         <td noWrap class="td"><input type="text" name="handler" value='<%=b_TrackbillBean.getFixedQueryValue("handler")%>' maxlength='50' style="width:120" class="edbox"></td>

        <td noWrap class="td" align="center">&nbsp;制单人&nbsp;</td>
         <td noWrap class="td"><input type="text" name="creator" value='<%=b_TrackbillBean.getFixedQueryValue("creator")%>' maxlength='50' style="width:120" class="edbox"></td>

            </TR>
             <TR>

          <td noWrap class="td" align="center">&nbsp;达因面&nbsp;</td>
         <td noWrap class="td"><input type="text" name="dyne_side" value='<%=b_TrackbillBean.getFixedQueryValue("dyne_side")%>' maxlength='50' style="width:120" class="edbox"></td>
           <td noWrap class="td" align="hot_side">&nbsp;热封面&nbsp;</td>
         <td noWrap class="td"><input type="text" name="hot_side" value='<%=b_TrackbillBean.getFixedQueryValue("hot_side")%>' maxlength='50' style="width:120" class="edbox"></td>
            </TR>

            <TR>

          <td noWrap class="td" align="center">&nbsp;等级&nbsp;</td>
         <td noWrap class="td"><input type="text" name="grade" value='<%=b_TrackbillBean.getFixedQueryValue("grade")%>' maxlength='50' style="width:120" class="edbox"></td>
           <td noWrap class="td" align="center">&nbsp;总损耗&nbsp;</td>
         <td noWrap class="td"><input type="text" name="tot_ull" value='<%=b_TrackbillBean.getFixedQueryValue("tot_ull")%>' maxlength='50' style="width:120" class="edbox"></td>
          <td noWrap class="td" align="center">&nbsp;总成品率&nbsp;</td>
         <td noWrap class="td"><input type="text" name="tot_ratio" value='<%=b_TrackbillBean.getFixedQueryValue("tot_ratio")%>' maxlength='50' style="width:120" class="edbox"></td>

           </TR>
            <TR>
              <%if(loginBean.hasLimits(pageCode,op_add))  {String qu = "sumitFixedQuery("+b_TrackbillBean.OPERATE_SEARCH+",-1)";%>
              <TD nowrap colspan=3 height=30 align="center">
                 <INPUT class="button" title = "查询" onClick="sumitFixedQuery(<%=b_TrackbillBean.OPERATE_SEARCH%>)" type="button" value=" 查询(Q) " name="button" onKeyDown="return getNextElement();"><pc:shortcut key="q" script='<%=qu%>'/><%}%>
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


