package engine.erp.quality;

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
import engine.util.StringUtils;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 质量管理--产品合格证</p>
 * <p>Description: 质量管理--产品合格证<</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author
 * @version 1.0
 */

public final class B_CertifiedCard extends BaseAction implements Operate
{
  private static final String Quality_SQL = "SELECT * FROM zl_certifiedcard WHERE ? ORDER BY carddate DESC";//
  //查询过后总数量
  private static final String MASTER_SUM_SQL    = "SELECT SUM(nvl(salenum,0)) zsl,SUM(nvl(pagenum,0)) zhssl,SUM(nvl(producenum,0)) zscsl FROM zl_certifiedcard WHERE  ? ";

  private static final String MASTER_STRUT_SQL = "SELECT * FROM zl_certifiedcard WHERE 1<>1";//
  private static final String MASTER_SQL    = "SELECT zl_certifiedcard.*,kc_dm.cpbm,kc_dmsx.sxz,kc_dm.pm||kc_dm.gg product FROM zl_certifiedcard ,kc_dm ,kc_dmsx  WHERE zl_certifiedcard.cpid=kc_dm.cpid AND zl_certifiedcard.dmsxid=kc_dmsx.dmsxid  ?  ?  ? ? ";


 /**
   * 建立收发单据列表信息的数据集
   */
  private EngineDataSet dsWorkCheck = new EngineDataSet();
  //private EngineDataSet dsMasterTable = new EngineDataSet();//主表数据集
  /**
   *查询总数量
   */
  private EngineDataSet dsSumNumber = new EngineDataSet();
  public  HtmlTableProducer table = new HtmlTableProducer(dsWorkCheck, "zl_certifiedcard", "zl_certifiedcard");//查询得到数据库中配置的字段
  /**
   * 用于定位数据集
   */
  private EngineRow locateRow = null;
  public  String loginid = "";   //登录员工的ID
  /**
   * 保存用户输入的信息
   */
  private RowMap rowInfo = new RowMap();

  /**
   * 是否在添加状态
   */
  public  boolean isAdd = true;
  public  boolean isPrint = true;
  private String qtyFormat = null;
  public boolean submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
  /**
   * 保存修改操作的行记录指针
   */
  private long  editrow = 0;
  //
  private String dygs=null;
  private String workNo = null;
  private User user = null;
  public String zsl=null;
  public String zhssl = null;
  public String zscsl = null;
  /**
   * 点击返回按钮的URL
   */
  public  String retuUrl = null;
  private String zdrid=null, zdr=null;
  /**
    * 点击排序
   */
  public  ArrayList listCorpType = new ArrayList();
  public  ArrayList orderFieldCodes = new ArrayList(); //排序的字段编码
  public  ArrayList orderFieldNames = new ArrayList(); //排序的字段名称
  public  ArrayList selectedOrders  = null;
  private String    orderBy = "";

