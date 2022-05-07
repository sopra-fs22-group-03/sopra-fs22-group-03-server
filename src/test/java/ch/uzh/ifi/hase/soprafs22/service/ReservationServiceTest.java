package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Carpark;
import ch.uzh.ifi.hase.soprafs22.entity.Reservation;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    ReservationRepository reservationRepository;
    UserService userService;
    CarparkService carparkService;
    BillingRepository billingRepository;
    Reservation testReservation;
    Reservation reservationUpdateRequest;
    Carpark testCarpark;

    @InjectMocks
    ReservationService reservationService;

    @BeforeEach
    public void setup() {
        // given
        testReservation = new Reservation();
        testReservation.setUserId(1L);
        testReservation.setCarparkId(100001L);
        testReservation.setCheckinDate("08.05.2022");
        testReservation.setCheckinTime("08:00");
        testReservation.setCheckoutDate("08.05.2022");
        testReservation.setCheckoutTime("18:00");
    }

    @Test
    public void createNewReservation_carparkAlreadyFull_throwHttpStatusException() {
        // setup spy object
        ReservationService reservationService1 = Mockito.spy(reservationService);

        // since carpark is already full, the helper method checkIfReservationIsPossible() should throw a FORBIDDEN exception
        Mockito.doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN)).when(reservationService1).checkIfReservationIsPossible(Mockito.any());

        //try to create a new reservation but expect a status error with code 403 to be thrown
        try {
            reservationService1.createReservation(testReservation);
            Assertions.fail("FORBIDDEN exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(403, ex.getRawStatusCode());
        }
    }

    @Test
    public void getSingleReservationByReservationIdTest() {
        Mockito.when(reservationRepository.findById(Mockito.anyLong())).thenReturn(testReservation);

        Reservation receivedReservation = reservationService.getSingleReservationByReservationId(1);

        assertEquals(testReservation.getCheckoutTime(), receivedReservation.getCheckoutTime());
    }

    @Test
    public void getSingleReservationByReservationIdTest_throwHttpStatusException() {
        long invalidReservationId = 0;

        //try to get a parkingslip that does not exist; status error with code 404 should be thrown
        try {
            reservationService.getSingleReservationByReservationId(invalidReservationId);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(404, ex.getRawStatusCode());
        }
    }

//    @Test
//    public void updateExistingReservation_validInput_success() {
//        // setup spy object
//        ReservationService reservationService1 = Mockito.spy(reservationService);
//
//        // assume that reservation change is more than 2 hours in advance
//        Mockito.when(reservationService1.isReservationCheckInXMinutesInAdvance(Mockito.any(), Mockito.anyLong())).thenReturn(true);
//
//        // mock parkingfee calculation
//        Mockito.when(reservationService1.calculateParkingFeeOfReservation(Mockito.any(Reservation.class))).thenReturn(10F);
//
//        // test
//        Reservation updatedReservation = reservationService1.updateReservation(testReservation, reservationUpdateRequest);
//
//        assertEquals(updatedReservation.getUserId(), testReservation.getUserId());
//        assertEquals(updatedReservation.getCarparkId(), testReservation.getCarparkId());
//        assertEquals(updatedReservation.getCheckinDate(), testReservation.getCheckinDate());
//        assertEquals(updatedReservation.getCheckinTime(), testReservation.getCheckinTime());
//        assertEquals(updatedReservation.getCheckoutDate(), testReservation.getCheckoutDate());
//        assertEquals(updatedReservation.getCheckoutTime(), testReservation.getCheckoutTime());
//
//    }



