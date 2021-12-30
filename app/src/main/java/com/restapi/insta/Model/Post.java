package com.restapi.insta.Model;

public class Post {

    private String ImageUrl;
    private String description;
    private String postid;
    private String publisher;

    public Post() {
    }

    public Post(String imageUrl, String description, String postid, String publisher) {
        ImageUrl = imageUrl;
        this.description = description;
        this.postid = postid;
        this.publisher = publisher;
    }


    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
