package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("billingRepository")
public interface BillingRepository extends JpaRepository<Billing, Long> {
    Billing findById(long billingId);

    List<Billing> findAllByUserId(long userId);

}
