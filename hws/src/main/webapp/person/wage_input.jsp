<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.*,java.math.BigDecimal,java.util.*"%>
<%
if(!loginBean.hasLimits("wage_input", request, response))
    return;
  engine.erp.person.B_WageInput b_WageInputBean = engine.erp.person.B_WageInput.getInstance(request);
  synchronized(b_WageInputBean){
  String retu = b_WageInputBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet fieldList = b_WageInputBean.getFieldListTable();
  EngineDataSet list = b_WageInputBean.getDetailTable();

  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp personclassBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON_CLASS);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_LIST);

  String curUrl = request.getRequestURL().toString();
  ArrayList opkey = new ArrayList();
  opkey.add("1"); opkey.add("2");opkey.add("3");opkey.add("4");
  opkey.add("5");opkey.add("6");opkey.add("7");opkey.add("8");
  opkey.add("9");opkey.add("10");opkey.add("11");opkey.add("12");
  ArrayList opval = new ArrayList();
  opval.add("1"); opval.add("2");opval.add("3"); opval.add("4");
  opval.add("5"); opval.add("6");opval.add("7"); opval.add("8");
  opval.add("9"); opval.add("10");opval.add("11"); opval.add("12");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};
  String key = "ppdfsgg";
  pageContext.setAttribute(key, list);
  int iPage = loginBean.getPageSize();
  String pageSize = ""+iPage;
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type"  content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<SCRIPT language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value  = row;
  form1.submit();
}
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function executeQuery()
{
  if(fixedQueryform.nf.value==""||fixedQueryform.yf.value=="");
  {
    alert("年份,月份必须都选择才有效!");
    return;
  }
}
function showFixedQuery()
{
  showFrame('fixedQuery',true,"",true);
}
function showImportForm()
{
  showFrame('importForm',true,"",true);
}
function showImportwage()
{
  showFrame('importwage',true,"",true);
}
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD nowrap align="center">工资表</TD>
</TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>"  onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum"  VALUE="">
  <TABLE width="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
    <td class="td" nowrap> <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/> </td>
        <%
          String t="sumitForm("+b_WageInputBean.MONTH_CHANGE+",-1)";//saleOrderBean.DETAIL_CHANGE;
        %>
        <td class="tdTitle" align="right">本年度月份</td>
        <td class="td" >
         <pc:select name="month" addNull="1"  input="0" style="width:50"  onSelect="<%=t%>" >
          <%=b_WageInputBean.listToOption(lists, opkey.indexOf(b_WageInputBean.getMonth()))%>
          </pc:select>
        </td>
      <TD align="right">
      <%

        RowMap[] rows=b_WageInputBean.getDetailRowinfos();

        RowMap masterRow=b_WageInputBean.getMasterRowinfo();
        //b_WageInputBean.init();
        String sfjz = masterRow.get("sfjz");
        if(sfjz.equals("0")){
          String fins = "if(confirm('完成后不能修改,确认要完成吗?')) sumitForm("+b_WageInputBean.OVER+");";
          String sav = "sumitForm("+Operate.POST+");";
      %>
      <%if(!(list.getRowCount()==0)){%>
      <input name="button" type="button" class="button" onClick="if(confirm('完成后不能修改,确认要完成吗?')) sumitForm(<%=b_WageInputBean.OVER%>);" value="完成(F)">
      <pc:shortcut key="f" script="<%=fins%>" /><%}%>
      <%if(rows.length==0){%>
      <INPUT class="button" onClick="showImportForm()" type="button" value="引入工资(W)" name="Import" onKeyDown="return getNextElement();">
      <pc:shortcut key="w" script="showImportForm()" />
      <%}%>
      <%if(!(list.getRowCount()==0)){%>
      <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
      <pc:shortcut key="s" script="<%=sav%>" />
      <input name="button" type="button" class="button" onClick="showImportwage();" value="引计件工资(R)">
      <pc:shortcut key="r" script="showImportwage()" />
      <%}%>
      <%}%>
      <INPUT class="button" onClick="showFixedQuery()" type="button" value="查询(Q)" name="Query" onKeyDown="return getNextElement();">
      <pc:shortcut key="q" script="showFixedQuery()" />
      <input name="button2" type="button" align="Right" class="button" onClick="location.href='<%=b_WageInputBean.retuUrl%>'" value="返回(C)" border="0">
      <%String ret = "location.href='"+b_WageInputBean.retuUrl+"'";%>
      <pc:shortcut key="c" script="<%=ret%>" />
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
       <td nowrap>
       <%if(masterRow.get("sfjz").equals("0")&&(!b_WageInputBean.getMonth().equals(""))){%>
       <input type="hidden" name="multiIdInput" value="" onchange="sumitForm(<%=b_WageInputBean.DETAIL_MULTI_ADD%>)">
       <input name="image" class="img" type="image" title="新增" onClick="PersonMultiSelect('form1','srcVar=multiIdInput&isoff=0+1','undefined','undefined');" src="../images/add.gif" border="0">
       <%}%>
       </td>
      <td nowrap>部门</td>
      <td nowrap>姓名</td>
      <td nowrap>类别</td>
      <%
      ArrayList fieldNameList=new ArrayList();
      StringBuffer gsfield=new StringBuffer();//公式所涉及到的字段的列表
      fieldList.first();
      for(int h=0;h<fieldList.getRowCount();h++)
      {
        String fn=fieldList.getValue("dyzdm");
        out.println("<td nowrap>"+fieldList.getValue("mc")+"</td>");
        String jsgs=fieldList.getValue("jsgs").trim();
        if(!jsgs.equals(""))
        {
          gsfield.append(fieldList.getValue("jsgs")+"|");
        }
        fieldNameList.add(fn);
        fieldList.next();
      }
      %>
    </tr>

        <%

          RowMap detail = null;
          int i=0;
          boolean fb=masterRow.get("sfjz").equals("1")?true:false;
          for(; i<rows.length; i++)
          {
            detail = rows[i];
            RowMap prow=personBean.getLookupRow(detail.get("personid"));
        %>
       <tr onclick="selectRow();">
       <td class="td" width=45 align="center">
       <%if(masterRow.get("sfjz").equals("0")){%>
        <input name="image3" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle">
       <%}%>
       </td>
      <td class="td" nowrap align="left"><%=deptBean.getLookupName(detail.get("deptid"))%><INPUT TYPE="HIDDEN" NAME="deptid_<%=i%>" VALUE="<%=detail.get("deptid")%>" style="width:100%"  CLASS="edFocused_r"></td>
      <td class="td" nowrap align="left"><%=prow.get("xm")%><INPUT TYPE="HIDDEN" NAME="personid_<%=i%>" VALUE="<%=detail.get("personid")%>" style="width:100%"  CLASS="edFocused_r"></td>
      <td class="td" nowrap align="left"><%=prow.get("lb")%></td>
      <%
        fieldList.first();
        for(int k=0;k<fieldList.getRowCount();k++)
        {
          String fieldName=fieldList.getValue("dyzdm");
          String fieldclass=fieldList.getValue("ly").equals("2")?"ednone_r":"edFocused_r";
          String readOnly=fieldList.getValue("ly").equals("2")?"readOnly":"";
          String isOperator=fieldList.getValue("ly").equals("2")?"":"onChange=\"gzchange('"+i+"')\"";
          int index=gsfield.toString().indexOf(fieldName);
          String eventOccur=(index==-1)?"":isOperator;
          fieldclass=fb?"ednone_r":fieldclass;
          if(sfjz.equals("1"))
          {
            fieldclass="ednone_r";
            readOnly = "readOnly";
          }
      %>
      <td class="td" nowrap align="right"><INPUT TYPE="TEXT" NAME="<%=fieldName%>_<%=i%>" VALUE="<%=detail.get(fieldName)%>" style="width:100%" <%=eventOccur%> CLASS="<%=fieldclass%>" onKeyDown="return getNextElement();" <%=readOnly%>></td>
      <%
        fieldList.next();
        }
      %>
    </tr>
    <%
      }
      for(; i < iPage; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <%
        fieldList.first();
    for(int j=0;j<fieldList.getRowCount();j++)
    {
      out.println("<td nowrap class=\"td\"></td>");
      fieldList.next();
    }
      %>
    </tr>
    <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;">
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
<%--查询--%>
<div class="queryPop" id="fixedQuery" name="fixedQuery">
   <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
            <TABLE cellspacing=3 cellpadding=0 border=0>
             <TR>
                  <td align="center" nowrap class="td">姓名</td>
                  <td class="td" nowrap>
                    <INPUT class="edbox" style="WIDTH: 100px" name="personid$xm" value='<%=b_WageInputBean.getFixedQueryValue("personid$xm")%>' onKeyDown="return getNextElement();">
                  </td>
                  <td align="center" nowrap class="td">部门</td>
                  <td class="td" nowrap>
                  <pc:select name="deptid"  style="width:120" addNull="1">
                  <%=deptBean.getList(b_WageInputBean.getFixedQueryValue("deptid"))%>
                  </pc:select>
                  </td>
             </TR>
             <TR>
                  <td align="center" nowrap class="td">年份</td>
                  <td class="td" nowrap>
                    <pc:select name="gzkxzbID$nf" input="0"  style="width:80" >
                    <%=b_WageInputBean.dataSetToOption(b_WageInputBean.getYearList(),"nf","nf",b_WageInputBean.getFixedQueryValue("gzkxzbID$nf"),null,null)%>
                     </pc:select>
                    <td class="td" nowrap>月份</td>
                    <td>
                    <pc:select name="gzkxzbID$yf"  input="0" style="width:130" >
                    <%=b_WageInputBean.listToOption(lists, opkey.indexOf(b_WageInputBean.getFixedQueryValue("gzkxzbID$yf")))%>
                    </pc:select>
                  </td>
             </TR>
            <tr>
            <td nowrap class='td'>有无残疾</td>
            <td nowrap class='td'colspan='3'>
            <input type='radio' onKeyDown='return getNextElement()' name='personid$isdeformity' value=''>全部
            <input type='radio' onKeyDown='return getNextElement()' name='personid$isdeformity' value='1'>有
            <input type='radio' onKeyDown='return getNextElement()' name='personid$isdeformity' value='0'>无
            </td>
            </tr>
            </TABLE>
      </TD>
    </TR>
    <TR>
      <TD colspan="4" nowrap class="td" align="center">
      <INPUT class="button" onClick="sumitFixedQuery(<%=b_WageInputBean.FIXED_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
      <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
      </TD>
   </TR>
 </TABLE>
  </DIV>
   <%--引入工资--%>
   <div class="queryPop" id="importForm" name="importForm">
   <div class="queryTitleBox" align="right"><A onClick="hideFrame('importForm')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
            <TABLE cellspacing=3 cellpadding=0 border=0>
             <TR>
             <td align="center" colspan="4" class="td"><p><b>请选择想要引用的年份与从份!</b></p></td>
             <td align="center" nowrap class="td">&nbsp;</td>
            </TR>
             <TR>
             <td align="center" colspan="4" class="td">&nbsp;</td>
            </TR>
             <TR>
                  <td align="center" nowrap class="td">年份</td>
                  <td class="td" nowrap>
                    <pc:select name="nf" input="0" style="width:80" >
                    <%=b_WageInputBean.dataSetToOption(b_WageInputBean.getYearList(),"nf","nf",b_WageInputBean.year,null,null)%>
                     </pc:select>
                  </td>
                  <td align="center" nowrap class="td">月份</td>
                  <td class="td" nowrap>
                    <pc:select name="yf" addNull="1"  input="0" style="width:130" >
                    <%=b_WageInputBean.listToOption(lists, opkey.indexOf(b_WageInputBean.currentmonth))%>
                    </pc:select>
                  </td>
             </TR>
            </TABLE>
      </TD>
    </TR>
    <TR>
      <TD colspan="4" nowrap class="td" align="center">
      <INPUT class="button" onClick="sumitFixedQuery(<%=Operate.ADD%>)" type="button" value=" 引入 " name="button" onKeyDown="return getNextElement();">
      <INPUT class="button" onClick="hideFrame('importForm')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
      </TD>
   </TR>
 </TABLE>
  </DIV>
<%--引入计件工资--%>
   <div class="queryPop" id="importwage" name="importwage">
   <div class="queryTitleBox" align="right"><A onClick="hideFrame('importwage')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
            <TABLE cellspacing=3 cellpadding=0 border=0>
             <TR>
             <td align="center" colspan="4" class="td"><p><b>请输入时间段!</b></p></td>
             <td align="center" nowrap class="td">&nbsp;</td>
            </TR>
             <TR>
             <td align="center" colspan="4" class="td">&nbsp;</td>
            </TR>
            <TR>
              <TD nowrap class="td">日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="startdate" value='<%=b_WageInputBean.startday%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(startdate);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="enddate" value='<%=b_WageInputBean.endday%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(enddate);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
        </TABLE>
      </TD>
    </TR>
    <TR>
      <TD colspan="4" nowrap class="td" align="center">
      <INPUT class="button" onClick="sumitFixedQuery(<%=b_WageInputBean.CITE%>)" type="button" value=" 引入 " name="button" onKeyDown="return getNextElement();">
      <INPUT class="button" onClick="hideFrame('importwage')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
      </TD>
   </TR>
 </TABLE>
</DIV>
</form>
<script langeage="javascript">
function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
function gzchange(i)
  {
   <%
     fieldList.first();
   for(int k=0;k<fieldList.getRowCount();k++)
   {
     String fieldName=fieldList.getValue("dyzdm");
     out.println("var "+fieldName+"= document.all['"+fieldName+"_'+i];");
     out.println("if("+fieldName+".value==\"\")"+fieldName+".value=0;");
     out.println("if(!checkNumber("+fieldName+"))return;");
     fieldList.next();
   }
   ArrayList namearray = new ArrayList();//公式对应的名称
   ArrayList gsarray = new ArrayList();//公式
   ArrayList al =(ArrayList) b_WageInputBean.getGSArrayList();
   namearray = (ArrayList) al.get(0);
   gsarray = (ArrayList) al.get(1);
   ArrayList tmp = new ArrayList();//存放在页面上打印的公式
   for(int y=0;y<gsarray.size();y++)
   {
     String na = (String)namearray.get(y);//公式左
     String tow = na+".value="+gsarray.get(y);//公式右
     tmp.add(tow+"; if(''+"+na+".value == 'NaN' || ''+"+na+".value == 'Infinity')"+na+".value=0;");
   }
   String stmp="";
   for(int n=0;n<tmp.size();n++)
   {
     String st = (String)tmp.get(n);
     if(stmp.length()<st.length())
     {
       st=stmp;
       stmp=(String)tmp.get(n);//大
       tmp.set(n,st);//小
     }
   }
   for(int n=0;n<tmp.size();n++)
   {
     out.println((String)tmp.get(n));
   }
   out.println(stmp);
   %>
  }
  function checkNumber(obj)
  {
    if (isNaN(obj.value)){
      alert(obj.value + " 不是有效的数值。请核对后重新输入。");
      obj.focus();
      obj.select();
      return false;
    }
    return true;
  }
</script>
<%out.print(retu);}%>
</body>
</html>