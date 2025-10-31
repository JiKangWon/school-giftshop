package model.transport;

// Dùng để so sánh trong hàng đợi ưu tiên (PriorityQueue)
public class Node implements Comparable<Node> {
    private long id; // address_id
    private double distance; // Khoảng cách từ điểm bắt đầu

    public Node(long id, double distance) {
        this.id = id;
        this.distance = distance;
    }

    // Getters
    public long getId() {
        return id;
    }

    public double getDistance() {
        return distance;
    }

    // So sánh để PriorityQueue biết Node nào có "distance" nhỏ nhất
    @Override
    public int compareTo(Node other) {
        return Double.compare(this.distance, other.distance);
    }
}
