package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.*;

/**
 * Internal Reservation Representation
 * This class composes the internal representation of the reservation and defines how
 * a reservation is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "RESERVATION")
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private long carparkId;

    @Column(nullable = false)
    private String checkinDate;

    @Column(nullable = false)
    private String checkinTime;

    @Column(nullable = false)
    private String checkoutDate;

    @Column(nullable = false)
    private String checkoutTime;

    @Column(nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private float parkingFee;

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

    public long getCarparkId() {
        return carparkId;
    }

    public void setCarparkId(long carparkId) {
        this.carparkId = carparkId;
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

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public float getParkingFee() {
        return parkingFee;
    }

    public void setParkingFee(float parkingFee) {
        this.parkingFee = parkingFee;
    }

}
