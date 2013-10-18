<%@ page contentType="text/html; charset=UTF-8" %><%request.setCharacterEncoding("UTF-8");%>
<%@ page import = "engine.erp.chart.WebHitChart" %>
<%@ page import = "engine.erp.chart.WebHitDataSet" %>
<%@ page import = "java.io.PrintWriter" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.Iterator" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="sample.css" type="text/css"/>
<title>Timeseries Chart Example</title>
</head>
<body>
<%

	String section = request.getParameter("section");
	if (section == null ? false : section.equals("All")) section = null;
	String filename = WebHitChart.generateXYChart(section, session, new PrintWriter(out));
	String graphURL = request.getContextPath() + "/servlet/DisplayChart?filename=" + filename;

	ArrayList sectionList = new WebHitDataSet().getSections();
%>
<table border=0 width="100%">
	<tr>
	<td align="center">
	<img src="<%= graphURL %>" width=500 height=300 border=0 usemap="#<%= filename %>">

	<p>The chart shown above has tooltips and drilldown enabled.</p>

	<table bordercolordark="FFFFFF" bordercolorlight="000000" width="400" cellpadding="20" cellspacing="0" border="1" class="panel">
	<tr><td>
		<table border=0 cellpadding=2 width=100%>
		<form method=POST action="xy_chart.jsp">
		<tr valign=top>
			<td><b>Section</b></td>
			<td>
				<select name=section class=pullDown>
				<option>All</option>
<%				Iterator iter = sectionList.listIterator();
				while (iter.hasNext()) {
					String optionSection = (String)iter.next();
					if (optionSection.equals(section)) { %>
						<option selected><%= optionSection %></option>
<%					} else { %>
						<option><%= optionSection %></option>
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
