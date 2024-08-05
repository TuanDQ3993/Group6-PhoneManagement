CREATE DATABASE PhoneManagement;
USE PhoneManagement;

-- Table to manage user roles
CREATE TABLE role
(
    role_id   INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
);

-- Table to manage user accounts
CREATE TABLE useraccount
(
    user_id      INT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL,
    password     VARCHAR(255) NOT NULL,
    fullname     VARCHAR(50)  NOT NULL,
    address      VARCHAR(255)  NOT NULL,
    phone_number VARCHAR(50)  NOT NULL,
    role_id      INT,
    active       BIT,
    avatar       LONGTEXT,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES role (role_id)
);

-- Table to manage product categories
CREATE TABLE category
(
    category_id   INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL
);

-- Table to manage products
CREATE TABLE products
(
    product_id      INT AUTO_INCREMENT PRIMARY KEY,
    product_name    VARCHAR(100)   NOT NULL,
    description     TEXT,
    quantity        INT,
    category_id     INT,
    warranty_period INT, -- Warranty period (months)
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    brand_name       VARCHAR(50),
    FOREIGN KEY (category_id) REFERENCES category (category_id)
);

-- Table to manage colors
CREATE TABLE color
(
    color_id   INT AUTO_INCREMENT PRIMARY KEY,
    color_name VARCHAR(50) NOT NULL
);

-- Junction table to manage many-to-many relationship between products and colors
CREATE TABLE productinfo
(
    product_color_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id       INT,
    image            TEXT,
    color_id         INT,
    quantity         INT,
    price           DECIMAL(10, 2) NOT NULL,
    last_updated     DATETIME DEFAULT CURRENT_TIMESTAMP,
     isdeleted 		BIT,
--     PRIMARY KEY (product_id, color_id),
    FOREIGN KEY (product_id) REFERENCES products (product_id),
    FOREIGN KEY (color_id) REFERENCES color (color_id)
);

-- Table to manage orders
CREATE TABLE orders
(
    order_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT, -- nguoi mua
    saler_id     INT, -- nguoi ban
    order_date   DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2),
    status       VARCHAR(50),
    note         VARCHAR(250),
    receiver     VARCHAR(50),
    address      VARCHAR(250),
    phone_number VARCHAR(50),
    payment      VARCHAR(20),    -- COD hay Onl...
    FOREIGN KEY (user_id) REFERENCES useraccount (user_id)
);

-- Table to manage order details
CREATE TABLE orderdetail
(
    order_detail_id  INT AUTO_INCREMENT PRIMARY KEY,
    order_id         INT,
    product_color_id INT,
    quantity         INT            NOT NULL,
    price            DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (order_id),
    FOREIGN KEY (product_color_id) REFERENCES productinfo (product_color_id)
);

-- Table to manage warranty and repair
-- Table to manage warranty and repair
CREATE TABLE warrantyrepair (
    warranty_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(50),
    image TEXT,
    status VARCHAR(50),
    user_id INT,   -- Người cần sửa
    technical_id INT, -- Người sửa
    issue_description VARCHAR(255),
    is_deleted BIT,
    repair_date DATE,
    type VARCHAR(50),
    order_id INT, -- Thêm cột để liên kết với bảng orders
    FOREIGN KEY (technical_id) REFERENCES useraccount(user_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) 
);

INSERT INTO role (role_name)
VALUES ('ADMIN'),
       ('SALER'),
       ('TECHNICAL STAFF'),
       ('USER');
INSERT INTO useraccount (username, password, fullname, address, phone_number, role_id, active, avatar)
VALUES ('quangtuan3903@gmail.com', '$2a$10$c0zBKI./IHXIbP5aHoGwMe7QLMJAA0nqqDWKCg8llkvSw9.ptnC1e', 'Dinh Quang Tuan',
        'Address 28', '09176427813', 1,1, NULL),
       ('tranminhkhoa@gmail.com', '$2a$10$eWCqQFasUPkVxS1C8hfql.IlQJRy6xWDcUyGAjsbCu5wURGdblWFm', 'Tran Minh Khoa',
        'Address 31', '0912123456', 2, 1, NULL),
       ('Phananhquan@gmail.com', '$2a$10$PPNxFIMET68uiko6NdtnDOAh.t2c20dKvqyLt5qaBXKhq.pGNeb2C', 'Phan Anh Quan',
        'Address 30', '0999123456', 2, 1, NULL),
       ('quyendinhnam@gmail.com', '$2a$10$f0fQP2d1sGhmzqeLYfbp/.qh/BFWL2MSye5omCCbbf2X0HfCfNAP2', 'Quyen Dinh Nam',
        'Address 28', '0977123456', 2, 1, NULL),
       ('hoangthicuc@gmail.com', '$2a$10$wSo6R.191TVjMcFSw/x8teZ8.SY76BLp96lnVMSzk.iDAgtUmIDe.', 'Hoang Thi Cuc',
        'Address 19', '0989012346', 3, 1, NULL),
       ('hoangminhtri@gmail.com', '$2a$10$P5esZ.wZFxKXXZACrIdAu.pwGwZrtheFVFYTsRpPdd2ldbP0F9rHy', 'Hoang Minh Tri',
        'Address 29', '0988123456', 3, 1, NULL),
       ('nguyenhaoquang@gmail.com', '$2a$10$mbLv4pILsinIeRDHWYhFo.QChQ1DRzq.GPVj0dqjHi/a/xuqMPut2', 'Nguyen Hao Quang',
        'Address 28', '01628849127', 3, 1, NULL),
       ('tranthib@gmail.com', '$2a$10$3rsfAW5k0E449aL0zaiOEO9GjsLNEOmNb6U6kVtCE7yZ8rk54aXba', 'Tran Thi B', 'Address 3',
        '0923456789', 4, 1, NULL),
       ('ngothianh@gmail.com', '$2a$10$uO8iwtZA3L9ZG2smjmH7COcFGnYpq.lv9r3fitetlMqoLE2wNtRjO', 'Ngo Thi Anh',
        'Address 6', '0956789012', 4, 1, NULL),
       ('nguyenhoang@gmail.com', '$2a$10$A5.xmwG6yFPVjpBFzoWA.ega1BoA79vptTGhJoPLJIawzYcFAEH/S', 'Nguyen Hoang',
        'Address 8', '0978901234', 4, 1, NULL);
