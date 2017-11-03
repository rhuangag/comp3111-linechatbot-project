package com.example.bot.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextHandler {
    //Declaration of data members
	String text;
	//String keyword;
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
    	   // keyword=null;
    	    type = UNKNOWN;
    }
    
    //Methods
    
  //  public String getKeyword() {
   // 	    return keyword;
  //  }
    
    public int getType() {
	    return type;
}
    
    //TODO
    //Analyse the text input and initialize the data member  type 
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
    	//check whether the customer is in booking process
    	try {
    		//find the last quetion type for the specific customer who is sending text now
    		Connection connection = KitchenSinkController.getConnection();	
    		String query = "SELECT type FROM questionRecord where customerid=?";
    		PreparedStatement stmt = connection.prepareStatement(query);
    		stmt.setString(1,customer.getID());
    		ResultSet rs =stmt.executeQuery();
    		//check whether the customer ask questions before
    		if (rs.next()) {
    			int temp=rs.getInt(1);
    			while (rs.next()) {
    				temp=rs.getInt(1);
    			}
    		//we find the customer did ask question before, temp is the type of last question	
    			if (temp>=BOOK_I && temp<BOOK_IX) {
    			    //the customer is in the booking process
    				type=temp+1;
    				record(customer);
    				
    				rs.close();
    				stmt.close();
    				connection.close();
    				
    				Booking booking=new Booking(customer);
    				//now just assume the customer will perfectly reply the correct information in the prototype
    				return booking.askForInformation(type ,text);
    				}
    			else {
    				//the customer is not in the booking process
    				rs.close();
					stmt.close();
					connection.close();
    				return checkFiltering(customer);}
    				}
    		else {
    			//the customer did not ask question before. 
    			rs.close();
				stmt.close();
				connection.close();
    			return checkFiltering(customer);}
    	//TODO stop booking 
    		//now the chatbot did not support the interrupt of the booking process
    	}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return e.toString();}
    }
   
   private String checkFiltering(Customer customer) {
	   	try {
			Connection connection = KitchenSinkController.getConnection();	
			String query = "SELECT type FROM questionRecord where customerid=?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, customer.getID());
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
			//now temp is the type of the last question 	
			if (temp==FILTER_I ) {
				//the customer just do the filter searching and we have returned a list of tour
				type=FILTER_II;
				record(customer);
					
				
				
					
				
				Filter filter =new Filter(customer.getID());

				String number_text=text.replaceAll("[^0-9]" , "");
				if (number_text.isEmpty()) {
					rs.close();
					stmt.close();
					connection.close();
					return newFAQ(customer);
				}
					
				//answer is a reply that confirming the information
				String answer=filter.viewDetails(number_text);
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
					stmt4.executeUpdate();
					record(customer);
					
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
					record(customer);
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
			record(customer);
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
				
    			
				stmt4.close();
				connection.close();
			
				return newCancel(customer);}
    		
    		else {
    			type=FAQ;
        		record(customer);
        		reply=rs.getString(4);
        		rs.close();
				stmt3.close();
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
		   Connection connection = KitchenSinkController.getConnection();
		   PreparedStatement trigger = connection.prepareStatement("SELECT keyword FROM keywordlistforfunction WHERE type = 4 and keyword like concat('%',concat(',',?,','),'%')");
		   ResultSet key=null;
		   String[] parts = text.replaceAll("\\p{P}" , "").toLowerCase().split(" ");
		   int count=0;
		   for (int i=0;i<parts.length;i++) {
		   trigger.setString(1, parts[i]);
		   key=trigger.executeQuery();
		   if (key.next())
			   break;
		   count++;
		   }
		   key.close();
		   trigger.close();
		   if (count!=parts.length) {
    		type=CANCEL;
    		record(customer);
    		
    		String reply="noRecord";
    		PreparedStatement stmt = connection.prepareStatement("SELECT TourJoined FROM CustomerTable WHERE TourJoined like concat('%',?,'%')");
    		
    		ResultSet rs=null;
    		for (int i=0;i<parts.length;i++) {
    		stmt.setString(1, parts[i]);
    		rs =stmt.executeQuery();
    		if (rs.next()) {
    			reply=rs.getString(1);
    			break;
    			}
    		}
    		
    		stmt.close();
    		rs.close();

    		connection.close();
    		

    		result=customer.cancelBooking(reply);

    		}
    	 else 
    		{
    		 connection.close();
    		return newHitory(customer);}
	  }
	   catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return e.toString();}
	   return result;
    }
   
    private String newHitory(Customer customer) {
    	try {
    	   Connection connection = KitchenSinkController.getConnection();
 		   PreparedStatement trigger = connection.prepareStatement("SELECT keyword FROM keywordlistforfunction WHERE type = 7 and keyword like concat('%',concat(',',?,','),'%')");
 		   ResultSet key=null;
 		   String[] parts = text.replaceAll("\\p{P}" , "").toLowerCase().split(" ");
 		   int count=0;
 		   for (int i=0;i<parts.length;i++) {
 		   trigger.setString(1, parts[i]);
 		   key=trigger.executeQuery();
 		   if (key.next())
 			   break;
 		   count++;
 		   }
 		   key.close();
 		   trigger.close();
    		if (count!=parts.length) {
    			if (customer.getHistory()==null) {
    				connection.close();
    				return unknown(customer);
    			}
    			
    			else {
    				type=HISTORY;
    				record(customer);
    				connection.close();
    				return customer.getHistory();
    			}
    		
    			}
    		else {
    			connection.close();
    			return newRecommendation(customer);}
    	}catch(Exception e) {
    		log.info("Exception while reading database: {}", e.toString());
	   		return e.toString();
    	}
    }
    
    private String newRecommendation(Customer customer) {
    	try {
    	   Connection connection = KitchenSinkController.getConnection();
  		   PreparedStatement trigger = connection.prepareStatement("SELECT keyword FROM keywordlistforfunction WHERE type=6 and keyword like concat('%',concat(',',?,','),'%')");
  		   ResultSet key=null;
  		   String[] parts = text.replaceAll("\\p{P}" , "").toLowerCase().split(" ");
  		   int count=0;
  		   for (int i=0;i<parts.length;i++) {
  		   trigger.setString(1, parts[i]);
  		   key=trigger.executeQuery();
  		   if (key.next())
  			   break;
  		   count++;
  		   }
  		   key.close();
  		   trigger.close();
    	if (count!=parts.length) {
    		if (customer.getRecommendation()==null) {
    			connection.close();
    			return unknown(customer);
    			
    		}
    		else {
    			type=RECOMMENDATION;
    			record(customer);
    			connection.close();
    			return customer.getRecommendation();
    			}}
    	else {
    		connection.close();
    		return newFiltering(customer);}
    	}catch(Exception e) {
    		log.info("Exception while reading database: {}", e.toString());
	   		return e.toString();
    	}
    }
    
    
    
    private String newFiltering(Customer customer) {
    	if(true) { 	String ID=customer.getID();
		Filter filter=new Filter(ID);
    		
    		return filter.filterSearch(text);}
    	
    		try {
    			String ID=customer.getID();
        		Filter filter=new Filter(ID);
        		
    			String[] parts = text.replaceAll("[^a-zA-Z0-9-\\s]" , "").toLowerCase().split(" ");
    	    	String reply=null;
    	    	int countloop=0;
    	    	Connection connection = KitchenSinkController.getConnection();
    	    	PreparedStatement findonekey = connection.prepareStatement("SELECT reply FROM onekeyword WHERE keyword1 =?");
    	    	ResultSet onekey=null;
    	    	for (int i=0; i<parts.length;i++) {
    	    		findonekey.setString(1, parts[i]);
    	    		onekey =findonekey.executeQuery();
    	    		if (onekey.next()) {
    	    			reply=onekey.getString(1);
    	    			break;}
    	    		countloop++;
    	    		
    	    	}
    	 
    			onekey.close();
    			findonekey.close();
    			
    	    	//the only one keyword not found
    	    	
    	    	if (countloop!=parts.length) {
    	    		type=FILTER_I;
    	    		record(customer);
    	    		connection.close();
    	    		return filter.filterSearch(reply);	
    	    		
    				
    	    	}
    		//now check two keywords
    	    	PreparedStatement findtwokey1 = connection.prepareStatement("SELECT reply FROM twokeyword WHERE keyword1 LIKE concat('%',concat(',',?,','),'%')");
    	    	ResultSet twokey1=null;
    	    	countloop=0;
    	    	for (int i=0; i<parts.length;i++) {
    	    		findtwokey1.setString(1, parts[i]);
    	    		twokey1 =findtwokey1.executeQuery();
    	    		if (twokey1.next()) {
    	    			reply=twokey1.getString(1);
    	    			break;}
    	    		countloop++;
    	    		
    	    	}
    			twokey1.close();
    			findtwokey1.close();
    			if (countloop!=parts.length) {
	    			PreparedStatement findtwokey2 = connection.prepareStatement("SELECT reply FROM twokeyword WHERE keyword2 LIKE concat('%',concat(',',?,','),'%')");
	    			ResultSet twokey2=null;
	    			countloop=0;
    	    		for (int i=0; i<parts.length;i++) {
    	    			findtwokey2.setString(1, parts[i]);
    	    			twokey2 =findtwokey2.executeQuery();
    	    			if (twokey2.next()) {
    	    				reply=twokey2.getString(1);
    	    				break;}
    	    			countloop++;
    	    			
    	    	}
    	    		twokey2.close();
	    			findtwokey2.close();
    			if (countloop!=parts.length){
    				type=FILTER_I;
    				record(customer);
    				connection.close();
    				return filter.filterSearch(reply);
    			}
    		}
    		//now find three keyword
    			PreparedStatement findthreekey1 = connection.prepareStatement("SELECT reply FROM threekeyword WHERE keyword1 LIKE concat('%',concat(',',?,','),'%')");
    	    	ResultSet threekey1=null;
    	    	countloop=0;
    	    	for (int i=0; i<parts.length;i++) {
    	    		findthreekey1.setString(1, parts[i]);
    	    		threekey1 =findthreekey1.executeQuery();
    	    		if (threekey1.next()) {
    	    			reply=threekey1.getString(1);
    	    			break;}
    	    		countloop++;
    	    		
    	    	}
    			threekey1.close();
    			findthreekey1.close();
    			if (countloop!=parts.length) {
    				PreparedStatement findthreekey2 = connection.prepareStatement("SELECT reply FROM threekeyword WHERE keyword2 LIKE concat('%',concat(',',?,','),'%')");
        	    	ResultSet threekey2=null;
        	    	countloop=0;
        	    	for (int i=0; i<parts.length;i++) {
        	    		findthreekey2.setString(1, parts[i]);
        	    		threekey2 =findthreekey2.executeQuery();
        	    		if (threekey2.next()) {
        	    			reply=threekey2.getString(1);
        	    			break;}
        	    		countloop++;
        	    		
        	    	}
        			threekey2.close();
        			findthreekey2.close();
        			if (countloop!=parts.length) {
        				PreparedStatement findthreekey3 = connection.prepareStatement("SELECT reply FROM threekeyword WHERE keyword3 LIKE concat('%',concat(',',?,','),'%')");
            	    	ResultSet threekey3=null;
            	    	countloop=0;
            	    	for (int i=0; i<parts.length;i++) {
            	    		findthreekey3.setString(1, parts[i]);
            	    		threekey3 =findthreekey3.executeQuery();
            	    		if (threekey3.next()) {
            	    			reply=threekey3.getString(1);
            	    			break;}
            	    		countloop++;
            	    		
            	    	}
            			threekey3.close();
            			findthreekey3.close();
            			if (countloop!=parts.length) {
            				type=FILTER_I;
            				record(customer);
            				connection.close();
            				return filter.filterSearch(reply); 
            			}
        			}
    			}
    		String[] number=text.replaceAll("[^0-9]", ",").split(",");
    		String temp="";
    		temp+=number[0];
    		for (int i=1;i<number.length;i++) {
    			if (!number[i].isEmpty()) {
    				temp+=",";
    				temp+=number[i];
    				
    			}
    		}
    		if (temp.isEmpty()) {
    			connection.close();
    			return newBooking(customer);}
    		type=FILTER_I;
			record(customer);
			connection.close();
			return filter.filterSearch(temp);
    	   /* */
    		}catch(Exception e) {
    			log.info("Exception while reading database: {}", e.toString());
    	   		return e.toString();
    		}
    }
    
    
    
    private String newBooking(Customer customer) {
       	try {
       	   Connection connection = KitchenSinkController.getConnection();
   		   PreparedStatement trigger = connection.prepareStatement("SELECT keyword FROM keywordlistforfunction WHERE type=6 and keyword like concat('%',concat(',',?,','),'%')");
   		   ResultSet key=null;
   		   String[] parts = text.replaceAll("\\p{P}" , "").toLowerCase().split(" ");
   		   int count=0;
   		   for (int i=0;i<parts.length;i++) {
   		   trigger.setString(1, parts[i]);
   		   key=trigger.executeQuery();
   		   if (key.next())
   			   break;
   		   count++;
   		   }
   		   key.close();
   		   trigger.close();
    	if (count!=parts.length) {
       		type=FILTER_I;
       		record(customer);
       		connection.close();
       		Filter filter=new Filter(customer.getID());
       		return filter.filterSearch("book");
       		}
       	else {
       		connection.close();
       		return unknown(customer);}
       	
       	}catch(Exception e) {
       		log.info("Exception while reading database: {}", e.toString());
	   		return e.toString();
       	}
    }
    
    private String unknown(Customer customer) {
    	type = UNKNOWN;
    	record(customer);
    	return "Sorry, we can not find matched result.";
    }
    
    //TODO
    //After analysing the text, record the type of input in a temporary database(log) and record the question to the question-recording database
    public void record(Customer customer) {
    	try {
			Connection connection = KitchenSinkController.getConnection();
			//record the question to the question-recording database table named questionRecord
			String query1 = " insert into questionRecord values ( ?,?,?)";
			        
			
			PreparedStatement stmt = connection.prepareStatement(query1);
			//use a static data member to record the no.
			
			stmt.setString(1, text); 
			stmt.setInt(2, type);
			stmt.setString(3, customer.getID());
			stmt.executeQuery();
			if (type<8)
			{String query2 = " insert into usefulquestionRecord  values ( ?,?,?)";
			
			PreparedStatement stmt2 = connection.prepareStatement(query2);
			stmt2.setString(1, text);
			stmt2.setInt(2, type);
			stmt2.setString(3, customer.getID());
			stmt2.executeQuery();
			stmt2.close();}
			
			stmt.close();
			connection.close();
			
    	}catch (Exception e) {
			log.info("Exception while reading file: {}", e.toString());
			
		}
    
    }

}
