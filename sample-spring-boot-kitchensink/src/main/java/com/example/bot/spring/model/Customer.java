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
			if (!history.isEmpty())
				history.clear();
			try {
				Connection connection = KitchenSinkController.getConnection();
				PreparedStatement stmt = connection.prepareStatement
						("SELECT TourID, TourName, Date, Duration, Price, Status from CustomerRecord where UserID "
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
			    return "There is no record.";
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
		history.findHistory(userID);
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
	
	
	//delete date in customer table and update status in customer record
	//currently assume customer only provide tourID to cancel the booking
	
	public String cancelBooking(String keyword) {
		String result =null;
	try {
		Connection connection = KitchenSinkController.getConnection();
		String searching=null;
		
		//delete booking from Customer Table
		PreparedStatement stmtForCustomerTable = connection.prepareStatement
				("SELECT * FROM CustomerTable where UserID LIKE " +userID +" and TourJoined LIKE cancat('%', ?, '%'); \n"
						+"DELETE FROM CustomerTable where UserID LIKE " +userID + " and TourJoined LIKE cancat('%', ?, '%')");
		//not sure whether can run with this + and + type, need test
		
		stmtForCustomerTable.setString(1, keyword);
		ResultSet rsForCustomerTable = stmtForCustomerTable.executeQuery();
		
		
		//invalid or incorrect input. BUT seems this sentence is too long. Is it neccessary? Or how can we rewrite?
		if (!rsForCustomerTable.next()) {
			result="Sorry but you provided invalid or incorrect tour ID you want to cancel. Please tell us that you want to cancel and provide tour ID in the same sentence again if you still want to cancel.";
			rsForCustomerTable.close();
			stmtForCustomerTable.close();
			connection.close();
			return result;
		}
		
		else{
			
		rsForCustomerTable.close();
		stmtForCustomerTable.close();
		
		//update status to cancelled in customer record
		PreparedStatement stmtForCustomerRecord = connection.prepareStatement
				("UPDATE CustomerRecord SET Status='cancelled by customer' where UserID LIKE" +userID + " and TourID LIKE cancat('%', ?, '%')");
		stmtForCustomerRecord.setString(1, keyword);
		ResultSet rsForCustomerRecord = stmtForCustomerRecord.executeQuery();
		rsForCustomerRecord.close();
		stmtForCustomerRecord.close();
		
		result="Your booking has been cancelled. Hope to serve for you next time!";
		
		connection.close();
		}
	} catch (Exception e){
		log.info("Exception while reading database: {}", e.toString());
	}
	
		return result;
	}
	
}