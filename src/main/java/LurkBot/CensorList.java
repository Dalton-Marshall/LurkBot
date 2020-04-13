package LurkBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class CensorList {
	private ArrayList<String> words;
	
	private File censoredWordsFile = null;
	private Scanner reader = null;
	private String censoredWordsLine = null;
	private FileWriter writer = null;
	
	public CensorList() {
		words = new ArrayList<>();
		
		censoredWordsFile = new File("censoredWords.txt");
		
		try {
			reader = new Scanner(censoredWordsFile);
			
			while(reader.hasNextLine()) {
				censoredWordsLine = reader.nextLine();
				words.add(censoredWordsLine);
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO log the error
			// TODO display error to user
			e.printStackTrace();
		}
	}
	
	public void addWord(String word) {
		words.add(word);
	}
	
	public void removeWord(String word) {
		words.remove(word);
	}
	
	public boolean stringContainsCensoredWord(String inputStr) {
	    //return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
	    return words.parallelStream().anyMatch(inputStr::contains);
	}
	
	public int getSize() {
		return words.size();
	}
	
	public Object[] toStringArray() {
		Object[] data = new String[words.size()];
		
		data = words.toArray();
		
		return data;
	}
	
	public boolean saveToFile() {
		// TODO export commandList to commands.txt
		try {
			writer = new FileWriter(censoredWordsFile, false);
			
			for(String entry : words) {
				writer.write(entry + "\n");
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
}
