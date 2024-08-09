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
    category_name VARCHAR(100) NOT NULL,
    deleted       BIT
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
    note_technical VARCHAR(255),
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
INSERT INTO category (category_name, deleted)
VALUES ('Smartphones',1),('Laptops',1),('Accessories',1);


INSERT INTO products (product_name, description, quantity, category_id, warranty_period, created_at, brand_name)
VALUES
 ('iPhone 15 Pro Max 256GB', 'iPhone 15 Pro Max is the most anticipated high-end smartphone of 2023. With many new and improved features, iPhone 15 Pro Max will definitely be a great choice for users looking for a phone with powerful performance, quality camera and luxurious design.', 111, 1, 12, '2023-07-29', 'Apple');

INSERT INTO products (product_name, description, quantity, category_id, warranty_period, created_at, brand_name)
VALUES
-- Smartphone Category
('Galaxy S23 Ultra 512GB', 'The Samsung Galaxy S23 Ultra is a premium smartphone with a large AMOLED display, powerful performance, and sharp cameras.', 50, 1, 12, '2024-01-15', 'Samsung'),
('Sony Xperia 1 IV 256GB', 'Sony Xperia 1 IV delivers an immersive entertainment experience with a 4K display and advanced camera system.', 40, 1, 12, '2024-01-15', 'Sony'),
('iPhone 15 Pro 128GB', 'iPhone 15 Pro delivers superior performance with many new features, suitable for high-end users.', 90, 1, 12, '2024-01-15', 'Apple'),
('Galaxy Z Fold 4 1TB', 'The Samsung Galaxy Z Fold 4 is a premium foldable phone with a large screen and multitasking features.', 20, 1, 12, '2024-01-15', 'Samsung'),
('iPhone 14 Pro Max 512GB', 'iPhone 14 Pro Max with massive storage and lots of camera and performance improvements.', 80, 1, 12, '2024-01-15', 'Apple'),
('iPhone 13 Pro Max 128GB', 'The iPhone 13 Pro Max will certainly be the most anticipated high-end smartphone in 2021. The iPhone 13 series was launched on September 14, 2021 at the "California Streaming" event held online similar to last year, along with 3 other versions: iPhone 13, 13 mini and 13 Pro. So how much does the 13 Pro Max cost? What special Let find out now!', 55, 1, 24, '2024-01-15', 'Apple'),
('iPhone 14 Pro Max 128GB', 'iPhone 14 Pro Max with massive storage and lots of camera and performance improvements.', 80, 1, 12, '2024-01-15', 'Apple'),
('Samsung Galaxy Z Fold6', 'The Samsung Galaxy Z Fold 6 is a premium foldable phone with a large screen and multitasking features.', 20, 1, 12, '2024-01-15', 'Samsung'),
('Samsung Galaxy A15 LTE 8GB 128GB', 'Samsung Galaxy A15 LTE is equipped with high-end Super AMOLED screen technology, with FHD + resolution of 1080 x 2340 pixels. The products large 6.5-inch screen combined with a fast scan frequency, limits screen flickering or blurring when used. This new phone of the Samsung Galaxy A series also has a sharp camera cluster and a large memory of 8B + 128GB for unlimited use.', 20, 1, 12, '2024-01-15', 'Samsung'),
('Samsung Galaxy S23 FE 8GB 256GB', 'Samsung Galaxy S23 is equipped with high-end Super AMOLED screen technology, with FHD + resolution of 1080 x 2340 pixels. The products large 6.5-inch screen combined with a fast scan frequency, limits screen flickering or blurring when used. This new phone of the Samsung Galaxy A series also has a sharp camera cluster and a large memory of 8B + 128GB for unlimited use.', 20, 1, 12, '2024-01-15', 'Samsung'),
('Samsung Galaxy S24 Ultra 12GB 1TB', 'Samsung Galaxy S24 Ultra 12GB 1TB premium with big screen and multitasking features.', 20, 1, 12, '2024-01-15', 'Samsung'),
('Điện thoại Sony Xperia 10VI 8GB 128GB', 'Sony Xperia 10 VI is equipped with Adreno GPU graphics card combined with high-end Snapdragon 6 Gen 1 chipset to bring powerful performance when used. 8GB RAM + 128GB ROM storage capacity allows downloading many applications or images to the device. The rear camera up to 48 MP and the front camera 8 MP support the ability to take clearer and more impressive photos. Sony Xperia 10 VI phone is equipped with a 5000 mAh battery capacity, helping to perform various activities without worrying about running out of battery quickly.', 40, 1, 12, '2024-01-15', 'Sony'),

-- Laptops Category
('Dell XPS 13 9310', 'Dell XPS 13 with powerful performance and thin and light design, ideal for professional users.', 30, 2, 24, '2024-01-15', 'Dell'),
('MacBook Air M2 2023', 'MacBook Air M2 with new processor and thin and light design, brings high performance and long battery life.', 45, 2, 24, '2024-01-15', 'Apple'),
('MacBook Pro 16 inch M1 Max 10 CPU - 32 GPU 32GB 1TB 2021', 'Not only a recognizable feature on smartphones, the rabbit ears have now appeared on the latest generation of Macbooks. Macbook Pro M1 Max with a unique design, quality screen brings a superior experience. The 2021 16-inch Macbook Pro computer is equipped with an extremely powerful configuration with the Apple M1 Max chip with 10CPU, 32GPU with a capacity of up to 32GB RAM and 1TB SSD memory for outstanding performance.', 45, 2, 24, '2024-01-15', 'Apple'),


