package engine.erp.store;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.project.*;
import engine.html.*;
import engine.common.*;
import java.math.BigDecimal;

import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.dataset.EngineDataSet;
import engine.report.event.ReportDataLoadedListener;
import engine.report.event.TempletAfterProvideListener;
import engine.report.event.TempletProvideResponse;
import engine.report.util.ReportData;
import java.util.ArrayList;
import engine.erp.store.B_SaleOutStore;
import engine.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.Hashtable;

/**
 * <p>Title:  销售出库单套打</p>
 * <p>Description: 销售出库单套打</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author yjg
 * @version 1.0
 */

public class B_SaleOutStoreBillPrint implements TempletAfterProvideListener
{
  private static final String PRINT_MASTER_SQL       = " SELECT * from VW_OUTPUTLIST_MST_BILL_WRAPPER ";//主
  private static final String PRINT_DETAIL_SQL       = " SELECT * from VW_OUTPUTLIST_DTL_BILL_WRAPPER ";//从

  private RowMap[] masterRows;//套打主表
  private ArrayList drows=null;//套打从表
  private ArrayList mprint;
  private ArrayList dprint;
  public  String widthSyskey = "";
  public  String lengthSyskey = "";

    /**
     * 分割字符串s,用sep分割成一个字符串数组.并保存到HashTable中
     * @para s 源串
     * @para sep 分割符
     * @para isGetLength 解析出长度.true则解析长度,false则解析宽度
     * @return 返回字符串，是Hash表中key为field的值
     */
    public final String parseEspecialString(String s, String sep, boolean isGetLength)
    {
      //保存返回的串值.
      String returnS = "";
      if(s==null || s.equals(""))
        return "0";
      String[] code = StringUtils.parseString(s, sep);
      //宽度的键及值.
      String key=null, value = null;
      //长度的键及值.
      //String lengthKey=null, lengthValue = null;
      //取宽度
      int j = 0; //值在被分割出来的数组中的index位置.在这处始终key的index>value的index:
      for(int i=0; i<code.length; i++)
      {
        if(i%2 > 0){
          value = code[i];
          j = i;//保存住上一个key的value的index
        }
        else{
          key = code[i].trim();
        }
        //任何情况下key的index>value的index.如相反则说明现在还只有key而没有value,那么回去找紧接着的value.
        //如果j<i则说当前的value是上一个key的value
        if ( j<i ) continue;
        if(value==null)
          continue;
        if(key.equals(isGetLength?lengthSyskey:widthSyskey))
         {
          return value;
         }
      }
      return "";
    }
  /**
   * 报表模板打开后的事件调用的方法
   * @param request WEB请求
   * @param templet 报表模板对象
   * @param context 报表上下文对象
   * @param response 模板装载的响应对象
   */
  public void templetAfterProvide(ServletRequest request, TempletData templet, ContextData context, TempletProvideResponse response)
  {
    //引用LoginBean
    HttpServletRequest req = (HttpServletRequest)request;
    engine.common.LoginBean loginBean = engine.common.LoginBean.getInstance(req);
    this.widthSyskey = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");
    this.lengthSyskey = loginBean.getSystemParam("SYS_PRODUCT_SPEC_LENGTH");

    EngineDataSet dsBillWrapperMaster      = new EngineDataSet();//2004-4-2 21:23 新增 专门为套打的主表数据集.
    EngineDataSet dsBillWrapperDetail      = new EngineDataSet();//2004-4-2 21:23 新增 专门为套打的从表数据集.

    dsBillWrapperMaster.setProvider(context.getDataSetProvider());
    dsBillWrapperDetail.setProvider(context.getDataSetProvider());
    String sfdjid = req.getParameter("sfdjid");

      if(sfdjid==null)
        sfdjid="";
      ArrayList mastListKey = new ArrayList();
      ArrayList mastListValue = new ArrayList();
      //累计总共打印了几页
      //int totalPageNos = 0;
      //当前打印的是第几页
      //int cureentPageNo = 0;
      /*
      1.mrows中保存的是从sql语句中查询出来原始的主要是由cpbm,dmsxid分组的记录.
      2.但是可能会有cpbm, 长度*宽度 也相同的记录.因为是用cpbm,dmsxid分组,而不是cpbm ,sxz分组
      3.而打印的本意是:cpbm,sxz=长度*宽度相同的统一归到一笔主表记录下面.所以,主表中记录要求:
        3.1 cpbm,sxz=长度*宽度组合的数据在主表数据集中不允许有相同的
      4.因此,下面的操作就是:除去主表mrows数据集中cpbm,sxz=长度*宽度组合有多笔相同的记录.
        保持mrows数据中cpbm,sxz=长度*宽度组合没有相同的
      */
      //主表是直接产生RowMap数组
      dsBillWrapperMaster.setQueryString(PRINT_MASTER_SQL+" where sfdjid='"+sfdjid+"'");
      if(dsBillWrapperMaster.isOpen())
        dsBillWrapperMaster.refresh();
      else
        dsBillWrapperMaster.openDataSet();
      dsBillWrapperMaster.first();
      for(int i=0;i<dsBillWrapperMaster.getRowCount();i++)
      {
        String cpbm = dsBillWrapperMaster.getValue("cpbm");
        String sxz = dsBillWrapperMaster.getValue("sxz");
        String ckdgs = dsBillWrapperMaster.getValue("ckdgs");
        String width = null;
        //如果是平张纸品那么它的打印出来的规格要从全从规格属性里取
        if (ckdgs.equals("3"))
        {
          width = parseEspecialString(sxz, "()", true);//取长度
          width = width + "*" + parseEspecialString(sxz, "()", false);//取宽度
          //如果是平张纸的话那么,规格的格式直接就是规格属性里取到的:长度*宽度
        }
        else
          width = dsBillWrapperMaster.getValue("gg") + parseEspecialString(sxz,"()", false);

        String allName = cpbm + "$" + width;
        //如果是平张纸品(存货类别 kc_chlb表中的ckdgs=3)并且它也是可销售的那么它的hssl就用数据库中取出来的.
        //除此之外的情况,那么主表中的hssl字段就不须要用到了.程序中在这里将其置为空串.
        String issale = dsBillWrapperMaster.getValue("issale");
        boolean isNoPaper = !(issale.equals("1")&&ckdgs.equals("3"));

        int index = mastListKey.indexOf(allName);
        if(index < 0)
        {
          RowMap rm = new RowMap(dsBillWrapperMaster);
          rm.put("gg", width);
          rm.put("isPaper", isNoPaper ? "false" : "true");
          //empty hssl's value
          if(isNoPaper)
            rm.put("hssl", "");
          mastListKey.add(allName);
          mastListValue.add(rm);
        }
        dsBillWrapperMaster.next();
      }
      ///从表放入ArrayList中
      Hashtable detalTable = new Hashtable();
      dsBillWrapperDetail.setQueryString(PRINT_DETAIL_SQL+" where sfdjid='"+sfdjid+"'");
      if(dsBillWrapperDetail.isOpen())
        dsBillWrapperDetail.refresh();
      else
        dsBillWrapperDetail.openDataSet();
      //ArrayList al = new ArrayList();//放从表数据
      dsBillWrapperDetail.first();
      for(int i=0;i<dsBillWrapperDetail.getRowCount();i++)
      {
        String sl = dsBillWrapperDetail.getValue("sl");
        String ph = dsBillWrapperDetail.getValue("ph");
        String cpbm = dsBillWrapperDetail.getValue("cpbm");

        RowMap rm = new RowMap();
        rm.put("sl", dsBillWrapperDetail.getValue("sl"));
        rm.put("hssl", dsBillWrapperDetail.getValue("hssl"));
        rm.put("ph", dsBillWrapperDetail.getValue("ph"));
        rm.put("cpbm", dsBillWrapperDetail.getValue("cpbm"));
        rm.put("dmsxid", dsBillWrapperDetail.getValue("dmsxid"));
        rm.put("gg", dsBillWrapperDetail.getValue("gg"));
        String sxz = dsBillWrapperDetail.getValue("sxz");
        String ckdgs = dsBillWrapperDetail.getValue("ckdgs");
        String width = null;
        //如果是平张纸品那么它的打印出来的规格要从全从规格属性里取
        if (ckdgs.equals("3"))
        {
          width = parseEspecialString(sxz, "()", true);//取长度
          width = width + "*" + parseEspecialString(sxz, "()", false);//取宽度
          //如果是平张纸的话那么,规格的格式直接就是规格属性里取到的:长度*宽度
          rm.put("gg", width);
        }
        else
        {
          width = rm.get("gg") + parseEspecialString(sxz,"()", false);
          rm.put("gg", width);
        }
        String allName = cpbm + "$" + width;
        ArrayList detailList = (ArrayList)detalTable.get(allName);
        if(detailList == null)
        {
          detailList = new ArrayList();
          detalTable.put(allName, detailList);
        }
        detailList.add(rm);
        dsBillWrapperDetail.next();
      }
      //process print info
      mprint = new ArrayList();//主,放的是rowmap
      dprint = new ArrayList();//从,放的是arraylist
      for(int page=0; page < mastListKey.size(); page++)
      {
        RowMap mastRow = (RowMap)mastListValue.get(page);
        String allName = (String)mastListKey.get(page);
        boolean isPaper = mastRow.get("isPaper").equals("true");
        ArrayList detailRows = (ArrayList)detalTable.get(allName);
        if(detailRows == null){
          page++;
          continue;
        }
        //detail print bill data
        int num = 0;
        while(num < detailRows.size())
        {
          //add mast print row
          if(num > 0)
            mastRow = (RowMap)mastRow.clone();
          mprint.add(mastRow);
          //process detail print row
          BigDecimal pageSlTotal = new BigDecimal(0);
          BigDecimal pageHsslTotal = new BigDecimal(0);
          ArrayList list = new ArrayList();
          boolean isEnd = false;
          for(int i=0; i<9; i++)
          {
            RowMap detailPrintRow = new RowMap();
            for(int j=0; j<3; j++)
            {
              RowMap detailRow = (RowMap)detailRows.get(num);
              String ph = detailRow.get("ph");
              String sl = detailRow.get("sl");
              String hssl = detailRow.get("hssl");
              detailPrintRow.put("sl"+j, sl.length()==0 ? "0" : sl);
              detailPrintRow.put("ph"+j, ph);
              //是平装纸则求它的每页面的换算数量页数
              pageSlTotal = pageSlTotal.add(new BigDecimal(sl.equals("")?"0":sl));
              if (isPaper)
                pageHsslTotal = pageHsslTotal.add(new BigDecimal(hssl.equals("")?"0":hssl));

              num++;
              if(num >= detailRows.size())
              {
                isEnd = true;
                break;
              }
            } /*end of j=3*/
            list.add(detailPrintRow);
            if(isEnd)
              break;
          }/*end of i=9*/
          dprint.add(list.toArray(new RowMap[list.size()]));
          mastRow.put("total", "合计:"+pageSlTotal.toString());
          if(isPaper)
            mastRow.put("hssl", pageHsslTotal.toString());
        }
      }
      int pageCount = mprint.size();
      ReportData[] rd = new ReportData[mprint.size()];
      for(int i=0;i<mprint.size();i++)
      {

        RowMap mr = (RowMap)mprint.get(i);
        String cpbm = mr.get("cpbm");
        RowMap[] mrs =new RowMap[1];//主
        mr.put("pageNo", "第" + String.valueOf(i+1)+ "页" + "/"+  "共" + String.valueOf(pageCount) + "页");
        mrs[0]=mr;
        RowMap[] drs = (RowMap[])dprint.get(i);
        /*
        ArrayList dlist = (ArrayList)list.get(i);
        RowMap[] drs =new RowMap[dlist.size()];//从
        for(int j=0;j<dlist.size();j++)
        {
        RowMap dr = (RowMap)dlist.get(j);
        drs[j] = dr;
        String sl = mr.get("sl0");
        }
        */
        ReportData td = new ReportData();
        td.addReportData("mrs",mrs);
        td.addReportData("drs",drs);
        rd[i] = td;
      }
      context.addReportDatas(rd);
      dsBillWrapperDetail.closeDataSet();
      dsBillWrapperMaster.closeDataSet();
  }

