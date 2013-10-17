<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String pageCode = "prix_formula";
%>
<%
  engine.erp.sale.dafa.B_PrixFormula b_PrixFormulaBean = engine.erp.sale.dafa.B_PrixFormula.getInstance(request);
  String pageCode = "prix_formula";
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
function showInterFrame(oper, rownum){
  var url = "kc_dmlbcs_edit.jsp?operate="+oper+"&rownum="+rownum;
  document.all.interframe1.src = url;
  showFrame('detailDiv',true,"",true);
}
function showInterFrame2(oper, rownum){
  var url = "khdjcs_edit.jsp?operate="+oper+"&rownum="+rownum;
  document.all.interframe1.src = url;
  showFrame('detailDiv',true,"",true);
}
function hideInterFrame(){//隐藏FRAME
  hideFrame('detailDiv');
  form1.submit();
}
function hideFrameNoFresh(){
  hideFrame('detailDiv');
  }
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<%
  String retu = b_PrixFormulaBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp pruductKindBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_KIND);//物资类别

  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_PrixFormulaBean.getOneTable();
  EngineDataSet ds_jssz = b_PrixFormulaBean.getJjgsTable();
  //EngineDataSet ds_dmlbcs = b_PrixFormulaBean.getDmlbcsTable();
  EngineDataSet ds_khdj = b_PrixFormulaBean.getKhdjTable();

  ArrayList opkey = new ArrayList(); opkey.add("A");opkey.add("B"); opkey.add("C");  opkey.add("D"); opkey.add("E");opkey.add("F");
  ArrayList opval = new ArrayList(); opval.add("一级客户");opval.add("二级客户"); opval.add("三级客户");  opval.add("现金客户"); opval.add("四级客户");opval.add("五级客户");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};

  boolean canedit = loginBean.hasLimits(pageCode,op_add)||loginBean.hasLimits(pageCode,op_edit);
  String edClass = canedit?"class=edbox":"class=edline";
  String readonly = canedit?"":"readonly";
  ds.first();
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td>
    <table cellspacing=0 width="100%" cellpadding=0>
     <tr>
         <td nowrap><div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_0');return false;">业务员奖金计算公式</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_1" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_1');return false;">客户等级系数</a></div></td>
         <%--td nowrap><div id="tabDivINFO_EX_2" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_2');return false;">客户等级系数</a></div></td--%>
         <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a></td>
     </tr>
     </table>
     <center>
        <div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:800;height:550;overflow-y:auto;overflow-x:auto;">
        <table id="tableview1"  class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0" width="100%">
                  <tr>
                  <td  noWrap class="tdTitle">计算方式</td>
                  <td noWrap class="td"><INPUT TYPE="radio" NAME="jsfs" VALUE="1" <%=(ds.getValue("jsfs").equals("1"))?"checked":((ds.getValue("jsfs").equals(""))?"checked":"")%>>按发票<INPUT TYPE="radio" NAME="jsfs" VALUE="2" <%=(ds.getValue("jsfs").equals("2"))?"checked":""%>>按提单</td>
                  <td  noWrap class="tdTitle">月利率</td>
                  <td  noWrap class="td"><input type="text" name="rll" value='<%=ds.getValue("rll")%>' maxlength='<%=ds.getColumn("rll").getPrecision()%>' style="width:110" class="edFocused_r" onKeyDown="return getNextElement();" ></td>
                  <td  noWrap class="tdTitle">应收款超期收回业务员扣息比例(每天%)</td>
                  <td  noWrap class="td"><input type="text" name="kxbl" value='<%=ds.getValue("kxbl")%>' maxlength='<%=ds.getColumn("kxbl").getPrecision()%>' style="width:110" class="edFocused_r" onKeyDown="return getNextElement();" ></td>
                  </tr>
                  <tr>
                  <td  noWrap class="tdTitle">销售价增加(元)</td>
                  <td  noWrap class="td"><input type="text" name="xsjzj" value='<%=ds.getValue("xsjzj")%>' maxlength='<%=ds.getColumn("xsjzj").getPrecision()%>' style="width:110" class="edFocused_r" onKeyDown="return getNextElement();" ></td>
                  <td noWrap class="tdTitle">提成率增加(%)</td>
                  <td noWrap class="td"><input type="text" name="tclzj" value='<%=ds.getValue("tclzj")%>' maxlength='<%=ds.getColumn("tclzj").getPrecision()%>' style="width:110" class="edFocused_r" onKeyDown="return getNextElement();"></td>
                  <td noWrap class="tdTitle">回笼天数增加(天)</td>
                  <td noWrap class="td"><input type="text" name="hltszj" value='<%=ds.getValue("hltszj")%>' maxlength='<%=ds.getColumn("hltszj").getPrecision()%>' style="width:110" class="edFocused_r" onKeyDown="return getNextElement();"></td>
                 </tr>
                 <tr>
                  <td  noWrap class="tdTitle">销售价减少(元)</td>
                  <td  noWrap class="td"><input type="text" name="xsjjs" value='<%=ds.getValue("xsjjs")%>' maxlength='<%=ds.getColumn("xsjjs").getPrecision()%>' style="width:110" class="edFocused_r" onKeyDown="return getNextElement();" ></td>
                  <td noWrap class="tdTitle">提成率减少(%)</td>
                  <td noWrap class="td"><input type="text" name="tcljs" value='<%=ds.getValue("tcljs")%>' maxlength='<%=ds.getColumn("tcljs").getPrecision()%>' style="width:110" class="edFocused_r" onKeyDown="return getNextElement();"></td>
                  <td noWrap class="tdTitle">回笼天数减少(天)</td>
                  <td noWrap class="td"><input type="text" name="hltsjs" value='<%=ds.getValue("hltsjs")%>' maxlength='<%=ds.getColumn("hltsjs").getPrecision()%>' style="width:110" class="edFocused_r" onKeyDown="return getNextElement();"></td>
                 </tr>
                 <tr>
                  <td  noWrap class="tdTitle">允许更小单位</td>
                  <td noWrap class="td"><INPUT TYPE="radio" NAME="sfxdw" VALUE="1" <%=(ds.getValue("sfxdw").equals("1"))?"checked":((ds.getValue("sfxdw").equals(""))?"checked":"")%>>是<INPUT TYPE="radio" NAME="sfxdw" VALUE="0" <%=(ds.getValue("sfxdw").equals("0"))?"checked":""%>>否</td>
                 </tr>
                 <tr>
                  <td  noWrap colspan="6" class="tdTitle">业务员奖金计算公式</td>
                  </tr>
                 <tr>
                  <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
                  <%
                    int i=0;
                    ds_jssz.first();
                    for(; i<ds_jssz.getRowCount(); i++)
                    {
                      if(ds_jssz.getValue("ly").equals("2")){
                  %>
                  <tr >
                    <td  nowarp width="15%" class="tdTitle"><%=ds_jssz.getValue("mc")%> =</td>
                    <td class="td" nowarp><input type="text" name="jsgs_<%=i%>" value="<%=ds_jssz.getValue("jsgs")%>" style="width=100%" onBlur="valueChange('<%=i%>')" class="edbox" ></td>
                  </tr>
                  <%}ds_jssz.next();}%>
                <tr>
                  <td class="tableTitle" nowarp>运算符:</td>
                  <td class="td" colspan="5" ><input name="plus" type="button" value=" + " onClick="plusClick('plus')">&nbsp;<input name="subtract" type="button" value=" - " onClick="plusClick('subtract')">&nbsp;<input name="button3" type="button" value=" * " onClick="plusClick('*')">&nbsp;<input name="button4" type="button" value=" / " onClick="plusClick('chu')">&nbsp;<input name="button5" type="button" value=" ( " onClick="plusClick('left')">&nbsp;<input name="button6" type="button" value=" ) " onClick="plusClick('right')"></td>
                </tr>
                <tr>
                  <td class="tableTitle">项目</td>
                  <td>
                  <%
                   ds_jssz.first();
                   for(int j=0;j<ds_jssz.getRowCount();j++)
                   {
                      String field=ds_jssz.getValue("dyzdm");
                   %>
                       <input name="plus" style="width:150" type="button" value="<%=ds_jssz.getValue("mc")+"("+field+")"%>" onClick="plusClick('<%=field%>')" >
                   <%
                   ds_jssz.next();
                   }
                   %>
                  </td>
                </tr>
                  </table>
                 </tr>
                 <tr>
                 <td class="td" align="left">
                 销售允许天数利息（XSA）=资金允许回笼天数*销售额；<br>
                 销售回笼利息（XSB）=(月结算天数—回款日)*回款金额；<br>
                 应收款月初余额利息（XSC）=月初余额*月结算天数；<br>
                 销售利息（XSD）=（月结算天数—销售提货日期）*销售额；<br>
                 利息奖=（XSA+XSB—XSC—XSD）*日利率；<br>
                 </td>
                </tr>
        </table>
       </td>
    </tr>
  </table>
      <script language="javascript">initDefaultTableRow('tableview1',1);</script>
      </div>
