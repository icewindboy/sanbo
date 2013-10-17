<%@ page contentType="text/html; charset=UTF-8"%><%@ page import="engine.dataset.*"%><%@ include file="../pub/init.jsp"%><%!
  String oldwzlbid = null;
%><%
  String tableid = request.getParameter("tableid");
  String index = request.getParameter("index");
  String wzlbid = request.getParameter("wzlbid");
  if(tableid == null || tableid.length() == 0 || index == null || index.length() == 0 || wzlbid == null)
    return;
  //if(oldwzlbid != null && wzlbid.equals(oldwzlbid))
    //return;
  oldwzlbid = wzlbid;

  engine.erp.common.B_PropertySelect propertyBean = engine.erp.common.B_PropertySelect.getInstance(request);
  EngineDataSet ds = propertyBean.getPropertyData(wzlbid);
  StringBuffer buf = new StringBuffer("var oTB = parent.");
  buf.append(tableid).append("; removeRow(oTB,").append(index).append(");");
  int rowCount = ds.getRowCount();
  ds.first();
  int k =0;
  for(int i=0; i<rowCount; i++)
  {
    if(i%2 == 0)
    {
      buf.append("oTR = oTB.insertRow("+ index +"+" + k +");");
      k++;
    }
    buf.append("addCellCaption(oTR, '");
    buf.append(ds.getValue("sxmc")).append("');");
    buf.append("addCellInput(oTR, 'sx_").append(ds.getValue("dlsxid")).append("');");
    if(i%2 == 0 && i == rowCount-1)
      buf.append("oTd=oTR.insertCell(); oTd.className='td'; oTd=oTR.insertCell(); oTd.className = 'td';");

    ds.next();
  }
  buf.append("location.href='';");
  //System.out.println(buf);
%>
<script language='javascript'>
function removeRow(oTB, index)
{
  while(index < oTB.rows.length)
  {
    if('trColumninfo' == oTB.rows(index).id)
      break;

    oTB.deleteRow(index);
  }
}
function addCellCaption(oTR, caption)
{
  var oTd = oTR.insertCell();
  oTd.className = "td";
  oTd.nowrap = true;
  oTd.innerText = caption;
}
function addCellInput(oTR, name)
{
  var oTd = oTR.insertCell();
  oTd.className = "td";
  oTd.nowrap = true;
  var inputHTML = "<input class='edbox' name='"+ name +"' style='width:160' value=''>";
  oTd.innerHTML = inputHTML;
}
<%=buf.toString()%>
</script>