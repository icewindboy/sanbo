<%@ page language="java" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	response.sendRedirect(path + "/login.jsp");
%>
