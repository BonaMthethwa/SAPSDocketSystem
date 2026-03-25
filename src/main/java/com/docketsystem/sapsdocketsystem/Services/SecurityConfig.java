package com.docketsystem.sapsdocketsystem.Services;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import jakarta.servlet.SessionTrackingMode;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Configuration
    
    @Order(1)
    public static class App1ConfigurationAdapter {

        @Bean
public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
}
@Autowired
private AdminService adminService;

//(Note to self) Zama remember this is for admin security configuration, This methos is what secures the admin pages
@Bean
public SecurityFilterChain filterChainApp1(HttpSecurity http, HandlerMappingIntrospector introspector)
        throws Exception {
    MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

    http
        .userDetailsService(adminService) 
        .csrf(csrf -> csrf.disable())
        .securityMatcher("/admin/**")
        .authorizeHttpRequests(auth -> auth
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/loginAdmin1")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/**")).hasRole("ADMIN")
        )
        .formLogin(form -> form
                .loginPage("/admin/loginAdmin1")
                .loginProcessingUrl("/admin/loginAdmin1")
                .usernameParameter("email")
                .passwordParameter("password")
                .failureUrl("/admin/loginAdmin1?error=true")
                .defaultSuccessUrl("/admin/welcomeAdmin", true)
        )
        .logout(logout -> logout
                .logoutUrl("/admin_logout")
                .logoutSuccessUrl("/protectedLinks")
                .deleteCookies("JSESSIONID")
        )
        .exceptionHandling(ex -> ex.accessDeniedPage("/403"));

    return http.build();
}
    }

        // (Note to self) Zama remember this is for user security configuration, This method is what secures the user pages
    @Configuration
    @Order(2)
    public static class App2ConfigurationAdapter {

        @Bean
        public SecurityFilterChain filterChainApp2(HttpSecurity http, HandlerMappingIntrospector introspector)
                throws Exception {
            MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
            http.securityMatcher("/user*")
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(mvcMatcherBuilder.pattern("/user*"))
                            .hasRole("USER"))
                    .formLogin(form -> form
                            .loginPage("/loginUser")
                            .loginProcessingUrl("/loginUser")
                            .failureUrl("/loginUser?error=loginError")
                            .defaultSuccessUrl("/welcomePage", true))
                    .logout(logout -> logout
                            .logoutUrl("/user_logout")
                            .logoutSuccessUrl("/protectedLinks")
                            .deleteCookies("JSESSIONID"))
                    .exceptionHandling(ex -> ex
                            .accessDeniedPage("/403"))
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }

    @Bean
public ServletContextInitializer servletContextInitializer() {
    return servletContext -> servletContext.setSessionTrackingModes(
        Collections.singleton(SessionTrackingMode.COOKIE)
    );
}
  
  
}
