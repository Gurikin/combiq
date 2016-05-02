package ru.atott.combiq.rest.bean;

import ru.atott.combiq.service.file.Location;

public class UploadFileBean {

    private String location;

    public static UploadFileBean EXAMPLE;

    static {
        EXAMPLE = new UploadFileBean("imageAddress");
    }

    public UploadFileBean() { }

    public UploadFileBean(String location) {
        this.location = location;
    }

    public UploadFileBean(Location location) {
        this.location = location.toString();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
