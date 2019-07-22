package com.example.aml.eventreminderchallenge.retrofit;

import com.example.aml.eventreminderchallenge.model.weather.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    // API for getting weather condtion for 16 days
    @GET("{type}")
    Call<WeatherResponse> getWeatherCondition(@Path("type") String type , // daily
                                               @Query("q") String q,  // city name
                                               @Query("cnt") int cnt ,  // to specify limit of numder  day
                                               @Query("appid") String appid ,  // API Token
                                               @Query("units") String units  // determine unit as metric
     );
}
