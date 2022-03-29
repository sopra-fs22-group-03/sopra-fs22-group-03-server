package ch.uzh.ifi.hase.soprafs22.rest.dto;


import java.time.*;

public class UserGetDTO {

  private Long id;
//  private String password;
  private String username;
  private LocalDate creationDate;
  private boolean logged_in;

  private LocalDate birthday;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

//  public String getPassword() {
//    return password;
//  }

//  public void setPassword(String password) {
//    this.password = password;
//  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public boolean getLogged_in() {
    return logged_in;
  }

  public void setLogged_in(boolean logged_in) {
    this.logged_in = logged_in;
  }

  public LocalDate getCreationDate() {return creationDate;}

  public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

  public LocalDate getBirthday() {return birthday;}

  public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
}
