package engine.common;

import java.io.Serializable;
import java.text.SimpleDateFormat;
/**
 * <p>Title: 即时消息类</p>
 * <p>Description: 即时消息类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author 江海岛
 * @version 1.0
 */

public final class UserMessage implements Serializable{

  private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  //发消息的用户id
  private String fromUserId = null;
  //发消息的用户姓名
  private String fromUserName = null;
  //目标用户id
  private String toUserId   = null;
  //消息
  private String message    = null;

  private java.util.Date sendDate = null;

  public UserMessage(String fromUserId, String fromUserName, String toUserId, String message)
  {
    this.fromUserId   = fromUserId == null ? "" : fromUserId;
    this.fromUserName = fromUserName == null ? "" : fromUserName;
    this.toUserId     = toUserId == null ? "" : toUserId;
    this.message      = message == null ? "" : message;
    this.sendDate     = new java.util.Date();
  }

  public String getFromUserId() {
    return fromUserId;
  }

  public String getFromUserName() {
    return fromUserName;
  }

  public String getMessage() {
    return message;
  }

  public String getToUserId() {
    return toUserId;
  }

  public java.util.Date getSendDate()
  {
    return sendDate;
  }

  public String getSendDateString()
  {
    synchronized(format)
    {
      return format.format(sendDate);
    }
  }
}