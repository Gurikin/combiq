<#import "../_layout/templates.ftl" as templates />
<#import "../_layout/functions.ftl" as functions />
<#import "admin-common.ftl" as common />

<#assign sidebar>
    <@common.sidebar activeMenuItem='options' />
</#assign>

<@templates.layoutWithSidebar
        chapter='admin'
        subTitle='Настройки сайта'
        pageTitle='Настройки'
        sidebar=sidebar
        mainContainerClass='co-rightbordered'>

    <#-- @ftlvariable name="options" type="ru.atott.combiq.service.bean.Options" -->

    <form action="/admin/options" method="post">

        <div class="checkbox">
            <label>
                <input ${functions.checkedIf(options.postsInProduction)} name="postsInProduction" type="checkbox">
                (CO-44) Включить статьи для отображения на бою
            </label>
        </div>

        <div style="margin-top: 40px">
            <button type="submit" class="btn btn-primary">Сохранить</button>
        </div>
    </form>

</@templates.layoutWithSidebar>