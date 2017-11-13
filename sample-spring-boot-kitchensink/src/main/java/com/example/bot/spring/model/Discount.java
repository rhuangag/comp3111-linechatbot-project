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

@Slf4j
public class Discount implements Observer{
	//Dclaration of data members
	String targetdate;
	String tourID;
	String targettime;
	private static final DateTimeFormatter FORMAT= DateTimeFormatter.ofPattern("yyyyMMddHHmm");
	
	public void update(Observable o, Object arg) {
		TimeManager temp = (TimeManager)o;
		String dateTime = FORMAT.format(temp.getDateTime());
		String date = dateTime.substring(0, 8);
		String time = dateTime.substring(8, 12);
		if((date == getTargetDate()) && (time.substring(0, 3) == getTargetTime().substring(0,3))) {
			discountNews();
		}
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
		this.tourID=tempid;
		
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
	}
		return tourID;
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
		this.targetdate=tempdate;
		
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
	}
		return targetdate;
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
		this.targettime=temptime;
		
		
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
	}
		return targettime;
	}
	
	//Methods
	private void discountNews() {
		try {
		Connection connection = KitchenSinkController.getConnection();
		PreparedStatement info = connection.prepareStatement("select * from discounttourlist where tourid=?");
		info.setString(1, getTourID());
		ResultSet news = info.executeQuery();
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

