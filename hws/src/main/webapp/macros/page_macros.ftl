<#macro showPageInfo page functionName="changePage">
<script language="javascript">
	//innerChangePage = ${functionName};
</script>
		<#assign numPages = page.totalPage>
		<#assign thisPage = page.currentPage>
		<#assign numRecords = page.totalRecords>
		<#if (numPages > 1 ) >
			<#if (thisPage >1) >
				<span class="start"><a href="javascript:void(0)" onclick='javascript:${functionName}(1);return false;'><@s.text name="infowarelab.page.firstpage"/></a></span>
				<#assign previous = page.currentPage>
				<#if page.getHasPrePage()>
					<#assign previous = page.currentPage -1 >
				</#if>
				<span class="previous"><a href="javascript:void(0)" onclick='javascript:${functionName}(${previous});return false;'><@s.text name="infowarelab.page.prepage"/></a></span>
			<#else>
				<span class="start"><@s.text name="infowarelab.page.firstpage"/></span>
				<span class="previous"><@s.text name="infowarelab.page.prepage"/></span>
			</#if>
			(<@s.text name="infowarelab.page.thispage"><@s.param>${thisPage}</@s.param></@s.text>/<@s.text name="infowarelab.page.totalpages"><@s.param>${numPages}</@s.param></@s.text> 共${numRecords}条) 
			<#if (thisPage == numPages) >
				<span class="next"><@s.text name="infowarelab.page.nextpage"/></span>
				<span class="end"><@s.text name="infowarelab.page.lastpage"/></span>
			<#else>
				<#assign next = page.currentPage>
				<#if page.getHasNextPage()>
					<#assign next = page.currentPage + 1 >
				</#if>
				<span class="next"><a href="javascript:void(0)" onclick='javascript:${functionName}(${next});return false;'><@s.text name="infowarelab.page.nextpage"/></a></span>
				<span class="end"><a href="javascript:void(0)" onclick='javascript:${functionName}(${numPages});return false;'><@s.text name="infowarelab.page.lastpage"/></a></span>
			</#if>

			<#assign totalRecord = page.totalRecords>
	   		<@s.text name="infowarelab.page.jump"/>
	   		<input name="currentPage" class="input_mini" type="text" size="2" onkeypress="if(event.keyCode==13){javascript:var cpage=1;var currentPage=this;if(currentPage.value<=${numPages}&&currentPage.value.indexOf('.')==-1&& currentPage.value>0){cpage=currentPage.value}else{alert('<@s.text name="infowarelab.page.js.outtotalpages"/>');return false;}${functionName}(cpage);return false;}" title="<@s.text name="infowarelab.page.jump.title"/>"/>
	   		<@s.text name="infowarelab.page.page"/>
	   </#if>
</#macro>

<#macro fillLine pageSize dataCount columnCount>
<#assign trStyle=0>
<#assign leftLine=(pageSize-dataCount)>
	<#if (leftLine>0)>
		<#list 1..leftLine as i>
			<#if (trStyle==0)>
				<tr class="even">
				<#assign trStyle=1>
			<#else>
				<tr>		
				<#assign trStyle=0>
			</#if>
				<#list 1..columnCount as j>
					<td>&nbsp;</td>
				</#list>				
			</tr>
		</#list>
	</#if>	
</#macro>