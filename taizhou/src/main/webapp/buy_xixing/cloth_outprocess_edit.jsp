      <%--采购申请单主表--%>
  <%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
  <%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal,java.util.ArrayList,engine.html.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%><%
  engine.erp.buy.xixing.B_ClothOutProcess B_ClothOutProcessBean = engine.erp.buy.xixing.B_ClothOutProcess.getInstance(request);
  String pageCode = "cloth_outprocess";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String SYS_APPROVE_ONLY_SELF =B_ClothOutProcessBean.SYS_APPROVE_ONLY_SELF;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  boolean isApproveOnly = SYS_APPROVE_ONLY_SELF.equals("1") ? true : false;//true仅制单人可提交
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
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);//LookUp部门
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  engine.project.LookUp buyOrderBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BUY_ORDER_GOODS);
  //engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);//LookUp结算方式
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//LookUp规格属性
  engine.project.LookUp wbBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_FOREIGN_CURRENCY);
  engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);//LookUp结算方式

  String retu = B_ClothOutProcessBean.doService(request, response);
  /* if(retu.indexOf("backList();")>-1)
  {
  out.print(retu);
  return;
  }*/
  EngineDataSet ds = B_ClothOutProcessBean.getMaterTable();
  HtmlTableProducer table = B_ClothOutProcessBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
  String loginId = B_ClothOutProcessBean.loginId;

  RowMap masterRow = B_ClothOutProcessBean.getMasterRowinfo();

  EngineDataSet PbqkmxTable = B_ClothOutProcessBean.getPbqkmxTable();
  EngineDataSet WjgqkTable = B_ClothOutProcessBean.getWjgqkTable();
  EngineDataSet WjgcpTable = B_ClothOutProcessBean.getWjgcpTable();

  RowMap[] PbqkmxRows= B_ClothOutProcessBean.getPbqkmxRowinfos();
  int ss = PbqkmxRows.length;
  RowMap[] WjgqkRows= B_ClothOutProcessBean.getWjgqkRowinfos();
  RowMap[] WjgcpRows= B_ClothOutProcessBean.getWjgcpRowinfos();

  String deptid = masterRow.get("deptid");
  String zt=masterRow.get("zt");
  String czyid = masterRow.get("czyid");
  boolean isHasDeptLimit = loginBean.getUser().isDeptHandle(deptid, czyid);//判断登陆员工是否有操作改制单人单据的权限

  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  String SYS_CUST_NAME = loginBean.getSystemParam("SYS_CUST_NAME");//客户名称,用于客制化程序
  //if ( !SYS_CUST_NAME.equals("essen") ) bjfs = "0";//如果是essen的话,那么程序就一没有换算数量,一直用计量单位来报价
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
  boolean  isRead = B_ClothOutProcessBean.isRep || B_ClothOutProcessBean.isApprove || (!B_ClothOutProcessBean.masterIsAdd() && (zt.equals("9") || zt.equals("2")));//表示在审批中或已入库不能修改
  String slClass_r = (isRead || !isHasDeptLimit) ? "class=ednone_r" : "class=edFocused_r";

  boolean isCanAmend = true;//判断取消审批后主表数据是否能修改
  boolean isCanRework = true;//判断取消审批后从表数据是否能修改
  // boolean isEnd = zt.equals("1");
  boolean isEnd = B_ClothOutProcessBean.isRep||B_ClothOutProcessBean.isReport || B_ClothOutProcessBean.isApprove || (!B_ClothOutProcessBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !B_ClothOutProcessBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(B_ClothOutProcessBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
  String edClass = isEnd  ? "class=edline" : "class=edbox";
  String detailClass = isEnd  ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd  ? "class=ednone_r" : "class=edFocused_r";
  //String readonly = isEnd  ? " readonly" : "";
  String title = zt.equals("1") ? ("已审核"/* 审核人:"+ds.getValue("shr")*/) : (zt.equals("9") ? "审批中" : "未审核");

  String readonly = (isEnd || !isHasDeptLimit) ? " readonly" : "";
  String masterReadonly = isCanAmend ? readonly : "readonly";

  String slReadonly = (isRead || !isHasDeptLimit) ? "readonly" : "";//数量和单价在审批的时候可以修改,汇率在审批通过也可以修改

  deptBean.regData(ds, "deptid");personBean.regData(ds, "personid");
  if(!isEnd)
    personBean.regConditionData(ds, "deptid");
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript">

  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}


    function sumitForm(oper, row)
    {
      lockScreenToWait("处理中, 请稍候！");
      form1.rownum.value = row;
      form1.operate.value = oper;
      form1.submit();
    }
    function backList()
    {
      location.href='cloth_outprocess.jsp';
    }
    function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'jbr', 'deptid', eval('form1.deptid.value'), '');
     }
     function productCodeSelect(obj, i)
     {
       ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
     }
     function corpCodeSelect(obj,i)
     {
       ProvideCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid_'+i+'&srcVar=dwdm_'+i+'&srcVar=dwmc_'+i,
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=B_ClothOutProcessBean.ONCHANGE%>)');
                   }
                   function bcorpCodeSelect(obj,j)
                   {
                     ProcessCodeChange(document.all['prod'], obj.form.name, 'srcVar=bdwtxid_'+j+'&srcVar=bdwdm_'+j+'&srcVar=bdwmc_'+j,
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=B_ClothOutProcessBean.BONCHANGE%>)');
                   }
                   function productNameSelect(obj,i)
                   {
                     ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
                   }
                   function productCodeSelect1(obj, i)
                   {
                     ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=bcpid_'+i+'&srcVar=cpbm1_'+i+'&srcVar=product1_'+i+'&srcVar=jldw1_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i,'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value);
                   }
                   function bproductNameSelect(obj,i)
                   {
                     ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=bcpid_'+i+'&srcVar=cpbm1_'+i+'&srcVar=product1_'+i+'&srcVar=jldw1_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i,'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value);
                   }
                   function propertyNameSelect(obj,bcpid,i)
                   {
                     PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                                        'fieldVar=dmsxid&fieldVar=sxz', bcpid, obj.value);
                   }


                   //     function corpNameSelect(obj)
                   // {
                   //  ProvideNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid_'+i+'&srcVar=dwdm_'+i'&srcVar=dwmc_'+i,
                   //    'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=B_ClothOutProcessBean.ONCHANGE%>)');
                   //  }
                   function corpNameSelect(obj,i)
                   {
                     ProvideNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid_'+i+'&srcVar=dwdm_'+i+'&srcVar=dwmc_'+i,
                            'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=B_ClothOutProcessBean.ONCHANGE%>)');
                              }
                              function bcorpNameSelect(obj,j)
                              {
                                ProcessNameChange(document.all['prod'], obj.form.name, 'srcVar=bdwtxid_'+j+'&srcVar=bdwdm_'+j+'&srcVar=bdwmc_'+j,
            'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=B_ClothOutProcessBean.BONCHANGE%>)');
              }
              function cal_tota(type)
              {
                var tmpObj;
                var tot=0;
     for(i=0; i<<%=PbqkmxRows.length%>; i++)
       {
       if(type == 'bpsl')
         tmpObj = document.all['bpsl_'+i];
       else if(type=='grsl')
         tmpObj = document.all['grsl_'+i];


       else if(type=='je0')
         tmpObj = document.all['je0_'+i];
       else
         return;

       if(tmpObj.value!="" && !isNaN(tmpObj.value))
         tot += parseFloat(tmpObj.value);
     }

     if(type == 'bpsl')
       document.all['t_bpsl'].value = formatQty(tot);
     else if(type == 'grsl')
       document.all['t_grsl'].value = formatSum(tot);
     else if(type == 'je0')
       document.all['t_cje'].value = formatSum(tot);
              }

    function cal_totb(type)
              {
                var tmpObj;
                var tot=0;
  for(i=0; i<<%=WjgqkRows.length%>; i++)
    {
    if(type == 'trsl')
      tmpObj = document.all['trsl_'+i];
    else if(type=='ccsl')
      tmpObj = document.all['ccsl_'+i];
    else if(type=='cpsl')
      tmpObj = document.all['cpsl_'+i];
    else if(type=='jgje')
      tmpObj = document.all['jgje_'+i];
    else if(type=='yssl')
      tmpObj = document.all['yssl_'+i];
    else
      return;

    if(tmpObj.value!="" && !isNaN(tmpObj.value))
      tot += parseFloat(tmpObj.value);
  }

  if(type == 'trsl')
    document.all['t_trsl'].value = formatQty(tot);
  else if(type == 'ccsl')
    document.all['t_ccsl'].value = formatSum(tot);
  else if(type == 'cpsl')
    document.all['t_cpsl'].value = formatSum(tot);
  else if(type == 'jgje')
    document.all['t_bje'].value = formatSum(tot);
  else if(type == 'yssl')
    document.all['t_yssl'].value = formatSum(tot);

              }
