package engine.erp.baseinfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.jsp.PageContext;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.Locate;
import com.borland.dx.dataset.Variant;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.util.StringUtils;
/**
 * <p>Title: </p>
 * <p>Description: 打印出来以后的html：</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */
/*
<table CELLSPACING="0" CELLPADDING="0" BORDER="0" width="100%">
 <tr>
   <td nowrap class=node><a href="javascript:void(0)" onMouseUp="nodeShowMenu(0, true)" onMouseOut="LeaveMenu('menu_dept')">Engine ERP系统</td>
 </tr>
 <tr id='tr_node_1'>
   <td nowrap class=node><img id='img_line_1' border=0 src='../images/clc.gif' onClick='ShowHide(tr_context_1,false,img_line_1,img_icon_1)'><img id='img_icon_1' border=0 src='../images/fc.gif' onClick='ShowHide(tr_context_1,false,img_line_1,img_icon_1)'><a href='javascript:void(0)' onClick="GotoNode(1)">&nbsp;总论</a></td>
 </tr>
 <tr id='tr_context_1' style='display:none'>
   <td nowrap class=node>
     <table CELLSPACING="0" CELLPADDING="0" BORDER="0" width="100%">
       <tr id='tr_node_2'>
         <td nowrap class=node><IMG BORDER=0 SRC="../images/line.gif"><img id='img_line_2' border=0 src='../images/cc.gif' onClick='ShowHide(tr_context_2,false,img_line_2,img_icon_2)'><img id='img_icon_2' border=0 src='../images/fc.gif' onClick='ShowHide(tr_context_2,false,img_line_2,img_icon_2)'>
           <a href='javascript:void(0)'  onMouseUp='"+mouseUpFuction+"(0, true)' onMouseOut='"+mouseOutFuction+"'>" onclick="GotoNode(7);">&nbsp;CMM概论</a></td>
       </tr>
       <tr id='tr_context_2'>
         <td nowrap class=node>
           <table CELLSPACING="0" CELLPADDING="0" BORDER="0" width="100%">
             <tr>
               <td nowrap class=node><IMG BORDER=0 SRC="../images/line.gif"><IMG BORDER=0 SRC="../images/line.gif"><img border=0 src='../images/line_t.gif'><img border=0 src='../images/fs.gif'><a class='td' HREF="#" onclick="GotoNode(7);return false;">CMM的理论基础</a></td>
             </tr>
             <tr>
               <td nowrap class=node><IMG BORDER=0 SRC="../images/line.gif"><IMG BORDER=0 SRC="../images/line.gif"><img border=0 src='../images/line_b_c.gif'><img border=0 src='../images/fs.gif'><a class=td>CMM发展过程</a></td>
             </tr>
           </table>
         </td>
       </tr>
       <tr id='tr_node_3'>
         <td nowrap class=node><IMG BORDER=0 SRC="../images/line_b_c.gif"><img id='img_line_3' border=0 src='../images/clc.gif' onClick='ShowHide(tr_context_3,false,img_line_3,img_icon_3)'><img id='img_icon_3' border=0 src='../images/fc.gif' onClick='ShowHide(tr_context_3,false,img_line_3,img_icon_3)'>
           <a class='td' HREF="javascript:void(0)" onclick="GotoNode(7);">&nbsp;个体软件过程</a></td>
       </tr>
       <tr id='tr_context_3'>
         <td nowrap class=node>
           <table CELLSPACING="0" CELLPADDING="0" BORDER="0" width="100%">
             <tr>
               <td nowrap class=node><IMG BORDER=0 width="16" SRC="../images/transparent.gif"><IMG BORDER=0 width="16" SRC="../images/transparent.gif"><img border=0 src='../images/line_t.gif'><img border=0 src='../images/fs.gif'><a class='td' HREF="#" onclick="GotoNode(7);return false;">个体软件过程概述</a></td>
             </tr>
             <tr>
               <td nowrap class=node><IMG BORDER=0 width="16" SRC="../images/transparent.gif"><IMG BORDER=0 width="16" SRC="../images/transparent.gif"><img border=0 src='../images/line_b_c.gif'><img border=0 src='../images/fs.gif'><a class=td>个体软件过程框架</a></td>
             </tr>
           </table>
         </td>
       </tr>
     </table>
   </TD>
 </tr>
</TABLE>
*/
public class HtmlTree implements java.io.Serializable
{
  private static String transparent    = "../images/tree/transparent.gif";   //两个结点之间的线

