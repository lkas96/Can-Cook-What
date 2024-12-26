package com.project.mini.lkas.ccw.model;

public class Review {

    // PART 1
    private String name; // userName
    private String email; // user email
    // private String postedDate; // on form submission

    // PART 2
    private String idMeal;
    private String mealTitle;
    private String mealPicture; // image url

    // PART 3
    private String reviewTitle;
    private String reviewMessage;

    // part 4
    private String bloggerUrl;
    private Long publishOnEpoch;
    private String postId;

    //Part 5
    private String helperDate;

    public String getHelperDate() {
        return helperDate;
    }

    public void setHelperDate(String helperDate) {
        this.helperDate = helperDate;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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

    public String getIdMeal() {
        return idMeal;
    }

    public void setIdMeal(String idMeal) {
        this.idMeal = idMeal;
    }

    public String getMealTitle() {
        return mealTitle;
    }

    public void setMealTitle(String mealTitle) {
        this.mealTitle = mealTitle;
    }

    public String getMealPicture() {
        return mealPicture;
    }

    public void setMealPicture(String mealPicture) {
        this.mealPicture = mealPicture;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public String getReviewMessage() {
        return reviewMessage;
    }

    public void setReviewMessage(String reviewMessage) {
        this.reviewMessage = reviewMessage;
    }

    public String getBloggerUrl() {
        return bloggerUrl;
    }

    public void setBloggerUrl(String bloggerUrl) {
        this.bloggerUrl = bloggerUrl;
    }

    public Long getPublishOnEpoch() {
        return publishOnEpoch;
    }

    public void setPublishOnEpoch(Long publishOnEpoch) {
        this.publishOnEpoch = publishOnEpoch;
    }

    public Review(String name, String email, String idMeal, String mealTitle, String mealPicture,
            String reviewTitle, String reviewMessage) {
        this.name = name;
        this.email = email;
        this.idMeal = idMeal;
        this.mealTitle = mealTitle;
        this.mealPicture = mealPicture;
        this.reviewTitle = reviewTitle;
        this.reviewMessage = reviewMessage;
    }

    public Review() {
    }

    @Override
    public String toString() {
        return "Review [name=" + name + ", email=" + email + ", idMeal=" + idMeal + ", mealTitle=" + mealTitle
                + ", mealPicture=" + mealPicture + ", reviewTitle=" + reviewTitle + ", reviewMessage=" + reviewMessage
                + ", bloggerUrl=" + bloggerUrl + ", publishOnEpoch=" + publishOnEpoch + "]";
    }


    
}
