<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*,engine.erp.sale.dafa.*"%>
<%@ page import="java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "saler_prize";
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%
  if(!loginBean.hasLimits("saler_prize", request, response))
    return;
  B_SalerPrizeDetail B_SalerPrizeDetailBean = B_SalerPrizeDetail.getInstance(request);

%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript">
  function showInterFrame(oper,rownum)
  {
    paraStr = "saler_prize_modify.jsp?operate="+oper+"&rownum="+rownum;
    openSelectUrl(paraStr, "Modify", winopt2);
}
/*
  function showInterFrame(oper, rownum){
    var url = "saler_prix_modify.jsp?operate="+oper+"&rownum="+rownum;
    document.all.interframe1.src = url;
    showFrame('detailDiv',true,"",true);
  }
*/
  function hideInterFrame(){//隐藏FRAME
    hideFrame('detailDiv');
    form1.submit();
  }
  function hideFrameNoFresh(){
    hideFrame('detailDiv');
  }
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr>
    <td NOWRAP align="center"></td>
  </tr>
</table>
<%
  String retu = B_SalerPrizeDetailBean.doService(request, response);
  if(retu.indexOf("backList()")>-1 || retu.indexOf("toDetail()")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet dsCjjTable = B_SalerPrizeDetailBean.getCjjTable();


  String typeClass = "class=ednone";
  String numtypeClass = "class=ednone_r";
  String readonly = "readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
<table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
  <tr valign="top">
  <td width="400"><table border=0 CELLPADDING=0 CELLSPACING=0 class="table">
  <tr>
  <td  class="activeVTab">奖金明细</td>
  </tr>
</table>
<table class="editformbox" cellspacing=2 cellpadding=0 width="110%">
  <tr>
  <td>
<table cellspacing="2" cellpadding="0" border="0" width="100%" bgcolor="#f0f0f0">
     <td colspan="6" nowrap>
    <table cellspacing=0 width="100%" cellpadding=0>
     <tr>
         <td nowrap><div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_0');return false;">差价奖</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_1" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_1');return false;">提成奖</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_2" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_2');return false;">利息奖</a></div></td>
         <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a></td>
     </tr>
     </table>
     <div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:1000;hight:300;overflow-y:auto;overflow-x:auto;">
     <table id="tableview1" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
     <tr class="tableTitle">

           <td width="40">单据号</td>
           <td>日期</td>

           <td>产品代码</td>
           <td>品名</td>

           <td>规格</td>
           <td>数量</td>

           <td>单价</td>
           <td>金额</td>
           <td>差价奖</td>
           <td>基准价</td>
           <td>销售差价</td>
           <td width="50">差价提成率(%)</td>
           <td>计息天数</td>
           <td>回款天数</td>
           <td width="50">回款提成率(%)</td>
           <td>允许天数利息</td>
           <td>销售利息</td>
    </tr>
     <%
      int j=0;
      double zsl=0;
      double zjje = 0;
      double zcjj = 0;
      double zxscj = 0;
      double zyxtslx = 0;
      double zxslx = 0;

      RowMap[] cjjRows = B_SalerPrizeDetailBean.getCjjRows();
      int count = cjjRows.length;
      RowMap cjjdetail = null;

      for(; j<count; j++)
      {
        cjjdetail = cjjRows[j];
        zsl = zsl+Double.parseDouble(cjjdetail.get("sl").equals("")?"0":cjjdetail.get("sl"));
        zjje = zjje+Double.parseDouble(cjjdetail.get("jje").equals("")?"0":cjjdetail.get("jje"));
        zcjj = zcjj+Double.parseDouble(cjjdetail.get("cjj").equals("")?"0":cjjdetail.get("cjj"));
        zxscj = zxscj+Double.parseDouble(cjjdetail.get("xscj").equals("")?"0":cjjdetail.get("xscj"));
        zyxtslx= zyxtslx+Double.parseDouble(cjjdetail.get("yxtslx").equals("")?"0":cjjdetail.get("yxtslx"));
        zxslx = zxslx+Double.parseDouble(cjjdetail.get("xslx").equals("")?"0":cjjdetail.get("xslx"));
        String tdhwid = cjjdetail.get("tdhwid");
     %>
      <tr onclick="selectRow();" onDblClick="selectRow();showInterFrame(<%=B_SalerPrizeDetailBean.SHOW_DETAIL%>,<%=tdhwid%>)">

          <td class="td" align="right" width="40"><INPUT TYPE="TEXT" NAME="tdbh_<%=j%>" VALUE="<%=cjjdetail.get("tdbh")%>" style="width:60"  <%=typeClass%>  <%=readonly%>><INPUT TYPE="hidden" NAME="tdhwid_<%=j%>" VALUE="<%=cjjdetail.get("tdhwid")%>" style="width:50"  <%=typeClass%>  <%=readonly%>></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="rq_<%=j%>" VALUE="<%=cjjdetail.get("rq")%>" style="width:60"  <%=typeClass%> <%=readonly%>></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="cpbm_<%=j%>" VALUE="<%=cjjdetail.get("cpbm")%>" style="width:40" MAXLENGTH="10" <%=typeClass%>  <%=readonly%>>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="pm_<%=j%>" VALUE="<%=cjjdetail.get("pm")%>" style="width:50"  <%=typeClass%>  <%=readonly%>></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="gg_<%=j%>" VALUE="<%=cjjdetail.get("gg")%>" style="width:40"  <%=typeClass%>  <%=readonly%>></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="sl_<%=j%>" VALUE="<%=cjjdetail.get("sl")%>" style="width:50"  <%=numtypeClass%>  <%=readonly%>></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="dj_<%=j%>" VALUE="<%=cjjdetail.get("dj")%>" style="width:50"  <%=numtypeClass%>  <%=readonly%>></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="jje_<%=j%>" VALUE="<%=cjjdetail.get("jje")%>" style="width:50"  <%=numtypeClass%>  <%=readonly%>></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="cjj_<%=j%>" VALUE="<%=cjjdetail.get("cjj")%>" style="width:50"  <%=numtypeClass%>  <%=readonly%>></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="jzj_<%=j%>" VALUE="<%=cjjdetail.get("jzj")%>" style="width:50"  <%=numtypeClass%>  <%=readonly%>></td>
          <td class="td" align="right"  width="30"><INPUT TYPE="TEXT" NAME="xscj_<%=j%>" VALUE="<%=cjjdetail.get("xscj")%>" style="width:30"  <%=numtypeClass%>  <%=readonly%>></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="cjtcl_<%=j%>" VALUE="<%=cjjdetail.get("cjtcl")%>" style="width:30"  <%=numtypeClass%>  <%=readonly%>></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="jxts_<%=j%>" VALUE="<%=cjjdetail.get("jxts")%>" style="width:40"  <%=numtypeClass%>  <%=readonly%>></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="hlts_<%=j%>" VALUE="<%=cjjdetail.get("hlts")%>" style="width:40"  <%=numtypeClass%>  <%=readonly%>></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="hltcl_<%=j%>" VALUE="<%=cjjdetail.get("hltcl")%>" style="width:30"  <%=numtypeClass%>  <%=readonly%>></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="yxtslx_<%=j%>" VALUE="<%=cjjdetail.get("yxtslx")%>" style="width:60"  <%=numtypeClass%>  <%=readonly%>></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="xslx_<%=j%>" VALUE="<%=cjjdetail.get("xslx")%>" style="width:60"  <%=numtypeClass%>  <%=readonly%>></td>
      </tr>
      <%
      dsCjjTable.next();
        }
        j=count+1;
      %>
        <tr>
         <td class="td" align="right">&nbsp;</td>
         <td class="td" align="right"></td>
         <td class="td" align="right"></td>
         <td class="td" align="right"></td>
         <td class="td" align="right"></td>

         <td class="td" align="right"><%=engine.util.Format.formatNumber(String.valueOf(zsl),"#0.00")%></td>

         <td class="td" align="right"></td>
         <td class="td" align="right"><%=engine.util.Format.formatNumber(String.valueOf(zjje),"#0.00") %></td>
         <td class="td" align="right"><%=engine.util.Format.formatNumber(String.valueOf(zcjj),"#0.00") %></td>
         <td class="td" align="right"></td>
         <td class="td" align="right"><%=engine.util.Format.formatNumber(String.valueOf(zxscj),"#0.00") %></td>
         <td class="td" align="right"></td>
         <td class="td" align="right"></td>
         <td class="td" align="right"></td>
        <td class="td" align="right"></td>
         <td class="td" align="right"><%=engine.util.Format.formatNumber(String.valueOf(zyxtslx),"#0.00") %></td>
         <td class="td" align="right"><%=engine.util.Format.formatNumber(String.valueOf(zxslx),"#0.00") %></td>
        </tr>
        <%
        for(; j < 20; j++){
        %>
         <tr>
         <td class="td" align="right">&nbsp;</td>
         <td class="td" align="right"></td>
         <td class="td" align="right"></td>

         <td class="td" align="right"></td>
         <td class="td" align="right"></td>
         <td class="td" align="right"></td>
         <td class="td" align="right"></td>
         <td class="td" align="right"></td>
         <td class="td" align="right"></td>
         <td class="td" align="right"></td>
         <td class="td" align="right"></td>
        <td class="td" align="right"></td>
         <td class="td" align="right"></td>
         <td class="td" align="right"></td>
         <td class="td" align="right"></td>
         <td class="td" align="right"></td>
        <td class="td" align="right"></td>
        </tr>
        <%}%>
      </table>
      <script language="javascript">initDefaultTableRow('tableview1',1);</script>
      </div>
