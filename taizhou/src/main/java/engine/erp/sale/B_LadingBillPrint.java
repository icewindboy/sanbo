package engine.erp.sale;

import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.report.event.TempletProvideResponse;
import engine.report.event.TempletAfterProvideListener;
import engine.report.util.ReportData;
import engine.dataset.*;
import engine.util.StringUtils;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Title: 套打 </p>
 * <p>Description: 提供套打数据 主从表的数据集分别提供,放入在RowMap中</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 胡康宁
 * @version 1.0
 */

public final class B_LadingBillPrint implements TempletAfterProvideListener
{
  private static final String PRINT_MASTER_SQL = " SELECT * from VW_SALE_LADING_BILL ";//套打的主表
  private static final String PRINT_DETAIL_SQL = " SELECT t.pm,t.gg,t.sxz,t.dj,t.jldw,t.tdid,sum(nvl(t.sl,0))sl,sum(nvl(t.jje,0))jje,t.ckdgs from VW_SALE_LADING_BILL_DETAIL t WHERE ? GROUP BY t.pm,t.gg,t.sxz,t.jldw,t.tdid,t.dj,t.ckdgs ";//套打的从表
  private  String syskey="";
  private  String widthSyskey = "";
  private  String lengthSyskey = "";
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
    this.syskey = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");
    this.widthSyskey = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");
    this.lengthSyskey = loginBean.getSystemParam("SYS_PRODUCT_SPEC_LENGTH");

    EngineDataSet dsMaster  = new EngineDataSet();//套打的主表
    EngineDataSet dsDetail  = new EngineDataSet();//套打的从表
    dsMaster.setProvider(context.getDataSetProvider());
    dsDetail.setProvider(context.getDataSetProvider());
    String tdid = req.getParameter("tdid");
    if(tdid==null)
      tdid="";
    //主表是直接产生RowMap数组
    dsMaster.setQueryString(PRINT_MASTER_SQL+" where tdid='"+tdid+"'");
    dsMaster.openDataSet();
    RowMap masterRow = new RowMap(dsMaster);//放主表的数据(数组)

    dsDetail.setQueryString(StringUtils.combine(PRINT_DETAIL_SQL,"?",new String[]{" t.tdid='"+tdid+"' "}));
    dsDetail.openDataSet();
    //ArrayList al = new ArrayList();//放从表数据
    RowMap[] drows = new RowMap[dsDetail.getRowCount()];
    dsDetail.first();
    for(int i=0;i<dsDetail.getRowCount();i++)
    {
      RowMap rm = new RowMap();
      String sl = dsDetail.getValue("sl");
      String price="";
      String pm = dsDetail.getValue("pm");
      String gg = dsDetail.getValue("gg");
      String jldw = dsDetail.getValue("jldw");
      String sxz = dsDetail.getValue("sxz");
      String dj = dsDetail.getValue("dj");
      String jje = dsDetail.getValue("jje");
      String ckdgs =  dsDetail.getValue("ckdgs");//出库单格式
      price = dj;
      String width = parseEspecialString(sxz,"()");
      rm.put("pm",pm);
      rm.put("gg",gg+width);
      if(ckdgs.equals("3"))
      {
        width = parseEspecialString(sxz, "()", true);//取长度
        width = width + "*" + parseEspecialString(sxz, "()", false);//取宽度
        rm.put("gg",width);
      }
      rm.put("price",dj);
      rm.put("sl",sl);
      rm.put("jje",jje);
      drows[i] = rm;
      dsDetail.next();
    }

    ReportData td = new ReportData();
    td.addReportData("ds", masterRow);
    td.addReportData("list", drows);
    context.addReportData(td);
    dsMaster.closeDataSet();
    dsDetail.closeDataSet();
  }

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
   * 分割字符串s,用sep分割成一个字符串数组.并保存到HashTable中
   * 返回字符串，是Hash表中key为field的值
   */
  public final String parseEspecialString(String s, String sep)
  {
    if(s==null || s.equals(""))
      return "0";
    String[] code = StringUtils.parseString(s, sep);
    String key=null, value = null;
    for(int i=0; i<code.length; i++)
    {
      if(i%2 > 0){
        value = code[i];
      }
      else{
        key = code[i].trim();
      }
      if(value==null)
        continue;
      if(key.equals(syskey))
        return value;
    }
    return "";
  }
}