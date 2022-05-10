package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class CarparkGetDTO {

    private long carparkId;
    private String name;
    private long maxCapacity;
    private long numOfEmptySpaces;
    private String street;
    private String streetNo;
    private long zipCode;
    private String city;
    private double longitude;
    private double latitude;
    private String weekdayOpenFrom;
    private String weekdayOpenTo;
    private String weekendOpenFrom;
    private String weekendOpenTo;
    private long hourlyTariff;
    private String remarks;
    private boolean isCheckedIn;

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

    public boolean getIsCheckedIn() {
        return isCheckedIn;}

    public void setIsCheckedIn(boolean isCheckedIn) {
        this.isCheckedIn = isCheckedIn;
    }
}
