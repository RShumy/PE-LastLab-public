package ro.ubb.domain.validators;

import ro.ubb.domain.BaseEntity;
import ro.ubb.domain.Comment;
import ro.ubb.repository.RepositoryException;

public class CommentValidator implements Validator<Comment> {

    @Override
    public void validate(Comment comment) throws ValidatorException {
        if (comment.getCommentMessage().length() > 200)
            throw new ValidatorException("Length exceeds 200 characters");
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
    public Comment checkEmptyBeforeUpdate(Comment comment, Comment beforeUpdate) throws ValidatorException {
        return null;
    }

}
