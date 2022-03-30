package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class ParkingslipGetDTO {

    private long parkingslipId;
    private long carparkId;
    private long userId;
    private String checkinDate;
    private String checkinTime;
    private String checkoutDate;
    private String checkoutTime;

    public long getParkingslipId() {
        return parkingslipId;
    }

    public void setParkingslipId(long parkingslipId) {
        this.parkingslipId = parkingslipId;
    }

    public long getCarparkId() {
        return carparkId;
    }

    public void setCarparkId(long carparkId) {
        this.carparkId = carparkId;
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
}
