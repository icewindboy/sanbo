<%--�ɹ���ⵥ�ӱ�--%>
<%
/**
 * 2004-3-31 14:25 �޸� �̵������δ����滻Ϊ�������һ��include����һ���ļ�
 * 2004-3-30 15:27 �޸� javascript�޸�ҳ���Ͽ���������ֵ������,����������λ,
 *    ����,��������������ϵ��������������Ч:���޸����е�һ�������һ���ǿյĻ������ı�
 * 2004-3-30 15:44 ���� �����������������ʹ�����˸������Լ���ͬ���Ŷ����js���� yjg
 * 2004-3-27 11:32 ���� ����ɾ������Ϊ���еİ�ť yjg
 * 03.24 15:59 �޸� (js)����ŵ��ݲ���Ҫ�ֶ������������ڴ˴�д��Ϊ�����ֶ�. yjg
 * 03:23 21:59 ��Ϊ�ɹ���ⵥ�������ֶ�����.�����ڴ˴�д�������ֶ�����. yjg
 * 03.19 20:32 ���� ����Ϊ���㲻�˳���ҳ��Ϳ���ֱ�Ӵ�ӡxxx_top.jsp���г��ĵ��ݶ��ӵ���һ��,��һ�ʰ�ť yjg
 * 03:18 15:59 ������һ�����㵥λtd.���,table�е������td��λ��Ҳ������Ӧ�ĵ���. yjg
 * 03.18 10:00 -- 03.18 14:28 ����һ�п�ʼ, ��������⼸��buttont�ϼ�������Ӧ�Ŀ�ݼ�.��:<pc:.../>�������ǩ.ͬʱ,button��onclick�¼�ΪbuttonEventX().yjg
 * 03.18 10:42 ���� �����￪ʼ����������buttonEventX()����,��Ҫ���������ʱ�����ڸ���ҳ��ӿ�ݼ�.��ԭ����button��onclick�¼���Ĵ���ŵ��˴�����. yjg
 * 03.08 21:4  �޸�  ��Ϊ�ϼ���ʾ���� ���� ��������  ��� �ϼ��ֶ������е� ��������Ӧ�ֶδ�λ��.���ڸĺ���ʾ��λ��������� yjg
 * 03.08 21:14 ���� �����رհ�ť�ṩ������ҳ���Ǳ��������ʱʹ��. yjg
 * 03.06 15:40 �޸� UI���޸�. ��������ֶηŵ�Ʒ�� ����.����Ҳ���˵���. yjg
 * 03.06 21:30 �޸� ������˴�ԭ���Ĵ����if����:if ( isHandwork.valu == "1")
 *                  ��Ϊ ���ڵ� if (isHandword == "1")
 * 03.05 20:40 �޸� ע�͵����´���.��Ϊ,���汻ע�͵��Ĵ������ɻ�������������һ��ı�. yjg
 * 03.05 16:54 ���� �������ڹ������ѡ���js���� yjg
 * 03.02 21:26 ���� ������ӡ���ݰ�ť�������Ųɹ���ⵥҳ���ϵ����ݴ�ӡ����. yjg
 * 02.23 11:46 ���� ������ʾ�����⼸����ť�������м���isReport���� yjg
 * 02.18 15:47 �޸� ����Ӧ�� ѡ��ĳ� ��������ı���. ͬʱ������Ӧ�ڴ��ı����ڻس��Զ���������ģ����ѯ���ڵ��¼� yjg
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
