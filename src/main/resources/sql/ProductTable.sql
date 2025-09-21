-- Create Product table if it doesn't exist
CREATE TABLE IF NOT EXISTS Product (
    pid INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    color VARCHAR(30),
    buying_price DECIMAL(10,2) NOT NULL,
    selling_price DECIMAL(10,2) NOT NULL,
    expiry_date TEXT
);

-- Create Stock table if it doesn't exist
CREATE TABLE IF NOT EXISTS Stock (
    stock_id INTEGER PRIMARY KEY AUTOINCREMENT,
    pid INTEGER NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (pid) REFERENCES Product(pid) ON DELETE CASCADE
);

-- Insert sample products
INSERT OR IGNORE INTO Product (name, category, color, buying_price, selling_price, expiry_date) VALUES
('iPhone 15', 'Electronics', 'Black', 800.00, 999.99, '2025-12-31'),
('Samsung Galaxy S24', 'Electronics', 'Silver', 700.00, 899.99, '2025-11-30'),
('MacBook Pro', 'Electronics', 'Space Gray', 1600.00, 1999.99, '2025-10-15'),
('Nike Air Max', 'Footwear', 'White', 100.00, 129.99, '2024-12-31'),
('Adidas Ultraboost', 'Footwear', 'Black', 120.00, 149.99, '2024-11-30');

-- Insert sample stock
INSERT OR IGNORE INTO Stock (pid, quantity) VALUES 
(1, 50),
(2, 30),
(3, 20),
(4, 100),
(5, 75);
