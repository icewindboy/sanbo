<%--物资编码列表页面--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate, java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%><%String pageCode = "productinfoset";
  if(!loginBean.hasLimits("productinfoset", request, response))
    return;
  engine.erp.baseinfo.Product productBean = engine.erp.baseinfo.Product.getInstance(request);
  engine.project.LookUp firstkindBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_KIND);
  engine.project.LookUp workshopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp stockKindBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STOCKS_KIND);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);//LookUp仓库信息
  String retu = productBean.doService(request, response);//location.href='baln.htm'
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

<script language="javascript">
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value  = row;
    form1.submit();
  }
  function showInterFrame(oper, rownum){
    var url = "productinfoset_edit.jsp?operate="+oper+"&rownum="+rownum;
    document.all.interframe1.src = url;
    showFrame('detailDiv',true,"",true);
  }

  function hideInterFrame()//隐藏FRAME
  {
    lockScreenToWait("处理中, 请稍候！");
    hideFrame('detailDiv');
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

  function hideFrameNoFresh()
  {
    hideFrame('detailDiv');
  }
</script>
<%
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = productBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
  boolean isEssen = productBean.CUST_ESSEN.equals(productBean.systemCustName);
  boolean isShengyu = productBean.CUST_SHENGYU.equals(productBean.systemCustName);
%>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD nowrap align="center">物资编码表</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum"  VALUE="">
  <TABLE width="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
       <td class="td" nowrap>
     <%String key = "ppdfs"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
</td>
      <TD align="right"><%if(loginBean.hasLimits(pageCode, op_search)){%>
        <INPUT class="button" onClick="showFixedQuery()" type="button" value=" 查询(Q)" name="Query" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/>
        <%}%>
        <input name="button2" type="button" align="Right" class="button" onClick="location.href='<%=productBean.retuUrl!=null ? productBean.retuUrl : "../blank.htm"%>'" value=" 返回(C)"border="0">
        <% String href = productBean.retuUrl!=null ? productBean.retuUrl : "../blank.htm";
         String back ="location.href='"+href+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/>
       <input type="button" class="button" value=" 打印 " onclick="location.href='../pub/pdf.jsp?code=productcode&operate=0'">
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=75 align="center"><%if(loginBean.hasLimits(pageCode, op_add)){%><input name="image" class="img" type="image" title="新增(A)" onClick="showInterFrame(11,-1)" src="../images/add.gif" border="0">
      <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/><%}%>
      </td>
      <td nowrap>物资编码</td>
      <td nowrap>助记码</td>
      <td nowrap>品名</td>
      <td nowrap>规格</td>
      <%if(isShengyu){%>
      <td nowrap>花号</td>
      <td nowrap>款式</td>
      <%}%>
      <td nowrap>条形码</td>
      <td nowrap>图号</td>
      <td nowrap>ABC分类</td>
      <td nowrap>存货类别</td>
      <td nowrap>物资类别</td>
      <td nowrap>计量单位</td>
      <td nowrap>换算单位</td>
      <td nowrap>换算比例</td>
      <td nowrap>计划单价</td>
      <td nowrap>生产用单位</td>
      <td nowrap>生产用公式</td>
      <td nowrap>存货性质</td>
      <td nowrap>计价方式</td>
      <td nowrap>存放仓库</td>
      <td nowrap>库存上限</td>
      <td nowrap>库存下限</td>
      <td nowrap>提前期</td>
      <td nowrap>生产车间</td>
      <td nowrap>生产性质</td>
      <td nowrap>规格属性</td>
      <td nowrap>销售</td>
      <td nowrap>批号跟踪</td>
      <td nowrap>备注</td>
      <%if(isEssen){%>
      <td nowrap>成交半成品数</td>
      <td nowrap>成交滤饼含量</td>
      <td nowrap>成交滤饼水份</td>
      <%}%>
    </tr>
     <%
       list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
      <td class="td">
        <input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
        <%if(list.getValue("isprops").equals("1")){%>
        <input name="image3" class="img" type="image" title="物资规格属性" onClick="PropertyEdit(<%=list.getValue("cpid")%>,<%=list.getValue("wzlbid")%>)" src="../images/select_prod.gif" border="0"><%}%>
        <%if(loginBean.hasLimits(pageCode, op_delete)){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
        <%}%></td>
      <td class="td" nowrap><%=list.getValue("cpbm")%></td>
      <td class="td" nowrap><%=list.getValue("zjm")%></td>
      <td class="td" nowrap><%=list.getValue("pm")%></td>
      <td class="td" nowrap><%=list.getValue("gg")%></td>
      <%if(isShengyu){%>
      <td class="td" nowrap><%=list.getValue("hh")%></td>
      <td class="td" nowrap><%=list.getValue("ks")%></td>
      <%}%>
      <td class="td" nowrap><%=list.getValue("txm")%></td>
      <td class="td" nowrap><%=list.getValue("th")%></td>
      <td class="td" align="center" nowrap><%=list.getValue("abc").equals("A") ? "A" : (list.getValue("abc").equals("B") ? "B" : (list.getValue("abc").equals("C")) ? "C" : "")%></td>
      <td class="td" nowrap><%=stockKindBean.getLookupName(list.getValue("chlbid"))%></td>
      <td class="td" nowrap><%=firstkindBean.getLookupName(list.getValue("wzlbid"))%></td>
      <td class="td" align="center" nowrap><%=list.getValue("jldw")%></td>
      <td class="td" align="center" nowrap><%=list.getValue("hsdw")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("hsbl")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("jhdj")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("scydw")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("scdwgs")%></td>
      <td class="td" nowrap><%=list.getValue("chxz").equals("1") ? "自制件" : (list.getValue("chxz").equals("2") ? "外购件" : list.getValue("chxz").equals("3") ? "外协件" : list.getValue("chxz").equals("4") ? "虚拟件" : "")%></td>
      <td class="td" nowrap><%=list.getValue("jjff").equals("1") ? "加权平均法" : (list.getValue("chxz").equals("2") ? "移动平均法" : "计划单价法" )%></td>
      <td class="td" nowrap><%=storeBean.getLookupName(list.getValue("storeid"))%></td>
      <td class="td" nowrap><%=list.getValue("maxsl")%></td>
      <td class="td" nowrap><%=list.getValue("minsl")%></td>
      <td class="td" nowrap><%=list.getValue("tqq")%></td>
      <td class="td" nowrap><%=workshopBean.getLookupName(list.getValue("deptid"))%></td>
      <td class="td" nowrap><%=list.getValue("productProp").equals("0") ? "正品" : (list.getValue("productProp").equals("1") ? "副品" : "废品")%></td>
      <td class="td" nowrap><%=list.getValue("isprops").equals("1")?"√" :""%></td>
      <td class="td" nowrap><%=list.getValue("issale").equals("1")?"√" :""%></td>
      <td class="td" nowrap><%=list.getValue("isbatchno").equals("1")?"√" :""%></td>
      <td class="td" nowrap><%=list.getValue("bz")%></td>
      <%if(isEssen){%>
      <td class="td" nowrap><%=list.getValue("sjbcps")%></td>
      <td class="td" nowrap><%=list.getValue("sjlbhl")%></td>
      <td class="td" nowrap><%=list.getValue("sjlbsf")%></td><%}%>
    </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <%if(isShengyu){%><td class="td" nowrap></td>
      <td class="td" nowrap></td><%}%>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <%if(isEssen){%>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td><%}%>
      </tr>
    <%}%>
  </table>