</center>
<%--
<center>
<div id="cntDivINFO_EX_1" class="tabContent" style="display:none;width:760;height:550;overflow-y:auto;overflow-x:hidden;">
        <table id="tableview2"  width="100%" border="0" cellpadding="0" cellspacing="1" class="table" dwcopytype="CopyTableRow">
            <tr class="tableTitle">
                  <td  align="center" nowrap>
                  <input name="image4" class="img" type="image" title="新增" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
                  </td>
                  <td>物资类别</td>
                  <td>提成比例</td>
            </tr>
     <%
      int j=0;
      ds_dmlbcs.first();
      for(; j<ds_dmlbcs.getRowCount(); j++)
      {
     %>
            <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=ds_dmlbcs.getRow()%>)" >
                <td class="td" align="center">
                <input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=ds_dmlbcs.getRow()%>)" src="../images/edit.gif" border="0">
                <input name="image32" class="img" type="image" title="删除"  onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=ds_dmlbcs.getRow()%>)"  src="../images/del.gif" border="0" align="absmiddle">
                </td>
                <td class="td"  align="left">
                <INPUT TYPE="TEXT"  NAME="wzlbid_<%=j%>" VALUE="<%=pruductKindBean.getLookupName(ds_dmlbcs.getValue("wzlbid"))%>" style="width:100%" class="ednone" onKeyDown="return getNextElement();"  readonly >
               </td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="lbcs_<%=j%>" VALUE="<%=ds_dmlbcs.getValue("lbcs")%>" style="width:100%" class="ednone_r" onKeyDown="return getNextElement();"  readonly ></td>
            </tr>
            <%
            ds_dmlbcs.next();
      }
            for(; j < 25; j++){
           %>
           <tr>
           <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
           </tr>
            <%
              }
           %>
        </table>
        <script language="javascript">initDefaultTableRow('tableview2',1);</script>
