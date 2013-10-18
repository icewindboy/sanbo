<%@ page import="engine.project.*, engine.dataset.RowMap"%>
<%QueryForm form= QueryForm.getInstance(request);
  RowMap search = form.getSearchRow();
  String divName = (String)pageContext.getAttribute("divName");
  if(divName == null)
    divName = "divQuery";
  String hideFunction = (String)pageContext.getAttribute("hideFunction");
  if(hideFunction == null)
    hideFunction = "";
  String submitFunction = (String)pageContext.getAttribute("submitFunction");
  if(submitFunction == null)
    submitFunction = "";
%>
<div id="<%=divName%>" name="<%=divName%>" class="queryPop">
  <div align=right class="queryTitleBox"><A HREF="#" onclick="<%=hideFunction%>"><img src="../images/closewin.gif" border=0></A></div>
  <table border="0"cellpadding="0" cellspacing="3">
    <tr>
      <td> <table border="0" cellpadding="0" cellspacing="3">
          <tr>
            <td noWrap class="td" align="center">查询项目</td>
            <td noWrap class="td" align="center">操作符</td>
            <td noWrap class="td" align="center">值(空值无效)</td>
            <td noWrap class="td" align="center">逻辑符</td>
          </tr>
          <tr>
            <td noWrap class="td" align="center"><pc:select name="item1" style="width:120">
                <%=form.getItemOption("item1")%></pc:select></td>
            <td noWrap class="td"><pc:select name="opsign1" size="1" style="width:80">
                <%=form.getOpsignOption("opsign1")%></pc:select></td>
            <td noWrap class="td"><input type="text" name="value1" value='<%=search.get("value1")%>' style="width:140" class="edbox" title="值为空时,条件不起作用"></td>
            <td noWrap class="td"><pc:select name="link1" size="1" style="width:60">
                <%=form.getLinkOption("link1")%></pc:select></td>
          </tr>
          <tr>
            <td noWrap class="td" align="center"><pc:select name="item2" style="width:120">
                <%=form.getItemOption("item2")%></pc:select></td>
            <td noWrap class="td"> <pc:select name="opsign2" size="1" style="width:80">
                <%=form.getOpsignOption("opsign2")%></pc:select></td>
            <td noWrap class="td"><input type="text" name="value2" value='<%=search.get("value2")%>' style="width:140" class="edbox" title="值为空时,条件不起作用"></td>
            <td noWrap class="td"><pc:select name="link2" size="1" style="width:60">
                <%=form.getLinkOption("link2")%></pc:select></td>
          </tr>
          <tr>
            <td noWrap class="td" align="center"><pc:select name="item3" style="width:120">
                <%=form.getItemOption("item3")%></pc:select></td>
            <td noWrap class="td"> <pc:select name="opsign3" size="1" style="width:80">
                <%=form.getOpsignOption("opsign3")%></pc:select></td>
            <td noWrap class="td"><input type="text" name="value3" value='<%=search.get("value3")%>' style="width:140" class="edbox" title="值为空时,条件不起作用"></td>
            <td noWrap class="td"><pc:select name="link3" size="1" style="width:60">
                <%=form.getLinkOption("link3")%></pc:select></td>
          </tr>
          <tr>
            <td noWrap class="td" align="center"><pc:select name="item4" style="width:120">
                <%=form.getItemOption("item4")%></pc:select></td>
            <td noWrap class="td"> <pc:select name="opsign4" size="1" style="width:80">
                <%=form.getOpsignOption("opsign4")%></pc:select></td>
            <td noWrap class="td"><input type="text" name="value4" value='<%=search.get("value4")%>' style="width:140" class="edbox" title="值为空时,条件不起作用"></td>
            <td noWrap class="td"><pc:select name="link4" size="1" style="width:60">
                <%=form.getLinkOption("link4")%></pc:select></td>
          </tr>
          <tr>
            <td noWrap class="td" align="center"><pc:select name="item5" style="width:120">
                <%=form.getItemOption("item4")%></pc:select></td>
            <td noWrap class="td"> <pc:select name="opsign5" size="1" style="width:80">
                <%=form.getOpsignOption("item5")%></pc:select></td>
            <td noWrap class="td"><input type="text" name="value5" value='<%=search.get("value5")%>' style="width:140" class="edbox" title="值为空时,条件不起作用"></td>
            <td noWrap class="td"><pc:select name="link5" size="1" style="width:60">
                <%=form.getLinkOption("link5")%></pc:select></td>
          </tr>
          <tr>
            <td noWrap class="td" align="center"><pc:select name="item6" style="width:120">
                <%=form.getItemOption("item6")%></pc:select></td>
            <td noWrap class="td"> <pc:select name="opsign6" size="1" style="width:80">
                <%=form.getOpsignOption("opsign6")%></pc:select></td>
            <td noWrap class="td"><input type="text" name="value6" value='<%=search.get("value6")%>' style="width:140" class="edbox" title="值为空时,条件不起作用"></td>
            <td noWrap class="td">&nbsp; <%--pc:select name="link6" size="1" style="width:60">form.getLinkOption("link6")</pc:select--%>
            </td>
          </tr>
          <tr>
            <td colspan="4" noWrap align="center" height="30"> <input name="button" type="button" class="button" onClick='<%=submitFunction%>;' value=" 查询 ">
              <input name="button2" type="button" class="button" onClick="<%=hideFunction%>" value=" 关闭 ">
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</div>