package info.minatchy.facturation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import info.minatchy.facturation.repository.IssuerRepository;

/**
 * Pré-remplissage optionnel de la base au premier démarrage.
 * Si aucun émetteur n'existe, on crée un profil exemple.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    public DataInitializer(IssuerRepository issuerRepository) {
        this.issuerRepository = issuerRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final IssuerRepository issuerRepository;

    @Override
    public void run(String... args) {
        // Ne pas créer de profil émetteur par défaut pour éviter d'insérer des données d'exemple
        if (issuerRepository.count() == 0) {
            log.info("Aucun profil émetteur par défaut créé. Configurez votre profil via /settings.");
        }
    }
}
