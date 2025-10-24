package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.Address;
import model.AdjacentAddress;

public class AdjacentAddressDAO {
    public static ArrayList<AdjacentAddress> selectAll(){
        ArrayList<AdjacentAddress> arr = new ArrayList<>();
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM adjacent_addresses";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Long a1 = rs.getLong("address1_id");
                Long a2 = rs.getLong("address2_id");
                Double distance = rs.getObject("distance", Double.class);
                Address addr1 = AddressDAO.selectById(a1);
                Address addr2 = AddressDAO.selectById(a2);
                arr.add(new AdjacentAddress(addr1, addr2, distance));
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return arr;
    }

    public static AdjacentAddress selectByKey(Long address1Id, Long address2Id){
        if (address1Id == null || address2Id == null) return null;
        AdjacentAddress aa = null;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM adjacent_addresses WHERE address1_id=? AND address2_id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, address1Id);
            st.setLong(2, address2Id);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                Double distance = rs.getObject("distance", Double.class);
                Address addr1 = AddressDAO.selectById(address1Id);
                Address addr2 = AddressDAO.selectById(address2Id);
                aa = new AdjacentAddress(addr1, addr2, distance);
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return aa;
    }

    public static int insert(AdjacentAddress aa){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "INSERT INTO adjacent_addresses(address1_id, address2_id, distance) VALUES (?,?,?)";
            PreparedStatement st = conn.prepareStatement(sql);
            if (aa.getAddress1() != null && aa.getAddress1().getId() != null) st.setLong(1, aa.getAddress1().getId()); else st.setNull(1, java.sql.Types.INTEGER);
            if (aa.getAddress2() != null && aa.getAddress2().getId() != null) st.setLong(2, aa.getAddress2().getId()); else st.setNull(2, java.sql.Types.INTEGER);
            if (aa.getDistance() != null) st.setDouble(3, aa.getDistance()); else st.setNull(3, java.sql.Types.DOUBLE);
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int insert(ArrayList<AdjacentAddress> arr){
        int count = 0;
        for(AdjacentAddress aa: arr) count += insert(aa);
        return count;
    }

    public static int delete(AdjacentAddress aa){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "DELETE FROM adjacent_addresses WHERE address1_id=? AND address2_id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, aa.getAddress1().getId());
            st.setLong(2, aa.getAddress2().getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int update(AdjacentAddress aa){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "UPDATE adjacent_addresses SET distance=? WHERE address1_id=? AND address2_id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            if (aa.getDistance() != null) st.setDouble(1, aa.getDistance()); else st.setNull(1, java.sql.Types.DOUBLE);
            st.setLong(2, aa.getAddress1().getId());
            st.setLong(3, aa.getAddress2().getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
