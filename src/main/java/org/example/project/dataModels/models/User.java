package org.example.project.dataModels.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.project.dataModels.enums.Role;

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

    public User(User user){
        this.id = user.id;
        this.role = user.role;
        this.login = user.login;
        this.password = user.password;
    }
}
