package LurkBot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Calendar;

import java.util.Timer;
import java.util.TimerTask;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.events.channel.ChannelGoLiveEvent;
import com.github.twitch4j.common.events.user.PrivateMessageEvent;

public class Bot {
	private BotUI botui;
	private TwitchClient twitchClient;
	private OAuth2Credential credential;
	private String channelName = "Channel";
	private final String OAUTH = "qmbevxrfj5rp0jmesst";
	private String chatLogString = LocalDateTime.now() + "\n";
	private Date datetime = new Date();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
	private File chatLogFile = new File(dateFormat.format(datetime) + ".txt");
	private Calendar streamStartTime;
	private Timer timer5;
	private Timer timer10;
	private Timer timer15;
	private Timer timer30;
	
	public Bot(BotUI botui, CommandList commands, CensorList censors) {
		this.botui = botui;
		
		timer5 = new Timer();
		timer10 = new Timer();
		timer15 = new Timer();
		timer30 = new Timer();
		
		timer5.schedule(new TimerTask() { 
			   @Override
			   public void run() {
				   //twitchClient.getChat().sendMessage(channelName, message);
				   botui.chatroomAllAppendMessage("\nCheck out my youtube channel: youtube.com/channel");
			   }
			},  5000); //300000);
		timer10.schedule(new TimerTask() { 
			   @Override
			   public void run() {
				 //twitchClient.getChat().sendMessage(channelName, message);
			   }
			},  600000);
		timer15.schedule(new TimerTask() { 
			   @Override
			   public void run() {
				 //twitchClient.getChat().sendMessage(channelName, message);
			   }
			},  900000);
		timer30.schedule(new TimerTask() { 
			   @Override
			   public void run() {
				 //twitchClient.getChat().sendMessage(channelName, message);
			   }
			},  3600000);
		
		credential = new OAuth2Credential("twitch", OAUTH); // bot oauth
		twitchClient = TwitchClientBuilder.builder()
            .withEnableHelix(true) // to talk to twitch API
            //.withEnableTMI(true) // to get list of chatters
            .withChatAccount(credential) // to connect to chatroom
            .withEnableChat(true)
            .build();
		
		twitchClient.getChat().joinChannel(channelName);
		
		this.botui.chatroomAllAppendMessage("= Entered chatroom =");
		
		// Used for !uptime command primarily
		twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class).subscribe(event -> {
			//System.out.println("[" + event.getChannel().getName() + "] went live with title " + event.getTitle() + " on game " + event.getGameId() + "!");
			//streamStartTime = LocalDateTime.now();
			streamStartTime = event.getFiredAt();
		});
		
		// Read chat room of currently connected channel
		twitchClient.getChat().getEventManager().onEvent(ChannelMessageEvent.class).subscribe(event -> {
			botui.chatroomAllAppendMessage("\n" + event.getUser().getName() + " > " + event.getMessage());
			chatLogString += event.getFiredAt() + ":" + event.getUser().getName() + ":" + event.getMessage() + "\n";
			
			BufferedWriter out = null;
			if(chatLogString.length() > (Integer.MAX_VALUE / 2)) {
				try {
					out = new BufferedWriter(new FileWriter(chatLogFile, true));
					out.write(chatLogString);
					out.close();
					chatLogString = "";
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			// TODO compare words in message to list of censored words
			if(event.getMessage().contains("www.") || event.getMessage().contains(".com")) {
				event.timeout(event.getUser().getName(), Duration.ofHours(1), "Link used without permission.");
				//sendMessageToChatroom("@" + event.getUser().getName() + ", please dont send links.");
			}
			
			if(event.getMessage().contains("@" + channelName)) {
				botui.chatroomDirectMessagesAppendMessage(event.getUser().getName() + " > " + event.getMessage());
			}
			
			if(event.getMessage().charAt(0) == '!') {
				String commandString = event.getMessage().substring(1, event.getMessage().length());
				
				String response = commands.getResponseString(commandString);
				
				//twitchClient.getChat().sendMessage(channelName, "@" + event.getUser().getName() + response);
				//sendMessageToChatroom("@" + event.getUser().getName() + response);
			}
		});
		
		// Handles whispers, displays in appropriate chatroom tab
		twitchClient.getChat().getEventManager().onEvent(PrivateMessageEvent.class).subscribe(event -> {
			//System.out.println("[Whisper] " + event.getUser().getName() + ": " + event.getMessage());
			botui.chatroomAllAppendMessage(event.getUser().getName() + " > " + event.getMessage());
		});
	}
	
	public String getChannelName() {
		return channelName;
	}
	
	public String getUptime() {
		String uptimeString = "";
		Calendar now = Calendar.getInstance();
		
		streamStartTime.get(Calendar.MINUTE);
		streamStartTime.get(Calendar.SECOND);
		
		uptimeString += (now.get(Calendar.HOUR) - streamStartTime.get(Calendar.HOUR)) + " hours, " + 
				(now.get(Calendar.MINUTE) - streamStartTime.get(Calendar.MINUTE)) + " minutes, and " + 
				(now.get(Calendar.SECOND) - streamStartTime.get(Calendar.SECOND)) + " seconds.";
		
		return uptimeString;
	}
	
	public void leaveChannel() {
		twitchClient.getChat().leaveChannel(channelName);
	}
	
	public void logChat() {
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(chatLogFile, true));
			out.write(chatLogString);
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void sendMessageToChatroom(String message) {
		twitchClient.getChat().sendMessage(channelName, message);
	}
}
