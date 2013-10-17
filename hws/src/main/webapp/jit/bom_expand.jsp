<%@ page contentType="text/html; charset=UTF-8" %><%
  String nodeid = request.getParameter("nodeid");
  String path = request.getParameter("path");
  if(nodeid == null || nodeid.length() == 0 || path == null || path.length() == 0)
    return;
  engine.erp.jit.B_Bom bomBean = engine.erp.jit.B_Bom.getInstance(request);
  String info = bomBean.expandChilds(nodeid, path);
  if(info != null){
    out.println(info);
    return;
  }
  String node = request.getParameter("node");
%><script language="javascript">
<%if(node == null || !node.equals("1")){%>
  var imgLineObj = parent.document.all['img_line_<%=path%>'];
  parent.changeNodeClick(imgLineObj, 'tr_cont_<%=path%>', 'img_line_<%=path%>', 'img_icon_<%=path%>');
  var tdContextObj = parent.document.all['td_cont_<%=path%>'];
  tdContextObj.innerHTML = "<%bomBean.getChildrenHTML(pageContext, nodeid, path);%>";
  imgLineObj.onclick();
<%} else {%>
  var tdNodeObj = parent.document.all['td_node_<%=path%>'];
  tdNodeObj.innerHTML = "<%bomBean.getNodeHTML(pageContext, nodeid, path);%>";
  var tdContextObj = parent.document.all['td_cont_<%=path%>'];
  tdContextObj.innerHTML = "<%bomBean.getChildrenHTML(pageContext, nodeid, path);%>";
<%}%>
</SCRIPT>