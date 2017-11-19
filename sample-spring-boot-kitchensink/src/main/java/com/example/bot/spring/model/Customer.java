package com.example.bot.spring;


import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * The class Cutomer models a the customer behavior and stores the information(user ID and customer history) of a customer.
 * 
 * 
 * 
 *
 */
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
					String result="Tour ID: "+rs.getString("TourID")+ "\nTour Name: "+rs.getString("TourName")+"\nDepartureDate: "+rs.getString("DepartureDate")+ 
							"\nDuration: "+rs.getString("Duration")+"\nPrice: "+rs.getString("Price")+"\nStatus: "+rs.getString("Status")+"\n";
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
				String result="";
				Iterator<String> iterator=history.iterator();
				while(iterator.hasNext()) {
					result=result+iterator.next()+"\n";
				}
				return result;
			}
		}
	}
	
	//Constructor
	/**
	 * Constructor of the class Customer. It will make the input as the user id of the customer.
	 * @param ID This is the user ID of the customer generated
	 */
	public Customer(String ID) {
		userID=ID;
		history=new CustomerHistory();
	}
	
	//Methods
	/**
	 * This method is used to get the user ID of the customer.
	 * @return java.lang.String This returns the user ID.
	 */
	public String getID() {
		return userID;
	}
	
	//Return the customer history from instance history
	/**
	 * This method is used to get the history of the customer.
	 * @return java.lang.String This returns a String containing the result of the search of customer history
	 */
	public String getHistory() {
		history.findHistory(userID);
		return history.getHistory();
	}
	
	//TODO
	//Analyse the customer history and return the recommendation
	/**
	 * This method analyse the previous customer records and give a recommendation 
	 * @return java.lang.Sting This returns the reply message containing the recommended tour.
	 */
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
				
				if(rs_history.getString("Duration").contains("2"))
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
			if(departureTime_number[0]<departureTime_number[1])
				prefer_departure = "weekend";
			else
				prefer_departure = "weekday";
			
			if(((duration_number[0]==0) && (duration_number[1]==0)) || (duration_number[0] == duration_number[1]))
				prefer_duration = "0";
			else if(duration_number[0]>duration_number[1])
				prefer_duration = "2";
			else
				prefer_duration = "3";
			
			
			//Suppose the Tour List table in excel is named as TourList in db
			PreparedStatement stmt = connection.prepareStatement
					("SELECT TourID, TourDescription, Duration, Date from TourList");
			
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				recommendationID.add(rs.getString("TourID"));
				if(rs.getString("Duration").contains(prefer_duration)) {
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
					if(rs.getString("TourDescription").toLowerCase().contains("mountain") ) {
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
		for(String i: historyID) {
			prefer_recommendationID.remove(i);
		}
		
		
		if(recommendationID.isEmpty()) {
			output = "Sorry, I have no more recommendation to you. Thanks for your support very much.";
		}
		else if(!prefer_recommendationID.isEmpty()) {
			
			//Random rand = new Random(System.currentTimeMillis());
			//int position = rand.nextInt(prefer_recommendationID.size());
			//String outputID = prefer_recommendationID.get(position);
			output = Statement(prefer_recommendationID.get(0));
			//select from db
			//output= Statement(outputID);			
		}
		else {
			//Random rand = new Random(System.currentTimeMillis());
			//int position = rand.nextInt(recommendationID.size());
			//String outputID = recommendationID.get(position);
			output = Statement(recommendationID.get(0));
			//select from db
			//output= Statement(outputID);
		}
		
		
		
		return output;
	}
	
	
	//helper funtion -- input->ID, output->string of all details
	private String Statement(String tourID) {
		String result = null;
		
		try {
			Connection connection = KitchenSinkController.getConnection();
			PreparedStatement stmt = connection.prepareStatement
					("SELECT TourID, TourName, TourDescription, Duration, Date, WeekendPrice, WeekdayPrice from TourList where TourID like concat('%', ?, '%')");
			
			stmt.setString(1, tourID);
			
			ResultSet rs = stmt.executeQuery();
			
			
			while(rs.next()) {
				result="Tour ID: "+rs.getString("TourID")+ "\nTour Name: "+rs.getString("TourName")+"\nTour Description: "+rs.getString("TourDescription")+ 
						"\nDuration: "+rs.getString("Duration")+"\nDate: "+rs.getString("Date")+"\nWeekend Price: "+rs.getString("WeekendPrice")+"\nWeekday Price: "+rs.getString("WeekdayPrice");
				
			}
			
			
			rs.close();
			stmt.close();
			connection.close();
		} catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
		}
		
		if(result==null)
			result = tourID;
		return result;
	}
	
	//helper function: convert the date to weekday/weekend
	private String calcDate(String date) {
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
	/**
	 * This method cancels one booking with the input tour id and user id by marking the booking in the database as "cancelled by customer".
	 * It also reply the customer to inform that whether the cancel is successful.
	 * @param keyword This is the tour id of the tour that is going to be cancelled
	 * @return java.lang.String This is the reply message to indicate whether the cancellation is successful.
	 */
	public String cancelBooking(String keyword) {
		String result =null;
		try {
		Connection connection = KitchenSinkController.getConnection();
		//delete booking from Customer Table
		PreparedStatement stmtForCustomerTable = connection.prepareStatement
		("SELECT * FROM CustomerTable where UserID =? and TourJoined like concat ('%',?,'%')");
		//not sure whether can run with this + and + type, need test
		stmtForCustomerTable.setString(1, userID);
		stmtForCustomerTable.setString(2, keyword);
		ResultSet rsForCustomerTable = stmtForCustomerTable.executeQuery();
		PreparedStatement stmtForUpdateCustomerTable=connection.prepareStatement
		("Update CustomerTable SET Status='cancelled by customer' where UserID =? and TourJoined like concat ('%',?,'%')");
		stmtForUpdateCustomerTable.setString(1, userID);
		stmtForUpdateCustomerTable.setString(2, keyword);
		stmtForUpdateCustomerTable.executeUpdate();
		
		if (rsForCustomerTable.next()) {
			//update status to cancelled in customer record
			PreparedStatement stmtForCustomerRecord = connection.prepareStatement
			("UPDATE CustomerRecord SET Status='cancelled by customer' where UserID =? and TourID=?");
			stmtForCustomerRecord.setString(1, userID);
			stmtForCustomerRecord.setString(2, keyword);
			stmtForCustomerRecord.executeUpdate();
			stmtForCustomerRecord.close();
			result="Your booking has been cancelled. Hope to serve for you next time!";
		}
		else{
		//invalid or incorrect input. BUT seems this sentence is too long. Is it neccessary? Or how can we rewrite?
		result="Sorry but you provided invalid or incorrect tourID. \n"
				+"Please tell me that you want to cancel and provide tour ID in the same sentence again.\n\n"
				+ "If you are not sure for your tourID, you may ask me to search for your booking histroy";
		}
		stmtForUpdateCustomerTable.close();
		rsForCustomerTable.close();
		stmtForCustomerTable.close();
		connection.close();
		} catch (Exception e){
		log.info("Exception while reading database: {}", e.toString());}

		return result;
		}

		
	
}
