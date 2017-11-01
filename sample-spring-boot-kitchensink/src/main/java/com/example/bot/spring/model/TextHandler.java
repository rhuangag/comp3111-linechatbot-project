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
	public static int Question_ID=0;
	//define different types for question
    public static final int FAQ=1;
    public static final int UNKNOWN=2;
    public static final int CANCEL=3;
    public static final int FILTER_I=4;
    public static final int FILTER_II=5;
    public static final int BOOK_I=6;
    public static final int BOOK_II=7;
    public static final int BOOK_III=8;
    public static final int BOOK_IV=9;
    public static final int BOOK_V=10;
    public static final int BOOK_VI=11;
    public static final int BOOK_VII=12;
    public static final int BOOK_VIII=13;
    public static final int BOOK_IX=14;
    public static final int HISTORY=15;
    public static final int RECOMMENDATION=16;
    
    
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
    	reply=newFAQ(customer);
    	//checkBooking(customer);
    /*	checkFiltering();
    	
    	newCancel();
    	newHistory();
    	newRecommendation();
    	
    	
    	newFiltering();
    	newBooking();
    	otherReply();*/
    	
    	return reply;
    	
		    
}
   private String checkBooking(Customer customer) {
    	//check whether the customer is booking
    	try {
    		Connection connection = KitchenSinkController.getConnection();	
    		String query = "SELECT T1.type FROM questionRecord as T1  WHERE T1.No>= ALL (SELECT T2.type FROM�@questionRecord as T2�@)";
    		PreparedStatement stmt = connection.prepareStatement(query);
    		ResultSet rs =stmt.executeQuery();
    		if (rs.next()) {
    			if (rs.getInt(1)>=BOOK_I && rs.getInt(1)<BOOK_IX) {
    			
    				this.type=rs.getInt(1)+1;
    				record();
    				
    				rs.close();
    				stmt.close();
    				connection.close();
    				//customer?
    				Booking booking=new Booking(customer," ");
    				//keyword is depending on the current type
    				return "123";//Booking.askForInformation(type-1 ,text);
    				}
    			else
    				checkFiltering(customer);
    				}
    		else
    			checkFiltering(customer);
    		//TODO stop booking
    	}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());}
    	return null;
    }
   
   private String checkFiltering(Customer customer) {
   	try {
		Connection connection = KitchenSinkController.getConnection();	
		String query = "SELECT T1.type FROM questionRecord as T1  WHERE T1.No>= ALL (SELECT T2.type FROM questionRecord as T2)";
		PreparedStatement stmt = connection.prepareStatement(query);
		ResultSet rs =stmt.executeQuery();
		if (!rs.next()) 
			return newFAQ(customer);
		if (rs.getInt(1)==FILTER_I ) {
			
			this.type=rs.getInt(1)+1;
			record();
				
			rs.close();
			stmt.close();
			connection.close();
			//customer?
				
			//keyword is depending on the current type
			Filter filter =new Filter();
			return filter.viewDetails(text);}
		else if (rs.getInt(1)==FILTER_II) {
			if (text=="Yes") {
				this.type=BOOK_I;
				record();
				
				rs.close();
				stmt.close();
				connection.close();
				Booking booking=new Booking(customer," ");
				//keyword is depending on the current type
				return "123";//Booking.askForInformation(type ,text);
				}
			else 
				return null;
		}
				
				
		else
			newFAQ(customer);
		//TODO stop booking
	}catch (Exception e){
		log.info("Exception while reading database: {}", e.toString());}   
   	 return null;
   }
   
   private String newFAQ(Customer customer){
        try {	
        Connection connection = KitchenSinkController.getConnection();	 		
        
    	//directly seach the text in db to get the type  	
		PreparedStatement stmt1 = connection.prepareStatement("SELECT type, reply FROM FAQRecord WHERE question=?");
		stmt1.setString(1, text);
		ResultSet rs =stmt1.executeQuery();
		if(rs.next()) {
			type =rs.getInt(1);
			record();
			String reply= rs.getString(2);
			
			rs.close();
			stmt1.close();
			connection.close();
			
			return reply;}
		
    	// if not found, firstly get words from text
    	String[] parts = text.toLowerCase().split(" ");
    	
    	//TODO
    	//search every word in db
    	PreparedStatement stmt2 = connection.prepareStatement("SELECT keyword1, keyword2, type, reply FROM keywordListForFAQ WHERE keyword1 LIKE concat('%',concat(',',?,','),'%')");
    	for (int i=0; i<parts.length;i++) {
    		stmt2.setString(1, parts[i]);
    		rs =stmt2.executeQuery();
    		if (rs.next())
    			break;
    		
    	}
    	//the first keyword not found
    	
    	if (!rs.first()) {
    		rs.close();
			stmt2.close();
			connection.close();
			
    		//return newCancel(customer);
			return "not";
    	}
    	//now we find out the first keyword, check whether we need the second keyword
    	
    	String reply=rs.getString(4);
    	rs.close();
		stmt2.close();
		connection.close();
    	 return reply;
    	 //we do not need the second record, return 
/*    	if (rs.getString(2)=="null") {
    		type=FAQ;
    		record();
    		String reply=rs.getString(4);
    		rs.close();
			stmt2.close();
			connection.close();
    		return reply;}
    	else {
    		//check whether the sentence contains the second keyword
    		PreparedStatement stmt3 = connection.prepareStatement("SELECT keyword1, keyword2, type, reply FROM keywordListForFAQ WHERE keyword2 LIKE concat('%',' ',?,' ','%')");
    		for (int i=0; i<parts.length;i++) {
    			stmt3.setString(1, parts[i]);
        		rs =stmt3.executeQuery();
        		if (rs.next())
        			break;
    		}
    		//second keyword not found
    		if (!rs.first()) {
    			rs.close();
				stmt3.close();
				connection.close();
			
				return newCancel(customer);}
				//second keyword found 
    		else {
    			type=FAQ;
        		record();
        		rs.close();
				stmt3.close();
				connection.close();
        		return rs.getString(4);} 
    	}*/
    	}catch (Exception e){
    		log.info("Exception while reading database: {}", e.toString());}
        return null;
        
    }
    
   private String newCancel(Customer customer){
	   try {
     
	   if (text.toLowerCase().contains("cancel")) {
    		type=CANCEL;
    		record();
    		Connection connection = KitchenSinkController.getConnection();
    		
    		PreparedStatement stmt = connection.prepareStatement("SELECT TourJoined FROM CustomerTable WHERE TourJoined=?");
    		String[] parts = text.toLowerCase().split(" ");
    		ResultSet rs=null;
    		for (int i=0;i<parts.length;i++) {
    		stmt.setString(1, parts[i]);
    		rs =stmt.executeQuery();
    		if (rs.next())
    			break;}
    		String key=rs.getString(1);
    		


    		return "you want to canel";
    		//return customer.cancelBooking(key);
    		}
    	else 
    		return newHitory(customer);
	   }catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());}
	   return null;
    }
   
    private String newHitory(Customer customer) {
    	if (text.toLowerCase().contains("history")) {
    		if (customer.getHistory()==null) {
    			return unknown();
    		}
    			
    		else {
    			type=HISTORY;
    			record();
    			return "you want to check history";
    			//return customer.getHistory();
    			}
    		
    		}
    	else 
    		return newRecommendation(customer);
    }
    
    private String newRecommendation(Customer customer) {
    	if (text.toLowerCase().contains("recommendation")) {
    		if (customer.getRecommendation()==null) {
    			return unknown();
    		}
    		else {
    			type=RECOMMENDATION;
    			record();
    			return "you want to get recommendation";
    			//return customer.getRecommendation();
    			}}
    	else 
    		return "you want to do filter-searching";
    		//return newFiltering(customer);
    }
    
    private String newFiltering(Customer customer) {
    	    return "";
    }
    
    private String newBooking(Customer customer) {
       	if (text.toLowerCase().contains("book")) {
       		type=FILTER_I;
       		record();
       		Filter filter=new Filter();
       		return filter.filterSearch("book");
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
			String query1 = " insert into questionRecord (Question_ID, question,type)"
			        + " values (?, ?,?)";
			
			PreparedStatement stmt = connection.prepareStatement(query1);
			//use a static data member to record the no.
			stmt.setInt(1, Question_ID++);
			stmt.setString(2, text);
			stmt.setInt(3, type);
			
			stmt.executeQuery();
			
			stmt.close();
			connection.close();
			
    	}catch (Exception e) {
			log.info("Exception while reading file: {}", e.toString());
		}
    
    }

}
