package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class NotificationGetDTO {

    private long notificationId;
    private long requesterId;
    private long requestedId;
    private long billingId;
    private String splitRequestStatus;

    public long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(long notificationId) {
        this.notificationId = notificationId;
    }

    public long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(long requesterId) {
        this.requesterId = requesterId;
    }

    public long getRequestedId() {
        return requestedId;
    }

    public void setRequestedId(long requestedId) {
        this.requestedId = requestedId;
    }

    public long getBillingId() {
        return billingId;
    }

    public void setBillingId(long billingId) {
        this.billingId = billingId;
    }

    public String getSplitRequestStatus() {
        return splitRequestStatus;
    }

    public void setSplitRequestStatus(String splitRequestStatus) {
        this.splitRequestStatus = splitRequestStatus;
    }
}