-- Accessories Category
('Apple AirPods Pro', 'Apple AirPods Pro với tính năng chống ồn chủ động và chất lượng âm thanh tuyệt vời.', 120, 3, 12, '2024-01-15', 'Apple'),
('Sony WH-1000XM4', 'Sony WH-1000XM4 là tai nghe chống ồn chủ động với chất lượng âm thanh cao và khả năng kết nối không dây.', 70, 3, 12, '2024-01-15', 'Sony');

INSERT INTO color(color_name)
VALUES('Xanh'),('Vàng'),('Đen'),('Hồng'),('Trắng'),('Bạc');

INSERT INTO productinfo (product_id, image, color_id, quantity, last_updated, price, isdeleted)
VALUES
(1, 'iphone-15-pro-max-blue-1-1.jpg', 1, 55, '2023-07-29', 1999,1),
(2,'samsung-galaxy-s23-ultra-1-1.jpg',3,55,'2023-07-23', 1199,1),
(3,'sony-xperia-1-iv-1-600x600.jpg',3,55,'2024-02-23', 1099,1),
(4,'iphone-15-pro-max-blue-1-1.jpg', 2, 55, '2023-03-29', 1499,1),
(5,'samsung-galaxy-z-fold-3-silver-1-600x600.jpg',4,55,'2023-03-29', 1799,1),
(6,'2023_3_7_638138235534641283_iphone-14-vang-1.jpg',2,44,'2023-03-21', 1299,1),
(14,'dell-xps-13-9310-i7-jgnh61-2-org.jpg',3,54, '2023-05-12', 1499,1),
(15,'macbook-air-15-inch-m2-2023-70w-1.jpg',5, 89, '2023-07-21', 1299,1),
(17, 'tai-nghe-bluetooth-airpods-pro-2nd-gen-usb-c-charge-apple-1.jpg',5,91,'2023-08-12', 249,1),
(18, 'pdp_wh-1000xm4.jpg', 3, 43, '2024-01-29', 349,1),
(7, 'iphone-13-pro-max.jpg', 1, 55, '2024-01-29', 2000, 1),
(7, '4_36_3_2_1_9.jpg', 5, 55, '2024-01-29', 2000, 1),
(7, '2_61_8_2_1_12.jpg', 3, 55, '2024-01-29', 1999, 1),
(8, 'v_ng_18.jpg', 2, 55, '2024-01-29', 2199, 1),
(8, 'b_c_1_9.jpg', 2, 55, '2024-01-29', 2000, 1),
(9, 'samsung-galaxy-z-fold-6-xanh_5_.jpg', 1, 55, '2024-01-29', 3999, 1),
(9, 'image_1171.jpg', 4, 55, '2024-01-29', 3999, 1),
(10, 'galaxy-a15-vang.jpg', 2, 55, '2024-01-29', 1599, 1),
(10, 'galaxy-a15-den.jpg', 2, 55, '2024-01-29', 1599, 1),
(16, 'macbook-pro-2021-004_3.jpg', 5, 55, '2024-01-29', 3999, 1),
(16, 'macbook-pro-2021-001_3.jpg', 6, 55, '2024-01-29', 3999, 1),
(11, 'samsung-s23-fe_1.jpg', 1, 55, '2024-01-29', 899, 1),
(11, 'samsung-galaxy-s23-fe_16__2.jpg', 3, 55, '2024-01-29', 899, 1),
(12, 'samsung-galaxy-s24-ultra_10__2.jpg', 1, 55, '2024-01-29', 899, 1),
(12, 'ss-s24-ultra-xam-222_2.jpg', 6, 55, '2024-01-29', 899, 1),
(13, 'dien-thoai-sony-xperia-10-vi_1_.jpg', 3, 55, '2024-01-29', 599, 1),
(13, 'dien-thoai-sony-xperia-10-vi.jpg', 1, 55, '2024-01-29', 599, 1);
INSERT INTO warrantyrepair (product_name, image, status, user_id, technical_id, issue_description, is_deleted, repair_date, type, order_id)
VALUES 
('iPhone 15 Pro Max 256GB', 'iphone-15-pro-max-blue-1-1.jpg', 'Pending', 9, 6, 'Screen not responding', 0, '2024-01-10', 'Warranty', 1),
('Galaxy S23 Ultra 512GB', 'samsung-galaxy-s23-ultra-1-1.jpg', 'In Progress', 10, 7, 'Battery draining quickly', 0, '2024-02-05', 'Repair', 2),
('Sony Xperia 1 IV 256GB', 'sony-xperia-1-iv-1-600x600.jpg', 'Completed', 11, 8, 'Camera malfunctioning', 0, '2024-02-20', 'Warranty', 3),
('iPhone 15 Pro 128GB', 'iphone-15-pro-max-blue-1-1.jpg', 'Pending', 9, 6, 'Touchscreen issue', 0, '2024-03-15', 'Repair', 4),
('Galaxy Z Fold 4 1TB', 'samsung-galaxy-z-fold-3-silver-1-600x600.jpg', 'In Progress', 10, 7, 'Hinge problem', 0, '2024-03-25', 'Warranty', 5),
('iPhone 14 Pro Max 512GB', '2023_3_7_638138235534641283_iphone-14-vang-1.jpg', 'Completed', 11, 8, 'Battery not charging', 0, '2024-04-01', 'Repair', 6);
