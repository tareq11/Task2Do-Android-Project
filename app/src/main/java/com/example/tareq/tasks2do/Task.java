package com.example.tareq.tasks2do;

/**
 * Created by Tareq on 11/24/2017.
 */

public class Task {

private int id;
private String name;
private String dateTime;
private int duration;
private CategoryEnum category;
private String description;


public Task(int id , String name , String dateTime ,int duration, CategoryEnum category,String description)
{
    this.id=id;
    this.name=name;
    this.duration=duration;
    this.dateTime=dateTime;
    this.category=category;
    this.description=description;
}

public int getId(){ return id;}
public String  getName(){ return name;}
public String getDateTime(){ return dateTime;}
public CategoryEnum getCategory(){ return category;}
public int getDuration(){ return duration;}
public String getDescription(){ return description;}

}
