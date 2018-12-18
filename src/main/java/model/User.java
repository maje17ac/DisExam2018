package model;

import utils.Hashing;

public class User {

  public int id;
  public String firstname;
  public String lastname;
  public String email;
  private String password;
  //MAIKEN NOTES:
  private String token;
  private  long createdTime;

  //MAIKEN NOTES: Added createdTime in constructor
  public User(int id, String firstname, String lastname, String password, String email, long createdTime) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
    //MAIKEN NOTES: Hasher passordet i konstruktøren
    this.password = Hashing.sha(password);
    this.email = email;
    this.createdTime = createdTime;
  }

  // MAIKEN NOTES: getmetode for token
  public String getToken() {
    return token;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  //MAIKEN NOTES: Når jeg setter passord, setter jeg passord som hashet(metoden brukes dog ikke, da den også settes som hashet i databasekallet)
  public static void setPassword(String password) {
    password = Hashing.sha(password);
  }

  //MAIKE NOTES: Get og metode for created time
  public long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(long createdTime) {
    this.createdTime = createdTime;
  }
}
