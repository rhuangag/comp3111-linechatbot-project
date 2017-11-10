package com.example.bot.spring;

import java.awt.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Observable;


import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Notification {
	private final String[] cancelMessage= {"Sorry to tell you that your tour for ", " is cancelled since not enough customer joined, hope to serve for you next time."};
	private final String[] confirmMessage= {"Glad to tell you that your tour for ", " is confirmed. The information of the guide for this tour is the follwing: "};
	private String currentDate;
	
	public Notification(){
		currentDate=null;
	}
	
	//update the time and check if fulfill the requirement to go to Notify()
	public void update(){}
	
	private String TargetDate(int remindDays){
		String targetDate="";
		String[] parts = currentDate.split("/");
		int day=Integer.parseInt(parts[0])+remindDays;
		int month=Integer.parseInt(parts[1]);
		int year=Integer.parseInt(parts[2]);
		if (month==2&&day>29&&(year%400==0)||(year%100!=0&&year%4==0)){
			day-=29;
			month++;
		}
		else if (month==2&&day>28&&year%4!=0||(year%100==0&&year%400!=0)) {
			day-=28;
			month++;
		}
		else if (day>31&&(month==1||month==3||month==5||month==7||month==8||month==10||month==12)) {
			if (month==12) {
				day-=31;
				month=1;
				year++;
			}
			else {
				day-=31;
				month++;
			}
		}
		else if(day>30&&(month==4||month==6||month==9||month==11)) {
			day-=30;
			month++;
		}
		
		if (day < 10) {targetDate+="0";}
		targetDate+=Integer.toString(day)+"/";
		if (month < 10) {targetDate+="0";}
		targetDate+=Integer.toString(month)+"/"+Integer.toString(year);
		return targetDate;
}
	
	
	
	//check the condition (3 days before a tour) and determine whether the tour is confirmed, full or cancelled. Update this status in database;
	private void NotifyStatus(){
		String targetDate=TargetDate(3);
		String remindDate=TargetDate(2);
		String CancelMessage="";
		try {
		Connection connection = KitchenSinkController.getConnection();
		PreparedStatement notifyCancelled =connection.prepareStatement("Select booktableid from BookingTable where departureDate=? and status='availiable' and cast(confirmedCustomer as int)<cast(minimumcustomer as int)");
		PreparedStatement notifyConfirmed =connection.prepareStatement("Select booktableid, tourguide, tourguideaccount from BookingTable where status='availiable' and cast(confirmedCustomer as int)>=cast(minimumcustomer as int)");
		
		notifyCancelled.setString(1, targetDate);
		ResultSet cancelRs=notifyCancelled.executeQuery();
		ResultSet confirmRs=notifyConfirmed.executeQuery();
		
		while(cancelRs.next()) {
			String cancelTour=cancelRs.getString(1);
			PreparedStatement notifyUserCancel =connection.prepareStatement("Select userid from customertable where tourjoined=?");
			notifyUserCancel.setString(1, cancelTour);
			ResultSet rsNotifyCancel=notifyUserCancel.executeQuery();
			while(rsNotifyCancel.next()) {
				String cancelUser=rsNotifyCancel.getString(1);
				pushCancelMessage(cancelUser,cancelTour);
			}
			rsNotifyCancel.close();
			notifyUserCancel.close();
		}
		cancelRs.close();
		
		while(confirmRs.next()) {
			String confirmedTour=confirmRs.getString(1);
			String guideInformation="Name: "+ confirmRs.getString(2) + " LINE account: "+ confirmRs.getString(3) ;
			PreparedStatement notifyUserConfirm =connection.prepareStatement("Select userid from customertable where tourjoined=?");
			notifyUserConfirm.setString(1, confirmedTour);
			ResultSet rsNotifyConfirm=notifyUserConfirm.executeQuery();
			while(rsNotifyConfirm.next()) {
				String confirmedUser=rsNotifyConfirm.getString(1);
				pushConfirmMessage(confirmedUser,confirmedTour,guideInformation);
			}
			rsNotifyConfirm.close();
			notifyUserConfirm.close();
		}
		confirmRs.close();
	
		PreparedStatement UpdateCancelled =connection.prepareStatement("Update BookingTable set status='cancelled' where departureDate=? and status='availiable' and cast(confirmedCustomer as int)<cast(minimumcustomer as int)");
		PreparedStatement UpdateFull =connection.prepareStatement("Update BookingTable set status='full' where departureDate=? and status='availiable' and cast(confirmedCustomer as int)<cast(minimumcustomer as int)");
		PreparedStatement UpdateConfirmed =connection.prepareStatement("Update BookingTable set status='confirmed' where departureDate=? and status='availiable' and cast(confirmedCustomer as int)<cast(minimumcustomer as int)");
		UpdateCancelled.executeUpdate();
		UpdateFull.executeUpdate();
		UpdateConfirmed.executeUpdate();
		
		
		notifyCancelled.close();
		notifyConfirmed.close();
		UpdateCancelled.close();
		UpdateFull.close();
		UpdateConfirmed.close();
		connection.close();
		
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
	}
}

	
	//push a message to the customer who booked the tour when the status of a tour changed to confirmed or cancelled due to participants number
	private String pushConfirmMessage(String userID, String tour, String guideInformation){
		String message=confirmMessage[0]+tour+confirmMessage[1]+guideInformation;
		TextMessage textMessage = new TextMessage(message);
		PushMessage pushMessage = new PushMessage(
		        "<to>",
		        textMessage
		);

		Response<BotApiResponse> response =
		        LineMessagingServiceBuilder
		                .create("<channel access token>")
		                .build()
		                .pushMessage(pushMessage)
		                .execute();
		System.out.println(response.code() + " " + response.message());


		return message;
		
	}
	private String pushCancelMessage(String userID, String tour) {
		String message=cancelMessage[0]+tour+ cancelMessage[1];
		return message;
	}
	
	//functional function in this class
	
}