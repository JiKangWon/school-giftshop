package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.Address;

public class AddressDAO {
    public static ArrayList<Address> selectAll(){
        ArrayList<Address> arr = new ArrayList<>();
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM addresses";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
            	Long id = rs.getLong("id");
                String country = rs.getString("country");
                String province = rs.getString("province");
                String district = rs.getString("district");
                String ward = rs.getString("ward");
                String street = rs.getString("street");
                Address a = new Address(id, country, province, district, ward, street);
                arr.add(a);
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return arr;
    }

    public static Address selectById(Long id){
        if (id == null) return null;
        Address a = null;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM addresses WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                String country = rs.getString("country");
                String province = rs.getString("province");
                String district = rs.getString("district");
                String ward = rs.getString("ward");
                String street = rs.getString("street");
                a = new Address(id, country, province, district, ward, street);
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return a;
    }

    public static int insert(Address address){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "INSERT INTO addresses(id, country, province, district, ward, street) VALUES (?,?,?,?,?,?)";
            PreparedStatement st = conn.prepareStatement(sql);
            if(address.getId() != null) st.setLong(1, address.getId()); else st.setNull(1, java.sql.Types.INTEGER);
            st.setString(2, address.getCountry());
            st.setString(3, address.getProvince());
            st.setString(4, address.getDistrict());
            st.setString(5, address.getWard());
            st.setString(6, address.getStreet());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int insert(ArrayList<Address> arr){
        int count = 0;
        for(Address a: arr) count += insert(a);
        return count;
    }

    public static int delete(Address address){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "DELETE FROM addresses WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, address.getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int delete(ArrayList<Address> arr){
        int count = 0;
        for(Address a: arr) count += delete(a);
        return count;
    }

    public static int update(Address address){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "UPDATE addresses SET country=?, province=?, district=?, ward=?, street=? WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, address.getCountry());
            st.setString(2, address.getProvince());
            st.setString(3, address.getDistrict());
            st.setString(4, address.getWard());
            st.setString(5, address.getStreet());
            st.setLong(6, address.getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
