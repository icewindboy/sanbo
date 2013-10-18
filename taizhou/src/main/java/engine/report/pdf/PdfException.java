package engine.report.pdf;

import engine.util.EngineRuntimeException;

/**
 * <p>Title: Ddf异常</p>
 * <p>Description: Ddf报表生成时的异常</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public class PdfException extends EngineRuntimeException
{
  public PdfException()
  {
    super();
  }

  public PdfException(String msg)
  {
    super(msg);
  }
}