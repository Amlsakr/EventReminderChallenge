
package com.example.aml.eventreminderchallenge.model.weather;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

public class Weather {

    @SerializedName("icon")
    private String icon;
    @SerializedName("description")
    private String description;
    @SerializedName("main")
    private String main;
    @SerializedName("id")
    private int id;

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public String getMain() {
        return main;
    }

    public int getId() {
        return id;
    }
}
