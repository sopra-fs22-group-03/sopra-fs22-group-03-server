package ch.uzh.ifi.hase.soprafs22.entity;


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
    private long billingId;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private String checkinDate;

    @Column(nullable = false)
    private String checkinTime;

    @Column(nullable = false)
    private String checkoutDate;

    @Column(nullable = false)
    private String checkoutTime;

    @Column(nullable = false)
    private long carparkId;

    @Column(nullable = true)
    private String licensePlate;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private long duration;

    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(nullable = true)
    private long userIdOfSplitPartner;

    public long getBillingId() {
        return billingId;
    }

    public void setBillingId(long billingId) {
        this.billingId = billingId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(String checkinDate) {
        this.checkinDate = checkinDate;
    }

    public String getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(String checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public String getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(String checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public long getCarparkId() {
        return carparkId;
    }

    public void setCarparkId(long carparkId) {
        this.carparkId = carparkId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public long getUserIdOfSplitPartner() {
        return userIdOfSplitPartner;
    }

    public void setUserIdOfSplitPartner(long userIdOfSplitPartner) {
        this.userIdOfSplitPartner = userIdOfSplitPartner;
    }
}
