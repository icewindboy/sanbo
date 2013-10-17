package engine.erp.baseinfo;

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

import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 物资规格属性编辑类</p>
 * <p>Description: 物资规格属性编辑类<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class PropertyEdit extends BaseAction implements Operate
{
  private static final String PROD_PROPERTY_SQL = "SELECT * FROM kc_dmsx WHERE isdelete='0' AND cpid=";
  private static final String SPEC_PROPERTY_SQL = "SELECT a.dlsxID, a.sxmc, a.sxlx FROM kc_dmlbsx a WHERE a.wzlbid='@' ";
  private static final String PROPERTY_ADD_SQL
      = "SELECT * FROM kc_dmsx WHERE sxz='@' AND cpid='@' ORDER BY isdelete";
  private static final String PROPERTY_EDIT_SQL
      = "SELECT * FROM kc_dmsx WHERE sxz='@' AND cpid='@' AND dmsxid<>'@' ORDER BY isdelete";

  private EngineDataSet dsPropertyEdit  = new EngineDataSet();//规格属性表
  private EngineDataSet dsSpecProperty  = new EngineDataSet();//

  public String MULTY_ADD ="20001";
  private RowMap  rowInfo  = new RowMap(); //主表添加行或修改行的引用
  private RowMap  m_rowInfo  = new RowMap(); //父类的规格属性和保存在规格属性表中的属性并集
  public ArrayList keys = null;//保存规格属性用特定分割符分割后的数组
  /**
   * 是否在添加状态
   */
  public  boolean isAdd = true;
  //public  int num = 0; //规格属性用特定分割符分割后的数组的长度
  private B_CopyProperty B_CopyPropertyBean = null;
  /**
   * 保存修改操作的行记录指针
   */
  private long    editrow = 0;

  public  String retuUrl = null;
  private String cpid = null;   //参数产品ID
  private String wzlbid = null;   //参数产品大类ID
  /**
   * 物资规格属性的实例
   * @param request jsp请求
   * @return 返回物资规格属性的实例
   */
  public static PropertyEdit getInstance(HttpServletRequest request)
  {
    PropertyEdit propertyEditBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "propertyEditBean";
      propertyEditBean = (PropertyEdit)session.getAttribute(beanName);
      if(propertyEditBean == null)
      {
        propertyEditBean = new PropertyEdit();
        session.setAttribute(beanName, propertyEditBean);
      }
    }
    return propertyEditBean;
  }

  /**
   * 构造函数
   */
  private PropertyEdit()
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
    setDataSetProperty(dsPropertyEdit, null);
    setDataSetProperty(dsSpecProperty, null);

    dsPropertyEdit.setSequence(new SequenceDescriptor(new String[]{"dmsxid"}, new String[]{"s_kc_dmsx"}));

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(POST), new Post());
    addObactioner(String.valueOf(EDIT), new Property_Add_Edit());
    addObactioner(String.valueOf(ADD), new Property_Add_Edit());
    addObactioner(String.valueOf(DEL), new Delete());
    addObactioner(String.valueOf(MULTY_ADD), new Multy_Add());

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
      String opearate = request.getParameter(OPERATE_KEY);
      if(opearate != null && opearate.trim().length() > 0)
      {
        RunData data = notifyObactioners(opearate, request, response, null);
        if(data == null)
          return showMessage("无效操作", false);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsPropertyEdit.isOpen() && dsPropertyEdit.changesPending())
        dsPropertyEdit.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Session失效时，调用的函数
   */
  public final void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsPropertyEdit != null){
      dsPropertyEdit.close();
      dsPropertyEdit = null;
    }
    if(dsSpecProperty != null){
      dsSpecProperty.close();
      dsSpecProperty = null;
    }
    log = null;
    rowInfo = null;
    m_rowInfo = null;
  }

  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }
  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    return dsPropertyEdit;
  }
  /*得到父类产品的规格属性*/
  public final EngineDataSet getFatherTable()
  {
    if(!dsSpecProperty.isOpen())
      dsSpecProperty.open();
    return dsSpecProperty;
  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_rowInfo; }

  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      retuUrl = data.getParameter("src","");
      retuUrl = retuUrl.trim();
      cpid = request.getParameter("cpid");
      wzlbid = request.getParameter("wzlbid");
      //提取产品属性数据
      String sql = PROD_PROPERTY_SQL+cpid;
      dsPropertyEdit.setQueryString(sql);
      if(dsPropertyEdit.isOpen()){
        dsPropertyEdit.readyRefresh();
        dsPropertyEdit.refresh();
      }
      else
        dsPropertyEdit.openDataSet();
    }
  }
  class Multy_Add implements Obactioner
  {
    /**
     * 添加或修改的触发操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      String sxzs = request.getParameter("sxzs");
      if(sxzs.length() == 0)
        return;

      String[] dmsxids = parseString(sxzs,",");//解析出合同货物ID数组
      for(int i=0; i < dmsxids.length; i++)
      {
        if(dmsxids[i].equals("-1"))
          continue;

        String dmsxid = dmsxids[i];
        B_CopyPropertyBean =get_OrderProductBean(request);     //提货
        RowMap saleRow = B_CopyPropertyBean.getLookupRow(dmsxid);

        dsPropertyEdit.insertRow(false);
        dsPropertyEdit.setValue("dmsxid", "-1");
        String classmc=saleRow.get("classmc");
        String metalname =saleRow.get("metalname");
        dsPropertyEdit.setValue("cpid", cpid);
        dsPropertyEdit.setValue("sxz", classmc+metalname);
        dsPropertyEdit.setValue("classmc", classmc);
        dsPropertyEdit.setValue("metalname", metalname);
        dsPropertyEdit.setValue("isdelete", "0");
        dsPropertyEdit.post();
      }
      dsPropertyEdit.saveChanges();

    }
  }
  /**
   * 添加或修改操作的触发类
   */
  class Property_Add_Edit implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 添加或修改的触发操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      isAdd = action.equals(String.valueOf(ADD));
      //提取所属的大类属性
      String SQL = combineSQL(SPEC_PROPERTY_SQL, "@", new String[]{wzlbid});
      dsSpecProperty.setQueryString(SQL);
      if(dsSpecProperty.isOpen())
        dsSpecProperty.refresh();
      else
        dsSpecProperty.openDataSet();
      if(isAdd && dsSpecProperty.getRowCount()<1)
      {
        data.setMessage(showJavaScript("alert('该物资没有大类属性，请先设置大类属性');parent.hideInterFrame();"));
        return;
      }
      if(keys == null)
        keys = new ArrayList();
      else
        keys.clear();
      m_rowInfo.clear();
      //初始化属性值
      String sxmcz = null;
      for(int j=0; j< dsSpecProperty.getRowCount(); j++)
      {
        dsSpecProperty.goToRow(j);
        sxmcz = dsSpecProperty.getValue("sxmc");//得到属性名称值
        if(!keys.contains(sxmcz)){
          keys.add(sxmcz);
          //m_rowInfo.put(sxmcz, "");
        }
      }
      //修改操作
      if(!isAdd)
      {
        dsPropertyEdit.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsPropertyEdit.getInternalRow();
        String sxz = dsPropertyEdit.getValue("sxz");
        m_rowInfo.put(dsPropertyEdit);
        /*
        String[] code = parseString(sxz, "()");
        String key = null, value = null;
        //num = keys.size()+1;
        //把属性名称作为Key推入RowMap里面，在页面修改的时候要显示所有属性
        for(int i=0; i<code.length; i++)
        {
          if(i%2 > 0)
          {
            value = code[i];
            m_rowInfo.put(key, value);//如果该规格属性中含有类别属性降覆盖Key并赋值
          }
          else{
            key = code[i].trim();
            if(!keys.contains(key))
              keys.add(key);
          }
        }
        */
      }

      data.setMessage(showJavaScript("showInterFrame();"));
    }
  }
  /**
   * 删除操作的触发类
   */
  class Delete implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发删除操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.setValue("isdelete", "1");
      ds.post();
      ds.saveChanges();
      ds.deleteRow();
      try{
        ds.saveChanges();
      }
      catch(Exception ex){
        log.warn("delete",ex);
        if(dsPropertyEdit.isOpen() && dsPropertyEdit.changesPending())
          dsPropertyEdit.reset();
        //data.setMessage(showJavaScript("alert('该物资规格属性已被引用')"));
        return;
      }
    }
  }
  /**
   * 主表保存操作的触发类
   */
  class Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      rowInfo.put(data.getRequest());
      EngineDataSet ds = dsSpecProperty;
      ds.first();
      //校验是否全部未空
      boolean isNotNull = false;
      String propValue = null, sxlx=null, sx=null, alert=null;
      EngineRow locateRow = new EngineRow(ds, "sxmc");
      String classmc = rowInfo.get("classmc");
      String metalname = rowInfo.get("metalname");
      /*
      for(int i=0; i<keys.size(); i++)
      {
        String key = (String)keys.get(i);
        propValue = rowInfo.get(key);
        if(propValue.length() > 0)
          isNotNull = true;
        else
          continue;

        locateRow.setValue(0, key);
        if(ds.locate(locateRow, Locate.FIRST))
          sxlx = ds.getValue("sxlx");
        else
          sxlx = "";

        if(sxlx.equals("number"))
        {
          if((alert = checkNumber(propValue,  key+"的属性")) != null)
          {
            data.setMessage(showJavaScript("hideInterFrame();")+alert);
            return;
          }
        }
      }
      */
      if(metalname.equals(""))
      {
        data.setMessage(showJavaScript("hideInterFrame(); alert('属性值不完整！')"));
        return;
      }

      /*
      * 如果是主表增加就用上级的属性加上页面输入值如果是编辑就用原来存储在数据库的大类属性加用户输入值
      * 这样的话如果类别属性被删除了，在修改的时候还能保存

      StringBuffer buf = null;
      for(int i=0; i<keys.size(); i++)
      {
        String key = (String)keys.get(i);
        propValue = rowInfo.get(key);
        if(propValue.length() == 0)
          continue;
      //
        locateRow.setValue(0, key);
        if(ds.locate(locateRow, Locate.FIRST))
          sxlx = ds.getValue("sxlx");
        else
          sxlx = "";

        if(buf == null)
          buf = new StringBuffer();
        else
          buf.append(" ");
      //字符串的格式
        if(sxlx.equals("lowerchar"))
          propValue = propValue.toLowerCase();
        else if(sxlx.equals("upperchar"))
          propValue = propValue.toUpperCase();
        buf.append(ds.getValue("sxmc")).append("(").append(propValue).append(")");
      }
      */
      String sxz = classmc.equals("")?metalname:classmc+metalname;
      if(!isAdd)
        dsPropertyEdit.goToInternalRow(editrow);

      String SQL = isAdd ? combineSQL(PROPERTY_ADD_SQL, "@", new String[]{sxz, cpid})
                   : combineSQL(PROPERTY_EDIT_SQL, "@", new String[]{sxz, cpid, dsPropertyEdit.getValue("dmsxid")});
      EngineDataSet dsTemp = new EngineDataSet();
      setDataSetProperty(dsTemp, SQL);
      dsTemp.openDataSet();
      if(isAdd && dsTemp.getRowCount() > 0)
      {
        if(dsTemp.getValue("isdelete").equals("0"))
          data.setMessage(showJavaScript("hideInterFrame();alert('该属性已经存在,请重填！');"));
        else
        {
          dsTemp.setValue("isdelete", "0");
          dsTemp.post();
          dsTemp.saveChanges();
          dsPropertyEdit.refresh();
        }
        return;
      }
      else if(!isAdd && dsTemp.getRowCount() > 0 &&  dsTemp.getValue("isdelete").equals("0"))
      {
        data.setMessage(showJavaScript("hideInterFrame();alert('该属性已经存在,请重填！');"));
        return;
      }

      if(isAdd)
      {
        dsPropertyEdit.insertRow(false);
        dsPropertyEdit.setValue("dmsxid", "-1");
      }

      dsPropertyEdit.setValue("cpid", cpid);
      dsPropertyEdit.setValue("sxz", sxz);
      dsPropertyEdit.setValue("classmc", classmc);
      dsPropertyEdit.setValue("metalname", metalname);
      dsPropertyEdit.setValue("isdelete", "0");
      dsPropertyEdit.saveChanges();
      data.setMessage(showJavaScript("hideInterFrame();"));
    }
  }
  public B_CopyProperty get_OrderProductBean(HttpServletRequest req)
  {
    if(B_CopyPropertyBean == null)
      B_CopyPropertyBean = B_CopyProperty.getInstance(req);
    return B_CopyPropertyBean;
  }
}