function cal_totc(type)
{
  var tmpObj;
  var tot=0;
  for(i=0; i<<%=WjgcpRows.length%>; i++)
  {
    if(type == 'sl')
      tmpObj = document.all['sl_'+i];
    else if(type == 'hssl')
      tmpObj = document.all['hssl_'+i];

    else if(type=='je2')
      tmpObj = document.all['je2_'+i];
    else
      return;
    if(tmpObj.value!="" && !isNaN(tmpObj.value))
      tot += parseFloat(tmpObj.value);
  }

  if(type == 'sl')
    document.all['t_sl'].value = formatQty(tot);
  else if(type == 'hssl')
    document.all['t_hssl'].value = formatQty(tot);
  else if(type == 'je2')
    document.all['t_je'].value = formatSum(tot);

    }
      function sl_onchange(i,s, isBigUnit)
      {
        var bpslObj = document.all['bpsl_'+i];
        var grslObj = document.all['grsl_'+i];
        var djObj = document.all['dj_'+i];
        //var jeObj = document.all['je'+s+'_'+i];
        var trslObj = document.all['trsl_'+i];

        var ccslObj = document.all['ccsl_'+i];
        var cpslObj = document.all['cpsl_'+i];
        var ysslObj = document.all['yssl_'+i];
        var yslObj = document.all['ysl_'+i];
        var jgdjObj = document.all['jgdj_'+i];
        var jgjeObj = document.all['jgje_'+i];
        var je0Obj = document.all['je0_'+i];
        var jgwdjObj = document.all['jgwdj_'+i];
        var slObj = document.all['sl_'+i];
        var je2Obj = document.all['je2_'+i];
        if (s==0)
        {
          if(bpslObj.value=="")
            return;
          if(isNaN(bpslObj.value)){
            alert("输入的数量非法");
            bpslObj.focus();
            return;
          }
          if(bpslObj.value<0){
            alert("不能输入小于等于零的数")
                return;
          }
          cal_tota('bpsl');
          if(djObj.value=="")
            return;
          if(isNaN(djObj.value))
          {
            alert("输入的单价非法");
            djObj.focus();
            return;
          }
          if (bpslObj.value!="" && !isNaN(bpslObj.value)){
            //alert(bpslObj.value);alert(bpslObj.value);
            je0Obj.value = formatSum(parseFloat(bpslObj.value) * parseFloat(djObj.value));
            cal_tota('je0');
          }
          else
            je0Obj.value='';
          if(grslObj.value=="")
            return;
          if(isNaN(grslObj.value)){
            alert("输入的数量非法");
            grslObj.focus();
            return;
          }
          if(grslObj.value<0){
            alert("不能输入小于等于零的数")
                return;
          }
          cal_tota('grsl');
          return;
        }
        if (s==1)
        {
          if(trslObj.value=="")
            return;
          if(isNaN(trslObj.value)){
            alert("输入的数量非法");
            trslObj.focus();
            return;
          }
          if(trslObj.value<0){
            alert("不能输入小于等于零的数")
                return;
          }
          cal_totb('trsl');
          if(jgdjObj.value=="")
            return;
          if(isNaN(jgdjObj.value))
          {
            alert("输入的单价非法");
            jgdjObj.focus();
            return;
          }
          if(ccslObj.value=="")
            return;
          if(isNaN(ccslObj.value)){
            alert("输入的数量非法");
            ccslObj.focus();
            return;
          }
          if(ccslObj.value<0){
            alert("不能输入小于等于零的数")
                return;
          }
          cal_totb('ccsl');

          if (ccslObj.value!="" && !isNaN(ccslObj.value)){
            // alert(trslObj.value);alert(trslObj.value);
            jgjeObj.value = formatPrice(parseFloat(ccslObj.value) * parseFloat(jgdjObj.value));
            cal_totb('jgje');
          }
          else
            jeObj.value='';
          ysslObj.value = formatSum(parseFloat(ccslObj.value) - parseFloat(trslObj.value));
          yslObj.value = formatSum(parseFloat(ysslObj.value)/parseFloat(trslObj.value)*100);
          cal_totb('yssl');


          if(cpslObj.value=="")
            return;
          if(isNaN(cpslObj.value)){
            alert("输入的数量非法");
            cpslObj.focus();
            return;
          }
          if(cpslObj.value<0){
            alert("不能输入小于等于零的数")
                return;
          }
          cal_totb('cpsl');
          return;
        }
        if (s==2)
        {
          if(slObj.value=="")
            return;
          if(isNaN(slObj.value)){
            alert("输入的数量非法");
            slObj.focus();
            return;
          }
          if(slObj.value<0){
            alert("不能输入小于等于零的数")
                return;
          }
          cal_totc('sl');
          if(jgwdjObj.value=="")
            return;
          if(isNaN(jgwdjObj.value))
          {
            alert("输入的单价非法");
            jgwdjObj.focus();
            return;
          }
          if (slObj.value!="" && !isNaN(slObj.value)){
            // alert(slObj.value);alert(slObj.value);
            je2Obj.value = formatSum(parseFloat(slObj.value) * parseFloat(jgwdjObj.value));
            cal_totc('je2');
          }
          else
            je2Obj.value='';
          //slObj.value="";
          //cal_tot('sl');
          // cal_tot('je');
          return;

        }
        //jeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(jgwdjObj.value));
      }
    function je_onchange(i,s, isBigUnit)
{
  var bpslObj = document.all['bpsl_'+i];
  var grslObj = document.all['grsl_'+i];
  var djObj = document.all['dj_'+i];
  //var jeObj = document.all['je'+s+'_'+i];
  var trslObj = document.all['trsl_'+i];

  var ccslObj = document.all['ccsl_'+i];
  var cpslObj = document.all['cpsl_'+i];
  var ysslObj = document.all['yssl_'+i];
  var yslObj = document.all['ysl_'+i];
  var jgdjObj = document.all['jgdj_'+i];
  var jgjeObj = document.all['jgje_'+i];
  var je0Obj = document.all['je0_'+i];
  var jgwdjObj = document.all['jgwdj_'+i];
  var slObj = document.all['sl_'+i];
  var je2Obj = document.all['je2_'+i];
  if (s==0)
  {
    if(bpslObj.value=="")
  return;
if(isNaN(bpslObj.value)){
  alert("输入的数量非法");
  bpslObj.focus();
  return;
}
if(bpslObj.value<0){
  alert("不能输入小于等于零的数")
      return;
}
cal_tota('bpsl');

if(isNaN(djObj.value))
{
  alert("输入的单价非法");
  djObj.focus();
  return;
}
if (je0Obj.value!="" && !isNaN(je0Obj.value)){
  //alert(bpslObj.value);alert(bpslObj.value);
  djObj.value = formatPrice(parseFloat(je0Obj.value) / parseFloat(bpslObj.value));
  //je0Obj.value = formatSum(parseFloat(bpslObj.value) * parseFloat(djObj.value));
  cal_tota('je0');
}
else
  je0Obj.value='';
if(grslObj.value=="")
  return;
if(isNaN(grslObj.value)){
  alert("输入的数量非法");
  grslObj.focus();
  return;
}
if(grslObj.value<0){
  alert("不能输入小于等于零的数")
      return;
}
cal_tota('grsl');
          return;
  }
  if (s==1)
  {
    if(trslObj.value=="")
      return;
    if(isNaN(trslObj.value)){
      alert("输入的数量非法");
      trslObj.focus();
      return;
    }
    if(trslObj.value<0){
      alert("不能输入小于等于零的数")
          return;
    }
    cal_totb('trsl');

    if(isNaN(jgdjObj.value))
    {
      alert("输入的单价非法");
      jgdjObj.focus();
      return;
    }
    if(ccslObj.value=="")
      return;
    if(isNaN(ccslObj.value)){
      alert("输入的数量非法");
      ccslObj.focus();
      return;
    }
    if(ccslObj.value<0){
      alert("不能输入小于等于零的数")
          return;
    }
    cal_totb('ccsl');

    if (jgjeObj.value!="" && !isNaN(jgjeObj.value)){
      // alert(trslObj.value);alert(trslObj.value);
      jgdjObj.value = formatPrice(parseFloat(jgjeObj.value) / parseFloat(ccslObj.value));
      //jgjeObj.value = formatSum(parseFloat(trslObj.value) * parseFloat(jgdjObj.value));
      cal_totb('jgje');
    }
    else
      jeObj.value='';
    ysslObj.value = formatSum(parseFloat(ccslObj.value) - parseFloat(trslObj.value));
    yslObj.value = formatSum(parseFloat(ysslObj.value)/parseFloat(trslObj.value));
    cal_totb('yssl');
    if(cpslObj.value=="")
  return;
if(isNaN(cpslObj.value)){
  alert("输入的数量非法");
  cpslObj.focus();
  return;
}
if(cpslObj.value<0){
  alert("不能输入小于等于零的数")
      return;
}
    cal_totb('cpsl');
    return;
  }
  if (s==2)
  {
    if(slObj.value=="")
  return;
if(isNaN(slObj.value)){
  alert("输入的数量非法");
  slObj.focus();
  return;
}
if(slObj.value<0){
  alert("不能输入小于等于零的数")
      return;
}
cal_totc('sl');

if(isNaN(jgwdjObj.value))
{
  alert("输入的单价非法");
  jgwdjObj.focus();
  return;
}
if (je2Obj.value!="" && !isNaN(je2Obj.value)){
  // alert(trslObj.value);alert(trslObj.value);
  jgwdjObj.value = formatPrice(parseFloat(je2Obj.value) / parseFloat(slObj.value));
  //jgjeObj.value = formatSum(parseFloat(trslObj.value) * parseFloat(jgdjObj.value));
  cal_totc('je2');
}
else
  je2Obj.value='';
    return;
  }
  //jeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(jgwdjObj.value));
      }
