package engine.web.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

import engine.web.lookup.LookupParam;
import engine.web.lookup.Lookup;
import engine.web.lookup.LookupFacade;
import engine.util.StringUtils;
/**
 * Title:        下拉框控件用来代替网页的select控件
 * Description:  下拉框控件用来代替网页的select控件
 * Copyright:    Copyright (c) 2002
 * Company:      JAC
 * @author hukn
 * @version 1.0
 */
public final class Select extends TagSupport
{
  private final static String DOWN_ARROW        = "../images/down_arrow.gif";
  private final static String KEY_OBJECT_NAME   = "keyObj";
  private final static String VALUE_OBJECT_NAME = "valueObj";

  private String id = null;
  private String name = null;
  private String keyValue = null;
  private String valueName = null;
  private String downImage = null;
  private String fixedDiv = null;
  private String floatDiv = null;
  private String objName = null;
  private String className = null;
  private String style = null;
  private String onSelect = null;
  private boolean isEnter2tab = false;
  private boolean isInput = true;
  private boolean isDisable = false;
  private boolean isNullOption = false;
  private boolean isCombox = false;
  private Lookup lookUpObj = null;
  private LookupParam[] lookupParam = null;
  
  private String contextPath="";


  public void setId( String newId ) {
    id = newId;
  }

  public String getId() {
    return id;
  }

  /**
   * 设置控件的名称
   * @param name 控件的名称
   */
  public void setName(String name)
  {
    this.name = name;
    this.fixedDiv = "d2_" + name;
    this.floatDiv = "d1_" + name;
    this.objName  = "o_" + name;
  }

  /**
   * 设置控件显示标题的名称
   * @param valueName 控件显示标题的名称
   */
  public void setValueName(String rvalueName)
  {
    this.valueName = rvalueName;
  }

  /**
   * 设置控件的值
   * @param value 控件的值
   */
  public void setValue(String value)
  {
    this.keyValue = value;
  }
  /**
   * 设置下拉框图片的路径
   * @param downImage 拉框图片的路径
   */
  public void setDownImage(String downImage)
  {
    this.downImage = downImage;
  }

  /**
   * 设置控件的class名称
   * @param className 控件的class名称
   */
  public void setClassName(String className)
  {
    this.className = className;
  }

  /**
   * 设置控件的class名称
   * @param className 控件的class名称
   */
  public void setStyle(String style)
  {
    this.style = style;
  }

  /**
   * 设置控件的onSelect事件
   * @param onSelect onSelect事件名称
   */
  public void setOnSelect(String onSelect)
  {
    this.onSelect = onSelect;
  }

  /**
   * 是否将回车转化为tab键
   * @param enter2tab 是否将回车转化为tab键 1:将回车转化为tab键
   */
  public void setEnter2tab(String enter2tab)
  {
    this.isEnter2tab = "1".equals(enter2tab);
  }

  /**
   * 是否不能用
   * @param input 是否不能用 1:不能用, 0:能用
   */
  public void setInput(String input)
  {
    this.isInput = "1".equals(input);
  }

  /**
   * 是否可以输入
   * @param input 是否可以输入 1:可以输入, 0:只能选择
   */
  public void setDisable(String disable)
  {
    this.isDisable = "1".equals(disable);
  }

  /**
   * 是否可以输入
   * @param input 是否可以输入 1:可以输入, 0:只能选择
   */
  public void setAddNull(String addNull)
  {
    this.isNullOption = "1".equals(addNull);
  }

  /**
   * 是否是:combox输入框，否:dbLookup
   * @param input 是否可以输入 1:combox, 0:dbLookup
   */
  public void setCombox(String combox){
    this.isCombox = "1".equals(combox);
  }

  /**
   * 设置lookup Bean 的 Key
   * @param lookupKey lookup Bean 的 Key
   */
  public void setLookup(String lookupKey){
    Object o = pageContext.getAttribute(lookupKey);
    this.lookUpObj = o != null && o instanceof Lookup ? (Lookup)o : null;
  }

