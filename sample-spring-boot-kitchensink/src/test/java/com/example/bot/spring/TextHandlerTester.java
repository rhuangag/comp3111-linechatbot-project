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

import com.example.bot.spring.TextHandler;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {  TextHandlerTester.class})
public class TextHandlerTester {
	

	public void onebookingTest() throws Exception {
		TextHandler texthandler=new TextHandler("book");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("11. 3D842 Shenzhen city tour");
	}
	
	public void onechooseTest() throws Exception {
		TextHandler texthandler=new TextHandler("11");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("3D842 Shenzhen city tour* Window of The World  * Splendid China & Chinese Folk Culture Village *");
	}
	
	public void noTest() throws Exception {
		TextHandler texthandler=new TextHandler("no");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("Stop searching.");
	}
	@Test
	public void testOrderone() throws Exception{
		onebookingTest();
		onechooseTest();
		noTest();
	}
	
	public void HiTest() throws Exception {
		TextHandler texthandler=new TextHandler("hi");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("Hello! How can I help you?");
	}
	
	public void bookingTest() throws Exception {
		TextHandler texthandler=new TextHandler("hotspring");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("5. 3D991 Qingyuan historic-landscape tour");
	}
	
	public void chooseTest() throws Exception {
		TextHandler texthandler=new TextHandler("5");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("3D991 Qingyuan historic-landscape tour* Baojing Palace of Yingde *");
	}
	
	public void dnoTest() throws Exception {
		TextHandler texthandler=new TextHandler("yes");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("When are you planning to go for the trip?");
	}
	
	public void edateTest() throws Exception {
		TextHandler texthandler=new TextHandler("07/10/2017");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("May I know your name?");
	}
	
	public void fnameTest() throws Exception {
		TextHandler texthandler=new TextHandler("hrb");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("May I know your ID?");
	}
	
	public void gidTest() throws Exception {
		TextHandler texthandler=new TextHandler("666");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("May I know your age?");
	}
	
	public void hageTest() throws Exception {
		TextHandler texthandler=new TextHandler("16");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("Could you please tell us your phone number?");
	}
	
	public void iadultsTest() throws Exception {
		TextHandler texthandler=new TextHandler("123456");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("Could you please tell us the number of adults?");
	}
	
	public void jchildrenTest() throws Exception {
		TextHandler texthandler=new TextHandler("2");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("Could you please tell us the number of children?");
	}
	
	public void ktoodlersTest() throws Exception {
		TextHandler texthandler=new TextHandler("1");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("Could you please tell us the number of toodlers?");
	}
	
	public void lrequestTest() throws Exception {
		TextHandler texthandler=new TextHandler("0");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("special request");
	}
	
	
	public void mconfirmTest() throws Exception {
		TextHandler texthandler=new TextHandler("no");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("please reply 'confirm'");
	}
	
	
	public void nthankTest() throws Exception {
		TextHandler texthandler=new TextHandler("confirm");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("Thanks for booking! Your order is being well processed");
	}
	
	public void ofeedbackTest() throws Exception {
		TextHandler texthandler=new TextHandler("5");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("feedback is received");
	}
	@Test
	public void testOrder() throws Exception{
		HiTest();
		bookingTest();
		chooseTest();
		dnoTest();
		edateTest();
		fnameTest();
		gidTest();
		hageTest();
		iadultsTest();
		jchildrenTest();
		ktoodlersTest();
		lrequestTest();
		mconfirmTest();
		nthankTest();
		ofeedbackTest();
	}
	@Test
	public void test() throws Exception {
		TextHandler texthandler=new TextHandler("t");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("we cannot understand or find any match answer");
	}
	@Test
	public void twokeyTest() throws Exception {
		TextHandler texthandler=new TextHandler("hot spring");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("5. 3D991 Qingyuan historic-landscape tour");
	}
	@Test
	public void threekeyTest() throws Exception {
		TextHandler texthandler=new TextHandler("water theme park");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("1. 2D003");
	}
	@Test
	public void numberTest() throws Exception {
		TextHandler texthandler=new TextHandler("500");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("1. 2D001");
	}
	@Test
	public void cancelTest() throws Exception {
		TextHandler texthandler=new TextHandler("cancel");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("Sorry but you provided invalid or incorrect tourID");
	}
	@Test
	public void updateTest() throws Exception {
		TextHandler texthandler=new TextHandler("password");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("please reply givemefile");
	}
	@Test
	public void updatedisTest() throws Exception {
		TextHandler texthandler=new TextHandler("discountevent");
		Customer customer=new Customer("aaa");
		boolean thrown = false;
		String result = null;
		try {
			result = texthandler.messageHandler(customer);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).contains("Please input the information");
	}
}