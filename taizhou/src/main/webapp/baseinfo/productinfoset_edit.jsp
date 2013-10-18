<%--物资编码编辑页面--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over   = "op_over";
  String op_edit_abc = "op_edit_abc";
%><%String pageCode = "productinfoset";
  if(!loginBean.hasLimits("productinfoset", request, response))
    return;
  engine.erp.baseinfo.Product productBean = engine.erp.baseinfo.Product.getInstance(request);
  engine.project.LookUp firstkindBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_KIND);
  engine.project.LookUp workshopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp stockKindBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STOCKS_KIND);//存货类别
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);//LookUp仓库信息
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
  form1.submit();
}

function backList()
{
  location.href='productinfoset.jsp';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<%String retu = productBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  RowMap row = productBean.getRowinfo();
  EngineDataSet ds = productBean.getOneTable();
  boolean isEdit = productBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);
  String custName = loginBean.getSystemParam("SYS_CUST_NAME");
  boolean hasABC = !"shengyu".equals(custName) || loginBean.hasLimits(pageCode, op_edit_abc);
  String readonly = isEdit ? "" : "readonly";
  String chxz = row.get("chxz").equals("1") ? "自制件" : (row.get("chxz").equals("2") ? "外购件" : row.get("chxz").equals("3") ? "外协件" : row.get("chxz").equals("4") ? "虚拟件" : "");
  String jjff = row.get("jjff").equals("1") ? "加权平均法" : (row.get("chxz").equals("2") ? "移动平均法" : "计划单价法" );
  String issale = row.get("issale").equals("1") ? "是" : "否";
  String isprops = row.get("isprops").equals("1") ? "是" : "否";
