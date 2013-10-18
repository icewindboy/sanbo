<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%><%
  String   lookupName = request.getParameter("lookup");
  String[] inputName = request.getParameterValues("srcVar");    //输入框数组
  String[] fieldName = request.getParameterValues("fieldVar");    //字段名称
  String[] idValues  = request.getParameterValues("idVar");
  String   srcFrm    = request.getParameter("srcFrm");    //传递的原form的名称
  String   methodName = request.getParameter("method");
  if(lookupName == null || lookupName.length() == 0 ||
     inputName == null || fieldName == null ||
     idValues == null)
    return;
%><script language="javascript"><%
  engine.project.LookUp lookupBean = engine.project.LookupBeanFacade.getInstance(request, lookupName);
  synchronized(lookupBean){
    lookupBean.regData(idValues);
    engine.dataset.RowMap row = lookupBean.getLookupRow(idValues);
    int length = inputName.length > fieldName.length ? fieldName.length : inputName.length;
    for(int i=0; i< length; i++)
    {
      String value = row.get(fieldName[i]);
      out.print("parent."); out.print(srcFrm); out.print("."); out.print(inputName[i]);
      out.print(".value='"); out.print(value); out.print("';");
    }
  }
  if(methodName != null && methodName.length() > 0)
    out.print("parent."+ methodName +";");
%>
</script>
