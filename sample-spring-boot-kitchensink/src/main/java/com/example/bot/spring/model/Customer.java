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
			history=new Vector<String>();
		}
		
		
		//TODO
		//Find the customer history in the database and put each row as a string in the vector "history"
		public void findHistory(String userID) {
			if (!history.isEmpty())
				history.clear();
			try {
				Connection connection = KitchenSinkController.getConnection();
				PreparedStatement stmt = connection.prepareStatement
						("SELECT TourID, TourName, DepartureDate, Duration, Price, Status from CustomerRecord where UserID "
								+ "like concat('%', ?, '%')");
				stmt.setString(1, userID);
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					String result="Tour ID: "+rs.getString("TourID")+ "\tTour Name: "+rs.getString("TourName")+"\tDepartureDate: "+rs.getString("DepartureDate")+ 
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
	public String getID() {
		return userID;
	}
	
	//Return the customer history from instance history
	public String getHistory() {
		history.findHistory(userID);
		return history.getHistory();
	}
	
	//TODO
	//Analyse the customer history and return the recommendation
	public String getRecommendation() {
		
		Vector<String> historyID = new Vector<String>();
		Vector<String> recommendationID = new Vector<String>();//store all tourID first and remove historyID then
		String output;
		
		//Extract history-TourID and from the database
		try {
			Connection connection = KitchenSinkController.getConnection();
			PreparedStatement stmt_history = connection.prepareStatement
					("SELECT TourID from CustomerRecord where UserID "
							+ "like concat('%', ?, '%')");
			stmt_history.setString(1, userID);
			ResultSet rs_history = stmt_history.executeQuery();
			while(rs_history.next()) {
				historyID.add(rs_history.getString("TourID"));
			}
			
			//emmm...not sure
			
			//Suppose the Tour List table in excel is named as TourList in db
			PreparedStatement stmt = connection.prepareStatement
					("SELECT TourID from TourList where UserID like concat('%',?,'%')");
			stmt.setString(1, userID);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				recommendationID.add(rs.getString("TourID"));
			}
			
			rs_history.close();
			stmt_history.close();
			
			rs.close();
			stmt.close();
			connection.close();
		} catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
		}	
		
		//remove the historyID from all tourID
		for(String i: historyID) {
			recommendationID.remove(i);
		}
		
		if(recommendationID.size() == 0) {
			output = "Sorry, I have no more recommendation to you. Thanks for your support very much!";
		}
		else {
			Random rand = new Random(System.currentTimeMillis());
			int position = rand.nextInt(recommendationID.size());
			String outputID = recommendationID.get(position);
			//select from db
			output= Statement(outputID);
		}
		
		
		return output;
	}
	
	
	//helper funtion -- input->ID, output->string of all details
	public String Statement(String tourID) {
		String result = null;
		
		try {
			Connection connection = KitchenSinkController.getConnection();
			PreparedStatement stmt = connection.prepareStatement
					("SELECT TourID, TourName, TourDescription, Days, Date, WeekendPrice, WeekdayPrice from TourList where UserID = "+ this.userID +" AND TourID = "+ tourID +";");
			
			ResultSet rs = stmt.executeQuery();
			
			
			while(rs.next()) {
				result="Tour ID: "+rs.getString("TourID")+ "\tTour Name: "+rs.getString("TourName")+"\tTour Description: "+rs.getString("TourDescription")+ 
						"\tDays: "+rs.getString("Days")+"\tDate: "+rs.getString("Date")+"\tWeekend Price: "+rs.getString("WeekendPrice")+"\tWeekday Price: "+rs.getString("WeekdayPrice")+"\n";
				
			}
			
			
			rs.close();
			stmt.close();
			connection.close();
		} catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
		}
		return result;
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
		String userID="wwual";
		//delete booking from Customer Table
		PreparedStatement stmtForCustomerTable = connection.prepareStatement
		("SELECT * FROM CustomerTable where UserID= " +userID +" and TourJoined LIKE cancat('%', ?, '%')");
		//not sure whether can run with this + and + type, need test
		stmtForCustomerTable.setString(1, keyword);
		ResultSet rsForCustomerTable = stmtForCustomerTable.executeQuery();
		PreparedStatement stmtForUpdateCustomerTable=connection.prepareStatement
		("Update CustomerTable SET Status='cancelled by customer' where UserID= " +userID + " and TourJoined LIKE cancat('%', ?, '%')");
		stmtForUpdateCustomerTable.executeUpdate();
		//invalid or incorrect input. BUT seems this sentence is too long. Is it neccessary? Or how can we rewrite?
		if (!rsForCustomerTable.next()) {
		result="Sorry but you provided invalid or incorrect tour ID you want to cancel. Please tell us that you want to cancel and provide tour ID in the same sentence again if you still want to cancel. If you are not sure for your tourID, you may ask me to search for your histroy";
		}
		else{
		rsForCustomerTable.beforeFirst();
		//update status to cancelled in customer record
		PreparedStatement stmtForCustomerRecord = connection.prepareStatement
		("UPDATE CustomerRecord SET Status='cancelled by customer' where UserID=" +userID + " and TourID LIKE cancat('%', ?, '%')");
		stmtForCustomerRecord.setString(1, keyword);
		ResultSet rsForCustomerRecord = stmtForCustomerRecord.executeQuery();
		rsForCustomerRecord.close();
		stmtForCustomerRecord.close();
		result="Your booking has been cancelled. Hope to serve for you next time!";
		}
		stmtForUpdateCustomerTable.close();
		rsForCustomerTable.close();
		stmtForCustomerTable.close();
		connection.close();
		} catch (Exception e){
		log.info("Exception while reading database: {}", e.toString());
		return e.toString();
		}

		return result;
		}

		
	
}
