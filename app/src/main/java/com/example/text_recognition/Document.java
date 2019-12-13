package com.example.text_recognition;

public class Document {

    private String Name;
    private int image;

    public Document(String name, int image) {
        this.Name = name;
        this.image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }


}
