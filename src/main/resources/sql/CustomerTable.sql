-- Create Customer table if it doesn't exist
CREATE TABLE IF NOT EXISTS Customer (
    customer_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT
);

-- Insert sample customers
INSERT OR IGNORE INTO Customer (name, email, phone, address) VALUES 
('John Doe', 'john.doe@example.com', '1234567890', '123 Main St, City, Country'),
('Jane Smith', 'jane.smith@example.com', '0987654321', '456 Oak Ave, Town, Country'),
('Bob Johnson', 'bob.johnson@example.com', '5551234567', '789 Pine Rd, Village, Country');
