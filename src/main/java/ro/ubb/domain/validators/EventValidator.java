package ro.ubb.domain.validators;

import ro.ubb.domain.*;
import ro.ubb.repository.RepositoryException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventValidator implements Validator<Event> {

    private Pattern PATTERN = Pattern.compile("[^A-Za-z0-9-\\.!?/\\\\'*&+@=%$<>{}\\[]#:`~\"]");

    @Override
    public void validate(Event event) throws ValidatorException {
        if (event.getEventName().isEmpty()) throw new ValidatorException("Event Name cannot be NULL(Empty)");
        Matcher match = PATTERN.matcher(event.getEventName());
        if (match.find())
            throw new ValidatorException("Contains Special characters that are not allowed");
        if (event.getEventName().length() > 20)
            throw new ValidatorException("Event name cannot exceed 20 characters");
        if (event.getEventDate().isBefore(LocalDate.now()) || (event.getEventDate().isEqual(LocalDate.now()) && event.getEventTime().isBefore(LocalTime.now())) )
            throw new ValidatorException("Cannot create an Event with a past date or time");
        if (event.getEventPrice() < 0)
            throw new ValidatorException("Price cannot be lower than 0");
    }

    @Override
    public void validateId (Integer entityId, Iterable<? extends BaseEntity> allEntities) throws RepositoryException {
        boolean contains = false;
        String entityType = "";
        for (BaseEntity entity: allEntities)
        {
            entityType = entity.getClass().getTypeName();
            if (entity.getIdEntity()== entityId) {
                contains = true;
                break;
            }
        }
        if (!contains)
            throw new RepositoryException("Entity ID does not exist in " + entityType);
    }

    @Override
    public Event checkEmptyBeforeUpdate(Event updated, Event beforeUpdate) throws ValidatorException {
        String eventName = updated.getEventName();
        Float eventPrice = updated.getEventPrice();
        if (eventName.equals("0") || eventName.isEmpty() || eventName.matches("\n"))
            updated.setEventName(beforeUpdate.getEventName());
        if (eventPrice == 0 || eventPrice == null)
            updated.setEventPrice(beforeUpdate.getEventPrice());
        return updated;
    }
}
