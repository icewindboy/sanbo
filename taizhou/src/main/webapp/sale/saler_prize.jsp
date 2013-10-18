<%--业务员奖金--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate, com.borland.dx.dataset.Locate,java.util.*"%>
<%
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "saler_prize";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.sale.B_SalerPrize B_SalerPrizeBean = engine.erp.sale.B_SalerPrize.getInstance(request);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);

  boolean haseditlimit = loginBean.hasLimits(pageCode, op_edit);
  boolean hassearchlimit = loginBean.hasLimits(pageCode, op_search);
  boolean hasdellimit = loginBean.hasLimits(pageCode, op_delete);
  boolean hasaddlimit = loginBean.hasLimits(pageCode, op_add);
  boolean editandadd = haseditlimit||hasaddlimit;

  boolean isCanSearch=loginBean.hasLimits(pageCode,op_search);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanDel=loginBean.hasLimits(pageCode, op_delete);
  String rll = B_SalerPrizeBean.getTrueRll();//得到日利率

%>
<%
  String retu = B_SalerPrizeBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();

%>
<%
   String key = "loginmanagedata";

   EngineDataSet tmp=B_SalerPrizeBean.getDetailTable();//工资数据
   EngineDataSet list = B_SalerPrizeBean.getMaterTable();//主表
   EngineDataSet dsField = B_SalerPrizeBean.getJjTable();
   RowMap[] drows = B_SalerPrizeBean.getDetailRowinfos();

   pageContext.setAttribute(key, tmp);
   int iPage = loginBean.getPageSize();
   String pageSize = ""+iPage;
   String sfjz = list.getValue("sfjz");
   String t="sumitForm("+B_SalerPrizeBean.MONTH_CHANGE+",-1)";//saleOrderBean.DETAIL_CHANGE;
   String djxz = "";//row.get("djxz");
   String jjzbid = B_SalerPrizeBean.getJjzbid();

   int cols = 0;
   ArrayList sumar = new ArrayList();
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

