package com.tms.main;

import java.util.*;
import com.tms.dao.UserDAO;
import com.tms.model.User;
import com.tms.dao.TicketDAO;
import com.tms.model.Ticket;
public class MainApp {
	public static void main(String[] args) {
		Scanner obj=new Scanner(System.in);
		UserDAO userDAO=new UserDAO();
		
		System.out.println("1.Register");
		System.out.println("2.Login");
		
		System.out.println("Choose Option :");
		int choice=obj.nextInt();
		obj.nextLine();
		
		if(choice==1) {
			System.out.println("REGISTER :");
			System.out.println("Enter Name :");
			String name=obj.nextLine();
			
			System.out.println("Enter Email :");
			String email=obj.nextLine();
			
			System.out.println("Enter Password :");
			String password=obj.nextLine();
			
			User newUser=new User(name,email,password,"USER");
			boolean success=userDAO.register(newUser);
			
			if(success) {
				System.out.println("Registration Successful");
			}else {
				System.out.println("Registration Failed!");
			}
		}else if(choice ==2) {
			System.out.println("LOGIN  :");
			System.out.println("Enter email :");
			String email=obj.nextLine();
			
			System.out.print("Enter Password: ");
            String password=obj.nextLine();
            
            User user=userDAO.login(email, password);
            
            if(user!=null) {
            	System.out.println("LOGIN SUCCESSFUL");
            	System.out.println("Welcomen"+user.getName());
            	if(user.getName().equals("ADMIN")) {
            		System.out.println("\n===== ADMIN MENU =====");
            	    System.out.println("1. View All Tickets");
            	    System.out.println("2.Update ticket Status");
            	    System.out.print("Choose option: ");
            	    int adminOption = obj.nextInt();
            	    obj.nextLine();
            	    TicketDAO ticketDAO = new TicketDAO();
            	    
            	    if(adminOption==1) {
            	    	List<Ticket> tickets = ticketDAO.getAllTickets();
            	    	for(Ticket t:tickets) {
            	    		System.out.println("Ticket Id:"+t.getTicketId());
            	    		 System.out.println("User ID: " + t.getUserId());
                             System.out.println("Title: " + t.getTitle());
                             System.out.println("Priority: " + t.getPriority());
                             System.out.println("Status: " + t.getStatus());
            	    	}
            	    }else if(adminOption==2) {
            	    	System.out.print("Enter Ticket ID: ");
                        int ticketId = obj.nextInt();
                        obj.nextLine();

                        System.out.print("Enter New Status (OPEN/IN_PROGRESS/CLOSED): ");
                        String status = obj.nextLine().toUpperCase();

                        boolean updated = ticketDAO.updateTicketStatus(ticketId, status);

                        if (updated)
                            System.out.println("Ticket Updated Successfully!");
                        else
                            System.out.println("Update Failed!");
            	    }
            	} else {
            		System.out.println("\n===== USER MENU =====");
            	    System.out.println("1. Create Ticket");
            	    System.out.println("2. View My Tickets");
            	    System.out.print("Choose option: ");

            	    int option = obj.nextInt();
            	    obj.nextLine();

            	    if (option == 1) {
            	        System.out.print("Enter Ticket Title: ");
            	        String title = obj.nextLine();

            	        System.out.print("Enter Description: ");
            	        String description = obj.nextLine();

            	        System.out.print("Enter Priority (LOW/MEDIUM/HIGH): ");
            	        String priority = obj.nextLine().toUpperCase();
            	        
            	        // Create Ticket object
            	        Ticket ticket = new Ticket(user.getUserId(), title, description, priority);

            	        // Call DAO
            	        TicketDAO ticketDAO = new TicketDAO();
            	        boolean created = ticketDAO.createTicket(ticket);

            	        if (created) {
            	            System.out.println("Ticket Created Successfully!");
            	        } else {
            	            System.out.println("Failed to Create Ticket!");
            	        }
            	}
            }
            	 
         }else {
            	System.out.println("invalid Email or Password");
           }
			
		}
		obj.close();
	}
}
