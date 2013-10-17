package engine.common;

import java.io.Serializable;
import java.io.OutputStream;
import java.io.Writer;
import java.io.PrintWriter;
import engine.report.html.*;
/**
 * <p>Title: 默认HTML报表的助手</p>
 * <p>Description: 默认HTML报表的助手</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author 江海岛
 * @version 1.0
 */

public final class DefaultHtmlHelper implements HtmlHelper, java.io.Serializable
{
  //private StringBuffer buf = null;
  private PrintWriter writer = null;

  private int minRow = -1; //最小的行数
  private int maxRow = -1; //最大的行数

  private int printRow;//总打印的行数

  private boolean isCalculate = true;
  private boolean isNeedReprint = true; //是否需要重新打印
  private boolean isPrintBlank = false;//是否打印空白
  private boolean isExcel      = false;

  public DefaultHtmlHelper(){
  }

  /**
   * 设置输出流, 与setWriter是互坼的
   * @param os 输出流
   */
  public void setOutputStream(OutputStream os){
    if(os == null)
      throw new NullPointerException("the out must not null");
    writer = new PrintWriter(os, true);
  }

  /**
   * 设置Writer对象与setOutputStream是互坼的
   * @param out Writer对象
   */
  public void setWriter(Writer out){
    if(out == null)
      throw new NullPointerException("the out must not null");
    writer = new PrintWriter(out, true);
  }

  /**
   * 得到总打印的行数
   * @return 打印的行数
   */
  public int getPrintRow()
  {
    return this.printRow;
  }

  /**
   * 设置总打印的行数
   * @param printRow 打印的行数
   */
  public void setPrintRow(int printRow)
  {
    this.printRow = printRow;
  }

  /**
   * 得到数据的最小的行数
   * @return 最小的行数
   */
  public int getMinRow()
  {
    return this.minRow;
  }

  /**
   * 设置数据的最小的行数
   * @param minRow 最小的行数
   */
  public void setMinRow(int minRow)
  {
    this.isNeedReprint = this.minRow != minRow;
    this.minRow = minRow;
  }

  /**
   * 得到数据的最大的行数
   * @return 数据的最大的行数
   */
  public int getMaxRow()
  {
    return this.maxRow;
  }

  /**
   * 设置数据的最大的行数
   * @param maxRow 最大的行数
   */
  public void setMaxRow(int maxRow)
  {
    this.isNeedReprint = this.maxRow != maxRow;
    this.maxRow = maxRow;
  }

  /**
   * 复位打印的缓冲
   */
  public void needReprint()
  {
    //if(buf != null)
      //buf.setLength(0);
    this.isNeedReprint = true;
  }

  public void flush(){
    if(writer != null){
      writer.flush();
      writer = null;
    }
  }

  /**
   * 复位打印的缓冲
   */
  public void clear()
  {
    needReprint();
    flush();
    isCalculate = true;
    isNeedReprint = true;
    isPrintBlank = false;
    minRow = -1; //最小的行数
    maxRow = -1; //最大的行数
  }

  /**
   * 得到报表打印结果
   * @return 返回报表打印结果
   *
  public String toString()
  {
    return buf == null ? "" : buf.toString();
  }*/

  public void print(String s)
  {
    writer.print(s);
  }

  public void println()
  {
    writer.println();
  }

  public void println(String s)
  {
    writer.println(s);
  }

  public void printTable(Table table)
  {
    //table.toStringBuffer(getBuf());
    StringBuffer buf = table.toStringBuffer(null);
    writer.println(buf);
  }

  /*private StringBuffer getBuf()
  {
    if(buf == null)
      buf = new StringBuffer();
    return buf;
  }*/
  /**
   * 是否需要重新计算计算
   * @return 返回是否需要重新计算计算
   */
  public boolean isCalculate()
  {
    return this.isCalculate;
  }

  /**
   * 设置是否需要重新计算计算
   * @param isCalculate 是否需要重新计算计算
   */
  public void setIsCalculate(boolean isCalculate)
  {
    this.isCalculate = isCalculate;
  }

  /**
   * 是否需要重新打印
   * @return 返回是否需要重新打印
   */
  public boolean isNeedReprint()
  {
    return this.isNeedReprint;
  }

  /**
   * 是否打印空白表格
   * @return 返回是否打印空白表格
   */
  public boolean isPrintBlank() {
    return isPrintBlank;
  }

  /**
   * 设置是否打印空白表格
   * @param isPrintBlank 是否打印空白表格
   */
  public void setIsPrintBlank(boolean isPrintBlank) {
    this.isPrintBlank = isPrintBlank;
  }

  /**
   * 是否打印为Excel格式
   * @return 是否打印为Excel格式
   */
  public boolean isExcel() {
    return isExcel;
  }

  public void setIsExcel(boolean isExcel)
  {
    this.isExcel = isExcel;
  }
}
