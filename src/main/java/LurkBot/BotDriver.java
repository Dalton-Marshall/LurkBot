package LurkBot;

// imports

public class BotDriver {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		CommandList commands = new CommandList();
		CensorList censors = new CensorList();
		
		BotUI window = new BotUI();
		Bot chatbot = new Bot(window, commands, censors);
		window.connectBot(chatbot);
		commands.connectBot(chatbot);
	}
}
