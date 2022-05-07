package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.BookingType;
import ch.uzh.ifi.hase.soprafs22.constant.PaymentStatus;
import ch.uzh.ifi.hase.soprafs22.constant.SplitRequestStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Billing;
import ch.uzh.ifi.hase.soprafs22.entity.Notification;
import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.entity.Reservation;
import ch.uzh.ifi.hase.soprafs22.repository.BillingRepository;
import ch.uzh.ifi.hase.soprafs22.repository.NotificationRepository;
import ch.uzh.ifi.hase.soprafs22.repository.ParkingslipRepository;
import ch.uzh.ifi.hase.soprafs22.repository.ReservationRepository;
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
    private final BillingService billingService;
    private final ReservationService reservationService;
    private final ParkingslipService parkingslipService;
    private final ReservationRepository reservationRepository;
    private final ParkingslipRepository parkingslipRepository;
    private final BillingRepository billingRepository;


    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               BillingService billingService,
                               ReservationService reservationService,
                               ParkingslipService parkingslipService,
                               ReservationRepository reservationRepository,
                               ParkingslipRepository parkingslipRepository,
                               BillingRepository billingRepository) {
        this.notificationRepository = notificationRepository;
        this.billingService = billingService;
        this.reservationService = reservationService;
        this.parkingslipService = parkingslipService;
        this.reservationRepository = reservationRepository;
        this.parkingslipRepository = parkingslipRepository;
        this.billingRepository = billingRepository;
    }

    public Notification getSingleNotificationByNotificationId(long notificationId) {
        Notification notificationByNotificationId = notificationRepository.findById(notificationId);

        // throw error if notification does not exist
        if (notificationByNotificationId == null) {
            String baseErrorMessage = "The notification with id %d was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, notificationId));
        }
        return notificationByNotificationId;
    }

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

    public void handleResponse(Notification notification, boolean requestIsAccepted) {

        // extract associated billing with notification (=billing of requesting user)
        long billingId = notification.getBillingId();
        Billing billingOfRequestor = billingService.getSingleBillingByBillingId(billingId);

        // handle response
        if (requestIsAccepted) {
            // change status of notification
            notification.setSplitRequestStatus("accepted");

            // change status of requestor bill and half the amount
            billingOfRequestor.setPaymentStatus(PaymentStatus.SPLIT_ACCEPTED);
            if (billingOfRequestor.getBookingType() == BookingType.RESERVATION) {
                Reservation reservationOfRequestor = reservationService.getSingleReservationByReservationId(billingOfRequestor.getBookingId());
                float halfAmount = reservationOfRequestor.getParkingFee()/2;
                reservationOfRequestor.setParkingFee(halfAmount);
                reservationOfRequestor = reservationRepository.save(reservationOfRequestor);
                reservationRepository.flush();

                // create billing for requested user
                Billing billingOfRequested = new Billing();
                billingOfRequested.setUserId(billingOfRequestor.getUserIdOfSplitPartner());
                billingOfRequested.setBookingType(billingOfRequestor.getBookingType());
                billingOfRequested.setBookingId(billingOfRequestor.getBookingId());
                billingOfRequested.setPaymentStatus(PaymentStatus.OUTSTANDING);

                billingOfRequested = billingRepository.save(billingOfRequested);
                billingRepository.flush();
            }
            else if (billingOfRequestor.getBookingType() == BookingType.PARKINGSLIP) {
                //TODO case: split accepted for parkingslip
            }
        }
        else {
            //TODO case split not accepted
        }

    }
}