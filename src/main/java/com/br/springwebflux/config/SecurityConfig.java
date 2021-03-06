package com.br.springwebflux.config;

import com.br.springwebflux.service.UserDetailsServiceImp;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;


@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity caso queria usar @PreAuthorize no controller
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        //@formatter:off
        return http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST, "/movies/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT, "/movies/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/movies/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/movies/**").hasRole("USER")
                .pathMatchers("/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/webjars/**")
                .permitAll()
                .anyExchange().authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic()
                .and()
                .build();
        //@formatter:on
    }

    @Bean
    ReactiveAuthenticationManager authenticationManager(UserDetailsServiceImp userDetailsServiceImp) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsServiceImp);
    }


    //Autenticação em memória
//    @Bean
//    public MapReactiveUserDetailsService userDetailsService() {
//        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        UserDetails user = User.withUsername("user")
//                .password(passwordEncoder.encode("user"))
//                .roles("USER")
//                .build();
//
//        UserDetails admin = User.withUsername("admin")
//                .password(passwordEncoder.encode("admin"))
//                .roles("USER", "ADMIN")
//                .build();
//
//        return new MapReactiveUserDetailsService(user, admin);
//    }
}
