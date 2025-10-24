package model;

import java.time.LocalDateTime;

public class Cart {
    private User user;             // FK -> users
    private Product product;       // FK -> products
    private Integer quantity;
    private LocalDateTime createdAt;

    public Cart() {}

    public Cart(User user, Product product, Integer quantity, LocalDateTime createdAt) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Cart{" +
                "user=" + (user != null ? user.getId() : null) +
                ", product=" + (product != null ? product.getId() : null) +
                ", quantity=" + quantity +
                ", createdAt=" + createdAt +
                '}';
    }
}
