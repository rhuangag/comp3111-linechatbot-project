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
						("SELECT TourID, TourName, Date, Duration, Price, Status from CustomerRecord where UserID "
								+ "like concat('%', ?, '%')");
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
		
		Vector<String> historyID = new Vector<String>();
		Vector<String> recommendationID = new Vector<String>();//store all tourID first and remove historyID then
		Vector<String> prefer_recommendationID = new Vector<String>();//according to history
		String output;
		
		//Extract history-TourID and from the database
		try {
			Connection connection = KitchenSinkController.getConnection();
			PreparedStatement stmt_history = connection.prepareStatement
					("SELECT TourID, DepartureDate, Duration, TourDescription from CustomerRecord where UserID like concat('%', ?, '%')");
			stmt_history.setString(1, userID);
			
			// 0-->hot+spring 1-->mountain
			boolean[] label = {false, false};
			// 0--># of weekday, 1--># of weekend
			int[] departureTime_number = {0, 0};
			// 0--># of '2', 1--># of '3'
			int[] duration_number = {0, 0};
			
			
			ResultSet rs_history = stmt_history.executeQuery();
			while(rs_history.next()) {
				historyID.add(rs_history.getString("TourID"));
				
				if(calcDate(rs_history.getString("DepartureDate")) == "weekday")
					departureTime_number[0]++;
				else
					departureTime_number[1]++;
				
				if(rs_history.getString("Duration") == "2")
					duration_number[0]++;
				else
					duration_number[1]++;
				if(label[0]==false)
					label[0] = (rs_history.getString("TourDescription").toLowerCase().contains("hot") && rs_history.getString("TourDescription").toLowerCase().contains("spring"));
				if(label[1]==false)
					label[1] = rs_history.getString("TourDescription").toLowerCase().contains("mountain");
			}
			
			//criteria confirmation
			String prefer_departure;
			String prefer_duration;
			if(departureTime_number[0]>=departureTime_number[1])
				prefer_departure = "weekday";
			else
				prefer_departure = "weekend";
			
			if(duration_number[0]>=duration_number[1])
				prefer_duration = "2";
			else
				prefer_duration = "3";
			
			
			//Suppose the Tour List table in excel is named as TourList in db
			PreparedStatement stmt = connection.prepareStatement
					("SELECT TourID, TourDescription, Duration, Date from TourList where UserID like concat('%',?,'%')");
			stmt.setString(1, userID);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				recommendationID.add(rs.getString("TourID"));
				if(rs.getString("Duration")==prefer_duration) {
					prefer_recommendationID.add(rs.getString("TourID"));
					continue;
				}
				if(prefer_departure=="weekend") {
					if(rs.getString("Date").toLowerCase().contains("sat")||rs.getString("Date").toLowerCase().contains("sun")) {
						prefer_recommendationID.add(rs.getString("TourID"));
						continue;
					}
				}
				if(label[0]==true) {
					if(rs.getString("TourDescription").toLowerCase().contains("hot") && rs.getString("TourDescription").toLowerCase().contains("spring")) {
						prefer_recommendationID.add(rs.getString("TourID"));
						continue;
					}
					
				}
				if(label[1]==true) {
					if(rs.getString("TourDescription").toLowerCase().contains("hot") && rs.getString("TourDescription").toLowerCase().contains("spring")) {
						prefer_recommendationID.add(rs.getString("TourID"));	
						continue;
					}
					
				}
				
				
				
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
			output = "Sorry, I have no more recommendation to you. Thanks for your support very much.";
		}
		else if(prefer_recommendationID.size() != 0) {
			Random rand = new Random(System.currentTimeMillis());
			int position = rand.nextInt(prefer_recommendationID.size());
			String outputID = prefer_recommendationID.get(position);
			//select from db
			output= Statement(outputID);			
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
	
	//helper function: convert the date to weekday/weekend
	public String calcDate(String date) {
		int index = 0;
		int month = 0;
		int day = 0;
		int year = 0;
		//day
		for(; index < date.length(); index++) {
			char temp = date.charAt(index);
		
			if(temp == '/') {
				index++;
				break;
			}
			else 
				day = day*10 + Character.getNumericValue(temp);
		}
		//month
		for(; index < date.length(); index++) {
			char temp = date.charAt(index);
		
			if(temp == '/') {
				index++;
				break;
			}
			else 
				month = month*10 + Character.getNumericValue(temp);
		}
		//year
		for(; index < date.length(); index++) {
			year = year*10 + Character.getNumericValue(date.charAt(index));
		}
		
		Calendar c = Calendar.getInstance();
		c.set(year, month-1, day);
		
		if((c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)||(c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY))
			return "weekend";
		else
			return "weekday";
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
				("SELECT * FROM CustomerTable where UserID LIKE " +userID +" and TourJoined LIKE concat('%', ?, '%'); \n"
						+"DELETE FROM CustomerTable where UserID LIKE " +userID + " and TourJoined LIKE concat('%', ?, '%')");
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
				("UPDATE CustomerRecord SET Status='cancelled by customer' where UserID LIKE" +userID + " and TourID LIKE concat('%', ?, '%')");
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