package engine.web.lookup;

import engine.util.EngineRuntimeException;
/**
 * <p>Title: LookupHelper缓冲池异常</p>
 * <p>Description: LookupHelper缓冲池异常</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */
public class LookupHelperPoolException extends EngineRuntimeException
{
  public LookupHelperPoolException(){
    super();
  }

  public LookupHelperPoolException(String msg, Throwable nested){
    super(msg, nested);
  }

}