<%--����ƿ�ⵥ�б�--%>
<%
/**
 * 02.28 19:45 ����  ������������������ͼ��.��Ϊԭ����С�ĸĳ����ʱ��ȥ����. �����ڼ�����. yjg
 */
%>
<%@ page contentType="text/html; charset=gb2312"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%>
<%@ page import="java.math.BigDecimal"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%><%
  engine.erp.store.shengyu.B_MoveStore moveStoreBean = engine.erp.store.shengyu.B_MoveStore.getInstance(request);
  moveStoreBean.djxz = 8;
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  engine.project.LookUp moveBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_MOVE);
  //ϵͳ������SYS_APPROVE_ONLY_SELF �����ж�1=���ƶ��˿��ύ,0=��Ȩ���˿��ύ
  boolean isZdridApprove = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF").equals("1")?true:false;
  String pageCode = "move_store_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String loginId = moveStoreBean.loginId;
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
  parent.location.href='move_store_edit.jsp';
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
  lockScreenToWait("������, ���Ժ�");
  parent.bottom.location.href='move_store_bottom.jsp?operate=<%=moveStoreBean.SHOW_DETAIL%>&rownum='+masterRow;
  unlockScreenWait();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">�ƿⵥ�б�</TD>
  </TR></TABLE>
<%String retu = moveStoreBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = moveStoreBean.getMaterTable();
  HtmlTableProducer table = moveStoreBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="100%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "1111"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize()-6; String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" nowrap align="right">
       <%if(loginBean.hasLimits(pageCode, op_add)){%>
         <%--02.28 19:45 ����  ������������������ͼ��.��Ϊԭ����С�ĸĳ����ʱ��ȥ����. �����ڼ�����. yjg--%>
         <input name="add" class="button" type="button" title="����(ALT+A)" value="�����ƿⵥ(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)">
          <pc:shortcut key="a" script='<%="sumitForm("+Operate.ADD+", -1)"%>'/>
      <%}%>
      <input name="search2" type="button" class="button" title="����(ALT+Z)" value="����(Z)" onClick="if(confirm('�Ƿ�ȷ������'))sumitForm(<%=moveStoreBean.RECODE_ACCOUNT%>)"  onKeyDown="return getNextElement();">
       <pc:shortcut key="z" script='<%="sumitForm("+ moveStoreBean.RECODE_ACCOUNT +",-1)"%>'/>
       <%if(hasSearchLimit){%>
        <input name="search2" type="button" class="button" title="��ѯ(ALT+Q)" value="��ѯ(Q)" onClick="showFixedQuery()"  onKeyDown="return getNextElement();">
         <pc:shortcut key="q" script="showFixedQuery()"/>
      <%}%>
        <%if(moveStoreBean.retuUrl!=null){%>
         <input name="button22" type="button" class="button" title="����(ALT+C)" value="����(C)" onClick="buttonEventC()" onKeyDown="return getNextElement();">
        <pc:shortcut key="c" script='buttonEventC()'/>
      <%}%></td>
    </tr>
  </table>
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap align="center"></td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%
      BigDecimal tt_zsl = new BigDecimal(0);
      String zsl = "0";
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++)   {
        list.goToRow(i);
        zsl = list.getValue("zsl");
        if(moveStoreBean.isDouble(zsl))
          tt_zsl = tt_zsl.add(new BigDecimal(zsl));
        boolean isInit = false;
        String rowClass =list.getValue("zt");
        if(rowClass.equals("0"))
          isInit = true;
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        String zt = list.getValue("zt");
        String sprid = list.getValue("sprid");
        boolean isCancel = zt.equals("1");
        isCancel = isCancel && loginId.equals(sprid);
        //�Ƿ��Ƶ��˿����ύ������.
        isInit = isZdridApprove?(loginId.equals(list.getValue("zdrid"))&&isInit):isInit;
    %>
    <%--02.17 20:49 ����onclick�¼� yjg--%>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
      <td <%=rowClass%> align="center" nowrap><input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"�޸�" :"�鿴"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
      <%if(isInit){%><input name="image3" class="img" type="image" title='�ύ����'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
      <%if(isCancel){%><input name="image3" class="img" type="image" title='ȡ������' onClick="if(confirm('�Ƿ�ȡ�������ü�¼��'))sumitForm(<%=moveStoreBean.CANCEL_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%></td>
      <%table.printCells(pageContext, rowClass);%>
    </tr>
    <%  list.next();
      }
     %>
    <tr><td class="tdTitle" nowrap>��ҳ�ϼ�</td>
       <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td>
<td class=td>&nbsp;</td><td class=td align=right><%=tt_zsl%></td>
     <td class=td align=right></td>
     <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td>
   </tr>
   <tr><td class="tdTitle" nowrap>�ܺϼ�</td>
       <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td>
<td class=td>&nbsp;</td><td class=td align=right><%=moveStoreBean.totalzsl%></td>
      <td class=td align=right></td>
     <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td>
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
    <%if(!moveStoreBean.masterIsAdd()){
    int row = moveStoreBean.getSelectedRow();
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
   parent.location.href='<%=moveStoreBean.retuUrl%>';
 }
  //�رղ�ѯС�Ӵ�
  function buttonEventT()
  {
    hideFrame('fixedQuery');
  }
  //��Ʒ����ѡ��
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=sfdjid$cpbm&srcVar=cpmc','fieldVar=cpbm&fieldVar=product', obj.value);
}
//��Ʒ���Ƹı�ʱ��ѡ��
function productNameSelect(obj,i)
{
    ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=sfdjid$cpbm&srcVar=cpmc','fieldVar=cpbm&fieldVar=product',obj.value);
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
            <TR>
              <TD class="td" nowrap>���ݺ�</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="sfdjdh" name="sfdjdh" value='<%=moveStoreBean.getFixedQueryValue("sfdjdh")%>' onKeyDown="return getNextElement();"></TD>
              <TD nowrap class="td">�����ֿ�</TD>
              <TD nowrap class="td"><pc:select name="storeid" addNull="1" style="width:160">
                <%=storeBean.getList(moveStoreBean.getFixedQueryValue("storeid"))%></pc:select>
              </TD>
            </TR>
            <TR>
              <TD nowrap class="td">�������</TD>
              <TD nowrap class="td"><pc:select name="sfdjlbid" addNull="1" style="width:160">
                <%=moveBean.getList(moveStoreBean.getFixedQueryValue("sfdjlbid"))%></pc:select>
              </TD>
              <TD class="td" nowrap>����</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:160">
                <%=deptBean.getList(moveStoreBean.getFixedQueryValue("deptid"))%></pc:select>
              </TD>
            </TR>
            <TR>
              <TD nowrap class="td">�շ�����</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="sfrq$a" value='<%=moveStoreBean.getFixedQueryValue("sfrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=ѡ������ onClick="selectDate(sfrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="sfrq$b" style="WIDTH: 130px" name="sfrq$b" value='<%=moveStoreBean.getFixedQueryValue("sfrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=ѡ������ onClick="selectDate(sfrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD nowrap class="td">������</TD>
               <td nowrap class="td" colspan="3">
                 <input class="edbox" id="jsr" name="jsr" value='<%=moveStoreBean.getFixedQueryValue("jsr")%>' onKeyDown="return getNextElement();">
               </td>
            </tr>
            <TR>
              <TD class="td" nowrap>Ʒ��</TD>
              <TD nowrap class="td">
                <input type="text" class="edbox" style="width:70"  name="sfdjid$pm" onKeyDown="return getNextElement();" value='<%=moveStoreBean.getFixedQueryValue("sfdjid$pm")%>'>
                ���
                <input type="text" class="edbox" style="width:70"  name="sfdjid$gg" onKeyDown="return getNextElement();" value='<%=moveStoreBean.getFixedQueryValue("sfdjid$gg")%>'>
              </TD>
            </TR>
            <TR>
             <TD nowrap class="td">��Ʒ����</TD>
              <TD class="td" nowrap colspan="3"><INPUT class="edbox" style="WIDTH:70" id="sfdjid$cpbm" name="sfdjid$cpbm" value='<%=moveStoreBean.getFixedQueryValue("sfdjid$cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this)">
              <INPUT class="edbox" style="WIDTH:130" id="cpmc" name="cpmc" value='<%=moveStoreBean.getFixedQueryValue("cpmc")%>' onKeyDown="return getNextElement();" onchange="productNameSelect(this)">
              <INPUT TYPE="HIDDEN" NAME="sfdjid" value="<%=moveStoreBean.getFixedQueryValue("sfdjid")%>"><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=sfdjid$cpbm&srcVar=cpmc','fieldVar=cpbm&fieldVar=product',fixedQueryform.sfdjid.value)">
               <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="sfdjid.value='';sfdjid$cpbm.value='';cpmc.value='';">
              </TD>
            </TR>
            <TR>
              <TD class="td" nowrap>״̬</TD>
              <TD colspan="3" nowrap class="td">
                <%String zt = moveStoreBean.getFixedQueryValue("zt");%>
                <input type="radio" name="zt" value=""<%=zt.equals("")?" checked" :""%>>
                ȫ��
                <input type="radio" name="zt" value="9"<%=zt.equals("9")?" checked" :""%>>
                ������
                <input type="radio" name="zt" value="0"<%=zt.equals("0")?" checked" :""%>>
                δ��
                <input type="radio" name="zt" value="1"<%=zt.equals("1")?" checked" :""%>>
                ����
                <input type="radio" name="zt" value="2"<%=zt.equals("2")?" checked" :""%>>
                ����
              </TD>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center">
                <INPUT type="button" class="button" title="��ѯ(ALT+F)" value="��ѯ(F)" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)"  name="button" onKeyDown="return getNextElement();">
                  <pc:shortcut key="f" script='<%="sumitFixedQuery("+Operate.FIXED_SEARCH+")"%>'/>
                <INPUT  type="button" class="button" title="�ر�(ALT+T)" value="�ر�(T)" onClick="hideFrame('fixedQuery')" name="button2" onKeyDown="return getNextElement();">
                  <pc:shortcut key="t" script='<%="buttonEventT()"%>'/>
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<%} out.print(retu);%>
</body>
</html>