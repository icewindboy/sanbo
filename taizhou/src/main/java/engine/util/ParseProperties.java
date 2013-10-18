package engine.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Map;
/**
 *
 * <p>Title: 分析特定属性的文件（以UTF-8形式）,并装载到类Properties中</p>
 * <p>Description: 文件的格式(一行表示一个属性)：<br>
 * Truth = Beauty <br>
 * Truth:Beauty <br>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author 江海岛
 * @version 1.0
 */
public class ParseProperties implements java.io.Serializable
{
  private static final String keyValueSeparators = "=: \t\r\n\f";

  private static final String strictKeyValueSeparators = "=:";

  private static final String whiteSpaceChars = " \t\r\n\f";

  /**
   * 从文件读取并分析属性保存到Hashtable中，并返回属性, 读取文件的编码方式是UTF-8
   * @param inStream 输入流
   * @return 返回得到的属性
   * @throws IOException 抛出IO异常
   */
  public static Hashtable parse(InputStream inStream) throws IOException
  {
    return parse(inStream, "UTF-8");
  }
  /**
   * 从文件读取并分析属性保存到Hashtable中，并返回属性
   * @param inStream 输入流
   * @param encoding 读取文件的编码方式//
   * @return 返回得到的属性
   * @throws IOException 抛出IO异常
   */
  public static Hashtable parse(InputStream inStream, String encoding) throws IOException
  {
    Hashtable properties = new Hashtable();
    parse(properties,inStream, encoding);
    return properties;
  }
  /**
   * 从文件读取并分析属性保存到Hashtable中, 读取文件的编码方式是UTF-8
   * @param inStream 输入流
   * @throws IOException 抛出IO异常
   */
  public static void parse(Map properties, InputStream inStream) throws IOException
  {
    parse(properties,inStream, "UTF-8");
  }
  /**
   * 从文件读取并分析属性保存到Hashtable中
   * @param properties 保存属性Hashtable
   * @param inStream 输入流
   * @param encoding 读取文件的编码方式 如果为null则用默认的
   * @throws IOException 抛出IO异常
   */
  public static void parse(Map properties, InputStream inStream, String encoding) throws IOException
  {
    parse(properties, inStream, encoding, false);
  }
  /**
   * 从文件读取并分析属性保存到Hashtable中
   * @param properties 保存属性Hashtable
   * @param inStream 输入流
   * @param encoding 读取文件的编码方式 如果为null则用默认的
   * @param keyToCap 是否将所有的key转化为大写的
   * @throws IOException 抛出IO异常
   */
  public static void parse(Map properties, InputStream inStream, String encoding, boolean keyToCap) throws IOException/*synchronized*/
  {
      BufferedReader in = new BufferedReader(encoding == null ?
          new InputStreamReader(inStream) : new InputStreamReader(inStream, encoding));
      while (true) {
          String line = in.readLine();
          if (line == null)
              return;

          if (line.length() > 0) {
              char firstChar = line.charAt(0);
              if ((firstChar != '#') && (firstChar != '!')) {
                  while (continueLine(line)) {
                      String nextLine = in.readLine();
                      if(nextLine == null)
                          nextLine = new String("");
                      String loppedLine = line.substring(0, line.length()-1);

                      int startIndex=0;
                      for(startIndex=0; startIndex<nextLine.length(); startIndex++)
                          if (whiteSpaceChars.indexOf(nextLine.charAt(startIndex)) == -1)
                              break;
                      nextLine = nextLine.substring(startIndex,nextLine.length());
                      line = new String(loppedLine+nextLine);
                  }

                  int len = line.length();
                  int keyStart;
                  for(keyStart=0; keyStart<len; keyStart++) {
                      if(whiteSpaceChars.indexOf(line.charAt(keyStart)) == -1)
                          break;
                  }

                  if (keyStart == len)
                      continue;

                  int separatorIndex;
                  for(separatorIndex=keyStart; separatorIndex<len; separatorIndex++) {
                      char currentChar = line.charAt(separatorIndex);
                      if (currentChar == '\\')
                          separatorIndex++;
                      else if(keyValueSeparators.indexOf(currentChar) != -1)
                          break;
                  }

                  int valueIndex;
                  for (valueIndex=separatorIndex; valueIndex<len; valueIndex++)
                      if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1)
                          break;

                  if (valueIndex < len)
                      if (strictKeyValueSeparators.indexOf(line.charAt(valueIndex)) != -1)
                          valueIndex++;

                  while (valueIndex < len) {
                      if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1)
                          break;
                      valueIndex++;
                  }
                  String key = line.substring(keyStart, separatorIndex);
                  String value = (separatorIndex < len) ? line.substring(valueIndex, len) : "";

                  key = loadConvert(key);
                  value = loadConvert(value);
                  if(keyToCap)
                    key = key.toUpperCase();
                  properties.put(key, value);
              }
          }
      }
    }
    /*
     * Converts encoded &#92;uxxxx to unicode chars
     * and changes special saved chars to their original forms
     */
    private static String loadConvert (String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);

        for(int x=0; x<len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if(aChar == 'u') {
                    int value=0;
                    for (int i=0; i<4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                          case '0': case '1': case '2': case '3': case '4':
                          case '5': case '6': case '7': case '8': case '9':
                             value = (value << 4) + aChar - '0';
                             break;
                          case 'a': case 'b': case 'c':
                          case 'd': case 'e': case 'f':
                             value = (value << 4) + 10 + aChar - 'a';
                             break;
                          case 'A': case 'B': case 'C':
                          case 'D': case 'E': case 'F':
                             value = (value << 4) + 10 + aChar - 'A';
                             break;
                          default:
                              throw new IllegalArgumentException(
                                           "Malformed \\uxxxx encoding.");
                        }
                    }
                    outBuffer.append((char)value);
                } else {
                    if (aChar == 't') aChar = '\t';
                    else if (aChar == 'r') aChar = '\r';
                    else if (aChar == 'n') aChar = '\n';
                    else if (aChar == 'f') aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
    /*
     * Returns true if the given line is a line that must
     * be appended to the next line
     */
    private static boolean continueLine (String line) {
        int slashCount = 0;
        int index = line.length() - 1;
        while((index >= 0) && (line.charAt(index--) == '\\'))
            slashCount++;
        return (slashCount % 2 == 1);
    }
}