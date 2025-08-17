-- Create Credentials table for user authentication
CREATE TABLE IF NOT EXISTS Credentials (
    eid TEXT PRIMARY KEY,
    pass TEXT NOT NULL,
    role TEXT DEFAULT 'employee'
);

-- Insert default admin credentials
INSERT OR IGNORE INTO Credentials (eid, pass, role) VALUES 
('admin', 'admin123', 'admin'),
('user1', 'password1', 'employee'),
('user2', 'password2', 'employee');
