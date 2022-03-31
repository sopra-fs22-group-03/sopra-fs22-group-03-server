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
    }

    public List<Carpark> getCarparks() {
        return this.carparkRepository.findAll();
    }

    public Carpark getSingleCarparkById(long id) {
        Carpark carparkById = carparkRepository.findByCarparkId(id);

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