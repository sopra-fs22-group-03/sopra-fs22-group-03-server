package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.PaymentStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Billing;
import ch.uzh.ifi.hase.soprafs22.entity.Carpark;
import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.BillingRepository;
import ch.uzh.ifi.hase.soprafs22.repository.CarparkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Billing Service
 * This class is the "worker" and responsible for all functionality related to
 * the billing
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class BillingService {

    private final Logger log = LoggerFactory.getLogger(BillingService.class);

    private final BillingRepository billingRepository;

    @Autowired
    public BillingService(@Qualifier("billingRepository") BillingRepository billingRepository) {
        this.billingRepository = billingRepository;
    }

    public List<Billing> getAllBillingsByUserId(long userId) {
        return this.billingRepository.findAllByUserId(userId);
    }

    public Billing getSingleBillingByBillingId(long billingId) {
        Billing billingByBillingId = billingRepository.findById(billingId);

        // throw error if carpark does not exist
        if (billingByBillingId == null) {
            String baseErrorMessage = "The billing with id %d was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, billingId));
        }
        return billingByBillingId;
    }

    public Billing payBilling(Billing unpaidBilling) {
        // change payment status to "paid"
        unpaidBilling.setPaymentStatus(PaymentStatus.PAID);

        unpaidBilling = billingRepository.save(unpaidBilling);
        billingRepository.flush();

        return unpaidBilling;
    }

//    TODO (send notification to splitting partner in this method?)
    public Billing splitBillingWithRequestedUser(User requestedUser, Billing billingBeforeSplit) {

        // get userId of user the bill is split with
        long requestedUserId = requestedUser.getId();

        // update billing
        billingBeforeSplit.setUserIdOfSplitPartner(requestedUserId);
        billingBeforeSplit.setPaymentStatus(PaymentStatus.SPLIT_REQUESTED);

        // save billing
        Billing billingAfterSplitRequest = billingRepository.save(billingBeforeSplit);
        billingRepository.flush();

        return billingAfterSplitRequest;
    }

}