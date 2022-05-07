package ch.uzh.ifi.hase.soprafs22.controller;
import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.entity.Reservation;
import ch.uzh.ifi.hase.soprafs22.service.ParkingslipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.lang.Math;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * BillingControllerTest
 * This is a WebMvcTest which allows to test the ParkingslipController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the ParkingslipController works.
 */
@WebMvcTest(ParkingslipController.class)
class ParkingslipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParkingslipService parkingslipService;

    Parkingslip parkingslip;

    @BeforeEach
    void setUp() {
        parkingslip = new Parkingslip();
        parkingslip.setId(1L);
        parkingslip.setUserId(1L);
        parkingslip.setCarparkId(100001L);
        parkingslip.setCheckinDate("08.05.2032");
        parkingslip.setCheckinTime("08:00");
        parkingslip.setCheckoutDate("09.05.2032");
        parkingslip.setCheckoutTime("08:00");
        parkingslip.setLicensePlate("ZH11");
        parkingslip.setParkingFee(48);
    }

    @Test
    void testGetReservationInformation() throws Exception{
        // valid userId
        long validParkingslipId = parkingslip.getId();

        // when the mock object (reservationService) is called for getSingleReservationByReservationId() method with any parameters,
        // then it will return the object "reservation"
        given(parkingslipService.getSingleParkingslipByParkingslipId(validParkingslipId)).willReturn(parkingslip);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/parkingslips/{parkingslipId}", validParkingslipId);


        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parkingslipId", is(Math.toIntExact(parkingslip.getId()))))
                .andExpect(jsonPath("$.userId", is(Math.toIntExact(parkingslip.getUserId()))))
                .andExpect(jsonPath("$.carparkId", is(Math.toIntExact(parkingslip.getCarparkId()))))
                .andExpect(jsonPath("$.checkinDate", is(parkingslip.getCheckinDate())))
                .andExpect(jsonPath("$.checkinTime", is(parkingslip.getCheckinTime())))
                .andExpect(jsonPath("$.checkoutDate", is(parkingslip.getCheckoutDate())))
                .andExpect(jsonPath("$.checkoutTime", is(parkingslip.getCheckoutTime())))
                .andExpect(jsonPath("$.licensePlate", is(parkingslip.getLicensePlate())))
        ;
    }


    @Test
    void testGetReservationInformation_invaldiParkingslipId_404thrown() throws Exception {
        // given
        long invalidParkingslipId = 0; //some random invalid userId
        String baseErrorMessage = "The parkingslip with id %d was not found";
        String errorMessage = String.format(baseErrorMessage, invalidParkingslipId);

        ResponseStatusException notFoundException = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);

        // when the mock object (userService) is called for getSingleUserById() method with an invalid userId,
        // then it will return the object "notFoundException"
        given(parkingslipService.getSingleParkingslipByParkingslipId(invalidParkingslipId)).willThrow(notFoundException);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/parkingslips/{parkingslipId}", invalidParkingslipId);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is(errorMessage)));

    }
}