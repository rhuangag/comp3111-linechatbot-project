package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Message {
    //Declaration of data members
	String text;
	String keyword;
	int type;
    

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
    	
    	    return;
    }
    
    
}
