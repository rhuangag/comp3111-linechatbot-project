package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntKeyword extends Keyword{
	int key;
	    
    public IntKeyword(int t, int k) {
	    	super(t);
	    	key=k;
	}
	public int getKey() {
	     return key;
    }
}
