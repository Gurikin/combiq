package ru.atott.combiq.service.post;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.atott.combiq.dao.entity.PostEntity;
import ru.atott.combiq.dao.repository.PostRepository;
import ru.atott.combiq.service.bean.Post;
import ru.atott.combiq.service.mapper.PostMapper;
import ru.atott.combiq.service.site.UserContext;

@Service
public class PostServiceImpl implements PostService {

    private PostMapper postMapper = new PostMapper();

    @Autowired
    private PostRepository postRepository;

    @Override
    public Page<Post> getPosts(long page, long size) {
        Pageable pageable = new PageRequest((int) page, (int) size, Sort.Direction.DESC, PostEntity.CREATE_DATE_FIELD);
        Page<PostEntity> result = postRepository.findAll(pageable);
        return result.map(postEntity -> postMapper.map(postEntity));
    }

    @Override
    public Page<Post> getPublishedPosts(long page, long size) {
        Pageable pageable = new PageRequest((int) page, (int) size, Sort.Direction.DESC, PostEntity.CREATE_DATE_FIELD);
        Page<PostEntity> result = postRepository.findByPublished(pageable, true);
        return result.map(postEntity -> postMapper.map(postEntity));
    }

    @Override
    public Post getPost(String postId) {
        return postMapper.safeMap(postRepository.findOne(postId));
    }

    @Override
    public Post getPublishedPost(String postId) {
        Post post = getPost(postId);

        if (post != null && !post.isPublished()) {
            return null;
        }

        return post;
    }

    @Override
    public PostBuilder createPostBuilder(UserContext userContext, String postId) {
        if (postId == null) {
            return new PostEntityBuilder(userContext);
        } else {
            PostEntity postEntity = postRepository.findOne(postId);

            if (postEntity == null) {
                throw new PostNotFoundException(postId);
            }

            return new PostEntityBuilder(userContext, postEntity);
        }
    }

    @Override
    public Post save(UserContext uc, PostBuilder postBuilder) {
        Validate.isTrue(!uc.isAnonimous());

        PostEntity postEntity = ((PostEntityBuilder) postBuilder).build();
        postEntity = postRepository.save(postEntity);
        return postMapper.map(postEntity);
    }
}
