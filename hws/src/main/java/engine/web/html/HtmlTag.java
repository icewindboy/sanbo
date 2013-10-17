package engine.web.html;

/**
 * Title:        模板自定义的标签
 * Description:  模板自定义的标签<br>
 * Copyright:    Copyright (c) 2001
 * Company:      ENGINE
 * @author hukn
 * @version 1.0
 */

public interface HtmlTag extends java.io.Serializable
{
  //HTML标签
  /**
   * HTML标签的title
   */
  public static final String TITLE   = "title";

  /**
   * HTML标签的form
   */
  public static final String FORM   = "form";

  /**
   * HTML标签的link
   */
  public static final String LINK =  "link";

  /**
   * HTML标签的table
   */
  public static final String TABLE   = "table";

  /**
   * HTML标签的tr
   */
  public static final String TR      = "tr";

  /**
   * HTML标签的td
   */
  public static final String TD      = "td";

  /**
   * HTML标签的br
   */
  public static final String BR      = "br";

  /**
   * HTML标签的iframe标签
   */
  public static final String IFRAME  = "iframe";
  /**
   * HTML标签属性的ID
   */
  public static final String ID      = "id";

  /**
   * HTML标签属性的border
   */
  public static final String BORDER  = "border";

  /**
   * HTML标签属性的align
   */
  public static final String ALIGN   = "align";

  /**
   * 横向对齐的左对齐
   */
  public static final String ALIGN_LEFT = "left";

  /**
   * 横向对齐的居中
   */
  public static final String ALIGN_CENTER = "center";

  /**
   * 横向对齐的居右
   */
  public static final String ALIGN_RIGHT = "right";

  /**
   * HTML标签属性的valign
   */
  public static final String VALIGN  = "valign";

  /**
   * HTML标签属性的valign
   */
  public static final String VALIGN_MIDDLE  = "middle";

  /**
   * HTML标签属性的cellspacing
   */
  public static final String CELLSPACING   = "cellspacing";

  /**
   * HTML标签属性的cellpadding
   */
  public static final String CELLPADDING   = "cellpadding";

  /**
   * HTML标签属性的colspan
   */
  public static final String COLSPAN = "colspan";

  /**
   * HTML标签属性的rowspan
   */
  public static final String ROWSPAN = "rowspan";

  /**
   * HTML标签属性的class
   */
  public static final String CLASS = "class";

  /**
   * HTML标签属性的style
   */
  public static final String STYLE = "style";

  /**
   * HTML标签属性的onclick
   */
  public static final String ONCLICK = "onclick";

  /**
   * HTML标签属性的ondblclick
   */
  public static final String ONDBLCLICK = "ondblclick";

  /**
   * HTML标签属性的nbsp
   */
  public static final String NBSP = "&nbsp;";

  //自定义的标签
  //page 有关
  /**
   * 页面大小
   */
  public static final String PAGE    = "page";


  /**
   * 页面宽度
   */
  public static final String PAGE_WIDTH    = "width";

  /**
   * 页面高度
   */
  public static final String PAGE_HEIGHT   = "height";

  /**
   * 页面的左边距
   */
  public static final String MAGIN_LEFT    = "magin-left";

  /**
   * 页面的右边距
   */
  public static final String MAGIN_RIGHT   = "magin-right";

  /**
   * 页面的上边距
   */
  public static final String MAGIN_TOP     = "magin-top";

  /**
   * 页面的下边距
   */
  public static final String MAGIN_BOTTOM  = "magin-bottom";

  /**
   * 页脚左边
   */
  public static final String FOOTER_LEFT   = "footer-left";

  /**
   * 页脚中间
   */
  public static final String FOOTER_CENTER = "footer-center";

  /**
   * 页脚右边
   */
  public static final String FOOTER_RIGHT  = "footer-right";

  /**
   * 查询对应form的名称
   */
  public static final String QUERY_FORM   = "queryform";

  /**
   * 查询对应table的名称
   */
  public static final String QUERY_TABLE   = "querytable";

  /**
   * 默认的单元格高度
   */
  public static final String CELLHEIGHT    = "cellheight";

  /**
   * 是否直接显示打印对话框
   */
  public static final String DIALOG        = "dialog";

  //table有关
  /**
   * 锁定表格的类型：dynamic,static,summary
   */
  public static final String TYPE    = "type";           //

  /**
   * 表头的行数
   */
  public static final String HEADER  = "header";

  /**
   * 不到一页是否填空填满一页
   */
  public static final String FILLNULL= "fillnull";

  /**
   * 表格的偏移量
   */
  public static final String OFFSET  = "offset";

  /**
   * 是否不可用的
   */
  public static final String DISABLE = "disable";

  /**
   * 单元格字体大小
   */
  public static final String FONT_SIZE     = "font-size";

  /**
   * 单元格字体类型
   */
  public static final String FONT_FACE     = "font-face";

  /**
   * 单元格字体是否加粗
   */
  public static final String FONT_BOLD     = "font-bold";

  /**
   * 单元格左边界宽度
   */
  public static final String BORDER_LEFT   = "border-left";

  /**
   * 单元格右边界宽度
   */
  public static final String BORDER_RIGHT  = "border-right";

  /**
   * 单元格上边界宽度
   */
  public static final String BORDER_TOP    = "border-top";

