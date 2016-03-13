<#-- @ftlvariable name="question" type="ru.atott.combiq.service.bean.Question" -->
<#-- @ftlvariable name="comment" type="ru.atott.combiq.dao.entity.QuestionComment" -->
<#-- @ftlvariable name="position" type="ru.atott.combiq.service.question.impl.QuestionPositionInDsl" -->

<#import "_layout/templates.ftl" as templates />
<#import "_layout/parts.ftl" as parts />
<#import "_layout/functions.ftl" as functions />

<#assign head>
    <script type="text/javascript" src="http://vk.com/js/api/share.js?93" charset="windows-1251"></script>

    <#if position??>
    <script>
        $(document).keydown(function(event) {
            if (document.activeElement &&
                    ['input', 'textarea', 'select'].indexOf((document.activeElement.tagName || '').toLowerCase()) != -1) {
                return;
            }

            var previousUrl = '${position.resolvePreviouesQuestionUrl(urlResolver)!?js_string}';
            var nextUrl = '${position.resolveNextQuestionUrl(urlResolver)!?js_string}';

            if (event.ctrlKey && event.keyCode == 39 && nextUrl) {
                location.href = nextUrl;
            }

            if (event.ctrlKey && event.keyCode == 37 && previousUrl) {
                location.href = previousUrl;
            }
        });
    </script>
    </#if>
</#assign>

<#assign sidebar>
    <#if functions.hasRoleSaOrContenter()>
        <div>
            <button class="btn btn-primary" onclick="ko.openDialog('co-questionposter');">
                Добавить новый вопрос
            </button>
        </div>
    </#if>

    <div>
        <h4>Полезное</h4>
        <ol class="list-unstyled co-question-aside-tips">
            <#list tags as tag>
                <#if tag.suggestViewOthersQuestionsLabel??>
                    <li>
                        <div class="row">
                            <div class="col-md-1">
                                <span class="glyphicon glyphicon-link" aria-hidden="true"></span>
                            </div>
                            <div class="col-md-10">
                                <a href="/questions/tagged/${tag.tag?url}">
                                ${tag.suggestViewOthersQuestionsLabel?html} →
                                </a>
                            </div>
                        </div>
                    </li>
                </#if>
            </#list>

            <li>
                <div class="row">
                    <div class="col-md-1">
                        <span class="glyphicon glyphicon-ok"></span>
                    </div>
                    <div class="col-md-10">
                        Возможно, вам будет проще готовиться к собеседованию по уже готовым
                        <a href="/questionnaires">опросникам →</a>
                    </div>
                </div>
            </li>
        </ol>
    </div>

    <#if question.classNames?? && question.classNames?size != 0>
        <div>
            <h4>JavaDoc</h4>
            <ol class="list-unstyled co-question-aside-tips">
                <#list question.classNames as className>
                    <li>
                        <div class="row">
                            <div class="col-md-1">
                                <span class="glyphicon glyphicon-book"></span>
                            </div>
                            <div class="col-md-10">
                                <a href="https://docs.oracle.com/javase/8/docs/api/${className?replace(".", "/")}.html">${className}</a>
                                <span style="font-size: 12px">
                                    (<a style="color: #777777"
                                        href="http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/8u40-b25/${className?replace(".", "/")}.java">src</a>)
                                </span>
                            </div>
                        </div>
                    </li>
                </#list>
            </ol>
        </div>
    </#if>

    <#--<#if questionsFeed?size != 0>
        <div>
            <h4>Лента обновлений</h4>
            <@listQuestionsFeed questionsFeed=questionsFeed />
        </div>
    </#if>-->

    <#if landing>
        <div>
            <h4>ВКонтакте</h4>
            <!-- VK Widget -->
            <div id="vk_groups"></div>
            <script type="text/javascript">
                VK.Widgets.Group("vk_groups", {mode: 2, width: "220", height: "400"}, 111268840);
            </script>
        </div>
    </#if>
</#assign>

