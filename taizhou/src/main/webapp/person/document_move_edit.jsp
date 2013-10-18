<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*,engine.erp.person.*"%>
<%@ page import="java.util.*"%>
<%
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_approve ="op_approve";
  String pageCode = "document_move";
  if(!loginBean.hasLimits("document_move", request, response))
    return;
  B_Document_move documentBean = B_Document_move.getInstance(request);
  boolean issend = documentBean.issendperson;
  boolean isrecive = documentBean.isreciveperson;
  EngineDataSet dsrl_file = documentBean.getFileTable();
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<script language="javascript">
function backList()
{
 location.href='../person/document_move.jsp';
}
function upload()
  {

    openurl('upload', 'document_upload.jsp', null, null, 335, 180, true);

}
function sumitForm(oper, row)
{
       form1.rownum.value = row;
       form1.operate.value = oper;
       form1.submit();
}
function priceSelect(i)
{

   getRowValue(document.all['prod'],'<%=engine.project.SysConstant.BEAN_AREA_PRICE%>','form1','srcVar=per_price_'+i+'&srcVar=wage_cust_'+i+'&srcVar=wage_price_'+i+'&srcVar=per_fee_'+i+'&srcVar=fee_cust_'+i+'&srcVar=fee_price_'+i+'&srcVar=isSendcar_'+i,'fieldVar=per_price&fieldVar=wage_cust&fieldVar=wage_price&fieldVar=per_fee&fieldVar=fee_cust&fieldVar=fee_price&fieldVar=isSendcar',eval('form1.area_price_id_'+i+'.value'));
   associateSelect(document.all['prod2'], '<%=engine.project.SysConstant.BEAN_AREA_CAR%>', 'car_id_'+i, 'area_price_id',eval('form1.area_price_id_'+i+'.value'),'' ,'true');

}
function carSelect(i)
{

 getRowValue(document.all['prod'],'<%=engine.project.SysConstant.BEAN_AREA_CAR%>','form1','srcVar=cal_stand_'+i,'fieldVar=cal_stand',eval('form1.car_id_'+i+'.value'),"weightchange("+i+");");

}
function weightchange(i)
{

  var cal_stand=document.all['cal_stand_'+i];
  if(cal_stand.value==''){
    alert("请先选择车型");
    return;
  }
  var weight=document.all['weight_'+i];
  if (weight.value=="")
      weight.value=0;
  var fee_weight=document.all['fee_weight_'+i];
  if (parseFloat(weight.value) < parseFloat(cal_stand.value)&&parseFloat(weight.value)>0 )
    fee_weight.value=parseFloat(cal_stand.value);

  else
    fee_weight.value=parseFloat(weight.value);
}
function deptChange()
{
      associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
function corpCodeSelect(obj,i)
{
  CustCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid_'+i+'&srcVar=dwdm_'+i+'&srcVar=dwmc_'+i+'&srcVar=personid_'+i+'&srcVar=ywyxm_'+i,
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc&fieldVar=personid&fieldVar=xm', obj.value);
}
function corpNameSelect(obj,i)
{
  CustNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid_'+i+'&srcVar=dwdm_'+i+'&srcVar=dwmc_'+i+'&srcVar=personid_'+i+'&srcVar=ywyxm_'+i,
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc&fieldVar=personid&fieldVar=xm', obj.value);
}
function checkBdlx()
{
PersonMultiSelect('form1','srcVar=multiIdInput','undefined','undefined');
}
function showfile(i)
 {

   openurl('upload', 'document_file.jsp?i='+i, null, null,100, 100, true);
 }



</script>
<%
  String retu = documentBean.doService(request, response);
 if(retu.indexOf("backList()")>-1 || retu.indexOf("toDetail()")>-1)
 {
   out.print(retu);
   return;
 }
  engine.project.LookUp priceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_AREA_PRICE);
  engine.project.LookUp CarBean= engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_AREA_CAR);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp personBean =  engine.project.LookupBeanFacade.getInstance(request,  engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_ALL);

  String curUrl = request.getRequestURL().toString();
  RowMap m_RowInfo = documentBean.getMasterRowinfo();   //行到主表的一行信息
  RowMap areapriceRow= null;
  RowMap carRow= null;
  RowMap[] fileRows= documentBean.getFileRowinfos();//从表应聘人员教育情况的多行信息
  RowMap corpRow= null;

 EngineDataSet dsDWTX = documentBean.getMaterTable();
 EngineDataSet dsDWTX_LXR = null;//documentBean.getDetailTable();


 ArrayList opkey = new ArrayList(); opkey.add("1");opkey.add("0");
 ArrayList opval = new ArrayList(); opval.add("送货用车"); opval.add("非送货用车");
 ArrayList[] list_prop  = new ArrayList[]{opkey, opval};
 String isEnd=m_RowInfo.get("state");
 boolean isNOTEnd=documentBean.isMasterAdd;
 boolean isCanAdd =loginBean.hasLimits(pageCode, op_add)&&issend&&isNOTEnd;
 boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit)&&issend&&isNOTEnd;
 isCanEdit = isCanEdit&&!isEnd.equals("8");
 String rowClass = isCanEdit ? "edbox" : "edline";
 String disable = isCanEdit ? "0" : "1";
 boolean isCanDelete =(loginBean.hasLimits(pageCode, op_delete))||documentBean.masterIsAdd()&&issend&&isNOTEnd;
 String typeClass = (isCanEdit)?"class=edFocused": "class=edline";
 String readonly = (isCanEdit)?"":"readonly";
 EngineDataSet listperson = documentBean.getrecivePersonTable();
