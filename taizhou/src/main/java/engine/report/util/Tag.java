package engine.report.util;

import engine.web.html.HtmlTag;
/**
 * Title:        模板自定义的标签
 * Description:  模板自定义的标签<br>
1 打印的三种情况：（例子见buy_number.htm。需要将模板文件保存在WEB_INFO/classes/report目录下）<br>
1.1 套打：<br>
将页面重定向：response.sendRedirect(&quot;../pub/pdfprint.jsp?operate=Operate.PRINT_PRECISION&amp;code=kc_stock&amp;data=ptfp&quot;);<br>
参数code表示套打描述文件名,data表示套打数据保存在session中的key,数据是ReportData[]实例
<br>
1.2 非套打: <br>
将页面重定向：response.sendRedirect(&quot;../pub/pdfprint.jsp?operate=Operate.PRINT_BILL&amp;code=kc_stock&quot;);
<br>
<p> 1.3 模板打印<br>
  节点的url ../pub/pdf.jsp?code=buy_number 内部编码：buy_number。buy_number就是模板的名称</p>
<p> 1.4 动态模板打印：<br>
  将页面重定向：response.sendRedirect(&quot;../pub/pdf.jsp?code=buy_number.buf&quot;);//名称必须要以.buf结尾<br>
  用StringBuffer类代替静态的模板，此时名称必须要以.buf结尾. buy_number.buf表示将StringBuffer类保存在session中的key</p>
<p>2.1编写模板文件</p>
<p>&lt;html&gt;<br>
  &lt;title name=&quot;采购统计量&quot;&gt;\\报表名称<br>
  &lt;body bgcolor=&quot;#FFFFFF&quot; text=&quot;#000000&quot; leftmargin=&quot;0&quot;
  topmargin=&quot;25&quot;&gt;<br>
  &lt;%--type:templet(模板打印),precision(套打)。height:页面高度(厘米单位)。width：页面宽度。magin-left：页面左边距。magin-right：页面右边距。magin-top：页面上边距。magin-bottom：页面下边距。footer-left：页脚左边。footer-center：页脚中间。footer-right：页脚右边。font-size：默认的字体大小--%&gt;
  <br>
  &lt;page type=&quot;templet&quot; height=&quot;21&quot; width=&quot;29.7&quot;
  magin-left=&quot;1&quot; magin-right=&quot;1&quot; magin-top=&quot;0.8&quot;
  magin-bottom=&quot;1&quot; footer-center=&quot;第 &amp;P 页，共 &amp;N 页&quot; font-size=&quot;9&quot;&gt;<br>
  &lt;%--dataset：数据源描述标签，需要/dataset来结束。name：名称。object：数据库对象或SQL语句。type：sql(视图或sql语句),procedure(存储过程)--%&gt;
  <br>
  &lt;dataset name=&quot;buy&quot; object=&quot;VW_BUY_NUMBER_REPORT&quot; type=&quot;sql&quot;&gt;<br>
  &lt;%--field：字段描述标签。name：名称。caption：字段显示的名称。group:是否可分组(false/true),默认false。sum:是否可统计(false/true),默认false
  include：包含其他的字段可用逗号分割多个字段--%&gt;
  <br>
  &lt;field name=&quot;bh&quot; caption=&quot;合同号&quot; group=&quot;true&quot; include=&quot;htid&quot;&gt;<br>
  &lt;field name=&quot;wzpm&quot; caption=&quot;品名&quot; group=&quot;true&quot;&gt;<br>
  &lt;field name=&quot;wzgg&quot; caption=&quot;规格&quot; group=&quot;true&quot;&gt;<br>
  &lt;field name=&quot;zl&quot; caption=&quot;重量&quot; sum=&quot;true&quot;&gt;<br>
  &lt;field name=&quot;hsdj&quot; caption=&quot;单价&quot; group=&quot;true&quot;&gt;<br>
  &lt;field name=&quot;je&quot; caption=&quot;含税金额&quot; sum=&quot;true&quot;&gt;<br>
  &lt;field name=&quot;xm&quot; caption=&quot;业务员&quot; group=&quot;true&quot;&gt;<br>
  &lt;field name=&quot;dwmc&quot; caption=&quot;供货单位&quot; group=&quot;true&quot;&gt;<br>
  &lt;field name=&quot;mc&quot; caption=&quot;部门&quot; group=&quot;true&quot;&gt;
  <br>
  &lt;%--where：查询条件标签。field：字段名。caption：显示名称。linktable:外键关联的表。linkcolumn:外键字段名。querycolumn：用于显示的字段名。extendname:field的扩展名。opersign：操作符号(&amp;gt;,
  &amp;lt; , =, &amp;gt;=, &amp;lt;=, &amp;lt;&amp;gt;, like, in, left_like, right_like)。initvalue：初始化值。datatype：数据类型(varchar,number,date,time,datetime)。type：输入框类型(text,
  lookup, select, combox, radio)。value：类型是select或combox的枚举值。lookup：类型是select或combox的lookupBean的key名称。script：lookup类型的输入框要调用的javasript函数。span：是否独占一行--%&gt;<br>
  &lt;where field=&quot;htrq&quot; caption=&quot;合同日期&quot; linktable=&quot;&quot;
  linkcolumn=&quot;&quot; querycolumn=&quot;&quot; extendname=&quot;a&quot; opersign=&quot;&amp;gt;=&quot;
  initvalue=&quot;2001-01-01&quot; datatype=&quot;date&quot;&gt;<br>
  &lt;where field=&quot;htrq&quot; caption=&quot;--&quot; linktable=&quot;&quot;
  linkcolumn=&quot;&quot; querycolumn=&quot;&quot; extendname=&quot;b&quot; opersign=&quot;&amp;lt;=&quot;
  initvalue=&quot;2005-01-01&quot; datatype=&quot;date&quot;&gt;<br>
  &lt;where field=&quot;bh&quot; caption=&quot;合同号&quot; datatype=&quot;varchar&quot;&gt;
  <br>
  &lt;where field=&quot;htlb&quot; caption=&quot;类别&quot; value=&quot;=全部,1=进口,2=未审&quot;
  type=&quot;radio&quot; span=&quot;true&quot;&gt;<br>
  &lt;where field=&quot;deptid&quot; caption=&quot;部门&quot; datatype=&quot;number&quot;
  opersign=&quot;=&quot; type=&quot;lookup&quot; span=&quot;true&quot; script=&quot;CustSingleSelect('this.form.name','@','fieldVar=dwtxid&amp;fieldVar=dwmc')&quot;&gt;<br>
  &lt;where field=&quot;cghth&quot; value=&quot;=全部,9=审批中,2=未审&quot; type=&quot;select&quot;&gt;<br>
  &lt;%--param：查询语句另外的参数标签。name：名称。datatype：数据类型--%&gt; <br>
  &lt;param name=&quot;fgsid&quot; datatype=&quot;number&quot;&gt;<br>
  &lt;/dataset&gt;<br>
  &lt;%--table:table标签。type：锁定表格的类型：dynamic(动态表格,打印多行数据),static(静态表格),summary(每页的总结表格)。dataset：需要用到的数据源名称(整个表格默认的数据源，若显示字段没有指定数据源，则用此数据源)。fillnull：动态表格没有数据是否以空行填满一页true/false，默认false。header：动态表格固定的标题行数。maxrow：动态表可打印的最大行数。offset：表格的偏移量，负数向上偏移。--%&gt;
  <br>
  &lt;table width=&quot;100%&quot; border=&quot;0&quot; cellspacing=&quot;0&quot;
  cellpadding=&quot;1&quot; type=&quot;static&quot;&gt;<br>
  &lt;tr&gt;<br>
  &lt;td align=&quot;center&quot; valign=&quot;middle&quot; border-left=&quot;0&quot;
  border-right=&quot;0&quot; border-top=&quot;0&quot; border-bottom=&quot;0&quot;
  font-size=&quot;12&quot; font-bold=&quot;1&quot;&gt;采购统计量&lt;/td&gt;<br>
  &lt;/tr&gt;<br>
  &lt;/table&gt;<br>
  &lt;table dataset=&quot;buy&quot; width=&quot;100%&quot; border=&quot;0&quot;
  cellspacing=&quot;0&quot; cellpadding=&quot;2&quot; type=&quot;dynamic&quot;
  fillnull=&quot;true&quot; header=&quot;1&quot;&gt;<br>
  &lt;tr&gt; <br>
  &lt;%--td:td标签。nowrap：是否不折行true/false，默认false。field：对应的字段名--%&gt; <br>
  &lt;td align=&quot;center&quot; valign=&quot;middle&quot; nowrap=&quot;true&quot;
  field=&quot;bh&quot;&gt;合同号&lt;/td&gt;<br>
  &lt;td align=&quot;center&quot; valign=&quot;middle&quot; nowrap=&quot;true&quot;
  field=&quot;wzpm&quot;&gt;品名&lt;/td&gt;<br>
  &lt;td align=&quot;center&quot; valign=&quot;middle&quot; nowrap=&quot;true&quot;
  field=&quot;wzgg&quot;&gt;规格&lt;/td&gt;<br>
  &lt;td align=&quot;center&quot; valign=&quot;middle&quot; nowrap=&quot;true&quot;
  field=&quot;zl&quot;&gt;重量&lt;/td&gt;<br>
  &lt;td align=&quot;center&quot; valign=&quot;middle&quot; nowrap=&quot;true&quot;
  field=&quot;hsdj&quot;&gt;单价&lt;/td&gt;<br>
  &lt;td align=&quot;center&quot; valign=&quot;middle&quot; nowrap=&quot;true&quot;
  field=&quot;je&quot;&gt;含税金额&lt;/td&gt;<br>
  &lt;td align=&quot;center&quot; valign=&quot;middle&quot; nowrap=&quot;true&quot;
  field=&quot;xm&quot;&gt;业务员&lt;/td&gt;<br>
  &lt;td align=&quot;center&quot; valign=&quot;middle&quot; nowrap=&quot;true&quot;
  field=&quot;dwmc&quot;&gt;供货单位&lt;/td&gt;<br>
  &lt;td align=&quot;center&quot; valign=&quot;middle&quot; nowrap=&quot;true&quot;
  field=&quot;mc&quot;&gt;部门&lt;/td&gt;<br>
  &lt;/tr&gt;<br>
  &lt;tr&gt; <br>
  &lt;%--value:引用字段值标签。dataset：制定的数据源。field：对应的字段名。format：数值的格式化。chineseformat：金额的中文大学格式。zeroformat：数值为0时的打印格式--%&gt;
  <br>
  &lt;td nowrap=&quot;true&quot;&gt;&lt;value field=&quot;bh&quot;&gt;&lt;/td&gt;<br>
  &lt;td nowrap=&quot;true&quot;&gt;&lt;value field=&quot;wzpm&quot;&gt;&lt;/td&gt;<br>
  &lt;td nowrap=&quot;true&quot;&gt;&lt;value field=&quot;wzgg&quot;&gt;&lt;/td&gt;<br>
  &lt;td nowrap=&quot;true&quot; align=&quot;right&quot;&gt;&lt;value field=&quot;zl&quot;
  format=&quot;#0.000&quot;&gt;&lt;/td&gt;<br>
  &lt;td nowrap=&quot;true&quot; align=&quot;right&quot;&gt;&lt;value field=&quot;hsdj&quot;
  format=&quot;#0.000&quot;&gt;&lt;/td&gt;<br>
  &lt;td nowrap=&quot;true&quot; align=&quot;right&quot;&gt;&lt;value field=&quot;je&quot;
  format=&quot;#0.00&quot;&gt;&lt;/td&gt;<br>
  &lt;td nowrap=&quot;true&quot; align=&quot;right&quot;&gt;&lt;value field=&quot;xm&quot;&gt;&lt;/td&gt;<br>
  &lt;td nowrap=&quot;true&quot;&gt;&lt;value field=&quot;dwmc&quot;&gt;&lt;/td&gt;<br>
  &lt;td nowrap=&quot;true&quot;&gt;&lt;value field=&quot;mc&quot;&gt;&lt;/td&gt;<br>
  &lt;/tr&gt;<br>
  &lt;%--widths：各个单元格对应的宽度的标签，必须在table内--%&gt; <br>
  &lt;widths value=&quot;11,18,11,7,6,7,11,18,12&quot;&gt; <br>
  &lt;/table&gt;<br>
  &lt;/body&gt;<br>
  &lt;/html&gt;<br>
  <br>
  <p>2.2编写套打文件（*.htm）</p>
  &lt;html&gt;<br> &lt;title name=&quot;存货库存单据&quot;/&gt;<br>
  &lt;page type=&quot;precision&quot; height=&quot;29.7&quot; width=&quot;21&quot;
  font-size=&quot;9&quot;/&gt;<br>
  &lt;%--坐标的原点是从左上角开始的。y:表示纵坐标,x:表示横坐标--,type的值(head:表头,body:表体,用于打印多条记录的,foot:表尾)--%&gt;<br>
  &lt;table type=&quot;head&quot; DataSet=&quot;headdata&quot;&gt;<br>
  &lt;tr&gt; <br>
  &lt;td y=&quot;1&quot; x=&quot;2&quot; align=&quot;center&quot; align=&quot;left&quot;
  field=&quot;cpbm&quot;&gt;&lt;/td&gt;<br>
  &lt;td y=&quot;1&quot; x=&quot;2&quot; align=&quot;center&quot; align=&quot;left&quot;
  field=&quot;pm&quot;&gt;&lt;/td&gt;<br>
  &lt;td y=&quot;1&quot; x=&quot;2&quot; align=&quot;center&quot; align=&quot;left&quot;
  field=&quot;gg&quot;&gt;&lt;/td&gt;<br>
  &lt;td y=&quot;1&quot; x=&quot;2&quot; align=&quot;center&quot; align=&quot;left&quot;
  field=&quot;jldw&quot;&gt;&lt;/td&gt;<br>
  &lt;td y=&quot;1&quot; x=&quot;2&quot; align=&quot;center&quot; align=&quot;left&quot;
  field=&quot;hsdw&quot;&gt;&lt;/td&gt;<br>
  &lt;td y=&quot;1&quot; x=&quot;2&quot; align=&quot;center&quot; align=&quot;left&quot;
  field=&quot;kcsl&quot; multirow=&quot;true&quot;&gt;&lt;/td&gt;<br>
  &lt;/tr&gt;<br>
  &lt;/table&gt; <br>
  &lt;%--space:行间隙。y,x表体开始的坐标, 每个打印的格子多需要设置横坐标。nowrap:超过最大长度是否自动折行(只对表体有效)。multirow:是否可打印多行。nowrap属性和multirow属性是不能同是共存的--%&gt;<br>
  &lt;table type=&quot;body&quot; DataSet=&quot;bodydata&quot; y=&quot;1&quot; x=&quot;2&quot;
  space=&quot;0.5&quot;&gt;<br>
  &lt;tr&gt; <br>
  &lt;td x=&quot;3&quot; align=&quot;center&quot; align=&quot;left&quot; field=&quot;cpbm&quot;
  font-size=&quot;9&quot; format=&quot;#.00&quot; maxlength=&quot;10&quot; nowrap=&quot;true&quot;&gt;&lt;/td&gt;<br>
  &lt;td x=&quot;4.5&quot; align=&quot;center&quot; align=&quot;left&quot; field=&quot;pm&quot;&gt;&lt;/td&gt;<br>
  &lt;td x=&quot;6&quot; align=&quot;center&quot; align=&quot;left&quot; field=&quot;gg&quot;&gt;&lt;/td&gt;<br>
  &lt;td x=&quot;8&quot; align=&quot;center&quot; align=&quot;left&quot; field=&quot;jldw&quot;&gt;&lt;/td&gt;<br>
  &lt;td x=&quot;10&quot; align=&quot;center&quot; align=&quot;left&quot; field=&quot;hsdw&quot;&gt;&lt;/td&gt;<br>
  &lt;td x=&quot;13&quot; align=&quot;center&quot; align=&quot;left&quot; field=&quot;kcsl&quot;
  multirow=&quot;true&quot; title=&quot;备注&quot;&gt;&lt;/td&gt;<br>
  &lt;/tr&gt;<br>
  &lt;/table&gt;<br>
  &lt;table type=&quot;foot&quot; DataSet=&quot;footdata&quot;&gt;<br>
  &lt;tr&gt; <br>
  &lt;td y=&quot;1&quot; x=&quot;2&quot; align=&quot;center&quot; align=&quot;left&quot;
  field=&quot;cpbm&quot; multirow=&quot;true&quot;&gt;&lt;/td&gt;<br>
  &lt;td y=&quot;1&quot; x=&quot;2&quot; align=&quot;center&quot; align=&quot;left&quot;
  field=&quot;pm&quot;&gt;&lt;/td&gt;<br>
  &lt;td y=&quot;1&quot; x=&quot;2&quot; align=&quot;center&quot; align=&quot;left&quot;
  field=&quot;gg&quot;&gt;&lt;/td&gt;<br>
  &lt;td y=&quot;1&quot; x=&quot;2&quot; align=&quot;center&quot; align=&quot;left&quot;
  field=&quot;jldw&quot;&gt;&lt;/td&gt;<br>
  &lt;td y=&quot;1&quot; x=&quot;2&quot; align=&quot;center&quot; align=&quot;left&quot;
  field=&quot;hsdw&quot;&gt;&lt;/td&gt;<br>
  &lt;td y=&quot;1&quot; x=&quot;2&quot; align=&quot;center&quot; align=&quot;left&quot;
  field=&quot;kcsl&quot;&gt;&lt;/td&gt;<br>
  &lt;/tr&gt;<br>
  &lt;/table&gt;<br>
  &lt;/html&gt;<br>
  3.自定义的标签 见javadoc</p>
  4.报表监听器的实现：<br>
  5.
 * a.创建文件：模板名称.properties<br>
 * b.文件内容:TempletProvideListener=engine.xxx.xxx<br>
 * TempletLoadListener=engine.xxx.xxx<br>
 * ReportDataLoadingListener=engine.xxx.xxx<br>
 * ReportDataLoadedListener=engine.xxx.xxx<br>
 * Copyright:    Copyright (c) 2001
 * Company:      ENGINE
 * @author hukn
 * @version 1.0
 */

public interface Tag extends HtmlTag
{
}
