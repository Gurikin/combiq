package ru.atott.combiq.rest.v1.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.atott.combiq.rest.mapper.QuestionBeanMapper;
import ru.atott.combiq.rest.mapper.QuestionSearchBeanMapper;
import ru.atott.combiq.rest.request.QuestionRequest;
import ru.atott.combiq.rest.utils.RestContext;
import ru.atott.combiq.rest.v1.BaseRestController;
import ru.atott.combiq.service.bean.Question;
import ru.atott.combiq.service.markdown.MarkdownService;
import ru.atott.combiq.service.question.QuestionService;
import ru.atott.combiq.service.search.question.SearchContext;
import ru.atott.combiq.service.search.question.SearchContextFactory;
import ru.atott.combiq.service.search.question.SearchResponse;
import ru.atott.combiq.service.search.question.SearchService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class QuestionRestController extends BaseRestController {

    @Autowired
    private SearchContextFactory searchContextFactory;

    @Autowired
    private SearchService searchService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private MarkdownService markdownService;

    /**
     * Найти вопросы по здаданному dsl. Если dsl не найден, вернуть все вопросы.
     *
     * @param dsl
     *      Поисковый запрос. Синтаксис dsl можно посмотреть
     *      <a href="https://github.com/combiq/combiq/wiki/%D0%9F%D0%BE%D0%B8%D1%81%D0%BA">здесь</a>.
     *
     * @param page
     *      Номер страницы (zero-based).
     *
     * @param pageSize
     *      Размер возвращаемой страницы.
     *
     * @response.200.doc
     *      Найденные вопросы.
     *
     * @response.200.example
     *      {@link ru.atott.combiq.rest.bean.QuestionSearchBean#EXAMPLE}
     */
    @RequestMapping(value = "/rest/v1/question", method = RequestMethod.GET)
    @ResponseBody
    public Object search(
            @RequestParam(value = "dsl", required = false) Optional<String> dsl,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {

        SearchContext searchContext = searchContextFactory.listByDsl(page, pageSize, dsl.orElse(""));
        SearchResponse searchResponse = searchService.searchQuestions(searchContext);
        QuestionSearchBeanMapper questionSearchBeanMapper = new QuestionSearchBeanMapper();
        return questionSearchBeanMapper.map(getContext(), searchResponse);
    }

    /**
     * Вернуть вопрос по заданному идентификатору questionId.
     *
     * @param questionId
     *      Идентификатор вопроса.
     *
     * @response.200.doc
     *      Вопрос по заданному идентификатору.
     *
     * @response.200.example
     *      {@link ru.atott.combiq.rest.bean.QuestionBean#EXAMPLE}
     *
     * @response.404.doc
     *      В случае если вопрос не найден.
     */
    @RequestMapping(value = "/rest/v1/question/{questionId}", method = RequestMethod.GET)
    public Object get(
            @PathVariable("questionId") String questionId) {

        Question question = questionService.getQuestion(questionId);

        if (question == null) {
            return responseNotFound();
        }

        QuestionBeanMapper questionMapper = new QuestionBeanMapper();
        return questionMapper.map(getContext(), question);
    }

    /**
     * Создать вопрос.
     *
     * @request.body.example
     *      {@link QuestionRequest#EXAMPLE}
     *
     * @response.200.doc
     *      В случае успеха, созданный вопрос.
     *
     * @response.200.example
     *      {@link ru.atott.combiq.rest.bean.QuestionBean#EXAMPLE}
     */
    @RequestMapping(value = "/rest/v1/question", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAnyRole('sa','contenter')")
    public Object createQuestion(
            @Valid @RequestBody QuestionRequest request) {

        RestContext context = getContext();

        Question question = new Question();
        updateAndSaveQuestion(context, question, request);

        questionService.saveQuestion(context.getUc(), question);

        QuestionBeanMapper questionBeanMapper = new QuestionBeanMapper();
        return questionBeanMapper.map(context, question);
    }

    /**
     * Обновить вопрос по заданному идентификатору.
     *
     * @request.body.example
     *      {@link QuestionRequest#EXAMPLE}
     *
     * @param questionId
     *      Идентификатор обновляемого вопроса.
     *
     * @response.200.doc
     *      В случае успеха, обновленный вопрос.
     *
     * @response.200.example
     *      {@link ru.atott.combiq.rest.bean.QuestionBean#EXAMPLE}
     */
    @RequestMapping(value = "/rest/v1/question/{questionId}", method = RequestMethod.PUT)
    @ResponseBody
    @PreAuthorize("hasAnyRole('sa','contenter')")
    public Object saveQuestion(
            @PathVariable("questionId") String questionId,
            @Valid @RequestBody QuestionRequest request) {

        Question question = questionService.getQuestion(questionId);

        if (question == null) {
            return responseNotFound();
        }

        RestContext context = getContext();
        updateAndSaveQuestion(context, question, request);

        QuestionBeanMapper questionBeanMapper = new QuestionBeanMapper();
        return questionBeanMapper.map(context, question);
    }

    private void updateAndSaveQuestion(RestContext context, Question question, QuestionRequest request) {
        question.setTitle(request.getTitle());
        question.setBody(markdownService.toMarkdownContent(context.getUc(), request.getBody()));
        question.setLevel(request.getLevel());
        question.setTags(request.getTags() != null ? request.getTags() : Collections.emptyList());
        question.setLastModify(new Date());
        if(request.getLinkedQuestions() != null)
        question.setLinkedQuestions(request.getLinkedQuestions()
        .stream().map(x -> {
                    Question y = new Question();
                    y.setId(x);
                    return y;
                }).collect(Collectors.toList()));

        questionService.saveQuestion(context.getUc(), question);
    }
}
