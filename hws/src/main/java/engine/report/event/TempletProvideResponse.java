package engine.report.event;

import java.io.Serializable;

/**
 * <p>Title: report</p>
 * <p>Description: 报表工具</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public final class TempletProvideResponse implements Serializable {

  private boolean isInit = false;

  public void needInit(boolean isInit){
    this.isInit = isInit;
  }

  public boolean isNeedInit(){
    return this.isInit;
  }

}