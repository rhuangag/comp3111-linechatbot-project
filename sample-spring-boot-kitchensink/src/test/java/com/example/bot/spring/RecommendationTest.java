
/*package com.example.bot.spring;


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
@SpringBootTest(classes = { RecommendationTest.class, Customer.class })
public class RecommendationTest {
	
	
	//format: result="Tour ID: "+rs.getString("TourID")+ "\nTour Name: "+rs.getString("TourName")+"\nTour Description: "+rs.getString("TourDescription")+ "\nDuration: "+rs.getString("Duration")+"\nDate: "+rs.getString("Date")+"\nWeekend Price: "+rs.getString("WeekendPrice")+"\nWeekday Price: "+rs.getString("WeekdayPrice");
	@Test
	public void testRecommend() throws Exception{
		Customer customer = new Customer ("U7602b36236a0bc9ea3871c89f4e834dd");
		//boolean thrown = false;
		String result = null;
		
		try {
			result = customer.getRecommendation();
		}catch (Exception e) {
			//thrown = true;
		}
		
		//assertThat(thrown).isEqualTo(false);
		assertThat(result).isEqualTo("Tour ID: 2D001\r\n" + 
				"Tour Name: Shimen National Forest Tour\r\n" + 
				"Tour Description: Shimen colorful pond * stunning red maple * Staying at \"Yihua Hot Spring Hotel\"\r\n" + 
				"Duration: 2\r\n" + 
				"Date: Mon / Wed / Sat\r\n" + 
				"Weekend Price: 599\r\n" + 
				"Weekday Price: 499");
	}
}
*/