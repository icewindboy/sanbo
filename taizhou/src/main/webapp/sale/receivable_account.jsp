<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal"%>
<%!

  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";

  String pageCode = "receivable_account";

%>
<%
if(!loginBean.hasLimits("receivable_account", request, response))
    return;
  engine.erp.sale.B_ReceivableAccount b_ReceivableAccountBean = engine.erp.sale.B_ReceivableAccount.getInstance(request);
  engine.project.LookUp creditCardBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK_CREDIT_CARD);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_LIST);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp areaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_AREA);
  engine.project.LookUp personclassBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON_CLASS);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);//引用人员信息

  String retu = b_ReceivableAccountBean.doService(request, response);//location.href='baln.htm'
  String param = b_ReceivableAccountBean.XS_INIT_OVER;
%>
<%
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = b_ReceivableAccountBean.getDetailTable();

  String curUrl = request.getRequestURL().toString();
  boolean isCanADD =loginBean.hasLimits(pageCode, op_add) && param.equals("0") ;
  boolean isCanOVER =loginBean.hasLimits(pageCode, op_over) && param.equals("0") ;
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit) && param.equals("0") ;
  boolean isCanSearch =loginBean.hasLimits(pageCode, op_search);
  String edclass = isCanEdit?"class=edbox":"class=ednone";
  String readonly = isCanEdit?"":"readonly";
  String numClass =isCanEdit?"class='edFocused_r'":"class='ednone_r'";
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

  function showFixedQuery()
   {
    showFrame('fixedQuery',true,"",true);
   }

  function hideFrameNoFresh()
  {
    hideFrame('interframe1');
  }
