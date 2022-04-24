package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Billing;
import ch.uzh.ifi.hase.soprafs22.entity.Carpark;
import ch.uzh.ifi.hase.soprafs22.entity.Reservation;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.BillingRepository;
import ch.uzh.ifi.hase.soprafs22.repository.ReservationRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;
import static java.time.temporal.ChronoUnit.*;

/**
 * Reservation Service
 * This class is the "worker" and responsible for all functionality related to
 * the reservation
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class ReservationService {

    private final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final CarparkService carparkService;

    @Autowired
    public ReservationService(@Qualifier("reservationRepository") ReservationRepository reservationRepository, UserService userService,
                              CarparkService carparkService) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
        this.carparkService = carparkService;
    }

    public List<Reservation> getAllReservationsByUserId(long userId) {
        return this.reservationRepository.findAllByUserId(userId);
    }

    public Reservation getSingleReservationByReservationId(long reservationId) {
        Reservation reservationByReservationId = reservationRepository.findById(reservationId);

        // throw error if carpark does not exist
        if (reservationByReservationId == null) {
            String baseErrorMessage = "The reservation with id %d was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, reservationId));
        }
        return reservationByReservationId;
    }

//    TODO (Implement checks if reservation requested by user is valid (minimum duration) and possible at specified carpark, time, date, ..)
    public Reservation createReservation(Reservation newReservation) {
        // DO CHECKS IF RESERVATION IS VALID / EMPTY SPACES IN PARKING ETC.
        //...

        // add licensePlate to newReservation
        User userOfReservation = userService.getSingleUserById(newReservation.getUserId());
        newReservation.setLicensePlate(userOfReservation.getLicensePlate());

        // calculate total parking fee of reservation and write it to newReservation
        float parkingFee = calculateParkingFeeOfReservation(newReservation);
        newReservation.setParkingFee(parkingFee);

        // save reservation
        newReservation = reservationRepository.save(newReservation);
        reservationRepository.flush();

        return newReservation;
    }

    public int deleteAllReservationsOfUserId (long userId) {

        // delete all future reservations of user
        try {
            reservationRepository.deleteAllByUserId(userId);
            reservationRepository.flush();
            return 0;
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.valueOf(500), "Deletion of future reservations failed.");
        }
    }

    public int deleteSingleReservationByReservationId (long reservationId) {

        Reservation reservationToBeDeleted = getSingleReservationByReservationId(reservationId);

        // check if cancellation of reservation is more than 2 hours in advance
        boolean is2HoursInAdvance = isReservationCheckInXMinutesInAdvance(reservationToBeDeleted, 120);

        if (is2HoursInAdvance) {
            // delete reservation
            try {
                reservationRepository.deleteById(reservationId);
                reservationRepository.flush();
                return 0;
            }
            catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.valueOf(500), "Deletion of reservation failed.");
            }
        }
        // throw exception if reservation starts in less than 2 hours
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Reservation starts in less than 2 hours. Hence, deletion is not possible anymore.");
        }
    }

    //    TODO (Implement checks if reservation requested by user is valid (minimum duration) and possible at specified carpark, time, date, ..)
    public Reservation updateReservation(Reservation reservationToBeUpdated, Reservation reservationUpdateRequest) {


        // update fields which are provided by user (=not null)
        if (reservationUpdateRequest.getCheckinDate() != null) {
            reservationToBeUpdated.setCheckinDate(reservationUpdateRequest.getCheckinDate());
        }

        if (reservationUpdateRequest.getCheckinTime() != null) {
            reservationToBeUpdated.setCheckinTime(reservationUpdateRequest.getCheckinTime());
        }

        if (reservationUpdateRequest.getCheckoutDate() != null) {
            reservationToBeUpdated.setCheckoutDate(reservationUpdateRequest.getCheckoutDate());
        }

        if (reservationUpdateRequest.getCheckoutTime() != null) {
            reservationToBeUpdated.setCheckoutTime(reservationUpdateRequest.getCheckoutTime());
        }

        // calculate new total parking fee of reservation
        float parkingFee = calculateParkingFeeOfReservation(reservationToBeUpdated);
        reservationToBeUpdated.setParkingFee(parkingFee);

        // save changes
        reservationToBeUpdated = reservationRepository.save(reservationToBeUpdated);
        reservationRepository.flush();

        log.debug("Updated Information for Reservation: {}", reservationToBeUpdated);
        return reservationToBeUpdated;
    }

    /**
     *
     * HELPER FUNCTIONS
     *
     */

    private float calculateParkingFeeOfReservation(Reservation reservation) {

        // retrieve carpark of reservation
        Carpark carparkOfReservation = carparkService.getSingleCarparkById(reservation.getCarparkId());

        // convert datetime string in correct format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String checkinDateTime = reservation.getCheckinDate() + " " + reservation.getCheckinTime();
        String checkoutDateTime = reservation.getCheckoutDate() + " " + reservation.getCheckoutTime();

        // calculate reservation duration in minutes
        LocalDateTime reservationStart = LocalDateTime.parse(checkinDateTime, formatter);
        LocalDateTime reservationEnd = LocalDateTime.parse(checkoutDateTime, formatter);
        float parkingDurationInMinutes = MINUTES.between(reservationStart, reservationEnd);

        // calculate total parkingFee
        float hourlyParkingTariff = carparkOfReservation.getHourlyTariff();
        float parkingFee = parkingDurationInMinutes*(hourlyParkingTariff/60);

        return parkingFee;
    }

    private boolean isReservationCheckInXMinutesInAdvance(Reservation reservation, long minutes) {

        // convert datetime string in correct format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String checkinDateTime = reservation.getCheckinDate() + " " + reservation.getCheckinTime();

        // convert into LocalDateTime
        LocalDateTime reservationStart = LocalDateTime.parse(checkinDateTime, formatter);

        // get current time in Zurich
        ZoneId zurichZoneId = ZoneId.of("Europe/Zurich");
        ZonedDateTime now = ZonedDateTime.now(zurichZoneId);

        // calculate difference between now and Check-In time in minutes
        long differenceInMinutes = MINUTES.between(reservationStart, now)*(-1);
        System.out.println(reservationStart);
        System.out.println(now);
        System.out.println(differenceInMinutes);

        // check if more than specified X minutes in advance
        if (differenceInMinutes >= minutes) {
            return true;
        }
        else {
            return false;
        }
    }

}