  private static String first_leaf_line     = "../images/tree/line_T.gif";  //叶子的正常线
  private static String normal_leaf_line     = "../images/tree/line_T.gif";  //叶子的正常线
  private static String last_leaf_line       = "../images/tree/line_L.gif";//本层树的最后叶子的线

  private static String LAST_NODE_CLOSE_LINE = "../images/tree/clc.gif";   //本层树的最后结点的合上时的加号
  private static String LAST_NODE_OPEN_LINE  = "../images/tree/clo.gif";   //本层树的最后结点的打开时的减号
  private static String NODE_CLOSE_LINE      = "../images/tree/cc.gif";    //结点的合上时的加号
  private static String NODE_OPEN_LINE       = "../images/tree/co.gif";    //结点的打开时的减号
  private static String NODE_OPEN_ICON       = "../images/tree/fo.gif";    //结点的打开时的图标
  private static String NODE_CLOSE_ICON      = "../images/tree/fc.gif";    //结点的关闭时的图标
  private static String LEAF_ICON            = "../images/tree/fs.gif";    //叶子的图标

  //private static final int TYPE_RECUR  = 1; //递归算法定位子节点
  //private static final int TYPE_CODE   = 2; //按照编码的排列定位子节点

  private String nodeClass        = "node";//树状列表的Table的Class（显示的style）

  private String treeTableProp    = "depttreebox";//树状列表的Table的属性包括（class,style）
  private String mouseUpFuction   = "nodeShowMenu";//鼠标
  private String mouseOutFuction  = "LeaveMenu('menu_dept')";//鼠标
  private String nodeClickFuction = "GotoNode";//鼠标
  private String expandNodeFuction= "NodeExpand('?','?')";
  private String mouseDownFuction = "NodeMouseDown('?')";

  private transient  PrintWriter out = null;
  private transient  String      comma = null;
  private transient  boolean     isInnerHTML = false;
  //private FastStringBuffer bufHtml = new FastStringBuffer(2048);//保存

  private String prefix            = null;
  public  String nodeIdName        = "id";
  public  String nodeCodeName      = "code";
  public  String parentIdName      = "parentid";
  public  String nodeCaption       = "name";
  public  String rootCaption       = null;
  private boolean isRecur          = false;//是否用递归算法定位子节点
  private String childCountName    = null; //子节点数量的字段名称, null表示
  private String rootId            = null; //跟结点值

  private DataSet ds = null;
  private List   vOtherInfo = null;
  private String pathCode = null;
  private boolean isAllExpand = false;//是否将树全部展开
  //use recur
  private String[] pathList = null; //保存路径节点的各个ID

  public HtmlTree(){
    this.prefix = "";
  };

  public HtmlTree(String prefix){
    this.prefix = prefix == null ? "" : prefix;
  }
  /**
   * 添加字符到StringBuffer中
   * @param value 字符
   */
  private void print(String value)
  {
    out.print(value);
  }
  /**
   * 添加字符和回车符到StringBuffer中
   * @param value 字符
   */
  private void priln(String value)
  {
    if(isInnerHTML)
      out.print(value);
    else
      out.println(value);
    //bufHtml.append("\n");
  }

  /**
   * 打印树状表格的头
   */
  private void printTreeHead()
  {
    priln("<SCRIPT>");
    priln("function ShowHide(Layer, isLast, ImageNode, ImageIcon){");
    priln("if(Layer.style.display=='none'){");
    priln("  Layer.style.display='block';");
    priln("  ImageNode.src= isLast ? '"+ LAST_NODE_OPEN_LINE +"':'"+ NODE_OPEN_LINE +"';");
    priln("  ImageIcon.src= '"+ NODE_OPEN_ICON +"';");
    priln("}else{");
    priln("  Layer.style.display='none';");
    priln("  ImageNode.src= isLast ? '"+ LAST_NODE_CLOSE_LINE +"':'"+ NODE_CLOSE_LINE +"';");
    priln("  ImageIcon.src= '"+ NODE_CLOSE_ICON +"';");
    priln("}}");
    priln("</SCRIPT>");
    priln("<TABLE cellspacing=1 cellpadding=1 class="+ treeTableProp +" border=0><TR><TD valign=top>");
  }
  /**
   * 创建HTML树状表格
   * @param ds 需要创建HTML树状表格数据集控件
   * @param rootCaption 树根结点的标题
   * @return 创建完毕的字符串
   */
  public void setTreeProperty(DataSet ds, String rootCaption, String idColumnName,
                              String codeColumnName, String parentidColumnName, String captionColumnName)
  {
    setTreeProperty(ds, rootCaption, idColumnName, codeColumnName, parentidColumnName, captionColumnName, null);
  }
  /**
   * 创建HTML树状表格
   * @param ds 需要创建HTML树状表格数据集控件
   * @param rootCaption 树根结点的标题
   * @return 创建完毕的字符串
   */
  public void setTreeProperty(DataSet ds, String rootCaption, String idColumnName,
                              String codeColumnName, String parentidColumnName, String captionColumnName,
                              List otherInfo)
  {
    setTreeProperty(ds, rootCaption, idColumnName, codeColumnName, parentidColumnName, captionColumnName, otherInfo, false, null, null);
  }

