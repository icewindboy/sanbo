package engine.common;

import engine.util.EngineException;

/**
 * <p>Title: 没有发现用户的异常</p>
 * <p>Description: 没有发现用户的异常</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author 江海岛
 * @version 1.0
 */
public class UserNotFoundException extends EngineException
{

  /**
   * 构造一个UserNotFoundException类
   */
  public UserNotFoundException()
  {
    super();
  }

  /**
   * 构造一个UserNotFoundException类，含有特定的信息.
   * @param msg 详细信息.
   */
  public UserNotFoundException( String msg )
  {
    super(msg);
  }

  /**
   * 构造一个UserNotFoundException类，含有特定的信息和nested <code>Throwable</code>.
   * @param msg 详细信息.
   * @param nested the exception or error that caused this exception
   *               to be thrown.
   */
  public UserNotFoundException(String msg, Throwable nested)
  {
    super(msg, nested);
  }
}