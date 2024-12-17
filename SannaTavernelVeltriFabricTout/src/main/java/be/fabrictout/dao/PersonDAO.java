package be.fabrictout.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import be.fabrictout.javabeans.Person;

public class PersonDAO extends DAO<Person> {
	
	protected Connection connect = null;

	public PersonDAO(Connection conn) {
		super(conn);
	}
	
	public int getNextIdDAO() {
		System.out.println("PersonDAO : getNextIdDAO");
		String sql = "{CALL get_next_person_id(?)}";
		try (CallableStatement stmt = connect.prepareCall(sql)) {
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.execute();
			return stmt.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public boolean createDAO(Person obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteDAO(Person obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateDAO(Person obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Person findDAO(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Person> findAllDAO() {
		// TODO Auto-generated method stub
		return null;
	}

}