</div>
</center>
--%>
<center>
<div id="cntDivINFO_EX_1" class="tabContent" style="display:none;width:760;height:550;overflow-y:auto;overflow-x:hidden;">
        <table id="tableview2"  width="100%" border="0" cellpadding="0" cellspacing="1" class="table" dwcopytype="CopyTableRow">
            <tr class="tableTitle">
                  <td  align="center" nowrap>
                  <input name="image4" class="img" type="image" title="新增" onClick="showInterFrame2(<%=b_PrixFormulaBean.KHDJ_ADD%>,-1)" src="../images/add.gif" border="0">
                  </td>
                  <td>客户价值</td>
                  <td>调整系数(%)</td>
                  <td>回款期限</td>
                  <td>资金占有费率(%)</td>
            </tr>
     <%
      int h=0;
      ds_khdj.first();
      for(; h<ds_khdj.getRowCount(); h++)
      {
     %>
            <tr onDblClick="showInterFrame2(<%=b_PrixFormulaBean.KHDJ_EDIT%>,<%=ds_khdj.getRow()%>)" >
                <td class="td" align="center">
                <input name="image2" class="img" type="image" title="修改" onClick="showInterFrame2(<%=b_PrixFormulaBean.KHDJ_EDIT%>,<%=ds_khdj.getRow()%>)" src="../images/edit.gif" border="0">
                <input name="image32" class="img" type="image" title="删除"  onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_PrixFormulaBean.KHDJ_DEL%>,<%=ds_khdj.getRow()%>)"  src="../images/del.gif" border="0" align="absmiddle">
                </td>
                <td class="td"  align="left">
                <%=opval.get(opkey.indexOf(ds_khdj.getValue("xydj")))%>
               </td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="adjustxs_<%=h%>" VALUE="<%=ds_khdj.getValue("adjustxs")%>" style="width:100%" class="ednone_r" onKeyDown="return getNextElement();"  readonly ></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="rtnlimit_<%=h%>" VALUE="<%=ds_khdj.getValue("rtnlimit")%>" style="width:100%" class="ednone_r" onKeyDown="return getNextElement();"  readonly ></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="fundxs_<%=h%>" VALUE="<%=ds_khdj.getValue("fundxs")%>" style="width:100%" class="ednone_r" onKeyDown="return getNextElement();"  readonly ></td>
            </tr>
            <%
            ds_khdj.next();
      }
            for(; h < 25; h++){
           %>
           <tr>
           <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
           <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
           </tr>
            <%
              }
           %>
        </table>
        <script language="javascript">initDefaultTableRow('tableview2',1);</script>
