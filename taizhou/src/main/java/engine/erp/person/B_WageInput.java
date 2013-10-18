package engine.erp.person;

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
 * <p>Title: 人力资源-工资录入</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ENGINE</p>
 * @version 1.0
 */
public final class B_WageInput extends BaseAction implements Operate
  {

    public  static final String DETAIL_MULTI_ADD = "10801";
    public  static final String TO_NEXT_MONTH = "4938434";//结转至下月
    public  static final String MONTH_CHANGE = "555558888";//所选月份变化
    public  static final String OVER = "88888888888";
    public  static final String CITE  = "10001";          //引用计件工资

    private static final String MASTER_STRUT_SQL = "SELECT * FROM rl_gzkxzb WHERE 1<>1 ";
    private static final String DETAIL_STRUT_SQL = "SELECT * FROM rl_gzkx WHERE 1<>1 ";

    private static final String MASTER_SQL    = "SELECT * FROM rl_gzkxzb WHERE 1=1 ? ORDER BY gzkxzbID DESC ";
    private static final String DETAIL_SQL    = "SELECT a.* FROM rl_gzkx a,emp b WHERE a.personid=b.personid ? ORDER BY a.deptid,b.bm ";//

    private static final String DETAIL_SEARCH_SQL    = "SELECT * FROM (SELECT a.*,b.bm FROM rl_gzkx a,emp b WHERE a.personid=b.personid   ORDER BY a.deptid,b.bm) WHERE 1=1 ? ORDER BY  deptid,bm ";//
    private static final String FIELD_LIST_SQL    = "SELECT * FROM rl_gzkxsz where 1=1 ? ORDER BY PXH";//
    //一次引入全部
    private static final String DETAIL_ADD_SQL
        = "SELECT a.personid, a.deptid FROM emp a "
        + "WHERE (SELECT COUNT(*) FROM rl_gzkx b WHERE b.gzkxzbid = ? AND b.personid = a.personid) = 0 "
        + "AND a.deptid IS NOT NULL AND a.isDelete='0'";
    //引入以前的数据
    private static final String GET_OLD_DATA_SQL
        = "INSERT INTO rl_gzkx(gzkxid, personid,deptid,? gzkxzbid) "
        + "SELECT s_rl_gzkx.NEXTVAL, a.personid,a.deptid,? ?  "
        + "FROM rl_gzkx a, emp b WHERE gzkxzbid=? and a.personid=b.personid and b.isDelete='0' and a.personid not in(select personid from rl_gzkx where gzkxzbid=?)";
    private static final String YEAR_SQL="select DISTINCT nf from rl_gzkxzb ORDER BY nf DESC";
    private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
    private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
    private EngineDataSet dsfieldListTable  = new EngineDataSet();//字段
    //private boolean isMasterAdd = true;    //是否在添加状态
    private long    masterRow = -1;         //保存主表修改操作的行记录指针
    private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
    private ArrayList d_RowInfos = null; //从表多行记录的引用
    private LookUp personBean = null;
    private boolean isInitQuery = false; //是否已经初始化查询条件
    private QueryBasic fixedQuery = new QueryFixedItem();
    public  String retuUrl = null;
    public  String loginId = "";   //登录员工的ID
    public  String loginCode = ""; //登陆员工的编码
    public  String loginName = ""; //登录员工的姓名
    private String qtyFormat = null, priceFormat = null, sumFormat = null;
    private String fgsid = null;   //分公司ID
    private String gzkxzbID="";//主表
    public String  year="";//
    public String month="";//
    public String currentmonth="";//当前
    public String currentyear="";//当前
    public String sfjz = "";
    public String startday ="";
    public String endday ="";

    /**
     * 销售合同列表的实例
     * @param request jsp请求
     * @param isApproveStat 是否在审批状态
     * @return 返回销售合同列表的实例
     */
    public static B_WageInput getInstance(HttpServletRequest request)
    {
      B_WageInput b_WageInputBean = null;
      HttpSession session = request.getSession(true);
      synchronized (session)
      {
        String beanName = "b_WageInputBean";
        b_WageInputBean = (B_WageInput)session.getAttribute(beanName);
        if(b_WageInputBean == null)
        {
          //引用LoginBean
          LoginBean loginBean = LoginBean.getInstance(request);
          b_WageInputBean = new B_WageInput();
          b_WageInputBean.qtyFormat = loginBean.getQtyFormat();
          b_WageInputBean.priceFormat = loginBean.getPriceFormat();
          b_WageInputBean.sumFormat = loginBean.getSumFormat();
          b_WageInputBean.fgsid = loginBean.getFirstDeptID();
          b_WageInputBean.loginId = loginBean.getUserID();
          b_WageInputBean.loginName = loginBean.getUserName();
          //设置格式化的字段
          session.setAttribute(beanName, b_WageInputBean);
        }
      }
      return b_WageInputBean;
    }
    /**
     * 构造函数
     */
    private B_WageInput()
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
      setDataSetProperty(dsMasterTable, combineSQL(MASTER_SQL,"?",new String[]{" AND sfjz='0' "}));
      setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);
      setDataSetProperty(dsfieldListTable,combineSQL(FIELD_LIST_SQL,"?",new String[]{""}) );
      dsDetailTable.setTableName("RL_GZKX");
      dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"gzkxzbID"}, new String[]{"S_RL_GZKXZB"}));
      dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"gzkxID"}, new String[]{"S_RL_GZKX"}));
      dsDetailTable.setSort(new SortDescriptor("", new String[]{"deptid"}, new boolean[]{false}, null, 0));
      Detail_Post detailPost = new Detail_Post();
      Master_Add masterAdd=new Master_Add();
      addObactioner(String.valueOf(INIT), new Init());
      addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
      addObactioner(String.valueOf(POST), detailPost);
      addObactioner(String.valueOf(OVER), new Master_Over());//完成
      addObactioner(String.valueOf(DETAIL_MULTI_ADD), new Detail_Add());//选择
      addObactioner(String.valueOf(ADD), masterAdd);//结转至下月
      addObactioner(String.valueOf(MONTH_CHANGE), new MonthOnchange());
      addObactioner(String.valueOf(CITE), new Cite());
      addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
      dsDetailTable.addLoadListener(new com.borland.dx.dataset.LoadListener() {
        public void dataLoaded(LoadEvent e)
        {
          initRowInfo(true,false,true);
          initRowInfo(false,false,true);
        }
    });
    }
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
      if(dsfieldListTable != null){
        dsfieldListTable.close();
        dsfieldListTable = null;
      }
      log = null;
      m_RowInfo = null;
      d_RowInfos = null;
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
    private final void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit)
    {
      //是否是主表
      if(isMaster){
        if(isInit && m_RowInfo.size() > 0)
          m_RowInfo.clear();
        if(!isAdd)
          m_RowInfo.put(getMaterTable());
      }else
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
      B_WageFieldInfo b_WageFieldInfoBean  =  B_WageFieldInfo.getInstance(request);
      EngineDataSet fieldList = b_WageFieldInfoBean.getOneTable();
      RowMap rowInfo =  getMasterRowinfo();
      //保存网页的所有信息
      rowInfo.put(request);
      //从数据集中获取记录行数
      if(!dsDetailTable.isOpen())
      {
        dsDetailTable.open();
      }
      int rownum = dsDetailTable.getRowCount();
      RowMap detailRow = null;
      for(int i=0; i<rownum; i++)
      {
        detailRow = (RowMap)d_RowInfos.get(i);
        fieldList.first();
        for(int k=0;k<fieldList.getRowCount();k++)
        {
          String fieldName=fieldList.getValue("dyzdm");
          detailRow.put(fieldName, rowInfo.get(fieldName+"_"+i));//
          fieldList.next();
        }
        detailRow.put("deptid", rowInfo.get("deptid_"+i));//
        detailRow.put("personid", rowInfo.get("personid_"+i));//
        d_RowInfos.set(i,detailRow);
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
    /*得到字段列表表对象*/
    public final EngineDataSet getFieldListTable(){
      if(!dsfieldListTable.isOpen())
        dsfieldListTable.open();
      return dsfieldListTable;
    }
    public  final ArrayList getGSArrayList()
    {
      ArrayList name = new ArrayList();//名称
      ArrayList gs = new ArrayList();//公式
      ArrayList search = new ArrayList();//供查
      ArrayList ret = new ArrayList();//要返回的
      try{
        EngineDataSet tmp= new EngineDataSet();
        setDataSetProperty(tmp,combineSQL(FIELD_LIST_SQL,"?",new String[]{" and ly='2'"}));
        tmp.open();
        tmp.first();
        for(int i=0;i<tmp.getRowCount();i++)
        {
          name.add(tmp.getValue("dyzdm"));
          gs.add(tmp.getValue("jsgs"));
          search.add(tmp.getValue("jsgs"));
          tmp.next();
        }
        for(int j=0;j<name.size();j++)
        {
          String fieldname = (String)name.get(j);
          String replacegs = (String)search.get(j);//与fieldname对应的公式
          for(int h=0;h<gs.size();h++)
          {
            String newjsgs="";//新公式
            String jsgs = (String)gs.get(h);//要更新的公式
            if(jsgs.indexOf(fieldname)>-1)
            {
              newjsgs = engine.util.StringUtils.replaceIgnoreCase(jsgs,fieldname,"("+replacegs+")");
              gs.set(h,newjsgs);
            }
          }
        }
        EngineDataSet fieldtable =(EngineDataSet) getFieldListTable();
        for(int t=0;t<gs.size();t++)
        {
          String jgs = (String)gs.get(t);
          String njgs = "";
          fieldtable.first();
          for(int y=0;y<fieldtable.getRowCount();y++)
          {
            String fname = fieldtable.getValue("dyzdm");
            if(jgs.indexOf(fname)>-1)
            {
              jgs = engine.util.StringUtils.replaceIgnoreCase(jgs,fname,"parseFloat("+fname+".value)");

            }
            fieldtable.next();
            //"parseFloat("+(String)fieldNameList.get(h)+".value)"
          }
          gs.set(t,jgs);
        }
        ret.add(0,name);
        ret.add(1,gs);
        }catch(Exception e){ ArrayList nw = new ArrayList();nw.add(0,new ArrayList());nw.add(1,new ArrayList());return nw; }
      return ret;
    }
    /*得到字段的列表串*/
    public final String getFieldNameList()
    {
      StringBuffer tmp=new StringBuffer();
      if(!dsfieldListTable.isOpen())
        dsfieldListTable.open();
      dsfieldListTable.first();
      for(int i=0;i<dsfieldListTable.getRowCount();i++)
      {
        tmp.append(dsfieldListTable.getValue("dyzdm")+",");
        dsfieldListTable.next();
      }
      return tmp.toString();
    }
    /*得到主表的年份列表*/
    public final EngineDataSet getYearList()
    {
      EngineDataSet dsyear=new EngineDataSet();
      try{
        setDataSetProperty(dsyear,YEAR_SQL);
        dsyear.open();
        }catch(Exception e){}
        return dsyear;
    }
    /*打开从表*/
    public final void openDetailTable(boolean isMasterAdd)
    {
      gzkxzbID = dsMasterTable.getValue("gzkxzbID");//关链
      String SQL =combineSQL(DETAIL_SQL,"?",new String[]{" AND a.gzkxzbID="+(isMasterAdd ? "-1" : gzkxzbID)});
      dsDetailTable.setQueryString(SQL);
      if(!dsDetailTable.isOpen())
      {
        dsDetailTable.open();
      }
      else
      {
        dsDetailTable.refresh();
      }
    }
    /*得到主表一行的信息*/
    public final RowMap getMasterRowinfo() { return m_RowInfo; }
    /*得到从表多列的信息*/
    public final RowMap[] getDetailRowinfos() {
      RowMap[] rows = new RowMap[d_RowInfos.size()];
      d_RowInfos.toArray(rows);
      return rows;
    }
    /*当前操作的月份*/
    public final String getMonth(){return month;}
    public final String getFixedQueryValue(String col)
    {
      return fixedQuery.getSearchRow().get(col);
    }
    public void init()
    {
      initRowInfo(true,false,true);
      initRowInfo(false,false,true);
    }
    /**
     * 初始化操作的触发类
     */
    class Init implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        GregorianCalendar calendar=new GregorianCalendar();
        currentyear=String.valueOf(calendar.get(Calendar.YEAR));
        currentmonth=String.valueOf(calendar.get(Calendar.MONTH)+1);
        retuUrl = data.getParameter("src");
        retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
        dsMasterTable.setQueryString(MASTER_STRUT_SQL);
        dsMasterTable.open();
        if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
          dsDetailTable.empty();
        synchronized(dsDetailTable){
          openDetailTable(true);
          }

        month="";
        initRowInfo(false,false,true);
        if(!dsfieldListTable.isOpen())
          dsfieldListTable.open();
        else
          dsfieldListTable.refresh();
        //初始化查询项目和内容
        RowMap row = fixedQuery.getSearchRow();
        row.clear();
        row.put("gzkxzbID$yf", currentmonth);
        row.put("gzkxzbID$nf", currentyear);
      }
    }
    /**
     * 从表保存操作的触发类
     */
    class Detail_Post implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest request=data.getRequest();
        RowMap detailrow = null;
        putDetailInfo(request);
        B_WageFieldInfo b_WageFieldInfoBean  =  B_WageFieldInfo.getInstance(request);
        EngineDataSet fieldList = b_WageFieldInfoBean.getOneTable();
        dsDetailTable.first();
        for(int i=0; i<d_RowInfos.size(); i++)
        {
          detailrow = (RowMap)d_RowInfos.get(i);
          String deptid = detailrow.get("deptid");
          String personid = detailrow.get("personid");
          String vl="";
          fieldList.first();
          for(int k=0;k<fieldList.getRowCount();k++)
          {
            String fieldName=fieldList.getValue("dyzdm");
            vl=detailrow.get(fieldName);
            dsDetailTable.setValue(fieldName, detailrow.get(fieldName));
            fieldList.next();
          }
          dsDetailTable.setValue("gzkxzbID", gzkxzbID);
          dsDetailTable.post();
          dsDetailTable.next();
        }
        dsDetailTable.saveChanges();
   }
  }
  /**
   * 根据月份的变化显示主表的相应记录
   */
  class MonthOnchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String yf = data.getParameter("month");
      if(yf.equals(""))return;
      Calendar  cd= new GregorianCalendar();
      String nf = String.valueOf(cd.get(Calendar.YEAR));//得到当前的年份
      String sql = "SELECT count(*) FROM rl_gzkxzb WHERE nf='"+nf + "' and yf='"+yf+"'";//查主表
      String count = dataSetProvider.getSequence(sql);
      if(count.equals("0"))
      {
        gzkxzbID = dataSetProvider.getSequence("S_RL_GZKXZB");
        dsMasterTable.insertRow(false);
        dsMasterTable.setValue("gzkxzbID",gzkxzbID);
        dsMasterTable.setValue("nf",nf);
        dsMasterTable.setValue("yf",yf);
        dsMasterTable.setValue("sfjz","0");
        dsMasterTable.post();
        dsMasterTable.saveChanges();
        sfjz="0";
        //openDetailTable(false);
      }
      else
      {
        sql = "SELECT * FROM rl_gzkxzb WHERE nf='"+nf + "' and yf='"+yf+"'";
        dsMasterTable.setQueryString(sql);
        dsMasterTable.refresh();
        dsMasterTable.readyRefresh();
        gzkxzbID = dsMasterTable.getValue("gzkxzbID");
        sfjz = dsMasterTable.getValue("sfjz");
        //openDetailTable(false);
      }
      //sfjz = dsMasterTable.getValue("sfjz");
      year=nf;
      month=yf;
      Date ksdate = new SimpleDateFormat("yyyy-MM-dd").parse(nf+"-"+yf+"-01");
      startday = nf+"-"+yf+"-01";
      cd.setTime(ksdate);
      String TS = String.valueOf(cd.getActualMaximum(Calendar.DATE));//得到当前月份的天数getActualMaximum(Calendar.DATE)
      endday = nf+"-"+yf+"-"+TS;
      //Date jsdate = new SimpleDateFormat("yyyy-MM-dd").parse(enddate);
      synchronized(dsDetailTable){
        openDetailTable(false);
          }
      initRowInfo(true,false,true);
      initRowInfo(false,false,true);

    }
  }
    /**
     *  查询操作
     */
    class Master_Search implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest request=data.getRequest();
        initQueryItem(request);
        String nf=request.getParameter("gzkxzbID$nf");
        String yf=request.getParameter("gzkxzbID$yf");
        if(nf.equals("")||yf.equals(""))
        {
          data.setMessage(showJavaScript("alert('年份与月份都必须选择!')"));
          return;
        }
        else
        {
          String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM rl_gzkxzb WHERE nf='"+nf+"' and yf='"+yf+"'");
          if(count.equals("0"))
          {
            data.setMessage(showJavaScript("alert('该年月没造工资表!')"));
            return;
          }
        }
        fixedQuery.setSearchValue(request);
        String SQL = fixedQuery.getWhereQuery();
        if(SQL.length() > 0)
          SQL = " AND "+SQL;
        SQL = combineSQL(DETAIL_SEARCH_SQL, "?", new String[]{ SQL});
        if(!dsDetailTable.getQueryString().equals(SQL))
        {
          if(dsDetailTable.isOpen())
            dsDetailTable.close();
          setDataSetProperty(dsDetailTable,SQL);
          dsDetailTable.open();
        }
        month=yf;
        year = nf;
        gzkxzbID=dataSetProvider.getSequence("SELECT gzkxzbID FROM rl_gzkxzb WHERE nf='"+nf+"' and yf='"+yf+"'");
        SQL = combineSQL(MASTER_SQL,"?",new String[]{" and gzkxzbID="+gzkxzbID});
        dsMasterTable.setQueryString(SQL);
        dsMasterTable.refresh();
        initRowInfo(true,false,true);
        initRowInfo(false,false,true);
      }
      /**
       * 初始化查询的各个列
       * @param request web请求对象
       */
      private void initQueryItem(HttpServletRequest request)
      {
        if(isInitQuery)
          return;
        EngineDataSet detail = dsDetailTable;
        if(!detail.isOpen())
          detail.open();
        //初始化固定的查询项目
        fixedQuery = new QueryFixedItem();
        fixedQuery.addShowColumn("", new QueryColumn[]{
          new QueryColumn(detail.getColumn("deptid"), null, null, null, null, "="),
          new QueryColumn(detail.getColumn("gzkxzbID"), "rl_gzkxzb", "gzkxzbID", "nf", "nf", "="),
          new QueryColumn(detail.getColumn("gzkxzbID"), "rl_gzkxzb", "gzkxzbID", "yf", "yf", "="),
          new QueryColumn(detail.getColumn("personid"), "emp", "personid", "xm", "xm", "like"),
          new QueryColumn(detail.getColumn("personid"), "emp", "personid", "isdeformity", "isdeformity", "="),
        });
        isInitQuery = true;
      }
    }
    /**
     *  从表新增操作
     */
    class Detail_Add implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest req = data.getRequest();
        putDetailInfo(data.getRequest());
        //initRowInfo(false,false,true);
        gzkxzbID=dsMasterTable.getValue("gzkxzbID");
        if(gzkxzbID.equals(""))
          return;
        if(action.equals(String.valueOf(DETAIL_MULTI_ADD)))
        {
          RowMap detailrow = null;
          String multiIdInput = m_RowInfo.get("multiIdInput");
          if(multiIdInput.length() == 0)
            return;
          String[] personids = parseString(multiIdInput,",");
          for(int i=0; i < personids.length; i++)
          {
            if(personids[i].equals("-1"))
              continue;
            RowMap personRow = getpersonNameBean(req).getLookupRow(personids[i]);
            String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM rl_gzkx WHERE gzkxzbID='"+gzkxzbID+"' and personid='"+personids[i]+"'");
            if(!count.equals("0"))
              continue;
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("gzkxID", "-1");
            dsDetailTable.setValue("personid", personids[i]);
            String deptid=personRow.get("deptid");
            dsDetailTable.setValue("deptid", personRow.get("deptid"));
            dsDetailTable.setValue("gzkxzbID", gzkxzbID);
            dsDetailTable.post();
          }
          dsDetailTable.saveChanges();
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
        dsDetailTable.refresh();
        initRowInfo(false,false,true);
      }
    }
    /**
     *  完成操作
     */
    class Master_Over implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        if(!dsMasterTable.isOpen())
          return;
        if(dsDetailTable.getRowCount()<0)
        {
          data.setMessage(showJavaScript("alert('没造工资,不能完成!')"));
          return;
        }
        /*
        Calendar  cd= new GregorianCalendar();
        int yf = cd.get(Calendar.MONTH)+1;//得到当前的年份
        if(Integer.parseInt(dsMasterTable.getValue("yf"))>yf)
        {
          data.setMessage(showJavaScript("alert('只能完成当月和以前月份数据!')"));
          return;
        }
        */
        dsMasterTable.setValue("sfjz","1");
        dsMasterTable.saveChanges();
        initRowInfo(true,false,true);
      }
    }
    /**
     *引用操作
     * */
    class Master_Add implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest request=data.getRequest();
        String strmonth=request.getParameter("yf").trim();
        String stryear=request.getParameter("nf").trim();
        if(strmonth.equals("")||stryear.equals(""))
        {
          data.setMessage(showJavaScript("alert('年份与月份必选!')"));
          return;
        }
        String sql = "SELECT gzkxzbID FROM rl_gzkxzb WHERE nf='"+stryear + "' and yf='"+strmonth+"'";//查主表
        String zbid = dataSetProvider.getSequence(sql);
        if(zbid==null)
        {
          data.setMessage(showJavaScript("alert('所引用年月没有数据!')"));
          return;
        }
        else
        {
          sql="select count(*) from rl_gzkx where gzkxzbID="+zbid;
          if(dataSetProvider.getSequence(sql).equals("0"))
          {
            data.setMessage(showJavaScript("alert('所引用年月没有数据!')"));
            return;
          }
          String fieldnamelist=getFieldNameList();
          sql=combineSQL(GET_OLD_DATA_SQL,"?",new String[]{fieldnamelist,fieldnamelist,dsMasterTable.getValue("gzkxzbID"),zbid,dsMasterTable.getValue("gzkxzbID")});
          dataSetResolver.updateQuery(new String[]{sql});
          initRowInfo(false,false,true);
        }
      }
    }
    /**
     *  引用计件工资
     */
    class Cite implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest request =data.getRequest();
        putDetailInfo(data.getRequest());
        EngineDataSet ds = getDetailTable();
        gzkxzbID = dsMasterTable.getValue("gzkxzbID");//关链
        String start = request.getParameter("startdate");
        String enddate = request.getParameter("enddate");
        if(start==null||enddate==null||start.equals("")||enddate.equals(""))
        {
          data.setMessage(showJavaScript("alert('请输入日期!');"));
          return;
        }
        else if(!isDate(start)||!isDate(enddate))
        {
          data.setMessage(showJavaScript("alert('非法日期!');"));
          return;
        }
        try{
          Date ksdate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
          Date jsdate = new SimpleDateFormat("yyyy-MM-dd").parse(enddate);
          if(ksdate.compareTo(jsdate)>0)
            {
            data.setMessage(showJavaScript("alert('非法日期!');"));
            return;
            }
          }catch(Exception e){}

        EngineDataSet tmp = new EngineDataSet();
        setDataSetProperty(tmp,"SELECT SUM(a.jjgz)jjgz,a.grid FROM REP_SC_SHOP_WAGE_COLLECT a,rl_gzkx b WHERE a.grid=b.personid AND b.gzkxzbid='"+gzkxzbID+"' and  a.djrq>=to_date('"+start+ "','YYYY-MM-DD') and a.djrq<=to_date('"+enddate+ "','YYYY-MM-DD') GROUP BY a.grid");
        tmp.open();
        String name = dataSetProvider.getSequence("select t.dyzdm from rl_gzkxsz t WHERE t.ly=3");
        if(name==null||name.equals(""))
          return;

        String SQL =combineSQL(DETAIL_SQL,"?",new String[]{" AND a.gzkxzbID="+gzkxzbID});
        EngineDataSet alltable = new EngineDataSet();
        setDataSetProperty(alltable,SQL);
        alltable.open();
        alltable.setTableName("rl_gzkx");

        RowMap detailrow = null;
        alltable.first();
        for(int i=0;i<alltable.getRowCount();i++)
        {
          //detailrow = (RowMap)d_RowInfos.get(i);
          String personid = alltable.getValue("personid");
          alltable.setValue(name,"");
          tmp.first();
          for(int j=0;j<tmp.getRowCount();j++)
          {
            String grid = tmp.getValue("grid");
            String jjgz = tmp.getValue("jjgz");
            if(personid.equals(grid))
            {
              alltable.setValue(name,engine.util.Format.formatNumber(jjgz,"#0.00"));
            }
            tmp.next();
          }
          alltable.post();
          alltable.next();
        }
        alltable.saveChanges();



        dsDetailTable.refresh();
        initRowInfo(true,false,true);
        initRowInfo(false,false,true);
      }
    }
    /**
     *  计算工资
     */
    class Calculate implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest request =data.getRequest();
        putDetailInfo(data.getRequest());
        B_WageFieldInfo b_WageFieldInfoBean  =  B_WageFieldInfo.getInstance(request);
        EngineDataSet fieldList = b_WageFieldInfoBean.getOneTable();
        HashMap gs = new HashMap();//放入字段-公式对
        String ly ="";
        String jsgs ="";
        String dyzdm ="";
        fieldList.first();
        for(int i=0;i<fieldList.getRowCount();i++)
        {
          ly = fieldList.getValue("ly");
          dyzdm =  fieldList.getValue("dyzdm");
          if(ly.equals("2"))
          {
            jsgs = fieldList.getValue("jsgs");
            gs.put(dyzdm,jsgs);
          }
          fieldList.next();
        }
        //把公式值






      }
    }
    /**
     *  从表删除操作
     */
    class Detail_Delete implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        putDetailInfo(data.getRequest());
        EngineDataSet ds = getDetailTable();
        int rownum = Integer.parseInt(data.getParameter("rownum"));
        //删除临时数组的一列数据
        d_RowInfos.remove(rownum);
        ds.goToRow(rownum);
        ds.deleteRow();
        ds.saveChanges();
        //ds.refresh();
      }
    }
    /**
      *人员信息
     */
    public LookUp getpersonNameBean(HttpServletRequest req)
    {
      if(personBean == null)
        personBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PERSON);
      return personBean;
  }
}
