package ru.atott.combiq.rest.v1.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.atott.combiq.rest.mapper.QuestionBeanMapper;
import ru.atott.combiq.rest.mapper.QuestionSearchBeanMapper;
import ru.atott.combiq.rest.request.QuestionChangeRequest;
import ru.atott.combiq.rest.request.QuestionRequest;
import ru.atott.combiq.rest.utils.RestContext;
import ru.atott.combiq.rest.v1.BaseRestController;
import ru.atott.combiq.service.bean.Question;
import ru.atott.combiq.service.markdown.MarkdownService;
import ru.atott.combiq.service.question.QuestionBuilder;
import ru.atott.combiq.service.question.QuestionService;
import ru.atott.combiq.service.search.question.SearchContext;
import ru.atott.combiq.service.search.question.SearchContextFactory;
import ru.atott.combiq.service.search.question.SearchResponse;
import ru.atott.combiq.service.search.question.SearchService;

import javax.validation.Valid;
import java.util.Optional;

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
     * @request.body.example
     *      {@link QuestionRequest#EXAMPLE}
     *     *
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
    @ResponseBody
    public Object get(
            @PathVariable("questionId") String questionId,
            @RequestBody(required = false) QuestionRequest request) {

        Question question = questionService.getQuestion(questionId);

        if (question == null) {
            return responseNotFound();
        }

        QuestionBeanMapper questionMapper = new QuestionBeanMapper();
        if (request == null || request.getRequestedFields().isEmpty()){
            return questionMapper.map(getContext(), question);
        }
        else {
            return questionMapper.requestMap(getContext(), question, request.getRequestedFields());
        }
    }

    /**
     * Создать вопрос.
     *
     * @request.body.example
     *      {@link QuestionChangeRequest#EXAMPLE}
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
            @Valid @RequestBody QuestionChangeRequest request) {
        RestContext context = getContext();

        QuestionBuilder questionBuilder = questionService.createQuestionBuilder(context.getUc(), null);
        updateQuestion(questionBuilder, request);
        Question question = questionService.saveQuestion(context.getUc(), questionBuilder);

        QuestionBeanMapper questionBeanMapper = new QuestionBeanMapper();
        return questionBeanMapper.map(context, question);
    }

    /**
     * Обновить вопрос по заданному идентификатору.
     *
     * @request.body.example
     *      {@link QuestionChangeRequest#EXAMPLE}
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
            @Valid @RequestBody QuestionChangeRequest request) {

        RestContext context = getContext();
        QuestionBuilder questionBuilder = questionService.createQuestionBuilder(context.getUc(), questionId);
        updateQuestion(questionBuilder, request);
        Question question = questionService.saveQuestion(context.getUc(), questionBuilder);

        QuestionBeanMapper questionBeanMapper = new QuestionBeanMapper();
        return questionBeanMapper.map(context, question);
    }

    private void updateQuestion(QuestionBuilder questionBuilder, QuestionChangeRequest request) {
        questionBuilder
                .setTitle(request.getTitle())
                .setBody(request.getBody())
                .setLevel(request.getLevel())
                .setTags(request.getTags())
                .setLinkedQuestions(request.getLinkedQuestions());
    }
}
