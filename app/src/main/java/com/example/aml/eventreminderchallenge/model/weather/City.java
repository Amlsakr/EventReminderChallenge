
package com.example.aml.eventreminderchallenge.model.weather;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

public class City {

    @SerializedName("timezone")
    private int timezone;
    @SerializedName("population")
    private int population;
    @SerializedName("country")
    private String country;
    @SerializedName("coord")
    private Coord coord;
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private int id;

    public int getTimezone() {
        return timezone;
    }

    public int getPopulation() {
        return population;
    }

    public String getCountry() {
        return country;
    }

    public Coord getCoord() {
        return coord;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
