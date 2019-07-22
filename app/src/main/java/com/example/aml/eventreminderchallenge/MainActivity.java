package com.example.aml.eventreminderchallenge;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.aml.eventreminderchallenge.adapter.EventAdapter;
import com.example.aml.eventreminderchallenge.eventBus.Events;
import com.example.aml.eventreminderchallenge.eventBus.GlobalBus;
import com.example.aml.eventreminderchallenge.model.Event;
import com.example.aml.eventreminderchallenge.model.weather.City;
import com.example.aml.eventreminderchallenge.model.weather.DayWeather;
import com.example.aml.eventreminderchallenge.model.weather.WeatherResponse;
import com.example.aml.eventreminderchallenge.retrofit.APIClient;
import com.example.aml.eventreminderchallenge.retrofit.ApiInterface;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_CALENDAR;
import static android.Manifest.permission.WRITE_CALENDAR;

public class MainActivity extends AppCompatActivity {
    RecyclerView RV;
    ArrayList<Event> eventArrayList;
    List<DayWeather> dayWeatherList = new ArrayList<>();
    private static final int PERMISSION_REQUEST_CODE = 200;
    EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalBus.getBus().register(this);
        RV = findViewById(R.id.RV);
        eventAdapter = new EventAdapter(this);

        // only load weather condition for all 16 daqys then if the event occurs at next 16
        // days I diplay weather condition
        loadWeatherCondition();
        // ask permission for read and write calendar
        boolean permissionGranted = checkPermission();
        if (permissionGranted) {
            readFromCalendar();
            Intent i = new Intent(MainActivity.this, GetLastEvents.class);
            this.startService(i);

        } else {
            requestPermission();
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CALENDAR);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_CALENDAR);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{READ_CALENDAR, WRITE_CALENDAR}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted) {
                        readFromCalendar();
                        Intent i = new Intent(MainActivity.this, GetLastEvents.class);
                        this.startService(i);
                    } else {

                        Toast.makeText(MainActivity.this,
                                "Permission Denied, You cannot access Calendar", Toast.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(READ_CALENDAR)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{READ_CALENDAR, WRITE_CALENDAR},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /**
     * Gets the date from content provider Using Calendar
     */
    public void readFromCalendar() {
        CalendarContentResolver calendarContentResolver = new CalendarContentResolver(MainActivity.this);
        Set<String> calendars = calendarContentResolver.getCalendars();
        Log.e("freeee", calendars.size() + "size");
        String[] projection = new String[]{
                CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.CALENDAR_DISPLAY_NAME,
                CalendarContract.Events.DURATION,
                CalendarContract.Events.STATUS};

        Calendar startTime = Calendar.getInstance();
        startTime.setTime(Calendar.getInstance().getTime());
        Calendar endTime = Calendar.getInstance();
        endTime.set(2019, 11, 01, 00, 00);
        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ))";
        Cursor cursor = this.getBaseContext().
                getContentResolver().
                query(CalendarContract.Events.CONTENT_URI, projection, selection, null, CalendarContract.Events.DTSTART + " ASC");

        eventArrayList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setCalendarID(cursor.getString(0));
                event.setTitle(cursor.getString(1));
                event.setDescription(cursor.getString(2));
                event.setStartTime((new Date(cursor.getLong(3))).toString());
                event.setEndTime((new Date(cursor.getLong(4))).toString());
                event.setAllDay(cursor.getString(5));
                event.setLocation(cursor.getString(6));
                event.setDisplayName(cursor.getString(7));
                event.setDuration(cursor.getString(8));
                event.setStatus(cursor.getString(9));

                int day = getSequenceOfNext16days(event.getStartTime());
                event.setRemainingDays(day);
                eventArrayList.add(event);
            } while (cursor.moveToNext());
        }
        eventAdapter.setEvents(eventArrayList);
        RV.setAdapter(eventAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
        RV.setLayoutManager(llm);
        Log.e("rrrr", "Rrrr");
        }


    /**
     * Load weather condition
     */
    private void loadWeatherCondition() {
        String cityName = "Cairo";


        String apiKey = "28ea27a52136a836d86ce454ba6c0786";
        String units = "metric";
        ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);
        Call<WeatherResponse> call = apiInterface.getWeatherCondition(
                "daily", cityName, 16, apiKey, units);

        Log.e("url", call.request().url().toString());

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                WeatherResponse weatherResponse = response.body();
                if (weatherResponse != null) {
                    City city = weatherResponse.getCity();
                    dayWeatherList
                            = weatherResponse.getDayWeatherList();
                    eventAdapter.setDayWeatherList(dayWeatherList);
                    eventAdapter.notifyDataSetChanged();

                    Log.e("weather", new Gson().toJson(weatherResponse));
                    Log.e("weather", "weather" + String.valueOf(
                            dayWeatherList.get(0).getHumidity()));
                    Log.e("weather", String.valueOf(
                            dayWeatherList.get(0).getTemp().getDay()));

                    Log.e("weather", dayWeatherList.get(0)
                            .getWeather().get(0).getDescription());
                    }
            }
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                t.printStackTrace();
                Log.e("weather", t.getMessage());
            }
        });


    }


    private int getSequenceOfNext16days(String startDate) {
        String eventStartDate = getDateFromDate(startDate);
        String currentDate = getCurrentDate();
        return differentiateDays(currentDate, eventStartDate);
    }

    /**
     * Gets the current date
     *
     * @return a date at instance of object from String
     *
     */
    public String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        Log.e("current date", formattedDate);

        return formattedDate;
    }

    /**
     * Gets the date at specific format "dd-MMM-yyyy HH:mm"
     *
     * @return a date at instance of object from String
     *
     */
    public String getDateFromDate(String time) {
        //   String inputPattern = "EEE MM dd 'T'HH:mm:ss SSS'Z' yyyy";
        String inputPattern = "EE MMM dd HH:mm:ss z yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                Locale.ENGLISH);
        String DoutputPattern = "dd-MMM-yyyy HH:mm";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat DoutputFormat = new SimpleDateFormat(DoutputPattern);

        Date date = null;
        String DStr = null;

        try {
            date = inputFormat.parse(time);

            DStr = DoutputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DStr;
    }

    /**
     * Gets the difference of days
     * to differentiat the date of event from current date
     * @return  int difference of the day
     */


    public int differentiateDays(String currentDate, String eventStartDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        try {
            Date date1 = simpleDateFormat.parse(currentDate);
            Date date2 = simpleDateFormat.parse(eventStartDate);
            int differentiateday = getDifference(date1, date2);
            return differentiateday;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
        }


    public int getDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;

        return (int) elapsedDays;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEvent(Events.updateEvent updateEvent) {
        Log.e("resultService", "updateEvent" + "");
        readFromCalendar();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent i = new Intent(MainActivity.this, GetLastEvents.class);
        this.stopService(i);
        GlobalBus.getBus().unregister(this);
    }
}
