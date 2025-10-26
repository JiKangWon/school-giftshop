CREATE DATABASE school_giftshop;
GO

USE school_giftshop;
GO

-- Bảng addresses
CREATE TABLE dbo.addresses (
    id INT IDENTITY(10000000,1) NOT NULL PRIMARY KEY,
    country NVARCHAR(100) NOT NULL,
    province NVARCHAR(100) NULL,
    district NVARCHAR(100) NULL,
    ward NVARCHAR(100) NULL,
    street NVARCHAR(255) NULL
);
GO

-- Bảng users (dùng cho buyer và seller)
CREATE TABLE dbo.users (
    id BIGINT IDENTITY(10000000,1) NOT NULL PRIMARY KEY,
    userName NVARCHAR(100) NOT NULL,
    password NVARCHAR(255) NOT NULL,
    balance DECIMAL(18,2) NOT NULL DEFAULT(0.00),
    name NVARCHAR(200) NULL,
    address_id INT NULL,                  -- FK -> addresses(id)
    addressNumber NVARCHAR(50) NULL,
    phone NVARCHAR(30) NULL,
    createdAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    isSeller BIT NOT NULL DEFAULT(0)
);
-- unique userName
CREATE UNIQUE INDEX UX_users_userName ON dbo.users(userName);
-- FK constraint to addresses
ALTER TABLE dbo.users
    ADD CONSTRAINT FK_users_addresses FOREIGN KEY(address_id) REFERENCES dbo.addresses(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL;
GO

-- Bảng categories
CREATE TABLE dbo.categories (
    id INT IDENTITY(10000000,1) NOT NULL PRIMARY KEY,
    name NVARCHAR(150) NOT NULL
);
CREATE UNIQUE INDEX UX_categories_name ON dbo.categories(name);
GO

-- Bảng products
CREATE TABLE dbo.products (
    id BIGINT IDENTITY(10000000,1) NOT NULL PRIMARY KEY,
    stock INT NOT NULL DEFAULT(0),
    price DECIMAL(18,2) NOT NULL DEFAULT(0.00),
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX) NULL,
    category_id INT NULL,                  -- FK -> categories(id)
    status NVARCHAR(50) NOT NULL DEFAULT('active'),
    createdAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);
