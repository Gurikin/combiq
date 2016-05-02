package ru.atott.combiq.dao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import ru.atott.combiq.dao.Types;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Document(indexName = "#{domainResolver.resolveQuestionIndex()}", type = Types.question)
public class QuestionEntity {

    public static final String TIMESTAMP_FIELD = "timestamp";
    public static final String ASKEDCOUNT_FIELD = "askedCount";

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

    private String legacyId;

    private boolean deleted;

    private int stars;

    private Date lastModify;

    private Integer askedCount;

    private int askedToday;

    private List<String> linkedQuestions;

    public Date getLastModify() {
        return lastModify;
    }

    public void setLastModify(Date lastModify) {
        this.lastModify = lastModify;
    }

    @Field(index = FieldIndex.not_analyzed, type = FieldType.String)
    private String authorId;

    private String authorName;

    @Field(index = FieldIndex.not_analyzed, type = FieldType.String)
    private String humanUrlTitle;

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

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
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

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorId() {
        return authorId;
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

    public Integer getAskedCount() {
        return askedCount;
    }

    public void setAskedCount(Integer askedCount) {
        this.askedCount = askedCount;
    }

    public int getAskedToday() {
        return askedToday;
    }

    public void setAskedToday(int askedToday) {
        this.askedToday = askedToday;
    }

    public void setLinkedQuestions(List<String> linkedQuestions) {
        this.linkedQuestions = linkedQuestions;
    }

    public List<String> getLinkedQuestions() {
        return linkedQuestions;
    }
}
