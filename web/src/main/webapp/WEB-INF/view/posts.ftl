<#import "_layout/templates.ftl" as templates />
<#import "_layout/parts.ftl" as parts />
<#import "_layout/functions.ftl" as functions />

<#assign sidebar>

</#assign>

<@templates.layoutWithSidebar sidebar=sidebar chapter='posts'>

    <ul class="co-posts">
        <#list posts as post>
        <#-- @ftlvariable name="post" type="ru.atott.combiq.service.bean.Post" -->

            <li>
                <h2>
                    <a href="${urlResolver.getPostUrl(post)}">
                    ${post.title}
                    </a>
                </h2>

                <div>
                ${(post.preview.html)!''}
                </div>
                <div>
                    <a href="${urlResolver.getPostUrl(post)}">
                        Читать дальше →
                    </a>
                </div>
            </li>
        </#list>
    </ul>

    <@templates.paging paging=paging />
</@templates.layoutWithSidebar>