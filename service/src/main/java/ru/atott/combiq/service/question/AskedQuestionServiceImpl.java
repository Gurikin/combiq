package ru.atott.combiq.service.question;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.atott.combiq.dao.entity.QuestionEntity;
import ru.atott.combiq.dao.entity.UserEntity;
import ru.atott.combiq.dao.repository.QuestionRepository;
import ru.atott.combiq.dao.repository.UserRepository;
import ru.atott.combiq.service.site.EventService;
import ru.atott.combiq.service.site.UserContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AskedQuestionServiceImpl implements AskedQuestionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private EventService eventService;

    @Override
    public void addAskedCount(UserContext uc, String questionId) {
        Validate.isTrue(!uc.isAnonimous());
        Validate.notNull(questionId);

        UserEntity user = userRepository.findOne(uc.getUserId());
        Set<String> questionsAskedSet = user.getAskedQuestions();
        if (questionsAskedSet == null) {
            questionsAskedSet = new HashSet<>();
        }
        questionsAskedSet = new HashSet<>(questionsAskedSet);
        if (!questionsAskedSet.contains(questionId)) {
            questionsAskedSet.add(questionId);
            user.setAskedQuestions(questionsAskedSet);
            userRepository.save(user);

            QuestionEntity questionEntity = questionRepository.findOne(questionId);
            questionEntity.setAskedToday(questionEntity.getAskedToday() + 1);
            questionRepository.save(questionEntity);
        }
    }

    @Override
    public boolean isAskedQuestion(UserContext uc, String questionId) {
        if (uc.isAnonimous()) {
            return false;
        }

        if (StringUtils.isBlank(questionId)) {
            return false;
        }

        UserEntity user = userRepository.findOne(uc.getUserId());
        Set<String> questionsAskedSet = user.getAskedQuestions();

        if (CollectionUtils.isEmpty(questionsAskedSet)) {
            return false;
        }

        return questionsAskedSet.contains(questionId);
    }

    @Override
    public void recountAskedCounts(){
        List<QuestionEntity> questionEntities = questionRepository.findByAskedTodayGreaterThan(1);
        questionEntities.forEach(questionEntity -> {
            int askedCount = 0;

            if (questionEntity.getAskedCount() != null) {
                askedCount = questionEntity.getAskedCount();
            }

            askedCount = askedCount + questionEntity.getAskedToday();

            questionEntity.setAskedCount(askedCount);
            questionEntity.setAskedToday(0);
        });
        if (questionEntities.size() > 0) {
            questionRepository.save(questionEntities);
        }
        eventService.recountQuestions();
    }
}
