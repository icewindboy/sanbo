<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.project.*,engine.erp.person.shengyu.*"%>
<%@ page import="java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "train_feedback";
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("train_feedback", request, response))
  return;
B_TrainFeedback b_TrainFeedbackBean = B_TrainFeedback.getInstance(request);
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
  location.href='../person_shengyu/train_feedback.jsp';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr>
    <td NOWRAP align="center"></td>
  </tr>
</table>
<%
  String retu = b_TrainFeedbackBean.doService(request, response);
if(retu.indexOf("backList()")>-1 || retu.indexOf("toDetail()")>-1)
{
  out.print(retu);
  return;
}
String curUrl = request.getRequestURL().toString();
RowMap m_RowInfo = b_TrainFeedbackBean.getMasterRowinfo();   //行到主表的一行信息
String s = m_RowInfo.get("personid");

//RowMap[] pxkcRows= b_TrainFeedbackBean.getCcsqRowinfos();//从表的多行信息


EngineDataSet ds = b_TrainFeedbackBean.getMaterTable();
EngineDataSet dsDWTX_LXR = null;//b_TrainFeedbackBean.getDetailTable();


if(b_TrainFeedbackBean.isApprove)
{
  //corpBean.regData(ds, "dwtxid");
  personBean.regData(ds, "personid");
  //storeBean.regData(ds, "storeid");
  deptBean.regData(ds, "deptid");
  //corpBean.regData(ds, "dwt_dwtxid");
}

//EngineDataSet dsrl_pxkc = b_TrainFeedbackBean.getCcsqTable();

//ArrayList opkey    = new ArrayList(); opkey.add("1"); opkey.add("2"); opkey.add("3");opkey.add("4");opkey.add("5");
//ArrayList opval    = new ArrayList(); opval.add("职员离职"); opval.add("部门调动"); opval.add("类别变动");opval.add("职务变迁");opval.add("职员复职");
//ArrayList[] lists  = new ArrayList[]{opkey, opval};


boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit)||b_TrainFeedbackBean.masterIsAdd();
boolean isCanDelete =(loginBean.hasLimits(pageCode, op_delete))||b_TrainFeedbackBean.masterIsAdd();
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
  <td  class="activeVTab">员工反馈信息</td>
  </tr>
</table>
<table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
  <tr>
  <td>
<table cellspacing="2" cellpadding="0" border="0" width="100%" bgcolor="#f0f0f0">
   <%personBean.regConditionData(ds, "personid");%>
              <tr>
                  <td nowrap class="tdTitle">员工</td>
