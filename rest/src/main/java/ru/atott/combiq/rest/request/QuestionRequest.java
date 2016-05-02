package ru.atott.combiq.rest.request;

import com.google.common.collect.Lists;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

public class QuestionRequest {

    public static QuestionRequest EXAMPLE;

    static {
        EXAMPLE = new QuestionRequest();
        EXAMPLE.setTitle("Заголовок вопроса");
        EXAMPLE.setBody("Markdown разметка содержания/ответа вопроса");
        EXAMPLE.setLevel("D1");
        EXAMPLE.setTags(Lists.newArrayList("core", "jms"));
    }

    @NotEmpty
    private String title;

    private List<String> tags;

    @NotEmpty
    private String level;

    private String body;

    private List<String> linkedQuestions;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getLinkedQuestions() {
        return linkedQuestions;
    }

    public void setLinkedQuestions(List<String> linkedQuestions) {
        this.linkedQuestions = linkedQuestions;
    }
}
