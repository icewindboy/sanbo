<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,
engine.util.StringUtils,
engine.html.*,java.util.ArrayList,engine.report.util.ReportData"
%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%>
<%
  //engine.erp.store.xixing.B_SaleOutStore b_SaleOutStore = engine.erp.store.xixing.B_SaleOutStore.getInstance(request);
  //String pageCode = "outputlist";
  //String retu = b_SaleOutStore.doService(request, response);
  String cpid = request.getParameter("cpid");
  String xsj = request.getParameter("xsj")==null?"":request.getParameter("xsj");
  /*EngineDataSet list = b_SaleOutStore.getCertifyCardTable();
  //list.first();
  int count = list.getRowCount();
  if (count==0) return;
  */
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  prodBean.regData(new String[]{cpid});
  RowMap  prodRow= prodBean.getLookupRow(cpid);
  String hh = prodRow.get("hh");
  String pm = prodRow.get("pm");
  String ks = prodRow.get("ks");
  ks = hh.equals("")||ks.equals("")?ks:"("+ks+")";
  String gg = prodRow.get("gg");
  String abc = prodRow.get("abc");
  String txm = prodRow.get("txm");
  String bz = prodRow.get("bz");
  String zxbz = prodRow.get("zxbz");
  String[] bzList = StringUtils.parseString(bz, "\r\n");
  String[] zxbzList = StringUtils.parseString(zxbz, "\r\n");
  int bzsize = bzList.length;
  int zxbzsize = zxbzList.length;

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<title>合格证打印</title>
<style>
.font {
  font-size:10px; font-weight:bold;
}
</style>
</head>
<body onLoad="window.print();window.close();">
<div style="position: absolute; top:0; left:0; z-index: 1;">
  <table border="0" cellpadding="0" cellspacing="0">
   <tr><td>
<div style="height:210">
  <table border="0" cellpadding="0" cellspacing="0">
   <tr>
     <td height="10" nowrap class="font">产品执行标准:
      <%for (int j = 0 ;j < zxbzsize; j++){%>
        <%=(j==0?"":"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")%>
        <%=zxbzList[j]%>
        <%}%>
     </td>
   </tr>
   <tr><td height="10" nowrap class="font">品名：<%=pm%></td></tr>
   <tr><td height="10" nowrap class="font">货号：<%=hh+ks%></td></tr>
   <%for (int i =0; i<bzsize; i++) {%>
       <tr><td height="10" nowrap class="font"><%=bzList[i]%></td></tr>
  <%}%>
  </table></div></td></tr>
    <tr>
      <td colspan="2"><object align="left" classid="clsid:D9347033-9612-11D1-9D75-00C04FCC8CDC" codebase="MSBCODE9.OCX" id="BarCodeCtrl1" width="168" height="70">
          <param name="Style" value="2">
          <param name="SubStyle" value="0">
          <param name="Validation" value="1">
          <param name="LineWeight" value="3">
          <param name="Direction" value="0">
          <param name="ShowData" value="1">
          <param name="Value" value="<%=txm%>"><!--条形码号码-->
          <param name="ForeColor" value="0">
          <param name="BackColor" value="16777215">
        </object></td>
    </tr>
    <tr><td height="5" nowrap class="font"></tr>
    <tr><td height="10" nowrap class="font" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=abc%>: 统一售价：￥<%=xsj%></tr>
  </table>
</div>
</body>
</html>
