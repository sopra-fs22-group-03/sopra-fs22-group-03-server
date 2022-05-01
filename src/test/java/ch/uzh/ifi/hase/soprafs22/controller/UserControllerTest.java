package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs22.service.ReservationService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import java.lang.Math;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @MockBean
    private ReservationService reservationService;

    private User user;


    @BeforeEach
     void setup() {
        user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setUsername("testUsername");
        user.setStreet("Musterstrasse");
        user.setStreetNo("1");
        user.setZipCode(8000L);
        user.setCity("Zurich");
        user.setEmail("tes@test.ch");
        user.setPhoneNumber("'0790000001'");
        user.setLicensePlate("ZH1");
        user.setCreditCardNumber(1111111111111111L);
        user.setIsLoggedIn(true);
        user.setIsManager(false);
        user.setToken("'14527952-3ce3-465e-9674-b7ef35d02911'");
    }

    @Test
    void testCreateUser() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("password");
        userPostDTO.setUsername("testUsername");
        userPostDTO.setStreet("Musterstrasse");
        userPostDTO.setStreetNo("1");
        userPostDTO.setZipCode(8000L);
        userPostDTO.setCity("Zurich");
        userPostDTO.setEmail("tes@test.ch");
        userPostDTO.setPhoneNumber("0790000001");
        userPostDTO.setLicensePlate("ZH1");
        userPostDTO.setCreditCardNumber(1111111111111111L);


        // when the mock object (userService) is called for createUser() method with any parameters,
        // then it will return the object "user"
        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        //assertTrue(true);
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(Math.toIntExact(user.getId()))))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.street", is(user.getStreet())))
                .andExpect(jsonPath("$.streetNo", is(user.getStreetNo())))
                //.andExpect(jsonPath("$.zipCode", is(user.getZipCode())))
                .andExpect(jsonPath("$.city", is(user.getCity())))
                .andExpect(jsonPath("$.phoneNumber", is(user.getPhoneNumber())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.creditCardNumber", is(user.getCreditCardNumber())))
        ;
    }

    @Test
    void testLoginUserSucessful() throws Exception {
        user.setIsLoggedIn(true);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("password");
        userPostDTO.setUsername("testUsername");

        // when the mock object (userService) is called for createUser() method with any parameters,
        // then it will return the object "user"
        given(userService.loginUser(Mockito.any())).willReturn(user);


        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(Math.toIntExact(user.getId()))))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.street", is(user.getStreet())))
                .andExpect(jsonPath("$.streetNo", is(user.getStreetNo())))
                //.andExpect(jsonPath("$.zipCode", is(user.getZipCode())))
                .andExpect(jsonPath("$.city", is(user.getCity())))
                .andExpect(jsonPath("$.phoneNumber", is(user.getPhoneNumber())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.creditCardNumber", is(user.getCreditCardNumber())))
                .andExpect(jsonPath("$.isLoggedIn", is(user.getIsLoggedIn())))
        ;
    }


    @Test
    void testGetProfileInformation() throws Exception {
        user.setIsLoggedIn(true);

        // valid userId
        long validUserId = user.getId();

        // when the mock object (userService) is called for getSingleUserById() method with any parameters,
        // then it will return the object "user"
        given(userService.getSingleUserById(validUserId)).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/profile", validUserId);

        // then
        //assertTrue(true);
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(Math.toIntExact(user.getId()))))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.street", is(user.getStreet())))
                .andExpect(jsonPath("$.streetNo", is(user.getStreetNo())))
                //.andExpect(jsonPath("$.zipCode", is(user.getZipCode())))
                .andExpect(jsonPath("$.city", is(user.getCity())))
                .andExpect(jsonPath("$.phoneNumber", is(user.getPhoneNumber())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.creditCardNumber", is(user.getCreditCardNumber())))
                .andExpect(jsonPath("$.isLoggedIn", is(user.getIsLoggedIn())))
        ;
    }

    @Test
    void testGetProfileInformation_invaldiUserid_404thrown() throws Exception {
        // given
        long invalidUserId = 0; //some random invalid userId
        String baseErrorMessage = "The user with id %d was not found";
        String errorMessage = String.format(baseErrorMessage, invalidUserId);

        ResponseStatusException notFoundException = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);

        // when the mock object (userService) is called for getSingleUserById() method with an invalid userId,
        // then it will return the object "notFoundException"
        given(userService.getSingleUserById(invalidUserId)).willThrow(notFoundException);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/profile", invalidUserId);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is(errorMessage)));

    }

    @Test
    void testLogoutUser() throws Exception {

        // valid userId
        long validUserId = user.getId();

        // when the mock object (userService) is called for getSingleUserById() method with any parameters,
        // then it will return the object "user"
        given(userService.getSingleUserById(validUserId)).willReturn(user);

        user.setIsLoggedIn(false);
        given(userService.logoutUser(user, validUserId)).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users/{userId}/logout", validUserId);

        // then
        //assertTrue(true);
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(Math.toIntExact(user.getId()))))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.street", is(user.getStreet())))
                .andExpect(jsonPath("$.streetNo", is(user.getStreetNo())))
                //.andExpect(jsonPath("$.zipCode", is(user.getZipCode())))
                .andExpect(jsonPath("$.city", is(user.getCity())))
                .andExpect(jsonPath("$.phoneNumber", is(user.getPhoneNumber())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.creditCardNumber", is(user.getCreditCardNumber())))
                .andExpect(jsonPath("$.isLoggedIn", is(user.getIsLoggedIn())))
        ;
    }

    @Test
    void testLogoutUser_400thrown() throws Exception {

        // valid userId
        long userId = user.getId();
        String baseErrorMessage = "The user with id %d is already logged out";
        String errorMessage = String.format(baseErrorMessage, userId);

        ResponseStatusException badRequestException = new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        // when the mock object (userService) is called for getSingleUserById() method with any parameters,
        // then it will return the object "user"
        given(userService.getSingleUserById(userId)).willReturn(user);
        user.setIsLoggedIn(false);
        given(userService.logoutUser(user, userId)).willThrow(badRequestException);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users/{userId}/logout", userId);

        // then
        //assertTrue(true);
        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(is(errorMessage)));

    }

