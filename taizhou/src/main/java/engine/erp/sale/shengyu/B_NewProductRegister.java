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
 * <p>Title: �²�Ʒ�����ǼǱ�</p>
 * <p>Copyright: right reserved (c) 2004</p>
 * <p>Company: ENGINE</p>
 * <p>Author: ������</p>
 * @version 1.0
 */

public final class B_NewProductRegister extends BaseAction implements Operate
{
  private static final String CPFHDJ_SQL = "SELECT a.* FROM xs_cpfhdj a,kc_dm b WHERE a.cpid=b.cpid and a.fgsid='?' ? order by a.djh desc, b.cpbm desc";
  //private static final String CPFHDJ_SQL = "SELECT a.* FROM xs_cpfhdj a WHERE a.fgsid='?' ? ";
  private static final String APPROVE_SQL = "SELECT * FROM xs_cpfhdj WHERE cpfhdjid='?' ";//��������ʱ����ȡһ����¼
  private EngineDataSet dsCpfhdjTable = new EngineDataSet();//���ݼ�
  private RowMap rowInfo = new RowMap();
  public  boolean isAdd = false;
  private long    editrow = 0;
  public  String retuUrl = null;

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsCpfhdjTable, "xs_cpfhdj");
  public  HtmlTableProducer table = new HtmlTableProducer(dsCpfhdjTable, "xs_cpfhdj", "a");//��ѯ�õ����ݿ������õ��ֶ�

  public  String loginid = "";   //��¼Ա����ID
  public  String loginCode = ""; //��½Ա���ı���
  public  String loginName = ""; //��¼Ա��������
  private String fgsid = null;   //�ֹ�˾ID
  public boolean isReport = false;
  public  boolean isApprove = false;     //�Ƿ�������״̬
  private String cpfhdjid = null;
  public  static final String CANCER_APPROVE           = "9002";
  public  static final String OVER                     = "9003";          //���
  public  static final String SALE_CANCER              = "9004";          //����
  public  static final String SELECT_PRODUCT           = "9005";
  public  static final String SXZ_CHANGE               = "9006";
  public  static final String AFTER_SELECT_PRODUCT     = "9006";
  public  static final String AFTER_POST               = "9007";
  public  static final String DWTXID_CHANGE   = "1007";
  public  static final String REPORT                   = "9008";      //����׷��
  private QueryFixedItem fixedQuery = new QueryFixedItem();//����̶���ѯ��
  public boolean submitType;//�����ж�true=���ƶ��˿��ύ,false=��Ȩ���˿��ύ
  private boolean isInitQuery = false;
  public String dwdm="";
  public String dwmc="";
  private User user = null;//��½�û��������û�����Ȩ�ޣ�
  /**
   * �ӻỰ�еõ��������ÿ���Ϣ��ʵ��
   * @param request jsp����
   * @return �����������ÿ���Ϣ��ʵ��
   */

  public static B_NewProductRegister getInstance(HttpServletRequest request)
  {
    B_NewProductRegister b_NewProductRegisterBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_NewProductRegisterBean";
      b_NewProductRegisterBean = (B_NewProductRegister)session.getAttribute(beanName);
      //�жϸ�session�Ƿ��и�bean��ʵ��
      if(b_NewProductRegisterBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        b_NewProductRegisterBean = new B_NewProductRegister();
        b_NewProductRegisterBean.fgsid = loginBean.getFirstDeptID();
        b_NewProductRegisterBean.loginid = loginBean.getUserID();
        b_NewProductRegisterBean.user = loginBean.getUser();
        b_NewProductRegisterBean.loginName = loginBean.getUserName();
        session.setAttribute(beanName, b_NewProductRegisterBean);//���뵽session��
      }
    }
    return b_NewProductRegisterBean;
  }
  /**
   * ���캯��
   */
  private B_NewProductRegister()
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
    setDataSetProperty(dsCpfhdjTable, combineSQL(CPFHDJ_SQL,"?",new String[]{fgsid,""}));                        //��ȡ��ȫ������
    //dsCpfhdjTable.setSequence(new SequenceDescriptor(new String[]{"cpfhdjid"}, new String[]{"s_xs_cpfhdj"})); //����������sequence
    dsCpfhdjTable.setSequence(new SequenceDescriptor(new String[]{"djh"}, new String[]{"SELECT pck_base.billNextCode('xs_cpfhdj','djh') from dual"}));
    dsCpfhdjTable.setTableName("xs_cpfhdj");
    //dsCpfhdjTable.setSort(new SortDescriptor("", new String[]{"cpfhdjid"}, new boolean[]{false}, null, 0));
    //��Ӳ����Ĵ�������
    Cpfhdj_Add_Edit add_edit = new Cpfhdj_Add_Edit();
    addObactioner(String.valueOf(ADD), add_edit);                  //����
    addObactioner(String.valueOf(EDIT), add_edit);                 //�޸�
    addObactioner(String.valueOf(INIT), new Cpfhdj_Init());  //��ʼ�� operate=0
    addObactioner(String.valueOf(POST), new Cpfhdj_Post());  //����
    addObactioner(String.valueOf(DEL), new Cpfhdj_Del()); //ɾ��
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());//ȡ������
    addObactioner(String.valueOf(SALE_CANCER), new Cancer());//����
    addObactioner(String.valueOf(OVER), new Over());//���
    addObactioner(String.valueOf(SELECT_PRODUCT), new Select_Product());
    addObactioner(String.valueOf(AFTER_SELECT_PRODUCT), new Select_Product());
    addObactioner(String.valueOf(AFTER_POST), new After_Post());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());//��ѯ
    addObactioner(String.valueOf(DWTXID_CHANGE), new Dwtxid_Change());
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
      if(dsCpfhdjTable.isOpen() && dsCpfhdjTable.changesPending())
        dsCpfhdjTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvmҪ���ĺ���,��������������
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsCpfhdjTable != null){
      dsCpfhdjTable.close();
      dsCpfhdjTable = null;
    }
    if(masterProducer != null)
    {
      masterProducer.release();
      masterProducer = null;
    }
    log = null;
    rowInfo = null;
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
  /**
   * ��ʼ������Ϣ
   * @param isAdd �Ƿ�ʱ���
   * @param isInit �Ƿ���³�ʼ��
   * @throws java.lang.Exception �쳣
   */
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //�Ƿ�ʱ��Ӳ���
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();
    if(!isAdd)
      rowInfo.put(getOneTable());
    else
    {
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      rowInfo.put("czrq", today);//�Ƶ�����
      rowInfo.put("czy", loginName);//����Ա
      rowInfo.put("czyid", loginid);
      rowInfo.put("zt", "0");
    }
  }
      /*�õ������*/
  public final EngineDataSet getOneTable()
  {
    //if(!dsCpfhdjTable.isOpen())
    //dsCpfhdjTable.open();
    return dsCpfhdjTable;
  }
  /**
   *�õ����һ����Ϣ
   * */
  public final RowMap getRowinfo() {return rowInfo;}


  //------------------------------------------
  //����ʵ�ֵ���:��ʼ��;����,�޸�,ɾ��
  //------------------------------------------
  /**
   * ��ʼ�������Ĵ�����
   */
  class Cpfhdj_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dwdm ="";
      dwmc ="";
      isReport = false;
      isApprove = false;
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

      String MSQL =  combineSQL(CPFHDJ_SQL, "?", new String[]{fgsid, ""});
      dsCpfhdjTable.setQueryString(MSQL);
      dsCpfhdjTable.setRowMax(null);
    }
  }
  /**
   * ���������Ĵ�����
   */
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginid);
      //�õ�request�Ĳ���,ֵ��Ϊnull, ����""����
      String id = null;
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
      String sql = combineSQL(APPROVE_SQL, "?", new String[]{id});
      dsCpfhdjTable.setQueryString(sql);
      if(dsCpfhdjTable.isOpen())
        dsCpfhdjTable.readyRefresh();
      dsCpfhdjTable.refresh();
      initRowInfo(false, true);
    }
  }
  /**
   * ���
   */
  class Over implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsCpfhdjTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      cpfhdjid = dsCpfhdjTable.getValue("cpfhdjid");
      dsCpfhdjTable.setValue("zt","8");
      dsCpfhdjTable.saveChanges();
    }
  }
  /**
   * ����
   */
  class Cancer implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsCpfhdjTable.goToRow(row);
      dsCpfhdjTable.setValue("zt", "4");
      dsCpfhdjTable.saveChanges();
    }
  }
  /**
   * ȡ������
   */
  class Cancer_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsCpfhdjTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsCpfhdjTable,dsCpfhdjTable.getRow(),"newproduct_register");
    }
  }
  /**
   * ��ӵ�����б�Ĳ�����
   */
  class Add_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsCpfhdjTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      cpfhdjid = dsCpfhdjTable.getValue("cpfhdjid");
      String content = dsCpfhdjTable.getValue("djh");
      String deptid = dsCpfhdjTable.getValue("deptid");
      approve.putAproveList(dsCpfhdjTable, dsCpfhdjTable.getRow(), "newproduct_register", content,deptid);
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
      rowInfo.put(data.getRequest());
      String tdhwid = rowInfo.get("seltdhwid");
      String wzdjid = dataSetProvider.getSequence("select wzdjid from xs_tdhw where tdhwid='"+tdhwid+"'");
      if(wzdjid!=null)
        rowInfo.put("wzdjid",wzdjid);
      if(action.equals(String.valueOf(SELECT_PRODUCT)))
        rowInfo.put("tdhwid",tdhwid);
      else
        rowInfo.put("xs__tdhwid",tdhwid);
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
      table.getWhereInfo().setWhereValues(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      if(!SQL.equals(""))
        SQL= " and "+SQL;
      SQL=combineSQL(CPFHDJ_SQL, "?", new String[]{fgsid,SQL});
      dsCpfhdjTable.setQueryString(SQL);
      dsCpfhdjTable.setRowMax(null);
    }
  }

  /**
   * ��ӻ��޸Ĳ����Ĵ�����
   */
  class Cpfhdj_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isReport = false;
      isApprove = false;
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsCpfhdjTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsCpfhdjTable.getInternalRow();
      }
      initRowInfo(isAdd, true);
      //data.setMessage(showJavaScript("toDetail();"));
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
      rowInfo.put(data.getRequest());//�����������ϸ��Ϣ
      String dwtxid=rowInfo.get("dwtxid");
      RowMap corRow = corpBean.getLookupRow(dwtxid);
      rowInfo.put("personid",corRow.get("personid"));
      rowInfo.put("deptid",corRow.get("deptid"));
    }
  }
  /**
   * ��������Ĵ�����
   */
  class Cpfhdj_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //У������
      //t.wzdjid,t.tdhwid,t.deptid,t.dwtxid,t.xs__tdhwid,t.cpid,t.dj,t.fkqk,t.dmsxid
      //���ж����ݱ����Ƿ�����ͬ��������λ����Ʒ��������ԣ����ţ��ֹ�˾
      rowInfo.put(data.getRequest());
      String wzdjid = rowInfo.get("wzdjid");
      String deptid = rowInfo.get("deptid");
      String dwtxid = rowInfo.get("dwtxid");
      String cpid = rowInfo.get("cpid");
      String dj = rowInfo.get("dj");
      String dmsxid = rowInfo.get("dmsxid");
      String jhsl = rowInfo.get("jhsl");
      String temp = "";
      if(dwtxid.equals(""))
      {
        data.setMessage(showJavaScript("alert('�����빺����λ!')"));
        return;
      }
      if(deptid.equals(""))
      {
        data.setMessage(showJavaScript("alert('�����벿��!')"));
        return;
      }
      if(cpid.equals(""))
      {
        data.setMessage(showJavaScript("alert('�������Ʒ!')"));
        return;
      }
      String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM xs_cpfhdj t WHERE t.dwtxid='"+dwtxid+"' AND t.deptid='"+deptid+"' AND t.cpid='"+cpid+"' AND t.dmsxid='"+dmsxid+"'  AND t.fgsid='"+fgsid+"' ");
      if(count!=null&&!count.equals("0"))
      {
        if(isAdd||!count.equals("1"))
        {
          data.setMessage(showJavaScript("alert('������λ����Ʒ��������ԣ��������߲����ظ�!')"));
          return;
        }
      }
      if(dj.equals(""))
      {
        data.setMessage(showJavaScript("alert('���۲��ܿ�!')"));
        return;
      }
      if(jhsl.equals(""))
      {
        data.setMessage(showJavaScript("alert('�ƻ��������ܿ�!')"));
        return;
      }
      if((temp = checkNumber(dj, "����")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if((temp = checkNumber(jhsl, "�ƻ�����")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if(isAdd)
      {
        String ncount = dataSetProvider.getSequence("SELECT COUNT(*) FROM xs_cpfhdj t WHERE t.dwtxid='"+dwtxid+"' AND t.deptid='"+deptid+"' AND t.cpid='"+cpid+"'  AND t.fgsid='"+fgsid+"' ");
        if(ncount!=null&&ncount.equals("1"))
        {
          data.setMessage(showJavaScript("alert('������λ����Ʒ�����Ų����ظ�!')"));
          return;
        }
        ds.insertRow(false);
        cpfhdjid = dataSetProvider.getSequence("s_xs_cpfhdj");
        ds.setValue("cpfhdjid",cpfhdjid);
        ds.setValue("zt","0");
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//�Ƶ�����
        ds.setValue("czyid", loginid);
        ds.setValue("czy", loginName);//����Ա
        ds.setValue("fgsid", fgsid);//�ֹ�˾
        isAdd=false;

      }
      else
        ds.goToInternalRow(editrow);
      ds.setValue("wzdjid", wzdjid);
      ds.setValue("deptid", deptid);
      ds.setValue("dwtxid", dwtxid);
      ds.setValue("cpid", cpid);
      ds.setValue("dj", dj);
      ds.setValue("dmsxid", dmsxid);
      ds.setValue("jhsl", jhsl);
      ds.post();
      ds.saveChanges();
      editrow = ds.getInternalRow();
      //data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }
  /**
   * ��������Ĵ�����
   */
  class After_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      rowInfo.put(data.getRequest());
      String tdhwid = rowInfo.get("tdhwid");
      String xs__tdhwid = rowInfo.get("xs__tdhwid");
      String fkqk = rowInfo.get("fkqk");
      dsCpfhdjTable.setValue("xs__tdhwid", xs__tdhwid);
      dsCpfhdjTable.setValue("tdhwid", tdhwid);
      dsCpfhdjTable.setValue("fkqk", fkqk);
      dsCpfhdjTable.post();
      dsCpfhdjTable.saveChanges();
    }
  }

  /**
   * ɾ�������Ĵ�����
   */
  class Cpfhdj_Del implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.deleteRow();
      ds.saveChanges();
    }
  }
}


