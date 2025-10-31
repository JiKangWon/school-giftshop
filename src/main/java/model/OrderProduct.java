package model;

import java.time.LocalDateTime;

public class OrderProduct {
    private Long id;
    private Order order;
    private Product product;
    private Integer quantity;
    private String review;
    private LocalDateTime receivedAt;
    
    // --- (THAY ĐỔI) ---
    // private String currentLocation; // (Bỏ dòng này)
    private Address currentLocation; // (Thay bằng dòng này)
    // --- (HẾT THAY ĐỔI) ---

    private Boolean isReturn;
    private String returnStatus;
    private String returnReason;

    public OrderProduct() {}

    // --- (CẬP NHẬT CONSTRUCTOR) ---
    public OrderProduct(Long id, Order order, Product product, Integer quantity, String review, 
                        LocalDateTime receivedAt, Address currentLocation, // (Đã thay đổi kiểu)
                        Boolean isReturn, String returnStatus, String returnReason) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.review = review;
        this.receivedAt = receivedAt;
        this.currentLocation = currentLocation; // (Đã thay đổi)
        this.isReturn = (isReturn != null) ? isReturn : false;
        this.returnStatus = returnStatus;
        this.returnReason = (returnReason != null) ? returnReason : "";
    }

    // (Constructor cũ - có thể bỏ nếu không dùng)
    public OrderProduct(Long id, Order order, Product product, Integer quantity, String review, LocalDateTime receivedAt, String oldLocation) {
         // (Hàm này giờ đã lỗi thời, nhưng tạm giữ)
         this(id, order, product, quantity, review, receivedAt, null, false, null, "");
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }
    public LocalDateTime getReceivedAt() { return receivedAt; }
    public void setReceivedAt(LocalDateTime receivedAt) { this.receivedAt = receivedAt; }

    // --- (THAY ĐỔI GETTER/SETTER) ---
    public Address getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(Address currentLocation) { this.currentLocation = currentLocation; }
    // --- (HẾT THAY ĐỔI) ---

    public Boolean getIsReturn() { return (isReturn != null) ? isReturn : false; }
    public void setIsReturn(Boolean isReturn) { this.isReturn = isReturn; }
    public String getReturnStatus() { return returnStatus; }
    public void setReturnStatus(String returnStatus) { this.returnStatus = returnStatus; }
    public String getReturnReason() { return (returnReason != null) ? returnReason : ""; }
    public void setReturnReason(String returnReason) { this.returnReason = returnReason; }

    @Override
    public String toString() {
        return "OrderProduct{" +
                "id=" + id +
                ", order=" + (order != null ? order.getId() : null) +
                ", product=" + (product != null ? product.getId() : null) +
                ", quantity=" + quantity +
                ", currentLocation=" + (currentLocation != null ? currentLocation.getId() : null) + // (Đã thay đổi)
                ", isReturn=" + isReturn +
                '}';
    }
}
