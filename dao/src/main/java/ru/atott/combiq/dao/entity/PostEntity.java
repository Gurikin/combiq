package ru.atott.combiq.dao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import ru.atott.combiq.dao.Types;

import java.util.Date;

@Document(indexName = "#{domainResolver.resolveQuestionIndex()}", type = Types.post)
public class PostEntity {

    public static final String CREATE_DATE_FIELD = "createDate";

    @Id
    private String id;

    private Date createDate;

    private boolean published;

    private String authorUserId;

    private String authorUserName;

    private String title;

    private MarkdownContent content;

    private MarkdownContent preview;

    @Field(index = FieldIndex.not_analyzed, type = FieldType.String)
    private String humanUrlTitle;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(String authorUserId) {
        this.authorUserId = authorUserId;
    }

    public String getAuthorUserName() {
        return authorUserName;
    }

    public void setAuthorUserName(String authorUserName) {
        this.authorUserName = authorUserName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MarkdownContent getContent() {
        return content;
    }

    public void setContent(MarkdownContent content) {
        this.content = content;
    }

    public MarkdownContent getPreview() {
        return preview;
    }

    public void setPreview(MarkdownContent preview) {
        this.preview = preview;
    }

    public String getHumanUrlTitle() {
        return humanUrlTitle;
    }

    public void setHumanUrlTitle(String humanUrlTitle) {
        this.humanUrlTitle = humanUrlTitle;
    }
}
