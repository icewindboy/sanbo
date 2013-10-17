<%@ page contentType="text/html; charset=UTF-8" %><%@ page import="engine.common.UserFacade"%><%@ include file="init.jsp"%>
<html>
<head>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK rel="stylesheet" href="<%=request.getContextPath() %>/scripts/public.css" type="text/css">
<STYLE type="text/css">
.botton_out {
    border-top:1 solid #ffffff; border-left:1 solid #ffffff;
    border-bottom:1 solid #000000; border-right:1 solid #000000;
}
.botton_in {
    border-top:1 solid #000000; border-left:1 solid #000000;
    border-bottom:1 solid #ffffff; border-right:1 solid #ffffff;
}
.botton_normal {
    border:1 solid #BCD7F4;
}
</STYLE>
</head>

<body oncontextmenu="return true;" onload="bodyLoad();" text="#000000" style="margin-left: 0px; margin-top: 0px; margin-right: 0px; margin-bottom: 0px;" scroll=no>
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
<tr><td height="64">
  <TABLE width="100%" height="45" border=0 cellPadding=0 cellSpacing=0 bgColor="#0d73bd">
    <TR>
      <TD width=269><IMG height=45 src="<%=request.getContextPath() %>/images/pic_top_01.gif" width=269 ondblclick="window.open('')"></TD>
    <TD align=left>&nbsp;</TD>
        <TD align=right width=30>&nbsp;</TD>
      </TR>
  </TABLE>
  <TABLE width="100%" height="19" border="0" cellspacing="0" cellpadding="0" style="border-bottom: 1px solid #104a7b">
    <TR valign="middle">
      <TD width="*" class="toolbar">【<%="会员:"+loginBean.getUserName()+"&nbsp;"%>&nbsp;日期：<%=loginBean.getAccountDate()%>&nbsp;】</TD>
      <TD width="181" align="right" class="toolbar">
        
      </TD>
    </TR>
  </TABLE>
</td></tr>
<tr><td height="100%">
  <%--iframe id="public_left_menu" src="left.jsp" style="display:block; position:absolute; z-index:2; top:66; left:-145; width:160; height:500" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe--%>
  <table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td width="15" nowrap>
      

