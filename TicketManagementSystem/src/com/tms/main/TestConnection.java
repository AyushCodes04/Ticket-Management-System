package com.tms.main;

import java.util.*;
import com.tms.dao.UserDAO;
import com.tms.model.User;

public class TestConnection {
	public static void main(String[] args) {
		Scanner obj=new Scanner(System.in);
		UserDAO userDAO=new UserDAO();
		
		System.out.println("	LOGIN	");
		System.out.println("Enter email: ");
		String email=obj.nextLine();
		
		System.out.println("Enter Password: ");
		String password=obj.nextLine();
		
		User user=userDAO.login(email,password);
		
		if(user!=null) {
			System.out.println("Login successful!");
			System.out.println("Welcome "+user.getName());
			System.out.println("Role: "+user.getRole());
		}else {
			System.out.println("Invalid Email or Password!");
		}
		
		obj.close();
    }
}
