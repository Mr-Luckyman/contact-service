CREATE TABLE IF NOT EXISTS invites (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    company_id UUID NOT NULL,
    role VARCHAR(255) NOT NULL,
    inviter_person_id UUID,
    referral_code VARCHAR(8) NOT NULL UNIQUE,
    status VARCHAR(32) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_invites_company
        FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    CONSTRAINT fk_invites_inviter_person
        FOREIGN KEY (inviter_person_id) REFERENCES persons(id) ON DELETE SET NULL,
    CONSTRAINT chk_invites_status
        CHECK (status IN ('PENDING', 'ACCEPTED', 'EXPIRED', 'CANCELLED')),
    CONSTRAINT chk_invites_referral_code_length
        CHECK (char_length(referral_code) = 8)
);

CREATE INDEX IF NOT EXISTS idx_invites_referral_code ON invites(referral_code);
CREATE INDEX IF NOT EXISTS idx_invites_company_id ON invites(company_id);
CREATE INDEX IF NOT EXISTS idx_invites_email ON invites(email);

CREATE UNIQUE INDEX IF NOT EXISTS ux_invites_active_email_company
    ON invites(email, company_id)
    WHERE status = 'PENDING';
