package ru.atott.combiq.rest.bean;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PostBean {

    public static PostBean EXAMPLE;

    public static List<PostBean> EXAMPLE_LIST;

    static {
        EXAMPLE = new PostBean();
        EXAMPLE.setId("12");
        EXAMPLE.setPublished(true);
        EXAMPLE.setAuthor(UserBean.EXAMPLE);
        EXAMPLE.setContent(MarkdownContentBean.EXAMPLE);
        EXAMPLE.setPreview(MarkdownContentBean.EXAMPLE);
        EXAMPLE.setCreateDate(new Date());

        EXAMPLE_LIST = Collections.singletonList(EXAMPLE);
    }

    private String id;

    private Date createDate;

    private UserBean author;

    private boolean published;

    private String title;

    private MarkdownContentBean content;

    private MarkdownContentBean preview;

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

    public UserBean getAuthor() {
        return author;
    }

    public void setAuthor(UserBean author) {
        this.author = author;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public MarkdownContentBean getContent() {
        return content;
    }

    public void setContent(MarkdownContentBean content) {
        this.content = content;
    }

    public MarkdownContentBean getPreview() {
        return preview;
    }

    public void setPreview(MarkdownContentBean preview) {
        this.preview = preview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
