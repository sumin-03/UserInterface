package com.example.userinterface;

public class ItemModel {
    private String name;
    private String manufacturer;
    private String jsonString;

    // 생성자
    public ItemModel(String name, String manufacturer, String jsonString) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.jsonString = jsonString;
    }
    public ItemModel() {
    }

    public String getTitle() {
        return name;
    }
    public String getDescription() {
        return manufacturer;
    }
    public String getJsonString() {
        return jsonString;
    }
}