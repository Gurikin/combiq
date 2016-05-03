package ru.atott.combiq.service.question;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.atott.combiq.dao.entity.QuestionComment;
import ru.atott.combiq.dao.entity.QuestionEntity;
import ru.atott.combiq.dao.repository.Jdk8ClassRepository;
import ru.atott.combiq.dao.repository.QuestionRepository;
import ru.atott.combiq.service.AccessException;
import ru.atott.combiq.service.CombiqConstants;
import ru.atott.combiq.service.ServiceException;
import ru.atott.combiq.service.bean.Question;
import ru.atott.combiq.service.mapper.QuestionMapper;
import ru.atott.combiq.service.markdown.MarkdownService;
import ru.atott.combiq.service.site.EventService;
import ru.atott.combiq.service.site.UserContext;
import ru.atott.combiq.service.util.NumberService;
import ru.atott.combiq.service.util.TransletirateService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.atott.combiq.service.user.UserRoles.contenter;
import static ru.atott.combiq.service.user.UserRoles.sa;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static String[] alphabet = 
            {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};

    @Autowired
    private MarkdownService markdownService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private Jdk8ClassRepository jdk8ClassRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private NumberService numberService;

    @Autowired
    TransletirateService transletirateService;

    @Override
    public void saveComment(UserContext uc, String questionId, String comment) {
        Validate.isTrue(!uc.isAnonimous());
        Validate.notEmpty(comment);

        QuestionEntity questionEntity = questionRepository.findOne(questionId);
        List<QuestionComment> comments = questionEntity.getComments();

        if (comments == null) {
            comments = new ArrayList<>();
        } else {
            comments = new ArrayList<>(comments);
        }

        QuestionComment questionComment = new QuestionComment();
        questionComment.setContent(markdownService.toMarkdownContent(uc, comment));
        questionComment.setPostDate(new Date());
        questionComment.setUserId(uc.getUserId());
        questionComment.setId(UUID.randomUUID().toString());

        if (uc.getRoles().contains(sa)) {
            questionComment.setUserName(CombiqConstants.combiqUserName);
        } else {
            questionComment.setUserName(uc.getUserName());
        }
        comments.add(questionComment);

        questionEntity.setComments(comments);
        questionRepository.save(questionEntity);

        eventService.createPostQuestionCommentEvent(uc, questionEntity, questionComment.getId());
    }

    @Override
    public void updateComment(UserContext uc, String questionId, String commentId, String commentMarkdown) {
        Validate.isTrue(!uc.isAnonimous());
        Validate.notEmpty(commentMarkdown);

        QuestionEntity questionEntity = questionRepository.findOne(questionId);
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

        if (!Objects.equals(questionComment.getUserId(), uc.getUserId())) {

            if (Sets.intersection(uc.getRoles(), Sets.newHashSet(sa, contenter)).size() == 0) {
                throw new AccessException();
            }
        }

        questionComment.setContent(markdownService.toMarkdownContent(uc, commentMarkdown));
        questionComment.setEditDate(new Date());

        if (!Objects.equals(questionComment.getUserId(), uc.getUserId())) {
            questionComment.setEditUserId(uc.getUserId());
            questionComment.setEditUserName(uc.getUserName());
        } else {
            questionComment.setEditUserId(null);
            questionComment.setEditUserName(null);
        }

        questionRepository.save(questionEntity);

        eventService.createEditQuestionCommentEvent(uc, questionEntity, commentId);
    }

    @Override
    public void saveQuestionBody(UserContext uc, String questionId, String body) {
        QuestionEntity questionEntity = questionRepository.findOne(questionId);
        questionEntity.setBody(markdownService.toMarkdownContent(uc, body));
        questionRepository.save(questionEntity);
    }

    @Override
    public void saveQuestion(UserContext uc, Question question){
        Validate.isTrue(!uc.isAnonimous());

        QuestionEntity questionEntity;

        if (question.getId() == null){
            questionEntity = new QuestionEntity();
            questionEntity.setTimestamp(new Date().getTime());
            questionEntity.setId(Long.toString(numberService.getUniqueNumber()));
            questionEntity.setTitle(question.getTitle());
            questionEntity.setAuthorId(uc.getUserId());
            questionEntity.setAuthorName(uc.getUserName());
            question.setLinkedQuestions(question.getLinkedQuestions());
            eventService.createQuestion(uc, questionEntity);
        } else {
            questionEntity = questionRepository.findOne(question.getId());
            questionEntity.setClassNames(null);
            questionEntity.setTitle(question.getTitle());
            eventService.editQuestion(uc, questionEntity);
            Set<String> previusLinked = questionEntity.getLinkedQuestions();
            if(previusLinked !=null ) {
                previusLinked.removeAll(question.getLinkedQuestions());
                final String id = questionEntity.getId();
                previusLinked.forEach(x -> unLinkQuestion(id, x));
            }
        }
        questionEntity.setLastModify(question.getLastModify());
        questionEntity.setHumanUrlTitle(transletirateService.lowercaseAndTransletirate(question.getTitle(), 80));
        questionEntity.setTags(question.getTags());
        questionEntity.setLevel(Integer.parseInt(question.getLevel().substring(1)));
        questionEntity.setBody(question.getBody());
        questionEntity.setLinkedQuestions(question.getLinkedQuestions());
        questionEntity = questionRepository.save(questionEntity);
        question.setId(questionEntity.getId());
        Set<String> linked = questionEntity.getLinkedQuestions();
        if(linked != null) {
            linked.forEach(x-> linkQuestion(question.getId(), x));
        }
    }

    @Override
    public List<String> refreshMentionedClassNames(Question question) {
        String text = question.getTitle();

        List<String> classNames = Arrays.asList(StringUtils.split(text, " ")).stream()
                .filter(StringUtils::isNotBlank)
                .filter(s -> StringUtils.startsWithAny(s.toLowerCase(), alphabet))
                .map(s -> s.replaceAll("[^A-z.]", ""))
                .map(s -> StringUtils.removeEnd(s, "."))
                .distinct()
                .collect(Collectors.toList());

        List<String> actualClassNames = Collections.emptyList();

        if (classNames.size() != 0) {
            actualClassNames = StreamSupport.stream(jdk8ClassRepository.findAll(classNames).spliterator(), false)
                    .filter(entity -> entity != null)
                    .flatMap(entity -> entity.getClassNames().stream())
                    .distinct()
                    .collect(Collectors.toList());
        }

        question.setClassNames(actualClassNames);

        QuestionEntity questionEntity = questionRepository.findOne(question.getId());
        questionEntity.setClassNames(actualClassNames);
        questionRepository.save(questionEntity);

        return actualClassNames;
    }

    @Override
    public void deleteQuestion(UserContext uc, String questionId){
        QuestionEntity questionEntity = questionRepository.findOne(questionId);
        questionEntity.setDeleted(true);
        questionRepository.save(questionEntity);
        eventService.deleteQuestion(uc, questionEntity);
    }

    @Override
    public void restoreQuestion(UserContext uc, String questionId){
        QuestionEntity questionEntity = questionRepository.findOne(questionId);
        questionEntity.setDeleted(false);
        questionRepository.save(questionEntity);
        eventService.restoreQuestion(uc, questionEntity);
    }

    @Override
    public  Question getQuestion(String id){
        QuestionEntity questionEntity = questionRepository.findOne(id);
        QuestionMapper questionMapper = new QuestionMapper();
        return questionMapper.safeMap(questionEntity);
    }

    @Override
    public void deleteComment(UserContext uc, String questionId, String commentId) {
        Validate.isTrue(!uc.isAnonimous());

        QuestionEntity questionEntity = questionRepository.findOne(questionId);

        if (CollectionUtils.isEmpty(questionEntity.getComments())) {
            return;
        }

        List<QuestionComment> comments = questionEntity.getComments().stream()
                .filter(comment -> {
                    if (!Objects.equals(comment.getId(), commentId)) {
                        return true;
                    }

                    return !(Objects.equals(comment.getUserId(), uc.getUserId()) || uc.hasAnyRole(sa, contenter));
                })
                .collect(Collectors.toList());
        questionEntity.setComments(comments);
        questionRepository.save(questionEntity);
    }

    private boolean linkQuestion(String linkSource, String linkTarget) {
        QuestionEntity questionEntity = questionRepository.findOne(linkTarget);
        if(questionEntity == null){
            return false;
        }

        if(questionEntity.getLinkedQuestions() == null) {
            questionEntity.setLinkedQuestions(new HashSet<>());
        }
        questionEntity.getLinkedQuestions().add(linkSource);
        questionRepository.save(questionEntity);
        return true;
    }

    private void unLinkQuestion(String linkSource, String linkTarget) {
        QuestionEntity questionEntity = questionRepository.findOne(linkTarget);
        if(questionEntity == null || questionEntity.getLinkedQuestions() == null){
            return;
        }
        questionEntity.getLinkedQuestions().remove(linkSource);
        questionRepository.save(questionEntity);
    }

    @Override
    public Set<Question> getLinkedQuestion(UserContext uc, Set<String> id) {
        Set<Question> linkedQuestions = new HashSet<>();
        QuestionMapper questionMapper = new QuestionMapper();
        if(id == null || id.isEmpty()) {
            return null;
        }
        questionRepository.findAll(id)
                .forEach(x -> linkedQuestions.add(questionMapper.map(x)));
        return linkedQuestions;
    }
}