//客户编码
function custCodeSelect(obj)
{
   //CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',fixedQueryform.dwtxid.value)
   // CustomerCodeChange('2',document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
  CustCodeChange(document.all['prod'],obj.form.name,'srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',obj.value);
}
//客户名称
function custNameSelect(obj)
{
  //CustomerNameChange('2',document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
//客户名称
function customerNameSelect(obj,i)
{
  //CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm_'+i+'&srcVar=dwtxid_'+i+'&srcVar=dwmc_'+i+'&srcVar=personid_'+i+'&srcVar=xm_'+i,'fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=personid&fieldVar=xm',obj.value,'sumitForm(<%=b_ReceivableAccountBean.DETAIL_CHANGE%>,'+i+')');
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm_'+i+'&srcVar=dwtxid_'+i+'&srcVar=dwmc_'+i+'&srcVar=personid_'+i+'&srcVar=xm_'+i,'fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=personid&fieldVar=xm',obj.value,'');
}
//客户编码
function customerCodeSelect(obj,i)
{
  //CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm_'+i+'&srcVar=dwtxid_'+i+'&srcVar=dwmc_'+i+'&srcVar=personid_'+i+'&srcVar=xm_'+i,'fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=personid&fieldVar=xm',obj.value,'sumitForm(<%=b_ReceivableAccountBean.DETAIL_CHANGE%>,'+i+')');
    CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm_'+i+'&srcVar=dwtxid_'+i+'&srcVar=dwmc_'+i+'&srcVar=personid_'+i+'&srcVar=xm_'+i,'fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=personid&fieldVar=xm',obj.value,'');
}
function customerSelect(i)
{
  //CustSingleSelect('form1','srcVar=dwtxid_'+i+'&srcVar=dwdm_'+i+'&srcVar=dwmc_'+i+'&srcVar=personid_'+i+'&srcVar=xm_'+i,'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc&fieldVar=personid&fieldVar=xm','','sumitForm(<%=b_ReceivableAccountBean.DETAIL_CHANGE%>,'+i+')');
   //CustSingleSelect('form1','srcVar=dwtxid_'+i+'&srcVar=dwdm_'+i+'&srcVar=dwmc_'+i+'&srcVar=personid_'+i+'&srcVar=xm_'+i+'&srcVar=areacode_'+i,'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc&fieldVar=personid&fieldVar=xm&fieldVar=areacode');
   CustSingleSelect('form1','srcVar=dwtxid_'+i+'&srcVar=dwdm_'+i+'&srcVar=dwmc_'+i+'&srcVar=personid_'+i+'&srcVar=xm_'+i,'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc&fieldVar=personid&fieldVar=xm');
}
function showInterFrame(oper, rownum)
{
  var url = "saler_prix_set_edit.jsp?operate="+oper+"&rownum="+rownum;
  document.all.interframe1.src = url;
  showFrame('detailDiv',true,"",true);
}
function showAddFrame()
 {
  showFrame('addframe',true,"",true);
  document.all.dqhtext.style.display = 'block';
  document.all.dwdmtext.style.display = 'none';
   }
function hideFrameNoFresh(){
  hideFrame('detailDiv');
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
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD nowrap align="center">应收帐款初始化</TD>
</TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum"  VALUE="">
  <TABLE width="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
     <td class="td" nowrap>
     <%
         String key = "ppdfsgg";
         pageContext.setAttribute(key, list);
         int iPage = loginBean.getPageSize();
         String pageSize = ""+iPage;
         %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
     </td>
       <TD align="right">
        <%if(isCanEdit){
           String confirm = "if(confirm('是否真的要完成初始化，初始化后您将无法做任何改动？')) sumitForm("+b_ReceivableAccountBean.OVER+");";
           String sav = "sumitForm("+Operate.POST+");";
         %>
        <%if(isCanADD){%>
        <input name="button6" type="button" width=60 class="button" style="width:80" onClick="showAddFrame();" value="批量增加(A)">
        <%}%>
        <%if(isCanOVER){%>
        <input name="button" type="button" width=60 class="button" style="width:100" onClick="if(confirm('是否真的要完成初始化，初始化后您将无法做任何改动？')) sumitForm(<%=b_ReceivableAccountBean.OVER%>);" value="完成初始化(C)">
        <%}%>
        <input name="button" type="button" class="button" style="width:50" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
         
        <%}%>
        <%if(isCanSearch){%>
        <INPUT class="button" onClick="showFixedQuery()"style="width:50" type="button" value="查询(Q) " name="Query" onKeyDown="return getNextElement();">
         
        <%}%>
        <% String ret = "location.href='"+b_ReceivableAccountBean.retuUrl+"'";%>
        <input name="button2" style="width:60" type="button" align="Right" class="button" onClick="location.href='<%=b_ReceivableAccountBean.retuUrl%>'" value=" 返回(N) "border="0">
        
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap  width=100>
         <%if(isCanADD){
           String add = "sumitForm("+Operate.ADD+",-1)";
         %>
       <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      
         <%}%>
       </td>
       <td nowrap>地区编码</td>
       <td nowrap>地区</td>
       <td nowrap>单位编码</td>
       <td nowrap>单位名称</td>
       <td nowrap>客户类型</td>
       <td nowrap>应收款</td>
       <td nowrap>业务员</td>
    </tr>
     <%
       corpBean.regData(list,"dwtxid");
       int i=0;
       RowMap[] rows = b_ReceivableAccountBean.getDetailRowinfos();
       int count = rows.length;//行数
       for(; i<rows.length; i++)
       {
         RowMap row = rows[i];
         RowMap corprow = corpBean.getLookupRow(row.get("dwtxid"));
         RowMap corparearow = areaBean.getLookupRow(corprow.get("dqh"));
         String personid = row.get("personid");
     %>
    <tr onclick="selectRow()">
    <td class="td" width="10%">
      <%if(isCanEdit||isCanADD){%>
      <img style='cursor:hand' src='../images/view.gif' border=0 onClick="customerSelect(<%=i%>)">
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=i%>)" src="../images/del.gif" border="0">
       <%}%>
      <%if(isCanADD){%><input name="image" class="img" type="image" title="复制当前行" onClick=" sumitForm(<%=b_ReceivableAccountBean.copy%>,<%=i%>)" src="../images/copyadd.gif" border="0">
     <%}%></td>
    <td class="td" nowrap ><INPUT TYPE="TEXT" NAME="areacode_<%=i%>" VALUE="<%=corparearow.get("areacode")%>" style="width:100%"  class="ednone_r"  onKeyDown="return getNextElement();" readonly></td>
    <td class="td" nowrap ><%=corprow.get("dqmc")%></td>
    <td class="td" nowrap ><INPUT TYPE="TEXT" <%=edclass%> <%=readonly%> NAME="dwdm_<%=i%>" VALUE="<%=corprow.get("dwdm")%>" style="width:100%"  class="edFocused_r"  onKeyDown="return getNextElement();"  onchange="customerCodeSelect(this,<%=i%>)"  ></td>
    <td class="td" nowrap ><INPUT TYPE="text" <%=edclass%> <%=readonly%> align="left" NAME="dwmc_<%=i%>" VALUE="<%=corpBean.getLookupName(row.get("dwtxid"))%>" style="width:100%"   onchange="customerNameSelect(this,<%=i%>)"  class='edFocused' >
    <INPUT TYPE="hidden" NAME="dwtxid_<%=i%>" VALUE="<%=row.get("dwtxid")%>" style="width:50"  class="edbox"  onKeyDown="return getNextElement();"></td>
    <td noWrap class="td" width="">
        <%if(isCanEdit){%>
         <pc:select name='<%="khlx_"+i%>' style="width:100%" value='<%=row.get("khlx")%>'>
          <pc:option value=''></pc:option>
          <pc:option value='A'>A</pc:option>
          <pc:option value='C'>C</pc:option>
         </pc:select>
         <%}else{%><%=row.get("khlx")%><%}%>
    </td>
    <td class="td" nowrap >
       <INPUT TYPE="TEXT" <%=numClass%> <%=readonly%> NAME="ysk_<%=i%>" VALUE="<%=row.get("ysk")%>" style="width:100%" MAXLENGTH="<%=list.getColumn("ysk").getPrecision()%>"  <%=readonly%>>
    </td>
    <td class="td" nowrap><INPUT TYPE="hidden" NAME="personid_<%=i%>" VALUE="<%=personid%>" style="width:80"  class="ednone"  onKeyDown="return getNextElement();" readonly><INPUT TYPE="TEXT" NAME="xm_<%=i%>" VALUE="<%=personBean.getLookupName(personid)%>" style="width:80"  class="ednone"  onKeyDown="return getNextElement();" readonly></td>
    </tr>
    <%
      }
      i=count+1;
    %>
      <tr>
      <td class="tdTitle" nowrap>总合计</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=b_ReceivableAccountBean.getZysk()%>' readonly></td>
      <td class="td" nowrap></td>
      </tr>
    <%
      for(; i < iPage; i++){
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
    </tr>
    <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;">
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
   <div class="queryPop" id="fixedQuery" name="fixedQuery">
   <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
        <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD align="center" nowrap class="td">往来单位</TD>
              <td nowrap class="td" colspan=3>
              <input type="text" class="edbox" style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=b_ReceivableAccountBean.getFixedQueryValue("dwdm")%>' onchange="custCodeSelect(this)" >
              <input type="text" name="dwmc"  style="width:260" value='<%=b_ReceivableAccountBean.getFixedQueryValue("dwmc")%>' class="edbox"  onchange="custNameSelect(this)" >
              <INPUT TYPE="HIDDEN" NAME="dwtxid" value=''>
              <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',fixedQueryform.dwtxid.value)">
              <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value='';">
              </td>
             </tr>
             <tr>
             <td align="center" nowrap class="td">应收款</td>
              <td class="td" nowrap>
              <INPUT class="edbox"  style="WIDTH: 60px" name="ysk$a" value='<%=b_ReceivableAccountBean.getFixedQueryValue("ysk$a")%>'  onKeyDown="return getNextElement();">--
              <INPUT class="edbox"  style="WIDTH: 60px" name="ysk$b" value='<%=b_ReceivableAccountBean.getFixedQueryValue("ysk$b")%>'  onKeyDown="return getNextElement();">
             </td>
              <td align="center" nowrap class="td">地区号</td>
              <td class="td" nowrap>
              <INPUT class="edbox"  style="WIDTH:60px" name="areacode$a" value='<%=b_ReceivableAccountBean.getFixedQueryValue("areacode$a")%>'  onKeyDown="return getNextElement();">--
              <INPUT class="edbox"  style="WIDTH:60px" name="areacode$b" value='<%=b_ReceivableAccountBean.getFixedQueryValue("areacode$b")%>'  onKeyDown="return getNextElement();">
              </td>
              </TR>
             <tr>
             <td align="center" nowrap class="td">业务员模糊</td>
              <td class="td" nowrap>
              <INPUT class="edbox"  style="WIDTH:120px" name="xm" value='<%=b_ReceivableAccountBean.getFixedQueryValue("xm")%>'  onKeyDown="return getNextElement();">
             </td>
              <TD class="td" nowrap>业务员</TD>
              <TD class="td" nowrap>
                <pc:select name="personid" addNull="1" style="width:100">
                  <%=personBean.getList(b_ReceivableAccountBean.getFixedQueryValue("personid"))%>
                </pc:select>
               </TD>
             </tr>
             <td align="center" nowrap class="td">客户类型</td>
              <td class="td" nowrap>
              <INPUT class="edbox"  style="WIDTH:120px" name="khlx" value='<%=b_ReceivableAccountBean.getFixedQueryValue("khlx")%>'  onKeyDown="return getNextElement();">
             </td>
             </TR>
              <TR>
             </TABLE>
            <TR>
              <TD colspan="4" nowrap class="td" align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=b_ReceivableAccountBean.FIXED_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
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
              <TD CLASS="td" id="dqhtext" name="dqhtext" style="display:block">
                <pc:select name="batchdqh" style="width:170">
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
        <TD colspan="4" nowrap class="td" align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=b_ReceivableAccountBean.BATCH_ADD%>)" type="button" value=" 确定 " name="button" onKeyDown="return getNextElement();">
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