package engine.dataset.sql;

/**
 * SQL符号（轻量级SQL分析器）
 * <p>Title: 轻量级SQL分析器</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */
interface SQLToken
{
  public static final int UNKNOWN     = 0;
  public static final int SELECT      = 1;
  public static final int FIELD       = 2;
  public static final int CONSTANT    = 3;
  public static final int STRING      = 4;
  public static final int FUNCTION    = 5;
  public static final int EXPRESSION  = 6;
  public static final int FROM        = 7;
  public static final int TABLE       = 8;
  public static final int WHERE       = 9;
  public static final int PARAMETER   = 10;
  public static final int COMMENT     = 11;
  public static final int OTHER       = 12;
  public static final int GROUP       = 13;
  public static final int HAVING      = 14;
  public static final int ORDER       = 15;
}

