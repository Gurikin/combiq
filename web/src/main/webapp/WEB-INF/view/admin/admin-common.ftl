<#import "../_layout/templates.ftl" as templates />
<#import "../_layout/functions.ftl" as functions />

<#macro sidebar activeMenuItem>
<ul class="co-nav co-nav-right-bordered nav nav-pills nav-stacked">
    <li role="presentation" class="${templates.if(activeMenuItem == 'dashboard', 'active')}">
        <a href="/admin">
            <span>Dashboard</span>
        </a>
    </li>

    <#if functions.hasRole('sa')>
        <li role="presentation" class="${templates.if(activeMenuItem == 'options', 'active')}">
            <a href="/admin/options">
                <span>Настройки</span>
            </a>
        </li>
    </#if>

    <#if functions.hasRole('sa')>
    <li role="presentation" class="${templates.if(activeMenuItem == 'users', 'active')}">
        <a href="/admin/users">
            <span>Пользователи</span>
        </a>
    </li>
    </#if>

    <#if functions.hasRole('sa')>
        <li role="presentation" class="${templates.if(activeMenuItem == 'events', 'active')}">
            <a href="/admin/events">
                <span>События</span>
            </a>
        </li>
    </#if>

    <#if functions.hasRole('sa') || functions.hasRole('contenter')>
        <li role="presentation" class="${templates.if(activeMenuItem == 'posts', 'active')}">
            <a href="/admin/posts">
                <span>Статьи</span>
            </a>
        </li>
    </#if>
</ul>
</#macro>