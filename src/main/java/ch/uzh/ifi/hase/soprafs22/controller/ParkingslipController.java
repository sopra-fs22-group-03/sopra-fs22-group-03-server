package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.rest.dto.ParkingslipGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.ParkingslipService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Parkingslip Controller
 * This class is responsible for handling all REST request that are related to
 * past parkingslips.
 * The controller will receive the request and delegate the execution to the
 * ParkingslipService and finally return the result.
 */
@RestController
public class ParkingslipController {

    private final ParkingslipService parkingslipService;
    private final UserService userService;

    ParkingslipController(ParkingslipService parkingslipService, UserService userService) {
        this.parkingslipService = parkingslipService;
        this.userService = userService;
    }

    @GetMapping("/parkingslips/{parkingslipId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ParkingslipGetDTO getReservationInformation(@PathVariable(value = "parkingslipId") long parkingslipId) {

        // get internal reservation representation by provided path variable reservationId
        Parkingslip parkingslipByParkingslipId = parkingslipService.getSingleParkingslipByParkingslipId(parkingslipId);

        // convert internal representation to API representation
        ParkingslipGetDTO parkingslipGetDTO = DTOMapper.INSTANCE.convertEntityToParkingslipGetDTO(parkingslipByParkingslipId);

        return parkingslipGetDTO;
    }
}
