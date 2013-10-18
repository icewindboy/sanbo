package engine.web.upload;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetException;

public final class File
{
  private FileUpload m_parent   = null;
  private byte[] selfDatas      = null;
  private int m_startData       = 0;
  private int m_endData         = 0;
  private int m_size            = 0;
  private String m_fieldname    = null;
  private String m_filename     = null;
  private String m_fileExt      = null;
  private String m_filePathName = null;
  private String m_contentType  = null;
  private String m_contentDisp  = null;
  private String m_typeMime     = null;
  private String m_subTypeMime  = null;
  //private String m_contentString = null;
  private boolean m_isMissing   = true;
  public static final int SAVEAS_AUTO = 0;
  public static final int SAVEAS_VIRTUAL = 1;
  public static final int SAVEAS_PHYSICAL = 2;

  File(){
  }

  public void download(HttpServletResponse res, String destFileName)
      throws IOException
  {
    if(m_contentType == null || m_contentType.length() == 0)
      res.setContentType("application/octet-stream");
    else
      res.setContentType(m_contentType);

    res.setContentLength(m_size);
    if(destFileName != null && destFileName.length()>0)
      res.setHeader("Content-Disposition",
                    new StringBuffer("attachment; filename=").append(destFileName).toString());
    this.write(res.getOutputStream());
  }

  public void saveAs(OutputStream os) throws FileUploadException, IOException
  {
    if(os == null)
      throw new IllegalArgumentException("There is no specified destination output stream.");
    try{
      this.write(os);
    }
    catch(IOException e)
    {
      throw new FileUploadException("OutputStream can't be saved (1120).");
    }
  }

  /**
   * 保存到数据集中
   * @param ds 数据集对象
   * @param columnName 字段名
   * @throws FileUploadException 文件上传异常
   * @throws IOException 读写异常
   */
  public void saveAs(DataSet ds, String columnName) throws FileUploadException, IOException
  {
    if(ds == null)
      throw new IllegalArgumentException("The DataSet cannot be null.");
    if(columnName == null)
      throw new IllegalArgumentException("The columnName cannot be null.");
    if(columnName.length() == 0)
      throw new IllegalArgumentException("The columnName cannot be empty.");

    ds.setInputStream(columnName, new ByteArrayInputStream(m_parent.m_binArray, m_startData, m_size));
  }

  public void saveAs(String destFilePathName) throws FileUploadException, IOException
  {
    saveAs(destFilePathName, 0);
  }

  public void saveAs(String destFilePathName, int optionSaveAs) throws FileUploadException, IOException
  {
    String path = new String();
    path = m_parent.getPhysicalPath(destFilePathName, optionSaveAs);
    if(path == null)
      throw new IllegalArgumentException("There is no specified destination file (1140).");
    try
    {
      java.io.File file = new java.io.File(path);
      FileOutputStream fileOut = new FileOutputStream(file);
      this.write(fileOut);
      fileOut.close();
    }
    catch(IOException e)
    {
      throw new FileUploadException("File can't be saved (1120).");
    }
  }

  public void fileToField(ResultSet rs, String columnName)
      throws SQLException, FileUploadException, IOException, ServletException
  {
    long numBlocks = 0L;
    int blockSize = 0x10000;
    int leftOver = 0;
    int pos = 0;
    if(rs == null)
      throw new IllegalArgumentException("The RecordSet cannot be null (1145).");
    if(columnName == null)
      throw new IllegalArgumentException("The columnName cannot be null (1150).");
    if(columnName.length() == 0)
      throw new IllegalArgumentException("The columnName cannot be empty (1155).");
    numBlocks = BigInteger.valueOf(m_size).divide(BigInteger.valueOf(blockSize)).longValue();
    leftOver = BigInteger.valueOf(m_size).mod(BigInteger.valueOf(blockSize)).intValue();
    try
    {
      for(int i = 1; (long)i < numBlocks; i++)
      {
        rs.updateBinaryStream(columnName, new ByteArrayInputStream(m_parent.m_binArray, pos, blockSize), blockSize);
        pos = pos != 0 ? pos : 1;
        pos = i * blockSize;
      }

      if(leftOver > 0)
        rs.updateBinaryStream(columnName, new ByteArrayInputStream(m_parent.m_binArray, pos, leftOver), leftOver);
    }
    catch(SQLException e)
    {
      byte binByte2[] = new byte[m_size];
      System.arraycopy(m_parent.m_binArray, m_startData, binByte2, 0, m_size);
      rs.updateBytes(columnName, binByte2);
    }
    catch(Exception e)
    {
      throw new FileUploadException("Unable to save file in the DataBase (1130).");
    }
  }

  public boolean isMissing()
  {
    return m_isMissing;
  }

  public String getFieldName()
  {
    return m_fieldname;
  }

  public String getFileName()
  {
    return m_filename;
  }

  public String getFilePathName()
  {
    return m_filePathName;
  }

  public String getFileExt()
  {
    return m_fileExt;
  }

  public String getContentType()
  {
    return m_contentType;
  }

  public String getContentDisp()
  {
    return m_contentDisp;
  }

  public String getContentString()
  {
    String strTMP = new String(m_parent.m_binArray, m_startData, m_size);
    return strTMP;
  }

  public String getTypeMIME()
      throws IOException
  {
    return m_typeMime;
  }

  public String getSubTypeMIME()
  {
    return m_subTypeMime;
  }

  public int getSize()
  {
    return m_size;
  }

  protected int getStartData()
  {
    return m_startData;
  }

  protected int getEndData()
  {
    return m_endData;
  }

  //剥离父亲的数据
  public void leaveParent(){
    selfDatas = new byte[m_size];
    System.arraycopy(m_parent.m_binArray, m_startData, selfDatas, 0, m_size);
  }

  public void write(OutputStream os) throws IOException
  {
    if(m_parent != null)
      os.write(m_parent.m_binArray, m_startData, m_size);
    else
      os.write(selfDatas);
  }

  protected void setParent(FileUpload parent)
  {
    m_parent = parent;
    selfDatas = null;
  }

  protected void setStartData(int startData)
  {
    m_startData = startData;
  }

  protected void setEndData(int endData)
  {
    m_endData = endData;
  }

  protected void setSize(int size)
  {
    m_size = size;
  }

  protected void setIsMissing(boolean isMissing)
  {
    m_isMissing = isMissing;
  }

  protected void setFieldName(String fieldName)
  {
    m_fieldname = fieldName;
  }

  protected void setFileName(String fileName)
  {
    m_filename = fileName;
  }

  protected void setFilePathName(String filePathName)
  {
    m_filePathName = filePathName;
  }

  protected void setFileExt(String fileExt)
  {
    m_fileExt = fileExt;
  }

  protected void setContentType(String contentType)
  {
    m_contentType = contentType;
  }

  protected void setContentDisp(String contentDisp)
  {
    m_contentDisp = contentDisp;
  }

  protected void setTypeMIME(String TypeMime)
  {
    m_typeMime = TypeMime;
  }

  protected void setSubTypeMIME(String subTypeMime)
  {
    m_subTypeMime = subTypeMime;
  }

  public byte getBinaryData(int index)
  {
    if(m_startData + index > m_endData)
      throw new ArrayIndexOutOfBoundsException("Index Out of range (1115).");
    if(m_startData + index <= m_endData)
      return m_parent.m_binArray[m_startData + index];
    else
      return 0;
  }
}