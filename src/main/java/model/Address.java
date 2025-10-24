package model;

public class Address {
    private Long id;
    private String country;
    private String province;
    private String district;
    private String ward;
    private String street;

    public Address() {}

    public Address(Long id, String country, String province, String district, String ward, String street) {
        this.id = id;
        this.country = country;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.street = street;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", district='" + district + '\'' +
                ", ward='" + ward + '\'' +
                ", street='" + street + '\'' +
                '}';
    }
}
