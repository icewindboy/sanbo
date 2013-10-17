package engine.web.html;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
/**
 * <p>Title: 将网页用用Excel控件显示类</p>
 * <p>Description: 将网页用用Excel控件显示类
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public final class HtmlExcel implements java.io.Serializable
{
  HtmlExcel(PageContext pageContext, String report, boolean landscape, float[] margin, int headerstart, int headerend)
  {
    this.out = pageContext.getOut();
    pageContext.getResponse().setContentType("application/vnd.ms-excel; charset=utf-8");
    write(margin, landscape, report, headerstart, headerend);
  }
  private JspWriter out;

  private void print(String s)
  {
    try{
      out.print(s);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  private void priln(String s)
  {
    try{
      out.println(s);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  private void write(float[] margin, boolean landscape, String report, int headerstart, int headerend)
  {
    print("<meta name=ProgId content=Excel.Sheet>");
    priln("<meta name=Generator content=\"Microsoft Excel 9\">");
    priln("<style>");
    priln("<!--table {mso-displayed-decimal-separator:\"\\.\"; mso-displayed-thousand-separator:\"\\,\";}");
    print("@page {mso-footer-data:\"&R第 &P 页，共 &N 页\";");
    print("margin:");
    if(margin == null)
      print(".75in .75in .75in .75in;");
    else
    {
      for(int i=0; i<3; i++)
      {
        try{
          print(margin[i]/2.54 + "in ");
        }
        catch(ArrayIndexOutOfBoundsException aiex){
          print(".75in ");
        }
      }
      print(";");
    }
    //
    if(landscape)
      print("mso-page-orientation:landscape;");
    priln("}-->");
    priln("</style>");
    priln("<!--[if gte mso 9]><xml>");
    priln(" <x:ExcelWorkbook>");
    priln("  <x:ExcelWorksheets>");
    priln("   <x:ExcelWorksheet>");
    priln("    <x:Name>"+ report +"</x:Name>");
    priln("    <x:WorksheetOptions>");
    priln("     <x:DefaultRowHeight>285</x:DefaultRowHeight>");
    priln("     <x:Print>");
    priln("      <x:ValidPrinterInfo/>");
    priln("      <x:PaperSizeIndex>9</x:PaperSizeIndex>");
    priln("      <x:HorizontalResolution>600</x:HorizontalResolution>");
    priln("      <x:VerticalResolution>600</x:VerticalResolution>");
    priln("     </x:Print>");
    priln("     <x:Selected/>");
    priln("     <x:DoNotDisplayGridlines/>");
    priln("     <x:Panes>");
    priln("      <x:Pane>");
    priln("       <x:Number>3</x:Number>");
    priln("       <x:ActiveRow>9</x:ActiveRow>");
    priln("       <x:ActiveCol>1</x:ActiveCol>");
    priln("      </x:Pane>");
    priln("     </x:Panes>");
    priln("     <x:ProtectContents>True</x:ProtectContents>");
    priln("     <x:ProtectObjects>True</x:ProtectObjects>");
    priln("     <x:ProtectScenarios>True</x:ProtectScenarios>");
    priln("    </x:WorksheetOptions>");
    priln("   </x:ExcelWorksheet>");
    priln("  </x:ExcelWorksheets>");
    priln("  <x:ProtectStructure>True</x:ProtectStructure>");
    priln("  <x:ProtectWindows>False</x:ProtectWindows>");
    priln(" </x:ExcelWorkbook>");
    if(headerend > 0)
    {
      priln(" <x:ExcelName>");
      priln("  <x:Name>Print_Titles</x:Name>");
      priln("  <x:SheetIndex>1</x:SheetIndex>");
      priln("  <x:Formula>="+ report +"!$"+ (headerstart<=0?1:headerstart) +":$"+ headerend +"</x:Formula>");
      priln(" </x:ExcelName>");
    }
    priln("</xml><![endif]-->");
  }

  /**
   * 网页用Excel的控件显示
   * @param pageContext jsp的pageContext
   * @param reportName 报表名称
   * @param landscape 是否横向打印
   * @param pageMargin 顺时针:上,下,右,左; 单位厘米
   * @param headerStart 打印标题开始行
   * @param headerEnd 打印标题结束行
   */
  public static void showExcel(PageContext pageContext, String reportName, boolean landscape, float[] pageMargin, int headerStart, int headerEnd)
  {
    if(pageContext == null)
      return;
    if(reportName == null || (reportName != null && reportName.equals("")))
      reportName = "Sheet1";
    new HtmlExcel(pageContext, reportName, landscape, pageMargin, headerStart, headerEnd);
  }
  /**
   * 网页用Excel的控件显示
   * @param pageContext jsp的pageContext
   * @param reportName 报表名称
   * @param landscape 是否横向打印
   * @param pageMargin 顺时针:上,下,右,左; 单位厘米
   */
  public static void showExcel(PageContext pageContext, String reportName, boolean landscape, float[] pageMargin)
  {
    showExcel(pageContext, reportName, landscape, pageMargin, 0, 0);
  }
  /**
   * 网页用Excel的控件显示
   * @param pageContext jsp的pageContext
   * @param reportName 报表名称
   * @param landscape 是否横向打印
   */
  public static void showExcel(PageContext pageContext, String reportName, boolean landscape)
  {
    showExcel(pageContext, reportName, landscape, null, 0, 0);
  }
  /**
   * 网页用Excel的控件显示
   * @param pageContext jsp的pageContext
   * @param reportName 报表名称
   */
  public static void showExcel(PageContext pageContext, String reportName)
  {
    showExcel(pageContext, reportName, false, null, 0, 0);
  }
}