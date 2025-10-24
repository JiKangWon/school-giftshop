package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class User {
    private Long id;
    private String userName;
    private String password;
    private BigDecimal balance;
    private String name;
    private Address address;          // FK -> addresses
    private String addressNumber;
    private String phone;
    private LocalDateTime createdAt;
    private int isSeller;             // theo mẫu (0/1)

    public User() {}

    public User(Long id, String userName, String password, BigDecimal balance, String name, Address address, String addressNumber, String phone, LocalDateTime createdAt, int isSeller) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.balance = balance;
        this.name = name;
        this.address = address;
        this.addressNumber = addressNumber;
        this.phone = phone;
        this.createdAt = createdAt;
        this.isSeller = isSeller;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public String getAddressNumber() { return addressNumber; }
    public void setAddressNumber(String addressNumber) { this.addressNumber = addressNumber; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getIsSeller() { return isSeller; }
    public void setIsSeller(int isSeller) { this.isSeller = isSeller; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", balance=" + balance +
                ", name='" + name + '\'' +
                ", address=" + address +
                ", addressNumber='" + addressNumber + '\'' +
                ", phone='" + phone + '\'' +
                ", createdAt=" + createdAt +
                ", isSeller=" + isSeller +
                '}';
    }
}
