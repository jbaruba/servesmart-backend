IF DB_ID(N'servesmart_java_entitie') IS NULL
BEGIN
    CREATE DATABASE servesmart_java_entitie;
END;


USE servesmart_java_entitie;

IF OBJECT_ID('dbo.role', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.role (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        name VARCHAR(20) NOT NULL
    );
END;

IF OBJECT_ID('dbo.users', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        is_active BIT NOT NULL,
        role_id INT NOT NULL,
        create_at DATETIME2(6) NOT NULL,
        phone_number VARCHAR(32) NOT NULL,
        first_name VARCHAR(150) NOT NULL,
        last_name VARCHAR(150) NOT NULL,
        email VARCHAR(256) NOT NULL,
        address VARCHAR(255) NOT NULL,
        password_hash VARCHAR(255) NOT NULL
    );
END;

IF OBJECT_ID('dbo.login_log', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.login_log (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        user_id INT NOT NULL,
        date DATETIME2(6) NOT NULL,
        status VARCHAR(20) NOT NULL
    );
END;

IF OBJECT_ID('dbo.menu_category', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.menu_category (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        is_active BIT NOT NULL,
        position INT NOT NULL,
        name VARCHAR(150) NOT NULL
    );
END;

IF OBJECT_ID('dbo.menu_items', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.menu_items (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        alcohol BIT NOT NULL,
        dairy BIT NOT NULL,
        gluten BIT NOT NULL,
        is_active BIT NOT NULL,
        menu_categories_id INT NOT NULL,
        nuts BIT NOT NULL,
        price NUMERIC(10,2) NOT NULL,
        name VARCHAR(150) NOT NULL,
        description VARCHAR(2000) NULL
    );
END;

IF OBJECT_ID('dbo.orders_status', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.orders_status (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        name VARCHAR(20) NOT NULL
    );
END;

IF OBJECT_ID('dbo.restaurant_table_status', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.restaurant_table_status (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        name VARCHAR(20) NOT NULL
    );
END;

IF OBJECT_ID('dbo.restaurant_table', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.restaurant_table (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        is_active BIT NOT NULL,
        restaurant_table_status_id INT NOT NULL,
        seats INT NOT NULL,
        label VARCHAR(100) NOT NULL
    );
END;

IF OBJECT_ID('dbo.reservation_status', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.reservation_status (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        name VARCHAR(20) NOT NULL
    );
END;

IF OBJECT_ID('dbo.reservation', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.reservation (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        party_size INT NOT NULL,
        reservation_status_id INT NOT NULL,
        restaurant_table_id INT NOT NULL,
        event_datetime DATETIME2(6) NOT NULL,
        phone_number VARCHAR(32) NULL,
        full_name VARCHAR(200) NOT NULL
    );
END;

IF OBJECT_ID('dbo.orders', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.orders (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        orders_status_id INT NOT NULL,
        restaurant_table_id INT NULL,
        user_id INT NOT NULL,
        create_at DATETIME2(6) NOT NULL
    );
END;

IF OBJECT_ID('dbo.order_item', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.order_item (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        is_active BIT NOT NULL,
        items_price NUMERIC(10,2) NOT NULL,
        items_quantity INT NOT NULL,
        menu_items_id INT NOT NULL,
        orders_id INT NOT NULL,
        items_name VARCHAR(150) NOT NULL,
        notes VARCHAR(255) NULL
    );
END;


IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UK_user_email' AND object_id = OBJECT_ID('dbo.users'))
BEGIN
    ALTER TABLE dbo.users ADD CONSTRAINT UK_user_email UNIQUE (email);
END;

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UK_category_name' AND object_id = OBJECT_ID('dbo.menu_category'))
BEGIN
    ALTER TABLE dbo.menu_category ADD CONSTRAINT UK_category_name UNIQUE (name);
END;

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UK_table_label' AND object_id = OBJECT_ID('dbo.restaurant_table'))
BEGIN
    ALTER TABLE dbo.restaurant_table ADD CONSTRAINT UK_table_label UNIQUE (label);
END;


IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK4qu1gr772nnf6ve5af002rwya')
BEGIN
    ALTER TABLE dbo.users
    ADD CONSTRAINT FK4qu1gr772nnf6ve5af002rwya FOREIGN KEY (role_id) REFERENCES dbo.role(id);
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_LoginLog_User')
BEGIN
    ALTER TABLE dbo.login_log
    ADD CONSTRAINT FK_LoginLog_User FOREIGN KEY (user_id) REFERENCES dbo.users(id);
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_MenuItems_Category')
BEGIN
    ALTER TABLE dbo.menu_items
    ADD CONSTRAINT FK_MenuItems_Category FOREIGN KEY (menu_categories_id) REFERENCES dbo.menu_category(id);
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_RestaurantTable_Status')
BEGIN
    ALTER TABLE dbo.restaurant_table
    ADD CONSTRAINT FK_RestaurantTable_Status FOREIGN KEY (restaurant_table_status_id) REFERENCES dbo.restaurant_table_status(id);
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Reservation_Status')
BEGIN
    ALTER TABLE dbo.reservation
    ADD CONSTRAINT FK_Reservation_Status FOREIGN KEY (reservation_status_id) REFERENCES dbo.reservation_status(id);
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Reservation_Table')
BEGIN
    ALTER TABLE dbo.reservation
    ADD CONSTRAINT FK_Reservation_Table FOREIGN KEY (restaurant_table_id) REFERENCES dbo.restaurant_table(id);
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Orders_Status')
BEGIN
    ALTER TABLE dbo.orders
    ADD CONSTRAINT FK_Orders_Status FOREIGN KEY (orders_status_id) REFERENCES dbo.orders_status(id);
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Orders_Table')
BEGIN
    ALTER TABLE dbo.orders
    ADD CONSTRAINT FK_Orders_Table FOREIGN KEY (restaurant_table_id) REFERENCES dbo.restaurant_table(id);
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Orders_User')
BEGIN
    ALTER TABLE dbo.orders
    ADD CONSTRAINT FK_Orders_User FOREIGN KEY (user_id) REFERENCES dbo.users(id);
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_OrderItem_MenuItems')
BEGIN
    ALTER TABLE dbo.order_item
    ADD CONSTRAINT FK_OrderItem_MenuItems FOREIGN KEY (menu_items_id) REFERENCES dbo.menu_items(id);
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_OrderItem_Orders')
BEGIN
    ALTER TABLE dbo.order_item
    ADD CONSTRAINT FK_OrderItem_Orders FOREIGN KEY (orders_id) REFERENCES dbo.orders(id);
END;


IF NOT EXISTS (SELECT 1 FROM dbo.role)
BEGIN
    INSERT INTO dbo.role(name) VALUES ('ADMIN'), ('STAFF');
END;

IF NOT EXISTS (SELECT 1 FROM dbo.restaurant_table_status)
BEGIN
    INSERT INTO dbo.restaurant_table_status(name) VALUES ('AVAILABLE'), ('OCCUPIED'), ('RESERVED');
END;
