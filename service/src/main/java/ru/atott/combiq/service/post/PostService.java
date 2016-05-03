package ru.atott.combiq.service.post;

import org.springframework.data.domain.Page;
import ru.atott.combiq.service.bean.Post;
import ru.atott.combiq.service.site.UserContext;

public interface PostService {

    Page<Post> getPosts(long page, long size);

    Page<Post> getPublishedPosts(long page, long size);

    Post getPost(String postId);

    Post getPublishedPost(String postId);

    PostBuilder createPostBuilder(UserContext userContext, String postId);

    Post save(UserContext userContext, PostBuilder postBuilder);
}
