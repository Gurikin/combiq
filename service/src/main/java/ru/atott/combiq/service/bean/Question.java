package ru.atott.combiq.service.bean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import ru.atott.combiq.dao.entity.MarkdownContent;
import ru.atott.combiq.dao.entity.QuestionComment;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class Question {

    private String id;

    private String title;

    private List<String> tags;

    private String level;

    private long reputation;

    private String tip;

    private MarkdownContent body;

    private List<QuestionComment> comments;

    private boolean landing;

    private List<String> classNames;

    private String humanUrlTitle;

    private boolean deleted;

    private String authorId;

    private String authorName;

    private int stars;

    private Date lastModify;

    private int askedCount;

    private List<String> linkedQuestions;

    public Date getLastModify() {
        return lastModify;
    }

    public void setLastModify(Date lastModify) {
        this.lastModify = lastModify;
    }

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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getAskedCount() {
        return askedCount;
    }

    public void setAskedCount(int askedCount) {
        this.askedCount = askedCount;
    }

    public List<String> getLinkedQuestions() {
        return linkedQuestions;
    }

    public void setLinkedQuestions(List<String> linkedQuestions) {
        this.linkedQuestions = linkedQuestions;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("title", title)
                .append("tags", tags)
                .append("level", level)
                .append("reputation", reputation)
                .append("tip", tip)
                .append("body", body)
                .append("comments", comments)
                .append("landing", landing)
                .append("classNames", classNames)
                .append("humanUrlTitle", humanUrlTitle)
                .append("deleted", deleted)
                .append("authorId", authorId)
                .append("authorName", authorName)
                .append("stars", stars)
                .append("lastModify", lastModify)
                .append("askedCount", askedCount)
                .append("linkedQuestions", linkedQuestions)
                .toString();
    }
}

