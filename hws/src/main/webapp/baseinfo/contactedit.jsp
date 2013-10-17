<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<%@ page import="engine.erp.baseinfo.*, engine.erp.system.*, engine.common.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "corplist";
%><%
  if(!loginBean.hasLimits("corplist", request, response))
    return;
  B_Corp corpBean=B_Corp.getInstance(request);
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
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  var name =  form1.xm.value.trim();
  if(name == '')
  {
    alert("姓名不能为空！");
    return;
  }

  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}

function backList()
{
  location.href='corpedit.jsp';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar"><tr>
    <td NOWRAP align="center"></td>
  </tr></table>
<%String retu = corpBean.doService(request, response);
  if(retu.indexOf("backList()")>-1)
   {
     out.print(retu);
     return;
  }
  B_Area areaBean = B_Area.getInstance(request);
  B_Person personBean = B_Person.getInstance(request);
  B_Country countryBean = B_Country.getInstance(request);
  boolean isSave = loginBean.hasLimits(pageCode, op_add) || loginBean.hasLimits(pageCode, op_edit);
  String rowClass = isSave ? "edbox" : "edline";
  String readOnly = isSave ? "" : " readonly";

  String curUrl = request.getRequestURL().toString();
  RowMap row = corpBean.getDetailRowinfo();
  RowMap m_RowInfo = corpBean.getMasterRowinfo();
  EngineDataSet ds = corpBean.getDetailTable();
  

%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HidDEN" NAME="operate" value="">
	<table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
    <tr valign="top">
      <td width="400"><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
          <tr>
            <td class="activeVTab">联系人信息</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=2 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="2" CELLPADDING="0" BORDER="0" width="100%" bgcolor="#f0f0f0">
                <tr>
                  <td width="14%" height="20" noWrap class="tdTitle">&nbsp;所属单位 </td>
                  <td colspan="3" noWrap class="td"><input type="text" name="dwdm" value='<%=m_RowInfo.get("dwmc")%>'  style="width:390" class="edline" readonly onKeyDown="return getNextElement();">
                  </td>
                </tr>
                <tr>
                  <td height="21" noWrap class="tdTitle">姓名</td>
                  <td width="35%" noWrap class="td">
                  <input type="text" name="xm" value='<%=row.get("xm")%>' maxlength='<%=ds.getColumn("xm").getPrecision()%>' style="width:160" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();">
                  </td>
                  <td width="13%" noWrap class="tdTitle">称呼</td>
                  <td width="38%" noWrap class="td"><input type="text" name="ch" value='<%=row.get("ch")%>' maxlength='<%=ds.getColumn("ch").getPrecision()%>' style="width:160" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                </tr>
              
                <tr>
                  <td noWrap class="tdTitle">性别</td>
                  <td noWrap class="td"><input type="radio" name="sex" value="1" <%=row.get("sex").equals("1")?"checked":""%>>
                    男 <input type="radio" name="sex" value="0" <%=row.get("sex").equals("0")?"checked":""%>>
                    女 </td>
                  <td noWrap class="tdTitle">职务</td>
                  <td noWrap class="td"><input type="text" name="zw" value='<%=row.get("zw")%>' maxlength='<%=ds.getColumn("zw").getPrecision()%>' style="width:160" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">&nbsp;办公电话&nbsp;</td>
                  <td noWrap class="td"><input type="text" name="bgdh" value='<%=row.get("bgdh")%>' maxlength='<%=ds.getColumn("bgdh").getPrecision()%>' style="width:160" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                  <td noWrap class="tdTitle">传真</td>
                  <td noWrap class="td"><input type="text" name="cz" value='<%=row.get("cz")%>' maxlength='<%=ds.getColumn("cz").getPrecision()%>' style="width:160" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">移动电话</td>
                  <td noWrap class="td"><input type="text" name="yddh" value='<%=row.get("yddh")%>' maxlength='<%=ds.getColumn("yddh").getPrecision()%>' style="width:160" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                  <td noWrap class="tdTitle">传呼</td>
                  <td noWrap class="td"><input type="text" name="chhm" value='<%=row.get("chhm")%>' maxlength='<%=ds.getColumn("chhm").getPrecision()%>' style="width:160" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">家庭电话</td>
                  <td noWrap class="td"><input type="text" name="jtjh" value='<%=row.get("jtjh")%>' maxlength='<%=ds.getColumn("jtjh").getPrecision()%>' style="width:160" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                  <td noWrap class="tdTitle">&nbsp;婚姻状况&nbsp;</td>
                  <td noWrap class="td"><input type="radio" name="isMarry" value="1"<%=row.get("isMarry").equals("1")?" checked":""%>>
                    已婚
                    <input type="radio" name="isMarry" value="0"<%=row.get("isMarry").equals("0")?" checked":""%>>
                    未婚</td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">生日</td>
                  <td noWrap class="td"><input type="text" name="sr" value='<%=row.get("sr")%>' maxlength='<%=ds.getColumn("sr").getPrecision()%>' style="width:130" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();">
                    <%if(isSave){%><A href="#"><IMG title=选择日期 onClick="selectDate(sr);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A><%}%></td>
                  <td noWrap class="tdTitle">直接上级</td><%String disable = isSave ? "0" : "1";%>
                  <td noWrap class="td"><pc:select name="sjID" addNull="1" style="width:160" disable="<%=disable%>" className="<%=rowClass%>">
                    <%=corpBean.getLinkmanList(row.get("sjID"))%></pc:select></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">详细地址</td>
                  <td colspan="3" noWrap class="td"><input type="text" name="xxdz" value='<%=row.get("xxdz")%>' maxlength='<%=ds.getColumn("xxdz").getPrecision()%>' style="width:390" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">电子邮件</td>
                  <td noWrap class="td"><input type="text" name="dzyj" value='<%=row.get("dzyj")%>' maxlength='<%=ds.getColumn("dzyj").getPrecision()%>' style="width:160" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                  <td noWrap class="tdTitle">邮政编码</td>
                  <td noWrap class="td"><input type="text" name="yzbm" value='<%=row.get("yzbm")%>' maxlength='<%=ds.getColumn("yzbm").getPrecision()%>' style="width:160" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">备注</td>
                  <td colspan="3" noWrap class="td"><textarea name="bz" rows="4" cols="61"<%=readOnly%>><%=row.get("bz")%></textarea></td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <table CELLSPACING=0 CELLPADDING=0 width="100%">
          <tr>
            <td noWrap class="tableTitle"><br>
            <%if(isSave){%>
              <input name="button1" type="button" class="button" onClick="sumitForm(<%=Operate.DETAIL_RE_ADD%>);" value="确定添加(N)">
              <pc:shortcut key="n" script='<%="sumitForm("+ Operate.DETAIL_RE_ADD +")"%>'/>
              <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.DETAIL_POST%>);" value="确定返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.DETAIL_POST +")"%>'/>
            <%}%><input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C) ">
              <pc:shortcut key="c" script='backList();'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
</body>
</html>