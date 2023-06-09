package com.epam.esm.epammodule4.config;

import com.epam.esm.epammodule4.security.OAuth2UserService;
import com.epam.esm.epammodule4.security.jwt.AuthEntryPointJwt;
import com.epam.esm.epammodule4.security.jwt.AuthTokenFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import java.util.Arrays;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class WebSecurityConfig {

    @Value("${app.use-oauth2}")
    private boolean useOauth2;

    @Resource
    private UserDetailsService userDetailsService;
    AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                .antMatchers("/auth/login", "/auth/register", "/auth/refreshtoken", "/auth/logout").permitAll()
                .antMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/users/*").authenticated()
                .antMatchers(HttpMethod.PATCH, "/users").authenticated()
                .antMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/users/*").hasRole("ADMIN")

                .antMatchers(HttpMethod.GET, "/tags", "/tags/*").permitAll()
                .antMatchers(HttpMethod.PATCH, "/tags").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/tags").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/tags/*").hasRole("ADMIN")

                .antMatchers(HttpMethod.GET, "/certificates", "/certificates/*").permitAll()
                .antMatchers(HttpMethod.PATCH, "/certificates").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/certificates").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/certificates/*").hasRole("ADMIN")

                .antMatchers(HttpMethod.GET, "/orders").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/orders/*/user", "/orders/user", "/orders/cost").authenticated()
                .antMatchers(HttpMethod.PATCH, "/orders").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/orders").authenticated()
                .antMatchers(HttpMethod.DELETE, "/orders/*").hasRole("ADMIN");

        if (useOauth2) {
            http.oauth2Login()
                    .userInfoEndpoint()
                    .oidcUserService(oauthUserService());
        }

        http.authenticationProvider(authenticationProvider());
        http.cors().configurationSource(corsConfigurationSource());
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200"));
        configuration.addAllowedHeader("*");
        configuration.setAllowedMethods(Arrays.asList(
                HttpMethod.OPTIONS.name(),
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.HEAD.name()
        ));
        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public OidcUserService oauthUserService() {
        return new OAuth2UserService();
    }
}