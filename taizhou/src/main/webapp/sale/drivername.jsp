<%--驾驶员行驶地区价格--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
  String op_provider="op_provider";
%><%String pageCode = "drivername";
  if(!loginBean.hasLimits("drivername", request, response))
    return;

  engine.erp.sale.DriverName driverNameBean = engine.erp.sale.DriverName.getInstance(request);
  engine.project.LookUp areaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_AREA);
  engine.project.LookUp areaCodeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_AREA_CODE);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//规格属性
   engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  String retu = driverNameBean.doService(request, response);//location.href='baln.htm'
 // boolean isProvider=loginBean.hasLimits(pageCode, op_provider);
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
  function showInterFrame(oper, rownum){
    var url = "drivernameedit.jsp?operate="+oper+"&rownum="+rownum;
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
  EngineDataSet list = driverNameBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
  String bjfs = null;
  bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
%>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD nowrap align="center">驾驶员信息</TD>
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
        <input name="button2" type="button" align="Right" class="button" onClick="location.href='<%=driverNameBean.retuUrl%>'" value=" 返回(C)"border="0">
        <% String back ="location.href='"+driverNameBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/>
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
       <td nowrap align="center" width=45><%if(loginBean.hasLimits(pageCode, op_add)){%><input name="image" class="img" type="image" title="新增(A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/><%}%></td>
      <td nowrap>驾驶员代码</td>
      <td nowrap>驾驶员姓名</td>
      <td nowrap>部门</td>
      <td nowrap>是否本公司</td>
      <td nowrap>备注</td>


    </tr>
     <%

       list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
        <td class="td"><input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <%if(loginBean.hasLimits(pageCode, op_delete)){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      <%}%></td>
       <td class="td" align="right" nowrap><%=list.getValue("driver_code")%></td>
       <td class="td" align="right" nowrap><%=list.getValue("driver_name")%></td>


      <td class="td" nowrap><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
      <%String isSelfCorp=list.getValue("isSelfCorp");
                 if (isSelfCorp.equals("1")){%>
                <td class="td" align="right">是</td>
                 <%}if (isSelfCorp.equals("0")){%>
                 <td class="td" align="right">否</td>
          <%}%>


      <td class="td" nowrap><%=list.getValue("memo")%></td>


    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
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
    <TABLE cellspacing=3 cellpadding=0 border=0><%driverNameBean.table.printWhereInfo(pageContext);%>
     </TABLE>
            <TR>
              <TD colspan="4" nowrap class="td" align="center"><%if(loginBean.hasLimits(pageCode, op_search)){%><INPUT class="button" onClick="sumitFixedQuery(<%=driverNameBean.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script='<%="sumitFixedQuery("+ driverNameBean.FIXED_SEARCH +",-1)"%>'/>
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
      <TD><iframe id="interframe1" src="" width="550" height="320" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(retu);%>
</body>
</html>