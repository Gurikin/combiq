package ru.atott.combiq.web.controller.admin;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.atott.combiq.service.bean.Post;
import ru.atott.combiq.service.post.PostService;
import ru.atott.combiq.web.bean.PagingBean;
import ru.atott.combiq.web.bean.PagingBeanBuilder;
import ru.atott.combiq.web.controller.BaseController;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PostAdminController extends BaseController {

    private PagingBeanBuilder pagingBeanBuilder = new PagingBeanBuilder();

    @Autowired
    private PostService postService;

    @RequestMapping(value = "/admin/posts", method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('sa','contenter')")
    public ModelAndView list(
            HttpServletRequest httpServletRequest,
            @RequestParam(defaultValue = "1") int page) {
        page = getZeroBasedPage(page);

        Page<Post> posts = postService.getPosts(page, 10);
        PagingBean paging = pagingBeanBuilder.build(posts, page, httpServletRequest);

        ModelAndView modelAndView = new ModelAndView("admin/posts");
        modelAndView.addObject("paging", paging);
        modelAndView.addObject("posts", Lists.newArrayList(posts.getContent()));
        return modelAndView;
    }

    @RequestMapping(value = "/admin/posts/edit")
    @PreAuthorize("hasAnyRole('sa','contenter')")
    public ModelAndView edit(@RequestParam(value = "id", required = false) String id) {
        ModelAndView modelAndView = new ModelAndView("admin/editPost");
        modelAndView.addObject("postId", id);
        return modelAndView;
    }
}
