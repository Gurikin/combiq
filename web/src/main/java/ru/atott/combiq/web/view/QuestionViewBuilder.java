package ru.atott.combiq.web.view;

import org.springframework.web.servlet.ModelAndView;
import ru.atott.combiq.dao.entity.QuestionComment;
import ru.atott.combiq.service.bean.Question;
import ru.atott.combiq.service.bean.QuestionTag;
import ru.atott.combiq.service.search.comment.LatestComment;
import ru.atott.combiq.service.search.question.QuestionPositionInDsl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestionViewBuilder {
    private Question question;
    private QuestionPositionInDsl positionInDsl;
    private String dsl;
    private List<QuestionTag> tags;
    private String canonicalUrl;
    private List<Question> anotherQuestions;
    private List<LatestComment> questionsWithLatestComments;
    private boolean favorite;
    private boolean asked;
    private List<Question> linkedQuestions = Collections.emptyList();

    public String getDsl() {
        return dsl;
    }

    public void setDsl(String dsl) {
        this.dsl = dsl;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public QuestionPositionInDsl getPositionInDsl() {
        return positionInDsl;
    }

    public void setPositionInDsl(QuestionPositionInDsl positionInDsl) {
        this.positionInDsl = positionInDsl;
    }

    public List<QuestionTag> getTags() {
        return tags;
    }

    public void setTags(List<QuestionTag> tags) {
        this.tags = tags;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public void setCanonicalUrl(String canonicalUrl) {
        this.canonicalUrl = canonicalUrl;
    }

    public List<Question> getAnotherQuestions() {
        return anotherQuestions;
    }

    public void setAnotherQuestions(List<Question> anotherQuestions) {
        this.anotherQuestions = anotherQuestions;
    }

    public List<LatestComment> getQuestionsWithLatestComments() {
        return questionsWithLatestComments;
    }

    public void setQuestionsWithLatestComments(List<LatestComment> questionsWithLatestComments) {
        this.questionsWithLatestComments = questionsWithLatestComments;
    }

    public List<Question> getLinkedQuestions() {
        return linkedQuestions;
    }

    public void setLinkedQuestions(List<Question> linkedQuestions) {
        this.linkedQuestions = linkedQuestions;
    }

    public boolean isAsked() {
        return asked;
    }

    public void setAsked(boolean asked) {
        this.asked = asked;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public ModelAndView build() {
        List<QuestionComment> comments = question.getComments();
        if (comments == null) {
            comments = Collections.emptyList();
        }
        comments = comments.stream()
                .sorted((a, b) -> - a.getPostDate().compareTo(b.getPostDate()))
                .collect(Collectors.toList());

        ModelAndView mav = new ModelAndView("question");
        mav.addObject("question", question);
        mav.addObject("position", positionInDsl);
        mav.addObject("dsl", dsl);
        mav.addObject("tags", tags);
        mav.addObject("comments", comments);
        mav.addObject("landing", question.isLanding());
        mav.addObject("canonicalUrl", canonicalUrl);
        mav.addObject("anotherQuestions", anotherQuestions);
        mav.addObject("questionsWithLatestComments", questionsWithLatestComments);
        mav.addObject("favorite", favorite);
        mav.addObject("asked", asked);
        mav.addObject("linkedQuestions", linkedQuestions);
        return mav;
    }
}
