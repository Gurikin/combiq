package ru.atott.combiq.rest.mapper;

import ru.atott.combiq.dao.entity.QuestionComment;
import ru.atott.combiq.rest.bean.CommentBean;
import ru.atott.combiq.rest.bean.MarkdownContentBean;
import ru.atott.combiq.rest.utils.BeanMapper;
import ru.atott.combiq.rest.utils.RestContext;
import ru.atott.combiq.service.user.UserService;

public class CommentBeanMapper implements BeanMapper<QuestionComment, CommentBean> {

    private UserService userService;

    private UserBeanMapper userBeanMapper = new UserBeanMapper();

    @Override
    public CommentBean map(RestContext context, QuestionComment source) {
        if (userService == null) {
            userService = context.getApplicationContext().getBean(UserService.class);
        }

        CommentBean bean = new CommentBean();
        bean.setContent(new MarkdownContentBean(source.getContent()));
        bean.setId(source.getId());
        bean.setEditDate(source.getEditDate());
        bean.setPostDate(source.getPostDate());

        if (source.getUserId() != null) {
            bean.setAuthor(userBeanMapper.safeMap(context, userService.findById(source.getUserId())));
        }

        if (source.getEditUserId() != null) {
            bean.setEditedBy(userBeanMapper.safeMap(context, userService.findById(source.getUserId())));
        }

        return bean;
    }
}
