package com.hotelconnect.backend.users;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class CustomUserDetail implements UserDetails {

    private static final long serialVersionUID = 1L;
    @Getter
    private User user;
    private Set<GrantedAuthority> authorities;

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();  // Asumimos que 'password' es un campo de la entidad User
    }

    @Override
    public String getUsername() {
        return user.getEmail();  // Usamos el email para el nombre de usuario
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();  // Asumiendo que tienes ese método en tu entidad User
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();  // Lo mismo aquí
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();  // Lo mismo aquí
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();  // Lo mismo aquí
    }
}


