package info.minatchy.facturation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ── Autorisation des routes ──────────────────────────────────────
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/error").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )

                // ── Formulaire de login ──────────────────────────────────────────
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll()
                )

                // ── Logout ───────────────────────────────────────────────────────
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // ── CSRF ─────────────────────────────────────────────────────────
                // CookieCsrfTokenRepository : compatible Thymeleaf via th:action
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )

                // ── Headers de sécurité ──────────────────────────────────────────
                .headers(headers -> headers
                        // HSTS : force HTTPS pendant 1 an, incluant les sous-domaines
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                                .preload(true)
                        )
                        // Empêche le clickjacking
                        .frameOptions(frame -> frame.deny())
                        // Empêche le MIME sniffing
                        .contentTypeOptions(ct -> {
                        })
                        // XSS protection (legacy mais utile sur vieux navigateurs)
                        .xssProtection(xss -> xss
                                .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                        )
                        // Content Security Policy : adapté à Thymeleaf (pas de JS inline externe)
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; " +
                                                "script-src 'self' https://cdn.jsdelivr.net; " +
                                                "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; " +
                                                "connect-src 'self' https://cdn.jsdelivr.net;" +
                                                "style-src-elem 'self' 'unsafe-inline' https://cdn.jsdelivr.net 'unsafe-inline'; " +
                                                "script-src-elem 'self' https://cdn.jsdelivr.net 'unsafe-inline';" +
                                                "img-src 'self' data:; " +
                                                "font-src 'self' https://cdn.jsdelivr.net; " +
                                                "object-src 'none'; " +
                                                "frame-ancestors 'none'; " +
                                                "base-uri 'self'; " +
                                                "form-action 'self'"
                                )
                        )
                        // Referrer Policy : ne pas fuiter l'URL dans les requêtes externes
                        .referrerPolicy(ref -> ref
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                        )
                        // Permissions Policy : désactive les APIs navigateur inutiles
                        .addHeaderWriter(new StaticHeadersWriter(
                                "Permissions-Policy",
                                "camera=(), microphone=(), geolocation=(), payment=()"
                        ))
                );
        // Note : la redirection HTTP → HTTPS est gérée par HttpToHttpsConfig
        // via un connecteur Tomcat dédié sur le port 8080.
        // requiresChannel() a été supprimé dans Spring Security 7.

        return http.build();
    }
}
