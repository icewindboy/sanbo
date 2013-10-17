package engine.dataset.sql;

/**
 * <p>Title: SQL语句表达式常量</p>
 * <p>Description: SQL语句表达式常量</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author ENGINE
 * @version 1.0
 */

public interface Expression extends java.io.Serializable
{
  //关系表达式
  /**
   * 大于
   */
  public static final String GREATER = ">";

  /**
   * 大于
   */
  public static final String GREATER_HTML = "&gt;";

  /**
   * 小于
   */
  public static final String LESS = "<";

  /**
   * 大于
   */
  public static final String LESS_HTML = "&lt;";

  /**
   * 等于
   */
  public static final String EQUALS = "=";

  /**
   * 大于等于
   */
  public static final String GREATER_EQUALS = ">=";

  /**
   * 大于等于
   */
  public static final String GREATER_EQUALS_HTML = "&gt;=";

  /**
   * 小于等于
   */
  public static final String LESS_EQUALS = "<=";

  /**
   * 小于等于
   */
  public static final String LESS_EQUALS_HTML = "&lt;=";

  /**
   * 不等于
   */
  public static final String NOT_EQUALS = "<>";

  /**
   * 不等于
   */
  public static final String NOT_EQUALS_HTML = "&lt;&gt;";

  /**
   * 相似于
   */
  public static final String LIKE = "like";

  /**
   * 不包含
   */
  public static final String NOT_LIKE = "not like";

  /**
   * 左边相似于
   */
  public static final String LEFT_LIKE = "left_like";

  /**
   * 右边相似于
   */
  public static final String RIGHT_LIKE = "right_like";

  /**
   * IN
   */
  public static final String IN = "in";

  //WHERE子句的连接符号
  /**
   * AND
   */
  public static final String AND = "AND";

  /**
   * OR
   */
  public static final String OR = "OR";
}