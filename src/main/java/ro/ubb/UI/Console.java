package ro.ubb.UI;

import ro.ubb.domain.Comment;
import ro.ubb.domain.Event;
import ro.ubb.domain.EventType;
import ro.ubb.domain.User;
import ro.ubb.service.CommentService;
import ro.ubb.service.EventService;
import ro.ubb.service.UserService;

import java.util.Scanner;

public class Console {
    private UserService userService;
    private EventService eventService;
    private CommentService commentService;
    Scanner scan = new Scanner(System.in);

    public Console(UserService userService, EventService eventService, CommentService commentService) {
        this.userService = userService;
        this.eventService = eventService;
        this.commentService = commentService;
    }


    public void printMenu() {
        System.out.println(
                "a. List all users      || b. List all events     || c. List all comments     || d. List all Participants\n" +
                "1. Add new user        || 2. Add new event       || 3. Add new comment       || 4. Add new User Participant\n" +
                "5. Delete user         || 6. Delete event        || 7. Delete comment        || 8. Delete Participant\n" +
                "9. Find user by id     || 10. Find event by id   || 11. Find comment by id   || 12. Find Participant\n" +
                "11. Update user of id  || 12. Update event of id || 13. Update comment of id || 14. Update Participant\n" +
                "15. Filter User        || 16. Filter Event       || 17. Filter Comment\n" +
                "X. Exit");
    }

    public void runMenu(){

        Boolean exit = false;
        while(!exit){
            printMenu();
            String option = scan.nextLine();

            switch (option) {
                case "a":
                    handlePrintUsers();
                    break;
                case "b":
                    handlePrintEvents();
                    break;
                case "c":
                    handlePrintComments();
                    break;
                case "1":
                    handleAddUser();
                    break;
                case "2":
                    handleAddEvent();
                    break;
                case "3":
                    handleAddComment();
                    break;
                case "4":
                    handleDeleteUser();
                    break;
                case "5":
                    handleDeleteEvent();
                    break;
                case "6":
                    handleDeleteComment();
                    break;
                case "7":
                    handleFindUser();
                    break;
                case "8":
                    handleFindEvent();
                    break;
                case "9":
                    handleFindComment();
                    break;
                case "10":
                    handleUpdateUser();
                    break;
                case "11":
                    handleUpdateEvent();
                    break;
                case "12":
                    handleUpdateComment();
                    break;
                case "13":
                    handleFilterUser();
                    break;
                case "14":
//                    handleFilterUser();
                    break;
                case "15":
//                    handleFilterUser();
                    break;
                case "X":
                    exit = true;
                    break;
                default:
                    System.out.println("this option is not yet implemented");
            }
        }

    }

    private void handleFilterUser() {
        System.out.println("Choose the Filter Type\n" +
                "a. Contains in Name  || b. Contains in User Name || c. Has email from domain");
        String option = scan.nextLine();
        String filter;
        switch (option) {
            case "a":
                System.out.println("Type the string contained in the Name:");
                filter = scan.nextLine();
                System.out.println(userService.filterUsers(user -> user.getName().contains(filter)));
                break;
            case "b":
                System.out.println("Type the string contained in the User Name:");
                filter = scan.nextLine();
                System.out.println(userService.filterUsers(user -> user.getUserName().contains(filter)));
                break;
            case "c":
                System.out.println("Type the domain after the @ sign:");
                filter = scan.nextLine();
                System.out.println(userService.filterUsers(user -> user.getEmail().replaceAll("^.+@", "").contains(filter)));
                break;
            default:
                System.out.println("Nothing was selected from the options. Exit by default");
                break;
        }
    }

    private void handleUpdateComment() {
        System.out.println("Enter Comment ID to modify message:");
        Integer commentID = scan.nextInt();
        scan.nextLine();

        Comment comment = commentService.findComment(commentID).get();
        System.out.println("Enter new Comment message:");
        String commentMessage = scan.nextLine();

        commentService.updateComment(commentID, commentMessage);
    }

