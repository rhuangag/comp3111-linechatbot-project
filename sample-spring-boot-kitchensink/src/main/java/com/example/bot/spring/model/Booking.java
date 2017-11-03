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
	
	public String askForInformation(int phase, String information) {
		switch (phase)
		{
			case 9:
				return this.askForDate(information);
			case 10:
				return this.askForName(information);
			case 11:
				return this.askForID(information);
			case 12:
				return this.askage(information);
			case 13:
				return this.askphone(information);
			case 14:
				return this.askForAdults(information);
			case 15:
				return this.askForChildrent(information);
			case 16:
				return this.askForToodler(information);
			case 17:
				return this.askrequest(information);
			case 18:
				return this.doubleCheck(information);
			case 19:
				return this.confirm(information);
			case 20:
				return this.getFeedback(information);
			
			default:
				return this.breakBooking();}
	}
	
	
	//TODO
	//The 1st step of booking. Return an output to ask the date of the tour
	public String askForDate(String tourID) {
		try {
		Connection connection = KitchenSinkController.getConnection();
		String createdb = "CREATE table "+this.customerBelonging.getID() +"(customerID varchar(50), "
				+ " tourID varchar(10), dateDeparture varchar(20), CustomerName varchar(20), ID varchar(20), "
				+ " phone varchar(12), Adults Int, Children Int, Toodlers Int, SpecialRequest varchar(100)"
				+ ", age varchar(3), fee float)";
		String insertdb = "Insert Into " + this.customerBelonging.getID() + "(customerID,tourID,dateDeparture,CustomerName,ID,phone,Adults,"
				+ "Children,Toodlers,SpecialRequest,age,fee)VALUES ('"
				+ this.customerBelonging.getID()+"', '" + tourID + "', null, null, null, null, 0, 0, 0,"
				+ " null, null, 0)";
		
		String asking = "When are you planning to go for the trip? (The dates available are: \n" + "(";
		String queryDate = "Select Distinct departuredate from bookingtable where tourid like concat('%', ?,'%')";
		PreparedStatement stmt1 = connection.prepareStatement(createdb);
		PreparedStatement stmt2 = connection.prepareStatement(insertdb);
		PreparedStatement stmt3 = connection.prepareStatement(queryDate);
		/*stmt1.setString(1, this.customerBelonging.getID());
		stmt2.setString(1, this.customerBelonging.getID());
		stmt2.setString(2, this.customerBelonging.getID());
		stmt2.setString(3, tourID); */
		stmt3.setString(1, tourID); 
 
		stmt1.executeUpdate();
		stmt2.executeUpdate();
		ResultSet rs = stmt3.executeQuery();
		while (rs.next()){
			asking = asking + rs.getString(1) + "\n";
		}
		asking = asking + ".)";
		stmt1.close();
		stmt2.close();
		stmt3.close();
		connection.close();
		return asking;
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return (e.toString()+ "asking1");}
	}
	
	//TODO
	//The 2nd step of booking. Record the date in the temporary database and return an output to ask name of the customer
    public String askForName(String date) {
    	try {
    	Connection connection = KitchenSinkController.getConnection();
		String InsertDB = "Update " + this.customerBelonging.getID() + " SET dateDeparture = '" + date + "'";
		
		PreparedStatement stmt = connection.prepareStatement(InsertDB);
		String asking = "May I know your name?";
		stmt.executeUpdate();
		stmt.close();
		connection.close();
    	    return asking;
    	}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return (e.toString()+"asking2");}
    }
    public String askage(String ID) {
    	try {
    		Connection connection = KitchenSinkController.getConnection();
    		String InsertDB = "Update " + this.customerBelonging.getID() + " SET ID = '" + ID + "'";
    		
    		PreparedStatement stmt = connection.prepareStatement(InsertDB);
    		String asking = "May I know your age?";
    		stmt.executeUpdate();
    		stmt.close();
    		connection.close();
        	    return asking;    	
    
    	}catch (Exception e){
    			log.info("Exception while reading database: {}", e.toString());
    			return (e.toString()+ "asking3");}
    }
    //TODO
    //The 3rd step of booking. Record the name in the temporary database and return an output to ask ID of the customer
    public String askForID(String name){
    	try {
    		Connection connection = KitchenSinkController.getConnection();
    		String InsertDB = "Update " + this.customerBelonging.getID() + " SET CustomerName = '" + name + "'";
    		
    		PreparedStatement stmt = connection.prepareStatement(InsertDB);
    		String asking = "May I know your ID?";
    		stmt.executeUpdate();
    		stmt.close();
    		connection.close();
        	    return asking;    	
    
    	}catch (Exception e){
    			log.info("Exception while reading database: {}", e.toString());
    			return (e.toString()+"asking4");}
    }
    public String askphone(String age){
    	try {
    		Connection connection = KitchenSinkController.getConnection();
    		String InsertDB = "Update " + this.customerBelonging.getID() + " SET age = '" + age + "'";
    		
    		PreparedStatement stmt = connection.prepareStatement(InsertDB);
    		String asking = "Could you please tell us your phone number?";
    		stmt.executeUpdate();
    		stmt.close();
    		connection.close();
        	    return asking;    	
    	    }catch (Exception e){
    			log.info("Exception while reading database: {}", e.toString());
    			return (e.toString()+"asking5");}
    }
	
	//TODO
	//The 2nd step of booking. Record the date in the temporary database and return an output to ask no. of adults
    public String askForAdults(String phone) {
    	try {
    		Connection connection = KitchenSinkController.getConnection();
    		String InsertDB = "Update " + this.customerBelonging.getID() + " SET phone = '" + phone + "'";
    		
    		PreparedStatement stmt = connection.prepareStatement(InsertDB);
    		String asking = "Could you please tell us the number of adults?";
    		stmt.executeUpdate();
    		stmt.close();
    		connection.close();
        	    return asking;    	
    	    }catch (Exception e){
    			log.info("Exception while reading database: {}", e.toString());
    			return (e.toString()+"asking6");}
    }
    
    //TODO
    //The 3rd step of booking. Record the no. of adults in the temporary database and return an output to ask no. of children(0-3)
	public String askForChildrent(String numberOfAdults){
		try {
    		Connection connection = KitchenSinkController.getConnection();
    		String InsertDB = "Update " + this.customerBelonging.getID() + " SET Adults = " + numberOfAdults;
    		
    		PreparedStatement stmt = connection.prepareStatement(InsertDB);
    		String asking = "Could you please tell us the number of children?";
    		stmt.executeUpdate();
    		stmt.close();
    		connection.close();
        	    return asking;    
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return (e.toString()+"asking7");}
	}
	
	//TODO
	//The 4th step of booking. Record the no. of children in the temporary database and return an output to ask no. of Toodlers(4-11)
	public String askForToodler(String numberOfChildren) {
		try {
    		Connection connection = KitchenSinkController.getConnection();
    		String InsertDB = "Update " + this.customerBelonging.getID() + " SET Children = " + numberOfChildren;
    		
    		PreparedStatement stmt = connection.prepareStatement(InsertDB);
    		String asking = "Could you please tell us the number of toodlers?";
    		stmt.executeUpdate();
    		stmt.close();
    		connection.close();
        	    return asking;    
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return (e.toString()+"asking8");}
	}
	//TODO
	//The 5th step of booking. Record the no. of Toodlers in the temporary database, use calculate() to calculate the fee,
	//and return an output including all the information collected and the fee to double check with the customer
	public String askrequest(String numberOfToodlers) {
		try {
			Connection connection = KitchenSinkController.getConnection();
			String InsertDB = "Update " + this.customerBelonging.getID() + " SET Toodlers = " + numberOfToodlers;
			PreparedStatement stmt1 = connection.prepareStatement(InsertDB);
			stmt1.executeUpdate();
			stmt1.close();
			String asking = "Is there any more special request we can arrange for you?";
    		connection.close();
			return asking;
		}
		catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return (e.toString()+"request");}
	}
	public String doubleCheck(String request) {
		try {
		Connection connection = KitchenSinkController.getConnection();
		String InsertDB = "Update " + this.customerBelonging.getID() + " SET SpecialRequest = '" + request + "'";
		PreparedStatement stmt1 = connection.prepareStatement(InsertDB);
		stmt1.executeUpdate();
		
		stmt1.close();
		PreparedStatement queryTour = connection.prepareStatement("SELECT * from " +
				this.customerBelonging.getID());
		ResultSet tour = queryTour.executeQuery();
		tour.next();		

		//String queryAns = " insert into questionRecord (" + this.customerBelonging.getID()
		//	+ ", numberOfToodlers) values " + number; 

		PreparedStatement queryPrice = connection.prepareStatement("select price from BookingTable where "
				+ "(tourID like" + tour.getString(2) + "and " + "date like " + tour.getString(3) + ")");
		ResultSet pricers = queryPrice.executeQuery();
		pricers.next();
		double price = pricers.getInt(1);

		int NumA = tour.getInt(7);
		int NumC = tour.getInt(8);
		int NumT = tour.getInt(9);
		double finalcost = NumA*price + NumC*0.8*price;
		PreparedStatement insertp = connection.prepareStatement("Update " + this.customerBelonging.getID()
		+ "Set fee = " + finalcost);
		insertp.executeUpdate();
		String DoubleCheckList =
				"Please check the booking status: \n"
				+ "Customer: " + tour.getString(4) + "\n"
				+ "Tour ID: " + tour.getString(2) + "\n"
				+ "Age: " + tour.getString(11) + "\n"
				+ "Customer ID: " + tour.getString(5) + "\n"
				+ "Phone number: " + tour.getString(6) + "\n"
				+ "Departure Date: " + tour.getString(3) + "\n"
				+ "Number of Adults: " + tour.getInt(7) + "\n"
				+ "Number of Children: " + tour.getInt(8) + "\n"
				+ "Number of Toodlers: " + tour.getInt(9) + "\n"
				+ "Total Price: " + finalcost + "(HKD)\n"
				+ "Special Request: " + tour.getString(10) + "\n"
				+ "Please check if they are correct.If correct, please reply 'confirm'.";
		queryTour.close();
		tour.close();
		queryPrice.close();
		insertp.close();
		connection.close();
		return DoubleCheckList;
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return (e.toString()+"check");}
	}
	
	//TODO
	//The 6th(last) step of booking. Transfer all the data recorded in the temporary database to the booking table,
	//return an output to notify the customer that this booking is confirmed, and ask for the feedback
	public String confirm(String checkAnswer) {
		try
		{
			Connection connection = KitchenSinkController.getConnection();
    		PreparedStatement getall = connection.prepareStatement("select * from " + 
			this.customerBelonging.getID());
    		ResultSet all = getall.executeQuery();
    		all.next();
    		PreparedStatement insertCT = connection.prepareStatement("Insert Into CustomerTable "
    				+ "VALUES (" + all.getString(4) + ", " + all.getString(5) + ", " + all.getString(6)
    				+ ", " + all.getString(6) + ", " + all.getString(11) + ", " + all.getString(2) 
    				+ ", " + all.getInt(7) + ", " + all.getInt(8) + ", " + all.getInt(9) + ", "
    				+ all.getDouble(12) + ", 0, " + all.getString(10) + ", null, " + all.getString(1) + ")");
    		insertCT.executeUpdate();
        	PreparedStatement searchduration = connection.prepareStatement("Select * from tourlist"
    				+ " where tourID like " + all.getString(2));
    		ResultSet duration = searchduration.executeQuery();
    		duration.next();
    		PreparedStatement insertCR = connection.prepareStatement("Insert Into CustomerRecord "
    				+ "VALUES (" + all.getString(1) + ", " + all.getString(2) + ", " + duration.getString(2)
    				+ ", " + all.getString(3) + ", " + duration.getString(4) + ", " + all.getString(12) 
    				+ ", 'booked' " + duration.getString(3) + ")");
    		insertCR.executeUpdate();
    		searchduration.close();
    		insertCT.close();
    		getall.close();
    		insertCR.close();
    		connection.close();

    		return "Thanks for booking! Your order is being well processed, can you give your marks on "
    				+ "the service this time? (1-5: 1 for terrible and 5 for excellent) \n"
    				+ "And you can also tell us any improvement we can make at the same time.";
		}
		catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return (e.toString()+"confirm");}
	}
	//TODO
	//Record the feedback, transter all the data in the log database to the feedback table, delete the log table,
	//and return an output to thank the customer
	public String getFeedback(String feedback) {
		try {
			Connection connection = KitchenSinkController.getConnection();
			PreparedStatement query = connection.prepareStatement("Select * from "
    				+ this.customerBelonging.getID());
    		ResultSet rs = query.executeQuery();
    		rs.next();
    		PreparedStatement insert = connection.prepareStatement("Insert into feedbacktable values ( "
    				+ this.customerBelonging.getID() + ", " + feedback + rs.getString(2) + ")");
    		insert.executeUpdate();
    		insert.close();
    		PreparedStatement deletethetable = connection.prepareStatement("Drop table "
    				+ this.customerBelonging.getID());
    		deletethetable.executeUpdate();
    		query.close();
    		deletethetable.close();
    		connection.close();
    		return "Your feedback is received with thanks! Wish you a pleasant journey!";
		}
		catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return (e.toString()+ "feedback");}
	}
	
	//TODO
	//During the booking, if customer enter something unrelated to the booking information, stop current booking,
	//delete data in temporary database and return an output to notify the customer that this booking is cancelled
	public String breakBooking() {
		try {
			Connection connection = KitchenSinkController.getConnection();
    		PreparedStatement deletethetable = connection.prepareStatement("Drop table "
    				+ this.customerBelonging.getID());
    		deletethetable.executeUpdate();
    		deletethetable.close();
    		connection.close();

    		return "Booking Cancled, thanks for coming!";
		}
		catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return (e.toString()+ "break");}
	}
}