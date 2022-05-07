package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Notification;
import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.entity.Reservation;
import ch.uzh.ifi.hase.soprafs22.rest.dto.NotificationGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.ParkingslipGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.ReservationGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.NotificationService;
import ch.uzh.ifi.hase.soprafs22.service.ParkingslipService;
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

    NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/users/{userId}/notifications")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<NotificationGetDTO> getAllPendingNotificationsOfUser(@PathVariable(value = "userId") long userId) {

        // fetch all reservations for provided userId
        List<Notification> notificationsOfUser= notificationService.getAllPendingNotificationsByUserId(userId);
        List<NotificationGetDTO> notificationGetDTOS = new ArrayList<>();

        // convert each reservation to the API representation
        for (Notification notification : notificationsOfUser) {
            notificationGetDTOS.add(DTOMapper.INSTANCE.convertEntityToNotificationGetDTO(notification));
        }

        // return list of all reservationGetDTO
        return notificationGetDTOS;
    }
}
