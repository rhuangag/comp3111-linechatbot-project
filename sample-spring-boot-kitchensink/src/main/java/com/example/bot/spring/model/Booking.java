package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Slf4j
public class Booking {
	//data member for this class
	private Customer customerBelonging;
	//Methods
	public Booking(Customer customerBelong)
	{
		this.customerBelonging = customerBelong;
	}
	//TODO
	//Store the information collected and return an output to ask for next information
	
	public String askForInformation(String type, String information) {
		switch (type)
		{
			case "Yes":
				return this.askForDate(information);
			case "date":
				return this.askForName(information);
			case "name":
				return this.askForID(information);
			case "ID":
				return this.askForAdults(information);
			case "#adults":
				return this.askForChildrent(information);
			case "#children":
				return this.askForToodler(information);
			case "#Toodler":
				return this.doubleCheck(information);
			case "doubleCheck":
				return this.confirm(information);
			case "confirm":
				return this.getFeedback();
			
			default:
				return null;
		}
	}
	
	
	//TODO
	//The 1st step of booking. Return an output to ask the date of the tour
	public String askForDate(String tourID) {
		try {
		Connection connection = KitchenSinkController.getConnection();
		String createdb = "CREATE table " +this.customerBelonging.getID() + " (customerID varchar(10), "
				+ " tourID varchar(10), dateDeparture varchar(20), CustomerName varchar(20), ID varchar(20), "
				+ "Adults Int, Children Int, Toodlers Int)";
		String insertdb = "Insert Into " + this.customerBelonging.getID() + " VALUES (" 
				+ this.customerBelonging.getID() + ", " + tourID + ", ' ', ' ', ' ', 0, 0, 0)";
		
		String asking = "When are you planning to go for the trip? (The dates available are: \n";
		String queryDate = "Select Distinct departuredate from bookingtable where (tourid == " + tourID +
				" )";
		PreparedStatement stmt1 = connection.prepareStatement(createdb);
		PreparedStatement stmt2 = connection.prepareStatement(insertdb);
		PreparedStatement stmt3 = connection.prepareStatement(queryDate);

		String asking2 = "May I know your name?";
		stmt1.executeQuery();
		stmt2.executeQuery();
		ResultSet rs = stmt3.executeQuery();
		while (rs.next())
		{
			asking = asking + rs.getString(0) + "\n";
		}
		connection.close();
		return asking;
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());}
    	return null;
	}
	
	//TODO
	//The 2nd step of booking. Record the date in the temporary database and return an output to ask name of the customer
    public String askForName(String date) {
    	try {
    	Connection connection = KitchenSinkController.getConnection();
		String InsertDB = "Update " + this.customerBelonging.getID() + " SET dateDeparture = " + date;
		
		PreparedStatement stmt = connection.prepareStatement(InsertDB);
		String asking = "May I know your name?";
		stmt.executeQuery();
		connection.close();
    	    return asking;
    	}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());}
    	return null;
    }
    
    //TODO
    //The 3rd step of booking. Record the name in the temporary database and return an output to ask ID of the customer
    public String askForID(String name){
    	try {
    		Connection connection = KitchenSinkController.getConnection();
    		String InsertDB = "Update " + this.customerBelonging.getID() + " SET CustomerName = " + name;
    		
    		PreparedStatement stmt = connection.prepareStatement(InsertDB);
    		String asking = "May I know your ID?";
    		stmt.executeQuery();
    		connection.close();
        	    return asking;    	
    
    	}catch (Exception e){
    			log.info("Exception while reading database: {}", e.toString());}
        	return null;
    }

	
	//TODO
	//The 2nd step of booking. Record the date in the temporary database and return an output to ask no. of adults
    public String askForAdults(String ID) {
    	try {
    		Connection connection = KitchenSinkController.getConnection();
    		String InsertDB = "Update " + this.customerBelonging.getID() + " SET ID = " + ID;
    		
    		PreparedStatement stmt = connection.prepareStatement(InsertDB);
    		String asking = "Could you please tell us the number of adults?";
    		stmt.executeQuery();
    		connection.close();
        	    return asking;    	
    	    }catch (Exception e){
    			log.info("Exception while reading database: {}", e.toString());}
        	return null;
    }
    
    //TODO
    //The 3rd step of booking. Record the no. of adults in the temporary database and return an output to ask no. of children(0-3)
	public String askForChildrent(String numberOfAdults){
		try {
    		Connection connection = KitchenSinkController.getConnection();
    		String InsertDB = "Update " + this.customerBelonging.getID() + " SET Adults = " + numberOfAdults;
    		
    		PreparedStatement stmt = connection.prepareStatement(InsertDB);
    		String asking = "Could you please tell us the number of children?";
    		stmt.executeQuery();
    		connection.close();
        	    return asking;    
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());}
    	return null;
	}
	
	//TODO
	//The 4th step of booking. Record the no. of children in the temporary database and return an output to ask no. of Toodlers(4-11)
	public String askForToodler(String numberOfChildren) {
		try {
    		Connection connection = KitchenSinkController.getConnection();
    		String InsertDB = "Update " + this.customerBelonging.getID() + " SET Children = " + numberOfChildren;
    		
    		PreparedStatement stmt = connection.prepareStatement(InsertDB);
    		String asking = "Could you please tell us the number of toodlers?";
    		stmt.executeQuery();
    		connection.close();
        	    return asking;    
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());}
    	return null;
	}
	//TODO
	//The 5th step of booking. Record the no. of Toodlers in the temporary database, use calculate() to calculate the fee,
	//and return an output including all the information collected and the fee to double check with the customer
	public String doubleCheck(String numberOfToodlers ) {
		try {
		Connection connection = KitchenSinkController.getConnection();
		String InsertDB = "Update " + this.customerBelonging.getID() + " SET Toodlers = " + numberOfToodlers;
		PreparedStatement stmt1 = connection.prepareStatement(InsertDB);
		stmt1.executeQuery();
		
		
		PreparedStatement queryTour = connection.prepareStatement("SELECT tourID, DepartureDate,"
				+ "Adults, Children, Toodlers, CustomerName, ID from " + this.customerBelonging.getID());
		ResultSet tour = queryTour.executeQuery();
		

		//String queryAns = " insert into questionRecord (" + this.customerBelonging.getID()
		//	+ ", numberOfToodlers) values " + number; 

		PreparedStatement queryPrice = connection.prepareStatement("select price from BookingTable where "
				+ "(tourID like" + tour.getString(1) + "and " + "date like " + tour.getString(2) + ")");
		ResultSet pricers = queryPrice.executeQuery();
		double price = pricers.getInt(1);
		int NumA = tour.getInt(3);
		int NumC = tour.getInt(4);
		int NumT = tour.getInt(5);
		double finalcost = NumA*price + NumC*0.8*price;
		String DoubleCheckList =
				"Please check the booking status: \n"
				+ "Customer: " + tour.getString(6) + "\n"
				+ "Tour ID: " + tour.getString(1) + "\n"
				+ "Customer ID: " + tour.getString(7) + "\n"
				+ "Departure Date: " + tour.getString(2) + "\n"
				+ "Number of Adults: " + tour.getInt(3) + "\n"
				+ "Number of Children: " + tour.getInt(4) + "\n"
				+ "Number of Toodlers: " + tour.getInt(5) + "\n"
				+ "Total Price: " + finalcost + "(HKD)\n"
				+ "Please check if they are correct.If correct, please reply 'Confirmed'.";
		connection.close();
		return DoubleCheckList;
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());}
    	return null;
	}
	
	//TODO
	//The 6th(last) step of booking. Transfer all the data recorded in the temporary database to the booking table,
	//return an output to notify the customer that this booking is confirmed, and ask for the feedback
	public String confirm(String checkAnswer) {
		
		return null;
	}
	//TODO
	//Record the feedback, transter all the data in the log database to the feedback table, delete the log table,
	//and return an output to thank the customer
	public String getFeedback() {
		
		return null;
	}
	
	//TODO
	//Calculate and return the fee based on the no. of participants
	public int calculate() {
		
		return 0;
	}
	
	//TODO
	//During the booking, if customer enter something unrelated to the booking information, stop current booking,
	//delete data in temporary database and return an output to notify the customer that this booking is cancelled
	public String breakBooking() {
		
		return null;
	}
	
	
	
	
	
	
	
	
	
}
