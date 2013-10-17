<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.project.*,engine.erp.produce.*,java.math.BigDecimal"%>
<%@ page import="java.util.*"%>

<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "sc_track_type";
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("sc_track_type", request, response))
    return;
  B_SC_Track_Type Track_TypeBean = B_SC_Track_Type.getInstance(request);


%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<script language="javascript">
function areaChange(isCode,i){
  //连接的对象。编码更改对应地区更改
  var linkObj = FindSelectObject(isCode ? "dqh_"+i : "code_"+i);
  if(linkObj == null)
    return;
  var areaid = isCode ? "form1.code_"+i+".value" : "form1.dqh_"+i+".value";
  areaid = eval(areaid);
  linkObj.SetSelectedKey(areaid);
  //function getRowValue(iframeObj, lookup, frmName, srcVar, fieldVar, curID, methodName)
  getRowValue(document.all['prod'],'<%=engine.project.SysConstant.BEAN_AREA_PRICE%>','form1','srcVar=per_price_'+i+'&srcVar=ton_price_'+i,'fieldVar=per_price&fieldVar=ton_price',eval("form1.dqh_"+i+".value"))
}

function sumitForm(oper, row)
{
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}

function deptChange()
{
 associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
 }
function backList()
{
  location.href='../produce/sc_track_type.jsp';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr>
    <td NOWRAP align="center"></td>
  </tr>
</table>
<%
  String retu = Track_TypeBean.doService(request, response);
  if(retu.indexOf("backList()")>-1 || retu.indexOf("toDetail()")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  RowMap m_RowInfo = Track_TypeBean.getMasterRowinfo();   //行到主表的一行信息

  RowMap[] pxkcRows= Track_TypeBean.getPxkcRowinfos();//从表应聘人员教育情况的多行信息


  EngineDataSet dsDWTX = Track_TypeBean.getMaterTable();
  EngineDataSet dsDWTX_LXR = null;//Track_TypeBean.getDetailTable();

  EngineDataSet dsrl_pxkc = Track_TypeBean.getPxkcTable();
  engine.project.LookUp producebean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_NAME);


  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);

  String rowClass = isCanEdit ? "edbox" : "edline";
  String disable = isCanEdit ? "0" : "1";
  boolean isCanDelete =(loginBean.hasLimits(pageCode, op_delete))||Track_TypeBean.masterIsAdd();
  String typeClass = (isCanEdit)?"class=edFocused": "class=edline";
  String readonly = (isCanEdit)?"":"readonly";
  ArrayList opkey = new ArrayList(); opkey.add("0"); opkey.add("1");opkey.add("2");opkey.add("3");
   ArrayList opval = new ArrayList(); opval.add("普通"); opval.add("底涂");opval.add("分切");opval.add("横切");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};
  ArrayList opkey_prop = new ArrayList(); opkey_prop.add("1");opkey_prop.add("2");
  ArrayList opval_prop = new ArrayList(); opval_prop.add("A类型"); opval_prop.add("B类型");
  ArrayList[] list_prop  = new ArrayList[]{opkey_prop, opval_prop};
  producebean.regData(dsrl_pxkc,"gymcID");
%>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
<table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
  <tr valign="top">
  <td width="400"><table border=0 CELLPADDING=0 CELLSPACING=0 class="table">
  <tr>
  <td  class="activeVTab">生产跟踪单工序</td>
  </tr>
</table>
<table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
  <tr>
  <td>
