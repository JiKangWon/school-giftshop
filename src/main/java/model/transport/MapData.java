package model.transport;

import java.util.List;
import model.Address;
import model.AdjacentAddress;
import model.OrderProduct;

/**
 * Gói tất cả dữ liệu cần thiết cho 'order_map.jsp'
 */
public class MapData {
    // Dữ liệu chung
    private OrderProduct orderProduct;
    private double totalDistance;
    private long currentAddressId; // ID của vị trí hiện tại
    
    // Dữ liệu lộ trình
    private List<Address> path;
    
    // Dữ liệu toàn bộ bản đồ
    private List<Address> allNodes;
    private List<AdjacentAddress> allEdges;

    // Getters và Setters
    public OrderProduct getOrderProduct() { return orderProduct; }
    public void setOrderProduct(OrderProduct op) { this.orderProduct = op; }

    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double d) { this.totalDistance = d; }

    public long getCurrentAddressId() { return currentAddressId; }
    public void setCurrentAddressId(long id) { this.currentAddressId = id; }

    public List<Address> getPath() { return path; }
    public void setPath(List<Address> p) { this.path = p; }

    public List<Address> getAllNodes() { return allNodes; }
    public void setAllNodes(List<Address> n) { this.allNodes = n; }

    public List<AdjacentAddress> getAllEdges() { return allEdges; }
    public void setAllEdges(List<AdjacentAddress> e) { this.allEdges = e; }
}