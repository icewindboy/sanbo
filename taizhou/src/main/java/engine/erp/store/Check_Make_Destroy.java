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

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

/**
 * 2004-4-19 14:26 新增 直接取到换算数量取代下面的cehssl yjg
 * 03.12 11:18 新增 新增在生成损溢单的时候取得规格属性id yjg
 */
import com.borland.dx.dataset.*;
/**
 * <p>Title: 库存——盘点单中生成损溢单</p>
 * <p>Description: 库存——盘点单中生成损溢单<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */


public final class Check_Make_Destroy extends BaseAction implements Operate
{
  public  static final String TURNPAGE = "9996";// 新增 为明细表格番页而加的事件

  private static final String MASTER_STRUT_SQL = "SELECT * FROM kc_sfdj WHERE 1<>1";
  private static final String MASTER_SQL       = "SELECT * FROM kc_sfdj WHERE djxz='7' AND fgsid=? and pdid=? ";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM kc_sfdjmx WHERE 1<>1";
  private static final String DETAIL_SQL       = "SELECT * FROM kc_sfdjmx WHERE sfdjid= ";
  //private static final String UPDATE_SQL     = "UPDATE sc_jh SET zt='2' WHERE scjhid= ";//下达物料需求后update生产计划的状态
  private static final String STORE_CHECK_SQL  = "SELECT a.*, b.hsbl, c.sxz FROM kc_pdmx a, kc_dm b, kc_dmsx c WHERE a.cpid=b.cpid AND nvl(a.scsl,0)<>nvl(a.zcsl,0) AND a.dmsxid = c.dmsxid(+) AND a.cpid = c.cpid(+) AND pdid='?' ";
  private static final String UPDATE_KC_PD_SQL = "UPDATE kc_pd SET zt = 1 where pdid = '?'";
  private static final String UPDATE_KC_PD_STRUT_SQL = "SELECT * from kc_pd WHERE 1<>1";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsUpdatePDTable  = new EngineDataSet();//当生成损

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "kc_sfdj.5");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "kc_sfdjmx.5");
  public boolean isMasterAdd = true;    //是否在添加状态
  private int    masterRow   = -1;         //保存生产计划主表操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private EngineDataSet checkStoreData = null;

  public  String retuUrl = null;

  public  String loginId   = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid  = null;   //分公司ID
  private String sfdjid = null;
  private String pdid   = null;
  private String deptid = null;
  private String storeid = null;
  private String djxz    = null;
  //被分页后的数据集中某一个页面中从第几笔记录开始到第几笔数据结束.如第二页的资料范围是从第51-101笔
  public int min = 0;
  public int max = 0;
  /**
   * 物料需求计划列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回物料需求计划列表的实例
   */
  public static Check_Make_Destroy getInstance(HttpServletRequest request)
  {
    Check_Make_Destroy checkMakeDestroyBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "checkMakeDestroyBean";
      checkMakeDestroyBean = (Check_Make_Destroy)session.getAttribute(beanName);
      if(checkMakeDestroyBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        checkMakeDestroyBean = new Check_Make_Destroy();
        checkMakeDestroyBean.qtyFormat = loginBean.getQtyFormat();

        checkMakeDestroyBean.fgsid = loginBean.getFirstDeptID();
        checkMakeDestroyBean.loginId = loginBean.getUserID();
        checkMakeDestroyBean.loginName = loginBean.getUserName();
        //设置格式化的字段
        checkMakeDestroyBean.dsDetailTable.setColumnFormat("sl", checkMakeDestroyBean.qtyFormat);
        checkMakeDestroyBean.dsMasterTable.setColumnFormat("hssl", checkMakeDestroyBean.qtyFormat);
        session.setAttribute(beanName, checkMakeDestroyBean);
      }
    }
    return checkMakeDestroyBean;
  }

  /**
   * 构造函数
   */
  private Check_Make_Destroy()
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
  {
    setDataSetProperty(dsMasterTable, MASTER_STRUT_SQL);
    setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);
    setDataSetProperty(dsUpdatePDTable, UPDATE_KC_PD_STRUT_SQL);

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"sfdjdh"}, new String[]{"SELECT pck_base.billNextCode('kc_sfdj','sfdjdh','h') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"sfdjdh"}, new boolean[]{true}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"rkdmxid"}, new String[]{"s_kc_sfdjmx"}));

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(POST), new Master_Post());

    addObactioner(TURNPAGE, new Turn_Page());//翻页事件
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
      if(dsMasterTable.isOpen() && dsMasterTable.changesPending())
        dsMasterTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Session失效时，调用的函数
   */
  public final void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsMasterTable != null){
      dsMasterTable.close();
      dsMasterTable = null;
    }
    if(dsDetailTable != null){
      dsDetailTable.close();
      dsDetailTable = null;
    }
    if(checkStoreData != null){
      checkStoreData.close();
      checkStoreData = null;
    }
    if(dsUpdatePDTable != null){
      dsUpdatePDTable.close();
      dsUpdatePDTable = null;
    }
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
    checkStoreData = null;
    if(masterProducer != null)
    {
      masterProducer.release();
      masterProducer = null;
    }
    if(detailProducer != null)
    {
      detailProducer.release();
      detailProducer = null;
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

  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否是主表
    if(isMaster){
      if(isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear();

      if(!isAdd)
        m_RowInfo.put(getMaterTable());
      else
      {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        m_RowInfo.put("zdrq", today);//制单日期
        m_RowInfo.put("zdr", loginName);//操作员
        m_RowInfo.put("sfrq", today);//计划日期
        m_RowInfo.put("zdrid", loginId);
        m_RowInfo.put("pdid", pdid);
        m_RowInfo.put("deptid", deptid);
        m_RowInfo.put("storeid", storeid);
        m_RowInfo.put("djxz", "7");
      }
    }
    else
    {
      EngineDataSet dsDetail = dsDetailTable;
      if(d_RowInfos == null)
        d_RowInfos = new ArrayList(dsDetail.getRowCount());
      else if(isInit)
        d_RowInfos.clear();

      dsDetail.first();
      for(int i=0; i<dsDetail.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsDetail);
        d_RowInfos.add(row);
        dsDetail.next();
      }
    }
  }

  /**
   * 从表保存操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private final void putDetailInfo(HttpServletRequest request)
  {
    RowMap rowInfo = getMasterRowinfo();
    //保存网页的所有信息
    rowInfo.put(request);

    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=min; i<=max; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      //detailRow.put("cpid", rowInfo.get("cpid_"+i));
      detailRow.put("kwid", rowInfo.get("kwid_"+i));//库位
      //detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//
      //detailRow.put("hssl", formatNumber(rowInfo.get("hssl_"+i), qtyFormat));//
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//
      detailRow.put("ph", rowInfo.get("ph_"+i));//
      detailRow.put("bz", rowInfo.get("bz_"+i));//备注
      detailRow.put("cpid", rowInfo.get("cpid_"+i));
      detailRow.put("sl", rowInfo.get("sl_"+i));
      detailRow.put("hssl", rowInfo.get("hssl_"+i));
      detailRow.put("ph", rowInfo.get("ph_"+i));
      detailRow.put("bz", rowInfo.get("bz_"+i));

      //保存用户自定义的字段
      /*
      FieldInfo[] fields = detailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
      */

    }
  }

  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /*得到从表表对象*/
  public final EngineDataSet getDetailTable(){
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    return dsDetailTable;
  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }

  /**
   * 主表是否在添加状态
   * @return 是否在添加状态
   */
  public final boolean masterIsAdd() {return isMasterAdd; }

  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //
      pdid = request.getParameter("pdid");
      deptid = request.getParameter("deptid");
      storeid = request.getParameter("storeid");
      masterRow = Integer.parseInt(request.getParameter("rownum"));
      String SQL = combineSQL(MASTER_SQL, "?", new String[]{fgsid, pdid});
      dsMasterTable.setQueryString(SQL);
      if(dsMasterTable.isOpen())
        dsMasterTable.refresh();
      else
        dsMasterTable.open();
      if(dsMasterTable.getRowCount() > 0)
      {
        isMasterAdd=false;
        sfdjid = dsMasterTable.getValue("sfdjid");
        String detailSql = DETAIL_SQL + sfdjid;
        dsDetailTable.setQueryString(detailSql);
        if(dsDetailTable.isOpen())
          dsDetailTable.refresh();
        else
          dsDetailTable.open();
      }
      else {
        isMasterAdd = true;
        dsDetailTable.setQueryString(DETAIL_STRUT_SQL);
        if(dsDetailTable.isOpen())
          dsDetailTable.refresh();
        else
          dsDetailTable.open();
        String sql = combineSQL(STORE_CHECK_SQL, "?", new String[]{pdid});
        if(checkStoreData == null)
        {
          checkStoreData = new EngineDataSet();
          setDataSetProperty(checkStoreData, null);
        }
        checkStoreData.setQueryString(sql);
        if(!checkStoreData.isOpen())
          checkStoreData.openDataSet();
        else
          checkStoreData.refresh();

        if(checkStoreData.getRowCount()<1)
        {
          String tmpMessage = showJavaScript("alert('本次盘点没有损溢')");
          tmpMessage += showJavaScript("backList();");
          data.setMessage(tmpMessage);
          return;
        }
        EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wjid");
        for(int i=0; i<checkStoreData.getRowCount(); i++)
        {
          checkStoreData.goToRow(i);
          String wjid = checkStoreData.getValue("pdmxid");
          locateGoodsRow.setValue(0, wjid);
          if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST)){
            dsDetailTable.insertRow(false);
            BigDecimal zcsl = checkStoreData.getValue("zcsl").length()>0 ? new BigDecimal(checkStoreData.getValue("zcsl")) : new BigDecimal(0);//帐存数量
            BigDecimal scsl = checkStoreData.getValue("scsl").length()>0 ?  new BigDecimal(checkStoreData.getValue("scsl")) : new BigDecimal(0);//实存实量
            BigDecimal hssl = checkStoreData.getValue("hssl").length()>0 ?  new BigDecimal(checkStoreData.getValue("hssl")) : new BigDecimal(0);//实存实量
            BigDecimal zchssl = checkStoreData.getValue("zchssl").length()>0 ?  new BigDecimal(checkStoreData.getValue("zchssl")) : new BigDecimal(0);//实存实量
            BigDecimal cehssl = hssl.subtract(zchssl);
            //String hsbl = checkStoreData.getValue("hsbl");//换算比例
            //String sxz = checkStoreData.getValue("sxz");//换算比例
            //2004-4-19 14:26 新增 直接取到换算数量取代下面的cehssl yjg
            BigDecimal ce = scsl.subtract(zcsl);
            /*try
           {
             hssl = calculateExpression(hsbl, sxz).multiply(ce);
           }
           catch (Exception e)
           {
             hssl = new BigDecimal(0);
            }
            */
            //02.19 21:32 修改 将原来的cd*/hsbl改成现在的cd/hsbl.并考虑为0时情况. yjg
            //double cehssl = hsbl ==0 ? 0 : ce/hsbl;
            dsDetailTable.setValue("rkdmxid", "-1");
            dsDetailTable.setValue("wjid", checkStoreData.getValue("pdmxid"));
            dsDetailTable.setValue("cpid", checkStoreData.getValue("cpid"));
            dsDetailTable.setValue("ph", checkStoreData.getValue("ph"));
            dsDetailTable.setValue("sl", formatNumber(String.valueOf(ce), qtyFormat));
            dsDetailTable.setValue("hssl", cehssl.toString());//2004-4-19 14:26 与上面的相同时间标志对应
            dsDetailTable.setValue("sfdjid", "-1");
            //03.12 11:18 新增 新增在生成损溢单的时候取得规格属性id
            dsDetailTable.setValue("dmsxID", checkStoreData.getValue("dmsxID"));
            dsDetailTable.setValue("kwid", checkStoreData.getValue("kwid"));
            dsDetailTable.post();
            //创建一个与用户相对应的行
          }
          //RowMap detailrow = new RowMap(dsDetailTable);
          //d_RowInfos.add(detailrow);
        }
        initRowInfo(false, false, false);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
    }
  }
  /**
   * 主表保存操作的触发类
   */
  class Master_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());

      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      //校验表单数据
      String temp = checkMasterInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      if(!isMasterAdd)
        sfdjid = dsMasterTable.getValue("sfdjid");
      //得到主表主键值
      if(isMasterAdd){
        ds.insertRow(false);
        sfdjid = dataSetProvider.getSequence("s_kc_sfdj");
        ds.setValue("sfdjid", sfdjid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zt","0");
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);
        ds.setValue("zdr", loginName);//操作员
        ds.setValue("djxz", "7");
      }
      //保存从表的数据
     RowMap detailrow = null;
     BigDecimal totalNum = new BigDecimal(0);
     EngineDataSet detail = getDetailTable();
     detail.first();
     for(int i=0; i<detail.getRowCount(); i++)
     {
       detailrow = (RowMap)d_RowInfos.get(i);
       //新添的记录
       if(isMasterAdd)
         detail.setValue("sfdjid", sfdjid);
       double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;
       double hssl = detailrow.get("hssl").length() > 0 ? Double.parseDouble(detailrow.get("hssl")) : 0;//换算数量
       detail.setValue("sl", detailrow.get("sl"));//保存数量
       detail.setValue("hssl", String.valueOf(hssl));//保存数量
       detail.setValue("cpid", detailrow.get("cpid"));
       detail.setValue("kwid", detailrow.get("kwid"));
       detail.setValue("dmsxid", detailrow.get("dmsxid"));//物资规格属性
       detail.setValue("djxz", "7");
       detail.setValue("ph", detailrow.get("ph"));
       detail.setValue("bz", detailrow.get("bz"));//备注
       //保存用户自定义字段
       /*
       FieldInfo[] fields = detailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, detailrow.get(fieldCode));
       }
*/
       detail.post();
       totalNum = totalNum.add(detail.getBigDecimal("sl"));
       detail.next();
     }

     //保存主表数据
     ds.setValue("pdid", pdid);//仓库id
     ds.setValue("storeid", storeid);//仓库id
     ds.setValue("jsr", rowInfo.get("jsr"));//经手人
     ds.setValue("deptid", deptid);//部门id
     ds.setValue("sfdjlbid", rowInfo.get("sfdjlbid"));//收发单据类别ID
     ds.setValue("djxz", "7");//单据性质
     ds.setValue("jfkm", rowInfo.get("jfkm"));//用途ID
     ds.setValue("sfrq", rowInfo.get("sfrq"));//收发日期
     ds.setValue("zsl", totalNum.toString());//总数量
     ds.setValue("bz", rowInfo.get("bz"));//备注
     //保存用户自定义的字段
     /*
     FieldInfo[] fields = masterProducer.getBakFieldCodes();
     for(int j=0; j<fields.length; j++)
     {
       String fieldCode = fields[j].getFieldcode();
       detail.setValue(fieldCode, rowInfo.get(fieldCode));
     }
*/
     ds.post();

     dsUpdatePDTable.updateQuery(new String[]{combineSQL(UPDATE_KC_PD_SQL, "?", new String[]{pdid})});
     if(dsUpdatePDTable.isOpen())
     {
       dsUpdatePDTable.readyRefresh();
       dsUpdatePDTable.refresh();
     }
     else
       dsUpdatePDTable.open();

     ds.saveDataSets(new EngineDataSet[]{ds, detail,dsUpdatePDTable}, null);
     LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_LOSS);
     data.setMessage(showJavaScript("backList();"));
   }
   /**
    * 校验从表表单信息从表输入的信息的正确性
    * @return null 表示没有信息
    */
   private String checkDetailInfo()
   {
     String temp = null;
     RowMap detailrow = null;
     if(d_RowInfos.size()<1)
       return showJavaScript("alert('不能保存空的数据')");
     //ArrayList list = new ArrayList(d_RowInfos.size());
     for(int i=0; i<d_RowInfos.size(); i++)
     {
       int row = i+1;
       detailrow = (RowMap)d_RowInfos.get(i);
       String cpid = detailrow.get("cpid");
       if(cpid.equals(""))
         return showJavaScript("alert('第"+row+"行产品不能为空');");
       /**
        if(list.contains(cpid))
         return showJavaScript("alert('第"+row+"行产品重复');");
         else
         list.add(cpid);
         */
       String sl = detailrow.get("sl");
       if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
         return temp;
       if(sl.length()>0 && sl.equals("0"))
         return showJavaScript("alert('第"+row+"行数量不能为零！');");
     }
     return null;
   }

   /**
    * 校验主表表表单信息从表输入的信息的正确性
    * @return null 表示没有信息,校验通过
    */
   private String checkMasterInfo()
   {
     RowMap rowInfo = getMasterRowinfo();
     String temp = rowInfo.get("sfrq");
     if(temp.equals(""))
       return showJavaScript("alert('收发日期不能为空！');");
     else if(!isDate(temp))
       return showJavaScript("alert('非法收发日期！');");
     temp = rowInfo.get("storeid");
     if(temp.equals(""))
       return showJavaScript("alert('请选择仓库！');");
     return null;
   }
  }
  /**
   * 2004-5-2 19:00 明细资料数据集页面翻页功能.
   */
  class Turn_Page implements Obactioner
  {
    /**
     * 按页翻动明细数据集的数据
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //保存输入的明细信息
     putDetailInfo(data.getRequest());
    }
  }
}
