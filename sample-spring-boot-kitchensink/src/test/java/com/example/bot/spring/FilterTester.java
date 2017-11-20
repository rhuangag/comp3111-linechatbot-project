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
@SpringBootTest(classes = { FilterTester.class})
public class FilterTester{
		
	 	
	    public void testAFilterSearch1() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.filterSearch("book");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("11.");
	 	}
	    
	 	
	 	
		 public void testAViewDetials1() {
			 	boolean thrown =false;
			 	String result="";
			 	try {
				Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
				result=tester.viewDetails("1");
				}
				catch (Exception e) {
					thrown = true;
				}
				assertThat(thrown).isEqualTo(false);
				assertThat(result).contains("2D001");
		    }
	 
	 

	 public void testBFilterSearch2() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.filterSearch("300,500");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("3D077");
	    }
	 
	 
	
	 public void testBViewDetials2() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.viewDetails("2");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("2D005");
	    }
	 
	

	 public void testCFilterSearch3() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.filterSearch("600");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("3D019");
	    }
	 
	 
	 public void testCViewDetials3() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.viewDetails("1");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("3D019");
	    }
	 
	 
	 public void testDFilterSearch4() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.filterSearch("3");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("3D842");
	    }
	 

	 public void testDViewDetials4() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.viewDetails("2");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("3D075");
	    }
	 
	 public void testEFilterSearch5() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.filterSearch(">2");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("3D842");
	    }

	 public void testEViewDetials5() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.viewDetails("3");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("3D077");
	    }

	 public void testFFilterSearch6() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.filterSearch(">500");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("3D991");
	    }
	 

	 public void testFViewDetials6() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.viewDetails("5");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("3D842");
	    }
	 
	 
	 public void testGFilterSearch7() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.filterSearch("<3");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("2D");
	    }
	 
	
	 public void testGViewDetials7() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.viewDetails("3");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("2D003");
	    }
	 

	 public void testHFilterSearch8() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.filterSearch("<500");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("2D001");
	    }
	 

	 public void testHViewDetials8() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.viewDetails("2");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("2D002");
	    }
	 

	 public void testJFilterSearch10() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.filterSearch("US");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("Sorry");
	    }
	 
	 

	 public void testIFilterSearch9() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.filterSearch("hot spring");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("2D001");
	    }
	 

	 public void testIViewDetials9() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.viewDetails("5");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("3D991");
	    }
	 

	 public void testJViewDetials10() {
		 	boolean thrown =false;
		 	String result="";
		 	try {
			Filter tester = new Filter("U4e37da0ad17a38c22b3011d3d1b3644d");
			result=tester.viewDetails("qweqwe");
			}
			catch (Exception e) {
				thrown = true;
			}
			assertThat(thrown).isEqualTo(false);
			assertThat(result).contains("Sorry that there is no such a choice.");
	    }
	 
	 @Test 
	 public void testingInOrder() {
		 testAFilterSearch1();
		 testAViewDetials1();
		 testBFilterSearch2();
		 testBViewDetials2();
		 testCFilterSearch3();
		 testCViewDetials3();
		 testDFilterSearch4();
		 testDViewDetials4();
		 testEFilterSearch5();
		 testEViewDetials5();
		 testFFilterSearch6();
		 testFViewDetials6();
		 testGFilterSearch7();
		 testGViewDetials7();
		 testHFilterSearch8();
		 testHViewDetials8();
		 testIFilterSearch9();
		 testIViewDetials9();
		 testJFilterSearch10();
		 testJViewDetials10(); 
	 }
	  
}