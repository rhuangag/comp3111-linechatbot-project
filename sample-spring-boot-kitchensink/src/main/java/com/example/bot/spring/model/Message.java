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
    

    //Constructor
    public Message(String t) {
    	    text=t;
    	    keyword=null;
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
			stmt.setString(1, No);
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
    
    
}
