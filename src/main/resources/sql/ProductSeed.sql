-- Insert sample products
INSERT INTO Product (name, category, color, buying_price, selling_price, expiry_date) VALUES ('iPhone 15', 'Electronics', 'Black', 800.00, 999.99, '2025-12-31');
INSERT INTO Product (name, category, color, buying_price, selling_price, expiry_date) VALUES ('Samsung Galaxy S24', 'Electronics', 'Silver', 700.00, 899.99, '2025-11-30');
INSERT INTO Product (name, category, color, buying_price, selling_price, expiry_date) VALUES ('MacBook Pro', 'Electronics', 'Space Gray', 1600.00, 1999.99, '2025-10-15');
INSERT INTO Product (name, category, color, buying_price, selling_price, expiry_date) VALUES ('Nike Air Max', 'Footwear', 'White', 100.00, 129.99, '2024-12-31');
INSERT INTO Product (name, category, color, buying_price, selling_price, expiry_date) VALUES ('Adidas Ultraboost', 'Footwear', 'Black', 120.00, 149.99, '2024-11-30');

-- Insert sample stock
INSERT INTO Stock (pid, quantity) VALUES (1, 50);
INSERT INTO Stock (pid, quantity) VALUES (2, 30);
INSERT INTO Stock (pid, quantity) VALUES (3, 20);
INSERT INTO Stock (pid, quantity) VALUES (4, 100);
INSERT INTO Stock (pid, quantity) VALUES (5, 75);

-- Insert sample customers
INSERT INTO Customer (name, email, phone, address) VALUES ('John Doe', 'john.doe@example.com', '1234567890', '123 Main St, City, Country');
INSERT INTO Customer (name, email, phone, address) VALUES ('Jane Smith', 'jane.smith@example.com', '0987654321', '456 Oak Ave, Town, Country');
INSERT INTO Customer (name, email, phone, address) VALUES ('Bob Johnson', 'bob.johnson@example.com', '5551234567', '789 Pine Rd, Village, Country');
