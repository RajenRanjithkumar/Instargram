package com.restapi.insta.Model;

public class User {

    private String name; //name
    private String email;
    private String username;
    private String bio; //boi
    private String imageurl;
    private String id;
    private Boolean online;

    public User() {
    }


    public User(String name, String email, String username, String bio, String imageurl, String id, Boolean online) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.bio = bio;
        this.imageurl = imageurl;
        this.id = id;
        this.online = online;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }
}
