INSERT INTO tickets (
ticket_id,
project_assignment,
issue_description,
assigned_employee,
support_level,
priority,
generation_date_time,
current_status,
remarks
)
VALUES
('INC-1001', 'HR-Portal', 'Login page not loading for HR employees after latest deployment', 'John Smith', 'L2', 'P1_CRITICAL', NOW(), 'OPEN', 'Reported by multiple users'),
('INC-1002', 'ERP-Telemed', 'Patient records sync failing intermittently', 'Jane Doe', 'L3', 'P2_HIGH', NOW(), 'IN_PROGRESS', 'Under investigation');