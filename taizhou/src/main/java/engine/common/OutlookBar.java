package engine.common;

import java.util.*;
import engine.dataset.*;

import com.borland.dx.dataset.*;
/**
 * <p>Title: </p>
 * <p>Description: 打印出来以后的html：</p>
<DIV id=menulayer0Div class="menulayer">
  <DIV id=barlayer0Div class="barlayer">
    <TABLE border="0" cellPadding="0" cellSpacing="0" height="22" onclick="menubarpush(0)" width="120">
      <tr>
        <td nowrap class="treeMenuActive">&nbsp;人 力 资 源&nbsp;</td>
      </tr>
      <tr>
        <td height=2><img height=2 src='../images/solu_nav_line.gif' width='100%'></td>
      </tr>
      <TR>
    </TABLE>
  </DIV>
  <DIV id=iconlayer0Div class="iconlayer">
    <table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>
      <tr>
        <td nowrap class=treeMenuItem onClick=GotoNode('281','../person/credit_card.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;银行信用卡</td>
      </tr>
      <tr>
        <td nowrap class=treeMenuItem onClick=GotoNode('282','../person/employee_cardno.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;员工信用卡管理</td>
      </tr>
    </table>
  </DIV>
  <DIV id=uplayer0Div class="uplayer"><IMG height=16 width=16 src="" onmousedown="this.src='../image/scrollup2.gif';menuscrollup()"
     onmouseout="this.src='../image/scrollup.gif';menuscrollstop()" onmouseup="this.src='../image/scrollup.gif';menuscrollstop()">
  </DIV>
  <DIV id=downlayer0Div class="downlayer"><IMG height=16 width=16 src="" onmousedown="this.src='../image/scrolldown2.gif';menuscrolldown()"
    onmouseout="this.src='../image/scrolldown.gif';menuscrollstop()"onmouseup="this.src='../image/scrolldown.gif';menuscrollstop()">
  </DIV>
  <SCRIPT language='javascript'>
    menuIconWidth[0] = iconlayer0Div.scrollWidth + 0;
    menuIconHeight[0] = iconlayer0Div.scrollHeight + 0;
  </SCRIPT>
</DIV>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 **/

public final class OutlookBar implements java.io.Serializable
{
  public static String transparent    = "../images/tree/transparent.gif";   //两个结点之间的线

  public static String first_leaf_line     = "../images/tree/line_T.gif";  //叶子的正常线
  public static String normal_leaf_line     = "../images/tree/line_T.gif";  //叶子的正常线
  public static String last_leaf_line       = "../images/tree/line_L.gif";//本层树的最后叶子的线

  public static String LAST_NODE_CLOSE_LINE = "../images/tree/clc.gif";   //本层树的最后结点的合上时的加号
  public static String LAST_NODE_OPEN_LINE  = "../images/tree/clo.gif";   //本层树的最后结点的打开时的减号
  public static String NODE_CLOSE_LINE      = "../images/tree/cc.gif";    //结点的合上时的加号
  public static String NODE_OPEN_LINE       = "../images/tree/co.gif";    //结点的打开时的减号
  public static String NODE_OPEN_ICON       = "../images/tree/fo.gif";    //结点的打开时的图标
  public static String NODE_CLOSE_ICON      = "../images/tree/fc.gif";    //结点的关闭时的图标
  public static String LEAF_ICON            = "../images/tree/fs.gif";    //叶子的图标

  private StringBuffer bufHtml = new StringBuffer(4096);//保存

  public String ID_NAME          = "id";
  public String CODE_NAME        = "code";
  public String PARENT_NAME      = "parentid";
  public String CAPTION_NAME     = "name";

  private DataSet ds = null;
  private Vector vOtherInfo = null;
  //private String pathCode = null;

  //diff
  private int tabCount;  //计算tab的数量
  private String tabActiveClass = "treeMenuActive";
  private String tabNormalClass = "treeMenuNormal";
  /**
   * 添加字符到StringBuffer中
   * @param value 字符
   */
  private void print(String value)
  {
    bufHtml.append(value);
  }
  /**
   * 添加字符和回车符到StringBuffer中
   * @param value 字符
   */
  private void priln(String value)
  {
    bufHtml.append(value);
    bufHtml.append("\n");
  }

  /**
   * 创建HTML树状表格
   * @param ds 需要创建HTML树状表格数据集控件
   * @param rootCaption 树根结点的标题
   * @return 创建完毕的字符串
   */
  public void setTreeProperty(DataSet ds, String idColumnName,
                              String codeColumnName, String parentidColumnName, String captionColumnName)
  {
    setTreeProperty(ds, idColumnName, codeColumnName, parentidColumnName, captionColumnName, null);
  }

