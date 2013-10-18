package engine.erp.chart;

import java.util.Date;

/**
 *
 * <p>Title: </p>
 * <p>Description:javaBean 有时间,项目,数量三个字段 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class WebHit {

  protected Date hitDate = null;
  protected String section = null;
  protected long hitCount = 0;

  public WebHit(Date dHitDate, String sSection, long lHitCount) {
    this.hitDate = dHitDate;
    this.section = sSection;
    this.hitCount = lHitCount;
  }

  public Date getHitDate() {
    return this.hitDate;
  }

  public String getSection() {
    return this.section;
  }

  public long getHitCount() {
    return this.hitCount;
  }

  public void setHitDate(Date dHitDate) {
    this.hitDate = dHitDate;
  }

  public void setSection(String sSection) {
    this.section = sSection;
  }

  public void setHitCount(long lHitCount) {
    this.hitCount = lHitCount;
  }

}
