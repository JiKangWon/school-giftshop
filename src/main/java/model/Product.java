package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private Long id;
    private Integer stock;
    private BigDecimal price;
    private String name;
    private String description;
    private Category category;   // FK -> categories
    private String status;
    private LocalDateTime createdAt;

    public Product() {}

    public Product(Long id, Integer stock, BigDecimal price, String name, String description, Category category, String status, LocalDateTime createdAt) {
        this.id = id;
        this.stock = stock;
        this.price = price;
        this.name = name;
        this.description = description;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", stock=" + stock +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
