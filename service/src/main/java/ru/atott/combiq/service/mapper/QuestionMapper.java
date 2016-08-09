package ru.atott.combiq.service.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.atott.combiq.dao.entity.MarkdownContent;
import ru.atott.combiq.dao.entity.QuestionEntity;
import ru.atott.combiq.dao.repository.QuestionRepository;
import ru.atott.combiq.service.bean.Question;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class QuestionMapper implements Mapper<QuestionEntity, Question> {

    public QuestionMapper() { }

    @Override
    public Question map(QuestionEntity source) {
        String questionId = source.getId();

        Question question = new Question();
        question.setId(questionId);
        question.setTitle(source.getTitle());

        List<String> tags = source.getTags() == null ? Collections.emptyList() : source.getTags();
        question.setTags(tags.stream().map(String::toLowerCase).collect(Collectors.toList()));

        question.setLastModify(source.getLastModify());

        question.setLastModify(source.getLastModify());
        question.setLinkedQuestions(source.getLinkedQuestions());
        if (source.getAskedCount() == null) {
            question.setAskedCount(source.getAskedToday());
        } else {
            question.setAskedCount(source.getAskedCount() + source.getAskedToday());
        }
        return question;
    }

}
