package engine.html;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;
/**
 * <p>Title: 将网页用用Excel控件显示类</p>
 * <p>Description: 将网页用用Excel控件显示类<br>
 * 调用Excel<br>
 *1.将网页的&lt;html&gt;改为:<br>
 * <textarea rows="5" name="S1" cols="90">
 * <html xmlns:v="urn:schemas-microsoft-com:vml"
 * xmlns:o="urn:schemas-microsoft-com:office:office"
 * xmlns:x="urn:schemas-microsoft-com:office:excel"
 * xmlns="http://www.w3.org/TR/REC-html40">
 * </textarea>
 * <br>
 * 2.在Head之间加代码:
 * <input type="text" size="90%" value="<%loginBean.showExcel(......);%>"><br>
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public final class HtmlExcel implements java.io.Serializable
{
  /**
   * 设置上下文地址
   * @param res WEB响应对象
   */
  public static void setContentType(HttpServletResponse res)
  {
    res.setContentType("application/vnd.ms-excel; charset=UTF-8");
  }

  private static void write(OutputStream os, String report, boolean landscape,
                            float[] margin,  int headerstart, int headerend)
  {
    PrintWriter out = new PrintWriter(os);
    out.print  ("<meta name=ProgId content=Excel.Sheet>");
    out.println("<meta name=Generator content=\"Microsoft Excel 9\">");
    out.println("<style>");
    out.println("<!--table {mso-displayed-decimal-separator:\"\\.\"; mso-displayed-thousand-separator:\"\\,\";}");
    out.print  ("@page {mso-footer-data:\"&R第 &P 页，共 &N 页\";");
    //输出页边距
    out.print  ("margin:");
    if(margin == null)
      out.print(".75in .75in .75in .75in;");
    else
    {
      for(int i=0; i<3; i++)
      {
        try{
          out.print(margin[i]/2.54 + "in ");
        }
        catch(ArrayIndexOutOfBoundsException aiex){
          out.print(".75in ");
        }
      }
      out.print(";");
    }
    //
    if(landscape)
      out.print("mso-page-orientation:landscape;");
    out.println("}-->");
    out.println("</style>");
    out.println("<!--[if gte mso 9]><xml>");
    out.println(" <x:ExcelWorkbook>");
    out.println("  <x:ExcelWorksheets>");
    out.println("   <x:ExcelWorksheet>");
    out.println("    <x:Name>"+ report +"</x:Name>");
    out.println("    <x:WorksheetOptions>");
    out.println("     <x:DefaultRowHeight>285</x:DefaultRowHeight>");
    out.println("     <x:Print>");
    out.println("      <x:ValidPrinterInfo/>");
    out.println("      <x:PaperSizeIndex>9</x:PaperSizeIndex>");
    out.println("      <x:HorizontalResolution>600</x:HorizontalResolution>");
    out.println("      <x:VerticalResolution>600</x:VerticalResolution>");
    out.println("     </x:Print>");
    out.println("     <x:Selected/>");
    out.println("     <x:DoNotDisplayGridlines/>");
    out.println("     <x:Panes>");
    out.println("      <x:Pane>");
    out.println("       <x:Number>3</x:Number>");
    out.println("       <x:ActiveRow>9</x:ActiveRow>");
    out.println("       <x:ActiveCol>1</x:ActiveCol>");
    out.println("      </x:Pane>");
    out.println("     </x:Panes>");
    out.println("     <x:ProtectContents>True</x:ProtectContents>");
    out.println("     <x:ProtectObjects>True</x:ProtectObjects>");
    out.println("     <x:ProtectScenarios>True</x:ProtectScenarios>");
    out.println("    </x:WorksheetOptions>");
    out.println("   </x:ExcelWorksheet>");
    out.println("  </x:ExcelWorksheets>");
    out.println("  <x:ProtectStructure>True</x:ProtectStructure>");
    out.println("  <x:ProtectWindows>False</x:ProtectWindows>");
    out.println(" </x:ExcelWorkbook>");
    if(headerend > 0)
    {
      out.println(" <x:ExcelName>");
      out.println("  <x:Name>Print_Titles</x:Name>");
      out.println("  <x:SheetIndex>1</x:SheetIndex>");
      out.println("  <x:Formula>="+ report +"!$"+ (headerstart<=0?1:headerstart) +":$"+ headerend +"</x:Formula>");
      out.println(" </x:ExcelName>");
    }
    out.println("</xml><![endif]-->");
  }

  /**
   * 网页用Excel的控件显示
   * @param OutputStream 输出流
   * @param reportName 报表名称
   * @param landscape 是否横向打印
   * @param pageMargin 顺时针:上,下,右,左; 单位厘米
   * @param headerStart 打印标题开始行
   * @param headerEnd 打印标题结束行
   */
  public static void showExcel(OutputStream os, String reportName, boolean landscape, float[] pageMargin, int headerStart, int headerEnd)
  {
    if(os == null)
      return;
    if(reportName == null || (reportName != null && reportName.equals("")))
      reportName = "Sheet1";

    write(os, reportName, landscape, pageMargin, headerStart, headerEnd);
  }
  /**
   * 网页用Excel的控件显示
   * @param pageContext jsp的pageContext
   * @param reportName 报表名称
   * @param landscape 是否横向打印
   * @param pageMargin 顺时针:上,下,右,左; 单位厘米
   */
  public static void showExcel(OutputStream os, String reportName, boolean landscape, float[] pageMargin)
  {
    showExcel(os, reportName, landscape, pageMargin, 0, 0);
  }
  /**
   * 网页用Excel的控件显示
   * @param pageContext jsp的pageContext
   * @param reportName 报表名称
   * @param landscape 是否横向打印
   */
  public static void showExcel(OutputStream os, String reportName, boolean landscape)
  {
    showExcel(os, reportName, landscape, null, 0, 0);
  }
  /**
   * 网页用Excel的控件显示
   * @param pageContext jsp的pageContext
   * @param reportName 报表名称
   */
  public static void showExcel(OutputStream os, String reportName)
  {
    showExcel(os, reportName, false, null, 0, 0);
  }
}