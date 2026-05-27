-- =============================================================================
-- NEXUS TICKETING — Refactored Schema
-- =============================================================================

-- ----------------------------------------------------------------------------
-- 1. EMPLOYEES  (merged Employee + User — single source of truth)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS employees (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(100) NOT NULL UNIQUE,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    role          ENUM('ADMIN','PROJECT_MANAGER','EMPLOYEE') NOT NULL DEFAULT 'EMPLOYEE',
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    employee_name VARCHAR(100) NOT NULL,
    designation   VARCHAR(100),
    department    VARCHAR(100),
    status        ENUM('ACTIVE','INACTIVE','ON_LEAVE') NOT NULL DEFAULT 'ACTIVE',
    created_at    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 2. PROJECTS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS projects (
    id             BIGINT      AUTO_INCREMENT PRIMARY KEY,
    project_code   VARCHAR(30) NOT NULL UNIQUE,
    project_name   VARCHAR(100) NOT NULL,
    description    TEXT,
    project_status ENUM('ACTIVE','ON_HOLD','COMPLETED','CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    start_date     DATE,
    end_date       DATE,
    created_at     DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 3. EMPLOYEE ↔ PROJECT  (Many-to-Many mapping table)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS employee_projects (
    employee_id BIGINT NOT NULL,
    project_id  BIGINT NOT NULL,
    PRIMARY KEY (employee_id, project_id),
    CONSTRAINT fk_ep_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT fk_ep_project  FOREIGN KEY (project_id)  REFERENCES projects(id)  ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- 4. TICKETS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tickets (
    id                 BIGINT      AUTO_INCREMENT PRIMARY KEY,
    ticket_id          VARCHAR(20) NOT NULL UNIQUE,
    project_assignment VARCHAR(100) NOT NULL,
    issue_description  TEXT        NOT NULL,
    assigned_employee  VARCHAR(100),
    support_level      ENUM('L1','L2','L3') NOT NULL,
    priority           ENUM('P1_CRITICAL','P2_HIGH','P3_MEDIUM','P4_LOW') NOT NULL,
    generation_date_time DATETIME,
    response_date_time   DATETIME,
    resolution_time      DATETIME,
    current_status     ENUM('OPEN','IN_PROGRESS','RESOLVED','CLOSED') NOT NULL DEFAULT 'OPEN',
    resolution_details TEXT,
    remarks            TEXT,
    created_at         DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 5. SHIFT HOURS  (configuration)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS shift_hours (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    shift_name  VARCHAR(100) NOT NULL,
    start_time  TIME         NOT NULL,
    end_time    TIME         NOT NULL,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 6. HOLIDAYS  (configuration — weekends excluded automatically in code)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS holidays (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    holiday_name VARCHAR(150) NOT NULL,
    holiday_date DATE         NOT NULL UNIQUE,
    description  TEXT,
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 7. SLA CONFIG  (configuration)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sla_config (
    id                    BIGINT       AUTO_INCREMENT PRIMARY KEY,
    priority              VARCHAR(20)  NOT NULL,
    support_level         VARCHAR(10)  NOT NULL,
    response_time_hours   DOUBLE       NOT NULL,
    resolution_time_hours DOUBLE       NOT NULL,
    is_active             BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at            DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_sla_priority_level (priority, support_level)
);
