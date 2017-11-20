 package com.example.bot.spring;

 

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.LineBotMessages;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.time.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.time.format.DateTimeFormatter;


import com.example.bot.spring.PaymentReminder;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { PaymentReminderTester.class})
public class PaymentReminderTester {
	private static final DateTimeFormatter FORMAT= DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm");
	
	//this also cover that no tour is found
	@Test
    public void testUpdate1() {
    	    boolean thrown = false;
		PaymentReminder tester = new PaymentReminder();
		TimeManager tm = TimeManager.getTimer();
		
		ZoneId currentZone = ZoneId.of("Asia/Shanghai");
        ZonedDateTime zonedNow = ZonedDateTime.now(currentZone);
        ZonedDateTime target = zonedNow.withHour(12);
        
		tm.addObserver(tester);
		
        int result = 0;
		try {
			tm.setZonedDateTime(target);
			tm.testNotify();
			result = tester.inupdate1;
    	 	}catch(Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(1);
    }

    @Test
    public void testUpdate2() {
    	    boolean thrown = false;
		PaymentReminder tester = new PaymentReminder();
		TimeManager tm = TimeManager.getTimer();
		
		ZoneId currentZone = ZoneId.of("Asia/Shanghai");
        ZonedDateTime zonedNow = ZonedDateTime.now(currentZone);
        ZonedDateTime target = zonedNow.withHour(20);
        
		tm.addObserver(tester);
		
        int result = 0;
		try {
			tm.setZonedDateTime(target);
			tm.testNotify();
			result = tester.inupdate2;
    	 	}catch(Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(1);
    }
    
    @Test
    public void testReminder1() {
    	    boolean thrown = false;
		PaymentReminder tester = new PaymentReminder();
		TimeManager tm = TimeManager.getTimer();
		
		ZoneId currentZone = ZoneId.of("Asia/Shanghai");
        ZonedDateTime zonedNow = ZonedDateTime.now(currentZone);
        ZonedDateTime target = zonedNow.withDayOfMonth(6).withHour(12);
        
		tm.addObserver(tester);
		
        int result = 0;
		try {
			tm.setZonedDateTime(target);
			tm.testNotify();
			result = tester.inreminder;
    	 	}catch(Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(1);
    }
    
    @Test
    public void testReminder2() {
    	    boolean thrown = false;
		PaymentReminder tester = new PaymentReminder();
		TimeManager tm = TimeManager.getTimer();
		
		ZoneId currentZone = ZoneId.of("Asia/Shanghai");
        ZonedDateTime zonedNow = ZonedDateTime.now(currentZone);
        ZonedDateTime target = zonedNow.withDayOfMonth(6).withHour(12);
        
		tm.addObserver(tester);
		
        int result = 0;
		try {
			tm.setZonedDateTime(target);
			tm.testNotify();
			result = tester.inreminder2;
    	 	}catch(Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(1);

    }
}