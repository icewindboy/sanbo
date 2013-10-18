package engine.report.util;

import engine.util.EngineRuntimeException;

/**
 * <p>Title: 报表运行异常</p>
 * <p>Description: 报表运行异常</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public class ReportException extends EngineRuntimeException
{
  public ReportException()
  {
    super();
  }

  public ReportException(String msg)
  {
    super(msg);
  }
}