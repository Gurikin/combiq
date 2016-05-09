package ru.atott.combiq.service.question;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.atott.combiq.dao.entity.QuestionComment;
import ru.atott.combiq.dao.entity.QuestionEntity;
import ru.atott.combiq.dao.repository.Jdk8ClassRepository;
import ru.atott.combiq.dao.repository.QuestionRepository;
import ru.atott.combiq.service.bean.Question;
import ru.atott.combiq.service.mapper.QuestionMapper;
import ru.atott.combiq.service.site.EventService;
import ru.atott.combiq.service.site.UserContext;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static String[] alphabet = 
            {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private Jdk8ClassRepository jdk8ClassRepository;

    @Autowired
    private EventService eventService;

    @Override
    public List<String> refreshMentionedClassNames(Question question) {
        List<String> mentionedClassNames = getMentionedClassNames(question.getTitle());

        question.setClassNames(mentionedClassNames);

        QuestionEntity questionEntity = questionRepository.findOne(question.getId());
        questionEntity.setClassNames(mentionedClassNames);
        questionRepository.save(questionEntity);

        return mentionedClassNames;
    }

    @Override
    public List<String> getMentionedClassNames(String title) {

        List<String> classNames = Arrays.asList(StringUtils.split(title, " ")).stream()
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

        return actualClassNames;
    }

    @Override
    public QuestionBuilder createQuestionBuilder(UserContext userContext, String questionId) {
        QuestionEntityBuilder builder;

        if (questionId == null) {
            builder = new QuestionEntityBuilder(userContext);
        } else {
            QuestionEntity questionEntity = questionRepository.findOne(questionId);

            if (questionEntity == null) {
                throw new QuestionNotFoundException("Question " + questionId + "is not found.");
            }

            builder = new QuestionEntityBuilder(userContext, questionEntity);
        }

        return builder;
    }

    @Override
    public Question saveQuestion(UserContext userContext, QuestionBuilder questionBuilder) {
        QuestionEntityBuilder questionEntityBuilder = (QuestionEntityBuilder) questionBuilder;
        QuestionEntity questionEntity = questionEntityBuilder.build();
        questionEntity = questionRepository.save(questionEntity);
        String questionEntityId = questionEntity.getId();

        Set<String> originalLinkedQuestions = questionEntityBuilder.getOriginalLinkedQuestions();
        Set<String> linkedQuestions = questionEntity.getLinkedQuestions();
        if (linkedQuestions == null) {
            linkedQuestions = Collections.emptySet();
        }

        Sets
                .difference(originalLinkedQuestions, linkedQuestions)
                .forEach(previousQuestionId -> unlinkQuestion(previousQuestionId, questionEntityId));

        Sets
                .difference(linkedQuestions, originalLinkedQuestions)
                .forEach(linkedQuestionId -> linkQuestion(linkedQuestionId, questionEntityId));


        for (QuestionComment addedComment : questionEntityBuilder.getAddedComments()) {
            eventService.createPostQuestionCommentEvent(userContext, questionEntity, addedComment.getId());
        }

        for (QuestionComment updatedComment : questionEntityBuilder.getUpdatedComments()) {
            eventService.createEditQuestionCommentEvent(userContext, questionEntity, updatedComment.getId());
        }

        if (questionEntityBuilder.isCreated()) {
            eventService.createQuestion(userContext, questionEntity);
        } else {
            eventService.editQuestion(userContext, questionEntity);
        }

        QuestionMapper questionMapper = new QuestionMapper();
        return questionMapper.map(questionEntity);
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

    private boolean linkQuestion(String questionId, String linkedQuestionId) {
        QuestionEntity questionEntity = questionRepository.findOne(questionId);

        if (questionEntity == null) {
            return false;
        }

        Set<String> linkedQuestions = new HashSet<>();

        if (!CollectionUtils.isEmpty(questionEntity.getLinkedQuestions())) {
            linkedQuestions.addAll(questionEntity.getLinkedQuestions());
        }

        linkedQuestions.add(linkedQuestionId);
        questionEntity.setLinkedQuestions(linkedQuestions);

        questionRepository.save(questionEntity);

        return true;
    }

    private void unlinkQuestion(String questionId, String unlinkedQuestionId) {
        QuestionEntity questionEntity = questionRepository.findOne(questionId);

        if (CollectionUtils.isEmpty(questionEntity.getLinkedQuestions())){
            return;
        }

        Set<String> linkedQuestions = questionEntity.getLinkedQuestions();
        linkedQuestions.remove(unlinkedQuestionId);
        questionEntity.setLinkedQuestions(linkedQuestions);

        questionRepository.save(questionEntity);
    }

    @Override
    public List<Question> getQuestions(Set<String> questionIds) {

        if (CollectionUtils.isEmpty(questionIds)) {
            return Collections.emptyList();
        }

        QuestionMapper questionMapper = new QuestionMapper();
        Iterable<QuestionEntity> questions = questionRepository.findAll(questionIds);
        return questionMapper.toList(questions);
    }
}
