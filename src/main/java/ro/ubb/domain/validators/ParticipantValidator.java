package ro.ubb.domain.validators;

import ro.ubb.domain.BaseEntity;
import ro.ubb.domain.Participant;
import ro.ubb.repository.RepositoryException;

public class ParticipantValidator implements Validator<Participant>{
    @Override
    public void validate(Participant orice) throws ValidatorException {

    }

    @Override
    public void validateId(Integer id, Iterable<? extends BaseEntity> allEntities) throws RepositoryException {
        boolean contains = false;
        String entityType = "";
        for (BaseEntity entity: allEntities)
        {
            entityType = entity.getClass().getTypeName();
            if (entity.getIdEntity()== id) {
                contains = true;
                break;
            }
        }
        if (!contains)
            throw new RepositoryException("Entity ID does not exist in " + entityType);
    }

    @Override
    public Participant checkEmptyBeforeUpdate(Participant updated, Participant beforeUpdate) throws ValidatorException {
        if (updated.getIdEvent() == 0 || updated.getIdEvent() == null)
            updated.setIdEvent(beforeUpdate.getIdEvent());
        if (updated.getIdUser() == 0 || updated.getIdUser() == null)
            updated.setIdUser(beforeUpdate.getIdUser());
        return updated;
    }
}