  /*
  RowMap[] mastRows = (RowMap[])mrows.values().toArray(new RowMap[mrows.size()]);
  for (int j=0; j<mastRows.length;j++)
  {
  //用来记录每一个与主表数据集的一笔数据相对应的明细资料分组中被包含进去的明细资料的记录个数.
  int perGroupNumber = 0;
  ArrayList dprintrow = new ArrayList();
  RowMap tempMrm = mastRows[j];
  String cpbm = tempMrm.get("cpbm");
        String dmsxid = tempMrm.get("dmsxid");
        String mgg = tempMrm.get("gg");
        //ArrayList dArray = new ArrayList();
        int m = 0;
        RowMap rtmp = new RowMap();
        BigDecimal alBtotal = new BigDecimal(0);
        for (int l=0;l<al.size();l++)
        {
          //对从表
          RowMap tempDrm = (RowMap)al.get(l);
          String dcpbm = tempDrm.get("cpbm");
          String ddmsxid = tempDrm.get("dmsxid");
          String dgg = tempDrm.get("gg");
          String ph = tempDrm.get("ph");
          String sl = tempDrm.get("sl");
          String hssl = tempDrm.get("hssl");
          //2004-4-29 10:35 修改 判断是不是一个要分到一组中的条件是:主从表的cpbm,宽度*长度相等 yjg
          if (dcpbm.equals(cpbm) && dgg.equals(mgg))
          {
            perGroupNumber++;
            rtmp.put("ph"+m,ph);
            rtmp.put("sl"+m,sl);
            rtmp.put("hssl"+m,hssl);
            //对cpbm, dmsxid相同的数据(即:打印出来的时候会打印在同一张纸上)合计sl
            alBtotal = alBtotal.add(new BigDecimal(sl.equals("")?"0":sl));
            m=m+1;
            if(m==3)
            {
              //从表的记录数多于3行
              m=0;
              dprintrow.add(rtmp);
              rtmp = new RowMap();
            }
            //dArray.add(tempDrm);//取出子表对应主表一行的数据.
          }
        }
        //形成 合计:xxx.xx供打印
        tempMrm.put("total", "合计:"+alBtotal.toString());
        if(m<3)
          dprintrow.add(rtmp);//
        //如果dprintrow的大小大于一页9行的数量.那么就要分页来打印了.
        //里面记录了.属于同一个明细资料长*宽分组的明细数据.
        //循环几次.即按每组9行.要分拆成几组
        if (dprintrow.size()>9)
        {
          int total = dprintrow.size()/9 + (dprintrow.size()%9 > 0 ? 1 : 0);
          //tempMrm.put("pageNo", "第" + String.valueOf(j+1)+ "页" + "/"+  "共" + String.valueOf(mrows.size()+total-1) + "页");
          for (int i=0;i<total;i++)
          {
            //确定这一页会有几条记录
            int tempSize = dprintrow.size()-(i*9)>9?9:dprintrow.size()-(i*9);
            RowMap[] drs= new RowMap[tempSize];
            //取从表打印数据数据集中的第几笔到第几笔组成一页.
            String sl0 = "0";
            String sl1 = "0";
            String sl2 = "0";
            String hssl0 = "0";
            String hssl1 = "0";
            String hssl2 = "0";
            boolean isPaper = tempMrm.get("isPaper").equals("true");
            tempMrm.put("total", "0");
            tempMrm.put("hssl", "");
            BigDecimal btotal = new BigDecimal(0);
            BigDecimal hsslBtotal = new BigDecimal(0);
            for (int v=i*9,w=0; v<(i+1)*9&&v<dprintrow.size();v++,w++)
            {
              RowMap rp = (RowMap)dprintrow.get(v);
              drs[w] = (RowMap)dprintrow.get(v);
              sl0 = rp.get("sl0").equals("")?"0":rp.get("sl0");
              sl1 = rp.get("sl1").equals("")?"0":rp.get("sl1");
              sl2 = rp.get("sl2").equals("")?"0":rp.get("sl2");
              //是平装纸则求它的每页面的换算数量页数
              if (isPaper)
              {
                hssl0 = rp.get("hssl0").equals("")?"0":rp.get("hssl0");
                hssl1 = rp.get("hssl1").equals("")?"0":rp.get("hssl1");
                hssl2 = rp.get("hssl2").equals("")?"0":rp.get("hssl2");
                //求每页换算数量和数量的和
                hsslBtotal=hsslBtotal.add(new BigDecimal(hssl0.equals("")?"0":hssl0).add(new BigDecimal(hssl1.equals("")?"0":hssl1).add(new BigDecimal(hssl2.equals("")?"0":hssl2))));
              }
              btotal=btotal.add(new BigDecimal(sl0.equals("")?"0":sl0).add(new BigDecimal(sl1.equals("")?"0":sl1).add(new BigDecimal(sl2.equals("")?"0":sl2))));

              //float tempTotal= tempMrm.get("total").equals("")?0:Float.parseFloat(tempMrm.get("total"));
              //float tempSl = Float.parseFloat(sl0) + Float.parseFloat(sl1) + Float.parseFloat(sl2);
              //tempMrm.put("total", Float.toString(tempTotal+tempSl));
            }
            tempMrm.put("total", "合计:"+btotal.toString());
            tempMrm.put("hssl", isPaper?hsslBtotal.toString():"");
            totalPageNos++;
            //同一组的明细资料分成几页,那么主表也要相应的有几个主表资料头
            RowMap tempNewMrm = (RowMap)tempMrm.clone();
            mprint.add(tempNewMrm);//放的是RowMap;
            dprint.add(drs);//放的是arrayList
          }
        }
        else
        {
          RowMap[] drs= new RowMap[dprintrow.size()];
          for(int q=0;q<dprintrow.size();q++)
          {
            RowMap rp =(RowMap)dprintrow.get(q);
            drs[q] = (RowMap)dprintrow.get(q);
            String sl = rp.get("sl0");
          }
          totalPageNos++;
          mprint.add(tempMrm);//放的是RowMap
          dprint.add(drs);//放的是arrayList
        }
        //如果明细资料中按长度*宽度分组后被分出来的数据条数和本来sql抽取到的明细资料表数据条数一样.那说明只存在一种分组.只须打印一张.
        //不须要再对主表数据集mrows进行循环了.
        if ( perGroupNumber == al.size() ) break;
      }*/
      /*
      * 对数据集进行循环,对货物按产品编码cpbm进行分组,相同的货物每三个一行,
      * 不足三个的占一行.多余三个的分成几行
      dsBillWrapperDetail.first();
      for(int i=0;i<dsBillWrapperDetail.getRowCount();i++)
      {
        String cpbm = dsBillWrapperDetail.getValue("cpbm");
        if(contains.contains(cpbm))
        {
          //已经分组的货物不再分组
          dsBillWrapperDetail.next();
          continue;
        }
        rm = new RowMap();
        int h=0;
        String count = dataSetProvider.getSequence("SELECT count(*) from VW_OUTPUTLIST_DTL_BILL_WRAPPER where sfdjid='"+sfdjid+"'");
        int totalcount = Integer.parseInt(count);//对应同类货物(不同规格属性)总数,有totalcount/3+Integer.parseInt(((totalcount%3)>0)?"1":"0")行
        for(int j=0;j<al.size();j++)
        {
          //al里包含了数据集里的数据
          RowMap rt =(RowMap)al.get(j);
          String ph = rt.get("ph");
          //String cpbm = rt.get("cpbm");
          if(rt.get("cpbm").equals(cpbm))
          {
            //提取出对应tdhwid
            String sl = rt.get("sl");
            String sn = "s"+(h%3);
            rm.put(sn,sl);
            h=h+1;
            if(totalcount<3&&h==totalcount)
            {
              //总数小于3且循环到结束.
              rm.put("ph",ph);
              drows.add(rm);//循环完
              contains.add(cpbm);
            }
            else if((h%3)==0)
            {
              //
              rm.put("ph",ph);
              drows.add(rm);
              contains.add(cpbm);
            }
            else if(totalcount>3&&h==totalcount)
            {
              //
              rm.put("ph",ph);
              drows.add(rm);//循环完
              contains.add(cpbm);
            }
          }
        }////////
        dsBillWrapperDetail.next();
      }*/
}



