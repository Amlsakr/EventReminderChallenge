
package com.example.aml.eventreminderchallenge.model.weather;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

public class Temp {
    @SerializedName("morn")
    private double morn;
    @SerializedName("eve")
    private double eve;
    @SerializedName("night")
    private double night;
    @SerializedName("max")
    private double max;
    @SerializedName("min")
    private double min;
    @SerializedName("day")
    private double day;

    public double getMorn() {
        return morn;
    }

    public double getEve() {
        return eve;
    }

    public double getNight() {
        return night;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getDay() {
        return day;
    }
}
