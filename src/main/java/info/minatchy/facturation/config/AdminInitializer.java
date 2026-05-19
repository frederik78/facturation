package info.minatchy.facturation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import info.minatchy.facturation.model.User;
import info.minatchy.facturation.repository.UserRepository;

import java.util.Date;

@Configuration
public class AdminInitializer {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, 
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            log.info(">>> initAdmin démarré");
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole("ADMIN");
                admin.setCreatedAt(new Date().toString());
                admin.setEnabled(true);
                userRepository.save(admin);
                log.info(">>> Admin créé avec succès");
            } else {
                log.info(">>> Admin existe déjà");
            }
        };
    }
}