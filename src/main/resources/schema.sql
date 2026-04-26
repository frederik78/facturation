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