INSERT INTO category (category_name)
VALUES ('Smartphones'),('Laptops'),('Accessories');


INSERT INTO products (product_name, description, quantity, category_id, warranty_period, created_at, brand_name)
VALUES
 ('iPhone 15 Pro Max 256GB', 'iPhone 15 Pro Max là một chiếc điện thoại thông minh cao cấp được mong đợi nhất năm 2023. Với nhiều tính năng mới và cải tiến, iPhone 15 Pro Max chắc chắn sẽ là một lựa chọn tuyệt vời cho những người dùng đang tìm kiếm một chiếc điện thoại có hiệu năng mạnh mẽ, camera chất lượng và thiết kế sang trọng.', 111, 1, 12, '2023-07-29', 'Iphone');

INSERT INTO products (product_name, description, quantity, category_id, warranty_period, created_at, brand_name)
VALUES
-- Smartphone Category
('Samsung Galaxy S23 Ultra 512GB', 'Samsung Galaxy S23 Ultra là một chiếc điện thoại thông minh cao cấp với màn hình AMOLED lớn, hiệu năng mạnh mẽ và camera sắc nét.', 50, 1, 12, '2024-01-15', 'Samsung'),
('Sony Xperia 1 IV 256GB', 'Sony Xperia 1 IV mang đến trải nghiệm giải trí tuyệt vời với màn hình 4K và hệ thống camera tiên tiến.', 40, 1, 12, '2024-01-15', 'Sony'),
('iPhone 15 Pro 128GB', 'iPhone 15 Pro mang đến hiệu năng vượt trội với nhiều tính năng mới, phù hợp cho người dùng cao cấp.', 90, 1, 12, '2024-01-15', 'Apple'),
('Samsung Galaxy Z Fold 4 1TB', 'Samsung Galaxy Z Fold 4 là chiếc điện thoại gập cao cấp với màn hình lớn và tính năng đa nhiệm.', 20, 1, 12, '2024-01-15', 'Samsung'),
('iPhone 14 Pro Max 512GB', 'iPhone 14 Pro Max với dung lượng lưu trữ lớn và nhiều cải tiến về camera và hiệu năng.', 80, 1, 12, '2024-01-15', 'Apple'),

-- Laptops Category
('Dell XPS 13 9310', 'Dell XPS 13 với hiệu năng mạnh mẽ và thiết kế mỏng nhẹ, lý tưởng cho người dùng chuyên nghiệp.', 30, 2, 24, '2024-01-15', 'Dell'),
('MacBook Air M2 2023', 'MacBook Air M2 với bộ xử lý mới và thiết kế mỏng nhẹ, mang đến hiệu suất cao và thời lượng pin lâu.', 45, 2, 24, '2024-01-15', 'Apple'),

-- Accessories Category
('Apple AirPods Pro', 'Apple AirPods Pro với tính năng chống ồn chủ động và chất lượng âm thanh tuyệt vời.', 120, 3, 12, '2024-01-15', 'Apple'),
('Sony WH-1000XM4', 'Sony WH-1000XM4 là tai nghe chống ồn chủ động với chất lượng âm thanh cao và khả năng kết nối không dây.', 70, 3, 12, '2024-01-15', 'Sony');

INSERT INTO color(color_name)
VALUES('Xanh'),('Vàng'),('Đen'),('Hồng'),('Trắng');

INSERT INTO productinfo (product_id, image, color_id, quantity, last_updated, price, isdeleted)
VALUES
(1, 'iphone-15-pro-max-blue-1-1.jpg', 1, 55, '2023-07-29', 1999,1),
(2,'samsung-galaxy-s23-ultra-1-1.jpg',3,55,'2023-07-23', 1199,1),
(3,'sony-xperia-1-iv-1-600x600.jpg',3,55,'2024-02-23', 1099,1),
(4,'iphone-15-pro-max-blue-1-1.jpg', 2, 55, '2023-03-29', 1499,1),
(5,'samsung-galaxy-z-fold-3-silver-1-600x600.jpg',4,55,'2023-03-29', 1799,1),
(6,'2023_3_7_638138235534641283_iphone-14-vang-1.jpg',2,44,'2023-03-21', 1299,1),
(7,'dell-xps-13-9310-i7-jgnh61-2-org.jpg',3,54, '2023-05-12', 1499,1),
(8,'macbook-air-15-inch-m2-2023-70w-1.jpg',5, 89, '2023-07-21', 1299,1),
(9, 'tai-nghe-bluetooth-airpods-pro-2nd-gen-usb-c-charge-apple-1.jpg',5,91,'2023-08-12', 249,1),
(10, 'pdp_wh-1000xm4.jpg', 3, 43, '2024-01-29', 349,1);
