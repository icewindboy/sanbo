package engine.erp.produce;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import java.util.*;
import engine.html.*;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.dataset.sql.*;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.*;
import engine.project.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.*;

import com.borland.dx.dataset.*;

public final class B_Produce_Sale_Detail extends BaseAction implements Operate
{

  private static final String XSHT_STRUCT_SQL = "select d.dwmc,c.cpid,a.htrq rq,a.htbh,a.dwtxid,b.sl,b.dj,b.jje,b.jhrq,b.bz from xs_ht a,xs_hthw b, vw_xs_wzdj c ,dwtx d where 1<>1 "; //销售合同
  private static final String SCJH_STRUCT_SQL    = "select b.cpid,a.jhrq rq,a.jhh,a.deptid,b.sl,b.ksrq,b.wcrq,b.jgyq from sc_jh a,sc_jhmx b  where 1<>1 ";
  private static final String SCRWD_STRUCT_SQL    = "select b.cpid,a.rq ,a.rwdh,a.deptid,b.sl,b.ksrq,b.wcrq,b.jgyq from sc_rwd a,sc_rwdmx b where 1<>1 ";
  private static final String CKD_STRUCT_SQL    = "select b.cpid, a.sfrq rq,a.sfdjdh,a.deptid,b.sl,b.dj,b.je,b.ph,b.bz,"
              +" decode(a.djxz,1,'采购入库单',2,'销售出库单',3,'自制收货单',4,'生产领料单',5,'外加工入库单',6,'外加工发料单',7,'报损单',8,'移库单') djxz "
            +" from kc_sfdj a,kc_sfdjmx b where  1<>1 ";
  private static final String THD_STRUCT_SQL    = "select b.cpid,a.tdrq rq,a.tdbh,c.dwmc,b.sl,b.dj,b.jje,a.deptid,d.xm,f.htbh,a.jsfsid from xs_td a,xs_tdhw b,dwtx c,emp d,xs_hthw e,xs_ht f where 1<>1";
  private static final String SCRBB_STRUCT_SQL  = "select a.cpid,a.rq,a.deptid,b.mc,c.xm,a.gx,a.sl,a.de from Rep_Sc_Workload a ,bm b,emp c where  1<>1 ";

  private static final String XSHT_SQL = "select * from (select d.dwmc,c.cpid,a.htrq rq,a.htbh,a.dwtxid,b.sl,b.dj,b.jje,b.jhrq,b.bz from xs_ht a,xs_hthw b, vw_xs_wzdj c ,dwtx d where a.htid=b.htid and d.dwtxid=a.dwtxid and b.wzdjid=c.wzdjid) produce_sale_detail where 1=1 "; //销售合同
  private static final String SCJH_SQL    = "select * from (select b.cpid,a.jhrq rq,a.jhh,a.deptid,b.sl,b.ksrq,b.wcrq,b.jgyq from sc_jh a,sc_jhmx b where a.scjhid=b.scjhid) produce_sale_detail where 1=1 ";//生产计划单
  private static final String SCRWD_SQL    = "select * from (select b.cpid,a.rq ,a.rwdh,a.deptid,b.sl,b.ksrq,b.wcrq,b.jgyq from sc_rwd a,sc_rwdmx b where a.rwdid=b.rwdid) produce_sale_detail where 1=1 ";//生产任务单
  private static final String CKD_SQL    = "select * from (select b.cpid, a.sfrq rq,a.sfdjdh,a.deptid,b.sl,b.dj,b.je,b.ph,b.bz,"
            +" decode(a.djxz,1,'采购入库单',2,'销售出库单',3,'自制收货单',4,'生产领料单',5,'外加工入库单',6,'外加工发料单',7,'报损单',8,'移库单') djxz "
            +" from kc_sfdj a,kc_sfdjmx b where a.sfdjid=b.sfdjid ) produce_sale_detail where 1=1 ";//出库单
  private static final String THD_SQL    = "select * from (select b.cpid,a.tdrq rq,a.tdbh,c.dwmc,b.sl,b.dj,b.jje,a.deptid,d.xm,f.htbh,a.jsfsid from xs_td a,xs_tdhw b,dwtx c,emp d,xs_hthw e,xs_ht f where a.tdid=b.tdid and a.personid=d.personid and c.dwtxid=a.dwtxid and e.hthwid=b.hthwid and e.htid=f.htid ) produce_sale_detail where 1=1 ";//提货单
  private static final String SCRBB_SQL  = "select * from (select a.cpid,a.rq,a.deptid,b.mc,c.xm,a.gx,a.sl,a.de from Rep_Sc_Workload a ,bm b,emp c where a.deptid=b.deptid and a.personid=c.personid) produce_sale_detail where 1=1 ";//生产日报表
  //private EngineDataSet ds_Produce_Sale_Detail  = new EngineDataSet();
  public  HtmlTableProducer table = new HtmlTableProducer(null, "produce_sale_detail", "produce_sale_detail");
  public String activetab = "SetActiveTab(INFO_EX,'INFO_EX_0')";//从表当前的div
  private EngineDataSet ds_xsht  = new EngineDataSet();//销售合同
  private EngineDataSet ds_scjh  = new EngineDataSet();//生产计划
  private EngineDataSet ds_scrwd = new EngineDataSet();//生产任务单
  private EngineDataSet ds_ckd = new EngineDataSet();//出库单
  private EngineDataSet ds_thd  = new EngineDataSet();//提货单
  private EngineDataSet ds_scrbb  = new EngineDataSet();//生产日报表