<script language="javascript">
  function toDetail()
  {
  location.href='employee_info_edit.jsp';
  }
  function sumitForm(oper, row)
  {
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
  function checkBdlx()
  {
    if(form1.month.value == "")
    {
    alert("请选择月份!!");
    return;
    }
    else
      PersonMultiSelect("form1","srcVar=multiIdInput&isoff=0$1","undefined","undefined");
}
function prixprint()
{
  location.href='prix_print.jsp';
}
function openwin(jjzbid,personid)
{
  paraStr = "saler_prix_detail.jsp?operate=0&jjzbid="+jjzbid+"&personid="+personid;
  openSelectUrl(paraStr, "SingleCustSelector", winopt2);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD NOWRAP align="center">业务员奖金</TD>
</TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
<table id="tbcontrol" width="100%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"></pc:dbNavigator>
     </td>
      <td class="tdTitle" align="right">本年度月份</td>
      <td class="td" >
      <pc:select name="month" style="width:50" value="<%=B_SalerPrizeBean.getMonth()%>" onSelect="<%=t%>">
        <pc:option value="1">1</pc:option>
        <pc:option value="2">2</pc:option>
        <pc:option value="3">3</pc:option>
        <pc:option value="4">4</pc:option>
        <pc:option value="5">5</pc:option>
        <pc:option value="6">6</pc:option>
        <pc:option value="7">7</pc:option>
        <pc:option value="8">8</pc:option>
        <pc:option value="9">9</pc:option>
        <pc:option value="10">10</pc:option>
        <pc:option value="11">11</pc:option>
        <pc:option value="12">12</pc:option>
      </pc:select>
      </td>
      <td class="td" align="right">
      <%if(sfjz.equals("0")&&editandadd){
       String f = "if(confirm('完成后不能修改,确认要完成吗?')) sumitForm("+B_SalerPrizeBean.OVER+",-1)";
       String cal = "sumitForm("+B_SalerPrizeBean.CALCULATE+",-1)";
       String sav = "sumitForm("+Operate.POST+",-1)";
       String r = "sumitForm("+B_SalerPrizeBean.GET+",-1)";
       %>
      <input name="search" type="button" style="width:80" class="button" onClick="sumitForm(<%=B_SalerPrizeBean.GET%>,-1)" value="取业务员(R)">
      <pc:shortcut key="r" script='<%=r%>'/>
      <input name="search" type="button" class="button" style="width:50" onClick="if(confirm('完成后不能修改,确认要完成吗?')) sumitForm(<%=B_SalerPrizeBean.OVER%>,-1)" value="完成(W)">
      <pc:shortcut key="w" script='<%=f%>'/>
      <input name="search" type="button" class="button" style="width:50" onClick="sumitForm(<%=B_SalerPrizeBean.CALCULATE%>,-1)" value="计算(E)">
       <pc:shortcut key="e" script='<%=cal%>'/>
      <input name="search" type="button" class="button"  style="width:50" onClick="sumitForm(<%=Operate.POST%>,-1)" value="保存(S)">
      <pc:shortcut key="s" script='<%=sav%>'/>
      <%}%>
      <%if(isCanSearch){%>
      <input name="search" type="button" class="button"  style="width:50" onClick="showFixedQuery()" value="查询(Q)">
      <pc:shortcut key="q" script='showFixedQuery()'/>
      <%}%>
       <%String ret = "location.href='"+B_SalerPrizeBean.retuUrl+"'";%>
      <input name="button2" type="button" class="button"  style="width:50" onClick="location.href='<%=B_SalerPrizeBean.retuUrl%>'" value="返回(C)">
      <pc:shortcut key="c" script='<%=ret%>'/>
    </td>
    </tr>
  </table>
  <table id="tableview1" width="120%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle" >
      <td nowrap>
       <%if((loginBean.hasLimits(pageCode,op_add))&&(sfjz.equals("0")&&hasaddlimit)){%>
       <input type="hidden" name="multiIdInput" value="" onchange="sumitForm(<%=Operate.DETAIL_ADD%>)">
       <input name="image" class="img" type="image" title="新增" onClick="checkBdlx()" src="../images/add.gif" border="0">
       <%}%></td>
     <td nowrap class="tdTitle">员工姓名</td>
     <td nowrap class="tdTitle">部门</td>
     <%
       ArrayList fieldNameList=new ArrayList();//全部字段
       ArrayList gsfieldname = new ArrayList();//等于公式的字段
       dsField.first();
       for(int i=0;i<dsField.getRowCount();i++)
       {
         if(dsField.getValue("sfxs").equals("1"))
           out.println("<td nowrap class=\"tdTitle\">"+dsField.getValue("mc")+"</td>");
         fieldNameList.add(dsField.getValue("dyzdm"));
         if(dsField.getValue("ly").equals("2")&&dsField.getValue("sfkxg").equals("1"))
           gsfieldname.add(dsField.getValue("dyzdm"));
         dsField.next();
       }
      %>
      </tr>
        <%
          int i=0;
          int rowcount = drows.length;
          sumar = new ArrayList();
          tmp.first();
          for(; i < drows.length; i++) {
            int coln =0;
            RowMap drow = drows[i];
            String personid = drow.get("personid");
            RowMap prow=personBean.getLookupRow(drow.get("personid"));
            //String jjid =
         %>
      <tr  onclick="selectRow();" <%=editandadd?"onDblClick='openwin("+jjzbid+","+personid+")'":""%> >
      <td class="td" nowrap align="center">
       <%if(sfjz.equals("0")&&hasdellimit){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i %>)" src="../images/del.gif" border="0">
        <%}%></td>
      <td class="td" nowrap ><%=personid%><%=prow.get("xm")%></td>
      <td class="td" nowrap >
     <%=deptBean.getLookupName(drow.get("deptid"))%>
      <%
        dsField.first();
        for(int j=0;j<dsField.getRowCount();j++)
        {
          String fieldName = dsField.getValue("dyzdm")+"_"+i;
          if(!(dsField.getValue("sfxs").equals("1")))
          {
        %>
         <INPUT TYPE="hidden"  NAME="<%=fieldName%>" VALUE="<%=drow.get(dsField.getValue("dyzdm"))  %>"  readOnly>
            <%
          }
          dsField.next();
        }
      %>
     </td>
      <%
        dsField.first();
        for(int j=0;j<dsField.getRowCount();j++)
        {
          boolean canedit = dsField.getValue("ly").equals("2")||dsField.getValue("sfkxg").equals("0");
          String fieldclass=canedit?"ednone_r":"edFocused_r";
          String readOnly=canedit?"readOnly":"";
          String isOperator=canedit?"":"onChange=\"gzchange('"+i+"')\"";
          if(!editandadd)
          {
            fieldclass="ednone_r";
            readOnly="readOnly";
          }
          if(sfjz.equals("1"))
          {
            fieldclass="ednone_r";
            readOnly="readOnly";
          }
          String fieldName = dsField.getValue("dyzdm")+"_"+i;
          if(dsField.getValue("sfxs").equals("1"))
          {
            String value = drow.get(dsField.getValue("dyzdm")).equals("")?"0":drow.get(dsField.getValue("dyzdm"));
            if(sumar.size()==0||coln>(sumar.size()-1))
              sumar.add(value);
            else
            {
              sumar.set(coln,String.valueOf(Double.parseDouble((String)sumar.get(coln))+Double.parseDouble(value)));
            }
            coln = coln+1;
      %>
      <td nowrap class="td" ><INPUT TYPE="TEXT"  NAME="<%=fieldName%>" VALUE="<%=engine.util.Format.formatNumber(drow.get(dsField.getValue("dyzdm")).equals("")?"0":drow.get(dsField.getValue("dyzdm")),"#0.00") %>"  MAXLENGTH="<%=tmp.getColumn(dsField.getValue("dyzdm")).getPrecision()%>"  style="width:80" <%=isOperator%>  CLASS="<%=fieldclass%>" onKeyDown="return getNextElement();" <%=readOnly%>>
      </td>
      <%
          }
          dsField.next();
        }
      %>
    </tr>
    <%
        //tmp.next();
      }
      i=rowcount+1;
    %>
    <tr>
      <td class="td"></td>
      <td class="td">合计</td>
      <td class="td"></td>
    <%
    if(rowcount==0)
      sumar = new ArrayList();
     if(sumar.size()>0){
     for(int p =0 ;p<sumar.size();p++)
     {
       out.print("<td class='td'>"+engine.util.Format.formatNumber(sumar.get(p),"#0.00")+"</td>");
     }
     }else
     {
       dsField.first();
       for(int j=0;j<dsField.getRowCount();j++)
       {
         if(dsField.getValue("sfxs").equals("1"))
         {
         out.println("<td nowrap class=\"td\"></td>");
         }
         dsField.next();
        }
     }
     %>

    </tr>

    <%
        //tmp.next();
      for(; i < iPage; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <%
        dsField.first();
        for(int j=0;j<dsField.getRowCount();j++)
        {
          if(dsField.getValue("sfxs").equals("1"))
          {
          out.println("<td nowrap class=\"td\"></td>");
          }
          dsField.next();
        }
      %>
    </tr>
    <%}%>
  </table>
  </form>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
function sumitFixedQuery(oper)
  {
    lockScreenToWait("处理中, 请稍候！");
    fixedQueryform.operate.value = oper;
    fixedQueryform.submit();
  }
function showFixedQuery()
  {
    showFrame('fixedQuery', true, "", true);
  }
  </SCRIPT>
  <form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
    <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
    <div class="queryPop" id="fixedQuery" name="fixedQuery">
      <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
      <TABLE cellspacing=3 cellpadding=0 border=0>
        <TR>
          <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <td noWrap class="td" align="center">年份</td>
              <td noWrap class="td">
              <input type="text" name="nf" value='<%=B_SalerPrizeBean.getFixedQueryValue("nf")%>' maxlength='6' style="width:80" class="edbox">
              </td>
            <td noWrap class="td" align="center">月份</td>
           <td class="td" ><%String yf =B_SalerPrizeBean.getFixedQueryValue("yf"); %>
            <pc:select name="yf" style="width:80" value="<%=yf%>" >
              <pc:option value="1">1</pc:option>
              <pc:option value="2">2</pc:option>
              <pc:option value="3">3</pc:option>
              <pc:option value="4">4</pc:option>
              <pc:option value="5">5</pc:option>
              <pc:option value="6">6</pc:option>
              <pc:option value="7">7</pc:option>
              <pc:option value="8">8</pc:option>
              <pc:option value="9">9</pc:option>
              <pc:option value="10">10</pc:option>
              <pc:option value="11">11</pc:option>
              <pc:option value="12">12</pc:option>
            </pc:select>
            </td>
            </TR>
             <tr>
             <td noWrap class="td" align="center">部门</td>
              <td noWrap class="td">
              <%
                String deptid = B_SalerPrizeBean.getFixedQueryValue("deptid");
                String personid = B_SalerPrizeBean.getFixedQueryValue("personid");
                String onChange ="sumitFixedQuery("+Operate.DEPT_CHANGE+")";
                deptBean.regConditionData("deptid",new String[]{deptid});
                personBean.regConditionData("personid", new String[]{personid});
              %>
              <pc:select name="deptid" addNull="1" style="width:110"  onSelect="<%=onChange%>" >
                <%=deptBean.getList(B_SalerPrizeBean.getFixedQueryValue("deptid"))%>
              </pc:select>
              </td>
              <td noWrap class="td" align="center">姓名</td>
              <td noWrap class="td">
              <pc:select name="personid" style="width:110">
                    <%=personBean.getList(B_SalerPrizeBean.getFixedQueryValue("personid"), "deptid",B_SalerPrizeBean.getFixedQueryValue("deptid"))%>
              </pc:select>
              </td>
            </tr>
            <TR>
              <TD nowrap colspan=4 height=30 align="center">
                <% String ss = "sumitFixedQuery("+Operate.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value="查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script="<%=ss%>" />
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(X)" name="button2" onKeyDown="return getNextElement();">
                 <pc:shortcut key="x" script="hideFrame('fixedQuery')" />
              </TD>
            </TR>
          </TABLE>
        </TD>
        </TR>
      </TABLE>
    </DIV>
  </form>
<script langeage="javascript">
function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
function cal(n)
{
  for(h=0;h<n;h++)
  {
    gzchange(h);
  }
}
 function gzchange(i)
  {
   <%
     dsField.first();
   for(int k=0;k<dsField.getRowCount();k++)
   {
     String fieldName=dsField.getValue("dyzdm");
     out.println("var "+fieldName+"= document.all['"+fieldName+"_'+i];");
     out.println("if("+fieldName+".value==\"\")"+fieldName+".value=0;");
     out.println("if(!checkNumber("+fieldName+"))return;");
     dsField.next();
   }

  StringBuffer beRefed = new StringBuffer();
  StringBuffer notRef = new StringBuffer();
  dsField.first();
 for(int k=0;k<dsField.getRowCount();k++)
 {
   String fieldName=dsField.getValue("dyzdm");
   if(dsField.getValue("ly").equals("2")&&!fieldName.equals("jj"))
   {
     String jsgs=dsField.getValue("jsgs");
     StringBuffer tp=new StringBuffer(jsgs);
     for(int h=0;h<fieldNameList.size();h++)
     {
       String name = (String)fieldNameList.get(h);
       if(name.equals("jj"))continue;
       jsgs=engine.util.StringUtils.replace(jsgs,"rll",rll);
       jsgs=engine.util.StringUtils.replace(jsgs,name,"parseFloat("+name+".value)");
     }
     boolean contain = false;
     for(int n=0;n<gsfieldname.size();n++)
     {
       if(jsgs.indexOf((String)gsfieldname.get(n))>-1)
       {
       beRefed.append(fieldName+".value=formatPrice("+jsgs+");"+"if (isNaN("+fieldName+".value)){"+fieldName+".value=0;}");
       contain=true;
       break;
       }
       else
       {
         contain=false;
       }
     }
     if(!contain)
       notRef.append(fieldName+".value=formatPrice("+jsgs+");"+"if (isNaN("+fieldName+".value)){"+fieldName+".value=0;}");
   }
   dsField.next();
   }
   out.println(notRef.toString());
   out.println(beRefed.toString());
   out.println("jj.value=formatPrice("+B_SalerPrizeBean.getGS()+");");
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
<%out.print(retu);%>
</body>
</html>