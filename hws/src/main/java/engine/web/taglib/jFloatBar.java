package engine.web.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.io.IOException;
import java.util.*;
import java.lang.StringBuffer;

/**
 * 浮动工具栏, 扩展标签来显示网页页数，继承TagSupport类
 */
public class jFloatBar extends TagSupport
{
  private String searchFunc=null;
  private String addFunc=null;
  private String saveFunc=null;
  private String approveFunc=null;
  private String cancelFunc=null;
  private String customButton=null;
  private StringBuffer buf = new StringBuffer();;
  /**
   *
   */
  private void print(String s)
  {
    buf.append(s);
  }
  private void println()
  {
    buf.append("\n");
  }
  private void println(String s)
  {
    print(s);
    println();
  }
  private void printDiv()
  {
    println("<div ID='floater' style='position:absolute; left:178px; top:0px; z-index:1; background-color: RED; width: 400px; height: 30px;'>");
    print("<table width='100%' height='100%' border=0 cellspacing=1 cellpadding=0 align=center bgcolor=000000 class='td0'>");
    print("<tr>");
    println("<td>");
    print("<table width='100%' border=2 cellspacing=0 cellpadding=0 bgcolor=#FFFFFF bordercolorlight=605ca8 bordercolordark=605ca8 bordercolor='605ca8'>");
    print("<tr bgcolor=ffffff>");
    println("<td  align='center'>");
        print("<table width='100%' border=0 cellspacing=1 cellpadding=1>");
        print("<tr>");
        println("<td class='td2' align='left'>");
    if(this.searchFunc!=null)
      print("<input type='button' name='search' value='查找' onClick="+searchFunc+">");
    if(this.addFunc!=null)
      print("<input type='button' name='add' value='新增' onClick="+addFunc+">");
    if(this.saveFunc!=null)
      print("<input type='button' name='ok' value='保存' onClick="+saveFunc+">");
    if(this.approveFunc!=null)
      print("<input type='button' name='approve' value='审核' onClick="+approveFunc+">");
    if(this.cancelFunc!=null)
      print("<input type='button' name='cancel' value='返回' onClick="+cancelFunc+">");
    if(this.customButton!=null)
      print("customButton");
          print("</td>");
        print("</tr>");
      println("</table>");
      print("</td>");
    print("</tr>");
  println("</table>");
    print("<td>");
    print("</tr>");
    print("</table>");
    print("</div>");
  }
  /**
   *
   */
  private void printJavaScript()
  {
    println("<script LANGUAGE='JavaScript'>");
    println("self.onError=null; currentX = currentY = 0;  whichIt = null;");
    println("lastScrollX = 0; lastScrollY = 0;");
    println("NS = (document.layers) ? 1 : 0;  IE = (document.all) ? 1: 0;");
    //<!-- STALKER CODE -->
    println("function heartBeat() {");
    println("  if(IE) { diffY = document.body.scrollTop; diffX = document.body.scrollLeft; }");
    println("  if(NS) { diffY = self.pageYOffset; diffX = self.pageXOffset; }");
    println("  if(diffY != lastScrollY) {");
    println("    percent = .1 * (diffY - lastScrollY);");
    println("    if(percent > 0) percent = Math.ceil(percent);");
    println("    else percent = Math.floor(percent);");
    println("    if(IE) document.all.floater.style.pixelTop += percent;");
    println("    if(NS) document.floater.top += percent;");
    println("    lastScrollY = lastScrollY + percent;");
    println("  }");
    println("  if(diffX != lastScrollX) {");
    println("    percent = .1 * (diffX - lastScrollX);");
    println("    if(percent > 0) percent = Math.ceil(percent);");
    println("    else percent = Math.floor(percent);");
    println("    if(IE) document.all.floater.style.pixelLeft += percent;");
    println("    if(NS) document.floater.left += percent;");
    println("    lastScrollX = lastScrollX + percent;");
    println("  }");
    println("}");
    //<!-- /STALKER CODE -->
    //<!-- DRAG DROP CODE -->
    println("function checkFocus(x,y) {");
    println("  stalkerx = document.floater.pageX;");
    println("  stalkery = document.floater.pageY;");
    println("  stalkerwidth = document.floater.clip.width;");
    println("  stalkerheight = document.floater.clip.height;");
    println("  if( (x > stalkerx && x < (stalkerx+stalkerwidth)) && (y > stalkery && y < (stalkery+stalkerheight))) return true;");
    println("  else return false;");
    println("}");
    println("function grabIt(e) {");
    println("  if(IE) {");
    println("     whichIt = event.srcElement;");
    println("     while (whichIt.id.indexOf('floater') == -1) {");
    println("       whichIt = whichIt.parentElement;");
    println("       if (whichIt == null) { return true; }");
    println("     }");
    println("     whichIt.style.pixelLeft = whichIt.offsetLeft;");
    println("     whichIt.style.pixelTop = whichIt.offsetTop;");
    println("     currentX = (event.clientX + document.body.scrollLeft);");
    println("     currentY = (event.clientY + document.body.scrollTop);");
    println("  }");
    println("  else {");
    println("    window.captureEvents(Event.MOUSEMOVE);");
    println("    if(checkFocus (e.pageX,e.pageY)) {");
    println("      whichIt = document.floater;");
    println("      StalkerTouchedX = e.pageX-document.floater.pageX;");
    println("      StalkerTouchedY = e.pageY-document.floater.pageY;");
    println("    }");
    println("  }");
    println("  return true;");
    println("}");
    println("function moveIt(e) {");
    println("  if (whichIt == null) { return false; }");
    println("  if(IE) {");
    println("    newX = (event.clientX + document.body.scrollLeft);");
    println("    newY = (event.clientY + document.body.scrollTop);");
    println("    distanceX = (newX - currentX);    distanceY = (newY - currentY);");
    println("    currentX = newX;    currentY = newY;");
    println("    whichIt.style.pixelLeft += distanceX;");
    println("    whichIt.style.pixelTop += distanceY;");
    println("    if(whichIt.style.pixelTop < document.body.scrollTop) whichIt.style.pixelTop = document.body.scrollTop;");
    println("    if(whichIt.style.pixelLeft < document.body.scrollLeft) whichIt.style.pixelLeft = document.body.scrollLeft;");
    println("    if(whichIt.style.pixelLeft > document.body.offsetWidth - document.body.scrollLeft - whichIt.style.pixelWidth - 20) whichIt.style.pixelLeft = document.body.offsetWidth - whichIt.style.pixelWidth - 20;");
    println("    if(whichIt.style.pixelTop > document.body.offsetHeight + document.body.scrollTop - whichIt.style.pixelHeight - 5) whichIt.style.pixelTop = document.body.offsetHeight + document.body.scrollTop - whichIt.style.pixelHeight - 5;");
    println("    event.returnValue = false;");
    println("  }");
    println("  else {");
    println("    whichIt.moveTo(e.pageX-StalkerTouchedX,e.pageY-StalkerTouchedY);");
    println("    if(whichIt.left < 0+self.pageXOffset) whichIt.left = 0+self.pageXOffset;");
    println("    if(whichIt.top < 0+self.pageYOffset) whichIt.top = 0+self.pageYOffset;");
    println("    if( (whichIt.left + whichIt.clip.width) >= (window.innerWidth+self.pageXOffset-17)) whichIt.left = ((window.innerWidth+self.pageXOffset)-whichIt.clip.width)-17;");
    println("    if( (whichIt.top + whichIt.clip.height) >= (window.innerHeight+self.pageYOffset-17)) whichIt.top = ((window.innerHeight+self.pageYOffset)-whichIt.clip.height)-17;");
    println("    return false;");
    println("  }");
      println("  return false;");
    println("  }");
    println("function dropIt() {");
    println("  whichIt = null;");
    println("  if(NS) window.releaseEvents (Event.MOUSEMOVE);");
    println("  return true;");
    println("}");
    //<!-- DRAG DROP CODE -->
    println("if(NS) {");
    println("  window.captureEvents(Event.MOUSEUP|Event.MOUSEDOWN);");
    println("  window.onmousedown = grabIt;");
    println("  window.onmousemove = moveIt;");
    println("  window.onmouseup = dropIt;");
    println("}");
    println("if(IE) {");
    println("  document.onmousedown = grabIt;");
    println("  document.onmousemove = moveIt;");
    println("  document.onmouseup = dropIt;");
    println("}");
    println("if(NS || IE) action = window.setInterval('heartBeat()',1);");
    println("</script>");
  }
  /**
   *
   */
  public void setAddFunc(String addFunc)
  {
    this.addFunc = addFunc;
  }
  public void setApproveFunc(String approveFunc)
  {
    this.approveFunc = approveFunc;
  }
  public void setCancelFunc(String cancelFunc)
  {
    this.cancelFunc = cancelFunc;
  }
  public void setSaveFunc(String saveFunc)
  {
    this.saveFunc = saveFunc;
  }
  public void setSearchFunc(String searchFunc)
  {
    this.searchFunc = searchFunc;
  }
  /**
   * 重载父类的方法
   */
  public int doStartTag() throws JspTagException
  {
    try
    {
      JspWriter out = pageContext.getOut();
      printDiv();
      out.println(buf.toString());
    }
    catch (IOException ioe)
    {
      System.out.println("jFloatBar: Error printing info:" + ioe);
    }
    return(SKIP_BODY);
  }
  public void setCustomButton(String customButton)
  {
    this.customButton = customButton;
  }
}