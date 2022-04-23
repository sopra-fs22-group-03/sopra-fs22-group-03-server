package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.BookingType;
import ch.uzh.ifi.hase.soprafs22.constant.PaymentStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Billing;
import ch.uzh.ifi.hase.soprafs22.entity.Carpark;
import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.CarparkRepository;
import ch.uzh.ifi.hase.soprafs22.repository.ParkingslipRepository;
import ch.uzh.ifi.hase.soprafs22.repository.BillingRepository;
import ch.uzh.ifi.hase.soprafs22.rss.Feed;
import ch.uzh.ifi.hase.soprafs22.rss.FeedMessage;
import ch.uzh.ifi.hase.soprafs22.rss.RSSFeedParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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

    private final ParkingslipRepository parkingslipRepository;

    private final BillingRepository billingRepository;


    @Autowired
    public CarparkService(@Qualifier("carparkRepository") CarparkRepository carparkRepository,
                          @Qualifier("parkingslipRepository") ParkingslipRepository parkingslipRepository,
                          @Qualifier("billingRepository") BillingRepository billingRepository) {
        this.carparkRepository = carparkRepository;
        this.parkingslipRepository = parkingslipRepository;
        this.billingRepository = billingRepository;

    }

    public List<Carpark> getCarparks() {

        // get current number of empty parking spaces from RSSFeed
        updateRSSFeed();

        return this.carparkRepository.findAll();
    }

    public Carpark getSingleCarparkById(long id) {
        Carpark carparkById = carparkRepository.findById(id);

        // throw error if carpark does not exist
        if (carparkById == null) {
            String baseErrorMessage = "The carpark with id %d was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, id));
        }

        // get current number of empty parking spaces from RSSFeed
        updateRSSFeed();

        return carparkById;
    }


    public Parkingslip performCheckinOfUser(User user, Carpark carpark) {
        long userId = user.getId();

        // throws 403 exception if user is already checked-in in a carpark
        if (parkingslipRepository.existsParkingslipByUserIdAndCheckinDateIsNotNullAndAndCheckoutDateIsNull(userId)){
            String baseErrorMessage = "You are already checked-in in a car park. Please check-out before checking-in again.";
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format(baseErrorMessage));
        }

        long carparkId = carpark.getId();
        Carpark carparkToCheckIn = carparkRepository.findById(carparkId);

        if (carparkToCheckIn.getNumOfEmptySpaces() <= 0){
            String baseErrorMessage = "Car park has no empty spaces. Please an other car park";
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format(baseErrorMessage));
        }

        Parkingslip parkingslipCheckin = new Parkingslip();
        parkingslipCheckin.setUserId(userId);
        parkingslipCheckin.setCarparkId(carparkId);
        parkingslipCheckin.setCheckinDate(LocalDate.now().toString());
        parkingslipCheckin.setCheckinTime(LocalTime.now().toString());
        parkingslipCheckin.setLicensePlate(user.getLicensePlate());

        parkingslipCheckin = parkingslipRepository.save(parkingslipCheckin);
        parkingslipRepository.flush();

        // update number of empty parking spaces from RSSFeed and Parkingslips
        updateRSSFeed();

        return parkingslipCheckin;
    }

//    TODO (throws 404 exception if user is currently not checked-in within carpark)
    public Parkingslip performCheckoutOfUser(User user, Carpark carpark) {
        long userId = user.getId();
        long hourlyTariff = carpark.getHourlyTariff();

        // check if user is already checked-in
        if (!parkingslipRepository.existsParkingslipByUserIdAndCheckinDateIsNotNullAndAndCheckoutDateIsNull(userId)){
            String baseErrorMessage = "You cannot check-out because you are not checked-in in a car park.";
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format(baseErrorMessage));
        }
        // check that user can only check out in the respective car park!

        Parkingslip parkingslipCheckout = parkingslipRepository.findParkingslipByUserIdAndCheckoutDateIsNull(userId);
        parkingslipCheckout.setCheckoutDate(LocalDate.now().toString());
        parkingslipCheckout.setCheckoutTime(LocalTime.now().toString());

        parkingslipCheckout.setParkingFee(hourlyTariff);

        // convert check-in und check-out dates to date format to calculate the time spent in the carpark
        LocalDate dateCheckin = LocalDate.parse(parkingslipCheckout.getCheckinDate());
        LocalTime timeCheckin= LocalTime.parse(parkingslipCheckout.getCheckinTime());
        LocalDateTime dateTimeCheckin = LocalDateTime.of(dateCheckin, timeCheckin);
        LocalDateTime dateTimeCheckinFrom = LocalDateTime.from(dateTimeCheckin);

        LocalDateTime dateTimeCheckoutTo = LocalDateTime.now();


        // calculate parking fee
        long parkingFee = (long) ChronoUnit.MINUTES.between(dateTimeCheckinFrom, dateTimeCheckoutTo) * hourlyTariff /60;
        parkingslipCheckout.setParkingFee(parkingFee);

        parkingslipCheckout = parkingslipRepository.save(parkingslipCheckout);
        parkingslipRepository.flush();

        // convert parking slip into bill after checkout
        createBillingFromParkingslip(parkingslipCheckout);

        // update number of empty parking spaces from RSSFeed and Parkingslips
        updateRSSFeed();

        return parkingslipCheckout;
    }

    private void updateRSSFeed() {
        RSSFeedParser parser = new RSSFeedParser("https://www.pls-zh.ch/plsFeed/rss/");
        Feed feed = parser.readFeed();

        // updates capacity in all carparks from RSS-feed
        for (FeedMessage item : feed.getMessages()) {
            Carpark carparkByLink = carparkRepository.findCarparkByLink(item.getLink());
            long carparkId = carparkByLink.getId();

            // the description contains the status (open/closed) and the number of empty parking lots
            // here, we will, thus, extract both substrings (status, number of emtpy parking lots)
            String description = item.getDescription();
            String status = description.substring(0, description.indexOf("/") - 1);
            String emptySpacesString = description.substring(description.indexOf("/") + 1, description.length()).trim();

            // get the number of checked-in cars for this carpark
            int numParkingslips = parkingslipRepository.countByCarparkId(carparkId);

            int emptySpaces;
            try {
                emptySpaces = Integer.parseInt(emptySpacesString);
            }
            catch (Exception e) { // the number of empty spaces is set to zero if it cannot be parsed
                emptySpaces = 0;
            }

            if (status.equals("open")) {
                carparkByLink.setOpen(true);
                carparkByLink.setNumOfEmptySpaces(emptySpaces - numParkingslips);
            }
            else {
                carparkByLink.setOpen(false);
                carparkByLink.setNumOfEmptySpaces(0);
            }
        }
    }

    // convert parking slip into bill after checkout
    private void createBillingFromParkingslip(Parkingslip parkingslip) {
        Billing billing = new Billing();
        billing.setUserId(parkingslip.getUserId());
        billing.setBookingType(BookingType.PARKINGSLIP);
        billing.setBookingId(parkingslip.getId());
        billing.setPaymentStatus(PaymentStatus.OUTSTANDING);

        billing = billingRepository.save(billing);
        billingRepository.flush();

    }

}