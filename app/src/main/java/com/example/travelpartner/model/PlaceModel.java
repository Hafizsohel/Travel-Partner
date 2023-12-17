package com.example.travelpartner.model;

public class PlaceModel {
    private String  name;
    private String  location;
    private String  img_url;

    public PlaceModel(String name, String location, String img_url) {
        this.name = name;
        this.location = location;
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    @Override
    public String toString() {
        return "PlaceModel{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", img_url='" + img_url + '\'' +
                '}';
    }
}
