package ch.uzh.ifi.hase.soprafs22.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.time.*;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private boolean isManager;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private long streetNo;

    @Column(nullable = false)
    private Long zipCode;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String email;

    @Column(nullable = true)
    private String licensePlate;

    @Column(nullable = false)
    private long creditCardNumber;

    @Column(nullable = false, unique = true)
    private String token;


    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public long getStreetNo() {
        return streetNo;
    }

    public void setStreetNo(long streetNo) {
        this.streetNo = streetNo;
    }

    public Long getZipCode() {
        return zipCode;
    }

    public void setZipCode(Long zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
}
