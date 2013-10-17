package engine.erp.sale.shengyu;

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
import java.util.*;
import java.text.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;

/**
* <p>Title: ����Ӧ�տ�</p>
* <p>Copyright: right reserved (c) 2004</p>
* <p>Company: ENGINE</p>
* <p>Author: ������</p>
* @version 1.0
*/

public final class B_OtherBalance extends BaseAction implements Operate
{

  private static final String Balance_SQL = "SELECT * FROM xs_other_fund  WHERE 1=1 and ? AND fgsid=? ?  order by otherfundno desc ";
  private static final String EDIT_SQL = "SELECT * FROM xs_other_fund  WHERE otherfundid=? order by otherfundno desc ";
  private static final String Balance_STRUCT_SQL = "SELECT * FROM xs_other_fund  WHERE 1<>1 order by otherfundno desc ";//
  private EngineDataSet dsBalanceTable = new EngineDataSet();//���ݼ�
  private RowMap m_RowInfo = new RowMap();
  public  boolean isAdd = false;
  private long    editrow = 0;
  public  String retuUrl = null;
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsBalanceTable, "xs_other_fund");
  //public  HtmlTableProducer table = new HtmlTableProducer(dsBalanceTable, "xs_other_fund", "a");//��ѯ�õ����ݿ������õ��ֶ�
  public  String loginid = "";   //��¼Ա����ID
  public  String loginCode = ""; //��½Ա���ı���
  public  String loginName = ""; //��¼Ա��������
  private String fgsid = null;   //�ֹ�˾ID
  public boolean isReport = false;
  public  boolean isApprove = false;     //�Ƿ�������״̬
  private ArrayList d_RowInfos = null; //���м�¼������
  public  static final String SALE_OVER                = "9003";          //���
  public  static final String SELECT_PRODUCT           = "9005";
  public  static final String SXZ_CHANGE               = "9006";
  public  static final String AFTER_SELECT_PRODUCT     = "9006";
  public  static final String AFTER_POST               = "9007";
  public  static final String REPORT                   = "9008";      //����׷��
  public static final String BATCHINIT                 = "9009";
  public static final String BATCHPOST                 = "9010";
  public  static final String DWTXID_CHANGE            = "1007";
  public  static final String CANCER_APPROVE            = "1004";
  public  static final String PRODUCT_CHANGE           = "9012";
  private QueryFixedItem fixedQuery = new QueryFixedItem();//����̶���ѯ��
  public boolean submitType;//�����ж�true=���ƶ��˿��ύ,false=��Ȩ���˿��ύ
  private boolean isInitQuery = false;
  public String dwdm="";
  public String dwmc="";
  private User user = null;//��½�û��������û�����Ȩ�ޣ�
  private String otherfundid = null;
  public String []zt;
  /**
   * �ӻỰ�еõ��������ÿ���Ϣ��ʵ��
   * @param request jsp����
   * @return �����������ÿ���Ϣ��ʵ��
   */

  public static B_OtherBalance getInstance(HttpServletRequest request)
  {
    B_OtherBalance otherBalanceBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "otherBalanceBean";
      otherBalanceBean = (B_OtherBalance)session.getAttribute(beanName);
      //�жϸ�session�Ƿ��и�bean��ʵ��
      if(otherBalanceBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        otherBalanceBean = new B_OtherBalance();
        otherBalanceBean.fgsid = loginBean.getFirstDeptID();
        otherBalanceBean.loginid = loginBean.getUserID();
        otherBalanceBean.user = loginBean.getUser();
        otherBalanceBean.loginName = loginBean.getUserName();
        session.setAttribute(beanName, otherBalanceBean);//���뵽session��
      }
    }
    return otherBalanceBean;
  }
  /**
   * ���캯��
   */
  private B_OtherBalance()
  {
    try {
    jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  /**
   * ��ʼ������
   * @throws Exception �쳣��Ϣ
   */
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsBalanceTable, combineSQL(Balance_SQL,"?",new String[]{""}));
    dsBalanceTable.setSequence(new SequenceDescriptor(new String[]{"otherfundid"}, new String[]{"S_XS_OTHER_FUND"})); //����������sequence
    dsBalanceTable.setTableName("xs_other_fund");
    //dsBalanceTable.setSort(new SortDescriptor("", new String[]{"otherfundid"}, new boolean[]{false}, null, 0));
    //��Ӳ����Ĵ�������
    Balance_Add_Edit add_edit = new Balance_Add_Edit();
    addObactioner(String.valueOf(ADD), add_edit);                  //����
    addObactioner(String.valueOf(EDIT), add_edit);                 //�޸�
    addObactioner(String.valueOf(INIT), new Balance_Init());  //��ʼ�� operate=0
    addObactioner(String.valueOf(POST), new Balance_Post());  //����
    addObactioner(String.valueOf(DEL), new Balance_Del()); //ɾ��
    addObactioner(String.valueOf(SELECT_PRODUCT), new Select_Product());
    addObactioner(String.valueOf(AFTER_SELECT_PRODUCT), new Select_Product());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());//��ѯ
    addObactioner(String.valueOf(PRODUCT_CHANGE), new Balance_Change());
    addObactioner(String.valueOf(DWTXID_CHANGE), new Dwtxid_Change());

    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(REPORT), new Approve());

    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());
    addObactioner(String.valueOf(SALE_OVER), new Over());//���
  }
  /**
   * JSP���õĺ���
   * @param request ��ҳ���������
   * @param response ��ҳ����Ӧ����
   * @return ����HTML��javascipt�����
   * @throws Exception �쳣
   */
  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      String operate = request.getParameter("operate");
      if(operate != null && operate.trim().length() > 0)
      {
        RunData data = notifyObactioners(operate, request, response, null);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsBalanceTable.isOpen() && dsBalanceTable.changesPending())
        dsBalanceTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvmҪ���ĺ���,��������������
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsBalanceTable != null){
      dsBalanceTable.close();
      dsBalanceTable = null;
    }
    if(masterProducer != null)
    {
      masterProducer.release();
      masterProducer = null;
    }
    log = null;
    m_RowInfo = null;
  }
  /**
   * �õ����������
   * ʵ��BaseAction�еĳ��󷽷�
   * ��־�е���
   * @return �������������
   */
  protected Class childClassName()
  {
    return getClass();
  }
  /**
   * �õ��̶���ѯ���û������ֵ
   * @param col ��ѯ������
   * @return �û������ֵ
   */
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
  }
  /*�õ��ӱ���е���Ϣ*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
  /**
   * ��ʼ������Ϣ
   * @param isAdd �Ƿ�ʱ���
   * @param isInit �Ƿ���³�ʼ��
   * @throws java.lang.Exception �쳣
   */
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //�Ƿ�ʱ��Ӳ���
    if(isInit && m_RowInfo.size() > 0)
      m_RowInfo.clear();
    if(!isAdd)
      m_RowInfo.put(getOneTable());
    else
    {
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String otherfundno="";
      m_RowInfo.put("czrq", today);//�Ƶ�����
      m_RowInfo.put("czy", loginName);//����Ա
      m_RowInfo.put("kdrq", today);
      m_RowInfo.put("czyid", loginid);
      m_RowInfo.put("otherfunddate", today);
      m_RowInfo.put("zt", "0");
      otherfundno = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_other_fund','otherfundno') from dual");
      m_RowInfo.put("otherfundno", otherfundno);
      m_RowInfo.put("jbr", loginName);
      m_RowInfo.put("fgsid", fgsid);
    }
  }
  /*�õ������*/
  public final EngineDataSet getOneTable()
  {
    //if(!dsBalanceTable.isOpen())
    //dsBalanceTable.open();
    return dsBalanceTable;
  }
  /**
   *�õ����һ����Ϣ
   * */
  public final RowMap getRowinfo() {return m_RowInfo;}


  //------------------------------------------
  //����ʵ�ֵ���:��ʼ��;����,�޸�,ɾ��
  //------------------------------------------
  /**
   * ��ʼ�������Ĵ�����
   */
  class Balance_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove= false;
      dwdm ="";
      dwmc ="";
      zt = new String[]{""};
      HttpServletRequest request = data.getRequest();
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      fixedQuery.getSearchRow().clear();
      masterProducer.init(request, loginid);
      String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      if(code.equals("1"))
        submitType=true;
      else
        submitType=false;


      String SQL = " AND zt<>8   AND zt<>4 ";
      String MSQL =  combineSQL(Balance_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      dsBalanceTable.setQueryString(MSQL);
      dsBalanceTable.setRowMax(null);


    }
  }
  /**
   * ѡ���²�Ʒ
   **/
  class Select_Product implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      m_RowInfo.put(data.getRequest());
      String tdhwid = m_RowInfo.get("seltdhwid");
      String wzdjid = dataSetProvider.getSequence("select wzdjid from xs_tdhw where tdhwid='"+tdhwid+"'");
      if(wzdjid!=null)
        m_RowInfo.put("wzdjid",wzdjid);
      if(action.equals(String.valueOf(SELECT_PRODUCT)))
        m_RowInfo.put("tdhwid",tdhwid);
      else
        m_RowInfo.put("xs__tdhwid",tdhwid);
    }
  }

  class Search implements Obactioner
  {
    /**
     * ������ʼ������
     * @parma  action ����ִ�еĲ�������ֵ��
     * @param  o      �����߶���
     * @param  data   ���ݵ���Ϣ����
     * @param  arg    �����߶������<cardID >notifyObactioners</cardID >�������ݵĲ���
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;

      zt = data.getRequest().getParameterValues("zt");
      if(!(zt==null))
      {
        StringBuffer sbzt = null;
        for(int i=0;i<zt.length;i++)
        {
          if(sbzt==null)
            sbzt= new StringBuffer(" AND zt IN(");
          sbzt.append(zt[i]+",");
        }
        if(sbzt == null)
          sbzt =new StringBuffer();
        else
          sbzt.append("-99)");
        SQL = SQL+sbzt.toString();
      }
      else
        zt = new String[]{""};

      String MSQL = combineSQL(Balance_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      if(!dsBalanceTable.getQueryString().equals(MSQL))
      {
        dsBalanceTable.setQueryString(MSQL);
        dsBalanceTable.setRowMax(null);//�Ա�dbNavigatorˢ�����ݼ�
      }
    }
    /**
     * ��ʼ����ѯ�ĸ�����
     * @param request web�������
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;//�ѳ�ʼ����ѯ����
      EngineDataSet master = dsBalanceTable;
      if(!master.isOpen())
        master.open();//���������ݼ�
      //��ʼ���̶��Ĳ�ѯ��Ŀ
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("otherfundno"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("otherfunddate"), null, null, null, "a", ">="),//�Ƶ�����
        new QueryColumn(master.getColumn("otherfunddate"), null, null, null, "b", "<="),//�Ƶ�����
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//����id
        new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("sprid"), "emp", "personid", "xm", "xm", "like"),//�ӱ�Ʒ��
        new QueryColumn(master.getColumn("czy"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("jbr"), null, null, null, null, "like")
      });
      isInitQuery = true;//��ʼ�����
    }
}

  //&#$
  /**
   * ��ӵ�����б�Ĳ�����
   */
  class Add_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsBalanceTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsBalanceTable.getValue("otherfundno");
      String deptid = dsBalanceTable.getValue("deptid");
      String otherfundid = dsBalanceTable.getValue("otherfundid");
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_other_fund a WHERE a.otherfundid='"+otherfundid+"'");
      if(zt!=null&&!zt.equals("0"))
      {
        dsBalanceTable.readyRefresh();
        return;
      }
      approve.putAproveList(dsBalanceTable, dsBalanceTable.getRow(),"other_fund_aprove", content,deptid);
    }
  }
  //&#$
  /**
   * ���������Ĵ�����
   */
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd=false;
      String id=null;
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginid);
      if(String.valueOf(REPORT).equals(action))
      {
        isReport=true;
        isApprove = false;
        id=request.getParameter("id");
      }else
      {
        isApprove = true;
        id = data.getParameter("id", "");
      }
      String sql = combineSQL(EDIT_SQL, "?", new String[]{id});
      dsBalanceTable.setQueryString(sql);
      if(dsBalanceTable.isOpen())
        dsBalanceTable.readyRefresh();
      dsBalanceTable.refresh();

      otherfundid = id;
      initRowInfo(false,false);
    }
  }
  /**
   * ȡ������
   */
  class Cancer_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsBalanceTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      String otherfundid = dsBalanceTable.getValue("otherfundid");
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_other_fund a WHERE a.otherfundid='"+otherfundid+"'");
      if(zt!=null&&!zt.equals("1"))
      {
        dsBalanceTable.readyRefresh();
        return;
      }
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsBalanceTable,dsBalanceTable.getRow(),"other_fund_aprove");
    }
  }
  /**
   * ����
   */
  class Cancer implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String otherfundid = data.getParameter("rownum");

      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_other_fund a WHERE a.otherfundid='"+otherfundid+"'");
      if(zt!=null&&(zt.equals("8")||zt.equals("9")||zt.equals("4")))
      {
        dsBalanceTable.readyRefresh();
        return;
      }

      dsBalanceTable.setQueryString(combineSQL(EDIT_SQL,"?",new String[]{otherfundid}));
      if(!dsBalanceTable.isOpen())
        dsBalanceTable.openDataSet();
      else
        dsBalanceTable.refresh();
      dsBalanceTable.setValue("zt", "4");
      dsBalanceTable.saveChanges();
      dsBalanceTable.readyRefresh();
    }
  }
  /**
   * ���
   */
  class Over implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsBalanceTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      dsBalanceTable.setValue("zt","8");
      dsBalanceTable.saveChanges();
    }
  }
