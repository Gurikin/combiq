package ru.atott.combiq.rest.request;

public class PostRequest {

    public static PostRequest EXAMPLE;

    static {
        EXAMPLE = new PostRequest();
        EXAMPLE.setPublished(false);
        EXAMPLE.setContent("Содержание статьи");
        EXAMPLE.setPreview("Превью статьи");
        EXAMPLE.setTitle("Заголовок статьи");
    }

    private String title;

    private String content;

    private String preview;

    private boolean published;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
