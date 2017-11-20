package com.example.bot.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * The class Text Handler is a subject work in mediator pattern. This work as mediater that control the interactions between Filter, Booking, UpdateRecord, Report and Customer (see detial descriptions in javadocs in those classes).
 * The Text Handler is used to recognize and identify the type of requirement from customer and called different functions based on the type of recent requirement from customer and customers' current input.
 * 
 *
 */
@Slf4j
public class TextHandler {
    //Declaration of data members
	String text;
	//String keyword;
	int type;
	String[] parts;
	
	//define different types for questions
	private  final int FAQ=1;
    private  final int MEANINGLESS=2;
    private  final int UNKNOWN=3;
    private  final int CANCEL=4;
    private  final int FILTER_I=5;
    private  final int RECOMMENDATION=6;
    private  final int HISTORY=7;
    private  final int FILTER_II=8;
    private  final int BOOK_I=9;
    private  final int BOOK_II=10;
    private  final int BOOK_III=11;
    private  final int BOOK_IV=12;
    private  final int BOOK_V=13;
    private  final int BOOK_VI=14;
    private  final int BOOK_VII=15;
    private  final int BOOK_VIII=16;
    private  final int BOOK_IX=17;
    private  final int BOOK_X=18;
    private  final int BOOK_XI=19;
    private  final int BOOK_XII=20;
    private  final int DISCOUNT=100;
    private  final int PassWord=101;
    private  final int GiveMeFile=102;
    private  final int UpdatePayment=103;
    private  final int DiscountEvent=104;

    
    
    //Constructor
    /**
     * Constructor of the class TextHandler. It stores the String and initializes other data members.
     * @param t This is the text to be handled
     */
    public TextHandler(String t) {
    	    text=t;
    	   // keyword=null;
    	    type = UNKNOWN;
    	    parts = t.replaceAll("[^a-zA-Z0-9-\\s]" , " ").replaceAll("[\n]" , " ").toLowerCase().split(" ");
    }
    
    //Methods
    /**
     * This method is an access method to get the type of the text message.
     * @return This returns the type of the message
     */
    public int getType() {
	    return type;
}
    
