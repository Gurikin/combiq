package ru.atott.combiq.service.bean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import ru.atott.combiq.dao.entity.MarkdownContent;
import ru.atott.combiq.dao.entity.QuestionComment;

import java.util.List;

public class Question {
    private String id;
    private String title;
    private List<String> tags;
    private String level;
    private long reputation;
    private QuestionAttrs attrs;
    private String tip;
    private MarkdownContent body;
    private List<QuestionComment> comments;
    private boolean landing;
    private List<String> classNames;
    private String humanUrlTitle;
    private Boolean deleted;
    private String authorId;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public long getReputation() {
        return reputation;
    }

    public void setReputation(long reputation) {
        this.reputation = reputation;
    }

    public QuestionAttrs getAttrs() {
        return attrs;
    }

    public void setAttrs(QuestionAttrs attrs) {
        this.attrs = attrs;
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

    public QuestionComment getLastComment() {
        if (CollectionUtils.isEmpty(comments)) {
            return null;
        }

        return comments.get(comments.size() - 1);
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

    public String getHumanUrlTitle() {
        return humanUrlTitle;
    }

    public void setHumanUrlTitle(String humanUrlTitle) {
        this.humanUrlTitle = humanUrlTitle;
    }

    public Boolean getDeleted() {return deleted;}

    public void setDeleted(Boolean deleted) {this.deleted = deleted;}

    public String getAuthorId() {return authorId;}

    public void setAuthorId(String authorId) {this.authorId = authorId;}

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("title", title)
                .append("tags", tags)
                .append("level", level)
                .append("reputation", reputation)
                .append("attrs", attrs)
                .append("tip", tip)
                .append("body", body)
                .append("comments", comments)
                .append("landing", landing)
                .append("classNames", classNames)
                .append("humanUrlTitle", humanUrlTitle)
                .toString();
    }
}
