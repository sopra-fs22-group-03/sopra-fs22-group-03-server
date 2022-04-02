package ch.uzh.ifi.hase.soprafs22.entity;


import ch.uzh.ifi.hase.soprafs22.constant.BookingType;
import ch.uzh.ifi.hase.soprafs22.constant.PaymentStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.*;

/**
 * Internal Billing Representation
 * This class composes the internal representation of the billing and defines how
 * a invoice is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "BILLING")
public class Billing implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private BookingType bookingType;

    @Column(nullable = false)
    private long bookingId;

    @Column(nullable = true)
    private long userIdOfSplitPartner;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }

    public long getUserIdOfSplitPartner() {
        return userIdOfSplitPartner;
    }

    public void setUserIdOfSplitPartner(long userIdOfSplitPartner) {
        this.userIdOfSplitPartner = userIdOfSplitPartner;
    }


}
