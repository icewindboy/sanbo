<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="java.util.ArrayList,engine.dataset.*"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "corplist";
%><%
  if(!loginBean.hasLimits("corplist", request, response))
    return;
  B_Corp corpBean = B_Corp.getInstance(request);
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
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function toDetail(){
  location.href='contactedit.jsp';
}

function backList()
{
  location.href='corplist.jsp';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar"><tr>
    <td NOWRAP align="center"></td>
  </tr></table>
<%String retu = corpBean.doService(request, response);
  if(retu.indexOf("backList()")>-1 || retu.indexOf("toDetail()")>-1)
  {
    out.print(retu);
    return;
  }
  LookUp areaBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_AREA);
  LookUp areaCodeBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_AREA_CODE);
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
  LookUp countryBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_COUNTRY);
  boolean hasAdd = loginBean.hasLimits(pageCode, op_add);
  boolean hasDelete = loginBean.hasLimits(pageCode, op_delete);
  boolean hasEdit = loginBean.hasLimits(pageCode, op_edit);
  boolean isSave = hasAdd || hasEdit;
  String rowClass = isSave ? "edbox" : "edline";
  String readOnly = isSave ? "" : " readonly";
  String disable = isSave ? "0" : "1";
  //String isInput = isSave ? "1" : "0";

  String curUrl = request.getRequestURL().toString();
  RowMap m_RowInfo = corpBean.getMasterRowinfo();
  EngineDataSet dsDWTX = corpBean.getMaterTable();
  EngineDataSet dsDWTX_LXR = corpBean.getDetailTable();
  EngineDataSet dsCorpFile = corpBean.dsCorpFile;
  ArrayList lxs = corpBean.listCorpType;
