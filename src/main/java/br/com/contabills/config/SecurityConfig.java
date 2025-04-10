package br.com.contabills.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Classe de configuração de segurança da aplicação.
 * 
 * Define as políticas de segurança HTTP, autenticação, autorização, e o uso de tokens JWT.
 * 
 * Permite acesso público a rotas específicas como login, cadastro, Swagger e H2 console em ambiente de desenvolvimento.
 * 
 * Utiliza filtro {@link AuthorizationFilter} antes da autenticação padrão do Spring Security para processar JWTs.
 * 
 * @author Gerson
 * @version 1.0
 */
@Configuration
public class SecurityConfig {

    /**
     * Construtor padrão
     */
    public SecurityConfig() {
    }

    @Autowired
    AuthorizationFilter authorizationFilter;

    @Autowired
    Environment env;

    /**
     * Define a cadeia de filtros de segurança da aplicação.
     *
     * @param http objeto de configuração de segurança HTTP
     * @return {@link SecurityFilterChain} com configurações aplicadas
     * @throws Exception se ocorrer erro na configuração
     */
    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and() 
                .authorizeHttpRequests()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/h2-console/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/cadastro").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/login").permitAll()
                .and()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .headers().frameOptions().disable()
                .and()
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);

        if (env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("dev")) {
            http.authorizeHttpRequests().anyRequest().permitAll();
        } else {
            http.authorizeHttpRequests().anyRequest().authenticated();
        }

        return http.build();
    }

    /**
     * Cria e expõe o bean {@link AuthenticationManager}, necessário para autenticação.
     *
     * @param config {@link AuthenticationConfiguration} do Spring
     * @return {@link AuthenticationManager} configurado
     * @throws Exception se ocorrer erro ao recuperar o gerenciador
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean de {@link PasswordEncoder} usando o algoritmo BCrypt.
     *
     * @return {@link BCryptPasswordEncoder} para codificação de senhas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