ALTER TABLE dbo.products
    ADD CONSTRAINT FK_products_categories FOREIGN KEY(category_id) REFERENCES dbo.categories(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL;
GO

-- Bảng product_images
CREATE TABLE dbo.product_images (
    id BIGINT IDENTITY(10000000,1) NOT NULL PRIMARY KEY,
    product_id BIGINT NOT NULL,            -- FK -> products(id)
    imgLink NVARCHAR(1000) NOT NULL
);
ALTER TABLE dbo.product_images
    ADD CONSTRAINT FK_product_images_products FOREIGN KEY(product_id) REFERENCES dbo.products(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;
GO

-- Bảng orders
CREATE TABLE dbo.orders (
    id BIGINT IDENTITY(10000000,1) NOT NULL PRIMARY KEY,
    user_id BIGINT NOT NULL,               -- FK -> users(id)
    createdAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    status NVARCHAR(50) NOT NULL DEFAULT('pending'),
    total_amount DECIMAL(18,2) NOT NULL DEFAULT(0.00)
);
ALTER TABLE dbo.orders
    ADD CONSTRAINT FK_orders_users FOREIGN KEY(user_id) REFERENCES dbo.users(id)
    ON UPDATE CASCADE
    ON DELETE NO ACTION;
GO

-- Bảng order_products (chi tiết đơn hàng)
CREATE TABLE dbo.order_products (
    id BIGINT IDENTITY(10000000,1) NOT NULL PRIMARY KEY,
    order_id BIGINT NOT NULL,              -- FK -> orders(id)
    product_id BIGINT NOT NULL,            -- FK -> products(id)
    quantity INT NOT NULL DEFAULT(1),
    review NVARCHAR(MAX) NULL,
    receivedAt DATETIME2 NULL,
    currentLocation NVARCHAR(255) NULL
);
ALTER TABLE dbo.order_products
    ADD CONSTRAINT FK_order_products_orders FOREIGN KEY(order_id) REFERENCES dbo.orders(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;
ALTER TABLE dbo.order_products
    ADD CONSTRAINT FK_order_products_products FOREIGN KEY(product_id) REFERENCES dbo.products(id)
    ON UPDATE CASCADE
    ON DELETE NO ACTION;
GO

-- Bảng carts (Giỏ hàng) - composite PK (user, product)
CREATE TABLE dbo.carts (
    user_id BIGINT NOT NULL,               -- FK -> users(id)
    product_id BIGINT NOT NULL,            -- FK -> products(id)
    quantity INT NOT NULL DEFAULT(1),
    createdAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT PK_carts PRIMARY KEY (user_id, product_id)
);
ALTER TABLE dbo.carts
    ADD CONSTRAINT FK_carts_users FOREIGN KEY(user_id) REFERENCES dbo.users(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;
ALTER TABLE dbo.carts
    ADD CONSTRAINT FK_carts_products FOREIGN KEY(product_id) REFERENCES dbo.products(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;
GO

-- Bảng favorite_lists (Danh sách yêu thích) - composite PK
CREATE TABLE dbo.favorite_lists (
    user_id BIGINT NOT NULL,               -- FK -> users(id)
    product_id BIGINT NOT NULL,            -- FK -> products(id)
    createdAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT PK_favorite_lists PRIMARY KEY (user_id, product_id)
);
ALTER TABLE dbo.favorite_lists
    ADD CONSTRAINT FK_fav_users FOREIGN KEY(user_id) REFERENCES dbo.users(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;
ALTER TABLE dbo.favorite_lists
    ADD CONSTRAINT FK_fav_products FOREIGN KEY(product_id) REFERENCES dbo.products(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;
GO

-- Bảng adjacent_addresses (khoảng cách giữa 2 address)
CREATE TABLE dbo.adjacent_addresses (
    address1_id INT NOT NULL,
    address2_id INT NOT NULL,
    distance FLOAT NOT NULL,               -- khoảng cách (ví dụ km)
    CONSTRAINT PK_adjacent_addresses PRIMARY KEY (address1_id, address2_id)
)
GO


ALTER TABLE dbo.adjacent_addresses
    ADD CONSTRAINT CK_adjacent_no_self CHECK (address1_id <> address2_id);
GO
ALTER TABLE dbo.adjacent_addresses
    ADD CONSTRAINT FK_adj_addr1 FOREIGN KEY(address1_id) REFERENCES dbo.addresses(id) ON DELETE NO ACTION;
GO
ALTER TABLE dbo.adjacent_addresses
    ADD CONSTRAINT FK_adj_addr2 FOREIGN KEY(address2_id) REFERENCES dbo.addresses(id) ON DELETE NO ACTION;
GO
CREATE NONCLUSTERED INDEX IX_adj_address1 ON dbo.adjacent_addresses(address1_id);
CREATE NONCLUSTERED INDEX IX_adj_address2 ON dbo.adjacent_addresses(address2_id);
GO

CREATE TRIGGER dbo.trg_addresses_after_delete
ON dbo.addresses
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;

    DELETE aa
    FROM dbo.adjacent_addresses aa
    INNER JOIN deleted d
        ON aa.address1_id = d.id OR aa.address2_id = d.id;
END;
GO
 
SET IDENTITY_INSERT dbo.addresses ON;
INSERT INTO dbo.addresses (id, country, province, district, ward, street)
VALUES
 (10000000,N'Việt Nam',N'TP. Hồ Chí Minh',N'Quận 1',N'Phường Bến Nghé',N'123 Lê Lợi'),
 (10000001,N'Việt Nam',N'TP. Hồ Chí Minh',N'Quận 7',N'Phường Tân Phong',N'77 Nguyễn Văn Linh'),
 (10000002,N'Việt Nam',N'TP. Hồ Chí Minh',N'Thủ Đức',N'Phường Linh Trung',N'9 Phạm Văn Đồng'),
 (10000003,N'Việt Nam',N'Hà Nội',N'Quận Ba Đình',N'Phường Điện Biên',N'45 Độc Lập'),
 (10000004,N'Việt Nam',N'Hà Nội',N'Quận Hoàn Kiếm',N'Phường Hàng Bạc',N'12 Hàng Bạc'),
 (10000005,N'Việt Nam',N'Da Nang',N'Quận Hải Châu',N'Phường Thạch Thang',N'8 Bạch Đằng'),
 (10000006,N'Việt Nam',N'Đà Nẵng',N'Quận Sơn Trà',N'Phường Mân Thái',N'200 Võ Nguyên Giáp'),
 (10000007,N'Việt Nam',N'An Giang',N'TP. Long Xuyên',N'Phường Mỹ Long',N'5 Nguyễn Huệ'),
 (10000008,N'Việt Nam',N'Bình Dương',N'Thủ Dầu Một',N'Phường Phú Cường',N'10 Đại lộ Bình Dương'),
 (10000009,N'Việt Nam',N'Đồng Nai',N'Biên Hòa',N'Phường Tân Hiệp',N'33 Nguyễn Ái Quốc'),
 (10000010,N'Việt Nam',N'Hải Phòng',N'Quận Ngô Quyền',N'Phường Máy Tơ',N'66 Lê Lợi'),
 (10000011,N'Việt Nam',N'Khánh Hòa',N'TP. Nha Trang',N'Phường Vạn Thạnh',N'9 Trần Phú'),
 (10000012,N'Việt Nam',N'Phú Yên',N'TP. Tuy Hòa',N'Phường 1',N'17 Nguyễn Huệ'),
 (10000013,N'Việt Nam',N'Quảng Nam',N'TP. Tam Kỳ',N'Phường An Mỹ',N'88 Trần Hưng Đạo'),
 (10000014,N'Việt Nam',N'Quảng Ninh',N'TP. Hạ Long',N'Phường Hồng Gai',N'3 Hạ Long'),
 (10000015,N'Việt Nam',N'Lâm Đồng',N'Bảo Lộc',N'Phường Lộc Phát',N'55 Lý Thường Kiệt'),
 (10000016,N'Việt Nam',N'Kiên Giang',N'Rạch Giá',N'Phường An Hòa',N'24 Nguyễn Trung Trực'),
 (10000017,N'Việt Nam',N'Sóc Trăng',N'TP. Sóc Trăng',N'Phường 1',N'11 Trần Hưng Đạo'),
 (10000018,N'Việt Nam',N'Bến Tre',N'Bến Tre',N'Phường Phú Khương',N'7 Nguyễn Đình Chiểu'),
 (10000019,N'Việt Nam',N'Long An',N'TP. Tân An',N'Phường 3',N'2 Lê Lợi');
SET IDENTITY_INSERT dbo.addresses OFF;
GO

/********************************************************************************
  2) adjacent_addresses (30 bản ghi) - đảm bảo address1_id < address2_id
*********************************************************************************/
INSERT INTO dbo.adjacent_addresses(address1_id, address2_id, distance)
VALUES
 (10000000,10000001,5.2),(10000000,10000002,12.7),(10000000,10000003,175.0),
 (10000001,10000002,9.5),(10000001,10000004,170.1),(10000002,10000005,640.0),
 (10000003,10000004,2.1),(10000003,10000005,650.3),(10000004,10000005,648.0),
 (10000005,10000006,6.5),(10000006,10000007,1200.0),(10000007,10000008,220.0),
 (10000008,10000009,60.0),(10000009,10000010,240.0),(10000010,10000011,400.0),
 (10000011,10000012,45.0),(10000012,10000013,120.0),(10000013,10000014,250.5),
 (10000014,10000015,1000.0),(10000015,10000016,400.0),(10000016,10000017,350.0),
 (10000017,10000018,180.0),(10000018,10000019,150.0),(10000005,10000010,750.0),
 (10000002,10000004,680.0),(10000001,10000011,1800.0),(10000008,10000015,900.0),
 (10000006,10000009,340.0),(10000003,10000008,1200.0),(10000012,10000019,420.0);
GO

/********************************************************************************
  3) users (20 bản ghi)
*********************************************************************************/
SET IDENTITY_INSERT dbo.users ON;
INSERT INTO dbo.users (id, userName, password, balance, name, address_id, addressNumber, phone, createdAt, isSeller)
VALUES
 (10000000,N'seller_shop',N'Seller!2025',5000000.00,N'Trường Giftshop',10000000,N'Kiosk 1',N'0900000000','2025-01-01 08:00:00',1),
 (10000001,N'buyer_anna',N'Anna@1234',150000.00,N'Anna Nguyen',10000001,N'77/1',N'0911111111','2025-02-05 09:10:00',0),
 (10000002,N'buyer_binh',N'Binh@1234',250000.00,N'Binh Tran',10000002,N'9B',N'0922222222','2025-02-10 10:20:00',0),
 (10000003,N'buyer_chi',N'Chi#2025',50000.00,N'Chi Le',10000003,N'45',N'0933333333','2025-03-12 11:30:00',0),
 (10000004,N'buyer_duy',N'Duy$123',800000.00,N'Duy Vu',10000004,N'12A',N'0944444444','2025-03-20 12:00:00',0),
 (10000005,N'buyer_hoa',N'Hoa%123',120000.00,N'Hoa Pham',10000005,N'8',N'0955555555','2025-04-01 14:00:00',0),
 (10000006,N'buyer_kien',N'Kien*123',300000.00,N'Kien Hoang',10000006,N'200',N'0966666666','2025-04-05 15:00:00',0),
 (10000007,N'buyer_linh',N'Linh!234',450000.00,N'Linh Do',10000007,N'5',N'0977777777','2025-05-01 16:00:00',0),
 (10000008,N'buyer_minh',N'Minh^123',600000.00,N'Minh Phu',10000008,N'10',N'0988888888','2025-05-05 17:00:00',0),
 (10000009,N'buyer_nam',N'Nam&123',200000.00,N'Nam Ngo',10000009,N'33',N'0999999999','2025-05-10 18:00:00',0),
 (10000010,N'buyer_oanh',N'Oanh(123',90000.00,N'Oanh Tran',10000010,N'66',N'0912345010','2025-06-01 09:00:00',0),
 (10000011,N'buyer_phuc',N'Phuc)123',180000.00,N'Phuc Nguyen',10000011,N'9',N'0912345011','2025-06-10 10:00:00',0),
 (10000012,N'buyer_quang',N'Quang+123',350000.00,N'Quang Le',10000012,N'17',N'0912345012','2025-06-20 11:00:00',0),
 (10000013,N'buyer_hang',N'Hang=123',470000.00,N'Hang Pham',10000013,N'88',N'0912345013','2025-07-01 12:00:00',0),
 (10000014,N'buyer_trung',N'Trung?123',120000.00,N'Trung Do',10000014,N'3',N'0912345014','2025-07-05 13:00:00',0),
 (10000015,N'buyer_hoa2',N'Hoa2!123',900000.00,N'Hoa Vu',10000015,N'55',N'0912345015','2025-07-10 14:00:00',0),
 (10000016,N'buyer_hieu',N'Hieu~123',220000.00,N'Hieu Pham',10000016,N'24',N'0912345016','2025-07-20 15:00:00',0),
 (10000017,N'buyer_kim',N'Kim%123',330000.00,N'Kim Tran',10000017,N'11',N'0912345017','2025-08-01 16:00:00',0),
 (10000018,N'buyer_lam',N'Lam@1234',140000.00,N'Lam Ngo',10000018,N'7',N'0912345018','2025-08-05 17:00:00',0),
 (10000019,N'buyer_hung',N'Hung$123',510000.00,N'Hung Le',10000019,N'2',N'0912345019','2025-08-10 18:00:00',0);
SET IDENTITY_INSERT dbo.users OFF;
GO

/********************************************************************************
  4) categories (8 bản ghi)
*********************************************************************************/
SET IDENTITY_INSERT dbo.categories ON;
INSERT INTO dbo.categories (id, name)
VALUES
 (10000000,N'Apparel'),
 (10000001,N'Uniforms'),
 (10000002,N'Books'),
 (10000003,N'Stationery'),
 (10000004,N'Accessories'),
 (10000005,N'Hoodies'),
 (10000006,N'Mugs'),
 (10000007,N'Bags');
SET IDENTITY_INSERT dbo.categories OFF;
GO

/********************************************************************************
  5) products (30 bản ghi)
*********************************************************************************/
SET IDENTITY_INSERT dbo.products ON;
INSERT INTO dbo.products (id, stock, price, name, description, category_id, status, createdAt)
VALUES
 (10000000,100,120000.00,N'T-shirt - School Logo (M)',N'Cotton 100% - M',10000000,N'active','2025-07-01 08:00:00'),
 (10000001,80,140000.00,N'T-shirt - School Logo (L)',N'Cotton 100% - L',10000000,N'active','2025-07-01 08:05:00'),
 (10000002,50,250000.00,N'Uniform - Blue Set (S)',N'Đồng phục thể dục - S',10000001,N'active','2025-06-15 09:00:00'),
 (10000003,60,250000.00,N'Uniform - Blue Set (M)',N'Đồng phục thể dục - M',10000001,N'active','2025-06-15 09:05:00'),
 (10000004,40,250000.00,N'Uniform - Blue Set (L)',N'Đồng phục thể dục - L',10000001,N'active','2025-06-15 09:10:00'),
 (10000005,30,120000.00,N'Intro to Computer Science',N'Sách giáo trình nhập môn CNTT',10000002,N'active','2025-05-20 10:00:00'),
 (10000006,150,18000.00,N'Notebook - 100 pages',N'Sổ tay 100 trang',10000003,N'active','2025-05-25 11:00:00'),
 (10000007,120,22000.00,N'Notebook - 200 pages',N'Sổ tay 200 trang',10000003,N'active','2025-05-25 11:05:00'),
 (10000008,40,350000.00,N'Hoodie - Campus (M)',N'Hoodie in logo - M',10000005,N'active','2025-06-01 12:00:00'),
 (10000009,30,350000.00,N'Hoodie - Campus (L)',N'Hoodie in logo - L',10000005,N'active','2025-06-01 12:05:00'),
 (10000010,200,60000.00,N'Stationery Set',N'Bút + Thước + Gôm',10000003,N'active','2025-05-10 13:00:00'),
 (10000011,90,40000.00,N'Pen - Gel',N'Bút gel 0.5mm',10000003,N'active','2025-05-11 13:10:00'),
 (10000012,70,80000.00,N'Backpack - School',N'Balo vải - 20L',10000007,N'active','2025-04-20 14:00:00'),
 (10000013,160,50000.00,N'Mug - Campus',N'Ly sứ in logo',10000006,N'active','2025-03-30 09:00:00'),
 (10000014,25,90000.00,N'Keychain - Logo',N'Móc khoá kim loại',10000004,N'active','2025-02-28 10:00:00'),
 (10000015,80,45000.00,N'Bookmark - Leather',N'Bookmark da',10000004,N'active','2025-03-05 11:00:00'),
 (10000016,60,170000.00,N'Jacket - Campus',N'Áo khoác',10000000,N'active','2025-01-10 08:00:00'),
 (10000017,90,22000.00,N'Pencil - HB',N'Chì HB',10000003,N'active','2025-02-01 09:00:00'),
 (10000018,45,300000.00,N'Limited Hoodie - Anniversary',N'Phiên bản kỷ niệm',10000005,N'active','2025-07-20 08:00:00'),
 (10000019,200,15000.00,N'Postcard - Campus',N'Card in cảnh trường',10000004,N'active','2025-03-15 10:00:00'),
 (10000020,75,180000.00,N'Canvas Bag - Logo',N'Túi vải có in logo',10000007,N'active','2025-04-02 12:00:00'),
 (10000021,120,5000.00,N'Sticker Set',N'Set sticker 10 miếng',10000004,N'active','2025-04-05 12:30:00'),
 (10000022,40,120000.00,N'Calculator - Basic',N'Máy tính cơ bản',10000003,N'active','2025-05-01 09:00:00'),
 (10000023,15,450000.00,N'Special Jacket - Signed',N'Áo khoác có chữ ký',10000000,N'active','2025-07-30 08:00:00'),
 (10000024,300,15000.00,N'Eraser',N'Gôm tẩy',10000003,N'active','2025-02-10 09:00:00'),
 (10000025,90,220000.00,N'Premium Backpack',N'Balo chất liệu cao cấp',10000007,N'active','2025-03-01 10:00:00'),
 (10000026,40,70000.00,N'Cap - Campus',N'Nón lưỡi trai',10000000,N'active','2025-04-12 11:00:00'),
 (10000027,55,25000.00,N'Badge - Metal',N'Huy hiệu kim loại',10000004,N'active','2025-05-15 12:00:00'),
 (10000028,85,32000.00,N'Scarf - Winter',N'Khăn quàng',10000000,N'active','2025-06-30 13:00:00'),
 (10000029,110,9000.00,N'Ribbon - Event',N'Reng trang trí',10000004,N'active','2025-01-20 09:00:00');
SET IDENTITY_INSERT dbo.products OFF;
GO

/********************************************************************************
  6) orders (30 bản ghi) - tạm total_amount = 0, sẽ cập nhật sau
*********************************************************************************/
SET IDENTITY_INSERT dbo.orders ON;
INSERT INTO dbo.orders (id, user_id, createdAt, status, total_amount)
VALUES
 (10000000,10000001,'2025-08-01 10:00:00',N'completed',0.00),
 (10000001,10000002,'2025-08-02 11:00:00',N'completed',0.00),
 (10000002,10000003,'2025-08-03 12:30:00',N'completed',0.00),
 (10000003,10000004,'2025-08-04 13:00:00',N'processing',0.00),
 (10000004,10000005,'2025-08-05 14:00:00',N'pending',0.00),
 (10000005,10000006,'2025-08-06 15:10:00',N'completed',0.00),
 (10000006,10000007,'2025-08-07 16:20:00',N'completed',0.00),
 (10000007,10000008,'2025-08-08 09:30:00',N'processing',0.00),
 (10000008,10000009,'2025-08-09 10:40:00',N'completed',0.00),
 (10000009,10000010,'2025-08-10 11:50:00',N'completed',0.00),
 (10000010,10000011,'2025-08-11 12:30:00',N'returned',0.00),
 (10000011,10000012,'2025-08-12 13:30:00',N'completed',0.00),
 (10000012,10000013,'2025-08-13 14:30:00',N'completed',0.00),
 (10000013,10000014,'2025-08-14 15:30:00',N'processing',0.00),
 (10000014,10000015,'2025-08-15 16:30:00',N'completed',0.00),
 (10000015,10000016,'2025-08-16 17:30:00',N'pending',0.00),
 (10000016,10000017,'2025-08-17 09:00:00',N'completed',0.00),
 (10000017,10000018,'2025-08-18 10:00:00',N'completed',0.00),
 (10000018,10000019,'2025-08-19 11:00:00',N'completed',0.00),
 (10000019,10000001,'2025-08-20 12:00:00',N'processing',0.00),
 (10000020,10000002,'2025-08-21 13:00:00',N'completed',0.00),
 (10000021,10000003,'2025-08-22 14:10:00',N'completed',0.00),
 (10000022,10000004,'2025-08-23 15:20:00',N'pending',0.00),
 (10000023,10000005,'2025-08-24 16:25:00',N'completed',0.00),
 (10000024,10000006,'2025-08-25 17:30:00',N'completed',0.00),
 (10000025,10000007,'2025-08-26 09:15:00',N'completed',0.00),
 (10000026,10000008,'2025-08-27 10:45:00',N'processing',0.00),
 (10000027,10000009,'2025-08-28 11:55:00',N'completed',0.00),
 (10000028,10000010,'2025-08-29 12:05:00',N'completed',0.00),
 (10000029,10000011,'2025-08-30 13:15:00',N'completed',0.00);
SET IDENTITY_INSERT dbo.orders OFF;
GO

/********************************************************************************
  7) order_products (~75 bản ghi)
   - mỗi order có 1-4 products; id của order_products set rõ
*********************************************************************************/
SET IDENTITY_INSERT dbo.order_products ON;
INSERT INTO dbo.order_products (id, order_id, product_id, quantity, review, receivedAt, currentLocation)
VALUES
 -- order 10000000
 (10000000,10000000,10000000,2,NULL,'2025-08-02 10:00:00',N'Hoàn thành'),
 (10000001,10000000,10000006,1,N'Ok','2025-08-02 10:00:00',N'Hoàn thành'),

 -- order 10000001
 (10000002,10000001,10000002,1,NULL,'2025-08-03 11:00:00',N'Hoàn thành'),
 (10000003,10000001,10000007,3,NULL,'2025-08-03 11:00:00',N'Hoàn thành'),

 -- order 10000002
 (10000004,10000002,10000008,1,N'Nice','2025-08-04 12:00:00',N'Hoàn thành'),

 -- order 10000003
 (10000005,10000003,10000010,2,NULL,NULL,N'Đang giao'),

 -- order 10000004
 (10000006,10000004,10000011,5,NULL,NULL,N'Pending'),

 -- order 10000005
 (10000007,10000005,10000005,1,NULL,'2025-08-07 16:00:00',N'Hoàn thành'),
 (10000008,10000005,10000006,2,NULL,'2025-08-07 16:00:00',N'Hoàn thành'),

 -- order 10000006
 (10000009,10000006,10000012,1,NULL,'2025-08-08 17:00:00',N'Hoàn thành'),
 (10000010,10000006,10000013,2,NULL,'2025-08-08 17:00:00',N'Hoàn thành'),

 -- order 10000007
 (10000011,10000007,10000014,1,NULL,NULL,N'Đang giao'),
 (10000012,10000007,10000015,2,NULL,NULL,N'Đang giao'),

 -- order 10000008
 (10000013,10000008,10000016,1,NULL,NULL,N'Processing'),

 -- order 10000009
 (10000014,10000009,10000017,10,NULL,'2025-08-10 10:40:00',N'Hoàn thành'),

 -- order 10000010
 (10000015,10000010,10000018,1,N'Returned reason',NULL,N'Returned'),

 -- order 10000011
 (10000016,10000011,10000019,4,NULL,'2025-08-12 14:00:00',N'Hoàn thành'),

 -- order 10000012
 (10000017,10000012,10000020,1,NULL,'2025-08-13 15:00:00',N'Hoàn thành'),
 (10000018,10000012,10000021,2,NULL,'2025-08-13 15:00:00',N'Hoàn thành'),

 -- order 10000013
 (10000019,10000013,10000022,1,NULL,'2025-08-14 16:00:00',N'Hoàn thành'),

 -- order 10000014
 (10000020,10000014,10000023,1,NULL,NULL,N'Processing'),
 (10000021,10000014,10000024,3,NULL,NULL,N'Processing'),

 -- order 10000015
 (10000022,10000015,10000025,1,NULL,'2025-08-16 18:00:00',N'Hoàn thành'),

 -- order 10000016
 (10000023,10000016,10000026,2,NULL,NULL,N'Pending'),

 -- order 10000017
 (10000024,10000017,10000027,4,NULL,'2025-08-18 11:00:00',N'Hoàn thành'),
 (10000025,10000017,10000029,10,NULL,'2025-08-18 11:00:00',N'Hoàn thành'),

 -- order 10000018
 (10000026,10000018,10000000,1,NULL,'2025-08-19 12:00:00',N'Hoàn thành'),
 (10000027,10000018,10000003,1,NULL,'2025-08-19 12:00:00',N'Hoàn thành'),

 -- order 10000019
 (10000028,10000019,10000004,2,NULL,NULL,N'Processing'),

 -- order 10000020
 (10000029,10000020,10000002,1,NULL,'2025-08-21 14:00:00',N'Hoàn thành'),
 (10000030,10000020,10000006,5,NULL,'2025-08-21 14:00:00',N'Hoàn thành'),

 -- order 10000021
 (10000031,10000021,10000008,2,NULL,'2025-08-22 15:00:00',N'Hoàn thành'),
 (10000032,10000021,10000013,1,NULL,'2025-08-22 15:00:00',N'Hoàn thành'),

 -- order 10000022
 (10000033,10000022,10000005,1,NULL,NULL,N'Pending'),

 -- order 10000023
 (10000034,10000023,10000000,3,NULL,'2025-08-24 17:00:00',N'Hoàn thành'),
 (10000035,10000023,10000020,1,NULL,'2025-08-24 17:00:00',N'Hoàn thành'),

 -- order 10000024
 (10000036,10000024,10000011,2,NULL,'2025-08-25 18:00:00',N'Hoàn thành'),
 (10000037,10000024,10000024,10,NULL,'2025-08-25 18:00:00',N'Hoàn thành'),

 -- order 10000025
 (10000038,10000025,10000021,5,NULL,'2025-08-26 10:00:00',N'Hoàn thành'),

 -- order 10000026
 (10000039,10000026,10000012,2,NULL,NULL,N'Processing'),
 (10000040,10000026,10000013,1,NULL,NULL,N'Processing'),

 -- order 10000027
 (10000041,10000027,10000014,2,NULL,'2025-08-28 12:00:00',N'Hoàn thành'),
 (10000042,10000027,10000015,1,NULL,'2025-08-28 12:00:00',N'Hoàn thành'),

 -- order 10000028
 (10000043,10000028,10000016,1,NULL,'2025-08-29 13:00:00',N'Hoàn thành'),
 (10000044,10000028,10000018,1,NULL,'2025-08-29 13:00:00',N'Hoàn thành'),

 -- order 10000029
 (10000045,10000029,10000017,8,NULL,'2025-08-30 14:00:00',N'Hoàn thành');
SET IDENTITY_INSERT dbo.order_products OFF;
GO

/********************************************************************************
  8) Cập nhật tổng tiền cho orders từ order_products * product.price
*********************************************************************************/
-- Cập nhật tổng tiền chính xác cho mỗi đơn
UPDATE o
SET total_amount = ISNULL(ops.total,0)
FROM dbo.orders o
LEFT JOIN (
    SELECT op.order_id, SUM(op.quantity * p.price) AS total
    FROM dbo.order_products op
    JOIN dbo.products p ON op.product_id = p.id
    GROUP BY op.order_id
) ops ON o.id = ops.order_id;
GO

/********************************************************************************
  9) carts (15 bản ghi)
*********************************************************************************/
INSERT INTO dbo.carts (user_id, product_id, quantity, createdAt)
VALUES
 (10000001,10000003,1,'2025-09-01 10:00:00'),
 (10000002,10000000,2,'2025-09-01 11:00:00'),
 (10000003,10000006,3,'2025-09-02 12:00:00'),
 (10000004,10000008,1,'2025-09-03 09:00:00'),
 (10000005,10000010,5,'2025-09-03 10:00:00'),
 (10000006,10000012,1,'2025-09-04 11:00:00'),
 (10000007,10000013,2,'2025-09-05 12:00:00'),
 (10000008,10000020,1,'2025-09-06 13:00:00'),
 (10000009,10000025,1,'2025-09-06 14:00:00'),
 (10000010,10000016,1,'2025-09-07 15:00:00'),
 (10000011,10000021,4,'2025-09-07 16:00:00'),
 (10000012,10000024,2,'2025-09-08 09:00:00'),
 (10000013,10000002,1,'2025-09-08 10:00:00'),
 (10000014,10000029,6,'2025-09-09 11:00:00'),
 (10000015,10000001,1,'2025-09-09 12:00:00');
GO

/********************************************************************************
 10) favorite_lists (25 bản ghi)
*********************************************************************************/
INSERT INTO dbo.favorite_lists (user_id, product_id, createdAt)
VALUES
 (10000001,10000008,'2025-07-10 10:00:00'),
 (10000001,10000006,'2025-07-11 11:00:00'),
 (10000002,10000005,'2025-07-12 12:00:00'),
 (10000002,10000007,'2025-07-13 13:00:00'),
 (10000003,10000000,'2025-07-14 14:00:00'),
 (10000004,10000003,'2025-07-15 15:00:00'),
 (10000005,10000010,'2025-07-16 16:00:00'),
 (10000006,10000013,'2025-07-17 17:00:00'),
 (10000007,10000016,'2025-07-18 18:00:00'),
 (10000008,10000020,'2025-07-19 09:00:00'),
 (10000009,10000021,'2025-07-20 10:00:00'),
 (10000010,10000022,'2025-07-21 11:00:00'),
 (10000011,10000024,'2025-07-22 12:00:00'),
 (10000012,10000025,'2025-07-23 13:00:00'),
 (10000013,10000026,'2025-07-24 14:00:00'),
 (10000014,10000027,'2025-07-25 15:00:00'),
 (10000015,10000028,'2025-07-26 16:00:00'),
 (10000016,10000029,'2025-07-27 17:00:00'),
 (10000017,10000011,'2025-07-28 09:00:00'),
 (10000018,10000004,'2025-07-29 10:00:00'),
 (10000019,10000002,'2025-07-30 11:00:00'),
 (10000001,10000013,'2025-08-01 12:00:00'),
 (10000002,10000025,'2025-08-02 13:00:00'),
 (10000003,10000020,'2025-08-03 14:00:00'),
 (10000004,10000021,'2025-08-04 15:00:00');
GO