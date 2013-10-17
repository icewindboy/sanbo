package engine.common;

import java.io.*;
import java.util.*;
/**
 * <p>Title: 管理登录用户的类</p>
 * <p>Description: 管理登录用户的类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author 江海岛
 * @version 1.0
 */
/**
 * 2004.5.1 允许同一用户名同一IP可多次登录
 */
public final class UserFacade implements Serializable
{
  //登录用户池
  private static Hashtable userPool = new Hashtable(50);

  private static Boolean bMoreLogin = null;

  public synchronized final static boolean isInit()
  {
    return bMoreLogin != null;
  }
  /**
   * 设置是否允许同一用户同一IP开多个窗体
   * @param isMore 是否允许同一用户同一IP开多个窗体
   */
  public synchronized final static void setMoreLogin(boolean isMore)
  {
    if(bMoreLogin == null)
      bMoreLogin = new Boolean(isMore);
  }

  /**
   * 是否允许同一用户同一IP开多个窗体
   * @return 返回是否允许同一用户同一IP开多个窗体
   */
  public synchronized final static boolean isMoreLogin()
  {
    return bMoreLogin == null ? false : bMoreLogin.booleanValue();
  }

  /**
   * 在在线用户列表中得到一个登录用户对象
   * @param userId 用户的ID
   * @return 返回在线用户对象
   */
  public final static User getUser(String userId)
  {
    if(userId == null)
      return null;
    //2004.5.1 允许同一用户名同一IP可多次登录
    //return (User)userPool.get(userId);
    synchronized(userPool)
    {
      ArrayList list = (ArrayList)userPool.get(userId);
      if(list == null)
        return null;
      else if(list.size() == 0)
      {
        userPool.remove(userId);
        return null;
      }
      else
        return (User)list.get(0);
    }
  }

  /**
   * 在在线用户列表中删除一个登录用户对象
   * @param userId 用户的ID
   * @return 返回删除的用户对象
   */
  public final static void removeUser(User user)
  {
    //2004.5.1 允许同一用户名同一IP可多次登录
    //return (User)userPool.remove(userId);
    if(user == null)
      return;
    synchronized(userPool)
    {
      //检查不可用的用户
      checkInvalidUsers();

      String userId = user.getUserId();
      ArrayList list = (ArrayList)userPool.get(userId);
      if(list == null)
        return;

      list.remove(user);
      if(list.size() == 0)
        userPool.remove(userId);
    }
    /*User tempUser = (User)list.get(0);
    /if(tempUser.getIp().equals(user.getIp()))
      list.remove(user);
    else
    {
      userPool.remove(userId);
      for(int i=0; i<list.size(); i++)
      {
        tempUser = (User)list.get(i);
        tempUser.logout();
        tempUser.invalid();
      }
      list.clear();
    }*/
  }

  /**
   * 将用户登录用户对象保存到在线用户列表中
   * @param user 登录用户对象
   */
  public final static void putUser(User user)
  {
    putUser(user, true);
  }

  /**
   * 将用户登录用户对象保存到在线用户列表中
   * @param user 登录用户对象
   * @param isCheckInvalidUsers 是否检测无效用户
   */
  final static void putUser(User user, boolean isCheckInvalidUsers)
  {
    if(user == null)
      return;
    String userId = user.getUserId();
    if(userId == null)
      return;
    synchronized(userPool)
    {
      //检查不可用的用户
      if(isCheckInvalidUsers)
        checkInvalidUsers();
      //2004.5.1 允许同一用户名同一IP可多次登录
      ArrayList list = (ArrayList)userPool.get(userId);
      //User oldUser = (User)userPool.get(userId);
      if(list == null || list.size() == 0)
      {
        user.login();
        if(list == null)
          list = new ArrayList();
        list.add(user);
        userPool.put(userId, list);
      }
      else// if(user != oldUser)
      {
        User tempUser = (User)list.get(0);
        if(!isMoreLogin() || !tempUser.getIp().equals(user.getIp()))
        {
          for(int i=0; i<list.size(); i++)
          {
            tempUser = (User)list.get(i);
            tempUser.logout();
            tempUser.invalid();
          }
          list.clear();
        }
        //oldUser.logout();
        //oldUser.invalid();
        user.login();
        if(!list.contains(user))
          list.add(user);
        //userPool.put(userId, user);
      }
    }
    /*else if(oldUser.getIp().equals(user.getIp()) && maxCount >= oldUser.getLoginCount())
    /  return;
      else
    oldUser.login();*/
  }

  /**
   * 得到所有用户列表
   * @return 所有用户列表
   */
  public final static User[] getUsers()
  {
    synchronized(userPool)
    {
      //检查不可用的用户
      checkInvalidUsers();

      Collection values = userPool.values();
      ArrayList[] allusers = (ArrayList[])values.toArray(new ArrayList[values.size()]);
      User[] users = new User[allusers.length];
      for(int i=0; i<allusers.length; i++)
        users[i] = (User)allusers[i].get(0);
      return users;
    }
  }

  /**
   * 给用户发送消息
   * @param msg 消息对象
   * @throws UserNotFoundException 没有发现用户异常
   */
  public final static void sendMessage(UserMessage msg)
    throws UserNotFoundException
  {
    synchronized(userPool)
    {
      String toUserId = msg.getToUserId();
      if(toUserId == null)
        return;
      User toUser = getUser(toUserId);//(User)userPool.get(toUserId);
      if(toUser == null)
        throw new UserNotFoundException();
      toUser.setMessage(msg);
    }
  }

  /**
   * 检查不可用的用户
   */
  private final static void checkInvalidUsers()
  {
    Date currDate = new Date();
    Collection values = userPool.values();
    ArrayList[] allusers = (ArrayList[])values.toArray(new ArrayList[values.size()]);
    for(int i=0; i<allusers.length; i++)
    {
      ArrayList oneusers = allusers[i];
      for(int j=0; j<oneusers.size(); j++)
      {
        User user = (User)oneusers.get(j);
        long lastAccess = user.getLastAccessDate();
        //20分钟为限制
        if(currDate.getTime() - lastAccess > 20*60*1000)
        {
          String userId = user.getUserId();
          ArrayList list = (ArrayList)userPool.get(userId);
          if(list == null)
            continue;

          list.remove(user);
          if(list.size() == 0)
            userPool.remove(userId);
        }
      }
    }
  }

  /**
   * 得到在线人数
   * @return 返回在线人数
   */
  public final static int size()
  {
    return userPool.size();
  }
}