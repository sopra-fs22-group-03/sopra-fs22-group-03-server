package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.BookingType;
import ch.uzh.ifi.hase.soprafs22.constant.PaymentStatus;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.*;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

/**
 * Test class for the UserResource REST resource.
 *
 * @see BillingService
 */
@WebAppConfiguration
@SpringBootTest
public class BillingServiceIntegrationTest {


    @Qualifier("billingRepository")
    @Autowired
    private BillingRepository billingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private CarparkService carparkService;
    @Autowired
    private BillingService billingService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private NotificationService notificationService;

    private User user;
    private User user_2;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        billingRepository.deleteAll();
        notificationRepository.deleteAll();

        // given
        user = new User();
        user.setPassword("password");
        user.setUsername("testUsername");
        user.setStreet("Musterstrasse");
        user.setStreetNo("1");
        user.setZipCode(8000L);
        user.setCity("Zurich");
        user.setEmail("test@test.ch");
        user.setPhoneNumber("'0790000001'");
        user.setLicensePlate("ZH1");
        user.setCreditCardNumber(1111111111111111L);

        user_2 = new User();
        user_2.setPassword("password");
        user_2.setUsername("testUsername2");
        user_2.setStreet("Musterstrasse");
        user_2.setStreetNo("1");
        user_2.setZipCode(8000L);
        user_2.setCity("Zurich");
        user_2.setEmail("test2@test.ch");
        user_2.setPhoneNumber("'0790000001'");
        user_2.setLicensePlate("ZH1");
        user_2.setCreditCardNumber(1111111111111111L);
    }

    @Test
    void testGetAllBillingsByUserIdAfterCreatingReservation() {
        User createdUser = userService.createUser(user);
        long validUserId = createdUser.getId();

        Reservation testReservation = new Reservation();
        testReservation.setUserId(validUserId);
        testReservation.setCarparkId(100001L);
        testReservation.setCheckinDate("2032-05-08");
        testReservation.setCheckinTime("08:00");
        testReservation.setCheckoutDate("2032-05-08");
        testReservation.setCheckoutTime("18:00");

        Reservation createdReservation = reservationService.createReservation(testReservation);

        // get all billings of the created user
        List<Billing> returnedBillings = billingService.getAllBillingsByUserId(validUserId);

        assertEquals(returnedBillings.size(), 1);
    }

    @Test
    void testPayBillingAfterCheckout() {
        User createdUser = userService.createUser(user);
        long validUserId = createdUser.getId();

        Carpark carparkToCheckin = carparkService.getSingleCarparkById(100001);
        carparkToCheckin.setNumOfEmptySpaces(100);

        // checkin
        Parkingslip parkingslipCheckedin = carparkService.performCheckinOfUser(createdUser,carparkToCheckin);
        // checkout
        Parkingslip parkingslipCheckedout = carparkService.performCheckoutOfUser(createdUser, carparkToCheckin);
        assertEquals(parkingslipCheckedin.getParkingFee(),
                     parkingslipCheckedout.getParkingFee(),
                    0.05d);

        List<Billing> returnedBillings = billingService.getAllBillingsByUserId(validUserId);
        Billing billing = returnedBillings.get(0);
        assertEquals(billing.getPaymentStatus(), PaymentStatus.OUTSTANDING);

        // pay the billing
        billingService.payBilling(billing);
        assertEquals(billing.getPaymentStatus(), PaymentStatus.PAID);
    }

    @Test
    void testPayBillingAfterCreatingReservation() {
        User createdUser = userService.createUser(user);
        long validUserId = createdUser.getId();

        Reservation testReservation = new Reservation();
        testReservation.setUserId(validUserId);
        testReservation.setCarparkId(100001L);
        testReservation.setCheckinDate("2032-05-08");
        testReservation.setCheckinTime("08:00");
        testReservation.setCheckoutDate("2032-05-08");
        testReservation.setCheckoutTime("18:00");

        Reservation createdReservation = reservationService.createReservation(testReservation);
        assertEquals(createdReservation.getParkingFee(), 20, 0.05d);

        List<Billing> returnedBillings = billingService.getAllBillingsByUserId(validUserId);
        Billing billing = returnedBillings.get(0);
        assertEquals(billing.getPaymentStatus(), PaymentStatus.OUTSTANDING);

        // pay the billing
        billingService.payBilling(billing);
        assertEquals(billing.getPaymentStatus(), PaymentStatus.PAID);
    }

    @Test
    void testSplitBillingWithRequestedUser() {
        User requestingUser = userService.createUser(user);
        long requestingUserId = requestingUser.getId();
        User requestedUser = userService.createUser(user_2);
        long requestedUserId = requestedUser.getId();

        // create reservation of which the bill will be split
        Reservation testReservation = new Reservation();
        testReservation.setUserId(requestingUserId);
        testReservation.setCarparkId(100001L);
        testReservation.setCheckinDate("2032-05-08");
        testReservation.setCheckinTime("08:00");
        testReservation.setCheckoutDate("2032-05-08");
        testReservation.setCheckoutTime("18:00");

        Reservation createdReservation = reservationService.createReservation(testReservation);
        assertEquals(createdReservation.getParkingFee(), 20, 0.05d);

        // get the bill of the created reservation
        List<Billing> returnedBillings = billingService.getAllBillingsByUserId(requestingUserId);
        Billing billing = returnedBillings.get(0);
        assertEquals(billing.getPaymentStatus(), PaymentStatus.OUTSTANDING);

        // split the bill
        Billing billingAfterSplitRequest = billingService.splitBillingWithRequestedUser(requestedUser, billing);

        // the userId of the split partner should not be null anymore
        assertEquals(requestedUserId, billingAfterSplitRequest.getUserIdOfSplitPartner());
        // the parking fee should remain unchanged. However, the payment status should have changed
        assertEquals(createdReservation.getParkingFee(), 20, 0.05d);
        assertEquals(PaymentStatus.SPLIT_REQUESTED, billingAfterSplitRequest.getPaymentStatus());
    }

}
