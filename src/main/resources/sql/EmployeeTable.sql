-- Create Employee table for employee management
CREATE TABLE IF NOT EXISTS Employee (
    employee_id INTEGER PRIMARY KEY,
    eid TEXT UNIQUE NOT NULL,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    phone TEXT,
    address TEXT,
    hire_date TEXT NOT NULL,
    salary REAL DEFAULT 0.0,
    status TEXT DEFAULT 'active',
    FOREIGN KEY (eid) REFERENCES Credentials(eid) ON DELETE CASCADE
);

-- Insert sample employees
INSERT OR IGNORE INTO Employee (eid, name, email, phone, address, hire_date, salary, status) VALUES
('admin', 'System Administrator', 'admin@shwandashop.com', '555-0100', 'Admin Office', '2024-01-01', 50000.0, 'active'),
('user1', 'John Smith', 'john.smith@shwandashop.com', '555-0101', '123 Main St', '2024-01-15', 35000.0, 'active'),
('user2', 'Sarah Johnson', 'sarah.johnson@shwandashop.com', '555-0102', '456 Oak Ave', '2024-02-01', 32000.0, 'active');
