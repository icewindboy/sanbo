<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.*,java.math.BigDecimal"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "product_price";
%>
<%
if(!loginBean.hasLimits("product_price", request, response))
    return;
  engine.erp.sale.B_ProductPrice b_productpricebean = engine.erp.sale.B_ProductPrice.getInstance(request);//得到实例(初始化实例变量)
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);//engine.project.SysConstant.BEAN_CORP 外来单位JAVABEAN
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);//engine.project.SysConstant.BEAN_PRODUCT  存货信息JAVABEAN
  String retu = b_productpricebean.doService(request, response);//src=../pub/main.jsp(初始化函数取值)
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = b_productpricebean.getDetailTable();
  String curUrl = request.getRequestURL().toString();
  boolean cansearch = loginBean.hasLimits(pageCode, op_search);
  boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  boolean canedit = b_productpricebean.getState();
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

<SCRIPT language="javascript">
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
    ProdCodeChange(document.all['prod'], obj.form.name,'srcVar=cpid&srcVar=cpid$pm&srcVar=cpid$cpbm&srcVar=cpid$gg','fieldVar=cpid&fieldVar=pm&fieldVar=cpbm&fieldVar=gg',obj.value);
}
//产品名称
function productNameSelect(obj)
{
    ProdNameChange(document.all['prod'], obj.form.name,'srcVar=cpid&srcVar=cpid$pm&srcVar=cpid$cpbm&srcVar=cpid$gg','fieldVar=cpid&fieldVar=pm&fieldVar=cpbm&fieldVar=gg',obj.value);
}
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD nowrap align="center">产品销售定价</TD>
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
         String s = "location.href='"+b_productpricebean.retuUrl+"'";
         %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
     </td>
      <TD align="right">
        <%if(isCanEdit&&!canedit){
        String m = "sumitForm("+b_productpricebean.MODIFY+");";
         %>
         <INPUT class="button" onClick="sumitForm(<%=b_productpricebean.MODIFY%>);" type="button" value="修改(M)" name="MODIFY" onKeyDown="return getNextElement();">
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
      <input name="button2" type="button" align="Right" class="button" onClick="location.href='<%=b_productpricebean.retuUrl%>'" value=" 返回(C) "border="0">
      <pc:shortcut key="c" script="<%=s%>" />
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>产品代码</td>
      <td nowrap>品名 规格</td>
      <td nowrap>单位</td>
      <td nowrap>奖金比例(%)</td>
      <td nowrap>销售价</td>
      <td nowrap>销售底价</td>
      <td nowrap>基准价</td>
      <td nowrap>差价提成率(%)</td>
      <td nowrap>允许回款天数</td>
      <td nowrap>回款提成率(%)</td>
    </tr>
    <%
    prodBean.regData(list,"cpid");
    int i=0;
    RowMap[] rows = b_productpricebean.getDetailRowinfos();
    String[] widthName = new String[]{"jjbl", "xsj", "xsdj", "xsjzj","xstcl"};
    int[] widthMin = new int[]{60, 60, 70, 70,70};
    int[] widths = b_productpricebean.getMaxStyleWidth(rows, widthName, widthMin);
    for(; i<rows.length; i++)
    {
    %>
    <tr>
      <%
     RowMap row = rows[i];
     RowMap prodRow = prodBean.getLookupRow(row.get("cpid"));
     %>
      <td class="td" nowrap align="left"><%=row.get("cpbm")%><INPUT TYPE="HIDDEN" NAME="wzdjid_<%=i%>"  VALUE="<%=row.get("wzdjid")%>"></td><!--品名 规格-->
      <td class="td" nowrap align="left"><%=prodRow.get("product")%></td><!--品名 规格-->
      <td class="td" nowrap align="left"><%=prodRow.get("jldw")%></td><!--计量单位-->
      <td class="td" nowrap align="right"><INPUT TYPE="TEXT" NAME="jjbl_<%=i%>" VALUE="<%=row.get("jjbl")%>" style="width:100%" MAXLENGTH="<%=list.getColumn("jjbl").getPrecision()%>" <%=detailClass_r%> <%=readonly%> ></td>
      <td class="td" nowrap align="right"><INPUT TYPE="TEXT" NAME="xsj_<%=i%>" VALUE="<%=row.get("xsj")%>" style="width:100%" MAXLENGTH="<%=list.getColumn("xsj").getPrecision()%>" <%=detailClass_r%> <%=readonly%> ></td>
      <td class="td" nowrap align="right"><INPUT TYPE="TEXT" NAME="xsdj_<%=i%>" VALUE="<%=row.get("xsdj")%>" style="width:100%" MAXLENGTH="<%=list.getColumn("xsdj").getPrecision()%>" <%=detailClass_r%> <%=readonly%> ></td>
      <td class="td" nowrap align="right"><INPUT TYPE="TEXT" NAME="xsjzj_<%=i%>" VALUE="<%=row.get("xsjzj")%>" style="width:100%" MAXLENGTH="<%=list.getColumn("xsjzj").getPrecision()%>" <%=detailClass_r%> <%=readonly%>  ></td>
      <td class="td" nowrap align="right"><INPUT TYPE="TEXT" NAME="xstcl_<%=i%>" VALUE="<%=row.get("xstcl")%>" style="width:100%" MAXLENGTH="<%=list.getColumn("xstcl").getPrecision()%>"  <%=detailClass_r%> <%=readonly%> ></td>
      <td class="td" nowrap align="right"><INPUT TYPE="TEXT" NAME="hkts_<%=i%>" VALUE="<%=row.get("hkts")%>" style="width:100%" MAXLENGTH="<%=list.getColumn("hkts").getPrecision()%>" <%=detailClass_r%> <%=readonly%> ></td>
      <td class="td" nowrap align="right"><INPUT TYPE="TEXT" NAME="hktcl_<%=i%>" VALUE="<%=row.get("hktcl")%>" style="width:100%" MAXLENGTH="<%=list.getColumn("hktcl").getPrecision()%>" <%=detailClass_r%> <%=readonly%> ></td>
    </tr>
    <%
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
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
                <TD align="center" nowrap class="td">产品代码</TD>
                <td nowrap class="td" colspan="3">
                <INPUT TYPE="HIDDEN" NAME="cpid" value="">
                <input class="edbox" style="WIDTH:80" name="cpid$cpbm" value='<%=b_productpricebean.getFixedQueryValue("cpid$cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this)" >
                <INPUT class="edbox" style="WIDTH:120" id="cpid$pm" name="cpid$pm" value='<%=b_productpricebean.getFixedQueryValue("cpid$pm")%>' maxlength='10' onKeyDown="return getNextElement();"  onchange="productNameSelect(this)">
                <INPUT class="edline" style="WIDTH:120" id="cpid$gg" name="cpid$gg" value='<%=b_productpricebean.getFixedQueryValue("cpid$gg")%>' maxlength='10' onKeyDown="return getNextElement();" readonly>
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cpid&srcVar=cpid$pm&srcVar=cpid$cpbm&srcVar=cpid$gg','fieldVar=cpid&fieldVar=pm&fieldVar=cpbm&fieldVar=gg',fixedQueryform.cpid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';cpid$pm.value='';cpid$gg.value='';cpid$cpbm.value='';">
                </td>
             </tr>
              <TR>
                  <td align="center" nowrap class="td">销售价</td>
                  <td class="td" nowrap>
                    <INPUT class="edbox" style="WIDTH: 60px" name="xsj$a" value='<%=b_productpricebean.getFixedQueryValue("xsj$a")%>' onKeyDown="return getNextElement();">--
                    <INPUT class="edbox"  style="WIDTH: 60px" name="xsj$b" value='<%=b_productpricebean.getFixedQueryValue("xsj$b")%>' onKeyDown="return getNextElement();">
                    为空<input type="checkbox" name="xsj" value="0" >
                  </td>
                  <td align="center" nowrap class="td">销售底价</td>
                  <td class="td" nowrap>
                    <INPUT class="edbox" style="WIDTH: 60px" name="xsdj$a" value='<%=b_productpricebean.getFixedQueryValue("xsdj$a")%>' onKeyDown="return getNextElement();">--
                    <INPUT class="edbox"  style="WIDTH: 60px" name="xsdj$b" value='<%=b_productpricebean.getFixedQueryValue("xsdj$b")%>' onKeyDown="return getNextElement();">
                    为空<input type="checkbox" name="xsdj" value="0" >
                 </td>
                 </td>
             </TR>
             <TR>
                  <td align="center" nowrap class="td">基准价</td>
                  <td class="td" nowrap>
                    <INPUT class="edbox" style="WIDTH: 60px" name="xsjzj$a" value='<%=b_productpricebean.getFixedQueryValue("xsjzj$a")%>' onKeyDown="return getNextElement();">--
                    <INPUT class="edbox"  style="WIDTH: 60px" name="xsjzj$b" value='<%=b_productpricebean.getFixedQueryValue("xsjzj$b")%>' onKeyDown="return getNextElement();">
                    为空<input type="checkbox" name="xsjzj" value="0" >
                  </td>
                  <td align="center" nowrap class="td">差价提成率</td>
                  <td class="td" nowrap>
                    <INPUT class="edbox" style="WIDTH: 60px" name="xstcl$a" value='<%=b_productpricebean.getFixedQueryValue("xstcl$a")%>' onKeyDown="return getNextElement();">--
                    <INPUT class="edbox" style="WIDTH: 60px" name="xstcl$b" value='<%=b_productpricebean.getFixedQueryValue("xstcl$b")%>' onKeyDown="return getNextElement();">
                    为空<input type="checkbox" name="xstcl" value="0" >
                  </td>
             </TR>
             <TR>
                  <td align="center" nowrap class="td">允许回款天数</td>
                  <td class="td" nowrap>
                    <INPUT class="edbox" style="WIDTH: 60px" name="hkts$a" value='<%=b_productpricebean.getFixedQueryValue("hkts$a")%>' onKeyDown="return getNextElement();">--
                    <INPUT class="edbox"  style="WIDTH: 60px" name="hkts$b" value='<%=b_productpricebean.getFixedQueryValue("hkts$b")%>' onKeyDown="return getNextElement();">
                    为空<input type="checkbox" name="hkts" value="0" >
                  </td>
                  <td align="center" nowrap class="td">回款提成率</td>
                  <td class="td" nowrap>
                    <INPUT class="edbox" style="WIDTH: 60px" name="hktcl$a" value='<%=b_productpricebean.getFixedQueryValue("hktcl$a")%>' onKeyDown="return getNextElement();">--
                    <INPUT class="edbox" style="WIDTH: 60px" name="hktcl$b" value='<%=b_productpricebean.getFixedQueryValue("hktcl$b")%>' onKeyDown="return getNextElement();">
                    为空<input type="checkbox" name="hktcl" value="0" >
                  </td>
             </TR>
             <tr>
                 <td align="center" nowrap class="td">奖金比例</td>
                 <td class="td" nowrap>
                    <INPUT class="edbox" style="WIDTH: 60px" name="jjbl$a" value='<%=b_productpricebean.getFixedQueryValue("jjbl$a")%>' onKeyDown="return getNextElement();">--
                    <INPUT class="edbox"  style="WIDTH: 60px" name="jjbl$b" value='<%=b_productpricebean.getFixedQueryValue("jjbl$b")%>' onKeyDown="return getNextElement();">
                    为空<input type="checkbox" name="jjbl" value="0" >
                 </td>
                </td>
                </tr>
            </TABLE>
            <TR>
              <TD colspan="4" nowrap class="td" align="center">
                <% String qu = "sumitFixedQuery("+b_productpricebean.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=b_productpricebean.FIXED_SEARCH%>)" type="button" value="查询(K)" name="button" onKeyDown="return getNextElement();">
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