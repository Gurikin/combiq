package ru.atott.combiq.service.mapper;

import ru.atott.combiq.dao.entity.PostEntity;
import ru.atott.combiq.service.bean.Post;

public class PostMapper implements Mapper<PostEntity, Post> {

    @Override
    public Post map(PostEntity source) {
        Post bean = new Post();
        bean.setId(source.getId());
        bean.setCreateDate(source.getCreateDate());
        bean.setPublished(source.isPublished());
        bean.setAuthorUserId(source.getAuthorUserId());
        bean.setAuthorUserName(source.getAuthorUserName());
        bean.setTitle(source.getTitle());
        bean.setContent(source.getContent());
        bean.setPreview(source.getPreview());
        bean.setHumanUrlTitle(source.getHumanUrlTitle());
        return bean;
    }
}
