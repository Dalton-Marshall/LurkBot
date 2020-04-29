package LurkBot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Scanner;
import java.util.Calendar;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.events.channel.ChannelGoLiveEvent;
import com.github.twitch4j.common.events.user.PrivateMessageEvent;
import com.github.twitch4j.tmi.domain.Chatters;

public class Bot {
	private BotUI botui;
	private CommandList commands;
	private CensorList censors;
	private TwitchClient twitchClient;
	private TwitchClient whisperTwitchClient;
	private OAuth2Credential botCredential;
	private OAuth2Credential channelCredential;
	private File settingsFile;
	private Scanner reader;
	private String[] settingsFileLine;
	private String channelName;
	private String channelOauth;
	private String[] channelMods;
	private final String BOTOAUTH = "qmbevizwjp4vi8krqrfj5rp0jmesst";
	private String chatLogString = LocalDateTime.now() + "\n";
	private Date datetime = new Date();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
	private File chatLogFile = new File("ChatLogs/" + dateFormat.format(datetime) + ".txt");
	private Calendar streamStartTime;
	
	public Bot(BotUI botui, CommandList commands, CensorList censors) {
		
		this.botui = botui;
		this.commands = commands;
		this.censors = censors;
		
		settingsFile = new File("settings.txt");
		
		try {
			reader = new Scanner(settingsFile);
			
			while(reader.hasNextLine()) {
				settingsFileLine = reader.nextLine().split(":");
				if(settingsFileLine[1] != null) {
					switch(settingsFileLine[0]) {
					case "SavedChannel":
						channelName = settingsFileLine[1];
					case "ChannelOauth":
						channelOauth = settingsFileLine[1];
					case "ChannelMods":
						channelMods = settingsFileLine[1].split(",");
						break;
					default:
						// Log error
					}
				}
			}
			
			this.botui.setChannelModsList(channelMods);
			this.botui.setChannelName(channelName);
			this.botui.setChannelOauth(channelOauth);
			
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO log the error
			// TODO display error to user
			e.printStackTrace();
		}
		
		botCredential = new OAuth2Credential("twitch", BOTOAUTH); // bot oauth
		twitchClient = TwitchClientBuilder.builder()
            .withEnableHelix(true) // to talk to twitch API
            .withEnableTMI(true) // to get list of chatters
            .withChatAccount(botCredential) // to connect to chatroom
            .withEnableChat(true)
            .build();
		
		channelCredential = new OAuth2Credential("twitch", channelOauth); // user oauth
		whisperTwitchClient = TwitchClientBuilder.builder()
	            .withEnableHelix(true) // to talk to twitch API
	            .withChatAccount(channelCredential) // to connect to chatroom
	            .withEnableChat(true)
	            .build();
		
		twitchClient.getChat().joinChannel(channelName);
		
		botui.chatroomAllAppendMessage("= Entered chatroom =");
		botui.chatroomWhispersAppendMessage("= Whispers to you =");
		botui.chatroomDirectMessagesAppendMessage("= Direct messages to you =");
		botui.chatroomModMessagesAppendMessage("= Mod messages =");
		
		// Used for !uptime command primarily
		twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class).subscribe(event -> {
			//System.out.println("[" + event.getChannel().getName() + "] went live with title " + event.getTitle() + " on game " + event.getGameId() + "!");
			//streamStartTime = LocalDateTime.now();
			streamStartTime = event.getFiredAt();
		});
		
		// Handles whispers, displays in appropriate chatroom tab
		whisperTwitchClient.getChat().getEventManager().onEvent(PrivateMessageEvent.class).subscribe(event -> {
			//System.out.println("[Whisper] " + event.getUser().getName() + ": " + event.getMessage());
			botui.chatroomWhispersAppendMessage("[from] " + event.getUser().getName() + " > " + event.getMessage());
		});
		
		// Read chat room of currently connected channel
		twitchClient.getChat().getEventManager().onEvent(ChannelMessageEvent.class).subscribe(event -> {
			botui.chatroomAllAppendMessage(event.getUser().getName() + " > " + event.getMessage());
			chatLogString += "[" + event.getFiredAt().getTime() + "]" + event.getUser().getName() + ":" + event.getMessage() + "\n";
			
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
			
			if(event.getUser().getName() == "executeorder77") {
				botui.chatroomModMessagesAppendMessage(event.getUser().getName() + " > " + event.getMessage());
			}
			
			if(censors.stringContainsCensoredWord(event.getMessage().toLowerCase())) {
				sendMessageToChatroom("/timeout " + event.getUser().getName() + " " + 300 + " Posted a censored word.");
			}
			
			// Direct messages received
			if(event.getMessage().contains("@" + channelName)) {
				botui.chatroomDirectMessagesAppendMessage(event.getUser().getName() + " > " + event.getMessage());
			}
			
			// Link prevention
			// TODO allow moderators
			if(event.getMessage().contains("www.") || event.getMessage().contains(".com") || event.getMessage().contains(".net") || event.getMessage().contains(".org")) {
				//event.timeout(event.getUser().getName(), Duration.ofHours(1), "Link used without permission.");
				sendMessageToChatroom("/timeout " + event.getUser().getName() + " " + 600 + " Posted a link.");
				sendMessageToChatroom("@" + event.getUser().getName() + " Warning: Please do not post links.");
			}
			
			// Commands
			if(event.getMessage().charAt(0) == '!') {
				String commandString = event.getMessage().substring(1, event.getMessage().length()).toLowerCase();
				
				String response = commands.getResponseString(commandString);
				
				sendMessageToChatroom("@" + event.getUser().getName() + " " + response);
			}
		});
	}
	
	public void getViewers() {
		Chatters chatters = twitchClient.getMessagingInterface().getChatters(channelName).execute();
		
		System.out.println("VIPs: " + chatters.getVips());
		System.out.println("Mods: " + chatters.getModerators());
		System.out.println("Admins: " + chatters.getAdmins());
		System.out.println("Staff: " + chatters.getStaff());
		System.out.println("Viewers: " + chatters.getViewers());
		//System.out.println("All Viewers (sum of the above): " + chatters.getAllViewers());
	}
	
	public String getChannelName() {
		return channelName;
	}
	
	public void setChannel(String channelName) {
		this.channelName = channelName;
	}
	
	public String getChannelOauth() {
		return channelOauth;
	}
	
	public void setChannelOauth(String channelOauth) {
		this.channelOauth = channelOauth;
	}
	
	public String[] getChannelMods() {
		return channelMods;
	}
	
	public void setChannelMods(String[] channelMods) {
		for(int i = 0; i < channelMods.length; i++) {
			this.channelMods[i] = channelMods[i];
		}
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
		if(message.contains("/mod ") || message.contains("/unmod ")) {
			whisperTwitchClient.getChat().sendMessage(channelName, message);
		}
		else {
			twitchClient.getChat().sendMessage(channelName, message);
		}
	}
}
