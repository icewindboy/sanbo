<%--����̵㵥����--%>
<%@ page contentType="text/html; charset=gb2312"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%><%
  engine.erp.store.shengyu.B_StoreCheck storeCheckBean = engine.erp.store.shengyu.B_StoreCheck.getInstance(request);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);

  String pageCode = "store_check";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String loginId = storeCheckBean.loginId;
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
  location.href='store_check_edit.jsp';
}
function sumitForm(oper, row)
{
  lockScreenToWait("������, ���Ժ�");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function showInterFrame(pdid, deptid,storeid,row){
  var url = "check_make_destroy.jsp?operate=0&pdid="+pdid+"&deptid="+deptid+"&storeid="+storeid+"&rownum="+row;
  document.all.interframe1.src = url;
  showFrame('detailDiv',true,"",true);
}
function hideFrameNoFresh()
  {
    hideFrame('detailDiv');
  }


</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">����̵㵥�б�</TD>
  </TR></TABLE>
<%String retu = storeCheckBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = storeCheckBean.getMaterTable();
  HtmlTableProducer table = storeCheckBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="100%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "1111"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" nowrap align="right">
       <%if(loginBean.hasLimits(pageCode, op_add)){%>
        <input name="add" class="button" type="button" title="����(ALT+A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" value="�����̵㵥(A)">
       <pc:shortcut key="a" script='<%="sumitForm("+Operate.ADD+", -1)"%>'/>
      <%}%>
       <%if(hasSearchLimit){%><input name="search2" type="button" class="button" title="��ѯ(ALT+Q)" value="��ѯ(Q)" onClick="showFixedQuery()" onKeyDown="return getNextElement();">
       <pc:shortcut key="q" script="showFixedQuery()"/>
      <%}%>
        <%if(storeCheckBean.retuUrl!=null){%><input name="button22" type="button" class="button" title="����(ALT+C)" value="����(C)" onClick="location.href='<%=storeCheckBean.retuUrl%>'"  onKeyDown="return getNextElement();">
       <pc:shortcut key="c" script='buttonEventC()'/>
     <%}%></td>
    </tr>
  </table>
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap align="center"></td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++){
        list.goToRow(i);
        boolean isInit = false;
        String rowClass =list.getValue("zt");
        if(rowClass.equals("0"))
          isInit = true;
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        //String sprid=list.getValue("sprid");
        String zt = list.getValue("zt");
        String sfdjid = list.getValue("sfdjid");
        //boolean isCancel = zt.equals("1");
        //isCancel =  isCancel && loginId.equals(sprid);//�Ƿ����ȡ������
    %>
    <%--02.19 22:42 ȥ��onclick�¼�,��Ϊ����ҳ����Ҫ�ĳɲ��ǿ����ҳ�� yjg--%>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="selectRow();">
      <td <%=rowClass%> align="center" nowrap><input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"�޸�" :"�鿴"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
      <%--if(isInit){%><input name="image3" class="img" type="image" title='�ύ����'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}--%>
      <%--if(isCancel){%><input name="image2" class="img" type="image" title='ȡ������' onClick="if(confirm('�Ƿ�ȡ�������ü�¼��'))sumitForm(<%=storeCheckBean.CANCEL_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}--%>
      <%--<input name="image" class="img" type="image" title=<%=sfdjid.equals("") ? "�������絥" : "�鿴���絥"%> onClick="showInterFrame(<%=list.getValue("pdid")%>, <%=list.getValue("deptid")%>,<%=list.getValue("storeid")%>,<%=list.getRow()%>)"
         src='../images/edit.old.gif' border="0">
      --%>
    </td>
      <%table.printCells(pageContext, rowClass);%>
    </tr>
    <%  list.next();
      }
      for(; i < iPage; i++){
        out.print("<tr>");
        table.printBlankCells(pageContext, "class=td");
        out.print("<td>&nbsp;</td></tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
    <%if(!storeCheckBean.masterIsAdd()){
    int row = storeCheckBean.getSelectedRow();
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
     location.href='<%=storeCheckBean.retuUrl%>';
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
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0><%storeCheckBean.table.printWhereInfo(pageContext);%>
              <TD nowrap colspan=5 height=30 align="center"><INPUT  onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" class="button" title="��ѯ(ALT+F)" value="��ѯ(F)" name="button" onKeyDown="return getNextElement();">
                    <pc:shortcut key="f" script='<%="sumitFixedQuery("+Operate.FIXED_SEARCH+")"%>'/>
                <INPUT type="button" class="button"  title="�ر�(ALT+T)" value="�ر�(T)" onClick="hideFrame('fixedQuery')"  name="button2" onKeyDown="return getNextElement();">
                   <pc:shortcut key="t" script='<%="buttonEventT()"%>'/>
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
  <div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="800" height="400" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</form>
<%} out.print(retu);%>
</body>
</html>