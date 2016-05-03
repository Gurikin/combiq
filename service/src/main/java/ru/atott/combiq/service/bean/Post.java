package ru.atott.combiq.service.bean;

import ru.atott.combiq.dao.entity.MarkdownContent;

import java.util.Date;

public class Post {

    private String id;

    private Date createDate;

    private boolean published;

    private String authorUserId;

    private String authorUserName;

    private String title;

    private MarkdownContent content;

    private MarkdownContent preview;

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
