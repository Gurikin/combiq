package ru.atott.combiq.rest.bean;

import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class QuestionBean {

    public static QuestionBean EXAMPLE;

    public static List<QuestionBean> EXAMPLE_LIST;

    static {
        EXAMPLE = new QuestionBean();
        EXAMPLE.setId("577");
        EXAMPLE.setLevel("D2");
        EXAMPLE.setCommentsCount(4);
        EXAMPLE.setChangeDate(new Date());
        EXAMPLE.setTags(Lists.newArrayList("concurrency"));
        EXAMPLE.setTitle("Как сделать Safe Publishing используя synchronized?");
        EXAMPLE.setUri("http://combiq.ru/questions/577/kak-sdelat-safe-publishing-ispolzuya-synchronized");
        EXAMPLE_LIST = Lists.newArrayList(EXAMPLE);
    }

    private String id;

    private String title;

    private List<String> tags;

    private String uri;

    private int commentsCount;

    private Date changeDate;

    private String level;

    private MarkdownContentBean body;

    private List<String> linkedQuestions;

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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public MarkdownContentBean getBody() {
        return body;
    }

    public void setBody(MarkdownContentBean body) {
        this.body = body;
    }

    public List<String> getLinkedQuestions() {
        return linkedQuestions;
    }

    public void setLinkedQuestions(List<String> linkedQuestions) {
        this.linkedQuestions = linkedQuestions;
    }
}