  public void setTreeProperty(DataSet ds, String rootCaption, String idColumnName,
                              String codeColumnName, String parentidColumnName, String captionColumnName,
                              List otherInfo, boolean isRecur, String childCountColumn)
  {
    setTreeProperty(ds, rootCaption, idColumnName, codeColumnName, parentidColumnName, captionColumnName, otherInfo, isRecur, childCountColumn, null);
  }

  /**
   * 创建HTML树状表格
   * @param ds 需要创建HTML树状表格数据集控件
   * @param rootCaption 树根结点的标题
   * @return 创建完毕的字符串
   */
  public void setTreeProperty(DataSet ds, String rootCaption, String idColumnName,
                              String codeColumnName, String parentidColumnName, String captionColumnName,
                              List otherInfo, boolean isRecur, String childCountColumn, String rootId)
  {
    this.ds = ds;
    this.rootCaption = rootCaption;
    this.nodeIdName  = idColumnName;
    this.nodeCodeName= codeColumnName;
    this.parentIdName= parentidColumnName;
    this.nodeCaption = captionColumnName;
    this.vOtherInfo  = otherInfo;
    this.isRecur     = isRecur;
    this.childCountName = childCountColumn;
    //清空路径
    this.pathList    = null;
    this.rootId      = rootId == null ? "0" : rootId;
  }
  /**
   * 提取字段值
   * @param columnNeme 字段名称
   * @return 返回字段值
   */
  private String getValue(String columnNeme)
  {
    return EngineDataSet.getValue(ds, ds.getColumn(columnNeme).getOrdinal());
  }

  /**
   * 查找所有需要展开的节点的ID(用于递归算法)
   */
  private void lookUpPath()
  {
    if(pathCode == null)
      pathList = new String[0];
    else
      pathList = StringUtils.parseString(pathCode, "$");
/*
    pathList.add(pathCode);
    //层数
    int level = 0;
    EngineRow row = new EngineRow(ds, nodeIdName);
    row.setValue(0, pathCode);
    while(ds.locate(row, Locate.FIRST))
    {
      if(++level > 20)//最大20层
        break;
      String tempId = getValue(parentIdName);
      if(tempId.equals("0"))//表示到根节点了
        break;
      pathList.add(tempId);
      row.setValue(0, tempId);
    }*/
  }

  /**
   * 是否有子节点
   * @return 是否有子节点
   */
  private boolean hasChild()
  {
    long intenetRow = ds.getInternalRow();
    try{
      EngineRow row = new EngineRow(ds, parentIdName);
      row.setValue(0, getValue(nodeIdName));
      return ds.locate(row, Locate.FIRST);
    }
    finally{
      ds.goToInternalRow(intenetRow);
    }
  }

  /**
   * 字节点是否已经打开(用于递归算法)
   * @return 返回是否已经打开
   */
  private boolean childsHasOpen()
  {
    if(this.childCountName == null)
      return true;

    return hasChild();
    /*long intenetRow = ds.getInternalRow();
    try{
      EngineRow row = new EngineRow(ds, parentIdName);
      row.setValue(0, getValue(nodeIdName));
      return ds.locate(row, Locate.FIRST);
    }
    finally{
      ds.goToInternalRow(intenetRow);
    }*/
  }


  /**
   * 提取附加信息
   * @return 附加信息
   */
  private String getOtherInfo()
  {
    if(vOtherInfo == null)
      return "";
    else
      return String.valueOf(vOtherInfo.get(ds.getRow()));
  }