</div>
</center>
  <SCRIPT LANGUAGE="javascript">INFO_EX = new TabControl('INFO_EX',0);
      AddTabItem(INFO_EX,'INFO_EX_0','tabDivINFO_EX_0','cntDivINFO_EX_0');
      AddTabItem(INFO_EX,'INFO_EX_1','tabDivINFO_EX_1','cntDivINFO_EX_1');
      //AddTabItem(INFO_EX,'INFO_EX_2','tabDivINFO_EX_2','cntDivINFO_EX_2');
      if (window.top.StatFrame+''!='undefined'){ var tmp_curtab=window.top.StatFrame.GetRegisterVar('INFO_EX');if (tmp_curtab!='') {SetActiveTab(INFO_EX,tmp_curtab);}}
      function setActive()
      {
          hideFrame('detailDiv');
          //blur();
      form1.submit();
      SetActiveTab(INFO_EX,'INFO_EX_1');
      return false;
      }
  </SCRIPT>

       </td>
    </tr>
  </table>
        <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">
         <tr>
         <td>&nbsp;</td>
         </tr>
          <tr>
            <td  noWrap class="tableTitle">
              <%String p = "sumitForm("+Operate.POST+");";String ret = "location.href='"+b_PrixFormulaBean.retuUrl+"'";%>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存(S) ">
              <pc:shortcut key="s" script="<%=p%>" />
              <input name="btnback" type="button" class="button" onClick="location.href='<%=b_PrixFormulaBean.retuUrl%>'" value=" 返回(C) ">
              <pc:shortcut key="c" script="<%=ret%>" />
            </td>
          </tr>
        </table>
</form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpaddinfg=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="300" height="300" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<SCRIPT LANGUAGE="javascript">
var currentJsgsobj;
function valueChange(i)
{
     currentJsgsobj = document.all['jsgs_'+i];
}
function plusClick(name)
{
 if(currentJsgsobj==null)return;
  else if(name=="plus")
 currentJsgsobj.value=currentJsgsobj.value+"+";
  else if(name=="subtract")
 currentJsgsobj.value=currentJsgsobj.value+"-";
  else if(name=="*")
 currentJsgsobj.value=currentJsgsobj.value+"*";
  else if(name=="chu")
 currentJsgsobj.value=currentJsgsobj.value+"/";
  else if(name=="left")
 currentJsgsobj.value=currentJsgsobj.value+"(";
  else if(name=="right")
 currentJsgsobj.value=currentJsgsobj.value+")";
else
currentJsgsobj.value=currentJsgsobj.value+name;
}
</SCRIPT>
<%out.print(retu);%>
</BODY>
</Html>