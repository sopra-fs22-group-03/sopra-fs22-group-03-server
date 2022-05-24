/**
 * TESTS outcommented
 * Need to be adapted to current entities
 */

package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Billing;
import ch.uzh.ifi.hase.soprafs22.entity.Reservation;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.BillingRepository;
import ch.uzh.ifi.hase.soprafs22.repository.ReservationRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
    private BillingRepository billingRepository;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private BillingService billingService;

    private User user;
    private Reservation testReservation;

    @BeforeEach
    public void setup() {
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

        // given: new reservation request
        testReservation = new Reservation();
        testReservation.setUserId(user.getId());
        testReservation.setCarparkId(100000L);
        testReservation.setCheckinDate("2032-05-20");
        testReservation.setCheckinTime("08:00");
        testReservation.setCheckoutDate("2032-05-20");
        testReservation.setCheckoutTime("18:00");
    }

    @AfterEach
    void afterEach() {
        reservationRepository.deleteAll();
        userRepository.deleteAll();
        billingRepository.deleteAll();
    }

    @Test
    public void testCreateReservation_validInputs_success() {
        long validUserId = user.getId();
        // new user does not have any reservations
        assertTrue(reservationRepository.findAllByUserId(validUserId).isEmpty());

        // when: create new reservation
        Reservation createdReservation = reservationService.createReservation(testReservation);

        // then: created reservation should match the reservation request specified by user
        // Besides, the user should have one reservation
        assertEquals(reservationRepository.findAllByUserId(validUserId).size(), 1);

        assertEquals(testReservation.getUserId(), createdReservation.getUserId());
        assertEquals(testReservation.getCarparkId(), createdReservation.getCarparkId());
        assertEquals(testReservation.getCheckinDate(), createdReservation.getCheckinDate());
        assertEquals(testReservation.getCheckinTime(), createdReservation.getCheckinTime());
        assertEquals(testReservation.getCheckoutDate(), createdReservation.getCheckoutDate());
        assertEquals(testReservation.getCheckoutTime(), createdReservation.getCheckoutTime());
        assertEquals(30,createdReservation.getParkingFee(), 0.05d);
        assertFalse(billingService.getAllBillingsByUserId(validUserId).isEmpty());
    }

    @Test
    public void testCreateReservation_throwHttpStatusException_403_ReservationIsLessThanXMinutes() {
        long validUserId = user.getId();
        // new user does not have any reservations
        assertTrue(reservationRepository.findAllByUserId(validUserId).isEmpty());

        // given
        testReservation.setCheckoutTime("09:00");

        try {
            // then: try to update the reservation
            reservationService.createReservation(testReservation);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(403, ex.getRawStatusCode());
        }
    }

    @Test
    public void testGetAllReservationsByUserId() {
        long validUserId = user.getId();
        // new user does not have any reservations
        assertTrue(reservationRepository.findAllByUserId(validUserId).isEmpty());

        // create new reservation
        Reservation createdReservation = reservationService.createReservation(testReservation);

        // get all reservations
        List<Reservation> allReservations = reservationService.getAllReservationsByUserId(user.getId());

        // then: created reservation should match the reservation request specified by user
        // Besides, the user should have one reservation
        assertEquals(allReservations.size(), 1);
    }

    @Test
    public void testCreateReservation_throwHttpStatusException_403_ReservationIsInThePast() {
        long validUserId = user.getId();
        // new user does not have any reservations
        assertTrue(reservationRepository.findAllByUserId(validUserId).isEmpty());

        // given
        String newCheckinDate = LocalDate.now().plusDays(-2).toString();
        String newCheckoutDate = LocalDate.now().plusDays(2).toString();
        testReservation.setCheckinDate(newCheckinDate);
        testReservation.setCheckoutDate(newCheckoutDate);

        try {
            // then: try to update the reservation
            reservationService.createReservation(testReservation);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(403, ex.getRawStatusCode());
        }
    }

    @Test
    public void testGetSingleReservationByReservationId() {
        long validUserId = user.getId();
        // new user does not have any reservations
        assertTrue(reservationRepository.findAllByUserId(validUserId).isEmpty());

        // create new reservation
        Reservation createdReservation = reservationService.createReservation(testReservation);

        // get all reservations
        List<Reservation> allReservations = reservationService.getAllReservationsByUserId(user.getId());
        Reservation reservation = allReservations.get(0);

        // then: created reservation should match the reservation request specified by user
        // Besides, the user should have one reservation
        assertEquals(createdReservation.getUserId(), reservation.getUserId());
        assertEquals(createdReservation.getCarparkId(), reservation.getCarparkId());
        assertEquals(createdReservation.getCheckinDate(), reservation.getCheckinDate());
        assertEquals(createdReservation.getCheckinTime(), reservation.getCheckinTime());
        assertEquals(createdReservation.getCheckoutDate(), reservation.getCheckoutDate());
        assertEquals(createdReservation.getCheckoutTime(), reservation.getCheckoutTime());
    }

    /*@Test
    public void testDeleteAllReservationsOfUserId() {
        long validUserId = user.getId();
        // new user does not have any reservations
        assertTrue(reservationRepository.findAllByUserId(validUserId).isEmpty());

        // create new reservation
        Reservation createdReservation = reservationService.createReservation(testReservation);

        // get all reservations
        List<Reservation> allReservations = reservationService.getAllReservationsByUserId(validUserId);
        assertEquals(allReservations.size(), 1);

        // then: delete all reservations of user
        reservationService.deleteAllReservationsOfUserId(validUserId);
        assertTrue(reservationService.getAllReservationsByUserId(validUserId).isEmpty());
    }*/

    @Test
    public void testDeleteSingleReservationByReservationId() {
        long validUserId = user.getId();
        // new user does not have any reservations
        assertTrue(reservationRepository.findAllByUserId(validUserId).isEmpty());

        // create new reservation
        Reservation reservationToBeDeleted = reservationService.createReservation(testReservation);
        long reservationId = reservationToBeDeleted.getId();

        // then: delete reservation of user
        reservationService.deleteSingleReservationByReservationId(reservationId);
        assertTrue(reservationService.getAllReservationsByUserId(validUserId).isEmpty());
        assertTrue(billingService.getAllBillingsByUserId(validUserId).isEmpty());
    }


    @Test
    public void testUpdateReservation() {
        long validUserId = user.getId();
        // new user does not have any reservations
        assertTrue(reservationRepository.findAllByUserId(validUserId).isEmpty());

        // create new reservation
        Reservation createdReservation = reservationService.createReservation(testReservation);

        // get all reservations
        List<Reservation> allReservations = reservationService.getAllReservationsByUserId(validUserId);
        Reservation reservationToBeUpdated = allReservations.get(0);

        // given
        Reservation reservationUpdateRequest = new Reservation();
        reservationUpdateRequest.setUserId(validUserId);
        reservationUpdateRequest.setCarparkId(100000L);
        reservationUpdateRequest.setCheckinDate("2031-05-20");
        reservationUpdateRequest.setCheckinTime("08:00");
        reservationUpdateRequest.setCheckoutDate("2031-05-20");
        reservationUpdateRequest.setCheckoutTime("18:00");

        // then: update the reservation
        reservationService.updateReservation(reservationToBeUpdated, reservationUpdateRequest);

        assertEquals(reservationUpdateRequest.getUserId(), reservationToBeUpdated.getUserId());
        assertEquals(reservationUpdateRequest.getCarparkId(), reservationToBeUpdated.getCarparkId());
        assertEquals(reservationUpdateRequest.getCheckinDate(), reservationToBeUpdated.getCheckinDate());
        assertEquals(reservationUpdateRequest.getCheckinTime(), reservationToBeUpdated.getCheckinTime());
        assertEquals(reservationUpdateRequest.getCheckoutDate(), reservationToBeUpdated.getCheckoutDate());
        assertEquals(reservationUpdateRequest.getCheckoutTime(), reservationToBeUpdated.getCheckoutTime());
        assertEquals(30,createdReservation.getParkingFee(), 0.05d);
    }

    @Test
    public void testUpdateReservationTwoHoursInAdvance_throwHttpStatusException_403() {
        long validUserId = user.getId();
        // new user does not have any reservations
        assertTrue(reservationRepository.findAllByUserId(validUserId).isEmpty());

        // create new reservation
        Reservation reservationToBeUpdated = reservationService.createReservation(testReservation);

        // given
        String newCheckinDate = LocalDate.now().plusDays(-2).toString();
        String newCheckoutDate = LocalDate.now().plusDays(2).toString();

        Reservation reservationUpdateRequest = new Reservation();
        reservationUpdateRequest.setUserId(validUserId);
        reservationUpdateRequest.setCarparkId(100000L);
        reservationUpdateRequest.setCheckinDate(newCheckinDate);
        reservationUpdateRequest.setCheckinTime("18:00");
        reservationUpdateRequest.setCheckoutDate(newCheckoutDate);
        reservationUpdateRequest.setCheckoutTime("18:00");

        try {
            // then: try to update the reservation
            reservationService.updateReservation(reservationToBeUpdated, reservationUpdateRequest);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(403, ex.getRawStatusCode());
        }
    }

}