<@templates.layoutWithSidebar
        head=head
        dsl=dsl
        chapter='questions'
        subTitle=question.title
        sidebar=sidebar
        sidebarContainerClass='co-question-aside'
        ogDescription=question.title>

        <div class="co-question">
            <div>
                <div>
                    <div class="co-question-title">
                    ${question.title}
                    <div class="star">
                        <#if user??>
                           <#if  userFavorite>
                                    <img src="/static/images/site/dislike-star.png" width="35"
                                     height="35" onclick="
                                    $.post('/questions/${question.id}/dislike').done(function(x) {
                                    document.location.reload(true);});"></img>
                           <#else>
                                    <img  src="/static/images/site/like-star.png"
                                     width="35" height="35"  onclick="
                                    $.post('/questions/${question.id}/like').done(function(x) {
                                    document.location.reload(true);});"></img>
                           </#if>
                        <#else>
                            <img  src="/static/images/site/like-star.png"
                             width="35" height="35"  onclick="document.location.href = '/login.do';"></img>
                        </#if>
                        <span>${question.stars}</span>
                    </div>
                    </div>
                    <div class="co-question-body">
                        <@parts.contentEditor content=question.body url='/questions/${question.id}/content' />
                    </div>

                    <@questionStaff />
                </div>
            </div>
        </div>

        <@questionPosition />

        <#if landing>
            <@landingBlock />
        </#if>

        <@listQuestionComments />
</@templates.layoutWithSidebar>

<#macro landingBlock>
    <div class="co-landing">
        <div class="row">
            <div class="col-md-8 co-landing-another-questions">
                <#if anotherQuestions??>
                    <h4>Другие вопросы для подготовки к собеседованию</h4>
                    <ul>
                        <#list anotherQuestions as anotherQuestion>
                            <li>
                                <a href="${urlResolver.getQuestionUrl(anotherQuestion)}">
                                    ${anotherQuestion.title}
                                </a>
                            </li>
                        </#list>
                    </ul>
                </#if>
            </div>
            <div class="col-md-4 co-landing-another-desc">
                <a href="http://combiq.ru">Combiq.ru</a> - это проект с открытым исходным кодом, цель которого
                собрать в одном месте
                всю полезную информацию для Java программистов, которые
                готовятся к собеседованию на новое место работы.
            </div>
        </div>
    </div>
</#macro>

<#macro questionStaff>
    <ul class="co-question-staff">
        <#list question.tags as tag>
            <li>
                <a class="co-tag" href="/questions/tagged/${tag}">${tag}</a>
            </li>
        </#list>
        <#if question.level??>
            <li style="margin-left: 15px;" class="co-small">
                <@parts.questionLevel level=question.level class='co-small' />
                <span class="inline" style="margin-left: 4px;">
                    ${parts.explainLevel(question.level)} уровень
                </span>
            </li>
        </#if>
        <li class="pull-right" style="padding-top: 2px;">
            <script type="text/javascript"><!--
            document.write(VK.Share.button({url: "${canonicalUrl}"},{type: "round", text: "Поделиться", eng: 1}));
            --></script>
        </li>

    </ul>

    <#if functions.hasRoleSaOrContenter()>
        <a  href="#" onclick="ko.openDialog('co-questionposter',{id: '${question.id?js_string}'}); return false;">
            Изменить вопрос
        </a>

        <#if question.deleted>
            <a class="pull-right" href="#"
                    onclick="$.post('/questions/${question.id}/restore');
                    window.location.replace('/questions/search');">Востановить Вопрос</a>
        <#else>
            <a class="pull-right" href="#"
                onclick="$.post('/questions/${question.id}/delete');
                window.location.replace('/questions/search');">Удалить Вопрос</a>
        </#if>
    </#if>
</#macro>

