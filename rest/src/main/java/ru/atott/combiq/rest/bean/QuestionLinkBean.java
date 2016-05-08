package ru.atott.combiq.rest.bean;

import com.google.common.collect.Lists;
import java.util.List;

public class QuestionLinkBean {

    public static QuestionLinkBean EXAMPLE;

    public static List<QuestionLinkBean> EXAMPLE_LIST;

    static {
        EXAMPLE = new QuestionLinkBean();
        EXAMPLE.setId("577");
        EXAMPLE.setTitle("Как сделать Safe Publishing используя synchronized?");
        EXAMPLE.setUri("http://combiq.ru/questions/577/kak-sdelat-safe-publishing-ispolzuya-synchronized");
        EXAMPLE_LIST = Lists.newArrayList(EXAMPLE);
    }

    private String id;

    private String title;

    private String uri;

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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
