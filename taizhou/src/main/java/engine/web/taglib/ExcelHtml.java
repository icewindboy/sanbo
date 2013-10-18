package engine.web.taglib;

import java.io.Writer;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public class ExcelHtml
    extends DependTag {
  private String name = "Sheet1";
  private String footer = "&R第 &P 页，共 &N 页";

  public int doStartTag() throws javax.servlet.jsp.JspException {
    try {
      pageContext.getOut().print(excelHeader());
    }
    catch (java.io.IOException e) {
      e.printStackTrace();
    }
    return EVAL_BODY_INCLUDE;
  }

  private String excelHeader() {
    StringBuffer out = new StringBuffer();
    out.append("<html xmlns:v=\"urn:schemas-microsoft-com:vml\"").append("\n");
    out.append("xmlns:o=\"urn:schemas-microsoft-com:office:office\"").append("\n");
    out.append("xmlns:x=\"urn:schemas-microsoft-com:office:excel\"").append("\n");
    out.append("xmlns=\"http://www.w3.org/TR/REC-html40\">").append("\n");
    out.append("<head>").append("\n");
    out.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">").append("\n");
    out.append("<meta name=ProgId content=Excel.Sheet>").append("\n");
    out.append("<meta name=Generator content=\"Microsoft Excel 9\">").append("\n");
    out.append("<style>").append("\n");
    out.append("<!--table {mso-displayed-decimal-separator:\"\\.\"; mso-displayed-thousand-separator:\"\\,\";}").append("\n");
    if (footer != null && footer.length() > 0) {
      out.append("@page {mso-footer-data:\"").append(footer).append("\";");
      out.append("}").append("\n");
    }
    out.append("-->").append("\n");
    out.append("</style>").append("\n");
    out.append("</head>").append("\n");
    return out.toString();
  }

  public void release() {
    super.release();
    name = "Sheet1";
    footer = "&R第 &P 页，共 &N 页";
  }

  //
  public String getFooter() {
    return footer;
  }

  public void setFooter(String footer) {
    this.footer = footer;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
