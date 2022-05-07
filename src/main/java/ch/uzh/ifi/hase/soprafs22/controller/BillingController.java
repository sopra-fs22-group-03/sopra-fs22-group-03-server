package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.BillingService;
import ch.uzh.ifi.hase.soprafs22.service.CarparkService;
import ch.uzh.ifi.hase.soprafs22.service.NotificationService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Billing Controller
 * This class is responsible for handling all REST request that are related to
 * the billing.
 * The controller will receive the request and delegate the execution to the
 * BillingService and finally return the result.
 */
@RestController
public class BillingController {

    private final BillingService billingService;
    private final UserService userService;
    private final NotificationService notificationService;

    BillingController(BillingService billingService, UserService userService,
                      NotificationService notificationService) {
        this.billingService = billingService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/users/{userId}/billing")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<BillingGetDTO> getAllBillingsOfUser(@PathVariable(value = "userId") long userId) {

        // fetch all billings for provided userId
        List<Billing> billingsOfUser= billingService.getAllBillingsByUserId(userId);
        List<BillingGetDTO> billingGetDTOS = new ArrayList<>();

        // convert each billing to the API representation
        for (Billing billing : billingsOfUser) {
            billingGetDTOS.add(DTOMapper.INSTANCE.convertEntityToBillingGetDTO(billing));
        }

        // return list of all billingGetDTO
        return billingGetDTOS;
    }

    @GetMapping("/billings/{billingId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public BillingGetDTO getBillingInformation(@PathVariable(value = "billingId") long billingId) {

        // get internal billling representation by provided path variable billingId
        Billing billingByBillingId = billingService.getSingleBillingByBillingId(billingId);

        // convert internal representation to API representation
        BillingGetDTO billingGetDTO = DTOMapper.INSTANCE.convertEntityToBillingGetDTO(billingByBillingId);

        return billingGetDTO;
    }

    @PostMapping("/billings/{billingId}/pay")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public BillingGetDTO payBillingByBillingId(@PathVariable(value = "billingId") long billingId) {

        // get internal billling representation by provided path variable billingId
        Billing unpaidBillingByBillingId = billingService.getSingleBillingByBillingId(billingId);

        // pay the billing
        Billing paidBilling = billingService.payBilling(unpaidBillingByBillingId);

        // convert internal representation to API representation
        BillingGetDTO paidBillingGetDTO = DTOMapper.INSTANCE.convertEntityToBillingGetDTO(paidBilling);

        return paidBillingGetDTO;
    }

    @PostMapping("/billings/{billingId}/split")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public BillingGetDTO splitBillingByBillingIdWithRequestedUser(@RequestBody UsernameDTO usernameDTO, @PathVariable(value = "billingId") long billingId) {

        // get splitting partner from usernameDTO object
        String username = usernameDTO.getUsername();
        User requestedUser = userService.getSingleUserByName(username);

        // get internal billling representation by provided path variable billingId
        Billing billingBeforeSplit = billingService.getSingleBillingByBillingId(billingId);

        // split the billing between owner of billing (=myself) and requestedUser
        Billing billingAfterSplitRequest = billingService.splitBillingWithRequestedUser(requestedUser, billingBeforeSplit);

        // convert internal representation to API representation
        BillingGetDTO billingAfterSplitDTO = DTOMapper.INSTANCE.convertEntityToBillingGetDTO(billingAfterSplitRequest);

        // generate new notification to the user the bill is split with
        Notification newNotification = notificationService.createNotificationFromBilling(billingAfterSplitRequest);

        return billingAfterSplitDTO;
    }
}
