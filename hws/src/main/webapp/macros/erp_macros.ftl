<!--
  Set the META tag for ContentType.  We do this in addition to
  setting the Response header because the header does not get
  cached.  So in order to maintain the ContentType we must present
  it inside the rendered page itself.
-->
<#macro showContentType ctype="text/html; charset=utf-8">
    <meta http-equiv="Content-Type" content="${ctype}" />
</#macro>


<!--===========================For static start============================================== -->
<#macro showResourcePath>${contextPath}/${staticLocation}</#macro>

<!-- Show path to a theme image -->
<#macro showResourceImagePath imageName><@showResourcePath />image/${imageName}</#macro>

<!-- Shows an IMG tag for a theme image -->
<#macro showResourceImage imageName><img alt="${imageName}" src="<@showResourceImagePath imageName />" ></#macro>

<!-- Shows path to a file in a theme's style directory -->
<#macro showResourceStylePath stylesheet><@showResourcePath />css/${stylesheet}</#macro>

<!-- Shows a STYLE or LINK REL tag for a file in a theme's style directory -->
<#macro showResourceStyle stylesheet><link rel="stylesheet" type="text/css" href="<@showResourceStylePath stylesheet/>" /></#macro>

<!-- Shows path to a file in a theme's scripts directory-->
<#macro showResourceScriptPath scriptFile ><@showResourcePath />js/${scriptFile}</#macro>

<!-- Shows SCRIPT tag for a file in a theme's scripts directory-->
<#macro showResourceScript scriptFile >
<script language="javascript" type="text/javascript" src="<@showResourceScriptPath scriptFile />"></script>
</#macro>

<!--===========================For static end============================================== -->



<!--===========================For Branding start============================================== -->
<#macro showBrandingResourcePath>${contextPath}/${brandingStaticLocation}</#macro>

<!-- Show path to a theme image -->
<#macro showBrandingResourceImagePath imageName><@showBrandingResourcePath />image/${imageName}</#macro>

<!-- Shows an IMG tag for a theme image -->
<#macro showBrandingResourceImage imageName><img src="<@showBrandingResourceImagePath imageName />" border="0"></#macro>

<!-- Shows path to a file in a theme's style directory -->
<#macro showBrandingResourceStylePath stylesheet><@showBrandingResourcePath />css/${stylesheet}</#macro>

<!-- Shows a STYLE or LINK REL tag for a file in a theme's style directory -->
<#macro showBrandingResourceStyle stylesheet><link rel="stylesheet" type="text/css" href="<@showBrandingResourceStylePath stylesheet/>" /></#macro>

<!-- Shows path to a file in a theme's scripts directory-->
<#macro showBrandingResourceScriptPath scriptFile ><@showBrandingResourcePath />js/${scriptFile}</#macro>

<!-- Shows SCRIPT tag for a file in a theme's scripts directory-->
<#macro showBrandingResourceScript scriptFile ><script type="text/javascript" src="<@showBrandingResourceScriptPath scriptFile />"></script></#macro>

<!-- Shows path to a file in a theme's scripts directory-->
<#macro showBrandingResourcei18nScriptPath scriptFile ><@showBrandingResourcePath />js/${scriptFile}</#macro>

<!-- Shows SCRIPT tag for a file in a theme's scripts directory-->
<#macro showBrandingResourcei18nScript scriptFile ><script type="text/javascript" src="<@showBrandingResourcei18nScriptPath scriptFile />"></script></#macro>
<!--===========================For Branding end============================================== -->

<!--===========================For Normal start============================================== -->
<#macro headerDiv>
<#include "/${templateLocation}common/header.ftl" encoding="UTF-8">
</#macro>


<#macro navigationDiv>
<#include "/${templateLocation}common/navigation.ftl" encoding="UTF-8">
</#macro>

<#macro footerDiv>
<#include "/${templateLocation}common/footer.ftl" encoding="UTF-8">
</#macro>
<#macro showValidator>
	<@showResourceScript 'validator.js' />
</#macro>

<!--===========================For Normal end============================================== -->


<!-- Add by Jeecy for message-->
<#macro showActionMsg>
	<#if actionMessages?exists>
		<#if actionMessages?has_content>
 <div class="Main_handleBar check_pass" id="Main_handleBar" >
  <div class="Main_handleBar_head"></div>
         <div class="Main_handleBar_body">
		    	<p><#list actionMessages as msg>
				${action.getText(msg)}
		    	</#list></p>
            </div>
         <div class="Main_handleBar_foot"><a href="javascript:void(0)" class="close" onclick="closeHandleBar()"></a></div>
         </div>
		<#else>
		<#assign errorMsg = stack.findValue("errorMsg")?if_exists/>
		<#if errorMsg?exists && errorMsg!="">
      <div class="Main_handleBar check_fall" id="Main_handleBar" >
        <div class="Main_handleBar_head"></div>
          <div class="Main_handleBar_body">${action.getText(errorMsg)}</div>
				<!--ExceptionStack:${exceptionStack?if_exists}-->
          <div class="Main_handleBar_foot"><a href="javascript:void(0)" class="close" onclick="closeHandleBar()"></a></div>
       </div>
		</#if>
	</#if>
	</#if>		
</#macro>

<#macro subStringForList SomeName="" defaultLen=20>
	<p class="textOverflow" title="${SomeName?html}">
		<#if SomeName?has_content>${SomeName?html}</#if>
	</p>
</#macro>

<#macro showLeftBar>
	<div class="Main_Left" id="Main_left"> 
	<div class="Main_Left_Top"><@s.text name="elearning.common.header.mainmenu.sa"/></div> 
	<div class="Main_Left_Content" id="MenuBox"> 
		
	</div> 
	<div class="Main_Left_Bottom"></div> 
</div> 

</#macro>

<#function addParams url param>
	<#assign returnValue = "" /> 
	<#if url?exists>
			<#if (url?index_of("?") > 0)>
				<#assign returnValue = url + "&" + param />;
			<#else>
				<#assign returnValue = url + "?" + param />;
			</#if>
	</#if>
	<#return returnValue>
</#function>
