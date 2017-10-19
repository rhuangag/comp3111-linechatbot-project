package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Booking {
	//no data member for this class
	
	//Methods
	
	//TODO
	//The 1st step of booking. Return an output to ask the date of the tour
	public String askForDate() {
		
		return null;
	}
	
	//TODO
	//The 2nd step of booking. Record the date in the temporary database and return an output to ask no. of adults
    public String askForAdults() {
    	    
    	    return null;
    }
    
    //TODO
    //The 3rd step of booking. Record the no. of adults in the temporary database and return an output to ask no. of children(0-3)
	public String askForChildrent(){
		
		return null;
	}
	
	//TODO
	//The 4th step of booking. Record the no. of children in the temporary database and return an output to ask no. of teenagers(4-11)
	public String askForTeenager() {
		
		return null;
	}
	
	//TODO
	//The 5th step of booking. Record the no. of teenagers in the temporary database, use calculate() to calculate the fee,
	//and return an output including all the information collected and the fee to double check with the customer
	public String doubleCheck() {
		
		return null;
	}
	
	//TODO
	//The 6th(last) step of booking. Transfer all the data recorded in the temporary database to the booking table,
	//return an output to notify the customer that this booking is confirmed, and ask for the feedback
	public String confirm() {
		
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