  /**
   * 得到收发单据信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回收发单据信息的实例
   */
  public static B_CertifiedCard getInstance(HttpServletRequest request)
  {
    B_CertifiedCard b_CertifiedBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "CertifiedCardBean";
      b_CertifiedBean = (B_CertifiedCard)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_CertifiedBean == null)
      {

        b_CertifiedBean = new B_CertifiedCard();
        LoginBean loginBean = LoginBean.getInstance(request);
        b_CertifiedBean.qtyFormat = loginBean.getQtyFormat();
        b_CertifiedBean.zdrid=loginBean.getUserID();
        b_CertifiedBean.zdr=loginBean.getUserName();
        b_CertifiedBean.user = loginBean.getUser();
        b_CertifiedBean.loginid = loginBean.getUserID();
        b_CertifiedBean.workNo = loginBean.getUser().getWorkNo();
        b_CertifiedBean.dsWorkCheck.setColumnFormat("salenum", b_CertifiedBean.qtyFormat);
        b_CertifiedBean.dsWorkCheck.setColumnFormat("pagenum", b_CertifiedBean.qtyFormat);
        b_CertifiedBean.dsWorkCheck.setColumnFormat("producenum", b_CertifiedBean.qtyFormat);
        session.setAttribute(beanName, b_CertifiedBean);
      }
    }
    return b_CertifiedBean;
  }
  /**
   * 构造函数
   */
  private B_CertifiedCard()
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
    //setDataSetProperty(dsMasterTable, MASTER_SQL);
    setDataSetProperty(dsWorkCheck, null);
    setDataSetProperty(dsSumNumber, null);
    //dsWorkCheck.setSort(new SortDescriptor("", new String[]{"cardid"}, new boolean[]{false}, null, 0));
    dsWorkCheck.setSequence(new SequenceDescriptor(new String[]{"cardid"}, new String[]{"S_zl_certifiedCard"}));
    //添加操作的触发对象
    B_WorkProcedure_Add_Edit add_edit = new B_WorkProcedure_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_WorkProcedure_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_WorkProcedure_Post());
    addObactioner(String.valueOf(DEL), new B_WorkProcedure_Delete());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());
    addObactioner(String.valueOf(ORDERBY), new Orderby());
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
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsWorkCheck.isOpen() && dsWorkCheck.changesPending())
        dsWorkCheck.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  //----Implementation of the BaseAction abstract class
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsWorkCheck != null){
      dsWorkCheck.closeDataSet();
      dsWorkCheck = null;
    }
    if(dsSumNumber != null){
      dsSumNumber.closeDataSet();
      dsSumNumber = null;
    }
    log = null;
    rowInfo = null;
    locateRow = null;
    if(table != null)
    {
      table.release();
      table = null;
    }
  }
  //----Implementation of the BaseAction abstract class
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName()
  {
    return getClass();
  }
  //
  public String getCardSet()
 {
   return dygs;
  }

  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    //String today = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
    //是否时添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();
    if(!isAdd)
      rowInfo.put(getOneTable());
    else{
      rowInfo.put("cardDate",today);
      rowInfo.put("qualityer", workNo);
      rowInfo.put("creator", zdr);
      //b_CertifiedBean.zdr=loginBean.getUserName();
      //loginBean.
    }
  }

  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    //if(!dsWorkCheck.isOpen())
      //dsWorkCheck.open();
    return dsWorkCheck;
  }

  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }
  /**
   * 初始化操作的触发类
   */
  class B_WorkProcedure_Init implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发初始化操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<cardID >notifyObactioners</cardID >方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      table.init(data.getRequest(), user.getUserId());
      table.getWhereInfo().clearWhereValues();
      //先提取数据结果
      if(!dsWorkCheck.isOpen())
      {
        dsWorkCheck.setQueryString(MASTER_STRUT_SQL);
        dsWorkCheck.openDataSet();
      }

      String condition = table.getWhereInfo().getWhereQuery();
      if(condition == null || condition.length()==0)
        condition = user.getHandleDeptValue("deptid");
      else
        condition += " AND "+user.getHandleDeptValue("deptid");
      dsWorkCheck.setQueryString(combineSQL(Quality_SQL, "?", new String[]{condition}) );//部门权限
      dsWorkCheck.readyRefresh();

      dsSumNumber.setQueryString(combineSQL(MASTER_SUM_SQL, "?", new String[]{condition}));
      if(dsSumNumber.isOpen())
        dsSumNumber.refresh();
      else
        dsSumNumber.openDataSet();
      int cn = dsSumNumber.getRowCount();
      if(dsSumNumber.getRowCount()<1){
        zsl="0";
        zhssl="0";
        zscsl ="0";
      }
      else{
        zsl=dsSumNumber.getValue("zsl");
        zhssl=dsSumNumber.getValue("zhssl");
        zscsl=dsSumNumber.getValue("zscsl");
      }

      zsl = zsl.equals("")?"0":zsl;
      zhssl = zhssl.equals("")?"0":zhssl;
      zscsl = zscsl.equals("")?"0":zscsl;

      listCorpType.clear();
      //排序的字段
      orderBy = "";//"dwdq.areacode, d.dwdm";
      if(selectedOrders == null)
        selectedOrders = new ArrayList();
      else
        selectedOrders.clear();

      orderFieldCodes.clear();
      orderFieldNames.clear();

      FieldInfo[] fields = table.getAllField();
      for(int i=0; i<fields.length; i++)
      {
        String linkTable = fields[i].getLinktable();
        if(linkTable == null)
        {
          orderFieldCodes.add(table.getTableAliasName()+"."+fields[i].getFieldcode());
          orderFieldNames.add(fields[i].getFieldname());
        }
        else
        {
          String[] fieldCodes = fields[i].getShowFields();
          String[] fieldNames = fields[i].getShowFieldNames();
          for(int j=0; fieldCodes!=null && j<fieldCodes.length; j++)
          {
            orderFieldCodes.add(linkTable+"."+fieldCodes[j]);
            orderFieldNames.add(fieldNames[j]);
          }
        }
      }


      zsl = zsl.equals("")?"0":zsl;
      zhssl = zhssl.equals("")?"0":zhssl;
      zscsl = zscsl.equals("")?"0":zscsl;
      //data.setMessage(showJavaScript("showFixedQuery();"));
    }
  }
  /**
   * 初始化操作的触发类
   */
  class Search implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发初始化操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<cardID >notifyObactioners</cardID >方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      table.getWhereInfo().setWhereValues(data.getRequest());
      //fixedQuery.setSearchValue(data.getRequest());
      String condition = table.getWhereInfo().getWhereQuery();
      if(condition == null || condition.length()==0)
        condition = user.getHandleDeptValue("deptid");
      else
        condition += " AND "+user.getHandleDeptValue("deptid");
      dsWorkCheck.setQueryString(combineSQL(Quality_SQL, "?", new String[]{condition}) );//部门权限
      dsWorkCheck.readyRefresh();

      dsSumNumber.setQueryString(combineSQL(MASTER_SUM_SQL, "?", new String[]{condition}));
      if(dsSumNumber.isOpen())
        dsSumNumber.refresh();
      else
        dsSumNumber.openDataSet();

      int cn = dsSumNumber.getRowCount();
      if(dsSumNumber.getRowCount()<1){
        zsl="0";
        zhssl="0";
        zscsl ="0";
      }
      else{
        zsl=dsSumNumber.getValue("zsl");
        zhssl=dsSumNumber.getValue("zhssl");
        zscsl=dsSumNumber.getValue("zscsl");
      }

      zsl = zsl.equals("")?"0":zsl;
      zhssl = zhssl.equals("")?"0":zhssl;
      zscsl = zscsl.equals("")?"0":zscsl;
    }
  }

  /**
   * 添加或修改操作的触发类
   */
  class B_WorkProcedure_Add_Edit implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 添加或修改的触发操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<cardID >notifyObactioners</cardID >方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd = action.equals(String.valueOf(ADD));
      isPrint = action.equals(String.valueOf(EDIT));
      if(!isAdd)
      {
        dsWorkCheck.goToRow(Integer.parseInt(data.getParameter("rownum")));
        String cpid = dsWorkCheck.getValue("cpid");
        dygs = dataSetProvider.getSequence("SELECT b.dygs FROM kc_dm a, kc_chlb b  WHERE a.cpid="+cpid);
        editrow = dsWorkCheck.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }

  /**
   * 保存操作的触发类
   */
  class B_WorkProcedure_Post implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发保存操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<cardID >notifyObactioners</cardID >方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //校验数据
      rowInfo.put(data.getRequest());
      String dmsxid  = rowInfo.get("dmsxid");//规格属性
      String cpid = rowInfo.get("cpid");//产品ID//产品编码
      String cardDate = rowInfo.get("cardDate");//合格证日期
      String qualityer = rowInfo.get("qualityer");//质检员
      String saleNum = rowInfo.get("saleNum");//数量
      String otherNum = rowInfo.get("otherNum");//其他数量
      String produceNum = rowInfo.get("produceNum");//生产用数量
      String batNo = rowInfo.get("batNo");//生产批号
      String createDate = rowInfo.get("createDate");//制单日期
      String creator =rowInfo.get("creator");//制单人
      String memo = rowInfo.get("memo");//备注
      String pageNum=rowInfo.get("pageNum");//张数
      String grossNum = rowInfo.get("grossNum");//毛重
      /*if(dmsxid.equals("")){
        *data.setMessage(showJavaScript("alert('检验代码不能为空！');"));
        *return;
      }*/
      String temp = "";
      if(saleNum.length()>0 && (temp = checkNumber(saleNum, "净重")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if(otherNum.length()>0 && (temp = checkNumber(otherNum, "纸芯重")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if(grossNum.length()>0 && (temp = checkNumber(grossNum, "毛重")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if(pageNum.length()>0 && (temp = checkNumber(pageNum, "张数")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if(produceNum.length()>0 && (temp = checkNumber(produceNum, "生产用数量")) != null)
      {
        data.setMessage(temp);
        return;
      }
      String count="0";
      if(isAdd)
        count = dataSetProvider.getSequence("SELECT COUNT(*) FROM zl_certifiedcard WHERE batno='"+batNo+"' AND cpid='"+cpid+"'");
      else{
        String cardid= ds.getValue("cardid");
        count = dataSetProvider.getSequence("SELECT COUNT(*) FROM zl_certifiedcard WHERE batno='"+batNo+"' AND cpid='"+cpid+"' AND cardid<>'"+cardid+"'");
      }
      if(!count.equals("0"))
      {
        data.setMessage(showJavaScript("alert('该产品已经存在该批号')"));
        return;
      }
      if(!isAdd)
        ds.goToInternalRow(editrow);//数据库指针

      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("deptid", user.getDeptId());
        ds.setValue("cardid", "-1");
      }
      ds.setValue("dmsxid",dmsxid);
      ds.setValue("cpid", cpid);
      ds.setValue("cardDate", cardDate);
      ds.setValue("qualityer", qualityer);
      ds.setValue("saleNum", saleNum);
      ds.setValue("otherNum", otherNum);
      ds.setValue("produceNum", produceNum);
      ds.setValue("batNo", batNo);
      String date =  new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      ds.setValue("createDate",date);
      ds.setValue("creator", creator);
      ds.setValue("creatorID",zdrid);
      ds.setValue("memo",memo);
      ds.setValue("produceGrade",rowInfo.get("produceGrade"));
      ds.setValue("dispartNum",rowInfo.get("dispartNum"));
      ds.setValue("produceArea",rowInfo.get("produceArea"));
      ds.setValue("lineNum",rowInfo.get("lineNum"));
      ds.setValue("pageNum",rowInfo.get("pageNum"));
      ds.setValue("grossnum",rowInfo.get("grossnum"));
      ds.post();
      ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_QUALITY_TOOLTYPE);//同步刷新数据
      editrow = ds.getInternalRow();//重新得到数据库指针
      isAdd = false;
      isPrint = true;
    }
  }
  /**
  * 排序操作
  */
 final class Orderby implements Obactioner
 {
   //----Implementation of the Obactioner interface
   /**
    * 排序触发操作
    * @parma  action 触发执行的参数（键值）
    * @param  o      触发者对象
    * @param  data   传递的信息的类
    * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
    */
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     orderBy = data.getParameter("sortColumnStr", "");
     selectedOrders.clear();
     if(orderBy.length() > 0)
     {
       String[] sorts = StringUtils.parseString(orderBy, ",");
       for(int i=0; i<sorts.length; i++){
         selectedOrders.add(sorts[i]);
       }
     }
     String SQL = table.getWhereInfo().getWhereQuery();
     String s=orderBy;
     SQL = combineSQL(MASTER_SQL, "?", new String[]{SQL.length()==0 ? " and 1=1 " : " and "+SQL+" ",
                      " and ",
                      "zl_certifiedcard."+user.getHandleDeptValue("deptid"),
                      orderBy.length() > 0 ? " ORDER BY "+orderBy+" desc " : " ORDER BY zl_certifiedcard.cardID desc"
                      });
     dsWorkCheck.setQueryString(SQL);
     dsWorkCheck.readyRefresh();
   }
  }

  /**
   * 删除操作的触发类
   */
  class B_WorkProcedure_Delete implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发删除操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<cardID >notifyObactioners</cardID >方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.deleteRow();
      ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_QUALITY_TOOLTYPE);//同步刷新数据
    }
  }
}
