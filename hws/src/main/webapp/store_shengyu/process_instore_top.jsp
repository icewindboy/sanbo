<%--��ӹ���ⵥ����--%>
<%@ page contentType="text/html; charset=gb2312"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal, java.util.ArrayList,engine.html.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%><%
  String pageCode = "process_instore_list";
    if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.store.shengyu.B_SelfGain selfGainBean = engine.erp.store.shengyu.B_SelfGain.getInstance(request);
  synchronized(selfGainBean){
  selfGainBean.isout ="1";
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp produceUseBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_USE);//LookUp��;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String loginId = selfGainBean.loginId;
  String SYS_APPROVE_ONLY_SELF =selfGainBean.SYS_APPROVE_ONLY_SELF;//�ύ�����Ƿ�ֻ���Ƶ��˿����ύ,1=���Ƶ��˿��ύ,0=��Ȩ���˿��ύ
  boolean isApproveOnly = SYS_APPROVE_ONLY_SELF.equals("1") ? true : false;//true���Ƶ��˿��ύ
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/frame.js"></script>
<script language="javascript">
function toDetail(){
  parent.location.href='process_instore_edit.jsp';
}
function sumitForm(oper, row)
{
  lockScreenToWait("������, ���Ժ�");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}

function showDetail(masterRow){
  selectRow();
  //lockScreenToWait("������, ���Ժ�");
  parent.bottom.location.href='process_instore_bottom.jsp?operate=<%=selfGainBean.SHOW_DETAIL%>&rownum='+masterRow;
  //unlockScreenWait();
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
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">��ӹ���ⵥ</TD>
  </TR></TABLE>
<%String retu = selfGainBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = selfGainBean.getMaterListTable();
  HtmlTableProducer table = selfGainBean.masterlistProducerProcess;
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="100%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "1111"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize()-8; String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" nowrap align="right">
      <%if(loginBean.hasLimits(pageCode, op_add)){%><input name="search3" type="button" class="button" onClick="sumitForm(<%=Operate.ADD%>,-1)" value="����(A)" onKeyDown="return getNextElement();">
          <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/>
         <%}%>
                   <input name="search2" type="button" class="button" title="����(ALT+Z)" value="����(Z)" onClick="if(confirm('�Ƿ�ȷ������'))sumitForm(<%=selfGainBean.RECODE_ACCOUNT%>)"  onKeyDown="return getNextElement();">
       <pc:shortcut key="z" script='<%="sumitForm("+ selfGainBean.RECODE_ACCOUNT +",-1)"%>'/>
       <%if(hasSearchLimit){%><input name="search2" type="button" class="button" title="��ѯ(ALT+Q)" value="��ѯ(Q)" onClick="showFixedQuery()"  onKeyDown="return getNextElement();">
       <pc:shortcut key="q" script="showFixedQuery()"/>
      <%}%>
        <%if(selfGainBean.retuUrl!=null){%><input name="button22" type="button" class="button" title="����(ALT+C)" value="����(C)" onClick="buttonEventC();" onKeyDown="return getNextElement();">
       <pc:shortcut key="c" script='buttonEventC()'/>
     <%}%>
    </td>
    </tr>
  </table>
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap align="center"><%--if(loginBean.hasLimits(pageCode, op_add)){%>
        <input name="image" class="img" type="image" title="����(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
      <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/><%}--%></td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%corpBean.regData(list,"dwtxid");
      BigDecimal t_zsl = new BigDecimal(0);
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++)   {
        list.goToRow(i);
        String zsl = list.getValue("totalNum");
        if(selfGainBean.isDouble(zsl))
          t_zsl = t_zsl.add(new BigDecimal(zsl));
        boolean isInit = false;
        String rowClass =list.getValue("state");
        if(rowClass.equals("0"))
          isInit = true;
        String creatorID = list.getValue("creatorID");
        //�ύ�����Ƿ�ֻ���Ƶ��˿����ύ
        //isApproveOnly = isApproveOnly && loginId.equals(zdrid);//���ʱֻ���Ƶ��˲ſ����ύ�������Ƶ��˵��ڵ�¼�˲���ʾ
        boolean isShow = isApproveOnly ? (loginId.equals(creatorID) && isInit) : isInit;//���ʱֻ���Ƶ��˲ſ����ύ�������Ƶ��˵��ڵ�¼�˲���ʾ
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        String sprid = list.getValue("approveID");
        String zt = list.getValue("state");
        boolean isCancel = zt.equals("1");
        isCancel = isCancel && loginId.equals(sprid);
    %>
    <%--02.17 12:04 ���� ����onClick�¼� yjg--%>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
      <td <%=rowClass%> align="center" nowrap><input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"�޸�" :"�鿴"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
      <%if(isShow){%><input name="image3" class="img" type="image" title='�ύ����'onClick="if(confirm('�Ƿ��ύ�����ü�¼��'))sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
      <%if(isCancel){%><input name="image3" class="img" type="image" title='ȡ������' onClick="if(confirm('�Ƿ�ȡ�������ü�¼��'))sumitForm(<%=selfGainBean.SELF_CANCEL_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%></td>
      <%--<td nowrap <%=rowClass%> align="left"><%=corpBean.getLookupName(list.getValue("dwtxid"))%></td>--%>
      <%table.printCells(pageContext, rowClass);%>
    </tr>
    <%  list.next();
      }
      i=count+1;
    %>
     <tr>
     <td class="tdTitle" nowrap>��ҳ�ϼ�</td>
     <td class="td" nowrap>&nbsp;</td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
     <td align="right" class="td"></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td><td class="td" nowrap></td>
      </tr>
       <tr>
     <td class="tdTitle" nowrap>�ܺϼ�</td>
     <td class="td" nowrap>&nbsp;</td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=selfGainBean.getZsl()%>' readonly></td>
     <td align="right" class="td"></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td><td class="td" nowrap></td>
      </tr>
      <%
      for(; i < iPage; i++){
        out.print("<tr>");
        table.printBlankCells(pageContext, "class=td");
        out.print("<td>&nbsp;</td></tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
<%if(!selfGainBean.masterIsAdd()){
    int row = selfGainBean.getSelectedRow();
    if(row >= 0)
      out.print("showSelected('tr_"+ row +"');");
  }%>
function sumitFixedQuery(oper)
{
  lockScreenToWait("������, ���Ժ�");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery(){
  <%if(hasSearchLimit){%>showFrame('fixedQuery', true, "", true);<%}%>
}
function sumitSearchFrame(oper)
{
  lockScreenToWait("������, ���Ժ�");
  searchform.operate.value = oper;
  searchform.submit();
}
function showSearchFrame(){
  hideFrame('fixedQuery');
  showFrame('searchframe', true, "", true);
}
//����
function buttonEventC()
{
  parent.location.href='<%=selfGainBean.retuUrl%>';
}
//�رղ�ѯС�Ӵ�
function buttonEventT()
{
  hideFrame('fixedQuery');
}
</SCRIPT>
<%if(hasSearchLimit){%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
         <%--selfGainBean.tableprocess.printWhereInfo(pageContext);--%>
            <%--TR>
              <TD class="td" nowrap>���ݺ�</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="sfdjdh" name="sfdjdh" value='<%=selfGainBean.getFixedQueryValue("sfdjdh")%>' maxlength='10' onKeyDown="return getNextElement();"></TD>
          <%--    <TD class="td" nowrap>����</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:160">
         <%=deptBean.getList(selfGainBean.getFixedQueryValue("deptid"))%></pc:select>
           </TD>
            <TR>
              <TD nowrap class="td">�շ�����</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="sfrq$a" value='<%=selfGainBean.getFixedQueryValue("sfrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=ѡ������ onClick="selectDate(sfrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="sfrq$b" style="WIDTH: 130px" name="sfrq$b" value='<%=selfGainBean.getFixedQueryValue("sfrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=ѡ������ onClick="selectDate(sfrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD nowrap class="td">��;</TD>
              <TD nowrap class="td"><pc:select name="ytid" addNull="1" style="width:160">
            <%=produceUseBean.getList(selfGainBean.getFixedQueryValue("ytid"))%></pc:select>
              </TD>
              <TD nowrap class="td">�ֿ�</TD>
              <TD nowrap class="td"><pc:select name="storeid" addNull="1" style="width:160">
            <%=storeBean.getList(selfGainBean.getFixedQueryValue("storeid"))%></pc:select>
           </TD>
            </TR>
           </TR>
            <TR>
              <TD align="center" nowrap class="td">��Ʒ����</TD>
               <td nowrap class="td" colspan="3"><input class="EDLine" style="WIDTH:330px" name="product" value='<%=selfGainBean.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="sfdjid" value="<%=selfGainBean.getFixedQueryValue("sfdjid")%>"><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=sfdjid&srcVar=product','fieldVar=cpid&fieldVar=product',fixedQueryform.sfdjid.value)">
               <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="sfdjid.value='';product.value='';">
            </td>
            </tr>
             <TR>
              <TD class="td" nowrap>��Ʒ����</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="sfdjid$cpbm" name="sfdjid$cpbm" value='<%=selfGainBean.getFixedQueryValue("sfdjid$cpbm")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>Ʒ�����</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="sfdjid$product" name="sfdjid$product" value='<%=selfGainBean.getFixedQueryValue("sfdjid$product")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>״̬</TD>
              <TD colspan="3" nowrap class="td">
                <%String zt = selfGainBean.getFixedQueryValue("zt");%>
                <input type="radio" name="zt" value=""<%=zt.equals("")?" checked" :""%>>
                ȫ��
                <input type="radio" name="zt" value="9"<%=zt.equals("9")?" checked" :""%>>
                ������
                <input type="radio" name="zt" value="0"<%=zt.equals("0")?" checked" :""%>>
                δ��
                <input type="radio" name="zt" value="1"<%=zt.equals("1")?" checked" :""%>>
                ����
            </TR--%>
                  <TR>
         <%selfGainBean.tableprocess.printWhereInfo(pageContext);%>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT type="button" class="button" title="��ѯ(ALT+F)" value="��ѯ(F)" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)"  name="button" onKeyDown="return getNextElement();">
                 <pc:shortcut key="f" script='<%="sumitFixedQuery("+Operate.FIXED_SEARCH+")"%>'/>
                <INPUT type="button" class="button" title="�ر�(ALT+T)" value="�ر�(T)" onClick="buttonEventT();"  name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="t" script='<%="buttonEventT()"%>'/>
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<%} out.print(retu);}%>
</body>
</html>