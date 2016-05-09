package ru.atott.combiq.service.question;

import com.google.common.collect.Sets;
import org.apache.commons.lang.Validate;
import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import ru.atott.combiq.dao.entity.QuestionComment;
import ru.atott.combiq.dao.entity.QuestionEntity;
import ru.atott.combiq.service.AccessException;
import ru.atott.combiq.service.ServiceException;
import ru.atott.combiq.service.markdown.MarkdownService;
import ru.atott.combiq.service.site.UserContext;
import ru.atott.combiq.service.util.ApplicationContextHolder;
import ru.atott.combiq.service.util.NumberService;
import ru.atott.combiq.service.util.TransletirateService;

import java.util.*;
import java.util.stream.Collectors;

import static ru.atott.combiq.service.user.UserRoles.contenter;
import static ru.atott.combiq.service.user.UserRoles.sa;

public class QuestionEntityBuilder implements QuestionBuilder {

    private UserContext userContext;

    private QuestionEntity questionEntity;

    private boolean created;

    private Set<String> originalLinkedQuestions = Collections.emptySet();

    private List<QuestionComment> addedComments = new ArrayList<>();

    private List<QuestionComment> updatedComments = new ArrayList<>();

    private List<QuestionComment> removedComments = new ArrayList<>();

    private MarkdownService markdownService;

    private QuestionService questionService;

    private TransletirateService transletirateService;

    private NumberService numberService;

    public QuestionEntityBuilder(UserContext userContext, QuestionEntity questionEntity) {
        this.userContext = userContext;
        this.questionEntity = questionEntity;

        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        markdownService = applicationContext.getBean(MarkdownService.class);
        questionService = applicationContext.getBean(QuestionService.class);
        transletirateService = applicationContext.getBean(TransletirateService.class);
        numberService = applicationContext.getBean(NumberService.class);

        if (this.questionEntity == null) {
            this.created = true;
            this.questionEntity = new QuestionEntity();
            this.questionEntity.setId(String.valueOf(numberService.getUniqueNumber()));
            this.questionEntity.setAuthorId(userContext.getUserId());
            this.questionEntity.setAuthorName(userContext.getUserName());
            this.questionEntity.setTimestamp(System.currentTimeMillis());
        }

        if (this.questionEntity.getLinkedQuestions() != null) {
            originalLinkedQuestions = new HashSet<>(this.questionEntity.getLinkedQuestions());
        }
    }

    public QuestionEntityBuilder(UserContext userContext) {
        this(userContext, null);
    }

    public QuestionEntity build() {
        questionEntity.setLastModify(new Date());
        return questionEntity;
    }

    public boolean isCreated() {
        return created;
    }

    public Set<String> getOriginalLinkedQuestions() {
        return originalLinkedQuestions;
    }

    public List<QuestionComment> getAddedComments() {
        return addedComments;
    }

    public List<QuestionComment> getUpdatedComments() {
        return updatedComments;
    }

    @Override
    public QuestionEntityBuilder setTitle(String title) {
        if (!Objects.equals(title, questionEntity.getTitle())) {
            questionEntity.setHumanUrlTitle(transletirateService.lowercaseAndTransletirate(title, 80));
            questionEntity.setTitle(title);
            questionEntity.setClassNames(questionService.getMentionedClassNames(title));
        }
        return this;
    }

    @Override
    public QuestionEntityBuilder setTags(Collection<String> tags) {
        if (tags == null) {
            questionEntity.setTags(null);
        } else {
            questionEntity.setTags(new ArrayList<>(tags));
        }
        return this;
    }

    @Override
    public QuestionEntityBuilder setLevel(int level) {
        questionEntity.setLevel(level);
        return this;
    }

    @Override
    public QuestionEntityBuilder setLevel(String level) {
        questionEntity.setLevel(Integer.parseInt(level.substring(1)));
        return this;
    }

    @Override
    public QuestionEntityBuilder setBody(String content) {
        if (StringUtils.isNotBlank(content)) {
            questionEntity.setBody(markdownService.toMarkdownContent(userContext, content));
        } else {
            questionEntity.setBody(null);
        }
        return this;
    }

    @Override
    public QuestionEntityBuilder setLinkedQuestions(Collection<String> linkedQuestions) {
        if (linkedQuestions != null) {
            questionEntity.setLinkedQuestions(new HashSet<>(linkedQuestions));
        } else {
            questionEntity.setLinkedQuestions(null);
        }
        return null;
    }

    @Override
    public QuestionComment addComment(String commentContent) {
        Validate.isTrue(!userContext.isAnonimous());
        Validate.notEmpty(commentContent);

        List<QuestionComment> comments = questionEntity.getComments();

        if (comments == null) {
            comments = new ArrayList<>();
        } else {
            comments = new ArrayList<>(comments);
        }

        QuestionComment questionComment = new QuestionComment();
        questionComment.setContent(markdownService.toMarkdownContent(userContext, commentContent));
        questionComment.setPostDate(new Date());
        questionComment.setUserId(userContext.getUserId());
        questionComment.setUserName(userContext.getUserName());
        questionComment.setId(UUID.randomUUID().toString());

        comments.add(questionComment);
        addedComments.add(questionComment);

        questionEntity.setComments(comments);

        return questionComment;
    }

    @Override
    public QuestionComment setCommentContent(String commentId, String commentContent) {
        Validate.isTrue(!userContext.isAnonimous());
        Validate.notEmpty(commentContent);

        List<QuestionComment> comments = questionEntity.getComments();

        if (comments == null) {
            comments = Collections.emptyList();
        }

        QuestionComment questionComment = comments.stream()
                .filter(comment -> Objects.equals(comment.getId(), commentId))
                .findFirst().orElse(null);

        if (questionComment == null) {
            throw new ServiceException("Question comment " + commentId + " not found.");
        }

        if (!Objects.equals(questionComment.getUserId(), userContext.getUserId())) {

            if (Sets.intersection(userContext.getRoles(), Sets.newHashSet(sa, contenter)).size() == 0) {
                throw new AccessException();
            }
        }

        questionComment.setContent(markdownService.toMarkdownContent(userContext, commentContent));
        questionComment.setEditDate(new Date());

        if (!Objects.equals(questionComment.getUserId(), userContext.getUserId())) {
            questionComment.setEditUserId(userContext.getUserId());
            questionComment.setEditUserName(userContext.getUserName());
        } else {
            questionComment.setEditUserId(null);
            questionComment.setEditUserName(null);
        }

        updatedComments.add(questionComment);

        return questionComment;
    }

    @Override
    public void removeComment(String commentId) {
        Validate.isTrue(!userContext.isAnonimous());

        QuestionComment removedComment = questionEntity.getComment(commentId);

        if (removedComment == null) {
            return;
        }

        if (Objects.equals(removedComment.getUserId(), userContext.getUserId())
                || userContext.hasAnyRole(sa, contenter)) {

            List<QuestionComment> comments = questionEntity.getComments().stream()
                    .filter(comment -> comment != removedComment)
                    .collect(Collectors.toList());

            questionEntity.setComments(comments);

            removedComments.add(removedComment);
        } else {
            throw new AccessException();
        }
    }
}
