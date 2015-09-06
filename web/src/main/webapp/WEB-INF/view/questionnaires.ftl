<#import "templates.ftl" as templates />

<@templates.layoutBody head=head chapter='questionnaires' subTitle='Опросники'>
    <div class="container">
        <h1>Опросники для собеседований</h1>
        <p>
            Ниже представлены несколько опросников, которые помогут вам провести техническое собеседование кандидатов
            на вакансию Java разработчик разных уровней (D1-D3).
        </p>
        <p>
            Техническое собеседование обычно проводится после предварительного собеседования HR-специалиста на вопрос
            «адекватности»  кандидата. За каждый ответ на вопрос Вы можете проставлять
            баллы в диапазоне от 1 до 5, где: 1 – не знает совсем, 5 – знает и понимает отлично.
            Кандидату обычно задаются все вопросы уровня, на который он претендует, и вопросы для всех предыдущих уровней.
            Следует помнить, что несмотря на наличие баллов, оценка уровня кандидата это вопрос исключительно  субъективный.
            Незнание  какой-либо  технологии  (раздела)  может  говорить  лишь  об отсутствии  опыта.
        </p>
        <ul>
            <#list questionnaires as questionnaire>
                <li>
                    <a href="/questionnaire/${questionnaire.id}">
                        ${questionnaire.name}
                    </a>
                </li>
            </#list>
        </ul>
    </div>
</@templates.layoutBody>