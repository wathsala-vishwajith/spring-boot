-- Sample data for Product API
-- This file is automatically executed on application startup

INSERT INTO products (id, name, description, price, category, stock_quantity, sku, active, created_at, updated_at) VALUES
(1, 'Wireless Mouse', 'Ergonomic wireless mouse with 6 programmable buttons and long battery life', 29.99, 'Electronics', 150, 'WM-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Mechanical Keyboard', 'RGB mechanical keyboard with Cherry MX switches', 89.99, 'Electronics', 75, 'KB-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'USB-C Hub', '7-in-1 USB-C hub with HDMI, USB 3.0, and SD card reader', 39.99, 'Electronics', 200, 'HUB-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Laptop Stand', 'Adjustable aluminum laptop stand for ergonomic viewing', 49.99, 'Accessories', 120, 'LS-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Webcam HD', '1080p HD webcam with built-in microphone', 59.99, 'Electronics', 90, 'WC-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Desk Lamp', 'LED desk lamp with adjustable brightness and color temperature', 34.99, 'Office Supplies', 180, 'DL-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'Monitor Arm', 'Dual monitor arm mount with cable management', 79.99, 'Accessories', 45, 'MA-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'Wireless Headset', 'Noise-canceling wireless headset with 30-hour battery', 129.99, 'Electronics', 60, 'WH-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'Cable Organizer', 'Set of cable clips and organizers for desk management', 12.99, 'Office Supplies', 300, 'CO-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'External SSD', '1TB portable external SSD with USB-C connection', 109.99, 'Electronics', 85, 'SSD-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'Notebook Set', 'Premium notebook set with dotted pages (3-pack)', 24.99, 'Office Supplies', 250, 'NB-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 'Pen Set', 'Gel pen set with ergonomic grip (12-pack)', 15.99, 'Office Supplies', 400, 'PS-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 'HDMI Cable', '6ft Premium HDMI 2.1 cable supporting 8K resolution', 19.99, 'Electronics', 500, 'HDMI-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 'Phone Stand', 'Adjustable phone stand with cable management', 16.99, 'Accessories', 220, 'PH-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 'Wireless Charger', 'Fast wireless charging pad for Qi-enabled devices', 24.99, 'Electronics', 175, 'WCH-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
