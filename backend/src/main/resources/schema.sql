CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id VARCHAR(20) NOT NULL UNIQUE,
    project_assignment VARCHAR(100) NOT NULL,
    issue_description TEXT NOT NULL,
    assigned_employee VARCHAR(100),
    support_level ENUM('L1', 'L2', 'L3') NOT NULL,
    priority ENUM('P1_CRITICAL', 'P2_HIGH', 'P3_MEDIUM', 'P4_LOW') NOT NULL,
    generation_date_time DATETIME,
    response_date_time DATETIME,
    resolution_time DATETIME,
    current_status ENUM(
        'OPEN',
        'IN_PROGRESS',
        'RESOLVED',
        'CLOSED'
    ) NOT NULL DEFAULT 'OPEN',
    resolution_details TEXT,
    remarks TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);

-- CREATE INDEX IF NOT EXISTS idx_ticket_id
-- ON tickets(ticket_id);

-- CREATE INDEX IF NOT EXISTS idx_project_assignment
-- ON tickets(project_assignment);

-- CREATE INDEX IF NOT EXISTS idx_current_status
-- ON tickets(current_status);

-- CREATE INDEX IF NOT EXISTS idx_priority
-- ON tickets(priority);

-- CREATE INDEX IF NOT EXISTS idx_created_at
-- ON tickets(created_at);

CREATE TABLE IF NOT EXISTS projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_code VARCHAR(30) NOT NULL UNIQUE,
    project_name VARCHAR(100) NOT NULL,
    description TEXT,
    status ENUM('ACTIVE', 'ON_HOLD', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    start_date DATE,
    end_date DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    designation VARCHAR(100),
    department VARCHAR(100),
    assigned_project VARCHAR(100),
    status ENUM('ACTIVE', 'INACTIVE', 'ON_LEAVE') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'PROJECT_MANAGER', 'EMPLOYEE') NOT NULL DEFAULT 'EMPLOYEE',
    employee_id BIGINT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);