<div id="cntDivINFO_EX_1" class="tabContent" style="display:none;width:1000;hight:300;overflow-y:auto;overflow-x:auto;">
    <center>
        <table id="tableview2" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
            <tr class="tableTitle">

                  <td>编号</td>
                  <td>日期</td>
                  <td>地区</td>
                  <td>单位代码</td>
                  <td>单位</td>
                  <td>类型</td>

                  <td>结算方式</td>
                  <td>结算号</td>
                  <td>金额</td>
                  <td>回款提成率</td>
                  <td>提成奖</td>
                  <td>回款利息</td>
                  <td>备注</td>
            </tr>
     <%
      RowMap[] detailRows = B_SalerPrizeDetailBean.getTcjRows();
      int k=0;
      double ztcje=0;
      double ztcj=0;
      double zhllx=0;
      int rowcs = detailRows.length;
      RowMap zgjydetail = null;
      for(; k<detailRows.length; k++)
      {
        zgjydetail = detailRows[k];

        ztcje = ztcje+Double.parseDouble(zgjydetail.get("je").equals("")?"0":zgjydetail.get("je"));
        ztcj = ztcj+Double.parseDouble(zgjydetail.get("tcj").equals("")?"0":zgjydetail.get("tcj"));
        zhllx = zhllx+Double.parseDouble(zgjydetail.get("dhllx").equals("")?"0":zgjydetail.get("dhllx"));
     %>
            <tr onclick="selectRow();">

                <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="djh_<%=k%>" VALUE="<%=zgjydetail.get("djh")%>" style="width:60" <%=typeClass%>  <%=readonly%>></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="rq_<%=k%>" VALUE="<%=zgjydetail.get("rq")%>" style="width:60"  <%=typeClass%> <%=readonly%>></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="dqmc_<%=k%>" VALUE="<%=zgjydetail.get("dqmc")%>" style="width:60"  <%=typeClass%>  <%=readonly%>></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="dwdm_<%=k%>" VALUE="<%=zgjydetail.get("dwdm")%>" style="width:50" <%=typeClass%> <%=readonly%>></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="dwmc_<%=k%>" VALUE="<%=zgjydetail.get("dwmc")%>" style="width:106"  <%=typeClass%> <%=readonly%>></td>

                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="khlx_<%=k%>" VALUE="<%=zgjydetail.get("khlx")%>" style="width:30"  <%=typeClass%>  <%=readonly%>></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="jsfs_<%=k%>" VALUE="<%=zgjydetail.get("jsfs")%>" style="width:60"  <%=typeClass%>  <%=readonly%>></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="jsdh_<%=k%>" VALUE="<%=zgjydetail.get("jsdh")%>" style="width:50"  <%=typeClass%>  <%=readonly%>></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="je_<%=k%>" VALUE="<%=zgjydetail.get("je")%>" style="width:80"  <%=numtypeClass%>  <%=readonly%>></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="tcl_<%=k%>" VALUE="<%=zgjydetail.get("tcl")%>" style="width:50"  <%=numtypeClass%>  <%=readonly%>></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="tcj_<%=k%>" VALUE="<%=zgjydetail.get("tcj")%>" style="width:80"  <%=numtypeClass%>  <%=readonly%>></td>

                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="dhllx_<%=k%>" VALUE="<%=zgjydetail.get("dhllx")%>" style="width:80"  <%=numtypeClass%>  <%=readonly%>></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="bz_<%=k%>" VALUE="<%=zgjydetail.get("bz")%>" style="width:80"  <%=typeClass%>  <%=readonly%>></td>
            </tr>
            <%
             }
             k=rowcs+1;
           %>
             <tr>
              <td class="td" align="right">&nbsp;</td>
              <td class="td" align="right"></td>
              <td class="td" align="right"></td>
              <td class="td" align="right"></td>
              <td class="td" align="right"></td>

              <td class="td" align="right"></td>
              <td class="td" align="right"></td>
              <td class="td" align="right"></td>
              <td class="td" align="right"><%=engine.util.Format.formatNumber(String.valueOf(ztcje),"#0.00")%></td>
              <td class="td" align="right"></td>
              <td class="td" align="right"><%=engine.util.Format.formatNumber(String.valueOf(ztcj),"#0.00") %></td>

              <td class="td" align="right"><%=engine.util.Format.formatNumber(String.valueOf(zhllx),"#0.00") %></td>
              <td class="td" align="right"></td>
        </tr>
             <%
             for(; k < 20; k++){
             %>
              <tr>
              <td class="td" align="right">&nbsp;</td>
              <td class="td" align="right">&nbsp;</td>
              <td class="td" align="right">&nbsp;</td>
              <td class="td" align="right">&nbsp;</td>
              <td class="td" align="right">&nbsp;</td>

              <td class="td" align="right">&nbsp;</td>
              <td class="td" align="right">&nbsp;</td>
              <td class="td" align="right">&nbsp;</td>
              <td class="td" align="right">&nbsp;</td>
              <td class="td" align="right">&nbsp;</td>
               <td class="td" align="right">&nbsp;</td>

              <td class="td" align="right">&nbsp;</td>
              <td class="td" align="right">&nbsp;</td>

        </tr>
        <%}%>
        </table>
        <script language="javascript">initDefaultTableRow('tableview2',1);</script>
    </center>
