<%@ page contentType="text/html; charset=UTF-8"%>
<%!
  java.util.Hashtable mapSxz = new java.util.Hashtable();
  /**
   * 解析属性值表达式
   * @param sxz 数据集中的属性值
   */
  private void parseProperty(String sxz)
  {
    if(mapSxz.size() > 0)
      mapSxz.clear();
    if(sxz == null)
      return;
    //分解属性值
    String[] sxzs = engine.util.StringUtils.parseString(sxz, "()");
    String key = null,  value = null;
    for(int i=0; i<sxzs.length; i++)
    {
      if(i%2 == 0)
        key = sxzs[i].trim();
      else
        mapSxz.put(key, sxzs[i].trim());
    }
  }
%><%
  String   sxz = request.getParameter("sxz");
  String[] exps = request.getParameterValues("exp");    //换算公式数组
  String[] inputName = request.getParameterValues("srcVar");    //输入框数组
  String   srcFrm    = request.getParameter("srcFrm");    //传递的原form的名称
  String   methodName = request.getParameter("method");
  if(exps == null || inputName == null)
    return;
%><script language="javascript"><%
synchronized(mapSxz){
  parseProperty(sxz);
  int length = inputName.length > exps.length ? exps.length : inputName.length;
  engine.util.MessageFormat msgformat = new engine.util.MessageFormat();
  for(int i=0; i<exps.length; i++)
  {
    String inputFull =  new StringBuffer("parent.").append(srcFrm!=null ? srcFrm+"." : "")
           .append(inputName[i]).append(".value=").toString();
    msgformat.applyPattern(exps[i]);
    String[] argNames = msgformat.getArgumentNames();
    for(int j=0; argNames!=null && j<argNames.length; j++)
    {
      if(mapSxz.get(argNames[j])==null)
        mapSxz.put(argNames[j], "0");
    }
    String value = msgformat.format(mapSxz);
    out.print("try{ textValue = ");
    out.print(value==null || value.length()==0 ? "''" : value);
    out.print(";");
    out.print(inputFull);
    out.print("textValue+''=='Infinity'? '':textValue; } catch(err){");
    out.print(inputFull);
    out.print("'';}");
  }
  if(methodName != null && methodName.length() > 0)
    out.print("parent."+ methodName +";");
}
%>
</script>