%>
<script language="javascript">
function areaChange(isCode){
  //连接的对象。编码更改对应地区更改
  var linkObj = FindSelectObject(isCode ? "dqh" : "code");
  if(linkObj == null)
    return;
  var areaid = isCode ? form1.code.value : form1.dqh.value;
  linkObj.SetSelectedKey(areaid);
}
</script>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
<table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
    <tr valign="top">
      <td width="400"><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
          <tr>
            <td class="activeVTab">往来单位信息</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=2 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="2" CELLPADDING="0" BORDER="0" width="100%" bgcolor="#f0f0f0">
                <tr>
                  <td noWrap class="tdTitle">&nbsp;单位编码&nbsp;</td>
                  <td noWrap class="td"><input type="text" name="dwdm" value='<%=m_RowInfo.get("dwdm")%>' maxlength='<%=dsDWTX.getColumn("dwdm").getPrecision()%>' style="width:120" class="edline" readOnly onKeyDown="return getNextElement();"></td>
                  <td noWrap class="tdTitle">&nbsp;所属国家&nbsp;</td>
                  <td noWrap class="td"><pc:select name="cdm" style="width:120" disable="<%=disable%>" className="<%=rowClass%>">
                    <%=countryBean.getList(m_RowInfo.get("cdm"))%> </pc:select>
                  </td>
                  <td noWrap class="tdTitle">&nbsp;所属地区&nbsp;</td>
                  <td noWrap class="td"><pc:select name="code" style="width:55" disable="<%=disable%>" className="<%=rowClass%>" onSelect="areaChange(true)">
									  <%=areaCodeBean.getList(m_RowInfo.get("dqh"))%></pc:select></td>
                  <td noWrap class="td"><pc:select name="dqh" style="width:100" disable="<%=disable%>" className="<%=rowClass%>" onSelect="areaChange(false)">
                    <%=areaBean.getList(m_RowInfo.get("dqh"))%></pc:select></td>
                </tr>
                <tr>
                  <!--
                  <td noWrap class="tdTitle">助记码</td>
                  <td noWrap class="td"><input type="text" name="zjm" value='<%=m_RowInfo.get("zjm")--%>' maxlength='<%=dsDWTX.getColumn("zjm").getPrecision()%>' style="width:120" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                  -->
                  <td noWrap class="tdTitle">单位名称</td>
                  <td colspan="6" noWrap class="td"><input type="text" name="dwmc" value='<%=m_RowInfo.get("dwmc")%>' maxlength='<%=dsDWTX.getColumn("dwmc").getPrecision()%>' style="width:350" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">电话</td>
                  <td noWrap class="td"><input type="text" name="tel" value='<%=m_RowInfo.get("tel")%>' maxlength='<%=dsDWTX.getColumn("tel").getPrecision()%>' style="width:120" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                  <td noWrap class="tdTitle">地址</td>
                  <td colspan="4" noWrap class="td"><input type="text" name="addr" value='<%=m_RowInfo.get("addr")%>' maxlength='<%=dsDWTX.getColumn("addr").getPrecision()%>' style="width:350" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">联系人</td>
                  <td noWrap class="td"><input type="text" name="lxr" value='<%=m_RowInfo.get("lxr")%>' maxlength='<%=dsDWTX.getColumn("lxr").getPrecision()%>' style="width:120" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                  <td noWrap class="tdTitle">税号</td>
                  <td colspan="4" noWrap class="td"><input type="text" name="nsrdjh" value='<%=m_RowInfo.get("nsrdjh")%>' maxlength='<%=dsDWTX.getColumn("nsrdjh").getPrecision()%>' style="width:350" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                </tr>
                <!--tr>
                  <td noWrap class="tdTitle">法人代表</td>
                  <td noWrap class="td"> <input type="text" name="frdb" value='<%=m_RowInfo.get("frdb")%>' maxlength='<%=dsDWTX.getColumn("frdb").getPrecision()%>' style="width:120" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                  <td noWrap class="tdTitle">开户行</td>
                  <td colspan="4" noWrap class="td"><input type="text" name="khh" value='<%=m_RowInfo.get("khh")%>' maxlength='<%=dsDWTX.getColumn("khh").getPrecision()%>' style="width:350" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                </tr-->
                <tr>
                  <td noWrap class="tdTitle">传真</td>
                  <td noWrap class="td"><input type="text" name="cz" value='<%=m_RowInfo.get("cz")%>' maxlength='<%=dsDWTX.getColumn("cz").getPrecision()%>' style="width:120" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                  <td noWrap class="tdTitle">帐号</td>
                  <td colspan="4" noWrap class="td"><input type="text" name="zh" value='<%=m_RowInfo.get("zh")%>' maxlength='<%=dsDWTX.getColumn("zh").getPrecision()%>' style="width:350" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                </tr>
                <tr>
                  <td height="21" noWrap class="tdTitle">邮政编码</td>
                  <td noWrap class="td"><input type="text" name="zp" value='<%=m_RowInfo.get("zp")%>' maxlength='<%=dsDWTX.getColumn("zp").getPrecision()%>' style="width:120" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                  <td noWrap class="tdTitle">网址</td>
                  <td noWrap class="td"><input type="text" name="http" value='<%=m_RowInfo.get("http")%>' maxlength='<%=dsDWTX.getColumn("http").getPrecision()%>' style="width:120" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                  <td noWrap class="tdTitle">电子邮件</td>
                  <td colspan="2" noWrap class="td"><input type="text" name="email" value='<%=m_RowInfo.get("email")%>' maxlength='<%=dsDWTX.getColumn("email").getPrecision()%>' style="width:150" class="<%=rowClass%>"<%=readOnly%> onKeyDown="return getNextElement();"></td>
                </tr>

                <tr>
                  <td noWrap class="tdTitle">业务类型</td>
                  <td colspan="6" noWrap class="td">
                    <%--String disabled = isSave ? "" : " disabled";--%>
                    <input type="checkbox" name="ywlx" value="1" <%=lxs.contains("1")?"checked":""%>  onKeyDown="return getNextElement();">
                    供货单位
                    <input type="checkbox" name="ywlx" value="2" <%=lxs.contains("2")?"checked":""%>  onKeyDown="return getNextElement();">
                    销货单位
                    <input type="checkbox" name="ywlx" value="3" <%=lxs.contains("3")?"checked":""%>  onKeyDown="return getNextElement();">
                    外加工单位
                    <input type="checkbox" name="ywlx" value="4" <%=lxs.contains("4")?"checked":""%>  onKeyDown="return getNextElement();">
                    承运单位
                    <input type="checkbox" name="ywlx" value="5" <%=lxs.contains("5")?"checked":""%>>
                    进口商</td>
                </tr>
                <tr>
                  <td colspan="7" noWrap><table CELLSPACING=0 WidTH="100%" CELLPADDING=0>
                      <tr>
                        <td nowrap><div id="tabDivINFO_EX_0" class="activeTab"><A class="tdTitle" HREF="#" ONCLICK="blur();SetActiveTab(INFO_EX,'INFO_EX_0');return false;">联系人</A></div></td>
                        <%--td nowrap><div id="tabDivINFO_EX_1" class="normalTab"><A class="tdTitle" HREF="#" ONCLICK="blur();SetActiveTab(INFO_EX,'INFO_EX_1');return false;">交易信息</A></div></td--%>
                        <td nowrap><div id="tabDivINFO_EX_2" class="normalTab"><A class="tdTitle" HREF="#" ONCLICK="blur();SetActiveTab(INFO_EX,'INFO_EX_2');return false;">相关附件</A></div></td>
                        <td nowrap><div id="tabDivINFO_EX_3" class="normalTab"><A class="tdTitle" HREF="#" ONCLICK="blur();SetActiveTab(INFO_EX,'INFO_EX_3');return false;">备注</A></div></td>
                        <td class="lastTab" valign=bottom width=100% align=right><A class="tdTitle" HREF="#">&nbsp;</A></td>
                      </tr>
                    </table>
                    <div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:100%;height:110;overflow-y:auto;overflow-x:hidden;">
                      <table id="tableview1" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
                        <tr class="tableTitle">
                          <td nowrap width=45 align="center">
                            <%if(hasAdd || hasEdit){%>
                            <input name="image" class="img" type="image" title="新增(ALT+A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>,-1)" src="../images/add.gif" border="0">
                            <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/>
                            <%}%>
                          </td>
                          <td nowrap>姓名</td>
                          <td nowrap>登录名</td>
                          <td nowrap>密码</td>
                          <td nowrap>称呼</td>
                          <td nowrap>职务</td>
                          <td nowrap>办公电话</td>
                          <td nowrap>移动电话</td>
                          <td nowrap>电子邮件</td>
                        </tr>
                      <%dsDWTX_LXR.first();
                        for(int i=0; i<dsDWTX_LXR.getRowCount(); i++){%>
                        <tr onDblClick="sumitForm(<%=Operate.DETAIL_EDIT%>,<%=i%>)">
                          <td class="td" align="center"><input name="image2" class="img" type="image" title='<%=(hasAdd || hasEdit) ? "修改" : "浏览"%>' onClick="sumitForm(<%=Operate.DETAIL_EDIT%>,<%=i%>)" src="../images/edit.gif" border="0" align="absmiddle">
                            <%if(isSave){%>
                            <input name="image3" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle">
                            <%}%>
                          </td>
                          <td class="td" nowrap><%=dsDWTX_LXR.getValue("xm")%></td>
                          <td class="td" nowrap><%=dsDWTX_LXR.getValue("loginName")%></td>
                          <td class="td" nowrap><%=dsDWTX_LXR.getValue("loginPassword")%></td>
                          <td class="td" nowrap><%=dsDWTX_LXR.getValue("ch")%></td>
                          <td class="td" nowrap><%=dsDWTX_LXR.getValue("zw")%></td>
                          <td class="td" nowrap><%=dsDWTX_LXR.getValue("bgdh")%></td>
                          <td class="td" nowrap><%=dsDWTX_LXR.getValue("yddh")%></td>
                          <td class="td" nowrap><%=dsDWTX_LXR.getValue("dzyj")%></td>
                        </tr>
                      <%dsDWTX_LXR.next();}%>
                      </table>
                      <script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</script>
                    </div>
                    <%--div id="cntDivINFO_EX_1" class="tabContent" style="display:none;width:100%;height:110;overflow-y:auto;overflow-x:hidden;">
                      <center>
                        <table CELLSPACING="0" CELLPADDING="0" WidTH="90%" BORDER="0">
                          <tr>
                            <td noWrap class="tdTitle" width="100">首次交易</td>
                            <td noWrap class="td" >&nbsp;</td>
                            <td noWrap class="tdTitle" width="100">最近交易</td>
                            <td noWrap class="td" >&nbsp;</td>
                          </tr>
                          <tr>
                            <td noWrap  class="tdTitle" >交易次数</td>
                            <td noWrap  class="td" ></td>
                            <td noWrap  class="tdTitle" >退货次数</td>
                            <td noWrap  class="td" ></td>
                          </tr>
                          <tr>
                            <td noWrap  class="tdTitle" >累计交易额</td>
                            <td noWrap  class="td" ></td>
                            <td noWrap  class="tdTitle" >累计利润</td>
                            <td noWrap  class="td" ></td>
                          </tr>
                          <tr>
                            <td noWrap  class="tdTitle" >累计欠款额</td>
                            <td noWrap  class="td" ></td>
                            <td noWrap  class="tdTitle" >信用额度</td>
                            <td noWrap  class="td" ></td>
                          </tr>
                        </table>
                      </center>
                    </div--%>
                    <div id="cntDivINFO_EX_2" class="tabContent" style="display:none;width:100%;height:110;overflow-y:auto;overflow-x:hidden;">
                      <table id="tableview2" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
                        <tr class="tableTitle">
                          <td nowrap width=45 align="center">
                            <%if(hasAdd || hasEdit){%>
                            <input name="image5" class="img" type="image" title="新增(ALT+B)" onClick="showInterFrame(<%=corpBean.FILE_ADD%>,-1)" src="../images/add.gif" border="0">
                            <pc:shortcut key="b" script='<%="showInterFrame("+ corpBean.FILE_ADD +",-1)"%>'/>
                            <%}%>
                          </td>
                          <td nowrap>附件名称</td>
                        </tr>
                       <%dsCorpFile.first();
                         for(int i=0; i<dsCorpFile.getRowCount(); i++){
                           String fileName = dsCorpFile.getValue("file_name");
                           if(fileName.length() > 0){
                             fileName = new StringBuffer("<A href=\"javascript:openUrlOpt2('corp_file_down.jsp?operate=")
                                      //.append(corpBean.FILE_DOWN)&file_id=")//operate
                                      //.append(dsCorpFile.getValue("file_id")).append("')\">")
                                      .append(corpBean.FILE_DOWN).append("&rownum=").append(i).append("')\">")
                                      .append(fileName).append("</A>").toString();
                           }
                       %>
                        <tr>
                          <td class="td" align="center">
                            <%if(isSave){%>
                            <input name="image22" class="img" type="image" title="修改" onClick="showInterFrame(<%=corpBean.FILE_EDIT%>,<%=i%>)" src="../images/edit.gif" border="0" align="absmiddle">
                            <input name="image32" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=corpBean.FILE_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle">
                            <%}%>
                          </td>
                          <td class="td" nowrap><%=fileName%></td>
                        </tr>
                        <%dsCorpFile.next();}%>
                      </table><script LANGUAGE="javascript">initDefaultTableRow('tableview2',1);</script>
                    </div>
                    <div id="cntDivINFO_EX_3" class="tabContent" style="display:none;width:100%;height:110;overflow-y:auto;overflow-x:hidden;">
                      <center>
                        <textarea name="bz" rows="6" cols="64"><%=m_RowInfo.get("bz")%></textarea>
                      </center>
                    </div>
                    <script LANGUAGE="javascript">INFO_EX = new TabControl('INFO_EX',0);AddTabItem(INFO_EX,'INFO_EX_0','tabDivINFO_EX_0','cntDivINFO_EX_0');
                      <%--AddTabItem(INFO_EX,'INFO_EX_1','tabDivINFO_EX_1','cntDivINFO_EX_1');--%>
                      AddTabItem(INFO_EX,'INFO_EX_2','tabDivINFO_EX_2','cntDivINFO_EX_2');
                      AddTabItem(INFO_EX,'INFO_EX_3','tabDivINFO_EX_3','cntDivINFO_EX_3');
                      if (window.top.StatFrame+''!='undefined'){ var tmp_curtab=window.top.StatFrame.GetRegisterVar('INFO_EX');if (tmp_curtab!='') {SetActiveTab(INFO_EX,tmp_curtab);}}
                    </script>
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
        <table CELLSPACING=0 CELLPADDING=0 width="100%">
          <tr>
            <td noWrap class="tableTitle"><br>
              <%if(hasAdd){%><input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
              <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +")"%>'/><%}%>
              <%if(!corpBean.isDirect && isSave){%><input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +")"%>'/><%}%>
              <%--input name="button3" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="合并客户"--%>
              <%if(!corpBean.isDirect && hasDelete){%><input name="btnback2" type="button" class="button" onClick="sumitForm(<%=Operate.DEL%>)" value=" 删除(D) ">
              <pc:shortcut key="d" script='<%="sumitForm("+ Operate.DEL +")"%>'/><%}%>
              <input name="btnback" type="button" class="button" onClick="<%=corpBean.isDirect ? "window.close();" : "backList();"%>" value=" 返回(C) ">
              <pc:shortcut key="c" script='<%=corpBean.isDirect ? "window.close();" : "backList();"%>'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
  function showInterFrame(oper, rownum){
    var url = "corp_file_edit.jsp?operate="+oper+"&rownum="+rownum;
    //var url = "corp_file_edit.jsp";
    document.all.interframe1.src = url;
    showFrame('detailDiv',true,"",true);
  }
  function hideInterFrame()//隐藏FRAME
  {
    //hideFrame('detailDiv');
    form1.operate.value=<%=corpBean.REFRESH_FILE%>;
    form1.submit();
  }
  function hideFrameNoFresh(){
    hideFrame('detailDiv');
  }
  <%String operate = request.getParameter("operate");
  if(corpBean.REFRESH_FILE.equals(operate) || corpBean.FILE_DEL.equals(operate)){%>SetActiveTab(INFO_EX,'INFO_EX_2');<%}%>
</script>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="335" height="180" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(retu);%>
</body>
</html>
