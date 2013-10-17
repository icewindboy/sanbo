package engine.html;

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
  protected static String transparent    = "../images/tree/transparent.gif";   //两个结点之间的线

  protected static String first_leaf_line     = "../images/tree/line_T.gif";  //叶子的正常线
  protected static String normal_leaf_line     = "../images/tree/line_T.gif";  //叶子的正常线
  protected static String last_leaf_line       = "../images/tree/line_L.gif";//本层树的最后叶子的线

  protected static String LAST_NODE_CLOSE_LINE = "../images/tree/clc.gif";   //本层树的最后结点的合上时的加号
  protected static String LAST_NODE_OPEN_LINE  = "../images/tree/clo.gif";   //本层树的最后结点的打开时的减号
  protected static String NODE_CLOSE_LINE      = "../images/tree/cc.gif";    //结点的合上时的加号
  protected static String NODE_OPEN_LINE       = "../images/tree/co.gif";    //结点的打开时的减号
  protected static String NODE_OPEN_ICON       = "../images/tree/fo.gif";    //结点的打开时的图标
  protected static String NODE_CLOSE_ICON      = "../images/tree/fc.gif";    //结点的关闭时的图标
  protected static String LEAF_ICON            = "../images/tree/fs.gif";    //叶子的图标

  protected String nodeClass           = "node";//树状列表的Table的Class（显示的style）

  protected String treeTableProp    = "depttreebox";//树状列表的Table的属性包括（class,style）
  protected String mouseUpFuction   = "nodeShowMenu";//鼠标
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