  /**
   * 创建HTML树状表格
   * @param allExpand 是否展开全部结点
   * @return 创建完毕的字符串
   */
  public String printHtmlTree(boolean allExpand)
  {
    this.isAllExpand = allExpand;
    this.pathCode = null;
    StringWriter aos = new StringWriter();
    this.out = new PrintWriter(aos, true);
    printHtmlTree();
    this.out.close();
    return aos.toString();
  }
  /**
   * 创建HTML树状表格
   * @param pathCode 展开树路径的编码
   * @return 创建完毕的字符串
   */
  public String printHtmlTree(String pathCode)
  {
    this.pathCode = (pathCode == null || pathCode.trim().equals(""))? null : pathCode.trim();
    this.isAllExpand = false;
    StringWriter aos = new StringWriter();
    this.out = new PrintWriter(aos, true);
    printHtmlTree();
    this.out.close();
    return aos.toString();
  }

  /**
   * 创建HTML树状表格
   * @param allExpand 是否展开全部结点
   * @return 创建完毕的字符串
   */
  public void printHtmlTree(PageContext pageContext, boolean allExpand)
  {
    this.isAllExpand = allExpand;
    this.pathCode = null;
    this.out = new PrintWriter(pageContext.getOut(), true);
    printHtmlTree();
    this.out.flush();
  }
  /**
   * 创建HTML树状表格
   * @param pathCode 展开树路径的编码
   * @return 创建完毕的字符串
   */
  public void printHtmlTree(PageContext pageContext, String pathCode)
  {
    this.pathCode = (pathCode == null || pathCode.trim().equals(""))? null : pathCode.trim();
    this.isAllExpand = false;
    this.out = new PrintWriter(pageContext.getOut(), true);
    printHtmlTree();
    this.out.flush();
  }
  /**
   * 创建HTML树状表格
   * @param pathCode 展开树路径的编码
   * @return 创建完毕的字符串
   */
  private void printHtmlTree()
  {
    isInnerHTML = false;
    this.comma = "\"";
    printTreeHead();
    printTable();
    //为空的话，不显示根结点
    if(rootCaption != null)
    {
      print("<tr>");
      printTd();
      print("<a class='link' href='javascript:void(0)'");
      if(mouseUpFuction != null)
        print(" onMouseUp='"+mouseUpFuction+"(0, true)'");
      if(mouseOutFuction != null)
        print(" onMouseOut="+mouseOutFuction);
      print(">");
      print(rootCaption);
      print("</td>");
      priln("</tr>");
    }
    //其他结点
    if(isRecur)//是否运用递归
    {
      lookUpPath();
      printTreeBody_Recur(rootId, 0, "");
    }
    else
    {
      ds.first();
      while(ds.inBounds())
      {
        String codename = getValue(nodeCodeName);
        printTreeBody(codename, 0);
      }
    }
    //
    priln("</table>");
    closeTag();
    //return bufHtml.toString();
  }

  public void printChildrenHTML(PageContext pageContext, String parentId, String pathCode)
  {
    this.isAllExpand = false;
    this.out = new PrintWriter(pageContext.getOut(), true);
    isInnerHTML = true;
    this.comma = "\\\"";
    StringTokenizer st = new StringTokenizer(pathCode, "$");
    int level = st.countTokens();
    if(isRecur)//是否运用递归
    {
      pathList = new String[0];
      //打印内容的table的开始
      printTable();
      //打印内容
      printTreeBody_Recur(parentId, level, pathCode);//1 至少是第2层
      //打印内容的table的结束
      priln("</table>");
    }
    else
      ;
    this.out.flush();
  }

