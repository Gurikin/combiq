package ru.atott.combiq.rest.v1.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.atott.combiq.rest.mapper.PostBeanMapper;
import ru.atott.combiq.rest.request.PostRequest;
import ru.atott.combiq.rest.utils.RestContext;
import ru.atott.combiq.rest.v1.BaseRestController;
import ru.atott.combiq.service.bean.Post;
import ru.atott.combiq.service.post.PostBuilder;
import ru.atott.combiq.service.post.PostNotFoundException;
import ru.atott.combiq.service.post.PostService;

import javax.validation.Valid;

@RestController
public class PostRestController extends BaseRestController {

    @Autowired
    private PostService postService;

    /**
     * Вернуть опубликованные статьи.
     *
     * @param page
     *      Номер страницы (zero-based).
     *
     * @param pageSize
     *      Размер возвращаемой страницы.
     *
     * @response.200.doc
     *      Найденные вопросы.
     *
     * @response.200.example
     *      {@link ru.atott.combiq.rest.bean.PostBean#EXAMPLE_LIST}
     */
    @RequestMapping(value = "/rest/v1/post", method = RequestMethod.GET)
    @ResponseBody
    public Object search(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {

        Page<Post> publishedPosts = postService.getPublishedPosts(page, pageSize);
        PostBeanMapper postBeanMapper = new PostBeanMapper();
        RestContext context = getContext();
        return postBeanMapper.toList(context, publishedPosts);
    }

    /**
     * Вернуть статью по заданному идентификатору postId.
     *
     * @param postId
     *      Идентификатор статьи.
     *
     * @response.200.doc
     *      Статья по заданному идентификатору.
     *
     * @response.200.example
     *      {@link ru.atott.combiq.rest.bean.PostBean#EXAMPLE}
     *
     * @response.404.doc
     *      В случае если статья не найдена.
     */
    @RequestMapping(value = "/rest/v1/post/{postId}", method = RequestMethod.GET)
    @ResponseBody
    public Object get(
            @PathVariable("postId") String postId) {

        Post post = postService.getPost(postId);

        if (post == null) {
            return responseNotFound();
        }

        PostBeanMapper postBeanMapper = new PostBeanMapper();
        return postBeanMapper.map(getContext(), post);
    }

    /**
     * Создать статью.
     *
     * @request.body.example
     *      {@link ru.atott.combiq.rest.request.PostRequest#EXAMPLE}
     *
     * @response.200.doc
     *      В случае успеха, созданная статья.
     *
     * @response.200.example
     *      {@link ru.atott.combiq.rest.bean.PostBean#EXAMPLE}
     */
    @RequestMapping(value = "/rest/v1/post", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAnyRole('sa','contenter')")
    public Object createPost(
            @Valid @RequestBody PostRequest request) {

        return createOrUpdatePost(null, request);
    }

    /**
     * Обновить статью по заданному идентификатору.
     *
     * @request.body.example
     *      {@link ru.atott.combiq.rest.request.PostRequest#EXAMPLE}
     *
     * @param postId
     *      Идентификатор обновляемой статьи.
     *
     * @response.200.doc
     *      В случае успеха, обновленная статья.
     *
     * @response.200.example
     *      {@link ru.atott.combiq.rest.bean.PostBean#EXAMPLE}
     */
    @RequestMapping(value = "/rest/v1/post/{postId}", method = RequestMethod.PUT)
    @ResponseBody
    @PreAuthorize("hasAnyRole('sa','contenter')")
    public Object updatePost(
            @PathVariable("postId") String postId,
            @Valid @RequestBody PostRequest request) {

        return createOrUpdatePost(postId, request);
    }

    private Object createOrUpdatePost(String postId, PostRequest request) {
        try {
            RestContext context = getContext();

            PostBuilder postBuilder = postService
                    .createPostBuilder(getContext().getUc(), postId)
                    .setContent(request.getContent())
                    .setPreview(request.getPreview())
                    .setTitle(request.getTitle())
                    .setPublished(request.isPublished());

            Post post = postService.save(context.getUc(), postBuilder);

            PostBeanMapper postBeanMapper = new PostBeanMapper();
            return postBeanMapper.map(context, post);
        } catch (PostNotFoundException ex) {
            return responseNotFound();
        }
    }
}
