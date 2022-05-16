package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.BookingType;
import ch.uzh.ifi.hase.soprafs22.constant.PaymentStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Billing;
import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.BillingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillingRepository billingRepository;

    @InjectMocks
    private BillingService billingService;

    @Test
    void testGetAllBillingsByUserId() {
        List<Billing> allBillings = new ArrayList();
        allBillings.add(new Billing(500001, 200001, BookingType.PARKINGSLIP, PaymentStatus.OUTSTANDING, 300001, 0));
        allBillings.add(new Billing(500002, 200001, BookingType.PARKINGSLIP, PaymentStatus.OUTSTANDING, 300002, 0));
        allBillings.add(new Billing(500002, 200001, BookingType.PARKINGSLIP, PaymentStatus.OUTSTANDING, 300003, 0));

        given(billingRepository.findAllByUserId(200001)).willReturn(allBillings);

        // get all billings of user with id 200001
        List<Billing> expected = billingService.getAllBillingsByUserId(200001);

        assertEquals(expected, allBillings);
    }

    @Test
    void testGetSingleBillingByBillingId() {
        final Billing billing = new Billing(500001, 200001, BookingType.PARKINGSLIP, PaymentStatus.OUTSTANDING, 300001, 0);

        long billingId = billing.getId();

        given(billingRepository.findById(billingId)).willReturn(billing);

        // get the expected bill
        final Billing expected = billingService.getSingleBillingByBillingId(billingId);

        assertEquals(expected, billing);
    }

    @Test
    void testGetSingleBillingByBillingId_throwHttpStatusException() {
        long invalidBillingId = 0;

        //try to get a billing that does not exist; status error with code 404 should be thrown
        try {
            billingService.getSingleBillingByBillingId(0);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(404, ex.getRawStatusCode());
        }
    }

    @Test
    void testPayBilling() {
        final Billing billing = new Billing(500001, 200001, BookingType.PARKINGSLIP, PaymentStatus.OUTSTANDING, 300001, 0);

        // pay the billing
        billingService.payBilling(billing);

        assertEquals(billing.getPaymentStatus(), PaymentStatus.PAID);
    }

    @Test
    void splitBillingWithRequestedUser() {
        // given
        Billing billingBeforeSplit = new Billing(500001, 200001, BookingType.PARKINGSLIP, PaymentStatus.OUTSTANDING, 300001, 0);

        User requestedUser = new User();
        requestedUser.setPassword("password");
        requestedUser.setUsername("testUsername");
        requestedUser.setStreet("Musterstrasse");
        requestedUser.setStreetNo("1");
        requestedUser.setZipCode(8000L);
        requestedUser.setCity("Zurich");
        requestedUser.setEmail("tes@test.ch");
        requestedUser.setPhoneNumber("'0790000001'");
        requestedUser.setLicensePlate("ZH1");
        requestedUser.setCreditCardNumber(1111111111111111L);
        requestedUser.setIsLoggedIn(true);
        requestedUser.setIsManager(false);
        requestedUser.setToken("'14527952-3ce3-465e-9674-b7ef35d02911'");
        requestedUser.setId(200002L);
        
        // update billing
        when(billingRepository.save(any(Billing.class))).thenReturn(billingBeforeSplit);
        Billing billingAfterSplitRequest = billingService.splitBillingWithRequestedUser(requestedUser, billingBeforeSplit);

        assertEquals(PaymentStatus.SPLIT_REQUESTED, billingAfterSplitRequest.getPaymentStatus());
        assertEquals(200002L, billingAfterSplitRequest.getUserIdOfSplitPartner());
    }
}