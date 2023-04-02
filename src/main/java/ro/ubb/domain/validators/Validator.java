package ro.ubb.domain.validators;

import ro.ubb.domain.BaseEntity;
import ro.ubb.repository.RepositoryException;

public interface Validator<Type extends BaseEntity> {
    void validate(Type orice) throws ValidatorException;
    void validateId(Integer id, Iterable<? extends BaseEntity> allEntities) throws RepositoryException;
    Type checkEmptyBeforeUpdate(Type updated, Type beforeUpdate) throws ValidatorException;
}
