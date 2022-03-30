package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setPassword("password");
    user.setUsername("firstname@lastname");
    user.setLogged_in(true);
    user.setCreationDate(LocalDate.now());

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
//        .andExpect(jsonPath("$[0].password", is(user.getPassword())))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].logged_in", is(user.getLogged_in())));
  }

  ////////////////// ENDPOINT 1 //////////////////
  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setPassword("password");
    user.setUsername("testUsername");
    user.setToken("1");
    user.setLogged_in(true);
    user.setCreationDate(LocalDate.now());
    user.setBirthday(LocalDate.of(1999,12,31));

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("password");
    userPostDTO.setUsername("testUsername");

    // when the mock object (userService) is called for createUser() method with any parameters,
      // then it will return the object "user"
    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.logged_in", is(user.getLogged_in())))
        .andExpect(jsonPath("$.creationDate", is(user.getCreationDate().toString())))
        .andExpect(jsonPath("$.birthday", is(user.getBirthday().toString())));
  }

    ////////////////// ENDPOINT 2 //////////////////
    @Test
    public void createUser_invalidInput_409thrown() throws Exception {
        // given
        String errorMessage = "The username provided already exists. Therefore, the user could not be created. Please pick a different username!";
        ResponseStatusException conflictException = new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("password");
        userPostDTO.setUsername("testUsername");

        // when the mock object (userService) is called for createUser() method with any parameters,
        // then it will return the object "conflictException"
        given(userService.createUser(Mockito.any())).willThrow(conflictException);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict())
                .andExpect(status().reason(is(errorMessage)));
    }

    ////////////////// ENDPOINT 3 //////////////////
    @Test
    public void getUserProfile_validInput_userRetrieved() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setLogged_in(true);
        user.setCreationDate(LocalDate.now());
        user.setBirthday(LocalDate.of(1999,12,31));

        // valid userId
        long validUserId = user.getId();

        // when the mock object (userService) is called for getSingleUserById() method with any parameters,
        // then it will return the object "user"
        given(userService.getSingleUserById(validUserId)).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}", validUserId);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.logged_in", is(user.getLogged_in())))
                .andExpect(jsonPath("$.creationDate", is(user.getCreationDate().toString())))
                .andExpect(jsonPath("$.birthday", is(user.getBirthday().toString())));
    }

    ////////////////// ENDPOINT 4 //////////////////
    @Test
    public void getUserProfile_invalidUserId_404thrown() throws Exception {
        // given
        long invalidUserId = 99; //some random invalid userId
        String baseErrorMessage = "The user with id %d was not found";
        String errorMessage = String.format(baseErrorMessage, invalidUserId);

        ResponseStatusException notFoundException = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);

        // when the mock object (userService) is called for getSingleUserById() method with an invalid userId,
        // then it will return the object "notFoundException"
        given(userService.getSingleUserById(invalidUserId)).willThrow(notFoundException);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}", invalidUserId);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is(errorMessage)));
    }

    ////////////////// ENDPOINT 5 //////////////////
    @Test
    public void updateUserProfile_validUserId_userUpdated() throws Exception {
        // given
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setPassword("password");
        updatedUser.setUsername("someNewUsername");
        updatedUser.setToken("1");
        updatedUser.setLogged_in(true);
        updatedUser.setCreationDate(LocalDate.now());

        // some random username update
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("someNewUsername");

        // valid userId
        long validUserId = updatedUser.getId();

        // when the mock object (userService) is called for getSingleUserById() method with any parameters,
        // then it will return the object "notFoundException"
        given(userService.updateUser(Mockito.any(), Mockito.any())).willReturn(updatedUser);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/{userId}", validUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    ////////////////// ENDPOINT 6 //////////////////
    @Test
    public void updateUserProfile_invalidUserId_404thrown() throws Exception {
        // given
        // some random username update
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("someNewUsername");

        // some random invalid userId
        long invalidUserId = 99;
        String baseErrorMessage = "The user with id %d was not found";
        String errorMessage = String.format(baseErrorMessage, invalidUserId);

        ResponseStatusException notFoundException = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);

        // when the mock object (userService) is called for getSingleUserById() method with an invalid userId,
        // then it will return the object "notFoundException"
        given(userService.getSingleUserById(invalidUserId)).willThrow(notFoundException);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/{userId}", invalidUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is(errorMessage)));
    }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"password": "password", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}