<%--销售管理--客户信誉额度--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<%@ taglib uri="/WEB-INF/pagescontrol.tld" prefix="pc"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "customer_credit_list";
%>
<%if(!loginBean.hasLimits("customer_credit_list", request, response))
  return;
engine.erp.sale.CustomerCreditBean xs_khxyedbean = engine.erp.sale.CustomerCreditBean.getInstance(request);
engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
String retu = xs_khxyedbean.doService(request, response);//location.href='baln.htm'
if(retu.indexOf("location.href=")>-1)
  return;
EngineDataSet list = xs_khxyedbean.getOneTable();
String curUrl = request.getRequestURL().toString();
boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
boolean canedit = xs_khxyedbean.getState();
String detailClass_r = canedit ? "class=edFocused_r": "class=ednone_r" ;
String readonly = canedit ?"": "readonly" ;
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
//客户编码
function customerCodeSelect(obj)
{
    CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
//客户名称
function customerNameSelect(obj)
{
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>

<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">客户信誉额度</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum"  VALUE="">
  <TABLE width="90%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
       <td class="td" nowrap>
      <%
        String key = "datasetlist";
        pageContext.setAttribute(key, list);
        int iPage = loginBean.getPageSize();
        String pageSize = ""+iPage;
        String p = "sumitForm("+Operate.POST+");";
      %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <TD align="right">
        <%if(isCanEdit&&!canedit){
        String m = "sumitForm("+xs_khxyedbean.MODIFY+");";
         %>
         <INPUT class="button" onClick="sumitForm(<%=xs_khxyedbean.MODIFY%>);" type="button" value="修改(M)" name="MODIFY" onKeyDown="return getNextElement();">
        <pc:shortcut key="m" script="<%=m%>" />
       <%}%>
        <%if(isCanEdit&&canedit){%>
       <input name="button2222232" type="button" align="Right" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)"border="0">
         <%}%>
        <pc:shortcut key="S" script="<%=p%>" />
        <INPUT class="button" onClick="showFixedQuery()" type="button" value="查询(Q)" name="Query" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script="showFixedQuery()" />
        <%if(xs_khxyedbean.retuUrl!=null){
           String s = "location.href='"+xs_khxyedbean.retuUrl+"'";
        %>
        <input name="button2222232" type="button" align="Right" class="button" onClick="location.href='<%=xs_khxyedbean.retuUrl%>'" value="返回(C)"border="0">
        <pc:shortcut key="c" script="<%=s%>" />
        <%}%>
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=45 align="center">
      </td>
      <td nowrap>地区号码</td>
      <td nowrap>地区</td>
      <td nowrap>单位号码</td>
      <td nowrap>购货单位</td>
      <td nowrap>信誉额度</td>
      <td nowrap>信誉等级</td>
      <td nowrap>基本回款天数</td>
      <td nowrap>回笼天数</td>
      <td nowrap>可用信誉度</td>
      <td nowrap>业务员</td>
    </tr>
      <%
        RowMap[] detailRows= xs_khxyedbean.getDetailRowinfos();
        corpBean.regData(list, "dwtxId");
        list.first();
        int i=0;
        for(; i<list.getRowCount(); i++)
        {
          String dwtxid = list.getValue("dwtxId");
          RowMap corprow = corpBean.getLookupRow(list.getValue("dwtxId"));
    %>
    <tr onclick="selectRow()">
      <td class="td" nowrap align="center">
      <%if(isCanEdit){%>
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/del.gif" border="0">
      <%}%>
      </td>
      <td class="td" nowrap><INPUT TYPE="TEXT" NAME="areacode_<%=i%>" VALUE="<%=list.getValue("areacode")%>" style="width:60" MAXLENGTH="<%=list.getColumn("areacode").getPrecision()%>" class="ednone_r"  onKeyDown="return getNextElement();" readonly></td>
      <td class="td" nowrap><INPUT TYPE="TEXT" NAME="dqmc_<%=i%>" VALUE="<%=corprow.get("dqmc")%>" style="width:80" MAXLENGTH="<%=list.getColumn("dqmc").getPrecision()%>" class="ednone_r"  onKeyDown="return getNextElement();" readonly><INPUT TYPE="hidden" NAME="dwtxid_<%=i%>" VALUE="<%=list.getValue("dwtxid")%>" style="width:50" MAXLENGTH="<%=list.getColumn("dwtxid").getPrecision()%>" class="edbox"  onKeyDown="return getNextElement();"></td>
      <td class="td" nowrap><INPUT TYPE="TEXT" NAME="dwdm_<%=i%>" VALUE="<%=corprow.get("dwdm")%>" style="width:80" MAXLENGTH="<%=list.getColumn("dwdm").getPrecision()%>" class="ednone_r"  onKeyDown="return getNextElement();" readonly></td>
      <td class="td" nowrap><INPUT TYPE="TEXT" NAME="dwmc_<%=i%>" VALUE="<%=corprow.get("dwmc")%>" style="width:250" MAXLENGTH="<%=list.getColumn("dwmc").getPrecision()%>" class="ednone"  onKeyDown="return getNextElement();" readonly></td>
      <td class="td" nowrap><INPUT TYPE="TEXT" NAME="xyed_<%=i%>" VALUE="<%=list.getValue("xyed")%>" style="width:80" MAXLENGTH="<%=list.getColumn("xyed").getPrecision()%>" <%=detailClass_r%>  <%=readonly%>  onKeyDown="return getNextElement();" onchange="verify(<%=i%>,true)"></td>
      <td class="td" nowrap><INPUT TYPE="TEXT" NAME="xydj_<%=i%>" VALUE="<%=list.getValue("xydj")%>" style="width:80" MAXLENGTH="<%=list.getColumn("xydj").getPrecision()%>" <%=detailClass_r%>  <%=readonly%>  onKeyDown="return getNextElement();" ></td>
      <td class="td" nowrap><INPUT TYPE="TEXT" NAME="hkts_<%=i%>" VALUE="<%=list.getValue("hkts")%>" style="width:80" MAXLENGTH="<%=list.getColumn("hkts").getPrecision()%>" <%=detailClass_r%>  <%=readonly%>  onKeyDown="return getNextElement();" onchange="verify(<%=i%>,false)"></td>
      <td class="td" nowrap><INPUT TYPE="TEXT" NAME="hlts_<%=i%>" VALUE="<%=list.getValue("hlts")%>" style="width:80" MAXLENGTH="<%=list.getColumn("hlts").getPrecision()%>" <%=detailClass_r%>  <%=readonly%>  onKeyDown="return getNextElement();" onchange="verify(<%=i%>,false)"></td>

      <td class="td" nowrap><INPUT TYPE="TEXT" NAME="kyxyed_<%=i%>" VALUE="<%=list.getValue("kyxyed")%>" style="width:80" MAXLENGTH="<%=list.getColumn("kyxyed").getPrecision()%>" class="ednone_r"  onKeyDown="return getNextElement();" readonly></td>
      <td class="td" nowrap><INPUT TYPE="TEXT" NAME="xm_<%=i%>" VALUE="<%=list.getValue("xm")%>" style="width:80" MAXLENGTH="<%=list.getColumn("xm").getPrecision()%>" class="ednone"  onKeyDown="return getNextElement();" readonly></td>
    </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
    <td class="td"></td>
    </tr>
    <%
      }
      RowMap row = xs_khxyedbean.getRowinfo();
   %>
  </table>
</form>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
   function verify(i,type)
   {
     var xyedObj = document.all['xyed_'+i];
     var xydjObj = document.all['xydj_'+i];
     var hktsObj = document.all['hkts_'+i];
     var showText = type?'信誉额度':'回款天数';
     var changeObj = type?xyedObj:hktsObj;
     if(isNaN(changeObj.value)){
       alert('输入的'+showText+'非法');
       changeObj.focus();
       changeObj.value="";
       return;
     }
   }
</SCRIPT>
 <form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
   <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
            <TR>
              <TD align="center" nowrap class="td">往来单位</TD>
              <td nowrap class="td" colspan=3>
              <input type="text" class="edbox" style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=xs_khxyedbean.getFixedQueryValue("dwdm")%>' onchange="customerCodeSelect(this)" >
              <input type="text" name="dwmc"  style="width:260" value='<%=xs_khxyedbean.getFixedQueryValue("dwmc")%>' class="edbox"  onchange="customerNameSelect(this)" >
              <INPUT TYPE="HIDDEN" NAME="dwtxid" value='<%=xs_khxyedbean.getFixedQueryValue("dwtxid")%>'>
              <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',fixedQueryform.dwtxid.value)">
              <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value='';">
              </td>
             </tr>
              <TR>
              <td align="center" nowrap class="td">信誉额度</td>
              <td class="td" nowrap>
              <INPUT class="edbox"  style="WIDTH: 60px" name="xyed$a" value='<%=xs_khxyedbean.getFixedQueryValue("xyed$a")%>'  onKeyDown="return getNextElement();">--
              <INPUT class="edbox"  style="WIDTH: 60px" name="xyed$b" value='<%=xs_khxyedbean.getFixedQueryValue("xyed$b")%>'  onKeyDown="return getNextElement();">
              </td>
              <td align="center" nowrap class="td">信誉等级</td>
              <td class="td" nowrap>
              <INPUT class="edbox"  style="WIDTH: 60px" name="xydj$a" value='<%=xs_khxyedbean.getFixedQueryValue("xydj$a")%>'  onKeyDown="return getNextElement();">--
              <INPUT class="edbox"  style="WIDTH: 60px" name="xydj$b" value='<%=xs_khxyedbean.getFixedQueryValue("xydj$b")%>'  onKeyDown="return getNextElement();">
              </td>
            </TR>
             <TR>
             <td align="center" nowrap class="td">回款天数</td>
              <td class="td" nowrap>
              <INPUT class="edbox"  style="WIDTH: 60px" name="hkts$a" value='<%=xs_khxyedbean.getFixedQueryValue("hkts$a")%>'  onKeyDown="return getNextElement();">--
              <INPUT class="edbox"  style="WIDTH: 60px" name="hkts$b" value='<%=xs_khxyedbean.getFixedQueryValue("hkts$b")%>'  onKeyDown="return getNextElement();">
             </td>
             <td align="center" nowrap class="td">地区号</td>
              <td class="td" nowrap>
              <INPUT class="edbox"  style="WIDTH: 60px" name="areacode$a" value='<%=xs_khxyedbean.getFixedQueryValue("areacode$a")%>'  onKeyDown="return getNextElement();">--
              <INPUT class="edbox"  style="WIDTH: 60px" name="areacode$b" value='<%=xs_khxyedbean.getFixedQueryValue("areacode$b")%>'  onKeyDown="return getNextElement();">
             </td>
             </tr>
             <tr>
             <td align="center" nowrap class="td">业务员</td>
              <td class="td" nowrap>
              <INPUT class="edbox"  style="WIDTH:120px" name="xm" value='<%=xs_khxyedbean.getFixedQueryValue("xm")%>'  onKeyDown="return getNextElement();">
             </td>
             </TR>
             <TR>
             <td align="center" nowrap class="td"></td>
              <td class="td" nowrap></td>
             </TR>
            <TR>
              <TD colspan="4" nowrap class="td" align="center">
                <INPUT class="button" onClick="sumitFixedQuery(<%=xs_khxyedbean.FIXED_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
              </TD>
            </TR>
    </TABLE>
  </DIV>
</form>
<%out.print(retu);%>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD>
      <iframe id="interframe1" src=""  width="300" height="200" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>