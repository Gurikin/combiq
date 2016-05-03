package ru.atott.combiq.service.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.atott.combiq.dao.entity.MarkdownContent;
import ru.atott.combiq.dao.entity.QuestionEntity;
import ru.atott.combiq.dao.repository.QuestionRepository;
import ru.atott.combiq.service.bean.Question;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionMapper implements Mapper<QuestionEntity, Question> {

    public QuestionMapper() { }

    @Autowired
    private QuestionRepository repository;

    @Override
    public Question map(QuestionEntity source) {
        String questionId = source.getId();

        Question question = new Question();
        question.setId(questionId);
        question.setTitle(source.getTitle());

        List<String> tags = source.getTags() == null ? Collections.emptyList() : source.getTags();
        question.setTags(tags.stream().map(String::toLowerCase).collect(Collectors.toList()));

        question.setLevel("D" + source.getLevel());
        if (source.getReputation() == null) {
            question.setReputation(0);
        } else {
            question.setReputation(source.getReputation());
        }
        question.setTip(source.getTip());
        if (source.getBody() != null) {
            question.setBody(source.getBody());
        } else {
            question.setBody(new MarkdownContent());
        }
        question.setComments(source.getComments());
        if (question.getComments() == null) {
            question.setComments(Collections.emptyList());
        }
        question.setDeleted(source.isDeleted());
        question.setAuthorId(source.getAuthorId());
        question.setAuthorName(source.getAuthorName());
        question.setLanding(source.isLanding());
        question.setClassNames(source.getClassNames());
        question.setHumanUrlTitle(source.getHumanUrlTitle());
        question.setStars(source.getStars());
        question.setLastModify(source.getLastModify());
        if(source.getLinkedQuestions() != null && !source.getLinkedQuestions().isEmpty()) {
            Set<Question> linked = new HashSet<>();
            repository.findAll(source.getLinkedQuestions())
                    .forEach(x -> {
                        Question y = new Question();
                        y.setHumanUrlTitle(x.getHumanUrlTitle());
                        y.setTitle(x.getTitle());
                        y.setId(x.getId());
                        linked.add(y);
                    });
            question.setLinkedQuestions(linked);
        }
        if (source.getAskedCount() == null) {
            question.setAskedCount(source.getAskedToday());
        } else {
            question.setAskedCount(source.getAskedCount() + source.getAskedToday());
        }
        return question;
    }
}
