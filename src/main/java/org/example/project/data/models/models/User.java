package org.example.project.data.models.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.project.data.models.enums.Role;

@Getter
@Setter
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "login")
  private String login;

  @Column(name = "password")
  private String password;

  @Column(name = "role")
  private Role role;

  public User(User user) {
    this.id = user.id;
    this.role = user.role;
    this.login = user.login;
    this.password = user.password;
  }
}
