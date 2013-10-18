package engine.html;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author hukn
 * @version 1.0
 */

public final class HtmlPrintCellResponse implements java.io.Serializable
{
  public static final int PRINT_TITLE = 1;

  public static final int PRINT_BODY = 2;

  public static final int PRINT_BLANK = 3;

  private FieldInfo field = null;

  private StringBuffer cellHeader = null;

  private StringBuffer cellContent = null;
  //是否是打印表头网页
  private int printType = 1;

  boolean response = true;

  /**
   * 构造函数
   * @param fields 各个字段
   * @param cellHeaders 该行的各个td的头(如:&lt;td nowrap align=right)
   * @param cellContents 该行的各个td之间的信息
   */
  HtmlPrintCellResponse(FieldInfo field, StringBuffer cellHeader, StringBuffer cellContent, int printType)
  {
    this.field = field;
    this.cellHeader = cellHeader;
    this.cellContent = cellContent;
    this.printType = printType;
  }

  /**
   * 调用这个方法表示允许打印这一行
   */
  public final void add() { response = true; }

  /**
   * 调用这个方法表示跳过这一行
   */
  public final void skip() { response = false;}

  /**
   * 得到td的打印内容
   * @return 返回字段数组
   */
  public StringBuffer getCellContent()
  {
    return cellContent;
  }

  /**
   * 设置td的打印内容
   * @param content td的打印内容
   */
  public void setCellContent(StringBuffer content)
  {
    this.cellContent = content;
  }

  /**
   * 得到td的头
   * @return 返回td数组
   */
  public StringBuffer getCellHeader()
  {
    return cellHeader;
  }

  /**
   * 设置td的头
   * @param header td的打印内容
   */
  public void setCellHeader(StringBuffer header)
  {
    this.cellHeader = header;
  }

  /**
   * 得到各个td之间的信息
   * @return 返回td之间的信息数组
   */
  public FieldInfo getField()
  {
    return field;
  }

  /**
   * 是否是打印表格的title
   */
  public int getPrintType()
  {
    return printType;
  }
}