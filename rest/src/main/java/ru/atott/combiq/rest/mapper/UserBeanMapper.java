package ru.atott.combiq.rest.mapper;

import ru.atott.combiq.rest.bean.UserBean;
import ru.atott.combiq.rest.utils.BeanMapper;
import ru.atott.combiq.rest.utils.RestContext;
import ru.atott.combiq.service.bean.User;

public class UserBeanMapper implements BeanMapper<User, UserBean> {

    @Override
    public UserBean map(RestContext restContext, User source) {
        UserBean bean = new UserBean();
        bean.setId(source.getId());
        bean.setName(source.getName());
        bean.setAvatarUri(source.getAvatarUrl());
        bean.setUri(restContext.getUrlResolver().externalize(restContext.getUrlResolver().getUserUrl(source.getId())));
        return bean;
    }
}
