package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User user;

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

      // when -> any object is being save in the userRepository -> return the dummy
      Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
  }

  @Test
  public void createUser_validInputs_success() {
      // when
      User createdUser = userService.createUser(user);

      // then
      Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

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


}
