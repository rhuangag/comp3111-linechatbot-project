package com.example.bot.spring;

import java.awt.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.PushMessage;

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
import java.time.format.DateTimeFormatter;

import java.util.Observable;

import lombok.extern.slf4j.Slf4j;

/**
 * The class TimeManager is a subject in the observer pattern. In addition, it is a singleton class which only has one object.
 * It implements a timer and notify its observers every hour at 0 minute.
 * 
 *
 */
@Slf4j
public class TimeManager extends Observable {
	//Data member declaration
    private final ScheduledExecutorService SCHEDULER;
    private String time;
    private ZonedDateTime dateTime;
	private static final DateTimeFormatter FORMAT= DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm");
	
	private static TimeManager uniqueTimer = new TimeManager();
	
	
	//Constructor
	private TimeManager() {
        this.SCHEDULER = Executors.newScheduledThreadPool(1);

	}
	
	//Get the singleton object
	/**
	 * This method is used to get the instance of this singleton class.
	 * @return TimeManager This returns the unique object of class TimeManager.
	 */
	public static TimeManager getTimer() {
		return uniqueTimer;
	}
	
	//Access function
	/**
	 * This method is used to get the time (yyyy/MM/dd/HH/mm) stored in the object.
	 * @return java.lang.String This returns the time in "yyyy/MM/dd/HH/mm" format.
	 */
	public String getTime() {
		return time;
	}
	
	/**
	 * This method is used to get the ZonedDateTime object stored in this object.
	 * @return ZonedDateTime This returns the ZonedDateTime object.
	 */
	public ZonedDateTime getDateTime() {
		return dateTime;
	}

	//Timer
	/**
	 * This method starts the timer at 0 minute in the next hour and notify the observers every hour. When the observers are notified, the time is recorded in the database.
	 */
	public void timing() {
		long delay = computeNextDelay(10,0);
		SCHEDULER.scheduleAtFixedRate(new Runnable() {
			public void run() {
				passTime();
			}
		}, delay, 1*60, TimeUnit.SECONDS);
	}
	
	
	private long computeNextDelay(int targetMin, int targetSec) 
    {
        ZoneId currentZone = ZoneId.of("Asia/Shanghai");
        ZonedDateTime zonedNow = ZonedDateTime.now(currentZone);
        ZonedDateTime zonedNextTarget = zonedNow.withMinute(targetMin).withSecond(targetSec);
        if(zonedNow.compareTo(zonedNextTarget) > 0)
            zonedNextTarget = zonedNextTarget.plusHours(1);
        

        Duration duration = Duration.between(zonedNow, zonedNextTarget);
        return duration.getSeconds();
    }
	
	
	
	//Methods
	private void passTime() {
		/* ONLY FOR TEST
		 * try {
		String temp=format.format(new Date());
		Connection connection = KitchenSinkController.getConnection();
		PreparedStatement pstm=connection.prepareStatement("insert into testfortimer values ('"+temp+"')");
		pstm.executeUpdate();
		}catch (Exception e) {
			log.info("Exception while reading file: {}", e.toString());
		}*/
		try{
			ZoneId currentZone = ZoneId.of("Asia/Shanghai");
        ZonedDateTime zonedNow = ZonedDateTime.now(currentZone);
		time = FORMAT.format(zonedNow);
		dateTime=zonedNow;
		Connection connection = KitchenSinkController.getConnection();
		PreparedStatement ps = connection.prepareStatement("insert into timer values ('"+time+"')");
		ps.executeUpdate();
		
		ps.close();
		connection.close();
		
		setChanged();
		notifyObservers(this);
	}catch (Exception e){
		log.info("Exception while reading database: {}", e.toString());
		TextMessage textMessage = new TextMessage(e.toString());
		PushMessage pushMessage = new PushMessage("U7602b36236a0bc9ea3871c89f4e834dd", textMessage);
		KitchenSinkController.pushMessageController(pushMessage);
	}
		
	}

}