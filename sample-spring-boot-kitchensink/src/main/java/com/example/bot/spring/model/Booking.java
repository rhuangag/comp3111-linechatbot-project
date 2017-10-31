package com.example.bot.spring*;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Slf4j
public class Booking {
	//data member for this class
	private Customer customerBelonging;
	private String tourID; // customer class not enrolled so far
	//Methods
	public Booking(Customer customerBelong, String tourID)
	{
		this.tourID = tourID;
		this.customerBelonging = customerBelong;
		String ID = I.parseLong("String") + customerBelong.getID(); // can not find getID function so far
		this.bookingID = ID;
	}
	//TODO
	//Store the information collected and return an output to ask for next information
	
	public String askForInformation(String type, String Information) {
		switch type:
		{
			case "Yes":
				return this.askForDate();
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
				return null
		}
	}
	
	
	//TODO
	//The 1st step of booking. Return an output to ask the date of the tour
	public String askForDate() {
		Connection connection = getConnection();
		PrepareStatement
		String createdb = "CREATE table " +this.customerBelonging.getID() + " (customerID varchar(20), "
				+ " tourID varchar(10), dateDeparture "+ ""
		
		
		String asking = "When are you planning to go for the trip?"
		String queryDate = " select DATE from bookingtable where 
		
		PreparedStatement stmt = connection.prepareStatement(queryDate);
		//use a static data member to record the no.
		String asking = "May I know your name?"
		stmt.executeQuery();
		connection.close();
		return asking;
	}
	
	//TODO
	//The 2nd step of booking. Record the date in the temporary database and return an output to ask name of the customer
    public String askForName(String date) {
    	Connection connection = getConnection();
		String queryAnsDate = " insert into questionRecord (" + this.customerBelonging.getID()
			+ ", date) values " + date;
		
		PreparedStatement stmt = connection.prepareStatement(queryAnsDate);
		//use a static data member to record the no.
		String asking = "May I know your name?"
		stmt.executeQuery();
		connection.close();
    	    return asking;
    }
    
    //TODO
    //The 3rd step of booking. Record the name in the temporary database and return an output to ask ID of the customer
    public String askForID(String name){
    	Connection connection = getConnection();
		String queryAnsName = " insert into questionRecord (" + this.customerBelonging.getID()
			+ ", name) values " + name;
		
		PreparedStatement stmt = connection.prepareStatement(queryAnsName);
		//use a static data member to record the no.
		String asking = "Could you please tell us your ID?"
		stmt.executeQuery();
		connection.close();
    	    return asking;
    }

	
	//TODO
	//The 2nd step of booking. Record the date in the temporary database and return an output to ask no. of adults
    public String askForAdults(String ID) {
    	Connection connection = getConnection();
		String queryAnsID = " insert into questionRecord (" + this.customerBelonging.getID()
			+ ", ID) values " + ID;
		
		PreparedStatement stmt = connection.prepareStatement(queryAnsID);
		//use a static data member to record the no.
		String asking = "Could you tell us how many adults will be in the trip?"
		stmt.executeQuery();
		connection.close();
    	    return asking;
    }
    
    //TODO
    //The 3rd step of booking. Record the no. of adults in the temporary database and return an output to ask no. of children(0-3)
	public String askForChildrent(String numberOfAdults){
		Connection connection = getConnection();
		int number = Integer.parseInt(numberOfAdults);
		String queryAns = " insert into questionRecord (" + this.customerBelonging.getID()
			+ ", numberOfAdults) values " + number; 
		
		PreparedStatement stmt = connection.prepareStatement(queryAns);
		String asking = "Could you tell us how many children will be in the trip?"
		stmt.executeQuery();
		connection.close();
    	return asking;		
	}
	
	//TODO
	//The 4th step of booking. Record the no. of children in the temporary database and return an output to ask no. of Toodlers(4-11)
	public String askForToodler(String numberOfChildren) {
		Connection connection = getConnection();
		int number = Integer.parseInt(numberOfChildren);
		String queryAns = " insert into questionRecord (" + this.customerBelonging.getID()
			+ ", numberOfChildren) values " + number; 
		
		PreparedStatement stmt = connection.prepareStatement(queryAns);
		String asking = "Could you tell us how many children will be in the trip?"
		stmt.executeQuery();
		connection.close();
    	return asking;		
	}
	
	
	
	//TODO
	//The 5th step of booking. Record the no. of Toodlers in the temporary database, use calculate() to calculate the fee,
	//and return an output including all the information collected and the fee to double check with the customer
	public String doubleCheck(String numberOfToodlers ) {
		Connection connection = getConnection();
		int number = Integer.parseInt(numberOfToodlers);
		String queryAns = " insert into questionRecord (" + this.customerBelonging.getID()
			+ ", numberOfToodlers) values " + number; 
		String queryPrice = "select price from tourlist where "
		PreparedStatement stmt = connection.prepareStatement(queryAns);
		
		stmt.executeQuery();
		connection.close();
    	return asking;				
	}
	
	//TODO
	//The 6th(last) step of booking. Transfer all the data recorded in the temporary database to the booking table,
	//return an output to notify the customer that this booking is confirmed, and ask for the feedback
	public String confirm(String checkAnswer) {
		
		
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
