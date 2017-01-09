package com.rohitw.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Created by ROHITW on 3/18/2015.
 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("rohitw").password("rohitw").roles("ADMIN");
        auth.inMemoryAuthentication().withUser("admin").password("rohitw").roles("ADMIN");
        auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/appln/**").access("hasRole('ROLE_ADMIN')")
            .antMatchers("/team/**").access("hasRole('ROLE_ADMIN')")
            .antMatchers("/appln/**").access("hasRole('ROLE_ADMIN')")
            .antMatchers("/env/**").access("hasRole('ROLE_ADMIN')")
            .antMatchers("/teamapp/**").access("hasRole('ROLE_ADMIN')")
            .antMatchers("/envprop/**").access("hasRole('ROLE_ADMIN')")
            .antMatchers("/login/**").access("hasRole('ROLE_ADMIN')")
            .antMatchers("/envinfo/**").access("hasRole('ROLE_ADMIN')")
            .and().formLogin();
    }

    @Bean
    public LogoutFilter logoutFilter() {
        // NOTE: See org.springframework.security.config.annotation.web.configurers.LogoutConfigurer
        // for details on setting up a LogoutFilter
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.setInvalidateHttpSession(true);

        LogoutFilter logoutFilter = new LogoutFilter("/", securityContextLogoutHandler);
        logoutFilter.setLogoutRequestMatcher(new AntPathRequestMatcher("/logout"));
        return logoutFilter;
    }
}
