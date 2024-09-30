package com.ClinicaDelCalzado_BackEnd.services.impl;

import com.ClinicaDelCalzado_BackEnd.entity.Administrator;
import com.ClinicaDelCalzado_BackEnd.services.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JWTCustomUserDetailsServiceImpl implements UserDetailsService {

    private final IAdminService adminService;

    @Autowired
    public JWTCustomUserDetailsServiceImpl (IAdminService adminService) {
        this.adminService = adminService;
    }

    public Collection<GrantedAuthority> mapToAuthorities(List<String> adminType) {
        return adminType.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Administrator administrator = adminService.findAdministratorById(Long.valueOf(username))
                .orElseThrow(() -> new UsernameNotFoundException("El administrador no existe"));
        return new User(administrator.getIdAdministrator().toString(), administrator.getPassword(), mapToAuthorities(List.of(administrator.getRole())));
    }
}
