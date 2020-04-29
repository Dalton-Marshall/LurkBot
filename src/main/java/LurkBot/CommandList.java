package LurkBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import java.util.Map;
import java.util.HashMap;

public class CommandList {
	private Map<String, CommandAttributes> commands = null;
	
	private Bot bot;
	private File commandsFile;
	private Scanner reader;
	private String[] commandsFileLine;
	private FileWriter writer;
	
	private class CommandAttributes
	{
	    private String responseString;
	    private int cooldown;
	    private String userTypes;

	    public CommandAttributes(String responseString, int cooldown, String userTypes)
	    {
	        this.responseString = responseString;
	        this.cooldown = cooldown;
	        this.userTypes = userTypes;
	    }
	    
	    public String getResponseString()
	    {
	        return responseString;
	    }
	    
	    public int getCooldown()
	    {
	        return cooldown;
	    }
	    
	    public String getUserTypes()
	    {
	        return userTypes;
	    }
	}
	
	public CommandList() {
		commands = new HashMap<>();
		
		// Immutable commands
		commands.put("addcommand", new CommandAttributes("Command added.", 1, "mod"));
		commands.put("deletecommand", new CommandAttributes("Command deleted.", 1, "mod"));
		commands.put("updatecommand", new CommandAttributes("Command updated.", 1, "mod"));
		//commands.put("uptime", new CommandAttributes(bot.getChannelName() + " has been live for " + bot.getUptime(), 1, "mod"));
		commands.put("changetitle", new CommandAttributes("newStreamTitle", 1, "mod"));
		commands.put("changegame", new CommandAttributes("newGame", 1, "mod"));
		
//		commands.put("addcommand", new Object[] {"Command added", 1, "mod"});
//		commands.put("deletecommand", new Object[] {"Command deleted.", 1, "mod"});
//		commands.put("updatecommand", new Object[] {"Command updated.", 1, "mod"});
//		commands.put("uptime", new Object[] {bot.getChannelName() + " has been live for " + bot.getUptime(), 1, "all"});
//		commands.put("changetitle", new Object[] {"newStreamTitle", 1, "mod"});
//		commands.put("changegame", new Object[] {"newGame", 1, "mod"});
		
		commandsFile = new File("commands.txt");
		
		try {
			reader = new Scanner(commandsFile);
			
			while(reader.hasNextLine()) {
				commandsFileLine = reader.nextLine().split(":");
				commands.put(commandsFileLine[0], new CommandAttributes(commandsFileLine[1], Integer.parseInt(commandsFileLine[2]), commandsFileLine[3]));
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO log the error
			// TODO display error to user
			e.printStackTrace();
		}
	}
	
	public void connectBot(Bot bot) {
		this.bot = bot;
	}
	
	public void addCommand(String commandString, String responseString, int responseCooldown, String userTypes ) {
		commands.put(commandString, new CommandAttributes(responseString, responseCooldown, userTypes));
		//commands.put(commandString, new Object[] { responseString, responseCooldown, userTypes });
	}
	
	public void deleteCommand(String commandString) {
		if(commands.containsKey(commandString)) {
			commands.remove(commandString);
		}
	}
	
	public void updateCommand(String commandString, String newResponseString, int newResponseCooldown, String newUserTypes) {
		if(commands.containsKey(commandString)) {
			commands.replace(commandString, new CommandAttributes(newResponseString, newResponseCooldown, newUserTypes));
		}
	}
	
	public int getSize() {
		return commands.size();
	}
	
	public String[][] to2DStringArray() {
		String[][] data = new String[commands.size()][4];
		
		int i = 0;
		for(Map.Entry<String, CommandAttributes> entry : commands.entrySet()) {
			data[i][0] = entry.getKey();
			data[i][1] = entry.getValue().getResponseString();
			data[i][2] = Integer.toString(entry.getValue().getCooldown());
			data[i][3] = entry.getValue().getUserTypes().toString();
			i++;
		}
		
		return data;
	}
	
	public boolean saveToFile() {
		String commandString,  responseString;
		String userTypes;
		int cooldown;
		
		try {
			writer = new FileWriter(commandsFile, false);
			
			for(Map.Entry<String, CommandAttributes> entry : commands.entrySet()) {
				commandString = entry.getKey();
				responseString = entry.getValue().getResponseString();
				cooldown = entry.getValue().getCooldown();
				userTypes = entry.getValue().getUserTypes();
				
				writer.write(commandString + ":" + responseString + ":" + cooldown + ":" + userTypes + "\n");
			}
			
			writer.close();
			
			return true;
		} catch (IOException e) {
			// TODO log the error
			// TODO display error to user
			e.printStackTrace();
		}
		
		return false;
	}
	
	public String getResponseString(String commandString) {
		String response = null;
		
		if(commands.containsKey(commandString)) {
			response = commands.get(commandString).getResponseString();
		}
		else {
			response = "invalid command";
		}
		
		return response;
	}
}
