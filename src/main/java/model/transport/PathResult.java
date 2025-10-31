package model.transport;

import java.util.List;
import model.Address;

/**
 * Lưu trữ kết quả từ thuật toán Dijkstra.
 */
public class PathResult {
    private List<Address> path;
    private double distance;

    public PathResult(List<Address> path, double distance) {
        this.path = path;
        this.distance = distance;
    }

    public List<Address> getPath() {
        return path;
    }

    public double getDistance() {
        return distance;
    }
}