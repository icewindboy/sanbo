package engine.report.html;

import java.io.Serializable;
/**
 * <p>Title: HTML报表的助手</p>
 * <p>Description: HTML报表的助手</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public interface HtmlHelper extends Serializable
{
  /**
   * 得到总打印的行数
   * @return 打印的行数
   */
  public int getPrintRow();

  /**
   * 得到数据的最小的行数
   * @return 最小的行数
   */
  public int getMinRow();

  /**
   * 得到数据的最大的行数
   * @return 数据的最大的行数
   */
  public int getMaxRow();

  /**
   * 得到报表打印结果
   * @return 返回报表打印结果
   */
  public String toString();

  /**
   * 打印字符串
   * @param s 字符串
   */
  public void print(String s);

  /**
   * 打印HTML的table
   * @param table
   */
  public void printTable(Table table);

  /**
   * 是否需要重新计算计算
   * @return 返回是否需要重新计算计算
   */
  public boolean isCalculate();

  /**
   * 设置是否需要重新计算计算
   * @param isCalculate 是否需要重新计算计算
   */
  public void setIsCalculate(boolean isCalculate);

  /**
   * 是否打印空白表格
   * @return 返回是否打印空白表格
   */
  public boolean isPrintBlank();

  /**
   * 是否打印Excel
   * @return 返回是否打印Excel
   */
  public boolean isExcel();
}