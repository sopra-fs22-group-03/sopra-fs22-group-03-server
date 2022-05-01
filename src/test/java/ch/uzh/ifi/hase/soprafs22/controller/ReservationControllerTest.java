package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.constant.BookingType;
import ch.uzh.ifi.hase.soprafs22.constant.PaymentStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Billing;
import ch.uzh.ifi.hase.soprafs22.entity.Reservation;
import ch.uzh.ifi.hase.soprafs22.rest.dto.ReservationPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.ReservationPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.service.BillingService;
import ch.uzh.ifi.hase.soprafs22.service.CarparkService;
import ch.uzh.ifi.hase.soprafs22.service.ReservationService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;



@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private UserService userService;

    @MockBean
    private CarparkService carparkService;

    Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUserId(1L);
        reservation.setCarparkId(100001L);
        reservation.setCheckinDate("08.05.2032");
        reservation.setCheckinTime("08:00");
        reservation.setCheckoutDate("09.05.2032");
        reservation.setCheckoutTime("08:00");
        reservation.setLicensePlate("ZH11");
        reservation.setParkingFee(48);
    }

    // TODO
    @Test
    void getAllReservationsOfUser() throws Exception {
// valid userId
        long validUserId = reservation.getUserId();

        List<Reservation> allReservationsOfUser = Collections.singletonList(reservation);

        // this mocks the BillingService
        given(reservationService.getAllReservationsByUserId(validUserId)).willReturn(allReservationsOfUser);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/reservations", validUserId);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].reservationId", is(Math.toIntExact(reservation.getId()))))
        ;
    }

    @Test
    void testGetReservationInformation() throws Exception {
        // valid userId
        long validReservationId = reservation.getId();

        // when the mock object (reservationService) is called for getSingleReservationByReservationId() method with any parameters,
        // then it will return the object "reservation"
        given(reservationService.getSingleReservationByReservationId(validReservationId)).willReturn(reservation);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/reservations/{reservationId}", validReservationId);


        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId", is(Math.toIntExact(reservation.getId()))))
                .andExpect(jsonPath("$.userId", is(Math.toIntExact(reservation.getUserId()))))
                .andExpect(jsonPath("$.carparkId", is(Math.toIntExact(reservation.getCarparkId()))))
                .andExpect(jsonPath("$.checkinDate", is(reservation.getCheckinDate())))
                .andExpect(jsonPath("$.checkinTime", is(reservation.getCheckinTime())))
                .andExpect(jsonPath("$.checkoutDate", is(reservation.getCheckoutDate())))
                .andExpect(jsonPath("$.checkoutTime", is(reservation.getCheckoutTime())))
                .andExpect(jsonPath("$.licensePlate", is(reservation.getLicensePlate())))
        ;
    }

    @Test
    void testCreateReservation() throws Exception {

        ReservationPostDTO reservationPostDTO = new ReservationPostDTO();
        reservationPostDTO.setUserId(1L);
        reservationPostDTO.setCarparkId(100001L);
        reservationPostDTO.setCheckinDate("08.05.2032");
        reservationPostDTO.setCheckinTime("08:00");
        reservationPostDTO.setCheckoutDate("09.05.2032");
        reservationPostDTO.setCheckoutTime("08:00");


        // when the mock object (reservationService) is called for createReservation() method with any parameters,
        // then it will return the object "reservation"
        given(reservationService.createReservation(Mockito.any())).willReturn(reservation);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(reservationPostDTO));

        // then
        //assertTrue(true);
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservationId", is(Math.toIntExact(reservation.getId()))))
                .andExpect(jsonPath("$.userId", is(Math.toIntExact(reservation.getUserId()))))
                .andExpect(jsonPath("$.carparkId", is(Math.toIntExact(reservation.getCarparkId()))))
                .andExpect(jsonPath("$.checkinDate", is(reservation.getCheckinDate())))
                .andExpect(jsonPath("$.checkinTime", is(reservation.getCheckinTime())))
                .andExpect(jsonPath("$.checkoutDate", is(reservation.getCheckoutDate())))
                .andExpect(jsonPath("$.checkoutTime", is(reservation.getCheckoutTime())))
                .andExpect(jsonPath("$.licensePlate", is(reservation.getLicensePlate())));
    }

    // TODO
    @Test
    void testDeleteSingleReservation() throws Exception {
        long validReservationId = reservation.getId();

        // when the mock object (reservationService) is called for getSingleReservationByReservationId() method with any parameters,
        // then it will return the object "reservation"
        given(reservationService.deleteSingleReservationByReservationId(validReservationId)).willReturn(0);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder deleteRequest = delete("/reservations/{reservationId}", validReservationId);


        mockMvc.perform(deleteRequest)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void testUpdateReservation() throws Exception {
        long validReservationId = reservation.getId();

        ReservationPutDTO reservationPutDTO = new ReservationPutDTO();
        reservationPutDTO.setReservationId(1L);
        reservationPutDTO.setCheckinDate("08.05.2030");
        reservationPutDTO.setCheckinTime("08:00");
        reservationPutDTO.setCheckoutDate("09.05.2030");
        reservationPutDTO.setCheckoutTime("08:00");


        // when the mock object (reservationService) is called for createReservation() method with any parameters,
        // then it will return the object "reservation"
        given(reservationService.updateReservation(Mockito.any(), Mockito.any())).willReturn(reservation);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/reservations/{reservationId}", validReservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(reservationPutDTO));

        // then
        //assertTrue(true);
        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId", is(Math.toIntExact(reservation.getId()))))
                .andExpect(jsonPath("$.userId", is(Math.toIntExact(reservation.getUserId()))))
                .andExpect(jsonPath("$.carparkId", is(Math.toIntExact(reservation.getCarparkId()))))
                .andExpect(jsonPath("$.checkinDate", is(reservation.getCheckinDate())))
                .andExpect(jsonPath("$.checkinTime", is(reservation.getCheckinTime())))
                .andExpect(jsonPath("$.checkoutDate", is(reservation.getCheckoutDate())))
                .andExpect(jsonPath("$.checkoutTime", is(reservation.getCheckoutTime())))
                .andExpect(jsonPath("$.licensePlate", is(reservation.getLicensePlate())));
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }

}