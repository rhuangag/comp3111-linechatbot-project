package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Slf4j
public class Customer{
	//Declaration of data members and private class "CustomerHistory"
    private String userID;
    CustomerHistory history;
    
	private class CustomerHistory{
		Vector<String> history;
		
		public CustomerHistory() {
			
		}
		
		
		//TODO
		//Find the customer history in the database and put each row as a string in the vector "history"
		public void findHistory(String userID) {
			try {
				Connection connection = KitchenSinkController.getConnection();
				PreparedStatement stmt = connection.prepareStatement
						("SELECT TourID, TourName, Date, Duration, Price, Status where UserID "
								+ "like cancat('%', ?, '%')");
				stmt.setString(1, userID);
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					String result="Tour ID: "+rs.getString("TourID")+ "\tTour Name: "+rs.getString("TourName")+"\tDate: "+rs.getString("Date")+ 
							"\tDuration: "+rs.getString("Duration")+"\tPrice: "+rs.getString("Price")+"\tStatus: "+rs.getString("Status")+"\n";
					history.add(result);
				}
				rs.close();
				stmt.close();
				connection.close();
			} catch (Exception e){
				log.info("Exception while reading database: {}", e.toString());
			}
			return;
		}
		
		//TODO
		//Read the vector and return all the content in the text output format
		public String getHistory() {
			if(history.isEmpty())
			    return null;
			else {
				String result=null;
				Iterator<String> iterator=history.iterator();
				while(iterator.hasNext()) {
					result=result+iterator.next()+"\n";
				}
				return result;
			}
		}
	}
	
	//Constructor
	public Customer(String ID) {
		userID=ID;
		history=new CustomerHistory();
	}
	
	//Methods
	
	//Return the customer history from instance history
	public String getHistory() {
		return history.getHistory();
	}
	
	//TODO
	//Analyse the customer history and return the recommendation
	public String getRecommendation() {
		
		return null;
	}
	
	//TODO
	//Cancel the booking in the database, mark the cancellation in the customer record
	//and return an output to inform that the booking is cancelled
	public String cancelBooking(Keyword keyword) {
		return null;
	}
	
}