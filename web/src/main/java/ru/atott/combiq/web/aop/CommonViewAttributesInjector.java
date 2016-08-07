package ru.atott.combiq.web.aop;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;
import ru.atott.combiq.service.search.comment.LatestCommentSearchService;
import ru.atott.combiq.service.site.OptionsService;
import ru.atott.combiq.service.site.RequestUrlResolver;
import ru.atott.combiq.service.site.UrlResolver;
import ru.atott.combiq.service.user.UserService;
import ru.atott.combiq.web.security.AuthService;
import ru.atott.combiq.web.security.CombiqUser;
import ru.atott.combiq.web.utils.ViewUtils;
import ru.atott.combiq.web.utils.WebUtils;
import ru.atott.combiq.web.view.InstantMessageHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Set;

@Component
public class CommonViewAttributesInjector extends HandlerInterceptorAdapter {

    private static final String INJECTED = "ru.atott.combiq.web.aop.CommonViewAttributesInjector.injected";

    private String resourceVersion = String.valueOf(System.currentTimeMillis());

    @Autowired
    private ViewUtils viewUtils;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Value("${auth.github.clientId}")
    private String githubClientId;

    @Value("${auth.vk.clientId}")
    private String vkClientId;

    @Value("${auth.stackexchange.clientId}")
    private String stackexchangeClientId;

    @Value("${auth.facebook.clientId}")
    private String facebookClientId;

    @Value("${web.toolbox.visible}")
    private boolean toolboxVisible;

    @Value("${web.toolbox.color}")
    private String toolboxColor;

    @Value("${web.toolbox.text}")
    private String toolboxText;

    @Value("${web.auth.github.enable}")
    private boolean githubEnable;

    @Value("${web.auth.vk.enable}")
    private boolean vkEnable;

    @Value("${web.auth.stackexchange.enable}")
    private boolean stackexchangeEnable;

    @Value("${web.auth.facebook.enable}")
    private boolean facebookEnable;

    @Autowired
    private OptionsService optionsService;

    @Autowired
    private InstantMessageHolder instantMessageHolder;

    @Autowired
    private LatestCommentSearchService latestCommentSearchService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        inject(request, modelAndView);
    }

    public void inject(HttpServletRequest request, ModelAndView modelAndView) {
        if (modelAndView != null
                && !(modelAndView.getView() instanceof RedirectView)
                && !modelAndView.getModel().containsKey(INJECTED)) {

            CombiqUser user = authService.getUser();

            UrlResolver urlResolver = new RequestUrlResolver(request);

            Set<String> roles = Collections.emptySet();
            if (user != null) {
                roles = user.getRoles();
            }

            modelAndView.addObject("utils", viewUtils);
            modelAndView.addObject("env", System.getProperty("env"));
            modelAndView.addObject("node", WebUtils.getNode());
            modelAndView.addObject("resourceVersion", resourceVersion);
            modelAndView.addObject("user", user);

            modelAndView.addObject("userId", authService.getUserId());
            modelAndView.addObject("roles", roles);
            modelAndView.addObject("userEmail", user != null ? user.getEmail() : null);
            modelAndView.addObject("urlResolver", new RequestUrlResolver(request));

            String sessionId = request.getSession(true).getId();
            String state = DigestUtils.sha256Hex(sessionId + authService.getLaunchDependentSalt());

            modelAndView.addObject("githubClientId", githubClientId);
            modelAndView.addObject("githubClientState", state);
            modelAndView.addObject("vkClientId", vkClientId);
            modelAndView.addObject("vkClientState", state);
            modelAndView.addObject("vkCallbackUrl", urlResolver.externalize("/login/callback/vk.do"));
            modelAndView.addObject("stackexchangeClientId", stackexchangeClientId);
            modelAndView.addObject("stackexchangeClientState", state);
            modelAndView.addObject("stackexchangeCallbackUrl", urlResolver.externalize("/login/callback/stackexchange.do"));
            modelAndView.addObject("facebookClientId", facebookClientId);
            modelAndView.addObject("facebookClientState", state);
            modelAndView.addObject("facebookCallbackUrl", urlResolver.externalize("/login/callback/facebook.do"));
            modelAndView.addObject("instantMessage", instantMessageHolder.get());
            modelAndView.addObject("githubEnable", githubEnable);
            modelAndView.addObject("vkEnable", vkEnable);
            modelAndView.addObject("stackexchangeEnable", stackexchangeEnable);
            modelAndView.addObject("facebookEnable", facebookEnable);
            modelAndView.addObject("latestCommentFeed", latestCommentSearchService.get5LatestComments());
            modelAndView.addObject("options", optionsService.getCachedOptions());

            if (WebUtils.isProductionClusterNodeRequest(request)) {
                modelAndView.addObject("toolboxVisible", true);
                modelAndView.addObject("toolboxColor", toolboxColor);
                modelAndView.addObject("toolboxText", "Production cluster's node: " + WebUtils.getNode());
            } else {
                modelAndView.addObject("toolboxVisible", toolboxVisible);
                modelAndView.addObject("toolboxColor", toolboxColor);
                modelAndView.addObject("toolboxText", toolboxText);
            }

            modelAndView.addObject(INJECTED, true);
        }
    }
}