  private boolean isInitQuery = false;                         //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  private User currUser = null;
  public String ksrq=null;
  public String jsrq=null;
  public String cpid=null;
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_Produce_Sale_Detail getInstance(HttpServletRequest request)
  {
    B_Produce_Sale_Detail produce_Sale_DetailBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "produce_Sale_DetailBean";
      produce_Sale_DetailBean = (B_Produce_Sale_Detail)session.getAttribute(beanName);
      if(produce_Sale_DetailBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);
        produce_Sale_DetailBean = new B_Produce_Sale_Detail();
        produce_Sale_DetailBean.currUser = loginBean.getUser();
        session.setAttribute(beanName, produce_Sale_DetailBean);
      }
    }
    return produce_Sale_DetailBean;
  }

  /**
   * 构造函数
   */
  private B_Produce_Sale_Detail()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }

  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private final void jbInit() throws java.lang.Exception
  { setDataSetProperty(ds_xsht, null);
    setDataSetProperty(ds_scjh, null);
    setDataSetProperty(ds_scrwd, null);
    setDataSetProperty(ds_ckd, null);
    setDataSetProperty(ds_thd, null);
    setDataSetProperty(ds_scrbb, null);
    //添加操作的触发对象

    addObactioner(String.valueOf(INIT), new B_Produce_Sale_Detail_Init());

    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());

  }

  //----Implementation of the BaseAction abstract class
  /**
   * JSP调用的函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      String operate = request.getParameter(OPERATE_KEY);
      if(operate != null && operate.trim().length() > 0)
      {
        RunData data = notifyObactioners(operate, request, response, null);
        if(data == null)
          return showMessage("无效操作", false);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){

      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Session失效时，调用的函数
   */
  public final void valueUnbound(HttpSessionBindingEvent event)
  {

    if(ds_xsht != null){
      ds_xsht.close();
      ds_xsht = null;
    }
    if(ds_scjh!= null){
      ds_scjh.close();
      ds_scjh = null;
    }
    if(ds_scrwd != null){
      ds_scrwd.close();
      ds_scrwd = null;
    }
    if(ds_ckd != null){
      ds_ckd.close();
      ds_ckd = null;
    }
    if(ds_thd != null){
      ds_thd.close();
      ds_thd = null;
    }
    if(ds_scrbb != null){
      ds_scrbb.close();
      ds_scrbb = null;
    }

  }

  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }
  public final EngineDataSet getxshtTable(){return ds_xsht;}
  public final EngineDataSet getscjhTable(){return ds_scjh;}
  public final EngineDataSet getscrwdTable(){return ds_scrwd;}
  public final EngineDataSet getckdTable(){return ds_ckd;}
  public final EngineDataSet getthdTable(){return ds_thd;}
  public final EngineDataSet getscrbbTable(){return ds_scrbb;}

  class B_Produce_Sale_Detail_Init implements Obactioner
    {
      //----Implementation of the Obactioner interface
      /**
       * 触发初始化操作
       * @parma  action 触发执行的参数（键值）
       * @param  o      触发者对象
       * @param  data   传递的信息的类
       * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
       */
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        retuUrl = data.getParameter("src");
        retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
        //初始化时清空数据集
        RowMap row = fixedQuery.getSearchRow();
        row.clear();
        ds_xsht.setQueryString(XSHT_STRUCT_SQL);
        ds_scjh.setQueryString(SCJH_STRUCT_SQL);
        ds_scrwd.setQueryString(SCRWD_STRUCT_SQL);
        ds_ckd.setQueryString(CKD_STRUCT_SQL);
        ds_thd.setQueryString(THD_STRUCT_SQL);
        ds_scrbb.setQueryString(SCRBB_STRUCT_SQL);

     if(ds_xsht.isOpen())
     ds_xsht.refresh();
     else
       ds_xsht.open();
     /*职工教育情况*/

     if(ds_scjh.isOpen())
     ds_scjh.refresh();
     else
       ds_scjh.open();
     /*职工工作经历*/

     if(ds_scrwd.isOpen())
       ds_scrwd.refresh();
     else
     ds_scrwd.open();
     /*职工工培训情况*/

     if(ds_ckd.isOpen())
       ds_ckd.refresh();
     else
     ds_ckd.open();
     /*职工奖惩情况*/
      if(ds_thd.isOpen())
       ds_thd.refresh();
     else
       ds_thd.open();
     if(ds_scrbb.isOpen())
      ds_scrbb.refresh();
    else
        ds_scrbb.open();

    table.init(data.getRequest(), currUser.getUserId());

        data.setMessage(showJavaScript("showFixedQuery()"));//初始化弹出查询界面
      }
  }
  /**
   *  查询操作
   */
  class FIXED_SEARCH implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      table.getWhereInfo().setWhereValues(data.getRequest());
      //fixedQuery.setSearchValue(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      QueryWhere where = table.getWhereInfo();
      ksrq = where.getWhereValue("produce_sale_detail$rq$a");
      jsrq = where.getWhereValue("produce_sale_detail$rq$b");
      cpid = where.getWhereValue("produce_sale_detail$cpid");
      if (cpid.equals("")){
        data.setMessage(showJavaScript("alert('请选择产品')"));
        return;
      }
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
        String xs_SQL = XSHT_SQL+SQL;
       ds_xsht.setQueryString(xs_SQL);
       String sc_SQL = SCJH_SQL+SQL;
       ds_scjh.setQueryString(sc_SQL);
       String scrwd_SQL = SCRWD_SQL+SQL;
       ds_scrwd.setQueryString(scrwd_SQL);
       String ckd_SQL = CKD_SQL+SQL;
       ds_ckd.setQueryString(ckd_SQL);
       String thd_SQL = THD_SQL+SQL;
       ds_thd.setQueryString(thd_SQL);
       String scrbb_SQL = SCRBB_SQL+SQL;
       ds_scrbb.setQueryString(scrbb_SQL);

      if(ds_xsht.isOpen())
      ds_xsht.refresh();
      else
        ds_xsht.open();
      /*职工教育情况*/

      if(ds_scjh.isOpen())
      ds_scjh.refresh();
      else
        ds_scjh.open();
      /*职工工作经历*/

      if(ds_scrwd.isOpen())
        ds_scrwd.refresh();
      else
      ds_scrwd.open();
      /*职工工培训情况*/

      if(ds_ckd.isOpen())
        ds_ckd.refresh();
      else
      ds_ckd.open();
      /*职工奖惩情况*/
       if(ds_thd.isOpen())
        ds_thd.refresh();
      else
        ds_thd.open();
      if(ds_scrbb.isOpen())
       ds_scrbb.refresh();
     else
        ds_scrbb.open();

    }


  }

}