<%--  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("personid").getFieldname()%></td>--%>
                  <td  noWrap class="td">
                  <%if(!isCanEdit) out.print("<input type='text' value='"+personBean.getLookupName(m_RowInfo.get("personid"))+"' style='width:110' class='edline' readonly>");
                  else {%>
                    <pc:select name="personid" addNull="1" style="width:110">
                       <%=personBean.getList(m_RowInfo.get("personid"), "deptid", m_RowInfo.get("deptid"))%>
                      </pc:select>
                    <%}%>
                   </td>
                    <td nowrap class="tdTitle">所属部门</td>
                  <%-- <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>--%>
                    <td noWrap class="td">
                    <%String onChange = "if(form1.deptid.value!='"+m_RowInfo.get("deptid")+"')sumitForm("+b_TrainFeedbackBean.DEPT_CHANGE+")";%>
                    <%if(!isCanEdit) out.print("<input type='text' value='"+deptBean.getLookupName(m_RowInfo.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select  name="deptid" addNull="1" style="width:111" onSelect="<%=onChange%>"> <%=deptBean.getList(m_RowInfo.get("deptid"))%>
                        </pc:select>
                    <%}%>
                  </td>
                  <td nowrap class="tdTitle">培训项目</td>
                  <td nowrap class="td"><input type="text" name="train_proj" value='<%=m_RowInfo.get("train_proj")%>' maxlength='<%=ds.getColumn("train_proj").getPrecision()%>' style="width:120" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                 </tr>
     <tr>
     <td nowrap class="tdTitle">培训开始时间</td>
                    <%-- <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jhrq").getFieldname()%></td>--%>
                  <td noWrap class="td"><input type="text" name="train_start" value='<%=m_RowInfo.get("train_start")%>' maxlength='10' style="width:85" class="edbox" onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                  <%if(isCanEdit){%>
                  <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.train_start);"></a>
       <%}%>
                  </td>
                  <td nowrap class="tdTitle">培训结束时间</td>
                    <%-- <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jhrq").getFieldname()%></td>--%>
                  <td noWrap class="td"><input type="text" name="train_end" value='<%=m_RowInfo.get("train_end")%>' maxlength='10' style="width:85" class="edbox" onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                  <%if(isCanEdit){%>
                  <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.train_end);"></a>
       <%}%>
                  </td>
            <%
              ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("2"); opkey.add("3");opkey.add("4");opkey.add("5");
                    ArrayList opval = new ArrayList(); opval.add("很满意"); opval.add("满意"); opval.add("一般");opval.add("不满意");opval.add("很差");
                    ArrayList[] lists  = new ArrayList[]{opkey, opval};
                    String ss=m_RowInfo.get("tech_material_ok");
                    String tt=m_RowInfo.get("techcher_ok");
            %>
     <td nowrap class="tdTitle">对教材满意度</td>
     <td noWrap class="td">
     <%
     //String t="sumitForm("+b_employeeinfoChangeBean.BDLX_CHANGE+",-1)";
       int te=0;
            try{
              te=Integer.parseInt(ss)-1;
              }catch(Exception e){te=0;}
     if(!isCanEdit){%><input type="HIDDEN" name="tech_material_ok" class="ednone" value="<%=opkey.get(te)%>"><%out.print(opval.get(te));}else{%>
       <pc:select name="tech_material_ok"  style="width:130">  <%--   onSelect="<%=t%>">--%>
     <%=b_TrainFeedbackBean.listToOption(lists, opkey.indexOf(m_RowInfo.get("tech_material_ok")))%>
         </pc:select>
    <%}%>
     </td>
  </tr>
     <tr>
     <td nowrap class="tdTitle">对教师满意度</td>
     <td noWrap class="td">
     <%
     //String t="sumitForm("+b_employeeinfoChangeBean.BDLX_CHANGE+",-1)";
       int ke=0;
     try{
       ke=Integer.parseInt(tt)-1;
       }catch(Exception e){ke=0;}
     if(!isCanEdit){%><input type="HIDDEN" name="techcher_ok" class="edbox" value="<%=opkey.get(ke)%>"><%out.print(opval.get(ke));}else{%>
       <pc:select name="techcher_ok"  style="width:130">  <%--   onSelect="<%=t%>">--%>
     <%=b_TrainFeedbackBean.listToOption(lists, opkey.indexOf(m_RowInfo.get("techcher_ok")))%>
         </pc:select>
    <%}%>
     </td>
      <td nowrap class="tdTitle">是否实现培训目标</td>
     <%String isImpl = m_RowInfo.get("isImpl");%>
      <td colspan="4" nowrap class="td">
     是
      <input type="radio" name="isImpl" value="1" <%=isImpl.equals("1")?" checked" :""%> checked>
     否
       <input type="radio" name="isImpl" value="2" <%=isImpl.equals("2")?" checked" :""%>></td>
     </tr>
             <tr>
             <td nowrap class="tdTitle">总结及建议</td>
                  <td class="td" colspan="5" align="left"><textarea  name="sum_up" cols="80" rows="5"><%=m_RowInfo.get("sum_up")%></textarea></td>
                </tr>
             <tr>
              <tr>
                  <td nowrap  class="tdTitle">备注</td>
                  <td colspan="3" nowrap class="td"> <input type="text" name="memo" value='<%=m_RowInfo.get("memo")%>' maxlength='64' style="width:300" class=edFocused onKeyDown="return getNextElement();" >
                  </td>

                </tr>
     </tr>
    <tr>
     <td colspan="6" nowrap>
     <table cellspacing=0 width="100%" cellpadding=0>
</table>
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
            String del = "sumitForm("+b_TrainFeedbackBean.DELETE_RETURN+",-1)";
           %>
          <%if(isCanDelete)%><input name="btnback2" type="button" class="button" title = "删除"  onClick="if(confirm('是否删除该记录？'))sumitForm(<%=b_TrainFeedbackBean.DELETE_RETURN%>,<%=request.getParameter("rownum")%>)" value='  删除(D)  '><pc:shortcut key="d" script='<%=del%>'/><%}%>
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
</html>
