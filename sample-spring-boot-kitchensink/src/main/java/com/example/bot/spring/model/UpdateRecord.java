package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.math.BigDecimal;
import java.math.*;

/**
 * 
 * The class UpdateRecord can allow employee of the companey update the payment data of customer, set discount event or ask for question record from customer.
 * This class is involved in the mediator pattern: mediator TextHandler will recognize if the customer is employee who activate the client channel by have correct password input. 
 * If yes, TextHandler will allow employee to update date about customer's payment or discount tour setting by calling this class.
 * 
 */
@Slf4j
public  class  UpdateRecord{
	//data member for this class
	private Customer customerBelonging;
	//Methods

  /**
   * Constructor of class UpdateRecord. It initializes the data members of the object.
   * @param customerBelong this is an object of Customer to allow UpdateRecord get information of Customer.
   */
	public UpdateRecord(Customer customerBelong)
	{
		this.customerBelonging = customerBelong;
	}
	//TODO
	//Store the information collected and return an output to ask for next information
	/**
	 * This function return format example of input to get information asked by employee.
	 * @param phase int this is the type recognized TextHandler about payment or discount.
	 * @param information java.lang.String This is the information needed to update payment of customers.
	 * @return java.lang.String This returns the infotmation of different message for different type of information asked by employee of the company.
	 */
	public String askForInformation(int phase, String information) {
		switch (phase)
		{
			case 103:
				return this.Payment(information);
			case 104:
				return this.Discount(information);
			default:
				return "Invalid input.";}
	}
	
	
	//TODO
	//The 1st step of booking. Return an output to ask the date of the tour
	/**
	 * 
	 * @param information java.lang.String This is the information of customer whose payment needed to be update provided by employee.
	 * @return java.lang.String This return the message of update condition, whether success or failed due to invalid input information provided by employee.
	 */
	public String Payment(String information) {
		try {		
		String[] parts=information.split("-");

		if (parts.length==3) {
		String customername=parts[0];
		String tourid=parts[1];
		Double payment=Double.parseDouble(parts[2]);
		Connection connection = KitchenSinkController.getConnection();
		PreparedStatement searchStmt = connection.prepareStatement("select name from customertable where name=? and tourjoined=?");
		searchStmt.setString(1, customername); 
		searchStmt.setString(2, tourid);
		ResultSet rsForSearch=searchStmt.executeQuery();
		if(rsForSearch.next()) {
		PreparedStatement stmt = connection.prepareStatement("update customertable set amountpaid=(amountpaid+?) where name=? and tourjoined=?");
		stmt.setDouble(1, payment); 
		stmt.setString(2, customername); 
		stmt.setString(3, tourid); 
		stmt.executeUpdate();
		stmt.close();
		
		searchStmt.close();
		rsForSearch.close();
		
		PreparedStatement searchStmtCheckIfPaid = connection.prepareStatement("select * from customertable where name=? and tourjoined=? and (amountPaid-tourfee)>-0.1 and status='booked'");
		searchStmtCheckIfPaid.setString(1, customername); 
		searchStmtCheckIfPaid.setString(2, tourid);
		ResultSet rsStmtCheckIfPaid=searchStmtCheckIfPaid.executeQuery();
		if(rsStmtCheckIfPaid.next()) {
			int number=0;
			number+=rsStmtCheckIfPaid.getInt("numberofadult");
			number+=rsStmtCheckIfPaid.getInt("numberofchildren");
			number+=rsStmtCheckIfPaid.getInt("numberoftoodler");
			PreparedStatement updateStmtCheckIfPaid = connection.prepareStatement("update customertable set status='paid' where name=? and tourjoined=? and (amountPaid-tourfee)>-0.1 and (amountPaid-tourfee)<0.1");
			updateStmtCheckIfPaid.setString(1, customername); 
			updateStmtCheckIfPaid.setString(2, tourid);
			updateStmtCheckIfPaid.executeUpdate();
			updateStmtCheckIfPaid.close();
			
			PreparedStatement updateStmtStatusInBookingTable = connection.prepareStatement("update bookingtable set confirmedcustomer=(confirmedcustomer+?) where booktableid=?");
			updateStmtStatusInBookingTable.setInt(1, number);
			updateStmtStatusInBookingTable.setString(2, tourid);
			updateStmtStatusInBookingTable.executeUpdate();
			updateStmtStatusInBookingTable.close();
			
		}
		
		
		searchStmtCheckIfPaid.close();
		rsStmtCheckIfPaid.close();
		
		connection.close();
		
		
		return "Update successfully.";}
		else {
		searchStmt.close();
		rsForSearch.close();
		connection.close();}
		}
		return "Invalid input. Maybe there are some error in the information provided";
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return (e.toString()+ "payment");}
}
	
	/**
	 * 
	 * @param java.lang.String This is the information tour that needed to be offered discount provided by employee.
	 * @return java.lang.String This return the message of event setting condition, whether success or failed due to invalid input information provided by employee.
	 */
	public String Discount(String information) {
		try {		
		String[] parts=information.split("-");

		
		
		if (parts.length==7) {
			String tourid=parts[0];
			String ratestring=parts[1];
			Double ratenumber=Double.parseDouble(parts[2]);
			int capacity=Integer.parseInt(parts[3]);
			int seat=Integer.parseInt(parts[4]);
			String date=parts[5];
			String time=parts[6];
		Connection connection = KitchenSinkController.getConnection();
		PreparedStatement stmt = connection.prepareStatement("insert into discounttourlist values (?,?,?,?,?,?,?)");
		
		stmt.setString(1, tourid); 
		stmt.setString(2, ratestring); 
		stmt.setDouble(3, ratenumber); 
		stmt.setInt(4, capacity);
		stmt.setInt(5, seat);
		stmt.setString(6, date); 
		stmt.setString(7, time); 
		stmt.executeUpdate();
		stmt.close();
		connection.close();
		return "Create a new discount event successfully.";}
		else
			return "Invalid input.";
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return (e.toString()+ "payment");}
	}
}