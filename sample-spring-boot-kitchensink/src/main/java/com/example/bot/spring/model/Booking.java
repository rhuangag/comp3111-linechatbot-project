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
 * The class Booking is used when the customer starts a booking. It can ask for information and collect the information from the customer.
 * It is involved in the mediator pattern: mediator TextHandler can create a Booking object when a booking starts. All the information about the customer is passed by the mediator.
 *
 */
@Slf4j
public class Booking {
	//data member for this class
	private Customer customerBelonging;
	
	/**
	 * Constructor of class Booking. It stores a Customer object for booking the tour.
	 * @param customerBelong This is used to temporarily store the customer's information
	 */
	//Methods
	public Booking(Customer customerBelong)
	{
		this.customerBelonging = customerBelong;
	}
	//TODO
	//Store the information collected and return an output to ask for next information
	/**
	 * This method can determine which step of booking the customer is in and ask for appropriate information and collect those information.
	 * @param phase This is used to determine the step of the booking
	 * @param information This is the answer of the previous question
	 * @return java.lang.String This returns the next question for the collection of information.
	 */
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
	private String askForDate(String tourID) {
		try {
		Connection connection = KitchenSinkController.getConnection();
		boolean getdiscount = false;
		String createdb = "CREATE table "+this.customerBelonging.getID() +"(customerID varchar(50), "
				+ " tourID varchar(10), dateDeparture varchar(20), CustomerName varchar(20), ID varchar(20), "
				+ " phone varchar(12), Adults Int, Children Int, Toodlers Int, SpecialRequest varchar(100)"
				+ ", age varchar(3), fee float, discount float, discountcapacity int)";
		String insertdb = "Insert Into " + this.customerBelonging.getID() + "(customerID,tourID,dateDeparture,CustomerName,ID,phone,Adults,"
				+ "Children,Toodlers,SpecialRequest,age,fee, discount, discountcapacity)VALUES ('"
				+ this.customerBelonging.getID()+"', '" + tourID + "', null, null, null, null, 0, 0, 0,"
				+ " null, null, 0, 1, 0)";
		
		String asking = "When are you planning to go for the trip? (The dates available are: \n" ;
		String queryDate = "Select Distinct departuredate from bookingtable where tourid like concat('%', ?,'%')";
		
		PreparedStatement discountcheck1 = connection.prepareStatement("Select * from discountuserlist where userid"
				+ " like '" + this.customerBelonging.getID() + "' and tourID like concat('%', ?,'%')");
		discountcheck1.setString(1, tourID);
		ResultSet dl = discountcheck1.executeQuery();
		if (dl.next()) {
			PreparedStatement discountapply1 = connection.prepareStatement("Select * from discounttourlist where"
					+ " tourID like concat('%', ?,'%')");
			discountapply1.setString(1, tourID);
			ResultSet da = discountapply1.executeQuery();
			da.next();
			insertdb = "Insert Into " + this.customerBelonging.getID() + "(customerID,tourID,dateDeparture,CustomerName,ID,phone,Adults,"
					+ "Children,Toodlers,SpecialRequest,age,fee, discount, discountcapacity)VALUES ('"
					+ this.customerBelonging.getID()+"', '" + tourID + "', null, null, null, null, 0, 0, 0,"
					+ " null, null, 0, " + da.getDouble(3) + ", " + da.getInt(5) + ")";
			discountapply1.close();
			getdiscount = true;
		}
		discountcheck1.close();
		
		PreparedStatement stmt1 = connection.prepareStatement(createdb);
		PreparedStatement stmt2 = connection.prepareStatement(insertdb);
		PreparedStatement stmt3 = connection.prepareStatement(queryDate);
		
		/*stmt1.setString(1, this.customerBelonging.getID());
		stmt2.setString(1, this.customerBelonging.getID());
		stmt2.setString(2, this.customerBelonging.getID());
		stmt2.setString(3, tourID); */
 
		stmt1.executeUpdate();
		stmt2.executeUpdate();
		stmt3.setString(1, tourID); 

		ResultSet rs = stmt3.executeQuery();
		while (rs.next()){
			asking = asking + rs.getString(1) + "\n";
		}
		
		if (getdiscount)
			asking = asking + "\n note that you will get discount in this booking~!";
		stmt1.close();
		stmt2.close();
		stmt3.close();
		connection.close();
		return asking;
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return ((e.toString()+ "asking1"));}
	}
	
