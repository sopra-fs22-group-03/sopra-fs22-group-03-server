package ch.uzh.ifi.hase.soprafs22.entity;


import javax.persistence.*;
import java.io.Serializable;

/**
 * Internal Carpark Representation
 * This class composes the internal representation of a car park and defines how
 * a car park is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "CARPARK")
public class Carpark implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long maxCapacity;

    @Column(nullable = false)
    private long numOfEmptySpaces;

    @Column(nullable = false)
    private String street;

    @Column(nullable = true)
    private String streetNo;

    @Column(nullable = false)
    private long zipCode;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private boolean open;

    @Column(nullable = false)
    private String weekdayOpenFrom;

    @Column(nullable = false)
    private String weekdayOpenTo;

    @Column(nullable = false)
    private String weekendOpenFrom;

    @Column(nullable = false)
    private String weekendOpenTo;

    @Column(nullable = false)
    private long hourlyTariff;

    @Column(nullable = true)
    private String remarks;

    @Column(nullable = false)
    private String link;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(long maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public long getNumOfEmptySpaces() {
        return numOfEmptySpaces;
    }

    public void setNumOfEmptySpaces(long numOfEmptySpaces) {
        this.numOfEmptySpaces = numOfEmptySpaces;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNo() {
        return streetNo;
    }

    public void setStreetNo(String streetNo) {
        this.streetNo = streetNo;
    }

    public long getZipCode() {
        return zipCode;
    }

    public void setZipCode(long zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getWeekdayOpenFrom() {
        return weekdayOpenFrom;
    }

    public void setWeekdayOpenFrom(String weekdayOpenFrom) {
        this.weekdayOpenFrom = weekdayOpenFrom;
    }

    public String getWeekdayOpenTo() {
        return weekdayOpenTo;
    }

    public void setWeekdayOpenTo(String weekdayOpenTo) {
        this.weekdayOpenTo = weekdayOpenTo;
    }

    public String getWeekendOpenFrom() {
        return weekendOpenFrom;
    }

    public void setWeekendOpenFrom(String weekendOpenFrom) {
        this.weekendOpenFrom = weekendOpenFrom;
    }

    public String getWeekendOpenTo() {
        return weekendOpenTo;
    }

    public void setWeekendOpenTo(String weekendOpenTo) {
        this.weekendOpenTo = weekendOpenTo;
    }

    public long getHourlyTariff() {
        return hourlyTariff;
    }

    public void setHourlyTariff(long hourlyTariff) {
        this.hourlyTariff = hourlyTariff;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


}