/*

    @Test
    void testUpdateUser() throws Exception {
        user.setIsLoggedIn(true);

        long userId = user.getId();

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setPassword("NEWpassword");
        userPutDTO.setUsername("NEWtestUsername");
        userPutDTO.setStreet("NEWMusterstrasse");
        userPutDTO.setEmail("NEWtes@test.ch");

        // when the mock object (userService) is called for createUser() method with any parameters,
        // then it will return the object "user"
        given(userService.getSingleUserById(userId)).willReturn(user);
        given(userService.updateUser(user, userPutDTO)).willReturn(user);


        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/{userId}/profile", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then
        //assertTrue(true);
        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(Math.toIntExact(user.getId()))))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.street", is(user.getStreet())))
                .andExpect(jsonPath("$.streetNo", is(user.getStreetNo())))
                //.andExpect(jsonPath("$.zipCode", is(user.getZipCode())))
                .andExpect(jsonPath("$.city", is(user.getCity())))
                .andExpect(jsonPath("$.phoneNumber", is(user.getPhoneNumber())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.creditCardNumber", is(user.getCreditCardNumber())))
        ;
    }*/
/*@Test
public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
  // given
  User user = new User();
  user.setPassword("password");
  user.setUsername("firstname@lastname");


  List<User> allUsers = Collections.singletonList(user);

  // this mocks the UserService -> we define above what the userService should
  // return when getUsers() is called
  given(userService.getUsers()).willReturn(allUsers);

  // when
  MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

  // then
  mockMvc.perform(getRequest).andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].password", is(user.getPassword())))
      .andExpect(jsonPath("$[0].username", is(user.getUsername())))
      .andExpect(jsonPath("$[0].logged_in", is(user.getLogged_in())))
      ;
}*/


/*
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
      user.setUserId(1L);
      user.setPassword("password");
      user.setUsername("testUsername");
      user.setToken("1");
      user.setLogged_in(true);
      user.setCreationDate(LocalDate.now());
      user.setBirthday(LocalDate.of(1999,12,31));

      // valid userId
      long validUserId = user.getUserId();

      // when the mock object (userService) is called for getSingleUserById() method with any parameters,
      // then it will return the object "user"
      given(userService.getSingleUserById(validUserId)).willReturn(user);

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder getRequest = get("/users/{userId}", validUserId);

      // then
      mockMvc.perform(getRequest)
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id", is(Math.toIntExact(user.getUserId()))))
              .andExpect(jsonPath("$.username", is(user.getUsername())))
              .andExpect(jsonPath("$.logged_in", is(user.getLogged_in())))
              .andExpect(jsonPath("$.creationDate", is(user.getCreationDate().toString())))
              .andExpect(jsonPath("$.birthday", is(user.getBirthday().toString())))
      ;
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
      updatedUser.setUserId(1L);
      updatedUser.setPassword("password");
      updatedUser.setUsername("someNewUsername");
      updatedUser.setToken("1");
      updatedUser.setLogged_in(true);
      updatedUser.setCreationDate(LocalDate.now());

      // some random username update
      UserPutDTO userPutDTO = new UserPutDTO();
      userPutDTO.setUsername("someNewUsername");

      // valid userId
      long validUserId = updatedUser.getUserId();

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

*//**
 * Helper Method to convert userPostDTO into a JSON string such that the input
 * can be processed
 * Input will look like this: {"password": "password", "username": "testUsername"}
 *
 * @param object
 * @return string
 *//*

*/

private String asJsonString(final Object object) {
  try {
    return new ObjectMapper().writeValueAsString(object);
  } catch (JsonProcessingException e) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        String.format("The request body could not be created.%s", e.toString()));
  }
}







    @Test
    void deleteUser() {
    }


}