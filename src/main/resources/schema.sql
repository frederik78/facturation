-- =============================================================================
-- Schéma SQLite — Application de facturation
-- =============================================================================
-- SQLite est une BD relationnelle embarquée :
--   - Typage dynamique (INTEGER, REAL, TEXT, BLOB, NUMERIC)
--   - Clés étrangères (activées via PRAGMA foreign_keys = ON)
--   - Transactions ACID
--   - Pas de type DATE natif → on stocke les dates en TEXT (ISO-8601 : YYYY-MM-DD)
-- =============================================================================

PRAGMA foreign_keys = ON;

-- -----------------------------------------------------------------------------
-- Table : issuer (émetteur — profil de l'utilisateur, unique)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS issuer (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    company_name TEXT    NOT NULL,               -- Raison sociale (ex: COMPANY_NAME)
    contact_name TEXT    NOT NULL,               -- Prénom Nom (ex: CONTACT_NAME)
    email        TEXT,
    phone        TEXT,
    tps_number   TEXT,                           -- Numéro TPS fédéral (ex: TPS_NUMBER RT0001)
    tvq_number   TEXT,                           -- Numéro TVQ provincial (ex: TVQ_NUMBER TQ0001)
    address      TEXT,
    city         TEXT,
    province     TEXT,
    postal_code  TEXT,
    country      TEXT
);

-- -----------------------------------------------------------------------------
-- Table : client (clients à facturer)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS client (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT    NOT NULL,                -- Nom / raison sociale
    address     TEXT,                            -- Rue + numéro
    city        TEXT,
    province    TEXT,
    postal_code TEXT,
    country     TEXT,
    email       TEXT,
    phone       TEXT
);

-- -----------------------------------------------------------------------------
-- Table : invoice (factures)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS invoice (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    invoice_number TEXT    NOT NULL UNIQUE,      -- N° affiché (ex: 2026001)
    invoice_date   TEXT    NOT NULL,             -- Date ISO-8601 (YYYY-MM-DD)
    status         TEXT    NOT NULL DEFAULT 'DRAFT'
                           CHECK (status IN ('DRAFT','SENT','PAID','CANCELLED')),
    notes          TEXT,                         -- Conditions de paiement, remarques
    client_id      INTEGER NOT NULL,
    issuer_id      INTEGER NOT NULL,

    CONSTRAINT fk_invoice_client FOREIGN KEY (client_id) REFERENCES client(id),
    CONSTRAINT fk_invoice_issuer FOREIGN KEY (issuer_id) REFERENCES issuer(id)
);

CREATE INDEX IF NOT EXISTS idx_invoice_client  ON invoice(client_id);
CREATE INDEX IF NOT EXISTS idx_invoice_date    ON invoice(invoice_date DESC);
CREATE INDEX IF NOT EXISTS idx_invoice_status  ON invoice(status);

-- -----------------------------------------------------------------------------
-- Table : invoice_item (lignes de prestation d'une facture)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS invoice_item (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    invoice_id   INTEGER NOT NULL,
    item_order   INTEGER NOT NULL DEFAULT 0,     -- Ordre d'affichage dans la facture
    description  TEXT    NOT NULL,               -- Titre (ex: Service informatique - Modernisation)
    detail       TEXT,                           -- Sous-détail libre (ex: Du 01/04 au 03/04)
    quantity     REAL    NOT NULL DEFAULT 1,     -- Quantité / heures (ex: 24.5)
    unit         TEXT    NOT NULL DEFAULT 'h',   -- Unité (h, jour, forfait, ...)
    unit_price   REAL    NOT NULL DEFAULT 0,     -- Prix unitaire en $ (ex: 125.00)
    period_start TEXT,                           -- Début de période ISO-8601 (optionnel)
    period_end   TEXT,                           -- Fin de période ISO-8601 (optionnel)

    CONSTRAINT fk_item_invoice FOREIGN KEY (invoice_id) REFERENCES invoice(id)
        ON DELETE CASCADE                        -- Supprime les lignes si la facture est supprimée
);

CREATE INDEX IF NOT EXISTS idx_item_invoice ON invoice_item(invoice_id);

- -----------------------------------------------------------------------------
-- Table : app_user (utilisateurs de l'application)
-- -----------------------------------------------------------------------------
-- Nommée 'app_user' et non 'user' car USER est un mot réservé dans de nombreux
-- moteurs SQL. Cela évite des conflits si le projet migre vers PostgreSQL/MySQL.
--
-- Le mot de passe doit être stocké sous forme de hash compatible avec le
-- DelegatingPasswordEncoder de Spring Security.
-- Format attendu : {bcrypt}$2a$10$...
--
-- Exemple d'insertion pour le mot de passe 'changeit' :
--   INSERT INTO app_user (username, password, role, enabled)
--   VALUES ('admin', '{bcrypt}$2a$10$VOTRE_HASH_ICI', 'ADMIN', 1);
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS app_user (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    username   TEXT    NOT NULL UNIQUE,          -- Identifiant de connexion
    password   TEXT    NOT NULL,                 -- Hash {bcrypt}$2a$10$... (jamais en clair)
    role       TEXT    NOT NULL DEFAULT 'ADMIN'
                       CHECK (role IN ('ADMIN', 'USER')),
    enabled    INTEGER NOT NULL DEFAULT 1        -- 1 = actif, 0 = désactivé (booléen SQLite)
                       CHECK (enabled IN (0, 1)),
    created_at TEXT    NOT NULL                  -- Date de création ISO-8601
                       DEFAULT (strftime('%Y-%m-%dT%H:%M:%SZ', 'now')),
    last_login TEXT                              -- Dernière connexion ISO-8601 (nullable)
);

CREATE INDEX IF NOT EXISTS idx_user_username ON app_user(username);

-- -----------------------------------------------------------------------------
-- Données initiales : compte administrateur par défaut
-- -----------------------------------------------------------------------------
-- ⚠️  IMPORTANT : remplacez ce hash avant toute mise en production.
--
-- Pour générer un hash bcrypt du mot de passe 'changeit' :
--
--   Option 1 — Java (recommandé, cohérent avec Spring Security) :
--     PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
--     System.out.println(encoder.encode("changeit"));
--
--   Option 2 — CLI Python :
--     python3 -c "import bcrypt; print('{bcrypt}' + bcrypt.hashpw(b'changeit', bcrypt.gensalt(10)).decode())"
--
--   Option 3 — CLI htpasswd (Apache) :
--     htpasswd -bnBC 10 "" changeit | tr -d ':\n' | sed 's/$2y/$2a/'
--     → puis préfixer manuellement avec {bcrypt}
--
-- Le hash ci-dessous correspond au mot de passe : changeit
-- -----------------------------------------------------------------------------
INSERT OR IGNORE INTO app_user (username, password, role, enabled)
VALUES (
    'admin',
    '{bcrypt}$2a$10$8K1p/a0dR6XXEuHBNQgqpOT7P5qnlKJlksBFMiNiTMTkCKNKGhNOu',
    'ADMIN',
    1
);