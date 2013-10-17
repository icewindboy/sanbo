package engine.web.lookup;

import engine.util.EngineRuntimeException;

/**
 * <p>Title: 非法Lookup注册参数的异常</p>
 * <p>Description: 非法Lookup注册参数的异常</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
*/

public class LookupInvalidArgumentException extends EngineRuntimeException {
  public LookupInvalidArgumentException(){
    super();
  }

  public LookupInvalidArgumentException(String msg){
    super(msg);
  }
}