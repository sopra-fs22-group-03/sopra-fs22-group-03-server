package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.repository.ParkingslipRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Parkingslip Service
 * This class is the "worker" and responsible for all functionality related to
 * past parkingslips
 */
@Service
@Transactional
public class ParkingslipService {

    private final Logger log = LoggerFactory.getLogger(ParkingslipService.class);

    private final ParkingslipRepository parkingslipRepository;

    @Autowired
    public ParkingslipService(ParkingslipRepository parkingslipRepository) {
        this.parkingslipRepository = parkingslipRepository;
    }

    public Parkingslip getSingleParkingslipByParkingslipId(long parkingslipId) {
        Parkingslip parkingslipByParkingslipId = parkingslipRepository.findById(parkingslipId);

        // throw error if carpark does not exist
        if (parkingslipByParkingslipId == null) {
            String baseErrorMessage = "The parkingslip with id %d was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, parkingslipId));
        }
        return parkingslipByParkingslipId;
    }
}