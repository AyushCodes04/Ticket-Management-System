package com.tms.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.tms.model.User;
import com.tms.util.DBconnection;

public class UserDAO {
	public User login(String email,String password) {
		String query="SELECT * FROM users where email = ? AND password = ?";
		try(Connection conn=DBconnection.getConnection();
			PreparedStatement ps=conn.prepareStatement(query)){
			ps.setString(1,email);
			ps.setString(2, password);;
			
			ResultSet rs=ps.executeQuery();
			
			if(rs.next()) {
				User user=new User(rs.getString("name"),
						rs.getString("email"),
						rs.getString("password"),
						rs.getString("role"));
				user.setUserId(rs.getInt("user_id"));
				return user;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean register(User user) {
		String query="INSERT INTO users(name,email,password,role) VALUES (?,?,?,?)";
		
		try(Connection conn=DBconnection.getConnection();
				PreparedStatement ps=conn.prepareStatement(query)){
			ps.setString(1, user.getName());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getPassword());
			ps.setString(4, user.getRole());
			
			int rows=ps.executeUpdate();
			
			return rows>0;
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
