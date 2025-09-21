-- Database initialization script for SQLite
-- Creates all necessary tables for the e-commerce system

-- Create Product table
CREATE TABLE IF NOT EXISTS Product (
    pid INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    category TEXT NOT NULL,
    color TEXT,
    buying_price REAL NOT NULL,
    selling_price REAL NOT NULL,
    expiry_date TEXT
);

-- Create Stock table
CREATE TABLE IF NOT EXISTS Stock (
    stock_id INTEGER PRIMARY KEY,
    pid INTEGER NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (pid) REFERENCES Product(pid) ON DELETE CASCADE
);

-- Create Customer table
CREATE TABLE IF NOT EXISTS Customer (
    customer_id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    phone TEXT,
    address TEXT
);

-- Create Orders table
CREATE TABLE IF NOT EXISTS Orders (
    order_id INTEGER PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    products TEXT NOT NULL,
    total_price REAL NOT NULL,
    datetime TEXT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id) ON DELETE CASCADE
);

-- Create Credentials table for authentication
CREATE TABLE IF NOT EXISTS Credentials (
    eid TEXT PRIMARY KEY,
    pass TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'employee'
);

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

-- Insert sample products
INSERT OR IGNORE INTO Product (name, category, color, buying_price, selling_price, expiry_date) VALUES 
('iPhone 15', 'Electronics', 'Black', 800.00, 999.99, '2025-12-31');
INSERT OR IGNORE INTO Product (name, category, color, buying_price, selling_price, expiry_date) VALUES 
('Samsung Galaxy S24', 'Electronics', 'Silver', 700.00, 899.99, '2025-11-30');
INSERT OR IGNORE INTO Product (name, category, color, buying_price, selling_price, expiry_date) VALUES 
('MacBook Pro', 'Electronics', 'Space Gray', 1600.00, 1999.99, '2025-10-15');
INSERT OR IGNORE INTO Product (name, category, color, buying_price, selling_price, expiry_date) VALUES 
('Nike Air Max', 'Footwear', 'White', 100.00, 129.99, '2024-12-31');
INSERT OR IGNORE INTO Product (name, category, color, buying_price, selling_price, expiry_date) VALUES 
('Adidas Ultraboost', 'Footwear', 'Black', 120.00, 149.99, '2024-11-30');

-- Insert sample stock
INSERT OR IGNORE INTO Stock (pid, quantity) VALUES 
(1, 50),
(2, 30),
(3, 20),
(4, 100),
(5, 75);

-- Insert sample customers
INSERT OR IGNORE INTO Customer (name, email, phone, address) VALUES 
('John Doe', 'john.doe@example.com', '1234567890', '123 Main St, City, Country'),
('Jane Smith', 'jane.smith@example.com', '0987654321', '456 Oak Ave, Town, Country'),
('Bob Johnson', 'bob.johnson@example.com', '5551234567', '789 Pine Rd, Village, Country');
