package com.example.bot.spring;

import java.awt.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.example.bot.spring.KitchenSinkController;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.TimeZone;
import java.time.LocalDateTime;
import java.time.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.time.format.DateTimeFormatter;

import java.util.Observer;
import java.util.Observable;

import lombok.extern.slf4j.Slf4j;

/**
 * The class Discount is an observer in the observer pattern. When the time of the dicount events come, it pushes the event message.
 * 
 *
 */
@Slf4j
public class Discount implements Observer{
	//Dclaration of data members
	private static final DateTimeFormatter FORMAT= DateTimeFormatter.ofPattern("yyyyMMddHHmm");
	
	//for test ONLY
	public int inupdate1 = 0;
	public int inupdate2 = 0;
	
	/**
	 * This method is used in the observer pattern to receive the notification from the subject class TimeManager. 
	 * When the current time reaches the event time, it pushes to event messages to all LINE friends of the chatbot.
	 */
	public void update(Observable o, Object arg) {
		TimeManager temp = (TimeManager)o;
		String dateTime = FORMAT.format(temp.getDateTime());
		String date = dateTime.substring(0, 8);
		String time = dateTime.substring(8, 12);
		if((date.equals(getTargetDate())) && (time.substring(0, 3).equals(getTargetTime().substring(0,3)))) {
			inupdate1 = 1;
			discountNews();
		}
		inupdate2 =1;
	}
	
	private String getTourID() {
		try {
		Connection connection = KitchenSinkController.getConnection();
		PreparedStatement all = connection.prepareStatement("select * from discounttourlist");
		String tempid=null;
		ResultSet findid = all.executeQuery();	
		while(findid.next()) {
			tempid=findid.getString(1);
		}
		all.close();
		findid.close();
		connection.close();
		return tempid;
		
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return e.toString();
	}
	}
	
	private String getTargetDate() {
		try {
		Connection connection = KitchenSinkController.getConnection();
		PreparedStatement all = connection.prepareStatement("select * from discounttourlist");
		String tempdate=null;
		ResultSet finddate = all.executeQuery();	
		while(finddate.next()) {
			tempdate=finddate.getString(6);
		}
		all.close();
		finddate.close();
		connection.close();
		return tempdate;
		
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return e.toString();
	}
	}
	private String getTargetTime() {
		try {
		Connection connection = KitchenSinkController.getConnection();
		PreparedStatement all = connection.prepareStatement("select * from discounttourlist");
		String temptime=null;
		ResultSet findtime = all.executeQuery();	
		while(findtime.next()) {
			temptime=findtime.getString(7);
		}
		all.close();
		findtime.close();
		connection.close();
		return temptime;
		
		
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return e.toString();
	}
	}
	
	//Methods
	private void discountNews() {
		try {
		Connection connection = KitchenSinkController.getConnection();
		PreparedStatement info = connection.prepareStatement("select * from discounttourlist where tourid=?");
		info.setString(1, getTourID());
		ResultSet news = info.executeQuery();
		news.next();
		String message;
		message="We have a discount event now. For tour "+news.getString(1)+", the first "+news.getInt(4)+" customers reply can have a discount rate ("+news.getString(2)+" off) for that tour. Each customer can reserve "+news.getInt(5)+" seats at most. If you want to get discount, reply Double11";
		
		PreparedStatement friend = connection.prepareStatement("select * from friends");
		
		ResultSet user = friend.executeQuery();
		while (user.next()) {
			pushDiscountNews(message,user.getString(1));
		}
		info.close();
		news.close();
		friend.close();
		user.close();
		connection.close();
		
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
		}
			
				
	}
	private void pushDiscountNews(String message,String userid) {
		TextMessage textMessage = new TextMessage(message);
		PushMessage pushMessage = new PushMessage(
		        userid,
		        textMessage
		        );
		KitchenSinkController.pushMessageController(pushMessage);
	}
}

