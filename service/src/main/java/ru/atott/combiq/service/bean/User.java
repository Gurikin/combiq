package ru.atott.combiq.service.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class User implements Serializable {
    private String id;
    private String name;
    private String email;
    private String passwordHash;
    private String login;
    private String location;
    private String home;
    private UserType type;
    private String avatarUrl;
    private List<String> roles;
    private Date registerDate;
    private Set<String> favoriteQuestions;
    private String nick;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public UserQualifier getQualifier() {
        return new UserQualifier(type, login);
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Set<String> getFavoriteQuestions() {
        return favoriteQuestions;
    }

    public void setFavoriteQuestions(Set<String> favoriteQuestions) {
        this.favoriteQuestions = favoriteQuestions;
    }
}
