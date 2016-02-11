package ru.atott.combiq.dao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import ru.atott.combiq.dao.Types;

import java.util.List;

@Document(indexName = "#{domainResolver.resolveQuestionIndex()}", type = Types.question)
public class QuestionEntity {
    @Id
    private String id;
    private String title;
    private List<String> tags;
    private int level;
    private Long reputation;
    private String tip;
    private MarkdownContent body;
    private List<QuestionComment> comments;
    private boolean landing;
    private List<String> classNames;
    private Long timestamp;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Long getReputation() {
        return reputation;
    }

    public void setReputation(Long reputation) {
        this.reputation = reputation;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public MarkdownContent getBody() {
        return body;
    }

    public void setBody(MarkdownContent body) {
        this.body = body;
    }

    public List<QuestionComment> getComments() {
        return comments;
    }

    public void setComments(List<QuestionComment> comments) {
        this.comments = comments;
    }

    public boolean isLanding() {
        return landing;
    }

    public void setLanding(boolean landing) {
        this.landing = landing;
    }

    public List<String> getClassNames() {
        return classNames;
    }

    public void setClassNames(List<String> classNames) {
        this.classNames = classNames;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
