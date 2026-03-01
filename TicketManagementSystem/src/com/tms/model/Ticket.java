package com.tms.model;
import java.time.LocalDateTime;
public class Ticket {
	private int ticketId;
	private int userId;
	private String title;
	private String description;
	private String status;
	private String priority;
	private LocalDateTime createdAt;
	
	public Ticket(int userId,String title,String description,String priority) {
		this.userId=userId;
		this.title=title;
		this.description=description;
		this.priority=priority;
		this.status="OPEN";
	}
	
	//Getter and Setter
	public int getTicketId() {return ticketId;}
	public void setTicketId(int ticketId) {this.ticketId=ticketId;}
	public int getUserId() {return userId;}
	public String getTitle() {return title;}
	public String getDescription() {return description;}
	public String getStatus() {return status;}
	public String getPriority() {return priority;}
}
