package ro.ubb.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class Event extends BaseEntity<Integer> {
    private String eventName;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private float eventPrice;
    private EventType eventType;
//    private EventStatus eventStatus;

    public Event(int idEntity) {
        super(idEntity);
    }

    public Event(){}

    public Event(int idEntity, String eventName, LocalDate eventDate, LocalTime eventTime, float eventPrice, EventType eventType) {
        super(idEntity);
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventPrice = eventPrice;
        this.eventType = eventType;
    }


    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalTime eventTime) {
        this.eventTime = eventTime;
    }

    public float getEventPrice() {
        return eventPrice;
    }

    public void setEventPrice(float eventPrice) {
        this.eventPrice = eventPrice;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "Event{" +
                " idEntity=`" + idEntity + '`' +
                ", eventName=`" + eventName + '`' +
                ", eventDate=`" + eventDate + '`' +
                ", eventTime=`" + eventTime + '`' +
                ", eventPrice=`" + eventPrice + '`' +
                ", eventType=`" + eventType + '`' +
                "}";
    }
}
