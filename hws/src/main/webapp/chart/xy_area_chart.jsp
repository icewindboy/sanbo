<%@ page contentType="text/html; charset=UTF-8" %><%request.setCharacterEncoding("UTF-8");%>
<%@ page import = "engine.erp.chart.WebHitChart" %>
<%@ page import = "engine.erp.chart.WebHitDataSet" %>
<%@ page import = "java.io.PrintWriter" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.Iterator" %>
<%

        //返回文件的路径
	String filename = WebHitChart.generateXYAreaChart(session, new PrintWriter(out));

	String graphURL = request.getContextPath() + "/servlet/DisplayChart?filename=" + filename;
        System.out.println(filename);
        System.out.println(graphURL);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="sample.css" type="text/css"/>
<title>Timeseries Stacked Area Chart Example</title>
</head>
<body>
<table border=0 width="100%">
	<tr>
       <td align="center">
       <br><br>
	<img src="<%= graphURL %>" width=500 height=300 border=0  usemap="#<%= filename %>" alt="">
	<table border=0 cellpadding=2 width=400>
		<tr>
		<td align=left>&nbsp;</td>
		<td align=left>&nbsp;</td>
		</tr>
		<tr>

		<td align=right><a href="index.html">Back to the home page</a></td>
		</tr>
	</table>
	</td>
	</tr>
</table>
</body>
</html>
