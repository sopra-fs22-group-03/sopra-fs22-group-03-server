package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository("billingRepository")
public interface BillingRepository extends JpaRepository<Billing, Long> {
    Billing findByBillingId(long id);

}
