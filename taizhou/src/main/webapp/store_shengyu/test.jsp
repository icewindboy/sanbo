<%--采购入库单从表--%>
<%
/**
 * 2004-3-31 14:25 修改 盘点机的这段代码替换为下面的那一句include另外一个文件
 * 2004-3-30 15:27 修改 javascript修改页面上可以输入数值的数量,换算数量栏位,
 *    数量,换算数量互动关系仅在如此情况下有效:当修改其中的一个如果另一个是空的话则跟随改变
 * 2004-3-30 15:44 新增 给部门下拉表加上能使经手人跟随它自己不同部门而变的js函数 yjg
 * 2004-3-27 11:32 新增 新增删除数量为空行的按钮 yjg
 * 03.24 15:59 修改 (js)因此张单据不需要手动操作了所以在此处写死为不能手动. yjg
 * 03:23 21:59 因为采购入库单不允许手动输入.所以在此处写死不许手动输入. yjg
 * 03.19 20:32 新增 新增为方便不退出此页面就可以直接打印xxx_top.jsp上列出的单据而加的上一笔,下一笔按钮 yjg
 * 03:18 15:59 新增了一个换算单位td.因此,table中的另外的td的位置也做了相应的调整. yjg
 * 03.18 10:00 -- 03.18 14:28 从这一行开始, 给下面的这几个buttont上加上了相应的快捷键.即:<pc:.../>的这个标签.同时,button的onclick事件为buttonEventX().yjg
 * 03.18 10:42 新增 从这里开始下面新增的buttonEventX()函数,主要是现在这个时候正在给网页添加快捷键.把原来在button的onclick事件里的代码放到此处来了. yjg
 * 03.08 21:4  修改  因为合计显示出来 数量 换算数量  金额 合计字段与表格中的 这三个相应字段错位了.现在改好显示错位的这个问题 yjg
 * 03.08 21:14 新增 新增关闭按钮提供给当此页面是被报表调用时使用. yjg
 * 03.06 15:40 修改 UI的修改. 规格属性字段放到品名 规格后.其它也做了调整. yjg
 * 03.06 21:30 修改 将下面此处原来的错误的if条件:if ( isHandwork.valu == "1")
 *                  改为 现在的 if (isHandword == "1")
 * 03.05 20:40 修改 注释掉如下代码.因为,下面被注释掉的代码会造成换算数量的数量一起改变. yjg
 * 03.05 16:54 新增 新增用于规格属性选择的js函数 yjg
 * 03.02 21:26 新增 新增打印单据按钮来把这张采购入库单页面上的内容打印出来. yjg
 * 02.23 11:46 新增 新增显示下面这几个按钮的条件中加上isReport条件 yjg
 * 02.18 15:47 修改 将供应商 选择改成 可输入的文本框. 同时加上响应在此文本框内回车自动弹出进行模糊查询窗口的事件 yjg
 */
%>
<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.store.shengyu.B_StoreBill storeBillBean = engine.erp.store.shengyu.B_StoreBill.getInstance(request);
  storeBillBean.djxz = 1;
  String pageCode = "contract_instore_list";
  //boolean hasApproveLimit = isApprove && loginBean.hasLimits(pageCode, op_approve);
%>
<%String retu = storeBillBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp firstkindBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_KIND);
  %>


                  <pc:select name="wzlbid" style="width:160">
                   <%=firstkindBean.getList()%>
                   </pc:select>
