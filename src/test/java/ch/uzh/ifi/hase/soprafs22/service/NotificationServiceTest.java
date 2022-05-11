package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.BookingType;
import ch.uzh.ifi.hase.soprafs22.constant.PaymentStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Billing;
import ch.uzh.ifi.hase.soprafs22.entity.Carpark;
import ch.uzh.ifi.hase.soprafs22.entity.Notification;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.BillingRepository;
import ch.uzh.ifi.hase.soprafs22.repository.NotificationRepository;
import org.aspectj.weaver.ast.Not;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock
    NotificationRepository notificationRepository;

    @Mock
    BillingRepository billingRepository;

    @InjectMocks
    NotificationService notificationService;

    @InjectMocks
    BillingService billingService;

    private Notification notification;
    @BeforeEach
    void setUp() {
        notification = new Notification();
        notification.setId(1);
        notification.setSplitRequestStatus("pending");
        notification.setBillingId(500001);
        notification.setRequesterId(200001);
        notification.setRequestedId(200002);

    }

    @Test
    void testGetSingleNotificationByNotificationId() {
        long validNotificationId = notification.getId();

        Mockito.when(notificationRepository.findById(validNotificationId)).thenReturn(notification);

        Notification returnedNotification = notificationService.getSingleNotificationByNotificationId(validNotificationId);

        assertEquals(returnedNotification, notification);
    }

    @Test
    void testGetSingleNotificationByNotificationId_throwHttpStatusException() {
        long invalidNotificationId = 0;

        //try to get a user that does not exist; status error with code 404 should be thrown
        try {
            notificationService.getSingleNotificationByNotificationId(invalidNotificationId);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(404, ex.getRawStatusCode());
        }    }

    // TODO
    @Test
    void createNotificationFromBilling() {
    }

    @Test
    void testGetAllPendingNotificationsByUserId() {
        long validNotificationId = notification.getId();

        List<Notification> allNotifications = Collections.singletonList(notification);

        // this mocks the BillingService
        given(notificationService.getAllPendingNotificationsByUserId(validNotificationId)).willReturn(allNotifications);

        // get all carparks
        List<Notification> expected = notificationService.getAllPendingNotificationsByUserId(validNotificationId);

        assertEquals(expected, allNotifications);
    }

    @Test
    void handleResponse_declined() {
    }
}