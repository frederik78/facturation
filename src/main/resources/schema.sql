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
-- Table : app_user (utilisateurs de l'application)
-- -----------------------------------------------------------------------------
-- Nommée 'app_user' et non 'user' car USER est un mot réservé dans de nombreux
-- moteurs SQL. Cela évite des conflits si le projet migre vers PostgreSQL/MySQL.
--
-- Le mot de passe doit être stocké sous forme de hash compatible avec le
-- DelegatingPasswordEncoder de Spring Security.
-- Format attendu : {bcrypt}$2a$10$...
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS app_user (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    username   TEXT    NOT NULL UNIQUE,
    password   TEXT    NOT NULL,
    role       TEXT    NOT NULL DEFAULT 'ADMIN'
                       CHECK (role IN ('ADMIN', 'USER')),
    enabled    INTEGER NOT NULL DEFAULT 1
                       CHECK (enabled IN (0, 1)),
    created_at TEXT    NOT NULL DEFAULT (datetime('now')),
    last_login TEXT
);

CREATE INDEX IF NOT EXISTS idx_user_username ON app_user(username);

-- -----------------------------------------------------------------------------
-- Données initiales : compte administrateur par défaut
-- -----------------------------------------------------------------------------
-- Le hash ci-dessous correspond au mot de passe : changeit
-- Pour regénérer : PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("changeit")
-- -----------------------------------------------------------------------------
-- INSERT OR IGNORE INTO app_user (username, password, role, enabled)
-- VALUES (
--     'admin',
--     '{bcrypt}$2a$10$8K1p/a0dR6XXEuHBNQgqpOT7P5qnlKJlksBFMiNiTMTkCKNKGhNOu',
--     'ADMIN',
--     1
-- );

-- -----------------------------------------------------------------------------
-- Table : issuer (émetteur — profil de l'utilisateur, unique)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS issuer (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    company_name TEXT    NOT NULL,
    contact_name TEXT    NOT NULL,
    email        TEXT,
    phone        TEXT,
    tps_number   TEXT,
    tvq_number   TEXT,
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
    name        TEXT    NOT NULL,
    address     TEXT,
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
    invoice_number TEXT    NOT NULL UNIQUE,
    invoice_date   TEXT    NOT NULL,
    status         TEXT    NOT NULL DEFAULT 'DRAFT'
                           CHECK (status IN ('DRAFT','SENT','PAID','CANCELLED')),
    notes          TEXT,
    client_id      INTEGER NOT NULL,
    issuer_id      INTEGER NOT NULL,

    CONSTRAINT fk_invoice_client FOREIGN KEY (client_id) REFERENCES client(id),
    CONSTRAINT fk_invoice_issuer FOREIGN KEY (issuer_id) REFERENCES issuer(id)
);

CREATE INDEX IF NOT EXISTS idx_invoice_client ON invoice(client_id);
CREATE INDEX IF NOT EXISTS idx_invoice_date   ON invoice(invoice_date DESC);
CREATE INDEX IF NOT EXISTS idx_invoice_status ON invoice(status);

-- -----------------------------------------------------------------------------
-- Table : invoice_item (lignes de prestation d'une facture)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS invoice_item (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    invoice_id   INTEGER NOT NULL,
    item_order   INTEGER NOT NULL DEFAULT 0,
    description  TEXT    NOT NULL,
    detail       TEXT,
    quantity     REAL    NOT NULL DEFAULT 1,
    unit         TEXT    NOT NULL DEFAULT 'h',
    unit_price   REAL    NOT NULL DEFAULT 0,
    period_start TEXT,
    period_end   TEXT,

    CONSTRAINT fk_item_invoice FOREIGN KEY (invoice_id) REFERENCES invoice(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_item_invoice ON invoice_item(invoice_id);