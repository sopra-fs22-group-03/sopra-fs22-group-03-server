package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Billing;
import ch.uzh.ifi.hase.soprafs22.entity.Carpark;
import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.BillingRepository;
import ch.uzh.ifi.hase.soprafs22.repository.ParkingslipRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BillingRepository billingRepository;

    @Mock
    private ParkingslipRepository parkingslipRepository;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private BillingService billingService;

    @InjectMocks
    private ReservationService reservationService;



    private User user;
    private User userUpdateRequest;

    @BeforeEach
    public void setup() {
        // given
        user = new User();
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
        user.setId(1L);

        userUpdateRequest = new User();
        userUpdateRequest.setPassword("password_update");
        userUpdateRequest.setUsername("testUsername_update");
        userUpdateRequest.setStreet("Musterstrasse_update");
        userUpdateRequest.setStreetNo("1_update");
        userUpdateRequest.setZipCode(8002L);
        userUpdateRequest.setCity("Zurich_update");
        userUpdateRequest.setEmail("tes_update@test.ch");
        userUpdateRequest.setPhoneNumber("'0790000002'");
        userUpdateRequest.setLicensePlate("ZH11");
        userUpdateRequest.setCreditCardNumber(1111111111111112L);
        userUpdateRequest.setIsLoggedIn(true);
        userUpdateRequest.setIsManager(false);
        userUpdateRequest.setToken("'14527952-3ce3-465e-9674-b7ef35d02911'");
        userUpdateRequest.setId(1L);
        
        // when -> any object is being save in the userRepository -> return the dummy
        lenient().when(userRepository.save(user)).thenReturn(user);
        lenient().when(userRepository.save(userUpdateRequest)).thenReturn(userUpdateRequest);
    }

    @Test
    public void createUser_validInputs_success() {
        // when
        User createdUser = userService.createUser(user);

        // then
        verify(userRepository, times(1)).save(Mockito.any());

        assertEquals(user.getId(), createdUser.getId());
        assertEquals(user.getPassword(), createdUser.getPassword());
        assertEquals(user.getUsername(), createdUser.getUsername());
        assertEquals(user.getStreet(), createdUser.getStreet());
        assertEquals(user.getStreetNo(), createdUser.getStreetNo());
        assertEquals(user.getZipCode(), createdUser.getZipCode());
        assertEquals(user.getCity(), createdUser.getCity());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getPhoneNumber(), createdUser.getPhoneNumber());
        assertEquals(user.getLicensePlate(), createdUser.getLicensePlate());
        assertEquals(user.getCreditCardNumber(), createdUser.getCreditCardNumber());
        assertFalse(createdUser.getIsManager());
        assertTrue(createdUser.getIsLoggedIn());
        assertNotNull(createdUser.getToken());
    }


    @Test
    public void createUser_duplicateInputs_throwsException() {
        // given -> a first user has already been created
        User createdUser = userService.createUser(user);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(user);

        //try to create a new user with already existing username; status error with code 400 should be thrown
        try {
            userService.createUser(user);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(400, ex.getRawStatusCode());
        }
    }

    @Test
    void testGetSingleUserById() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(user);

        User returnedUser = userService.getSingleUserById(1);

        assertEquals(user, returnedUser);
    }

    @Test
    void testGetSingleUserById_throwHttpStatusException() {
        long invalidUserId = 0;

        //try to get a user that does not exist; status error with code 404 should be thrown
        try {
            userService.getSingleUserById(invalidUserId);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(404, ex.getRawStatusCode());
        }
    }

    @Test
    void testGetSingleUserByName() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);

        User returnedUser = userService.getSingleUserByName("testUsername");

        assertEquals(user, returnedUser);
    }

    @Test
    void testGetSingleUserByName_throwHttpStatusException() {
        String invalidUserName = "WRONG_USERNAME";

        //try to get a user that does not exist; status error with code 404 should be thrown
        try {
            userService.getSingleUserByName(invalidUserName);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(404, ex.getRawStatusCode());
        }
    }

    @Test
    void testLoginUser() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);

        User returnedUser = userService.loginUser(user);

        assertEquals(returnedUser.getId(), user.getId());
        assertEquals(returnedUser.getIsLoggedIn(), true);
    }

    @Test
    void testLoginUser_throwHttpStatusException_404() {
        //try to get a user that does not exist; status error with code 404 should be thrown
        try {
            // test fails as user cannot be found / is not mocked
            userService.loginUser(user);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(404, ex.getRawStatusCode());
        }
    }

    // TODO
    @Test
    void testLoginUser_throwHttpStatusException_403() {
    }

    @Test
    void testLogoutUser() {
        long userId = user.getId();

        User returnedUser = userService.logoutUser(user, userId);

        assertEquals(returnedUser.getId(), user.getId());
        assertEquals(returnedUser.getIsLoggedIn(), false);
    }

//    @Test
//    void testLogoutUser_throwHttpStatusException() {
//        user.setIsLoggedIn(false);
//        long userId = user.getId();
//
//        try {
//            userService.logoutUser(user, userId);
//            Assertions.fail("BAD REQUEST exception should have been thrown!");
//        }
//        catch (ResponseStatusException ex) {
//            assertEquals(400, ex.getRawStatusCode());
//        }
//    }

    @Test
    void testUpdateUser() {
        User returnedUser = userService.updateUser(user, userUpdateRequest);

        assertEquals(returnedUser.getUsername(), userUpdateRequest.getUsername());
        assertEquals(returnedUser.getPassword(), userUpdateRequest.getPassword());
        assertEquals(returnedUser.getStreet(),userUpdateRequest.getStreet());
        assertEquals(returnedUser.getStreetNo(),userUpdateRequest.getStreetNo());
        assertEquals(returnedUser.getZipCode(),userUpdateRequest.getZipCode());
        assertEquals(returnedUser.getCity(),userUpdateRequest.getCity());
        assertEquals(returnedUser.getEmail(),userUpdateRequest.getEmail());
        assertEquals(returnedUser.getPhoneNumber(),userUpdateRequest.getPhoneNumber());
        assertEquals(returnedUser.getLicensePlate(),userUpdateRequest.getLicensePlate());
        assertEquals(returnedUser.getCreditCardNumber(),userUpdateRequest.getCreditCardNumber());

    }

    // TODO
    @Test
    void testDeleteUser() {
    }

    @Test
    void testDeleteUser_throwHttpStatusException_404() {
        long invalidUserId = 0;

        //try to delete a user that does not exist/cannot be found; status error with code 404 should be thrown
        //user is not mocked and, therefore, not found
        try {
            userService.deleteUser(invalidUserId);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(404, ex.getRawStatusCode());
        }
    }
}
