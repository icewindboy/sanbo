<%--生产工作能力列表--%><%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%><%String pageCode = "work_capacity" ;
  if(!loginBean.hasLimits("work_capacity", request, response))
    return;
  engine.erp.produce.B_WorkCapacity b_WorkCapacityBean  =  engine.erp.produce.B_WorkCapacity.getInstance(request);
  engine.project.LookUp firstkindBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_FIRST_KIND);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp workCenterBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_CENTER);
  String retu = b_WorkCapacityBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
   out.print(retu);
   return;
  }
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

<SCRIPT LANGUAGE="javascript">
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
  function showInterFrame(oper, rownum){
    var url = "work_capacityedit.jsp?operate="+oper+"&rownum="+rownum;
    document.all.interframe1.src = url;
    showFrame('detailDiv',true,"",true);
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
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>生产能力设置</TD>
  </TR>
</TABLE>
<%
  EngineDataSet list = b_WorkCapacityBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="90%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
    <td class="td" nowrap>
     <%String key = "55"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
     </td>
      <TD align="right"><%if(loginBean.hasLimits(pageCode,op_search)){%><INPUT class="button" onClick="showFixedQuery()" type="button" value=" 查询(Q)" name="Query" onKeyDown="return getNextElement();">
      <pc:shortcut key="q" script='showFixedQuery()'/><%}%>
  <%if(b_WorkCapacityBean.retuUrl!=null){%><input name="button2222232" type="button" align="Right"
  class="button" onClick="location.href='<%=b_WorkCapacityBean.retuUrl%>'" value=" 返回(C)"border="0">
  <% String back ="location.href='"+b_WorkCapacityBean.retuUrl+"'" ;%>
  <pc:shortcut key="c" script='<%=back%>'/><%}%></TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
    <td width="45" align="center"><%if(loginBean.hasLimits(pageCode, op_add)){%><input name="image" class="img" type="image" title="新增(A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/><%}%></td>
      <td>产品编码</td>
      <td>品名规格</td>
      <td>计量单位</td>
      <td>物资类别</td>
      <td>提前期</td>
      <td>设备数</td>
      <td>人工数</td>
      <td>日工时</td>
      <td>产量</td>
      <td>工作中心</td>
    </tr>
    <%prodBean.regData(list,"cpid");
      firstkindBean.regData(list,"wzlbid");
      workCenterBean.regData(list,"gzzxid");
      list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
     <td class="td"><input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <%if(loginBean.hasLimits(pageCode, op_delete)){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      <%}%></td>
       <%RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));%>
      <td class="td"><%=prodRow.get("cpbm")%></td>
      <td class="td"><%=prodRow.get("product")%></td>
      <td class="td" align="center"><%=prodRow.get("jldw")%></td>
      <td class="td" align="center"><%=firstkindBean.getLookupName(list.getValue("wzlbid"))%></td>
      <td class="td" align="right"><%=list.getValue("tqq")%></td>
      <td class="td" align="right"><%=list.getValue("sbs")%></td>
      <td class="td" align="right"><%=list.getValue("rgs")%></td>
      <td class="td" align="right"><%=list.getValue("rs")%></td>
      <td class="td" align="right"><%=list.getValue("cl")%></td>
      <td class="td"><%=workCenterBean.getLookupName(list.getValue("gzzxid"))%></td>
    </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
    function hide()
    {
      hideFrame('fixedQuery');
    }
</SCRIPT>
</form>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">

  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=4 cellpadding=0 border=0>
           <TR>
           <TD align="center" nowrap class="td">产品名称</TD>
               <td nowrap class="td"><input class="EDLine" style="WIDTH:130px" name="product" value='<%=b_WorkCapacityBean.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="sc_scnl$cpid" value="<%=b_WorkCapacityBean.getFixedQueryValue("cpid")%>"><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=sc_scnl$cpid&srcVar=product','fieldVar=cpid&fieldVar=product',fixedQueryform.sc_scnl$cpid.value)">
               <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="sc_scnl$cpid.value='';product.value='';">
            </td>
           </TR>
           <TR>
              <TD class="td" nowrap>产品编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="sc_scnl$scnlid$cpbm" name="sc_scnl$scnlid$cpbm" value='<%=b_WorkCapacityBean.getFixedQueryValue("sc_scnl$scnlid$cpbm")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>品名规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="sc_scnl$scnlid$product" name="sc_scnl$scnlid$product" value='<%=b_WorkCapacityBean.getFixedQueryValue("sc_scnl$scnlid$product")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
            <TR>
              <TD class="td" align="center" nowrap>物资类别</TD>
              <TD nowrap class="td"><pc:select name="sc_scnl$wzlbid" addNull="1" style="width:130">
                  <%=firstkindBean.getList(b_WorkCapacityBean.getFixedQueryValue("sc_scnl$wzlbid"))%></pc:select>
              </TD>
            </TR>
            <TR>
              <TD class="td" align="center" nowrap>工作中心</TD>
              <TD nowrap class="td"><pc:select name="sc_scnl$gzzxid" addNull="1" style="width:130">
                  <%=workCenterBean.getList(b_WorkCapacityBean.getFixedQueryValue("sc_scnl$gzzxid"))%></pc:select>
              </TD>
            </TR>
          </TABLE>
      <TR>
        <TD colspan="4" nowrap class="td" align="center"><%if(loginBean.hasLimits(pageCode, op_search)){%>
        <INPUT class="button" onClick="sumitFixedQuery(<%=b_WorkCapacityBean.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
           <pc:shortcut key="f" script='<%="sumitFixedQuery("+ b_WorkCapacityBean.FIXED_SEARCH +",-1)"%>'/>
          <%}%><INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X)" name="button2" onKeyDown="return getNextElement();">
           <pc:shortcut key="x" script='hide();'/>
        </TD>
      </TR>
    </TABLE>
  </DIV>
</form><%out.print(retu);%>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="300" height="380" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>