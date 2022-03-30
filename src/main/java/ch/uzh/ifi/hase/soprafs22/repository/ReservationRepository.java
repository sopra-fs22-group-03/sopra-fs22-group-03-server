package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("reservationRepository")
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Reservation findByReservationId(long id);
}
