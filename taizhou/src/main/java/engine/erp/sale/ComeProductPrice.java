package engine.erp.sale;

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
 * <p>Title: 销售管理-产品销售定价</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ENGINE</p>
 * @version 1.0
 */
public final class ComeProductPrice extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1009";
  /**
   *默认状态是不能修改
   * */
  private boolean canModify = false;
  /**
   *对应修改按钮
   * */
  public  boolean isAdd = false;//jjj
  private long    masterRow = -1;         //保存主表修改操作的行记录指针//jjj

  public static final String MODIFY = "1005";
  public static final String PRICE_ADD  = "3002";//jjj
  public static final String PRICE_POST  = "3003";//jjj
  public static final String PRICE_DEL  = "3004";//jjj
  /**
   * 提取物资销售单价信息的SQL语句
   */
  private static final String PRODUCT_PRICE_SQL = "SELECT * FROM xs_wzdj WHERE isNet=1 and fgsid=? ? ";//
  private static final String PRODUCT_LIST_SQL = "SELECT * FROM VW_COME_SALE_PRODUCT_PRICE WHERE (fgsid=? OR fgsid IS NULL) ? ORDER BY cpbm";
  private static final String PRODUCT_PRICESET_SQL = "SELECT * FROM xs_areaprice WHERE 1=1  ?  ";//jjj

  /**
   *本地数据集
   * 保存物资销售单价信息的数据集
   */
  private EngineDataSet dsComePriceList = new EngineDataSet();
  private EngineDataSet dsCome = new EngineDataSet();

  private EngineDataSet dsAreaProductPrice = new EngineDataSet();//jjj
  private ArrayList price_RowInfos = null; //多行记录的引用

  private ArrayList d_RowInfos = null; //多行记录的引用
  public  String retuUrl = null;//点击返回按钮的URL
  private boolean isInitQuery = false;
  public  String loginName = ""; //登录员工的姓名
  public  String personid = ""; //登录员工personid
  private String fgsID = null;   //分公司ID
  private String swzdjid ="";

  private QueryFixedItem fixedQuery = new QueryFixedItem();//定义固定查询类
  /**
   * 得到物资销售单价信息的实例
   * @param request jsp请求
   * @return 返回物资销售单价信息的实例
   */
  public static ComeProductPrice getInstance(HttpServletRequest request)
  {
    ComeProductPrice b_ComeProductBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_ComeProductBean";//名称-值对应
      b_ComeProductBean = (ComeProductPrice)session.getAttribute(beanName);
      if(b_ComeProductBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        String fgsID = loginBean.getFirstDeptID();
        b_ComeProductBean = new ComeProductPrice(fgsID);//调用构造函数
        b_ComeProductBean.loginName = loginBean.getUserName();
        b_ComeProductBean.personid=loginBean.getUserID();
        session.setAttribute(beanName,b_ComeProductBean);
      }
    }
    return b_ComeProductBean;
  }
  /**
   * 构造函数(实例变量:分公司ID为初始化参数)
   */
  private ComeProductPrice(String fgsid)
  {
    this.fgsID = fgsid;
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
    //初始化数据集的SQL语句.
    setDataSetProperty(dsComePriceList, null);
    setDataSetProperty(dsCome, null);

    setDataSetProperty(dsAreaProductPrice, combineSQL(PRODUCT_PRICESET_SQL,"?",new String[]{" AND 1=1 "}));//jjj

    dsAreaProductPrice.setSort(new SortDescriptor("", new String[]{"cjno"}, new boolean[]{false}, null, 0));//设置排序方式 jjj
    dsAreaProductPrice.setSequence(new SequenceDescriptor(new String[]{"areapriceid"}, new String[]{"s_xs_area_price"}));//设置主健的sequence jjj

    dsCome.setSequence(new SequenceDescriptor(new String[]{"wzdjID"}, new String[]{"s_xs_wzdj"}));
    //dsProductPriceList数据集在数据装载完后,才能对其执行操作(装载完发出通告)
    dsComePriceList.addLoadListener(new com.borland.dx.dataset.LoadListener() {
      public void dataLoaded(LoadEvent e)
      {
        initRowInfo(false, true);
      }
    });
    //添加操作的触发对象
    B_PriceSet_Edit add_edit = new B_PriceSet_Edit();//jjj
    addObactioner(String.valueOf(INIT), new ComeProductPrice_Init());//初始化
    addObactioner(String.valueOf(POST), new ComeProductPrice_Post());//保存
    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());//查询
    addObactioner(String.valueOf(MODIFY), new Modify());

    addObactioner(String.valueOf(EDIT), add_edit);//修改 jjj
    addObactioner(PRICE_ADD, new PriceAdd());//jjj
    addObactioner(PRICE_POST, new PricePost());//jjj
    addObactioner(PRICE_DEL, new PriceDel());//jjj
    addObactioner(String.valueOf(MODIFY), new Modify());
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
      if(dsCome.changesPending())
        dsCome.reset();
      if(dsComePriceList.changesPending())
        dsComePriceList.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsComePriceList != null){
      dsComePriceList.close();
      dsComePriceList = null;
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
    if(!dsComePriceList.isOpen())
    {
      dsComePriceList.open();
    }
    int rownum = dsComePriceList.getRowCount();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("jjbl", rowInfo.get("jjbl_"+i));//奖金比例
      detailRow.put("xsj", rowInfo.get("xsj_"+i));//销售价
      detailRow.put("xsdj", rowInfo.get("xsdj_"+i));//销售底价
      detailRow.put("xsjzj", rowInfo.get("xsjzj_"+i));//销售基准价
      detailRow.put("xstcl", rowInfo.get("xstcl_"+i));//销售提成率
      detailRow.put("wzdjID", rowInfo.get("wzdjID_"+i));//物资单价ID
      detailRow.put("hkts", rowInfo.get("hkts_"+i));//
      detailRow.put("hktcl", rowInfo.get("hktcl_"+i));//
      d_RowInfos.set(i,detailRow);
    }
  }
  /**
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private final void putAreaDetailInfo(HttpServletRequest request)
  {
    RowMap rowInfo = new RowMap();
    //保存网页的所有信息
    rowInfo.put(request);
    int rownum = dsAreaProductPrice.getRowCount();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      String dd=rowInfo.get("xs_jzj_"+i);
      detailRow = (RowMap)price_RowInfos.get(i);
      detailRow.put("cjno", rowInfo.get("cjno_"+i));//奖金比例
      detailRow.put("xs_jzj", rowInfo.get("xs_jzj_"+i));//销售基准价
      price_RowInfos.set(i,detailRow);
    }
  }
  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
  /*得到从表多列的信息*/
  public final RowMap[] getSectionRows() {
    RowMap[] rows = new RowMap[price_RowInfos.size()];
    price_RowInfos.toArray(rows);
    return rows;
  }
  /*得到状态*/
  public final boolean getState()
  {
    return canModify;
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
    StringBuffer buf = null;
    if(d_RowInfos == null)
      d_RowInfos = new ArrayList(dsComePriceList.getRowCount());
    else if(isInit)
      d_RowInfos.clear();

    dsComePriceList.first();
    for(int i=0; i<dsComePriceList.getRowCount(); i++)
    {
      if(buf == null)
        buf = new StringBuffer("AND cpid IN(").append(dsComePriceList.getValue("cpid"));
      else
        buf.append(",").append(dsComePriceList.getValue("cpid"));
      RowMap row = new RowMap(dsComePriceList);
      d_RowInfos.add(row);
      dsComePriceList.next();
    }

    if(buf == null)
      buf =new StringBuffer();
    else
      buf.append(")");

    String SQL = combineSQL(PRODUCT_PRICE_SQL, "?", new String[]{fgsID, buf.toString()});
    dsCome.setQueryString(SQL);
    if(dsCome.isOpen())
      dsCome.refresh();
    else
      dsCome.openDataSet();
  }
  /**
   * 初始化行信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   */
  private final void initRowInfo(boolean isAdd)
  {
    price_RowInfos = new ArrayList(dsAreaProductPrice.getRowCount());
    dsAreaProductPrice.first();
    for(int i=0; i<dsAreaProductPrice.getRowCount(); i++)
    {
      RowMap row = new RowMap(dsAreaProductPrice);
      price_RowInfos.add(row);
      dsAreaProductPrice.next();
    }
  }
  /*得到表对象*/
  public final EngineDataSet getDetailTable()
  {
    return dsComePriceList;
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
   * 添加或修改操作的触发类
   */
  class B_PriceSet_Edit implements Obactioner//jjj
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      swzdjid = data.getParameter("rownum");

      String sql = combineSQL(PRODUCT_PRICESET_SQL,"?",new String[]{" AND wzdjid='"+swzdjid+"'"});
      //if(!dsAreaProductPrice.isOpen())
      //{
      if(dsAreaProductPrice.isOpen())
        dsAreaProductPrice.close();
      setDataSetProperty(dsAreaProductPrice,sql);
      dsAreaProductPrice.open();
      //}else
      //  dsAreaProductPrice.setQueryString(sql);
      dsAreaProductPrice.readyRefresh();
      initRowInfo(true);
    }
  }
  /**
   * 初始化操作的触发类
   */
  class ComeProductPrice_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      canModify= false;
      dsComePriceList.setQueryString(combineSQL(PRODUCT_LIST_SQL,"?",new String[]{fgsID,""}));
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      fixedQuery.getSearchRow().clear();
      //data.setMessage(showJavaScript("showFixedQuery()"));
    }
  }
  /**
   * 添加查询操作的触发类
   */
  class FIXED_SEARCH implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      initQueryItem(data.getRequest());
      QueryBasic queryBasic = fixedQuery;
      queryBasic.setSearchValue(data.getRequest());
      String SQL = queryBasic.getWhereQuery();
      String xsj = request.getParameter("xsj");
      String jjbl = request.getParameter("jjbl");
      String xsdj = request.getParameter("xsdj");
      String xsjzj = request.getParameter("xsjzj");
      String xstcl = request.getParameter("xstcl");
      String hktcl = request.getParameter("hktcl");
      String hkts = request.getParameter("hkts");

      if(SQL.length() > 0)
        SQL = " AND " + SQL;
      if(xsj!=null)
        SQL = SQL+" AND xsj is null ";
      if(jjbl!=null)
        SQL = SQL+" AND jjbl is null ";
      if(xsdj!=null)
        SQL = SQL+" AND xsdj is null ";
      if(xsjzj!=null)
        SQL = SQL+" AND xsjzj is null ";
      if(xstcl!=null)
        SQL = SQL+" AND xstcl is null ";
      if(hktcl!=null)
        SQL = SQL+" AND hktcl is null ";
      if(hkts!=null)
        SQL = SQL+" AND hkts is null ";

      SQL = combineSQL(PRODUCT_LIST_SQL, "?", new String[]{fgsID, SQL}) ;
      if(!dsComePriceList.getQueryString().equals(SQL))
      {
        dsComePriceList.setQueryString(SQL);
        dsComePriceList.setRowMax(null);
      }
    }

    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      EngineDataSet master = dsComePriceList;
      //初始化固定的查询项目
      //往来单位dwtxId;信誉额度xyed;信誉等级xydj;回款天数hkts;
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("jjbl"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("jjbl"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("xsj"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("xsj"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("xsdj"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("xsdj"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("xsjzj"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("xsjzj"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("xstcl"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("xstcl"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("hkts"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("hkts"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("hktcl"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("hktcl"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("cpid"), "VW_COME_SALE_PRODUCT_PRICE", "cpid", "cpbm", "cpbm", "="),
        new QueryColumn(master.getColumn("cpid"), "VW_COME_SALE_PRODUCT_PRICE", "cpid", "pm", "pm", "="),//从表品名
        new QueryColumn(master.getColumn("cpid"), "VW_COME_SALE_PRODUCT_PRICE", "cpid", "gg", "gg", "="),//从表规格
      });
      isInitQuery = true;
    }
  }
  /**
   *点击修改按钮时触发的操作
   * MODIFY
   * */
  class Modify implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      canModify=true;
    }
  }
  /**
   * 保存操作的触发类
   */
  class ComeProductPrice_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String czrq = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      RowMap detailrow = null;
      putDetailInfo(data.getRequest());
      String temp = null;
      if((temp = checkDetailInfo()) !=null)
      {
        data.setMessage(temp);
        return;
      }

      EngineRow row = new EngineRow(dsCome, "cpid");
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String cpid = detailrow.get("cpid");
        String jjbl = detailrow.get("jjbl");
        String xsj  = detailrow.get("xsj");
        String xsdj = detailrow.get("xsdj");
        String xsjzj = detailrow.get("xsjzj");
        String xstcl = detailrow.get("xstcl");
        dsComePriceList.setValue("cpid", cpid);
        dsComePriceList.setValue("jjbl", jjbl);
        dsComePriceList.setValue("xsj", xsj);
        dsComePriceList.setValue("xsdj", xsdj);
        dsComePriceList.setValue("xsjzj", xsjzj);
        dsComePriceList.setValue("xstcl", xstcl);
        dsComePriceList.setValue("fgsID",fgsID);
        dsComePriceList.setValue("hkts",detailrow.get("hkts"));
        dsComePriceList.setValue("hktcl",detailrow.get("hktcl"));
        dsComePriceList.setValue("fgsID",fgsID);
        dsComePriceList.setValue("czrq",czrq);
        dsComePriceList.setValue("czy",loginName);
        dsComePriceList.setValue("czyID",personid);
        dsComePriceList.post();

        row.setValue(0, cpid);
        if(!dsCome.locate(row, Locate.FIRST))
        {
          dsCome.insertRow(false);
          dsCome.setValue("wzdjID","-1");
        }
        dsCome.setValue("cpid", cpid);
        dsCome.setValue("jjbl", jjbl);
        dsCome.setValue("xsj", xsj);
        dsCome.setValue("xsdj", xsdj);
        dsCome.setValue("xsjzj", xsjzj);
        dsCome.setValue("xstcl", xstcl);
        dsCome.setValue("fgsID",fgsID);
        dsCome.setValue("hkts",detailrow.get("hkts"));
        dsCome.setValue("hktcl",detailrow.get("hktcl"));
        dsCome.setValue("czrq",czrq);
        dsCome.setValue("czy",loginName);
        dsCome.setValue("czyID",personid);
        dsCome.setValue("isNet","1");
        dsCome.post();
      }
      dsCome.saveChanges();
      dsComePriceList.resetPendingStatus(true);
      canModify=false;
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String jjbl = detailrow.get("jjbl");
        if(jjbl.length()!=0)
        {
          if((temp = checkNumber(jjbl, "奖金比例")) != null)
            return temp;
        }
        String xsj = detailrow.get("xsj");
        if(xsj.length()!=0)
        {
          if((temp = checkNumber(xsj, "销售价")) != null)
            return temp;
        }
        String hkts = detailrow.get("hkts");
        if(hkts.length()!=0)
        {
          if((temp = checkNumber(hkts, "回款天数")) != null)
            return temp;
        }
        String hktcl = detailrow.get("hktcl");
        if(hktcl.length()!=0)
        {
          if((temp = checkNumber(hktcl, "回款提成率")) != null)
            return temp;
        }
        String xsdj = detailrow.get("xsdj");
        if(xsdj.length()!=0)
        {
          if((temp = checkNumber(xsdj, "销售底价")) != null)
            return temp;
        }
        String xsjzj = detailrow.get("xsjzj");
        if(xsjzj.length()!=0)
        {
          if((temp = checkNumber(xsjzj, "基准价")) != null)
            return temp;
        }
        String xstcl = detailrow.get("xstcl");
        if(xstcl.length()!=0)
        {
          if((temp = checkNumber(xstcl, "差价提成率")) != null)
            return temp;
        }
      }
      return null;
    }
  }
  /**
   * 检测字符串是否是数字型的
   * @param value 需要检测的字符串
   * @param caption javascipt需要显示的标题
   * @return 若返回null表示是数字，非null为javasrcip语句
   */
  public static String checkNumber(String value, String caption)
  {
    try{
      Double.parseDouble(value);
    }catch(Exception ex){
      return showJavaScript("alert('非法 "+caption+"！');");
    }
    return null;
  }



  /**
   * BOM子件领料工段添加
   */
  final class PriceAdd implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putAreaDetailInfo(data.getRequest());
      dsAreaProductPrice.insertRow(false);
      dsAreaProductPrice.setValue("wzdjid",swzdjid);
      dsAreaProductPrice.post();
      RowMap newrow = new RowMap(dsAreaProductPrice);
      price_RowInfos.add(newrow);
      isAdd=true;
    /*
    putSectionInfo(data);
    RowMap row = new RowMap();
    row.put("bomid", bomId);
    sectionRows.add(row);
    */
    }
  }

  /**
   * 保存操作的触发类
   */
  class PricePost implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      RowMap detailrow = null;
      putAreaDetailInfo(data.getRequest());
      //得到主表主键值
      //EngineRow row = new EngineRow(dsAreaProductPrice, "wzdjid");
      String temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      dsAreaProductPrice.first();
      for(int i=0; i<price_RowInfos.size(); i++)
      {
        detailrow = (RowMap)price_RowInfos.get(i);
        String wzdjid = detailrow.get("wzdjid");
        String cjno = detailrow.get("cjno");
        String xs_jzj  = detailrow.get("xs_jzj");
        dsAreaProductPrice.setValue("wzdjid", wzdjid);
        dsAreaProductPrice.setValue("cjno", cjno);
        dsAreaProductPrice.setValue("xs_jzj", xs_jzj);
        dsAreaProductPrice.next();
        dsAreaProductPrice.post();
      }
      dsAreaProductPrice.saveChanges();
      initRowInfo(true);
      //dsAreaProductPrice.resetPendingStatus(true);
      //canModify=false;
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo()
    {
      HashSet htmp = new HashSet();
      htmp.clear();
      String temp = null;
      RowMap detailrow = null;
      /**明细**/
      for(int i=0; i<price_RowInfos.size(); i++)
      {
        detailrow = (RowMap)price_RowInfos.get(i);
        temp = detailrow.get("xs_jzj");
        if(temp.equals(""))
          return showJavaScript("alert('基准价不能为空！');");
        if((temp = checkNumber(temp, "基准价")) != null)
          return showJavaScript("alert('基准价非法！');");
        String wzdjid="c"+detailrow.get("wzdjid");
        String cjno="d"+detailrow.get("cjno").trim();
        if(!htmp.add(wzdjid+cjno))
          return showJavaScript("alert('所选第"+(i+1)+"行重复!');");
      }
      return null;
    }
  }
  /**
   * 主表删除操作
   */
  class PriceDel implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      masterRow = dsAreaProductPrice.getInternalRow();
      dsAreaProductPrice.goToInternalRow(masterRow);
      dsAreaProductPrice.deleteRow();
      dsAreaProductPrice.saveDataSets(new EngineDataSet[]{dsAreaProductPrice}, null);
      initRowInfo(true);
    }
  }


}