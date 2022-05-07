package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class NotificationResponseDTO {

    private boolean requestIsAccepted;

    public boolean getRequestIsAccepted() {
        return requestIsAccepted;
    }

    public void setRequestIsAccepted (boolean requestIsAccepted) {
        this.requestIsAccepted = requestIsAccepted;
    }
}
