package ch.uzh.ifi.hase.soprafs22.entity;


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
    private long id;

    @Column(nullable = false)
    private long requesterId;

    @Column(nullable = false)
    private long requestedId;

    @Column(nullable = false)
    private long billingId;

    @Column(nullable = false)
    private String splitRequestStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
