-- ============================================================
-- Existing tickets table (preserved as-is)
-- ============================================================
CREATE TABLE IF NOT EXISTS tickets (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(255)  NOT NULL,
    description   TEXT,
    status        VARCHAR(50)   NOT NULL DEFAULT 'OPEN',
    priority      VARCHAR(50)   NOT NULL DEFAULT 'MEDIUM',
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    project_id    BIGINT,
    assignee_id   BIGINT
);

-- ============================================================
-- New: projects table
-- ============================================================
CREATE TABLE IF NOT EXISTS projects (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_name        VARCHAR(255) NOT NULL,
    project_description TEXT,
    project_status      VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    project_owner       VARCHAR(255),
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- New: employees table
-- ============================================================
CREATE TABLE IF NOT EXISTS employees (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_name   VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    phone_number    VARCHAR(50),
    department      VARCHAR(100),
    role            VARCHAR(100),
    status          VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Foreign keys (added only if not already present - safe to re-run)
ALTER TABLE tickets
    ADD CONSTRAINT fk_ticket_project  FOREIGN KEY (project_id)  REFERENCES projects(id)  ON DELETE SET NULL,
    ADD CONSTRAINT fk_ticket_assignee FOREIGN KEY (assignee_id) REFERENCES employees(id) ON DELETE SET NULL;
