package ru.atott.combiq.rest.mapper;

import ru.atott.combiq.rest.bean.TagBean;
import ru.atott.combiq.rest.utils.BeanMapper;
import ru.atott.combiq.rest.utils.RestContext;
import ru.atott.combiq.service.bean.DetailedQuestionTag;

public class TagBeanMapper implements BeanMapper<DetailedQuestionTag, TagBean> {

    @Override
    public TagBean map(RestContext restContext, DetailedQuestionTag source) {
        TagBean bean = new TagBean();
        bean.setTag(source.getValue());
        bean.setCount(source.getDocCount());
        if (source.getDetails() != null) {
            bean.setDescription(source.getDetails().getDescription());
        }
        return bean;
    }
}
