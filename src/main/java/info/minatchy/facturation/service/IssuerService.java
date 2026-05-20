package info.minatchy.facturation.service;

import info.minatchy.facturation.model.Issuer;
import info.minatchy.facturation.repository.IssuerRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service pour la gestion du profil émetteur (unique).
 */
@Service
public class IssuerService {

    public IssuerService(IssuerRepository issuerRepository) {
        this.issuerRepository = issuerRepository;
    }

    private final IssuerRepository issuerRepository;

    /**
     * Retourne le profil émetteur (toujours le premier enregistrement).
     */
    public Optional<Issuer> getIssuer() {
        return issuerRepository.findAll().stream().findFirst();
    }

    /**
     * Sauvegarde ou met à jour le profil émetteur.
     */
    @Transactional
    public Issuer save(Issuer issuer) {
        return issuerRepository.save(issuer);
    }
}
