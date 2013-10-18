package engine.common;

import java.util.Date;
import java.util.Map;
import java.util.Hashtable;
import java.util.Set;
import java.util.ArrayList;
import engine.util.StringUtils;
import java.io.*;
/**
 * <p>Title: 用户类</p>
 * <p>Description: 用户类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author 江海岛
 * @version 1.0
 */
public final class User implements Serializable// HttpSessionBindingListener
{
  //用户id
  private String  userId      = "";
  //用户编码
  private String  userNo      = "";
  //用户姓名
  private String  userName    = "";
  //工号
  private String  workNo      = "";
  //部门id
  private String  deptId      = "";
  //部门编码
  private String  deptNo      = "";
  //部门名称
  private String  deptName    = "";
  //分公司id
  private String  filialeId   = "";
  //分公司编码
  private String  filialeNo   = "";
  //分公司名称
  private String  filialeName = "";
  //是否是会员
  private boolean isMember   = false;
  //是否登录
  private boolean isLogin    = false;
  //创建实例的时间
  private long    createDate = -1;
  //最后访问时间
  private long    lastAccessDate = -1;
  //最后登录时间,需要更新到数据库
  private long    lastLoginDate = -1;
  //登录的用户名
  private String longinName = "";
  //登录密码
  private String password  =  null;
  //ip地址
  private String ip = "";
  //User-Agent
  private String userAgent = null;
  //用户浏览器分辨率
  private int screen = 1;
  //是否可用
  private boolean isValid = true;
  //用户消息
  private UserMessage msg = null;
  //保存部门权限信息数据
  private Hashtable deptHandle = null;
  //保存仓库权限数据
  private ArrayList storeHadle = null;
  //
  //private int loginCount = 0;
  /**
   * 构造函数.
   * 创建一个新的实例并设置创建时间createDate.
   */
  public User()
  {
    this.createDate = System.currentTimeMillis();
  }

  /**
   * 得到用户的访问次数
   * @return 返回用户的访问次数.

  public int getAccessCounter()
  {
    return 0;
  }

  /**
   * 得到用户在当前session中的访问次数
   * @return 返回用户在当前session中的访问次数
   *
  public int getAccessCounterForSession()
  {
    return 0;
  }

  /**
   * 增加用户的反问次数。
   *
  public void incrementAccessCounter()
  {
    setAccessCounter(getAccessCounter() + 1);
  }

  /**
   * 增加用户在当前session的反问次数。
   *
  public void incrementAccessCounterForSession()
  {
    setAccessCounterForSession(getAccessCounterForSession() + 1);
  }

  /**
   * 给用户设置访问次数
   * @param count 新的次数.
   *
  public void setAccessCounter(int count)
  {
  }

  /**
   * 给用户设置当前session访问次数
   * @param count The new count.
   *
  public void setAccessCounterForSession(int count)
  {
  }

  /**
   * 是否已经登录
   * @return 是否已经登录
   */
  public boolean isLogin()
  {
    return isLogin;
  }

  /**
   * Gets the last access date for this User.  This is the last time
   * that the user object was referenced.
   * @return A Java Date with the last access date for the user.
   */
  public long getLastAccessDate()
  {
    if (lastAccessDate == -1)
      setLastAccessDate();
    return lastAccessDate;
  }

  /**
   * Sets the last access date for this User. This is the last time
   * that the user object was referenced.
   */
  public void setLastAccessDate()
  {
    lastAccessDate = System.currentTimeMillis();
    if(isLogin = true)
      UserFacade.putUser(this, false);
  }

  /**
   * 提取当前登录用户行序号
   * @return 当前登录用户行序号
   */
  public String getUserId()
  {
    return userId;
  }

  /**
   * 提取当前登录用户编号
   * @return 当前登录用户编号
   */
  public String getUserNo()
  {
    return userNo;
  }

  /**
   * 提取当前登录用户名称
   * @return 当前登录用户名称
   */
  public String getUserName()
  {
    return userName;
  }

