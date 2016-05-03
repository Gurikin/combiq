package ru.atott.combiq.rest.mapper;

import org.apache.commons.collections.CollectionUtils;
import ru.atott.combiq.dao.entity.MarkdownContent;
import ru.atott.combiq.rest.bean.MarkdownContentBean;
import ru.atott.combiq.rest.bean.QuestionBean;
import ru.atott.combiq.rest.utils.BeanMapper;
import ru.atott.combiq.rest.utils.RestContext;
import ru.atott.combiq.service.bean.Question;

import java.util.Collections;
import java.util.stream.Collectors;

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
        bean.setLinkedQuestions(source.getLinkedQuestions());
        if (!MarkdownContent.isEmpty(source.getBody())) {
            bean.setBody(new MarkdownContentBean(source.getBody()));
        }

        return bean;
    }
}
