package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringKeyword extends Keyword{
    	String key;
    	    
    	public StringKeyword(int t, String k) {
    	    	super(t);
    	    	key=k;
    	}
    	public String getKey() {
    	    	return key;
    	}
}