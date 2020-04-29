package com.atixlabs.semillasmiddleware.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "users", schema="security")
public class User extends AuditableEntity{

    private static final PasswordEncoder pwEncoder = new BCryptPasswordEncoder();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public User() {
        this.active = true;
    }

    public User(Long id, String name, String lastName, String username, String password, Role role) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.active = true;
    }

    @PrePersist
    public void prePersist() {
        password = pwEncoder.encode(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return active == user.active
                && Objects.equals(id, user.id)
                && Objects.equals(name, user.name)
                && Objects.equals(lastName, user.lastName)
                && Objects.equals(email, user.email)
                && Objects.equals(phone, user.phone)
                && Objects.equals(username, user.username)
                && role.equals(user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastName, email, phone, username, password, role, active);
    }
}
