package ro.ubb.service;

import ro.ubb.domain.*;
import ro.ubb.domain.validators.Validator;
import ro.ubb.domain.additional.DateTimeValidatorAndParser;
import ro.ubb.repository.Repository;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class EventService {

    private Repository<Integer, Event> eventRepository;
    private Validator<Event> eventValidator;

    public EventService(Repository<Integer, Event> eventRepository, Validator<Event> eventValidator) {
        this.eventRepository = eventRepository;
        this.eventValidator = eventValidator;
    }

    public void addEvent (String eventName, String eventDate, String eventTime, float eventPrice, EventType eventType) throws Exception {
        LocalDate eventDateParsed = DateTimeValidatorAndParser.parseDate(eventDate);
        LocalTime eventTimeParsed = DateTimeValidatorAndParser.parseTime(eventTime);

        Event newEvent = new Event(1, eventName, eventDateParsed, eventTimeParsed, eventPrice, eventType);
        eventValidator.validate(newEvent);
        eventRepository.save(newEvent);
    }

    public void updateEvent(Integer id, String eventName, String eventDate, String eventTime, float eventPrice, EventType eventType){
        LocalDate eventDateParsed = DateTimeValidatorAndParser.parseDate(eventDate);
        LocalTime eventTimeParsed = DateTimeValidatorAndParser.parseTime(eventTime);

        Event updatedEvent = new Event (id, eventName,eventDateParsed,eventTimeParsed,eventPrice,eventType);
        updatedEvent = eventValidator.checkEmptyBeforeUpdate(updatedEvent,eventRepository.findOne(id).get());
        eventValidator.validate(updatedEvent);
        eventRepository.update(updatedEvent);
    }

    public List<Event> getAllEvents () {
        return (List<Event>) eventRepository.findAll();
    }

    public void deleteEvent(Integer id){
        try {
            if (eventRepository.findOne(id).isPresent())
                eventRepository.delete(id);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Optional<Event> findEvent(Integer id) {
        eventValidator.validateId(id,eventRepository.findAll());
        return eventRepository.findOne(id);
    }
}
