package com.atixlabs.semillasmiddleware.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "users", schema="security")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "last_name")
    private String lastName;

    private String email;

    private String phone;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore
    private String password;

    @JoinColumn(name = "id_role")
    @ManyToOne
    private Role role;

    private boolean active;

    /*Getters & Setters*/

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

}
