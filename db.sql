CREATE DATABASE PhoneManagement;
USE PhoneManagement;

-- Table to manage user roles
CREATE TABLE role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
);

-- Table to manage user accounts
CREATE TABLE useraccount (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    fullname VARCHAR(50) NOT NULL,
    address VARCHAR(50) NOT NULL,
    phone_number VARCHAR(50) NOT NULL,
    role_id INT,
    active BIT,
    avatar LONGTEXT, 
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES role(role_id)
);

-- Table to manage product categories
CREATE TABLE category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL
);

-- Table to manage products
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    quantity INT,
    category_id INT,
    price DECIMAL(10,2) NOT NULL,
    warranty_period INT, -- Warranty period (months)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category(category_id)
);

-- Table to manage colors
CREATE TABLE color (
    color_id INT AUTO_INCREMENT PRIMARY KEY,
    color_name VARCHAR(50) NOT NULL
);

-- Junction table to manage many-to-many relationship between products and colors
CREATE TABLE productcolor (
	product_color_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT,
    image TEXT,
    color_id INT,
    quantity INT,
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP,
--     PRIMARY KEY (product_id, color_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (color_id) REFERENCES color(color_id)
);

-- Table to manage orders
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,  -- nguoi mua
    saler_id INT, -- nguoi ban
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2),
    status VARCHAR(50), 
    FOREIGN KEY (user_id) REFERENCES useraccount(user_id)
);

-- Table to manage order details
CREATE TABLE orderdetail (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    product_color_id INT,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_color_id) REFERENCES productcolor(product_color_id)
);

-- Table to manage warranty and repair
CREATE TABLE warrantyrepair (
    warranty_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name varchar(50),
    image Text,
    status varchar(50),
    user_id INT,   -- Nguoi can sua
    technical_id INT, -- Nguoi sua
    issue_description VARCHAR(255),
    repair_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES useraccount(user_id)
);

-- Table to manage purchases (hàng nhập)
CREATE TABLE purchase (
    purchase_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,  -- Nguoi nhap hang
    purchase_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2),
    origin VARCHAR(50),
    status VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES useraccount(user_id)
);

-- Table to manage purchase details (chi tiết hàng nhập)
CREATE TABLE purchasedetail (
    purchase_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    purchase_id INT,
    product_color_id INT,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (purchase_id) REFERENCES purchase(purchase_id),
    FOREIGN KEY (product_color_id) REFERENCES productcolor(product_color_id)
);
INSERT INTO Role (role_name) VALUES ('ADMIN'), ('SALER'),('TECHNICAL STAFF'),('WAREHOUSE STAFF'),('USER');
INSERT INTO useraccount (username, password, fullname, address, phone_number, role_id, active, avatar) VALUES
('quangtuan3903@gmail.com', '$2a$10$c0zBKI./IHXIbP5aHoGwMe7QLMJAA0nqqDWKCg8llkvSw9.ptnC1e', 'Dinh Quang Tuan', 'Address 28', '09176427813', 1, 0, NULL),
('tranminhkhoa@gmail.com', '$2a$10$eWCqQFasUPkVxS1C8hfql.IlQJRy6xWDcUyGAjsbCu5wURGdblWFm', 'Tran Minh Khoa', 'Address 31', '0912123456', 2, 0, NULL),
('Phananhquan@gmail.com', '$2a$10$PPNxFIMET68uiko6NdtnDOAh.t2c20dKvqyLt5qaBXKhq.pGNeb2C', 'Phan Anh Quan', 'Address 30', '0999123456', 2, 0, NULL),
('quyendinhnam@gmail.com', '$2a$10$f0fQP2d1sGhmzqeLYfbp/.qh/BFWL2MSye5omCCbbf2X0HfCfNAP2', 'Quyen Dinh Nam', 'Address 28', '0977123456', 2, 0, NULL),
('hoangthicuc@gmail.com', '$2a$10$wSo6R.191TVjMcFSw/x8teZ8.SY76BLp96lnVMSzk.iDAgtUmIDe.', 'Hoang Thi Cuc', 'Address 19', '0989012346', 3, 0, NULL),
('hoangminhtri@gmail.com', '$2a$10$P5esZ.wZFxKXXZACrIdAu.pwGwZrtheFVFYTsRpPdd2ldbP0F9rHy', 'Hoang Minh Tri', 'Address 29', '0988123456', 3, 0, NULL),
('nguyenhaoquang@gmail.com', '$2a$10$mbLv4pILsinIeRDHWYhFo.QChQ1DRzq.GPVj0dqjHi/a/xuqMPut2', 'Nguyen Hao Quang', 'Address 28', '01628849127', 3, 0, NULL),
('hoangphihong@gmail.com', '$2a$10$dRDgRUtjIr8Qq3kiIUNXRePZErkZGoPCFaJ3xu9MbuxT0Nu3Vcjda', 'Hoang Phi Hong', 'Address 28', '0871682721', 4, 0, NULL),
('tranminhchau@gmail.com', '$2a$10$Pm/6kLFPAua20cgCZ9h/vugGUrXP9HBzOn47IAt5LON.uGJVDLO.a', 'Tran Minh Chau', 'Address 21', '0910123457', 4, 0, NULL),
('tranthib@gmail.com', '$2a$10$3rsfAW5k0E449aL0zaiOEO9GjsLNEOmNb6U6kVtCE7yZ8rk54aXba', 'Tran Thi B', 'Address 3', '0923456789', 5, 0, NULL),
('lethingoc@gmail.com', '$2a$10$NOtkUAz/pE7ymZq/DeLFTOh8KpAhZCRb6qyP53uG7m6DKof.quyHq', 'Le Thi Ngoc', 'Address 4', '0934567890', 5, 0, NULL),
('ngothianh@gmail.com', '$2a$10$uO8iwtZA3L9ZG2smjmH7COcFGnYpq.lv9r3fitetlMqoLE2wNtRjO', 'Ngo Thi Anh', 'Address 6', '0956789012', 5, 0, NULL),
('nguyenhoang@gmail.com', '$2a$10$A5.xmwG6yFPVjpBFzoWA.ega1BoA79vptTGhJoPLJIawzYcFAEH/S', 'Nguyen Hoang', 'Address 8', '0978901234', 5, 0, NULL);
