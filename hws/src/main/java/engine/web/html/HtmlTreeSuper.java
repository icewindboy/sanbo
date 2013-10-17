package engine.web.html;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author hukn
 * @version 1.0
 */

public abstract class HtmlTreeSuper implements java.io.Serializable
{
  protected static String transparent    = "../images/tree/transparent.gif";

  protected static String first_leaf_line     = "../images/tree/line_T.gif";
  protected static String normal_leaf_line     = "../images/tree/line_T.gif";
  protected static String last_leaf_line       = "../images/tree/line_L.gif";

  protected static String LAST_NODE_CLOSE_LINE = "../images/tree/clc.gif";
  protected static String LAST_NODE_OPEN_LINE  = "../images/tree/clo.gif";
  protected static String NODE_CLOSE_LINE      = "../images/tree/cc.gif";
  protected static String NODE_OPEN_LINE       = "../images/tree/co.gif";
  protected static String NODE_OPEN_ICON       = "../images/tree/fo.gif";
  protected static String NODE_CLOSE_ICON      = "../images/tree/fc.gif";
  protected static String LEAF_ICON            = "../images/tree/fs.gif";

  protected String nodeClass           = "node";

  protected String treeTableProp    = "depttreebox";
  protected String mouseUpFuction   = "nodeShowMenu";
  protected String mouseOutFuction  = "LeaveMenu(\"menu_dept\")";//鼠标
  protected String mouseOverFuction = null;//鼠标
  protected String nodeClickFuction = "GotoNode";//鼠标

  /**
   * 设置树结点击是的JavaScript函数名称
   * @param futionName JavaScript函数名称
   */
  public void setNodeClickFuction(String futionName)
  {
    nodeClickFuction = futionName;
  }

  /**
   * 设置鼠标离开结点时的JavaScript函数名称
   * @param futionName JavaScript函数名称
   */
  public void setMouseOutFuction(String futionName)
  {
    mouseOutFuction = futionName;
  }
  /**
   * 设置鼠标进入结点时的JavaScript函数名称
   * @param futionName JavaScript函数名称
   */
  public void setMouseOverFuction(String futionName)
  {
    mouseOverFuction = futionName;
  }
  /**
   * 设置鼠标右键的JavaScript函数名称
   * @param futionName JavaScript函数名称
   */
  public void setMouseUpFuction(String futionName)
  {
    mouseUpFuction = futionName;
  }

  /**
   * 设置树边框的风格
   * @param className 风格名称
   */
  public void setTreeTableClass(String className)
  {
    treeTableProp = className;
  }

  /**
   * 设置树节点的风格
   * @param className 风格名称
   */
  public void setNodeClass(String className)
  {
    nodeClass = className;
  }
}