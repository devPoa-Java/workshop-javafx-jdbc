package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {
	
	private Connection conn;
	
	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Department obj) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(
					"INSERT INTO  department "
					+ "(Name) "
					+ "VALUES "
					+ "(?)",
					Statement.RETURN_GENERATED_KEYS);
			
		    ps.setString(1, obj.getName());
		    int rowsAffected = ps.executeUpdate();
		    
		    if(rowsAffected > 0) { 
		    	rs = ps.getGeneratedKeys();
		    	if(rs.next()) {
		    		int id = rs.getInt(1);
		    		obj.setId(id);
		    	}
		    	else {
		    		throw new DbException("Unexpected error: No rows affected!");
		    	}
		    }
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public void update(Department obj) {
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(
					"UPDATE department "
					+ "SET Name=? "
					+ "where id=?");
			
			ps.setString(1, obj.getName());
			ps.setInt(2, obj.getId());
			ps.executeUpdate();
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(
					"DELETE FROM department WHERE Id= ?");
			
			ps.setInt(1, id);
			ps.executeUpdate();
					
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(
					"SELECT * FROM department where id=?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			if(rs.next()) {
				Department dep = new Department();
				dep.setId(rs.getInt("Id"));
				dep.setName(rs.getString("Name"));
				return dep;
			}
			return null;
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(
					"SELECT * FROM department ORDER BY Name");
			
			rs = ps.executeQuery();
			List<Department> departmentList = new ArrayList<>();
			
			while(rs.next()) {
				Department dep = new Department();
				dep.setId(rs.getInt("Id"));
				dep.setName(rs.getString("Name"));
				departmentList.add(dep);
			}
			return departmentList;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}
}
