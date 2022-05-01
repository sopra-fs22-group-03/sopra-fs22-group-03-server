package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.constant.BookingType;
import ch.uzh.ifi.hase.soprafs22.constant.PaymentStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Billing;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UsernameDTO;
import ch.uzh.ifi.hase.soprafs22.service.BillingService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.*;
import java.util.Collections;
import java.util.List;
import java.lang.Math;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * BillingControllerTest
 * This is a WebMvcTest which allows to test the BillingController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the BillingController works.
 */
@WebMvcTest(BillingController.class)
public class BillingControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BillingService billingService;

  @MockBean
  private UserService userService;



  ////////////////// ENDPOINT 1 //////////////////
  @Test
  public void givenBillingsOfUser_whenGetAllBillings_thenReturnJsonArrayOfAllBillings() throws Exception {
      // given
      Billing billing = new Billing();
      billing.setId(1L);
      billing.setUserId(1L);
      billing.setPaymentStatus(PaymentStatus.OUTSTANDING);
      billing.setBookingType(BookingType.RESERVATION);
      billing.setBookingId(1L);

      // valid userId
      long validUserId = billing.getUserId();

      List<Billing> allBillingsOfUser = Collections.singletonList(billing);

      // this mocks the BillingService
      given(billingService.getAllBillingsByUserId(eq(validUserId))).willReturn(allBillingsOfUser);

      // when
      MockHttpServletRequestBuilder getRequest = get("/users/{userId}/billing", validUserId);

      // then
      mockMvc.perform(getRequest).andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(1)))
              .andExpect(jsonPath("$[0].billingId", is(Math.toIntExact(billing.getId()))))
              .andExpect(jsonPath("$[0].bookingType", is(billing.getBookingType().toString())))
              .andExpect(jsonPath("$[0].bookingId", is(Math.toIntExact(billing.getBookingId()))))
              .andExpect(jsonPath("$[0].paymentStatus", is(billing.getPaymentStatus().toString())))
      ;
  }

  ////////////////// ENDPOINT 2 //////////////////
  @Test
  public void getBillingInformation_validInput_billingRetrieved() throws Exception {
      // given
      Billing billing = new Billing();
      billing.setId(1L);
      billing.setUserId(1L);
      billing.setPaymentStatus(PaymentStatus.OUTSTANDING);
      billing.setBookingType(BookingType.RESERVATION);
      billing.setBookingId(1L);


      // valid billingId
      long validBillingId = billing.getId();

      // when the mock object (billingService) is called for getSingleBillingByBillingId() method with any parameters,
      // then it will return the object "billing"
      given(billingService.getSingleBillingByBillingId(validBillingId)).willReturn(billing);

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder getRequest = get("/billings/{billingId}", validBillingId);

      // then
      mockMvc.perform(getRequest)
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.billingId", is(Math.toIntExact(billing.getId()))))
              .andExpect(jsonPath("$.bookingType", is(billing.getBookingType().toString())))
              .andExpect(jsonPath("$.bookingId", is(Math.toIntExact(billing.getBookingId()))))
              .andExpect(jsonPath("$.paymentStatus", is(billing.getPaymentStatus().toString())))

      ;
  }

    ////////////////// ENDPOINT 3 //////////////////
    @Test
    public void payOutstandingBilling_retrievePaidBilling() throws Exception {
        // given
        Billing unpaidbilling = new Billing();
        unpaidbilling.setId(1L);
        unpaidbilling.setUserId(1L);
        unpaidbilling.setPaymentStatus(PaymentStatus.OUTSTANDING);
        unpaidbilling.setBookingType(BookingType.RESERVATION);
        unpaidbilling.setBookingId(1L);

        Billing paidbilling = new Billing();
        paidbilling.setId(1L);
        paidbilling.setUserId(1L);
        paidbilling.setPaymentStatus(PaymentStatus.PAID);
        paidbilling.setBookingType(BookingType.RESERVATION);
        paidbilling.setBookingId(1L);


        // valid userId
        long validBillingId = unpaidbilling.getId();

        // mock objects
        given(billingService.getSingleBillingByBillingId(validBillingId)).willReturn(unpaidbilling);
        given(billingService.payBilling(Mockito.any(Billing.class))).willReturn(paidbilling);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/billings/{billingId}/pay", validBillingId);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.billingId", is(Math.toIntExact(paidbilling.getId()))))
                .andExpect(jsonPath("$.bookingType", is(paidbilling.getBookingType().toString())))
                .andExpect(jsonPath("$.bookingId", is(Math.toIntExact(paidbilling.getBookingId()))))
                .andExpect(jsonPath("$.paymentStatus", is(paidbilling.getPaymentStatus().toString())))

        ;
    }

    ////////////////// ENDPOINT 4 //////////////////
    @Test
    public void splitOutstandingBilling_retrieveSplitBilling() throws Exception {
        // given
        Billing billingBeforeSplit = new Billing();
        billingBeforeSplit.setId(1L);
        billingBeforeSplit.setUserId(1L);
        billingBeforeSplit.setPaymentStatus(PaymentStatus.OUTSTANDING);
        billingBeforeSplit.setBookingType(BookingType.RESERVATION);
        billingBeforeSplit.setBookingId(1L);

        Billing billingAfterSplitRequest = new Billing();
        billingAfterSplitRequest.setId(1L);
        billingAfterSplitRequest.setUserId(1L);
        billingAfterSplitRequest.setPaymentStatus(PaymentStatus.SPLIT_REQUESTED);
        billingAfterSplitRequest.setBookingType(BookingType.RESERVATION);
        billingAfterSplitRequest.setBookingId(1L);
        billingAfterSplitRequest.setUserIdOfSplitPartner(2L);

        UsernameDTO usernameDTO = new UsernameDTO();
        usernameDTO.setUsername("User2");

        User requestedUser = new User();



        // valid userId
        long validBillingId = billingBeforeSplit.getId();

        // mock objects
        given(billingService.getSingleBillingByBillingId(validBillingId)).willReturn(billingBeforeSplit);
        given(userService.getSingleUserByName(eq(usernameDTO.getUsername()))).willReturn(requestedUser);
        given(billingService.splitBillingWithRequestedUser(Mockito.any(User.class), eq(billingBeforeSplit))).willReturn(billingAfterSplitRequest);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/billings/{billingId}/split", validBillingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(usernameDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.billingId", is(Math.toIntExact(billingAfterSplitRequest.getId()))))
                .andExpect(jsonPath("$.bookingType", is(billingAfterSplitRequest.getBookingType().toString())))
                .andExpect(jsonPath("$.bookingId", is(Math.toIntExact(billingAfterSplitRequest.getBookingId()))))
                .andExpect(jsonPath("$.paymentStatus", is(billingAfterSplitRequest.getPaymentStatus().toString())))

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