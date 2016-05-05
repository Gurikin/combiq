package ru.atott.combiq.web.controller.question;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import ru.atott.combiq.service.bean.Question;
import ru.atott.combiq.service.dsl.DslParser;
import ru.atott.combiq.service.question.AskedQuestionService;
import ru.atott.combiq.service.question.FavoriteQuestionService;
import ru.atott.combiq.service.question.QuestionService;
import ru.atott.combiq.service.question.TagService;
import ru.atott.combiq.service.search.comment.LatestComment;
import ru.atott.combiq.service.search.comment.LatestCommentSearchService;
import ru.atott.combiq.service.search.question.GetQuestionContext;
import ru.atott.combiq.service.search.question.GetQuestionResponse;
import ru.atott.combiq.service.search.question.SearchService;
import ru.atott.combiq.service.site.RequestUrlResolver;
import ru.atott.combiq.service.site.UrlResolver;
import ru.atott.combiq.web.controller.BaseController;
import ru.atott.combiq.web.security.AuthService;
import ru.atott.combiq.web.view.QuestionViewBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class QuestionController extends BaseController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private LatestCommentSearchService latestCommentSearchService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TagService tagService;

    @Autowired
    private FavoriteQuestionService favoriteQuestionService;

    @Autowired
    private AskedQuestionService askedQuestionService;

    @RequestMapping(value = {
            "/questions/{questionId}",
            "/questions/{questionId}/{humanUrlTitle}"
    })
    public Object view(@PathVariable("questionId") String questionId,
                       @PathVariable("humanUrlTitle") Optional<String> humanUrlTitle,
                       @RequestParam(value = "index", required = false) Integer index,
                       @RequestParam(value = "dsl", required = false) String dsl,
                       HttpServletRequest request) {
        UrlResolver urlResolver = new RequestUrlResolver(request);

        GetQuestionContext context = new GetQuestionContext();
        context.setUserId(authService.getUserId());
        context.setId(questionId);

        if (index != null) {
            context.setProposedIndexInDslResponse(index);
            context.setDsl(DslParser.parse(dsl));
        }

        GetQuestionResponse questionResponse = searchService.getQuestion(getUc(), context);

        RedirectView redirectView = redirectToCanonicalUrlIfNeed(questionId, humanUrlTitle.orElse(null), questionResponse, request);

        if (redirectView != null) {
            return redirectView;
        }

        List<Question> anotherQuestions = null;
        Question question = questionResponse.getQuestion();
        if (question == null) {
            question = questionService.getQuestion(questionId);
        } else {
            if (questionResponse.getQuestion().isLanding()) {
                anotherQuestions = searchService
                        .searchAnotherQuestions(getUc(), questionResponse.getQuestion())
                        .map(response -> response.getQuestions().getContent())
                        .orElse(null);
            }
        }

        if (question == null) {
            throw new QuestionNotFoundException();
        }

        List<LatestComment> questionsWithLatestComments = Collections.emptyList();
        if (CollectionUtils.isEmpty(question.getComments())) {
            questionsWithLatestComments = latestCommentSearchService.get3LatestComments();
        }
        QuestionViewBuilder viewBuilder = new QuestionViewBuilder();
        viewBuilder.setQuestion(question);
        viewBuilder.setPositionInDsl(questionResponse.getPositionInDsl());
        viewBuilder.setDsl(dsl);
        viewBuilder.setTags(tagService.getTags(question.getTags()));
        viewBuilder.setCanonicalUrl(urlResolver.externalize(urlResolver.getQuestionUrl(question)));
        viewBuilder.setAnotherQuestions(anotherQuestions);
        viewBuilder.setQuestionsWithLatestComments(questionsWithLatestComments);
        viewBuilder.setFavorite(favoriteQuestionService.isFavoriteQuestion(getUc(), questionId));
        viewBuilder.setAsked(askedQuestionService.isAskedQuestion(getUc(), questionId));
        viewBuilder.setLinkedQuestions(questionService.getQuestions(question.getLinkedQuestions()));
        return viewBuilder.build();
    }


    private RedirectView redirectToCanonicalUrlIfNeed(String questionId,
                                                      String humanUrlTitle,
                                                      GetQuestionResponse searchResponse,
                                                      HttpServletRequest request) {
        Question question = searchResponse.getQuestion();

        if (question == null) {
            question = searchService.getQuestionByLegacyId(questionId);

            if (question == null) {
                return null;
            }

            UrlResolver urlResolver = new RequestUrlResolver(request);
            String questionUrl = urlResolver.getQuestionUrl(question, request.getQueryString());
            return movedPermanently(questionUrl);
        }

        if (StringUtils.isNotBlank(question.getHumanUrlTitle())
                && !Objects.equals(question.getHumanUrlTitle(), humanUrlTitle)) {
            UrlResolver urlResolver = new RequestUrlResolver(request);
            String questionUrl = urlResolver.getQuestionUrl(question, request.getQueryString());
            return movedPermanently(questionUrl);
        }

        return null;
    }
}
