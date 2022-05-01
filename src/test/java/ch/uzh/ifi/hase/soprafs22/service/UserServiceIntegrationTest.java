package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
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
  private UserService userService;

  @BeforeEach
  public void setup() {
    userRepository.deleteAll();
  }

  private User user;

  @Test
  public void createUser_validInputs_success() {

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
}
