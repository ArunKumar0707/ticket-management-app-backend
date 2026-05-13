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
(
    'INC-1001',
    'HR-Portal',
    'Login page not loading for HR employees after latest deployment',
    'John Smith',
    'L2',
    'P1_CRITICAL',
    NOW(),
    'OPEN',
    'Reported by multiple users'
),
(
    'INC-1002',
    'ERP-Telemed',
    'Patient records sync failing intermittently',
    'Jane Doe',
    'L3',
    'P2_HIGH',
    NOW(),
    'IN_PROGRESS',
    'Under investigation'
),
(
    'INC-1003',
    'Payroll-System',
    'Payslip generation delayed for December cycle',
    'Bob Johnson',
    'L1',
    'P3_MEDIUM',
    NOW(),
    'RESOLVED',
    'Fixed in v2.1.3'
),
(
    'INC-1004',
    'Employee-Management',
    'New employee onboarding form throwing 500 error',
    'Alice Williams',
    'L2',
    'P2_HIGH',
    NOW(),
    'OPEN',
    'Needs backend fix'
),
(
    'INC-1005',
    'HR-Portal',
    'Leave balance not updating after approval',
    'John Smith',
    'L1',
    'P4_LOW',
    NOW(),
    'CLOSED',
    'Resolved by cache clear'
);