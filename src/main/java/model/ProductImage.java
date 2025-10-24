package model;

public class ProductImage {
    private Long id;
    private Product product;   // FK -> products
    private String imgLink;

    public ProductImage() {}

    public ProductImage(Long id, Product product, String imgLink) {
        this.id = id;
        this.product = product;
        this.imgLink = imgLink;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getImgLink() { return imgLink; }
    public void setImgLink(String imgLink) { this.imgLink = imgLink; }

    @Override
    public String toString() {
        return "ProductImage{" +
                "id=" + id +
                ", product=" + (product != null ? product.getId() : null) +
                ", imgLink='" + imgLink + '\'' +
                '}';
    }
}