%>
<BODY oncontextmenu="window.event.returnValue=true" >
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<iframe id="prod2" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>

<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr>
    <td NOWRAP align="center"></td>
 </tr>
</table>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
<table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
  <tr valign="top">
   <td width="400"><table border=0 CELLPADDING=0 CELLSPACING=0 class="table">
  <tr>
   <td  class="activeVTab">公文流转
 <%=issend?"(发文件)":"(收文件)"%>
   <%if(isCanEdit&&issend){%>
 <input type="hidden" name="multiIdInput" value="" onchange="sumitForm(<%=documentBean.PERSON_ADD%>)">
<img style='cursor:hand' title="添加联系人" src='../images/person.gif' border=0 onClick="checkBdlx()">
<img style='cursor:hand'  title="查看联系人"  src='../images/edit.gif' border=0 onClick="location.href='reveciveperson.jsp'">
<%}%>
 </td>
  </tr>

</table>
<table class="editformbox" cellspacing=2 cellpadding=0 width="400">

  <tr>
   <td>
<table cellspacing="2" cellpadding="0" border="0" width="400" bgcolor="#f0f0f0">
  <tr>

    <td noWrap  class="tdTitle">文件类型</td>
             <td width="80" class="td">

              <%if(isCanEdit){
              String file_type = m_RowInfo.get("file_type");%>
              <pc:select name="file_type" style="width:80" addNull="1" combox="1" value="<%=file_type%>" >
               <pc:option value="请示类">请示类</pc:option>
               <pc:option value="通知类">通知类</pc:option>
               <pc:option value="文件类">文件类</pc:option>

              </pc:select>
              <%}else{%>
              <input type="text" name="file_type" value='<%=m_RowInfo.get("file_type")%>' maxlength="10" style="width:80" <%=typeClass%> onKeyDown="return getNextElement();" readonly>
              <%}%>
      </td>
     <td noWrap class="tdTitle">优先级</td>
  <td width="80" class="td">
   <%if(isCanEdit){
  String level = m_RowInfo.get("filelevel"); %>
  <pc:select name="filelevel" style="width:80" addNull="1" value="<%=level%>" >
               <pc:option value="0">普通</pc:option>
               <pc:option value="1">缓慢</pc:option>
               <pc:option value="2">紧急</pc:option>
              </pc:select>
   <%}else{%>
              <input type="text" name="filelevel" value='<%= m_RowInfo.get("filelevel").equals("2")?"紧急":(m_RowInfo.get("filelevel").equals("1")?"缓慢":"普通")%>' maxlength="10" style="width:80" <%=typeClass%> onKeyDown="return getNextElement();" readonly>
              <%}%>

   </td>
  </tr>
  <tr>
    <td nowrap class="tdTitle">主题</td>
  <%if(isCanEdit){%>
   <td nowrap class="td" colspan="3"><input type="text" name="topic" value='<%=m_RowInfo.get("topic")%>' maxlength="64" style="width:300" <%=typeClass%> onKeyDown="return getNextElement();" ></td>
  <%}else {%><td nowrap class="td" colspan="3"><input type="text" name="topic" value='<%=m_RowInfo.get("topic")%>' maxlength="64" style="width:240" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
  <%}%>
   </tr>
  <tr>
     <td nowrap class="tdTitle">标题</td>
  <%if(isCanEdit){%>
   <td nowrap class="td" colspan="3"><input type="text" name="caption" value='<%=m_RowInfo.get("caption")%>' maxlength="64" style="width:300" <%=typeClass%> onKeyDown="return getNextElement();" ></td>
  <%}else {%><td nowrap class="td" colspan="3"><input type="text" name="caption" value='<%=m_RowInfo.get("caption")%>' maxlength="64" style="width:300" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
  <%}%>
  </tr>
  <tr>
      <td  noWrap class="tdTitle">文件内容</td><%--其他信息--%>
       <td colspan="6" noWrap class="td"><textarea name="maintext" rows="16" onKeyDown="return getNextElement();" style="width:545"   <%=readonly%> > <%=m_RowInfo.get("maintext")%></textarea></td>
        </tr>
        <tr>
            <td class="td" nowrap colspan="2"><b>发件日期:</b><%=m_RowInfo.get("senddate")%></td>
            <td class="td"></td>
            <td class="td" align="right" nowrap><b>发件人:</b><%=m_RowInfo.get("sendperson")%></td>
          </tr>
   <tr>
    <%if( !issend){%>
    <tr>


    <td nowrap class="tdTitle">已读意见</td>
    <%if( !listperson.getValue("isread").equals("1")){%>
   <td nowrap class="td" colspan="4"><input type="text" name="sm" value='<%=listperson.getValue("sm")%>' maxlength="128" style="width:545"  class=edline onKeyDown="return getNextElement();" readonly ></td>
     <%}else{%>
     <td nowrap class="td" colspan="4"><input type="text" name="sm" value='<%=listperson.getValue("sm")%>' maxlength="128" style="width:545"  class=edbox onKeyDown="return getNextElement();"  ></td>
   <%}%>
  </tr>
   <%}%>
   <tr>
     <td colspan="6" nowrap>
     <table cellspacing=0 width="100%" cellpadding=0>

     <td nowrap><div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="#" >附件上传</a></div></td>
     <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a></td>
  </tr>

  </table>
  <div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:600;height:100;overflow-y:auto;overflow-x:auto;">
  <table id="tableview1" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
  <tr class="tableTitle">
     <td nowrap>
     <%if(isCanEdit){%>
     <input name="image" class="img" type="image" title="新增(A)" onClick="upload()" src="../images/add.gif" border="0">
     <%}%>
     </td>

                  <td nowrap>附件</td>



      </tr>
      <%


       dsrl_file.first();
       RowMap filedetail = null;
       for(int i=0; i<fileRows.length; i++)
       {
         filedetail = fileRows[i];
     %>
    <tr>
                <td class="td" align="center">

                <%if(isCanDelete){%><input name="image32" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=documentBean.FILE_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle">
                <%}%>
                <img style='cursor:hand'  title="下载附件"  src='../images/nextbill.gif' border=0 onClick="showfile(<%=i%>)">
                </td>
                <%if(isCanEdit){%>


                <td class="td" nowrap align="left"><INPUT TYPE="TEXT" NAME="filename_<%=i%>" VALUE="<%=filedetail.get("filename")%>" style="width:515" MAXLENGTH="<%=dsrl_file.getColumn("filename").getPrecision()%>" <%=typeClass%>   onKeyDown="return getNextElement();" readonly></td>

                 <%}else {%>




                <td class="td" align="left"><INPUT TYPE="TEXT" NAME="filename_<%=i%>" VALUE="<%=filedetail.get("filename")%>" style="width:515" MAXLENGTH="<%=dsrl_file.getColumn("filename").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" readonly></td>

               </tr> <%}%>
          <% dsrl_file.next();}%>

      </table>
      <script language="javascript">initDefaultTableRow('tableview1',1);</script>
      </div>
      </td>
      </tr>
   </table>
  </td>
  </tr>
 </table>
</table>
    <table CELLSPACING=0 CELLPADDING=0 width="600" align="center">
      <tr>
        <td noWrap class="tableTitle">
        <%if(isCanEdit&&issend){

        %>

        <%if(loginBean.hasLimits(pageCode,op_add)){
        String reu = "sumitForm("+Operate.POST+",-1)";
        %>
        <input name="button" type="button" title = "发送返回" class="button" onClick="sumitForm(<%=Operate.POST%>);" value='发送返回(S)'><pc:shortcut key="s" script='<%=reu%>'/><%}%>
        <%}%>
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
</html>