</div>
<div id="cntDivINFO_EX_2" class="tabContent" style="display:none;width:900;hight:300;overflow-y:auto;overflow-x:auto;">
      <center>
            <table id="tableview3" border="0" cellspacing="1" cellpadding="0"  width="100%">
     <%
      RowMap zggzjldetail = B_SalerPrizeDetailBean.getLxRow();
     %>
                <tr>
                <td class="td"  align="left">资金占用费：=（A+B-C-D）<INPUT TYPE="TEXT"  VALUE="<%=engine.util.Format.formatNumber(zggzjldetail.get("lxj"),"#0.00") %>"   class="edline"  readonly></td>
                </tr>
                <tr>
                <td class="td"  align="left">A:销售允许天数利息=资金允许回笼天数*销售额*（资金占有费率/当月天数）:&nbsp;<INPUT TYPE="TEXT"  class="edline"  VALUE="<%=engine.util.Format.formatNumber(zggzjldetail.get("XSA"),"#0.00") %>"  <%=readonly%>></td>
                </tr>
                <tr>
                <td class="td"  align="left">B:销售回笼利息=（月结算天数-回款日）*回款金额*（资金占有费率/当月天数）:&nbsp;<INPUT TYPE="TEXT"  class="edline" VALUE="<%=engine.util.Format.formatNumber(zggzjldetail.get("XSB"),"#0.00") %>"   readonly></td>
                </tr>
                <tr>
                <td class="td"  align="left">C:应收款月初余额利息=月初余额*月结算天数*（资金占有费率/当月天数）:&nbsp;<INPUT TYPE="TEXT"  class="edline"  VALUE="<%=engine.util.Format.formatNumber(zggzjldetail.get("XSC"),"#0.00") %>"    readonly></td>
                </tr>
                <tr>
                <td class="td"  align="left">D:销售利息=（月结算天数-销售提货日期）*销售额*（资金占有费率/当月天数）:&nbsp;<INPUT TYPE="TEXT"   class="edline" VALUE="<%=engine.util.Format.formatNumber(zggzjldetail.get("XSD"),"#0.00") %>"    readonly></td>
                </tr>
                <tr>
                <td class="td"  align="left">当月天数:&nbsp;<INPUT TYPE="TEXT"  VALUE="<%=zggzjldetail.get("days")%>"    class="edline"  readonly></td>
                </tr>
            </table>
      </center>
</div>
  <SCRIPT LANGUAGE="javascript">INFO_EX = new TabControl('INFO_EX',0);
      AddTabItem(INFO_EX,'INFO_EX_0','tabDivINFO_EX_0','cntDivINFO_EX_0');
      AddTabItem(INFO_EX,'INFO_EX_1','tabDivINFO_EX_1','cntDivINFO_EX_1');
      AddTabItem(INFO_EX,'INFO_EX_2','tabDivINFO_EX_2','cntDivINFO_EX_2');
      if (window.top.StatFrame+''!='undefined'){ var tmp_curtab=window.top.StatFrame.GetRegisterVar('INFO_EX');if (tmp_curtab!='') {SetActiveTab(INFO_EX,tmp_curtab);}}
  </SCRIPT>
</td>
</tr>
</table>
<tr>
<td> </td>
</tr>
</table>
</table>
    <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
      <tr>
        <td noWrap class="tableTitle">
          <input name="btnback" type="button" class="button" onClick="window.close();" value='  关闭(C)  '>
          <pc:shortcut key="c" script='window.close()'/>
        </td>
      </tr>
    </table>
</form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="300" height="200" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(retu);%>
</body>
</html>