  /**
   * 得到当前登录用户工号
   * @return 返回当前登录用户工号
   */
  public String getWorkNo()
  {
    return workNo;
  }

  /**
   * 提取当前登录用户的部门行序号
   * @return 当前登录用户的部门行序号
   */
  public String getDeptId()
  {
    return deptId;
  }

  /**
   * 提取当前登录用户的部门编号
   * @return 当前登录用户的部门编号
   */
  public String getDeptNo()
  {
    return deptNo;
  }

  /**
   * 提取当前登录用户的部门名称
   * @return 当前登录用户的部门名称
   */
  public String getDeptName()
  {
    return deptName;
  }

  /**
   * 得到登录用户的分公司序号
   * @return 返回登录用户的分公司序号
   */
  public String getFilialeId()
  {
    return filialeId;
  }

  /**
   * 得到登录用户的分公司名称
   * @return 返回登录用户的分公司名称
   */
  public String getFilialeName()
  {
    return filialeName;
  }
  /**
   * 得到登录用户的分公司编号
   * @return 返回登录用户的分公司编号
   */
  public String getFilialeNo()
  {
    return filialeNo;
  }
  /**
   * 得到登录用户是否是会员单位
   * @return 返回登录用户是否是会员单位
   */
  public boolean isMember()
  {
    return isMember;
  }

  /**
   * 是否可用
   * @return 返回是否可用
   */
  public boolean isValid()
  {
    return isValid;
  }

  /**
   * 是否是超级用户
   * @return 是否是超级用户
   */
  boolean isAdmin()
  {
    return getUserId().equals("0") || getUserId().equals("-1");
  }

  /**
   * 将用户的session为invalid
   */
  void invalid(){
    this.isValid = false;
  }

  /**
   * 设置用户id
   * @param userId 用户id
   */
  void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * 设置用户名称
   * @param userName 用户名称
   */
  void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * 设置用户编码
   * @param userNo
   */
  void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  /**
   * 设置用户编码
   * @param userNo
   */
  void setWrokNo(String workNo) {
    this.workNo = workNo;
  }

  /**
   * 设置部门id
   * @param deptId 部门id
   */
  void setDeptId(String deptId) {
    this.deptId = deptId;
  }

  /**
   * 设置部门名称
   * @param deptName 部门名称
   */
  void setDeptName(String deptName) {
    this.deptName = deptName;
  }

  /**
   * 设置部门编号
   * @param deptNo
   */
  void setDeptNo(String deptNo) {
    this.deptNo = deptNo;
  }

  /**
   * 设置分公司id
   * @param filialeId 分公司id
   */
  void setFilialeId(String filialeId) {
    this.filialeId = filialeId;
  }

  /**
   * 设置分公司名称
   * @param filialeName 分公司名称
   */
  void setFilialeName(String filialeName) {
    this.filialeName = filialeName;
  }

  /**
   * 设置分公司编码
   * @param filialeNo 分公司编码
   */
  void setFilialeNo(String filialeNo) {
    this.filialeNo = filialeNo;
  }

  /**
   * 登录
   */
  void login() {
    this.isLogin = true;
    //this.loginCount++;
  }

  /**
   * 退出
   */
  void logout(){
    this.isLogin = false;
    //this.loginCount --;
  }

  void setIsMember(boolean isMember) {
    this.isMember = isMember;
  }

  /*
  public int getLoginCount()
  {
    return this.loginCount;
  }*/

  /**
   * 得到用户的ip
   * @return 用户的ip
   */
  public String getIp() {
    return ip;
  }

  /**
   * 设置用户的ip
   * @param 用户的ip
   */
  void setIp(String ip) {
    this.ip = ip;
  }

  /**
   * 得到用户的浏览器代理对象
   * @return 用户的浏览器代理对象
   */
  public String getUserAgent() {
    return userAgent;
  }

