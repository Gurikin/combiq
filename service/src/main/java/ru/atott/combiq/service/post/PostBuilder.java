package ru.atott.combiq.service.post;

public interface PostBuilder {

    PostBuilder setTitle(String title);

    PostBuilder setContent(String contentMarkdown);

    PostBuilder setPublished(boolean published);

    PostBuilder setPreview(String previewMarkdown);
}