    //TODO
    //Analyse the text input and initialize the data member  type 
    /**
     * This method can analyse the text message and return appropriate message.
     * @param customer This is used to provide the customer's information
     * @return java.lang.String This returns the appropriate message to reply 
     */
    public String messageHandler(Customer customer) {
    	String reply=null;
    	reply=checkStatus(customer);   	
    	return reply;		    
}
    private String checkStatus(Customer customer) {
    	//check whether the customer is in booking process
    	try {
    		//find the last quetion type for the specific customer who is sending text now
    		Connection connection = KitchenSinkController.getConnection();	
			PreparedStatement count = connection.prepareStatement("select count(index) from questionrecord where customerid=?");
			count.setString(1, customer.getID());
			ResultSet index=count.executeQuery();
			index.next();
			int number=index.getInt(1);
			count.close();
			index.close();
    		String query = "SELECT type FROM questionRecord where customerid=? and index=?";
    		PreparedStatement stmt = connection.prepareStatement(query);
    		stmt.setString(1,customer.getID());
    		stmt.setInt(2,number);
    		ResultSet rs =stmt.executeQuery();
    		//check whether the customer ask questions before
    		if (rs.next()) {
    			int temp=rs.getInt(1);
    			while (rs.next()) {
    				temp=rs.getInt(1);
    			}
    		//we find the customer did ask question before, temp is the type of last question	
    			if (temp>=BOOK_I && temp<BOOK_XII) {
    			    //the customer is in the booking process
    				
    				
    				rs.close();
    				stmt.close();
    				connection.close();
    				
    				Booking booking=new Booking(customer);
    				type=temp+1;
    				String reply=booking.askForInformation(type ,text);
    				//now just assume the customer will perfectly reply the correct information in the prototype
    				/*if (reply== "Booking Cancled, thanks for coming!" || reply.contains("ERROR")) {
    					type=MEANINGLESS;
    					record(customer);
    					return "Your booking is interrupted. Please book again.";
    					}
    				record(customer);
    				return reply;*/
    				if (reply == "Booking Cancled, thanks for coming!" || reply.contains("ERROR") || reply.contains("PSQLE")) {
    					type=MEANINGLESS;
    					record(customer);
    					try {
    						booking.askForInformation(0,"ha");
    					} catch (Exception e){
    						return "Your booking is interrupted. Please book again.(Maybe you had some invalid input)";
    						}
    					return "Your booking is interrupted. Please book again.(Maybe you had some invalid input)";
    					}
    				record(customer);
    				return reply;
    				
    				}
    			else if(temp==FILTER_I) {
    					//the customer just do the filter searching and we have returned a list of tour
    					Filter filter =new Filter(customer.getID());

    					String number_text="";
    					for (int i=0;i<parts.length;i++) {
    						if (isNumeric(parts[i])) {
    							number_text=parts[i];
    							break;
    						}
    					}
    					if (number_text.isEmpty()) {
    						rs.close();
    						stmt.close();
    						
    						PreparedStatement clearTempFilterTable = connection.prepareStatement
    								("Delete from TemporaryFilterTable where userId =?");
    						clearTempFilterTable.setString(1, customer.getID());
    						clearTempFilterTable.executeUpdate();
    						clearTempFilterTable.close();
    						connection.close();
    						return PasswordMatch(customer);
    					}
    					String answer=filter.viewDetails(number_text);
    					if (answer=="Sorry that there is no such a choice. You may ask for specific tours again and please show me the coorect choice :)") {
    						type=MEANINGLESS;
    						record(customer);
    						rs.close();
    						stmt.close();
    						PreparedStatement clearTempFilterTable = connection.prepareStatement
    								("Delete from TemporaryFilterTable where userId =?");
    						clearTempFilterTable.setString(1, customer.getID());
    						clearTempFilterTable.executeUpdate();
    						clearTempFilterTable.close();
    						connection.close();
    						return answer;
    					}
    					//answer is a reply that confirming the information
    					type=FILTER_II;
    					record(customer);
    					
    					String answer_reply=answer;
    					String[] parts = answer.split(" ");
    					String tourID=parts[0];
    					PreparedStatement stmt2 = connection.prepareStatement("insert into tempfortourID values (?,?)");
    					stmt2.setString(1,customer.getID());
    					stmt2.setString(2,tourID);
    					stmt2.executeUpdate();
    					rs.close();
    					stmt.close();
    					stmt2.close();
    					connection.close();
    					return answer_reply;}
    			else if (temp==FILTER_II) {
    				if (text.toLowerCase().contains("yes")) {
    					type=BOOK_I;
    					PreparedStatement stmt3 = 
    							connection.prepareStatement("SELECT temptourid from tempfortourid where customerid=?");
    					stmt3.setString(1,customer.getID());
    					rs =stmt3.executeQuery();
    					rs.next();
    					String tourID=rs.getString(1);
    					record(customer);
    					PreparedStatement stmt4 = connection.prepareStatement("Delete from TempfortourID where customerID=?");
    					stmt4.setString(1,customer.getID());
    					stmt4.executeUpdate();
    					
    					
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
    					PreparedStatement stmt5 = connection.prepareStatement("Delete from TempfortourID where customerID=?");
    					stmt5.setString(1,customer.getID());
    					stmt5.executeUpdate();
    					rs.close();
    					stmt.close();
    					stmt5.close();
    					
    					connection.close();
    					type=MEANINGLESS;
    					record(customer);
    					return "Stop searching.";}
    			}
    			else if (temp==UpdatePayment||temp==DiscountEvent) {
    				rs.close();
					stmt.close();
					connection.close();
    				type=MEANINGLESS;
					record(customer);
					UpdateRecord update=new UpdateRecord(customer);
					return update.askForInformation(temp,text);
    			}
    			else {
    				//the customer is not in the booking process
    				rs.close();
					stmt.close();
					connection.close();
    				return PasswordMatch(customer);}
    				}
    		else {
    			//the customer did not ask question before. 
    			rs.close();
				stmt.close();
				connection.close();
    			return PasswordMatch(customer);}
    	//TODO stop booking 
    		//now the chatbot did not support the interrupt of the booking process
    	}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return e.toString();}
    }
    private String PasswordMatch(Customer customer) {
    	if (functionMatch(PassWord,parts))
    	{
    		type=PassWord;
    		record(customer);
    		try {
    			Connection connection = KitchenSinkController.getConnection();
    			PreparedStatement hhh = connection.prepareStatement("select * from whitelist where clientid=?");
    			hhh.setString(1, customer.getID());
    			ResultSet aa=hhh.executeQuery();
    			if (!aa.next()) {
    			PreparedStatement whitelist = connection.prepareStatement("insert into whitelist values (?)");
    			whitelist.setString(1, customer.getID());
    			whitelist.executeUpdate();
    			whitelist.close();
    			hhh.close();
    			aa.close();
    			connection.close();}
    		}catch(Exception e) {
    	 		log.info("Exception while reading database: {}", e.toString());}
    		return "Want to get the record, please reply givemefile;\nWant to update the payment, please reply updatepayment;\nWant to add a new discount event, please reply discountevent.";
    	}
    	else
    		return GiveFile(customer);
    }
    private String GiveFile(Customer customer) {
    	try {
			Connection connection = KitchenSinkController.getConnection();
			PreparedStatement clientlist = connection.prepareStatement("select * from whitelist where clientid=?");
			clientlist.setString(1, customer.getID());
			ResultSet client=clientlist.executeQuery();
			boolean a=client.next();
			clientlist.close();
			client.close();
			connection.close();
      	if (functionMatch(GiveMeFile,parts)&& a)
    	{
    		type=GiveMeFile;
    		record(customer);
    		//String t="";
    		Report report1 = new Report("usefulquestionrecord",customer);
    		String astring = report1.writeReport();
    		//t="usefulquestion \n " + report1.writeReport();
    		Report report2 = new Report("feedbacktable",customer);
    		String bstring = report2.writeReport();
    		return "Above is the data collected so far.";
    		//t=t+"\n "+"feedbacktable"+"\n"+report2.writeReport();
    		//return t;
    	}
    	else
    		return UpdateRecord(customer);
		}catch(Exception e) {
	 		log.info("Exception while reading database: {}", e.toString());
	 		return e.toString();}
    }
    private String UpdateRecord(Customer customer) {
    	try {
			Connection connection = KitchenSinkController.getConnection();
			PreparedStatement clientlist = connection.prepareStatement("select * from whitelist where clientid=?");
			clientlist.setString(1, customer.getID());
			ResultSet client=clientlist.executeQuery();
			boolean a=client.next();
			clientlist.close();
			client.close();
			connection.close();
      	if (functionMatch(UpdatePayment,parts)&& a)
    	{
    		type=UpdatePayment;
    		record(customer);   		
    		return "Please input the information in this format: username-tourid-payment, eg. LI Hua-2D00311122017-199.6";
    	}
    	else if (functionMatch(DiscountEvent,parts)&& a) {
    		type=DiscountEvent;
    		record(customer);   		
    		return "Please input the information in this format: tourid-discountrate(percentage)-discountrate(float number)-capacity-seats-date-time, eg. 2D0031112-50%-0.5-2-3-20171112-0900";
    	}
    	else
    		return Discount(customer);
		}catch(Exception e) {
	 		log.info("Exception while reading database: {}", e.toString());
	 		return e.toString();}
    }
    private String Discount(Customer customer) {
   	try {
   		   Connection connection = KitchenSinkController.getConnection();	
   		//String[] parts = text.replaceAll("\\p{P}" , "").toLowerCase().split(" ");
		   PreparedStatement event = connection.prepareStatement("SELECT tourid FROM discounttourlist");
		   ResultSet exist=event.executeQuery();
		   String tourid=null;
		   while (exist.next()) {
			   tourid= exist.getString(1);
		   }
		   event.close();
		   exist.close();
 		if (functionMatch(DISCOUNT,parts)&& tourid!=null) {
 			PreparedStatement counting = connection.prepareStatement("SELECT count(userid) FROM discountuserlist");
 			ResultSet number=counting.executeQuery();
 			number.next();
 			PreparedStatement capacity = connection.prepareStatement("SELECT capacity FROM discounttourlist where tourid=?");
 			capacity.setString(1,tourid);
 			ResultSet rs=capacity.executeQuery();
 			rs.next();
 			if (number.getInt(1)>=rs.getInt(1)) {
 				rs.close();
 				capacity.close();
 				number.close();
 				counting.close();
 				connection.close();
 				type= MEANINGLESS;
 				record(customer);
 				return "Sorry, ticket sold out.";
 			}
 			
 			else {
 				rs.close();
 				capacity.close();
 				PreparedStatement checkdiscount= connection.prepareStatement("SELECT * FROM discountuserlist where userid=?");
 	 			checkdiscount.setString(1, customer.getID());
 				ResultSet discount=checkdiscount.executeQuery();
 				if (!discount.next()) {
 				type=DISCOUNT;
 				record(customer);
 				number.close();
 				counting.close();
 				discount.close();
 				checkdiscount.close();
 							
 				PreparedStatement insertdiscount = connection.prepareStatement(" insert into discountuserlist values ( ?,?)");
 				
 				insertdiscount.setString(1, customer.getID()); 
 				insertdiscount.setString(2, tourid);
 				insertdiscount.executeUpdate();
 				
 				insertdiscount.close();
 				connection.close();
 				return "Congratulations! You get the discount.";}
 				else
 				{   rs.close();
 					capacity.close();
 					number.close();
 	 				counting.close();
 					type= MEANINGLESS;
 	 				record(customer);
 	 				discount.close();
 	 				checkdiscount.close();
 	 				connection.close();
 	 				return "You have already got the discount.";
 				}
 			}
 		
 			}
 		else {
 			connection.close();
 			return newFAQ(customer);}
 	}catch(Exception e) {
 		log.info("Exception while reading database: {}", e.toString());
	   		return e.toString();
 	}
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
		rs.close();
		stmt1.close();   	
    	if (keywordMatch(parts,"keywordListForFAQ","keyword1")) {
    		if (keywordMatch(parts,"keywordListForFAQ","keyword2")) {
    			PreparedStatement findkey = connection.prepareStatement("SELECT reply FROM keywordListForFAQ WHERE lower(keyword3) LIKE concat('%',concat(',',?,','),'%')");
    			ResultSet key=null;
    			int countloop=0;
        		for (int i=0; i<parts.length;i++) {
        			findkey.setString(1, parts[i]);
        			key =findkey.executeQuery();
        			if (key.next()) { 
        				reply=key.getString(1);
        				break;}
        			countloop++;}
        		key.close();
    			findkey.close();
    			connection.close();
        		if (countloop!=parts.length) {
        			type=FAQ;
            		record(customer);
            		return reply;
    				}
    		}
    	}  		
    		connection.close();
    		return newCancel(customer);  		
    	}catch (Exception e){
    		log.info("Exception while reading database: {}", e.toString());   		
    		return (e.toString()+"newFAQ");
    		}       
    }
    
   private String newCancel(Customer customer){
	   String result=null;
	   try {
		   Connection connection = KitchenSinkController.getConnection();
		   if (functionMatch(CANCEL,parts)) {
    		type=CANCEL;
    		record(customer);   		
    		String reply="noRecord";
    		PreparedStatement stmt = connection.prepareStatement("SELECT Tourid FROM Customerrecord WHERE lower(Tourid) =?");   		
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
			return (e.toString()+"newcancel");}
	   return result;
    }
   
   private String newHitory(Customer customer) {
   		String[] parts = text.replaceAll("\\p{P}" , "").toLowerCase().split(" ");
   		if (functionMatch(HISTORY,parts)) { 			
   				type=HISTORY;
   				record(customer);   				
   				return customer.getHistory(); 		
   			}
   		else   			
   			return newRecommendation(customer);
}

    
    private String newRecommendation(Customer customer) {
    	try {
    	if (functionMatch(RECOMMENDATION,parts)) {
    			type=RECOMMENDATION;
    			record(customer);
    			return customer.getRecommendation();
    		}
    	else 
    		return newFiltering(customer);
    	}catch(Exception e) {
    		log.info("Exception while reading database: {}", e.toString());
	   		return e.toString();
    	}
    }
    
    
    
    private String newFiltering(Customer customer) {
    		try {
    			String ID=customer.getID();
        		Filter filter=new Filter(ID);
        		
    	    	String reply=null;
    	    	int countloop=0;
    	    	Connection connection = KitchenSinkController.getConnection();
    	    	if (parts.length>2) {
        		String query="SELECT keyword1 FROM threekeyword WHERE lower(keyword1) LIKE concat('%',concat(',',?,','),'%')";
    			PreparedStatement findthreekey = connection.prepareStatement(query);
    			
    			String keyword1=null;
        		for (int i=0; i<parts.length-2;i++) {
        			findthreekey.setString(1, parts[i]);
        			ResultSet threekey=null;
        			threekey =findthreekey.executeQuery();
        			if (threekey.next()){ 
        				keyword1=threekey.getString(1);
        				threekey.close();
						findthreekey.close();
            			PreparedStatement findkeyword23 = connection.prepareStatement("SELECT keyword3,reply FROM threekeyword WHERE lower(keyword2) LIKE concat('%',concat(',',?,','),'%') and keyword1=?");
            			findkeyword23.setString(1, parts[i+1]);
            			findkeyword23.setString(2, keyword1);
            			ResultSet k=null;
            			k=findkeyword23.executeQuery();
        				if (k.next()) {
        					if (k.getString(1).contains(parts[i+2])) {
        						reply=k.getString(2);
        						type=FILTER_I;
        						record(customer);
        						k.close();
        						findkeyword23.close();
        						connection.close();
            					return filter.filterSearch(reply);  }      					
        				}
        				k.close();
						findkeyword23.close();
        				}
        			threekey.close(); 
        		}
        		      			
    			findthreekey.close();}
    		//now check two keywords
        		String query="SELECT keyword1 FROM twokeyword WHERE lower(keyword1) LIKE concat('%',concat(',',?,','),'%')";
    			PreparedStatement findkey = connection.prepareStatement(query);
    			
    			String keyword1=null;
        		for (int i=0; i<parts.length-1;i++) {
        			findkey.setString(1, parts[i]);
        			ResultSet key=null;
        			key =findkey.executeQuery();
        			if (key.next()){ 
        				keyword1=key.getString(1);
        				key.close();
						findkey.close();
            			PreparedStatement findkeyword2 = connection.prepareStatement("SELECT reply FROM twokeyword WHERE lower(keyword2) LIKE concat('%',concat(',',?,','),'%') and keyword1=?");
            			findkeyword2.setString(1, parts[i+1]);
            			findkeyword2.setString(2, keyword1);
            			ResultSet k=null;
            			k=findkeyword2.executeQuery();
        				if (k.next()) {
        						reply=k.getString(1);
        						type=FILTER_I;
        						record(customer);
        						k.close();
        						findkeyword2.close();
        						connection.close();
            					return filter.filterSearch(reply);        					
        				}
        				k.close();
						findkeyword2.close();
        				}
        			key.close(); 
        		}
        		      			
    			findkey.close();
    		
    		//now find three keyword

    	    	PreparedStatement findonekey = connection.prepareStatement("SELECT reply FROM onekeyword WHERE lower(keyword1) =?");
    	    	ResultSet onekey=null;
    	    	countloop=0;
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
    		String[] number=text.replaceAll("[^0-9]", ",").split(",");
    		String temp="";
    		
    		for (int i=0;i<number.length;i++) {
    			if (!number[i].isEmpty()&&(isNumeric(number[i]))) {
    				
    				temp+=number[i];
    				if (i!=number.length-1)
    					temp+=",";
    				
    			}
    		}
    		if (temp.isEmpty()) {
    			connection.close();
    			return newBooking(customer);}
    		if (text.contains("cheaper than")||text.contains("less than"))
    				temp="<"+temp;
    		if (text.contains("higher than")||text.contains("more than"))
    				temp=">"+temp;

			if (filter.filterSearch(temp)=="Sorry, we cannot find any match answer for your question :( We already record your question and will forward it to the tour company.") {
				type=UNKNOWN;
				record(customer);
				connection.close();
				return filter.filterSearch(temp);
			}
    		type=FILTER_I;
			record(customer);
			connection.close();
			return filter.filterSearch(temp);
    	   /* */
    		}catch(Exception e) {
    			log.info("Exception while reading database: {}", e.toString());
    	   		return (e.toString()+"newfiltering");
    		}
    }
    
    
    //enrich db
    private String newBooking(Customer customer) {
       	try {
    	if (functionMatch(FILTER_I,parts)) {
       		type=FILTER_I;
       		record(customer);
       		Filter filter=new Filter(customer.getID());
       		return filter.filterSearch("book");
       		}
       	else 
       		return unknown(customer);
       	}catch(Exception e) {
       		log.info("Exception while reading database: {}", e.toString());
	   		return (e.toString()+"newbooking");
       	}
    }
    
    private String unknown(Customer customer) {
    	type = UNKNOWN;
    	record(customer);
    	return "Sorry, we cannot understand or find any match answer for your question :( We already record your question and will forward it to the tour company.";
    }
    
    //TODO
    //After analysing the text, record the type of input in a temporary database(log) and record the question to the question-recording database
    private void record(Customer customer) {
    	try {
			Connection connection = KitchenSinkController.getConnection();
			//record the question to the question-recording database table named questionRecord
			String query1 = " insert into questionRecord values ( ?,?,?,?)";
			
			PreparedStatement count = connection.prepareStatement("select count(index) from questionrecord where customerid=?");
			count.setString(1, customer.getID());
			ResultSet index=count.executeQuery();
			index.next();
			int number=index.getInt(1)+1;
			count.close();
			index.close();
			PreparedStatement stmt = connection.prepareStatement(query1);
			//use a static data member to record the no.
			
			stmt.setString(1, text); 
			stmt.setInt(2, type);
			stmt.setString(3, customer.getID());
			stmt.setInt(4, number);
			stmt.executeUpdate();
			if (type==5)
			{String query2 = " insert into usefulquestionRecord  values ( ?,?,?)";
			
			PreparedStatement stmt2 = connection.prepareStatement(query2);
			stmt2.setString(1, text);
			stmt2.setInt(2, type);
			stmt2.setString(3, customer.getID());
			stmt2.executeUpdate();
			stmt2.close();}
			
			stmt.close();
			connection.close();
			
    	}catch (Exception e) {
			log.info("Exception while reading file: {}", e.toString());
			
		}
    
    }
    private boolean functionMatch(int type,String[] parts){
    	try {
    			   Connection connection = KitchenSinkController.getConnection();
    	 		   PreparedStatement trigger = connection.prepareStatement("SELECT keyword FROM keywordlistforfunction WHERE type = ? and keyword like concat('%',concat(',',?,','),'%')");
    	 		   trigger.setInt(1,type);
    			   ResultSet key=null;   	 		   
    	 		   int count=0;
    	 		   for (int i=0;i<parts.length;i++) {
    	 		   trigger.setString(2, parts[i]);
    	 		   key=trigger.executeQuery();
    	 		   if (key.next())
    	 			   break;
    	 		   count++;
    	 		   }
    	 		   key.close();
    	 		   trigger.close();
    			   connection.close();
    			   if (count!=parts.length)
    				return true;
    			   else 
    				return false;
    }catch (Exception e) {
		log.info("Exception while reading file: {}", e.toString());
		return false;}
	}
    private boolean keywordMatch(String[] parts,String table,String colomn) {
    	try {
    		Connection connection = KitchenSinkController.getConnection();
    		String query="SELECT reply FROM "+table+" WHERE lower("+colomn+") LIKE concat('%',concat(',',?,','),'%')";
			PreparedStatement findkey = connection.prepareStatement(query);
			ResultSet key=null;
			int countloop=0;
    		for (int i=0; i<parts.length;i++) {
    			findkey.setString(1, parts[i]);
    			key =findkey.executeQuery();
    			if (key.next()) 
    				break;
    			countloop++;}
    		key.close();
			findkey.close();
			connection.close();
    		if (countloop!=parts.length)
    			return true;
    		else
    			return false;
    	}catch (Exception e) {
    		log.info("Exception while reading file: {}", e.toString());
    		return false;}
    }
	private boolean isNumeric(String str){  
		  for (int i = str.length();--i>=0;){    
		   if (!Character.isDigit(str.charAt(i))){  
		    return false;  
		   }  
		  }  
		  return true;  
		}  
}