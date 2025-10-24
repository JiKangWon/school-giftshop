package model;

import java.time.LocalDateTime;

public class FavoriteList {
    private User user;
    private Product product;
    private LocalDateTime createdAt;

    public FavoriteList() {}

    public FavoriteList(User user, Product product, LocalDateTime createdAt) {
        this.user = user;
        this.product = product;
        this.createdAt = createdAt;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "FavoriteList{" +
                "user=" + (user != null ? user.getId() : null) +
                ", product=" + (product != null ? product.getId() : null) +
                ", createdAt=" + createdAt +
                '}';
    }
}
