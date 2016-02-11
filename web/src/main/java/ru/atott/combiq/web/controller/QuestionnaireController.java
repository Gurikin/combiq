package ru.atott.combiq.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.atott.combiq.service.bean.Questionnaire;
import ru.atott.combiq.service.question.QuestionnaireService;
import ru.atott.combiq.web.bean.SuccessBean;
import ru.atott.combiq.web.request.ContentRequest;

import javax.servlet.http.HttpServletResponse;

@Controller
public class QuestionnaireController extends BaseController {

    @Autowired
    private QuestionnaireService questionnaireService;

    @RequestMapping(value = "/questionnaire/{questionnaireId}")
    public ModelAndView view(@PathVariable("questionnaireId") String questionnaireId) {
        Questionnaire questionnaire = questionnaireService.getQuestionnaire(questionnaireId);

        ModelAndView modelAndView = new ModelAndView("questionnaire");
        modelAndView.addObject("questionnaire", questionnaire);
        return modelAndView;
    }

    @RequestMapping(value = "/questionnaire/{questionnaireId}/{title}.pdf", method = RequestMethod.GET)
    public void view(@PathVariable("questionnaireId") String questionnaireId,
                     @PathVariable("title") String title,
                     HttpServletResponse response) throws Exception {
        Questionnaire questionnaire = questionnaireService.getQuestionnaire(questionnaireId);

        response.setContentType("application/pdf");

        questionnaireService.exportQuestionnareToPdf(questionnaire, response.getOutputStream());
        response.flushBuffer();
    }

    @RequestMapping(value = "/questionnaire/{questionnaireId}/title", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAnyRole('sa','contenter')")
    public Object updateTitle(@PathVariable("questionnaireId") String questionnaireId,
                              @RequestBody ContentRequest title) {
        questionnaireService.updateQuestionnaireTitle(questionnaireId, title.getContent());
        return new SuccessBean();
    }
}
