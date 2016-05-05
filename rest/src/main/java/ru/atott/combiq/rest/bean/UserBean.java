package ru.atott.combiq.rest.bean;

public class UserBean {

    public static UserBean EXAMPLE;

    static {
        EXAMPLE = new UserBean();
        EXAMPLE.setId("AU1d7i-CE29pBsA_iQt8");
        EXAMPLE.setName("User Name");
        EXAMPLE.setUri("http://combiq.ru/users/AU1d7i-CE29pBsA_iQt8");
        EXAMPLE.setAvatarUri("http://combiq.ru/static/images/site/avatar.png");
    }

    private String id;

    private String name;

    private String uri;

    private String avatarUri;

    public UserBean() { }

    public UserBean(String id, String name, String uri, String avatarUri) {
        this.id = id;
        this.name = name;
        this.uri = uri;
        this.avatarUri = avatarUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }
}
