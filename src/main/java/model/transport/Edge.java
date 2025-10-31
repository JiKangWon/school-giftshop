package model.transport;

// Đại diện cho một cạnh (đường đi) trong đồ thị
public class Edge {
    private long targetNodeId; // Nút đích (address_id)
    private double weight;     // Trọng số (distance)

    public Edge(long targetNodeId, double weight) {
        this.targetNodeId = targetNodeId;
        this.weight = weight;
    }

    // Getters
    public long getTargetNodeId() {
        return targetNodeId;
    }

    public double getWeight() {
        return weight;
    }
}
