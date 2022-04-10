package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Billing;
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

import java.util.List;

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

    @Autowired
    public ReservationService(@Qualifier("reservationRepository") ReservationRepository reservationRepository, UserService userService) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
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

//    TODO (Implement checks if reservation requested by user is possible/valid at specified carpark, time, date, ..)
    public Reservation createReservation(Reservation newReservation) {
        // DO CHECKS IF RESERVATION IS VALID / EMPTY SPACES IN PARKING ETC.
        //...

        // add licensePlate to newReservation
        User userOfReservation = userService.getSingleUserById(newReservation.getUserId());
        newReservation.setLicensePlate(userOfReservation.getLicensePlate());

        // calculate parkingFee
        // ...

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

}