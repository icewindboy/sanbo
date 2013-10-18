<%--客户统一折扣--%><%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.html.*,java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "custmer_unitive_discount";
%>
<%
  if(!loginBean.hasLimits("custmer_unitive_discount", request, response))
    return;
  engine.erp.sale.xixing.B_CustomerUnitiveDiscount b_CustomerUnitiveDiscountBean  =  engine.erp.sale.xixing.B_CustomerUnitiveDiscount.getInstance(request);
  String retu = b_CustomerUnitiveDiscountBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
  out.print(retu);
%>
<html>
<head>
<title></title>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript" src="../scripts/frame.js"></script>
<SCRIPT LANGUAGE="javascript">
function toDetail(oper, row){
  // 转到主从明细
  lockScreenToWait("处理中, 请稍候！");
  location.href='custmer_unitive_discount_edit.jsp?operate='+oper+'&rownum='+row;
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
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
function showInterFrame(oper, rownum){
  var url = "custmer_unitive_discount_edit.jsp?operate="+oper+"&rownum="+rownum;
  document.all.interframe1.src = url;
  showFrame('detailDiv',true,"",true);
}
function hideInterFrame()//隐藏FRAME
{
  hideFrame('detailDiv');
  form1.submit();
}
function hideFrameNoFresh()
{
  hideFrame('detailDiv');
}
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>客户统一折扣</TD>
  </TR>
</TABLE>
<%
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//引用部门
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);//引用往来单位
  EngineDataSet list = b_CustomerUnitiveDiscountBean.getOneTable();
  //HtmlTableProducer table = b_CustomerUnitiveDiscountBean.masterProducer;//主表数据的表格打印
  String curUrl = request.getRequestURL().toString();

  boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);

  ArrayList CPopkey = new ArrayList(); CPopkey.add("A");CPopkey.add("B"); CPopkey.add("C");
  ArrayList CPopval = new ArrayList(); CPopval.add("A");CPopval.add("B"); CPopval.add("C");
  ArrayList[] CPlists  = new ArrayList[]{CPopkey, CPopval};

  ArrayList opkey = new ArrayList(); opkey.add("lsj");opkey.add("ccj"); opkey.add("msj");  opkey.add("qtjg1"); opkey.add("qtjg2");opkey.add("qtjg3");
  ArrayList opval = new ArrayList(); opval.add("零售价");opval.add("出厂价"); opval.add("门市价");  opval.add("其他价格1"); opval.add("其他价格2");opval.add("其他价格3");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE width="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
       <td class="td" nowrap>
        <%
          String key = "cpfhdjtable";
          pageContext.setAttribute(key, list);
          int iPage = loginBean.getPageSize();
          String pageSize = String.valueOf(iPage);
        %>
        <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
        </td>
        <td class="td" nowrap align="right">
        <%if(b_CustomerUnitiveDiscountBean.retuUrl!=null){
          String s = "location.href='"+b_CustomerUnitiveDiscountBean.retuUrl+"'";
           %>
         <input name="button22" type="button" class="button"  style="width:60"  onClick="location.href='<%=b_CustomerUnitiveDiscountBean.retuUrl%>'" value=" 返回(C) " onKeyDown="return getNextElement();">
         <pc:shortcut key="c" script="<%=s%>" />
        <%}%>
        </td>
   </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td>
      <% String add = "showInterFrame("+Operate.ADD+",-1)";%>
      <input name="image" class="img" type="image" title="新增(A)" onClick="toDetail(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <pc:shortcut key="a" script="<%=add%>" />
      </td>
      <td nowrap>信誉等级</td>
      <td nowrap>产品类别</td>
      <td nowrap>定价类型</td>
      <td height='24' nowrap>折扣(%)</td>
    </tr>
    <%
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++)
      {
        boolean isInit = false;
        String loginid=b_CustomerUnitiveDiscountBean.loginid;
        boolean cansubmit=false;
        boolean submitType = b_CustomerUnitiveDiscountBean.submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
    %>
    <tr onDblClick="toDetail(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="selectRow()" >
      <td class="td"  align="center" nowrap>
      <%if(isCanEdit){%><input name="image2" class="img" type="image" title="修改" onClick="toDetail(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0"><%}%>
      <%if(isCanDelete){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0"><%}%>
      </td>
      <td class="td"  nowrap><%=list.getValue("xydj")%></td>
      <td class="td"   nowrap><%=list.getValue("cplx")%></td>
      <td class="td"   height='24' nowrap>
      <%
       int index =  opkey.indexOf(list.getValue("djlx"));
       out.print(index==-1?"":opval.get(index));
      %>
      </td>
      <td class="td"   nowrap><%=list.getValue("zk")%></td>
    </tr>
    <%
      list.next();
      }
      for(; i < iPage; i++){
        out.print("<tr><td>&nbsp;</td>");
        out.print("<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>");
        out.print("</tr>");
      }
      %>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form>
<%--form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
   <div class="queryPop" id="fixedQuery" name="fixedQuery">
   <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
         <TABLE cellspacing=3 cellpadding=0 border=0>
        <TR>
         <%b_CustomerUnitiveDiscountBean.table.printWhereInfo(pageContext);%>
            <TR>
              <TD colspan="4" nowrap class="td" align="center">
                <% String qu = "sumitFixedQuery("+b_CustomerUnitiveDiscountBean.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=b_CustomerUnitiveDiscountBean.FIXED_SEARCH%>)" type="button" value="查询(K)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="k" script='<%=qu%>'/>
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(x)" name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script="hideFrame('fixedQuery')"/>
            </td>
            </tr>
            </table>
              </TD>
            </TR>
    </TABLE>
  </DIV>
</form--%>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="500" height="225" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(retu);%>
</body>
</html>