<table cellspacing="2" cellpadding="0" border="0" width="100%" bgcolor="#f0f0f0">

    <INPUT TYPE="HIDDEN" NAME="track_type_ID"  VALUE="<%=m_RowInfo.get("track_type_ID")%>">
     <tr><td nowrap class="tdTitle">类型编号</td>

       <td nowrap class="td"><input type="text" name="type_code" value='<%=m_RowInfo.get("type_code")%>' maxlength="10" style="width:80" class=edline onKeyDown="return getNextElement();"  readonly ></td>

       <td nowrap class="tdTitle">类型名称</td>
       <%if(isCanEdit){%>
       <td nowrap class="td"><input type="text" name="type_name" value='<%=m_RowInfo.get("type_name")%>' maxlength="10" style="width:80" <%=typeClass%> onKeyDown="return getNextElement();" ></td>
       <%}else {%><td nowrap class="td"><input type="text" name="type_name" value='<%=m_RowInfo.get("type_name")%>' maxlength="10" style="width:80" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
       <%}%>
       <td nowrap class="tdTitle">属性</td>
       <%if(isCanEdit){%>
         <td nowrap class="td"><pc:select name="type_prop" addNull="1" style="width:130" >
                 <%=Track_TypeBean.listToOption(list_prop,opkey_prop.indexOf(m_RowInfo.get("type_prop")))%>
               </pc:select></td>
       <%}else {%><td nowrap class="td"><input type="text" name="type_prop" value='<%=m_RowInfo.get("type_prop")%>' maxlength="10" style="width:80" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
       <%}%>
     </tr>

    <tr>
     <td colspan="6" nowrap>
     <table cellspacing=0 width="100%" cellpadding=0>
     <tr>
         <td nowrap><div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_0');return false;">生产跟踪单工序</a></div></td>
         <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a></td>
     </tr>
     </table>
     <div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:750;height:400;overflow-y:auto;overflow-x:auto;">
     <table id="tableview1" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
     <tr class="tableTitle">
          <td nowrap>
          <%if(loginBean.hasLimits(pageCode,op_add)){
            String add = "sumitForm("+Track_TypeBean.PXKC_ADD+",-1)";
          %>
          <%if(isCanEdit){%><input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Track_TypeBean.PXKC_ADD%>,-1)" src="../images/add.gif" border="0">

          <%}}%>
           </td>
                  <td>工艺名称</td>
                  <td>跟踪类型</td>
                  <td>排序号</td>


    </tr>
     <%
      RowMap pxkcdetail = null;
      for(int i=0; i<pxkcRows.length; i++)
      {
        pxkcdetail = pxkcRows[i];


     %>
            <tr>
                <td class="td" align="center">

                <%if(isCanDelete){%><input name="image32" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Track_TypeBean.PXKC_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle"><%}%>
                </td>

                <%if(isCanEdit){%>
                  <TD nowrap class="td">
                   <pc:select  className="edFocused" name='<%="gymcID_"+i%>' value='<%=pxkcdetail.get("gymcID")%>' style="width:110">
                  <%=producebean.getList(pxkcdetail.get("gymcID"))%></pc:select>

                 </td>
                <TD nowrap class="td">
                <pc:select name='<%="track_type_"+i%>' addNull="1" style="width:130" >
                <%=Track_TypeBean.listToOption(lists, opkey.indexOf(pxkcdetail.get("track_type")))%>
               </pc:select>
              </TD>
                <td class="td" align="center"><INPUT TYPE="TEXT" NAME="order_no_<%=i%>" VALUE="<%=pxkcdetail.get("order_no")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("order_no").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" ></td>


                 <%}else {%>


                <td class="td"  align="center"><INPUT TYPE="TEXT" NAME="gymcID_<%=i%>" VALUE="<%=producebean.getLookupName(pxkcdetail.get("gymcID"))%>" style="width:80"  <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
               <% if(pxkcdetail.get("track_type").equals("0")){%>
                <td class="td" align="center"><INPUT TYPE="TEXT" NAME="track_type_<%=i%>" VALUE="普通" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("track_type").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td><%}%>
                 <% if(pxkcdetail.get("track_type").equals("1")){%>
                <td class="td" align="center"><INPUT TYPE="TEXT" NAME="track_type_<%=i%>" VALUE="底涂" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("track_type").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td><%}%>
                 <% if(pxkcdetail.get("track_type").equals("2")){%>
                <td class="td" align="center"><INPUT TYPE="TEXT" NAME="track_type_<%=i%>" VALUE="分切" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("track_type").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td><%}%>
                <% if(pxkcdetail.get("track_type").equals("3")){%>
                 <td class="td" align="center"><INPUT TYPE="TEXT" NAME="track_type_<%=i%>" VALUE="横切" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("track_type").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td><%}%>
                <td class="td" align="center"><INPUT TYPE="TEXT" NAME="order_no_<%=i%>" VALUE="<%=pxkcdetail.get("order_no")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("order_no").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>

               </tr> <%}%>
      <%}%>
      </table>
      <script language="javascript">initDefaultTableRow('tableview1',1);</script>
      </div>
  <SCRIPT LANGUAGE="javascript">INFO_EX = new TabControl('INFO_EX',0);
      AddTabItem(INFO_EX,'INFO_EX_0','tabDivINFO_EX_0','cntDivINFO_EX_0');
      if (window.top.StatFrame+''!='undefined'){ var tmp_curtab=window.top.StatFrame.GetRegisterVar('INFO_EX');if (tmp_curtab!='') {SetActiveTab(INFO_EX,tmp_curtab);}}
  </SCRIPT>
</td>
</tr>
</table>
<tr>
<td> </td>
</tr>
</table>
</table>
    <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
      <tr>
        <td noWrap class="tableTitle">
        <%if(isCanEdit){
          String add = "sumitForm("+Operate.POST_CONTINUE+",-1)";
        %>
          <input name="button2" type="button" class="button" title = "保存添加"onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value='保存添加(N)'><pc:shortcut key="n" script='<%=add%>'/>
           <%if(loginBean.hasLimits(pageCode,op_add)){
            String reu = "sumitForm("+Operate.POST+",-1)";
           %>
          <input name="button" type="button" title = "保存返回" class="button" onClick="sumitForm(<%=Operate.POST%>);" value='保存返回(S)'><pc:shortcut key="s" script='<%=reu%>'/><%}%>
          <%if(loginBean.hasLimits(pageCode,op_add)){
            String del = "sumitForm("+Track_TypeBean.DELETE_RETURN+",-1)";
           %>
          <%if(isCanDelete)%><input name="btnback2" type="button" class="button" title = "删除"  onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Track_TypeBean.DELETE_RETURN%>,<%=request.getParameter("rownum")%>)" value='  删除(D)  '><pc:shortcut key="d" script='<%=del%>'/><%}}%>
           <%
             String ret = "backList()";
           %>
          <input name="btnback" type="button" title = "返回" class="button" onKeyDown="return onInputKeyboard();" onClick="backList();" value='  返回(C)  '><pc:shortcut key="c" script='<%=ret%>'/>
        </td>
      </tr>
    </table>
</form>
<%out.print(retu);%>
</body>
</html></font></font>