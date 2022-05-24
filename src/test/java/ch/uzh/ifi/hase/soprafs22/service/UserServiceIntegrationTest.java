package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Carpark;
import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.entity.Reservation;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.BillingRepository;
import ch.uzh.ifi.hase.soprafs22.repository.CarparkRepository;
import ch.uzh.ifi.hase.soprafs22.repository.ParkingslipRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;
  @Autowired
  @Qualifier("carparkRepository")
  private CarparkRepository carparkRepository;
  @Autowired
  private ParkingslipRepository parkingslipRepository;
  @Autowired
  private BillingRepository billingRepository;

  @Autowired
  private UserService userService;
  @Autowired
  private CarparkService carparkService;
  @Autowired
  private ReservationService reservationService;


  @BeforeEach
  public void setup() {
      // given
      assertNull(userRepository.findByUsername("testUsername"));

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

      userUpdated = new User();
      userUpdated.setPassword("password_update");
      userUpdated.setUsername("testUsername_update");
      userUpdated.setStreet("Musterstrasse_update");
      userUpdated.setStreetNo("1_update");
      userUpdated.setZipCode(8002L);
      userUpdated.setCity("Zurich_update");
      userUpdated.setEmail("tes_update@test.ch");
      userUpdated.setPhoneNumber("'0790000002'");
      userUpdated.setLicensePlate("ZH11");
      userUpdated.setCreditCardNumber(1111111111111112L);
      userUpdated.setId(1L);

      }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        parkingslipRepository.deleteAll();
    }

    private User user;
  private User userUpdated;
  private Carpark testCarpark;
  private Parkingslip testParkingslipCheckedin;

  @Test
  public void createUser_validInputs_success() {

      // when
      User createdUser = userService.createUser(user);

      // then
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
  public void createUser_duplicateUsername_throwsException() {

      User createdUser = userService.createUser(user);

      // attempt to create second user with same username
      User user2 = new User();
      user2.setPassword("password2");
      user2.setUsername("testUsername");

      //try to create a new user with already existing username; status error with code 400 should be thrown
      try {
          userService.createUser(user2);
          Assertions.fail("BAD REQUEST exception should have been thrown!");
      }
      catch (ResponseStatusException ex) {
          assertEquals(400, ex.getRawStatusCode());
      }
  }

    @Test
    void testGetSingleUserById() {
        User createdUser = userService.createUser(user);

        long validUserId = createdUser.getId();

        User returnedUser = userService.getSingleUserById(validUserId);

        // then
        assertEquals(user.getId(), returnedUser.getId());
        assertEquals(user.getPassword(), returnedUser.getPassword());
        assertEquals(user.getUsername(), returnedUser.getUsername());
        assertEquals(user.getStreet(), returnedUser.getStreet());
        assertEquals(user.getStreetNo(), returnedUser.getStreetNo());
        assertEquals(user.getZipCode(), returnedUser.getZipCode());
        assertEquals(user.getCity(), returnedUser.getCity());
        assertEquals(user.getEmail(), returnedUser.getEmail());
        assertEquals(user.getPhoneNumber(), returnedUser.getPhoneNumber());
        assertEquals(user.getLicensePlate(), returnedUser.getLicensePlate());
        assertEquals(user.getCreditCardNumber(), returnedUser.getCreditCardNumber());
        assertFalse(returnedUser.getIsManager());
        assertTrue(returnedUser.getIsLoggedIn());
        assertNotNull(returnedUser.getToken());
    }

    @Test
    public void testGetSingleUserById_throwHttpStatusException() {
        User createdUser = userService.createUser(user);

        //try to create a new user with already existing username; status error with code 400 should be thrown
        try {
            long invalidUserId = 0;
            User returnedUser = userService.getSingleUserById(invalidUserId);            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(404, ex.getRawStatusCode());
        }
    }

    @Test
    void testGetSingleUserByName() {
        User createdUser = userService.createUser(user);

        String validUsername = createdUser.getUsername();

        User returnedUser = userService.getSingleUserByName(validUsername);

        // then
        assertEquals(user.getId(), returnedUser.getId());
        assertEquals(user.getPassword(), returnedUser.getPassword());
        assertEquals(user.getUsername(), returnedUser.getUsername());
        assertEquals(user.getStreet(), returnedUser.getStreet());
        assertEquals(user.getStreetNo(), returnedUser.getStreetNo());
        assertEquals(user.getZipCode(), returnedUser.getZipCode());
        assertEquals(user.getCity(), returnedUser.getCity());
        assertEquals(user.getEmail(), returnedUser.getEmail());
        assertEquals(user.getPhoneNumber(), returnedUser.getPhoneNumber());
        assertEquals(user.getLicensePlate(), returnedUser.getLicensePlate());
        assertEquals(user.getCreditCardNumber(), returnedUser.getCreditCardNumber());
        assertFalse(returnedUser.getIsManager());
        assertTrue(returnedUser.getIsLoggedIn());
        assertNotNull(returnedUser.getToken());
    }

    @Test
    public void testGetSingleUserByName_throwHttpStatusException() {
        User createdUser = userService.createUser(user);

        //try to create a new user with already existing username; status error with code 400 should be thrown
        try {
            String invalidUsername = "INVALID_USERNAME";
            User returnedUser = userService.getSingleUserByName(invalidUsername);            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(404, ex.getRawStatusCode());
        }
    }

    @Test
    void testLoginUser() {
        User createdUser = userService.createUser(user);

        User returnedUser = userService.loginUser(user);

        assertEquals(returnedUser.getId(), user.getId());
        assertEquals(returnedUser.getIsLoggedIn(), true);
    }

    @Test
    void testLoginUser_throwHttpStatusException_404() {
        //try to get a user that does not exist; status error with code 404 should be thrown
        try {
            userService.loginUser(user);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(404, ex.getRawStatusCode());
        }
    }

    @Test
    void testUpdateUser() {
        User createdUser = userService.createUser(user);

        User returnedUser = userService.updateUser(createdUser, userUpdated);

        assertEquals(returnedUser.getUsername(), userUpdated.getUsername());
        assertEquals(returnedUser.getPassword(), userUpdated.getPassword());
        assertEquals(returnedUser.getStreet(),userUpdated.getStreet());
        assertEquals(returnedUser.getStreetNo(),userUpdated.getStreetNo());
        assertEquals(returnedUser.getZipCode(),userUpdated.getZipCode());
        assertEquals(returnedUser.getCity(),userUpdated.getCity());
        assertEquals(returnedUser.getEmail(),userUpdated.getEmail());
        assertEquals(returnedUser.getPhoneNumber(),userUpdated.getPhoneNumber());
        assertEquals(returnedUser.getLicensePlate(),userUpdated.getLicensePlate());
        assertEquals(returnedUser.getCreditCardNumber(),userUpdated.getCreditCardNumber());
        assertFalse(returnedUser.getIsManager());
        assertTrue(returnedUser.getIsLoggedIn());
        assertNotNull(returnedUser.getToken());
    }

    @Test
    void testDeleteUser() {
        User createdUser = userService.createUser(user);
        long validUserId = createdUser.getId();
        userService.deleteUser(validUserId);

        assertNull(userRepository.findById(validUserId));
    }

    @Test
    void testDeleteUser_throwHttpStatusException_404() {
        long invalidUserId = 0;
        //try to delete a user that does not exist/cannot be found; status error with code 404 should be thrown
        try {
            userService.deleteUser(invalidUserId);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(404, ex.getRawStatusCode());
        }
    }

    @Test
    void testDeleteUser_throwHttpStatusException_403_isCheckedin() {
        User createdUser = userService.createUser(user);
        long validUserId = createdUser.getId();

        Carpark carparkToCheckin = carparkService.getSingleCarparkById(100001);
        carparkToCheckin.setNumOfEmptySpaces(100);

        Parkingslip parkingslip = carparkService.performCheckinOfUser(createdUser,carparkToCheckin);

        //try to delete a user that is checked-in; status error with code 403 should be thrown
        try {
            userService.deleteUser(validUserId);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(403, ex.getRawStatusCode());
        }
    }

    @Test
    void testDeleteUser_throwHttpStatusException_403_hasUnpaidBillings() {
        User createdUser = userService.createUser(user);
        long validUserId = createdUser.getId();

        Carpark carparkToCheckin = carparkService.getSingleCarparkById(100001);

        Reservation testReservation = new Reservation();
        testReservation.setUserId(validUserId);
        testReservation.setCarparkId(100001L);
        testReservation.setCheckinDate("2032-05-08");
        testReservation.setCheckinTime("08:00");
        testReservation.setCheckoutDate("2032-05-08");
        testReservation.setCheckoutTime("18:00");

        Reservation createdReservation = reservationService.createReservation(testReservation);

        //try to delete a user that has not paid all his biliings; status error with code 403 should be thrown
        try {
            userService.deleteUser(validUserId);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(403, ex.getRawStatusCode());
        }
    }



}
