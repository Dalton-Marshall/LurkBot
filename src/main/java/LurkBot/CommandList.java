package LurkBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class CommandList {
	private Map<String, String> commands = null;
	
	private File commandsFile = null;
	private Scanner reader = null;
	private String[] commandsFileLine = null;
	private FileWriter writer = null;
	
	CommandList() {
		commands = new HashMap<>();
		
		commandsFile = new File("commands.txt");
		
		try {
			reader = new Scanner(commandsFile);
			
			while(reader.hasNextLine()) {
				commandsFileLine = reader.nextLine().split(":");
				commands.put(commandsFileLine[0], commandsFileLine[1]);
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO log the error
			// TODO display error to user
			e.printStackTrace();
		}
	}
	
	public void addCommand(String commandString, String responseString) {
		commands.put(commandString, responseString);
		
		try {
			writer = new FileWriter(commandsFile, true);
			writer.write(commandString + ":" + responseString + "\n");
			writer.close();
		} catch (IOException e) {
			// TODO log the error
			// TODO display error to user
			e.printStackTrace();
		}
	}
	
	public void deleteCommand(String commandString) {
		if(commands.containsKey(commandString)) {
			commands.remove(commandString);
			
			// remove command from commands.txt
		}
	}
	
	public void updateCommand(String commandString, String newResponseString) {
		if(commands.containsKey(commandString)) {
			commands.replace(commandString, newResponseString);
			
			// TODO update command in commands.txt
		}
	}
	
	public String getResponseString(String commandString) {
		String response = null;
		
		if(commands.containsKey(commandString)) {
			response = commands.get(commandString); // value
		}
		else {
			response = "invalid command";
		}
		
		return response;
	}
}
