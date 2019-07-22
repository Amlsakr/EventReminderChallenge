package com.example.aml.eventreminderchallenge.eventBus;

// File: Events.java
// Events class with get and set methods
public class Events {

    // updateEvent used to send message from Service to activity
    public static class updateEvent {
        private boolean update ;
        public updateEvent (boolean update){
            this.update = update ;
        }

        public boolean getUpdateEvent(){
            return update ;
        }
    }




}
