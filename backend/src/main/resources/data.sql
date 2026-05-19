-- Seed projects (only insert if table is empty)
INSERT INTO projects (project_name, project_description, project_status, project_owner)
SELECT * FROM (
  SELECT 'Website Redesign',    'Complete overhaul of the company public website',   'ACTIVE',      'Alice Johnson' UNION ALL
  SELECT 'Mobile App v2',       'Second major release of the mobile application',     'IN_PROGRESS', 'Bob Smith'     UNION ALL
  SELECT 'API Integration Hub', 'Central hub for third-party API integrations',       'ACTIVE',      'Carol Davis'   UNION ALL
  SELECT 'Data Analytics',      'Business intelligence and reporting platform',       'ON_HOLD',     'David Lee'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM projects LIMIT 1);

-- Seed employees
INSERT INTO employees (employee_name, email, phone_number, department, role, status)
SELECT * FROM (
  SELECT 'Alice Johnson',  'alice@company.com',  '+1-555-0101', 'Engineering', 'Senior Developer',  'ACTIVE'   UNION ALL
  SELECT 'Bob Smith',      'bob@company.com',    '+1-555-0102', 'Engineering', 'Backend Developer', 'ACTIVE'   UNION ALL
  SELECT 'Carol Davis',    'carol@company.com',  '+1-555-0103', 'Product',     'Product Manager',   'ACTIVE'   UNION ALL
  SELECT 'David Lee',      'david@company.com',  '+1-555-0104', 'QA',          'QA Engineer',        'ACTIVE'   UNION ALL
  SELECT 'Eva Martinez',   'eva@company.com',    '+1-555-0105', 'Design',      'UI/UX Designer',    'INACTIVE' UNION ALL
  SELECT 'Frank Wilson',   'frank@company.com',  '+1-555-0106', 'DevOps',      'DevOps Engineer',   'ACTIVE'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM employees LIMIT 1);

-- Seed tickets
INSERT INTO tickets (title, description, status, priority, project_id, assignee_id)
SELECT * FROM (
  SELECT 'Login page not loading',         'Users report blank screen on login',                  'OPEN',        'HIGH',   1, 1 UNION ALL
  SELECT 'Dashboard widget crashes',       'Widget throws JS error on data load',                 'IN_PROGRESS', 'MEDIUM', 1, 2 UNION ALL
  SELECT 'API timeout on large datasets',  'Requests over 10k records time out',                  'OPEN',        'HIGH',   3, 2 UNION ALL
  SELECT 'Add dark mode support',          'Implement system-wide dark mode toggle',              'IN_PROGRESS', 'LOW',    2, 5 UNION ALL
  SELECT 'Update user profile endpoint',   'Profile PUT endpoint returns 500 on missing fields',  'RESOLVED',    'MEDIUM', 3, 1 UNION ALL
  SELECT 'Mobile nav menu broken',         'Hamburger menu does not open on iOS 17',             'OPEN',        'HIGH',   2, 5 UNION ALL
  SELECT 'Export to CSV feature',          'Users need CSV export for all list views',           'OPEN',        'LOW',    4, 4 UNION ALL
  SELECT 'Performance regression in v2.1', 'Page load 3x slower after last deploy',             'IN_PROGRESS', 'HIGH',   2, 3 UNION ALL
  SELECT 'Email notification delay',       'Notification emails arrive hours late',              'RESOLVED',    'MEDIUM', 3, 6 UNION ALL
  SELECT 'Analytics chart labels overlap', 'X-axis labels overlap on small viewports',           'CLOSED',      'LOW',    4, 5
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tickets LIMIT 1);
