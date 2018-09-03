package com.example.tareq.tasks2do;
import android.content.res.Resources;
import android.widget.Switch;

import java.sql.*;

public class Utils {

    public static int theme =0;
    public static String language ="";

    public Utils() {

    }

    public String getCategoryAsString(CategoryEnum category ,Resources resources ){
        String categ="";

         switch(category) {
             case ALL :
                 categ = resources.getString(R.string.all);
                break;
             case HOME:
                 categ = resources.getString(R.string.home);
                 break;
             case WORK:
                 categ = resources.getString(R.string.work);
                 break;
             case COLLEGE:
                 categ = resources.getString(R.string.college);
                 break;
             case OTHERS:
                 categ = resources.getString(R.string.others);
                 break;
            default:
                break;
         }

        return categ;
    }
    public CategoryEnum getCategoryAsEnum(String category ){

        if (category.equals("الكل") || category.equals("הכל"))
            return CategoryEnum.ALL;
        else if (category.equals("البيت") || category.equals("בית"))
            return CategoryEnum.HOME;
        else if (category.equals("الكلية") || category.equals("מכללה"))
            return CategoryEnum.COLLEGE;
        else if (category.equals("العمل") || category.equals("עבודה"))
            return CategoryEnum.WORK;
        else if (category.equals("اخرى") || category.equals("אחר"))
            return CategoryEnum.OTHERS;
        return CategoryEnum.valueOf(category);

    }
    public String getPlayPauseString(String status ){
        String newStatus="";
        if (status.equals("يتم المعالجة") || status.equals("מתבצע"))
            return newStatus= "processed";
        else if (status.equals("متوقف") || status.equals("מושהה"))
            newStatus="paused";
        else newStatus=status;
        return newStatus;

    }
}