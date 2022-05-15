package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class SplitRequestDTO {

    private String username;
    private String splitRequestMsg;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSplitRequestMsg() {
        return splitRequestMsg;
    }

    public void setSplitRequestMsg(String splitRequestMsg) {
        this.splitRequestMsg = splitRequestMsg;
    }
}
