package com.sib.ibanklosucl.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.HeaderWriterFilter;

@Configuration
@Profile("dev1")
@Slf4j
public class NoSecurityConfig extends WebSecurityConfigurerAdapter {
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
                .antMatchers("/breapi/**").permitAll()
                .antMatchers("/assets/**").permitAll()
                .antMatchers("/metricsapi").permitAll()
                .antMatchers("/metricsapi/**").permitAll()
                 .antMatchers("/test").permitAll()
                .antMatchers("/test/**").permitAll()
//                .antMatchers("/ibanklosucl/api/bre/**").permitAll()
//                .antMatchers("/ibanklosucl/api/**").permitAll()
//                .antMatchers("/api/getAmberData").permitAll()
//                .antMatchers("/api/getAmberData/**").permitAll()
//                .antMatchers("/getAmberData").permitAll()
//                .antMatchers("/getAmberData/**").permitAll()
//                .antMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .defaultSuccessUrl("/loading") //configure screen after login success
                .loginPage("/login")
                .and()
                .csrf().disable()
                .logout()
                .permitAll()
                .and()
                .sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/login?sessionExpired=true");

    }
}

