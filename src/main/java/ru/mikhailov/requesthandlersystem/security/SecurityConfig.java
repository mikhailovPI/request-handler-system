/*
package ru.mikhailov.requesthandlersystem.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtConfigurer jwtConfigurer;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/auth/logout").authenticated()
                .antMatchers("/auth/login").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/operator/**").hasRole("OPERATOR")
                .antMatchers("/user/**").hasRole("USER")
                .anyRequest()
                .authenticated()
                */
/*.and()
                .apply()*//*
;
    }
}
*/
