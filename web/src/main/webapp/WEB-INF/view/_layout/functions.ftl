<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />

<#function if condition a b=''>
    <#if condition>
        <#return a>
    <#else>
        <#return b>
    </#if>
</#function>

<#function checkedIf condition>
    <#return if(condition, 'checked="checked"', '')>
</#function>

<#function hasRole roleName>
    <@security.authorize access="hasRole('" + roleName + "')">
        <#return true>
    </@security.authorize>
    <#return false>
</#function>

<#function hasRoleSaOrContenter>
    <#return hasRole("sa") || hasRole("contenter")>
</#function>