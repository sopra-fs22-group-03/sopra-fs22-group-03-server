package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("reservationRepository")
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Reservation findById(long reservationId);

    List<Reservation> findAllByUserId(long userId);

    List<Reservation> findAllByCarparkId(long carparkId);

    void deleteAllByUserId(long userId);
}