/**
* ��ӻ��޸Ĳ����Ĵ�����
*/
class Balance_Add_Edit implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    isApprove= false;
    isAdd = action.equals(String.valueOf(ADD));
    if(!isAdd)
    {
      dsBalanceTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      editrow = dsBalanceTable.getInternalRow();
    }
    initRowInfo(isAdd, true);
    //data.setMessage(showJavaScript("toDetail();"));
  }
}
/**
* ��ӻ��޸Ĳ����Ĵ�����
*/
class Balance_Change implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest request = data.getRequest();
    m_RowInfo.put(request);
    String personid =m_RowInfo.get("personid");
    String djlx =  m_RowInfo.get("djlx");
    String dj = dataSetProvider.getSequence("select "+djlx+" from xs_wzdj where personid='"+personid+"'");
    m_RowInfo.put("dj",dj);
    m_RowInfo.put("zk","");
    m_RowInfo.put("prom_price","");
    //initRowInfo(isAdd, true);
  }
}
  /**
   * ��������Ĵ�����
   */
  class Balance_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //У������
      //t.otherfundid t.personid t.dwtxid t.deptid t.jsfsid t.otherfundno t.otherfunddate  t.otherfunditem t.otherfund t.khlx t.jbr t.bz
      //t.czrq t.czy t.czyid t.fgsid t.custaddr

      //���ж����ݱ����Ƿ�����ͬ��������λ����Ʒ��������ԣ����ţ��ֹ�˾
      m_RowInfo.put(data.getRequest());
      String dwtxid = m_RowInfo.get("dwtxid");
      String personid = m_RowInfo.get("personid");
      String deptid = m_RowInfo.get("deptid");
      String jsfsid = m_RowInfo.get("jsfsid");
      String otherfundno = m_RowInfo.get("otherfundno");
      String otherfunddate = m_RowInfo.get("otherfunddate");
      String otherfunditem =m_RowInfo.get("otherfunditem");
      String otherfund =m_RowInfo.get("otherfund");
      String khlx =m_RowInfo.get("khlx");
      String jbr =m_RowInfo.get("jbr");
      String temp = "";
      if(dwtxid.equals(""))
      {
        data.setMessage(showJavaScript("alert('������ͻ�!')"));
        return;
      }
      if(personid.equals(""))
      {
        data.setMessage(showJavaScript("alert('��ѡ��ҵ��Ա!')"));
        return;
      }
      if(jbr.equals(""))
      {
        data.setMessage(showJavaScript("alert('��ѡ�񾭰���!')"));
        return;
      }
      if(deptid.equals(""))
      {
        data.setMessage(showJavaScript("alert('��ѡ����!')"));
        return;
      }
      if(otherfunddate.equals(""))
      {
        data.setMessage(showJavaScript("alert('��ʼʱ�䲻�ܿ�!')"));
        return;
      }
      if(checkNumber(otherfund, "���") != null)
      {
        data.setMessage(showJavaScript("alert('�����Ч!')"));
        return;
      }
      if(jsfsid.equals(""))
      {
        data.setMessage(showJavaScript("alert('��ѡ����㷽ʽ!')"));
        return;
      }
      if(khlx.equals(""))
      {
        data.setMessage(showJavaScript("alert('��ѡ��ͻ�����!')"));
        return;
      }

      if(isAdd)
      {
        //String otherfundno = m_RowInfo.get("otherfundno");
        String count = dataSetProvider.getSequence("select count(*) from xs_other_fund t where t.otherfundno='"+otherfundno+"'");
        if(!count.equals("0"))
        {
          otherfundno = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_other_fund','otherfundno') from dual");
        }
        ds.insertRow(false);
        otherfundid = dataSetProvider.getSequence("s_xs_other_fund");
        ds.setValue("otherfundid", otherfundid);//����
        ds.setValue("zt","0");
        ds.setValue("otherfundno",otherfundno);
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//�Ƶ�����
        ds.setValue("czyid", loginid);
        ds.setValue("czy", loginName);//����Ա
        ds.setValue("fgsid", fgsid);//�ֹ�˾
        isAdd=false;
      }
      else
        ds.goToInternalRow(editrow);
      ds.setValue("khlx", khlx);
      ds.setValue("deptid", deptid);
      ds.setValue("dwtxid", dwtxid);
      ds.setValue("personid", personid);
      ds.setValue("otherfundno", otherfundno);
      ds.setValue("jsfsid", jsfsid);
      ds.setValue("otherfunddate", otherfunddate);
      ds.setValue("otherfunditem", otherfunditem);
      ds.setValue("otherfund", otherfund);
      ds.setValue("jsfsid", jsfsid);
      ds.setValue("jbr", jbr);
      ds.setValue("bz",m_RowInfo.get("bz"));
      ds.setValue("custaddr",m_RowInfo.get("custaddr"));
      ds.post();
      ds.saveChanges();
      editrow = ds.getInternalRow();
      //data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }
  /**
   *�ı乺����λʱ�����Ĳ���
   * */
  class Dwtxid_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_CORP);
      String olddwtxId=m_RowInfo.get("dwtxid");
      m_RowInfo.put(data.getRequest());
      String dwtxid=m_RowInfo.get("dwtxid");
      RowMap corRow = corpBean.getLookupRow(dwtxid);
      if(olddwtxId.equals(dwtxid))
        return;
      else
      {
        m_RowInfo.put("dwtxid",dwtxid);
        m_RowInfo.put("kdrq",req.getParameter("kdrq"));
        m_RowInfo.put("otherfundno",req.getParameter("otherfundno"));
        m_RowInfo.put("personid",corRow.get("personid"));
        m_RowInfo.put("deptid",corRow.get("deptid"));
      }
    }
  }

  /**
   * ɾ�������Ĵ�����
   */
  class Balance_Del implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      if(!isAdd)
      {
        ds.goToInternalRow(editrow);
        //ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
        ds.deleteRow();
        ds.saveChanges();
      }
      data.setMessage(showJavaScript("backList();"));
    }
  }
}