package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;

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
			
			return;
		}
		
		//TODO
		//Read the vector and return all the content in the text output format
		public String getHistory() {
			
			return null;
		}
	}
	
	//Constructor
	public Customer(String ID) {
		userID=ID;
	}
	
	//Methods
	
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