package com.example.aml.eventreminderchallenge.model.weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DayWeather {
    @SerializedName("clouds")
    private int clouds;
    @SerializedName("deg")
    private int deg;
    @SerializedName("speed")
    private double speed;
    @SerializedName("weather")
    private List<Weather> weather;
    @SerializedName("humidity")
    private int humidity;
    @SerializedName("pressure")
    private double pressure;
    @SerializedName("temp")
    private Temp temp;
    @SerializedName("dt")
    private int dt;

    public int getClouds() {
        return clouds;
    }

    public int getDeg() {
        return deg;
    }

    public double getSpeed() {
        return speed;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public Temp getTemp() {
        return temp;
    }

    public int getDt() {
        return dt;
    }
}
