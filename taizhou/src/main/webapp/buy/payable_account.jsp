<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal"%>
<%!

  String op_add    = "op_add";
  String op_delete = "op_delete";
  String ap_all   = "ap_all";
  String op_search = "op_search";
  String pageCode = "payable_account";

%>
<%
if(!loginBean.hasLimits("payable_account", request, response))
    return;

  engine.erp.buy.B_PayableAccount b_PayableAccountBean = engine.erp.buy.B_PayableAccount.getInstance(request);

  engine.project.LookUp creditCardBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK_CREDIT_CARD);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_LIST);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp areaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_AREA);
  engine.project.LookUp personclassBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON_CLASS);
  String retu = b_PayableAccountBean.doService(request, response);//location.href='baln.htm'
  String param = b_PayableAccountBean.BUY_INIT_OVER;
%>
<%
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = b_PayableAccountBean.getDetailTable();
  String curUrl = request.getRequestURL().toString();
  boolean isCanEdit =loginBean.hasLimits(pageCode, ap_all) && param.equals("0") ;
  boolean isCanSearch =loginBean.hasLimits(pageCode, ap_all);
  String edClass = isCanEdit?"class=edbox":"class=ednone";
  String readonly = isCanEdit?"":"readonly";
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
  function hideInterFrame()//隐藏FRAME
  {
    hideFrame('interframe1');
    form1.submit();
  }

  function sumitFixedQuery(oper)
 {
   lockScreenToWait("处理中, 请稍候！");
   fixedQueryform.operate.value = oper;
   fixedQueryform.submit();
  }
  function sumitAddFrame(oper)
 {
   dqh = document.all.dqh.value;
   dwdm_a = document.all.dwdm_a.value;
   dwdm_b = document.all.dwdm_b.value;
   if(dqh=='' && dwdm_a=='' && dwdm_b==''){
     alert('请输入增加条件')
   }
   else{
     lockScreenToWait("处理中, 请稍候！");
     fixedQueryform.operate.value = oper;
     fixedQueryform.submit();
   }
  }
  function corpQueryCodeSelect(obj,srcVars)
  {
    ProvideCodeChange(document.all['prod'], obj.form.name, srcVars,
                      'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc',obj.value);
  }
  function corpQueryNameSelect(obj,srcVars)
  {
    ProvideNameChange(document.all['prod'], obj.form.name, srcVars,
                      'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
  }

  function showFixedQuery()
   {
    showFrame('fixedQuery',true,"",true);
   }
   function showAddFrame()
    {
     showFrame('addframe',true,"",true);
     document.all.dqhtext.style.display = 'block';
     document.all.dwdmtext.style.display = 'none';
   }
  function hideFrameNoFresh()
  {
    hideFrame('interframe1');
  }
  function corpCodeSelect(obj,i)
  {
    ProvideCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid_'+i+'&srcVar=dwdm_'+i+'&srcVar=dwmc_'+i+'&srcVar=areacode_'+i+'&srcVar=dqmc_'+i,
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc&fieldVar=areacode&fieldVar=dqmc', obj.value);
  }
  function corpNameSelect(obj,i)
  {
    ProvideNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid_'+i+'&srcVar=dwdm_'+i+'&srcVar=dwmc_'+i+'&srcVar=areacode_'+i+'&srcVar=dqmc_'+i,
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc&fieldVar=areacode&fieldVar=dqmc', obj.value);
 }
 function showText(obj){
  if(obj=='1'){
   document.all.dqhtext.style.display = 'block';
   document.all.dwdmtext.style.display = 'none';
  }
  else if(obj='2'){
    document.all.dwdmtext.style.display = 'block';
    document.all.dqhtext.style.display = 'none';
  }
}
</script>

<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD nowrap align="center">应付帐款初始化</TD>
</TR>
</TABLE>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum"  VALUE="">
  <TABLE width="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>

     <td class="td" nowrap>
           <td class="td" nowrap>
      <%String key = "ppd"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
     </td>
       <TD align="right">
        <%if(isCanEdit){%><input name="button6" type="button"  class="button" onClick="showAddFrame();" value="批量增加 ">
           <input name="button" type="button" class="button" onClick="if(confirm('是否真的要完成初始化，初始化你将无法做任何改动？')) sumitForm(<%=b_PayableAccountBean.OVER%>);" value="完成初始化 "><%}%>
            <%if(isCanEdit){%><input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存 "><%}%>
        <%if(isCanSearch){%><INPUT class="button" onClick="showFixedQuery()" type="button" value=" 查询 " name="Query" onKeyDown="return getNextElement();"><%}%>
        <input name="button2" type="button" align="Right" class="button" onClick="location.href='<%=b_PayableAccountBean.retuUrl%>'" value=" 返回 "border="0">
      </TD>
    </TR>
     <%--TR><TD align="right"><%if(isCanEdit){%><input name="button" type="button" width=60 class="button" onClick="sumitForm(<%=b_PayableAccountBean.copy%>);" value="复制一行 "><%}%></TR--%>
  </TABLE>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
       <td height='20' align="center" nowrap>
       <%if(isCanEdit){%>
       <input name="image" class="img" type="image" title="新增" onClick="sumitForm(<%=Operate.ADD%>)" src="../images/add_big.gif" border="0">
       <%}%>
      </td>
      <td nowrap>地区号</td>
      <td nowrap>地区名称</td>
      <td nowrap>单位代码</td>
      <td nowrap>单位名称</td>
      <td nowrap>客户类型</td>
      <td nowrap>应付款</td>
    </tr>

     <%corpBean.regData(list,"dwtxid");
       int i=0;
      RowMap[] rows = b_PayableAccountBean.getDetailRowinfos();
      for(; i<rows.length; i++)
      {
        RowMap row = rows[i];
        String dwtxid = row.get("dwtxid");
        RowMap corpRow = corpBean.getLookupRow(dwtxid);
        RowMap areaRow = areaBean.getLookupRow(corpRow.get("dqh"));
     %>
    <tr>
     <td class="td"><%if(isCanEdit){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=i%>)" src="../images/del.gif" border="0" onKeyDown="return getNextElement();">
      <input name="image" class="img" type="image" src='../images/view.gif' border=0 title="单选供应商" onClick="ProvideSingleSelect('form1','srcVar=dwtxid_<%=i%>&srcVar=dwmc_<%=i%>&srcVar=dwdm_<%=i%>&srcVar=areacode_<%=i%>&srcVar=dqmc_<%=i%>','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm&fieldVar=areacode&fieldVar=dqmc',form1.dwtxid_<%=i%>.value);" onKeyDown="return getNextElement();">
       <input name="image" class="img" type="image" title="复制当前行" onClick=" sumitForm(<%=b_PayableAccountBean.copy%>,<%=i%>)" src="../images/copyadd.gif" border="0" onKeyDown="return getNextElement();"><%}%></td>
      <td class="td" nowrap><input type="text" name="areacode_<%=i%>" value='<%=areaRow.get("areacode")%>'  class="ednone" onKeyDown="return getNextElement();" readonly></td>
     <td class="td" nowrap><input type="text" name="dqmc_<%=i%>" value='<%=areaRow.get("dqmc")%>' class="ednone" onKeyDown="return getNextElement();" readonly></td>
     <td class="td" nowrap align="left"><input type="text" name="dwdm_<%=i%>" value='<%=corpRow.get("dwdm")%>' <%=edClass%> onKeyDown="return getNextElement();" onchange="corpCodeSelect(this,<%=i%>);" <%=readonly%>></td>
      <td class="td" nowrap align="left" ><input type="text" name="dwmc_<%=i%>" value='<%=corpRow.get("dwmc")%>' <%=edClass%> onKeyDown="return getNextElement();" onchange="corpNameSelect(this,<%=i%>);" <%=readonly%>>
      <INPUT TYPE="hidden" align="left" NAME="dwtxid_<%=i%>" VALUE="<%=row.get("dwtxid")%>"  MAXLENGTH="<%=list.getColumn("dwtxid").getPrecision()%>" class='ednone_r' readonly></td>
      <td noWrap class="td">
     <%if(isCanEdit){%>
        <pc:select name='<%="khlx_"+i%>' style="width:80" value='<%=row.get("khlx")%>'>
          <pc:option value=''></pc:option>
          <pc:option value='A'>A</pc:option>
          <pc:option value='C'>C</pc:option>
         </pc:select>
     <%}else{%><%=row.get("khlx")%><%}%>
       </td>
      <td class="td" nowrap align="right"><INPUT TYPE="TEXT" NAME="yfk_<%=i%>" VALUE="<%=row.get("yfk")%>"  MAXLENGTH="<%=list.getColumn("yfk").getPrecision()%>" onchange="checkYfk(<%=i%>);" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();"></td>
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
    </tr>
    <%}%>
  </table>
</form>
<script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
<%=b_PayableAccountBean.adjustInputSize(new String[]{"areacode", "dqmc","dwdm", "dwmc", "yfk"}, "form1", rows.length)%>
	function checkYfk(i)
        {
          var yfkObj = document.all['yfk_'+i];
          if(isNaN(yfkObj.value)){
            alert("输入的数量非法");
            yfkObj.focus();
            return;
          }
        }
            </script>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;">
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
   <div class="queryPop" id="fixedQuery" name="fixedQuery">
   <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0><%b_PayableAccountBean.table.printWhereInfo(pageContext);%>
                <%--TD align="center" nowrap class="td">部门</TD>
                  <td class="td" nowrap>
                  <pc:select name="deptid"  style="width:150" addNull="1">
                  <%=deptBean.getList(b_PayableAccountBean.getFixedQueryValue("deptid"))%>
                  </pc:select>
                  </td--%>
                  <%--td align="center" nowrap class="td">类别</td>
                  <td class="td" nowrap>
                  <pc:select name="lb"  style="width:150" addNull="1" >
                 <%=personclassBean.getList(b_PayableAccountBean.getFixedQueryValue("lb"))%>
                 </pc:select>
                  </td>
             </TR>
             <TR>
                  <td align="center" nowrap class="td">信用卡名称</td>
                  <td class="td" nowrap >
                  <pc:select name="xykID"  style="width:150" addNull="1" ><%=creditCardBean.getList(b_PayableAccountBean.getFixedQueryValue("xykID"))%></pc:select>
                  </td>
                  <td align="center" nowrap class="td">储蓄卡帐号</td>
                  <td class="td" nowrap>
                    <INPUT class="edbox" style="WIDTH: 150" name="ygxykh" value='<%=b_PayableAccountBean.getFixedQueryValue("ygxykh")%>' onKeyDown="return getNextElement();">
                  </td--%>
             </TR>
            </TABLE>
            <TR>
              <TD colspan="4" nowrap class="td" align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=b_PayableAccountBean.FIXED_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
              </TD>
            </TR>
    </TABLE>
  </DIV>
  <div class="queryPop" id="addframe" name="addframe" >
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('addframe')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD colspan="2" NOWRAP CLASS="td"><b>批量增加单位</b></TD>
            </TR>
            <TR>
              <TD class="td"><INPUT TYPE="RADIO" NAME="scope" VALUE="1" onClick="showText(this.value);" checked>
                按地区增加</TD>
              <TD CLASS="td" id="dqhtext" name="dqhtext" style="display:block"><pc:select name="dqh" style="width:170">
                <%=areaBean.getList()%></pc:select> </TD>
            </TR>
            <TR>
              <TD colspan="2" CLASS="td"> <INPUT TYPE="RADIO" NAME="scope" VALUE="2" onClick="showText(this.value);">
                指定单位代码范围</TD>
            </TR>
            <TR id="dwdmtext" style="display:block">
              <TD colspan="2" CLASS="td"><input type="text" name="dwdm_a" style"50" class="edbox">
                至
                <input type="text" name="dwdm_b" style"50" class="edbox"></TD>
            </TR>
          </TABLE>
      <TR>
        <TD colspan="4" nowrap class="td" align="center"><INPUT class="button" onClick="sumitAddFrame(<%=b_PayableAccountBean.BATCH_ADD%>)" type="button" value=" 确定 " name="button" onKeyDown="return getNextElement();">
          <INPUT class="button" onClick="hideFrame('addframe')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
        </TD>
      </TR>
      </TD>
      </TR>
    </TABLE>
  </DIV>
</form><%out.print(retu);%>
</body>
</html>
