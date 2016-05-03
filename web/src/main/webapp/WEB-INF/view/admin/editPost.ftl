<#import "../_layout/templates.ftl" as templates />
<#import "../_layout/functions.ftl" as functions />
<#import "admin-common.ftl" as common />

<#assign sidebar>
    <@common.sidebar activeMenuItem='posts' />
</#assign>

<@templates.layoutWithSidebar
        chapter='admin'
        pageTitle='Редактировать статью'
        subTitle='Редактировать статью из раздела Блог'
        sidebar=sidebar
        mainContainerClass='co-rightbordered'>


    <co-posteditor params="
        postId: ${functions.if(postId??, "'" + (postId)!?js_string + "'", "null")}
    ">
    </co-posteditor>

</@templates.layoutWithSidebar>