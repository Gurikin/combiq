package ru.atott.combiq.rest.mapper;

import ru.atott.combiq.dao.entity.QuestionComment;
import ru.atott.combiq.rest.bean.CommentBean;
import ru.atott.combiq.rest.bean.MarkdownContentBean;
import ru.atott.combiq.rest.bean.UserBean;
import ru.atott.combiq.rest.utils.BeanMapper;
import ru.atott.combiq.rest.utils.RestContext;
import ru.atott.combiq.service.bean.User;
import ru.atott.combiq.service.user.UserService;

public class CommentBeanMapper implements BeanMapper<QuestionComment, CommentBean> {

    @Override
    public CommentBean map(RestContext context, QuestionComment source) {
        CommentBean bean = new CommentBean();
        bean.setContent(new MarkdownContentBean(source.getContent()));
        bean.setId(source.getId());
        bean.setEditDate(source.getEditDate());
        bean.setPostDate(source.getPostDate());

        UserService userService = context.getApplicationContext().getBean(UserService.class);
        User user;
        if (source.getUserId() != null) {
            String uri = context.getUrlResolver().getUserUrl(source.getUserId());
            uri = context.getUrlResolver().externalize(uri);
            user = userService.findById(source.getUserId());
            bean.setAuthor(new UserBean(source.getId(), user.getName(), uri, user.getAvatarUrl()));
        }

        if (source.getEditUserId() != null) {
            String uri = context.getUrlResolver().getUserUrl(source.getEditUserId());
            user = userService.findById(source.getUserId());
            bean.setEditedBy(new UserBean(source.getId(), user.getName(), uri, user.getAvatarUrl()));
        }

        return bean;
    }
}
