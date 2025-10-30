package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Category;
import model.Product;
import model.ProductImage;
import model.User;

import java.io.File; // Thêm import này
import java.io.IOException;
import java.io.InputStream; // Thêm import này
import java.math.BigDecimal;
import java.nio.file.Files; // Thêm import này
import java.nio.file.Path; // Thêm import này
import java.nio.file.Paths; // Thêm import này
import java.nio.file.StandardCopyOption; // Thêm import này
import java.sql.SQLException; // Thêm nếu ProductDAO.insert ném ra
import java.time.LocalDateTime;
import java.util.ArrayList; // Thêm import này
import java.util.Collection; // Thêm import này
import java.util.HashMap; // Thêm import này
import java.util.List; // Thêm import này
import java.util.Map; // Thêm import này
import java.util.UUID; // Để tạo tên file duy nhất

import database.ProductImageDAO; // Thêm DAO cho ảnh
import jakarta.servlet.annotation.MultipartConfig; //
import jakarta.servlet.http.Part;

import database.CategoryDAO;
import database.ProductDAO;

/**
 * Servlet implementation class Seller
 */
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 15    // 15 MB
)
public class Seller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String UPLOAD_DIR = "uploads" + File.separator + "products";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Seller() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Kiểm tra xem người dùng đã đăng nhập và có phải là seller không
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if(user == null || user.getIsSeller()==0) {
            // Nếu không phải, đá về trang chủ
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // Lấy đường dẫn con (ví dụ: /home, /products)
        String action = request.getPathInfo();
        if (action == null) {
            action = "/home"; // Mặc định
        }

        String viewName = ""; // Tên file .jsp con sẽ được nạp
        
        switch (action) {
            case "/home":
                // Tương ứng với chức năng "Lịch sử bán hàng" 
                viewName = "home.jsp"; 
                break;
            case "/products":
                try {
                    // 1. Định nghĩa số sản phẩm mỗi trang
                    int pageSize = 10;
                    
                    // 2. Lấy số trang hiện tại từ URL (mặc định là trang 1)
                    int currentPage = 1;
                    if (request.getParameter("page") != null) {
                        try {
                            currentPage = Integer.parseInt(request.getParameter("page"));
                        } catch (NumberFormatException e) {
                            currentPage = 1; // Nếu nhập bậy thì về trang 1
                        }
                    }

                    // 3. Lấy TỔNG số sản phẩm (Không cần sellerId)
                    int totalProducts = ProductDAO.getTotalProductCount();
                    
                    // 4. Tính TỔNG số trang
                    int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
                    
                    // 5. Lấy danh sách sản phẩm CHỈ CỦA TRANG HIỆN TẠI (Không cần sellerId)
                    ArrayList<Product> productList = ProductDAO.selectAllPaginated(currentPage, pageSize);
                    
                    // 6. Đặt các biến vào request để JSP sử dụng
                    request.setAttribute("productList", productList);
                    request.setAttribute("totalPages", totalPages);
                    request.setAttribute("currentPage", currentPage);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                // Tên file .jsp con sẽ được nạp
                viewName = "products.jsp"; 
                break;
            case "/add-product":
                try {
                    // LoadData() => Load các category lên
                    List<Category> categories = CategoryDAO.selectAll();
                    request.setAttribute("categories", categories);
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", "Không thể tải danh mục sản phẩm.");
                }
                viewName = "add_product.jsp"; // Tên file JSP chứa form
                break;
            case "/orders":
                viewName = "orders.jsp"; // Bạn sẽ tạo file này sau
                break;
            case "/report":
                // Tương ứng với chức năng "Báo cáo doanh thu" 
                viewName = "report.jsp"; // Bạn sẽ tạo file này sau
                break;
            case "/toggle-status": { // Thêm case mới
                try {
                    // 1. Lấy ID sản phẩm từ URL
                    Long productId = Long.parseLong(request.getParameter("id"));
                    
                    // 2. Gọi DAO để cập nhật trạng thái
                    ProductDAO.toggleProductStatus(productId);
                    
                    // 3. Lấy lại số trang hiện tại để quay về đúng trang đó
                    String page = request.getParameter("page");
                    if (page == null || page.isBlank()) {
                        page = "1";
                    }
                    
                    // 4. QUAN TRỌNG: Dùng redirect (PRG) để tải lại trang
                    // (Chúng ta giả định URL servlet là /kenh-ban)
                    response.sendRedirect(request.getContextPath() + "/seller-page/products?page=" + page);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    // Nếu lỗi, chuyển về trang products
                    response.sendRedirect(request.getContextPath() + "/seller-page/products");
                }
                return; // Dừng thực thi sau khi redirect
            }
            default:
                viewName = "home.jsp";
        }
        
        // Đặt tên file "view" con vào request
        request.setAttribute("view", viewName);
        
        // Forward tới trang layout CHÍNH (base.jsp)
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/seller/base.jsp");
        rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getPathInfo(); // Lấy action (vd: /add-product)

		if ("/add-product".equals(action)) {
			// 1. Kiểm tra session
			HttpSession session = request.getSession();
			User seller = (User) session.getAttribute("user");
			if (seller == null || seller.getIsSeller() == 0) {
				 response.sendRedirect(request.getContextPath() + "/login"); // Chưa đăng nhập hoặc không phải seller
				 return;
			}

            // 2. Chuẩn bị biến
			Map<String, String> oldInput = new HashMap<>(); // Lưu input cũ nếu có lỗi
			String error = null; // Lưu thông báo lỗi
			List<String> uploadedImagePaths = new ArrayList<>(); // Lưu đường dẫn ảnh đã upload
			long newProductId = -1; // Lưu ID sản phẩm mới tạo

            // 3. Xác định đường dẫn tuyệt đối để lưu file upload
            // getRealPath("") trả về thư mục gốc của webapp ĐÃ ĐƯỢC DEPLOY trên server
			String uploadPath = getServletContext().getRealPath("uploads/products");
            System.out.println("DEBUG: Upload path = " + uploadPath); // In ra để kiểm tra

            // Tạo thư mục nếu chưa tồn tại
			File uploadDir = new File(uploadPath);
			if (!uploadDir.exists()) {
				boolean created = uploadDir.mkdirs(); // Tạo cả thư mục cha nếu cần
                 if(!created) {
                     System.err.println("ERROR: Could not create upload directory: " + uploadPath);
                     // Có thể ném lỗi hoặc xử lý khác ở đây nếu không tạo được thư mục
                     forwardWithError(request, response, "Lỗi hệ thống: Không thể tạo thư mục lưu ảnh.", oldInput);
                     return;
                 }
			}

			try {
                // 4. Lấy các trường text từ request
				String name = request.getParameter("productName");
				String priceStr = request.getParameter("price");
				String stockStr = request.getParameter("stock");
				String description = request.getParameter("description");
				String categoryIdStr = request.getParameter("categoryId");

                // Lưu lại input để hiển thị nếu có lỗi
				oldInput.put("productName", name);
				oldInput.put("price", priceStr);
				oldInput.put("stock", stockStr);
				oldInput.put("description", description);
				oldInput.put("categoryId", categoryIdStr);

                // 5. Validate các trường text
				BigDecimal price = null;
				int stock = 0;
				int categoryId = 0;

				if (name == null || name.trim().isEmpty()) {
					error = "Tên sản phẩm không được để trống.";
				} else if (priceStr == null || priceStr.trim().isEmpty()) {
					error = "Giá sản phẩm không được để trống.";
				} else {
					try {
						price = new BigDecimal(priceStr.replace(",", "")); // Bỏ dấu phẩy nếu có
						if (price.compareTo(BigDecimal.ZERO) < 0) {
							 error = "Giá sản phẩm không được âm.";
						}
					} catch (NumberFormatException e) {
						error = "Giá sản phẩm không hợp lệ.";
					}
				}

				if (error == null) { // Chỉ kiểm tra tiếp nếu chưa có lỗi
					if (stockStr == null || stockStr.trim().isEmpty()) {
						error = "Số lượng tồn kho không được để trống.";
					} else {
						try {
							stock = Integer.parseInt(stockStr);
							if (stock < 0) {
								error = "Số lượng tồn kho không được âm.";
							}
						} catch (NumberFormatException e) {
							error = "Số lượng tồn kho không hợp lệ.";
						}
					}
				}

				 if (error == null) { // Chỉ kiểm tra tiếp nếu chưa có lỗi
					 if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
						error = "Vui lòng chọn danh mục sản phẩm.";
					} else {
						try {
							categoryId = Integer.parseInt(categoryIdStr);
						} catch (NumberFormatException e) {
							 error = "Danh mục không hợp lệ.";
						}
					}
				 }
				// Có thể thêm validate description (vd: độ dài)

                // 6. Nếu có lỗi validate -> Forward lại form
				if (error != null) {
					forwardWithError(request, response, error, oldInput);
					return; // Dừng xử lý
				}

                // 7. Xử lý các file upload
				Collection<Part> fileParts = request.getParts(); // Lấy tất cả các part (text field + file)
				boolean hasFile = false; // Biến kiểm tra xem có upload file nào không
                for (Part part : fileParts) {
					String fieldName = part.getName();
					String fileName = getFileName(part); // Lấy tên file gốc

					// Chỉ xử lý các part có name="productImages" và thực sự là file
					if ("productImages".equals(fieldName) && fileName != null && !fileName.isEmpty()) {
                        hasFile = true; // Đánh dấu là có file được upload

                         // Kiểm tra đuôi file (chỉ cho phép ảnh)
                         String lowerCaseFileName = fileName.toLowerCase();
                         if (!lowerCaseFileName.endsWith(".jpg") && !lowerCaseFileName.endsWith(".jpeg") && !lowerCaseFileName.endsWith(".png") && !lowerCaseFileName.endsWith(".gif")) {
                            forwardWithError(request, response, "Chỉ chấp nhận file ảnh có định dạng JPG, PNG, GIF. File '" + fileName +"' không hợp lệ.", oldInput);
                            return; // Dừng xử lý nếu file không hợp lệ
                        }

                         // Tạo tên file duy nhất để tránh ghi đè
						String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
						Path filePath = Paths.get(uploadPath + File.separator + uniqueFileName);

						// Lưu file vào thư mục đã xác định
						try (InputStream fileContent = part.getInputStream()) {
							Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
                             System.out.println("DEBUG: Copied file successfully to: " + filePath);
						} catch (IOException ioEx) {
							System.err.println("ERROR copying file '" + fileName + "': " + ioEx.getMessage());
							// Ném lỗi để catch bên ngoài xử lý chung
                            throw new IOException("Lỗi khi lưu file ảnh: " + fileName, ioEx);
						}

						// Lưu đường dẫn tương đối (dùng dấu / cho web) để lưu vào CSDL
						String relativePath = "/" + UPLOAD_DIR.replace(File.separator, "/") + "/" + uniqueFileName;
						uploadedImagePaths.add(relativePath);
					}
				}
                // (Tùy chọn) Kiểm tra xem có bắt buộc upload ảnh không
                // if (!hasFile) {
                //    forwardWithError(request, response, "Bạn phải chọn ít nhất một ảnh sản phẩm.", oldInput);
                //    return;
                // }

                // 8. Tạo đối tượng Product (chỉ 5 trường)
				Category category = new Category();
				category.setId(categoryId);

                // Dùng constructor 5 tham số nếu có, hoặc dùng setters
                Product newProduct = new Product();
                newProduct.setName(name.trim());
				newProduct.setPrice(price);
				newProduct.setStock(stock);
				newProduct.setDescription(description != null ? description.trim() : null);
				newProduct.setCategory(category);
                // Các trường status, createdAt sẽ lấy default từ CSDL
                // Không set seller

                // 9. Insert Product vào CSDL và lấy ID
				newProductId = ProductDAO.insertAndGetId(newProduct); // Gọi hàm insert 5 trường

				if (newProductId <= 0) {
					// Nếu insert thất bại (DAO trả về -1 hoặc ném lỗi)
					 throw new SQLException("Thêm sản phẩm vào CSDL thất bại, không lấy được ID.");
				}

                // 10. Insert các đường dẫn ảnh vào CSDL (bảng product_images)
				if (!uploadedImagePaths.isEmpty()) {
					for (String imgPath : uploadedImagePaths) {
						// Gọi DAO để insert ảnh với newProductId và imgPath
						ProductImageDAO.insert(newProductId, imgPath);
					}
				}

                // 11. Thành công -> Redirect về trang danh sách sản phẩm
				// Có thể thêm tham số success để hiển thị thông báo
				response.sendRedirect(request.getContextPath() + "/seller-page/products?success=ProductAdded");


			} catch (Exception e) { // Bắt lỗi chung (IOException, SQLException, NumberFormatException...)
				e.printStackTrace();

                // Cố gắng xóa các file đã upload nếu việc insert vào CSDL bị lỗi
                if (newProductId <= 0 && !uploadedImagePaths.isEmpty()) {
                    System.err.println("DB insert failed after saving files. Attempting to delete uploaded files.");
                    for(String relativePath : uploadedImagePaths) {
                        try {
                            String fileNameToDelete = relativePath.substring(relativePath.lastIndexOf('/') + 1);
                            Path filePathToDelete = Paths.get(uploadPath + File.separator + fileNameToDelete);
                            Files.deleteIfExists(filePathToDelete);
                            System.out.println("DEBUG: Deleted file due to error: " + filePathToDelete);
                        } catch (IOException deleteEx) {
                            System.err.println("ERROR deleting file '" + relativePath + "' after DB error: " + deleteEx.getMessage());
                        }
                    }
                }

				// Gửi lỗi về trang JSP
				forwardWithError(request, response, "Đã xảy ra lỗi khi đăng sản phẩm: " + e.getMessage(), oldInput);
			}

		} else {
			 // Xử lý các action POST khác nếu có (ví dụ: cập nhật sản phẩm)
			 // Tạm thời gọi doGet hoặc báo lỗi
			 doGet(request, response);
		}
	}
	private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String errorMessage, Map<String, String> oldInput) throws ServletException, IOException {
		request.setAttribute("error", errorMessage);
		request.setAttribute("oldInput", oldInput); // Giữ lại input cũ
		try {
            // Cần load lại danh mục để hiển thị dropdown
			List<Category> categories = CategoryDAO.selectAll();
			request.setAttribute("categories", categories);
		} catch (Exception e) {
			e.printStackTrace(); // Lỗi khi load lại category
			request.setAttribute("error", errorMessage + " (Lỗi tải lại danh mục)");
		}
		request.setAttribute("view", "add_product.jsp"); // Chỉ định view con
        // Forward về trang base layout (trang này sẽ include add_product.jsp)
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/seller/base.jsp");
		rd.forward(request, response);
	}
	private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] tokens = contentDisposition.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                // Lấy giá trị sau dấu =, bỏ dấu " ở đầu và cuối
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

}
