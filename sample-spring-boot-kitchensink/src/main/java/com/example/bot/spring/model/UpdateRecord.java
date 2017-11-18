package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.math.BigDecimal;
import java.math.*;


@Slf4j
public  class  UpdateRecord{
	//data member for this class
	private Customer customerBelonging;
	//Methods
	public UpdateRecord(Customer customerBelong)
	{
		this.customerBelonging = customerBelong;
	}
	//TODO
	//Store the information collected and return an output to ask for next information
	
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
	public String Payment(String information) {
		try {		
		String[] parts=information.split("-");

		if (parts.length==3) {
		String customername=parts[0];
		String tourid=parts[1];
		Double payment=Double.parseDouble(parts[2]);
		Connection connection = KitchenSinkController.getConnection();
		PreparedStatement stmt = connection.prepareStatement("update customertable set amountpaid=? where name=? and tourjoined=?");
		stmt.setDouble(1, payment); 
		stmt.setString(2, customername); 
		stmt.setString(3, tourid); 
		stmt.executeUpdate();
		stmt.close();
		connection.close();
		return "Update successfully.";}
		else
			return "Invalid input.";
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return (e.toString()+ "payment");}
	}
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