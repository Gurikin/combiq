package ru.atott.combiq.web.controller.post;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import ru.atott.combiq.service.bean.Post;
import ru.atott.combiq.service.post.PostService;
import ru.atott.combiq.service.site.RequestUrlResolver;
import ru.atott.combiq.service.site.UrlResolver;
import ru.atott.combiq.web.bean.PagingBean;
import ru.atott.combiq.web.bean.PagingBeanBuilder;
import ru.atott.combiq.web.controller.BaseController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;

@Controller
public class PostController extends BaseController {

    private PagingBeanBuilder pagingBeanBuilder = new PagingBeanBuilder();

    @Autowired
    private PostService postService;

    @RequestMapping(value = {
            "/posts/{postId}",
            "/posts/{postId}/{humanUrlTitle}"
    })
    public Object view(@PathVariable("postId") String postId,
                       @PathVariable("humanUrlTitle") Optional<String> humanUrlTitle,
                       HttpServletRequest request) {
        Post post = postService.getPublishedPost(postId);

        if (post == null) {
            return notFound();
        }

        RedirectView redirectView = redirectToCanonicalUrlIfNeed(post, humanUrlTitle.orElse(null), request);

        if (redirectView != null) {
            return redirectView;
        }

        ModelAndView modelAndView = new ModelAndView("post");
        modelAndView.addObject("post", post);
        return modelAndView;
    }

    @RequestMapping(value = "/posts", method = RequestMethod.GET)
    public ModelAndView getPosts(
            HttpServletRequest httpServletRequest,
            @RequestParam(defaultValue = "1") int page) {
        page = getZeroBasedPage(page);

        Page<Post> posts = postService.getPublishedPosts(page, 10);
        PagingBean paging = pagingBeanBuilder.build(posts, page, httpServletRequest);

        ModelAndView modelAndView = new ModelAndView("posts");
        modelAndView.addObject("paging", paging);
        modelAndView.addObject("posts", Lists.newArrayList(posts.getContent()));
        return modelAndView;
    }

    private RedirectView redirectToCanonicalUrlIfNeed(Post post,
                                                      String humanUrlTitle,
                                                      HttpServletRequest request) {
        if (!Objects.equals(post.getHumanUrlTitle(), humanUrlTitle)) {
            UrlResolver urlResolver = new RequestUrlResolver(request);
            String canonicalUrl = urlResolver.getPostUrl(post);
            return movedPermanently(canonicalUrl);
        }

        return null;
    }
}
