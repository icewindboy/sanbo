<%--����̵㵥��������絥�ӱ�--%>
<%
/**
 * 02.19 21:00 �������ͻ���������λ�û���һ��,����Ĵ�ӡ���ݲ���Ҳ������Ӧ�ĸĶ� yjg
 */
%>
<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.store.shengyu.Check_Make_Destroy checkMakeDestroyBean = engine.erp.store.shengyu.Check_Make_Destroy.getInstance(request);
  String pageCode = "report_destroy_list";
  //boolean hasApproveLimit = isApprove && loginBean.hasLimits(pageCode, op_approve);
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  if ( !checkDetails(oper) ) {return;}
  lockScreenToWait("������, ���Ժ�");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function backList()
{
  location.href='../store_shengyu/store_check_edit.jsp';
}
</script>
<%String retu = checkMakeDestroyBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//���ʹ������
  engine.project.LookUp storeLossBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_LOSS);
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = checkMakeDestroyBean.getMaterTable();
  EngineDataSet list = checkMakeDestroyBean.getDetailTable();
  HtmlTableProducer masterProducer = checkMakeDestroyBean.masterProducer;
  HtmlTableProducer detailProducer = checkMakeDestroyBean.detailProducer;
  RowMap masterRow = checkMakeDestroyBean.getMasterRowinfo();
  RowMap[] detailRows= checkMakeDestroyBean.getDetailRowinfos();
  String zt=masterRow.get("zt");
  boolean isEnd = !checkMakeDestroyBean.masterIsAdd() && !zt.equals("0");//��ʾ�Ѿ���˻������
  boolean isCanDelete = !isEnd && !checkMakeDestroyBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//û�н���,���޸�״̬,����ɾ��Ȩ��
  isEnd = isEnd || !(checkMakeDestroyBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//�����û����Զ����ֶ�
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  //boolean isAdd = checkMakeDestroyBean.isMasterAdd;
  String readonly = isEnd  ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("�����"/* �����:"+ds.getValue("shr")*/) : (zt.equals("9") ? "������" : "δ���");

  //2004-5-2 16:43 Ϊ����ϸ���ݼ������ҳ����
  String count = String.valueOf(list.getRowCount());
  int iPage = 30;
  String pageSize = String.valueOf(iPage);
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;">
  <table WIDTH="760" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0>
        <tr>
            <td class="activeVTab">���絥(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <%corpBean.regData(ds,"dwtxid");String jsr = masterRow.get("jsr");
                  if(!isEnd)
                    storeAreaBean.regConditionData(ds,"storeid");
                  %>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfdjdh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="sfdjdh" value='<%=masterRow.get("sfdjdh")%>' maxlength='<%=ds.getColumn("sfdjdh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfrq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="sfrq" value='<%=masterRow.get("sfrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="ѡ������" onclick="selectDate(form1.sfrq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td"><input type='text' value="<%=deptBean.getLookupName(masterRow.get("deptid"))%>" style='width:110' class='edline' readonly>
                   </td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfdjlbid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+storeLossBean.getLookupName(masterRow.get("sfdjlbid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="sfdjlbid" addNull="1" style="width:110">
                      <%=storeLossBean.getList(masterRow.get("sfdjlbid"))%> </pc:select>
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <input type='text' value="<%=storeBean.getLookupName(masterRow.get("storeid"))%>" style='width:110' class='edline' readonly>
                  </td>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jfkm").getFieldname()%></td>
                  <td  noWrap class="td"><input type="text" name="jfkm" value='<%=masterRow.get("jfkm")%>' maxlength='<%=ds.getColumn("jfkm").getPrecision()%>' style="width:110" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("bz").getFieldname()%></td>
                  <td  noWrap class="td"><input type="text" name="bz" value='<%=masterRow.get("bz")%>' maxlength='<%=ds.getColumn("bz").getPrecision()%>' style="width:110" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jsr").getFieldname()%></td>
                  <td  noWrap class="td"><%if(isEnd){%> <input type="text" name="jsr" value='<%=masterRow.get("jsr")%>' maxlength='<%=ds.getColumn("jsr").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                  <%}else {%>
                  <pc:select combox="1" className="edFocused" name="jsr" value="<%=jsr%>" style="width:110">
                  <%=personBean.getList()%></pc:select>
                  <%}%>
                  </td>
                  </tr>
                <%/*��ӡ�û��Զ�����Ϣ*/
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
                      out.print(masterProducer.getFieldInput(mBakFields[j], masterRow.get(filedcode), filedcode, style, isEnd, true));
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
                %>
                <tr>
                  <td class="td" colspan="8" nowrap>
                   <pc:navigator id="check_make_destroyNav" recordCount="<%=count%>" pageSize="<%=pageSize%>" form="form1" operate='<%="operate=sumitForm("+checkMakeDestroyBean.TURNPAGE+")"%>'/>
                 </td>
               </tr>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td nowrap>��Ʒ����</td>
                        <td nowrap>Ʒ�� ���</td>
                        <td nowrap>�������</td>
                        <td nowrap>����</td><%--02.19 21:00 �������ͻ���������λ�û���һ��,����Ĵ�ӡ���ݲ���Ҳ������Ӧ�ĸĶ� yjg--%>
                        <td nowrap>������λ</td>
                        <td nowrap>��������</td>
                        <td nowrap>���㵥λ</td>
                        <td nowrap>����</td>
                        <td nowrap>��λ</td>
                        <td nowrap>��ע</td>
                      </tr>
                    <%//prodBean.regData(list,"cpid");
                      //propertyBean.regData(list,"dmsxid");
                      //prodBean.regData(new String[]{"1"});
                      //propertyBean.regData(new String[]{"1"});
                      //buyOrderBean.regData(list,"hthwid");
                      BigDecimal t_sl = new BigDecimal(0),  t_hssl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      //2004-5-2 16:43 Ϊ��ϸ����ҳ�����ҳ
                     int min = check_make_destroyNav.getRowMin(request);
                     int max = check_make_destroyNav.getRowMax(request);
                     //����ȡ�ñ�ÿһҳ�����ݷ�Χ
                     checkMakeDestroyBean.min = min;
                     checkMakeDestroyBean.max = max > detailRows.length-1 ? detailRows.length-1 : max;
                     ArrayList cpidList = new ArrayList(max-min+1);
                     ArrayList dmsxidList = new ArrayList(max-min+1);
                     for(i=min; i<=max && i<detailRows.length; i++){
                       detail = detailRows[i];
                       String cpid = detail.get("cpid");
                       String dmsxid = detail.get("dmsxid");
                       cpidList.add(cpid);
                       dmsxidList.add(dmsxid);
                     }
                     prodBean.regData((String[])cpidList.toArray(new String[cpidList.size()]));
                     propertyBean.regData((String[])dmsxidList.toArray(new String[dmsxidList.size()]));//02.15 ���� ����ע��dmsxid����id��Ϊ��ע������ҳ��ͻ���� yjg

                     list.goToRow(min);
                     for(i=min; i<=max && i<detailRows.length; i++)   {
                       detail = detailRows[i];
                       String sl = detail.get("sl");
                       if(checkMakeDestroyBean.isDouble(sl))
                         t_sl = t_sl.add(new BigDecimal(sl));
                       String hssl = detail.get("hssl");
                       if(checkMakeDestroyBean.isDouble(hssl))
                         t_hssl = t_hssl.add(new BigDecimal(hssl));
                       String kwName = "kwid_"+i;
                       String dmsxidName = "dmsxid_"+i;
                    %>
                      <tr id="rowinfo_<%=i%>" onClick="showDetail()">
                        <td class="td" nowrap><%=i+1%></td>
                        <%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                          String hsbl = prodRow.get("hsbl");
                        detail.put("hsbl",hsbl);%>
                        <td class="td" nowrap><%=prodRow.get("cpbm")%></td>
                        <td class="td" nowrap><%=prodRow.get("product")%></td>
                         <td class="td" nowrap><input class="edline"  name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' readonly>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(!isEnd){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if('<%=detail.get("cpid")%>'==''){alert('���������Ʒ');return;}if('<%=prodRow.get("isprops")%>'=='0'){alert('������û�й������');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>','<%=detail.get("cpid")%>')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" align="right" nowrap><%=detail.get("sl")%></td>
                        <td class="td" nowrap><%=prodRow.get("jldw")%></td>
                        <td class="td" align="right" nowrap><%=detail.get("hssl")%></td>
                        <td class="td" nowrap><%=prodRow.get("hsdw")%></td>
                        <td><input type="text" class="ednone_r"  style="width:100%" onKeyDown="return getNextElement();" name="ph_<%=i%>" id="ph_<%=i%>" value='<%=detail.get("ph")%>' readonly>
                        <td class="td" nowrap>
                        <%if(isEnd) out.print("<input type='text' value='"+storeAreaBean.getLookupName(detail.get("kwid"))+"' style='width:100%' class='ednone' readonly>");
                        else {%>
                        <pc:select addNull="1" className="edFocused" name="<%=kwName%>" style="width:120">
                        <%=storeAreaBean.getList(detail.get("kwid"), "storeid", masterRow.get("storeid"))%></pc:select>
                        <%}%>

                        </td>
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" name="bz_<%=i%>" id="bz_<%=i%>" value='<%=detail.get("bz")%>' maxlength='<%=list.getColumn("bz").getPrecision()%>'<%=readonly%>></td>
                      </tr>
                      <%list.next();
                      }
                      for(; i < 4; i++){
                  %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>�ϼ�</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%>" value='<%=t_hssl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                      </tr>
                    </table></div>
                    </td>
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
            <td class="td"><b>�Ǽ�����:</b><%=masterRow.get("zdrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>�Ƶ���:</b><%=masterRow.get("zdr")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
              <%if(!isEnd){%>
              <input name="btnback" type="button" class="button" title="���淵��(ALT+S)" value="���淵��(S)" onClick="sumitForm(<%=Operate.POST%>);">
                <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
              <%}%>
              <input name="btnback" type="button" class="button"  title="�ر�(ALT+T)" value="�ر�(T)" onClick="backList();">
              <pc:shortcut key="t" script='<%="backList();"%>'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
  initDefaultTableRow('tableview1',1);
  <%=checkMakeDestroyBean.adjustInputSize(new String[]{"ph", "sxz", "bz"},  "form1", checkMakeDestroyBean.max-min+1, min)%>
    function checkDetails(oper)
    {
      if ( <%=max%>==0 ) return true;
      for (j=<%=min%>;j<=<%=max%>;j++)
      {
        obj = document.all['hssl_'+j];
        if(obj+''!='undefined' && isNaN(obj.value))
          obj.value = "";
        obj = document.all['scsl_'+j];
        if(obj+''!='undefined' && isNaN(obj.value))
          obj.value = "";
      }
      for (i=<%=min%>;i<=<%=max%>;i++)
      {
        hsslobj = document.all['hssl_'+i];
        scslobj = document.all['scsl_'+i];
        if ((hsslobj+''!='undefined' && isNaN(hsslobj.value))||
            (scslobj+''!='undefined' && isNaN(scslobj.value)) )
        {
           alert("��"+(i+1)+"�����ݲ��Ϸ�");
           return false;
        }
        else
          return true;
      }
  }
  function showDetail(masterRow){
    selectRow();
  }
</script>
<%out.print(retu);%>
</BODY>
</Html>