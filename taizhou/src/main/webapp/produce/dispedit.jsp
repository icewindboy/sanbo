<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.produce.B_SC_TrackBill b_TrackbillBean = engine.erp.produce.B_SC_TrackBill.getInstance(request);
  String pageCode = "sc_track_bill";
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
  location.href='sc_track_bill_edit.jsp';
}
function saveList()
{
sumitForm(<%=b_TrackbillBean.POST_FEQIE%>,-1);
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

<%
  String retu = b_TrackbillBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp guigeBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_SPEC_PROPERTY);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp dwtxBean =  engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp trackBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCE_TRACK_TYPE);
  engine.project.LookUp gongyiBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_TECHNICS_NAME);

  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_TrackbillBean.getMaterTable();
  EngineDataSet list = b_TrackbillBean.getdetailTable();//引用过来的数据集
  EngineDataSet list_detail =b_TrackbillBean.getdetail_de_Table();


  RowMap masterRow = b_TrackbillBean.getMasterRowinfo();//主表一行
  RowMap[] detailRows= b_TrackbillBean.getDeRowinfos();//从表多行
  RowMap[] detail_de_Rows= b_TrackbillBean.getDetailRowinfos();//从表的从表多行
  RowMap corpRow =dwtxBean.getLookupRow(masterRow.get("dwtxid"));
  RowMap detail_detail_RowMap;


  //boolean isEnd =  b_SaleInvoiceBean.isReport||b_SaleInvoiceBean.isApprove || (!b_SaleInvoiceBean.masterIsAdd() && !zt.equals("0"));
  //没有结束,在修改状态,并有删除权限
  boolean isCanDelete =  loginBean.hasLimits(pageCode, op_delete);
  boolean isCanEdit = loginBean.hasLimits(pageCode, op_edit);
  boolean isPutong = false;
  boolean isHengqie = false;
  boolean isDitu = false;
  boolean isFenqie = false;
  String edClass = isCanEdit ? "class=edbox" :"class=edline";
  String detailClass = isCanEdit ?  "class=edFocused" :  "class=edline";
  String detailClass_r = isCanEdit ? "class=edFocused_r" : "class=ednone_r";

  //String needColor = isEnd ? "" : " style='color:#660000'";

  boolean count=list.getRowCount()==0?true:false;

