<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,java.text.*,java.util.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.sale.xixing.B_SalePlan b_SalePlanBean = engine.erp.sale.xixing.B_SalePlan.getInstance(request);
  String pageCode = "sale_plan";
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
  location.href='sale_plan.jsp';
}
function add()
{
  if(form1.mdeptid.value=='')
  {
    alert('请选择部门!');
    return;
  }
 sumitForm(<%=Operate.DETAIL_ADD%>);
}
</script>

<%
  String retu = b_SalePlanBean.doService(request, response);
  if(retu.indexOf("backList();")>-1 || retu.indexOf("toFee")>-1 || retu.indexOf("toDock")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//引用部门
  engine.project.LookUp mdeptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//引用部门
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_SalePlanBean.getMaterTable();
  EngineDataSet list = b_SalePlanBean.getDetailTable();
  HtmlTableProducer masterProducer = b_SalePlanBean.masterProducer;
  RowMap masterRow = b_SalePlanBean.getMasterRowinfo();
  RowMap[] detailRows= b_SalePlanBean.getDetailRowinfos();
  String zt=masterRow.get("zt");
  boolean isEnd =  b_SalePlanBean.isReport||b_SalePlanBean.isApprove || (!b_SalePlanBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !b_SalePlanBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(b_SalePlanBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=edline" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  String title = zt.equals("0") ? ("未审批") : (zt.equals("9") ? "审批中" :(zt.equals("4")?"已作废":(zt.equals("8")?"完成":"已审")) );
  RowMap SumRow = b_SalePlanBean.SumRow;


%>
<BODY oncontextmenu="window.event.returnValue=true" onLoad="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST"  onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <input type="HIDDEN" name="wzdjid" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">销售计划</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
                <tr>
                  <td noWrap class="tdTitle">计划编号</td>
                  <td noWrap class="td"><input type="text" name="jhbh" value='<%=masterRow.get("jhbh")%>' maxlength='<%=ds.getColumn("jhbh").getPrecision()%>' style="width:110"  class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle">计划年度</td>
                  <td noWrap class="td"><input type="text" name="jhnd" value='<%=masterRow.get("jhnd")%>' maxlength='10' style="width:85" <%=edClass%>  onKeyDown="return getNextElement();"<%=readonly%>>
                  </td>
                 <td noWrap class="tdTitle">制单部门</td>
                 <td noWrap class="td">
                 <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:80' class='edline' readonly >");
                    else {%>
                    <pc:select name="deptid" addNull="1" style="width:110"  >
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                 </td>
                </tr>
                <%/*打印用户自定义信息*/
                int j=0;
                while(j < mBakFields.length){
                  out.print("<tr>");
                  for(int k=0; k<4; k++)
                  {
                    out.print("<td noWrap class='tdTitle'>");
                    out.print(j < mBakFields.length ? mBakFields[j].getFieldname() : "&nbsp;");
                    out.print("</td><td noWrap class='td'");
                    if(j < mBakFields.length)
                    {
                      boolean isMemo = mBakFields[j].getType() == FieldInfo.MEMO_TYPE;
                      out.print(isMemo ? " colspan=7>" : ">");
                      String filedcode = mBakFields[j].getFieldcode();
                      String style = (isMemo ? "style='width:690'" : "style='width:110'")+ " onKeyDown='return getNextElement();'";
                      out.print(masterProducer.getFieldInput(mBakFields[j], masterRow.get(filedcode), filedcode, style, isEnd, true));
                      out.print("</td>");
                      if(isMemo)
                        break;
                    }
                    else
                      out.print(">&nbsp;</td>");
                    j++;
                  }
                  out.println("</tr>");
                }
                int count = detailRows.length;
                %>
                <tr>
                  <td colspan="8" noWrap class="td">
                   <div style="display:block;width:900;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td width="61" rowspan="2" nowrap>
                        <%if(zt.equals("0")){%>
                          部门
                          <pc:select name="mdeptid" addNull="1" style="width:110"  >
                          <%=mdeptBean.getList()%>
                          </pc:select>
                        <input name="image" class="img" type="image" title="新增" onClick="add()" src="../images/add_big.gif" border="0">
                        <%}%>
                        </td>
                        <%
                          for(int i=0;i<count;i++){
                            RowMap detailrow =  detailRows[i];
                        %>
                          <td colspan="4" nowrap><%=deptBean.getLookupName(detailrow.get("deptid"))%>
                          <input type="HIDDEN"  id="deptid_<%=i%>"    name="deptid_<%=i%>"    value='<%=detailrow.get("deptid")%>'   >
                          <%if(zt.equals("0")){%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该部门？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/del.gif" border="0">
                           <%}%>
                          </td>
                        <%}%>
                        <%
                         //页面计算合计


                        %>
                        <td colspan="4" nowrap>月合计</td>
                     </tr>
                     <tr class="tableTitle">
                        <td nowrap>计划销售(元)</td>
                        <td nowrap>考核销售(元)</td>
                        <td nowrap>实际销售(元)</td>
                        <td nowrap>完成比率(%)</td>
                        <%
                          for(int i=0;i<count;i++){
                            RowMap detailrow =  detailRows[i];
                        %>
                        <td nowrap>计划销售(元)</td>
                        <td nowrap>考核销售(元)</td>
                        <td nowrap>实际销售(元)</td>
                        <td nowrap>完成比率(%)</td>
                        <%}%>
                      </tr>
                     <tr class="tableTitle">
                        <td nowrap>一月</td>
                        <%
                          for(int i=0;i<count;i++){
                            RowMap detailrow =  detailRows[i];
                        %>
                        <td nowrap><input type="text" name="jhxs1_<%=i%>" value='<%=detailrow.get("jhxs1")%>' maxlength='<%=list.getColumn("jhxs1").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="khxs1_<%=i%>" value='<%=detailrow.get("khxs1")%>' maxlength='<%=list.getColumn("khxs1").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="shijixs1_<%=i%>" value='<%=detailrow.get("shijixs1")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text" name="wcbL1__<%=i%>" value='<%=detailrow.get("wcbL1")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <%}%>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zxsjh1")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zkhxs1")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zshijixs1")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zwcbL1")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                      </tr>
                      <tr class="tableTitle">
                        <td nowrap>二月</td>
                        <%
                          for(int i=0;i<count;i++){
                            RowMap detailrow =  detailRows[i];
                        %>
                        <td nowrap><input type="text" name="jhxs2_<%=i%>" value='<%=detailrow.get("jhxs2")%>' maxlength='<%=list.getColumn("jhxs2").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="khxs2_<%=i%>" value='<%=detailrow.get("khxs2")%>' maxlength='<%=list.getColumn("khxs2").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="shijixs2_<%=i%>" value='<%=detailrow.get("shijixs2")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text" name="wcbL2__<%=i%>" value='<%=detailrow.get("wcbL2")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <%}%>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zxsjh2")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zkhxs2")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zshijixs2")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zwcbL2")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                      </tr>
                      <tr class="tableTitle">
                        <td nowrap>三月</td>
                        <%
                          for(int i=0;i<count;i++){
                            RowMap detailrow =  detailRows[i];
                        %>

                        <td nowrap><input type="text" name="jhxs3_<%=i%>" value='<%=detailrow.get("jhxs3")%>' maxlength='<%=list.getColumn("jhxs3").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="khxs3_<%=i%>" value='<%=detailrow.get("khxs3")%>' maxlength='<%=list.getColumn("khxs3").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="shijixs3_<%=i%>" value='<%=detailrow.get("shijixs3")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text" name="wcbL3__<%=i%>" value='<%=detailrow.get("wcbL3")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <%}%>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zxsjh3")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zkhxs3")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zshijixs3")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zwcbL3")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                      </tr>
                      <tr class="tableTitle">
                        <td nowrap>四月</td>
                        <%
                          for(int i=0;i<count;i++){
                            RowMap detailrow =  detailRows[i];
                        %>
                        <td nowrap><input type="text" name="jhxs4_<%=i%>" value='<%=detailrow.get("jhxs4")%>' maxlength='<%=list.getColumn("jhxs4").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="khxs4_<%=i%>" value='<%=detailrow.get("khxs4")%>' maxlength='<%=list.getColumn("khxs4").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="shijixs4_<%=i%>" value='<%=detailrow.get("shijixs4")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text" name="wcbL4__<%=i%>" value='<%=detailrow.get("wcbL4")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <%}%>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zxsjh4")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zkhxs4")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                       <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zshijixs4")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zwcbL4")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                      </tr>
                      <tr class="tableTitle">
                        <td nowrap>五月</td>
                        <%
                          for(int i=0;i<count;i++){
                            RowMap detailrow =  detailRows[i];
                        %>

                        <td nowrap><input type="text" name="jhxs5_<%=i%>" value='<%=detailrow.get("jhxs5")%>' maxlength='<%=list.getColumn("jhxs5").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="khxs5_<%=i%>" value='<%=detailrow.get("khxs5")%>' maxlength='<%=list.getColumn("khxs5").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="shijixs5_<%=i%>" value='<%=detailrow.get("shijixs5")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text" name="wcbL5__<%=i%>" value='<%=detailrow.get("wcbL5")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <%}%>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zxsjh5")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zkhxs5")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zshijixs5")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zwcbL5")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                      </tr>
                      <tr class="tableTitle">
                        <td nowrap>六月</td>
                        <%
                          for(int i=0;i<count;i++){
                            RowMap detailrow =  detailRows[i];
                        %>
                        <td nowrap><input type="text" name="jhxs6_<%=i%>" value='<%=detailrow.get("jhxs6")%>' maxlength='<%=list.getColumn("jhxs6").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="khxs6_<%=i%>" value='<%=detailrow.get("khxs6")%>' maxlength='<%=list.getColumn("khxs6").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="shijixs6_<%=i%>" value='<%=detailrow.get("shijixs6")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text" name="wcbL6__<%=i%>" value='<%=detailrow.get("wcbL6")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <%}%>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zxsjh6")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zkhxs6")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zshijixs6")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zwcbL6")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                      </tr>
                      <tr class="tableTitle">
                        <td nowrap>七月</td>
                        <%
                          for(int i=0;i<count;i++){
                            RowMap detailrow =  detailRows[i];
                        %>
                        <td nowrap><input type="text" name="jhxs7_<%=i%>" value='<%=detailrow.get("jhxs7")%>' maxlength='<%=list.getColumn("jhxs7").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="khxs7_<%=i%>" value='<%=detailrow.get("khxs7")%>' maxlength='<%=list.getColumn("khxs7").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="shijixs7_<%=i%>" value='<%=detailrow.get("shijixs7")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text" name="wcbL7__<%=i%>" value='<%=detailrow.get("wcbL7")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <%}%>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zxsjh7")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zkhxs7")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zshijixs7")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zwcbL7")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                      </tr>
                      <tr class="tableTitle">
                        <td nowrap>八月</td>
                        <%
                          for(int i=0;i<count;i++){
                            RowMap detailrow =  detailRows[i];
                        %>
                        <td nowrap><input type="text" name="jhxs8_<%=i%>" value='<%=detailrow.get("jhxs8")%>' maxlength='<%=list.getColumn("jhxs8").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="khxs8_<%=i%>" value='<%=detailrow.get("khxs8")%>' maxlength='<%=list.getColumn("khxs8").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="shijixs8_<%=i%>" value='<%=detailrow.get("shijixs8")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text" name="wcbL8__<%=i%>" value='<%=detailrow.get("wcbL8")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <%}%>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zxsjh8")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zkhxs8")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zshijixs8")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zwcbL8")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                      </tr>
                      <tr class="tableTitle">
                        <td nowrap>九月</td>
                        <%
                          for(int i=0;i<count;i++){
                            RowMap detailrow =  detailRows[i];
                        %>
                        <td nowrap><input type="text" name="jhxs9_<%=i%>" value='<%=detailrow.get("jhxs9")%>' maxlength='<%=list.getColumn("jhxs9").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="khxs9_<%=i%>" value='<%=detailrow.get("khxs9")%>' maxlength='<%=list.getColumn("khxs9").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="shijixs9_<%=i%>" value='<%=detailrow.get("shijixs9")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text" name="wcbL9__<%=i%>" value='<%=detailrow.get("wcbL9")%>'  style="width:90"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <%}%>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zxsjh9")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zkhxs9")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zshijixs9")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zwcbL9")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                      </tr>
                      <tr class="tableTitle">
                        <td nowrap>十月</td>
                        <%
                          for(int i=0;i<count;i++){
                            RowMap detailrow =  detailRows[i];
                        %>
                        <td nowrap><input type="text" name="jhxs10_<%=i%>" value='<%=detailrow.get("jhxs10")%>' maxlength='<%=list.getColumn("jhxs10").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="khxs10_<%=i%>" value='<%=detailrow.get("khxs10")%>' maxlength='<%=list.getColumn("khxs10").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="shijixs10_<%=i%>" value='<%=detailrow.get("shijixs10")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text" name="wcbL10__<%=i%>" value='<%=detailrow.get("wcbL10")%>'  style="width:100"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <%}%>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zxsjh10")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zkhxs10")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zshijixs10")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zwcbL10")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                      </tr>
                      <tr class="tableTitle">
                        <td nowrap>十一月</td>
                        <%
                          for(int i=0;i<count;i++){
                            RowMap detailrow =  detailRows[i];
                        %>
                        <td nowrap><input type="text" name="jhxs11_<%=i%>" value='<%=detailrow.get("jhxs11")%>' maxlength='<%=list.getColumn("jhxs11").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="khxs11_<%=i%>" value='<%=detailrow.get("khxs11")%>' maxlength='<%=list.getColumn("khxs11").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="shijixs11_<%=i%>" value='<%=detailrow.get("shijixs11")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text" name="wcbL11__<%=i%>" value='<%=detailrow.get("wcbL11")%>'  style="width:110"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <%}%>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zxsjh11")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zkhxs11")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zshijixs11")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=engine.util.Format.formatNumber(SumRow.get("zwcbL11"),"#0.00")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                      </tr>
                      <tr class="tableTitle">
                        <td nowrap>十二月</td>
                        <%
                          for(int i=0;i<count;i++){
                            RowMap detailrow =  detailRows[i];
                        %>
                        <td nowrap><input type="text" name="jhxs12_<%=i%>" value='<%=detailrow.get("jhxs12")%>' maxlength='<%=list.getColumn("jhxs12").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="khxs12_<%=i%>" value='<%=detailrow.get("khxs12")%>' maxlength='<%=list.getColumn("khxs12").getPrecision()%>' style="width:80"  <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                        <td nowrap><input type="text" name="shijixs12_<%=i%>" value='<%=detailrow.get("shijixs12")%>'  style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text" name="wcbL12__<%=i%>" value='<%=detailrow.get("wcbL12")%>'  style="width:120"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <%}%>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zxsjh12")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zkhxs12")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=SumRow.get("zshijixs12")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=engine.util.Format.formatNumber(SumRow.get("zwcbL12"),"#0.00")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                      </tr>
                      <tr class="tableTitle">
                        <td nowrap>年度合计</td>
                        <%

                          for(int i=0;i<count;i++){
                            RowMap detailrow =  detailRows[i];
                            String deptid = detailrow.get("deptid");
                            double zjhxs = Double.parseDouble(detailrow.get("jhxs1").equals("")?"0":detailrow.get("jhxs1"))+Double.parseDouble(detailrow.get("jhxs2").equals("")?"0":detailrow.get("jhxs2"))+Double.parseDouble(detailrow.get("jhxs3").equals("")?"0":detailrow.get("jhxs3"))+Double.parseDouble(detailrow.get("jhxs4").equals("")?"0":detailrow.get("jhxs4"))+Double.parseDouble(detailrow.get("jhxs5").equals("")?"0":detailrow.get("jhxs5"))+Double.parseDouble(detailrow.get("jhxs6").equals("")?"0":detailrow.get("jhxs6"))+Double.parseDouble(detailrow.get("jhxs7").equals("")?"0":detailrow.get("jhxs7"))+Double.parseDouble(detailrow.get("jhxs8").equals("")?"0":detailrow.get("jhxs8"))+Double.parseDouble(detailrow.get("jhxs9").equals("")?"0":detailrow.get("jhxs9"))+Double.parseDouble(detailrow.get("jhxs10").equals("")?"0":detailrow.get("jhxs10"))+Double.parseDouble(detailrow.get("jhxs11").equals("")?"0":detailrow.get("jhxs11"))+Double.parseDouble(detailrow.get("jhxs12").equals("")?"0":detailrow.get("jhxs12"));
                            double zkhxs = Double.parseDouble(detailrow.get("khxs1").equals("")?"0":detailrow.get("khxs1"))+Double.parseDouble(detailrow.get("khxs2").equals("")?"0":detailrow.get("khxs2"))+Double.parseDouble(detailrow.get("khxs3").equals("")?"0":detailrow.get("khxs3"))+Double.parseDouble(detailrow.get("khxs4").equals("")?"0":detailrow.get("jhxs4"))+Double.parseDouble(detailrow.get("khxs5").equals("")?"0":detailrow.get("khxs5"))+Double.parseDouble(detailrow.get("khxs6").equals("")?"0":detailrow.get("khxs6"))+Double.parseDouble(detailrow.get("khxs7").equals("")?"0":detailrow.get("khxs7"))+Double.parseDouble(detailrow.get("khxs8").equals("")?"0":detailrow.get("khxs8"))+Double.parseDouble(detailrow.get("khxs9").equals("")?"0":detailrow.get("khxs9"))+Double.parseDouble(detailrow.get("khxs10").equals("")?"0":detailrow.get("khxs10"))+Double.parseDouble(detailrow.get("khxs11").equals("")?"0":detailrow.get("khxs11"))+Double.parseDouble(detailrow.get("khxs12").equals("")?"0":detailrow.get("khxs12"));
                            double zshijixs = Double.parseDouble(detailrow.get("shijixs1").equals("")?"0":detailrow.get("shijixs1"))+Double.parseDouble(detailrow.get("shijixs2").equals("")?"0":detailrow.get("shijixs2"))+Double.parseDouble(detailrow.get("shijixs3").equals("")?"0":detailrow.get("shijixs3"))+Double.parseDouble(detailrow.get("shijixs4").equals("")?"0":detailrow.get("shijixs4"))+Double.parseDouble(detailrow.get("shijixs5").equals("")?"0":detailrow.get("shijixs5"))+Double.parseDouble(detailrow.get("shijixs6").equals("")?"0":detailrow.get("shijixs6"))+Double.parseDouble(detailrow.get("shijixs7").equals("")?"0":detailrow.get("shijixs7"))+Double.parseDouble(detailrow.get("shijixs8").equals("")?"0":detailrow.get("shijixs8"))+Double.parseDouble(detailrow.get("shijixs9").equals("")?"0":detailrow.get("shijixs9"))+Double.parseDouble(detailrow.get("shijixs10").equals("")?"0":detailrow.get("shijixs10"))+Double.parseDouble(detailrow.get("shijixs11").equals("")?"0":detailrow.get("shijixs11"))
                                             +Double.parseDouble(detailrow.get("shijixs12").equals("")?"0":detailrow.get("shijixs12"));
                            double zwcbl = zkhxs==0?0:zshijixs/zkhxs*100;


                        %>
                        <td nowrap><input type="text"  name=""  value="<%=engine.util.Format.formatNumber(zjhxs,"#0.00")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=engine.util.Format.formatNumber(zkhxs,"#0.00")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=engine.util.Format.formatNumber(zshijixs,"#0.00")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=engine.util.Format.formatNumber(zwcbl,"#0.00")%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <%}%>
                        <%
                         double zzxsjh = Double.parseDouble(SumRow.get("zxsjh1").equals("")?"0":SumRow.get("zxsjh1"))+Double.parseDouble(SumRow.get("zxsjh2").equals("")?"0":SumRow.get("zxsjh2"))+Double.parseDouble(SumRow.get("zxsjh3").equals("")?"0":SumRow.get("zxsjh3"))+Double.parseDouble(SumRow.get("zxsjh4").equals("")?"0":SumRow.get("zxsjh4"))+Double.parseDouble(SumRow.get("zxsjh5").equals("")?"0":SumRow.get("zxsjh5"))+Double.parseDouble(SumRow.get("zxsjh6").equals("")?"0":SumRow.get("zxsjh6"))+Double.parseDouble(SumRow.get("zxsjh7").equals("")?"0":SumRow.get("zxsjh7"))+Double.parseDouble(SumRow.get("zxsjh8").equals("")?"0":SumRow.get("zxsjh8"))+Double.parseDouble(SumRow.get("zxsjh9").equals("")?"0":SumRow.get("zxsjh9"))+Double.parseDouble(SumRow.get("zxsjh10").equals("")?"0":SumRow.get("zxsjh10"))+Double.parseDouble(SumRow.get("zxsjh11").equals("")?"0":SumRow.get("zxsjh11"))+Double.parseDouble(SumRow.get("zxsjh12").equals("")?"0":SumRow.get("zxsjh12"));
                         double zzkhxs = Double.parseDouble(SumRow.get("zkhxs1").equals("")?"0":SumRow.get("zkhxs1"))+Double.parseDouble(SumRow.get("zkhxs2").equals("")?"0":SumRow.get("zkhxs2"))+Double.parseDouble(SumRow.get("zkhxs3").equals("")?"0":SumRow.get("zkhxs3"))+Double.parseDouble(SumRow.get("zkhxs4").equals("")?"0":SumRow.get("zkhxs4"))+Double.parseDouble(SumRow.get("zkhxs5").equals("")?"0":SumRow.get("zkhxs5"))+Double.parseDouble(SumRow.get("zkhxs6").equals("")?"0":SumRow.get("zkhxs6"))+Double.parseDouble(SumRow.get("zkhxs7").equals("")?"0":SumRow.get("zkhxs7"))+Double.parseDouble(SumRow.get("zkhxs8").equals("")?"0":SumRow.get("zkhxs8"))+Double.parseDouble(SumRow.get("zkhxs9").equals("")?"0":SumRow.get("zkhxs9"))+Double.parseDouble(SumRow.get("zkhxs10").equals("")?"0":SumRow.get("zkhxs10"))+Double.parseDouble(SumRow.get("zkhxs11").equals("")?"0":SumRow.get("zkhxs11"))+Double.parseDouble(SumRow.get("zkhxs12").equals("")?"0":SumRow.get("zkhxs12"));
                         double zzshijixs = Double.parseDouble(SumRow.get("zshijixs1").equals("")?"0":SumRow.get("zshijixs1"))+Double.parseDouble(SumRow.get("zshijixs2").equals("")?"0":SumRow.get("zshijixs2"))+Double.parseDouble(SumRow.get("zshijixs3").equals("")?"0":SumRow.get("zshijixs3"))+Double.parseDouble(SumRow.get("zshijixs4").equals("")?"0":SumRow.get("zshijixs4"))+Double.parseDouble(SumRow.get("zshijixs5").equals("")?"0":SumRow.get("zshijixs5"))+Double.parseDouble(SumRow.get("zshijixs6").equals("")?"0":SumRow.get("zshijixs6"))+Double.parseDouble(SumRow.get("zshijixs7").equals("")?"0":SumRow.get("zshijixs7"))+Double.parseDouble(SumRow.get("zshijixs8").equals("")?"0":SumRow.get("zshijixs8"))+Double.parseDouble(SumRow.get("zshijixs9").equals("")?"0":SumRow.get("zshijixs9"))+Double.parseDouble(SumRow.get("zshijixs10").equals("")?"0":SumRow.get("zshijixs10"))+Double.parseDouble(SumRow.get("zshijixs11").equals("")?"0":SumRow.get("zshijixs11"))+Double.parseDouble(SumRow.get("zshijixs12").equals("")?"0":SumRow.get("zshijixs12"));
                         double zzwcbl = zzkhxs==0?0:zzshijixs/zzkhxs*100;
                        %>
                        <td nowrap><input type="text"  name=""  value="<%=zt.equals("1")?engine.util.Format.formatNumber(zzxsjh,"#0.00")+"":""%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=zt.equals("1")?engine.util.Format.formatNumber(zzkhxs,"#0.00")+"":""%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=zt.equals("1")?engine.util.Format.formatNumber(zzshijixs,"#0.00")+"":""%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                        <td nowrap><input type="text"  name=""  value="<%=zt.equals("1")?engine.util.Format.formatNumber(zzwcbl,"#0.00")+"":""%>" maxlength='' style="width:80"  class="ednone_r" onKeyDown="return getNextElement();" readonly></td>
                      </tr>
                    </table>
                   </div>
                    <SCRIPT LANGUAGE="javascript">rowinfo = new RowControl();
                   </SCRIPT></td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <table CELLSPACING=0 CELLPADDING=0 width="100%">
          <tr>
            <td class="td"><b>登记日期:</b><%=masterRow.get("czrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("czy")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
            <%if(!b_SalePlanBean.isApprove){%>
            <%if(!isEnd){%>
             <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存返回 ">
             <input name="button3"  style="width:50" type="button" class="button" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>);" value="删除(D)"><%}%>
              <%if(!b_SalePlanBean.isReport){%>
              <input name="btnback" type="button"  style="width:50" class="button" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script='<%="backList();"%>'/>
              <%}%>
              <%}%>
            </td>
          </tr>
        </table>
       </td>
    </tr>
  </table>
</form>
<script language="javascript">
  initDefaultTableRow('tableview1',1);
</script>
<%//&#$
if(b_SalePlanBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>