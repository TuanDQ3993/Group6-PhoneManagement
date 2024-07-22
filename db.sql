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