%>
 <BODY oncontextmenu="window.event.returnValue=true" >
 <iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
 <form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
 <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
 <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">

  <table  width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
  <tr valign="top">
  <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
  <tr>
  <td class="activeVTab">分切明细</td>
  </tr>
  </table>
  <table class="table"  id="tbcontrol" CELLSPACING=1 CELLPADDING=0 >
    <%int i = b_TrackbillBean.detailRow;
    RowMap detail =null;

    detail=detailRows[i];


     %>

           <tr class="tableTitle">
           <td nowrap width=10></td>
           <td height='20' nowrap>分切宽度1(mm)</td>
           <td height='20' nowrap>分切宽度2(mm)</td>
           <td height='20' nowrap>分切宽度3(mm)</td>
           <td height='20' nowrap>分切宽度4(mm)</td>
           <td height='20' nowrap>分切宽度5(mm)</td>
           <td height='20' nowrap>分切宽度6(mm)</td>
           <td height='20' nowrap>分切宽度7(mm)</td>
           <td height='20' nowrap>分切宽度8(mm)</td>
           <td height='20' nowrap>分切宽度9(mm)</td>
           </tr>
           <tr>
           <td nowrap width=10></td>
           <td class="td" nowrap align="right">

           <input type="hidden" name="scdwgs" value='<%=detail.get("scdwgs")%>' >
           <input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_widthl"  name="disp_width1"     value='<%=detail.get("disp_width1")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  readonly ></td><!--分切宽度-->
           <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_width2"  name="disp_width2"     value='<%=detail.get("disp_width2")%>'   maxlength='<%=list.getColumn("disp_width2").getPrecision()%>'  readonly></td><!--分切宽度-->
           <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_width3"  name="disp_width3"     value='<%=detail.get("disp_width3")%>'   maxlength='<%=list.getColumn("disp_width3").getPrecision()%>'  readonly></td><!--分切宽度-->
           <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_width4"  name="disp_width4"     value='<%=detail.get("disp_width4")%>'   maxlength='<%=list.getColumn("disp_width4").getPrecision()%>' readonly ></td><!--分切宽度-->
           <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_width5"  name="disp_width5"     value='<%=detail.get("disp_width5")%>'   maxlength='<%=list.getColumn("disp_width5").getPrecision()%>'  readonly ></td><!--分切宽度-->
           <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_width6"  name="disp_width6"     value='<%=detail.get("disp_width6")%>'   maxlength='<%=list.getColumn("disp_width6").getPrecision()%>'  readonly></td><!--分切宽度-->
           <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_width7"  name="disp_width7"     value='<%=detail.get("disp_width7")%>'   maxlength='<%=list.getColumn("disp_width7").getPrecision()%>' readonly ></td><!--分切宽度-->
           <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_width8"  name="disp_width8"     value='<%=detail.get("disp_width8")%>'   maxlength='<%=list.getColumn("disp_width8").getPrecision()%>' readonly ></td><!--分切宽度-->
           <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_width9"  name="disp_width9"     value='<%=detail.get("disp_width9")%>'   maxlength='<%=list.getColumn("disp_width9").getPrecision()%>' readonly ></td><!--分切宽度-->

           </tr>
      <tr class="tableTitle">
    <td height='20' align="center" nowrap>
     <%if(isCanEdit){%>

    <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=b_TrackbillBean.Disp_ADD%>,-1)"  src="../images/add_big.gif" border="0">

    <%}%>
        </td>
            <td height='12' nowrap>分切长度1(m)</td>

            <td height='12' nowrap>分切长度2(m)</td>

            <td height='12' nowrap>分切长度3(m)</td>

            <td height='12' nowrap>分切长度4(m)</td>

            <td height='12' nowrap>分切长度5(m)</td>

            <td height='12' nowrap>分切长度6(m)</td>

            <td height='12' nowrap>分切长度7(m)</td>

            <td height='12' nowrap>分切长度8(m)</td>

            <td height='12' nowrap>分切长度9(m)</td>
                       </tr>
            <%detail_detail_RowMap=null;
            list_detail.first();
            for(int j=0; j<detail_de_Rows.length; j++)   {
            int a=detail_de_Rows.length;
            detail_detail_RowMap=detail_de_Rows[j];
             %>
                        <tr>

                         <td class="td" align="center">

                <%if(isCanDelete){%><input name="image32" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_TrackbillBean.Disp_DEL%>,<%=j%>)" src="../images/del.gif" border="0" align="absmiddle"><%}%>
                </td>

                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_lenth1_<%=j%>"  name="disp_lenth1_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_lenth1")%>' onchange="countdispweight(<%=j%>)"  maxlength='<%=list_detail.getColumn("disp_lenth1").getPrecision()%>'  ></td><!--分切宽度-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_lenth2_<%=j%>"  name="disp_lenth2_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_lenth2")%>' onchange="countdispweight(<%=j%>)"  maxlength='<%=list_detail.getColumn("disp_lenth2").getPrecision()%>'  ></td><!--分切宽度-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_lenth3_<%=j%>"  name="disp_lenth3_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_lenth3")%>' onchange="countdispweight(<%=j%>)" maxlength='<%=list_detail.getColumn("disp_lenth3").getPrecision()%>'  ></td><!--分切宽度-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_lenth4_<%=j%>"  name="disp_lenth4_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_lenth4")%>' onchange="countdispweight(<%=j%>)"  maxlength='<%=list_detail.getColumn("disp_lenth4").getPrecision()%>'  ></td><!--分切宽度-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_lenth5_<%=j%>"  name="disp_lenth5_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_lenth5")%>' onchange="countdispweight(<%=j%>)"  maxlength='<%=list_detail.getColumn("disp_lenth5").getPrecision()%>'  ></td><!--分切宽度-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_lenth6_<%=j%>"  name="disp_lenth6_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_lenth6")%>' onchange="countdispweight(<%=j%>)"  maxlength='<%=list_detail.getColumn("disp_lenth6").getPrecision()%>'  ></td><!--分切宽度-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_lenth7_<%=j%>"  name="disp_lenth7_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_lenth7")%>' onchange="countdispweight(<%=j%>)"  maxlength='<%=list_detail.getColumn("disp_lenth7").getPrecision()%>'  ></td><!--分切宽度-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_lenth8_<%=j%>"  name="disp_lenth8_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_lenth8")%>' onchange="countdispweight(<%=j%>)"  maxlength='<%=list_detail.getColumn("disp_lenth8").getPrecision()%>'  ></td><!--分切宽度-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_lenth9_<%=j%>"  name="disp_lenth9_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_lenth9")%>' onchange="countdispweight(<%=j%>)"  maxlength='<%=list_detail.getColumn("disp_lenth9").getPrecision()%>'  ></td><!--分切宽度-->
                     </tr>
                        <%list_detail.next(); }%>
                       <tr>
                 <td nowrap width=10></td>
                   <td class="td">&nbsp;</td>
                   <td class="td"></td>
                   <td class="td"></td>
                   <td class="td"></td>
                   <td class="td"></td>
                   <td class="td"></td>
                   <td class="td"></td>
                   <td class="td"></td>
                   <td class="td"></td>

                    </tr>
                      <tr class="tableTitle">
                       <td nowrap width=10></td>
                        <td height='20' nowrap>分切重量1(kg)</td>
                        <td height='20' nowrap>分切重量2(kg></td>
                        <td height='20' nowrap>分切重量3(kg)</td>
                        <td height='20' nowrap>分切重量4(kg)</td>
                        <td height='20' nowrap>分切重量5(kg)</td>
                        <td height='20' nowrap>分切重量6(kg)</td>
                        <td height='20' nowrap>分切重量7(kg)</td>
                        <td height='20' nowrap>分切重量8(kg)</td>
                        <td height='20' nowrap>分切重量9(kg)</td>


                     </tr>
                   <%

                     detail_detail_RowMap =null;
                        list_detail.first();
                    for(int j=0; j<detail_de_Rows.length; j++)   {
                      detail_detail_RowMap=detail_de_Rows[j];

                     %>
                       <tr>
                       <td nowrap width=10></td>
                       <td class="td" nowrap align="right">
                       <input type="hidden" name="disp_weight_<%=j%>" value='0' >
                       <input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_weight1_<%=j%>"  name="disp_weight1_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_weight1")%>'   maxlength='<%=list_detail.getColumn("disp_weight1").getPrecision()%>'  ></td><!--分切宽度-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_weight2_<%=j%>"  name="disp_weight2_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_weight2")%>'   maxlength='<%=list_detail.getColumn("disp_weight2").getPrecision()%>'  ></td><!--分切宽度-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_weight3_<%=j%>"  name="disp_weight3_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_weight3")%>'   maxlength='<%=list_detail.getColumn("disp_weight3").getPrecision()%>'  ></td><!--分切宽度-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_weight4_<%=j%>"  name="disp_weight4_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_weight4")%>'   maxlength='<%=list_detail.getColumn("disp_weight4").getPrecision()%>'  ></td><!--分切宽度-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_weight5_<%=j%>"  name="disp_weight5_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_weight5")%>'   maxlength='<%=list_detail.getColumn("disp_weight5").getPrecision()%>'  ></td><!--分切宽度-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_weight6_<%=j%>"  name="disp_weight6_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_weight6")%>'   maxlength='<%=list_detail.getColumn("disp_weight6").getPrecision()%>'  ></td><!--分切宽度-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_weight7_<%=j%>"  name="disp_weight7_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_weight7")%>'   maxlength='<%=list_detail.getColumn("disp_weight7").getPrecision()%>'  ></td><!--分切宽度-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_weight8_<%=j%>"  name="disp_weight8_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_weight8")%>'   maxlength='<%=list_detail.getColumn("disp_weight8").getPrecision()%>'  ></td><!--分切宽度-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="disp_weight9_<%=j%>"  name="disp_weight9_<%=j%>"     value='<%=detail_detail_RowMap.get("disp_weight9")%>'   maxlength='<%=list_detail.getColumn("disp_weight9").getPrecision()%>'  ></td><!--分切宽度-->
                       </tr>
                      <%list_detail.next(); }%>
                       <tr>
                 <td nowrap width=10></td>
                   <td class="td">&nbsp;</td>
                   <td class="td"></td>
                   <td class="td"></td>
                   <td class="td"></td>
                   <td class="td"></td>
                   <td class="td"></td>
                   <td class="td"></td>
                   <td class="td"></td>
                   <td class="td"></td>

                    </tr>
                     <tr class="tableTitle">
                       <td nowrap width=10></td>
                        <td height='20' nowrap>客户代码1</td>
                        <td height='20' nowrap>客户代码2</td>
                        <td height='20' nowrap>客户代码3</td>
                        <td height='20' nowrap>客户代码4</td>
                        <td height='20' nowrap>客户代码5</td>
                        <td height='20' nowrap>客户代码6</td>
                        <td height='20' nowrap>客户代码7</td>
                        <td height='20' nowrap>客户代码8</td>
                        <td height='20' nowrap>客户代码9</td>
                     </tr>
                       <%
                   detail_detail_RowMap=null;
                        list_detail.first();
                    for(int j=0; j<detail_de_Rows.length; j++)   {
                      detail_detail_RowMap=detail_de_Rows[j];
                       %>
                        <tr>
                        <td nowrap width=10></td>
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="cust_code1_<%=j%>"  name="cust_code1_<%=j%>"     value='<%=detail_detail_RowMap.get("cust_code1")%>'   maxlength='<%=list_detail.getColumn("cust_code1").getPrecision()%>'  ></td><!--客户代码-->
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="cust_code2_<%=j%>"  name="cust_code2_<%=j%>"     value='<%=detail_detail_RowMap.get("cust_code2")%>'   maxlength='<%=list_detail.getColumn("cust_code2").getPrecision()%>'  ></td><!--客户代码-->
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="cust_code3_<%=j%>"  name="cust_code3_<%=j%>"     value='<%=detail_detail_RowMap.get("cust_code3")%>'   maxlength='<%=list_detail.getColumn("cust_code3").getPrecision()%>'  ></td><!--客户代码-->
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="cust_code4_<%=j%>"  name="cust_code4_<%=j%>"     value='<%=detail_detail_RowMap.get("cust_code4")%>'   maxlength='<%=list_detail.getColumn("cust_code4").getPrecision()%>'  ></td><!--客户代码-->
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="cust_code5_<%=j%>"  name="cust_code5_<%=j%>"     value='<%=detail_detail_RowMap.get("cust_code5")%>'   maxlength='<%=list_detail.getColumn("cust_code5").getPrecision()%>'  ></td><!--客户代码-->
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="cust_code6_<%=j%>"  name="cust_code6_<%=j%>"     value='<%=detail_detail_RowMap.get("cust_code6")%>'   maxlength='<%=list_detail.getColumn("cust_code6").getPrecision()%>'  ></td><!--客户代码-->
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="cust_code7_<%=j%>"  name="cust_code7_<%=j%>"     value='<%=detail_detail_RowMap.get("cust_code7")%>'   maxlength='<%=list_detail.getColumn("cust_code7").getPrecision()%>'  ></td><!--客户代码-->
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="cust_code8_<%=j%>"  name="cust_code8_<%=j%>"     value='<%=detail_detail_RowMap.get("cust_code8")%>'   maxlength='<%=list_detail.getColumn("cust_code8").getPrecision()%>'  ></td><!--客户代码-->
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="cust_code9_<%=j%>"  name="cust_code9_<%=j%>"     value='<%=detail_detail_RowMap.get("cust_code9")%>'   maxlength='<%=list_detail.getColumn("cust_code9").getPrecision()%>'  ></td><!--客户代码-->
                     </tr>
                       <%list_detail.next(); }%>
                           <tr>
                      <td nowrap width=10></td>
                      <td class="td">&nbsp;</td>
                      <td class="td"></td>
                      <td class="td"></td>
                      <td class="td"></td>
                      <td class="td"></td>
                      <td class="td"></td>
                      <td class="td"></td>
                      <td class="td"></td>
                      <td class="td"></td>

                    </tr>
                        <tr class="tableTitle">
                       <td nowrap width=10></td>
                       <td height='20' nowrap>货物批号1</td>
                       <td height='20' nowrap>货物批号2</td>
                       <td height='20' nowrap>货物批号3</td>
                       <td height='20' nowrap>货物批号4</td>
                       <td height='20' nowrap>货物批号5</td>
                       <td height='20' nowrap>货物批号6</td>
                       <td height='20' nowrap>货物批号7</td>
                       <td height='20' nowrap>货物批号8</td>
                       <td height='20' nowrap>货物批号9</td>
                      </tr>
                        <%
                   detail_detail_RowMap=null;
                        list_detail.first();
                    for(int j=0; j<detail_de_Rows.length; j++)   {
                      detail_detail_RowMap=detail_de_Rows[j];
                       %>
                        <tr>
                       <td nowrap width=10></td>
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="batch_no1_<%=j%>"  name="batch_no1_<%=j%>"     value='<%=detail_detail_RowMap.get("batch_no1")%>'   maxlength='<%=list_detail.getColumn("batch_no1").getPrecision()%>'  ></td><!--客户代码-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="batch_no2_<%=j%>"  name="batch_no2_<%=j%>"     value='<%=detail_detail_RowMap.get("batch_no2")%>'   maxlength='<%=list_detail.getColumn("batch_no2").getPrecision()%>'  ></td><!--客户代码-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="batch_no3_<%=j%>"  name="batch_no3_<%=j%>"     value='<%=detail_detail_RowMap.get("batch_no3")%>'   maxlength='<%=list_detail.getColumn("batch_no3").getPrecision()%>'  ></td><!--客户代码-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="batch_no4_<%=j%>"  name="batch_no4_<%=j%>"     value='<%=detail_detail_RowMap.get("batch_no4")%>'   maxlength='<%=list_detail.getColumn("batch_no4").getPrecision()%>'  ></td><!--客户代码-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="batch_no5_<%=j%>"  name="batch_no5_<%=j%>"     value='<%=detail_detail_RowMap.get("batch_no5")%>'   maxlength='<%=list_detail.getColumn("batch_no5").getPrecision()%>'  ></td><!--客户代码-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="batch_no6_<%=j%>"  name="batch_no6_<%=j%>"     value='<%=detail_detail_RowMap.get("batch_no6")%>'   maxlength='<%=list_detail.getColumn("batch_no6").getPrecision()%>'  ></td><!--客户代码-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="batch_no7_<%=j%>"  name="batch_no7_<%=j%>"     value='<%=detail_detail_RowMap.get("batch_no7")%>'   maxlength='<%=list_detail.getColumn("batch_no7").getPrecision()%>'  ></td><!--客户代码-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="batch_no8_<%=j%>"  name="batch_no8_<%=j%>"     value='<%=detail_detail_RowMap.get("batch_no8")%>'   maxlength='<%=list_detail.getColumn("batch_no8").getPrecision()%>'  ></td><!--客户代码-->
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="batch_no9_<%=j%>"  name="batch_no9_<%=j%>"     value='<%=detail_detail_RowMap.get("batch_no9")%>'   maxlength='<%=list_detail.getColumn("batch_no9").getPrecision()%>'  ></td><!--客户代码-->
                       </tr>
                      <%list_detail.next(); }%>

                   <tr>
                     <td nowrap width=10></td>
                     <td class="td">&nbsp;</td>
                     <td class="td"></td>
                     <td class="td"></td>
                     <td class="td"></td>
                     <td class="td"></td>
                     <td class="td"></td>
                     <td class="td"></td>
                     <td class="td"></td>
                     <td class="td"></td>

                    </tr>
                      </table>
                    <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tbcontrol',1);</SCRIPT>

             <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">

          <tr>
          <td colspan="3" noWrap class="tableTitle">
          <%if(isCanEdit){%>
          <input name="button2" type="button" class="button" onClick="saveList();" value="确定(s)">
          <pc:shortcut key="s" script="saveList();" />
          <%}%>
          <input name="btnback" type="button" class="button" onClick="backList();" value="返回(c)">
          <pc:shortcut key="c" script="backList();" />

          </td>
