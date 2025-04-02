package com.hotelconnect.backend.users;

import org.apache.catalina.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Buscar el usuario por email en la base de datos
        User domainUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));

        // Crear un conjunto de autoridades (roles)
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (domainUser.getRoles() != null) {
            for (Role role : domainUser.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(role.getRole()));  // Asegúrate de que Role tenga el método 'getRole()'
            }
        }

        // Crear un CustomUserDetail con el usuario y sus autoridades
        CustomUserDetail customUserDetail = new CustomUserDetail();
        customUserDetail.setUser(domainUser);
        customUserDetail.setAuthorities(authorities);

        return customUserDetail;
    }
}
