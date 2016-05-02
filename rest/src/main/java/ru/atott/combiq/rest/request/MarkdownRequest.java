package ru.atott.combiq.rest.request;


public class MarkdownRequest {

    private String markdown;

    private static MarkdownRequest EXAMPLE;

    static {
        EXAMPLE = new MarkdownRequest();
        EXAMPLE.setMarkdown("Пример запроса");
    }

    public MarkdownRequest() { }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }
}
