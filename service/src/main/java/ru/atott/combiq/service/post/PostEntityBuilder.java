package ru.atott.combiq.service.post;

import org.jsoup.helper.Validate;
import ru.atott.combiq.dao.entity.PostEntity;
import ru.atott.combiq.service.markdown.MarkdownService;
import ru.atott.combiq.service.site.UserContext;
import ru.atott.combiq.service.util.ApplicationContextHolder;
import ru.atott.combiq.service.util.NumberService;
import ru.atott.combiq.service.util.TransletirateService;

import java.util.Date;

public class PostEntityBuilder implements PostBuilder {

    private PostEntity postEntity;

    private UserContext userContext;

    public PostEntityBuilder(UserContext userContext, PostEntity postEntity) {
        Validate.notNull(userContext);

        this.postEntity = postEntity;
        this.userContext = userContext;

        if (this.postEntity == null) {
            this.postEntity = new PostEntity();
            this.postEntity.setId(String.valueOf(getNumberService().getUniqueNumber()));
            this.postEntity.setAuthorUserId(userContext.getUserId());
            this.postEntity.setAuthorUserName(userContext.getUserName());
            this.postEntity.setCreateDate(new Date());
        }
    }

    public PostEntityBuilder(UserContext userContext) {
        this(userContext, null);
    }

    public PostEntityBuilder setTitle(String title) {
        Validate.notNull(title);

        this.postEntity.setTitle(title);
        this.postEntity.setHumanUrlTitle(getTransletirateService().lowercaseAndTransletirate(title, 80));

        return this;
    }

    @Override
    public PostEntityBuilder setContent(String contentMarkdown) {
        this.postEntity.setContent(getMarkdownService().toMarkdownContent(userContext, contentMarkdown));

        return this;
    }

    @Override
    public PostEntityBuilder setPublished(boolean published) {
        this.postEntity.setPublished(published);

        return this;
    }

    @Override
    public PostBuilder setPreview(String previewMarkdown) {
        this.postEntity.setPreview(getMarkdownService().toMarkdownContent(userContext, previewMarkdown));

        return this;
    }

    public PostEntity build() {
        return this.postEntity;
    }

    private MarkdownService getMarkdownService() {
        return ApplicationContextHolder.getApplicationContext().getBean(MarkdownService.class);
    }

    private NumberService getNumberService() {
        return ApplicationContextHolder.getApplicationContext().getBean(NumberService.class);
    }

    private TransletirateService getTransletirateService() {
        return ApplicationContextHolder.getApplicationContext().getBean(TransletirateService.class);
    }
}
