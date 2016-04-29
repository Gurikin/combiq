<#import "_layout/templates.ftl" as templates />

<#assign head>
    <script>
        VK.init({apiId: 5065282, onlyWidgets: true});
    </script>
</#assign>

<@templates.layoutBody bodyClass='co-titler-body' head=head subTitle='готовьтесь к Java собеседованию здесь'>

    <div class="container">
        <div class="co-titler">
            <img class="hidden-xs hidden-sm" src="/static/images/background.svg">

            <div class="co-mainhead-androidapp">
                <a href="https://play.google.com/store/apps/details?id=ru.atott.combiq.android">
                    <img style="width: 210px;" alt='Доступно на Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/ru_badge_web_generic.png'/>
                </a>
            </div>

            <div class="co-mainhead text-center">
                <img class="hidden-xs" src="/static/images/site/titler.png">

                <h1 class="co-mainhead-content">
                    Всё, что может вам потребоваться
                    <br>
                    для подготовки к Java собеседованию
                </h1>
            </div>
        </div>

    </div>

    <div class="co-mainer">
        <div class="co-mainer-background"></div>
        <div class="container">
            <div class="co-mainer-title">
                Подключайся
            </div>
            <div class="co-mainer-desc">
                Combiq.ru - это проект с открытым исходным кодом,
                цель которого собрать в одном месте всю полезную информацию
                для Java программистов, которые готовятся к собеседованию
                на новое место работы.
            </div>
            <a class="co-mainer-a" href="https://github.com/combiq/combiq">Combiq.ru на github</a>
        </div>
    </div>

    <div class="container">
        <div class="row">
            <div class="col-md-4">
                <div class="co-mainer-desc" style="margin-top: 60px">
                    <p>
                        Мы собрали большое количество вопросов, которые
                        часто спрашивают на собеседовании на позицию Java разработчик.
                        Фильтруй их по тематике и своему уровню чтобы освежить знания и
                        подготовиться к собеседованию.
                    </p>
                    <p>
                        <a href="/questions">Перейти к вопросам →</a>
                    </p>
                </div>
            </div>
            <div class="col-md-8 hidden-xs hidden-sm">
                <img style="margin-top: -100px;" src="/static/images/site/questions-promo.png">
            </div>
        </div>
    </div>
    <div style="background-color: #E3E3E3; margin-bottom: -60px; padding-bottom: 40px; margin-top: 35px;">
        <div class="container">
            <div class="row text-center">
                <h2>Что <span class="co-colored">умеет</span> Combiq.ru</h2>
            </div>
            <div class="row text-center">
                <div class="col-md-4">
                    <p>
                        <img src="/static/images/search.png" alt="Искать вопросы">
                    </p>
                    <h3>
                        Вопросы
                    </h3>
                    <p>
                        У нас большая база вопросов для подготовки к собеседованию.
                        Гибкий поиск, основанный на возможностях <a href="https://www.elastic.co/products/elasticsearch">Elastic Search</a>,
                        поможет вам найти действительно нужные вопросы.
                    </p>
                    <p>
                        <a href="/questions">
                            <span class="glyphicon glyphicon-circle-arrow-right" aria-hidden="true"></span>
                            Вопросы
                        </a>
                    </p>
                </div>
                <div class="col-md-4">
                    <p>
                        <img src="/static/images/poll.png" alt="Опросники для собеседования">
                    </p>
                    <h3>
                        Опросники
                    </h3>
                    <p>
                        Если вы оказались по другую сторону баррикад и ищете себе сотрудников в
                        компанию, то уже подготовленные списки с вопросами помогут вам найти
                        компетентных специалистов.
                    </p>
                    <p>
                        <a href="/questionnaires">
                            <span class="glyphicon glyphicon-circle-arrow-right" aria-hidden="true"></span>
                            Опросники
                        </a>
                    </p>
                </div>
                <div class="col-md-4">
                    <p>
                        <img src="/static/images/plan.png" alt="Планы для подготовки">
                    </p>
                    <h3>
                        План подготовки
                    </h3>
                    <p>
                        Наш план подготовки к собеседованию поможет вам охватить
                        все области применения Java. В плане используются только компетентные
                        источники информации.
                    </p>
                    <p>
                        <a href="/education">
                            <span class="glyphicon glyphicon-circle-arrow-right" aria-hidden="true"></span>
                            План подготовки
                        </a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</@templates.layoutBody>