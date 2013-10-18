<%@ page contentType="text/html; charset=UTF-8" import="engine.dataset.*,java.util.*,engine.erp.baseinfo.*,engine.action.*,engine.project.*"%>
<%@ include file="../pub/init.jsp"%>
<html>
<head>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%
  if(!loginBean.hasLimits("propertylist", request, response))
    return;//权限管理
  engine.erp.baseinfo.SalePropertiesBean propertylist=engine.erp.baseinfo.SalePropertiesBean.getInstance(request);//创建实例
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<script language="javascript">
function toDetail(){
  location.href='enumvaluedit.jsp';
}
function hideFrameNoFresh(){
  hideFrame('detailDiv');
  }
function showInterFrame(oper, rownum){
  var url = "enumvaluedit.jsp?operate="+oper+"&rownum="+rownum;
  document.all.interframe1.src = url;
  showFrame('detailDiv',true,"",true);
}
function hideInterFrame()//隐藏FRAME
 {
 hideFrame('detailDiv');
  form1.submit();
 }
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function showSearchFrame(){
  showFrame('detailDiv', true, "", true);
}
</script>
<%
  //加载网页前先执行 参数operate=0 src=../main.jsp
  String retu = propertylist.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
%>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
<TABLE  BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar" width="60%">
  <TR>
    <TD NOWRAP align="center">枚举值定义</TD>
  </TR>
</TABLE>
 <table BORDER="0" CELLPADDING="0" CELLSPACING="0" align="center" width="60%">
  <tr valign="center"><TD align="right" ><input name="button22" type="button" class="button" onClick="location.href='propertyeidt.jsp'" value=" 返回 "></TD></tr>
   <tr valign="center">
     <td ><table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                <tr class="tableTitle">
                  <td>枚举ID</td>
                  <td>枚举值</td>
                  <td  nowrap align="center"><input name="image" class="img" type="image" title="新增" onClick="showInterFrame(<%=SalePropertiesBean.ENUM_ADD%>,-1)" src="../images/add.gif" border="0">
                  </td>
                </tr>
                  <%for(int i=0;i<propertylist.enumkey.size();i++){ %>
                  <tr onDblClick="showInterFrame(<%=SalePropertiesBean.ENUM_EDIT%>,<%=i%>)">
                  <td class="td" nowrap><%=propertylist.enumkey.get(i)%></td>
                  <td class="td"><%=propertylist.enumvalue.get(i)%></td>
                  <td class="td" nowrap align="center"><input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=SalePropertiesBean.ENUM_EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
                  <input name="image3" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=SalePropertiesBean.ENUM_DEL%>,<%=i%>)" src="../images/del.gif" border="0">
                  </td>
                </tr>
                 <%}%>
                 <tr>
                  <td class="td">&nbsp;</td>
                  <td class="td"></td>
                  <td class="td"></td>
                </tr>
                </table>
            </td>
          </tr>
        </table>
</form>
<script LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
</script>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="300" height="150" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</BODY>
</Html>
