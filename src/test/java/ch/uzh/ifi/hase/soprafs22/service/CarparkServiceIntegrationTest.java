package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Carpark;
import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.entity.Reservation;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.BillingRepository;
import ch.uzh.ifi.hase.soprafs22.repository.CarparkRepository;
import ch.uzh.ifi.hase.soprafs22.repository.ParkingslipRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

/**
 * Test class for the UserResource REST resource.
 *
 * @see CarparkService
 */
@WebAppConfiguration
@SpringBootTest
public class CarparkServiceIntegrationTest {

    @Autowired
    @Qualifier("carparkRepository")
    private CarparkRepository carparkRepository;
    @Autowired
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
    private BillingService billingService;

    private User user;

    @BeforeEach
    public void setup() {
        parkingslipRepository.deleteAll();
        billingRepository.deleteAll();
        userRepository.deleteAll();

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

    @Test
    void testGetCarparks() {

        // get all carparks
        List<Carpark> expected = carparkService.getCarparks();

        assertEquals(expected.size(), 36);
    }

    @Test
    void testGetSingleCarparkById() {
        Carpark returnedCarpark = carparkService.getSingleCarparkById(100001);
        Carpark carpark = carparkRepository.findById(100001);

        assertEquals(returnedCarpark.getId(), carpark.getId());
        assertEquals(returnedCarpark.getName(), carpark.getName());
        assertEquals(returnedCarpark.getMaxCapacity(), carpark.getMaxCapacity());
        assertEquals(returnedCarpark.getStreet(), carpark.getStreet());
        assertEquals(returnedCarpark.getStreetNo(), carpark.getStreetNo());
        assertEquals(returnedCarpark.getZipCode(), carpark.getZipCode());
        assertEquals(returnedCarpark.getCity(), carpark.getCity());
        assertEquals(returnedCarpark.getLatitude(), carpark.getLatitude());
        assertEquals(returnedCarpark.getLongitude(), carpark.getLongitude());
        assertEquals(returnedCarpark.isOpen(), carpark.isOpen());
        assertEquals(returnedCarpark.getWeekdayOpenFrom(), carpark.getWeekdayOpenFrom());
        assertEquals(returnedCarpark.getWeekdayOpenTo(), carpark.getWeekdayOpenTo());
        assertEquals(returnedCarpark.getWeekendOpenFrom(), carpark.getWeekendOpenFrom());
        assertEquals(returnedCarpark.getWeekendOpenTo(), carpark.getWeekendOpenTo());
        assertEquals(returnedCarpark.getHourlyTariff(), carpark.getHourlyTariff());
        assertEquals(returnedCarpark.getLink(), carpark.getLink());
        }

    @Test
    void testGetSingleCarparkById_throwHttpStatusException() {
        long invalidCarparkId = 0;

        //try to get a parkingslip that does not exist; status error with code 404 should be thrown
        try {
            carparkService.getSingleCarparkById(invalidCarparkId);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(404, ex.getRawStatusCode());
        }
    }

    @Test
    void testPerformCheckinOfUser() {
        User createdUser = userService.createUser(user);

        Carpark carparkToCheckin = carparkService.getSingleCarparkById(100001);
        carparkToCheckin.setNumOfEmptySpaces(100);

        // checkin
        Parkingslip parkingslip = carparkService.performCheckinOfUser(createdUser,carparkToCheckin);

        boolean isCheckedIn = carparkService.isUserCheckedInCarpark(createdUser, carparkToCheckin);
        assertEquals(isCheckedIn, true);
        assertNotNull(parkingslip.getCheckinDate());
        assertNotNull(parkingslip.getCheckinTime());
        assertNull(parkingslip.getCheckoutDate());
        assertNull(parkingslip.getCheckoutTime());
    }

    @Test
    void testPerformCheckinOfUserAlreadyCheckedin_throwHttpStatusException() {
        User createdUser = userService.createUser(user);

        Carpark carparkToCheckin = carparkService.getSingleCarparkById(100001);

        Parkingslip parkingslip = carparkService.performCheckinOfUser(createdUser,carparkToCheckin);

        try {
            Parkingslip parkingslip2 = carparkService.performCheckinOfUser(createdUser,carparkToCheckin);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(403, ex.getRawStatusCode());
        }
    }

    @Test
    void testPerformCheckoutOfUser() {
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

        // if a user checks-out successfully, he/she should get a bill
        assertNotNull(billingService.getAllBillingsByUserId(createdUser.getId()));
    }

    @Test
    void testPerformCheckoutOfUserNotCheckedin_throwHttpStatusException() {
        User createdUser = userService.createUser(user);

        Carpark carparkToCheckout = carparkService.getSingleCarparkById(100001);
        carparkToCheckout.setNumOfEmptySpaces(100);

        try {
            carparkService.performCheckoutOfUser(createdUser, carparkToCheckout);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(403, ex.getRawStatusCode());
        }
    }
}