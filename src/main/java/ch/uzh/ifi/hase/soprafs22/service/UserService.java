package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.time.*;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User getSingleUserById(long id) {
        User userById = userRepository.findById(id);

        // throw error if user does not exist
        if (userById == null) {
            String baseErrorMessage = "The user with id %d was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, id));
        }
        return userById;
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setLogged_in(true);
        newUser.setCreationDate(LocalDate.now());

        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User loginUser(User existingUser) {
        User userByUsername = userRepository.findByUsername(existingUser.getUsername());

        if (userByUsername == null) { //check if a user with the provided username exists
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The username provided does not exist. Please register first.");
        }
        else if (!existingUser.getPassword().equals(userByUsername.getPassword())) { //given a user with the provided username exists, check if the provided password is correct
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The password provided is not correct. Please try again.");
        }

        userByUsername.setToken(UUID.randomUUID().toString());
        userByUsername.setLogged_in(true);
        userByUsername = userRepository.save(userByUsername);
        userRepository.flush();

        return userByUsername;
    }

    public User logoutUser(User existingUser, Long id) {

        // check if user is already logged out
        if (!existingUser.getLogged_in()) {
            String baseErrorMessage = "The user with id %d is already logged out";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, id));
        }

        // logout user
        existingUser.setLogged_in(false);
        existingUser = userRepository.save(existingUser);
        userRepository.flush();

        return existingUser;
    }

    public User updateUser(User userToBeUpdated, User userUpdateRequest) {

        // update birthday if provided birthday is not null
        if (userUpdateRequest.getBirthday() != null) {
            userToBeUpdated.setBirthday(userUpdateRequest.getBirthday());
        }

        // update username if provided username is not null
        if (userUpdateRequest.getUsername() != null) {
            userToBeUpdated.setUsername(userUpdateRequest.getUsername());
        }

        // saves the given entity but data is only persisted in the database once
        // flush() is called
        userToBeUpdated = userRepository.save(userToBeUpdated);
        userRepository.flush();

        log.debug("Updated Information for User: {}", userToBeUpdated);
        return userToBeUpdated;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the name
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided already exists. Therefore, the user could not be created. Please pick a different username!";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username"));
        }
    }

}