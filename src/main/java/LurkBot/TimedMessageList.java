package LurkBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class TimedMessageList {
	private ArrayList<message> messageList;
	private File messagesFile = null;
	
	private Scanner reader = null;
	private String[] messagesFileLine = null;
	private FileWriter writer = null;
	
	private class message
	{
	    private String key;
	    private String message;
	    private int interval;
	    
	    public message(String key, String message, int interval)
	    {
	        this.key = key;
	        this.message = message;
	        this.interval = interval;
	    }
	    
	    @Override
	    public String toString()
	    {
	        return key;
	    }
	    
	    public String getMessage()
	    {
	        return message;
	    }
	    
	    public int getInterval()
	    {
	        return interval;
	    }
	}
	
	public TimedMessageList() {
		messageList = new ArrayList<>();
		messagesFile = new File("intervalMessages.txt");
		
		try {
			reader = new Scanner(messagesFile);
			
			while(reader.hasNextLine()) {
				messagesFileLine = reader.nextLine().split(":");
				messageList.add(new message(messagesFileLine[0], messagesFileLine[1], Integer.parseInt(messagesFileLine[2])));
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO log the error
			// TODO display error to user
			e.printStackTrace();
		}
		
		for(int i = 0; i < messageList.size(); i++) {
			new Timer().schedule(new TimerTask() { 
				   @Override
				   public void run() {
					   
					   //twitchClient.getChat().sendMessage(channelName, message);
					   //botui.chatroomAllAppendMessage("\nCheck out my youtube channel: youtube.com/channel");
				   }
				},  messageList.get(i).getInterval()); //300000);
		}
	}
	
	//private Timer timer = new Timer(int interval, String message);
	
//	timer5.schedule(new TimerTask() { 
//		   @Override
//		   public void run() {
//			   //twitchClient.getChat().sendMessage(channelName, message);
//			   //botui.chatroomAllAppendMessage("\nCheck out my youtube channel: youtube.com/channel");
//		   }
//		},  5000); //300000);
}
