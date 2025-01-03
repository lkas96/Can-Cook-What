package com.project.mini.lkas.ccw.model;

public class Post {

    private String publishedOn;
    private String url;
    private String title;
    private String content;

    public String getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(String publishedOn) {
        this.publishedOn = publishedOn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

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

    public Post(String publishedOn, String url, String title, String content) {
        this.publishedOn = publishedOn;
        this.url = url;
        this.title = title;
        this.content = content;
    }
}
