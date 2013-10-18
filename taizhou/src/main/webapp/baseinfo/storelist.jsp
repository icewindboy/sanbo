<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<%if(!loginBean.hasLimits("storelist", request, response))
    return;
  engine.erp.baseinfo.B_Store storeBean  =  engine.erp.baseinfo.B_Store.getInstance(request);
  String retu = storeBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
%>
<html>
<head>
<title></title>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<script LANGUAGE="javascript">
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
  function showInterFrame(oper, rownum)
  {
    var url = "storeedit.jsp?operate="+oper+"&rownum="+rownum;
    document.all.interframe1.src = url;
    showFrame('detailDiv',true,"",true);
  }

  function hideInterFrame()//隐藏FRAME
  {
    hideFrame('detailDiv');
    form1.submit();
  }
  function hideFrameNoFresh(){
    hideFrame('detailDiv');
  }
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>仓库列表</TD>
  </TR>
</TABLE>
<%out.print(retu);
  EngineDataSet list = storeBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="90%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
      <TD align="right"><%if(storeBean.retuUrl!=null){%><input name="button2222232" type="button" align="Right"
  class="button" onClick="location.href='<%=storeBean.retuUrl%>'" value=" 返回 "border="0"><%}%></TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>仓库编码</td>
      <td nowrap>仓库名称</td>
      <td nowrap>仓库全称</td>
      <td nowrap>仓库地址</td>
      <td nowrap>负责人</td>
      <td nowrap>所属部门</td>
      <td nowrap>仓库电话</td>
      <td nowrap>仓库传真</td>
      <td nowrap>备注</td>
      <td nowrap width=50 align="center"><input name="image" class="img" type="image" title="新增(ALT+A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
          <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/>
      </td>
    </tr>
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    	  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
    	  deptBean.regData(list, "deptid");    	  
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
      <td class="td" nowrap><%=list.getValue("ckbm")%></td>
      <td class="td" nowrap><%=list.getValue("ckmc")%></td>
      <td class="td" nowrap><%=list.getValue("ckqc")%></td>
      <td class="td" nowrap><%=list.getValue("ckdz")%></td>
      <td class="td" nowrap><%=list.getValue("hzr")%></td>
      <td class="td" nowrap><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
      <td class="td" nowrap><%=list.getValue("ckdh")%></td>
      <td class="td" nowrap><%=list.getValue("ckcz")%></td>
      <td class="td" nowrap><%=list.getValue("bz")%></td>
      <td class="td" nowrap width=50 align="center"><input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
        <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      </td>
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
    </tr>
    <%}%>
  </table>
  <script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="330" height="320" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>