  /**
   * 设置用户的浏览器代理对象
   * @return 用户的浏览器代理对象
   */
  void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  /**
   * 得到用户的登录名
   * @return 用户的登录名
   */
  public String getLonginName() {
    return longinName;
  }

  /**
   * 设置用户的登录名
   * @return 用户的登录名
   */
  void setLonginName(String longinName) {
    this.longinName = longinName;
  }

  /**
   * 得到用户的加密过的密码
   * @return 用户的加密过的密码
   */
  public String getPassword() {
    return password;
  }

  /**
   * 设置用户的加密过的密码
   * @param password 用户的加密过的密码
   */
  void setPassword(String password) {
    this.password = password;
  }

  /**
   * 在登录页面上检测用户的屏幕分辨率
   * @param screen 分辨率类型。0 - 640 * 480，1 - 800 * 600，2 - 1024 * 768，3 - 1280 * 720，4 - higher
   */
  void setScreen(int screen){
    this.screen = screen;
  }

  /**
   * 获取屏幕分辨率
   * @return 分辨率类型。0 - 640 * 480，1 - 800 * 600，2 - 1024 * 768，3 - 1280 * 720，4 - higher
   */
  public int getScreen(){
    return screen;
  }

  /**
   * 设置最后的登录时间,更新到数据库
   */
  void setLastLogin(){
    lastLoginDate = System.currentTimeMillis();
  }

  /**
   * 得到最后登录时间
   * @return 返回最后登录时间
   */
  public long getLastLoginDate() {
    if(lastLoginDate == -1)
      setLastLogin();
    return lastLoginDate;
  }

  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  /**
   * 初始化保存用户其他数据的map
   */
  private void initDeptHandle()
  {
    if(this.deptHandle == null)
      this.deptHandle = new Hashtable();
  }

  /**
   * 推入用户是否具有该部门的权限。isDeptHandle：true时,具有部门的所有权限
   * @param deptid 部门id
   * @param isDeptHandle 是否部门处理权限
   */
  synchronized void addDeptHandle(String deptid, boolean isDeptHandle)
  {
    if(deptid == null)
      return;
    initDeptHandle();
    deptHandle.put(deptid, new Boolean(isDeptHandle));
  }

  /**
   * 是否可以处理该部门该人员的单据
   * @param deptid 部门id
   * @param userId 人员id
   * @return 返回是否可处理
   */
  public boolean isDeptHandle(String deptid, String userId)
  {
    return isDeptHandle(deptid) ? true : this.userId.equals(userId);
  }

  /**
   * 是否可以处理该部门的单据
   * @param deptid 部门id
   * @return 返回是否可处理
   */
  public boolean isDeptHandle(String deptid)
  {
    Boolean isDeptHandle = deptHandle == null ? null : (Boolean)deptHandle.get(deptid);
    return isDeptHandle == null ? false : isDeptHandle.booleanValue();
  }

  /**
   * 得到具有权限的部门数组
   * @return 权限的部门数组
   */
  public synchronized String[] getHandleDepts()
  {
    if(deptHandle == null)
      return null;
    Set keys = deptHandle.keySet();
    return keys.isEmpty() ? null : (String[])keys.toArray(new String[keys.size()]);
  }

  /**
   * 得到具有权限的部门数组的值,默认的部门字段名称deptid，以逗号分开的字符串(例:deptid IN (1,2,3))，若没有任何部门权限，则返回空串
   * @return 权限的部门数组
   */
  public String getHandleDeptValue()
  {
    return getHandleDeptValue("deptid");
  }

  /**
   * 得到具有权限的部门数组的值,以逗号分开的字符串(例:deptid IN (1,2,3))，若没有任何部门权限，则返回1<>1
   * @param deptidName 部门字段名称
   * @return 权限的部门数组
   */
  public synchronized String getHandleDeptValue(String deptidName)
  {
    String s = StringUtils.getArrayValue(getHandleDepts());
    return s.length() == 0 ? "1<>1"
    : new StringBuffer(deptidName).append(" IN (").append(s).append(")").toString();
  }

