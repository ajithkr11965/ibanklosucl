package com.sib.ibanklosucl.config;

import com.sib.ibanklosucl.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@Profile({"prod","uat","dev"})
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService userDetailsService;

    public WebSecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(new CustomXFrameOptionsFilter(), HeaderWriterFilter.class)
                .headers(headers -> headers.frameOptions().disable())
                .authorizeRequests()
                .antMatchers("/").permitAll() // This will be your home screen URL
                .antMatchers("/login").permitAll()
                .antMatchers("/loginauth").permitAll()
                .antMatchers("/sso/**").permitAll()
                .antMatchers("/sso").permitAll()
                .antMatchers("/api").permitAll()
                .antMatchers("/breapi").permitAll()
                .antMatchers("/DSA").permitAll()
                .antMatchers("/DSA/**").permitAll()
                 .antMatchers("/test").permitAll()
                .antMatchers("/test/**").permitAll()
                .antMatchers("/metricsapi").permitAll()
                .antMatchers("/metricsapi/**").permitAll()
                .antMatchers("/breapi/**").permitAll()
                .antMatchers("/assets/**").permitAll()
                .anyRequest().authenticated() // All other URLs are secured
            .and()
            .formLogin()
                .loginPage("/login") // Specify the login page URL
                .defaultSuccessUrl("/dashboard", true) // Redirect to dashboard on success
                .permitAll()

             .and()
                .csrf().disable()
            .logout()
                .permitAll()
                .and()
                .sessionManagement()
                .maximumSessions(1) // Only allow 1 session per user
                .maxSessionsPreventsLogin(true) // Expire old session when a new session is created
                .expiredUrl("/login?expired=true") // Redirect if the session expired
                .sessionRegistry(sessionRegistry());
//                .expiredUrl("/login?sessionExpired=true");

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService); // Use custom user details service
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

}
