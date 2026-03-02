package com.tms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.tms.model.Ticket;
import com.tms.util.DBconnection;
public class TicketDAO {
	public boolean createTicket(Ticket ticket) {
		String query="INSERT INTO tickets (user_id,title,description,priority) VALUES(?,?,?,?)";
		try(Connection conn=DBconnection.getConnection();
				PreparedStatement ps=conn.prepareStatement(query)){
			ps.setInt(1, ticket.getUserId());
			ps.setString(2,ticket.getTitle());
	        ps.setString(3,ticket.getDescription());
	        ps.setString(4,ticket.getPriority());
	        
	        int rows=ps.executeUpdate();
	        return rows>0;
	            
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public List<Ticket> getTicketsByUser(int userId) {

	    List<Ticket> tickets = new ArrayList<>();
	    String query = "SELECT * FROM tickets WHERE user_id = ?";

	    try (Connection conn = DBconnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(query)) {

	        ps.setInt(1, userId);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            Ticket ticket = new Ticket(
	                    rs.getInt("user_id"),
	                    rs.getString("title"),
	                    rs.getString("description"),
	                    rs.getString("priority")
	            );

	            ticket.setTicketId(rs.getInt("ticket_id"));
	            tickets.add(ticket);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return tickets;
	}
	
	public List<Ticket> getAllTickets(){
		List<Ticket> tickets=new ArrayList<>();
		String query="SELECT * FROM tickets";
		
		try(Connection conn=DBconnection.getConnection();
				PreparedStatement ps=conn.prepareStatement(query);
				ResultSet rs=ps.executeQuery()){
			while(rs.next()) {
				Ticket ticket=new Ticket(rs.getInt("user_id"),
						rs.getString("title"),
						rs.getString("description"),
						rs.getString("priority"));
				ticket.setTicketId(rs.getInt("ticket_id"));
				tickets.add(ticket);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return tickets;
	}
	
	public boolean updateTicketStatus(int ticketId,String status) {
		String query="UPDATE tickets SET status =? WHERE ticket_id =?";
		try(Connection conn=DBconnection.getConnection();
				PreparedStatement ps=conn.prepareStatement(query)){
			ps.setString(1,status);
			ps.setInt(2, ticketId);
			int rows=ps.executeUpdate();
			return rows>0;
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