    private void handleUpdateEvent() {
        try {
            System.out.println("Enter Event ID that you want to update:");
            Integer id = scan.nextInt();
            scan.nextLine();

            // check id before giving the other values to update
            Event event = eventService.findEvent(id).get();
            System.out.println(event + "\n");

            System.out.println("Enter new Event name:");
            String eventName = scan.nextLine();


            System.out.println("Enter new Event date: ");
            String eventDate = scan.nextLine();
            if (eventDate.equals("0") || eventDate.isEmpty() || eventDate.equals("\n"))
                eventDate = String.valueOf(event.getEventDate());

            System.out.println("Enter new Event time: ");
            String eventTime = scan.nextLine();
            if (eventTime.equals("0") || eventTime.isEmpty() || eventTime.equals("\n"))
                eventTime = String.valueOf(event.getEventTime());

            System.out.println("Enter new Event price: ");
            float eventPrice = scan.nextFloat();
            scan.nextLine();

            EventType eventType = chooseEventType("update",event.getEventType());

            eventService.updateEvent(id,eventName,eventDate,eventTime,eventPrice,eventType);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleUpdateUser() {
        try {
            System.out.println("Enter User ID that you want to update:");
            Integer id = scan.nextInt();
            scan.nextLine();

            // check id before giving the other values to update
            User user = userService.findUser(id).get();
            System.out.println(user + "\n");

            System.out.println("Enter new User name:");
            String name = scan.nextLine();

            System.out.println("Enter new User username:");
            String userName = scan.nextLine();

            System.out.println("Enter new User password:");
            String password = scan.nextLine();

            System.out.println("Enter new User e-mail:");
            String email = scan.nextLine();

            userService.updateUser(id, name, userName, password, email);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleFindComment() {
        System.out.println("Find comment id:");
        Integer id = scan.nextInt();
        scan.nextLine();
        System.out.println(commentService.findComment(id));
    }

    private void handleFindEvent() {
        System.out.println("Find event id:");
        Integer id = scan.nextInt();
        scan.nextLine();
        System.out.println(eventService.findEvent(id));
    }

    private void handleFindUser() {
        System.out.println("Find user id:");
        Integer id = scan.nextInt();
        scan.nextLine();
        System.out.println(userService.findUser(id));
    }

    private void handleDeleteComment() {
        System.out.println("Delete comment ID:");
        Integer id = scan.nextInt();
        scan.nextLine();
        commentService.deleteComment(id);
    }

    private void handleDeleteEvent() {
        System.out.println("Delete event ID:");
        Integer id = scan.nextInt();
        scan.nextLine();
        eventService.deleteEvent(id);
    }

    private void handleDeleteUser() {
        System.out.println("Delete user ID, read User IDs");
        Integer id = scan.nextInt();
        scan.nextLine();
        userService.deleteUser(id);
    }


    private void handleAddComment() {
        try{

            System.out.println("Enter Participant Id:");
            Integer idParticipant = scan.nextInt();
            scan.nextLine();

            System.out.println("Enter comment:");
            String commentMessage = scan.nextLine();

            commentService.addComment(idParticipant, commentMessage);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleAddEvent() {
        try {

            System.out.println("Enter Event name: ");
            String eventName = scan.nextLine();

            System.out.println("Enter Event date: ");
            String eventDate = scan.nextLine();

            System.out.println("Enter Event time: ");
            String eventTime = scan.nextLine();

            System.out.println("Enter Event price: ");
            float eventPrice = scan.nextFloat();
            scan.nextLine();

            EventType eventType = chooseEventType("add", EventType.Online);

            eventService.addEvent(eventName, eventDate, eventTime, eventPrice, eventType);

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private EventType chooseEventType(String operationType, EventType eventType) {
        String addMessage = "Choose Event Type:\n" +
                "Press 1 for Online\n" +
                "Press 2 for Hybrid\n" +
                "Press 3 for Face_to_Face";
        if (operationType.equals("update"))
            System.out.println("Press 0 or Enter to keep the previous Value" + eventType);

        System.out.println(addMessage);

        String option = scan.nextLine();

        if (operationType.equals("update") && (option.equals("0") || option.equals("\n") || option.isEmpty()))
            return eventType;
        if (option.equals("1")) return eventType = EventType.Online;
        if (option.equals("2")) return eventType = EventType.Hybrid;
        if (option.equals("3")) return eventType = EventType.Face_to_Face;
        if (eventType != null) System.out.println("Event Type has been set to Default: Online");
        return eventType;
    }

    private void handlePrintComments() {
        System.out.println("All comments:");
        System.out.println(commentService.getAllComments());
    }

    private void handlePrintEvents() {
        System.out.println("All events:");
        eventService.getAllEvents().forEach(event -> {
            System.out.println(event);
        });
    }

    private void handleAddUser() {
        try {
//            System.out.println("Enter id:");
//            int userId = scan.nextInt();
//            scan.nextLine();

            System.out.println("Enter name:");
            String name = scan.nextLine();

            System.out.println("Enter userName: ");
            String userName = scan.nextLine();

            System.out.println("Enter password: ");
            String password = scan.nextLine();

            System.out.println("Enter email: ");
            String email = scan.nextLine();

            userService.addUser(name, userName, password, email);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void handlePrintUsers() {
        System.out.println("All users:");
        userService.getAllUsers().forEach(user -> {
            System.out.println(user);
        });
    }


}
