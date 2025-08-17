-- Create Product table if it doesn't exist
CREATE TABLE IF NOT EXISTS Product (
    pid INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    color VARCHAR(30),
    price DECIMAL(10,2) NOT NULL
);

-- Create Stock table if it doesn't exist
CREATE TABLE IF NOT EXISTS Stock (
    stock_id INTEGER PRIMARY KEY AUTOINCREMENT,
    pid INTEGER NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (pid) REFERENCES Product(pid) ON DELETE CASCADE
);

-- Insert sample products
INSERT OR IGNORE INTO Product (name, category, color, price) VALUES 
('iPhone 15', 'Electronics', 'Black', 999.99),
('Samsung Galaxy S24', 'Electronics', 'Silver', 899.99),
('MacBook Pro', 'Electronics', 'Space Gray', 1999.99),
('Nike Air Max', 'Footwear', 'White', 129.99),
('Adidas Ultraboost', 'Footwear', 'Black', 149.99);

-- Insert sample stock
INSERT OR IGNORE INTO Stock (pid, quantity) VALUES 
(1, 50),
(2, 30),
(3, 20),
(4, 100),
(5, 75);
