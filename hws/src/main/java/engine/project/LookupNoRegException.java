package engine.project;

import engine.util.EngineRuntimeException;

/**
 * <p>Title: Lookup没有被注册得异常</p>
 * <p>Description: Lookup没有被注册得异常</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public final class LookupNoRegException extends EngineRuntimeException
{
  public LookupNoRegException(){
    super();
  }

  public LookupNoRegException(String msg){
    super(msg);
  }
}