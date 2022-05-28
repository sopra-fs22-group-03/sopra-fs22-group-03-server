package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Carpark;
import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.BillingRepository;
import ch.uzh.ifi.hase.soprafs22.repository.CarparkRepository;
import ch.uzh.ifi.hase.soprafs22.repository.ParkingslipRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see ParkingslipService
 */
@WebAppConfiguration
@SpringBootTest
public class ParkingslipServiceIntegrationTest {
    @Autowired
    @Qualifier("parkingslipRepository")
    private ParkingslipRepository parkingslipRepository;
    @Autowired
    private BillingRepository billingRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private CarparkService carparkService;
    @Autowired
    private ParkingslipService parkingslipService;

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
    }

    @AfterEach
    void afterEach() {
        parkingslipRepository.deleteAll();
        userRepository.deleteAll();
        billingRepository.deleteAll();
    }

    @Test
    void testGetSingleParkingslipByParkingslipId() {
        User createdUser = userService.createUser(user);

        Carpark carparkToCheckin = carparkService.getSingleCarparkById(100001);
        carparkToCheckin.setNumOfEmptySpaces(100);

        boolean isCheckedinBeforeCheckin = carparkService.isUserCheckedInCarpark(createdUser, carparkToCheckin);
        assertEquals(isCheckedinBeforeCheckin, false);

        // checkin
        Parkingslip parkingslipCheckedin = carparkService.performCheckinOfUser(createdUser,carparkToCheckin);

        boolean isCheckedinAfterCheckin = carparkService.isUserCheckedInCarpark(createdUser, carparkToCheckin);
        assertEquals(isCheckedinAfterCheckin, true);

        Parkingslip parkingslipCheckedout = carparkService.performCheckoutOfUser(createdUser, carparkToCheckin);

        boolean isCheckediAfterCheckout= carparkService.isUserCheckedInCarpark(createdUser, carparkToCheckin);
        assertEquals(isCheckediAfterCheckout, false);

        assertEquals(parkingslipCheckedout.getId(), parkingslipCheckedin.getId());
        assertNotNull(parkingslipCheckedout.getCheckoutDate());
        assertNotNull(parkingslipCheckedout.getCheckoutTime());

        Parkingslip returnedParkingslip = parkingslipService.getSingleParkingslipByParkingslipId(parkingslipCheckedout.getId());

        assertEquals(parkingslipCheckedout.getUserId(), returnedParkingslip.getUserId());
        assertEquals(parkingslipCheckedout.getCarparkId(), returnedParkingslip.getCarparkId());
        assertEquals(parkingslipCheckedout.getCheckinDate(), returnedParkingslip.getCheckinDate());
        assertEquals(parkingslipCheckedout.getCheckinTime(), returnedParkingslip.getCheckinTime());
        assertEquals(parkingslipCheckedout.getCheckoutDate(), returnedParkingslip.getCheckoutDate());
        assertEquals(parkingslipCheckedout.getCheckoutTime(), returnedParkingslip.getCheckoutTime());
        assertEquals(parkingslipCheckedout.getParkingFee(), returnedParkingslip.getParkingFee(), 0.05d);
    }

    @Test
    void testGetSingleParkingslipByParkingslipId_throwHttpStatusException_404() {
        long invalidParkingslipId = 0;
        try {
            // then: try to update the reservation
            parkingslipService.getSingleParkingslipByParkingslipId(invalidParkingslipId);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(404, ex.getRawStatusCode());
        }
    }
}