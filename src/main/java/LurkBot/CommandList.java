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
		
		
	}
	
	public void deleteCommand(String commandString) {
		if(commands.containsKey(commandString)) {
			commands.remove(commandString);
		}
	}
	
	public void updateCommand(String commandString, String newResponseString) {
		if(commands.containsKey(commandString)) {
			commands.replace(commandString, newResponseString);
		}
	}
	
	public int getSize() {
		return commands.size();
	}
	
	public String[][] to2DStringArray() {
		String[][] data = new String[commands.size()][2];
		
		int i = 0;
		for(Map.Entry<String, String> entry : commands.entrySet()) {
			data[i][0] = entry.getKey();
			data[i][1] = entry.getValue();
			i++;
		}
		
		return data;
	}
	
	public boolean saveToFile() {
		String commandString,  responseString;
		// TODO export commandList to commands.txt
		try {
			writer = new FileWriter(commandsFile, false);
			
			for(Map.Entry<String, String> entry : commands.entrySet()) {
				commandString = entry.getKey();
				responseString = entry.getValue();
				writer.write(commandString + ":" + responseString + "\n");
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
			response = commands.get(commandString); // value
		}
		else {
			response = "invalid command";
		}
		
		return response;
	}
}
