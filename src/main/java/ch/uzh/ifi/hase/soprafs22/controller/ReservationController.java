package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Billing;
import ch.uzh.ifi.hase.soprafs22.entity.Reservation;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.BillingService;
import ch.uzh.ifi.hase.soprafs22.service.ReservationService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Reservation Controller
 * This class is responsible for handling all REST request that are related to
 * the reservation.
 * The controller will receive the request and delegate the execution to the
 * ReservationService and finally return the result.
 */
@RestController
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;

    ReservationController(ReservationService reservationService, UserService userService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }

    @GetMapping("/users/{userId}/reservations")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ReservationGetDTO> getAllReservationsOfUser(@PathVariable(value = "userId") long userId) {

        // fetch all reservations for provided userId
        List<Reservation> reservationsOfUser= reservationService.getAllReservationsByUserId(userId);
        List<ReservationGetDTO> reservationGetDTOS = new ArrayList<>();

        // convert each reservation to the API representation
        for (Reservation reservation : reservationsOfUser) {
            reservationGetDTOS.add(DTOMapper.INSTANCE.convertEntityToReservationGetDTO(reservation));
        }

        // return list of all reservationGetDTO
        return reservationGetDTOS;
    }

    @GetMapping("/reservations/{reservationId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ReservationGetDTO getReservationInformation(@PathVariable(value = "reservationId") long reservationId) {

        // get internal reservation representation by provided path variable reservationId
        Reservation reservationByReservationId = reservationService.getSingleReservationByReservationId(reservationId);

        // convert internal representation to API representation
        ReservationGetDTO reservationGetDTO = DTOMapper.INSTANCE.convertEntityToReservationGetDTO(reservationByReservationId);

        return reservationGetDTO;
    }

    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ReservationGetDTO createReservation(@RequestBody ReservationPostDTO reservationPostDTO) {

        // convert API reservation to internal representation
        Reservation newReservation = DTOMapper.INSTANCE.convertReservationPostDTOtoEntity(reservationPostDTO);

        // create reservation
        Reservation createdReservation = reservationService.createReservation(newReservation);

        // convert internal representation of reservation back to API
        ReservationGetDTO createdReservationDTO = DTOMapper.INSTANCE.convertEntityToReservationGetDTO(createdReservation);

        return createdReservationDTO;
    }

    @DeleteMapping("/reservations/{reservationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSingleReservation(@PathVariable(value = "reservationId") long reservationId) {

        // delete reservation
        int response = reservationService.deleteSingleReservationByReservationId(reservationId);

    }

    @PutMapping("/reservations/{reservationId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ReservationGetDTO updateReservation(@RequestBody ReservationPutDTO reservationPutDTO, @PathVariable(value = "reservationId") long reservationId) {

        // convert update requests to internal representation
        Reservation reservationUpdateRequest = DTOMapper.INSTANCE.convertReservationPutDTOtoEntity(reservationPutDTO);

        // get internal reservation representation by provided path variable reservationId
        Reservation reservationBeforeUpdate = reservationService.getSingleReservationByReservationId(reservationId);

        // update reservation
        Reservation reservationAfterUpdate = reservationService.updateReservation(reservationBeforeUpdate, reservationUpdateRequest);

        // convert internal representation to API representation
        ReservationGetDTO updatedReservationGetDTO = DTOMapper.INSTANCE.convertEntityToReservationGetDTO(reservationAfterUpdate);

        return updatedReservationGetDTO;
    }
}
