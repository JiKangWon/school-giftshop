package service;

import model.transport.Edge;
import model.transport.Node;
import model.transport.PathResult; // (MỚI) Import
import model.Address;
import java.util.*;

public class DijkstraService {

    /**
     * (CẬP NHẬT) Trả về PathResult (path + distance)
     */
    public static PathResult findShortestPath(Map<Long, List<Edge>> graph, long startNodeId, long endNodeId, Map<Long, Address> allAddresses) {
        
        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> previousNodes = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>();

        for (Long nodeId : allAddresses.keySet()) {
            distances.put(nodeId, Double.POSITIVE_INFINITY);
        }
        
        if (distances.get(startNodeId) == null) {
            System.err.println("Lỗi Dijkstra: Điểm bắt đầu (ID: " + startNodeId + ") không có trong danh sách Address.");
            return new PathResult(new ArrayList<>(), 0); // Trả về rỗng
        }
        
        distances.put(startNodeId, 0.0);
        pq.add(new Node(startNodeId, 0.0));

        while (!pq.isEmpty()) {
            Node currentNode = pq.poll();
            long u = currentNode.getId();
            double d = currentNode.getDistance();

            if (d > distances.get(u)) continue;
            if (u == endNodeId) break; 

            if (graph.get(u) != null) {
                for (Edge edge : graph.get(u)) {
                    long v = edge.getTargetNodeId();
                    double weight = edge.getWeight();
                    
                    if (distances.containsKey(v) && distances.get(u) + weight < distances.get(v)) {
                        distances.put(v, distances.get(u) + weight);
                        previousNodes.put(v, u); 
                        pq.add(new Node(v, distances.get(v))); 
                    }
                }
            }
        }

        // --- Truy vết đường đi ---
        List<Address> path = reconstructPath(startNodeId, endNodeId, previousNodes, allAddresses);
        
        // (MỚI) Lấy tổng khoảng cách
        double totalDistance = distances.get(endNodeId);
        if (totalDistance == Double.POSITIVE_INFINITY) {
            totalDistance = 0;
        }

        return new PathResult(path, totalDistance); // (MỚI) Trả về đối tượng
    }

    /**
     * Tái tạo lại đường đi (Không đổi)
     */
    private static List<Address> reconstructPath(long startNodeId, long endNodeId, Map<Long, Long> previousNodes, Map<Long, Address> allAddresses) {
        LinkedList<Address> path = new LinkedList<>();
        Long at = endNodeId;

        if (previousNodes.get(at) == null && at != startNodeId) {
            System.out.println("Không tìm thấy đường đi đến ID: " + endNodeId);
            return path; 
        }

        while (at != null) {
            Address address = allAddresses.get(at);
            if (address != null) {
                path.addFirst(address); 
            } else {
                 System.err.println("Lỗi: Không tìm thấy Address với ID " + at);
            }
            if (at == startNodeId) break;
            at = previousNodes.get(at);
        }

        if (path.isEmpty() || path.getFirst().getId() != startNodeId) {
             System.out.println("Không thể tái tạo đường đi.");
             return new ArrayList<>();
        }
        return path;
    }
}