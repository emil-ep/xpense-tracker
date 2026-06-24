package com.xperia.xpense_tracker.config;

import com.xperia.xpense_tracker.config.oauth2.Oauth2LoginSuccessHandler;
import com.xperia.xpense_tracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtAuthenticationFilter authenticationFilter;

    private final Oauth2LoginSuccessHandler oauth2LoginSuccessHandler;

    @Value("${frontend.url}")
    private String frontendUrl;

    public SecurityConfig(@Lazy Oauth2LoginSuccessHandler oauth2LoginSuccessHandler) {
        this.oauth2LoginSuccessHandler = oauth2LoginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request -> request
                                //TODO the "/v1/mcp/ should be removed once ai integrated within product.
                                //TODO this is added only for testing purposes
                                .requestMatchers("/v1/auth/**", "/actuator/**", "/actuator", "/v1/mcp/**", "/login", "/error").permitAll()
                                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage(frontendUrl + "/login")
                                .successHandler(oauth2LoginSuccessHandler)
//                        .defaultSuccessUrl("/home", true)
//                        .failureUrl("/login?error=true")
                        .failureHandler((request, response, exception) -> {
                            // This will tell us exactly what's going wrong
                            System.err.println("❌ OAuth2 Login Failed: " + exception.getMessage());
                            exception.printStackTrace();
                            response.sendRedirect("http://localhost:3000/login?error="
                                    + URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8));
                        })
                        .tokenEndpoint(token -> token.accessTokenResponseClient(accessTokenResponseClient()))
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        authenticationFilter, UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        return new RestClientAuthorizationCodeTokenResponseClient();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository){
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }
}
