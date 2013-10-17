package engine.web.lookup;

import engine.util.EngineRuntimeException;

/**
 * <p>Title: Lookup没有发现的异常</p>
 * <p>Description: 配置文件没有配置改lookup信息, 或修改了配置文件但没有装载进来</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public final class LookupNotFoundException extends EngineRuntimeException
{
  public LookupNotFoundException(){
    super();
  }

  public LookupNotFoundException(String msg){
    super(msg);
  }
}
