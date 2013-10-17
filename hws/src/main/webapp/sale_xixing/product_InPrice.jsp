<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.*,java.math.BigDecimal,java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "product_InPrice";
%>
<%
if(!loginBean.hasLimits("product_InPrice", request, response))
    return;
  engine.erp.sale.xixing.B_InProductPrice B_InProductPriceBeanbean = engine.erp.sale.xixing.B_InProductPrice.getInstance(request);//得到实例(初始化实例变量)
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);//engine.project.SysConstant.BEAN_CORP 外来单位JAVABEAN
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);//engine.project.SysConstant.BEAN_PRODUCT  存货信息JAVABEAN
  engine.project.LookUp stockKindBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STOCKS_KIND);
  String retu = B_InProductPriceBeanbean.doService(request, response);//src=../pub/main.jsp(初始化函数取值)
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = B_InProductPriceBeanbean.getDetailTable();
  String curUrl = request.getRequestURL().toString();
  boolean cansearch = loginBean.hasLimits(pageCode, op_search);
  boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  boolean canedit = B_InProductPriceBeanbean.getState();
  String detailClass_r = canedit ? "class=edFocused_r": "class=ednone_r" ;
  String readonly = canedit ?"": "readonly" ;

%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type"  content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript" src="../scripts/frame.js"></script>
<SCRIPT language="javascript">
  var isonchange=false;