  /**
   * 设置lookup对象参数
   * @param lookupParam storeid=1(表示列出storeid值等于1的选择框)
   */
  public void setLookupParam(String lookupParamStr){
    if(lookupParamStr == null){
      this.lookupParam = null;
      return;
    }
    String[][] params = StringUtils.getArrays(lookupParamStr);
    if(params == null || params.length == 0)
    {
      this.lookupParam = null;
      return;
    }

    String[] paramKeys   = params[0];
    String[] paramValues = params[1];

    this.lookupParam = new LookupParam[paramKeys.length];
    //
    for(int i=0; i<paramKeys.length; i++)
    {
      String[] values = paramValues[i]==null || paramValues[i].length() == 0 ?
                        null : StringUtils.parseString(paramValues[i], ",");
      this.lookupParam[i] = new LookupParam(paramKeys[i], values);
    }
  }

  /**
   * 标签的开始函数
   */
  public int doStartTag() throws JspTagException
  {

    try
    {
      JspWriter out = pageContext.getOut();
      StringBuffer buf = getStartTag();
      HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
      contextPath = request.getContextPath();
      out.println(buf);
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
      System.out.println("Select: Error doStartTag:" + ioe);
    }

    return EVAL_BODY_INCLUDE;
  }

  /**
   * 得到开始标签打印的字符串
   * @return 打印的字符串
   */
  public StringBuffer getStartTag() {
    return getStartTag(false);
  }

  /**
   * 得到开始标签打印的字符串
   * @param isInnerHTML 是否用于DOM的InnerHTM
   * @return 打印的字符串
   */
  public StringBuffer getStartTag(boolean isInnerHTML) {
    return getStartTag(null, isInnerHTML);
  }

  /**
   * 得到开始标签打印的字符串
   * @param buf 打印的StringBuffer
   * @return 打印的字符串
   */
  public StringBuffer getStartTag(StringBuffer buf) {
    return getStartTag(buf, false);
  }

  /**
   * 得到开始标签打印的字符串
   * @param buf 打印的StringBuffer
   * @param isInnerHTML 是否用于DOM的InnerHTM
   * @return 打印的字符串
   */
  public StringBuffer getStartTag(StringBuffer buf, boolean isInnerHTML)
  {
    if(buf == null)
      buf = new StringBuffer();

    String comma = isInnerHTML ? "\\\"" : "\"";
    //
    this.valueName = valueName == null || valueName.length()==0 ? "v_"+ name : valueName;
    this.downImage = downImage == null || downImage.length()==0 ? DOWN_ARROW : downImage;
    this.className = className == null || className.length()==0 ? "edbox" : className;
    this.style = style == null || style.length()==0 ? null : style;
    this.onSelect = onSelect == null || onSelect.length()==0 ? null : onSelect;

    buf.append("<DIV ID='");
    buf.append(fixedDiv).append("' class=").append(className);
    buf.append(" style='");
    if(!isInnerHTML)
      buf.append("width:5;");
    if(style != null)
      buf.append(style);
    buf.append("' onClick=ToggleSelect('").append(name).append("')>");
    buf.append("<table border=0 cellspacing=0 cellpadding=0><tr><td nowrap");
    if(style != null)
      buf.append(" width='100%'");

    buf.append("><INPUT CLASS='ednone' NAME='");
    buf.append(isCombox ? name : valueName).append("'");
    if(style != null)
      buf.append(" style='width:100%'");
    buf.append(" onKeyDown=").append(comma);
    buf.append("return SelectKeyDown('").append(name).append("');").append(comma);
    buf.append(" onKeyUp='SelectKeyUp(this.value);' onChange=").append(comma);
    buf.append("SelectChange('").append(name).append("');").append(comma);
    buf.append(isInput ? "" : "readonly").append(" value='");
    if(isCombox && keyValue!=null)
      buf.append(keyValue);
    buf.append("'>");
    buf.append("</td><td nowrap>");
    //
    buf.append("<IMG SRC='").append(downImage).append("' style='cursor:hand;' border=0>");
    buf.append("</td></tr></table>");
    buf.append("<INPUT TYPE=HIDDEN NAME='").append(isCombox ? valueName : name).append("'");
    buf.append(" link='").append(isCombox ? name : valueName).append("' value=''></DIV>");
    if(!isInnerHTML)
    {
      buf.append("\n");
      buf.append("<script language='javascript'>");
      printJavaScript(buf);
    }
    return buf;
  }

