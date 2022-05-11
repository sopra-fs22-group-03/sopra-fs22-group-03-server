/**
 * TESTS outcommented
 * Need to be adapted to current entities
 */

package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Reservation;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.ReservationRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class ReservationServiceIntegrationTest {

  @Qualifier("reservationRepository")
  @Autowired
  private ReservationRepository reservationRepository;

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ReservationService reservationService;

  @BeforeEach
  public void setup() {
      reservationRepository.deleteAll();
      userRepository.deleteAll();
  }

  private User user;

  @Test
  public void createReservation_validInputs_success() {
      // given: new user
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

      user = userRepository.save(user);
      userRepository.flush();

      // new user does not have any reservations
      assertTrue(reservationRepository.findAllByUserId(user.getId()).isEmpty());

      // given: new reservation request
      Reservation testReservation = new Reservation();
      testReservation.setUserId(user.getId());
      testReservation.setCarparkId(100000L);
      testReservation.setCheckinDate("2022-05-20");
      testReservation.setCheckinTime("08:00");
      testReservation.setCheckoutDate("2022-05-20");
      testReservation.setCheckoutTime("18:00");

      // when: create new reservation
      Reservation createdReservation = reservationService.createReservation(testReservation);

      // then: created reservation should match the reservation request specified by user
      assertEquals(testReservation.getUserId(), createdReservation.getUserId());
      assertEquals(testReservation.getCarparkId(), createdReservation.getCarparkId());
      assertEquals(testReservation.getCheckinDate(), createdReservation.getCheckinDate());
      assertEquals(testReservation.getCheckinTime(), createdReservation.getCheckinTime());
      assertEquals(testReservation.getCheckoutDate(), createdReservation.getCheckoutDate());
      assertEquals(testReservation.getCheckoutTime(), createdReservation.getCheckoutTime());
  }
}