  public boolean printNodeHTML(PageContext pageContext,
                               String  nodeId,
                               String  otherInfo,
                               String  pathCode){
    isInnerHTML = true;
    this.comma = "\\\"";
    //String parentId = null;
    String[] pathNodeids = StringUtils.parseString(pathCode, "$");
    int level = pathNodeids.length-1;
    boolean hasChild;
    if(isRecur){
      String parentId = pathNodeids.length == 1 ? "0_0" : pathNodeids[pathNodeids.length-2];
      EngineRow row = new EngineRow(ds, new String[]{nodeIdName, parentIdName});
      row.setValue(0, nodeId);
      row.setValue(1, parentId);
      if(!ds.locate(row, Locate.FIRST))
        return false;
      hasChild = childCountName == null ? hasChild() : ds.getBigDecimal(childCountName).intValue() > 0;
    }
    else{
      EngineRow row = new EngineRow(ds, new String[]{nodeIdName});
      row.setValue(0, nodeId);
      if(!ds.locate(row, Locate.FIRST))
        return false;

      String fatherCode = getValue(nodeCodeName);
      if(ds.next())
      {
        String nextCode = getValue(nodeCodeName);
        hasChild = nextCode.startsWith(fatherCode);
      }
      else
        hasChild = false;

      hasChild = childCountName == null ? hasChild() : ds.getBigDecimal(childCountName).intValue() > 0;
    }
    //
    this.out = new PrintWriter(pageContext.getOut(), true);
    String nodeName = getValue(nodeCaption);
    printNodeHTML(level, nodeId, nodeName, otherInfo, false, !hasChild, pathCode);
    this.out.flush();
    return true;
  }

  private void printTable() {priln("<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%'>");}

  private void printTd() {
    print("<td nowrap class='");
    print(nodeClass);
    print("'>");
  }

  private void printTd(String tdid) {
    print("<td nowrap class='");
    print(nodeClass);
    print("' id='");
    print(tdid);
    print("'>");
  }

  private void closeTag(){priln("</TD></TR></TABLE>");}

  private void printBetween(){
    print("<img align='absmiddle' border=0 src='");
    print(transparent);
    print("'>");
  }

  private void printLeafLink(){
    print("<img align='absmiddle' border=0 src='");
    print(last_leaf_line);
    print("'>");
  }

  /**
   * 创建各个树结点（用于递归）
   * @param fatherCode 父结点编号
   * @param level 层
   * @return 返回是否还有子结点
   */
  private void printTreeBody_Recur(String fatherID, int level, String fatherPath)
  {
    if(level > 20)//最大20层
      return;
    ds.first();
    int locate = Locate.FIRST;
    //用于查询子节点的row
    EngineRow parentRow = new EngineRow(ds, parentIdName);
    parentRow.setValue(0, fatherID);
    while(ds.locate(parentRow, locate))
    {
      locate = Locate.NEXT;
      String nodeid    = getValue(nodeIdName);
      String nodename  = getValue(nodeCaption);
      String parentid  = getValue(parentIdName);
      String otherInfo = getOtherInfo();
      boolean isExpand = isAllExpand ? true : level < pathList.length && pathList[level].equals(nodeid);
      
      //BigDecimal db = ds.getBigDecimal(childCountName);
      Column column = ds.getColumn(childCountName);
      
    //是否还有子结点
      boolean hasChild = false;
      if(column.getDataType()==Variant.DOUBLE)
      {
    	  Double childCountNameValue = ds.getDouble(childCountName);
    	  hasChild = childCountName == null ? hasChild() : childCountNameValue.intValue() > 0;
      }else if(column.getDataType()==Variant.BIGDECIMAL)
      {
    	  BigDecimal db = ds.getBigDecimal(childCountName);
    	  hasChild = childCountName == null ? hasChild() : db.intValue() > 0;
      }

      //
      String curPath = level == 0 ? nodeid :
                       new StringBuffer(fatherPath).append("$").append(nodeid).toString();
      printNode(level, nodeid, nodename, parentid, otherInfo, isExpand, !hasChild, curPath);
      //当前节点路径
      //打印子结点
      if(hasChild)
      {
        long row = ds.getInternalRow();
        printTreeBody_Recur(nodeid, level+1, curPath);
        ds.goToInternalRow(row);
        priln("</table></td></tr>");
      }
    }
  }
  /**
   * 创建各个树结点
   * @param fatherCode 父结点编号
   * @param level 层
   * @return 返回是否还有子结点
   */
  private boolean printTreeBody(String fatherCode, int level)
  {
    String nodeid    = getValue(nodeIdName);
    String nodename  = getValue(nodeCaption);
    String nodecode  = getValue(nodeCodeName);
    String parentid  = getValue(parentIdName);
    String otherInfo = getOtherInfo();
    boolean isExpand = isAllExpand ? true : (pathCode == null ? false : pathCode.startsWith(nodecode));
    boolean hasChild = false;//是否还有子结点
    boolean isLeaf = false;

    if(ds.next())
    {
      String nextCode = getValue(nodeCodeName);
      isLeaf = !nextCode.startsWith(nodecode);
      hasChild = nextCode.startsWith(fatherCode);
    }
    else
    {
      isLeaf = true;
      hasChild = false;
    }
    //
    printNode(level, nodeid, nodename, parentid, otherInfo, isExpand, isLeaf, null);
    //打印子结点
    if(!isLeaf)
    {
      while(true)
      {
        boolean isHasChild = printTreeBody(nodecode, level+1);
        if(!isHasChild)
          break;
      }
      priln("</table></td></tr>");

      hasChild = ds.inBounds() && getValue(nodeCodeName).startsWith(fatherCode);
    }
    return hasChild;
  }

