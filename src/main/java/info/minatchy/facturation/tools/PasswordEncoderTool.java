package info.minatchy.facturation.tools;

import info.minatchy.facturation.config.AdminInitializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


@Component
@Profile("encode-password")
public class PasswordEncoderTool implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(PasswordEncoderTool.class);


    private final PasswordEncoder passwordEncoder;

    public PasswordEncoderTool(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (args.length == 0) {
            log.info("Usage : --spring.profiles.active=encode-password --password=monmotdepasse");
            return;
        }
        Arrays.stream(args)
                .filter(arg -> arg.startsWith("--password="))
                .map(arg -> arg.substring("--password=".length()))
                .findFirst()
                .ifPresentOrElse(
                        pwd -> {
                            pwd = passwordEncoder.encode(pwd);
                            log.info("Hash BCrypt : " + pwd);

                        },
                        () -> log.info("Paramètre --password manquant.")
                );
    }
}
