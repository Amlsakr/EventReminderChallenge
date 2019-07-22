
package com.example.aml.eventreminderchallenge.model.weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class WeatherResponse {

    @SerializedName("list")
    private List<DayWeather> dayWeatherList;
    @SerializedName("cnt")
    private int count;
    @SerializedName("message")
    private double message;
    @SerializedName("cod")
    private String cod;
    @SerializedName("city")
    private City city;

    public List<DayWeather> getDayWeatherList() {
        return dayWeatherList;
    }

    public int getCount() {
        return count;
    }

    public double getMessage() {
        return message;
    }

    public String getCod() {
        return cod;
    }

    public City getCity() {
        return city;
    }
}
