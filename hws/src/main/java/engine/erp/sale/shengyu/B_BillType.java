package engine.erp.sale.shengyu;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.web.observer.Obactioner;
import engine.common.LoginBean;
import engine.project.SysConstant;
import engine.project.LookupBeanFacade;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
/**
 * <p>Title:������������</p>
 * <p>Copyright: right reserved (c) 2003</p>
 * <p>Company: ENGINE</p>
 * @version 1.0
 */
public final class B_BillType extends BaseAction implements Operate
{
  private static final String SENDMODE_SQL = "SELECT * FROM xs_fund_item WHERE 1=1 ? ";
  private EngineDataSet dsB_OrderType = new EngineDataSet();//���ݼ�
  private RowMap rowInfo = new RowMap();
  public  boolean isAdd = true;
  private long    editrow = 0;
  public  String retuUrl = null;
  /**
   * �ӻỰ�еõ��������ÿ���Ϣ��ʵ��
   * @param request jsp����
   * @return �����������ÿ���Ϣ��ʵ��
   */
  public static B_BillType getInstance(HttpServletRequest request)
  {
    B_BillType billTypeBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "billTypeBean";
      billTypeBean = (B_BillType)session.getAttribute(beanName);
      //�жϸ�session�Ƿ��и�bean��ʵ��
      if(billTypeBean == null)
      {
        billTypeBean = new B_BillType();
        session.setAttribute(beanName, billTypeBean);//���뵽session��
      }
    }
    return billTypeBean;
  }
  /**
   * ���������
   * ���캯��
   */
  private B_BillType()
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
    setDataSetProperty(dsB_OrderType, combineSQL(SENDMODE_SQL,"?",new String[]{""}));

    dsB_OrderType.setSort(new SortDescriptor("", new String[]{"funditemcode"}, new boolean[]{false}, null, 0));//��������ʽ
    dsB_OrderType.setSequence(new SequenceDescriptor(new String[]{"funditemid"}, new String[]{"s_xs_fund_item"}));//����������sequence
    //��Ӳ����Ĵ�������
    B_BillType_Add_Edit add_edit = new B_BillType_Add_Edit();

    addObactioner(String.valueOf(INIT), new B_BillType_Init());//��ʼ�� operate=0
    addObactioner(String.valueOf(ADD), add_edit);//����
    addObactioner(String.valueOf(EDIT), add_edit);//�޸�
    addObactioner(String.valueOf(POST), new B_BillType_Post());//����
    addObactioner(String.valueOf(DEL), new B_BillType_Delete());//ɾ��
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
      if(dsB_OrderType.isOpen() && dsB_OrderType.changesPending())
        dsB_OrderType.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvmҪ���ĺ���,��������������
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsB_OrderType != null){
      dsB_OrderType.close();
      dsB_OrderType = null;
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
      //���ô�������
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('xs_fund_item','funditemcode','','',3) from dual");
      rowInfo.put("funditemcode", code);
      }
  }
  /*�õ������*/
  public final EngineDataSet getOneTable()
  {
    if(!dsB_OrderType.isOpen())
      dsB_OrderType.open();
    return dsB_OrderType;
  }
  /**
   *�õ����һ����Ϣ
   * */
  public final RowMap getRowinfo() {return rowInfo;}


  //==========================================
  //����ʵ�ֵ���:��ʼ��;����,�޸�,ɾ��
  //==========================================
  /**
   * ��ʼ�������Ĵ�����
   */
  class B_BillType_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
    }
  }
  /**
   * ��ӻ��޸Ĳ����Ĵ�����
   */
  class B_BillType_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsB_OrderType.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsB_OrderType.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }
  /**
   * ��������Ĵ�����
   */
  class B_BillType_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //У������
      rowInfo.put(data.getRequest());
      String funditemcode = rowInfo.get("funditemcode");
      String funditemname = rowInfo.get("funditemname");
      if(funditemcode.equals("")){
        data.setMessage(showJavaScript("alert('��Ų���Ϊ�գ�');"));
        return;
      }
      if(funditemname.equals("")){
        data.setMessage(showJavaScript("alert('���Ͳ���Ϊ�գ�');"));
        return;
      }

      if(!isAdd)
        ds.goToInternalRow(editrow);
      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("funditemid", "-1");
      }
      ds.setValue("funditemcode", funditemcode);
      ds.setValue("funditemname", funditemname);
      ds.post();
      ds.saveChanges();
      //ˢ��lookup�����ݼ����������ݵ�ͬ��.�������Ӧ��lookup����
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_FUND_TYPE);
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }
  /**
   * ɾ�������Ĵ�����
   */
  class B_BillType_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.deleteRow();
      ds.saveChanges();
      //ˢ��lookup�����ݼ����������ݵ�ͬ��.�������Ӧ��lookup����
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_ORDER_TYPE);
    }
  }
}