package com.example.tareq.tasks2do;

public enum CategoryEnum {
    WORK("Work"),
    COLLEGE("College"),
    HOME("Home"),
    OTHERS("Others"),
    ALL("All");
    private String category;

    CategoryEnum(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}