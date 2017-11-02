package com.example.bot.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextHandler {
    //Declaration of data members
	String text;
	String keyword;
	int type;
	
	//define different types for question
	public  final int FAQ=1;
    public  final int MEANINGLESS=2;
    public  final int UNKNOWN=3;
    public  final int CANCEL=4;
    public  final int FILTER_I=5;
    public  final int RECOMMENDATION=6;
    public  final int HISTORY=7;
    public  final int FILTER_II=8;
    public  final int BOOK_I=9;
    public  final int BOOK_II=10;
    public  final int BOOK_III=11;
    public  final int BOOK_IV=12;
    public  final int BOOK_V=13;
    public  final int BOOK_VI=14;
    public  final int BOOK_VII=15;
    public  final int BOOK_VIII=16;
    public  final int BOOK_IX=17;
    public  final int BOOK_X=18;
    public  final int BOOK_XI=19;
    public  final int BOOK_XII=20;
    
    
    
    
    //Constructor
    public TextHandler(String t) {
    	    text=t;
    	    keyword=null;
    	    type = UNKNOWN;
    }
    
    //Methods
    
    public String getKeyword() {
    	    return keyword;
    }
    
    public int getType() {
	    return type;
}
    
    //TODO
    //Analyse the text input and initialize the data member "keyword" with the type and keywords
    public String messageHandler(Customer customer) {
    	String reply=null;
    	
    	reply=checkBooking(customer);
    /*	checkFiltering();
    	
    	newCancel();
    	newHistory();
    	newRecommendation();
    	newFAQ(customer);
    	
    	newFiltering();
    	newBooking();
    	otherReply();*/
    	
    	return reply;
    	
		    
}
   private String checkBooking(Customer customer) {
    	//check whether the customer is booking
    	try {
    		Connection connection = KitchenSinkController.getConnection();	
    		String query = "SELECT type FROM questionRecord";
    		PreparedStatement stmt = connection.prepareStatement(query);
    		ResultSet rs =stmt.executeQuery();

    		if (rs.next()) {
    			int temp=rs.getInt(1);
    			while (rs.next()) {
    				temp=rs.getInt(1);
    			}
    			
    			if (temp>=BOOK_I && temp<BOOK_IX) {
    			
    				type=temp+1;
    				record();
    				
    				rs.close();
    				stmt.close();
    				connection.close();
    				//customer?
    				Booking booking=new Booking(customer);
    				//keyword is depending on the current type
    				return booking.askForInformation(type ,text);
    				}
    			else {
    				rs.close();
					stmt.close();
					connection.close();
    				return checkFiltering(customer);}
    				}
    		else {
    			rs.close();
				stmt.close();
				connection.close();
    			return checkFiltering(customer);}
    		//TODO stop booking
    	}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return e.toString();}
    }
   
   private String checkFiltering(Customer customer) {
	   	try {
			Connection connection = KitchenSinkController.getConnection();	
			String query = "SELECT type FROM questionRecord";
			PreparedStatement stmt = connection.prepareStatement(query);
			ResultSet rs =stmt.executeQuery();
			//if the table is empty, the question must be the first question
			if (!rs.next()) {
				rs.close();
				stmt.close();
				connection.close();
				return newFAQ(customer);}
			int temp=rs.getInt(1);
			while (rs.next()) {
				temp=rs.getInt(1);
			}
				
			if (temp==FILTER_I ) {
				
				type=FILTER_II;
				record();
					
				
				//customer?
					
				//keyword is depending on the current type
				Filter filter =new Filter();

				//the text here is expected to be a number, we can add some code to check it later
				String answer=filter.viewDetails(text);
				String[] parts = answer.replaceAll("\\p{P}" , "").toLowerCase().split(" ");
				String tourID=parts[0];
				PreparedStatement stmt2 = connection.prepareStatement("insert into tempfortourID values (?,?)");
				stmt2.setString(1,customer.getID());
				stmt2.setString(2,tourID);
				stmt2.executeQuery();
				rs.close();
				stmt.close();
				stmt2.close();
				connection.close();
				return answer;}
			else if (temp==FILTER_II) {
				if (text=="Yes") {
					type=BOOK_I;
					PreparedStatement stmt3 = connection.prepareStatement("select tourID from tempfortourID where customerID=?");
					stmt3.setString(1,customer.getID());
					rs =stmt3.executeQuery();
					String tourID=rs.getString(1);
					PreparedStatement stmt4 = connection.prepareStatement("Delete tourID from TempfortourID where customerID=?");
					stmt4.setString(1,customer.getID());
					stmt4.executeQuery();
					record();
					
					rs.close();
					stmt.close();
					stmt3.close();
					stmt3.close();
					connection.close();
					Booking booking=new Booking(customer);
					//keyword is depending on the current type
					return booking.askForInformation(type ,tourID);
					}
				else    {
					PreparedStatement stmt5 = connection.prepareStatement("Delete tourID from TempfortourID where customerID=?");
					stmt5.setString(1,customer.getID());
					stmt5.executeQuery();
					rs.close();
					stmt.close();
					stmt5.close();
					
					connection.close();
					type=MEANINGLESS;
					record();
					return "Do you have any other questions?";}

			}
					
					
			else {
				rs.close();
				stmt.close();
				connection.close();
				return newFAQ(customer);}
			//TODO stop booking
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());   
	   	 	return e.toString();}
	   }
   
   private String newFAQ(Customer customer){
        try {	
        Connection connection = KitchenSinkController.getConnection();	 		
        String reply=null;
    	//directly seach the text in db to get the type  	
		PreparedStatement stmt1 = connection.prepareStatement("SELECT type,reply FROM FAQRecord WHERE question=?");
		stmt1.setString(1, text);
		ResultSet rs =stmt1.executeQuery();
		if(rs.next()) {
			type =rs.getInt(1);
			record();
			reply= rs.getString(2);
			
			rs.close();
			stmt1.close();
			connection.close();
			
			return reply;}
		
    	// if not found, firstly get words from text
    	String[] parts = text.replaceAll("\\p{P}" , "").toLowerCase().split(" ");
    	
    	//TODO
    	//search every word in db
    	int countloop=0;
    	
    	PreparedStatement stmt2 = connection.prepareStatement("SELECT keyword1, keyword2, type, reply FROM keywordListForFAQ WHERE keyword1 LIKE concat('%',concat(',',?,','),'%')");
    	for (int i=0; i<parts.length;i++) {
    		stmt2.setString(1, parts[i]);
    		rs =stmt2.executeQuery();
    		if (rs.next()) {
    			
    			break;}
    		countloop++;
    		
    	}
    	//the first keyword not found
    	
    	if (countloop==parts.length) {
    		rs.close();
			stmt2.close();
			connection.close();
			
    		return newCancel(customer);
			
    	}

    	//now we find out the first keyword, check whether we need the second keyword
    	 //we do not need the second record, return 

    		
    	    rs.close();
		    stmt2.close();
    		//check whether the sentence contains the second keyword
    		PreparedStatement stmt3 = connection.prepareStatement("SELECT keyword1, keyword2, type, reply FROM keywordListForFAQ WHERE  keyword2 LIKE concat('%',concat(',',?,','),'%')");
    		countloop=0;
    		for (int i=0; i<parts.length;i++) {
    			
    			//stmt3.setString(1, keyword1);
    			stmt3.setString(1, parts[i]);
        		rs =stmt3.executeQuery();
        		if (rs.next())
        			break;
        		//if (rs.getString(1)==keyword1 && rs.getString(2)==null)
        			//break;
        		countloop++;
    		}
    		//second keyword not found
    		if (countloop==parts.length) {
    			rs.close();
				stmt3.close();
				connection.close();
			
				return newCancel(customer);}
				//second keyword found 
    		PreparedStatement stmt4 = connection.prepareStatement("SELECT keyword1, keyword2, type, reply FROM keywordListForFAQ WHERE  keyword3 LIKE concat('%',concat(',',?,','),'%')");
    		countloop=0;
    		for (int i=0; i<parts.length;i++) {
    			
    			//stmt3.setString(1, keyword1);
    			stmt4.setString(1, parts[i]);
        		rs =stmt4.executeQuery();
        		if (rs.next())
        			break;
        		//if (rs.getString(1)==keyword1 && rs.getString(2)==null)
        			//break;
        		countloop++;
    		}
    		//second keyword not found
    		if (countloop==parts.length) {
    			rs.close();
				stmt3.close();
				connection.close();
    			rs.close();
				stmt4.close();
				connection.close();
			
				return newCancel(customer);}
    		
    		else {
    			type=FAQ;
        		record();
        		reply=rs.getString(4);
        		rs.close();
				stmt3.close();
				connection.close();
        		rs.close();
				stmt4.close();
				connection.close();
        		return reply;}  
    //	}
    	}catch (Exception e){
    		log.info("Exception while reading database: {}", e.toString());
    		return e.toString();}
        
        
    }
    
   private String newCancel(Customer customer){
	   String result=null;
	   try {
	   if (text.replaceAll("\\p{P}" , "").toLowerCase().contains("cancel")) {
    		type=CANCEL;
    		record();
    		Connection connection = KitchenSinkController.getConnection();
    		String key="noRecord";
    		PreparedStatement stmt = connection.prepareStatement("SELECT TourJoined FROM CustomerTable WHERE TourJoined like concat('%',?,'%')");
    		String[] parts = text.toLowerCase().split(" ");
    		ResultSet rs=null;
    		for (int i=0;i<parts.length;i++) {
    		stmt.setString(1, parts[i]);
    		rs =stmt.executeQuery();
    		if (rs.next()) {
    			key=rs.getString(1);
    			break;
    			}
    		}
    		
    		stmt.close();
    		rs.close();

    		connection.close();
    		

    		result=customer.cancelBooking(key);

    		}
    	else 
    		{

    		return newHitory(customer);}
	  }
	   catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return e.toString();}
	   return result;
    }
   
    private String newHitory(Customer customer) {
    	try {
    		if (text.replaceAll("\\p{P}" , "").toLowerCase().contains("history")) {
    			if (customer.getHistory()==null) {
    				return unknown();
    			}
    			
    			else {
    				type=HISTORY;
    				record();
    			
    				return customer.getHistory();
    			}
    		
    			}
    		else 
    			return newRecommendation(customer);
    	}catch(Exception e) {
    		log.info("Exception while reading database: {}", e.toString());
	   		return e.toString();
    	}
    }
    
    private String newRecommendation(Customer customer) {
    	if (text.replaceAll("\\p{P}" , "").toLowerCase().contains("recommendation")) {
    		if (customer.getRecommendation()==null) {
    			return unknown();
    		}
    		else {
    			type=RECOMMENDATION;
    			record();
    			
    			return customer.getRecommendation();
    			}}
    	else 
    		
    		return newFiltering(customer);
    }
    
    private String newFiltering(Customer customer) {
    		Filter filter=new Filter();
    		return filter.filterSearch("hotSpring");

    }
    
    private String newBooking(Customer customer) {
       	if (text.replaceAll("\\p{P}" , "").toLowerCase().contains("book")) {
       		/*type=FILTER_I;
       		record();
       		Filter filter=new Filter();
       		return filter.filterSearch("book");*/
       		Booking booking=new Booking(customer);
       		return booking.askForInformation(9,"2D001");
       		}
       	else {
       		return unknown();}
       	
       	}
    
    private String unknown() {
    	type = UNKNOWN;
    	record();
    	return "Sorry, we can not find matched result.";
    }
    
    //TODO
    //After analysing the text, record the type of input in a temporary database(log) and record the question to the question-recording database
    public void record() {
    	try {
			Connection connection = KitchenSinkController.getConnection();
			//record the question to the question-recording database table named questionRecord
			String query1 = " insert into questionRecord ( question,type)"
			        + " values ( ?,?)";
			
			PreparedStatement stmt = connection.prepareStatement(query1);
			//use a static data member to record the no.
			
			stmt.setString(1, text);
			stmt.setInt(2, type);
			stmt.executeQuery();
			if (type<8)
			{String query2 = " insert into usefulquestionRecord ( usefulquestion,type)"
			        + " values ( ?,?)";
			
			PreparedStatement stmt2 = connection.prepareStatement(query2);
			stmt2.setString(1, text);
			stmt2.setInt(2, type);
			stmt2.executeQuery();
			stmt2.close();}
			
			stmt.close();
			connection.close();
			
    	}catch (Exception e) {
			log.info("Exception while reading file: {}", e.toString());
		}
    
    }

}