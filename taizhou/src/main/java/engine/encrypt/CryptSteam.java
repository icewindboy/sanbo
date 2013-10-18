package engine.encrypt;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * <p>Title: 加密解密流的接口</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author hukn
 * @version 1.0
 */
public interface CryptSteam
{
  /**
   * 加密文件
   * @param i 输入文件流
   * @param o 输出文件流
   * @throws IOException 异常
   */
  public void encryptStream(InputStream i, OutputStream o) throws IOException;

  /**
   * 解密文件
   * @param i 输入文件流
   * @param o 输出文件流
   * @throws IOException 异常
   */
  public void decryptStream(InputStream i, OutputStream o) throws IOException;

  /**
   * 加密字符串
   * @param src 未加密的字符串
   * @return 返回加密的字符串
   */
  public String encryptString(String src);

  /**
   * 解密字符串
   * @param src 未解密的的字符串
   * @return 返回解密的字符串
   */
  public String decryptString(String src);

}