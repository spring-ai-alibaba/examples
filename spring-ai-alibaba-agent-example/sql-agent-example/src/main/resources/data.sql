-- E-commerce Sample Data
-- Provides realistic sample data for demonstration

-- Insert Categories
INSERT INTO categories (name, description) VALUES
('Electronics', 'Electronic devices and accessories'),
('Clothing', 'Apparel and fashion items'),
('Books', 'Books and publications'),
('Home & Kitchen', 'Home appliances and kitchen items'),
('Sports', 'Sports equipment and outdoor gear');

-- Insert Customers
INSERT INTO customers (name, email, phone, city, country) VALUES
('Zhang Wei', 'zhangwei@example.com', '13800138001', 'Beijing', 'China'),
('Li Na', 'lina@example.com', '13800138002', 'Shanghai', 'China'),
('Wang Fang', 'wangfang@example.com', '13800138003', 'Guangzhou', 'China'),
('Liu Yang', 'liuyang@example.com', '13800138004', 'Shenzhen', 'China'),
('Chen Jie', 'chenjie@example.com', '13800138005', 'Hangzhou', 'China'),
('Yang Ming', 'yangming@example.com', '13800138006', 'Chengdu', 'China'),
('Zhao Lin', 'zhaolin@example.com', '13800138007', 'Wuhan', 'China'),
('Huang Ying', 'huangying@example.com', '13800138008', 'Nanjing', 'China'),
('Zhou Tao', 'zhoutao@example.com', '13800138009', 'Xi''an', 'China'),
('Wu Jun', 'wujun@example.com', '13800138010', 'Suzhou', 'China');

-- Insert Products
INSERT INTO products (name, description, price, stock_quantity, category_id) VALUES
-- Electronics
('iPhone 15 Pro', 'Apple iPhone 15 Pro 256GB', 8999.00, 50, 1),
('MacBook Air M3', 'Apple MacBook Air 13-inch M3 chip', 9499.00, 30, 1),
('AirPods Pro 2', 'Apple AirPods Pro 2nd generation', 1899.00, 100, 1),
('Huawei Mate 60', 'Huawei Mate 60 Pro 512GB', 6999.00, 45, 1),
('Sony WH-1000XM5', 'Sony wireless noise-canceling headphones', 2599.00, 60, 1),

-- Clothing
('Nike Air Max', 'Nike Air Max 90 running shoes', 899.00, 200, 2),
('Uniqlo Down Jacket', 'Ultra light down jacket', 599.00, 150, 2),
('Levi''s 501 Jeans', 'Classic straight fit jeans', 699.00, 120, 2),
('Adidas Hoodie', 'Adidas Originals hoodie', 459.00, 180, 2),
('North Face Backpack', 'The North Face hiking backpack', 799.00, 80, 2),

-- Books
('The Three-Body Problem', 'Liu Cixin sci-fi novel', 59.00, 500, 3),
('Erta''s Philosophy', 'Philosophy introduction book', 89.00, 300, 3),
('Python Programming', 'Learn Python programming from scratch', 79.00, 400, 3),
('Machine Learning Basics', 'Introduction to machine learning', 129.00, 250, 3),
('Spring Boot in Action', 'Spring Boot development guide', 99.00, 350, 3),

-- Home & Kitchen
('Xiaomi Rice Cooker', 'Smart rice cooker 4L', 299.00, 100, 4),
('Dyson V15 Vacuum', 'Dyson cordless vacuum cleaner', 4999.00, 25, 4),
('Philips Air Fryer', 'Digital air fryer 5.5L', 899.00, 60, 4),
('IKEA Table Lamp', 'LED desk lamp with USB port', 199.00, 200, 4),
('Nespresso Coffee Machine', 'Automatic coffee machine', 1299.00, 40, 4),

-- Sports
('Wilson Tennis Racket', 'Professional tennis racket', 1599.00, 35, 5),
('Yoga Mat', 'Non-slip yoga mat 6mm', 129.00, 300, 5),
('Fitbit Charge 5', 'Fitness tracker smartband', 999.00, 80, 5),
('Decathlon Bicycle', 'Mountain bike 26-inch', 1899.00, 20, 5),
('Swimming Goggles', 'Anti-fog swimming goggles', 99.00, 150, 5);

-- Insert Orders
INSERT INTO orders (customer_id, order_date, status, total_amount, shipping_address) VALUES
(1, '2024-01-15 10:30:00', 'completed', 10898.00, 'Beijing Chaoyang District'),
(2, '2024-01-16 14:20:00', 'completed', 1498.00, 'Shanghai Pudong District'),
(3, '2024-01-17 09:15:00', 'shipped', 6999.00, 'Guangzhou Tianhe District'),
(4, '2024-01-18 16:45:00', 'completed', 2698.00, 'Shenzhen Nanshan District'),
(5, '2024-01-19 11:00:00', 'processing', 9499.00, 'Hangzhou Xihu District'),
(1, '2024-01-20 13:30:00', 'completed', 188.00, 'Beijing Haidian District'),
(6, '2024-01-21 10:00:00', 'shipped', 4999.00, 'Chengdu Jinjiang District'),
(7, '2024-01-22 15:20:00', 'pending', 899.00, 'Wuhan Wuchang District'),
(8, '2024-01-23 09:45:00', 'completed', 3497.00, 'Nanjing Xuanwu District'),
(9, '2024-01-24 14:10:00', 'processing', 1899.00, 'Xi''an Yanta District'),
(10, '2024-01-25 11:30:00', 'completed', 1128.00, 'Suzhou Industrial Park'),
(2, '2024-01-26 16:00:00', 'shipped', 8999.00, 'Shanghai Jing''an District'),
(3, '2024-01-27 10:20:00', 'completed', 599.00, 'Guangzhou Haizhu District'),
(4, '2024-01-28 13:45:00', 'pending', 2599.00, 'Shenzhen Futian District'),
(5, '2024-01-29 09:00:00', 'completed', 267.00, 'Hangzhou Binjiang District');

-- Insert Order Items
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
-- Order 1: iPhone + AirPods
(1, 1, 1, 8999.00),
(1, 3, 1, 1899.00),

-- Order 2: Nike shoes + Down jacket
(2, 6, 1, 899.00),
(2, 7, 1, 599.00),

-- Order 3: Huawei phone
(3, 4, 1, 6999.00),

-- Order 4: Sony headphones + Yoga mat
(4, 5, 1, 2599.00),
(4, 22, 1, 99.00),

-- Order 5: MacBook
(5, 2, 1, 9499.00),

-- Order 6: Books
(6, 11, 1, 59.00),
(6, 13, 1, 79.00),
(6, 14, 1, 50.00),

-- Order 7: Dyson vacuum
(7, 17, 1, 4999.00),

-- Order 8: Air fryer
(8, 18, 1, 899.00),

-- Order 9: Levi's jeans + Adidas hoodie + backpack
(9, 8, 1, 699.00),
(9, 9, 2, 459.00),
(9, 10, 2, 799.00),

-- Order 10: Bicycle
(10, 24, 1, 1899.00),

-- Order 11: Books + Yoga mat
(11, 15, 1, 99.00),
(11, 22, 1, 129.00),
(11, 6, 1, 899.00),

-- Order 12: iPhone
(12, 1, 1, 8999.00),

-- Order 13: Down jacket
(13, 7, 1, 599.00),

-- Order 14: Sony headphones
(14, 5, 1, 2599.00),

-- Order 15: Books
(15, 11, 1, 59.00),
(15, 12, 1, 89.00),
(15, 13, 1, 79.00),
(15, 22, 1, 40.00);
