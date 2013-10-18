<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.project.*,engine.erp.person.shengyu.*"%>
<%@ page import="java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "train_plan";
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("train_plan", request, response))
    return;
  B_TrainPlan B_TrainPlanBean = B_TrainPlan.getInstance(request);
  LookUp areaBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_AREA);
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT_BOTH);//BEAN_DEPT
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
  LookUp countryBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_COUNTRY);
  LookUp dutyBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_DUTY);
  LookUp personclassBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_CLASS);
  LookUp personctechBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_TECH);
  LookUp personNationBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_NATION);
  LookUp personPolityBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_POLITY);
  LookUp personEducationBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_EDUCATION);
  LookUp personNativeBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_NATIVE);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript">

function sumitForm(oper, row)
{
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function backList()
{
  location.href='../person_shengyu/train_plan.jsp';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr>
    <td NOWRAP align="center"></td>
  </tr>
</table>
<%
  String retu = B_TrainPlanBean.doService(request, response);
  if(retu.indexOf("backList()")>-1 || retu.indexOf("toDetail()")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  RowMap m_RowInfo = B_TrainPlanBean.getMasterRowinfo();   //行到主表的一行信息

  RowMap[] pxkcRows= B_TrainPlanBean.getPxkcRowinfos();//从表应聘人员教育情况的多行信息


  EngineDataSet dsDWTX = B_TrainPlanBean.getMaterTable();
  EngineDataSet dsDWTX_LXR = null;//B_TrainPlanBean.getDetailTable();

  EngineDataSet dsrl_pxkc = B_TrainPlanBean.getPxkcTable();

  ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("2"); opkey.add("3");opkey.add("4");opkey.add("5");
  ArrayList opval = new ArrayList(); opval.add("职员离职"); opval.add("部门调动"); opval.add("类别变动");opval.add("职务变迁");opval.add("职员复职");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};


  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit)||B_TrainPlanBean.masterIsAdd();
  boolean isCanDelete =(loginBean.hasLimits(pageCode, op_delete))||B_TrainPlanBean.masterIsAdd();
  String typeClass = (isCanEdit)?"class=edFocused": "class=edline";
  String readonly = (isCanEdit)?"":"readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
<table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
  <tr valign="top">
  <td width="400"><table border=0 CELLPADDING=0 CELLSPACING=0 class="table">
  <tr>
  <td  class="activeVTab">培训课程卡</td>
  </tr>
</table>
<table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
  <tr>
  <td>
<table cellspacing="2" cellpadding="0" border="0" width="100%" bgcolor="#f0f0f0">

    <INPUT TYPE="HIDDEN" NAME="personid"  VALUE="<%=m_RowInfo.get("personid")%>">
     <tr>
       <td nowrap class="tdTitle">培训编号</td>
       <td nowrap class="td"><input type="text" name="pxbh" value='<%=m_RowInfo.get("pxbh")%>'   class="edline" maxlength='<%=dsDWTX.getColumn("pxbh").getPrecision()%>' style="width:110" onKeyDown="return getNextElement();" readonly></td>
       <td nowrap class="tdTitle">计划名称</td>
       <td nowrap class="td"><input type="text" name="jhmc" value='<%=m_RowInfo.get("jhmc")%>' maxlength='<%=dsDWTX.getColumn("jhmc").getPrecision()%>' style="width:130" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
       <td nowrap class="tdTitle">负责单位</td>
       <td nowrap class="td"><input type="text" name="fzdw" value='<%=m_RowInfo.get("fzdw")%>' maxlength="16" style="width:130" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
     </tr>
     <tr>
       <td nowrap class="tdTitle">负责部门</td>
       <%--<td nowrap class="td"><input type="text" name="fzbm" value='<%=m_RowInfo.get("fzbm")%>' maxlength='<%=dsDWTX.getColumn("fzbm").getPrecision()%>' style="width:130" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>  --%>
       <td nowrap class="td">
       <%if((isCanEdit)){%>
       <pc:select name="deptid"  style="width:120">
       <%=deptBean.getList(m_RowInfo.get("deptid"))%>
        </pc:select>
       <%}else{%>
       <input type="text"  value='<%=deptBean.getLookupName(m_RowInfo.get("fzbm"))%>'  style="width:130" class="edline"  readonly>
       <%}%>
       </td>
     <td nowrap class="tdTitle">负责人</td>
       <td nowrap class="td"><input type="text" name="fzr" value='<%=m_RowInfo.get("fzr")%>' maxlength='<%=dsDWTX.getColumn("fzr").getPrecision()%>' style="width:130" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
       <td nowrap class="tdTitle">培训类型</td>
       <td nowrap class="td"><input type="text" name="pxlx" value='<%=m_RowInfo.get("pxlx")%>' maxlength='<%=dsDWTX.getColumn("pxlx").getPrecision()%>' style="width:120" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
     </tr>
     <tr>
       <td nowrap class="tdTitle">培训形式</td>
       <td colspan="3" nowrap class="td"><input type="text" name="pxxs" value='<%=m_RowInfo.get("pxxs")%>' maxlength='<%=dsDWTX.getColumn("pxxs").getPrecision()%>' style="width:120" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
       <td nowrap class="tdTitle">培训目标</td>
       <td nowrap class="td"><input type="text" name="pxmb" value='<%=m_RowInfo.get("pxmb")%>' maxlength='<%=dsDWTX.getColumn("pxmb").getPrecision()%>' style="width:130" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>

     <tr>
     <td colspan="6" nowrap>
     <table cellspacing=0 width="100%" cellpadding=0>
     <tr>
         <td nowrap><div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_0');return false;">培训计划</a></div></td>
         <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a></td>
     </tr>
     </table>
     <div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:750;height:110;overflow-y:auto;overflow-x:auto;">
     <table id="tableview1" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
     <tr class="tableTitle">
          <td nowrap>
          <%if(loginBean.hasLimits(pageCode,op_add)){
            String add = "sumitForm("+B_TrainPlanBean.PXKC_ADD+",-1)";
          %>
          <%if(isCanEdit)%>
          <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=B_TrainPlanBean.PXKC_ADD%>,-1)" src="../images/add.gif" border="0">
          <pc:shortcut key="a" script='<%=add%>'/>
          <%}%>
           </td>
                  <td>课程名称</td>
                  <td>培训教师</td>
                  <td>培训教材</td>
                  <td>培训时间</td>
                  <td>培训地点</td>
                  <td>课程费用(元)</td>
                  <td>咨询费用(元)</td>
                  <td>其他费用(元)</td>
    </tr>
     <%
      RowMap pxkcdetail = null;
      for(int i=0; i<pxkcRows.length; i++)
      {
        pxkcdetail = pxkcRows[i];
     %>
            <tr>
                <td class="td" align="center">
                <%if(isCanDelete){%><input name="image32" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=B_TrainPlanBean.PXKC_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle"><%}%>
                </td>
                <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="kcmc_<%=i%>" VALUE="<%=pxkcdetail.get("kcmc")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("kcmc").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="pxjs_<%=i%>" VALUE="<%=pxkcdetail.get("pxjs")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("pxjs").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="pxjc_<%=i%>" VALUE="<%=pxkcdetail.get("pxjc")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("pxjc").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                <td class="td" align="right">
                <INPUT TYPE="TEXT" NAME="pxsj_<%=i%>" VALUE="<%=pxkcdetail.get("pxsj")%>" style="width:80" MAXLENGTH="10" <%=typeClass%>  onChange="checkDate(this)" <%=readonly%>>
                <%if((isCanEdit)){%><a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(pxsj_<%=i%>);">
                </a><%}%>
                </td>
              <%--<%=detailClass_r%>--%>
                 <td class="td" align="right"><INPUT TYPE="TEXT" NAME="pxdj_<%=i%>" VALUE="<%=pxkcdetail.get("pxdj")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("pxdj").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                 <td class="td" ><INPUT TYPE="TEXT" class=edFocused_r NAME="kcfy_<%=i%>" VALUE="<%=pxkcdetail.get("kcfy")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("kcfy").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                 <td class="td" ><INPUT TYPE="TEXT" class=edFocused_r NAME="zxfy_<%=i%>" VALUE="<%=pxkcdetail.get("zxfy")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("zxfy").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                 <td class="td" ><INPUT TYPE="TEXT" class=edFocused_r NAME="qtfy_<%=i%>" VALUE="<%=pxkcdetail.get("qtfy")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("qtfy").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
           </tr>
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
        <td class="td" colspan="2"><b>登记日期:</b><%=m_RowInfo.get("createDate")%></td>
        <td class="td"></td>
        <td class="td"  colspan="0" align="right"><b>制单人:</b><%=m_RowInfo.get("creator")%></td>
      </tr>
      <tr>
        <td noWrap class="tableTitle">
        <%if(loginBean.hasLimits(pageCode,op_add)){
          String add = "sumitForm("+Operate.POST_CONTINUE+",-1)";
        %>
          <%if(isCanEdit)%>
          <input name="button2" type="button" class="button" title = "保存添加"onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value='保存添加(N)'><pc:shortcut key="n" script='<%=add%>'/><%}%>
           <%if(loginBean.hasLimits(pageCode,op_add)){
            String reu = "sumitForm("+Operate.POST+",-1)";
           %>
          <%if(isCanEdit)%><input name="button" type="button" title = "保存返回" class="button" onClick="sumitForm(<%=Operate.POST%>);" value='保存返回(S)'><pc:shortcut key="s" script='<%=reu%>'/><%}%>
          <%if(loginBean.hasLimits(pageCode,op_add)){
            String del = "sumitForm("+B_TrainPlanBean.DELETE_RETURN+",-1)";
           %>
          <%if(isCanDelete)%><input name="btnback2" type="button" class="button" title = "删除"  onClick="if(confirm('是否删除该记录？'))sumitForm(<%=B_TrainPlanBean.DELETE_RETURN%>,<%=request.getParameter("rownum")%>)" value='  删除(D)  '><pc:shortcut key="d" script='<%=del%>'/><%}%>
           <%if(loginBean.hasLimits(pageCode,op_add)){
           String ret = "backList()";
           %>
          <input name="btnback" type="button" title = "返回" class="button" onKeyDown="return onInputKeyboard();" onClick="backList();" value='  返回(C)  '><pc:shortcut key="c" script='<%=ret%>'/><%}%>
        </td>
      </tr>
    </table>
</form>
<%out.print(retu);%>
</body>
</html></font></font>