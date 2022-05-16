package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Carpark;
import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.CarparkService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Carpark Controller
 * This class is responsible for handling all REST request that are related to
 * the carpark.
 * The controller will receive the request and delegate the execution to the
 * CarparkService and finally return the result.
 */
@RestController
public class CarparkController {

    private final CarparkService carparkService;
    private final UserService userService;

    CarparkController(CarparkService carparkService, UserService userService) {
        this.carparkService = carparkService;
        this.userService = userService;
    }

    @GetMapping("/carparks")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<CarparkGetDTO> getAllCarparks() {
        // fetch all carparks in the internal representation
        List<Carpark> carparks = carparkService.getCarparks();
        List<CarparkGetDTO> carparkGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (Carpark carpark : carparks) {
            carparkGetDTOs.add(DTOMapper.INSTANCE.convertEntityToCarparkGetDTO(carpark));
        }
        return carparkGetDTOs;
    }

    @GetMapping("/carparks/{carparkId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CarparkGetDTO getCarparkInformation(@PathVariable(value = "carparkId") long carparkId,
                                               @PathVariable(value = "userId") long userId) {

        // get internal carpark representation by provided path variable carparkId
        Carpark carparkById = carparkService.getSingleCarparkById(carparkId);

        // convert internal representation to API representation
        CarparkGetDTO carparkGetDTO = DTOMapper.INSTANCE.convertEntityToCarparkGetDTO(carparkById);

        // get interal user representation from userIdDTO object
        User userById = userService.getSingleUserById(userId);

        // add isCheckedIn status to the carpark DTO object for the user in request body
        boolean isCheckedIn = carparkService.isUserCheckedInCarpark(userById, carparkById);
        carparkGetDTO.setIsCheckedIn(isCheckedIn);

        return carparkGetDTO;
    }

    @PostMapping("/carparks/{carparkId}/checkin")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ParkingslipGetDTO checkinUserInCarpark(@RequestBody UserIdDTO userIdDTO, @PathVariable(value = "carparkId") long carparkId) {

        // get internal carpark representation by provided path variable carparkId
        Carpark carparkById = carparkService.getSingleCarparkById(carparkId);

        // get interal user representation from userIdDTO object
        long userId = userIdDTO.getUserId();
        User userById = userService.getSingleUserById(userId);

        // perform checkin
        Parkingslip parkingslipCheckin = carparkService.performCheckinOfUser(userById, carparkById);

        // convert internal parkingslip to API
        ParkingslipGetDTO parkingslipCheckinGetDTO = DTOMapper.INSTANCE.convertEntityToParkingslipGetDTO(parkingslipCheckin);

        return parkingslipCheckinGetDTO;
    }

    @PostMapping("/carparks/{carparkId}/checkout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ParkingslipGetDTO checkoutUserOfCarpark(@RequestBody UserIdDTO userIdDTO, @PathVariable(value = "carparkId") long carparkId) {

        // get internal carpark representation by provided path variable carparkId
        Carpark carparkById = carparkService.getSingleCarparkById(carparkId);

        // get interal user representation from userIdDTO object
        long userId = userIdDTO.getUserId();
        User userById = userService.getSingleUserById(userId);

        // perform checkout
        Parkingslip parkingslipCheckout = carparkService.performCheckoutOfUser(userById, carparkById);

        // convert internal parkingslip to API
        ParkingslipGetDTO parkingslipCheckoutGetDTO = DTOMapper.INSTANCE.convertEntityToParkingslipGetDTO(parkingslipCheckout);

        return parkingslipCheckoutGetDTO;
    }
}
