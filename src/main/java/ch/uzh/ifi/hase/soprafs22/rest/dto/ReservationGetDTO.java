package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class ReservationGetDTO {

    private long reservationId;
    private long userId;
    private long carparkId;
    private String checkinDate;
    private String checkinTime;
    private String checkoutDate;
    private String checkoutTime;
    private String licensePlate;
    private long parkingFee;

    public long getReservationId() {
        return reservationId;
    }

    public void setReservationId(long reservationId) {
        this.reservationId = reservationId;
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

    public long getParkingFee() {
        return parkingFee;
    }

    public void setParkingFee(long parkingFee) {
        this.parkingFee = parkingFee;
    }
}
