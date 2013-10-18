<%--采购报价列表--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
  String op_provider="op_provider";
%><%String pageCode = "buyprice";
  if(!loginBean.hasLimits("buyprice", request, response))
    return;
  engine.erp.buy.xixing.BuyPrice buyPriceBean = engine.erp.buy.xixing.BuyPrice.getInstance(request);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//规格属性
  engine.project.LookUp wbBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_FOREIGN_CURRENCY);//外币信息
  String retu = buyPriceBean.doService(request, response);//location.href='baln.htm'
  boolean isProvider=loginBean.hasLimits(pageCode, op_provider);
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
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value  = row;
    form1.submit();
  }
  function showInterFrame(oper, rownum){
    var url = "buypriceedit.jsp?operate="+oper+"&rownum="+rownum;
    document.all.interframe1.src = url;
    showFrame('detailDiv',true,"",true);
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

   function hideInterFrame()//隐藏FRAME
   {
     lockScreenToWait("处理中, 请稍候！");
     hideFrame('detailDiv');
     form1.submit();
   }
   function hideFrameNoFresh(){
     hideFrame('detailDiv');
  }
  function corpCodeSelect(obj,srcVars)
 {
   ProvideCodeChange(document.all['prod'], obj.form.name, srcVars,
                     'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc',obj.value);
 }
 function corpNameSelect(obj,srcVars)
 {
   ProvideNameChange(document.all['prod'], obj.form.name, srcVars,
                     'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
 }
 function productCodeSelect(obj,srcVars)
 {
   ProdCodeChange(document.all['prod'], obj.form.name, srcVars,
                  'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
 }
 function productNameSelect(obj,srcVars)
 {
   ProdNameChange(document.all['prod'], obj.form.name, srcVars,
                  'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
  }
</SCRIPT>
<%
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = buyPriceBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
  String bjfs = null;
  bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
%>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD nowrap align="center">采购报价资料</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum"  VALUE="">
  <TABLE width="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
       <td class="td" nowrap>
     <%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
</td>
      <TD align="right"><%if(loginBean.hasLimits(pageCode, op_search)){%>
        <INPUT class="button" onClick="showFixedQuery()" type="button" value=" 查询(Q)" name="Query" onKeyDown="return getNextElement();">
       <pc:shortcut key="q" script='showFixedQuery()'/>
        <%}%>
        <input name="button2" type="button" align="Right" class="button" onClick="location.href='<%=buyPriceBean.retuUrl%>'" value=" 返回(C)"border="0">
        <% String back ="location.href='"+buyPriceBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/>
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
       <td nowrap align="center" width=45><%if(loginBean.hasLimits(pageCode, op_add)&&isProvider){%><input name="image" class="img" type="image" title="新增(A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/><%}%></td>
      <td nowrap>原辅料编号</td>     <!--    -->
      <td nowrap>名称</td>          <!--    -->
      <td nowrap>规格</td>
      <td nowrap>规格属性</td>
      <%if(isProvider){%><td nowrap>供应商</td><%}%>
      <td nowrap><%=isHsbj ? "换算单位" : "计量单位"%></td>
      <%--td nowrap>换算单位报价</td--%>
      <td nowrap>报价</td>             <!--   bj -->
      <td nowrap>基本质量情况</td>          <!-- jbzlqk   -->
   <%--   <td nowrap>含运费<%=list.format("hyf").equals("1") ? "含运费" : ""%>--%></td><!-- hyf   -->
   <!--   <td nowrap>含票</td> hp   -->
   <!--   <td nowrap>含硅</td> hg   -->
      <td nowrap>优惠条件</td>
      <td nowrap>开始日期</td>
      <td nowrap>结束日期</td>
      <%if(isProvider){%><td nowrap>供应商料号</td><%}%>
      <td nowrap>备注</td>
      <td nowrap>制单日期</td>
      <td nowrap>是否历史报价</td>
    </tr>
     <%corpBean.regData(list, "dwtxid");
       prodBean.regData(list,"cpid");
       wbBean.regData(list, "wbid");
       corpBean.regData(list,"dmsxid");
       propertyBean.regData(list, "dwtxid");
       list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr onDblClick="selectRow();showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
        <td class="td"><input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <%if(loginBean.hasLimits(pageCode, op_delete)){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      <%}%></td>
      <%RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));%>
      <td class="td" nowrap><%=prodRow.get("cpbm")%></td> <!-- 规格-->
      <td class="td" nowrap><%=prodRow.get("pm")%></td>  <!-- 品名-->
      <td class="td" nowrap><%=prodRow.get("gg")%></td>  <!-- 规格-->
      <td class="td" nowrap><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td>
      <%if(isProvider){%><td class="td" nowrap><%=corpBean.getLookupName(list.getValue("dwtxid"))%></td><%}%>
      <td class="td" align="left" nowrap ><%=isHsbj ? prodRow.get("hsdw") : prodRow.get("jldw")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("bj")%></td>
      <td class="td" align="left" nowrap><%=list.getValue("jbzlqk")%></td>
  <%--    <td class="td" align="right" nowrap><%=list.getValue("hyf")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("hp")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("hg")%></td>  --%>
      <td class="td" nowrap><%=list.getValue("yhtj")%></td>
      <td class="td" nowrap><%=list.getValue("ksrq")%></td>
      <td class="td" nowrap><%=list.getValue("jsrq")%></td>
      <%if(isProvider){%><td class="td" nowrap><%=list.getValue("gyslh")%></td><%}%>
      <td class="td" nowrap><%=list.getValue("bz")%></td>
      <td class="td" nowrap><%=list.getValue("czrq")%></td>
      <td class="td" nowrap align='left'><%String sflsbj=list.getValue("sflsbj");if(sflsbj.equals("1")) out.print("是"); else out.print("否");%></td>
    </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <%if(isProvider){%><td class="td" nowrap>&nbsp;</td><%}%>
      <%if(isProvider){%><td class="td" nowrap></td><%}%>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
    </tr>
    <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
  function hide()
  {
    hideFrame('fixedQuery');
  }
</SCRIPT>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
   <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0><%buyPriceBean.table.printWhereInfo(pageContext);%>
            <%--TR>
              <TD nowrap class="td">往来单位</TD>
              <TD class="td" nowrap>
                <input class="EDLine" style="WIDTH:130px" name="dwmc" value='<%=corpBean.getLookupName(buyPriceBean.getFixedQueryValue("cg_bj$dwtxid"))%>' onKeyDown="return getNextElement();"readonly>
                <input type="hidden" name="cg_bj$dwtxid" value="<%=buyPriceBean.getFixedQueryValue("cg_bj$dwtxid")%>"> <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=cg_bj$dwtxid&srcVar=dwmc','fieldVar=dwtxid&fieldVar=dwmc',fixedQueryform.cg_bj$dwtxid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cg_bj$dwtxid.value='';dwmc.value='';">
              </TD>
              <TD nowrap class="td">产品名称</TD>
              <td nowrap class="td"><input class="EDLine" style="WIDTH:130px" name="product" value='<%=buyPriceBean.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="cg_bj$cpid" value="<%=buyPriceBean.getFixedQueryValue("cg_bj$cpid")%>">
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cg_bj$cpid&srcVar=product','fieldVar=cpid&fieldVar=product',fixedQueryform.cg_bj$cpid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cg_bj$cpid.value='';product.value='';">
              </TD>
              </TR>
              <TR>
              <TD class="td" nowrap>产品编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="cg_bj$cgbjid$cpbm" name="cg_bj$cgbjid$cpbm" value='<%=buyPriceBean.getFixedQueryValue("cg_bj$cgbjid$cpbm")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>品名规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="cg_bj$cgbjid$product" name="cg_bj$cgbjid$product" value='<%=buyPriceBean.getFixedQueryValue("cg_bj$cgbjid$product")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
             <TR>
              <TD nowrap class="td">开始日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="cg_bj$ksrq$a" value='<%=buyPriceBean.getFixedQueryValue("cg_bj$ksrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(cg_bj$ksrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="cg_bj$ksrq$b" value='<%=buyPriceBean.getFixedQueryValue("cg_bj$ksrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(cg_bj$ksrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD nowrap class="td">结束日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="cg_bj$jsrq$a" value='<%=buyPriceBean.getFixedQueryValue("cg_bj$jsrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(cg_bj$jsrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="cg_bj$jsrq$b" value='<%=buyPriceBean.getFixedQueryValue("cg_bj$jsrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(cg_bj$jsrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR--%>
            </TABLE>
            <TR>
              <TD colspan="4" nowrap class="td" align="center"><%if(loginBean.hasLimits(pageCode, op_search)){%><INPUT class="button" onClick="sumitFixedQuery(<%=buyPriceBean.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script='<%="sumitFixedQuery("+ buyPriceBean.FIXED_SEARCH +",-1)"%>'/>
                <%}%><INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X) " name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script='hide();'/>
              </TD>
            </TR>
    </TABLE>
  </DIV>
</form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="680" height="420" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(retu);%>
</body>
</html>