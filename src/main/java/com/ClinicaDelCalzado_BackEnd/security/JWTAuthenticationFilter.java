package com.ClinicaDelCalzado_BackEnd.security;

import com.ClinicaDelCalzado_BackEnd.dtos.enums.AdminTypeEnum;
import com.ClinicaDelCalzado_BackEnd.services.impl.JWTCustomUserDetailsServiceImpl;
import com.ClinicaDelCalzado_BackEnd.util.JWTUtil;
import com.ClinicaDelCalzado_BackEnd.util.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTCustomUserDetailsServiceImpl customUserDetailsService;

    @Autowired
    private JWTUtil jwtGenerator;

    /**
     * Filtro para solicitar validación por token
     *
     * @param request petition of client
     * @param response response for client
     * @param filterChain filter chain
     * @throws ServletException throws server exception
     * @throws IOException throws IO exception
     */
    @Override
    protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getRequestTokenJWT(request);
        if (StringUtils.hasText(token) && jwtGenerator.validateToken(token)) {
            String userName = jwtGenerator.getSubjectFromToken(token);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);
            List<String> userRoles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

            if(userRoles.contains(AdminTypeEnum.PRINCIPAL.getKeyName()) || userRoles.contains(AdminTypeEnum.SECONDARY.getKeyName())
                    || userRoles.contains(AdminTypeEnum.ADMINISTRATOR.getKeyName())) {

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Verifica si existe la cabecera "Authorization" y cuyo valor comience con "Bearer"
     *
     * @param request petition of client
     * @return String indicate if the request contain a token
     */
    private String getRequestTokenJWT(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(SecurityConstants.HEADER);

        if(StringUtils.hasText(authenticationHeader) && authenticationHeader.startsWith(SecurityConstants.PREFIX)) {
            return authenticationHeader.substring(7);
        }
        return null;
    }
}