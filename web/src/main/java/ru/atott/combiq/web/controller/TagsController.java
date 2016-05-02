package ru.atott.combiq.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ru.atott.combiq.service.bean.DetailedQuestionTag;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ru.atott.combiq.service.bean.QuestionTag;
import ru.atott.combiq.service.question.TagService;
import ru.atott.combiq.web.bean.SuccessBean;
import ru.atott.combiq.web.request.EditTagRequest;

import java.util.List;

@Controller
public class TagsController extends BaseController {

    @Autowired
    private TagService tagService;

    @RequestMapping(value = "/tags")
    public ModelAndView tags() {
        ModelAndView modelAndView = new ModelAndView("tags");
        modelAndView.addObject("tags", tagService.getAllQuestionTags());
        return modelAndView;
    }

    @RequestMapping(value = "/tags", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<DetailedQuestionTag> getAvailableTags(){
        return tagService.getAllQuestionTags();
    }

    @RequestMapping(value = "/tags", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAnyRole('sa','contenter')")
    public Object post(@RequestBody EditTagRequest editTagRequest){
        QuestionTag tag = tagService.getTag(editTagRequest.getTag());
        tag.setDescription(editTagRequest.getDescription());
        tag.setSuggestViewOthersQuestionsLabel(editTagRequest.getSuggestViewOthersQuestionsLabel());
        tagService.save(tag);
        return new SuccessBean();
    }
}
