package ru.atott.combiq.rest.v1.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.atott.combiq.dao.entity.QuestionComment;
import ru.atott.combiq.dao.entity.UserEntity;
import ru.atott.combiq.dao.repository.UserRepository;
import ru.atott.combiq.rest.bean.CommentBean;
import ru.atott.combiq.rest.mapper.CommentBeanMapper;
import ru.atott.combiq.rest.request.CommentRequest;
import ru.atott.combiq.rest.v1.BaseRestController;
import ru.atott.combiq.service.question.QuestionService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CommentRestController extends BaseRestController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Создать комментарий к вопросу.
     *
     * @request.body.example
     *      {@link ru.atott.combiq.rest.request.CommentRequest#EXAMPLE}
     *
     * @param questionId
     *      Идентификатор вопроса
     *
     * @response.200.doc
     *      В случае успеха возврашает созданный коментарий
     *
     * @response.200.example
     *      {@link ru.atott.combiq.rest.bean.CommentBean#EXAMPLE}
     */

    @RequestMapping(value = "/rest/v1/question/{questionId}/comment", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('user')")
    public Object createComment(@PathVariable("questionId") String questionId,
                                @RequestBody CommentRequest request) {
        questionService.saveComment(getContext().getUc(), questionId, request.getContent());
        CommentBeanMapper mapper = new CommentBeanMapper();
        return mapper.map(getContext(), questionService.getQuestion(questionId).getLastComment());
    }

    /**
     * Обновить комментарий к вопросу
     *
     * @request.body.example
     *      {@link ru.atott.combiq.rest.request.CommentRequest#EXAMPLE}
     *
     * @param questionId
     *      Идентификатор вопроса
     *
     * @param commentId
     *      Идентификатор комментария
     *
     * @response.200.doc
     *      В случае успеха возврашает измененный коментарий
     *
     * @response.200.example
     *      {@link ru.atott.combiq.rest.bean.CommentBean#EXAMPLE}
     */

    @RequestMapping(value = "/rest/v1/question/{questionId}/comment/{commentId}", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('user')")
    public Object udpateComment(@PathVariable("questionId") String questionId,
                                @PathVariable("commentId") String commentId,
                                @RequestBody CommentRequest request) {
        questionService.updateComment(getContext().getUc(), questionId, commentId, request.getContent());
        return getComment(questionId, commentId);
    }


    /**
     * Удалить комментарий к вопросу
     *
     * @param questionId
     *      Идентификатор вопроса
     *
     * @param commentId
     *      Идентификатор коментария
     *
     * @response.200.doc
     *      В случае успеха
     */

    @RequestMapping(value = "/rest/v1/question/{questionId}/comment/{commentId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAnyRole('user')")
    public Object deleteComment(@PathVariable("questionId") String questionId,
                                @PathVariable("commentId") String commentId) {
        questionService.deleteComment(getContext().getUc(), questionId, commentId);
        return responseOk();
    }


    /**
     * Вернуть комментарии к вопросу
     *
     * @param questionId
     *      Идентификатор вопроса
     *
     * @response.200.doc
     *      В случае успеха все коментарии к вопросу в виде списка
     *
     * @response.200.example
     *      {@link ru.atott.combiq.rest.bean.CommentBean#EXAMPLE_LIST}
     */
    @RequestMapping(value = "/rest/v1/question/{questionId}/comment", method = RequestMethod.GET)
    public Object getComments(@PathVariable("questionId") String questionId) {
        List<QuestionComment> comments = questionService.getQuestion(questionId).getComments();
        CommentBeanMapper mapper = new CommentBeanMapper();
        List<CommentBean> bean = mapper.toList(getContext(), comments);
        Map<String, String> userMap = new HashMap<>();
        Iterator<UserEntity> iterator
                = (Iterator<UserEntity>) userRepository.findAll(comments.stream()
                .map(x -> x.getUserId()).distinct().collect(Collectors.toList()));
        iterator.forEachRemaining(x -> userMap.put(x.getId(), x.getAvatarUrl()));
        bean.iterator()
                .forEachRemaining(x -> x.getAuthor()
                        .setAvatarUrl(userMap.get(x.getAuthor().getId())));
        return bean;
    }

    /**
     * Вернуть комментарий к вопросу
     *
     * @param questionId
     *      Идентификатор вопроса
     *
     * @param commentId
     *      Идентификатор коментария
     *
     * @response.200.doc
     *      В случае успеха коментарий
     *
     * @response.200.example
     *      {@link ru.atott.combiq.rest.bean.CommentBean#EXAMPLE}
     */
    @RequestMapping(value = "/rest/v1/question/{questionId}/comment/{commentId}", method = RequestMethod.GET)
    public Object getComment(@PathVariable("questionId") String questionId,
                             @PathVariable("commentId") String commentId) {

        List<QuestionComment> comments = questionService.getQuestion(questionId).getComments();

        QuestionComment questionComment = comments.stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findFirst()
                .orElse(null);

        if (questionComment == null) {
            return responseNotFound();
        }

        CommentBeanMapper mapper = new CommentBeanMapper();
        return mapper.map(getContext(), questionComment);
    }
}
