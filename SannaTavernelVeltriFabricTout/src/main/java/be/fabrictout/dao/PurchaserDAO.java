package be.fabrictout.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import be.fabrictout.javabeans.Purchaser;

public class PurchaserDAO extends DAO<Purchaser> {
	private Connection connection;

	public PurchaserDAO(Connection connection) {
		super(connection);
		this.connection = connection;
	}
			
	@Override
	public boolean createDAO(Purchaser purchaser) {
		System.out.println("PurchaserDAO : createDAO");
	    String sql = "{CALL create_purchaser(?, ?, ?, ?, ?, ?, ?)}";
	    try (CallableStatement stmt = connection.prepareCall(sql)) {
	        stmt.setInt(1, purchaser.getIdPerson()); 
	        stmt.setString(2, purchaser.getFirstName());
	        stmt.setString(3, purchaser.getLastName());
	        stmt.setDate(4, Date.valueOf(purchaser.getBirthDate()));
	        stmt.setString(5, purchaser.getPhoneNumber());
	        stmt.setString(6, purchaser.getRegistrationCode());
	        stmt.setString(7, purchaser.getPassword());
	        stmt.execute();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	
	@Override
	public boolean updateDAO(Purchaser purchaser) {
		System.out.println("PurchaserDAO : updateDAO");
	    String sql = "{CALL update_purchaser(?, ?, ?, ?, ?, ?, ?)}";
	    try (CallableStatement stmt = connection.prepareCall(sql)) {
	        stmt.setInt(1, purchaser.getIdPerson());
	        stmt.setString(2, purchaser.getFirstName());
	        stmt.setString(3, purchaser.getLastName());
	        stmt.setDate(4, Date.valueOf(purchaser.getBirthDate()));
	        stmt.setString(5, purchaser.getPhoneNumber());
	        stmt.setString(6, purchaser.getRegistrationCode());
	        stmt.setString(7, purchaser.getPassword());
	        return stmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


    @Override
    public boolean deleteDAO(Purchaser purchaser) {
    	System.out.println("PurchaserDAO : deleteDAO");
    	String sql = "{CALL delete_purchaser(?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, purchaser.getIdPerson());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	
	
    @Override
    public Purchaser findDAO(int id) {
    	System.out.println("PurchaserDAO : findDAO");
        String sql = "{CALL find_purchaser(?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return setPurchaser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<Purchaser> findAllDAO() {
    	System.out.println("PurchaserDAO : findAllDAO");
        String sql = "{CALL find_all_purchasers()}";
        List<Purchaser> purchasers = new ArrayList<>();
        try (CallableStatement stmt = connection.prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                purchasers.add(setPurchaser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchasers;
    }

    
	
	public Purchaser setPurchaser(ResultSet rs) throws SQLException {
		System.out.println("PurchaserDAO : setPurchaser");
		Purchaser purchaser = new Purchaser(
				rs.getInt("id_person"), 
				rs.getString("firstName"), 
				rs.getString("lastName"),
				rs.getDate("birthDate").toLocalDate(), 
				rs.getString("phoneNumber"), 
				rs.getString("registrationCode"),
				rs.getString("password"));
		
		return purchaser;
	}

}
