<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList,engine.report.util.ReportData"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%>
<%
  engine.erp.store.B_SaleOutStore saleOutStoreBean = engine.erp.store.B_SaleOutStore.getInstance(request);
  String pageCode = "outputlist";
  String retu = saleOutStoreBean.doService(request, response);
  ArrayList ds = saleOutStoreBean.getMasterArray();
  ArrayList list = saleOutStoreBean.getDetailArray();
  int pageCount = ds.size();
  ReportData[] rd = new ReportData[pageCount];
  int totalPageNos = ds.size();
  for(int i=0;i<ds.size();i++)
  {
    RowMap mr = (RowMap)ds.get(i);
    String cpbm = mr.get("cpbm");
    RowMap[] mrs =new RowMap[1];//主
    mr.put("pageNo", "第" + String.valueOf(i+1)+ "页" + "/"+  "共" + String.valueOf(totalPageNos) + "页");
    mrs[0]=mr;
    RowMap[] drs = (RowMap[])list.get(i);
    /*
    ArrayList dlist = (ArrayList)list.get(i);
    RowMap[] drs =new RowMap[dlist.size()];//从
    for(int j=0;j<dlist.size();j++)
    {
      RowMap dr = (RowMap)dlist.get(j);
      drs[j] = dr;
      String sl = mr.get("sl0");
    }
    */
    ReportData td = new ReportData();
    td.addReportData("mrs",mrs);
    td.addReportData("drs",drs);
    rd[i] = td;
  }

  session.setAttribute("outputlist_bill_wrapper_print",rd );
  //String location = "<script language='javascript'>location.href='../pub/pdfprint.jsp?operate="+Operate.PRINT_PRECISION+"&code=xs_lading_bill_print&data=xs_lading_bill_print'</script>";
  //out.print(location);
  String ul = "../pub/pdfprint.jsp?operate="+Operate.PRINT_PRECISION+"&code=outputlist_bill_wrapper_print&data=outputlist_bill_wrapper_print";
  ul = response.encodeRedirectURL(ul);
  response.sendRedirect(ul);
%>