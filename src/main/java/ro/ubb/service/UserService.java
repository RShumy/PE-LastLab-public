package ro.ubb.service;

import ro.ubb.domain.User;
import ro.ubb.domain.validators.Validator;
import ro.ubb.repository.Repository;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

public class UserService {
    private Repository<Integer, User> userRepository;
    private Validator<User> userValidator;

    public UserService(Repository<Integer, User> userRepository, Validator<User> userValidator) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    public void addUser ( String name, String userName, String password, String email) throws Exception {
        User newUser = new User(1,name, userName, password, email);
        userValidator.validate(newUser);
        userRepository.save(newUser);
    }

    public List<User> getAllUsers () {
        return (List<User>) userRepository.findAll();
    }

    public void deleteUser(Integer id){
        try {
            if (userRepository.findOne(id).isPresent())
                userRepository.delete(id);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Optional<User> findUser(Integer id) {
        userValidator.validateId(id,userRepository.findAll());
        return userRepository.findOne(id);
    }

    public void updateUser(Integer id, String updatedName, String updatedUserName,
                                     String updatedPassword, String updatedEmail) {
        User updatedUser = new User(id, updatedName, updatedUserName, updatedPassword, updatedEmail);
        updatedUser = userValidator.checkEmptyBeforeUpdate(updatedUser, userRepository.findOne(id).get());
        userValidator.validate(updatedUser);
        userRepository.update(updatedUser);
    }

    public List<User> filterUsers (Predicate<User> userTruePredicate){
        Iterable<User> users = userRepository.findAll();
        Stream<User> userStream = stream(users.spliterator(),false);
        Stream<User> filteredStream = userStream.filter(userTruePredicate);
        return filteredStream.collect(Collectors.toList());
    }


}
