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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    public User getSingleUserByName(String username) {
        User userByName = userRepository.findByUsername(username);

        // throw error if user does not exist
        if (userByName == null) {
            String baseErrorMessage = "The user with username %s was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, username));
        }
        return userByName;
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setIsManager(false);
        newUser.setIsLoggedIn(true);
//        newUser.setCreationDate(LocalDate.now());

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
        userByUsername.setIsLoggedIn(true);
        userByUsername = userRepository.save(userByUsername);
        userRepository.flush();

        return userByUsername;
    }

    public User logoutUser(User existingUser, Long id) {

        // check if user is already logged out
        if (!existingUser.getIsLoggedIn()) {
            String baseErrorMessage = "The user with id %d is already logged out";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, id));
        }

        // logout user
        existingUser.setIsLoggedIn(false);
        existingUser = userRepository.save(existingUser);
        userRepository.flush();

        return existingUser;
    }

    public User updateUser(User userToBeUpdated, User userUpdateRequest) {

        // update fields which are provided by user (=not null)
        if (userUpdateRequest.getUsername() != null) {
            checkIfUsernameExists(userUpdateRequest.getUsername());
            userToBeUpdated.setUsername(userUpdateRequest.getUsername());
        }
        if (userUpdateRequest.getPassword() != null) {
            userToBeUpdated.setPassword(userUpdateRequest.getPassword());
        }
        if (userUpdateRequest.getStreet() != null) {
            userToBeUpdated.setStreet(userUpdateRequest.getStreet());
        }
        if (userUpdateRequest.getStreetNo() != null) {
            userToBeUpdated.setStreetNo(userUpdateRequest.getStreetNo());
        }
        if (userUpdateRequest.getZipCode() != null) {
            userToBeUpdated.setZipCode(userUpdateRequest.getZipCode());
        }
        if (userUpdateRequest.getCity() != null) {
            userToBeUpdated.setCity(userUpdateRequest.getCity());
        }
        if (userUpdateRequest.getPhoneNumber() != null) {
            userToBeUpdated.setPhoneNumber(userUpdateRequest.getPhoneNumber());
        }
        if (userUpdateRequest.getEmail() != null) {
            checkIfEmailExists(userUpdateRequest.getEmail());
            userToBeUpdated.setEmail(userUpdateRequest.getEmail());
        }
        if (userUpdateRequest.getLicensePlate() != null) {
            userToBeUpdated.setLicensePlate(userUpdateRequest.getLicensePlate());
        }
        if (userUpdateRequest.getCreditCardNumber() != null) {
            userToBeUpdated.setCreditCardNumber(userUpdateRequest.getCreditCardNumber());
        }

        // save changes
        userToBeUpdated = userRepository.save(userToBeUpdated);
        userRepository.flush();

        log.debug("Updated Information for User: {}", userToBeUpdated);
        return userToBeUpdated;
    }

    public int deleteUser (long userId) {

        User userToBeDeleted = getSingleUserById(userId);

        // TODO: CHECK HTTP STATUS CODE OF ERROR
        // check if user has no unpaid billings; otherwise, throw error
        if (!userHasPaidAllBillings(userToBeDeleted)) {
            String baseErrorMessage = "The user with username %s has unpaid billings. Deletion of user not possible." +
                    "Please pay all bills first.";
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format(baseErrorMessage, userToBeDeleted.getUsername()));
        }

        // TODO: CHECK HTTP STATUS CODE OF ERROR
        // check if user is not checked-in in a carpark; otherwise, throw error
        if (!userIsCheckedOutOfAllParkings(userToBeDeleted)) {
            String baseErrorMessage = "The user with username %s is still checked-in in a carpark. Deletion of user not possible." +
                    "Please check-out (and pay the corresponding bill) first.";
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format(baseErrorMessage, userToBeDeleted.getUsername()));
        }

        // delete user
        try {
            userRepository.deleteById(userId);
            userRepository.flush();
            return 0;
            }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.valueOf(500), "Deletion of user profile failed.");
        }
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the name
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param user
     * @throws ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User user) {
        User userByUsername = userRepository.findByUsername(user.getUsername());
        User userByEmail = userRepository.findByEmail(user.getEmail());

        String baseErrorMessage = "The %s provided already %s. Therefore, the user could not be created!";
        if (userByUsername != null && userByEmail != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username and the email", "exist"));
        }
        else if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username", "exists"));
        }
        else if (userByEmail != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "email", "exists"));
        }

    }

    private void checkIfUserIdIsValid(long id) {
        boolean userExists = userRepository.existsById(id);

        // throw error if user does not exist
        if (!userExists) {
            String baseErrorMessage = "The user with id %d was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, id));
        }
    }

    private void checkIfUsernameExists(String username) {
        boolean userExists = userRepository.existsByUsername(username);

        // throw error if username already exists
        if (userExists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The provided username already exists. Please pick a different one.");
        }
    }

    private void checkIfEmailExists(String email) {
        boolean userExists = userRepository.existsByEmail(email);

        // throw error if email already exists
        if (userExists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The provided email already exists. Please pick a different one.");
        }
    }

    // TODO: Implement check that user has no outstanding bills
    private boolean userHasPaidAllBillings(User user) {
        return true;
    }

    // TODO: Implement check that user is currently not checked-in into a parking
    private boolean userIsCheckedOutOfAllParkings(User user) {
        return true;
    }

}