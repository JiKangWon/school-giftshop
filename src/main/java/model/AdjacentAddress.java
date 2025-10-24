package model;

public class AdjacentAddress {
    private Address address1;
    private Address address2;
    private Double distance;

    public AdjacentAddress() {}

    public AdjacentAddress(Address address1, Address address2, Double distance) {
        this.address1 = address1;
        this.address2 = address2;
        this.distance = distance;
    }

    public Address getAddress1() { return address1; }
    public void setAddress1(Address address1) { this.address1 = address1; }

    public Address getAddress2() { return address2; }
    public void setAddress2(Address address2) { this.address2 = address2; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    @Override
    public String toString() {
        return "AdjacentAddress{" +
                "address1=" + (address1 != null ? address1.getId() : null) +
                ", address2=" + (address2 != null ? address2.getId() : null) +
                ", distance=" + distance +
                '}';
    }
}
