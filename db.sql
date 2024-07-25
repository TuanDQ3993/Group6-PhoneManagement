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
CREATE TABLE product (
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
    FOREIGN KEY (product_id) REFERENCES product(product_id),
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
    product_id INT,
    user_id INT,   -- Nguoi can sua
    technical_id INT, -- Nguoi sua
    issue_description VARCHAR(255),
    repair_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50),
    FOREIGN KEY (product_id) REFERENCES product(product_id),
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
    product_id INT,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (purchase_id) REFERENCES purchase(purchase_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
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

INSERT INTO category (category_name) VALUES ('Smartphones');
INSERT INTO category (category_name) VALUES ('Accessories');

INSERT INTO product (product_name, description, quantity, category_id, price, warranty_period)
VALUES 
('iPhone 13', 'Apple iPhone 13 with 128GB storage', 50, 1, 23000000, 24),
('Samsung Galaxy S21', 'Samsung Galaxy S21 with 256GB storage', 30, 1, 21000000, 24),
('Google Pixel 6', 'Google Pixel 6 with 128GB storage', 20, 1, 18000000, 24);

INSERT INTO color (color_name) VALUES ('Black');
INSERT INTO color (color_name) VALUES ('White');
INSERT INTO color (color_name) VALUES ('Blue');

INSERT INTO productcolor (product_id, image, color_id, quantity)
VALUES 
(1, 'https://electronicparadise.in/cdn/shop/files/Apple13black.jpg?v=1702471683&width=1146', 1, 25),
(1, 'https://m.media-amazon.com/images/I/61NTwRtdzfL._AC_UF1000,1000_QL80_.jpg', 2, 25),
(2, 'https://images.samsung.com/is/image/samsung/p6pim/uk/sm5g996bzkdeub/gallery/uk-galaxy-s21-5g-g996-485744-sm5g996bzkdeub-539103375?$650_519_PNG$', 1, 15),
(2, 'https://i.insider.com/603eb1f3b46d720018b04788?width=700', 3, 15),
(3, 'https://www.getorchard.com/cdn/shop/products/pixel6-black-generic_82fe6663-d632-4bef-9053-4a129d8d184c.png?v=1706299522&width=1445', 1, 10),
(3, 'https://m.media-amazon.com/images/I/81L2V8RIBPL.jpg', 2, 10);


INSERT INTO orders (user_id, saler_id, total_amount, status)
VALUES 
(10, 2, 44000000, 'Completed'),
(12, 3, 21000000, 'Completed'),
(1, 14, 25000000, 'Completed'),
(2, 14, 32000000, 'Cancelled'),
(3, 14, 15000000, 'Completed'),
(4, 14, 40000000, 'Cancelled'),
(5, 14, 27000000, 'Completed'),
(6, 14, 33000000, 'Cancelled'),
(7, 14, 19000000, 'Completed'),
(8, 14, 22000000, 'Cancelled'),
(9, 14, 28000000, 'Completed'),
(10, 14, 35000000, 'Cancelled');



INSERT INTO orderdetail (order_id, product_color_id, quantity, price)
VALUES 
(1, 1, 1, 23000000), -- iPhone 13 Black
(1, 3, 1, 21000000), -- Samsung Galaxy S21 Black
(3, 1, 1, 23000000), -- Product 1, Color 1
(3, 2, 1, 23000000), -- Product 2, Color 2
(4, 3, 1, 21000000), -- Product 3, Color 3
(4, 4, 1, 21000000), -- Product 4, Color 4
(5, 5, 1, 18000000), -- Product 5, Color 5
(5, 6, 1, 18000000), -- Product 6, Color 6
(6, 1, 1, 23000000), -- Product 1, Color 1
(6, 2, 1, 23000000), -- Product 2, Color 2
(7, 3, 1, 21000000), -- Product 3, Color 3
(7, 4, 1, 21000000), -- Product 4, Color 4
(8, 5, 1, 18000000), -- Product 5, Color 5
(8, 6, 1, 18000000), -- Product 6, Color 6
(9, 1, 1, 23000000), -- Product 1, Color 1
(9, 2, 1, 23000000), -- Product 2, Color 2
(10, 3, 1, 21000000), -- Product 3, Color 3
(10, 4, 1, 21000000), -- Product 4, Color 4
(11, 5, 1, 18000000), -- Product 5, Color 5
(11, 6, 1, 18000000), -- Product 6, Color 6
(12, 1, 1, 23000000), -- Product 1, Color 1
(12, 2, 1, 23000000); -- Product 2, Color 2




WITH OrderedOrders AS (
    SELECT 
        o.order_id AS OrderID,
        p.product_name AS ProductName,
        pc.image AS Image,
        o.total_amount AS TotalAmount,
        o.order_date AS OrderDate,
        u.fullname AS Username,
        ROW_NUMBER() OVER (PARTITION BY o.order_id ORDER BY od.order_detail_id) AS rn,
        (SELECT COUNT(*) FROM orderdetail WHERE order_id = o.order_id) - 1 AS countP
    FROM 
        orders o
    JOIN 
        useraccount u ON o.user_id = u.user_id
    JOIN 
        orderdetail od ON o.order_id = od.order_id
    JOIN 
        productcolor pc ON od.product_color_id = pc.product_color_id
    JOIN 
        product p ON pc.product_id = p.product_id
    Where o.saler_id =2
)
SELECT 
    OrderID,
    ProductName,
    Image,
    TotalAmount,
    OrderDate,
    Username,
    countP
FROM 
    OrderedOrders
WHERE 
    rn = 1;
    
    
select od.order_id,p.product_id,p.product_name,pc.image,od.quantity,od.price 
from orderdetail od 
join productcolor pc on pc.product_color_id=od.product_color_id
join product p on p.product_id =pc.product_id
where od.order_id=1

SELECT o.total_amount, o.order_date, ua.address, ua.fullname, ua.phone_number
FROM orders o
JOIN useraccount ua ON ua.user_id = o.user_id
WHERE o.order_id = 1







