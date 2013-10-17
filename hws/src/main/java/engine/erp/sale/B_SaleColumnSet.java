    package engine.erp.sale;

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
     * 销售分栏设置
     * <p>Copyright: Copyright (c) 2003</p>
     * <p>Company: </p>
     * @version 1.0
     */
    public final class B_SaleColumnSet extends BaseAction implements Operate
    {
      public  static final String SHOW_DETAIL = "9001";
      public  static final String DETAIL_CHANGE = "9007";
      public  static final String WZLB_ADD = "9008";
      public  static final String WZLB_CHANGE = "9009";
      private static final String MASTER_STRUT_SQL = "SELECT * FROM xs_flz WHERE 1<>1 ORDER BY mc DESC ";
      private static final String MASTER_SQL    = "SELECT * FROM xs_flz WHERE 1=1 ? ORDER BY mc DESC";
      private static final String DETAIL_STRUT_SQL = "SELECT * FROM xs_flzmx WHERE 1<>1";
      private static final String DETAIL_SQL    = "SELECT * FROM xs_flzmx WHERE flzid=";
      private static final String WL_SQL = "SELECT * FROM VW_SALE_FLZ ";
      private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
      private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
      private boolean isMasterAdd = true;    //是否在添加状态
      private boolean isWzlbAdd = false;    //是否在添加状态
      private long    masterRow = -1;         //保存主表修改操作的行记录指针
      private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
      private ArrayList d_RowInfos = null; //从表多行记录的引用
      public  String retuUrl = null;
      private String flzid = null;
      /**
       * 销售合同列表的实例
       * @param request jsp请求
       * @param isApproveStat 是否在审批状态
       * @return 返回销售合同列表的实例
       */
      public static B_SaleColumnSet getInstance(HttpServletRequest request)
      {
        B_SaleColumnSet b_SaleColumnSetBean = null;
        HttpSession session = request.getSession(true);
        synchronized (session)
        {
          String beanName = "b_SaleColumnSetBean";
          b_SaleColumnSetBean = (B_SaleColumnSet)session.getAttribute(beanName);
          if(b_SaleColumnSetBean == null)
          {
            //引用LoginBean
            LoginBean loginBean = LoginBean.getInstance(request);
            b_SaleColumnSetBean = new B_SaleColumnSet();
            session.setAttribute(beanName, b_SaleColumnSetBean);
          }
        }
        return b_SaleColumnSetBean;
      }

      /**
       * 构造函数
       */
      private B_SaleColumnSet()
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

        dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"flzid"}, new String[]{"S_XS_FLZ"}));
        dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"flzmxid"}, new String[]{"S_XS_FLZMX"}));

        Master_Add_Edit masterAddEdit = new Master_Add_Edit();
        Master_Post masterPost = new Master_Post();
        addObactioner(String.valueOf(INIT), new Init());
        addObactioner(SHOW_DETAIL, new ShowDetail());
        addObactioner(String.valueOf(ADD), masterAddEdit);
        addObactioner(String.valueOf(EDIT), masterAddEdit);
        addObactioner(String.valueOf(DEL), new Master_Delete());
        addObactioner(String.valueOf(POST), masterPost);

        addObactioner(String.valueOf(POST_CONTINUE), masterPost);
        addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
        addObactioner(String.valueOf(WZLB_ADD), new Detail_Add());
        addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
        addObactioner(String.valueOf(DETAIL_CHANGE), new Detail_Change());
        //addObactioner(String.valueOf(WZLB_ADD), new Wzlb_Add());
        addObactioner(String.valueOf(WZLB_CHANGE), new Wzlb_Change());



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
        log = null;
        m_RowInfo = null;
        d_RowInfos = null;
        deleteObservers();
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
            m_RowInfo.clear();//清除旧数据
          if(!isAdd)
            m_RowInfo.put(getMaterTable());//不是新增时,推入主表当前行
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
        for(int i=0; i<rownum; i++)
        {
          detailRow = (RowMap)d_RowInfos.get(i);
          detailRow.put("cpid", rowInfo.get("cpid_"+i));
          detailRow.put("wzlbid", rowInfo.get("wzlbid_"+i));
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
      /*打开从表*/
      public final void openDetailTable(boolean isMasterAdd)
      {
        flzid = dsMasterTable.getValue("flzid");//关链
        //isMasterAdd为真是返回空的从表数据集(主表新增时,从表要打开)
        dsDetailTable.setQueryString(DETAIL_SQL + (isMasterAdd ? "-1" : flzid));
        if(dsDetailTable.isOpen())
          dsDetailTable.refresh();
        else
          dsDetailTable.open();
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
      public final boolean wzlbIsAdd() {return isWzlbAdd; }


      /**
       * 得到选中的行的行数
       * @return 若返回-1，表示没有选中的行
       */
      public final int getSelectedRow()
      {
        if(masterRow < 0)
          return -1;

        dsMasterTable.goToInternalRow(masterRow);
        return dsMasterTable.getRow();
      }

      /**
       * 初始化操作的触发类
       */
      class Init implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          retuUrl = data.getParameter("src");
          retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
          HttpServletRequest request = data.getRequest();
          isMasterAdd = true;
          isWzlbAdd = false;
          String SQL = " ";
          SQL = combineSQL(MASTER_SQL, "?", new String[]{""});
          dsMasterTable.setQueryString(SQL);
          dsMasterTable.setRowMax(null);
          if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
            dsDetailTable.empty();
        }
      }

      /**
       * 显示从表的列表信息
       */
      class ShowDetail implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
          masterRow = dsMasterTable.getInternalRow();
          //打开从表
          openDetailTable(false);
        }
      }

      /**
       * 主表添加或修改操作的触发类
       */
      class Master_Add_Edit implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          isMasterAdd = String.valueOf(ADD).equals(action);//true主表新增
          if(!isMasterAdd)
          {
            dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));//查看或修改
            masterRow = dsMasterTable.getInternalRow();//返回当前行指针(long)
          }
          synchronized(dsDetailTable){
            openDetailTable(isMasterAdd);
          }
          initRowInfo(true, isMasterAdd, true);
          initRowInfo(false, isMasterAdd, true);

          data.setMessage(showJavaScript("toDetail();"));
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
            ds.goToInternalRow(masterRow);
          if(isMasterAdd){
            ds.insertRow(false);
            flzid = dataSetProvider.getSequence("S_XS_FLZ");//得到主表主键值
            ds.setValue("flzid", flzid);
          }
          //保存从表的数据
          RowMap detailrow = null;
          EngineDataSet detail = getDetailTable();
          detail.first();
          for(int i=0; i<detail.getRowCount(); i++)
          {
            detailrow = (RowMap)d_RowInfos.get(i);
            detail.setValue("cpid", detailrow.get("cpid"));
            detail.setValue("wzlbid", detailrow.get("wzlbid"));
            detail.setValue("flzid", flzid);
            detail.post();
            detail.next();
          }
          //保存主表数据
          ds.setValue("mc", rowInfo.get("mc"));//
          ds.setValue("bz", rowInfo.get("bz"));//
          ds.post();
          ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
          if(String.valueOf(POST_CONTINUE).equals(action))
          {
            isMasterAdd = true;
            detail.empty();
            initRowInfo(true, true, true);
            initRowInfo(false, true, true);//重新初始化从表的各行信息
          }
          else if(String.valueOf(POST).equals(action))
            data.setMessage(showJavaScript("backList();"));
          //else if(String.valueOf(POST).equals(action))
          //{}
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_SALE_COLUMN_BREAK);
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
          if(d_RowInfos.size()<1)
          {
            return showJavaScript("alert('从表为空,不能保存!');");
          }
          for(int i=0; i<d_RowInfos.size(); i++)
          {
            detailrow = (RowMap)d_RowInfos.get(i);
            String cpid=detailrow.get("cpid").trim();
            if(!cpid.equals("")&&!htmp.add(cpid))
              return showJavaScript("alert('所选产品重复!');");
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
          String temp = rowInfo.get("mc");
          if(temp.equals(""))
            return showJavaScript("alert('名称不能为空！');");
          return null;
        }
      }
      /**
       * 主表删除操作
       */
      class Master_Delete implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          if(isMasterAdd){
            data.setMessage(showJavaScript("backList();"));
            return;
          }
          EngineDataSet ds = getMaterTable();
          ds.goToInternalRow(masterRow);
          dsDetailTable.deleteAllRows();
          ds.deleteRow();
          ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
          d_RowInfos.clear();
          data.setMessage(showJavaScript("backList();"));
        }
      }
      /**
       *  从表新增,只是增加一空行
       */
      class Detail_Add implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          isWzlbAdd = false;
          HttpServletRequest request = data.getRequest();
          putDetailInfo(data.getRequest());

          ArrayList contain = new ArrayList();
          RowMap rowInfo = getMasterRowinfo();
          rowInfo.put(request);

          int rownum = d_RowInfos.size();
          RowMap detailRow = null;
          for(int i=0; i<rownum; i++)
          {
            detailRow = (RowMap)d_RowInfos.get(i);
            detailRow.put("cpid", rowInfo.get("cpid_"+i));
            detailRow.put("wzlbid", rowInfo.get("wzlbid_"+i));
            contain.add(rowInfo.get("cpid_"+i)+rowInfo.get("wzlbid_"+i));
          }
          String cpid="";
          String wzlbid="";
          if(String.valueOf(DETAIL_ADD).equals(action))
          {
            cpid = request.getParameter("singleInput");
            if(cpid.equals(""))
              return;
            EngineDataSet tmp = new EngineDataSet();
            if(tmp.open())
              tmp.close();
            setDataSetProperty(tmp,WL_SQL+" where cpid='"+cpid+"'");
            tmp.open();
            tmp.first();
            wzlbid = tmp.getValue("wzlbid");
            if(contain.contains(cpid+wzlbid))
            data.setMessage(showJavaScript("alert('已存在该物资类别!')"));
          }else
          {
            cpid="";
            isWzlbAdd = true;
          }

          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("flzmxid", "-1");
          dsDetailTable.setValue("cpid", cpid);
          dsDetailTable.setValue("wzlbid", "");
          dsDetailTable.post();
          RowMap detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
      /**
       *  物资类别新增
       *
       */
      class Wzlb_Add implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          HttpServletRequest request = data.getRequest();
          putDetailInfo(data.getRequest());
          isWzlbAdd = true;

          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("flzmxid", "-1");
          dsDetailTable.setValue("cpid", "");
          dsDetailTable.setValue("wzlbid", "");
          dsDetailTable.post();

          RowMap detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
       }
      }
      /**
       * 选择物资类别
       *
       */
      class Wzlb_Change implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          HttpServletRequest request = data.getRequest();
          putDetailInfo(data.getRequest());
          isWzlbAdd = true;

          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("flzmxid", "-1");
          dsDetailTable.setValue("cpid", "");
          dsDetailTable.setValue("wzlbid", "");

          RowMap detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
       }
      }
      /**
       * 直接选产品貌
       * 输入产品编码,产品名称
       * 对输入信息缓存
       */
      class Detail_Change implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          HttpServletRequest req = data.getRequest();
          putDetailInfo(data.getRequest());

          int rownum = d_RowInfos.size();
          RowMap detailRow = null;
        }
      }
      /**
       *  从表增加操作
       */
      class Detail_Delete implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          putDetailInfo(data.getRequest());
          int rownum = Integer.parseInt(data.getParameter("rownum"));
          d_RowInfos.remove(rownum);
          dsDetailTable.goToRow(rownum);
          dsDetailTable.deleteRow();
        }
      }
}