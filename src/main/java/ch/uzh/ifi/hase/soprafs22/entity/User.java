package ch.uzh.ifi.hase.soprafs22.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.time.*;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = false)
  private boolean logged_in;

  @Column(nullable = false)
  private LocalDate creationDate;

  @Column(nullable = true)
  private LocalDate birthday;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
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

  public void setBirthday(LocalDate birthday) {this.birthday = birthday;}
}
