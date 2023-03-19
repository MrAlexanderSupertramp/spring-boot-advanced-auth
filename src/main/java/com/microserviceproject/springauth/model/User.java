package com.microserviceproject.springauth.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"id", "username"}))
public class User implements UserDetails { //serializable is needed if using non-pk fields as reference key. If using default ID field as reference then no need of serializable.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "username", nullable = false)
    private String username;

    private String password;

    private String rawPassword;

    @ColumnDefault("false")
    private boolean isDisabled;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE }) 
    @JoinTable(name = "user_roles", joinColumns = { @JoinColumn(name = "user_username", referencedColumnName = "username") }, inverseJoinColumns = { @JoinColumn(name = "role_name", referencedColumnName = "name") })
    private Collection<Role> roles = new ArrayList<>(); //owner side. Joincolumn is must on owner side.

    public User(String name, String username, String password, String rawPassword, boolean isDisabled, Collection<Role> roles) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.rawPassword = rawPassword;
        this.isDisabled = isDisabled;
        this.roles = roles;
    }

    public User(String name, String username, String password, boolean isDisabled, Collection<Role> roles) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.isDisabled = isDisabled;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> {
                    return new SimpleGrantedAuthority(role.getName());
                }).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !this.isDisabled;
    }
    
}
