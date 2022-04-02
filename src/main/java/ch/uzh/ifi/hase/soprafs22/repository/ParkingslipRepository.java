package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Parkingslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("parkingslipRepository")
public interface ParkingslipRepository extends JpaRepository<Parkingslip, Long> {
    Parkingslip findById(long id);
}
