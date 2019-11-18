package LurkBot;

public class Connection {
	//Connection connection = new Connection();
	Connection() {}
	
	Connection(int z) {
		twitchClient.getChat().joinChannel("PlayOverwatch");
	}
}
