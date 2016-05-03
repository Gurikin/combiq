package ru.atott.combiq.dao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import ru.atott.combiq.dao.Types;

@Document(indexName = "#{domainResolver.resolveSystemIndex()}", type = Types.options)
public class OptionsEntity {

    @Id
    private String id;

    private Long lastNumber;

    private boolean postsInProduction;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getLastNumber() {
        return lastNumber;
    }

    public void setLastNumber(Long lastNumber) {
        this.lastNumber = lastNumber;
    }

    public boolean isPostsInProduction() {
        return postsInProduction;
    }

    public void setPostsInProduction(boolean postsInProduction) {
        this.postsInProduction = postsInProduction;
    }
}
