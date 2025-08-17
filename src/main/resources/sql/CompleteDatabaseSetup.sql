-- Complete Database Setup Script
-- This script creates all necessary tables for the e-commerce system

-- Create Product table
CREATE TABLE IF NOT EXISTS Product (
    pid INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    category TEXT NOT NULL,
    color TEXT,
    price REAL NOT NULL
);

-- Create Stock table
CREATE TABLE IF NOT EXISTS Stock (
    stock_id INTEGER PRIMARY KEY AUTOINCREMENT,
    pid INTEGER NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (pid) REFERENCES Product(pid) ON DELETE CASCADE
);

-- Create Customer table
CREATE TABLE IF NOT EXISTS Customer (
    customer_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    phone TEXT,
    address TEXT
);

-- Create Orders table
CREATE TABLE IF NOT EXISTS Orders (
    order_id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id INTEGER NOT NULL,
    products TEXT NOT NULL,
    total_price REAL NOT NULL,
    datetime TEXT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id) ON DELETE CASCADE
);

-- Insert sample data
INSERT OR IGNORE INTO Product (name, category, color, price) VALUES 
('iPhone 15', 'Electronics', 'Black', 999.99),
('Samsung Galaxy S24', 'Electronics', 'Silver', 899.99),
('MacBook Pro', 'Electronics', 'Space Gray', 1999.99),
('Nike Air Max', 'Footwear', 'White', 129.99),
('Adidas Ultraboost', 'Footwear', 'Black', 149.99);

INSERT OR IGNORE INTO Stock (pid, quantity) VALUES 
(1, 50),
(2, 30),
(3, 20),
(4, 100),
(5, 75);

INSERT OR IGNORE INTO Customer (name, email, phone, address) VALUES 
('John Doe', 'john.doe@example.com', '1234567890', '123 Main St, City, Country'),
('Jane Smith', 'jane.smith@example.com', '0987654321', '456 Oak Ave, Town, Country'),
('Bob Johnson', 'bob.johnson@example.com', '5551234567', '789 Pine Rd, Village, Country');
