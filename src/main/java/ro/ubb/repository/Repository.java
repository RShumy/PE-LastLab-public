package ro.ubb.repository;
import ro.ubb.domain.BaseEntity;
import ro.ubb.domain.validators.ValidatorException;

import java.io.FileNotFoundException;
import java.util.Optional;

public interface Repository<ID ,Type extends BaseEntity> {
    /**
     * Saves the given entity.
     *
     * @param entity
     *            must not be null.
     * @return an {@code Optional} - null if the entity was saved otherwise (e.g. id already exists) returns the entity.
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws ValidatorException
     *             if the entity is not valid.
     */
    Optional<Type> save(Type entity) throws ValidatorException;
    /**
     * Gets a entity with a given id.
     * @param id the id
     * @return the entity with the given id, or null if none exists
     */
    Optional<Type> findOne(ID id);
    /**
     * Returns all entity.
     * @return all entity.
     */
    Iterable<Type> findAll();
    /**
     * Updates a given entity by its id.
     * @param entity the given entity.
     * @throws RepositoryException if the entity id does not exist.
     */
    Optional<Type> update(Type entity) throws RepositoryException;
    /**
     * Deletes an entity with a given id.
     * @param id the id of the entity to delete.
     * @throws RepositoryException if there is no entity with the given id.
     */
    Optional<Type> delete(ID id) throws RepositoryException, FileNotFoundException;
}


