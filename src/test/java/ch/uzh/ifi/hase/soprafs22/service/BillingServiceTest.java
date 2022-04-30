package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.BookingType;
import ch.uzh.ifi.hase.soprafs22.constant.PaymentStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Billing;
import ch.uzh.ifi.hase.soprafs22.repository.BillingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

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
    void testPayBilling() {
        final Billing billing = new Billing(500001, 200001, BookingType.PARKINGSLIP, PaymentStatus.OUTSTANDING, 300001, 0);

        // pay the billing
        billingService.payBilling(billing);

        assertEquals(billing.getPaymentStatus(), PaymentStatus.PAID);
    }
}