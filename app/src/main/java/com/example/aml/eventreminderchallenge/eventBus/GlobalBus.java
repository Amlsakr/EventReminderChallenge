package com.example.aml.eventreminderchallenge.eventBus;

import org.greenrobot.eventbus.EventBus;
// File: GlobalBus.java
// GlobalBus class with EventBus oject

/**
 * This is class to Makeonly single instance of event bus
 *
 * @see java.lang.Object
 * @author aml
 */
public class GlobalBus {

    private static EventBus sBus ;
    /**
     * Get the EventBus
     * It gets only instance "Singleton pattern" from Event Bus at hole project
     * @return a <code> string </code>
     * specifying the area code
     */
    public static EventBus getBus (){

        if (sBus == null) {
      sBus = EventBus.getDefault();

        }
        return sBus;
    }
}
