package com.example.bot.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Message {
    //Declaration of data members
	String text;
	String keyword;
	int type;
	public static int No=0;
    public static final int FAQ=1;
    public static final int BOOK=2;
    public static final int CANCEL=3;
    public static final int FILTER=4;
    public static final int UNKNOWN=5;
    
    //Constructor
    public Message(String t) {
    	    text=t;
    	    keyword=null;
    	    type = 5;
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
    public void messageHandler() {
    	Connection connection = getConnection();
    	//TODO
    	//check whether the customer is booking
    	String query = "SELECT T1.type FROM questionRecord as T1  WHERE T1.No>= ALL (SELECT T2.type FROM¡@questionRecord as T2¡@)";
    	PreparedStatement stmt = connection.prepareStatement(query);
    	ResultSet rs =stmt.executeQuery();
    	if (rs.next()) {
    		if (rs.getInt(1)=BOOK) {
    			record();
    			//call the booking function;
    			//TODO
    		}
    	}
    		
    	//directly seach the text in db to get the type
    	
		PreparedStatement stmt1 = connection.prepareStatement("SELECT type FROM questionRecord WHERE question=?");
		stmt1.setString(1, text);
		rs =stmt1.executeQuery();
		if(rs.next())
			type =rs.getInt(1);
		
    	//if text is not in current db, firstly get words from text
    	String[] parts = text.toLowerCase().split(" ");
    	//case FAQ
    	if (checkFAQ(String[] parts))
    		type=FAQ;
    	else if (checkBook(text))
    		type =BOOK;
    	else if (checkCancel(text))
    		type=CANCEL;
    	else if (checkFilter(String[] parts))
    		type=FILTER;
    	else
    		type =UNKNOWN;
    	
    	
     	
    	connection.close();
    	record();
    	
    	switch (type) {
    	case FAQ:
    		//call FAQ function
    		break;
    	case BOOK:
    		//call book function
    		break;
    	case CANCEL:
    		//call cancel function
    		break;
    	case FILTER:
    		//call filter function
    		break;
    	default:
    		//return don't know
    	}
    	
    	
        return;    	
    }
    
    //TODO
    //After analysing the text, record the type of input in a temporary database(log) and record the question to the question-recording database
    public void record() {
    	try {
			Connection connection = getConnection();
			//record the question to the question-recording database table named questionRecord
			String query1 = " insert into questionRecord (No, question)"
			        + " values (?, ?)";
			
			PreparedStatement stmt = connection.prepareStatement(query1);
			//use a static data member to record the no.
			stmt.setString(1, No++);
			stmt.setString(2, text);
			
			stmt.executeQuery();
			
			//record the type of input in a temporary database(log)
			String query2 = " insert into temporary (No, type)"
			        + " values (?, ?)";
			
			PreparedStatement stmt = connection.prepareStatement(query2);
			//use a static data member to record the no.
			stmt.setInt(1, No);
			stmt.setString(2, type);
			
			stmt.executeQuery();
			
			connection.close();
    	}catch (Exception e) {
			log.info("Exception while reading file: {}", e.toString());
		}
    	    return;
    }
    
    public boolean checkFAQ(String[] parts) {    
    	//search every word in db
    	ResultSet rs;
    	PreparedStatement stmt = connection.prepareStatement("SELECT keyword1, keyword2, type FROM keywordlist WHERE keyword1 LIKE concat('%',' ',?,' ','%')");
    	for (int i=0; i<parts.length;i++) {
    		stmt.setString(1, parts[0]);
    		rs =stmt.executeQuery();
    		if (rs.next())
    			break;
    	}
    	if (!rs.next())
    		return false;
    	//now we find out the first keyword, check whether we need the second keyword
    	if (rs.getString(2)=="null")
    		return true;
    	else {
    		//check whether the sentence contains the second keyword
    		PreparedStatement stmt2 = connection.prepareStatement("SELECT keyword1, keyword2, type FROM keywordlistforfaq WHERE keyword2 LIKE concat('%',' ',?,' ','%')");
    		for (int i=0; i<parts.length;i++) {
    			stmt2.setString(1, parts[0]);
        		rs =stmt2.executeQuery();
        		if (rs.next())
        			break;
    		}
    		if (!rs.next())
        		return false;
    		else
    			return true;
    	}
    	
    }
    
    public boolean checkBook(text) {
    	//simpliy consider the base case now
    	if (text.toLowerCase().contains("book"))
    		return true;
    	else 
    		return false;
    }
    
    public boolean checkCancel(text) {  
    	//simpliy consider the base case now
    	if (text.toLowerCase().contains("cancel"))
    		return true;
    	else 
    		return false;
    }
    
    public boolean checkFilter(String[] parts) {
    	//simpliy consider only one keyword now
    	PreparedStatement stmt = connection.prepareStatement("SELECT keyword, type FROM keywordlistfilter WHERE keyword=?");
    	for (int i=0; i<parts.length;i++) {
			stmt1.setString(1, parts[0]);
    		ResultSet rs =stmt1.executeQuery();
    		if (rs.next())
    			return true;
		}
    	return false;
    }
}