//
//    @Test
//    public void createNewReservation_carparkAlreadyFull_throwHttpStatusException() {
//
//        // setup mock variables
//        long userId = 1L;
//        long carparkId = 100001;
//        String carparkName = "TestCarpark";
//        long maxCapacity = 100L;
//        long numOfEmptySpaces = 99L;
//        String street = "Test street";
//        String streetNo = "14";
//        long zipCode = 8000L;
//        String city = "Zurich";
//        double longitude = 8.5391825;
//        double latitude = 47.3686498;
//        boolean isOpen = true;
//        String weekdayOpenFrom = "01:00";
//        String weekdayOpenTo = "23:00";
//        String weekendOpenFrom = "06:00";
//        String weekendOpenTo = "20:00";
//        long hourlyTariff = 3L;
//
//
//        // create new test reservation with information specified in receiving DTO
//        // (userId, carparkId, checkinDate, checkinTime, checkoutDate, checkoutTime)
//        testReservation = new Reservation();
//        testReservation.setUserId(userId);
//        testReservation.setCarparkId(carparkId);
//        testReservation.setCheckinDate("08.05.2022");
//        testReservation.setCheckinTime("08:00");
//        testReservation.setCheckoutDate("08.05.2022");
//        testReservation.setCheckoutTime("18:00");
//
//        testCarpark = new Carpark();
//        testCarpark.setId(carparkId);
//        testCarpark.setMaxCapacity(maxCapacity);
//
//
//        // mock response of database
//        Mockito.when(reservationRepository.save(Mockito.any())).thenReturn(testReservation);
//        Mockito.doNothing().when(reservationService).checkIfReservationIsPossible(Mockito.any());
//        Mockito.when(testUserService.getSingleUserById(Mockito.anyLong())).thenReturn(testUser);
//        //Mockito.when(testCarparkService.getSingleCarparkById(Mockito.anyLong())).thenReturn(testCarpark);
//
//        Reservation newReservation = reservationService.createReservation(testReservation);
////        Mockito.verify(reservationRepository, Mockito.times(1)).save(Mockito.any());
////        Mockito.verify(carparkService, Mockito.times(1)).getSingleCarparkById(Mockito.any());
//
//        assertEquals(testReservation.getUserId(), newReservation.getUserId());
//        //assertEquals(testReservation.getCarparkId(), newReservation.getCarparkId());
//        assertEquals(testReservation.getCheckinDate(), newReservation.getCheckinDate());
//        assertEquals(testReservation.getCheckinTime(), newReservation.getCheckinTime());
//        assertEquals(testReservation.getCheckoutDate(), newReservation.getCheckoutDate());
//        assertEquals(testReservation.getCheckoutTime(), newReservation.getCheckoutTime());
//    }

    // mock that carpark is full, i.e., the method checkIfReservationIsPossible() throws a FORBIDDEN exception
//    @Test
//    public void createNewReservation_carparkAlreadyFull_throwHttpStatusException() {
//        MockitoAnnotations.openMocks(this);
//        // setup mock variables
//        long userId = 1L;
//        long carparkId = 100001;
//        String carparkName = "TestCarpark";
//        long maxCapacity = 100L;
//        long numOfEmptySpaces = 99L;
//        String street = "Test street";
//        String streetNo = "14";
//        long zipCode = 8000L;
//        String city = "Zurich";
//        double longitude = 8.5391825;
//        double latitude = 47.3686498;
//        boolean isOpen = true;
//        String weekdayOpenFrom = "01:00";
//        String weekdayOpenTo = "23:00";
//        String weekendOpenFrom = "06:00";
//        String weekendOpenTo = "20:00";
//        long hourlyTariff = 3L;
//
//        float parkingFee = 21;
//
//
//
//        // create new test reservation with information specified in receiving DTO
//        // (userId, carparkId, checkinDate, checkinTime, checkoutDate, checkoutTime)
//        testReservation = new Reservation();
//        testReservation.setUserId(userId);
//        testReservation.setCarparkId(carparkId);
//        testReservation.setCheckinDate("08.05.2022");
//        testReservation.setCheckinTime("08:00");
//        testReservation.setCheckoutDate("08.05.2022");
//        testReservation.setCheckoutTime("18:00");
//
//        testCarpark = new Carpark();
//        testCarpark.setId(carparkId);
//        testCarpark.setMaxCapacity(maxCapacity);
//
//        testUser = new User();
//        testUser.setId(userId);
//        testUser.setLicensePlate("ZH1");
//
//
//        // mock response of database
//        Mockito.doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN)).when(reservationService).checkIfReservationIsPossible(Mockito.any());
////        Mockito.when(reservationRepository.save(Mockito.any())).thenReturn(testReservation);
////        Mockito.when(userService.getSingleUserById(Mockito.any())).thenReturn(testUser);
////        Mockito.when(reservationService.calculateParkingFeeOfReservation(Mockito.any())).thenReturn(parkingFee);
////        Mockito.when(carparkService.getSingleCarparkById(Mockito.any())).thenReturn(testCarpark);
//
//        assertThrows(ResponseStatusException.class, () -> reservationService.createReservation(testReservation));
//
//
//    }

}