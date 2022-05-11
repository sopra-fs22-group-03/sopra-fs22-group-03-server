package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class ParkingslipServiceTest {

    @Mock
    ParkingslipRepository parkingslipRepository;
    Parkingslip parkingslip;

    @InjectMocks
    ParkingslipService parkingslipService;


    @BeforeEach
    public void setup() {
        // given
        parkingslip = new Parkingslip();
        parkingslip.setId(1L);
        parkingslip.setUserId(1L);
        parkingslip.setCarparkId(100001L);
        parkingslip.setCheckinDate("2032-05-08");
        parkingslip.setCheckinTime("08:00");
        parkingslip.setCheckoutDate("2032-05-08");
        parkingslip.setCheckoutTime("08:00");
        parkingslip.setLicensePlate("ZH11");
        parkingslip.setParkingFee(48);
    }

    @Test
    void testGetSingleParkingslipByParkingslipId() {
        Mockito.when(parkingslipRepository.findById(Mockito.anyLong())).thenReturn(parkingslip);

        Parkingslip receivedParkingslip = parkingslipService.getSingleParkingslipByParkingslipId(1);

        assertEquals(parkingslip.getParkingFee(), receivedParkingslip.getParkingFee());
    }

    @Test
    public void testGetSingleParkingslipByParkingslipId_throws_404_Excepption() {
        long invalidParkingslipId = 0;

        //try to get a parkingslip that does not exist; status error with code 404 should be thrown
        try {
            parkingslipService.getSingleParkingslipByParkingslipId(invalidParkingslipId);
            Assertions.fail("BAD REQUEST exception should have been thrown!");
        }
        catch (ResponseStatusException ex) {
            assertEquals(404, ex.getRawStatusCode());
        }
    }

}