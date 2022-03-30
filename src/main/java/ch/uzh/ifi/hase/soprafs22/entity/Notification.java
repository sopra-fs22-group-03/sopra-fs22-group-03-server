package ch.uzh.ifi.hase.soprafs22.entity;

import ch.uzh.ifi.hase.soprafs22.constant.SplitRequestStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.*;

/**
 * Internal Notification Representation
 * This class composes the internal representation of the notifications and defines how
 * a notification is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "NOTIFICATION")

public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long notificationId;

    @Column(nullable = false)
    private long requesterId;

    @Column(nullable = false)
    private long requestedId;

    @Column(nullable = false)
    private long billingId;

    @Column(nullable = false)
    private SplitRequestStatus response;

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

    public SplitRequestStatus getResponse() {
        return response;
    }

    public void setResponse(SplitRequestStatus response) {
        this.response = response;
    }

}
