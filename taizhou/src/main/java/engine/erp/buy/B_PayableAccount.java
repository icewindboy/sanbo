package engine.erp.buy;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import java.util.*;
import engine.html.*;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.project.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title:应付款初始化</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ENGINE</p>
 * @version 1.0
 */

public final class B_PayableAccount extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1009";
  public static final String OVER= "1001";
  public static final String copy= "1002";
  public static final String BATCH_ADD= "1003";
  /**
   * 提取主表信息的SQL语句
   */
  //private static final String B_PayableAccount_SQL = "SELECT b.dwtxid ,a.yfk,a.khlx from cg_yfk　a, dwtx b, dwtx_lx c WHERE a.dwtxid(+)=b.dwtxid AND b.dwtxid=c.dwtxid(+) AND c.ywlx=1 ORDER BY b.dwtxid ";//
  private static final String MASTER_STRUCT_SQL = "select * from cg_yfk where 1<>1";
  private static final String B_PayableAccount_save_SQL = "select * from cg_yfk where ? ";//保存时用到的sql
  private static final String MASTER_SQL = "select * from (select a.*, b.dwdm, b.dwmc, c.areacode, c.dqmc from cg_yfk a, dwtx b, dwdq c WHERE a.dwtxid=b.dwtxid and b.dqh=c.dqh) cg_yfk "
                       + " where cg_yfk.fgsid='?' ? ORDER BY cg_yfk.areacode, cg_yfk.dwdm, cg_yfk.khlx";//查询时的sql
  private static final String BATCH_ADD_SQL = "select a.dwtxid, a.dwdm, a.dwmc, b.areacode, b.dqh, b.dqmc from dwtx a, dwdq b, dwtx_lx c WHERE  a.dqh=b.dqh and a.dwtxid=c.dwtxid and c.ywlx='1' AND ";//批量增加SQL
  private static final String B_update_SQL = "update cg_yfk set rq=";//完成初始化的sql
  private static final String B_update = "UPDATE systemparam t SET t.value='1' WHERE t.code='BUY_INIT_OVER'";//完成初始化的sql
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private static final String MASTER_JE_SQL        = "SELECT SUM(nvl(yfk,0))yfk FROM cg_yfk WHERE fgsid=? ?  ";
  private String zzje="";//总金额
  private String JESQL="";//统计金额的SQL
  /**
   *本地数据集
   * 保存物资销售单价信息的数据集
   */
  private EngineDataSet ds_cg_yfk = new EngineDataSet();

  private EngineDataSet dsCrop = null;//批量增加单位

  //多行记录的引用
  private ArrayList d_RowInfos_ArrayList = null;

  public  HtmlTableProducer table = new HtmlTableProducer(ds_cg_yfk, "cg_yfk", "cg_yfk");//查询得到数据库中配置的字段
  /**
   * 点击返回按钮的URL
   *
   */
  public  static String retuUrl = null;
  public  static String BUY_INIT_OVER = null;
  private boolean isInitQuery = false;
  public static String loginName = ""; //登录员工的姓名
  private static String fgsID =null;   //分公司ID

  private static long masterRow = 0;//定位显示数据集指针
  /**
   * 定义固定查询类
   */
  private QueryFixedItem fixedQuery = new QueryFixedItem();
  /**
   * 得到物资销售单价信息的实例
   * @param request jsp请求
   * @return 返回物资销售单价信息的实例
   */

  public static B_PayableAccount getInstance(HttpServletRequest request)
  {
    B_PayableAccount b_PayableAccountBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_PayableAccountBean";
      b_PayableAccountBean = (B_PayableAccount)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      //b_employeecardnoBean = (B_EmployeeCardNo)session.getAttribute(beanName);
      if(b_PayableAccountBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        fgsID = loginBean.getFirstDeptID();
        b_PayableAccountBean = new B_PayableAccount();
        b_PayableAccountBean.loginName = loginBean.getUserName();
        b_PayableAccountBean.priceFormat = loginBean.getPriceFormat();
        b_PayableAccountBean.fgsid = loginBean.getFirstDeptID();
        //b_PayableAccountBean.BUY_INIT_OVER = loginBean.getSystemParam("BUY_INIT_OVER");
        b_PayableAccountBean.ds_cg_yfk.setColumnFormat("yfk", b_PayableAccountBean.priceFormat);
        session.setAttribute(beanName,b_PayableAccountBean);
      }
    }
    return b_PayableAccountBean;
  }
  /**
   * 构造函数(实例变量:分公司ID为初始化参数)
   */
  private B_PayableAccount()
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
  protected void jbInit() throws Exception
  {
      setDataSetProperty(ds_cg_yfk, MASTER_STRUCT_SQL);
      ds_cg_yfk.setSort(new SortDescriptor("", new String[]{"areacode", "dwdm", "khlx"}, new boolean[]{false, false, false}, null, 0));
      ds_cg_yfk.setTableName("cg_yfk");
      ds_cg_yfk.addLoadListener(new com.borland.dx.dataset.LoadListener() {
        public void dataLoaded(LoadEvent e) {
          initRowInfo(false, true);
        }
      }
    );
    //添加操作的触发对象
    addObactioner(String.valueOf(OVER), new B_PayableAccount_Post());
    addObactioner(String.valueOf(INIT), new B_PayableAccount_Init());
    addObactioner(String.valueOf(POST), new B_PayableAccount_Post());
    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());
    addObactioner(String.valueOf(DEL), new B_PayableAccount_Delete());
    addObactioner(String.valueOf(copy), new B_PayableAccount_copy());
    addObactioner(String.valueOf(ADD), new Master_Add());
    addObactioner(String.valueOf(BATCH_ADD), new Batch_Add());
  }

  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      String operate = request.getParameter(OPERATE_KEY);
      if(operate != null && operate.trim().length() > 0)
      {
        RunData data = notifyObactioners(operate, request, response, null);
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
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(ds_cg_yfk != null){
      ds_cg_yfk.close();
      ds_cg_yfk = null;
    }
    if(dsCrop != null){
      dsCrop.close();
      dsCrop = null;
    }
    log = null;
  }
  /**
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private final void putDetailInfo(HttpServletRequest request)
  {
    RowMap rowInfo = new RowMap();
    //保存网页的所有信息
    rowInfo.put(request);
    //从数据集中获取记录行数
    if(!ds_cg_yfk.isOpen())
    {
      ds_cg_yfk.open();
    }
    int rownum = ds_cg_yfk.getRowCount();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos_ArrayList.get(i);
      detailRow.put("dwtxid", rowInfo.get("dwtxid_"+i));//往来单位ID
      detailRow.put("yfk", rowInfo.get("yfk_"+i));//单位应付款
      detailRow.put("khlx", rowInfo.get("khlx_"+i));//客户
    }
  }


  /*得到总金额*/
  public final String getZje()
  {
    return zzje;
  }
  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos_ArrayList.size()];
    d_RowInfos_ArrayList.toArray(rows);
    return rows;
  }
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName()
  {
    return getClass();
  }
  /**
   * 初始化行信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   */
  private final void initRowInfo(boolean isAdd, boolean isInit)
  {
    EngineDataSet ds_cg_yfk = getDetailTable();;
    if(d_RowInfos_ArrayList == null)
      d_RowInfos_ArrayList = new ArrayList(ds_cg_yfk.getRowCount());
    else if(isInit)
      d_RowInfos_ArrayList.clear();

    //StringBuffer buf = new StringBuffer().append(" dwtxid IN(");
    ds_cg_yfk.first();
    int count  = ds_cg_yfk.getRowCount();
    for(int i=0; i<count; i++)
    {
      String dwtxid = ds_cg_yfk.getValue("dwtxid");
      /**
      if(i==count-1)
        buf = buf.append(dwtxid);
      else
        buf = buf.append(dwtxid).append(",");
        */
      RowMap row = new RowMap(ds_cg_yfk);
      row.put("InternalRow", String.valueOf(ds_cg_yfk.getInternalRow()));
      d_RowInfos_ArrayList.add(row);
      ds_cg_yfk.next();
    }
    int num = d_RowInfos_ArrayList.size();
    /**
    buf = buf.append(")");
    String SQL = buf.toString();
    ds_cg_yfk_save.setQueryString(combineSQL(B_PayableAccount_save_SQL, "?", new String[]{SQL}));
    if(ds_cg_yfk_save.isOpen())
      ds_cg_yfk_save.refresh();
    else
      ds_cg_yfk_save.open();
      */
  }

  /*得到表对象*/
  public final EngineDataSet getDetailTable()
  {
    return ds_cg_yfk;
  }
  /*得到总应收款*/
 public final String getZysk()
 {
   return zzje;
 }
  /**
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
   */
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
  }

  /**
   * 初始化操作的触发类
   */
  class B_PayableAccount_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      BUY_INIT_OVER = dataSetProvider.getSequence("select value from systemparam where code='BUY_INIT_OVER' ");
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //fixedQuery.getSearchRow().clear();
      table.getWhereInfo().clearWhereValues();
      ds_cg_yfk.setQueryString(combineSQL(MASTER_SQL, "?", new String[]{fgsID}));
      ds_cg_yfk.setRowMax(null);

      JESQL = combineSQL(MASTER_JE_SQL, "?", new String[]{fgsid});
      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
       if(dsje.getRowCount()<1)
       zzje="0";
       else
         zzje=dsje.getValue("yfk");

       zzje = zzje.equals("")?"0":zzje;
    }
  }
  /**
  * 增加一空白行操作
  */
 class Master_Add implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     //保存输入的明细信息
     putDetailInfo(data.getRequest());
     EngineDataSet ds = getDetailTable();
     ds.insertRow(false);
     ds.setValue("fgsid", fgsID);
     ds.post();
     RowMap detailrow = new RowMap();
     detailrow.put("InternalRow", String.valueOf(ds.getInternalRow()));
     d_RowInfos_ArrayList.add(detailrow);
   }
  }
  /**
   * 根据条件往来单位批量增加操作
   */
