<%@ page contentType="text/html; charset=UTF-8" isErrorPage="true"%><%
  engine.util.log.LogHelper log = engine.util.log.LogHelper.getLogHelper("jsp 404 error");
  log.error(request.getRequestURL(), exception);
%>
<html><head><title>信息</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=request.getContextPath()%>/scripts/public.css" type="text/css">
</head>
<script language="javascript" src="<%=request.getContextPath()%>/scripts/validate.js"></script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE cellSpacing='0' cellPadding='0' height="100%" align=center border=0>
<tr><td valign="middle">
<TABLE cellSpacing='0' cellPadding='0' width='372' align=center border=0>
  <TD vAlign=top align=left width=16 rowSpan=2><IMG height=187
      src="<%=request.getContextPath()%>/images/left.gif" width=16></TD>
  <TD height=175> <TABLE cellSpacing=0 cellPadding=0 width=348 border=0>
      <TR>
        <TD height=144> <DIV align=center>
            <TABLE id=AutoNumber1 style="BORDER-COLLAPSE: collapse" borderColor='#e0e1db' height=107 cellSpacing=0 cellPadding=0 width=314 border=1>
              <TR>
                <TD bgColor='#f5f2ed'><TABLE cellSpacing=0 cellPadding=0 width=314 border=0>
                    <TR>
                      <TD align=middle width=85><IMG height=100 src="<%=request.getContextPath()%>/images/ren.gif" width=78 border=0></TD>
                      <TD width=168>需要相关的业务流程配置,才能使用</TD>
                      <TD width=61><IMG height=35 src="<%=request.getContextPath()%>/images/dot.gif" width=20 border=0></TD>
                    </TR>
                  </TABLE></TD>
              </TR>
            </TABLE>
          </DIV></TD>
      </TR>
      <TR>
        <TD valign="top" align="center" height=31><input name="button" type="button" class="button"
            onclick="history.back()" value=" 返回 "></TD>
      </TR>
    </TABLE></TD>
    <TD valign=top width=8 rowSpan=2><IMG height=187 src="<%=request.getContextPath()%>/images/right.gif" width=8></TD>
  </TR>
  <TR>
    <TD width=348 height=12><IMG height=12 src="<%=request.getContextPath()%>/images/di.gif" width=348></TD>
  </TR>
</TABLE>
</td></tr></TABLE>
</BODY>
</HTML>