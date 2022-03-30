package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Carpark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("carparkRepository")
public interface CarparkRepository extends JpaRepository<Carpark, Long> {
    Carpark findByCarparkId(long id);
}
