<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<%
  String srcFrm=null;     //传递的原form的名称
  String multiIdInput = null;   //多选的ID组合串
  String controlKind = "checkbox";
  String msg = "";
  String caller = "window.opener.";
%><%
  engine.erp.store.Select_Batch selectBatchBean = engine.erp.store.Select_Batch.getInstance(request);
  String retu = selectBatchBean.doService(request, response);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String operate = request.getParameter("operate");
  //是否点击配批号图标按钮调用open的这张页面.如是:那即使只查出一笔资料也显示这个页面.如不是那么带回调用页面资料.
  //2004-4-22 17:34 去年operate是否为空的判断. 因为在翻页的时候会丢掉operate,而使multiIdInput没有.
  //从而造成在下面打印out.printlns js代码的时候打印不出来.因些,造成,只要一翻页的话,那么资料就带不回调用页面. yjg
  //if(operate !=null)
  //{

    srcFrm = selectBatchBean.srcFrm;//request.getParameter("srcFrm");
    multiIdInput = selectBatchBean.multiIdInput;//request.getParameter("srcVar");
    String methodName = selectBatchBean.methodName;
    //isIconFirst等于1则说明不是图标事件第一次进来的.0是第一次进来
    String isIconFirst = operate==null?"1":selectBatchBean.isIconFirst;
    String isFirstComeIn = operate==null?"1":selectBatchBean.isFirstComeIn;
    if (isFirstComeIn.equals("0"))
    {
      caller = "parent.";
    }
    //}
    String curUrl = request.getRequestURL().toString();
    EngineDataSet list = selectBatchBean.getOneTable();
    RowMap searchConditionRowInfo = selectBatchBean.searchConditionRowInfo;
    //1表示是要多选按钮形式
    boolean isMultiSelect = selectBatchBean.isMultiSelect;
    controlKind = isMultiSelect?"checkbox":"radio";
    String[] inputName = selectBatchBean.inputName;
    String[] fieldName = selectBatchBean.fieldName;
    String row = selectBatchBean.row;
    try
    {
      int count = list.getRowCount();
    //out.print("<script language='javascript' src='../scripts/validate.js'></script>");
    //如果找到的记录数等于1那么说明只找到一笔,则带回.
    if(count == 1 || count == 0 )
    {
      if(String.valueOf(selectBatchBean.SHORTCUTOP).equals(operate))
      {
        out.print("<script language='javascript'>");
        if (count==0)
        {
          out.print("alert('查无批号');");
        }
        else
        {
          if(inputName != null && fieldName != null)
          {
            int length = inputName.length > fieldName.length ? fieldName.length : inputName.length;
            String prefix= caller + srcFrm + ".";
            list.first();
            for(int i=0; i< length; i++)
            {
              out.print(prefix); out.print(inputName[i]);
              out.print(".value='");
              out.print(list.getValue(fieldName[i]));
              out.println("';");
              //如果当前调整的是kwid的话.
              //那么,要输出一个js函数,用来实现opener页面上kwid select框的SetSelectedIndex来显示出来相应的值
              if (fieldName[i].equals("kwid")){
                //out.println("window.opener.SetSelectedIndex("+list.getValue(fieldName[i])+");");
                out.println("var linkObj = " + caller + "FindSelectObject('"+ inputName[i] +"');");
                //out.println("var areaid = " + list.getValue(fieldName[i]) + ";");
                String sKwid = list.getValue(fieldName[i]).equals("")?"":list.getValue(fieldName[i]);
                out.println("var areaid = '" + sKwid + "';");
                out.println("linkObj.SetSelectedKey(areaid);");
              }
            }
            if (!methodName.equals(""))
              out.println(caller + methodName);
              //out.println(caller + "sl_onchange(" + row + ", false);");
          }
        }
        out.print("window.close();");
        out.print("</script>");
        return;
        /*
        if(multiIdInput != null)
        {
        String mutiId = "window.opener."+srcFrm+"."+multiIdInput;
        out.print(mutiId+".value="+ list.getValue("wzmxid") + ";");
        out.print(mutiId+".onchange();");
        out.print("window.close();");
        out.print("</script>");
        }
        */
      }//end if selectBatchBean.SHORTCUTOP.equals(operate)
      else//operate=0且记录集只找到一笔资料的时候.图标选择事件
      {
        msg = "<script language='javascript'>";
        //isIconFirst等于1则说明不是图标事件第一次进来的.
        if (count == 1 && isIconFirst.equals("1") && operate != null)
        {
          msg += "checkRadio(0);";
          //msg += "selectProduct();";
        }
        else
        {
          /*if (count<1)
          {
            msg+= "alert('查无批号');";
            //msg+= "window.close();";
            //out.print(msg);
            //return;
          }
          else
          {*/
          //如果是图标事件进来并且没有找到一笔记录,那么还是弹出这个窗口.给它看
          if (isFirstComeIn.equals("0"))
          {
            String tmpcpid = searchConditionRowInfo.get("cpid");
            String storeid = searchConditionRowInfo.get("storeid");
            String dmsxid = searchConditionRowInfo.get("dmsxid");
            out.print("<script language='javascript'>");
            out.print("parent.openSelectUrl('../store/select_batch.jsp?isFirstComeIn=1&cpid="
                      +tmpcpid
                      +"&storeid=" + storeid
                      +"&dmsxid=" + dmsxid
                      + "', 'BatchSelector', parent.winopt2);");
            out.print("</script>");
            return;
          }
          //}
        }
        msg += "</script>";
      }
    }
    else
    {
      //第一次进来的时候才parent.openSelectUrl
      if (operate!=null && isFirstComeIn.equals("0"))
      {
        out.print("<script language='javascript'>");
        out.print("parent.openSelectUrl('../store/select_batch.jsp', 'BatchSelector', parent.winopt2);");
        out.print("</script>");
        return;
      }
    }
    }catch(Exception e)
    {
      selectBatchBean.getLog().error("BatchSelect", e);
    }
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
function sumitForm(oper){
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
function selectProduct(row)
{
  var multiId = '';
  //如果不是多选checkbox的话那么就是radio单选.因此,就须要记录当前选中的行数.并使用js来给调用页面赋值.
  <%if ( !isMultiSelect )
    {
  %>
    if(row +'' == 'undefined')
    {
      var rodioObj = gCheckedObj(form1, false);
      if(rodioObj != null)
        row = rodioObj.value;
      else
        return;
    }
    if(!(row +'' == 'undefined'))
    {
      var e = form1.elements[row];
    <%
      if(inputName != null && fieldName != null && !isMultiSelect)
      {
        int length = inputName.length > fieldName.length ? fieldName.length : inputName.length;
        for(int i=0; i< length; i++)
        {
          out.println("obj = document.all['"+fieldName[i]+"_'+row];");
          out.print("window.opener."+ srcFrm+"."+inputName[i]);
          out.print(".value=");     out.println("obj.value;");
          //如果当前调整的是kwid的话.
          //那么,要输出一个js函数,用来实现opener页面上kwid select框的SetSelectedIndex来显示出来相应的值
          if (fieldName[i].equals("kwid")){
            out.println("var linkObj = window.opener.FindSelectObject('"+ inputName[i] +"');");
            //out.println("var areaid = " + list.getValue(fieldName[i]) + ";");
            String sKwid = list.getValue(fieldName[i]).equals("")?"":list.getValue(fieldName[i]);
            out.println("var areaid = obj.value;");
            out.println("linkObj.SetSelectedKey(areaid);");
          }
        }
          if (!methodName.equals(""))
              out.println("window.opener." + methodName);
        //out.println("window.opener.sl_onchange(" + row + ", false);");
      }
%> window.close();return;
  }
<%
  }//end !isMultiSelect.结束最外层是单选多选按钮形式判断的if
%>
//如果上面的判断没有执行,那么程序将执行到这里来.此处是,多选checkbox形式的处理部份.
  //多选择checkbox配批号的时候会造成opener的提交.而像上面的单选情况下则只是靠js来传值的.
  for(var i=0;i<form1.elements.length;i++)
  {
    var e = form1.elements[i];
    //alert("name:"+e.name+",type:"+e.type + ",checked:"+e.checked);
    if((e.type == "checkbox" || e.type == "radio") && e.name!='checkform' && e.checked == true)
      multiId += e.value+',';
  }
  if(multiId.length > 0){
    multiId += '-1';
  <%if(multiIdInput != null){
      //String mutiId = "parent."+srcFrm+"."+multiIdInput;
      String mutiId = "window.opener."+srcFrm+"."+multiIdInput;
      out.print(mutiId+".value=multiId;");
      out.print(mutiId+".onchange();");
    }%>
  }
  window.close();
}
function checkRadio(row){
  if(form1.sel.length+''=='undefined')
    form1.sel.checked = !form1.sel.checked;
  else
    form1.sel[row].checked = !form1.sel[row].checked;
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">批号选择清单</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="<%=request.getParameter("operate")%>">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <%--2004-4-13 12:21 新增 此行以下的hidden是新加上的为的是因为此页面要实现可让用户输入批号查询 yjg--%>
  <INPUT TYPE="HIDDEN" NAME="cpid" VALUE="<%=searchConditionRowInfo.get("cpid")%>">
  <INPUT TYPE="HIDDEN" NAME="storeid" VALUE="<%=searchConditionRowInfo.get("storeid")%>">
  <INPUT TYPE="HIDDEN" NAME="dmsxid" VALUE="<%=searchConditionRowInfo.get("dmsxid")%>">
  <INPUT TYPE="HIDDEN" NAME="sxz"    VALUE="<%=searchConditionRowInfo.get("sxz")%>">
  <INPUT TYPE="HIDDEN" NAME="srcFrm" VALUE="<%=searchConditionRowInfo.get("srcFrm")%>">
  <INPUT TYPE="HIDDEN" NAME="srcVar" VALUE="<%=searchConditionRowInfo.get("srcVar")%>">
  <INPUT TYPE="HIDDEN" NAME="methodName" VALUE="<%=searchConditionRowInfo.get("methodName")%>">
  <INPUT TYPE="HIDDEN" NAME="isMultiSelect" VALUE="<%=searchConditionRowInfo.get("isMultiSelect")%>">
  <INPUT TYPE="HIDDEN" NAME="row" VALUE="<%=searchConditionRowInfo.get("row")%>">
  <INPUT TYPE="HIDDEN" NAME="isIconFirst" VALUE="<%=isIconFirst%>">
  <INPUT TYPE="HIDDEN" NAME="isFirstComeIn" VALUE="<%=isFirstComeIn%>">

  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "97"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right">
       <font class="tableTitle">输入批号查询:</font>
       <input class="edFocused_r" style="width:100" id="ph" name="ph"  value="<%=searchConditionRowInfo.get("ph")%>" onKeyDown="return getNextElement();" onChange="sumitForm(<%=isMultiSelect?0:58%>)">
      <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduct();" title="选用(ALT+E)" value="选用(E)" onKeyDown="return getNextElement();">
          <pc:shortcut key="e" script="selectProduct()"/>
      <%}%>
        <input name="button2" type="button" class="button" onClick="window.close();" title="返回(ALT+C)" value="返回(C)" onKeyDown="return getNextElement();"></td>
        <pc:shortcut key="c" script="window.close()"/>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20>
       <% if ( isMultiSelect ){%>
       <input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
       <%}%>
      <td nowrap>产品编码</td>
      <td nowrap>品名 规格</td>
      <td nowrap>规格属性</td>
      <td nowrap>仓库</td>
      <td nowrap>批号</td>
      <td nowrap>库位</td>
      <td nowrap>库存量</td>
    </tr>
    <%prodBean.regData(list,"cpid");
      propertyBean.regData(list,"dmsxid");
      storeAreaBean.regData(list,"storeid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++){
    %>
    <tr <%=isMultiSelect?"":"onClick='selectRow()' onDblClick=selectProduct("+i+");"%>>
    <%RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));%>
      <td nowrap align="center" class="td"><%--checkbox形式下赋wzmxid的值.radio情况下赋i的值--%>
        <input type="<%=controlKind%>" name="sel" value='<%=isMultiSelect?list.getValue("wzmxid"):i+""%>' onKeyDown="return getNextElement();">
      </td>
      <td nowrap align="center" id="cpbm_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"> <%=prodRow.get("cpbm")%>
         <input type="hidden" id="wzmxid_<%=i%>" value='<%=list.getValue("wzmxid")%>'>
     </td>
      <td nowrap align="center" id="product_<%=i%>"  class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("product")%>
      </td>
      <td nowrap onClick="checkRadio(<%=i%>)"  id="dmsxid_<%=i%>" value="<%=list.getValue("dmsxid")%>" class="td" align="right">
      <%=propertyBean.getLookupName(list.getValue("dmsxid"))%>
      </td>
      <input type="hidden" name="sxz_<%=i%>" id="sxz_<%=i%>" value="<%=propertyBean.getLookupName(list.getValue("dmsxid"))%>">
      <td nowrap onClick="checkRadio(<%=i%>)" id="storeid_<%=i%>" class="td"><%=storeBean.getLookupName(list.getValue("storeid"))%>
      </td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="ph_<%=i%>" value="<%=list.getValue("ph")%>" class="td" align="right"><%=list.getValue("ph")%>
      </td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="kwmc_<%=i%>"class="td" align="right"><%=storeAreaBean.getLookupName(list.getValue("kwid"))%>
      <input type="hidden" id="kwid_<%=i%>" value='<%=list.getValue("kwid")%>'>
      </td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="zl_<%=i%>" value="<%=list.getValue("zl")%>" class="td" align="right"><%=list.getValue("zl")%>
     </td>
    </tr>
    <%  list.next();
      }
      for(; i < iPage; i++){
    %>
    <tr>
      <td class="td"></td>
      <td nowrap class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
</form>
<%out.print(retu);%>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
  form1.ph.focus();
</SCRIPT>
</body>
</html>
<%
if (!msg.equals(""))
  out.println(msg);
%>
