package ru.atott.combiq.rest.request;

import com.google.common.collect.Lists;
import org.elasticsearch.common.collect.Sets;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.Set;

public class QuestionRequest {

    public static QuestionRequest EXAMPLE;

    static {
        EXAMPLE = new QuestionRequest();
        EXAMPLE.setTitle("Заголовок вопроса");
        EXAMPLE.setBody("Markdown разметка содержания/ответа вопроса");
        EXAMPLE.setLevel("D1");
        EXAMPLE.setTags(Lists.newArrayList("core", "jms"));
        EXAMPLE.setLinkedQuestions(Sets.newHashSet("1", "23", "355"));
    }

    @NotEmpty
    private String title;

    private List<String> tags;

    @NotEmpty
    private String level;

    private String body;

    private Set<String> linkedQuestions;

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

    public Set<String> getLinkedQuestions() {
        return linkedQuestions;
    }

    public void setLinkedQuestions(Set<String> linkedQuestions) {
        this.linkedQuestions = linkedQuestions;
    }
}
