package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.constant.BookingType;
import ch.uzh.ifi.hase.soprafs22.constant.PaymentStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Billing;
import ch.uzh.ifi.hase.soprafs22.entity.Carpark;
import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import ch.uzh.ifi.hase.soprafs22.service.BillingService;
import ch.uzh.ifi.hase.soprafs22.service.CarparkService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CarparkControllerTest
 * This is a WebMvcTest which allows to test the CarparkController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the CarparkController works.
 */
@WebMvcTest(CarparkController.class)
public class CarparkControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CarparkService carparkService;

  @MockBean
  private UserService userService;



  ////////////////// ENDPOINT 1 //////////////////
  @Test
  public void givenCarparks_whenGetAllCarparks_thenReturnJsonArrayOfAllCarparks() throws Exception {
      // given
      Carpark carpark = new Carpark();
      carpark.setId(1L);
      carpark.setName("TestCarpark");
      carpark.setMaxCapacity(100L);
      carpark.setNumOfEmptySpaces(99L);
      carpark.setStreet("Test street");
      carpark.setStreetNo("10");
      carpark.setZipCode(8000L);
      carpark.setCity("Zurich");
      carpark.setLongitude(8.5391825);
      carpark.setLatitude(47.3686498);
      carpark.setOpen(true);
      carpark.setWeekdayOpenFrom("01:00");
      carpark.setWeekdayOpenTo("23:00");
      carpark.setWeekendOpenFrom("06:00");
      carpark.setWeekendOpenTo("20:00");
      carpark.setHourlyTariff(3L);
      carpark.setRemarks("Carpark is closed on public holidays.");

      List<Carpark> allCarparks = Collections.singletonList(carpark);

      // this mocks the CarparkService
      given(carparkService.getCarparks()).willReturn(allCarparks);

      // when
      MockHttpServletRequestBuilder getRequest = get("/carparks");

      // then
      mockMvc.perform(getRequest).andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(1)))
              .andExpect(jsonPath("$[0].carparkId", is(Math.toIntExact(carpark.getId()))))
              .andExpect(jsonPath("$[0].name", is(carpark.getName())))
              .andExpect(jsonPath("$[0].maxCapacity", is(Math.toIntExact(carpark.getMaxCapacity()))))
              .andExpect(jsonPath("$[0].numOfEmptySpaces", is(Math.toIntExact(carpark.getNumOfEmptySpaces()))))
              .andExpect(jsonPath("$[0].street", is(carpark.getStreet())))
              .andExpect(jsonPath("$[0].streetNo", is(carpark.getStreetNo())))
              .andExpect(jsonPath("$[0].zipCode", is(Math.toIntExact(carpark.getZipCode()))))
              .andExpect(jsonPath("$[0].city", is(carpark.getCity())))
              .andExpect(jsonPath("$[0].longitude", is(carpark.getLongitude())))
              .andExpect(jsonPath("$[0].latitude", is(carpark.getLatitude())))
              .andExpect(jsonPath("$[0].weekdayOpenFrom", is(carpark.getWeekdayOpenFrom())))
              .andExpect(jsonPath("$[0].weekdayOpenTo", is(carpark.getWeekdayOpenTo())))
              .andExpect(jsonPath("$[0].weekendOpenFrom", is(carpark.getWeekendOpenFrom())))
              .andExpect(jsonPath("$[0].weekendOpenTo", is(carpark.getWeekendOpenTo())))
              .andExpect(jsonPath("$[0].hourlyTariff", is(Math.toIntExact(carpark.getHourlyTariff()))))
              .andExpect(jsonPath("$[0].remarks", is(carpark.getRemarks())))
      ;
  }

  ////////////////// ENDPOINT 2 //////////////////
  @Test
  public void getCarparkInformation_validInput_carparkRetrieved() throws Exception {
      // given
      Carpark carpark = new Carpark();
      carpark.setId(1L);
      carpark.setName("TestCarpark");
      carpark.setMaxCapacity(100L);
      carpark.setNumOfEmptySpaces(99L);
      carpark.setStreet("Test street");
      carpark.setStreetNo("10");
      carpark.setZipCode(8000L);
      carpark.setCity("Zurich");
      carpark.setLongitude(8.5391825);
      carpark.setLatitude(47.3686498);
      carpark.setOpen(true);
      carpark.setWeekdayOpenFrom("01:00");
      carpark.setWeekdayOpenTo("23:00");
      carpark.setWeekendOpenFrom("06:00");
      carpark.setWeekendOpenTo("20:00");
      carpark.setHourlyTariff(3L);
      carpark.setRemarks("Carpark is closed on public holidays.");

      // valid carparkId
      long validCarparkId = carpark.getId();

      // mock the carparkService
      given(carparkService.getSingleCarparkById(validCarparkId)).willReturn(carpark);

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder getRequest = get("/carparks/{carparkId}", validCarparkId);

      // then
      mockMvc.perform(getRequest)
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.carparkId", is(Math.toIntExact(carpark.getId()))))
              .andExpect(jsonPath("$.name", is(carpark.getName())))
              .andExpect(jsonPath("$.maxCapacity", is(Math.toIntExact(carpark.getMaxCapacity()))))
              .andExpect(jsonPath("$.numOfEmptySpaces", is(Math.toIntExact(carpark.getNumOfEmptySpaces()))))
              .andExpect(jsonPath("$.street", is(carpark.getStreet())))
              .andExpect(jsonPath("$.streetNo", is(carpark.getStreetNo())))
              .andExpect(jsonPath("$.zipCode", is(Math.toIntExact(carpark.getZipCode()))))
              .andExpect(jsonPath("$.city", is(carpark.getCity())))
              .andExpect(jsonPath("$.longitude", is(carpark.getLongitude())))
              .andExpect(jsonPath("$.latitude", is(carpark.getLatitude())))
              .andExpect(jsonPath("$.weekdayOpenFrom", is(carpark.getWeekdayOpenFrom())))
              .andExpect(jsonPath("$.weekdayOpenTo", is(carpark.getWeekdayOpenTo())))
              .andExpect(jsonPath("$.weekendOpenFrom", is(carpark.getWeekendOpenFrom())))
              .andExpect(jsonPath("$.weekendOpenTo", is(carpark.getWeekendOpenTo())))
              .andExpect(jsonPath("$.hourlyTariff", is(Math.toIntExact(carpark.getHourlyTariff()))))
              .andExpect(jsonPath("$.remarks", is(carpark.getRemarks())))
      ;
  }

    ////////////////// ENDPOINT 3 //////////////////
    @Test
    public void checkinIntoCarpark_validInput_receiveCheckinParkingslip() throws Exception {
        // given
        Carpark carpark = new Carpark();
        carpark.setId(1L);
        carpark.setName("TestCarpark");
        carpark.setMaxCapacity(100L);
        carpark.setNumOfEmptySpaces(99L);
        carpark.setStreet("Test street");
        carpark.setStreetNo("10");
        carpark.setZipCode(8000L);
        carpark.setCity("Zurich");
        carpark.setLongitude(8.5391825);
        carpark.setLatitude(47.3686498);
        carpark.setOpen(true);
        carpark.setWeekdayOpenFrom("01:00");
        carpark.setWeekdayOpenTo("23:00");
        carpark.setWeekendOpenFrom("06:00");
        carpark.setWeekendOpenTo("20:00");
        carpark.setHourlyTariff(3L);
        carpark.setRemarks("Carpark is closed on public holidays.");

        UserIdDTO userIdDTO = new UserIdDTO();
        userIdDTO.setUserId(1L);

        User userToBeCheckedIn = new User();
        userToBeCheckedIn.setId(userIdDTO.getUserId());
        userToBeCheckedIn.setLicensePlate("ZH1");

        Parkingslip parkingslipCheckin = new Parkingslip();
        parkingslipCheckin.setId(1L);
        parkingslipCheckin.setCarparkId(carpark.getId());
        parkingslipCheckin.setUserId(userToBeCheckedIn.getId());
        parkingslipCheckin.setCheckinDate("20.10.2022");
        parkingslipCheckin.setCheckinTime("08:00");
        parkingslipCheckin.setLicensePlate(userToBeCheckedIn.getLicensePlate());

        // valid carparkId
        long validCarparkId = carpark.getId();

        // mock the carparkService and userService
        given(carparkService.getSingleCarparkById(validCarparkId)).willReturn(carpark);
        given(userService.getSingleUserById(eq(userIdDTO.getUserId()))).willReturn(userToBeCheckedIn);

        // mock generation of parkingslip
        given(carparkService.performCheckinOfUser(userToBeCheckedIn, carpark)).willReturn(parkingslipCheckin);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/carparks/{carparkId}/checkin", validCarparkId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userIdDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parkingslipId", is(Math.toIntExact(parkingslipCheckin.getId()))))
                .andExpect(jsonPath("$.carparkId", is(Math.toIntExact(parkingslipCheckin.getCarparkId()))))
                .andExpect(jsonPath("$.userId", is(Math.toIntExact(parkingslipCheckin.getUserId()))))
                .andExpect(jsonPath("$.checkinDate", is(parkingslipCheckin.getCheckinDate())))
                .andExpect(jsonPath("$.checkinTime", is(parkingslipCheckin.getCheckinTime())))
                .andExpect(jsonPath("$.licensePlate", is(parkingslipCheckin.getLicensePlate())))
                .andExpect(jsonPath("$.checkoutDate").doesNotExist())
                .andExpect(jsonPath("$.checkoutTime").doesNotExist())
                .andExpect(jsonPath("$.parkingFee", is(0.0)))
        ;
    }

    ////////////////// ENDPOINT 4 //////////////////
    @Test
    public void checkoutOfCarpark_validInput_receiveCheckoutParkingslip() throws Exception {
        // given
        Carpark carpark = new Carpark();
        carpark.setId(1L);
        carpark.setName("TestCarpark");
        carpark.setMaxCapacity(100L);
        carpark.setNumOfEmptySpaces(99L);
        carpark.setStreet("Test street");
        carpark.setStreetNo("10");
        carpark.setZipCode(8000L);
        carpark.setCity("Zurich");
        carpark.setLongitude(8.5391825);
        carpark.setLatitude(47.3686498);
        carpark.setOpen(true);
        carpark.setWeekdayOpenFrom("01:00");
        carpark.setWeekdayOpenTo("23:00");
        carpark.setWeekendOpenFrom("06:00");
        carpark.setWeekendOpenTo("20:00");
        carpark.setHourlyTariff(3L);
        carpark.setRemarks("Carpark is closed on public holidays.");

        UserIdDTO userIdDTO = new UserIdDTO();
        userIdDTO.setUserId(1L);

        User userToBeCheckedOut = new User();
        userToBeCheckedOut.setId(userIdDTO.getUserId());
        userToBeCheckedOut.setLicensePlate("ZH1");

        Parkingslip parkingslipCheckout = new Parkingslip();
        parkingslipCheckout.setId(1L);
        parkingslipCheckout.setCarparkId(carpark.getId());
        parkingslipCheckout.setUserId(userToBeCheckedOut.getId());
        parkingslipCheckout.setCheckinDate("20.10.2022");
        parkingslipCheckout.setCheckinTime("08:00");
        parkingslipCheckout.setLicensePlate(userToBeCheckedOut.getLicensePlate());
        parkingslipCheckout.setCheckoutDate("20.10.2022");
        parkingslipCheckout.setCheckoutTime("18:00");
        parkingslipCheckout.setParkingFee(30);


        // valid carparkId
        long validCarparkId = carpark.getId();

        // mock the carparkService and userService
        given(carparkService.getSingleCarparkById(validCarparkId)).willReturn(carpark);
        given(userService.getSingleUserById(eq(userIdDTO.getUserId()))).willReturn(userToBeCheckedOut);

        // mock generation of parkingslip
        given(carparkService.performCheckoutOfUser(userToBeCheckedOut, carpark)).willReturn(parkingslipCheckout);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/carparks/{carparkId}/checkout", validCarparkId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userIdDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parkingslipId", is(Math.toIntExact(parkingslipCheckout.getId()))))
                .andExpect(jsonPath("$.carparkId", is(Math.toIntExact(parkingslipCheckout.getCarparkId()))))
                .andExpect(jsonPath("$.userId", is(Math.toIntExact(parkingslipCheckout.getUserId()))))
                .andExpect(jsonPath("$.checkinDate", is(parkingslipCheckout.getCheckinDate())))
                .andExpect(jsonPath("$.checkinTime", is(parkingslipCheckout.getCheckinTime())))
                .andExpect(jsonPath("$.licensePlate", is(parkingslipCheckout.getLicensePlate())))
                .andExpect(jsonPath("$.checkoutDate", is(parkingslipCheckout.getCheckoutDate())))
                .andExpect(jsonPath("$.checkoutTime", is(parkingslipCheckout.getCheckoutTime())))
                .andExpect(jsonPath("$.parkingFee", is((double)parkingslipCheckout.getParkingFee())))
        ;
    }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"password": "password", "username": "testUsername"}
   *
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}