package com.atixlabs.semillasmiddleware.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Builder
@Table(name = "role", schema="security")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String code;

    private String description;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "role_permission",
            joinColumns = {@JoinColumn(name = "ID_ROLE")},
            inverseJoinColumns = {@JoinColumn(name = "ID_PERMISSION")})
    private Set<Permission> permissions;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "role_menu",
            joinColumns = {@JoinColumn(name = "ID_ROLE")},
            inverseJoinColumns = {@JoinColumn(name = "ID_MENU")})
    private Set<Menu> menus;

     public Role(
            Integer id, String code, String description, Set<Permission> permissions, Set<Menu> menus) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.permissions = permissions;
        this.menus = menus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.atixlabs.semillasmiddleware.security.model.Role role = (com.atixlabs.semillasmiddleware.security.model.Role) o;
        return Objects.equals(code, role.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, description, permissions, menus);
    }
}
