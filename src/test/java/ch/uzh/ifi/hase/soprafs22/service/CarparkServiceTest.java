package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import ch.uzh.ifi.hase.soprafs22.rss.FeedMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CarparkServiceTest {

    @Mock
    CarparkRepository carparkRepository;
    @Mock
    ParkingslipRepository parkingslipRepository;
    @Mock
    ReservationRepository reservationRepository;
    @Mock
    UserRepository userRepository;
        Carpark testCarpark;
    User testUser;
    Parkingslip testParkingslipCheckedin;

    @InjectMocks
    CarparkService carparkService;
    @InjectMocks
    UserService userService;
    @InjectMocks
    FeedMessage feedMessage;

    @BeforeEach
    void setUp() {
        testCarpark = new Carpark();
        testCarpark.setId(100101);
        testCarpark.setName("Test");
        testCarpark.setMaxCapacity(100);
        testCarpark.setNumOfEmptySpaces(0);
        testCarpark.setStreet("Teststrasse");
        testCarpark.setStreetNo("1");
        testCarpark.setZipCode(8000);
        testCarpark.setCity("Zürich");
        testCarpark.setLatitude(0);
        testCarpark.setLatitude(0);
        testCarpark.setOpen(false);
        testCarpark.setWeekdayOpenFrom("06:00");
        testCarpark.setWeekdayOpenTo("23:59");
        testCarpark.setWeekendOpenFrom("06:00");
        testCarpark.setWeekendOpenTo("23:59");
        testCarpark.setHourlyTariff(5);
        testCarpark.setRemarks("NONE");
        testCarpark.setLink("https://www.pls-zh.ch/parkhaus/accu.jsp?pid=accu");

        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("password");
        testUser.setUsername("testUsername");
        testUser.setStreet("Musterstrasse");
        testUser.setStreetNo("1");
        testUser.setZipCode(8000L);
        testUser.setCity("Zurich");
        testUser.setEmail("tes@test.ch");
        testUser.setPhoneNumber("'0790000001'");
        testUser.setLicensePlate("ZH1");
        testUser.setCreditCardNumber(1111111111111111L);
        testUser.setIsLoggedIn(true);
        testUser.setIsManager(false);
        testUser.setToken("'14527952-3ce3-465e-9674-b7ef35d02911'");

        testParkingslipCheckedin = new Parkingslip();
        testParkingslipCheckedin.setId(1L);
        testParkingslipCheckedin.setUserId(1L);
        testParkingslipCheckedin.setCarparkId(100001L);
        testParkingslipCheckedin.setCheckinDate("08.04.2022");
        testParkingslipCheckedin.setCheckinTime("08:00");
        testParkingslipCheckedin.setCheckoutDate(null);
        testParkingslipCheckedin.setCheckoutTime(null);
        testParkingslipCheckedin.setLicensePlate("ZH11");
        testParkingslipCheckedin.setParkingFee(0);


    }

    @Test
    void testGetCarparks() {
        List<Carpark> allCarparks = Collections.singletonList(testCarpark);

        // this mocks the BillingService
        given(carparkService.getCarparks()).willReturn(allCarparks);

        // get all carparks
        List<Carpark> expected = carparkService.getCarparks();

        assertEquals(expected, allCarparks);
    }

    @Test
    void testGetSingleCarparkById_() {
        Mockito.when(carparkRepository.findById(Mockito.anyLong())).thenReturn(testCarpark);

        Carpark returnedCarpark = carparkService.getSingleCarparkById(100101);

        assertEquals(testCarpark, returnedCarpark);
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

    /*@Test
    void testPerformCheckinOfUser() {
        testCarpark.setNumOfEmptySpaces(100);
        List<Reservation> reservations = new ArrayList<>();

        given(carparkRepository.findById(Mockito.anyLong())).willReturn(testCarpark);
        given(carparkRepository.findCarparkByLink(Mockito.anyString())).willReturn(testCarpark);
        given(reservationRepository.findAllByCarparkId(Mockito.anyLong())).willReturn(reservations);

        given(carparkService.performCheckinOfUser(testUser, testCarpark)).willReturn(testParkingslipCheckedin);
        Parkingslip expected = carparkService.performCheckinOfUser(testUser, testCarpark);

        assertEquals(expected, testParkingslipCheckedin);
    }*/

    //TODO
/*@Test
    void testPerformCheckinOfUserInFullCarpark_throwHttpStatusException() {
        testCarpark.setNumOfEmptySpaces(0);


        try {
            carparkService.performCheckinOfUser(testUser, testCarpark);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(403, ex.getRawStatusCode());
        }
    }*/

    //TODO
     /*@Test
    void testPerformCheckoutOfUser() {
        testCarpark.setNumOfEmptySpaces(100);
        testParkingslipCheckedin = carparkService.performCheckinOfUser(testUser, testCarpark);


        Parkingslip parkingslipCheckin = carparkService.performCheckoutOfUser(user, testCarpark);

        assertEquals(101, testCarpark.getNumOfEmptySpaces());
    }*/



    @Test
    void testPerformCheckoutOfUserNotCheckedin_throwHttpStatusException() {
        testCarpark.setNumOfEmptySpaces(100);

        try {
            carparkService.performCheckoutOfUser(testUser, testCarpark);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(403, ex.getRawStatusCode());
        }
    }

    @Test
    void countReservationsInCarparkAtDateTime() {
    }
}