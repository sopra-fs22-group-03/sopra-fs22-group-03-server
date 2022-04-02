package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Carpark;
import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.CarparkRepository;
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
 * Carpark Service
 * This class is the "worker" and responsible for all functionality related to
 * the carpark
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class CarparkService {

    private final Logger log = LoggerFactory.getLogger(CarparkService.class);

    private final CarparkRepository carparkRepository;

    @Autowired
    public CarparkService(@Qualifier("carparkRepository") CarparkRepository carparkRepository) {
        this.carparkRepository = carparkRepository;
        /**
         * Generate some records in Carpark database - to be deleted after persistent database exists
         * DELETE FROM HERE
         */
        Carpark carpark1 = new Carpark();
        Carpark carpark2 = new Carpark();

        carpark1.setName("carpark1");
        carpark1.setMaxCapacity(100);
        carpark1.setNumOfEmptySpaces(5);
        carpark1.setStreet("Mustertrasse");
        carpark1.setStreetNo(12);
        carpark1.setZipCode(8000);
        carpark1.setCity("Zürich");
        carpark1.setLongitude(8.545094);
        carpark1.setLatitude(47.373878);
        carpark1.setOpen(true);
        carpark1.setWeekdayOpenFrom("08:00");
        carpark1.setWeekdayOpenTo("22:00");
        carpark1.setWeekendOpenFrom("08:00");
        carpark1.setWeekendOpenTo("20:00");
        carpark1.setHourlyTariff(9);

        carpark2.setName("carpark2");
        carpark2.setMaxCapacity(30);
        carpark2.setNumOfEmptySpaces(5);
        carpark2.setStreet("Musterweg");
        carpark2.setStreetNo(15);
        carpark2.setZipCode(8000);
        carpark2.setCity("Zürich");
        carpark2.setLongitude(8.6);
        carpark2.setLatitude(47.1);
        carpark2.setOpen(true);
        carpark2.setWeekdayOpenFrom("08:00");
        carpark2.setWeekdayOpenTo("22:00");
        carpark2.setWeekendOpenFrom("08:00");
        carpark2.setWeekendOpenTo("20:00");
        carpark2.setHourlyTariff(9);

        carpark1 = carparkRepository.save(carpark1);
        carpark2 = carparkRepository.save(carpark2);
        carparkRepository.flush();
        /**
         * DELETE UNTIL HERE
         */
    }

    public List<Carpark> getCarparks() {
        return this.carparkRepository.findAll();
    }

    public Carpark getSingleCarparkById(long id) {
        Carpark carparkById = carparkRepository.findById(id);

        // throw error if carpark does not exist
        if (carparkById == null) {
            String baseErrorMessage = "The carpark with id %d was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, id));
        }
        return carparkById;
    }

//    TODO (throws 403 exception if user is already checked-in within carpark)
    public Parkingslip performCheckinOfUser(User user, Carpark carpark) {
        Parkingslip parkingslipCheckin = new Parkingslip();

        return parkingslipCheckin;
    }

//    TODO (throws 404 exception if user is currently not checked-in within carpark)
    public Parkingslip performCheckoutOfUser(User user, Carpark carpark) {
        Parkingslip parkingslipCheckout = new Parkingslip();

        return parkingslipCheckout;
    }

}