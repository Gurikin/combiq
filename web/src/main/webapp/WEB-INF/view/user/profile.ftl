<#import "user-common.ftl" as common />
<#import "../_layout/templates.ftl" as templates />
<#import "../_layout/parts.ftl" as parts />
<#import "../_layout/functions.ftl" as functions />

<@common.userLayout>

    <div class="row">
        <div class="col-md-3">
            <div class="co-userphoto pull-right">
                <img src="${headAvatarUrl}">
            </div>
        </div>
        <div class="col-md-9">
            <h1>${userName}</h1>
            <#if userRegisterDate??>
                <div>
                    Зарегистрировался ${userRegisterDate?string["dd MMM yyyy г."]}
                </div>
            </#if>
            <#if nick??>
            <div>
                 Ник: ${nick}  <a href="" onclick="ko.openDialog('co-editnick'); return false;">Изменить</a>
            </div>
            <#else>
                <a href="" onclick="ko.openDialog('co-editnick'); return false;">Выберите себе Ник</a>
            </#if>
            <co-questionssearch params="
                title: 'Избранные вопросы',
                dsl: 'favorite:true',
                size: 5">
            </co-questionssearch>
            <a class="co-exit" href="/logout.do">
                <h3>Выйти</h3>
            </a>
        </div>
    </div>

</@common.userLayout>