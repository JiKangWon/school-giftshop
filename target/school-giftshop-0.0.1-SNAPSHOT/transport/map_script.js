/**
 * Script này sẽ đọc dữ liệu JSON từ các thẻ <script> 
 * trong file order_map.jsp và vẽ bản đồ Vis.js
 */
document.addEventListener('DOMContentLoaded', function() {
    
    try {
        /**
         * Hàm helper để đọc và giải mã JSON từ thẻ script
         * (Chống lỗi XSS và lỗi cú pháp)
         */
        function getJsonData(id) {
            const element = document.getElementById(id);
            if (!element) {
                throw new Error('Không tìm thấy element dữ liệu: #' + id);
            }
            const rawData = element.textContent || element.innerText;
            
            // Giải mã HTML (ví dụ: &amp; -> &, &quot; -> ")
            const Txt = document.createElement("textarea");
            Txt.innerHTML = rawData;
            const decodedData = Txt.value;
            
            return JSON.parse(decodedData);
        }

        // --- BƯỚC A: Lấy và chuẩn bị dữ liệu từ Servlet (JSON) ---
        
        // Dùng hàm helper để đọc dữ liệu một cách an toàn
        const allNodesData = getJsonData('all-nodes-data');
        const allEdgesData = getJsonData('all-edges-data');
        const pathNodesData = getJsonData('path-nodes-data');
        
        // Lấy ID vị trí hiện tại (được truyền qua data-attribute)
        const mapContainer = document.getElementById('transport-map');
        const currentAddressId = parseInt(mapContainer.dataset.currentAddressId, 10) || -1; 

        const startAddressId = pathNodesData.length > 0 ? pathNodesData[0].id : -1;
        const endAddressId = pathNodesData.length > 0 ? pathNodesData[pathNodesData.length - 1].id : -1;

        // 2. Tạo Set và Map để tra cứu nhanh lộ trình (path)
        const pathNodeIds = new Set(pathNodesData.map(node => node.id));
        const pathEdgeMap = new Map();
        
        for (let i = 0; i < pathNodesData.length - 1; i++) {
            let id1 = pathNodesData[i].id;
            let id2 = pathNodesData[i+1].id;
            // Lưu cả 2 chiều để đảm bảo bắt dính
            pathEdgeMap.set(`${id1}_${id2}`, true);
            pathEdgeMap.set(`${id2}_${id1}`, true);
        }

        // --- BƯỚC B: Chuyển đổi dữ liệu sang định dạng Vis.js ---

        // 1. Tạo NODES
        const nodes = new vis.DataSet(allNodesData.map(node => {
            const isPathNode = pathNodeIds.has(node.id);
            const isCurrentNode = (node.id === currentAddressId);
            const isStartNode = (node.id === startAddressId);
            const isEndNode = (node.id === endAddressId);

            let nodeStyle = {
                shape: 'box',
                margin: 10,
                font: { size: 14, face: 'Arial' },
                color: {} // Sẽ định nghĩa bên dưới
            };

            if (isCurrentNode) {
                // (Yêu cầu 3) Highlight nổi bật vị trí hiện tại
                nodeStyle.color = { background: '#FFD700', border: '#FF8C00', highlight: { background: '#FFEC8B', border: '#FF8C00' } }; // Vàng/Cam
                nodeStyle.shape = 'ellipse'; // Hình tròn
                nodeStyle.font = { size: 18, face: 'Arial', weight: 'bold' };
                nodeStyle.shadow = { enabled: true, color: 'rgba(0,0,0,0.5)', size: 10, x: 5, y: 5 };
            } else if (isStartNode) {
                // Điểm đầu (Seller)
                nodeStyle.color = { background: '#A1FCA1', border: '#4CAF50', highlight: { background: '#D4FFD4', border: '#4CAF50' } }; // Green
            } else if (isEndNode) {
                // Điểm cuối (Customer)
                nodeStyle.color = { background: '#FCA1A1', border: '#F44336', highlight: { background: '#FFD4D4', border: '#F44336' } }; // Red
            } else if (isPathNode) {
                // Điểm trung gian (Kho) trên lộ trình
                nodeStyle.color = { background: '#D2E5FF', border: '#2B7CE9', highlight: { background: '#EAF2FF', border: '#2B7CE9' } }; // Blue
            } else {
                // (Yêu cầu 1) Các node không thuộc lộ trình (Màu xám)
                nodeStyle.color = { background: '#F0F0F0', border: '#BDBDBD', highlight: { background: '#FAFAFA', border: '#BDBDBD' } };
                nodeStyle.font = { color: '#BDBDBD' };
            }
            
            return {
                id: node.id,
                label: node.name,
                ...nodeStyle // Áp dụng style
            };
        }));

        // 2. Tạo EDGES
        const edges = new vis.DataSet(allEdgesData.map(edge => {
            // Đảm bảo address1 và address2 tồn tại trước khi truy cập id
            const fromId = edge.address1 ? edge.address1.id : null;
            const toId = edge.address2 ? edge.address2.id : null;
            
            // Bỏ qua cạnh không hợp lệ (nếu có)
            if (!fromId || !toId) {
                console.warn("Bỏ qua cạnh không hợp lệ:", edge);
                return null; // Sẽ được lọc ra sau
            }

            const isPathEdge = pathEdgeMap.has(`${fromId}_${toId}`) || pathEdgeMap.has(`${toId}_${fromId}`);
            
            let edgeStyle = {};

            if (isPathEdge) {
                // (Yêu cầu 2) Highlight cạnh thuộc lộ trình
                edgeStyle = {
                    color: { color: '#F44336', highlight: '#F44336', hover: '#F44336' }, // Màu đỏ
                    width: 4,
                    arrows: 'to',
                    label: edge.distance + ' km', // (Yêu cầu 4) Hiển thị KM trên cạnh
                    font: { align: 'middle', color: '#B71C1C', strokeWidth: 0 }
                };
            } else {
                // (Yêu cầu 1) Các cạnh không thuộc lộ trình (Màu xám)
                edgeStyle = {
                    color: { color: '#E0E0E0', highlight: '#BDBDBD', hover: '#BDBDBD' },
                    width: 1,
                    dashes: [5, 5] // Nét đứt
                };
            }

            return {
                id: `edge_${fromId}_${toId}`,
                from: fromId,
                to: toId,
                ...edgeStyle
            };
        }).filter(e => e != null)); // Lọc ra các cạnh null (nếu có)

        // --- BƯỚC C: Cấu hình và Vẽ bản đồ ---

        var container = document.getElementById('transport-map');
        var data = { nodes: nodes, edges: edges };

        var options = {
            layout: {
                randomSeed: 2 // Giữ layout ổn định
            },
            physics: {
                enabled: true, // Bật vật lý để nó tự sắp xếp
                solver: 'forceAtlas2Based',
                forceAtlas2Based: {
                    gravitationalConstant: -100,
                    springLength: 150,
                    springConstant: 0.05
                },
                stabilization: {
                   iterations: 150
                }
            },
            edges: {
                smooth: false,
            },
            interaction: {
                dragNodes: true, // Cho phép kéo node
                dragView: true, 
                zoomView: true
            }
        };

        var network = new vis.Network(container, data, options);

        // ======================================================
        // (BẮT ĐẦU) BƯỚC D: Thêm sự kiện CLICK cho Node
        // ======================================================

        // Lấy khung thông tin từ JSP
        const nodeInfoBox = document.getElementById('node-info-box');

        // Hàm helper để tái tạo logic getFullAddress() của Java
        function getFullAddress(address) {
            if (!address) return "Không có thông tin.";
            let parts = [
                address.street,
                address.ward,
                address.district,
                address.province,
                address.country
            ];
            // Lọc ra các phần null hoặc rỗng
            return parts.filter(p => p && p.trim() !== "").join(", ");
        }

        // Lắng nghe sự kiện click trên bản đồ
        network.on('click', function(params) {
            
            // Kiểm tra xem người dùng có click vào node nào không
            if (params.nodes.length > 0) {
                const clickedNodeId = params.nodes[0];
                
                // Tìm thông tin đầy đủ của node đó từ mảng data
                // (Sử dụng .find() trên mảng allNodesData đã được parse)
                const clickedNodeData = allNodesData.find(node => node.id === clickedNodeId);
                
                if (clickedNodeData) {
                    // Tạo nội dung HTML để hiển thị
                    let htmlContent = `
                        <h5 class"text-primary mb-1">${clickedNodeData.name}</h5>
                        <p class="mb-0">
                            <i class="bi bi-geo-alt-fill"></i> 
                            ${getFullAddress(clickedNodeData)}
                        </p>
                        <small class="text-muted">Node ID: ${clickedNodeData.id}</small>
                    `;
                    // Cập nhật khung thông tin
                    nodeInfoBox.innerHTML = htmlContent;
                }
                
            } else {
                // Nếu người dùng click ra ngoài (vào nền)
                nodeInfoBox.innerHTML = `
                    <p class="text-muted text-center">
                        <i class="bi bi-info-circle"></i> 
                        Nhấp vào một địa điểm trên bản đồ để xem chi tiết.
                    </p>
                `;
            }
        });
        
        // ======================================================
        // (KẾT THÚC) BƯỚC D
        // ======================================================

    
    } catch (error) {
        console.error("Lỗi khi vẽ bản đồ:", error);
        // Hiển thị lỗi cho người dùng (nếu cần)
        var container = document.getElementById('transport-map');
        if(container) {
            container.innerHTML = '<div class="alert alert-danger">Đã xảy ra lỗi khi render bản đồ. Vui lòng kiểm tra Console (F12).</div>';
        }
    }
});