</tr>
 </table>
 </td>
 </tr>
 </table>
 </form>
 <script language="javascript">

  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}

  function OrderSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
  {
   var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
   var winName= "SingleladingSelector";
   paraStr = "../produce/SC_Trackbill_import_lading.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+curID;
   if(methodName+'' != 'undefined')
   paraStr += "&method="+methodName;
   if(notin+'' != 'undefined')
   paraStr += "&notin="+notin;
   newWin =window.open(paraStr,winName,winopt);
   newWin.focus();
  }
  function selctbilloflading()
  {
    form1.selectedtdid.value='';
    OrderSingleSelect('form1','srcVar=selectedtdid','fieldVar=drawDetailID','',"sumitForm(<%=b_TrackbillBean.DETAIL_SALE_ADD%>,-1)");
   }
function countdispweight(j){
   var disp_width1=document.all['disp_width1'];
   if(disp_width1.value=="")
   disp_width1.value="0";
   var disp_width2=document.all['disp_width2'];
   if(disp_width2.value=="")
   disp_width2.value="0";
   var disp_width3=document.all['disp_width3'];
   if(disp_width3.value=="")
   disp_width3.value="0";
   var disp_width4=document.all['disp_width4'];
   if(disp_width4.value=="")
   disp_width4.value="0";
   var disp_width5=document.all['disp_width5'];
   if(disp_width5.value=="")
   disp_width5.value="0";
   var disp_width6=document.all['disp_width6'];
   if(disp_width6.value=="")
    disp_width6.value="0";
   var disp_width7=document.all['disp_width7'];
   if(disp_width7.value=="")
   disp_width7.value="0";
   var disp_width8=document.all['disp_width8'];
   if(disp_width8.value=="")
   disp_width8.value="0";
   var disp_width9=document.all['disp_width9'];
   if(disp_width9.value=="")
   disp_width9.value="0";
   var disp_lenth1=document.all['disp_lenth1_'+j];
   if(disp_lenth1.value=="")
   disp_lenth1.value="0";
   var disp_lenth2=document.all['disp_lenth2_'+j];
   if(disp_lenth2.value=="")
   disp_lenth2.value="0";
   var disp_lenth3=document.all['disp_lenth3_'+j];
   if(disp_lenth3.value=="")
   disp_lenth3.value="0";
   var disp_lenth4=document.all['disp_lenth4_'+j];
   if(disp_lenth4.value=="")
   disp_lenth4.value="0";
   var disp_lenth5=document.all['disp_lenth5_'+j];
   if(disp_lenth5.value=="")
   disp_lenth5.value="0";
   var disp_lenth6=document.all['disp_lenth6_'+j];
   if(disp_lenth6.value=="")
   disp_lenth6.value="0";
   var disp_lenth7=document.all['disp_lenth7_'+j];
   if(disp_lenth7.value=="")
   disp_lenth7.value="0";
   var disp_lenth8=document.all['disp_lenth8_'+j];
   if(disp_lenth8.value=="")
   disp_lenth8.value="0";
   var disp_lenth9=document.all['disp_lenth9_'+j];
   if(disp_lenth9.value=="")
   disp_lenth9.value="0";
   var scdwgs=document.all['scdwgs'];
   if(scdwgs.value=="")
   scdwgs.value="0";
   var disp_weight1=document.all['disp_weight1_'+j];
   var disp_weight2=document.all['disp_weight2_'+j];
   var disp_weight3=document.all['disp_weight3_'+j];
   var disp_weight4=document.all['disp_weight4_'+j];
   var disp_weight5=document.all['disp_weight5_'+j];
   var disp_weight6=document.all['disp_weight6_'+j];
   var disp_weight7=document.all['disp_weight7_'+j];
   var disp_weight8=document.all['disp_weight8_'+j];
   var disp_weight9=document.all['disp_weight9_'+j];
   var disp_weight=document.all['disp_weight_'+j];
   if(disp_weight.value=="")
   disp_weight.value="0";
   disp_weight1.value=parseFloat(disp_width1.value)*parseFloat(disp_lenth1.value)/parseFloat(scdwgs.value);
   disp_weight2.value=parseFloat(disp_width2.value)*parseFloat(disp_lenth2.value)/parseFloat(scdwgs.value);
   disp_weight3.value=parseFloat(disp_width3.value)*parseFloat(disp_lenth3.value)/parseFloat(scdwgs.value);
   disp_weight4.value=parseFloat(disp_width4.value)*parseFloat(disp_lenth4.value)/parseFloat(scdwgs.value);
   disp_weight5.value=parseFloat(disp_width5.value)*parseFloat(disp_lenth5.value)/parseFloat(scdwgs.value);
   disp_weight6.value=parseFloat(disp_width6.value)*parseFloat(disp_lenth6.value)/parseFloat(scdwgs.value);
   disp_weight7.value=parseFloat(disp_width7.value)*parseFloat(disp_lenth7.value)/parseFloat(scdwgs.value);
   disp_weight8.value=parseFloat(disp_width8.value)*parseFloat(disp_lenth8.value)/parseFloat(scdwgs.value);
   disp_weight9.value=parseFloat(disp_width9.value)*parseFloat(disp_lenth9.value)/parseFloat(scdwgs.value);
   disp_weight.value=parseFloat(disp_weight1.value)+parseFloat(disp_weight2.value)+parseFloat(disp_weight3.value)+parseFloat(disp_weight4.value)+parseFloat(disp_weight5.value)+parseFloat(disp_weight6.value)+parseFloat(disp_weight7.value)+parseFloat(disp_weight8.value)+parseFloat(disp_weight9.value);
  }

</script>

<%out.print(retu);%>
</BODY>
</Html>