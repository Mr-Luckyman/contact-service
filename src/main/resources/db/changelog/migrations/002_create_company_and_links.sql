CREATE TABLE IF NOT EXISTS companies (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS person_company_links (
    id UUID PRIMARY KEY,
    person_id UUID NOT NULL,
    company_id UUID NOT NULL,
    role VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_person_company_links_person
        FOREIGN KEY (person_id) REFERENCES people(id) ON DELETE RESTRICT,
    CONSTRAINT fk_person_company_links_company
        FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_companies_name ON companies(name);
CREATE INDEX IF NOT EXISTS idx_person_company_links_company_id ON person_company_links(company_id);
CREATE INDEX IF NOT EXISTS idx_person_company_links_person_id ON person_company_links(person_id);
