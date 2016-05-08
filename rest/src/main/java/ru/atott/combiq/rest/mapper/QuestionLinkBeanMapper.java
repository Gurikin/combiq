package ru.atott.combiq.rest.mapper;

import ru.atott.combiq.rest.bean.QuestionLinkBean;
import ru.atott.combiq.rest.utils.BeanMapper;
import ru.atott.combiq.rest.utils.RestContext;
import ru.atott.combiq.service.bean.Question;
import ru.atott.combiq.service.site.UrlResolver;

public class QuestionLinkBeanMapper implements BeanMapper<Question, QuestionLinkBean> {

    @Override
    public QuestionLinkBean map(RestContext restContext, Question source) {
        QuestionLinkBean bean = new QuestionLinkBean();
        bean.setId(source.getId());
        bean.setTitle(source.getTitle());

        UrlResolver urlResolver = restContext.getUrlResolver();
        String uri = urlResolver.getQuestionUrl(source);
        bean.setUri(urlResolver.externalize(uri));

        return bean;
    }
}
