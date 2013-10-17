package engine.erp.sale.xixing;

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
     * <p>Title: 客户信誉额度 </p>
     * 建了两个数据集,其中一个数据集用于获取所有往来单位,
     * 另一个数据集从信誉额度表中获取数据,条件是前一个数据集当前状态下中所有往来单位
     * 后一个数据集只保存前一个数据集中有填写内容的记录
     * 重点:数据集与ArrayList中间变量同步问题.
     * 客户信誉额度表:xs_khxyed的单表操作.
     * <p>Copyright: Copyright (c) 2003</p>
     * <p>Company: </p>
     * <p>Author:Engine </p>
     * @version 1.0
     */

    public final class B_CustomerCredit extends BaseAction implements Operate
    {
      /**
       *对应修改按钮
       * */
      public static final String MODIFY = "1005";
      /**
       *默认状态是不能修改
       * */
      private boolean canModify = false;
      /**
       * 查询操作
       **/
      public static final String FIXED_SEARCH = "1009";
      /**
       * 提取客户信誉额度表的结构
       */
      private static final String CUSTOMER_CREDIT_STRUCT_SQL = "SELECT * FROM xs_khxyed WHERE 1<>1 ";
      /**
       * 从与客户信誉额度相关联的视图中提取数据
       * 以便取出所有往来单位信息
       * */
      private static final String CUSTOMER_CREDIT_SQL = "SELECT * FROM VW_XS_XYED WHERE fgsid=? ? ORDER BY areacode,dwdm ";//
      /**
       * 客户信誉额度表
       * */
      private static final String XYED_SQL = "SELECT * FROM xs_khxyed  WHERE fgsid=? ? ";
      /**
       * 保存客户信誉额度信息的数据集(从视图中抽取)
       */
      private EngineDataSet dsxs_khxyed = new EngineDataSet();
      /**
       * 保存客户信誉额度信息的数据集
       */
      private EngineDataSet dsxs_khxyed_salve = new EngineDataSet();
      /**
       * 用于定位数据集
       */
      private EngineRow locateRow = null;

      /**
       * 保存用户输入的信息
       */
      private RowMap rowInfo = new RowMap();
      private ArrayList d_RowInfos = null;
      /**
       * 是否在添加状态
       */
      public  boolean isAdd = true;

      /**
       * 保存修改操作的行记录指针
       */
      private long    editrow = 0;

      /**
       * 点击返回按钮的URL
       */
      public  String retuUrl = null;

      private boolean isInitQuery = false;
      public  String loginName = ""; //登录员工的姓名
      private String fgsid = null;   //分公司ID
      /**
      * 定义固定查询类
       */
      private QueryFixedItem fixedQuery = new QueryFixedItem();
      /**
       * 得到客户信誉额度信息的实例
       * @param request jsp请求
       * @param isApproveStat 是否在审批状态
       * @return 返回报价资料信息的实例
       */
      public static B_CustomerCredit getInstance(HttpServletRequest request)
      {
        B_CustomerCredit b_CustomerCreditBean = null;
        HttpSession session = request.getSession(true);
        synchronized (session)
        {
          String beanName = "b_CustomerCreditBean";
          b_CustomerCreditBean = (B_CustomerCredit)session.getAttribute(beanName);
          //判断该session是否有该bean的实例
          if(b_CustomerCreditBean == null)
          {
            LoginBean loginBean = LoginBean.getInstance(request);
            String fgsid = loginBean.getFirstDeptID();
            b_CustomerCreditBean = new B_CustomerCredit(fgsid);
            b_CustomerCreditBean.loginName = loginBean.getUserName();
            //设置格式化的字段
            //b_CustomerCreditBean.dsxs_khxyed.setColumnFormat("xyed", loginBean.getPriceFormat());
            session.setAttribute(beanName, b_CustomerCreditBean);
          }
        }
        return b_CustomerCreditBean;
      }
      /**
       * 构造函数
       */
      private B_CustomerCredit(String fgsid)
      {
        this.fgsid = fgsid;
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
        setDataSetProperty(dsxs_khxyed, CUSTOMER_CREDIT_STRUCT_SQL);
        setDataSetProperty(dsxs_khxyed_salve, CUSTOMER_CREDIT_STRUCT_SQL);
        //dsxs_khxyed_salve.setSequence(new SequenceDescriptor(new String[]{"xyedid"}, new String[]{"S_XS_KHXYED"}));
        dsxs_khxyed.setSort(new SortDescriptor("", new String[]{"areacode","dwdm"}, new boolean[]{false,false}, null, 0));
        dsxs_khxyed.setTableName("xs_khxyed");

        dsxs_khxyed.addLoadListener(new com.borland.dx.dataset.LoadListener() {
          public void dataLoaded(LoadEvent e) {
            initRowInfo(false, true);
          }
        });
        //添加操作的触发对象
        addObactioner(String.valueOf(INIT), new B_CustomerCredit_Init());
        addObactioner(String.valueOf(DETAIL_DEL), new B_CustomerCredit_Delete());
        addObactioner(String.valueOf(POST), new B_CustomerCredit_Post());
        addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());
        addObactioner(String.valueOf(MODIFY), new Modify());
      }

      public String doService(HttpServletRequest request, HttpServletResponse response)
      {
        try{
          String opearate = request.getParameter(OPERATE_KEY);
          if(opearate != null && opearate.trim().length() > 0)
          {
            RunData data = notifyObactioners(opearate, request, response, null);
            if(data.hasMessage())
              return data.getMessage();
          }
          return "";
        }
        catch(Exception ex){
          if(dsxs_khxyed.isOpen() && dsxs_khxyed.changesPending())
            dsxs_khxyed.reset();
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
        if(dsxs_khxyed != null){
          dsxs_khxyed.close();
          dsxs_khxyed = null;
        }
        log = null;
        rowInfo = null;
        locateRow = null;
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
        if(!dsxs_khxyed.isOpen())
        {
          dsxs_khxyed.open();
        }
        int rownum = dsxs_khxyed.getRowCount();
        RowMap detailRow = null;
        for(int i=0; i<rownum; i++)
        {
          detailRow = (RowMap)d_RowInfos.get(i);
          detailRow.put("dwtxid", rowInfo.get("dwtxid_"+i));//往来单位

          //detailRow.put("djlx", rowInfo.get("djlx_"+i));
          //detailRow.put("zklx", rowInfo.get("zklx_"+i));
          //detailRow.put("zkl", rowInfo.get("zkl_"+i));

          detailRow.put("xyed", rowInfo.get("xyed_"+i));//信誉额度
          detailRow.put("xydj", rowInfo.get("xydj_"+i));//信誉等级
          detailRow.put("hkts", rowInfo.get("hkts_"+i));//回款天数
          d_RowInfos.set(i,detailRow);
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
      /**
       * 初始化行信息
       * @param isAdd 是否时添加
       * @param isInit 是否从新初始化
       * @throws java.lang.Exception 异常
       */
      private final void initRowInfo(boolean isAdd, boolean isInit) //throws java.lang.Exception
      {
        StringBuffer buf = null;
        EngineDataSet dsxs_khxyed_tmp = dsxs_khxyed;
        if(d_RowInfos == null)
          d_RowInfos = new ArrayList(dsxs_khxyed_tmp.getRowCount());
        else if(isInit)
          d_RowInfos.clear();
        dsxs_khxyed_tmp.first();
        for(int i=0; i<dsxs_khxyed_tmp.getRowCount(); i++)
        {
          if(buf == null)
            buf = new StringBuffer("AND dwtxid IN(").append(dsxs_khxyed_tmp.getValue("dwtxid"));
          else
            buf.append(",").append(dsxs_khxyed_tmp.getValue("dwtxid"));

          RowMap row = new RowMap(dsxs_khxyed_tmp);
          d_RowInfos.add(row);
          dsxs_khxyed_tmp.next();
        }
        if(buf == null)
          buf =new StringBuffer();
        else
          buf.append(")");
        String SQL = combineSQL(XYED_SQL, "?", new String[]{fgsid, buf.toString()});
        dsxs_khxyed_salve.setQueryString(SQL);
        if(dsxs_khxyed_salve.isOpen())
          dsxs_khxyed_salve.refresh();
        else
          dsxs_khxyed_salve.openDataSet();
      }
      /*得到表对象*/
      public final EngineDataSet getOneTable()
      {
        return dsxs_khxyed;
      }
      /*得到一列的信息*/
      public final RowMap getRowinfo()
      {
        return rowInfo;
      }
      /*得到状态*/
      public final boolean getState()
      {
        return canModify;
      }
      /*得到从表多列的信息*/
      public final RowMap[] getDetailRowinfos() {
        RowMap[] rows = new RowMap[d_RowInfos.size()];
        d_RowInfos.toArray(rows);
        return rows;
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

      //---------------------------------------------------------------------
      //以下是操作实现的类(共5个内部类)
      //---------------------------------------------------------------------

      /**
       * 初始化操作的触发类
       */
      class B_CustomerCredit_Init implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          canModify= false;
          retuUrl = data.getParameter("src");
          retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
          fixedQuery.getSearchRow().clear();

          String SQL = combineSQL(CUSTOMER_CREDIT_SQL,"?",new String[]{fgsid,""});
          dsxs_khxyed.setQueryString(SQL);
          dsxs_khxyed.setRowMax(null);
        }
      }
      /**
       * 添加查询操作的触发类
       */
      class FIXED_SEARCH implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          initQueryItem(data.getRequest());
          QueryBasic queryBasic = fixedQuery;
          queryBasic.setSearchValue(data.getRequest());
          String SQL = queryBasic.getWhereQuery();
          if(SQL.length() > 0)
            SQL = " AND "+SQL;
          SQL = combineSQL(CUSTOMER_CREDIT_SQL, "?", new String[]{fgsid, SQL});
          if(!dsxs_khxyed.getQueryString().equals(SQL))
          {
            dsxs_khxyed.setQueryString(SQL);
            dsxs_khxyed.setRowMax(null);
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
          EngineDataSet master = dsxs_khxyed;
          if(!master.isOpen())
            master.open();
          //初始化固定的查询项目
          //往来单位dwtxId;信誉额度xyed;信誉等级xydj;回款天数hkts;
          fixedQuery.addShowColumn("", new QueryColumn[]{
            new QueryColumn(master.getColumn("dwtxId"), null, null, null, null, "="),
            new QueryColumn(master.getColumn("dwmc"), null, null, null, null, "="),
            new QueryColumn(master.getColumn("dwdm"), null, null, null, null, "="),
            new QueryColumn(master.getColumn("areacode"), null, null, null,  "a", ">="),
            new QueryColumn(master.getColumn("areacode"), null, null, null, "b", "<="),
            new QueryColumn(master.getColumn("xm"), null, null, null, null, "like"),
            new QueryColumn(master.getColumn("xyed"), null, null, null, "a", ">="),
            new QueryColumn(master.getColumn("xyed"), null, null, null, "b", "<="),
            new QueryColumn(master.getColumn("xydj"), null, null, null, "a", ">="),
            new QueryColumn(master.getColumn("xydj"), null, null, null, "b", "<="),
            new QueryColumn(master.getColumn("hkts"), null, null, null, "a", ">="),
            new QueryColumn(master.getColumn("hkts"), null, null, null, "b", "<="),
          });
          isInitQuery = true;
        }
      }
      /**
       * 保存操作的触发类
       */
      class B_CustomerCredit_Post implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          RowMap detailrow = null;
          putDetailInfo(data.getRequest());

          for(int i=0; i<d_RowInfos.size(); i++)
          {
            detailrow = (RowMap)d_RowInfos.get(i);
            String xyed=detailrow.get("xyed");
            String hkts=detailrow.get("hkts");
            String tmp = null;
            if(xyed.length()>0)
            {
              tmp = checkNumber(xyed,"信誉额度");
            }
            if(tmp!=null)
            {
              data.setMessage(tmp);
              return;
            }
            if(hkts.length()>0)
            {
              tmp = checkNumber(hkts,"回款天数");
            }
            if(tmp!=null)
            {
              data.setMessage(tmp);
              return;
            }
          }
          EngineRow locateGoodsRow = new EngineRow(dsxs_khxyed_salve, new String[]{"dwtxid"});
          dsxs_khxyed_salve.first();
          for(int i=0; i<d_RowInfos.size(); i++)
          {
            detailrow = (RowMap)d_RowInfos.get(i);
            String dwtxid=detailrow.get("dwtxid");
            String xyed=detailrow.get("xyed");
            String xydj=detailrow.get("xydj");
            String hkts=detailrow.get("hkts");

            //String djlx=detailrow.get("djlx");
            //String zklx=detailrow.get("zklx");
            //String zkl=detailrow.get("zkl");

             //if(xyed.equals("")&&xydj.equals("")&&hkts.equals(""))
             //  continue;
             locateGoodsRow.setValue(0,dwtxid);
             if(!dsxs_khxyed_salve.locate(locateGoodsRow, Locate.FIRST))
             {
               dsxs_khxyed_salve.insertRow(false);
               //String xyedid = dataSetProvider.getSequence("S_XS_KHXYED");
               //dsxs_khxyed.setValue("xyedid",xyedid);
             }
             dsxs_khxyed_salve.setValue("dwtxid", dwtxid);
             dsxs_khxyed_salve.setValue("fgsid", fgsid);

             //dsxs_khxyed_salve.setValue("djlx", djlx);
             //dsxs_khxyed_salve.setValue("zklx", zklx);
             //dsxs_khxyed_salve.setValue("zkl", zkl);

             dsxs_khxyed_salve.setValue("xyed", xyed);
             dsxs_khxyed_salve.setValue("xydj", xydj);
             dsxs_khxyed_salve.setValue("hkts", hkts);
             dsxs_khxyed_salve.post();
          }
          dsxs_khxyed_salve.saveChanges();
          dsxs_khxyed.refresh();
          initRowInfo(false,true);
          canModify=false;
         /*
          EngineDataSet ds = new EngineDataSet();
          setDataSetProperty(ds,XYED_SQL);
          ds.setSequence(new SequenceDescriptor(new String[]{"xyedid"}, new String[]{"S_XS_KHXYED"}));
          for(int i=0; i<d_RowInfos.size(); i++)
          {
            detailrow = (RowMap)d_RowInfos.get(i);
            String dwtxid=detailrow.get("dwtxid");
            String xyed=detailrow.get("xyed");
            String xydj=detailrow.get("xydj");
            String hkts=detailrow.get("hkts");
              if(!xyed.trim().equals("")||!xydj.trim().equals("")||!hkts.trim().equals(""))
              {
                ds.setQueryString("SELECT * FROM xs_khxyed where dwtxid='"+dwtxid+"'");
                if(!ds.isOpen())
                  ds.open();
                ds.refresh();
                if(ds.getRowCount()==0)
                {
                  ds.insertRow(false);
                  ds.setValue("xyedid","-1");
                  ds.setValue("dwtxid", dwtxid);
                  ds.setValue("fgsid", fgsid);
                }
                ds.setValue("xyed", xyed);
                ds.setValue("xydj", xydj);
                ds.setValue("hkts", hkts);
                ds.post();
                ds.saveChanges();
              }
          }
          dsxs_khxyed.refresh();
          */
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
       * 删除操作的触发类
       */
      class B_CustomerCredit_Delete implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          EngineDataSet ds = getOneTable();
          int num = Integer.parseInt(data.getParameter("rownum"));
          ds.goToRow(num);
          ds.deleteRow();
          ds.saveChanges();
        }
      }
}