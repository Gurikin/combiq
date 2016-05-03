package ru.atott.combiq.rest.mapper;

import ru.atott.combiq.rest.bean.MarkdownContentBean;
import ru.atott.combiq.rest.bean.PostBean;
import ru.atott.combiq.rest.bean.UserBean;
import ru.atott.combiq.rest.utils.BeanMapper;
import ru.atott.combiq.rest.utils.RestContext;
import ru.atott.combiq.service.bean.Post;

public class PostBeanMapper implements BeanMapper<Post, PostBean> {

    @Override
    public PostBean map(RestContext restContext, Post source) {
        PostBean bean = new PostBean();
        bean.setId(source.getId());
        bean.setCreateDate(source.getCreateDate());

        UserBean author = new UserBean();
        author.setId(source.getAuthorUserId());
        author.setName(source.getAuthorUserName());
        String userUrl = restContext.getUrlResolver().getUserUrl(source.getAuthorUserId());
        author.setUri(restContext.getUrlResolver().externalize(userUrl));
        bean.setAuthor(author);

        bean.setPublished(source.isPublished());
        bean.setPreview(new MarkdownContentBean(source.getPreview()));
        bean.setContent(new MarkdownContentBean(source.getContent()));
        bean.setTitle(source.getTitle());

        return bean;
    }
}
