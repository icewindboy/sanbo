<%--印花记录--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%>
<%@ page import="java.math.BigDecimal,engine.erp.baseinfo.BasePublicClass"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%>
<%
  engine.erp.jit.B_Stamp B_StampBean = engine.erp.jit.B_Stamp.getInstance(request);
  String pageCode = "sc_stamp";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
%>
<jsp:include page="../baseinfo/script.jsp" flush="true"/>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function backList()
{
  location.href='sc_stamp.jsp';
}
</script>
<%String retu = B_StampBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = B_StampBean.getMaterTable();
  EngineDataSet list = B_StampBean.getDetailTable();
  RowMap masterRow = B_StampBean.getMasterRowinfo();
  RowMap[] detailRows= B_StampBean.getDetailRowinfos();
  boolean isEnd = false;//B_StampBean.isReport || B_StampBean.isApprove || (!B_StampBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";

%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<script language="javascript">var scaner=parent.scaner;</script>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="760" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">印花记录
            </td>
          </tr>
        </table>
             <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <tr>
                  <td  noWrap class="tdTitle">印花总数</td>
                  <td noWrap class="td">
                    <input type="text"  <%=edClass%> style="width:120"  id="yhzsl" name="yhzsl" value='<%=masterRow.get("yhzsl")%>'  maxlength='<%=ds.getColumn("yhzsl").getPrecision()%>' onKeyDown="return getNextElement();"  >
                  </td>
                  <td noWrap class="tdTitle">新厂总数</td>
                  <td noWrap class="td">
                    <input type="text"  <%=edClass%> style="width:120"  id="xczsl" name="xczsl" value='<%=masterRow.get("xczsl")%>'  maxlength='<%=ds.getColumn("xczsl").getPrecision()%>' onKeyDown="return getNextElement();"  >
                  </td>
                  <td noWrap class="tdTitle">老厂总数</td>
                  <td noWrap class="td">
                    <input type="text"  <%=edClass%> style="width:120"  id="lczsl" name="lczsl" value='<%=masterRow.get("lczsl")%>'  maxlength='<%=ds.getColumn("lczsl").getPrecision()%>' onKeyDown="return getNextElement();"  >
                  </td>
                  <td noWrap class="tdTitle">地毯</td>
                  <td noWrap class="td">
                    <input type="text"  <%=edClass%> style="width:120"  id="dtan" name="dtan" value='<%=masterRow.get("dtan")%>'  maxlength='<%=ds.getColumn("dtan").getPrecision()%>' onKeyDown="return getNextElement();"  >
                  </td>
                  </tr>
                  <tr>
                  <td noWrap class="tdTitle">半高档</td>
                  <td noWrap class="td">
                    <input type="text"  <%=edClass%> style="width:120"  id="bgad" name="bgad" value='<%=masterRow.get("bgad")%>'  maxlength='<%=ds.getColumn("bgad").getPrecision()%>' onKeyDown="return getNextElement();"  >
                  </td>
                  <td noWrap class="tdTitle">普通</td>
                  <td noWrap class="td">
                    <input type="text"  <%=edClass%> style="width:120"  id="putn" name="putn" value='<%=masterRow.get("putn")%>'  maxlength='<%=ds.getColumn("putn").getPrecision()%>' onKeyDown="return getNextElement();"  >
                  </td>
                  <td noWrap class="tdTitle">拉舍尔</td>
                  <td noWrap class="td">
                    <input type="text"  <%=edClass%> style="width:120"  id="lsr" name="lsr" value='<%=masterRow.get("lsr")%>'  maxlength='<%=ds.getColumn("lsr").getPrecision()%>' onKeyDown="return getNextElement();"  >
                  </td>
                </tr>
                 <%/*打印用户自定义信息*/
                int width = (detailRows.length > 4 ? detailRows.length : 4)*23 + 66;
                %>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td height='20' align="center" nowrap>
                           <%if(!isEnd){%>
                          <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/><%}%>
                        </td>
                        <td height='20' nowrap></td>
                        <td height='20' nowrap>单号</td>
                        <td height='20' nowrap>原总数</td>
                        <td nowrap>剩余数量</td>
                        <td nowrap>交货日期</td>
                        <td height='20' nowrap>备注</td>
                      </tr>
                    <%
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++){
                        detail = detailRows[i];
                    %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="djh_<%=i%>" name="djh_<%=i%>" value='<%=detail.get("djh")%>' maxlength='<%=list.getColumn("djh").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="yzsl_<%=i%>" name="yzsl_<%=i%>" value='<%=detail.get("yzsl")%>' maxlength='<%=list.getColumn("yzsl").getPrecision()%>' ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="sysl_<%=i%>" name="sysl_<%=i%>" value='<%=detail.get("sysl")%>' maxlength='<%=list.getColumn("sysl").getPrecision()%>' ></td>
                        <td class="td" nowrap>
                        <input type="text" <%=detailClass_r%> style="width:65" onKeyDown="return getNextElement();" name="jhrq_<%=i%>" id="jhrq_<%=i%>"value='<%=detail.get("jhrq")%>' maxlength='10' onchange="checkDate(this)">
                        <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.jhrq_<%=i%>);"></a>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" id="bz_<%=i%>" name="bz_<%=i%>" value='<%=detail.get("bz")%>' maxlength='<%=list.getColumn("bz").getPrecision()%>'  ></td>
                        </tr>
                        <%
                          list.next();
                      }
                      for(; i < 4; i++){
                     %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                      <td class="td">&nbsp;</td>
                      </tr>
                      <%}%>
                    </table>
                   </div>

                     <tr>
                    <td  noWrap class="tdTitle">备注</td><%--其他信息--%>
                    <td colspan="7" noWrap class="td"><textarea name="memo" rows="3" onKeyDown="return getNextElement();" style="width:660" ><%=masterRow.get("memo")%></textarea></td>
                    </tr>
              </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">
        <tr>
        <td class="td"><b>登记日期:</b><%=masterRow.get("zdrq")%></td>
        <td class="td"></td>
        <td class="td" align="right"><b>制单人:</b><%=masterRow.get("zdr")%></td>
        </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">

              <input name="btnback" type="button" class="button" title="保存(ALT+S)" value="保存(S)" onClick="sumitForm(<%=Operate.POST%>);" >
              <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
              <input name="button3" type="button" class="button" title="删除(ALT+D)" onClick="buttonEventD()" value="删除(D)">
              <pc:shortcut key="d" script="buttonEventD()"/>

              <input name="btnback" type="button" class="button" title="返回(ALT+C)" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script='<%="backList()"%>'/>
              <%if(!B_StampBean.masterIsAdd()){%><input type="button" class="button" value=" 打印 " onclick="location.href='../pub/pdfprint.jsp?code=sc_stamp_edit_bill&operate=<%=B_StampBean.PRINT_BILL%>&a$stampid=<%=masterRow.get("stampid")%>&src=../jit/sc_stamp_edit.jsp'"><%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    function buttonEventD()
    {
     if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);
  }
  </script>
<%if(B_StampBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</body>
</html>

