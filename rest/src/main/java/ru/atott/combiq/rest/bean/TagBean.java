package ru.atott.combiq.rest.bean;

import java.util.Collections;
import java.util.List;

public class TagBean {

    public static TagBean EXAMPLE;

    public static List<TagBean> EXAMPLE_LIST;

    static {
        EXAMPLE = new TagBean();
        EXAMPLE.setTag("core");
        EXAMPLE.setDescription("Описание тэга");
        EXAMPLE.setCount(10);
        EXAMPLE_LIST = Collections.singletonList(EXAMPLE);
    }

    private String tag;

    private String description;

    private long count;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
