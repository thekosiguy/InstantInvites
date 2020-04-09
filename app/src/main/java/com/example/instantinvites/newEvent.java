package com.example.instantinvites;

public class newEvent {
    String eventId;
    String name;
    String description;
    String venue;
    String startTime;
    String endTime;

    public newEvent(){}

    public newEvent(String eventId, String name, String description, String venue, String startTime, String endTime){
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.venue = venue;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getEventId(){
        return eventId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getVenue() {
        return venue;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
