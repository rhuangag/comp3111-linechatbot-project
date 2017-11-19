/*
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

import com.example.bot.spring.Customer;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { CustomerTester.class})
public class CustomerTester {
	@Test
    public void testGetId(){
		boolean thrown = false;
		Customer tester = new Customer("test");
        String result = null;
		try {
			result = tester.getID();
    	 	}catch(Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("test");	
    }
	
	@Test
	public void testGetHistory() {
		boolean thrown = false;
		Customer tester = new Customer("test");
        String result = null;
		try {
			result = tester.getHistory();
    	 	}catch(Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("There is no record.");
	}
	
	@Test
	public void testFindHistory() {
		boolean thrown = false;
		Customer tester = new Customer("U4e37da0ad17a38c22b3011d3d1b3644d");
        String result = null;
		try {
			result = tester.getHistory();
    	 	}catch(Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("Tour ID: ");
	}
	
	//test whether the vector is empty
	@Test
	public void testFindHistory2() {
		boolean thrown = false;
		Customer tester = new Customer("U4e37da0ad17a38c22b3011d3d1b3644d");
        String result = null;
		try {
			result = tester.getHistory();
			result = tester.getHistory();
    	 	}catch(Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("Tour ID: ");
	}
    
	@Test
	public void testCancelBooking() {
		boolean thrown = false;
		Customer tester = new Customer("U7602b36236a0bc9ea3871c89f4e834dd");
		Customer tester2 = new Customer("test");
        String result = null;
        String result2 = null;
		try {
			result = tester.cancelBooking("2D001");
			result2 = tester2.cancelBooking("2D001");
    	 	}catch(Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Your booking has been cancelled. Hope to serve for you next time!");
		assertThat(result2).contains("Sorry but you provided invalid or incorrect tourID.");
	}
	
	
}
*/