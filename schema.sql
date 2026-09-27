-- ============================================================
-- ApolloCare Database Schema
-- MySQL 8.x
-- ============================================================

CREATE DATABASE IF NOT EXISTS apollocare_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE apollocare_db;

-- ============================================================
-- TABLE: users
-- Stores both CUSTOMER and ADMIN accounts.
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    user_id    INT           AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100)  NOT NULL,
    email      VARCHAR(150)  NOT NULL UNIQUE,
    password   VARCHAR(255)  NOT NULL,   -- BCrypt hash
    phone      VARCHAR(15)   NOT NULL,
    address    TEXT          NOT NULL,
    role       ENUM('CUSTOMER', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLE: categories
-- Medicine categories (Pain Relief, Cold & Cough, etc.)
-- ============================================================
CREATE TABLE IF NOT EXISTS categories (
    category_id   INT          AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE
);

-- ============================================================
-- TABLE: medicines
-- Core product catalogue.
-- status=INACTIVE = soft-deleted (still shown in order history).
-- ============================================================
CREATE TABLE IF NOT EXISTS medicines (
    medicine_id           INT            AUTO_INCREMENT PRIMARY KEY,
    medicine_name         VARCHAR(200)   NOT NULL,
    description           TEXT,
    price                 DECIMAL(10, 2) NOT NULL,
    stock                 INT            NOT NULL DEFAULT 0,
    category_id           INT            NOT NULL,
    prescription_required TINYINT(1)     NOT NULL DEFAULT 0,
    image                 VARCHAR(255),
    status                ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    FOREIGN KEY (category_id) REFERENCES categories (category_id)
);

-- ============================================================
-- TABLE: orders
-- One row per placed order.
-- delivery_address is captured at order time (snapshot).
-- ============================================================
CREATE TABLE IF NOT EXISTS orders (
    order_id         INT            AUTO_INCREMENT PRIMARY KEY,
    user_id          INT            NOT NULL,
    total_amount     DECIMAL(10, 2) NOT NULL,
    order_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status           ENUM('PLACED', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED')
                     NOT NULL DEFAULT 'PLACED',
    delivery_address TEXT           NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

-- ============================================================
-- TABLE: order_items
-- One row per medicine per order.
-- price = price at the time of order (NOT live medicine price).
-- ============================================================
CREATE TABLE IF NOT EXISTS order_items (
    order_item_id INT            AUTO_INCREMENT PRIMARY KEY,
    order_id      INT            NOT NULL,
    medicine_id   INT            NOT NULL,
    quantity      INT            NOT NULL,
    price         DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id)    REFERENCES orders   (order_id),
    FOREIGN KEY (medicine_id) REFERENCES medicines (medicine_id)
);

-- ============================================================
-- SEED: Categories
-- ============================================================
INSERT INTO categories (category_name) VALUES
    ('Pain Relief'),
    ('Cold & Cough'),
    ('Vitamins & Supplements'),
    ('Skin Care'),
    ('Diabetes Care')
ON DUPLICATE KEY UPDATE category_name = category_name;

-- ============================================================
-- SEED: Sample Medicines
-- ============================================================
INSERT INTO medicines (medicine_name, description, price, stock, category_id, prescription_required, image, status) VALUES
    ('Paracetamol 500mg',  'Effective pain reliever and fever reducer. Suitable for adults and children over 12.',      12.50,  200, 1, 0, NULL, 'ACTIVE'),
    ('Ibuprofen 400mg',    'Non-steroidal anti-inflammatory drug (NSAID) for pain and inflammation relief.',            28.00,  150, 1, 0, NULL, 'ACTIVE'),
    ('Aspirin 75mg',       'Low-dose aspirin for pain relief and cardiovascular protection.',                           18.00,  180, 1, 0, NULL, 'ACTIVE'),
    ('Cough Syrup 100ml',  'Relieves dry and wet cough, soothes throat irritation.',                                   75.00,   80, 2, 0, NULL, 'ACTIVE'),
    ('Cetirizine 10mg',    'Antihistamine for cold, allergy and hay fever relief. Non-drowsy formula.',                 35.00,  120, 2, 0, NULL, 'ACTIVE'),
    ('Loratadine 10mg',    'Antihistamine for allergy symptoms including runny nose and watery eyes.',                  42.00,  100, 2, 0, NULL, 'ACTIVE'),
    ('Vitamin C 1000mg',   'High-strength Vitamin C for immunity boost and antioxidant support.',                      120.00,  90, 3, 0, NULL, 'ACTIVE'),
    ('Vitamin D3 60K IU',  'Supports bone health, muscle function and immune system. Weekly dosing.',                   95.00,  60, 3, 0, NULL, 'ACTIVE'),
    ('Zinc Sulphate 50mg', 'Essential mineral supplement for immunity, wound healing and cell growth.',                 55.00,  75, 3, 0, NULL, 'ACTIVE'),
    ('Calamine Lotion',    'Soothing lotion for skin irritation, rashes, insect bites and sunburn.',                   55.00,  75, 4, 0, NULL, 'ACTIVE'),
    ('Sunscreen SPF 50',   'Broad-spectrum UVA/UVB protection. Water-resistant formula for daily use.',               200.00,  40, 4, 0, NULL, 'ACTIVE'),
    ('Metformin 500mg',    'Oral diabetes medicine that controls blood sugar levels in type 2 diabetes.',               45.00, 100, 5, 1, NULL, 'ACTIVE'),
    ('Glimepiride 2mg',    'Stimulates pancreatic insulin production. Used with diet control for diabetes.',            85.00,  70, 5, 1, NULL, 'ACTIVE'),
    ('Glipizide 5mg',      'Sulfonylurea class medication to control blood sugar in type 2 diabetes.',                  60.00,  90, 5, 1, NULL, 'ACTIVE')
ON DUPLICATE KEY UPDATE medicine_name = medicine_name;

-- ============================================================
-- NOTE: Admin user is seeded automatically by DataInitializer.java
-- when the application starts for the first time.
-- Default credentials: admin@apollocare.com / Admin@123
-- ============================================================
