package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class CarparkGetDTO {

    private long carparkId;
    private String name;
    private long maxCapacity;
    private long numOfEmptySpaces;
    private String street;
    private long streetNo;
    private long zipCode;
    private String city;
    private long longitude;
    private long latitude;
    private String weekdayOpenFrom;
    private String weekdayOpenTo;
    private String weekendOpenFrom;
    private String weekendOpenTo;
    private Long hourlyTariff;
    private String remarks;

    public long getCarparkId() {
        return carparkId;
    }

    public void setCarparkId(long carparkId) {
        this.carparkId = carparkId;
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

    public long getStreetNo() {
        return streetNo;
    }

    public void setStreetNo(long streetNo) {
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

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
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

    public Long getHourlyTariff() {
        return hourlyTariff;
    }

    public void setHourlyTariff(Long hourlyTariff) {
        this.hourlyTariff = hourlyTariff;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
