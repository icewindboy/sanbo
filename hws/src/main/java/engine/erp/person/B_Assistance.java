package engine.erp.person;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.*;

import com.borland.dx.dataset.*;
import engine.dataset.*;
import engine.util.*;
import engine.util.log.LogHelper;
import engine.project.*;
import engine.common.LoginBean;

/**
 * <p>Title: 基础维护－人员辅助信息维护</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public class B_Assistance extends CommonClass
{
  //与地区操作相关变量
  public static final int OPERATE_ADD    = 20;//添加
  public static final int OPERATE_EDIT   = 21;//浏览或编辑
  public static final int OPERATE_DEL    = 22;//删除
  public static final int OPERATE_POST   = 23;//提交
  public static final int TYPE_CHANGE    = 31;//辅助信息类型的更改

  //表字段mps_wordbook.lx的各种类型
  public static final int PERSON_DUTY         = 1;//职务
  public static final int PERSON_TYPE         = 2;//人员类别
  public static final int PERSON_EDUCATION    = 3;//学历
  public static final int PERSON_PEOPLE       = 4;//民族
  public static final int PERSON_NATIVE_PLACE = 5;//籍贯
  public static final int PERSON_TECH_TITLE   = 6;//职称
  public static final int PERSON_POLITY       = 7;//政治面貌
  public static final int PERSON_YJXS       = 8;//移交信息

  public boolean isAdd = true;    //是否在添加状态
  public int     lx = PERSON_DUTY;//人员辅助信息的类型

  public EngineDataSet dsWorkbook  = new EngineDataSet();

  public RowMap rowInfo    = new RowMap(); //添加行或修改行的引用

  /**
   * 角色列表的实例
   * @param request jsp请求
   * @return 角色列表的实例
   */
  public static B_Assistance getInstance(HttpServletRequest request)
  {
    B_Assistance wordbookBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      wordbookBean = (B_Assistance)session.getAttribute("assistanceBean");
      if(wordbookBean == null)
      {
        wordbookBean = new B_Assistance();
        session.setAttribute("assistanceBean", wordbookBean);
      }
    }
    return wordbookBean;
  }
  /**
   * 构造函数
   */
  private B_Assistance()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
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
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    dsWorkbook.close();
    dsWorkbook = null;
    log = null;
    rowInfo = null;
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private void jbInit() throws Exception {
    setDataSetProperty(dsWorkbook, "SELECT * FROM rl_ygfzxx");

    dsWorkbook.setSort(new SortDescriptor("", new String[]{"dm"}, new boolean[]{false}, null, 0));
    dsWorkbook.setSequence(new SequenceDescriptor(new String[]{"ygfzxxid"}, new String[]{"s_rl_ygfzxx"}));
    /*dsWorkbook.addRowFilterListener(new com.borland.dx.dataset.RowFilterListener() {
      public void filterRow(ReadRow row, RowFilterResponse response) {
        dsWorkbook_filterRow(row, response);
      }
    });*/
  }
  /*数据集过滤事件
  void dsWorkbook_filterRow(ReadRow row, RowFilterResponse response) {
    if (row.getBigDecimal("lx").intValue() == lx)
      response.add();
  }
  */
  /**
   * JSP调用的函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    String sRe = "";
    if(request.getParameter("operate") == null || request.getParameter("operate").equals(""))
      return sRe;
    int operate = -1;
    try{
      operate = Integer.parseInt(request.getParameter("operate"));
    }
    catch(Exception ex){
      return sRe;
    }

    try{
      sRe = doService(request, response, operate);
    }
    catch(Exception ex){
      if(dsWorkbook.changesPending())
        dsWorkbook.reset();
      sRe = LoginBean.showMessage(ex.getMessage(), true);
      log.error("doService", ex);
    }

    return sRe;
  }
  /**
   * BEAN的逻辑部分
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @param operate 网页的操作类型
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private String doService(HttpServletRequest request, HttpServletResponse response, int operate) throws Exception
  {
    String sRe= "";
    switch(operate)
    {
      //辅助类型更改时
      case TYPE_CHANGE:
        try{
          lx = Integer.parseInt(request.getParameter("lx"));
        }
        catch(Exception e){
          return sRe;
        }
        if(lx < PERSON_DUTY || lx > PERSON_YJXS)
          lx = PERSON_DUTY;
        break;
      //
      case OPERATE_ADD:
      case OPERATE_EDIT:
        isAdd = operate == OPERATE_ADD;
        if(!isAdd)
          dsWorkbook.goToRow(Integer.parseInt(request.getParameter("rownum")));
        initRowInfo(isAdd, true);
        break;
      //
      case OPERATE_POST:
        //校验数据
        rowInfo.put(request);
        String dm = rowInfo.get("dm");
        String mc = rowInfo.get("mc");
        if(dm.equals(""))
          return LoginBean.showJavaScript("alert('编码不能为空！');");
        if(mc.equals(""))
          return LoginBean.showJavaScript("alert('名称不能为空！');");
        //if(countrycode.length())//长度
        if(isAdd || !dm.equals(dsWorkbook.getValue("dm")))
        {
          String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('rl_ygfzxx','dm','"+dm+"','lx="+lx+"') from dual");
          if(!count.equals("0"))
          {
            if(isAdd)
              initRowInfo(true, false);
            return LoginBean.showJavaScript("alert('编码("+ dm +")已经存在!');");
          }
        }

        if(isAdd)
        {
          dsWorkbook.insertRow(false);
          dsWorkbook.setValue("ygfzxxid", "-1");
          dsWorkbook.setValue("lx", String.valueOf(lx));
        }
        dsWorkbook.setValue("dm", dm);
        dsWorkbook.setValue("mc", mc);
        dsWorkbook.post();
        dsWorkbook.saveChanges();
        refreshDataSet();

        sRe = LoginBean.showJavaScript("parent.hideInterFrame();");
        break;
      //
      case OPERATE_DEL:
        dsWorkbook.goToRow(Integer.parseInt(request.getParameter("rownum")));
        dsWorkbook.deleteRow();
        dsWorkbook.saveChanges();
        refreshDataSet();
        break;
    }
    return sRe;
  }

  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   */
  private void initRowInfo(boolean isAdd, boolean isInit) throws Exception
  {
    if(!dsWorkbook.isOpen())
      dsWorkbook.open();
    //是否时添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();

    if(isAdd){
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('rl_ygfzxx','dm','','lx="+lx+"',6) from dual");
      rowInfo.put("dm", code);
    }
    else
      rowInfo.put(dsWorkbook);
  }

  /**
   * 得到部门列表的页面的<select>控件的<option></option>的字符串
   * @param currDeptid 当前部门ID，即是需要选定的部门ID
   * @return 部门信息列表
   */
  public synchronized String getPersonnelForOption(String currName, int type)
  {
    if(!dsWorkbook.isOpen())
      dsWorkbook.open();
    EngineDataView ds = dsWorkbook.cloneEngineDataView();
    String s = engine.action.BaseAction.dataSetToOption(ds, "mc", "mc", currName, "lx", String.valueOf(type));
    ds.close();
    ds = null;
    return s;
  }

  /**
   * 刷新数据集
   */
  private void refreshDataSet()
  {
    switch(lx)
    {
      case PERSON_DUTY:      //职务
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_PERSON_DUTY);
        break;
      case PERSON_TYPE:      //人员类别
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_PERSON_CLASS);
        break;
      case PERSON_EDUCATION: //学历
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_PERSON_EDUCATION);
        break;
      case PERSON_PEOPLE:    //民族
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_PERSON_NATION);
        break;
      case PERSON_NATIVE_PLACE://籍贯
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_PERSON_NATIVE);
        break;
      case PERSON_TECH_TITLE:  //职称
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_PERSON_TECH);
        break;
      case PERSON_POLITY:      //政治面貌
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_PERSON_POLITY);
        break;
      case PERSON_YJXS:      //移交信息
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_PRODUCE_PROCESS);
        break;
    }
  }
}