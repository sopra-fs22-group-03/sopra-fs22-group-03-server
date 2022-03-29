package ch.uzh.ifi.hase.soprafs22.rest.dto;

import java.time.LocalDate;

public class UserUpdatePutDTO {

  private String username;

  private LocalDate birthday;

  public LocalDate getBirthday() {
    return birthday;
  }

  public void setBirthday(LocalDate birthday) {
    this.birthday = birthday;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
