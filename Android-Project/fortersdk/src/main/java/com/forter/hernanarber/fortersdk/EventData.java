package com.forter.hernanarber.fortersdk;

public class EventData {

    final String name;
    final Object value;
    final long takenAt;
    final String deviceUID;

    public EventData(String name, Object value, String deviceUID) {
        this.name = name;
        this.value = value;
        this.deviceUID = deviceUID;
        this.takenAt = System.currentTimeMillis();
    }



}