  /**
   * 单元格下边界宽度
   */
  public static final String BORDER_BOTTOM = "border-bottom";

  /**
   * 各个列的宽度比列
   */
  public static final String WIDTHS  = "widths";

  /**
   * 单元格的高度
   */
  public static final String HEIGHT  = "height";

  /**
   * 与数据有关,表的主键字段
   */
  public static final String KEY    = "key";           //提供数据的标签

  /**
   * 与数据有关,默认字段标签
   */
  public static final String DEFAULT_FIELDS = "defaultfields"; //默认字段的标签

  /**
   * 提供数据的数据集的名称
   */
  public static final String DATASET = "dataset";

  /**
   * 字段名称
   */
  public static final String FIELD   = "field";

  /**
   * 字段显示的标题
   */
  public static final String CAPTION = "caption";

  /**
   * 是否分组:false/true
   */
  public static final String GROUP   = "group";

  /**
   * 小计或合计的字段
   */
  public static final String SUM     = "sum";

  /**
   * 字段是否显示
   */
  public static final String SHOW    = "show";

  /**
   * 包含其他的字段可用逗号分割多个字段
   */
  public static final String INCLUDE = "include";

  /**
   * 是否是普通的字段。不能用于高级查询
   */
  public static final String GENERAL = "general";

  /**
   * 作查询条件用（用where子句）
   */
  public static final String WHERE   = "where";

  /**
   * 数据类型：varchar,number,date,time,datetime
   */
  public static final String DATA_TYPE    = "datatype";

  /**
   * 外键关联的表
   */
  public static final String LINK_TABLE   = "linktable";

  /**
   * 外键字段名
   */
  public static final String LINK_COLUMN  = "linkcolumn";  //

  /**
   * 用于显示的字段名
   */
  public static final String QUERY_COLUMN = "querycolumn"; //

  /**
   * field name 的扩展名
   */
  public static final String EXTEND_NAME  = "extendname";  //

  /**
   * 查询的操作符号 &gt;, &lt; , =, &gt;=, &lt;=, &lt;&gt;, LIKE, IN
   */
  public static final String OPER_SIGN    = "opersign";    //

  /**
   * lookupBean的key名称
   */
  public static final String LOOKUP       = "lookup";      //

  /**
   * lookup类型的输入框要调用的javasript函数
   */
  public static final String SCRIPT       = "script";      //

  /**
   * /查询条件是否需要强制输入
   */
  public static final String NEED         = "need";

  /**
   * 查询条件初始化的值
   */
  public static final String INIT_VALUE   = "initvalue";

  /**
   * 是否另起一行
   */
  public static final String SPAN    = "span";

  /**
   * 数据集对象或SQL语句
   */
  public static final String OBJECT  = "object";

  /**
   * 初始化参数值
   */
  public static final String PARAM   = "param";

  /**
   * 名称
   */
  public static final String NAME    = "name";            //

  /**
   * 标签的属性(指值)
   */
  public static final String VALUE   = "value";           //

  /**
   * 数字的格式化
   */
  public static final String FORMAT  = "format";          //

  /**
   * 金额的中文大学格式
   */
  public static final String CHINESEFORMAT  = "chineseformat"; //

  /**
   * 数值为0是的打印格式
   */
  public static final String ZEROFORMAT  = "zeroformat";  //

  /**
   * 最大行数,只对动态表有效
   */
  public static final String MAXROW  = "maxrow";  //

  /**
   * 是否可以不折行的
   */
  public static final String NOWRAP  = "nowrap";

  //标签的特定值
  /**
   * 动态表格
   */
  public static final String TYPE_DYNAMIC  = "dynamic";  //

  /**
   * 静态表格
   */
  public static final String TYPE_STATIC   = "static";   //

  /**
   * 每页的总结表格
   */
  public static final String TYPE_SUMMARY  = "summary";  //

  /**
   * 每页的小计
   */
  public static final String TYPE_SUBTOTAL = "subtotal";

  /**
   * 每页的合计
   */
  public static final String TYPE_TOTAL = "total";

  /**
   * 模板打印
   */
  public static final String TYPE_TEMPLET  = "templet";

  /**
   * 套打
   */
  public static final String TYPE_PRECISION= "precision";//

  /**
   * true
   */
  public static final String TRUE          = "true";     //

  /**
   * false
   */
  public static final String FLASE         = "false";    //

  /**
   * DataSet type : sql
   */
  public static final String SQL          = "sql";     //

  /**
   * DataSet type : procedure
   */
  public static final String PROCEDURE    = "procedure";    //

  /**
   * DataSet type : custom
   */
  public static final String CUSTOM       = "custom";    //

  /**
   * Table type : head
   */
  public static final String HEAD       = "head";    //

  /**
   * Table type : body
   */
  public static final String BODY       = "body";    //

  /**
   * Table type : foot
   */
  public static final String FOOT       = "foot";    //

  /**
   * Y坐标轴属性
   */
  public static final String Y          = "y";

  /**
   * X坐标轴属性
   */
  public static final String X          = "x";

  /**
   * 套打表体行的间隙, 单位(cm)
   */
  public static final String SPACE     = "space";

  /**
   * 最大长度属性
   */
  public static final String MAXLENGTH  = "maxlength";

  /**
   *
   */
  public static final String MULTIROW   = "multirow";
}