  /**
   * 创建HTML树状表格
   * @param ds 需要创建HTML树状表格数据集控件
   * @param rootCaption 树根结点的标题
   * @return 创建完毕的字符串
   */
  public void setTreeProperty(DataSet ds, String idColumnName,
                              String codeColumnName, String parentidColumnName, String captionColumnName,
                              Vector otherInfo)
  {
    this.ds = ds;
    this.ID_NAME      = idColumnName;
    this.CODE_NAME    = codeColumnName;
    this.PARENT_NAME  = parentidColumnName;
    this.CAPTION_NAME = captionColumnName;
    this.vOtherInfo   = otherInfo;
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
   * @return 创建完毕的字符串
   */
  public String printHtmlTree()
  {
    tabCount = 0;
    if(bufHtml.length() > 0)
      bufHtml.setLength(0);
    //其他结点
    ds.first();
    while(ds.inBounds())
    {
      String codename = getValue(CODE_NAME);
      priln("<DIV id='menulayer"+ tabCount +"Div' class='menulayer'>");
      int tabCountTemp = tabCount;
      createTree(codename, 0);
      printMiniScroll(tabCountTemp);
      priln("</DIV>");
    }
    //
    printInitScript();
    return bufHtml.toString();
  }


  /**
   * 创建各个树结点
   * @param fatherCode 父结点编号
   * @param level 层
   * @return 返回是否还有子结点
   */
  private boolean createTree(String fatherCode, int level)
  {
    String nodeid    = getValue(ID_NAME);
    String nodename  = getValue(CAPTION_NAME);
    String nodecode  = getValue(CODE_NAME);
    String parentid  = getValue(PARENT_NAME);
    String otherInfo = getOtherInfo();
    //boolean isExpand = isAllExpand ? true : (pathCode == null ? false : pathCode.startsWith(nodecode));
    boolean hasChild = false;//是否还有子结点
    boolean isLeaf = false;

    if(ds.next())
    {
      String nextCode = getValue(CODE_NAME);
      isLeaf = !nextCode.startsWith(nodecode);
      hasChild = nextCode.startsWith(fatherCode);
    }
    else
    {
      isLeaf = true;
      hasChild = false;
    }

/*
  <DIV id=barlayer0Div class="barlayer">
    <TABLE border='0' cellPadding='0' cellSpacing='0' height='22' onclick='menubarpush(0)' width='100%'>
      <tr>
        <td nowrap class='treeMenuActive'>&nbsp;人 力 资 源&nbsp;</td>
      </tr>
      <tr>
        <td height=2><img height=2 src='../images/solu_nav_line.gif' width='100%'></td>
      </tr>
      <TR>
    </TABLE>
  </DIV>
  <DIV id=iconlayer0Div class="iconlayer">
    <table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>
      <tr>
        <td nowrap class=treeMenuItem onClick=GotoNode('281','../person/credit_card.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;银行信用卡</td>
      </tr>
      <tr>
        <td nowrap class=treeMenuItem onClick=GotoNode('282','../person/employee_cardno.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;员工信用卡管理</td>
      </tr>
    </table>
  </DIV>
*/
    String nodeEvent = "";
    //是否是第一层菜单，即BAR
    if(level == 0)
    {
      priln("<DIV id='barlayer"+ tabCount +"Div' class='barlayer'>");
      print("<TABLE border='0' cellPadding='0' cellSpacing='0' height='22' onclick='menubarpush("+ tabCount +")' width='100%'>");
      print("<tr><td nowrap class='menuTabBar'>&nbsp;");
      print(nodename);
      print("&nbsp;</td></tr>");
      priln("</TABLE></DIV>");
      //
      priln("<DIV id='iconlayer"+ tabCount +"Div' class='iconlayer'>");
      priln("<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>");
      
//      priln("<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOver=treeMenuMouseOver()>");
      //打印子结点
      while(true)
      {
        boolean isHasChild = createTree(nodecode, level+1);
        if(!isHasChild)
          break;
      }
      priln("</table></DIV>");

      hasChild = ds.inBounds() && getValue(CODE_NAME).startsWith(fatherCode);
      //增加tab数量
      tabCount ++;
    }
    else if(isLeaf)
    {
      print("<tr><td nowrap class=treeMenuItem");
      if(level == 1 && nodename.equals("--"))
        print("><hr class='menuSperator'>");
      else
      {
        print(" onClick=GotoNode('" + nodeid +"','"+ parentid +"')>");
        for(int i=0; i<level-1; i++)
          print("&nbsp;");
        print("<img align='absmiddle' border=0 src='"+ LEAF_ICON +"'>&nbsp;");
        print(nodename);
      }
      priln("</td></tr>");
    }
    else
    {
      print("<tr><td nowrap class=treeMenuItem>");
      if(level == 1 && nodename.equals("--"))
        print("<hr class='menuSperator'>");
      else{
        for(int i=0; i<level-1; i++)
          print("&nbsp;");
        print(nodename);
        print("<img align='absmiddle' border=0 src='"+ LEAF_ICON +"'>&nbsp;");
      }
      priln("</td></tr>");
      //打印子结点
      while(true)
      {
        boolean isHasChild = createTree(nodecode, level+1);
        if(!isHasChild)
          break;
      }

      hasChild = ds.inBounds() && getValue(CODE_NAME).startsWith(fatherCode);
    }
    return hasChild;
    /*else if(isLeaf)
    {
      if(mouseUpFuction != null)
        nodeEvent +=" onMouseUp=\""+mouseUpFuction +"('"+nodeid+"', false,"+ (isLeaf?"false":"true") +")\"";
      if(mouseOutFuction !=null)
        nodeEvent +=" onMouseOut="+mouseOutFuction;
      if(mouseOverFuction !=null)
        nodeEvent +=" onMouseOver="+mouseOverFuction;
      if(nodeClickFuction !=null)
        nodeEvent +=" onClick="+nodeClickFuction+"('"+nodeid+"','"+ parentid +"')";

      print("<tr>");
      print("<td nowrap class="+ nodeClass +" width='100%'"+ nodeEvent +">");
      for(int i=0; i<level-1; i++)
        printTransparent();
      //printLeafLink();
      print("<img align='absmiddle' border=0 src='" +LEAF_ICON +"'>&nbsp;"+ nodename);
      print(otherInfo);
      print("</td></tr>");
    }
    else
    {
      if(mouseUpFuction != null)
        nodeEvent +=" onMouseUp=\""+mouseUpFuction +"('"+nodeid+"', false,"+ (isLeaf?"false":"true") +")\"";
      if(nodeClickFuction !=null)
        nodeEvent +=" onClick="+nodeClickFuction+"('"+nodeid+"','"+ parentid +"')";

      String showHide = "ShowHide(tr_context_"+ nodeid +",false,img_line_"+ nodeid +",img_icon_"+ nodeid +")";
      print("<tr id='tr_node_"+nodeid+"'>");
      print("<td nowrap class="+ nodeClass +"  width='100%'>");
      for(int i=0; i<level-1; i++)
        printTransparent();
      print("<img align='absmiddle' id='img_line_"+ nodeid +"' border=0 src='");
      print((isExpand ? NODE_OPEN_LINE : NODE_CLOSE_LINE) +"' onClick='"+ showHide +"'>");
      //print("<img align='absmiddle' id='img_icon_" +nodeid+ "' border=0 src='");
      //print((isExpand ? NODE_OPEN_ICON : NODE_CLOSE_ICON) +"' onClick='"+ showHide +"'>");
      print("<a id='link_"+nodeid+"' href='javascript:void(0)'"+ nodeEvent +">&nbsp;"+ nodename +"</a>");
      print(otherInfo);
      print("</td></tr>");
      //
      print("<tr id='tr_context_"+ nodeid +"' style='display:"+ (isExpand?"block":"none") +"'>");
      printTd();
      printTable();
      //打印子结点
      while(true)
      {
        boolean isHasChild = createTree(nodecode, level+1);
        if(!isHasChild)
          break;
      }
      print("</table></td></tr>");

      hasChild = ds.inBounds() && getValue(CODE_NAME).startsWith(fatherCode);
    }*/
  }

  //打印滚动条
  private void printMiniScroll(int num)
  {
    print("<DIV id='uplayer"+ num +"Div' class='uplayer'>");
    print("<IMG height=16 width=16 src='../images/scrollup.gif' onmousedown=\"this.src='../images/scrollup2.gif';menuscrollup()\" ");
    print("onmouseout=\"this.src='../images/scrollup.gif';menuscrollstop()\" ");
    priln("onmouseup=\"this.src='../images/scrollup.gif';menuscrollstop()\"></DIV>");
    //
    print("<DIV id='downlayer"+ num +"Div' class='downlayer'>");
    print("<IMG height=16 width=16 src='../images/scrolldown.gif' onmousedown=\"this.src='../images/scrolldown2.gif';menuscrolldown()\" ");
    print("onmouseout=\"this.src='../images/scrolldown.gif';menuscrollstop()\" ");
    priln("onmouseup=\"this.src='../images/scrolldown.gif';menuscrollstop()\"></DIV>");
    //
    //print("<SCRIPT language='javascript'>");
    //print("menuIconWidth["+ num +"] = iconlayer0Div.scrollWidth + " + num + "; ");
    //print("menuIconHeight["+ num +"] = iconlayer0Div.scrollHeight + " + num + "; ");
    //priln("</SCRIPT>");
  }


  /**
   * 打印OutLookBar 的初始化函数
   */
  private void printInitScript()
  {
    print("<SCRIPT language='javascript'>");
    print("function bodyOnLoad(){");
    print("init(22, "+ tabCount +", 'menulayer', 'iconlayer', 'barlayer', 'uplayer', 'downlayer', 'Div');");
    priln("}");
    priln("</SCRIPT>");
  }

  private void printTransparent(){print("<img align='absmiddle' border=0 src='"+ transparent +"'>");}

  private void printLeafLink(){print("<img align='absmiddle' border=0 src='"+ last_leaf_line +"'>");}
}
