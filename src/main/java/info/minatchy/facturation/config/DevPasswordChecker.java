package info.minatchy.facturation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("default")
public class DevPasswordChecker implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DevPasswordChecker.class);

    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.password}")
    private String encodedPassword;

    public DevPasswordChecker(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (encodedPassword == null) {
            logger.warn("app.security.password is not set");
            return;
        }
        String cleanPassword = encodedPassword.trim();
        boolean matchesChangeit = passwordEncoder.matches("changeit", cleanPassword);
        boolean matchesAdmin = passwordEncoder.matches("admin", cleanPassword);

        logger.info("Password matches 'changeit'? {}", matchesChangeit);
        logger.info("Password matches 'admin'? {}", matchesAdmin);
        logger.info("Stored password length: {}", cleanPassword.length());
        logger.info("Stored password starts with: {}", cleanPassword.substring(0, Math.min(cleanPassword.length(), 10)));
    }
}
