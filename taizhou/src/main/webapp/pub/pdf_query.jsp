<%@page import="engine.dataset.sql.QueryWhereField"%><%

  String curUrl = request.getRequestURL().toString();
  //
  engine.report.util.ContextData pageData = pdf.context;
  String selGroupColumn = pdf.getWhereFieldValue("groupColumn");
  String[] columns = pageData.getAllColumns();
  boolean isShowColumnSel = columns != null && columns.length > 0;
  //
  String[] groupColumns = pageData.getGroupColumns();
  String[] sumColumns = pageData.getSumColumns();
  //如果可小计数组不为空并且可合计的字段数组不为空
  boolean isShowGroupSel = groupColumns != null && groupColumns.length > 0 && sumColumns != null && sumColumns.length>0;
  String formName = pdf.getPageForm("fixedQueryform");
  String tableName = pdf.getPageTable("querytable");
  StringBuffer bufFieldType = new StringBuffer();
%>
<script language="javascript" src="../scripts/exchangeselect.js"></script>
<script language="javascript">
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  getColumnArray();
  <%=formName%>.operate.value = oper;
  <%=formName%>.submit();
}
function hide()
{
  hideFrame('fixedQuery');
}
function addAdvanceRow(tableId, index)
{
  var oTB = document.all[tableId];
  if(oTB +'' == 'undefined')
    return;
  var i = oTB.rows.length;
  if(i>20)//max 20row
    return;
  if(index != i-1)
    return;
  var oTR = oTB.insertRow();
  var oTd = oTR.insertCell();
  oTd.className = "td";
  oTd.nowrap = true;
<%engine.web.taglib.Select select = new engine.web.taglib.Select();
  select.setName("item_\"+i+\"");
  select.setStyle("width:120");
%>oTd.innerHTML = "<%=select.getStartTag(true)%>";
  oTd = oTR.insertCell();
  oTd.className = "td";
  oTd.nowrap = true;
<%select.release();
  select.setName("opsign_\"+i+\"");
  select.setStyle("width:80");
%>oTd.innerHTML = "<%=select.getStartTag(true)%>";
  oTd = oTR.insertCell();
  oTd.className = "td";
  oTd.nowrap = true;
  oTd.innerHTML = "<input type='text' name='value_"+i +"' value='' style='width:140' class='edbox' onChange='valueOnchange(this,"+ i +");'>";

  var obj = GetSelectObject('item_'+i, true, true, "itemOnSelect("+i+");");
  AddSelectItem('','');
<%if(pdf.hasAdvanceField()){
     QueryWhereField[] fields = pdf.getWhereFields();
     String[] fieldNames = pdf.getWhereFieldNames();
     for(int i=0; i<fields.length; i++)
     {
       bufFieldType.append("typeObjs[typeObjs.length]='").append(fields[i].getDataType()).append("';");
       out.print("AddSelectItem('");
       out.print(fieldNames[i]);
       out.print("','");
       out.print(fields[i].getCaption());
       out.print("');");
     }
  }
%>
  obj = GetSelectObject('opsign_'+i, true, true, '');

  oTd = oTR.insertCell();
  oTd.className = "td";
  oTd.nowrap = true;
<%select.release();
  select.setName("lnsign_\"+i+\"");
  select.setStyle("width:50");
%>oTd.innerHTML = "<%=select.getStartTag(true)%>";
  var linkOnSel ="linkValue=<%=formName%>.lnsign_"+i+".value; if(linkValue != '') addAdvanceRow('"+tableId+"',"+i+");"
  obj = GetSelectObject('lnsign_'+i, true, true, linkOnSel);
  AddSelectItem('','');
  AddSelectItem('AND','并且');
  AddSelectItem('OR','或者');
}
var typeObjs = new Array();
<%=bufFieldType%>
//typeObjs[typeObjs.length] = 'varchar';
function itemOnSelect(i)
{
  var itemObj = FindSelectObject('item_'+i);
  var index = itemObj.selectedIndex-1;
  if(index < 0)
    return;
  //得到第几个选择项目
  var datatype = typeObjs[index];
  if(datatype == 'varchar' || datatype == 'char')
  {
    var obj = FindSelectObject('opsign_'+i);
    obj.RemoveAll();
    SetDestSelectObject(obj);
    AddSelectItem('=','等于');
    AddSelectItem('>','大于');
    AddSelectItem('<','小于');
    AddSelectItem('>=','大于等于');
    AddSelectItem('<=','小于等于');
    AddSelectItem('<>','不等于');
    AddSelectItem('left_like','左边包含');
    AddSelectItem('right_like','右边包含');
    AddSelectItem('like','包含');
    AddSelectItem('not like','不包含');
  }
  else if(datatype == 'number' || datatype == 'date' || datatype == 'time' || datatype == 'datetime')
  {
    var obj = FindSelectObject('opsign_'+i);
    obj.RemoveAll();
    SetDestSelectObject(obj);
    AddSelectItem('=','等于');
    AddSelectItem('>','大于');
    AddSelectItem('<','小于');
    AddSelectItem('>=','大于等于');
    AddSelectItem('<=','小于等于');
    AddSelectItem('<>','不等于');
  }
}
function valueOnchange(obj, i)
{
  var itemObj = FindSelectObject('item_'+i);
  var index = itemObj.selectedIndex-1;
  if(index < 0)
    return;
  //得到第几个选择项目
  var datatype = typeObjs[index];
  //var datatype = typeObjs[i];
  if(datatype == 'number')
    checkNumber(obj);
  else if(datatype == 'date')
    checkDate(obj);
}
var isHide = true;
function showAdanceInfo()
{
  isHide = !isHide;
  document.all.trAdvanceInfo.style.display = isHide ? 'none' : 'block';
  document.all.flagAdvance.innerText = isHide ? '-->>' : '--<<';
  if(!isHide)
    addAdvanceRow('tbAdvance',0);
}
<%--checkbox showColumn 的click事件--%>
function showColumn_onclick(obj, isShowGroupSel){
  fnEnableOption(obj.checked, true);
  if(obj.checked){
    var key = obj.value;
    <%if(isShowGroupSel){
        StringBuffer buf = new StringBuffer();
        for(int i=0; i <groupColumns.length; i++)
        {
          if(i > 0)
            buf.append(" || ");
          buf.append("key=='").append(groupColumns[i]).append("'");
        }
    %>
    if(<%=buf%>){
      var rowClick = "var n = getRowIndexByValue(groupCols,'"+ key
                   + "', true); var radio = document.all['groupColumn'];"
                   + "if(n==null) radio.checked = true; else if(n>=0) radio[n].checked = true;";
      fnAddRadioOption(groupCols, "groupColumn", rowClick, null);
    }
    <%}%>
  }
  else {
    fnRemoveOption(sortColumns, obj.value);
    <%if(isShowGroupSel){%>
    fnRemoveOption(groupCols, obj.value, true);<%}%>
  }
}
<%--小计行的点击--%>
function groupCols_rowonclick(obj)
{
  var n = getRowIndex(groupCols, obj);
  var radio = document.all['groupColumn'];
  if(n==null)
    radio.checked = true;
  else if(n>=0)
    radio[n].checked = true;
}
function getColumnArray()
{
  <%if(isShowColumnSel){
    out.println(formName +".allColumnStr.value = fnGetOptionValue(',', showColumns, true);");
    out.println(formName +".sortColumnStr.value = fnGetOptionValue(',', sortColumns);");
  }
  if(isShowGroupSel)
    out.println(formName +".groupColumnStr.value = fnGetOptionValue(',', groupCols, true);");
  %>
}
</script>
<%--BODY oncontextmenu="window.event.returnValue=true" onLoad="bodyLoad()" style="background-color:transparent"--%>
<form id="<%=formName%>" name="<%=formName%>" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="allColumnStr" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="sortColumnStr" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="groupColumnStr" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hide()" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD><TABLE cellspacing=3 cellpadding=0 border=0 id="<%=tableName%>" name="<%=tableName%>">
            <%engine.html.HtmlTableProducer.printWhereInfo(pageContext, pdf.whereQueryItem);%>
            <tr id="trColumninfo" style="display:none"><td colspan="4"></td></tr>
            <%if(pdf.hasAdvanceField()){%>
            <tr id="trAdvanceInfo" style="display:none"><td colspan="4">
              <table id="tbAdvance" cellspac="0" cellpadding="0" width="100%" border="0">
                <tr>
                  <td noWrap class="td" align="center">查询项目</td>
                  <td noWrap class="td" align="center">操作符</td>
                  <td noWrap class="td" align="center">值</td>
                  <td noWrap class="td" align="center">逻辑符</td>
                </tr>
              </table>
              </td>
            </tr>
            <tr><td colspan="4" nowrap align="right"><a id="flagAdvance" href="javascript:" onClick="showAdanceInfo();" title="高级查询">--&gt;&gt;</a></td></tr>
            <%} if(isShowColumnSel || isShowGroupSel) {%>
            <tr>
              <td colspan="4" nowrap class="td"><table cellspac="0" cellpadding="0" width="100%" border="0">
                  <tr>
                    <%if(isShowColumnSel) {
                      StringBuffer buf = new StringBuffer("<script language='javascript'>var oOption;");
                      //TOption(type, value, text, isEnable, rowClick, checkName, isChecked, checkClick)
                      for(int i=0; i<columns.length; i++)
                      {
                        boolean isShow = pageData.isShowColumn(columns[i]);
                        buf.append("oOption = new TOption('checkbox','").append(columns[i]).append("',");
                        buf.append("'").append(pageData.getCaption(columns[i])).append("',");
                        buf.append(isShow ? "true" : "false").append(",null,'showColumn',");
                        buf.append(isShow ? "true" : "false").append(",");
                        buf.append("'showColumn_onclick(this);');");
                        buf.append("fnAddOption(showColumns, oOption);");
                      }
                      buf.append("</script>");
                      //
                      StringBuffer sot = new StringBuffer("<script language='javascript'>");
                      columns = pageData.getSortColumns();
                      for(int i=0; columns!=null && i<columns.length; i++)
                      {
                        sot.append("oOption = new TOption('none','").append(columns[i]).append("',");
                        sot.append("'").append(pageData.getCaption(columns[i])).append("',true,null);");
                        sot.append("fnAddOption(sortColumns, oOption);");
                      }
                      sot.append("</script>");
                    %>
                    <td><table width="100%" cellspacing="0" cellpadding="0" border="0">
                        <TR>
													<td><table cellSpacing="0" cellPadding="0" border="0" width="100%">
                            <tr>
                              <td class="td" valign="top" nowrap height="25">&nbsp;</td>
                            </tr>
                            <tr>
                              <td height="65" valign="top"><img style='cursor:hand' src='../images/up_arrow.gif' onClick='fnMoveOption(showColumns, -1, showColumn)' title="上移"></td>
                            </tr>
                            <tr>
                              <td height="65" valign="bottom"><img style='cursor:hand' src='../images/down_arrow.gif' onClick='fnMoveOption(showColumns, 1, showColumn)' title="下移"></td>
                            </tr>
                          </table></td>
                          <td class=td> <table cellSpacing="0" cellPadding="0" border="0" width="100%">
                              <tr>
                                <td class="td" valign="top" nowrap height="25"><b>显示列名</b></td>
                              </tr>
                              <tr>
                                <td><div style="overflow-y: auto; width: 110; height: 130; background-color: white; border: 1 solid #777777;" onselectstart="return CancelSelect();">
                                    <table id="showColumns" name="showColumns" width="100%" cellSpacing="0" cellPadding="0" border="0" onclick="SelectOption(this, true)" ondblclick="fnExchangeSelect(showColumns, sortColumns, true)">
                                    </table><%=buf%>
                                  </div></td>
                              </tr>
                            </table></td>
                        </tr>
                      </table></td>
                    <td><table cellSpacing="0" cellPadding="0" border="0" width="100%">
                        <tr>
                          <td class="td" valign="top" nowrap height="25">&nbsp;</td>
                        </tr>
                        <tr>
                          <td height="45"></td>
                        </tr>
                        <tr>
                          <td><input name="button3" type="button" class="edbox" style="width:20;" onclick="fnExchangeSelect(showColumns, sortColumns, true)" value="&gt;" title="添加排序列名"></td>
                        </tr>
                        <tr>
                          <td height="2"></td>
                        </tr>
                        <tr>
                          <td><input name="button3" type="button" class="edbox" style="width:20;" onclick="fnRemoveMultiOption(sortColumns)" value="&lt;" title="移去排序列名"></td>
                        </tr>
                        <tr>
                          <td height="45"></td>
                        </tr>
                      </table></td>
                    <td><table cellSpacing="0" cellPadding="0" border="0" width="100%">
                        <tr>
                          <td class="td" valign="top" nowrap height="25"><b>排序列名</b></td>
                        </tr>
                        <tr>
                          <td><div style="overflow-y: auto; width: 110; height: 130; background-color: white; border: 1 solid #777777;" onselectstart="return CancelSelect();">
                              <table id="sortColumns" name="sortColumns" width="100%" cellSpacing="0" cellPadding="0" border="0" onclick="SelectOption(this)" ondblclick="fnRemoveMultiOption(sortColumns)">
                              </table><%=sot%>
                            </div></td>
                        </tr>
                      </table></td>
                    <td style="width:20"></td>
                    <%} if(isShowGroupSel){
                      boolean isshowgroup = pdf.getWhereFieldValue("isshowgroup").equals("1");
                      boolean isSelGroup = pageData.isGroupColumn(selGroupColumn);
                      StringBuffer buf = new StringBuffer("<script language='javascript'>var oOption1;");
                      //TOption(type, value, text, isEnable, rowClick, checkName, isChecked, checkClick)
                      columns = pdf.canGroupColumn;
                      for(int i=0; columns!=null && i<columns.length; i++)
                      {
                        buf.append("oOption1 = new TOption('radio','").append(columns[i]).append("',");//value
                        buf.append("'").append(pageData.getCaption(columns[i])).append("',");//text
                        buf.append("true,'groupCols_rowonclick(this);','groupColumn',");//isEnable
                        buf.append(!isSelGroup && i==0 ? "true" : selGroupColumn.equals(columns[i]) ? "true" : "false");//isChecked
                        buf.append(",null);fnAddOption(groupCols, oOption1);");
                      }
                      buf.append("</script>");
                    %>
                    <td><table cellSpacing="0" cellPadding="0" border="0" width="100%">
                        <tr>
                          <td class="td" valign="top" nowrap height="25"><input type="checkbox"
                              name="isshowgroup" value="1"<%=isshowgroup ? " checked" : ""%>>
                            <b>小计列名</b></td>
                        </tr>
                        <tr>
                          <td><div style="overflow-y: auto; width: 110; height: 130; background-color: white; border: 1 solid #777777;" onselectstart="return CancelSelect();">
                              <table id="groupCols" name="groupCols" width="100%" cellSpacing="0" cellPadding="0" border="0" onclick="SelectOption(this, true)">
                              </table><%=buf%>
                            </div></td>
                          <%}%>
                        </tr>
                      </table></td>
                </table></TD>
            </tr>
            <%}%>
            <TR>
              <TD nowrap colspan=5 height=30 align="center">
                <INPUT class="button" onClick="sumitFixedQuery(<%=engine.action.Operate.FIXED_SEARCH%>)" type="button" value="查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script='<%="sumitFixedQuery("+engine.action.Operate.FIXED_SEARCH+")"%>'/>
                <INPUT class="button" onClick="hide();" type="button" value="关闭(X)" name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script='hide();'/>
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