  /**
   * 得到具有权限的部门数组的where条件的值(例:((deptid=1 AND zdrid=2) OR deptid=2),若没有任何部门权限，则返回1<>1<br>
   * 部门字段名称默认是deptid，制单人字段名称默认是zdrid
   * @return 权限的部门数组
   */
  public String getHandleDeptWhereValue()
  {
    return getHandleDeptWhereValue("deptid", "zdrid");
  }

  /**
   * 得到具有权限的部门数组的where条件的值(例:((deptid=1 AND zdrid=2) OR deptid=2),若没有任何部门权限，则返回1<>1
   * @param deptidName 部门字段名称
   * @param useridName 制单人字段名称
   * @return 权限的部门数组
   */
  public synchronized String getHandleDeptWhereValue(String deptidName, String useridName)
  {
    String[] deptids = getHandleDepts();
    if(deptids == null || deptids.length == 0)
      return "1<>1";
    StringBuffer buf = new StringBuffer("(");
    for(int i=0; i<deptids.length; i++)
    {
      boolean isHandle = isDeptHandle(deptids[i]);
      if(!isHandle)
        buf.append("(");
      buf.append(deptidName).append("=").append(deptids[i]);
      if(!isHandle)
        buf.append(" AND ").append(useridName).append("=").append(this.userId).append(")");
      if(i < deptids.length -1)
        buf.append(" OR ");
    }
    buf.append(")");
    return buf.toString();
  }

  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  /**
   * 初始化保存仓库权限的数据
   */
  private void initStoreHandle()
  {
    if(this.storeHadle == null)
      this.storeHadle = new ArrayList();
  }

  /**
   * 推入用户是否具有仓库的权限
   * @param storeid 仓库id
   */
  synchronized void addStoreHandle(String storeid)
  {
    if(storeid == null)
      return;
    initStoreHandle();
    storeHadle.add(storeid);
  }

  /**
   * 是否可以处理该仓库的单据
   * @param storeid 仓库id
   * @return 返回是否可处理
   */
  public boolean isStoreHandle(String storeid)
  {
    return storeHadle == null ? false : storeHadle.indexOf(storeid) > 0;
  }

  /**
   * 得到具有权限的仓库数组
   * @return 权限的仓库数组
   */
  public synchronized String[] getHandleStores()
  {
    if(storeHadle == null)
      return null;
    return StringUtils.listToStrings(storeHadle);
  }

  /**
   * 得到具有权限的仓库数组的值，以逗号分开的字符串(例:storeid IN (1,2,3))，若没有任何部门权限，则返回空串<br>
   * 仓库字段名称默认为storeid
   * @return 权限的仓库数组的值
   */
  public String getHandleStoreValue()
  {
    return getHandleStoreValue("storeid");
  }

  /**
   * 得到具有权限的仓库数组的值，以逗号分开的字符串(例:storeid IN (1,2,3))，若没有任何部门权限，则返回空串
   * @param storeidName 仓库字段名称
   * @return 权限的仓库数组的值
   */
  public synchronized String getHandleStoreValue(String storeidName)
  {
    String s = StringUtils.getArrayValue(getHandleStores());
    return s.length() == 0 ? "1<>1"
    : new StringBuffer(storeidName).append(" IN (").append(s).append(")").toString();
  }

  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  /**
   * 是否有消息
   * @return 返回是否有消息
   */
  public boolean hasMessage(){
    return msg != null;
  }

  /**
   * 得到用户的在线消息,并清除消息
   * @return 在线消息
   */
  public UserMessage getMessage() {
    UserMessage userMsg = msg;
    msg = null;
    return userMsg;
  }

  /**
   * 设置用户的在线消息
   * @param msg 在线消息
   */
  public void setMessage(UserMessage msg) {
    this.msg = msg;
  }
}
