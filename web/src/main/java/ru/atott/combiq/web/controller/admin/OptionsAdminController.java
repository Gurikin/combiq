package ru.atott.combiq.web.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import ru.atott.combiq.service.bean.Options;
import ru.atott.combiq.service.site.OptionsService;
import ru.atott.combiq.web.controller.BaseController;

@Controller
public class OptionsAdminController extends BaseController {

    @Autowired
    private OptionsService optionsService;

    @RequestMapping(value = "/admin/options", method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('sa')")
    public ModelAndView get() {
        Options options = optionsService.getOptions();

        ModelAndView modelAndView = new ModelAndView("admin/options");
        modelAndView.addObject("options", options);
        return modelAndView;
    }

    @RequestMapping(value = "/admin/options", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('sa')")
    public RedirectView post(
            @RequestParam(value = "postsInProduction", required = false, defaultValue = "false") boolean postsInProduction) {

        Options options = optionsService.getOptions();
        options.setPostsInProduction(postsInProduction);

        optionsService.saveOptions(options);

        return redirect("/admin/options");
    }
}
