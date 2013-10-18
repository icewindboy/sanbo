<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate, java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.store.B_StoreProess b_StoreProessBean = engine.erp.store.B_StoreProess.getInstance(request);
  String pageCode = "self_gain_list";
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
   location.href='self_gain_list.jsp';
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i+'&srcVar=hsbl_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops&fieldVar=hsbl', obj.value,'product_change('+i+')');
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i+'&srcVar=hsbl_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops&fieldVar=hsbl', obj.value,'product_change('+i+')');
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                     'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'propertyChange('+i+')');
}
function deptchange()
{
 associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
function technicschange(i)
{
 associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE%>', 'gx_'+i, 'gylxid', eval('form1.gylxid_'+i+'.value'), '');
}
</script>
<%String retu = b_StoreProessBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String SC_STORE_UNIT_STYLE = b_StoreProessBean.SC_STORE_UNIT_STYLE;//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  String SYS_PRODUCT_SPEC_PROP =b_StoreProessBean.SYS_PRODUCT_SPEC_PROP;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  String SC_PRODUCE_UNIT_STYLE = b_StoreProessBean.SC_PRODUCE_UNIT_STYLE;//是否强制换算
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS_GOODS);//通过加工单明细id得到加工单号
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);

  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_StoreProessBean.getMaterTable();
  EngineDataSet list = b_StoreProessBean.getDetailTable();
  HtmlTableProducer masterProducer = b_StoreProessBean.masterProducer;
  HtmlTableProducer detailProducer = b_StoreProessBean.detailProducer;
  RowMap masterRow = b_StoreProessBean.getMasterRowinfo();
  RowMap[] detailRows= b_StoreProessBean.getDetailRowinfos();
  String state = masterRow.get("state");
  boolean isEdit = !b_StoreProessBean.isReport && state.equals("0") && (b_StoreProessBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));//在修改状态,并有修改权限
  boolean isCanDelete = state.equals("0") && !b_StoreProessBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  boolean isEnd = b_StoreProessBean.isReport || b_StoreProessBean.isApprove || (!b_StoreProessBean.masterIsAdd() && !state.equals("0"));//表示已经审核或已完成
  isEnd = isEnd || !(b_StoreProessBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = !isEdit ? "class=edline" : "class=edbox";
  String detailClass = !isEdit ? "class=ednone" : "class=edFocused";
  String detailClass_r = !isEdit ? "class=ednone_r" : "class=edFocused_r";
  String readonly = !isEdit ? " readonly" : "";
  //boolean isAdd = b_StoreProessBean.isDetailAdd;
  ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("2"); opkey.add("3");
  ArrayList opval = new ArrayList(); opval.add("拉丝"); opval.add("修理"); opval.add("织袋");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};
  storeAreaBean.regConditionData(ds, "storeid");
  storeBean.regData(ds, "storeid");
  String handleperson = masterRow.get("handleperson");
  personBean.regConditionData(ds, "deptid");
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">自制收获单</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("receivecode").getFieldname()%></td>
                  <td noWrap class="td">
                  <input type="text" class="edline" name="receiveCode" value='<%=masterRow.get("receiveCode")%>' maxlength='<%=ds.getColumn("receiveCode").getPrecision()%>' style="width:110"  onKeyDown="return getNextElement();" readonly>
                  </td>
                  <td noWrap class="tdTitle">收货日期</td>
                  <td noWrap class="td"><input type="text" name="receiveDate" value='<%=masterRow.get("receiveDate")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.receiveDate);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+workShopBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select  name="deptid" addNull="1" style="width:110" onSelect="deptChange()">
                      <%=workShopBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                   </td>
                  <td noWrap class="tdTitle">经手人</td>
                  <td  noWrap class="td">
                  <%if(isEnd){%><input type="text" name="handleperson" value='<%=masterRow.get("handleperson")%>' maxlength='<%=ds.getColumn("handleperson").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                  <%}else {%>
                  <pc:select combox="1" className="edFocused" name="handleperson" value="<%=handleperson%>" style="width:110">
                  <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%></pc:select>
                  <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%String sumit = "if(form1.storeid.value!='"+masterRow.get("storeid")+"'){sumitForm("+b_StoreProessBean.STORE_ONCHANGE+");}";%>
                    <%if(isEnd) out.print("<input type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="storeid" addNull="1" style="width:110" onSelect="<%=sumit%>">
                      <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
                    <%}%>
                  </td>
                 </tr>
                <%/*打印用户自定义信息*/
                int j=0;
                while(j < mBakFields.length){
                  out.print("<tr>");
                  for(int k=0; k<4; k++)
                  {
                    out.print("<td noWrap class='tdTitle'>");
                    out.print(j < mBakFields.length ? mBakFields[j].getFieldname() : "&nbsp;");
                    out.print("</td><td noWrap class='td'");
                    if(j < mBakFields.length)
                    {
                      boolean isMemo = mBakFields[j].getType() == FieldInfo.MEMO_TYPE;
                      out.print(isMemo ? " colspan=7>" : ">");
                      String filedcode = mBakFields[j].getFieldcode();
                      String style = (isMemo ? "style='width:690'" : "style='width:110'")+ " onKeyDown='return getNextElement();'";
                      out.print(masterProducer.getFieldInput(mBakFields[j], masterRow.get(filedcode), filedcode, style, isEdit, true));
                      out.print("</td>");
                      if(isMemo)
                        break;
                    }
                    else
                      out.print(">&nbsp;</td>");
                    j++;
                  }
                  out.println("</tr>");
                }
                int width = (detailRows.length > 4 ? detailRows.length : 4)*23 + 66;
                String count = String.valueOf(list.getRowCount());
                int iPage = 30;
                String pageSize = String.valueOf(iPage);
                %>
                 <tr>
                   <td colspan="8" noWrap class="td">
                   <pc:navigator id="self_gain_listNav" recordCount="<%=count%>" pageSize="<%=pageSize%>" form="form1" operate='<%="operate=sumitForm("+b_StoreProessBean.TURNPAGE+")"%>' disable='<%=b_StoreProessBean.isRepeat.equals("1")?"1":"0"%>'/>
                   </td>
                </tr>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                         <%--  <%if(isEdit){%>
                          <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/><%}%>--%>
                        </td>
                        <td height='20' nowrap>员工姓名</td>
                        <td height='20' nowrap>机台号</td>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名</td>
                        <td nowrap> 规格</td>
                        <td nowrap>实际规格</td>
                        <td nowrap>规格属性</td>
                        <td nowrap>数量</td>
                        <td nowrap>计量单位</td>
                        <td nowrap>生产数量</td>
                        <td  nowrap>生产单位</td>
                        <td nowrap>转数(转)</td>
                        <td  nowrap>加工要求</td>
                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%
                      prodBean.regData(list,"cpid");
                      BigDecimal t_sl = new BigDecimal(0), t_jjgs = new BigDecimal(0), t_jjgz = new BigDecimal(0), t_hssl = new BigDecimal(0);
                      BigDecimal t_scsl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String sl = detail.get("sl");
                        if(b_StoreProessBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String scsl = detail.get("scsl");
                        if(b_StoreProessBean.isDouble(scsl))
                          t_scsl = t_scsl.add(new BigDecimal(scsl));
                        String hssl = detail.get("hssl");
                        if(b_StoreProessBean.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));
                        String jjgs = detail.get("jjgs");
                        if(b_StoreProessBean.isDouble(jjgs))
                          t_jjgs = t_jjgs.add(new BigDecimal(jjgs));
                        String jjgz = detail.get("jjgz");
                        if(b_StoreProessBean.isDouble(jjgz))
                          t_jjgz = t_jjgz.add(new BigDecimal(jjgz));
                        String dmsxid = detail.get("dmsxid");
                        String sx = propertyBean.getLookupName(dmsxid);

                        personBean.regData(list,"personid");
                        RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                        String hsbl = prodRow.get("hsbl");
                        detail.put("hsbl",hsbl);
                    %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                        <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                        <%if(isEdit){%>
                         <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                        <td class="td" nowrap><%=personBean.getLookupName(detail.get("personid"))%></td>
                        <td class="td" nowrap><input type="text" class="ednone_r" style="width:65" onKeyDown="return getNextElement();" id="jth_<%=i%>" name="jth_<%=i%>" value='<%=detail.get("jth")%>'  readonly></td>

                        <td class="td" nowrap>
                        <%=prodRow.get("cpbm")%>
                        <input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        </td>
                        <td class="td" nowrap><%=prodRow.get("pm")%></td>
                        <td class="td" nowrap><%=prodRow.get("gg")%></td>
                        <td class="td" nowrap><input type="text" class="ednone_r" style="width:65" onKeyDown="return getNextElement();" id="sjgg_<%=i%>" name="sjgg_<%=i%>" value='<%=detail.get("sjgg")%>'  readonly></td>
                        <td class="td" nowrap>
                        <input class="ednone"  name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>'  onKeyDown="return getNextElement();" readonly >
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        </td>
                        <td class="td" nowrap><input type="text" class="ednone_r" style="width:65" onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>'  readonly></td>
                        <td class="td" nowrap><input type="text" class="ednone"   style="width:65" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" class="ednone_r" style="width:65" onKeyDown="return getNextElement();" id="scsl_<%=i%>" name="scsl_<%=i%>" value='<%=detail.get("scsl")%>'   readonly></td>
                        <td class="td" nowrap><input type="text" class="ednone"   style="width:65" onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="zs_<%=i%>"   name="zs_<%=i%>"   value='<%=detail.get("zs")%>'    <%=readonly%>></td>
                       <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="jgyq_<%=i%>" name="jgyq_<%=i%>" value='<%=detail.get("jgyq")%>'   <%=readonly%>></td>
                        <%FieldInfo[] bakFields = detailProducer.getBakFieldCodes();
                        for(int k=0; k<bakFields.length; k++)
                        {
                          String fieldCode = bakFields[k].getFieldcode();
                          out.print("<td class='td' nowrap>");
                          out.print(detailProducer.getFieldInput(bakFields[k], detail.get(fieldCode), fieldCode+"_"+k, "style='width:65'", isEdit, true));
                          out.println("</td>");
                        }
                        %>
                      </tr>
                      <%list.next();
                      }
                      for(; i < 4; i++){
                  %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>合计</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_scsl" name="t_scsl" type="text" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly></td>
                        <td align="right" class="td"></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                    </table>
                    </div>
                   </td>
                </tr>
                <tr>
                  <td  noWrap class="tdTitle">说明</td><%--其他信息--%>
                  <td colspan="7" noWrap class="td"><textarea name="memo" rows="3" onKeyDown="return getNextElement();" style="width:690"<%=readonly%>><%=masterRow.get("memo")%></textarea></td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">
          <tr>
            <td class="td"><b>登记日期:</b><%=masterRow.get("zdrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("zdr")%></td>
          </tr>
          <tr>
         <tr>
            <td colspan="3" noWrap class="tableTitle">
              <input type="hidden" name="selectedrwdid" value="">
              <input name="btnback" class="button" type="button" value="引入任务单(W)" style="width:115" onClick="selctRwd();">
              <pc:shortcut key="w" script="selctRwd();"/>
             <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
              <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
              <input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value=" 删除(D)">
             <pc:shortcut key="d" script='delMaster();'/>
              <input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
              <pc:shortcut key="c" script='backList();'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);

 function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
  function delMaster(){
    if(confirm('是否删除该记录？'))
      sumitForm(<%= Operate.DEL%>,-1);
  }
  function importProcess(){
    WorkloadSelectProcess('form1','srcVar=singleImportProcess','fieldVar=jgdid',form1.deptid.value,'sumitForm(<%=b_StoreProessBean.SINGLE_SEL_PROCESS%>)');
  }
  function product_change(i){
    document.all['dmsxid_'+i].value="";
    document.all['sxz_'+i].value="";
    document.all['widths_'+i].value="";
    associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_ROUTE%>', 'gylxid_'+i, 'cpid', eval('form1.cpid_'+i+'.value'), '',true);
  }
  function processDetail()
  {
    if(form1.deptid.value=='')
    {
      alert('请选择车间');
      return;
    }
    ProcessGoodsSelect('form1','srcVar=mutiprocess&deptid='+form1.deptid.value);
  }
  function propertyChange(i){
   var sxzObj = document.all['sxz_'+i];
   var scdwgsObj = document.all['scdwgs_'+i];
   if(sxzObj.value=='')
     return;
   var widthObj = document.all['widths_'+i];
   widthValue = parseString(sxzObj.value, '<%=SYS_PRODUCT_SPEC_PROP%>(', ')', '(');
   if(widthValue=='')
     return;
   widthObj.value =  widthValue;
   var slObj = document.all['sl_'+i];
   var hsslObj = document.all['hssl_'+i];
   var scslObj = document.all['scsl_'+i];
   var hsblObj = document.all['hsbl_'+i];
   if(slObj.value=='' && scslObj.value=='')
     return;
   if(slObj.value!='')
     scslObj.value = formatQty(parseFloat(slObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthValue));
   else if(slObj.value=='' && scslObj.value!=''){
     slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthValue)/parseFloat(scdwgsObj.value));
     if(hsblObj=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
        hsslObj.value = slobj.value;
     else
        hsslObj.value = formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
   }
   sl_onchange(i,false);
 }
 function big_change(){
  if(<%=detailRows.length%><1)
    return;
  for(t=0; t<<%=detailRows.length%>; t++){
    sl_onchange(t,false);
  }
}
 function sl_onchange(i, isBigUnit)
     {
       var oldhsblObj = document.all['hsbl_'+i];
       var sxzObj = document.all['sxz_'+i];
       unitConvert(document.all['prod_'+i], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'nsl_onchange('+i+','+isBigUnit+')');
     }
    function nsl_onchange(i, isBigUnit)
    {
      var slObj = document.all['sl_'+i];
      var hsslObj = document.all['hssl_'+i];
      var scslObj = document.all['scsl_'+i];
      var sxzObj = document.all['sxz_'+i];
      var scdwgsObj = document.all['scdwgs_'+i];
      var widthObj = document.all['widths_'+i];//规格属性的宽度
      var hsblObj = document.all['truebl_'+i];
      //var deObj = document.all['de_'+i];
      //var deslObj = document.all['desl_'+i];
      var jjgsObj = document.all['jjgs_'+i];
      var jjgzObj = document.all['jjgz_'+i];

      var obj = isBigUnit ? hsslObj : slObj;
      var showText = isBigUnit ? "输入的换算数量非法" : "输入的数量非法";
      var changeObj = isBigUnit ? slObj : hsslObj;
      if(obj.value=="")
        return;
      if(isNaN(obj.value)){
        alert(showText);
        obj.focus();
        return;
    }
    if(isBigUnit){
      if(changeObj.value=="" || '<%=SC_STORE_UNIT_STYLE%>'=='1'){//是否强制转换
        if(hsblObj.value!="" && !isNaN(hsblObj.value))
          changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
        //if(slObj.value!="" && !isNaN(slObj.value) )
        //  jjgsObj.value= formatSum(parseFloat(slObj.value) * jjgsgsz/ parseFloat(deslObj.value));
        //if(slObj.value !="" && !isNaN(slObj.value) && deObj.value!="")
         // jjgzObj.value= formatSum(parseFloat(slObj.value) * parseFloat(deObj.value));
      }
    }

    else{
      //if(slObj.value!="" && !isNaN(slObj.value)  )
       // jjgsObj.value= formatSum(parseFloat(slObj.value) * jjgsgsz/ parseFloat(deslObj.value));
      //if(slObj.value !="" && !isNaN(slObj.value) && deObj.value!="")
      //  jjgzObj.value= formatSum(parseFloat(slObj.value) * parseFloat(deObj.value));
      if(changeObj.value=="" || '<%=SC_STORE_UNIT_STYLE%>'=='1'){//是否强制转换
        if(hsblObj.value!="" && !isNaN(hsblObj.value))
          changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
      }
    }
    cal_tot('sl');
    cal_tot('jjgs');
    cal_tot('jjgz');
    cal_tot('hssl')
    if(scslObj.value!="" && '<%=SC_PRODUCE_UNIT_STYLE%>'!='1')
      return;
    else{
      if(widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
        scslObj.value= isBigUnit ? changeObj.value : slObj.value;
      else if(isBigUnit)
        scslObj.value = formatQty(hsblObj.value=="" ? parseFloat(hsslObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value) : parseFloat(hsslObj.value)*parseFloat(hsblObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value));
      else if(!isBigUnit)
        scslObj.value = formatQty(parseFloat(slObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value));
    }
    cal_tot('scsl');
    }
    function producesl_onchange(i)
    {
      var oldhsblObj = document.all['hsbl_'+i];
      var sxzObj = document.all['sxz_'+i];
      unitConvert(document.all['prod_'+i], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'newproducesl_onchange('+i+')');
    }
    function ProdSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
    {
      paraStr = "../pub/productselect.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&deptid="+curID;
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      openSelectUrl(paraStr, "SingleProdSelector", winopt2)
    }
    function selctRwd()
    {
      form1.selectedrwdid.value='';
      RwdSingleSelect('form1','srcVar=selectedrwdid','fieldVar=rwdid',"sumitForm(<%=Operate.DETAIL_ADD%>,-1)");
    }
    function RwdSingleSelect(frmName,srcVar,fieldVar,methodName,notin)
    {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
      var winName= "SingleladingSelector";
      paraStr = "../store/import_rwd_workload.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar;
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
    }
  </script>
<%out.print(retu);%>
</body>
</html>
