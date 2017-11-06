package com.example.bot.spring;

import java.awt.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.TimeZone;
import java.time.LocalDateTime;
import java.time.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.Observable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeManager extends Observable {
	//Data member declaration
    private final ScheduledExecutorService scheduler;
    private String time;
	private static SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd/HH");
	
	
	//Constructor
	public TimeManager() {
        this.scheduler = Executors.newScheduledThreadPool(1);

	}

	//Timer
	public void timing() {
		long delay = computeNextDelay(0,0);
		scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				passTime();
			}
		}, delay, 60*60, TimeUnit.SECONDS);
	}
	
	
	private long computeNextDelay(int targetMin, int targetSec) 
    {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("Asia/Shanghai");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNextTarget = zonedNow.withMinute(targetMin).withSecond(targetSec);
        if(zonedNow.compareTo(zonedNextTarget) > 0)
            zonedNextTarget = zonedNextTarget.plusHours(1);
        

        Duration duration = Duration.between(zonedNow, zonedNextTarget);
        return duration.getSeconds();
    }
	
	
	
	//Methods
	private void passTime() {
		/*try {
		String temp=format.format(new Date());
		Connection connection = KitchenSinkController.getConnection();
		PreparedStatement pstm=connection.prepareStatement("insert into testfortimer values ('"+temp+"')");
		pstm.executeUpdate();
		}catch (Exception e) {
			log.info("Exception while reading file: {}", e.toString());
		}*/
		time = format.format(new Date());
		setChanged();
		notifyObservers(this);
		
	}

}