%>
<form name="form1" action="<%=curUrl%>" method="POST"  enctype="multipart/form-data" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" value="14">
  <table BORDER="0" CELLPADDING="0" CELLSPACING="2" width="400" align="center">
    <tr valign="top">
      <td width="400"><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
          <tr>
            <td class="activeVTab">物资信息</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=2 CELLPADDING=0 width="100%">
          <tr>
            <td> <table CELLSPACING="1" CELLPADDING="3" BORDER="0" width="100%" bgcolor="#f0f0f0">
                <tr>
                  <%String sumit = "sumitForm("+productBean.ONCHANGE+")";%>
                  <td noWrap class="tdTitle">物资类别</td>
                  <td noWrap class="td">
                    <%if(!isEdit)out.print("<input type='text' value='"+firstkindBean.getLookupName(row.get("wzlbid"))+"' style='width:160' class='ednone' readonly>");
                        else {%>
                    <pc:select name="wzlbid" style="width:160" onSelect="<%=sumit%>">
                    <%=firstkindBean.getList(row.get("wzlbid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">物资编码</td>
                  <td noWrap class="td"><%=row.get("parentCode")%>-
                    <input type="text" name="self_code" value='<%=row.get("self_code")%>' style="width:120" class="edbox" maxlength='<%=productBean.product_len%>' size='<%=productBean.product_len%>' onKeyDown="return getNextElement();" onchange='reshow();' <%=readonly%>></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">存货类别</td>
                  <td noWrap class="td">
                    <%if(!isEdit)out.print("<input type='text' value='"+stockKindBean.getLookupName(row.get("chlbid"))+"' style='width:160' class='ednone' readonly>");
                  else {%>
                    <pc:select name="chlbid" style="width:160"> <%=stockKindBean.getList(row.get("chlbid"))%>
                    </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">助记码</td>
                  <td noWrap class="td"><input type="text" name="zjm" value='<%=row.get("zjm")%>' style="width:160" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>>
                  </td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">物资名称</td>
                  <td noWrap class="td"><input type="text" name="pm" value='<%=row.get("pm")%>' style="width:160" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td noWrap class="tdTitle">规格</td>
                  <td noWrap class="td"><input type="text" name="gg" value='<%=row.get("gg")%>' style="width:160" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                </tr>
                <%if(productBean.CUST_SHENGYU.equals(productBean.systemCustName)){%>
                <tr>
                  <td noWrap class="tdTitle">花号</td>
                  <td noWrap class="td"><input type="text" name="hh" value='<%=row.get("hh")%>' style="width:160" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td noWrap class="tdTitle">款式</td>
                  <td noWrap class="td"><input type="text" name="ks" value='<%=row.get("ks")%>' style="width:160" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                </tr>
                <%}%>
                <tr>
                  <td noWrap class="tdTitle">计量单位</td>
                  <td noWrap class="td"><input type="cpgg" name="jldw" value='<%=row.get("jldw")%>' style="width:160" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td noWrap class="tdTitle">换算单位</td>
                  <td noWrap class="td"><input type="text" name="hsdw" value='<%=row.get("hsdw")%>' style="width:160" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">换算比例</td>
                  <td noWrap class="td"><input type="text" name="hsbl" value='<%=row.get("hsbl")%>' maxlength='<%=ds.getColumn("hsbl").getPrecision()%>' style="width:160" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td noWrap class="tdTitle">计划单价</td>
                  <td noWrap class="td"><input type="text" name="jhdj" value='<%=row.get("jhdj")%>' maxlength='<%=ds.getColumn("jhdj").getPrecision()%>' style="width:160" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">生产用单位</td>
                  <td noWrap class="td"><input type="text" name="scydw" value='<%=row.get("scydw")%>' style="width:160" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td noWrap class="tdTitle">生产用公式</td>
                  <td noWrap class="td"><input type="text" name="scdwgs" value='<%=row.get("scdwgs")%>' maxlength='<%=ds.getColumn("scdwgs").getPrecision()%>' style="width:160" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                </tr>

                <tr>
                  <td noWrap class="tdTitle">存货性质</td>
                  <td noWrap class="td">
                    <%if(!isEdit)out.print("<input type='text' value='"+chxz+"' style='width:160' class='ednone' readonly>");
                  else {%>
                    <pc:select name="chxz" style="width:160" value='<%=row.get("chxz")%>'>
                      <pc:option value='1'>自制件</pc:option>
                      <pc:option value='2'>外购件</pc:option>
                      <pc:option value='3'>外协件</pc:option>
                      <pc:option value='4'>虚拟件</pc:option>
                    </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">计价方法</td>
                  <td noWrap class="td">
                    <%if(!isEdit)out.print("<input type='text' value='"+jjff+"' style='width:160' class='ednone' readonly>");
                  else {%>
                    <pc:select  name="jjff" style="width:160" value='<%=row.get("jjff")%>'>
                      <pc:option value='1'>加权平均法</pc:option>
                      <pc:option value='2'>移动平均法</pc:option>
                      <pc:option value='3'>计划单价法</pc:option>
                    </pc:select>
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">图</td>
                  <td noWrap class="td" colspan="3">
                  <input type="file" name="myfile"><br>
                   </td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">生产车间</td>
                  <td noWrap class="td">
                    <%if(!isEdit)out.print("<input type='text' value='"+workshopBean.getLookupName(row.get("deptid"))+"' style='width:160' class='ednone' readonly>");
                  else {%>
                    <pc:select name="deptid" className="edFocused" addNull="1" style="width:160" >
                    <%=workshopBean.getList(row.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">存放仓库</td>
                  <td noWrap class="td">
                    <%if(!isEdit)out.print("<input type='text' value='"+storeBean.getLookupName(row.get("storeid"))+"' style='width:160' class='ednone' readonly>");
                  else {%>
                    <pc:select name="storeid" className="edFocused" addNull="1" style="width:160" >
                    <%=storeBean.getList(row.get("storeid"))%> </pc:select>
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">提前期</td>
                  <td noWrap class="td"><input type="text" name="tqq" value='<%=row.get("tqq")%>' style="width:160" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td noWrap class="tdTitle">条形码</td>
                  <td noWrap class="td"><input type="text" name="txm" value='<%=row.get("txm")%>' style="width:160" class="edbox" onKeyDown="return getNextElement();" maxlength="32" size="32" <%=readonly%>></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">是否销售</td>
                  <td noWrap class="td">
                    <%if(!isEdit)out.print("<input type='text' value='"+issale+"' style='width:160' class='ednone' readonly>");
                  else {%>
                    <input type="radio" name="issale" value="1"<%=row.get("issale").equals("1") ? " checked" : ""%>>
                    是
                    <input type="radio" name="issale" value="0"<%=!row.get("issale").equals("1") ? " checked" : ""%>>
                    否
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">规格属性</td>
                  <td noWrap class="td">
                    <%if(!isEdit)out.print("<input type='text' value='"+isprops+"' style='width:160' class='ednone' readonly>");
                  else {%>
                    <input type="radio" name="isprops" value="1"<%=row.get("isprops").equals("1") ? " checked" : ""%> checked>
                    有
                    <input type="radio" name="isprops" value="0"<%=row.get("isprops").equals("0") ? " checked" : ""%>>
                    无
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">生产性质</td>
                  <td noWrap class="td">
                    <%String productProp=row.get("productProp");
                      String temp = productProp.equals("0") ? "正品": (productProp.equals("1") ? "副品" : "废品");%>
                    <%if(!isEdit) out.print("<input type='text' value='"+temp+"' style='width:85' class='edline' readonly>");
                    else {%>
                    <pc:select name="productProp"  style="width:110" value='<%=productProp%>'>
                    <pc:option value='0'>正品</pc:option> <pc:option value='1'>副品</pc:option>
                    <pc:option value='2'>废品</pc:option> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">是否批号跟踪</td>
                  <td noWrap class="td"><input type="checkbox" name="isbatchno" value="1" <%=row.get("isbatchno").equals("1") ? "checked" : ""%>></td>
                </tr>

                <tr>
                  <td noWrap class="tdTitle">备注</td>
                  <td noWrap class="td" colspan="3"><input type="text" name="bz" value='<%=row.get("bz")%>' maxlength='<%=ds.getColumn("bz").getPrecision()%>' style="width:380" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                </tr>
                <%if(productBean.CUST_ESSEN.equals(productBean.systemCustName)){%>
                <tr>
                  <td noWrap class="tdTitle">成交半成品数</td>
                  <td noWrap class="td"><input type="text" name="sjbcps" value='<%=row.get("sjbcps")%>' maxlength='<%=ds.getColumn("sjbcps").getPrecision()%>' style="width:160" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td noWrap class="tdTitle">成交滤饼含量</td>
                  <td noWrap class="td"><input type="text" name="sjlbhl" value='<%=row.get("sjlbhl")%>' maxlength='<%=ds.getColumn("sjlbhl").getPrecision()%>' style="width:160" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">成交滤饼水份</td>
                  <td noWrap class="td"><input type="text" name="sjlbsf" value='<%=row.get("sjlbsf")%>' maxlength='<%=ds.getColumn("sjlbsf").getPrecision()%>' style="width:160" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td noWrap class="tdTitle"></td>
                  <td noWrap class="td"></td>
                </tr>
                <%}%>
                <tr>
                  <td colspan="4" noWrap class="tdTitle" align="center">注释:生产用单位与计量单位换算关系（生产单位=
                    计量单位*生产公式）</td>
                </tr>
              </table></td>
          </tr>
        </table></td>
    </tr>
    <tr>
      <td> <table CELLSPACING=0 CELLPADDING=0 width="100%">
          <tr>
            <td noWrap class="tableTitle"><br>
              <%if(isEdit){%>
              <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
              <%}%>
              <input name="btnback" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 返回(C)">
              <pc:shortcut key="c" script='parent.hideFrameNoFresh();'/> </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
    function reshow()
  {
    var objself_code = form1.self_code;
    if(objself_code.value=='')
    {
      alert('顺序号位数不能为空');
      return;
    }
    if(isNaN(objself_code.value))
    {
      alert('顺序号位数非法');
      return;
    }
  }
  function scdwgs_check(obj, o){
    if(obj.value==""){
      alert(o+"不能为空");
      return;
    }
    if(isNaN(obj.value))
    {
      alert(o+"非法");
      return;
    }
  }
</script>
</BODY>
</Html>