package com.example.text_recognition.Class;

public class Document {
    private String name;
    private String text;
    private String image;
    private String email;
    private String emailShare;

    public Document()
    {

    }

    public Document(String name, String text, String image, String email, String emailShare) {
        this.name = name;
        this.text = text;
        this.image = image;
        this.email = email;
        this.emailShare = emailShare;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailShare() {
        return emailShare;
    }

    public void setEmailShare(String emailShare) {
        this.emailShare = emailShare;
    }
}
