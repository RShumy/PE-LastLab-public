package ro.ubb.domain;

import java.time.LocalDate;

public class Comment extends BaseEntity<Integer>{
    private Integer idParticipant;
    private LocalDate commentDate;
    private String commentMessage;

    public Comment(int idEntity) {
        super(idEntity);
    }

    public Comment(){}

    public Comment(Integer idEntity, Integer idParticipant, LocalDate commentDate ,String commentMessage) {
        super(idEntity);
        this.idParticipant = idParticipant;
        this.commentDate = commentDate;
        this.commentMessage = commentMessage;
    }

    public Integer getIdParticipant() {
        return idParticipant;
    }

    public void setIdParticipant(Integer idParticipant) {
        this.idParticipant = idParticipant;
    }

    public LocalDate getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(LocalDate commentDate) {
        this.commentDate = commentDate;
    }

    public String getCommentMessage() {
        return commentMessage;
    }

    public void setCommentMessage(String comment) {
        this.commentMessage = commentMessage;
    }

    @Override
    public String toString() {
        return "Comment{" +
                " idEntity=`" + idEntity + '`' +
                ", idUser=`" + idParticipant + '`' +
                ", commentDate=`" + commentDate + '`' +
                ", commentMessage=`" + commentMessage + '`' +
                "}";
    }
}
