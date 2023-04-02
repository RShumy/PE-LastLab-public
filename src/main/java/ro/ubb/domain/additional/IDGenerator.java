package ro.ubb.domain.additional;

import ro.ubb.domain.BaseEntity;
import ro.ubb.domain.Comment;
import ro.ubb.domain.Event;
import ro.ubb.domain.User;

public class IDGenerator<Type extends BaseEntity> {
    public static final String USER = "user";
    public static final String EVENT = "event";
    public static final String COMMENT = "comment";

    private String entityClassType;

    private static Integer userMaxId = 1;
    private static Integer eventMaxId = 1;
    private static Integer commentMaxId = 1;
    private static Integer maxId = 1;

    public static Integer generateId(Iterable<? extends BaseEntity> idSet) {
        if (!idSet.iterator().hasNext())
            return maxId;
        else {
            idSet.forEach(entity -> {
                if((Integer)entity.getIdEntity() > maxId)
                    maxId = (Integer)entity.getIdEntity();
            });
            return userMaxId+1;
        }
    }

    public static Integer generateUserId(Iterable<User> idSet) {
        if (!idSet.iterator().hasNext())
            return userMaxId;
        else {
            idSet.forEach(user -> {
                if(user.getIdEntity() > userMaxId)
                    userMaxId = user.getIdEntity();
            });
            return userMaxId+1;
        }
    }



    public static Integer generateEventId(Iterable<Event> idSet) {
        if (!idSet.iterator().hasNext())
            return eventMaxId;
        else {
            idSet.forEach(event -> {
                if(event.getIdEntity() > eventMaxId)
                    eventMaxId = event.getIdEntity();
            });
            return eventMaxId+1;
        }
    }

    public static Integer generateCommentId(Iterable<Comment> idSet) {
        if (!idSet.iterator().hasNext())
            return commentMaxId;
        else {
            idSet.forEach(comment -> {
                if(comment.getIdEntity() > commentMaxId)
                    commentMaxId = comment.getIdEntity();
            });
            return commentMaxId+1;
        }
    }
}