  /**
   * 打印节点信息
   * @param level 层数
   * @param nodeid 节点ID
   * @param nodename 节点名称
   * @param parentid 父节点ID
   * @param otherInfo 其他信息
   * @param isExpand 是否展开
   * @param isLeaf 是否叶子
   */
  private void printNode(int     level,
                         String  nodeid,
                         String  nodename,
                         String  parentid,
                         String  otherInfo,
                         boolean isExpand,
                         boolean isLeaf,
                         String  path)
  {
    //String nodeEvent = getNodeEvent(nodeid, parentid, isLeaf, path, level);
    String postfix = isRecur ? path : nodeid;
    String tr_node = new StringBuffer(prefix).append("tr_node_").append(postfix).toString();
    String td_node = new StringBuffer(prefix).append("td_node_").append(postfix).toString();
    //打印<tr id='tr_node_3486_0'><td nowrap class='node' id='td_node_3486_0'>
    print("<tr id='"); print(tr_node); print("'>");
    print("<td nowrap class='"); print(nodeClass); print("' id='"); print(td_node); print("'>");
    printNodeHTML(level, nodeid, nodename, otherInfo, isExpand, isLeaf, path);//parentid
    priln("</td></tr>");
    //
    String tr_context = new StringBuffer(prefix).append("tr_cont_").append(postfix).toString();
    String td_context = new StringBuffer(prefix).append("td_cont_").append(postfix).toString();
    if(isLeaf){
      //打印叶子的隐藏tr,用于动态显示叶子是否有下节点
      print("<tr id='"); print(tr_context); print("' style='display:none'>");
      printTd(td_context);
      priln("</td></tr>");
    }
    else
    {
      //String curPath = level == 0 ? nodeid : (path + "," + nodeid);
      //String postfix = postfix(path, nodeid);
      print("<tr id='"); print(tr_context); print("' style='display:");
      print(isExpand?"block":"none"); print("'>");
      //
      printTd(td_context);
      printTable();
    }
  }

  private void printNodeHTML(int     level,
                             String  nodeid,
                             String  nodename,
                             String  otherInfo,
                             boolean isExpand,
                             boolean isLeaf,
                             String  path){

    String nodeEvent = getNodeEvent(nodeid, isLeaf, path, level); //parentid
    String postfix = isRecur ? path : nodeid;
    if(isLeaf){
      for(int i=0; i<level; i++)
        printBetween();
      printLeafLink();
      //打印图标
      print("<img align='absmiddle' border=0 src='"); print(LEAF_ICON); print("'><a id='link_");
      print(postfix); print("' href='javascript:void(0)'"); print(nodeEvent); print(">&nbsp;");
      print(nodename); print("</a>");
    }
    else
    {
      String tr_context = new StringBuffer(prefix).append("tr_cont_").append(postfix).toString();
      String img_line = new StringBuffer(prefix).append("img_line_").append(postfix).toString();
      String img_icon = new StringBuffer(prefix).append("img_icon_").append(postfix).toString();
      String link = new StringBuffer(prefix).append("link_").append(postfix).toString();
      //ShowHide(tr_context_, false, img_line_, img_icon)
      String showHide = (isRecur && !childsHasOpen())
                      ? StringUtils.combine(expandNodeFuction,"?",new String[]{nodeid, path})//path
                        : new StringBuffer("ShowHide(").append(tr_context).append(",false,").
                        append(img_line).append(",").append(img_icon).append(")").toString();
      for(int i=0; i<level; i++)
        printBetween();
      print("<img align='absmiddle' id='"); print(img_line); print("' border=0 src='");
      print(isExpand ? NODE_OPEN_LINE : NODE_CLOSE_LINE); print("' onClick="); print(showHide); print(">");
      print("<img align='absmiddle' id='"); print(img_icon); print("' border=0 src='");
      print(isExpand ? NODE_OPEN_ICON : NODE_CLOSE_ICON); print("' onClick="); print(showHide); print(">");
      //<a
      print("<a id='"); print(link); print("' href='javascript:void(0)'");
      print(nodeEvent); print(">&nbsp;"); print(nodename); print("</a>");
    }
    print(otherInfo);
  }

