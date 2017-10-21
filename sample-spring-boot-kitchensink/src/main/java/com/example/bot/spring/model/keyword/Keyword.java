package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Keyword{
    	int type;
    	    
    	public Keyword(int t) {
    	    	type=t;
     }
	public int getType() {
	    	return type;
    	}
}