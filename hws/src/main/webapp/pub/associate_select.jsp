<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%><%
  String lookupName = request.getParameter("lookup");
  String selectName = request.getParameter("select");
  String parentid = request.getParameter("parentid");
  String field = request.getParameter("field");
  String selectid = request.getParameter("selectid");
  boolean isAddNull = request.getParameter("addnull")!= null && request.getParameter("addnull").equals("1");
  if(lookupName == null || lookupName.length() == 0 ||
     selectName == null || selectName.length() == 0 ||
     parentid == null || parentid.length() == 0 ||
     field == null || field.length() == 0)
    return;
%><script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
function resetSelect(objName)
{
  var obj = parent.FindSelectObject(objName);
  obj.RemoveAll();
  SetDestSelectObject(obj);
}
<%
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, lookupName);
  synchronized(personBean){
    personBean.regConditionData(field, new String[]{parentid});
    out.println("resetSelect('"+ selectName +"')");
    if(isAddNull)
      out.println("AddSelectItem('','');");
    out.println(personBean.getList(selectid, field, parentid));//AddSelectItem('52','英镑');AddSelectItem('66','测试');
  }
%>
</script>