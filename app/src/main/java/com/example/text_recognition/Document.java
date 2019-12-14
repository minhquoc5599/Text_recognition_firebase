package com.example.text_recognition;

public class Document {
    private String name;
    private String text;
    private String image;
    private String email;

    public Document()
    {

    }

    public Document(String name, String text, String image, String email) {
        this.name = name;
        this.text = text;
        this.image = image;
        this.email = email;
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
}
