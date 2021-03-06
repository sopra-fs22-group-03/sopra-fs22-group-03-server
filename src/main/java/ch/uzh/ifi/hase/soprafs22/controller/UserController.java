package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.ReservationService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;
    private final ReservationService reservationService;

    UserController(UserService userService, ReservationService reservationService) {
        this.userService = userService;
        this.reservationService = reservationService;
    }

//  @GetMapping("/users")
//  @ResponseStatus(HttpStatus.OK)
//  @ResponseBody
//  public List<UserGetDTO> getAllUsers() {
//    // fetch all users in the internal representation
//    List<User> users = userService.getUsers();
//    List<UserGetDTO> userGetDTOs = new ArrayList<>();
//
//    // convert each user to the API representation
//    for (User user : users) {
//      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
//    }
//    return userGetDTOs;
//  }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        User createdUser = userService.createUser(userInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO loginUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // login user
        User loggedInUser = userService.loginUser(userInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loggedInUser);
    }

    @GetMapping("/users/{userId}/profile")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getProfileInformation(@PathVariable(value = "userId") Long id) {

        // get internal user representation by provided path variable userId
        User userById = userService.getSingleUserById(id);

        // convert internal representation to API representation
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(userById);

        return userGetDTO;
    }

    @PostMapping("/users/{userId}/logout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO logoutUser(@PathVariable(value = "userId") Long id) {

        // get internal user representation by provided request body userId
        User userById = userService.getSingleUserById(id);

        // logout user
        User loggedOutUser = userService.logoutUser(userById, id);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loggedOutUser);
    }

    @PutMapping("/users/{userId}/profile")
    @ResponseStatus(HttpStatus.OK)
    public UserGetDTO updateUser(@RequestBody UserPutDTO userPutDTO, @PathVariable(value = "userId") Long id) {
        // convert API user updates to internal representation
        User userUpdateRequest = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

        // get user to be updated from path variable userId
        User userToBeUpdated = userService.getSingleUserById(id);

        // update user
        User updatedUser = userService.updateUser(userToBeUpdated, userUpdateRequest);

        // convert internal representation to API representation
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(updatedUser);

        return userGetDTO;

    }

    @DeleteMapping("/users/{userId}/profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(value = "userId") Long id) {

        // delete user profile
        int response1 = userService.deleteUser(id);

        // delete all future reservations associated with user to be deleted
        int response2 = reservationService.deleteAllReservationsOfUserId(id);

    }
}
