package com.talentpath.Battleship.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.talentpath.Battleship.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserDetailImpl implements UserDetails {


    private Integer id;

    private String username;
    private String email;

    @JsonIgnore             //when serializing to send this object, ignore this field
    private String password;

    Collection<? extends GrantedAuthority> authorities;

    public UserDetailImpl(){}

    public UserDetailImpl( User buildFrom ){
        this.id = buildFrom.getId();
        this.username = buildFrom.getUsername();
        this.email = buildFrom.getEmail();

        this.password = buildFrom.getPassword();

        this.authorities = buildFrom.getRoles().stream()
                .map( role -> new SimpleGrantedAuthority( role.getName().name() ))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

}