<#macro questionPosition>
    <#if position??>
        <div >
            <ul class="co-question-position">
                <#if position.previousQuestion??>
                    <li>
                        <div>
                            <a href="${position.resolvePreviouesQuestionUrl(urlResolver)}">
                                <span class="co-arrow">←</span> предыдущий
                            </a>
                        </div>
                        <div class="co-small pull-right co-question-position-keyboardtip">
                            ctrl + <span class="co-arrow">←</span>
                        </div>
                    </li>
                </#if>
                <li>
                    <strong>${position.index + 1}</strong> из <a href="/questions/search?q=${dsl!?url}">${position.total}</a>
                </li>
                <#if position.nextQuestion??>
                    <li>
                        <div>
                            <a href="${position.resolveNextQuestionUrl(urlResolver)}">
                                следующий <span class="co-arrow">→</span>
                            </a>
                        </div>
                        <div class="co-small co-question-position-keyboardtip">
                            ctrl + <span class="co-arrow">→</span>
                        </div>
                    </li>
                </#if>
            </ul>
        </div>
        <div style="margin-bottom: 25px;" class="clearfix"></div>
    </#if>
</#macro>

<#macro listQuestionComments>
    <div>
        <h4 id="comments">Комментарии</h4>
        <co-commentposter params="questionId: '${question.id?js_string}'"></co-commentposter>
        <div style="margin-top: 25px;">

            <#if !user?? && question.level == 'D3'>

                <div class="alert alert-warning">
                    <div class="row">
                        <div class="col-md-12">
                            <div>
                                Просматривать комментарии к вопросам уровня D3 могут только аутентифицированные пользователи.
                                <br>
                                Пожалуйста, войдите на Combiq.ru.
                            </div>
                            <div style="margin-top: 15px;">
                                <button onclick="ko.openDialog('co-login');" class="btn btn-primary">Войти на Combiq.ru</button>
                            </div>
                            <div class="co-small" style="margin-top: 15px;">
                                Для входа на Combiq.ru регистрация и подтверждение email не требуются.
                            </div>
                        </div>
                    </div>
                </div>

            <#else>

                <#if comments?? && comments?size &gt; 0>
                    <ul class="co-comments">
                        <#list comments as comment>
                            <li>
                                <@outComment comment=comment />
                            </li>
                        </#list>
                    </ul>

                <#else>
                    <div class="co-comments-notfound">
                        Комментариев к этому вопросу пока нет. Возможно, вам будут интересны комментарии
                        наших пользователей к другим вопросам:
                    </div>

                    <ul class="co-comments co-comments-latest">
                        <#list questionsWithLatestComments as question>
                            <li>
                                <div class="co-comments-question-title">
                                    <@parts.questionLevel level=question.level class='co-small' />
                                    <a href="${urlResolver.getQuestionUrl(question)}">${question.title}</a>
                                </div>
                                <div class="co-comments-question-comments">
                                    <@outComment comment=question.lastComment />
                                </div>
                            </li>
                        </#list>
                    </ul>
                </#if>

            </#if>
        </div>
    </div>
</#macro>

<#macro listQuestionsFeed questionsFeed>
    <ol class="list-unstyled">
        <#list questionsFeed as question>
           <li>
               <div class="co-questionsFeeds-question-title">
                   <a href="${urlResolver.getQuestionUrl(question)}">${question.title}</a>
               </div>
               <div class="co-comments-question-comments">
                   <@outComment comment=question.lastComment />
               </div>
           </li>
        </#list>
    </ol>
</#macro>

<#macro outComment comment>
    <span class="co-comments-meta" id="comment-${comment.id!}">
        ${comment.userName}, ${comment.postDate?string('dd MMMM yyyy, hh:mm')}
        <#if comment.editDate??>
            <span class="co-comments-meta-edited" title="${comment.editUserName!comment.userName}, ${comment.editDate?string('dd MMMM yyyy, hh:mm')}">изменён</span>
        </#if>
        <#if (user.id)! == comment.userId
                || functions.hasRole('sa')
                || functions.hasRole('contenter') >
            <a class="pull-right" href="#"
                onclick="ko.openDialog('co-editcomment', {
                    questionId: '${question.id?js_string}',
                    commentId: '${comment.id?js_string}',
                    commentMarkdown: '${comment.content.markdown?js_string}'
                }); return false;">
                Изменить
            </a>
        </#if>
    </span>
    <div class="co-comments-body">
    ${comment.content.html}
    </div>
</#macro>