class Batch_Add implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest req = data.getRequest();
    //保存输入的明细信息
    putDetailInfo(data.getRequest());
    EngineDataSet ds = getDetailTable();
    /**
    RowMap rowinfo = null;
    for(int i=0; i<d_RowInfos_ArrayList.size(); i++)
    {
      rowinfo = (RowMap)d_RowInfos_ArrayList.get(i);
      masterRow = Long.parseLong(rowinfo.get("InternalRow"));
      ds.goToInternalRow(masterRow);
      ds.setValue("dwtxid", rowinfo.get("dwtxid"));
      ds.setValue("khlx", rowinfo.get("khlx"));
      ds.setValue("yfk", rowinfo.get("yfk"));
      ds.post();
    }
    */
    String scope = req.getParameter("scope");//得到选择添加方式
    String dqh = req.getParameter("dqh");//得到选择哪个地区
    String dwdm_a = req.getParameter("dwdm_a");
    String dwdm_b = req.getParameter("dwdm_b");
    String SQL = null;
    if(scope.equals("1")){//选择查询条件为所有存货
      if(dqh.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择地区')"));
        //data.setMessage(showJavaScript("showAddFrame()"));
        return;
      }
      SQL = BATCH_ADD_SQL + " b.dqh=" +dqh;
    }
    //按单位代码范围
      if(scope.equals("2")){
        if(dwdm_a.equals("") && dwdm_b.equals(""))
        {
          data.setMessage(showJavaScript("alert('单位编码范围不能为空')"));
          //data.setMessage(showJavaScript("showAddFrame()"));
          return;
        }
        if(!dwdm_a.equals("") && !dwdm_b.equals(""))
          SQL = BATCH_ADD_SQL + " a.dwdm>='" + dwdm_a+"' AND a.dwdm<='"+dwdm_b+"'";
        if(dwdm_a.equals("") && !dwdm_b.equals(""))
          SQL = BATCH_ADD_SQL + " a.dwdm<='"+dwdm_b+"'";
        if(!dwdm_a.equals("") && dwdm_b.equals(""))
          SQL = BATCH_ADD_SQL + " a.dwdm>='"+dwdm_a+"'";
      }
    //查询条件为按存货类别筛选
    if(dsCrop == null)
    {
      dsCrop = new EngineDataSet();
      setDataSetProperty(dsCrop, null);
    }
    dsCrop.setQueryString(SQL);
    if(!dsCrop.isOpen())
      dsCrop.openDataSet();
    else{
      dsCrop.readyRefresh();
      dsCrop.refresh();
    }

    int row = dsCrop.getRowCount();
    dsCrop.first();
    EngineRow locateRow = new EngineRow(ds, "dwtxid");
    for(int i=0; i<dsCrop.getRowCount(); i++)
    {
      dsCrop.goToRow(i);
      String dwtxid = dsCrop.getValue("dwtxid");
      locateRow.setValue(0,dwtxid);
      if(!ds.locate(locateRow, Locate.FIRST)){
        ds.insertRow(false);
        ds.setValue("dwtxid", dsCrop.getValue("dwtxid"));
        ds.setValue("fgsid", fgsID);
        ds.post();
      }
    }
    initRowInfo(false,true);
  }
  }
  /**
  * 添加查询操作的触发类
  */
 class FIXED_SEARCH implements Obactioner
 {
   //----Implementation of the Obactioner interface
   /**
    * 添加或修改的触发操作
    * @parma  action 触发执行的参数（键值）
    * @param  o      触发者对象
    * @param  data   传递的信息的类
    * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
    */
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     table.getWhereInfo().setWhereValues(data.getRequest());
     //fixedQuery.setSearchValue(data.getRequest());
     String SQL = table.getWhereInfo().getWhereQuery();
     if(SQL.length() > 0)
       SQL = " AND "+SQL;
     SQL = combineSQL(MASTER_SQL, "?", new String[]{fgsID,SQL});
     ds_cg_yfk.setQueryString(SQL);
     ds_cg_yfk.setRowMax(null);
    }
  }

  /**
   * 保存操作的触发类
   */
  class B_PayableAccount_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      boolean isOver=String.valueOf(OVER).equals(action);
      RowMap detailrow = null;
      putDetailInfo(data.getRequest());
      EngineDataSet ds = new EngineDataSet();

      String temp=null;
      ArrayList list = new ArrayList(d_RowInfos_ArrayList.size()+1);
      int length = d_RowInfos_ArrayList.size();
      for(int i=0; i<d_RowInfos_ArrayList.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos_ArrayList.get(i);
        String DWTXID=detailrow.get("dwtxid");
        String khlx=detailrow.get("khlx");
        String yfk=detailrow.get("yfk");
        if(yfk.length()>0 && (temp=checkNumber(yfk,"应付款"))!=null)
        {
          data.setMessage(temp);
          return;
        }
        if(DWTXID.equals(""))
        {
          data.setMessage(showJavaScript("alert('往来单位不能为空')"));
          return;
        }
        StringBuffer buf = new StringBuffer().append(DWTXID).append(",").append(khlx);
        String s = buf.toString();
        if(list.contains(s))
        {
          data.setMessage(showJavaScript("alert('不能有相同的单位并且客户类型相同')"));
          return;
        }
        else
          list.add(s);
        String count ="0";
        String sql =null;
        int row = i+1;
        if(ds_cg_yfk.isNew(i)){
          if(khlx.equals(""))
            sql = "select count(*) from cg_yfk where dwtxid='"+DWTXID+"' and fgsid='"+fgsID+"' and khlx IS NULL";
          else
            sql = "select count(*) from cg_yfk where dwtxid='"+DWTXID+"' and fgsid='"+fgsID+"' and khlx='"+khlx+"'";
          count = dataSetProvider.getSequence(sql);
          if(!count.equals("0"))
          {
              data.setMessage(showJavaScript("alert('第"+row+"行在数据库中已存在')"));
              return;
          }
        }
      }

      //EngineRow locatRow = new EngineRow(ds_cg_yfk, new String[]{"dwtxid","khlx"});
      ds_cg_yfk.first();
      for(int i=0; i<ds_cg_yfk.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos_ArrayList.get(i);
        String DWTXID=detailrow.get("dwtxid");
        String khlx=detailrow.get("khlx");
        String yfk=detailrow.get("yfk");
        ds_cg_yfk.setValue("dwtxid", detailrow.get("dwtxid"));
        ds_cg_yfk.setValue("fgsid", fgsID);
        ds_cg_yfk.setValue("khlx", detailrow.get("khlx"));
        ds_cg_yfk.setValue("yfk", yfk);
        //ds_cg_yfk.isNew()
        ds_cg_yfk.next();








      }
      Date today = new Date();
      String day = new SimpleDateFormat("yyyy-MM-dd").format(today);
      String update_sql = B_update_SQL+"to_date('"+day+"','YYYY-MM-DD')";
      if(isOver)
        ds_cg_yfk.updateQuery(new String[]{update_sql,B_update});
      ds_cg_yfk.post();
      ds_cg_yfk.getColumn("areacode").setResolvable(false);
      ds_cg_yfk.getColumn("dwdm").setResolvable(false);
      ds_cg_yfk.saveChanges();

      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("yfk");
        zzje = zzje.equals("")?"0":zzje;

      BUY_INIT_OVER = dataSetProvider.getSequence("select value from systemparam where code='BUY_INIT_OVER' ");
    }
  }

  class B_PayableAccount_Delete implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发删除操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getDetailTable();
      int num = Integer.parseInt(data.getParameter("rownum"));
      ds.goToRow(num);
      //String dwtxid = ds.getValue("dwtxid");
     // String khlx = ds.getValue("khlx");
      ds.deleteRow();
      d_RowInfos_ArrayList.remove(num);
     /**
      EngineRow locateRow = new EngineRow(ds_cg_yfk_save, new String[]{"dwtxid", "khlx"});
      locateRow.setValue(0, dwtxid);
      locateRow.setValue(1, khlx);
      if(ds_cg_yfk_save.locate(locateRow, Locate.FIRST))
      {
        ds_cg_yfk_save.deleteRow();
        ds_cg_yfk_save.post();
        ds_cg_yfk_save.saveChanges();
      }
      initRowInfo(false, true);
      */
    }
  }
  class B_PayableAccount_copy implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发复制操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      putDetailInfo(request);
      EngineDataSet ds = getDetailTable();
      RowMap rowinfo = null;
      for(int i=0; i<d_RowInfos_ArrayList.size(); i++)
      {
        rowinfo = (RowMap)d_RowInfos_ArrayList.get(i);
        masterRow = Long.parseLong(rowinfo.get("InternalRow"));
        ds.goToInternalRow(masterRow);
        ds.setValue("dwtxid", rowinfo.get("dwtxid"));
        ds.setValue("khlx", rowinfo.get("khlx"));
        ds.setValue("yfk", rowinfo.get("yfk"));
        ds.post();
      }
      int num = Integer.parseInt(data.getParameter("rownum"));
      ds.goToRow(num);
      String areacode = ds.getValue("areacode");
      String dwdm = ds.getValue("dwdm");
      String dwtxid = ds.getValue("dwtxid");
      String khlx = ds.getValue("khlx");
      ds.insertRow(false);
      ds.setValue("dwdm", dwdm);
      ds.setValue("areacode", areacode);
      ds.setValue("dwtxid",dwtxid);
      ds.setValue("khlx", khlx);
      ds.setValue("fgsid", fgsID);
      ds.post();
      /**
       RowMap detail = new RowMap();
       detail.put("dwtxid", dwtxid);
       detail.put("khlx", khlx);
       d_RowInfos_ArrayList.add(detail);
       */
      initRowInfo(false,true);
    }
  }
}
