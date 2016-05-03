package ru.atott.combiq.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import ru.atott.combiq.service.site.UserContext;
import ru.atott.combiq.web.aop.CommonViewAttributesInjector;
import ru.atott.combiq.web.filter.RequestHolderFilter;
import ru.atott.combiq.web.security.AuthService;
import ru.atott.combiq.web.security.CombiqUser;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    @Autowired
    private AuthService authService;

    @Autowired
    private CommonViewAttributesInjector commonViewAttributesInjector;

    protected int getZeroBasedPage(int page) {
        return Math.max(0, page - 1);
    }

    protected CombiqUser getCombiqUser() {
        return authService.getUser();
    }

    protected UserContext getUc() {
        return authService.getUserContext();
    }

    protected RedirectView movedPermanently(String url) {
        RedirectView redirectView = new RedirectView(url);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return redirectView;
    }

    protected RedirectView redirect(String url) {
        return new RedirectView(url);
    }

    protected ModelAndView notFound() {
        HttpServletRequest httpRequest = RequestHolderFilter.REQUEST.get();
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.addObject("requestUrl", httpRequest.getRequestURL());
        commonViewAttributesInjector.inject(httpRequest, modelAndView);
        return modelAndView;
    }
}
