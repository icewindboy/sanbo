package engine.erp.chart;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.HashMap;

public class WebHitDataSet {

  protected ArrayList data = new ArrayList();

  public WebHitDataSet() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    //日期,类别,数量 共三个字段
    //在ArrayList里放入javaBean对象
    data.add(new WebHit(sdf.parse("2002-08-01"), "Catalog", 101923));
    data.add(new WebHit(sdf.parse("2002-08-02"), "Catalog", 113125));
    data.add(new WebHit(sdf.parse("2002-08-05"), "Catalog", 122148));
    data.add(new WebHit(sdf.parse("2002-08-06"), "Catalog", 117434));
    data.add(new WebHit(sdf.parse("2002-08-07"), "Catalog", 133256));
    data.add(new WebHit(sdf.parse("2002-08-08"), "Catalog", 157654));
    data.add(new WebHit(sdf.parse("2002-08-09"), "Catalog", 195356));
    data.add(new WebHit(sdf.parse("2002-08-12"), "Catalog", 122567));
    data.add(new WebHit(sdf.parse("2002-08-13"), "Catalog", 146343));
    data.add(new WebHit(sdf.parse("2002-08-14"), "Catalog", 184558));
    data.add(new WebHit(sdf.parse("2002-08-15"), "Catalog", 226524));
    data.add(new WebHit(sdf.parse("2002-08-16"), "Catalog", 235234));
    data.add(new WebHit(sdf.parse("2002-08-19"), "Catalog", 273442));
    data.add(new WebHit(sdf.parse("2002-08-20"), "Catalog", 253675));
    data.add(new WebHit(sdf.parse("2002-08-21"), "Catalog", 226434));
    data.add(new WebHit(sdf.parse("2002-08-22"), "Catalog", 236558));
    data.add(new WebHit(sdf.parse("2002-08-23"), "Catalog", 242655));
    data.add(new WebHit(sdf.parse("2002-08-26"), "Catalog", 232562));
    data.add(new WebHit(sdf.parse("2002-08-27"), "Catalog", 223226));
    data.add(new WebHit(sdf.parse("2002-08-28"), "Catalog", 252626));

    data.add(new WebHit(sdf.parse("2002-08-01"), "Checkout", 32355));
    data.add(new WebHit(sdf.parse("2002-08-02"), "Checkout", 28543));
    data.add(new WebHit(sdf.parse("2002-08-05"), "Checkout", 29665));
    data.add(new WebHit(sdf.parse("2002-08-06"), "Checkout", 34567));
    data.add(new WebHit(sdf.parse("2002-08-07"), "Checkout", 32453));
    data.add(new WebHit(sdf.parse("2002-08-08"), "Checkout", 29455));
    data.add(new WebHit(sdf.parse("2002-08-09"), "Checkout", 28558));
    data.add(new WebHit(sdf.parse("2002-08-12"), "Checkout", 31084));
    data.add(new WebHit(sdf.parse("2002-08-13"), "Checkout", 32568));
    data.add(new WebHit(sdf.parse("2002-08-14"), "Checkout", 33563));
    data.add(new WebHit(sdf.parse("2002-08-15"), "Checkout", 35675));
    data.add(new WebHit(sdf.parse("2002-08-16"), "Checkout", 37568));
    data.add(new WebHit(sdf.parse("2002-08-19"), "Checkout", 38764));
    data.add(new WebHit(sdf.parse("2002-08-20"), "Checkout", 35787));
    data.add(new WebHit(sdf.parse("2002-08-21"), "Checkout", 37865));
    data.add(new WebHit(sdf.parse("2002-08-22"), "Checkout", 39563));
    data.add(new WebHit(sdf.parse("2002-08-23"), "Checkout", 40291));
    data.add(new WebHit(sdf.parse("2002-08-26"), "Checkout", 39576));
    data.add(new WebHit(sdf.parse("2002-08-27"), "Checkout", 43623));
    data.add(new WebHit(sdf.parse("2002-08-28"), "Checkout", 41436));

    data.add(new WebHit(sdf.parse("2002-08-01"), "铜管", 45344));
    data.add(new WebHit(sdf.parse("2002-08-02"), "铜管", 43222));
    data.add(new WebHit(sdf.parse("2002-08-05"), "铜管", 44567));
    data.add(new WebHit(sdf.parse("2002-08-06"), "铜管", 46435));
    data.add(new WebHit(sdf.parse("2002-08-07"), "铜管", 42538));
    data.add(new WebHit(sdf.parse("2002-08-08"), "铜管", 39553));
    data.add(new WebHit(sdf.parse("2002-08-09"), "铜管", 44565));
    data.add(new WebHit(sdf.parse("2002-08-12"), "铜管", 46548));
    data.add(new WebHit(sdf.parse("2002-08-13"), "铜管", 55433));
    data.add(new WebHit(sdf.parse("2002-08-14"), "铜管", 58548));
    data.add(new WebHit(sdf.parse("2002-08-15"), "铜管", 45453));
    data.add(new WebHit(sdf.parse("2002-08-16"), "铜管", 34565));
    data.add(new WebHit(sdf.parse("2002-08-19"), "铜管", 56678));
    data.add(new WebHit(sdf.parse("2002-08-20"), "铜管", 54569));
    data.add(new WebHit(sdf.parse("2002-08-21"), "铜管", 56843));
    data.add(new WebHit(sdf.parse("2002-08-22"), "铜管", 43772));
    data.add(new WebHit(sdf.parse("2002-08-23"), "铜管", 32655));
    data.add(new WebHit(sdf.parse("2002-08-26"), "铜管", 39564));
    data.add(new WebHit(sdf.parse("2002-08-27"), "铜管", 37643));
    data.add(new WebHit(sdf.parse("2002-08-28"), "铜管", 34763));