function slk_onchange(k,isBigUnit)
{
  var oldhsblObj = document.all['hsbl_'+k];
  var sxzObj = document.all['sxz_'+k];
  unitConvert(document.all['prod_'+k], 'form1', 'srcVar=truebl_'+k, 'exp='+oldhsblObj.value, sxzObj.value, 'nsl_onchange('+k+','+isBigUnit+')');
}
function nsl_onchange(k,isBigUnit)
{
  var slObj = document.all['sl_'+k];
  var hsslObj = document.all['hssl_'+k];
  var djObj = document.all['jgwdj_'+k];
  var jeObj = document.all['je2_'+k];
  var hsblObj = document.all['truebl_'+k];
        if('<%=bjfs%>'=="0")//报价方式为按计量单位报价时判断
        {
          if(slObj.value=="")
          return;
          if(isNaN(slObj.value)){
            alert("输入的数量非法");
            slObj.focus();
            return;
          }
        //  if(slObj.value<0){
        //    alert("不能输入小于等于零的数")
       //         return;
       //  }

         if(hsblObj.value!="" && !isNaN(hsblObj.value) && hsblObj.value!="0")
           hsslObj.value= formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
         else
           hsslObj.value=formatQty(parseFloat(slObj.value)/1);
         cal_totc('hssl');
         cal_totc('sl');
         if(isNaN(djObj.value))
         {
           alert('输入的单价非法');
           return;
         }
         if(djObj.value=="")
           return;
         if(slObj.value!="" && !isNaN(slObj.value)){
           jeObj.value = formatQty(parseFloat(slObj.value) * parseFloat(djObj.value));
        //  if(hlObj.value!='' && !isNaN(hlObj.value) && hlObj.value!=0)
          //   ybjeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
         //  else
          //   ybjeObj.value ='';
         }
         else{
           jeObj.value='';
          // ybjeObj.value ='';
         }
       }
       else//按换算单位报价的判断和计算
       {
        if(slObj.value=="")
           return;
         if(isNaN(slObj.value)){
           alert("输入的换算数量非法");
           slObj.focus();
           return;
         }
       //    if(slObj.value<0){
       //      alert("不能输入小于等于零的数")
       //      return;
      //   }
         //alert("b")
         if(hsblObj.value!="" && !isNaN(hsblObj.value))
           //slObj.value= formatQty(parseFloat(hsslObj.value) * parseFloat(hsblObj.value));
          hsslObj.value= formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
         else
          hsslObj.value=formatQty(parseFloat(slObj.value)/1);
         cal_totc('sl');
         cal_totc('hssl');

         if(djObj.value=="")
           return;
         if(hsslObj.value!="" && !isNaN(hsslObj.value)){
           jeObj.value = formatQty(parseFloat(hsslObj.value) * parseFloat(djObj.value));
         //  if(hlObj.value!='' && !isNaN(hlObj.value) && hlObj.value!=0)
        //     ybjeObj.value = formatSum(parseFloat(hsslObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
        //   else
         //    ybjeObj.value ='';
         }
           else{
           jeObj.value='';
           //ybjeObj.value ='';
         }
       }

       cal_totc('je');
       //cal_tot('ybje');
   }

   function hssl_onchange(k,isBigUnit)
  {
    var oldhsblObj = document.all['hsbl_'+k];
    var sxzObj = document.all['sxz_'+k];
    unitConvert(document.all['prod_'+k], 'form1', 'srcVar=truebl_'+k, 'exp='+oldhsblObj.value, sxzObj.value, 'msl_onchange('+k+','+isBigUnit+')');
  }
  function msl_onchange(k,isBigUnit)
  {
    var slObj = document.all['sl_'+k];
    var hsslObj = document.all['hssl_'+k];
    var djObj = document.all['jgwdj_'+k];
    var jeObj = document.all['je2_'+k];
    var hsblObj = document.all['truebl_'+k];
    if('<%=bjfs%>'=="0")//报价方式为按计量单位报价时判断
      {
      //alert("asdf");
      if(hsslObj.value=="")
        return;
      if(isNaN(hsslObj.value)){
        alert("输入的换算数量非法");
        hsslObj.focus();
        return;
      }
//  if(hsslObj.value<0){
 //       alert("不能输入小于等于零的数")
 //           return;
//      }
      if(hsblObj.value!="" && !isNaN(hsblObj.value))
        slObj.value= formatQty(parseFloat(hsslObj.value) * parseFloat(hsblObj.value));
      //hsslObj.value= formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
      else
        slObj.value=formatQty(parseFloat(hsslObj.value) * 1);
      cal_totc('sl');
      cal_totc('hssl');
      if(isNaN(djObj.value))
      {
        alert('输入的单价非法');
        return;
      }
      if(djObj.value=="")
        return;
      if(slObj.value!="" && !isNaN(slObj.value)){
        //alert("d");
        jeObj.value = formatQty(parseFloat(slObj.value) * parseFloat(djObj.value));
        //  if(hlObj.value!='' && !isNaN(hlObj.value) && hlObj.value!=0)
        //   ybjeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
        //  else
        //   ybjeObj.value ='';
      }
      else{
        jeObj.value='';
        // ybjeObj.value ='';
      }
    }
    else//按换算单位报价的判断和计算
    {
      //alert("ddd");
      if(hsslObj.value=="")
        return;
      if(isNaN(hsslObj.value)){
        alert("输入的数量非法");
        hsslObj.focus();
        return;
      }
    //  if(hsslObj.value<0){
   //     alert("不能输入小于等于零的数")
   //         return;
   //   }

      if(hsblObj.value!="" && !isNaN(hsblObj.value) && hsblObj.value!="0")
        //hsslObj.value= formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
    slObj.value= formatQty(parseFloat(hsslObj.value) * parseFloat(hsblObj.value));
      else
        slObj.value=formatQty(parseFloat(hsslObj.value) * 1);
      cal_totc('hssl');
      cal_totc('sl');

      if(djObj.value=="")
        return;
      if(hsslObj.value!="" && !isNaN(hsslObj.value)){
        jeObj.value = formatPrice(parseFloat(hsslObj.value) * parseFloat(djObj.value));
        //  if(hlObj.value!='' && !isNaN(hlObj.value) && hlObj.value!=0)
        //     ybjeObj.value = formatSum(parseFloat(hsslObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
        //   else
        //    ybjeObj.value ='';
      }
      else{
        jeObj.value='';
        //ybjeObj.value ='';
      }
    }
    cal_totc('je');
    //cal_tot('ybje');
    }



    function djk_onchange(k, isBigUnit)
{
var slObj = document.all['sl_'+k];
var hsslObj = document.all['hssl_'+k];
//var hsblObj = document.all['truebl_'+k];
var djObj = document.all['jgwdj_'+k];
var jeObj = document.all['je2_'+k];
var sjrklObj = document.all['sjrkl_'+k];
var bjfs = <%=bjfs%>;
var hlObj = form1.hl;
//var ybjeObj = document.all['ybje_'+i];
if(djObj.value==""){
 jeObj.value='';
// ybjeObj.value='';
 return;
}
if(isNaN(djObj.value)){
 alert("输入的单价非法");
 djObj.focus();
 return;
}
if(djObj.value<0){
alert("不能输入小于等于零的数")
return;
}
if(bjfs==0)
{
 if(slObj.value!="" && !isNaN(slObj.value)){
    jeObj.value = formatQty(parseFloat(slObj.value) * parseFloat(djObj.value));
   // if(hlObj.value!='' && !isNaN(hlObj.value) && hlObj.value!=0)
     // ybjeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
    //else
      //ybjeObj.value ='';
 }
 else{
   jeObj.value='';
  // ybjeObj.value ='';
 }
}
else
{
 if(hsslObj.value!="" && !isNaN(hsslObj.value)){
   jeObj.value = formatQty(parseFloat(hsslObj.value) * parseFloat(djObj.value));
  // if(hlObj.value!='' && !isNaN(hlObj.value) && hlObj.value!=0)
 //    ybjeObj.value = formatSum(parseFloat(hsslObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
 //  else
 //     ybjeObj.value ='';
 }
 else{
   jeObj.value='';
   //ybjeObj.value ='';
 }
}
cal_totc('je');
//cal_tot('ybje');
    }




    function jek_onchange(k, isBigUnit)
{
 var slObj = document.all['sl_'+k];
 var djObj = document.all['jgwdj_'+k];
 var jeObj = document.all['je2_'+k];
 var hsslObj = document.all['hssl_'+k];
 //var hsblObj = document.all['truebl_'+k];
 var hlObj = form1.hl;
 var bjfs = <%=bjfs%>;

 //var ybjeObj = document.all['ybje_'+i];
 //var obj = isBigUnit ? ybjeObj : jeObj;
 var obj = jeObj;
 var showText = isBigUnit ? "输入的原币金额非法" : "输入的金额非法";
 var showText2 = isBigUnit ? "输入的原币金额小于零" : "输入的金额小于零";
 //var changeObj = isBigUnit ? jeObj : ybjeObj;
 var changeObj = jeObj
 if(obj.value=="")
   return;
 if(isNaN(obj.value))
 {
   alert(showText);
   obj.focus();
   return;
 }
//  if(obj.value<0)
//  {
 //     alert(showText2);
 //    obj.focus();
 //    return;
 //   }
 if(!isBigUnit){
   if(bjfs==0){
     if(slObj.value!="" && !isNaN(slObj.value && slObj.value!="0")){
       djObj.value = Math.abs(formatPrice(parseFloat(jeObj.value) / parseFloat(slObj.value)));
       if(jeObj.value*slObj.value<0)
       {
         hsslObj.value=formatQty(-1*hsslObj.value);
         slObj.value=formatQty(-1*slObj.value);
       }
     }
   }
   else{

     if(hsslObj.value!="" && !isNaN(hsslObj.value && hsslObj.value!="0"))
     {
       djObj.value =Math.abs(formatPrice(parseFloat(jeObj.value) / parseFloat(hsslObj.value)));
       if(jeObj.value*slObj.value<0)
       {
         hsslObj.value=formatQty(-1*hsslObj.value);
         slObj.value=formatQty(-1*slObj.value);
       }
     } //Math.abs
   }
 }
 cal_totc('je');
    }

