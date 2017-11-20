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

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.time.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.time.format.DateTimeFormatter;

import com.example.bot.spring.UpdateRecord;
import com.example.bot.spring.Customer;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { UpdateRecordTester.class})
	public class UpdateRecordTester{
	Customer example=new Customer("www");
	UpdateRecord tester = new UpdateRecord(example);

public void testAskForInformation1() {
boolean thrown =false;
String result="";
try {
result=tester.askForInformation(0, "qwe");
}

catch (Exception e) {

thrown = true;

}

assertThat(thrown).isEqualTo(false);

assertThat(result).isEqualTo("Invalid input.");

 	}


public void testAskForInformation2() {

boolean thrown =false;

String result="";

try {

result=tester.askForInformation(103, "qwe");

}

catch (Exception e) {

thrown = true;

}

assertThat(thrown).isEqualTo(false);

assertThat(result).contains("Invalid input. Maybe there");

 	}


public void testAskForInformation3() {

boolean thrown =false;

String result="";

try {

result=tester.askForInformation(104, "qwe");

}

catch (Exception e) {

thrown = true;

}

assertThat(thrown).isEqualTo(false);

assertThat(result).isEqualTo("Invalid input.");

 	}


public void testAskForInformation4() {

boolean thrown =false;

String result="";

try {

result=tester.askForInformation(103, "wu wei-2D10020171123-300");

}

catch (Exception e) {

thrown = true;

}

assertThat(thrown).isEqualTo(false);

assertThat(result).contains("Update success");

 	}



public void testAskForInformation5() {

boolean thrown =false;

String result="";

try {

result=tester.askForInformation(103, "wu wei-2D00320171111-0");

}

catch (Exception e) {

thrown = true;

}

assertThat(thrown).isEqualTo(false);

assertThat(result).contains("Update success");

 	}


@Test

public void testInOrder(){

testAskForInformation1();

testAskForInformation2();

testAskForInformation3();

testAskForInformation4();

testAskForInformation5();



}



    

}*/