  /**
   * 打印javascript
   * @param buf buf
   */
  private void printJavaScript(StringBuffer buf)
  {
    //
    buf.append(objName).append("=new TSelectObject('").append(name).append("','");

    buf.append(isCombox ? valueName : name).append("','");
    buf.append(isCombox ? name : valueName).append("','").append(floatDiv).append("','").append(fixedDiv);

    buf.append("',").append(isEnter2tab ? "true" : "false").append(",");

    if(onSelect == null)
      buf.append("null");
    else
      buf.append("\"").append(onSelect).append("\"");

    buf.append(",").append(isCombox ? "false" : "true");
    buf.append(");");
    //
    buf.append("RegisterSelect(").append(objName).append(");");
    buf.append("SetDestSelectObject(").append(objName).append(");");
    if(isNullOption)
      buf.append("AddSelectItem('','');");
    if(isDisable)
      buf.append("DisableVar('").append(name).append("');");
  }

  /**
   * 结束打印标签
   * @return EVAL_PAGE
   * @throws JspException 异常
   */
  public int doEndTag() throws JspException {
    try{
      JspWriter out = pageContext.getOut();
      StringBuffer buf = getEndTag();
      out.println(buf);
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
      System.out.println("Select: Error doEndTag:" + ioe);
    }

    this.name = null;
    this.keyValue = null;
    this.valueName = null;
    this.downImage = null;
    this.fixedDiv = null;
    this.floatDiv = null;
    this.objName = null;
    this.isInput = true;
    this.className = null;
    this.style = null;
    this.onSelect = null;
    this.isEnter2tab = false;
    this.isDisable = false;
    this.isNullOption = false;
    this.isCombox = false;
    this.lookUpObj = null;
    this.lookupParam = null;



    return EVAL_PAGE;
  }

  /**
   * 得到结束标签打印的字符串
   * @return 打印的字符串
   */
  public StringBuffer getEndTag()
  {
    return getEndTag(false).append("</script>");
  }

  /**
   * 得到结束标签打印的字符串
   * @param isInnerHTML 是否用于DOM的InnerHTML
   * @return 打印的字符串
   */
  public StringBuffer getEndTag(boolean isInnerHTML)
  {
    StringBuffer buf = getEndTag(null, isInnerHTML);
    if(!isInnerHTML)
      buf.append("</script>");
    return buf;
  }

  /**
   * 得到结束标签打印的字符串
   * @param isInnerHTML 是否用于DOM的InnerHTML
   * @return 打印的字符串
   */
  public StringBuffer getEndTag(StringBuffer buf)
  {
    return getEndTag(buf, false).append("</script>");
  }

  /**
   * 得到结束标签打印的字符串
   * @param buf 打印的StringBuffer
   * @param isInnerHTML 是否用于DOM的InnerHTML
   * @return 打印的字符串
   */
  public StringBuffer getEndTag(StringBuffer buf, boolean isInnerHTML)
  {
    if(buf == null)
      buf = new StringBuffer();

    if(isInnerHTML)
      printJavaScript(buf);
    if(lookUpObj != null)
    {
      String[][] options = lookUpObj.getList(lookupParam);
      for(int i=0; i<options.length; i++)
      {
        String[] option = options[i];
        buf.append("AddSelectItem('").append(option[0]).
            append("','").append(option[1]).append("');");
      }
    }

    if(!isCombox && keyValue != null)
    {
      buf.append("SetSelectedIndex('");
      buf.append(keyValue);
      buf.append("');");
    }
    return buf;
  }

  /**
   * 释放资源
   */
  public void release() {
    this.name = null;
    this.keyValue = null;
    this.valueName = null;
    this.downImage = null;
    this.fixedDiv = null;
    this.floatDiv = null;
    this.objName = null;
    this.isInput = true;
    this.className = null;
    this.style = null;
    this.onSelect = null;
    this.isEnter2tab = false;
    this.isDisable = false;
    this.isNullOption = false;
    this.isCombox = false;
    this.lookUpObj = null;
    this.lookupParam = null;
  }
}