function onNavigator()
  {
    if(isonchange)
    {
      if(confirm("已有改动,是否手工保存?"))
        return;
    }
    sumitForm("",-1);
}
function inputonchange()
{
  isonchange=true;
}
function sumitForm(oper, row)
{
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value  = row;
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
//产品编码
function productCodeSelect(obj)
{
    ProdCodeChange(document.all['prod'], obj.form.name,'srcVar=cpid&srcVar=cpid$product&srcVar=cpid$cpbm&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',obj.value);
}
//产品名称
function productNameSelect(obj)
{
    ProdNameChange(document.all['prod'], obj.form.name,'srcVar=cpid&srcVar=cpid$product&srcVar=cpid$cpbm&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',obj.value);
}
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD nowrap align="center">内部物资价格维护</TD>
</TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum"  VALUE="">
  <TABLE width="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
     <td class="td" nowrap>
     <%
         String key = "ppdfsgg";
         pageContext.setAttribute(key, list);
         int iPage = loginBean.getPageSize();
         String pageSize = ""+iPage;
         String ss= "sumitForm("+Operate.POST+");";
         String s = "location.href='"+B_InProductPriceBeanbean.retuUrl+"'";
         %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"  form="form1" operate="onNavigator();" />
     </td>
      <TD align="right">
        <%if(isCanEdit&&!canedit){
        String m = "sumitForm("+B_InProductPriceBeanbean.MODIFY+");";
         %>
         <INPUT class="button" onClick="sumitForm(<%=B_InProductPriceBeanbean.MODIFY%>);" type="button" value="修改(M)" name="MODIFY" onKeyDown="return getNextElement();">
        <pc:shortcut key="m" script="<%=m%>" />
       <%}%>
       <%if(isCanEdit&&canedit){%>
      <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存(S) ">
      <pc:shortcut key="s" script='<%=ss%>'/>
       <%}%>
     <%if(cansearch){%>
      <INPUT class="button" onClick="showFixedQuery()" type="button" value="查询(Q)" name="Query" onKeyDown="return getNextElement();">
      <pc:shortcut key="q" script='showFixedQuery()'/>
       <%}%>
      <input name="button2" type="button" align="Right" class="button" onClick="location.href='<%=B_InProductPriceBeanbean.retuUrl%>'" value=" 返回(C) "border="0">
      <pc:shortcut key="c" script="<%=s%>" />
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>产品代码</td>
      <td nowrap>品名</td>
      <td nowrap>规格</td>
      <td nowrap>存货类别</td>
      <td nowrap>内部价格</td>
      <td nowrap>计量单位</td>
    </tr>
    <%
    int i=0;
    //RowMap[] rows = B_InProductPriceBeanbean.getDetailRowinfos();
    //String[] widthName = new String[]{"ccj", "msj", "lsj", "qtjg1","qtjg2","qtjg3"};
    //int[] widthMin = new int[]{60, 60, 70, 70,70,70};
    //int[] widths = B_InProductPriceBeanbean.getMaxStyleWidth(rows, widthName, widthMin);
    list.first();
    for(; i<list.getRowCount(); i++)
    {
    %>
    <tr onclick="selectRow()">
      <td class="td" nowrap align="left"><%=list.getValue("cpbm")%><INPUT TYPE="HIDDEN" NAME="cpid_<%=i%>"  VALUE="<%=list.getValue("cpid")%>"></td><!--品名 规格-->
      <td class="td" nowrap align="left"><%=list.getValue("pm")%></td><!--品名-->
      <td class="td" nowrap align="left"><%=list.getValue("gg")%></td><!--品名-->
      <td class="td" nowrap align="left"><%=stockKindBean.getLookupName(list.getValue("chlbid"))%></td><!--存货类别id-->
      <td class="td" nowrap align="right"><INPUT TYPE="TEXT" NAME="jhdj_<%=i%>" VALUE="<%=list.getValue("jhdj")%>" style="width:100%" MAXLENGTH="<%=list.getColumn("jhdj").getPrecision()%>" <%=detailClass_r%>  onchange="inputonchange()" <%=readonly%> ></td>
      <td class="td" nowrap align="left"><%=list.getValue("jldw")%></td><!--计量单位-->
    </tr>
    <%
       list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
     <td class="td" nowrap></td>
    </tr>
    <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
   <div class="queryPop" id="fixedQuery" name="fixedQuery">
   <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
         <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
                <TD align="center" nowrap class="td">产品代码</TD>
                <td nowrap class="td" colspan="3">
                <INPUT TYPE="hidden" NAME="cpid" value="">
                <input class="edbox" style="WIDTH:100" name="cpid$cpbm" value='<%=B_InProductPriceBeanbean.getFixedQueryValue("cpid$cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this)" >
                <INPUT class="edbox" style="WIDTH:220" id="cpid$product" name="cpid$product" value='<%=B_InProductPriceBeanbean.getFixedQueryValue("cpid$product")%>' maxlength='220' onKeyDown="return getNextElement();"  onchange="productNameSelect(this)">
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cpid&srcVar=cpid$product&srcVar=cpid$cpbm&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',fixedQueryform.cpid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="fixedQueryform.cpid.value='';fixedQueryform.cpid$product.value='';fixedQueryform.cpid$cpbm.value='';">
                </td>
            </tr>
            <tr>
            <TD class="td" nowrap>存货类别</TD>
              <TD class="td" nowrap><pc:select name="chlbid" addNull="1" style="width:160">
              <%=stockKindBean.getList(B_InProductPriceBeanbean.getFixedQueryValue("chlbid"))%></pc:select>
              </td>
            </tr>
             <TR>
                  <td align="center" nowrap class="td">内部价格</td>
                  <td class="td" nowrap>
                    <INPUT class="edbox" style="WIDTH: 60px" name="jhdj$a" value='<%=B_InProductPriceBeanbean.getFixedQueryValue("jhdj$a")%>' onKeyDown="return getNextElement();">--
                    <INPUT class="edbox"  style="WIDTH: 60px" name="jhdj$b" value='<%=B_InProductPriceBeanbean.getFixedQueryValue("jhdj$b")%>' onKeyDown="return getNextElement();">
                    为空<input type="checkbox" name="jhdj" value="0" >
                  </td>
             </TR>
            </TABLE>
            <TR>
              <TD colspan="4" nowrap class="td" align="center">
                <% String qu = "sumitFixedQuery("+B_InProductPriceBeanbean.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=B_InProductPriceBeanbean.FIXED_SEARCH%>)" type="button" value="查询(K)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="k" script='<%=qu%>'/>
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(x)" name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script="hideFrame('fixedQuery')"/>
              </TD>
            </TR>
    </TABLE>
  </DIV>
</form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="320" height="325" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(retu);%>
</body>
</html>
