<%@ page contentType="text/html; charset=UTF-8" %><%request.setCharacterEncoding("UTF-8");%>
<%@ page import = "engine.erp.chart.WebHitChart" %>
<%@ page import = "engine.erp.chart.WebHitDataSet" %>
<%@ page import = "java.io.PrintWriter" %>
<%@ page import = "java.text.SimpleDateFormat" %>
<%@ page import = "java.text.ParseException" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.Date" %>
<%@ page import = "java.util.Iterator" %>
<%@ page import = "java.util.Locale" %>
<%
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
	String sDate = request.getParameter("hitDate");
        String series = request.getParameter("series");
        out.print(series);
	if (sDate == null) sDate = "All";
	Date dDate = null;
	try {
		dDate = sdf.parse(sDate);
	} catch (ParseException e) {
		//  Leave at null
	}
	String filename = WebHitChart.generateBarChart(dDate, session, new PrintWriter(out));
	String graphURL = request.getContextPath() + "/servlet/DisplayChart?filename=" + filename;
	ArrayList dateList = WebHitDataSet.getDateList();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="sample.css" type="text/css"/>
<title>Bar Chart Example</title>
</head>
<body>
<table border=0 width="100%">
	<tr>
	<td align="center">


	<img src="<%= graphURL %>" width=500 height=300 border=0 usemap="#<%= filename %>"  alt="">



	<table bordercolordark="FFFFFF" bordercolorlight="000000" width="400"
                cellpadding="20" cellspacing="0" border="1" class="panel">
	<tr><td>
		<table border=0 cellpadding=2 width=100%>
		<form method=POST action="bar_chart.jsp">
		<tr valign=top>
			<td><b>Hit Date</b></td>
			<td>
				<select name=hitDate class=pullDown>
				<option>All</option>
<%				Iterator iter = dateList.listIterator();
				while (iter.hasNext()) {
					Date optionDate = (Date)iter.next();
					if (optionDate.equals(dDate)) { %>
						<option selected><%= sdf.format(optionDate) %></option>
<%					} else { %>
						<option><%= sdf.format(optionDate) %></option>
<%					} %>
<%				} %>
				</select>
			</td>
			<td>
				<input type=image src="../images/button_refresh.png" width=80 height=22 name=refresh>
			</td>
		</tr>
		</form>
		</table>
	</td></tr>
	</table>
	<table border=0 cellpadding=2 width=400>
		<tr>

		<td align=right><a href="index.html">Back to the home page</a></td>
		</tr>
	</table>
	</td>
	</tr>
</table>
</body>
</html>
