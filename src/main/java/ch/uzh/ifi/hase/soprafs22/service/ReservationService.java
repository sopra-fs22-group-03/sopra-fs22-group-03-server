package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.BookingType;
import ch.uzh.ifi.hase.soprafs22.constant.PaymentStatus;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.BillingRepository;
import ch.uzh.ifi.hase.soprafs22.repository.CarparkRepository;
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
import java.util.ArrayList;
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
    private final BillingRepository billingRepository;


    @Autowired
    public ReservationService(@Qualifier("reservationRepository") ReservationRepository reservationRepository, UserService userService,
                              CarparkService carparkService, BillingRepository billingRepository) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
        this.carparkService = carparkService;
        this.billingRepository = billingRepository;
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

        // check if reservation end is after reservation start
        checkIfReservationDatesAreValid(newReservation);

        // check if minimum duration of reservations is fulfilled
        checkIfReservationDurationAtLeastXMinutes(newReservation, 120);

        // check if carpark has empty spaces during the entire reservation period
        checkIfReservationIsPossible(newReservation);

        // add licensePlate to newReservation
        User userOfReservation = userService.getSingleUserById(newReservation.getUserId());
        newReservation.setLicensePlate(userOfReservation.getLicensePlate());

        // calculate total parking fee of reservation and write it to newReservation
        float parkingFee = calculateParkingFeeOfReservation(newReservation);
        newReservation.setParkingFee(parkingFee);

        // save reservation
        newReservation = reservationRepository.save(newReservation);
        reservationRepository.flush();

        // create billing invoice for reservation
        createBillingFromReservation(newReservation);

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
            try {
                // delete billing associated with the reservation to be deleted
                int response = deleteBillingFromReservation(reservationToBeDeleted);

                // delete reservation
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

        // check if reservationToBeUpdated is more than 2 hours in advance; if not, then throw FORBIDDEN exception
        boolean is2HoursInAdvance = isReservationCheckInXMinutesInAdvance(reservationToBeUpdated, 120);

        if (!is2HoursInAdvance) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Reservation starts in less than 2 hours. Hence, changing the reservation is not possible anymore.");
        }


        // If reservationToBeUpdated is more than 2 hours in advance, then update fields which are provided by user (=not null)
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

        // check if reservation end is after reservation start
        checkIfReservationDatesAreValid(reservationToBeUpdated);

        // check if minimum duration of updated reservations is fulfilled
        checkIfReservationDurationAtLeastXMinutes(reservationToBeUpdated, 120);

        // check if new reservation is possible due to max Capacity already reached by other reservations in specified timeslot
        checkIfReservationIsPossible(reservationToBeUpdated);

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
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

    private void checkIfReservationDurationAtLeastXMinutes(Reservation reservation, long minutes) {

        // convert datetime string in correct format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String checkinDateTime = reservation.getCheckinDate() + " " + reservation.getCheckinTime();
        String checkoutDateTime = reservation.getCheckoutDate() + " " + reservation.getCheckoutTime();

        // calculate reservation duration in minutes
        LocalDateTime reservationStart = LocalDateTime.parse(checkinDateTime, formatter);
        LocalDateTime reservationEnd = LocalDateTime.parse(checkoutDateTime, formatter);
        float parkingDurationInMinutes = MINUTES.between(reservationStart, reservationEnd);

        if (parkingDurationInMinutes < minutes) {
            String baseErrorMessage = "Reservation not possible. Reason: Reservation duration is less than minimum duration of %d minutes.";
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format(baseErrorMessage, minutes));
        }

    }

    private void checkIfReservationDatesAreValid(Reservation reservation) {

        // convert datetime string in correct format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String checkinDateTime = reservation.getCheckinDate() + " " + reservation.getCheckinTime();
        String checkoutDateTime = reservation.getCheckoutDate() + " " + reservation.getCheckoutTime();

        // calculate reservation duration in minutes
        LocalDateTime reservationStart = LocalDateTime.parse(checkinDateTime, formatter);
        LocalDateTime reservationEnd = LocalDateTime.parse(checkoutDateTime, formatter);

        if (reservationEnd.isBefore(reservationStart)) {
            String baseErrorMessage = "Reservation not possible. Reason: Reservation end is before reservation start.";
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format(baseErrorMessage));
        }

    }

    private boolean isReservationCheckInXMinutesInAdvance(Reservation reservation, long minutes) {

        // convert datetime string in correct format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String checkinDateTime = reservation.getCheckinDate() + " " + reservation.getCheckinTime();

        // convert into LocalDateTime
        LocalDateTime reservationStart = LocalDateTime.parse(checkinDateTime, formatter);

        // get current time in Zurich
        ZoneId zurichZoneId = ZoneId.of("Europe/Zurich");
        ZonedDateTime now = ZonedDateTime.now(zurichZoneId);

        // calculate difference between now and Check-In time in minutes
        long differenceInMinutes = MINUTES.between(reservationStart, now)*(-1);

        // check if more than specified X minutes in advance
        if (differenceInMinutes >= minutes) {
            return true;
        }
        else {
            return false;
        }
    }

    // create billing invoice for reservation
    private void createBillingFromReservation(Reservation reservation) {
        Billing billing = new Billing();
        billing.setUserId(reservation.getUserId());
        billing.setBookingType(BookingType.RESERVATION);
        billing.setBookingId(reservation.getId());
        billing.setPaymentStatus(PaymentStatus.OUTSTANDING);

        billing = billingRepository.save(billing);
        billingRepository.flush();

    }

    // delete billing associated with reservation
    private int deleteBillingFromReservation(Reservation reservation) {
        long reservationId = reservation.getId();
        Billing billingOfToBeDeletedReservation = billingRepository.findByBookingTypeAndBookingId(BookingType.RESERVATION, reservationId);
        long billingIdOfToBeDeletedReservation = billingOfToBeDeletedReservation.getId();

        try {
            billingRepository.deleteById(billingIdOfToBeDeletedReservation);
            billingRepository.flush();
            return 0;
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.valueOf(500), "Deletion of billing associated with reservation failed.");
        }
    }

    private void checkIfReservationIsPossible(Reservation newReservation) {
        // retrieve carparkId and maxCapacity of carpark
        long carparkId = newReservation.getCarparkId();
        Carpark carpark = carparkService.getSingleCarparkById(carparkId);
        long maxCapacity = carpark.getMaxCapacity();

        // find all reservations for specified carpark
        List<Reservation> allReservationsInCarpark = reservationRepository.findAllByCarparkId(carparkId);

        // define DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // concatenate Date and Times
        String newReservationCheckinDateTime = newReservation.getCheckinDate() + " " + newReservation.getCheckinTime();
        String newReservationCheckoutDateTime = newReservation.getCheckoutDate() + " " + newReservation.getCheckoutTime();

        // convert into DateTime Format
        LocalDateTime newReservationStart = LocalDateTime.parse(newReservationCheckinDateTime, formatter);
        LocalDateTime newReservationEnd = LocalDateTime.parse(newReservationCheckoutDateTime, formatter);

        // count all reservations whose checkinDateTime is before and checkoutDateTime is after the specified zonedDateTime
        List<Reservation> refinedReservationList = new ArrayList<>();

        for (Reservation reservation : allReservationsInCarpark) {
            // concatenate Date and Times
            String checkinDateTime = reservation.getCheckinDate() + " " + reservation.getCheckinTime();
            String checkoutDateTime = reservation.getCheckoutDate() + " " + reservation.getCheckoutTime();

            // convert into DateTime Format
            LocalDateTime reservationStart = LocalDateTime.parse(checkinDateTime, formatter);
            LocalDateTime reservationEnd = LocalDateTime.parse(checkoutDateTime, formatter);

            // check if checkinDateTime is before and checkoutDateTime is after the specified zonedDateTime
            boolean newReservationStartIsBefore = newReservationStart.isBefore(reservationStart);
            boolean newReservationEndIsBefore = newReservationEnd.isBefore(reservationStart);
            boolean newReservationStartIsAfter = newReservationStart.isAfter(reservationEnd);
            boolean newReservationEndIsAfter = newReservationEnd.isAfter(reservationEnd);

            // increment counter
            if ((newReservationStartIsBefore && newReservationEndIsBefore) || (newReservationStartIsAfter && newReservationEndIsAfter)) {
                continue;
            }
            refinedReservationList.add(reservation);
        }

        // go through refined reservation list and check each "1-minute-slice" of the newReservation period if the carpark is free
        if (!refinedReservationList.isEmpty()) {
            LocalDateTime iterator = newReservationStart;
            while (iterator.isBefore(newReservationEnd)) {

                int count = 0;
                for (Reservation reservation : refinedReservationList) {
                    // concatenate Date and Times
                    String checkinDateTime = reservation.getCheckinDate() + " " + reservation.getCheckinTime();
                    String checkoutDateTime = reservation.getCheckoutDate() + " " + reservation.getCheckoutTime();

                    // convert into DateTime Format
                    LocalDateTime reservationStart = LocalDateTime.parse(checkinDateTime, formatter);
                    LocalDateTime reservationEnd = LocalDateTime.parse(checkoutDateTime, formatter);

                    // check if reservation lies in current "1-minute-slice" of newReservation
                    boolean isBefore = reservationStart.isBefore(iterator);
                    boolean isAfter = reservationEnd.isAfter(iterator);
                    // increment counter if applicable
                    if (isBefore && isAfter) {
                        count++;
                    }
                }
                long numOfEmptySpaces = maxCapacity - count;
                if (numOfEmptySpaces <= 0) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Reservation not possible because carpark is fully booked at: " + iterator);
                }
                iterator = iterator.plusMinutes(1);
            }
        }
        //return true;
    }

}