</form>
<script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
function PropertyEdit(cpid,wzlbid)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=590 height=470 top=0 left=0";
  var paraStr = "../baseinfo/product_propertylist.jsp?operate=0&cpid="+ cpid + "&wzlbid="+wzlbid;
  newwin = window.open(paraStr,"PropertyEdit", winopt);
  newwin.focus;
}
function hide()
{
  hideFrame('fixedQuery');
}

</script>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">

  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
             <TR>
              <TD class="td" nowrap>产品编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" name="cpbm" value='<%=productBean.getFixedQueryValue("cpbm")%>' maxlength='32' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>存货类别</TD>
              <TD class="td" nowrap><pc:select name="chlbid" addNull="1" style="width:160">
              <%=stockKindBean.getList(productBean.getFixedQueryValue("chlbid"))%></pc:select>
              </td>
            </TR>
            <TR>
              <TD class="td" nowrap>品名模糊</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" name="pm$a" value='<%=productBean.getFixedQueryValue("pm$a")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>品名等于</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" name="pm$b" value='<%=productBean.getFixedQueryValue("pm$b")%>' onKeyDown="return getNextElement();"></TD>
              </TR>
              <TR>
              <TD class="td" nowrap>规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" name="gg" value='<%=productBean.getFixedQueryValue("gg")%>' onKeyDown="return getNextElement();">
              <TD class="td" nowrap>存放仓库</TD>
              <TD class="td" nowrap><pc:select name="storeid" addNull="1" style="width:160">
              <%=storeBean.getList(productBean.getFixedQueryValue("storeid"))%></pc:select>
              </td>
              </TD>
            </TR>
            <TR>
            <TD class="td" nowrap>存货性质</TD>
              <TD class="td" nowrap>
              <pc:select name="chxz" addNull="1" style="width:160" value='<%=productBean.getFixedQueryValue("chxz")%>'>
                <pc:option value='1'>自制件</pc:option>
                <pc:option value='2'>外购件</pc:option>
                <pc:option value='3'>外协件</pc:option>
                <pc:option value='4'>虚拟件</pc:option>
              </pc:select></TD>
              <TD class="td" nowrap>物资类别</TD>
              <TD class="td" nowrap><pc:select name="wzlbid" addNull="1" style="width:160">
              <%=firstkindBean.getList(productBean.getFixedQueryValue("wzlbid"))%></pc:select>
              </td>
            </TR>
             <TR>
            <TD class="td" nowrap>ABC分类</TD>
              <TD class="td" nowrap>
              <pc:select name="abc" addNull="1" style="width:160" value='<%=productBean.getFixedQueryValue("abc")%>'>
                <pc:option value='A'>A</pc:option>
                <pc:option value='B'>B</pc:option>
                <pc:option value='C'>C</pc:option>
              </pc:select></TD>
              <TD class="td" nowrap>计价方法</TD>
              <TD class="td" nowrap>
              <pc:select name="jjff" addNull="1" style="width:160" value='<%=productBean.getFixedQueryValue("jjff")%>'>
                <pc:option value='1'>加权平均法</pc:option>
                <pc:option value='2'>移动平均法</pc:option>
                <pc:option value='3'>计划单价法</pc:option>
              </pc:select>
              </td>
            </TR>
            <TR>
            <TD class="td" nowrap>生产车间</TD>
              <TD class="td" nowrap><pc:select name="deptid" addNull="1" style="width:160">
              <%=workshopBean.getList(productBean.getFixedQueryValue("deptid"))%></pc:select>
              </td>
              <TD></TD>
            </TR>
            <TR>
             <TD class="td" nowrap>是否销售 </TD>
              <TD class="td" nowrap >
              <%String issale = productBean.getFixedQueryValue("issale");%>
               <input type="radio" name="issale" value=""<%=issale.equals("") ? " checked" : ""%>>
               全部
               <input type="radio" name="issale" value="1"<%=issale.equals("1") ? " checked" : ""%>>
                是
              <input type="radio" name="issale" value="0"<%=issale.equals("0") ? " checked" : ""%>>
                否
              </TD>
              <TD class="td" nowrap>规格属性 </TD>
              <TD class="td" nowrap >
              <%String isprops = productBean.getFixedQueryValue("isprops");%>
               <input type="radio" name="isprops" value=""<%=isprops.equals("") ? " checked" : ""%>>
               全部
               <input type="radio" name="isprops" value="1"<%=isprops.equals("1") ? " checked" : ""%>>
                有
              <input type="radio" name="isprops" value="0"<%=isprops.equals("0") ? " checked" : ""%>>
                无
              </TD>
            </TR>
          </TABLE>
      <TR>
        <TD colspan="4" nowrap class="td" align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=productBean.SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
          <pc:shortcut key="f" script='<%="sumitFixedQuery("+ productBean.SEARCH +")"%>'/>
          <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X)" name="button2" onKeyDown="return getNextElement();">
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
      <TD><iframe id="interframe1" src="" width="530" height='<%=isEssen ? "550" : isShengyu ? "530" : "500"%>' marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(retu);%>
</body>
</html>
