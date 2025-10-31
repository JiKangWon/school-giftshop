package service;

import database.AddressDAO;
import database.AdjacentAddressDAO;
import database.UserDAO;
import model.Address;
import model.AdjacentAddress;
import model.User;
import model.OrderProduct; // (MỚI) Import
import model.transport.Edge;
import model.transport.MapData; // (MỚI) Import
import model.transport.PathResult; // (MỚI) Import

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransportService {

    // Cache đồ thị
    private static Map<Long, List<Edge>> graphCache = null;
    private static Map<Long, Address> addressMapCache = null;
    private static List<AdjacentAddress> adjacentListCache = null; // (MỚI) Cache các cạnh
    private static User sellerCache = null;

    public synchronized void loadGraph() throws Exception {
        if (graphCache != null) {
            System.out.println("Đồ thị vận chuyển đã được tải, sử dụng cache.");
            return;
        }
        System.out.println("Đang tải dữ liệu đồ thị vận chuyển...");
        
        List<Address> allAddressList = AddressDAO.selectAll();
        adjacentListCache = AdjacentAddressDAO.selectAll(); // (MỚI) Cache các cạnh
        sellerCache = UserDAO.selectSeller();

        if (sellerCache == null || sellerCache.getAddress() == null) {
            System.err.println("LỖI NGHIÊM TRỌNG: Không tìm thấy Seller hoặc Seller không có địa chỉ.");
            throw new Exception("Không tìm thấy thông tin Seller hoặc địa chỉ Seller.");
        }

        addressMapCache = allAddressList.stream()
                .collect(Collectors.toMap(Address::getId, address -> address));
        
        Map<Long, List<Edge>> graph = new HashMap<>();
        for(Address addr : allAddressList) {
            graph.put(addr.getId(), new ArrayList<>());
        }

        for (AdjacentAddress adj : adjacentListCache) { // (MỚI) Dùng cache
            if (graph.containsKey(adj.getAddress1().getId()) && graph.containsKey(adj.getAddress2().getId())) {
                long a1 = adj.getAddress1().getId();
                long a2 = adj.getAddress2().getId();
                double distance = adj.getDistance();
                graph.get(a1).add(new Edge(a2, distance));
                graph.get(a2).add(new Edge(a1, distance));
            }
        }
        
        graphCache = graph;
        System.out.println("Tải đồ thị thành công. Nodes: " + addressMapCache.size() + ", Edges: " + adjacentListCache.size() + ", Seller Addr: " + (sellerCache.getAddress().getId()));
    }

    /**
     * (CẬP NHẬT) Trả về MapData
     */
    public MapData getMapDataForOrderProduct(OrderProduct op) throws Exception {
        
        if (graphCache == null || addressMapCache == null || sellerCache == null) {
            loadGraph();
        }

        MapData mapData = new MapData();
        mapData.setOrderProduct(op);

        // 1. Lấy địa chỉ bắt đầu (Seller)
        long startAddressId = sellerCache.getAddress().getId();

        // 2. Lấy địa chỉ kết thúc (Customer)
        Address endAddress = UserDAO.findAddressByOrderProductId(op.getId());
        if (endAddress == null) {
            throw new Exception("Không tìm thấy địa chỉ của khách hàng cho đơn hàng (op_id: " + op.getId() + ").");
        }
        long endAddressId = endAddress.getId();
        
        // 3. Gọi Dijkstra Service
        PathResult pathResult = DijkstraService.findShortestPath(
                graphCache, 
                startAddressId, 
                endAddressId,
                addressMapCache
        );

        // 4. (MỚI) Tìm ID của vị trí hiện tại
        // Duyệt cache địa chỉ để tìm ID khớp với TÊN vị trí hiện tại
        long currentAddressId = -1; // -1 nếu không tìm thấy
        String currentLocationName = op.getCurrentLocation().getName();
        
        // Cần xử lý trường hợp 'Processing' (là vị trí của Seller)
        if ("Processing".equalsIgnoreCase(currentLocationName)) {
            currentAddressId = startAddressId;
        } else {
             for (Address addr : addressMapCache.values()) {
                if (addr.getName() != null && addr.getName().equalsIgnoreCase(currentLocationName)) {
                    currentAddressId = addr.getId();
                    break;
                }
            }
        }
        if (currentAddressId == -1) {
             System.err.println("Cảnh báo: Không tìm thấy ID cho currentLocationName: " + currentLocationName);
        }

        // 5. (MỚI) Gói tất cả dữ liệu vào MapData
        mapData.setPath(pathResult.getPath());
        mapData.setTotalDistance(pathResult.getDistance());
        mapData.setCurrentAddressId(currentAddressId); // ID của vị trí hiện tại
        mapData.setAllNodes(new ArrayList<>(addressMapCache.values())); // Gửi tất cả Nodes
        mapData.setAllEdges(adjacentListCache); // Gửi tất cả Edges

        return mapData;
    }
}