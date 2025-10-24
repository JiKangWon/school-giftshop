package model;

import java.time.LocalDateTime;

public class OrderProduct {
    private Long id;
    private Order order;           // FK -> orders
    private Product product;       // FK -> products
    private Integer quantity;
    private String review;
    private LocalDateTime receivedAt;
    private String currentLocation;

    public OrderProduct() {}

    public OrderProduct(Long id, Order order, Product product, Integer quantity, String review, LocalDateTime receivedAt, String currentLocation) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.review = review;
        this.receivedAt = receivedAt;
        this.currentLocation = currentLocation;
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

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    @Override
    public String toString() {
        return "OrderProduct{" +
                "id=" + id +
                ", order=" + (order != null ? order.getId() : null) +
                ", product=" + (product != null ? product.getId() : null) +
                ", quantity=" + quantity +
                ", receivedAt=" + receivedAt +
                '}';
    }
}
