package com.example.demo.second.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserDeleted {
  @Id
  private Long id;

  private String email;

  private String password;

  public UserDeleted() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public void setPassword(String password) {
    this.password = password;
  }
}
