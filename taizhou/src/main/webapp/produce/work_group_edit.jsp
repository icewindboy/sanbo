<%--生产工作组编辑页面--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.produce.B_WorkGroup b_WorkGroupBean = engine.erp.produce.B_WorkGroup.getInstance(request);
  String pageCode = "work_group";
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function backList()
{
  location.href='work_group.jsp';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<%String retu = b_WorkGroupBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp workshopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp technicsNameBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_NAME);//工序名称
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_WorkGroupBean.getMaterTable();
  EngineDataSet list = b_WorkGroupBean.getDetailTable();
  RowMap masterRow = b_WorkGroupBean.getMasterRowinfo();
  RowMap[] detailRows= b_WorkGroupBean.getDetailRowinfos();
  boolean isEdit = b_WorkGroupBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);//在修改状态,并有修改权限
  boolean isCanDelete = !b_WorkGroupBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//不在增加状态,在修改状态,并有删除权限
  String edClass = !isEdit ? "class=edline" : "class=edbox";
  String detailClass = !isEdit ? "class=ednone" : "class=edFocused";
  String detailClass_r = !isEdit ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEdit ? "":" readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;"onKeyDown="return onInputKeyboard();">
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="400">
    <tr valign="top">
      <td>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="400" align="center">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="400" bgcolor="#f0f0f0" align="center">
               <%if(isEdit)
                personBean.regConditionData(ds,"deptid");%>
                <tr>
                  <td noWrap class="tdTitle">工作组编号</td>
                  <td noWrap class="td"><input type="text" name="gzzbh" value='<%=masterRow.get("gzzbh")%>' maxlength='10' style="width:110" onKeyDown="return getNextElement();" <%=edClass%> <%=readonly%>></td>
                  <td noWrap class="tdTitle">工作组名称</td>
                  <td noWrap class="td"><input type="text" name="gzzmc" value='<%=masterRow.get("gzzmc")%>' maxlength='<%=ds.getColumn("gzzmc").getPrecision()%>' style="width:110" <%=edClass%>  onKeyDown="return getNextElement();"<%=readonly%>>
                  </td>
                  </tr>
                  <tr>
                  <td noWrap class="tdTitle">所属车间</td>
                  <td noWrap class="td">
                    <%String sumit = "if(form1.deptid.value != '' && form1.deptid.value != '"+masterRow.get("deptid")+"')sumitForm("+b_WorkGroupBean.PUT_DEPT+");";%>
                    <%if(!isEdit) out.print("<input type='text' value='"+workshopBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="deptid" className="edFocused" addNull="1" style="width:110" onSelect="<%=sumit%>">
                      <%=workshopBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">工作组描述</td>
                  <td noWrap class="td"><input type="text" name="gzzms" value='<%=masterRow.get("gzzms")%>' maxlength='<%=ds.getColumn("gzzms").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                </tr>
                <%
                  int width = (detailRows.length > 6 ? detailRows.length : 6)*23 + 66;
                %>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:400;height=250;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="400" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                          <td height='20' align="center" nowrap><%if(isEdit){%>
                         <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add.gif" border="0">
                          <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/><%}%>
                        </td>
                        <td nowrap>员工姓名</td>
                        <td nowrap>工序名称</td>
                        <td nowrap>人员基数</td>
                      </tr>
                    <%technicsNameBean.regData(list,"gymcid");
                      int i=0;
                      RowMap detail = null;
                      String[] widthName = new String[]{"ryjs"};
                      int[] widthMin = new int[]{90};
                      int[] widths = b_WorkGroupBean.getMaxStyleWidth(detailRows, widthName, widthMin);
                      list.first();
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String gymcName = "gymcid_"+i;
                    %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <%if(isEdit){%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                        <td class="td" nowrap align="center">
                        <%String personidName="personid_"+i;%>
                        <%if(!isEdit) out.print("<input type='text' value='"+personBean.getLookupName(detail.get("personid"))+"' style='width:110' class='ednone' readonly>");
                        else {%>
                        <pc:select className="edFocused" name="<%=personidName%>" addNull="1" style="width:120">
                        <%=personBean.getList(detail.get("personid"), "deptid", masterRow.get("deptid"))%></pc:select>
                        <%}%>
                        </td>
                        <td class="td" nowrap>
                        <%if(!isEdit) out.print("<input type='text' value='"+technicsNameBean.getLookupName(detail.get("gymcid"))+"' style='width:110' class='ednone' readonly>");
                        else {%>
                        <pc:select addNull="1" className="edFocused" name="<%=gymcName%>"  style="width:100">
                        <%=technicsNameBean.getList(detail.get("gymcid"))%></pc:select>
                        <%}%>
                        </td>
                        <td class="td" nowrap align="center"><input type="text" <%=detailClass_r%> style="width:<%=widths[0]%>" onKeyDown="return getNextElement();" id="ryjs_<%=i%>" name="ryjs_<%=i%>" value='<%=detail.get("ryjs")%>' maxlength='<%=list.getColumn("ryjs").getPrecision()%>' onchange="js_onchange(<%=i%>, true)" <%=readonly%>></td>
                        </tr>
                        <%
                         list.next();
                          }
                        for(; i < 6; i++){
                        %>
                      <tr  id="rowinfo_<%=i%>">
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                      </tr>
                      <%}%>
                    </table></div>
              </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
        <table CELLSPACING=0 CELLPADDING=0 width="400" align="center">
          <tr>
            <td colspan="3" noWrap class="tableTitle">
             <%if(isEdit){%><input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
                         <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +")"%>'/><%}%>
              <%if(isCanDelete){%><input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value=" 删除(D)">
              <pc:shortcut key="d" script='delMaster();'/><%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" 打印 "--%>
               <input name="btnback" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 返回(C)">
               <pc:shortcut key="c" script='parent.hideFrameNoFresh()'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
  function delMaster(){
    if(confirm('是否删除该记录？'))
      sumitForm(<%= Operate.DEL%>,-1);
  }
  function js_onchange(i, isBigUnit)
  {
    var ryjsObj = document.all['ryjs_'+i];
    if(isNaN(ryjsObj.value)){
      alert("输入的人员基数非法");
      ryjsObj.focus();
      return;
    }
  }
</script>
<%out.print(retu);%>
</BODY>
</Html>
</body>
</html>
