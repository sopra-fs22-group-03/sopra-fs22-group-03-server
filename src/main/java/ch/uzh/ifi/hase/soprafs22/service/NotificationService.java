package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.BookingType;
import ch.uzh.ifi.hase.soprafs22.constant.PaymentStatus;
import ch.uzh.ifi.hase.soprafs22.constant.SplitRequestStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Billing;
import ch.uzh.ifi.hase.soprafs22.entity.Notification;
import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.entity.Reservation;
import ch.uzh.ifi.hase.soprafs22.repository.NotificationRepository;
import ch.uzh.ifi.hase.soprafs22.repository.ParkingslipRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Notification Service
 * This class is the "worker" and responsible for all functionality related to
 * notifications
 */
@Service
@Transactional
public class NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;


    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

//    public Parkingslip getSingleParkingslipByParkingslipId(long parkingslipId) {
//        Parkingslip parkingslipByParkingslipId = parkingslipRepository.findById(parkingslipId);
//
//        // throw error if carpark does not exist
//        if (parkingslipByParkingslipId == null) {
//            String baseErrorMessage = "The parkingslip with id %d was not found";
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, parkingslipId));
//        }
//        return parkingslipByParkingslipId;
//    }

    // create notification from billing
    public Notification createNotificationFromBilling(Billing billing) {

        Notification notification = new Notification();
        notification.setBillingId(billing.getId());
        notification.setRequesterId(billing.getUserId());
        notification.setRequestedId(billing.getUserIdOfSplitPartner());
        notification.setSplitRequestStatus("pending");
        notification = notificationRepository.save(notification);
        notificationRepository.flush();

        return notification;
    }

    // get all pending notifications of user
    public List<Notification> getAllPendingNotificationsByUserId(long userId) {
        return this.notificationRepository.findAllByRequestedIdAndSplitRequestStatus(userId, "pending");
    }
}