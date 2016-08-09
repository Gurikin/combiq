package ru.atott.combiq.rest.mapper;

import org.apache.commons.collections.CollectionUtils;
import ru.atott.combiq.dao.entity.MarkdownContent;
import ru.atott.combiq.dao.entity.QuestionEntity;
import ru.atott.combiq.rest.bean.MarkdownContentBean;
import ru.atott.combiq.rest.bean.QuestionBean;
import ru.atott.combiq.rest.utils.BeanMapper;
import ru.atott.combiq.rest.utils.RestContext;
import ru.atott.combiq.service.bean.Question;
import ru.atott.combiq.service.question.QuestionService;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class QuestionBeanMapper implements BeanMapper<Question, QuestionBean> {

    @Override
    public QuestionBean map(RestContext restContext, Question source) {
        QuestionBean bean = new QuestionBean();
        bean.setId(source.getId());

        if (CollectionUtils.isEmpty(source.getTags())) {
            bean.setTags(Collections.emptyList());
        } else {
            bean.setTags(source.getTags());
        }

        bean.setTitle(source.getTitle());
        bean.setUri(restContext.getUrlResolver().externalize(restContext.getUrlResolver().getQuestionUrl(source)));
        bean.setChangeDate(source.getLastModify());
        bean.setCommentsCount(source.getComments() != null ? source.getComments().size() : 0);
        bean.setLevel(source.getLevel());

        if (!CollectionUtils.isEmpty(source.getLinkedQuestions())) {
            QuestionService questionService = restContext.getApplicationContext().getBean(QuestionService.class);
            List<Question> linkedQuestions = questionService.getQuestions(source.getLinkedQuestions());
            QuestionLinkBeanMapper questionLinkBeanMapper = new QuestionLinkBeanMapper();
            bean.setLinkedQuestions(questionLinkBeanMapper.toList(restContext, linkedQuestions));
        }

        if (!MarkdownContent.isEmpty(source.getBody())) {
            bean.setBody(new MarkdownContentBean(source.getBody()));
        }

        return bean;
    }

    public QuestionBean requestMap(RestContext restContext, Question source, List<String> requestedField) {
        QuestionBean questionBeanSource = map(restContext, source);
        QuestionBean questionBean = new QuestionBean();

        Field[] filds = Question.class.getDeclaredFields();
        for (Field field:filds){
            if (requestedField.contains(field.getName())){
                try {
                    field.set(questionBean, field.get(questionBeanSource));
                }
                catch (IllegalAccessException e){
                    e.printStackTrace();
                }
            }
        }

        return questionBean;
    }
}
