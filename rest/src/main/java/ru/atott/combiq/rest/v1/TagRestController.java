package ru.atott.combiq.rest.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.atott.combiq.rest.mapper.TagBeanMapper;
import ru.atott.combiq.service.bean.DetailedQuestionTag;
import ru.atott.combiq.service.question.TagService;

import java.util.List;

@RestController
public class TagRestController extends BaseRestController {

    @Autowired
    private TagService tagService;

    /**
     * Вернуть список тегов.
     *
     * @response.200.doc
     *      Список тегов.
     *
     * @response.200.example
     *      {@link ru.atott.combiq.rest.bean.TagBean#EXAMPLE_LIST}
     */
    @RequestMapping(value = "/rest/v1/tag", method = RequestMethod.GET)
    @ResponseBody
    public Object getTags() {
        List<DetailedQuestionTag> tags = tagService.getAllQuestionTags();
        TagBeanMapper beanMapper = new TagBeanMapper();
        return beanMapper.toList(getContext(), tags);
    }
}
