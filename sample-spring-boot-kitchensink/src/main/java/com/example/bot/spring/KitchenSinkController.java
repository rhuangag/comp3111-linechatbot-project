/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import com.linecorp.bot.model.profile.UserProfileResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.BeaconEvent;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.UnfollowEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.AudioMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.imagemap.ImagemapArea;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.model.message.imagemap.MessageImagemapAction;
import com.linecorp.bot.model.message.imagemap.URIImagemapAction;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
/**
 * 
 * The class KitchenSinkController is the controller class of MVC model to respond to customer and regulate our functional classes
 * 
 */
@Slf4j
@LineMessageHandler
public class KitchenSinkController {
	


	@Autowired
	private LineMessagingClient lineMessagingClient;
	
/**
 * This method is used to recognize the text input from the user
 * @param event This is the text input from customer
 * @throws Exception This will happen when something goes wrong
 */
	@EventMapping
	public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		log.info("This is your entry point:");
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		TextMessageContent message = event.getMessage();
		handleTextContent(event.getReplyToken(), event, message);
	}

	/**
	 * This method is used to recognize the sticker input from the user
	 * @param event This is the sticker input from customer
	 */
	@EventMapping
	public void handleStickerMessageEvent(MessageEvent<StickerMessageContent> event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Sorry we cannot understand this kind of input.");
	}

	/**
	 *  This method is used to recognize the location message input from the user
	 * @param event This is the location message input from customer
	 */
	@EventMapping
	public void handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Sorry we cannot understand this kind of input.");
	}

	/**
	 * This method is used to recognize the image input from the user
	 * @param event This is the image input from customer
	 * @throws IOException This will happen when some errors happen in input or output stream
	 */
	@EventMapping
	public void handleImageMessageEvent(MessageEvent<ImageMessageContent> event) throws IOException {
		String replyToken =event.getReplyToken();
		log.info("Got image message from {}: {}", replyToken, event);
		
		final String emailAddress= "comp3111@travel.com";
	    
		replyText(replyToken,"Sorry. The chatbot cannot check your payment proof. Please send the image to our financial "
					+ "department via "+emailAddress);

	}

	/**
	 * This method is used to recognize the audio input from the user
	 * @param event This is the audio input from customer
	 * @throws IOException This will happen when some errors happen in input or output stream
	 */
	@EventMapping
	public void handleAudioMessageEvent(MessageEvent<AudioMessageContent> event) throws IOException {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Sorry we cannot understand this kind of input.");
	}

	/**
	 * This method is used to recognize the unfollow request from the user
	 * @param event This is the unfollow request from customer
	 */
	@EventMapping
	public void handleUnfollowEvent(UnfollowEvent event) {
		log.info("unfollowed this bot: {}", event);
	}

	/**
	 * This method is used to recognize the follow request from the user
	 * @param event This is the follow request from customer
	 */
	@EventMapping
	public void handleFollowEvent(FollowEvent event) {
		try{
		Connection connection = getConnection();
		PreparedStatement search = connection.prepareStatement("select * from friends where userid=?");
		search.setString(1, event.getSource().getUserId());
		ResultSet rs=search.executeQuery();
		if (!rs.next()) {
		PreparedStatement add = connection.prepareStatement("insert into friends values (?)");
		add.setString(1, event.getSource().getUserId());
		add.executeUpdate();
		add.close();
		}
		rs.close();
		search.close();
		connection.close();
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
		}
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Welcome to the chatbot!");
	}

	/**
	 * This method is used to recognize the Join request from the user
	 * @param event This is the Join request from customer
	 */
	@EventMapping
	public void handleJoinEvent(JoinEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Joined " + event.getSource());
	}

	/**
	 * This method is used to recognize Post Back action from the user
	 * @param event This is the post back action from customer
	 */
	@EventMapping
	public void handlePostbackEvent(PostbackEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Got postback " + event.getPostbackContent().getData());
	}

	/**
	 * This method is used to recognize Beacon action from the user
	 * @param event This is the Beacon action from customer
	 */
	@EventMapping
	public void handleBeaconEvent(BeaconEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Got beacon message " + event.getBeacon().getHwid());
	}

	/**
	 * This method is used to recognize other events from user
	 * @param event This is the other event input from customer
	 */
	@EventMapping
	public void handleOtherEvent(Event event) {
		log.info("Received message(Ignored): {}", event);
	}

	private void reply(@NonNull String replyToken, @NonNull Message message) {
		reply(replyToken, Collections.singletonList(message));
	}

	private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
		try {
			BotApiResponse apiResponse = lineMessagingClient.replyMessage(new ReplyMessage(replyToken, messages)).get();
			log.info("Sent messages: {}", apiResponse);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private void replyText(@NonNull String replyToken, @NonNull String message) {
		if (replyToken.isEmpty()) {
			throw new IllegalArgumentException("replyToken must not be empty");
		}
		if (message.length() > 1000) {
			message = message.substring(0, 1000 - 2) + "..";
		}
		this.reply(replyToken, new TextMessage(message));
	}


	private void handleTextContent(String replyToken, Event event, TextMessageContent content)
            throws Exception {
        String text = content.getText();

        log.info("Got text message from {}: {}", replyToken, text);
        
                
       	String reply = null;
       	
       	Customer customer=new Customer(event.getSource().getUserId());
       	TextHandler handler=new TextHandler(text);
       	reply=handler.messageHandler(customer);
       	this.replyText(replyToken, reply);
    }
    

	static String createUri(String path) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).build().toUriString();
	}

	private void system(String... args) {
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		try {
			Process start = processBuilder.start();
			int i = start.waitFor();
			log.info("result: {} =>  {}", Arrays.toString(args), i);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (InterruptedException e) {
			log.info("Interrupted", e);
			Thread.currentThread().interrupt();
		}
	}
/*
	private static DownloadedContent saveContent(String ext, MessageContentResponse responseBody) {
		log.info("Got content-type: {}", responseBody);

		DownloadedContent tempFile = createTempFile(ext);
		try (OutputStream outputStream = Files.newOutputStream(tempFile.path)) {
			ByteStreams.copy(responseBody.getStream(), outputStream);
			log.info("Saved {}: {}", ext, tempFile);
			return tempFile;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}*/
/*
	private static DownloadedContent createTempFile(String ext) {
		String fileName = LocalDateTime.now().toString() + '-' + UUID.randomUUID().toString() + '.' + ext;
		Path tempFile = KitchenSinkApplication.downloadedContentDir.resolve(fileName);
		tempFile.toFile().deleteOnExit();
		return new DownloadedContent(tempFile, createUri("/downloaded/" + tempFile.getFileName()));
	}
*/

	

/**
 * This is the constructor of KitchenSinkController.
 */
	public KitchenSinkController() {
		itscLOGIN = System.getenv("ITSC_LOGIN");
	}


	private String itscLOGIN;
	
/*    
	//The annontation @Value is from the package lombok.Value
	//Basically what it does is to generate constructor and getter for the class below
	//See https://projectlombok.org/features/Value
	@Value
	public static class DownloadedContent {
		Path path;
		String uri;
	}
*/    
    
	
	//an inner class that gets the user profile and status message
	class ProfileGetter implements BiConsumer<UserProfileResponse, Throwable> {
		private KitchenSinkController ksc;
		private String replyToken;
		
		public ProfileGetter(KitchenSinkController ksc, String replyToken) {
			this.ksc = ksc;
			this.replyToken = replyToken;
		}
		@Override
    	public void accept(UserProfileResponse profile, Throwable throwable) {
    		if (throwable != null) {
            	ksc.replyText(replyToken, throwable.getMessage());
            	return;
        	}
        	ksc.reply(
                	replyToken,
                	Arrays.asList(new TextMessage(
                		"Display name: " + profile.getDisplayName()),
                              	new TextMessage("Status message: "
                            		  + profile.getStatusMessage()))
        	);
    	}
    }
    
	
	/**
	 * This is used to connect our database in heroku
	 * @return This returns the connection to our heroku database
	 * @throws URISyntaxException This happens when URI is has syntax error 
	 * @throws SQLException This happen when SQL database related issues goes wrong
	 */
	public static Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));
		

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}
	
	
	static void pushMessageController(PushMessage pushMessage) {
		try {
		        LineMessagingServiceBuilder
		        // channel access token need to be changed later
		                .create("9xlCVi1/ahHiIRgB0tWNwmzB0mq/KkDHSmAGL+aXWYxK/rEi7chuPonwjtL1nh4Y+LN6He2nPOLLRh0RGuPjOGc6/1CIPQOtZi9klVr04EnKiK83oBv2rXk9IQvfGZD3gJFOZ3YFUQBp6Y55+uvfmwdB04t89/1O/w1cDnyilFU=")
		                .build()
		                .pushMessage(pushMessage)
		                .execute();
		}catch(Exception e) {
			System.out.println("There is an exception");
		}
		
	}

}