<DIV id='menulayer0Div' class='menulayer'>
<DIV id='barlayer0Div' class='barlayer'>
<TABLE border='0' cellPadding='0' cellSpacing='0' height='22' onclick='menubarpush(0)' width='100%'><tr><td nowrap class='menuTabBar'>&nbsp;人 力 资 源&nbsp;</td></tr></TABLE></DIV>
<DIV id='iconlayer0Div' class='iconlayer'>
<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('281','../person/credit_card.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;银行信用卡</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('282','../person/employee_cardno.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;员工信用卡管理</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('283','../person/employee_info.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;员工档案卡管理</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('421','../person/employee_change.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;员工信息变动</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('761','../person/wage_field_info.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;工资款项设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('762','../person/wage_field_expression.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;款项计算公式设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('801','../person/wage_input.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;工资录入</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1242','../person/invite_job.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;招聘计划</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1281','../person/apply_job.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;应聘信息</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1282','../person/train_plan.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;培训计划</td></tr>
<tr><td nowrap class=treeMenuItem><hr class='menuSperator'></td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1062','../pub/pdf.jsp?code=rl_off_person')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;离职员工一览表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1121','../pub/pdf.jsp?code=rl_change_person')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;职工变动情况表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1122','../pub/pdf.jsp?code=rl_train_person')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;职工培训情况明细表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1123','../pub/pdf.jsp?code=rl_award_person')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;职工奖惩情况明细表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1124','../pub/pdf.jsp?code=rl_insurance_person')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;职工保险情况明细表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1125','../pub/pdf.jsp?code=rl_other_person')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;职工其他情况明细表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1525','../pub/pdf.jsp?code=rl_employee_wage')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;员工月工资明细表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1545','../pub/pdf.jsp?code=rl_deformity_person')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;职工残疾情况明细表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1185','../pub/pdf.jsp?code=rl_employee_person')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;职员档案卡明细</td></tr>
</table></DIV>
<DIV id='uplayer0Div' class='uplayer'><IMG height=16 width=16 src='../images/scrollup.gif' onmousedown="this.src='../images/scrollup2.gif';menuscrollup()" onmouseout="this.src='../images/scrollup.gif';menuscrollstop()" onmouseup="this.src='../images/scrollup.gif';menuscrollstop()"></DIV>
<DIV id='downlayer0Div' class='downlayer'><IMG height=16 width=16 src='../images/scrolldown.gif' onmousedown="this.src='../images/scrolldown2.gif';menuscrolldown()" onmouseout="this.src='../images/scrolldown.gif';menuscrollstop()" onmouseup="this.src='../images/scrolldown.gif';menuscrollstop()"></DIV>
</DIV>
<DIV id='menulayer1Div' class='menulayer'>
<DIV id='barlayer1Div' class='barlayer'>
<TABLE border='0' cellPadding='0' cellSpacing='0' height='22' onclick='menubarpush(1)' width='100%'><tr><td nowrap class='menuTabBar'>&nbsp;采 购 管 理&nbsp;</td></tr></TABLE></DIV>
<DIV id='iconlayer1Div' class='iconlayer'>
<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('321','../buy/buyprice.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;采购报价</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('361','../buy/buy_apply.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;采购申请</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('25','../buy/buy_order.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;采购合同</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('223','../buy/buy_ordergoods.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;采购货物管理</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1601','../buy/buy_ordergoodsinit.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;采购货物初始化</td></tr>
<tr><td nowrap class=treeMenuItem><hr class='menuSperator'></td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('881','../pub/pdf.jsp?code=buy_perform')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;采购合同汇总表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('300','../pub/pdf.jsp?code=buy_order_detail')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;合同执行情况表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('883','../pub/pdf.jsp?code=buy_jhd_detail')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;采购进货单明细帐</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('884','../pub/pdf.jsp?code=buy_fp_detail')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;采购发票明细帐</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('901','../pub/pdf.jsp?code=buy_collect')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;采购汇总报表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('985','../pub/pdf.jsp?code=buy_tax')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;采购进项税统计表</td></tr>
</table></DIV>
<DIV id='uplayer1Div' class='uplayer'><IMG height=16 width=16 src='../images/scrollup.gif' onmousedown="this.src='../images/scrollup2.gif';menuscrollup()" onmouseout="this.src='../images/scrollup.gif';menuscrollstop()" onmouseup="this.src='../images/scrollup.gif';menuscrollstop()"></DIV>
<DIV id='downlayer1Div' class='downlayer'><IMG height=16 width=16 src='../images/scrolldown.gif' onmousedown="this.src='../images/scrolldown2.gif';menuscrolldown()" onmouseout="this.src='../images/scrolldown.gif';menuscrollstop()" onmouseup="this.src='../images/scrolldown.gif';menuscrollstop()"></DIV>
</DIV>
<DIV id='menulayer2Div' class='menulayer'>
<DIV id='barlayer2Div' class='barlayer'>
<TABLE border='0' cellPadding='0' cellSpacing='0' height='22' onclick='menubarpush(2)' width='100%'><tr><td nowrap class='menuTabBar'>&nbsp;销 售 管 理&nbsp;</td></tr></TABLE></DIV>
<DIV id='iconlayer2Div' class='iconlayer'>
<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('20','../sale_dafa/product_price.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;产品销售定价</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1321','../sale/comePrice.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;来料加工产品定价</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1625','../sale/areaprice.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;驾驶员行驶地区价格</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('21','../sale_dafa/sale_order_list.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销售合同</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('23','../sale_dafa/lading_bill.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销售货物管理</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('22','../sale/customer_credit_list.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;客户信誉额度</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('299','../sale/customer_product_discount.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;客户产品折扣</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1645','../sale/transport_list.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销售运单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('741','../sale_dafa/prix_formula.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;业务员奖金计算公式</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1081','../sale/saler_prix_set.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;业务员奖金设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1101','../sale_dafa/saler_prix.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;业务员奖金</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1342','../sale/areacar.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;行驶地区车辆信息</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1465','../sale/saler_handover.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;业务员移交</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1501','../sale/lading_init.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;提单初始化</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1381','../sale/drivername.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;驾驶员信息设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('2709','../sale_dafa/saler_prize.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;新业务员奖金</td></tr>
<tr><td nowrap class=treeMenuItem><hr class='menuSperator'></td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1443','../pub/pdf.jsp?code=dafa_xs_ht_collect')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销售合同汇总</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('841','../pub/pdf.jsp?code=dafa_ht_perform')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销售合同执行情况</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('842','../pub/pdf.jsp?code=dafa_xs_td_detail')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销售提单明细</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('843','../pub/pdf.jsp?code=dafa_xs_td_collect')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销售提单汇总</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('845','../pub/pdf.jsp?code=dafa_xs_cust_bill')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;单位销售流水账</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('846','../pub/pdf.jsp?code=dafa_saler_result')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;业务员销售业绩(提单)</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('847','../pub/pdf.jsp?code=xs_dept_stat')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;部门销售统计</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('848','../pub/pdf.jsp?code=xs_favour_stat')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销售优惠统计</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('849','../pub/pdf.jsp?code=dafa_prepay_stat')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;代垫费用统计</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('850','../pub/pdf.jsp?code=xs_tax_stat')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销项税统计</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('851','../pub/pdf.jsp?code=dafa_xs_prod_send')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;货物发出明细账</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('852','../pub/pdf.jsp?code=dafa_xs_day_income')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;收入日报表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('856','../pub/pdf.jsp?code=xs_saler_receivable')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;业务员应收账款统计</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('857','../pub/pdf.jsp?code=xs_saler_prize')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;业务员销售奖金</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('882','../pub/pdf.jsp?code=xs_fp_detail')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销售发票明细</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('903','../pub/pdf.jsp?code=xs_fp_collect')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销售发票汇总</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1541','../pub/pdf.jsp?code=xs_ret_money')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销售资金回笼表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1581','../pub/pdf.jsp?code=xs_ret_acmoney')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销售数量金额表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1582','../pub/pdf.jsp?code=xs_receive_acpm')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;月销售统计</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1585','../pub/pdf.jsp?code=sale_balance')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销售结算报表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1665','../pub/pdf.jsp?code=xs_driverwage')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;驾驶员工资表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1685','../pub/pdf.jsp?code=xs_fundReturnRatio')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;资金回笼期</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1686','../pub/pdf.jsp?code=xs_customer_xyed')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;客户信誉报表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1362','../pub/pdf.jsp?code=xs_personFee')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;业务员运费表</td></tr>
</table></DIV>
<DIV id='uplayer2Div' class='uplayer'><IMG height=16 width=16 src='../images/scrollup.gif' onmousedown="this.src='../images/scrollup2.gif';menuscrollup()" onmouseout="this.src='../images/scrollup.gif';menuscrollstop()" onmouseup="this.src='../images/scrollup.gif';menuscrollstop()"></DIV>
<DIV id='downlayer2Div' class='downlayer'><IMG height=16 width=16 src='../images/scrolldown.gif' onmousedown="this.src='../images/scrolldown2.gif';menuscrolldown()" onmouseout="this.src='../images/scrolldown.gif';menuscrollstop()" onmouseup="this.src='../images/scrolldown.gif';menuscrollstop()"></DIV>
</DIV>
<DIV id='menulayer3Div' class='menulayer'>
<DIV id='barlayer3Div' class='barlayer'>
<TABLE border='0' cellPadding='0' cellSpacing='0' height='22' onclick='menubarpush(3)' width='100%'><tr><td nowrap class='menuTabBar'>&nbsp;库 存 管 理&nbsp;</td></tr></TABLE></DIV>
<DIV id='iconlayer3Div' class='iconlayer'>
<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('225','../store/contract_instore_list.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;采购入库单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1163','../store/other_instore_list.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;其它入库单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('226','../store/outputlist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销售出库单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1607','../store/other_outstore_list.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;其它出库单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('484','../store/process_issue_list.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;外加工发料单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('488','../store/process_instore_list.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;外加工入库单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('227','../store/receive_material_list.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;生产领料单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1626','../store/newself_gain_list.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;自制收货单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('489','../store/move_store_list.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;移库单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('490','../store/report_destroy_list.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;损溢单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1063','../store/store_check.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;库存盘点单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('701','../store/produce_use.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;单据用途设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('702','../store/store_bill_kind.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;收发单据类别</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1141','../store/year_close.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;月末结账</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('2729','../store/package_list.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;包装方案设置</td></tr>
<tr><td nowrap class=treeMenuItem><hr class='menuSperator'></td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('823','../pub/pdf.jsp?code=kc_stocks')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;存货库存量表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1184','../pub/pdf.jsp?code=kc_stocks_prod_detail')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;存货明细表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('824','../pub/pdf.jsp?code=kc_stocks_detail')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;存货收发明细帐</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('825','../pub/pdf.jsp?code=kc_inout_collect')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;存货收发汇总表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('860','../pub/pdf.jsp?code=kc_inout_day')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;存货收发日报表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1341','../pub/pdf.jsp?code=kc_assort_collect')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;收发类别分类汇总</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('861','../pub/pdf.jsp?code=kc_store_stocks')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;存货分库结存表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('862','../pub/pdf.jsp?code=kc_bill_inout_detail')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;库存收发明细查询</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('863','../pub/pdf.jsp?code=kc_bill_inout_collect')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;库存收发汇总查询</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('864','../pub/pdf.jsp?code=kc_batchno_inout_collect')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;存货批号收发结存表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('866','../pub/pdf.jsp?code=kc_stocks_cost_diff')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;存货成本差异表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('2731','../pub/pdf.jsp?code=kc_package')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;包装用料情况表</td></tr>
</table></DIV>
<DIV id='uplayer3Div' class='uplayer'><IMG height=16 width=16 src='../images/scrollup.gif' onmousedown="this.src='../images/scrollup2.gif';menuscrollup()" onmouseout="this.src='../images/scrollup.gif';menuscrollstop()" onmouseup="this.src='../images/scrollup.gif';menuscrollstop()"></DIV>
<DIV id='downlayer3Div' class='downlayer'><IMG height=16 width=16 src='../images/scrolldown.gif' onmousedown="this.src='../images/scrolldown2.gif';menuscrolldown()" onmouseout="this.src='../images/scrolldown.gif';menuscrollstop()" onmouseup="this.src='../images/scrolldown.gif';menuscrollstop()"></DIV>
</DIV>
<DIV id='menulayer4Div' class='menulayer'>
<DIV id='barlayer4Div' class='barlayer'>
<TABLE border='0' cellPadding='0' cellSpacing='0' height='22' onclick='menubarpush(4)' width='100%'><tr><td nowrap class='menuTabBar'>&nbsp;财 务 管 理&nbsp;</td></tr></TABLE></DIV>
<DIV id='iconlayer4Div' class='iconlayer'>
<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('16','../baseinfo/fplblist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;发票类别维护</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('12','../baseinfo/banklist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;银行信息</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('13','../baseinfo/bankaccountlist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;帐号信息</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1194','../pub/pdf.jsp?code=cw_kc_outin_money')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;原料月收发金额汇总</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1463','../finance/advance_payment.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;采购预付款</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('28','../finance/buy_invoice.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;采购发票</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('581','../finance/buy_balance.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;采购结算</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1464','../finance/out_process_balance.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;外加工结算</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1182','../buy/payable_account.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;应付款初始化</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1021','../pub/pdf.jsp?code=buy_receivable_detail')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;应付帐款明细表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1041','../pub/pdf.jsp?code=buy_receivable_collect')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;应付帐款汇总表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1183','../sale/receivable_account.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;应收款初始化</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('29','../finance/sale_invoice.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销售发票</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('621','../finance_dafa/sale_balance.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;销售结算</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('853','../pub/pdf.jsp?code=dafa_receivable_detail')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;应收帐款明细</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('854','../pub/pdf.jsp?code=dafa_receivable_balance')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;应收帐款余额</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('855','../pub/pdf.jsp?code=dafa_receivable_collect')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;应收帐款分类汇总</td></tr>
</table></DIV>
<DIV id='uplayer4Div' class='uplayer'><IMG height=16 width=16 src='../images/scrollup.gif' onmousedown="this.src='../images/scrollup2.gif';menuscrollup()" onmouseout="this.src='../images/scrollup.gif';menuscrollstop()" onmouseup="this.src='../images/scrollup.gif';menuscrollstop()"></DIV>
<DIV id='downlayer4Div' class='downlayer'><IMG height=16 width=16 src='../images/scrolldown.gif' onmousedown="this.src='../images/scrolldown2.gif';menuscrolldown()" onmouseout="this.src='../images/scrolldown.gif';menuscrollstop()" onmouseup="this.src='../images/scrolldown.gif';menuscrollstop()"></DIV>
</DIV>
<DIV id='menulayer5Div' class='menulayer'>
<DIV id='barlayer5Div' class='barlayer'>
<TABLE border='0' cellPadding='0' cellSpacing='0' height='22' onclick='menubarpush(5)' width='100%'><tr><td nowrap class='menuTabBar'>&nbsp;生 产 管 理&nbsp;</td></tr></TABLE></DIV>
<DIV id='iconlayer5Div' class='iconlayer'>
<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('232','../produce/work_procedure.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;工序分段设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('233','../produce/technics_name.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;工序名称设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('362','../produce/technics_route_type.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;工艺路线类型</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('363','../produce/work_center.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;工作中心设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('364','../produce/technics_route.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;工艺路线设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('381','../produce/work_group.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;工作组设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('401','../produce/process_price.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;外加工价格</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1706','../produce/sc_track_type.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;生产跟踪单类型</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('441','../produce/bom.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;BOM表结构图</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('442','../produce/bom_batch.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;BOM表批量修改</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('681','../produce/bom_replace_list.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;物料可替换件设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('521','../produce/produce_plan.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;生产计划维护</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('541','../produce/mrp.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;物料需求维护</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('565','../produce/produce_task.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;生产任务维护</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('582','../produce/produce_process.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;生产加工单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1561','../produce/produce_outprocess.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;委外加工单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('641','../produce/wage_formula.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;工人工资公式设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1641','../produce/personal_worker_wage.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;工人工资</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('601','../produce/workload_worker.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;工人工作量</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('661','../produce/workload_group.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;按工作组输工作量</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('721','../produce/workload_dept.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;按部门输工作量</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('763','../produce/work_wage.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;工人工资汇总</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1221','../produce/sc_track_bill.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;生产跟踪卡</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1263','../produce/pridratio.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;成品率物资</td></tr>
<tr><td nowrap class=treeMenuItem><hr class='menuSperator'></td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('941','../pub/pdf.jsp?code=sc_bom_detail')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;配料单明细表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('961','../pub/pdf.jsp?code=sc_technics_route')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;产品工艺明细</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('962','../pub/pdf.jsp?code=sc_mrp_detail')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;物料需求明细表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('981','../pub/pdf.jsp?code=sc_plan_stat')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;生产计划完成情况表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1181','../pub/pdf.jsp?code=sc_shop_prod_stat')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;车间产品完成情况表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('983','../pub/pdf.jsp?code=sc_shop_task_stat')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;生产任务汇总表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('922','../pub/pdf.jsp?code=sc_shop_output')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;产量统计表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('984','../pub/pdf.jsp?code=sc_shop_day_produce')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;车间生产日报表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1001','../pub/pdf.jsp?code=sc_worker_workerload')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;工人工作量统计表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1201','../pub/pdf.jsp?code=sc_shop_wage_collect')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;车间工资汇总表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('986','../pub/pdf.jsp?code=sc_worker_wage')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;工人计件工资统计表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1162','../pub/pdf.jsp?code=sc_prod_cost')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;产品生产成本计算表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1283','../pub/pdf.jsp?code=sc_prodratio')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;成品率报表</td></tr>
</table></DIV>
<DIV id='uplayer5Div' class='uplayer'><IMG height=16 width=16 src='../images/scrollup.gif' onmousedown="this.src='../images/scrollup2.gif';menuscrollup()" onmouseout="this.src='../images/scrollup.gif';menuscrollstop()" onmouseup="this.src='../images/scrollup.gif';menuscrollstop()"></DIV>
<DIV id='downlayer5Div' class='downlayer'><IMG height=16 width=16 src='../images/scrolldown.gif' onmousedown="this.src='../images/scrolldown2.gif';menuscrolldown()" onmouseout="this.src='../images/scrolldown.gif';menuscrollstop()" onmouseup="this.src='../images/scrolldown.gif';menuscrollstop()"></DIV>
</DIV>
<DIV id='menulayer6Div' class='menulayer'>
<DIV id='barlayer6Div' class='barlayer'>
<TABLE border='0' cellPadding='0' cellSpacing='0' height='22' onclick='menubarpush(6)' width='100%'><tr><td nowrap class='menuTabBar'>&nbsp;基 础 管 理&nbsp;</td></tr></TABLE></DIV>
<DIV id='iconlayer6Div' class='iconlayer'>
<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('2','../baseinfo/deptlist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;部门信息</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('3','../baseinfo/wordbooklist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;员工辅助信息</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('5','../baseinfo/countrylist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;国家信息</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('6','../baseinfo/arealist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;地区信息</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('7','../baseinfo/corplist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;往来单位信息</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('10','../baseinfo/balancemodelist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;结算方式</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('11','../baseinfo/foreignCurrencyList.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;外币信息</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('35','../baseinfo/productinfoset.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;物资信息</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('187','../baseinfo/product_kind.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;物资类别设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('8','../baseinfo/storelist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;仓库信息</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('301','../baseinfo/storeplacelist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;库位信息</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('826','../baseinfo/stock_kind.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;存货类别</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1361','../baseinfo/ems_print.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;邮政特快专递</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1405','../baseinfo/send_mode.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;发货方式设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1442','../baseinfo/order_type.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;合同类型设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('2730','../baseinfo/tdfoundset.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;包装类别设置</td></tr>
<tr><td nowrap class=treeMenuItem><hr class='menuSperator'></td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1566','../pub/pdf.jsp?code=jc_units_imformation')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;往来单位信息报表</td></tr>
</table></DIV>
<DIV id='uplayer6Div' class='uplayer'><IMG height=16 width=16 src='../images/scrollup.gif' onmousedown="this.src='../images/scrollup2.gif';menuscrollup()" onmouseout="this.src='../images/scrollup.gif';menuscrollstop()" onmouseup="this.src='../images/scrollup.gif';menuscrollstop()"></DIV>
<DIV id='downlayer6Div' class='downlayer'><IMG height=16 width=16 src='../images/scrolldown.gif' onmousedown="this.src='../images/scrolldown2.gif';menuscrolldown()" onmouseout="this.src='../images/scrolldown.gif';menuscrollstop()" onmouseup="this.src='../images/scrolldown.gif';menuscrollstop()"></DIV>
</DIV>
<DIV id='menulayer7Div' class='menulayer'>
<DIV id='barlayer7Div' class='barlayer'>
<TABLE border='0' cellPadding='0' cellSpacing='0' height='22' onclick='menubarpush(7)' width='100%'><tr><td nowrap class='menuTabBar'>&nbsp;系 统 管 理&nbsp;</td></tr></TABLE></DIV>
<DIV id='iconlayer7Div' class='iconlayer'>
<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('19','../baseinfo/systemParamlist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;系统参数维护</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('15','../baseinfo/rolelist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;角色管理</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('17','../baseinfo/personlist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;员工权限管理</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('239','../baseinfo/nodepriviligelist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;权限代码维护</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('240','../baseinfo/nodeinfolist.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;界面信息维护</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('561','../baseinfo/approve_define.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;审批流程定义</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('304','../baseinfo/coderule.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;编码规则</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1468','../baseinfo/system_log.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;系统日志</td></tr>
</table></DIV>
<DIV id='uplayer7Div' class='uplayer'><IMG height=16 width=16 src='../images/scrollup.gif' onmousedown="this.src='../images/scrollup2.gif';menuscrollup()" onmouseout="this.src='../images/scrollup.gif';menuscrollstop()" onmouseup="this.src='../images/scrollup.gif';menuscrollstop()"></DIV>
<DIV id='downlayer7Div' class='downlayer'><IMG height=16 width=16 src='../images/scrolldown.gif' onmousedown="this.src='../images/scrolldown2.gif';menuscrolldown()" onmouseout="this.src='../images/scrolldown.gif';menuscrollstop()" onmouseup="this.src='../images/scrolldown.gif';menuscrollstop()"></DIV>
</DIV>
<DIV id='menulayer8Div' class='menulayer'>
<DIV id='barlayer8Div' class='barlayer'>
<TABLE border='0' cellPadding='0' cellSpacing='0' height='22' onclick='menubarpush(8)' width='100%'><tr><td nowrap class='menuTabBar'>&nbsp;质 量 管 理&nbsp;</td></tr></TABLE></DIV>
<DIV id='iconlayer8Div' class='iconlayer'>
<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1301','../quality/certified_card.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;产品合格证</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1262','../quality/verify_method.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;检验方法设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1302','../quality/quality_grade.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;质量等级设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1303','../quality/bug_grade.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;缺陷等级设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1304','../quality/quality_reason.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;质量原因设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1305','../quality/check_standard.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;检验标准设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1306','../quality/tool_type.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;检验器具分类</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1307','../quality/tool_info.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;检验器具信息</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1432','../quality/buy_check.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;采购入库检验</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1441','../quality/certified_card_set.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;合格证打印设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1202','../quality/check_type.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;检验类型设置</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1462','../quality/wrapper_check.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;原纸进货检验报告单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1222','../quality/film_check.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;原膜进货检验报告单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1707','../quality/adminicle_check.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;辅料验收报告单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('2708','../quality/chemical_material.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;化工原料检验报告单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1708','../quality/product_wrapper_check.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;成品纸检验报告单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1709','../quality/product_film_check.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;成品膜检验报告单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1710','../quality/newprovider_check.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;新供方产品质量评定表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1711','../quality/wrapper_process_check.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;镀铝纸生产过程检验</td></tr>
<tr><td nowrap class=treeMenuItem><hr class='menuSperator'></td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1712','../pub/pdf.jsp?code=buy_check_count_report')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;供应商合格率统计表</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1713','../pub/pdf.jsp?code=reject_report_film')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;镀铝膜不合格品余额累计</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1714','../pub/pdf.jsp?code=reject_report_wrapper')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;镀铝纸不合格品余额累计</td></tr>
</table></DIV>
<DIV id='uplayer8Div' class='uplayer'><IMG height=16 width=16 src='../images/scrollup.gif' onmousedown="this.src='../images/scrollup2.gif';menuscrollup()" onmouseout="this.src='../images/scrollup.gif';menuscrollstop()" onmouseup="this.src='../images/scrollup.gif';menuscrollstop()"></DIV>
<DIV id='downlayer8Div' class='downlayer'><IMG height=16 width=16 src='../images/scrolldown.gif' onmousedown="this.src='../images/scrolldown2.gif';menuscrolldown()" onmouseout="this.src='../images/scrolldown.gif';menuscrollstop()" onmouseup="this.src='../images/scrolldown.gif';menuscrollstop()"></DIV>
</DIV>
<DIV id='menulayer9Div' class='menulayer'>
<DIV id='barlayer9Div' class='barlayer'>
<TABLE border='0' cellPadding='0' cellSpacing='0' height='22' onclick='menubarpush(9)' width='100%'><tr><td nowrap class='menuTabBar'>&nbsp;成 本 管 理&nbsp;</td></tr></TABLE></DIV>
<DIV id='iconlayer9Div' class='iconlayer'>
<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1422','../quality/certified_card1.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;费用类别</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1423','../quality/certified_card1.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;费用名称</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1424','../quality/certified_card1.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;成本中心</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1425','../quality/certified_card1.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;成本计算流程</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1426','../quality/certified_card1.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;制造费用管理</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1427','../quality/certified_card1.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;原材料耗用分摊</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1428','../quality/certified_card1.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;成本计算</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('1429','../quality/certified_card1.jsp')><img align='absmiddle' border=0 src='../images/tree/fs.gif'>&nbsp;产品成本明细表</td></tr>
</table></DIV>
<DIV id='uplayer9Div' class='uplayer'><IMG height=16 width=16 src='../images/scrollup.gif' onmousedown="this.src='../images/scrollup2.gif';menuscrollup()" onmouseout="this.src='../images/scrollup.gif';menuscrollstop()" onmouseup="this.src='../images/scrollup.gif';menuscrollstop()"></DIV>
<DIV id='downlayer9Div' class='downlayer'><IMG height=16 width=16 src='../images/scrolldown.gif' onmousedown="this.src='../images/scrolldown2.gif';menuscrolldown()" onmouseout="this.src='../images/scrolldown.gif';menuscrollstop()" onmouseup="this.src='../images/scrolldown.gif';menuscrollstop()"></DIV>
</DIV>
<SCRIPT language='javascript'>function bodyOnLoad(){init(22, 10, 'menulayer', 'iconlayer', 'barlayer', 'uplayer', 'downlayer', 'Div');}
</SCRIPT>

      dddddddddddddddddddd
      </td>
      <td width="100%"><iframe id="main" src="main.jsp" border="1" style="display:block; position:absolute; z-index:1;" width="100%" height="100%" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="auto"></iframe></td>
    </tr>
  </table>
</td></tr></table>
<iframe id="public_inside" src="refresh.jsp" style="display:none;" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
</body>
<script language="javascript">
  function refresh()
  {
    var forms = main.document.forms;
    if(forms+'' == 'undefined')
      main.location.reload(true);
    else if(forms.length > 0)
      main.document.forms[0].submit();
    else
      main.location.reload(true);
  }

  var menuStyle = document.all.public_left_menu.style;
  var isOuting = false;//是否正在弹出中
  var width = 5;
  function bodyLoad()
  {
    //if(document.all.main.offsetTop > 66)
    menuStyle.top=document.all.main.offsetTop;
    menuStyle.height=document.all.main.offsetHeight;
    //document.all.public_left_menu.src = 'left.jsp';
    //document.all.public_left_menu.menureload();
  }
  function slideOut()
  {
    var left = parseInt(menuStyle.left);
    var speed = 10;
    while(left < 0)
    {
      isOuting = true;
      speed += 10;
      left += width;
      setTimeout("document.all.public_left_menu.style.left="+ left, speed);
      if(left >= 0)
        setTimeout("endSlideOut();", speed);
    }
  }
  function endSlideOut()
  {
    isOuting = false;
  }
  function slideIn()
  {
    var left = parseInt(menuStyle.left);
    var speed = 15;
    while(!isOuting && left > -145)
    {
      speed += 15;
      left -= width;
      setTimeout("document.all.public_left_menu.style.left="+ left, speed);
    }
  }
  function goMainPage()
  {
    document.all.main.src = "../pub/main.jsp";
  }
  function disableKey()
  {
    keyCode = window.event.keyCode;
    if(keyCode == 8 || (keyCode==78 && event.ctrlKey)) //back,ctrl+n
      return false;
  }
  function openurl(winName,url,iWidth,iHeight,isCenter,isResizable,isScrollbars){
    if(isCenter+'' == 'undefined')
      isCenter = false;
    if(isResizable+'' == 'undefined')
      isResizable = false;
    if(isScrollbars+'' == 'undefined')
      isScrollbars = false;

    var left = 0;
    var top = 0;
    if(iWidth+'' == 'undefined' && iWidth <= 0)
      iWidth = screen.width-10;
    else if(iWidth > screen.width-10)
      iWidth = screen.width-10;
    else if(isCenter)
      left = (screen.width-10 - iWidth)/2;

    if(iHeight+'' == 'undefined' && iHeight <= 0)
      iHeight = screen.height-130;
    else if(iHeight > screen.height-130)
      iHeight = screen.height-130;
    else if(isCenter)
      top = (screen.height-130 - iHeight)/2;

    if(winName +'' == 'undefined')
      winName = "";

    var winretu = window.open(url,winName,"left="+left+",top="+top+",width="+ iWidth +",height="+ iHeight
                + ",menubar=no,toolbar=no,status=no,scrollbars="+(isScrollbars ? "yes" : "no")
                + ",resizable="+ (isResizable ? "yes" : "no"));//790,height=455
    return winretu;
  }
  document.onclick=slideIn;
  document.onkeydown = disableKey;
  window.onresize = bodyLoad;
</script>
</html>