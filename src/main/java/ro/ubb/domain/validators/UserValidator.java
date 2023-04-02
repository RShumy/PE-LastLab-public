package ro.ubb.domain.validators;

import ro.ubb.domain.BaseEntity;
import ro.ubb.domain.User;
import ro.ubb.repository.RepositoryException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidator implements Validator<User> {

    private Pattern PATTERN = Pattern.compile("[^A-Za-z0-9_]");
    /* Regex scris de Student
     private String emailRegex = "^[A-Za-z0-9_]"+"\\."+"[A-Za-z0-9_]"+"@"+"[A-Za-z0-9]"+"\\."+"\\w{3}";
    */
    String emailRegex = "^[a-zA-Z0-9]+"+"(?:[a-zA-Z0-9._]+)@" +
            "(?:[a-zA-Z0-9-]{1,}\\.)"+"\\w{2,7}$";

    private String passRegEx = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])" +
            "(?=.*?[\\.$#-()!@%~`':;,<>\\\\?/{}\\|\\[\\]]).{6,20}$";

    private String nameRegEx = "^[A-Za-z]{1,}(?:[\\sA-Za-z]{1,})";

    @Override
    public void validate(User user) throws ValidatorException {
        if (user.getUserName().isEmpty()) throw new ValidatorException("Event Name cannot be NULL(Empty)");
        Matcher match = PATTERN.matcher(user.getUserName());
        // match.find() searches in the RegEx Pattern to validate the userName
        if (match.find())
            throw new ValidatorException("Contains Special characters that are not allowed!");
        if (user.getUserName().length() > 25)
            throw new ValidatorException("User Name cannot exceed 25 characters!");
        Pattern matchEmail = Pattern.compile(user.getEmail());
        if (!user.getEmail().matches(emailRegex))
            //if (!user.getEmail().contains(emailRegex))
            throw new ValidatorException("Not a valid email format!");
        Pattern passMatcher = Pattern.compile(passRegEx);
        if (!user.getPassword().matches(passRegEx))
            throw new ValidatorException("Password must have a minimum length of 6 and maximum of 20 and must contain at least:\n" +
                    "1 lowercase letter\n" +
                    "1 UPPERCASE letter\n" +
                    "1 Special character !@#$%^&*()`~<>/:;");
        Pattern nameMatcher = Pattern.compile(nameRegEx);
        if (!user.getName().matches(nameRegEx))
            throw new ValidatorException("Name must contain only letters and space");
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
    public User checkEmptyBeforeUpdate(User updateUser, User beforeUpdate){
        String name = updateUser.getName();
        String userName = updateUser.getUserName();
        String password = updateUser.getPassword();
        String email = updateUser.getEmail();
        if (name.equals("0") || name.isEmpty() || name.matches("\n"))
            updateUser.setName(beforeUpdate.getName());
        if (userName.equals("0") || userName.isEmpty() || userName.matches("\n"))
            updateUser.setUserName(beforeUpdate.getUserName());
        if (password.equals("0") || password.isEmpty() || password.matches("\n"))
            updateUser.setPassword(beforeUpdate.getPassword());
        if (email.equals("0") || email.isEmpty() || email.matches("\n"))
            updateUser.setEmail(beforeUpdate.getEmail());
        return updateUser;
    }
}
