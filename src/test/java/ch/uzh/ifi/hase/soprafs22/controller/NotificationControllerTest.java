package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Notification;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private UserService userService;

    Notification notificationPending;
    Notification notificationDeclined;
    User requesterUser;


    @BeforeEach
    void setUp() {
        notificationPending = new Notification();
        notificationPending.setId(600000);
        notificationPending.setBillingId(500000);
        notificationPending.setRequestedId(200001);
        notificationPending.setRequesterId(200002);
        notificationPending.setSplitRequestStatus("pending");

        notificationDeclined = new Notification();
        notificationDeclined.setId(600001);
        notificationDeclined.setBillingId(500000);
        notificationDeclined.setRequestedId(200001);
        notificationDeclined.setRequesterId(200002);
        notificationDeclined.setSplitRequestStatus("declined");

        requesterUser = new User();
        requesterUser.setId(200002);
        requesterUser.setUsername("Requester");


    }

    @Test
    void getAllPendingNotificationsOfUser() throws Exception {
        // valid userId
        long validRequestedId = notificationPending.getRequestedId();
        long validRequesterId = notificationPending.getRequesterId();

        List<Notification> allPendingNotifications = Collections.singletonList(notificationPending);

        // this mocks the CarparkService
        given(notificationService.getAllPendingNotificationsByUserId(validRequestedId)).willReturn(allPendingNotifications);

        // this mocks the UserService
        given(userService.getSingleUserById(validRequesterId)).willReturn(requesterUser);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/notifications", validRequestedId);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
        ;

    }

    @Test
    void handleNotificationResponse() {
    }
}