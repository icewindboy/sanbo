<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, java.util.*, com.borland.dx.dataset.Locate"%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("personlist", request, response))
    return;
  engine.erp.system.B_Person personBean = engine.erp.system.B_Person.getInstance(request);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.erp.system.B_Role roleBean = engine.erp.system.B_Role.getInstance(request);
  engine.erp.system.B_LimitsInfo limitsInfoBean = engine.erp.system.B_LimitsInfo.getInstance(request);
  engine.erp.baseinfo.B_Wordbook workbookBean = engine.erp.baseinfo.B_Wordbook.getInstance(request);
  engine.erp.baseinfo.B_Store storeBean = engine.erp.baseinfo.B_Store.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<BODY oncontextmenu="window.event.returnValue=true">
<%String retu = personBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  RowMap row = personBean.rowInfo;
  EngineDataSet ds = personBean.dsPerson;
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><tr><td NOWRAP align="center">员工信息</td></tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="limitchange" VALUE="0">
  <INPUT TYPE="HIDDEN" NAME="rolechange" VALUE="0">
  <INPUT TYPE="HIDDEN" NAME="storechange" VALUE="0">
  <INPUT TYPE="HIDDEN" NAME="deptchange" VALUE="0">
	<table BORDER="0" CELLPADDING="0" CELLSPACING="0" width="90%" align="center">
	<tr valign="top"><td>
	<table border="0" cellpadding="0" cellspacing="2">
    <tr valign="top">
      <td width="400">
			  <table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
          <tr>
            <td class="activeVTab">员工信息</td>
					</tr>
				</table>
        <table class="limittreebox" CELLSPACING=0 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="3" BORDER="0" width="100%" bgcolor="#f0f0f0">
                <tr>
                  <td noWrap class="tdTitle">员工编码</td>
                  <td noWrap class="td"><%=row.get("bm")%></td>
                  <td noWrap class="tdTitle">员工姓名</td>
                  <td noWrap class="td"><%=row.get("xm")%></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">登录名</td>
                  <td noWrap class="td"><input type="text" name="username" value="<%=row.get("username")%>" maxlength="<%=ds.getColumn("username").getPrecision()%>" style="width:130" class="edbox"></td>
                  <td noWrap class="tdTitle">部门</td>
                  <td noWrap class="td"><%=deptBean.getLookupName(row.get("deptid"))%></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">电话</td>
                  <td noWrap class="td"><%=row.get("phone")%></td>
                  <td noWrap class="tdTitle">电子邮件</td>
                  <td noWrap class="td"><input type="text" name="email" value="<%=row.get("email")%>" style="width:130" class="ednone" readonly=""></td>
                </tr>
              </table></td>
          </tr>
        </table><br>
     		<table border=0 CELLSPACING=0 CELLPADDING=0>
          <tr><td class="activeVTab">仓库权限</td></tr>
        </table>
        <table class="limittreebox" CELLSPACING=0 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="3" BORDER="0" width="100%" bgcolor="#f0f0f0">
              <%EngineRow storeRow = new EngineRow(personBean.dsStoreHandle, "storeid");
                EngineDataView dsStore = storeBean.getOneTable().cloneEngineDataView();//得到所有的角色信息
                dsStore.first();
                int time_ = dsStore.getRowCount()/4 + (dsStore.getRowCount()%4 > 0 ? 1 : 0);
                boolean isEof_ = false;
                for(int i=0; i<time_; i++)
                {
                  out.print("<tr>");
                  for(int j=0; j<4; j++){
                    out.print("<td noWrap class=td>");
                    if(isEof_)
                      out.print("<td class=td>&nbsp;</td>");
                    else{
                      String storeid = dsStore.getValue("storeid");
                      out.print("<input type=checkbox name=chkstore onChange='chkstore_onchange()' value="+storeid);
                      storeRow.setValue(0, storeid);
                      if(personBean.dsStoreHandle.locate(storeRow, Locate.FIRST))
                        out.print(" checked");
                      out.print(">"+ dsStore.getValue("ckmc") +"</td>");
                    }
                    out.print("</td>");
                    isEof_ = !dsStore.next();
                  }
                  out.print("</tr>");
                }
                dsStore.close();
                for(; time_ < 1; time_++){%>
                <tr>
                  <td class="td">&nbsp;</td>
                  <td class="td">&nbsp;</td>
                  <td class="td">&nbsp;</td>
                  <td class="td">&nbsp;</td>
                </tr>
              <%}%>
              </table>
            </td>
          </tr>
        </table><br>
       <table border=0 CELLSPACING=0 CELLPADDING=0>
         <tr>
           <td class="activeVTab">部门权限</td>
         </tr>
       </table>
       <table class="limittreebox" CELLSPACING=0 CELLPADDING=0 width="100%">
         <tr>
           <td>
             <table CELLSPACING="1" CELLPADDING="3" BORDER="0" width="100%" bgcolor="#f0f0f0">
               <tr><td><%=personBean.deptTreeInfo%></td></tr>
             </table>
           </td>
         </tr>
        </table>
        <table CELLSPACING=0 CELLPADDING=0 width="100%">
        <tr>
          <td noWrap class="tableTitle"><br>
            <input name="button" type="button" class="button" onClick="sumitForm(<%=personBean.OPERATE_POST%>);" value="保存(S)">
            <pc:shortcut key="s" script='<%="sumitForm("+personBean.OPERATE_POST+");"%>'/>
            <input name="button2" type="button" class="button" onClick="location.href='personlist.jsp'" value="返回(C)">
            <pc:shortcut key="c" script="location.href='personlist.jsp'"/>
          </td>
        </tr>
      </table>
		</td></tr></table>
      </td>
      <td width="100%">		<table border=0 CELLSPACING=0 CELLPADDING=0>
          <tr>
            <td class="activeVTab">员工角色</td>
          </tr>
        </table>
        <table class="limittreebox" CELLSPACING=0 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="3" BORDER="0" width="100%" bgcolor="#f0f0f0">
              <%Vector vRoleid = new Vector();
                EngineDataSet personRole = personBean.dsPersonRole;//得到人员角色对照信息
                int locate = Locate.FIRST;
                while(personBean.locateDataSet(personRole, "personid", row.get("personid"), locate))
                {
                  locate = Locate.NEXT;
                  vRoleid.add(personRole.getValue("roleid"));
                }
                EngineDataView role = roleBean.getRoleData();//得到所有的角色信息
                role.first();
                int time = role.getRowCount()/4 + (role.getRowCount()%4 > 0 ? 1 : 0);
                boolean isEof = false;
                for(int i=0; i<time; i++)
                {
                  out.print("<tr>");
                  for(int j=0; j<4; j++){
                    out.print("<td noWrap class=td>");
                    if(isEof)
                      out.print("<td class=td>&nbsp;</td>");
                    else{
                      String roleid = role.getValue("roleid");
                      out.print("<input type=checkbox name=chkrole onChange='chkrole_onchange()' onClick='chklimit_onchange();selectRole(this)' value="+roleid);
                      if(vRoleid.indexOf(roleid) > -1)
                        out.print(" checked");
                      out.print(">"+ role.getValue("rolename") +"</td>");
                    }
                    out.print("</td>");
                    isEof = !role.next();
                  }
                  out.print("</tr>");
                }
                role.close();
                for(; time < 1; time++){%>
                <tr>
                  <td class="td">&nbsp;</td>
                  <td class="td">&nbsp;</td>
                  <td class="td">&nbsp;</td>
                  <td class="td">&nbsp;</td>
                </tr>
              <%}%>
              </table>
            </td>
          </tr>
        </table><br>
			  <table border=0 CELLSPACING=0 CELLPADDING=0>
          <tr>
            <td class="activeVTab">员工权限</td>
          </tr>
        </table>
        <table class="limittreebox" CELLSPACING=0 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="3" BORDER="0" width="100%" bgcolor="#f0f0f0">
                <tr><td><%=personBean.nodeTreeInfo%></td></tr>
              </table>
            </td>
          </tr>
        </table>
			</td>
    </tr>
  </table>
</form>
</BODY>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
function GotoNode(nodeid, isNode)
{
  for(i=0; i<form1.chklimit.length; i++)
  {
    var chkvalue = form1.chklimit[i].value;
    var index = chkvalue.indexOf("_");
    if(index < 0)
      continue;
    if(chkvalue.substring(0, index) == nodeid)
    {
      form1.chklimit[i].checked = !form1.chklimit[i].checked;
      chklimit_onchange();
    }
  }
}
function chklimit_onchange()
{
  form1.limitchange.value="1";
}
function chkrole_onchange()
{
  form1.rolechange.value="1";
}
function chkstore_onchange()
{
  form1.storechange.value="1";
}
function chkdept_onchange()
{
  form1.deptchange.value="1";
}
function selectRole(roleidObj)
{
  lockScreenToWait("处理中, 请稍候！");
  var roleid = roleidObj.value;
  var ischeck = roleidObj.checked;
  <%=personBean.roleLimitScript.toString()%>
  unlockScreenWait()
}
</script>
</Html>