    data.add(new WebHit(sdf.parse("2002-08-01"), "Service", 55437));
    data.add(new WebHit(sdf.parse("2002-08-02"), "Service", 55745));
    data.add(new WebHit(sdf.parse("2002-08-05"), "Service", 52523));
    data.add(new WebHit(sdf.parse("2002-08-06"), "Service", 48563));
    data.add(new WebHit(sdf.parse("2002-08-07"), "Service", 34675));
    data.add(new WebHit(sdf.parse("2002-08-08"), "Service", 29455));
    data.add(new WebHit(sdf.parse("2002-08-09"), "Service", 43678));
    data.add(new WebHit(sdf.parse("2002-08-12"), "Service", 64377));
    data.add(new WebHit(sdf.parse("2002-08-13"), "Service", 43677));
    data.add(new WebHit(sdf.parse("2002-08-14"), "Service", 37574));
    data.add(new WebHit(sdf.parse("2002-08-15"), "Service", 32645));
    data.add(new WebHit(sdf.parse("2002-08-16"), "Service", 35345));
    data.add(new WebHit(sdf.parse("2002-08-19"), "Service", 26785));
    data.add(new WebHit(sdf.parse("2002-08-20"), "Service", 24754));
    data.add(new WebHit(sdf.parse("2002-08-21"), "Service", 22467));
    data.add(new WebHit(sdf.parse("2002-08-22"), "Service", 18545));
    data.add(new WebHit(sdf.parse("2002-08-23"), "Service", 20567));
    data.add(new WebHit(sdf.parse("2002-08-26"), "Service", 19325));
    data.add(new WebHit(sdf.parse("2002-08-27"), "Service", 17343));
    data.add(new WebHit(sdf.parse("2002-08-28"), "Service", 18533));

  }

  /**
   * 根据类别名
   * @param filterSection String
   * @return ArrayList
   */
  public ArrayList getDataByHitDate(String filterSection) {
    ArrayList results = new ArrayList();
    HashMap dateMap = new HashMap();
    Iterator iter = this.data.listIterator();
    int currentPosition = 0;
    while (iter.hasNext()) {
      WebHit webHit = (WebHit) iter.next();

      if (filterSection == null ? true : filterSection.equals(webHit.getSection())) {
        //空或者相同
        Integer position = (Integer) dateMap.get(webHit.getHitDate());
        if (position == null) {
          //点击点的时间--->转成Integer
          results.add(webHit);
          dateMap.put(webHit.getHitDate(), new Integer(currentPosition));
          currentPosition++;
        }
        else {
          //有
          WebHit previousWebHit = (WebHit) results.get(position.intValue());
          previousWebHit.setHitCount(previousWebHit.getHitCount() +
                                     webHit.getHitCount());
        }
      }
    }
    return results;
  }

  /**
   * 点击的时间点
   * @param filterHitDate Date
   * @return ArrayList
   */
  public ArrayList getDataBySection(Date filterHitDate) {
    ArrayList results = new ArrayList();
    HashMap sectionMap = new HashMap();
    Iterator iter = this.data.listIterator();
    int currentPosition = 0;
    while (iter.hasNext()) {
      WebHit webHit = (WebHit) iter.next();
      if (filterHitDate == null ? true : filterHitDate.equals(webHit.getHitDate())) {
        Integer position = (Integer) sectionMap.get(webHit.getSection());
        if (position == null) {
          results.add(webHit);
          sectionMap.put(webHit.getSection(), new Integer(currentPosition));
          currentPosition++;
        }
        else {
          WebHit previousWebHit = (WebHit) results.get(position.intValue());
          previousWebHit.setHitCount(previousWebHit.getHitCount() +
                                     webHit.getHitCount());
        }
      }
    }
    return results;
  }



  /**
   * 返回时间ArrayList
   * @return ArrayList
   */
  public static ArrayList getDateList() {
    ArrayList dateList = new ArrayList();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    try {
      dateList.add(sdf.parse("2002-08-20"));
      dateList.add(sdf.parse("2002-08-19"));
      dateList.add(sdf.parse("2002-08-18"));
      dateList.add(sdf.parse("2002-08-17"));
      dateList.add(sdf.parse("2002-08-16"));
      dateList.add(sdf.parse("2002-08-15"));
      dateList.add(sdf.parse("2002-08-14"));
      dateList.add(sdf.parse("2002-08-13"));
      dateList.add(sdf.parse("2002-08-12"));
      dateList.add(sdf.parse("2002-08-11"));
      dateList.add(sdf.parse("2002-08-10"));
      dateList.add(sdf.parse("2002-08-09"));
      dateList.add(sdf.parse("2002-08-08"));
      dateList.add(sdf.parse("2002-07-07"));
      dateList.add(sdf.parse("2002-08-06"));
      dateList.add(sdf.parse("2002-08-05"));
      dateList.add(sdf.parse("2002-08-04"));
      dateList.add(sdf.parse("2002-08-03"));
      dateList.add(sdf.parse("2002-08-02"));
      dateList.add(sdf.parse("2002-08-01"));
    }
    catch (ParseException e) {
      // ignore
    }
    return dateList;
  }

  public ArrayList getSections() {
    ArrayList list = new ArrayList();
    list.add("Catalog");
    list.add("Checkout");
    list.add("铜管");
    list.add("Service");
    return list;
  }



}
