package br.org.unicortes.barbearia.config;

import br.org.unicortes.barbearia.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private LogoutSuccessHandler customLogoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/barbeariaUnicortes/register").permitAll()
                        .requestMatchers("/barbeariaUnicortes/admin/**").hasRole("ADMIN")
                        .requestMatchers("/barbeariaUnicortes/barbeiro/**").hasRole("BARBER")
                        .requestMatchers("/barbeariaUnicortes/cliente/**").hasRole("CLIENT")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(authService), UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form//
                        .loginProcessingUrl("/barbeariaUnicortes/login")
                        .successHandler(customAuthenticationSuccessHandler)  // Use o handler customizado
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/barbeariaUnicortes/logout") // URL para o logout
                        .logoutSuccessHandler(customLogoutSuccessHandler) // URL para redirecionar após logout bem-sucedido
                        .invalidateHttpSession(true) // Invalida a sessão HTTP
                        .deleteCookies("JSESSIONID") // Exclui os cookies de sessão
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        /*UserDetails admin = User.builder()
                .username("admin")
                .password("$2a$12$AvcVZi9.Ak.Li7iqu9/d7OdnyLouk6wO2tK4e1nB0x5stcmW9foZK")
                .roles("ADMIN", "CLIENT", "BARBER")
                .build();

        System.out.println(admin);*/

        return authService;
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(authService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
