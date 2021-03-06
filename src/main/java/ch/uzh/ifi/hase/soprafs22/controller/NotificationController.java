package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Notification;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.NotificationGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.NotificationResponseDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.NotificationService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Parkingslip Controller
 * This class is responsible for handling all REST request that are related to
 * past parkingslips.
 * The controller will receive the request and delegate the execution to the
 * ParkingslipService and finally return the result.
 */
@RestController
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    NotificationController(NotificationService notificationService,
                           UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/users/{userId}/notifications")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<NotificationGetDTO> getAllPendingNotificationsOfUser(@PathVariable(value = "userId") long userId) {

        // fetch all reservations for provided userId
        List<Notification> notificationsOfUser= notificationService.getAllPendingNotificationsByUserId(userId);
        List<NotificationGetDTO> notificationGetDTOS = new ArrayList<>();

        // convert each reservation to the API representation
        int index = 0;
        for (Notification notification : notificationsOfUser) {
            // add converted notification
            notificationGetDTOS.add(DTOMapper.INSTANCE.convertEntityToNotificationGetDTO(notification));

            // add username of requester to DTO
            long requesterId = notificationGetDTOS.get(index).getRequesterId();
            User requester = userService.getSingleUserById(requesterId);
            notificationGetDTOS.get(index).setRequesterUsername(requester.getUsername());

            // increment index
            index++;
        }

        // return list of all reservationGetDTO
        return notificationGetDTOS;
    }

    @PostMapping("/notifications/{notificationId}/response")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void handleNotificationResponse(@RequestBody NotificationResponseDTO notificationResponseDTO, @PathVariable(value = "notificationId") long notificationId) {

        // get notification object
        Notification notification = notificationService.getSingleNotificationByNotificationId(notificationId);

        // handle response
        boolean requestIsAccepted = notificationResponseDTO.getRequestIsAccepted();
        notificationService.handleResponse(notification, requestIsAccepted);
    }
}
