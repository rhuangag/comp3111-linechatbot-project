package com.example.bot.spring;

import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.PushMessage;

import java.awt.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
 * The class PaymentReminder is an observer in the observer pattern.
 * When the date reaches 5 days before the departure date of a tour, the payment reminder messages are generated and pushed to the customers 
 * who booked this tour but haven't pay all the tour fee.
 * 
 *
 */
@Slf4j
public class PaymentReminder implements Observer {
	//Dclaration of data members
	String targetdate;
	private static final DateTimeFormatter FORMAT= DateTimeFormatter.ofPattern("yyyy/MM/dd/HH");
	
	//For test ONLY
	public int inupdate1 = 0;
	public int inupdate2 = 0;
	public int inreminder = 0;
	public int inreminder2 = 0;
	//Implement Observer
	/**
	 * This method is used in the observer pattern to receive the notification from the subject class TimeManager.
	 * It check whether the current date is 5 days before the departure day. If so, it pushes the reminder message to customers.
	 */
	public void update(Observable o, Object arg) {
		TimeManager temp = (TimeManager)o;
		String currentTime = FORMAT.format(temp.getDateTime().plusDays(5));
		String[] time = currentTime.split("/");
		if(time[3].equals("12")) {
			inupdate1 = 1;
			this.targetdate=time[0]+time[1]+time[2];
			reminder();
		}
		inupdate2=1;
			
	}
	
	//Methods
	private void reminder() {
		try {
		Connection connection = KitchenSinkController.getConnection();
		PreparedStatement pst = connection.prepareStatement("select userid, tourjoined, tourfee, amountpaid"
				+ " from customertable where tourjoined like concat('%', ?, '%') and status like concat('%', ?, '%')");
		pst.setString(1, this.targetdate);
		pst.setString(2, "booked");
		ResultSet rs = pst.executeQuery();
		while (rs.next()) {
			double amountOwed = rs.getDouble("tourfee")-rs.getDouble("amountpaid");
				inreminder = 1;
				//Get 2D002 from 2D00220171112
				String tourId = rs.getString("tourjoined").substring(0, 5);
				//Get 12/11/2017 from 2D00220171112
				String departureDate = rs.getString("tourjoined").substring(11, 13)+"/"+rs.getString("tourjoined").substring(9, 11)
						+"/"+rs.getString("tourjoined").substring(5, 9);
				String message="Payment Reminder:\n"
						+ "Please be reminded that you haven't paid the full tour fee for the following tour:\n"
						+ "TourID: "+tourId+"\nDeparture Date[dd/MM/yyyy]: "+departureDate+"\nAmount owed: "+amountOwed+" HKD\n\n"
						+ "Please be noted that the rest amount of tour fee need to be paid at least 3 days before departure. "
						+"Otherwise, your booking may be cancelled. Thanks for your cooperation.";
				// push message to users here
				TextMessage textMessage = new TextMessage(message);
				PushMessage pushMessage = new PushMessage(rs.getString("userid"), textMessage);
				KitchenSinkController.pushMessageController(pushMessage);
			
			inreminder2 =1;
		}
		rs.close();
		pst.close();
		connection.close();
		
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			TextMessage textMessage = new TextMessage(e.toString());
			PushMessage pushMessage = new PushMessage("U7602b36236a0bc9ea3871c89f4e834dd", textMessage);
			KitchenSinkController.pushMessageController(pushMessage);
		}
			
		
		
		
	}

}
