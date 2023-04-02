package ro.ubb.service;

import ro.ubb.domain.*;
import ro.ubb.domain.additional.IDGenerator;
import ro.ubb.domain.validators.CommentValidator;
import ro.ubb.domain.validators.Validator;
import ro.ubb.domain.validators.ValidatorException;
import ro.ubb.repository.Repository;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CommentService {

    private Repository<Integer, Comment> commentRepository;
    private Repository<Integer, Participant> participantRepository;

    private Validator<Comment> commentValidator;

    public CommentService(Repository<Integer, Comment> commentRepository, Repository<Integer, Participant> participantRepository, Validator commentValidator) {
        this.commentRepository = commentRepository;
        this.participantRepository = participantRepository;
        this.commentValidator = (CommentValidator) commentValidator;
    }

    public void addComment (Integer idParticipant, String commentMessage) throws ValidatorException {
        // Replace all above tilda quotes ` from commentMessage to normal single quotes
        commentMessage = commentMessage.replace("`","'");
        Comment newComment = new Comment(IDGenerator.generateCommentId(commentRepository.findAll()), idParticipant, LocalDate.now(), commentMessage);
        commentValidator.validate(newComment);
        // Validate idUser
        commentValidator.validateId(idParticipant, participantRepository.findAll());
        commentRepository.save(newComment);
    }

    public List<Comment> getAllComments () {
        return (List<Comment>) commentRepository.findAll();
    }

    public void deleteComment(Integer id){
        try {
            if (commentRepository.findOne(id).isPresent())
                commentRepository.delete(id);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Optional<Comment> findComment(Integer id){
        try{
            commentValidator.validateId(id,commentRepository.findAll());
            return commentRepository.findOne(id);
        } catch (ValidatorException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    public void updateComment(Integer commentID, String commentMessage) {
        Comment beforeUpdate = commentRepository.findOne(commentID).get();
        Comment comment = new Comment(commentID, beforeUpdate.getIdParticipant(), beforeUpdate.getCommentDate(), commentMessage);
        commentValidator.validate(comment);
        commentRepository.update(comment);
    }
}
