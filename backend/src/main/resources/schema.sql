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

CREATE INDEX idx_ticket_id
ON tickets(ticket_id);

CREATE INDEX idx_project_assignment
ON tickets(project_assignment);

CREATE INDEX idx_current_status
ON tickets(current_status);

CREATE INDEX idx_priority
ON tickets(priority);

CREATE INDEX idx_created_at
ON tickets(created_at);