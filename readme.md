# 🎁 School Giftshop – Ứng dụng Web Cửa hàng Quà tặng Trường học
**School Giftshop** là một ứng dụng web thương mại điện tử được xây dựng bằng **Java Servlet/JSP**, mô phỏng một cửa hàng quà tặng trực tuyến.
Dự án hỗ trợ nhiều vai trò người dùng: **Khách hàng**, **Người bán**, và **Người giao vận**, với các chức năng quản lý sản phẩm, giỏ hàng, đơn hàng và tối ưu hóa giao hàng bằng thuật toán.
---
## 🚀 Tính năng chính
### 1. 🔒 Chức năng chung
* **Xác thực người dùng:** Đăng ký, đăng nhập cho mọi vai trò.
* **Bảo mật:** Mã hóa mật khẩu bằng `util.Encryption`.
---
### 2. 🛍️ Khách hàng (Customer)
* **Trang chủ:** Duyệt, tìm kiếm, lọc sản phẩm theo danh mục.
* **Chi tiết sản phẩm:** Xem thông tin, hình ảnh và giá.
* **Giỏ hàng:** Thêm, xóa, cập nhật sản phẩm.
* **Danh sách yêu thích (Wishlist):** Thêm/xóa sản phẩm yêu thích.
* **Thanh toán:** Thực hiện quy trình đặt hàng và thanh toán.
* **Quản lý đơn hàng:** Xem lịch sử mua hàng.
* **Tài khoản:** Cập nhật thông tin cá nhân, đổi mật khẩu.
---
### 3. 🏪 Người bán (Seller)

* **Dashboard:** Giao diện quản lý riêng.
* **Quản lý sản phẩm:** Thêm, sửa, xóa (CRUD).
* **Quản lý hình ảnh:** Tải lên nhiều hình cho sản phẩm.
* **Quản lý đơn hàng:** Xem và xử lý đơn hàng.
* **Báo cáo:** Xem thống kê doanh thu, sản phẩm bán chạy (`seller/report.jsp`).
---
### 4. 🚚 Giao vận (Transport)
* **Bản đồ địa chỉ:** Quản lý các địa điểm (Nodes) và tuyến đường (Edges).
* **Tối ưu hóa đường đi:** Sử dụng **Thuật toán Dijkstra** (`service.DijkstraService`) để tìm đường ngắn nhất từ kho đến địa chỉ khách hàng.
* **Quản lý giao hàng:** Xem bản đồ, cập nhật trạng thái đơn giao.
---
## ⚙️ Công nghệ sử dụng
| Loại                      | Công nghệ                         |
| ------------------------- | --------------------------------- |
| **Ngôn ngữ Backend**      | Java (JDK 11+)                    |
| **Framework Web**         | Java Servlets, JSP, JSTL          |
| **Cơ sở dữ liệu**         | Microsoft SQL Server              |
| **Build & Quản lý dự án** | Apache Maven                      |
| **Frontend**              | JSP, JavaScript (module giao vận) |
| **Servlet Container**     | Apache Tomcat 10.1+               |
**Thư viện chính:**
* `jakarta.servlet-api`: Xử lý request/response web.
* `GSON`: Xử lý JSON (module bản đồ/giao vận).
* `commons-fileupload2`: Upload hình ảnh sản phẩm.
* `angus-mail`: Gửi email xác nhận (đăng ký hoặc đơn hàng).

---

## 🛠️ Cài đặt và Chạy dự án
### 1. Yêu cầu môi trường
* **JDK**: 11 trở lên
* **Apache Maven**
* **Microsoft SQL Server**
* **Apache Tomcat**: 10.1+
---
### 2. Cấu hình Cơ sở dữ liệu
1. Mở **SQL Server Management Studio (SSMS)**.
2. Tạo cơ sở dữ liệu mới, ví dụ: `SchoolGiftShop`.
3. Chạy file `createDB.sql` để tạo bảng và quan hệ.
4. (Tuỳ chọn) Chạy `queryDB.sql` để thêm dữ liệu mẫu.
---
### 3. Cấu hình Chuỗi kết nối
Mở tệp `src/main/java/database/JDBCUtil.java` và chỉnh lại thông tin trong phương thức `getConnection()`:

```java
String url = "jdbc:sqlserver://[SERVER_CUA_BAN]:1433;databaseName=school_giftshop;encrypt=true;trustServerCertificate=true;";
String user = "sa";
String password = "123456789";
```

---
### 4. Build và Deploy

Mở Terminal trong thư mục gốc (chứa `pom.xml`) và chạy:

```bash
mvn clean package
```

Sau khi build thành công, tệp `.war` sẽ được tạo tại thư mục `target/`, ví dụ:
`school-giftshop-0.0.1-SNAPSHOT.war`.

* Sao chép file `.war` vào thư mục `webapps/` của **Tomcat**,
  hoặc deploy qua giao diện quản lý Tomcat.
* Khởi động Tomcat và truy cập tại:
  👉 [http://localhost:8080/school-giftshop-0.0.1-SNAPSHOT/](http://localhost:8080/school-giftshop-0.0.1-SNAPSHOT/)

---

## 📂 Cấu trúc Thư mục

```
school-giftshop/
│
├── src/main/java/
│   ├── controller/        # Servlets xử lý logic
│   │   └── customer/      # Servlets cho khách hàng
│   ├── database/          # DAO và JDBCUtil
│   ├── model/             # Các lớp POJO
│   │   └── transport/     # Models cho module giao vận
│   ├── service/           # Logic nghiệp vụ (DijkstraService)
│   └── util/              # Lớp tiện ích (Encryption)
│
├── src/main/webapp/
│   ├── account/           # Trang tài khoản (đổi mật khẩu, thông tin)
│   ├── customer/          # JSP khách hàng
│   ├── seller/            # JSP người bán
│   ├── transport/         # JSP và JS module giao vận
│   ├── uploads/           # Lưu hình ảnh sản phẩm (tạo thủ công)
│   ├── WEB-INF/
│   │   └── web.xml        # Cấu hình Servlet
│   ├── index.jsp          # Trang đăng nhập
│   └── register.jsp       # Trang đăng ký
│
├── createDB.sql           # Script khởi tạo CSDL
├── queryDB.sql            # Script dữ liệu mẫu
└── pom.xml                # File cấu hình Maven
```

---

## 🧠 Ghi chú thêm

* Có thể mở rộng module **Báo cáo** và **Giao vận** để kết hợp biểu đồ thống kê và bản đồ tương tác.
* Hệ thống đã sẵn sàng để tích hợp thanh toán online và xác thực email tự động.

---

**📧 Tác giả:** *Nhóm dự án School Giftshop*
**📆 Phiên bản:** 1.0.0
**🛠️ Bản quyền:** MIT License
