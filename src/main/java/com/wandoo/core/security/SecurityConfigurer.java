package com.wandoo.core.security;

import com.wandoo.core.security.authentication.AuthenticationService;
import com.wandoo.core.security.authentication.ClientAuthenticationFilter;
import com.wandoo.core.security.authentication.ClientLoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ClientPrincipalService clientPrincipalService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/registration").anonymous()
                .antMatchers("/h2/**").permitAll() // for demonstration purposes only
                .antMatchers("/originator/**").access(getAllowedOriginatorsIpAddresses())
                .anyRequest().authenticated();

        http.addFilterBefore(new ClientLoginFilter("/login", authenticationManagerBean(), authenticationService),
                UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(new ClientAuthenticationFilter(authenticationService),
                UsernamePasswordAuthenticationFilter.class);

        http.csrf().disable().sessionManagement().sessionCreationPolicy(STATELESS);
        http.headers().frameOptions().sameOrigin();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(clientPrincipalService).passwordEncoder(bCryptPasswordEncoder());
    }

    private String getAllowedOriginatorsIpAddresses() {
        return originatorsIpAddresses().values().stream()
                .map(ip -> format("hasIpAddress('%s')", ip))
                .collect(joining(" or "));
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @ConfigurationProperties("security.originators")
    public Map<String, String> originatorsIpAddresses() {
        return newHashMap();
    }

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
