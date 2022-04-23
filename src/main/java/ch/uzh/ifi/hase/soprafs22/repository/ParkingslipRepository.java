package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("parkingslipRepository")
public interface ParkingslipRepository extends JpaRepository<Parkingslip, Long> {
    Parkingslip findById(long id);
    boolean existsParkingslipByUserIdAndCheckinDateIsNotNullAndAndCheckoutDateIsNull(long userId);
    Parkingslip findParkingslipByUserId(long userId);
    Parkingslip findParkingslipByUserIdAndCheckoutDateIsNull(long userId);
    int countByCarparkId(long carParkId);
    //void removeById(long parkingslipId);

}