  /*private String postfix(String path, String nodeid){
    //level_nodeid , nodeid
    return isRecur ? new StringBuffer().append(path).append("_").append(nodeid).toString() : nodeid;
  }*/
  /**
   * 得到节点事件
   * @param nodeid 节点id
   * @param parentid 父节点ID
   * @param isLeaf 是否叶子节点
   * @param level 层数
   * @return 返回节点事件字符串
   */
  private String getNodeEvent(String nodeid, boolean isLeaf, String path, int level)
  {
    StringBuffer nodeEvent = new StringBuffer();//"";
    if(mouseDownFuction != null && isRecur)
      nodeEvent.append(" onMouseDown=").append(StringUtils.combine(mouseDownFuction, "?", new String[]{path}));
    // onMouseUp=mouseUpFuction('nodeid', false, isLeaf, this, 'path') OR
    // onMouseUp=mouseUpFuction('nodeid', isRoot, isNode, isFirst)
    if(mouseUpFuction != null)
    {
      nodeEvent.append(" onMouseUp=").append(comma).append(mouseUpFuction).append("('").append(nodeid);
      nodeEvent.append("', false,").append(isLeaf?"false":"true");
      nodeEvent.append(isRecur ? ",this" : level ==0 ? ",true" : ",false");
      nodeEvent.append(")").append(comma);
    }
    if(mouseOutFuction !=null)
      nodeEvent.append(" onMouseOut=").append(mouseOutFuction);
    if(nodeClickFuction !=null)
      nodeEvent.append(" onClick=").append(nodeClickFuction).append("('").append(nodeid).append("','')");//").append(parentid).append("

    return nodeEvent.toString();
  }
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

  /**
   * 设置展开树节点的函数名称
   * @param fuctionName 函数名称
   */
  public void setExpandNodeFuction(String fuctionName)
  {
    this.expandNodeFuction = fuctionName;
  }

  /**
   * 设置鼠标点击的函数名称
   */
  public void setMouseDownFuction(String functionName)
  {
    this.mouseDownFuction = functionName;
  }
    /*String nodeEvent = "";
    if(mouseUpFuction != null)
      nodeEvent +=" onMouseUp=\""+mouseUpFuction +"('"+nodeid+"', false,"+ (isLeaf?"false":"true") +")\"";
    if(mouseOutFuction !=null)
      nodeEvent +=" onMouseOut="+mouseOutFuction;
    if(nodeClickFuction !=null)
      nodeEvent +=" onClick="+nodeClickFuction+"('"+nodeid+"','"+ parentid +"')";
    if(isLeaf)
    {
      print("<tr>");
      printTd();
      for(int i=0; i<level; i++)
        printBetween();
      printLeafLink();
      print("<img align='absmiddle' border=0 src='" +LEAF_ICON +"'><a id='link_"+nodeid+"' href='javascript:void(0)'"+ nodeEvent +">&nbsp;"
            + nodename +"</a>");
      print(otherInfo);
      print("</td></tr>");
    }
    else
    {
      String showHide = "ShowHide(tr_context_"+ nodeid +",false,img_line_"+ nodeid +",img_icon_"+ nodeid +")";
      print("<tr id='tr_node_"+nodeid+"'>");
      print("<td nowrap class="+ nodeClass +">");
      for(int i=0; i<level; i++)
        printBetween();
      print("<img align='absmiddle' id='img_line_"+ nodeid +"' border=0 src='");
      print((isExpand ? NODE_OPEN_LINE : NODE_CLOSE_LINE) +"' onClick='"+ showHide +"'>");
      print("<img align='absmiddle' id='img_icon_" +nodeid+ "' border=0 src='");
      print((isExpand ? NODE_OPEN_ICON : NODE_CLOSE_ICON) +"' onClick='"+ showHide +"'>");
      print("<a id='link_"+nodeid+"' href='javascript:void(0)'"+ nodeEvent +">&nbsp;"+ nodename +"</a>");
      print(otherInfo);
      print("</td></tr>");
      //
      print("<tr id='tr_context_"+ nodeid +"' style='display:"+ (isExpand?"block":"none") +"'>");
      printTd();
      printTable();*/

}