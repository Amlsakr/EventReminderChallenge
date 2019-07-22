package com.example.aml.eventreminderchallenge;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.example.aml.eventreminderchallenge.eventBus.Events;
import com.example.aml.eventreminderchallenge.eventBus.GlobalBus;
import java.util.Timer;
import java.util.TimerTask;

// Service runs at each 30 seconds to get the updates
public class GetLastEvents extends Service {

    private Timer timer = new Timer();
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Events.updateEvent updateEvent= new Events.updateEvent(true);
                GlobalBus.getBus().post(updateEvent);
            }
        }, 0, 30000);//30 seconds
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }




}
