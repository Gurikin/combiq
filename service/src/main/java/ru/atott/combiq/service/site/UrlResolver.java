package ru.atott.combiq.service.site;

import org.apache.commons.lang3.StringUtils;
import ru.atott.combiq.service.bean.Post;
import ru.atott.combiq.service.bean.Question;
import ru.atott.combiq.service.bean.QuestionnaireHead;

import java.net.URLEncoder;

public interface UrlResolver {

    String externalize(String relativeUrl);

    String getQuestionUrl(Question question);

    String getQuestionUrl(Question question, String queryString);

    String getQuestionCommentsUrl(Question question);

    String getQuestionCommentsUrl(Question question, String queryString);

    String getQuestionnaireUrl(QuestionnaireHead questionnaire);

    String getQuestionnaireUrl(QuestionnaireHead questionnaire, String queryString);

    String getPostUrl(Post post);

    String getPostUrl(String postId, String postHumanUrlTitle);

    String getUserUrl(String userId);

    static String encodeUrlComponent(String urlComponent) {
        try {
            if (StringUtils.isEmpty(urlComponent)) {
                return urlComponent;
            }

            return URLEncoder.encode(urlComponent, "utf-8");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
