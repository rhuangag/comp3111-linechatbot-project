package com.example.bot.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeManager {
	//Data member declaration
	private final ScheduledExecutorService scheduler;
	private static SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	//Constructor
	public TimeManager() {
        this.scheduler = Executors.newScheduledThreadPool(1);

	}
	
	
	
	public void timing() {
		scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				test();
			}
		}, 10, 10, TimeUnit.SECONDS);
	}
	
	public void test() {
		try {
		String temp=format.format(new Date());
		Connection connection = KitchenSinkController.getConnection();
		PreparedStatement pstm=connection.prepareStatement("insert into testfortimer values ('"+temp+"')");
		pstm.executeUpdate();
		}catch (Exception e) {
			log.info("Exception while reading file: {}", e.toString());
		}
		
		
	}

}