</script>
<BODY oncontextmenu="window.event.returnValue=true"  >
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr>
    <td NOWRAP align="center"></td>
  </tr>
</table>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
  <table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
    <tr valign="top">
      <td width="400"> <table width="90" border=0 CELLPADDING=0 CELLSPACING=0 class="table">
          <tr>
            <td width="190"  class="activeVTab">坯布外加工单(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
          <tr>
            <td> <table cellspacing="2" cellpadding="0" border="0" width="100%" bgcolor="#f0f0f0">
                <INPUT TYPE="HIDDEN" NAME="personid"  VALUE="">
                <tr>
                  <td nowrap class="tdTitle">外加工单号</td>
                  <td noWrap class="td"><input type="text" name="djh" value='<%=masterRow.get("djh")%>' maxlength='<%=ds.getColumn("djh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <%--  <td nowrap class="td"> <input type="text" <%=edClass%>  name="djh" value='<%=masterRow.get("djh")%>' maxlength="6" style="width:130"  onKeyDown="return getNextElement();" >--%>
                  </td>
                  <td nowrap class="tdTitle">采购日期</td>
                  <td nowrap class="td"><input type="text" name="slrq" value='<%=masterRow.get("slrq")%>' maxlength="10" style="width:100" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();" <%=masterReadonly%>>
                    <%if(!isEnd && isCanAmend && isHasDeptLimit){%>
                    <a href="#"><img src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onClick="selectDate(slrq);"></a>
                    <%}%>
                  </td>
                  <td nowrap class="tdTitle">订单日期</td>
                  <td nowrap class="td"><input type="text" name="ddrq" value='<%=masterRow.get("ddrq")%>' maxlength="10" style="width:100" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();" <%=masterReadonly%>>
                   <%if(!isEnd && isCanAmend && isHasDeptLimit){%>
                    <a href="#"><img src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onClick="selectDate(ddrq);"></a>
                   <%}%>
                  </td>
                  <td height="22" nowrap class="tdTitle">部门</td>
                  <td nowrap class="td">
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:100' class='edline' readonly>");
                    else {%>
                    <pc:select  name="deptid" addNull="1" style="width:111"  onSelect="deptchange();"  >
                    <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <%String jbr=masterRow.get("jbr");%>
                  <td nowrap class="tdTitle">采购(经办人)</td>
                  <td noWrap class="td">
                     <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+personBean.getLookupName(masterRow.get("personid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                     <pc:select combox="1" name="jbr" addNull="1" style="width:110"   value='<%=jbr%>'>
                    <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%> </pc:select>
                    <%}%>

                 <td noWrap class="tdTitle">仓库</td>
                  <td noWrap class="td">
                    <%String store_Change = "if(form1.storeid.value!='"+masterRow.get("storeid")+"')sumitForm("+")";%>
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:90' class='edline' readonly>");
                    else {%>
                    <pc:select name="storeid" addNull="1" style="width:90" > <%=storeBean.getList(masterRow.get("storeid"))%>
                    </pc:select>
                    <%}%>
                  </td>
                     </td>
                  </td>
                  <td nowrap class="tdTitle">备注</td>
                  <td colspan="2" nowrap class="td"> <input type="text" name="bz" value='<%=masterRow.get("bz")%>' maxlength='64' style="width:130" <%=edClass%> onKeyDown="return getNextElement();"<%=masterReadonly%> >
                  </td>
              </tr>
               <% int width1 = (PbqkmxRows.length > 3 ? PbqkmxRows.length : 3)*23 + 66;%>
               <% int width2 = (WjgqkRows.length > 3 ? WjgqkRows.length : 3)*23 + 80;%>
               <% int width3 = (WjgcpRows.length > 3 ? WjgcpRows.length : 3)*23 + 66;%>
                <tr>
                  <td colspan="8" nowrap> <table cellspacing=0 width="100%" cellpadding=0>
                      <tr>
                        <td nowrap> <div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_0');return false;">坯布情况明细表</a></div></td>
                        <td nowrap> <div id="tabDivINFO_EX_1" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_1');return false;">外加工情况明细表</a></div></td>
                        <td nowrap> <div id="tabDivINFO_EX_2" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_2');return false;">加工成品布料明细表</a></div></td>
                        <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a></td>
                      </tr>
                    </table>
                    <div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:750;height=<%=width1%>;overflow-y:auto;overflow-x:auto;">
                      <table id="tableview1" border="0" cellspacing="1" cellpadding="0" class="table" width=100% >
                        <tr class="tableTitle">
                          <td height='20' align="center" nowrap>
                            <%if(!isEnd && isHasDeptLimit){%>
                            <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=B_ClothOutProcessBean.PBQKMX_ADD%>,-1);" src="../images/add_big.gif" border="0">
                             <%--<pc:shortcut key="a" script='<%="sumitForm("+ Operate.PBQKMX_ADD +",-1)"%>'/>--%>
                            <%}%>
                          <td noWrap>坯布编码</td>
                          <td noWrap>坯布名称规格</td>
                          <td noWrap>供应单位名称</td>
                          <td noWrap>本批数量</td>
                          <td noWrap>计量单位</td>
                          <td width=80 noWrap>单价</td>
                          <td width=80 noWrap>金额</td>
                          <td noWrap>购入时间</td>
                          <td noWrap>购入数量</td>
                          <td noWrap>结算方式</td>
                          <td nowrap>客户类型</td>
                        </tr>
                        <%BigDecimal t_cje = new BigDecimal(0),t_bpsl = new BigDecimal(0),t_grsl = new BigDecimal(0);
                          corpBean.regData(PbqkmxTable,"dwtxid");
                          prodBean.regData(PbqkmxTable,"cpid");
                          RowMap PbqkmxRow = null;
                          int i=0;
                          for(;i<PbqkmxRows.length;i++)
                          {
                            PbqkmxRow = PbqkmxRows[i];
                            String s_cje = PbqkmxRow.get("je");
                            String s_bpsl = PbqkmxRow.get("bpsl");
                            String s_grsl = PbqkmxRow.get("grsl");
                            BigDecimal cje = s_cje.length() > 0 ? (new BigDecimal(s_cje)) : new BigDecimal(0);
                            BigDecimal bpsl = s_bpsl.length() > 0 ? (new BigDecimal(s_bpsl)) : new BigDecimal(0);
                            BigDecimal grsl = s_grsl.length() > 0 ? (new BigDecimal(s_grsl)) : new BigDecimal(0);
                            boolean isline = !isCanRework || !isHasDeptLimit;
                            String Class = isline ? "class=ednone" : detailClass;//从表Class模式
                            RowMap  prodRow= prodBean.getLookupRow(PbqkmxRow.get("cpid"));
                            t_cje = t_cje.add(cje);
                            t_bpsl = t_bpsl.add(bpsl);
                            t_grsl = t_grsl.add(grsl);
                            String bjsfsid = "bjsfsid_"+i;
                            String khlxb = "khlxb_"+i;
                       %>
                        <tr class="td" > <td nowrap>
                        <%if(!isEnd && isCanRework && isHasDeptLimit){%>
                           <img style='cursor:hand' title='单选物资' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw')">
                          <%}if(!isEnd && isHasDeptLimit){%>
                            <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=B_ClothOutProcessBean.PBQKMX_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                        <%}%>
                          </td>
                          <td class="td" nowrap> <input type="hidden" name="cpid_<%=i%>" value="<%=PbqkmxRow.get("cpid")%>">
                            <input type="text" <%=Class%> onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=masterReadonly%> >
                          </td>
                          <td class="td" nowrap><input type="text" <%=Class%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=masterReadonly%> >
                          </td>
                            <%RowMap dwpRow =corpBean.getLookupRow(PbqkmxRow.get("dwtxid"));%>
                          <td noWrap class="td" > <input type="hidden" name="dwtxid_<%=i%>" value='<%=PbqkmxRow.get("dwtxid")%>'>
                            <input type="text" <%=Class%>  onKeyDown="return getNextElement();" name="dwdm_<%=i%>" value='<%=dwpRow.get("dwdm")%>' onchange="corpCodeSelect(this,<%=i%>)" <%=masterReadonly%>>
                            <input type="text" <%=Class%> name="dwmc_<%=i%>" onKeyDown="return getNextElement();" value='<%=corpBean.getLookupName(PbqkmxRow.get("dwtxId"))%>'  onchange="corpNameSelect(this,<%=i%>)"<%=masterReadonly%> >
                            <%if(!isEnd){%>
                            <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProvideSingleSelect('form1','srcVar=dwtxid_<%=i%>&srcVar=dwdm_<%=i%>&srcVar=dwmc_<%=i%>','fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc')">
                            <%}%>

                          <td class="td" nowrap align="right">
                              <input type="text" <%=detailClass_r%> value='<%=PbqkmxRow.get("bpsl")%>' id="bpsl_<%=i%>" name="bpsl_<%=i%>"  onchange="sl_onchange(<%=i%>, '0', false)"  style="width:100%;"<%=masterReadonly%> >
                            </td>
                           </td>
                          <td class="td" nowrap><input type="text" class=ednone style="width:100%" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly>
                          </td>
                          <td class="td" nowrap align="right">
                              <input type="text" <%=detailClass_r%>  value='<%=PbqkmxRow.get("dj")%>' id="dj_<%=i%>" name="dj_<%=i%>"   onchange="sl_onchange(<%=i%>, '0',false)" style="width:100%;"<%=masterReadonly%>>
                            </td>
                          <td class="td" nowrap align="right">
                              <input type="text" <%=detailClass_r%> value='<%=PbqkmxRow.get("je")%>' id="je0_<%=i%>" name="je0_<%=i%>"  onchange="je_onchange(<%=i%>, '0',false)" style="width:100%;" <%=masterReadonly%>>
                            </td>
                          <td class="td" nowrap> <input type="text" <%=Class%> style="width:65" onKeyDown="return getNextElement();" name="grrq_<%=i%>" id="grrq_<%=i%>"value='<%=PbqkmxRow.get("grrq")%>' maxlength='10'<%=readonly%> onchange="checkDate(this)">
                            <%if(!isEnd && isCanRework){%>
                            <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.grrq_<%=i%>);"<%=masterReadonly%>></a>
                            <%}%>
                          </td>
                          <td class="td" nowrap align="right">
                          <input type="text" <%=detailClass_r%> value='<%=PbqkmxRow.get("grsl")%>' id="grsl_<%=i%>" name="grsl_<%=i%>"  onchange="sl_onchange(<%=i%>, '0',false)" style="width:100%;"<%=masterReadonly%>>
                          </td>
                          <td noWrap class="td">
                            <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+balanceBean.getLookupName(PbqkmxRow.get("jsfsid"))+"' style='width:90' class='edline' readonly>");
                           else {%>
                             <pc:select name="<%=bjsfsid%>" addNull="1" style="width:90"> <%=balanceBean.getList(PbqkmxRow.get("jsfsid"))%>
                           </pc:select>
                            <%}%>



                    <td noWrap class="td">
                    <%String khlxd=PbqkmxRow.get("khlx");%>
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+PbqkmxRow.get("khlx")+"' style='width:50' class='edline' readonly>");
                    else {%>
                    <pc:select name="<%=khlxb%>" style="width:50" value='<%=khlxd%>'>
                      <pc:option value=''></pc:option>
                    <pc:option value='A'>A</pc:option>
                    <pc:option value='C'>C</pc:option>
                        </pc:select>
                    <%}%>
                        </tr>
                        <%
                          }
                          for(; i < 4; i++){
                       %>
                        <tr class="td" >
                          <td nowrap>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                        </tr>
                        <%}%>



                        <tr class="td" >
                          <td nowrap align="center"><b>合计</b></td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td align="right" class="td"><input id="t_bpsl" name="t_bpsl" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_bpsl.toString(),loginBean.getQtyFormat())%>' readonly></td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td align="right" class="td"><input id="t_cje" name="t_cje" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_cje.toString(),loginBean.getQtyFormat())%>' readonly></td>
                          <td>&nbsp;</td>
                          <td align="right" class="td"><input id="t_grsl" name="t_grsl" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_grsl.toString(),loginBean.getQtyFormat())%>' readonly></td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                        </tr>
                   </table>
                      <script language="javascript">initDefaultTableRow('tableview1',1);
                      <%=B_ClothOutProcessBean.adjustInputSize(new String[]{"cpbm","product","dwdm","dwmc"}, "form1", PbqkmxRows.length)%>
                         function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
                         function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
                         function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
              </script>
                    </div>




                    <div id="cntDivINFO_EX_1" class="tabContent" style="display:none;width:750;height=<%=width2%>;overflow-y:auto;overflow-x:auto;">
              <table id="tableview2" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
                      <center>
                          <tr class="tableTitle">
                            <td  width=45 align="center" nowrap>
                              <%if(!isEnd){%>
                              <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=B_ClothOutProcessBean.WJGQK_ADD%>,-1);" src="../images/add_big.gif" border="0">
                              <%}%>
                            </td>
                            <td height='20' nowrap>外加工<br>
                              工序名称</td>
                            <td height='20' nowrap>加工单位</td>
                            <td height='20' nowrap>投入数量</td>
                            <td height='20' nowrap>产出数量</td>
                           <td height='20'   nowrap>计量单位</td>
                            <td height='20' nowrap>加工单价</td>
                            <td height='20' nowrap>金额</td>
                            <td height='20' nowrap>溢损数量 <br>
                              溢(+)损(-)</td>
                            <td height='20' nowrap>溢损率%<br>
                              溢(+)损(-)</td>
                            <td height='20' nowrap>次品数量</td>
                            <td height='20' nowrap>结算方式</td>
                            <td height='20' nowrap>客户类型</td>
                          </tr>
                    <%
                       BigDecimal t_bje = new BigDecimal(0),t_trsl = new BigDecimal(0),t_ccsl = new BigDecimal(0),t_yssl = new BigDecimal(0),t_cpsl = new BigDecimal(0);

                        RowMap WjgqkRow = null;
                        int j=0;
                        for(;j<WjgqkRows.length;j++)
                        {
                        corpBean.regData(WjgqkTable,"dwtxId");
                         WjgqkRow = WjgqkRows[j];
                         String s_bje = WjgqkRow.get("jgje");
                         String s_trsl = WjgqkRow.get("trsl");
                         String s_ccsl = WjgqkRow.get("ccsl");
                         String s_yssl = WjgqkRow.get("yssl");
                         String s_cpsl = WjgqkRow.get("cpsl");
                         BigDecimal jgje = s_bje.length() > 0 ? (new BigDecimal(s_bje)) : new BigDecimal(0);
                         BigDecimal trsl = s_trsl.length() > 0 ? (new BigDecimal(s_trsl)) : new BigDecimal(0);
                         BigDecimal ccsl = s_ccsl.length() > 0 ? (new BigDecimal(s_ccsl)) : new BigDecimal(0);
                         BigDecimal yssl = s_yssl.length() > 0 ? (new BigDecimal(s_yssl)) : new BigDecimal(0);
                         BigDecimal cpsl = s_cpsl.length() > 0 ? (new BigDecimal(s_cpsl)) : new BigDecimal(0);
                         RowMap dwpRow =corpBean.getLookupRow(WjgqkRow.get("dwtxId"));
                         boolean isline = !isCanRework || !isHasDeptLimit;
                        String Class = isline ? "class=ednone" : detailClass;//从表Class模式
                        t_bje = t_bje.add(jgje);
                        t_trsl = t_trsl.add(trsl);
                        t_ccsl = t_ccsl.add(ccsl);
                        t_yssl = t_yssl.add(yssl);
                        t_cpsl = t_cpsl.add(cpsl);
                        String jsfsid = "jsfsid_"+j;
                        String khlxa = "khlxa_"+j;
                        %>
                          <tr class="td" >
                        <td nowrap>
                        <%if(!isEnd && isCanRework && isHasDeptLimit){%>
                            <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=B_ClothOutProcessBean.WJGQK_DEL%>,<%=j%>)" src="../images/delete.gif" border="0">
                        <%}%>
                            </td>
                            <td>
                                <input type="text" <%=Class%> value='<%=WjgqkRow.get("wjggx")%>' id="wjggx_<%=j%>" name="wjggx_<%=j%>"  style="width:100%;" <%=masterReadonly%>>
                              </td>
                           <%RowMap bdwpRow =corpBean.getLookupRow(WjgqkRow.get("dwtxid"));
                            String aa =WjgqkRow.get("dwtxid");
                           %>
                            <td noWrap class="td" >
                            <input type="hidden" name="bdwtxid_<%=j%>" value='<%=WjgqkRow.get("dwtxid")%>'>
                            <input type="text" <%=Class%>   id='bdwdm_<%=j%>' name='bdwdm_<%=j%>' value='<%=bdwpRow.get("dwdm")%>' onKeyDown="return getNextElement();" onchange="bcorpCodeSelect(this,<%=j%>)" <%=masterReadonly%>>
                            <input type="text" <%=Class%>  name='bdwmc_<%=j%>' name='bdwmc_<%=j%>'  value='<%=bdwpRow.get("dwmc")%>' style="width:100"  onKeyDown="return getNextElement();" onchange="bcorpNameSelect(this,<%=j%>)"<%=masterReadonly%> >
                            <%if(!isEnd){%>
                            <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProcessSingleSelect('form1','srcVar=bdwtxid_<%=j%>&srcVar=bdwdm_<%=j%>&srcVar=bdwmc_<%=j%>','fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc')">
                            <%}%>
                            </td>

                             <td class="td" nowrap align="right">
                                <input type="text" <%=detailClass_r%> value='<%=WjgqkRow.get("trsl")%>' id="trsl_<%=j%>" name="trsl_<%=j%>"  onchange="sl_onchange(<%=j%>, '1', false)" style="width:100%;"<%=masterReadonly%> >
                              </td>
                            <td class="td" nowrap align="right">
                                <input type="text" <%=detailClass_r%> value='<%=WjgqkRow.get("ccsl")%>' id="ccsl_<%=j%>" name="ccsl_<%=j%>"   onchange="sl_onchange(<%=j%>, '1', false)" style="width:100%;"<%=masterReadonly%> >
                             </td>
                             <td>
                                <input type="text" <%=Class%> value='<%=WjgqkRow.get("jldw")%>' id="jldw5_<%=j%>" name="jldw5_<%=j%>" style="width:100%;" <%=masterReadonly%> >
                             </td>
                            <td class="td" nowrap align="right">
                                <input type="text" <%=detailClass_r%> value='<%=WjgqkRow.get("jgdj")%>' id="jgdj_<%=j%>" name="jgdj_<%=j%>" onchange="sl_onchange(<%=j%>, '1',false)"  <%=masterReadonly%>>
                              </td>
                            <td class="td" nowrap align="right">
                                <input type="text" <%=detailClass_r%> value='<%=WjgqkRow.get("jgje")%>' id="jgje_<%=j%>" name="jgje_<%=j%>"   onchange="je_onchange(<%=j%>, '1',false)"  <%=masterReadonly%>>
                              </td>
                            <td class="td" nowrap align="right">
                                <input type="text" class=ednone_r value='<%=WjgqkRow.get("yssl")%>' id="yssl_<%=j%>" name="yssl_<%=j%>"  style="width:100%;" readonly >
                              </td>
                            <td class="td" nowrap align="right">
                                <input type="text" class=ednone_r value='<%=WjgqkRow.get("ysl")%>' id="ysl_<%=j%>" name="ysl_<%=j%>"  style="width:100%;" readonly>
                              </td>
                            <td class="td" nowrap align="right">
                                <input type="text" <%=detailClass_r%> value='<%=WjgqkRow.get("cpsl")%>' id="cpsl_<%=j%>" name="cpsl_<%=j%>"  onchange="sl_onchange(<%=j%>, '1', false)" style="width:100%;" <%=masterReadonly%>>
                             </td>
                              <td noWrap class="td">
                            <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+balanceBean.getLookupName(WjgqkRow.get("jsfsid"))+"' style='width:90' class='edline' readonly>");
                           else {%>
                             <pc:select name="<%=jsfsid%>" addNull="1" style="width:90"> <%=balanceBean.getList(WjgqkRow.get("jsfsid"))%>
                           </pc:select>
                            <%}%>
                    <td noWrap class="td">
                    <%String khlxc=WjgqkRow.get("khlx");%>
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+WjgqkRow.get("khlx")+"' style='width:50' class='edline' readonly>");
                    else {%>
                    <pc:select name="<%=khlxa%>" style="width:50" value='<%=khlxc%>'>
                      <pc:option value=''></pc:option>
                    <pc:option value='A'>A</pc:option>
                    <pc:option value='C'>C</pc:option>
                        </pc:select>
                    <%}%>

                   </tr>
                          <%
                          }
                          for(; j < 4; j++){
                       %>
                          <tr class="td" >
                            <td nowrap>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                          </tr>
                          <%}%>



                          <tr class="td" >
                          <td nowrap align="center"><b>合计</b></td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td align="right" class="td"><input id="t_trsl" name="t_trsl" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_trsl.toString(),loginBean.getQtyFormat())%>' readonly></td>
                          <td align="right" class="td"><input id="t_ccsl" name="t_ccsl" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_ccsl.toString(),loginBean.getQtyFormat())%>' readonly></td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td align="right" class="td"><input id="t_bje" name="t_bje" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_bje.toString(),loginBean.getQtyFormat())%>' readonly></td>
                          <td align="right" class="td"><input id="t_yssl" name="t_yssl" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_yssl.toString(),loginBean.getQtyFormat())%>' readonly></td>
                          <td>&nbsp;</td>
                          <td align="right" class="td"><input id="t_cpsl" name="t_cpsl" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_cpsl.toString(),loginBean.getQtyFormat())%>' readonly></td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                        </tr>


                     </center>
                        </table>
                        <script language="javascript">initDefaultTableRow('tableview2',1);
                        <%=B_ClothOutProcessBean.adjustInputSize(new String[]{"bdwdm","bdwmc","jgje","jgdj"}, "form1", WjgqkRows.length)%>
                         function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
                         function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
                         function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
                        </script>

                    </div>

                    <div id="cntDivINFO_EX_2" class="tabContent" style="display:none;width:750;height=<%=width3%>;overflow-y:auto;overflow-x:auto;">
                      <center>
                        <table id="tableview3" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
                          <tr class="tableTitle">
                            <td nowrap>
                              <%if(!isEnd){%>
                              <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=B_ClothOutProcessBean.WJGCP_ADD%>,-1);" src="../images/add_big.gif" border="0">
                              <%}%>
                            </td>
                            <td height='20' nowrap>材料编号</td>
                            <td height='20' nowrap>名称规格</td>
                            <td height='20' nowrap>规格属性</td>
                            <td height='20' nowrap>数量</td>
                            <td height='20' nowrap>单位</td>
                            <td height='20' nowrap>换算数量</td>
                            <td height='20' nowrap>换算单位</td>
                            <td  height='20' nowrap>加工完单价</td>
                            <td height='20' nowrap>金额</td>
                             <td height='20' nowrap>库位</td>
                          </tr>
                          <%
                          prodBean.regData(WjgcpTable,"cpid");
                           propertyBean.regData(WjgcpTable,"dmsxid");

                            RowMap WjgcpRow = null;
                             BigDecimal t_sl = new BigDecimal(0),t_je = new BigDecimal(0),t_hssl = new BigDecimal(0);
                            int k=0;
                            for(;k<WjgcpRows.length;k++)
                            {

                              WjgcpRow = WjgcpRows[k];
                              String s_sl = WjgcpRow.get("sl");
                              String s_je = WjgcpRow.get("je");
                              String s_hssl = WjgcpRow.get("hssl");


                              BigDecimal sl = s_sl.length() > 0 ? (new BigDecimal(s_sl)) : new BigDecimal(0);
                               BigDecimal je = s_je.length() > 0 ? (new BigDecimal(s_je)) : new BigDecimal(0);
                                BigDecimal hssl = s_hssl.length() > 0 ? (new BigDecimal(s_hssl)) : new BigDecimal(0);
                               RowMap  prodRow= prodBean.getLookupRow(WjgcpRow.get("cpid"));
                               boolean isline = !isCanRework || !isHasDeptLimit;
                               String Class = isline ? "class=ednone" : detailClass;//从表Class模式
                               String kwName = "kwid_"+k;
                               String hsbl = prodRow.get("hsbl");
                               //String s_hssl = WjgcpRow.get("hssl");
                               //BigDecimal hssl = s_hssl.length() > 0 ? (new BigDecimal(s_hssl)) : new BigDecimal(0);

                               t_sl = t_sl.add(sl);
                               t_je = t_je.add(je);
                               t_hssl = t_hssl.add(hssl);
                        %>
                          <tr class="td" > <td nowrap>
                        <iframe id="prod_<%=k%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                        <%if(!isEnd && isCanRework && isHasDeptLimit){%>
                         <img style='cursor:hand' title='单选物资' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=bcpid_<%=k%>&srcVar=cpbm1_<%=k%>&srcVar=product1_<%=k%>&srcVar=jldw1_<%=k%>&srcVar=hsdw_<%=k%>&srcVar=hsbl_<%=k%>&srcVar=isprops_<%=k%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops')">
                            <%}if(!isEnd && isHasDeptLimit){%>
                            <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=B_ClothOutProcessBean.WJGCP_DEL%>,<%=k%>)" src="../images/delete.gif" border="0">
                          <%}%>
                          </td>
                            <td class="td" nowrap>
                            <input type="hidden" name="bcpid_<%=k%>" value='<%=WjgcpRow.get("cpid")%>'>
                            <input type='hidden' id='hsbl_<%=k%>' name='hsbl_<%=k%>' value='<%=prodRow.get("hsbl")%>'>
                            <input type='hidden' id='truebl_<%=k%>' name='truebl_<%=k%>' value=''>
                            <input type='hidden' id='isprops_<%=k%>' name='isprops_<%=k%>' value='<%=prodRow.get("isprops")%>'>
                            <input type="text" <%=Class%> onKeyDown="return getNextElement();" id="cpbm1_<%=k%>" name="cpbm1_<%=k%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect1(this,<%=k%>)" >
                          </td>
                          <td class="td" nowrap><input type="text" <%=Class%> onKeyDown="return getNextElement();" id="product1_<%=k%>" name="product1_<%=k%>" value='<%=prodRow.get("product")%>' onchange="bproductNameSelect(this,<%=k%>)" style="width:100%;" <%=masterReadonly%>>
                          </td>
                          <td class="td" nowrap>
                          <input <%=Class%>  name="sxz_<%=k%>" value='<%=propertyBean.getLookupName(WjgcpRow.get("dmsxid"))%>' onchange="if(form1.bcpid_<%=k%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.bcpid_<%=k%>.value,<%=k%>)" onKeyDown="return getNextElement();"<%=masterReadonly%> >
                          <input type="hidden" id="dmsxid_<%=k%>" name="dmsxid_<%=k%>" value="<%=WjgcpRow.get("dmsxid")%>">
                         <%String aa=prodRow.get("isprops");%>
                            <%if(!isEnd && isCanRework && isHasDeptLimit){%>
                          <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.bcpid_<%=k%>.value==''){alert('请先输入产品');return;}if('<%=prodRow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=k%>','sxz_<%=k%>',form1.bcpid_<%=k%>.value)">
                           <%}if(!isEnd && isHasDeptLimit){%>
                          <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=k%>.value='';sxz_<%=k%>.value='';">
                            <%}%>
                                <td class="td" nowrap align="right">
                                <input type="text" <%=detailClass_r%> value='<%=WjgcpRow.get("sl")%>' id="sl_<%=k%>" name="sl_<%=k%>"  onchange="slk_onchange(<%=k%>,false)"  <%=masterReadonly%> >
                             </td>
                          </td>
                          <td class="td" nowrap><input type="text" class=ednone style="width:100%" onKeyDown="return getNextElement();" id="jldw1_<%=k%>" name="jldw1_<%=k%>" value='<%=prodRow.get("jldw")%>' readonly>
                          </td>
                      <%--    <td class="td" nowrap><input type="text" <%=isHsbj ? slClass_r : "class=ednone_r"%> onKeyDown="return getNextElement();" id="hssl_<%=k%>" name="hssl_<%=k%>" value='<%=hssl%>' maxlength='<%=WjgcpTable.getColumn("hssl").getPrecision()%>' onchange="sl_onchange(<%=k%>, false)" style="width:100%;" <%=isHsbj ? slReadonly : "readonly"%>>--%>
                      <td class="td" nowrap>
                                <input type="text" <%=detailClass_r%> value='<%=WjgcpRow.get("hssl")%>' id="hssl_<%=k%>" name="hssl_<%=k%>"   onchange="hssl_onchange(<%=k%>,false)" <%=masterReadonly%>></td>
                             </td>
                            <td class="td" nowrap><input type="text" class=ednone style="width:100%" onKeyDown="return getNextElement();" id="hsdw_<%=k%>" name="hsdw_<%=k%>" value='<%=prodRow.get("hsdw")%>' readonly>
                             </td>
                              <td class="td" nowrap>
                        <%String as=WjgcpRow.get("jgwdj");%>
                                <input type="text" <%=detailClass_r%> value='<%=WjgcpRow.get("jgwdj")%>' id="jgwdj_<%=k%>" name="jgwdj_<%=k%>" onchange="djk_onchange(<%=k%>,false)"  <%=masterReadonly%> >
                              </td>
                              <td class="td" nowrap>
                        <%String ad=WjgcpRow.get("je");%>
                                <input type="text" <%=detailClass_r%> value='<%=WjgcpRow.get("je")%>' id="je2_<%=k%>" name="je2_<%=k%>" onchange="jek_onchange(<%=k%>,false)" <%=masterReadonly%>>
                              </td>

                    <td nowrap class="td">
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+storeAreaBean.getLookupName(WjgcpRow.get("kwid"))+"' style='width:100' class='edline' readonly>");
                    else {%>
                    <pc:select  name="<%=kwName%>" addNull="1" style="width:111"  onSelect="deptchange();"  >
                    <%=storeAreaBean.getList(WjgcpRow.get("kwid"))%> </pc:select>
                    <%}%>
                  </td>
                          </tr>
                          <%
                          }
                          for(; k < 4; k++){
                       %>
                          <tr class="td" >
                            <td nowrap>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                          </tr>
                          <%}%>
                          <tr class="td" >
                            <td nowrap align="center"><b>合计</b></td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_sl.toString(),loginBean.getQtyFormat())%>' readonly></td>
                            <td>&nbsp;</td>
                            <td align="right" class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_hssl.toString(),loginBean.getQtyFormat())%>' readonly></td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td align="right" class="td"><input id="t_je" name="t_je" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_je.toString(),loginBean.getQtyFormat())%>' readonly></td>
                        <td>&nbsp;</td>
                        </tr>
                        </table>
                        <script language="javascript">initDefaultTableRow('tableview3',1);
                         <%=B_ClothOutProcessBean.adjustInputSize(new String[]{"cpbm1","product1","je2","jgwdj","sl","hssl"}, "form1", WjgcpRows.length)%>
                         function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
                         function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
                         function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}

                      </script>
                      </center>
                    </div>
                  <tr class="td" >
                  <td nowrap>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                </tr>
              </table>
              <script language="javascript">initDefaultTableRow('tableview3',1); </script>
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
  <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
    <tr>
            <td class="td"><b>登记日期:</b><%=masterRow.get("czrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("czy")%></td>
          </tr>
    <tr>
      <td colspan="3" noWrap class="tableTitle"><%if(!isEnd ){%> <input name="button2" type="button" class="button" onClick="sumitForm(19);" value='保存添加(N)'><%}%>
        <script language='javascript'>GetShortcutControl(78,"sumitForm(19,-1)");</script>
       <%if(!isEnd ){%> <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value='保存返回(S)'>
       <%}%>
        <script language='javascript'>GetShortcutControl(83,"sumitForm(14,-1)");</script>
       <%if(!isEnd ){%> <input name="btnback2" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>)" value='删除(X)'><%}%>
        <script language='javascript'>GetShortcutControl(88,"if(confirm('是否删除该记录？'))sumitForm(1067,null)");</script>
        <%if(!B_ClothOutProcessBean.isRep){%><input name="btnback" type="button" class="button" onClick="backList();" value='  返回(C)  '><%}%>
        <script language='javascript'>GetShortcutControl(67,"backList();");</script>
      </td>
    </tr>
  </table>
</form>
<%if(B_ClothOutProcessBean.isApprove && !B_ClothOutProcessBean.isReport){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>

</body>
</html>