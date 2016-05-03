<#import "_layout/templates.ftl" as templates />
<#import "_layout/parts.ftl" as parts />
<#import "_layout/functions.ftl" as functions />

<#assign sidebar>

</#assign>

<@templates.layoutWithSidebar sidebar=sidebar chapter='posts'>

    <#-- @ftlvariable name="post" type="ru.atott.combiq.service.bean.Post" -->

    <h1>${post.title}</h1>

    <div>
        ${(post.content.html)!}
    </div>

</@templates.layoutWithSidebar>