	//TODO
	//The 2nd step of booking. Record the date in the temporary database and return an output to ask name of the customer
    private String askForName(String date) {
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
    private String askage(String ID) {
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
    private String askForID(String name){
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
    private String askphone(String age){
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
    private String askForAdults(String phone) {
    	try {
    		Connection connection = KitchenSinkController.getConnection();
    		String InsertDB = "Update " + this.customerBelonging.getID() + " SET phone = '" + phone + "'";
    		PreparedStatement Querydata = connection.prepareStatement("Select * from "+ this.customerBelonging.getID());
			ResultSet datas = Querydata.executeQuery();
			datas.next();
			PreparedStatement QueryMax = connection.prepareStatement("Select tourcapacity, currentcustomer"
					+ " from bookingtable where tourid like '" + datas.getString(2) + "' and departuredate like '"
					+ datas.getString(3) + "'");
			ResultSet moredata = QueryMax.executeQuery();   
			moredata.next();
			PreparedStatement stmt = connection.prepareStatement(InsertDB);
    		String asking = "Could you please tell us the number of adults? \n"
    				+ "The capacity of the trip is " + moredata.getInt(1) + "\n"
    				+ "and currently there are " + moredata.getInt(2)+ " people already.";
    		datas.close();
    		moredata.close();
    		Querydata.close();
    		QueryMax.close();
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
	private String askForChildrent(String numberOfAdults){
		try {
    		Connection connection = KitchenSinkController.getConnection();
    		String InsertDB = "Update " + this.customerBelonging.getID() + " SET Adults = " + numberOfAdults;
    		
    		PreparedStatement stmt = connection.prepareStatement(InsertDB);
    		String asking = "Could you please tell us the number of children?(age: 4-11)";
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
	private String askForToodler(String numberOfChildren) {
		try {
    		Connection connection = KitchenSinkController.getConnection();
    		String InsertDB = "Update " + this.customerBelonging.getID() + " SET Children = " + numberOfChildren;
    		
    		PreparedStatement stmt = connection.prepareStatement(InsertDB);
    		String asking = "Could you please tell us the number of toodlers?(age: 0-3)";
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
	private String askrequest(String numberOfToodlers) {
		try {
			Connection connection = KitchenSinkController.getConnection();
			PreparedStatement Querydata = connection.prepareStatement("Select * from "+ this.customerBelonging.getID());
			ResultSet datas = Querydata.executeQuery();
			datas.next();
			PreparedStatement QueryMax = connection.prepareStatement("Select tourcapacity, currentcustomer"
					+ " from bookingtable where tourid like '" + datas.getString(2) + "' and departuredate like '"
					+ datas.getString(3) +"'");
			ResultSet moredata = QueryMax.executeQuery();
			moredata.next();
			if ((moredata.getInt(2)+datas.getInt(7)+datas.getInt(8)+ Integer.parseInt(numberOfToodlers)) > moredata.getInt(1))
				return this.breakBooking();
			QueryMax.close();
			datas.close();
			moredata.close();
			Querydata.close();
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
	private String doubleCheck(String request) {
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
				+ " tourID like '" + tour.getString(2) + "' and " + "departuredate like '" + tour.getString(3)+"'" );
		ResultSet pricers = queryPrice.executeQuery();
		pricers.next();
		double price = pricers.getInt(1);

		int NumA = tour.getInt(7);
		int NumC = tour.getInt(8);
		int NumT = tour.getInt(9);
		int NumDiscount = tour.getInt(14);
		double discount = tour.getDouble(13);
		double finalcost = 0;
		if (NumDiscount >= (NumA + NumC))
			finalcost = (NumA*price + NumC*0.8*price)*discount;
		else if (NumDiscount > NumA) {
			finalcost = NumA*price*discount + (NumDiscount-NumA)*0.8*price*discount + 
			(NumC-NumDiscount+NumA)*0.8*price;}
		else {
			finalcost = NumDiscount*discount*price + (NumA-NumDiscount)*price
			+ NumC*0.8*price;}
		
		
	    BigDecimal b = new BigDecimal(finalcost); 
		float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();  
		PreparedStatement insertp = connection.prepareStatement("Update " + this.customerBelonging.getID()
		+ " Set fee = " + f1);
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
				+ "Total Price: " + f1 + "(HKD)\n"
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
	private String confirm(String checkAnswer) {
		try
		{
			if (!checkAnswer.toLowerCase().contains("confirm"))
				return breakBooking();

			Connection connection = KitchenSinkController.getConnection();
    		PreparedStatement getall = connection.prepareStatement("select * from " + 
			this.customerBelonging.getID());
    		ResultSet all = getall.executeQuery();
    		all.next();
    		int SUM = all.getInt(7) + all.getInt(8) + all.getInt(9);
    		//getstring(3) need to fix.
    		String A = "";
    		A = A + all.getString(3).split("/")[2];
    		A = A + all.getString(3).split("/")[1];
    		A = A + all.getString(3).split("/")[0];
    		PreparedStatement insertCT = connection.prepareStatement("Insert Into CustomerTable "
    				+ "VALUES ('" + all.getString(4) + "', '" + all.getString(5) + "', '" + all.getString(6)
    				+ "', " + Integer.parseInt(all.getString(11)) + ", '" + all.getString(2) + A 
    				+ "', " + all.getInt(7) + ", '" + all.getInt(8) + "', '" + all.getInt(9) + "', '"
    				+ all.getDouble(12) + "', 0, '" + all.getString(10) + "', 'booked', '" + all.getString(1) + "')");
    		insertCT.executeUpdate();
        	PreparedStatement searchduration = connection.prepareStatement("Select * from tourlist"
    				+ " where tourID like '" + all.getString(2)+"'");
    		ResultSet duration = searchduration.executeQuery();
    		duration.next();
    		PreparedStatement insertCR = connection.prepareStatement("Insert Into CustomerRecord "
    				+ "VALUES ('" + all.getString(1) + "', '" + all.getString(2) + "', '" + duration.getString(2)
    				+ "', '" + all.getString(3) + "', '" + duration.getString(4) + "', '" + all.getDouble(12) 
    				+ "', 'booked' , '" + duration.getString(3) + "')");
    		insertCR.executeUpdate();
    		PreparedStatement updatebookingtable = connection.prepareStatement("Update bookingtable SET "
    				+ "currentcustomer = currentcustomer + " + SUM + " where booktableID like '"
    				+ all.getString(2) + A + "'");
    		
    		
    		PreparedStatement checkdiscount = connection.prepareStatement("select userid from discountuserlist where userid=?");
    		checkdiscount.setString(1, customerBelonging.getID());
    		ResultSet docheck=checkdiscount.executeQuery();
    		if (docheck.next()) {
        		PreparedStatement deletediscount = connection.prepareStatement("update discountuserlist set userid='used' where userid=?");
        		deletediscount.setString(1, customerBelonging.getID());
        		deletediscount.executeUpdate();
        		deletediscount.close();
    		}
    		docheck.close();
    		checkdiscount.close();
    		
    		updatebookingtable.executeUpdate();
    		all.close();
    		duration.close();
    		searchduration.close();
    		updatebookingtable.close();
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
	private String getFeedback(String feedback) {
		try {
			Connection connection = KitchenSinkController.getConnection();
			PreparedStatement query = connection.prepareStatement("Select * from "
    				+ this.customerBelonging.getID());
    		ResultSet rs = query.executeQuery();
    		rs.next();
    		PreparedStatement insert = connection.prepareStatement("Insert into feedbacktable values ( '"
    				+ this.customerBelonging.getID() + "', '" + rs.getString(2) +"', '"+ feedback + "')");
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
	private String breakBooking() {
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