package com.example.aml.eventreminderchallenge.adapter;


// File: EventAdapter.java
// EventAdapter class for recycler view which display events
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.aml.eventreminderchallenge.model.Event;
import com.example.aml.eventreminderchallenge.R;
import com.example.aml.eventreminderchallenge.model.weather.DayWeather;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {
    /**
     * This is a adapter  class to display event and weather condition at each row
     *
     *
     * @see java.lang.Object
     * @author aml
     */
    ArrayList<Event> events = new ArrayList<>();  // contains all events
    Context context;
    List<DayWeather> dayWeatherList ;  // contain weather list for the next 16 coming days

    /**
     * Argument constructor with variable  initializes one of variable  to value and others to null
     * @see #EventAdapter(Context)
     */
    public EventAdapter( Context context) {
        this.context = context;
        }

    /**
     * Sets the weatherlis for the next 16 days and notify adapter that new data added to it
     * @param dayWeatherList the weather list code
     */
    public void setDayWeatherList(List<DayWeather> dayWeatherList) {
        this.dayWeatherList = dayWeatherList;
        notifyDataSetChanged();
    }

    /**
     * Sets the events for the events list  and notify adapter that new data added to it
     * @param events the weather list
     */
    public void setEvents(ArrayList<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    /**
     * Gets the view holder for row  at recycler view
     * @return a object of MyViewHolder which contain views for row
     * the extension code
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // create a new view
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_item, viewGroup, false);
        return new MyViewHolder(v);
    }

    /**
     * Sets the dat at view of row  from event list and weather list
     * @param myViewHolder the view holder for each row
     * @param position the position for each row
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        // get the event which should display at specific row
        Event event = events.get(position);
        myViewHolder.title.setText(event.getTitle());
        myViewHolder.name.setText(event.getDisplayName());


        if (event.getDescription() != null) {
          if (!event.getDescription().isEmpty()) {
              myViewHolder.description.setVisibility(View.VISIBLE);
              myViewHolder.description.setText(event.getDescription());
          }else
          myViewHolder.description.setVisibility(View.GONE);

      } else {
          myViewHolder.description.setVisibility(View.GONE);
      }
// status of event may be TENTATIVE, CONFIRMED, CONFIRMED
        if (event.getStatus().equals("0")){
            myViewHolder.status.setText("TENTATIVE");
        } else if (event.getStatus().equals("1")){
            myViewHolder.status.setText("CONFIRMED");
        } else if (event.getStatus().equals("2")){
            myViewHolder.status.setText("CONFIRMED");
        }


        if (event.getLocation() != null) {
            if (!event.getLocation().isEmpty()) {
                myViewHolder.location.setVisibility(View.VISIBLE);
                myViewHolder.location.setText(event.getLocation());
            }else
                myViewHolder.location.setVisibility(View.GONE);

        } else {
            myViewHolder.location.setVisibility(View.GONE);
        }



        //Event may be all day or at specicfic time
        if (event.getAllDay().equals("1")) {
            myViewHolder.allDay.setText("ALL DAY");

        }else  if (event.getAllDay().equals("0")) {
            myViewHolder.allDay.setText("FIXED");
        }

        myViewHolder.startTime.setText(getDateFromDate(event.getStartTime()));
        myViewHolder.endTime.setText(getDateFromDate(event.getEndTime()));

        // I only display weather condition If the event date occur at the next 16 days
        int day = event.getRemainingDays();
        if (day<= 16 && day >=0) {
            if (day ==0)
                day =1 ;
            else
                day = day -1;
            myViewHolder.LL_weatherCondition.setVisibility(View.VISIBLE);
          if (dayWeatherList!= null){
              myViewHolder.weatherCondition.setText(dayWeatherList.get(day).getWeather().get(0).getDescription());
                myViewHolder.temperature.setText(String.valueOf(dayWeatherList.get(day).getTemp().getDay()) + "°C");
                myViewHolder.humidity.setText(String.valueOf(dayWeatherList.get(day).getHumidity())+"%");
                Glide.with(context).load("https://openweathermap.org/img/w/"
                        + dayWeatherList.get(day).getWeather().get(0).getIcon()+ ".png").into(myViewHolder.weatherStateIcon);
            } }else  {
            myViewHolder.LL_weatherCondition.setVisibility(View.GONE);
        }
    }

    /**
     * Sets the dat at view of row  from event list and weather list
     * @return the size of list of events
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

    // File: MyViewHolder.java
    // MyViewHolder class with views to display events and weather condition
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView name;

        public TextView location;
        public TextView description;
        public TextView status;
        public TextView allDay;
        public TextView startTime;
        public TextView endTime;
        public LinearLayout LL_weatherCondition ;
        public TextView weatherCondition;
        public TextView temperature;
        public  TextView humidity;
        public  ImageView weatherStateIcon;

        /**
         * Argument constructor initializes itemView
         * @param itemView
         */

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            name = itemView.findViewById(R.id.name);

            location = itemView.findViewById(R.id.location);
            description = itemView.findViewById(R.id.description);
            status = itemView.findViewById(R.id.status);
            allDay = itemView.findViewById(R.id.allDay);
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.endTime);
            weatherCondition = itemView.findViewById(R.id.weatherCondition);
            temperature = itemView.findViewById(R.id.temperature);
            humidity = itemView.findViewById(R.id.humidity);
            weatherStateIcon = itemView.findViewById(R.id.weatherStateIcon);
            LL_weatherCondition = itemView.findViewById(R.id.LL_weatherCondition);

        }
    }

    /**
     * Gets the date at specific format "dd-MMM-yyyy HH:mm"
     *
     * @return a date at instance of object from String
     *
     */
    public String getDateFromDate(String time) {
        String inputPattern = "EE MMM dd HH:mm:ss z yyyy";
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
}
