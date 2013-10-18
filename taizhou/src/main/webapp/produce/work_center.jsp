<%--生产工作中心列表--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%><%String pageCode = "work_center" ;
   if(!loginBean.hasLimits("work_center", request, response))
    return;
  engine.erp.produce.B_WorkCenter b_WorkCenterBean  =  engine.erp.produce.B_WorkCenter.getInstance(request);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  String retu = b_WorkCenterBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
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
   var url = "work_centeredit.jsp?operate="+oper+"&rownum="+rownum;
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
    <TD colspan="6" align="center" NOWRAP>工作中心设置</TD>
  </TR>
</TABLE>
<%
  EngineDataSet list = b_WorkCenterBean.getOneTable();
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
      <TD align="right"><%if(loginBean.hasLimits(pageCode, op_search)){%><INPUT class="button" onClick="showFixedQuery()" type="button" value=" 查询(Q)" name="Query" onKeyDown="return getNextElement();">
   <pc:shortcut key="q" script='showFixedQuery()'/><%}%>
  <%if(b_WorkCenterBean.retuUrl!=null){%><input name="button2222232" type="button" align="Right"
  class="button" onClick="location.href='<%=b_WorkCenterBean.retuUrl%>'" value=" 返回(C)" border="0">
  <% String back ="location.href='"+b_WorkCenterBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/><%}%></TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <COLGROUP valign="middle" span="5">
    <COLGROUP width=60 align="center">
    <tr class="tableTitle">
    <td width="45" align="center"><%if(loginBean.hasLimits(pageCode, op_add)){%><input name="image" class="img" type="image" title="新增(A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/><%}%></td>
      <td>工作中心编号</td>
      <td>工作中心名称</td>
      <td>所属部门</td>
      <td>是否关键中心</td>
      <td>设备数</td>
      <td>人工数</td>
      <td>利用率%</td>
    </tr>
    <%deptBean.regData(list,"deptid");
      list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
       <td class="td"><input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <%if(loginBean.hasLimits(pageCode, op_delete)){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      <%}%></td>
      <td class="td"><%=list.getValue("gzzxbh")%></td>
      <td class="td"><%=list.getValue("gzzxmc")%></td>
      <td class="td"><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
      <td class="td" align="center"><%=list.getValue("sfgjgzzx").equals("1")? "是" : "否"%></td>
      <td class="td"><%=list.getValue("sbs")%></td>
      <td class="td"><%=list.getValue("rgs")%></td>
      <td class="td"><%=list.getValue("lyl")%></td>
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
              <TD nowrap class="td">工作中心编号</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="gzzxbh" value='<%=b_WorkCenterBean.getFixedQueryValue("gzzxbh")%>' maxlength='10' >
                </TD>
            </TR>
             <TD nowrap class="td">工作中心名称</TD>
               <td nowrap class="td"><input class="edbox" style="WIDTH:130px" name="gzzxmc" value='<%=b_WorkCenterBean.getFixedQueryValue("gzzxmc")%>'>
            </td>
             <TR>
              <TD class="td" align="center" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:130">
         <%=deptBean.getList(b_WorkCenterBean.getFixedQueryValue("deptid"))%></pc:select>
           </TD>
            </TR>
            </TABLE>
             <TR>
              <TD align="center" nowrap class="td">关键中心查询<%String sfgjgzzx = b_WorkCenterBean.getFixedQueryValue("sfgjgzzx");%>
              <input type="radio" name="sfgjgzzx" value=""<%=sfgjgzzx.equals("") ? " checked" : ""%>>
               全部
               <input type="radio" name="sfgjgzzx" value="1"<%=sfgjgzzx.equals("1") ? " checked" : ""%>>
                是
              <input type="radio" name="sfgjgzzx" value="0"<%=sfgjgzzx.equals("0") ? " checked" : ""%>>
                否
              </td>
            </TR>
            <TR>
              <TD colspan="4" nowrap class="td" align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=b_WorkCenterBean.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script='<%="sumitFixedQuery("+ b_WorkCenterBean.FIXED_SEARCH +",-1)"%>'/>
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X)" name="button2" onKeyDown="return getNextElement();">
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
      <TD><iframe id="interframe1" src="" width="300" height="300" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>