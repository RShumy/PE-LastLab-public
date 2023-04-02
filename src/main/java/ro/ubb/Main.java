package ro.ubb;

import ro.ubb.UI.Console;
import ro.ubb.domain.*;
import ro.ubb.domain.validators.*;
import ro.ubb.repository.DataBaseRepository;
import ro.ubb.repository.Repository;
import ro.ubb.service.CommentService;
import ro.ubb.service.EventService;
import ro.ubb.service.ParticipantService;
import ro.ubb.service.UserService;

public class Main {

    public static void main(String[] args) throws NoSuchMethodException {
        System.out.println("Hey");


        final String URL = System.getProperty("url");
        final String USER = System.getProperty("username");
        final String PASSWORD = System.getProperty("password");


//        Repository<Integer, User> userRepository = new FileRepository("NewUser.txt");
//        Repository<Integer, Event> eventRepository = new FileRepository("NewEvent.txt");
//        Repository<Integer, Comment> commentRepository = new FileRepository("NewComment.txt");

//        Repository<Integer, User> userRepository = (Repository<Integer, User>) RepositoryConfig.createRepository("NewUser.txt");
//        Repository<Integer, Event> eventRepository = (Repository<Integer, Event>) RepositoryConfig.createRepository("NewEvent.txt");
//        Repository<Integer, Comment> commentRepository = (Repository<Integer, Comment>) RepositoryConfig.createRepository("NewComment.txt");

//        Repository<Integer, User> userRepository = new XMLRepository("User.xml");
//        Repository<Integer, Event> eventRepository = new XMLRepository("Event.xml");
//        Repository<Integer, Comment> commentRepository = new XMLRepository("Comment.xml");

        Repository<Integer, User> userRepository = new DataBaseRepository<>(User.class,URL,USER,PASSWORD);
        Repository<Integer, Event> eventRepository = new DataBaseRepository<>(Event.class,URL,USER,PASSWORD);
        Repository<Integer, Comment> commentRepository = new DataBaseRepository<>(Comment.class,URL,USER,PASSWORD);
        Repository<Integer, Participant> participantRepository = new DataBaseRepository<>(Participant.class,URL,USER,PASSWORD);


        Validator<User> userValidator = new UserValidator();
        Validator<Event> eventValidator = new EventValidator();
        Validator<Participant> participantValidator = new ParticipantValidator();
        Validator<Comment> commentValidator = new CommentValidator();

        UserService userService = new UserService(userRepository, userValidator);
        EventService eventService = new EventService(eventRepository, eventValidator);
        ParticipantService participantService = new ParticipantService(participantRepository, participantValidator);
        CommentService commentService = new CommentService(commentRepository, participantRepository, commentValidator);

        Console main = new Console(userService, eventService, commentService);
        main.runMenu();

    }




}
