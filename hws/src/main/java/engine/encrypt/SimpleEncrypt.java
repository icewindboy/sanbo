package engine.encrypt;

import java.io.*;
/**
 * <p>Title: 简单的加密类</p>
 * <p>Description: 简单的加密类
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author hukn
 * @version 1.0
 */

public class SimpleEncrypt implements CryptSteam
{
  //加密的密钥
  private static final int secretKey = "JACkey".hashCode();

  /**
   * 加密文件
   * @param i 输入文件流
   * @param o 输出文件流
   * @throws IOException 异常
   */
  public void encryptStream(InputStream i, OutputStream o) throws IOException
  {
    try
    {
    cryptStream(i, o, secretKey, true);
    }
    finally{
      i.close();
      o.close();
    }
  }

  /**
   * 解密文件
   * @param i 输入文件流
   * @param o 输出文件流
   * @throws IOException 异常
   */
  public void decryptStream(InputStream i, OutputStream o) throws IOException
  {
    try
    {
      cryptStream(i, o, secretKey, false);
    }
    finally{
      i.close();
      o.close();
    }
  }
  /**
   * 加密或解密文件
   * @param i 输入文件流
   * @param o 输出文件流
   * @param secretKey 密匙
   * @param isEncrypt 是：加密 否：解密
   * @throws IOException 异常
   */
  private static void cryptStream(InputStream i, OutputStream o,
      int secretKey, boolean isEncrypt) throws IOException
  {
    synchronized(i)
    {
      DataInputStream dataIS = null;
      DataOutputStream dataOS = null;
      dataIS = new DataInputStream(i);
      dataOS = new DataOutputStream(o);
      int data = dataIS.read();
      if(isEncrypt)
      {
        for(; data != -1; data = dataIS.read())
          dataOS.write(data + secretKey);
      }
      else
      {
        for(; data != -1; data = dataIS.read())
          dataOS.write(data - secretKey);
      }
    }
  }

  /**
   * 加密字符串
   * @param src 未加密的字符串
   * @return 返回加密的字符串
   */
  public String encryptString(String src)
  {
    if(src == null)
      return "null";
    ByteArrayInputStream ais = new ByteArrayInputStream(src.getBytes());
    ByteArrayOutputStream aos = new ByteArrayOutputStream();
    try
    {
      cryptStream(ais, aos, secretKey, true);
      return aos.toString();
    }
    catch(IOException ex){
      return "null";
    }
    finally{
      try{
        ais.close();
      }
      catch(IOException ex){}
      try{
        aos.close();
      }
      catch(IOException ex){}
    }
  }

  /**
   * 解密字符串
   * @param src 未解密的的字符串
   * @return 返回解密的字符串
   */
  public String decryptString(String src)
  {
    if(src == null)
      return "null";
    ByteArrayInputStream ais = new ByteArrayInputStream(src.getBytes());
    ByteArrayOutputStream aos = new ByteArrayOutputStream();
    try
    {
      cryptStream(ais, aos, secretKey, false);
      return aos.toString();
    }
    catch(IOException ex){
      return "null";
    }
    finally{
      try{
        ais.close();
      }
      catch(IOException ex){}
      try{
        aos.close();
      }
      catch(IOException ex){}
    }
  }
}