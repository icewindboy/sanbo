package engine.util.calc;

import engine.util.EngineException;

/**
 * <p>Title: 非法的四则运算表达式异常</p>
 * <p>Description: 非法的四则运算表达式异常</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public class ExpressionException extends EngineException
{
  /**
   * 表达式中含有非法字符
   */
  public final static int INVALID_CHAR = 1;

  /**
   * 表达式中含有非法数字
   */
  public final static int INVALID_NUMBER = 2;

  /**
   * 表达式的数字太长了
   */
  public final static int NUMBER_TOO_LONG = 3;

  /**
   * 括号不匹配
   */
  public final static int PARENTHESIS_NOT_MATCH = 4;

  /**
   * 非法操作符
   */
  public final static int INVALID_OPERATE_SYMBOL = 5;

  /**
   * 除数为0的异常code
   */
  public final static int DIVISOR_IS_ZERO = 6;

  private int errorCode;

  public ExpressionException(int errorCode, String s){
    super(s);
    this.errorCode = errorCode;
  }

  public int getErrorCode(){
    return this.errorCode;
  }
}