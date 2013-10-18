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

    public final class B_CustomerUnitiveDiscount extends BaseAction implements Operate
    {
      private static final String CPFHDJ_SQL = "SELECT * FROM xs_khtyzk order by cplx   ";

      private EngineDataSet dsCpfhdjTable = new EngineDataSet();//���ݼ�
      private RowMap rowInfo = new RowMap();
      public  boolean isAdd = false;
      private long    editrow = 0;
      public  String retuUrl = null;

      //public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsCpfhdjTable, "xs_khtyzk");
      //public  HtmlTableProducer table = new HtmlTableProducer(dsCpfhdjTable, "xs_khtyzk", "a");//��ѯ�õ����ݿ������õ��ֶ�

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

      public static B_CustomerUnitiveDiscount getInstance(HttpServletRequest request)
      {
        B_CustomerUnitiveDiscount b_CustomerUnitiveDiscountBean = null;
        HttpSession session = request.getSession(true);
        synchronized (session)
        {
          String beanName = "b_CustomerUnitiveDiscountBean";
          b_CustomerUnitiveDiscountBean = (B_CustomerUnitiveDiscount)session.getAttribute(beanName);
          //�жϸ�session�Ƿ��и�bean��ʵ��
          if(b_CustomerUnitiveDiscountBean == null)
          {
            LoginBean loginBean = LoginBean.getInstance(request);
            b_CustomerUnitiveDiscountBean = new B_CustomerUnitiveDiscount();
            b_CustomerUnitiveDiscountBean.fgsid = loginBean.getFirstDeptID();
            b_CustomerUnitiveDiscountBean.loginid = loginBean.getUserID();
            b_CustomerUnitiveDiscountBean.user = loginBean.getUser();
            b_CustomerUnitiveDiscountBean.loginName = loginBean.getUserName();
            session.setAttribute(beanName, b_CustomerUnitiveDiscountBean);//���뵽session��
          }
        }
        return b_CustomerUnitiveDiscountBean;
      }
      /**
       * ���캯��
       */
      private B_CustomerUnitiveDiscount()
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
        //dsCpfhdjTable.setSequence(new SequenceDescriptor(new String[]{"cpfhdjid"}, new String[]{"s_xs_khtyzk"})); //����������sequence
        //dsCpfhdjTable.setSequence(new SequenceDescriptor(new String[]{"djh"}, new String[]{"SELECT pck_base.billNextCode('xs_khtyzk','djh') from dual"}));
        dsCpfhdjTable.setTableName("xs_khtyzk");
        //dsCpfhdjTable.setSort(new SortDescriptor("", new String[]{"cpfhdjid"}, new boolean[]{false}, null, 0));
        //��Ӳ����Ĵ�������
        Cpfhdj_Add_Edit add_edit = new Cpfhdj_Add_Edit();
        addObactioner(String.valueOf(ADD), add_edit);                  //����
        addObactioner(String.valueOf(EDIT), add_edit);                 //�޸�
        addObactioner(String.valueOf(INIT), new Cpfhdj_Init());  //��ʼ�� operate=0
        addObactioner(String.valueOf(POST), new Cpfhdj_Post());  //����
        addObactioner(String.valueOf(DEL), new Cpfhdj_Del()); //ɾ��

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
    String xydj = rowInfo.get("xydj");
    String cplx = rowInfo.get("cplx");
    String djlx = rowInfo.get("djlx");
    String zk = rowInfo.get("zk");

    String temp = "";
    if(xydj.equals(""))
    {
      data.setMessage(showJavaScript("alert('�����������ȼ�!')"));
      return;
    }
    if(cplx.equals(""))
    {
      data.setMessage(showJavaScript("alert('�������Ʒ���!')"));
      return;
    }
    if(djlx.equals(""))
    {
      data.setMessage(showJavaScript("alert('�����붨������!')"));
      return;
    }
    if(zk.equals(""))
    {
      data.setMessage(showJavaScript("alert('�������ۿ�!')"));
      return;
    }
    if((temp = checkNumber(zk, "�ۿ�")) != null)
    {
      data.setMessage(temp);
      return;
    }
    String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM xs_khtyzk t WHERE t.xydj='"+xydj+"' AND t.cplx='"+cplx+"' AND t.djlx='"+djlx+"'");
    if(count!=null&&!count.equals("0"))
    {
      if(isAdd||!count.equals("1"))
      {
        data.setMessage(showJavaScript("alert('�����ȼ�����Ʒ��𣬶������Ͳ����ظ�!')"));
        return;
      }
    }
    if(isAdd)
    {
      String ncount = dataSetProvider.getSequence("SELECT COUNT(*) FROM xs_khtyzk t WHERE t.xydj='"+xydj+"' AND t.cplx='"+cplx+"' AND t.djlx='"+djlx+"'");
      if(ncount!=null&&ncount.equals("1"))
      {
        data.setMessage(showJavaScript("alert('�����ȼ�����Ʒ��𣬶������Ͳ����ظ�!')"));
        return;
      }
      ds.insertRow(false);
      isAdd=false;
    }
    else
      ds.goToInternalRow(editrow);
    ds.setValue("xydj", xydj);
    ds.setValue("cplx", cplx);
    ds.setValue("djlx", djlx);
    ds.setValue("zk", zk);
    ds.post();
    ds.saveChanges();
    editrow = ds.getInternalRow();
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



