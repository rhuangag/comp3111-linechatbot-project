package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RangeKeyword extends Keyword{
	int[] key;
	    
	public RangeKeyword(int t, int k1, int k2) {
	    	super(t);
	    	key[0]=k1;
	    	key[1]=k2;
	}
	public int[] getKey() {
	    return key;
	}
}
