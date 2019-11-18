package LurkBot;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import java.awt.GridBagLayout;

public class BotDriver {

	private JFrame frmLurkbot;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BotDriver window = new BotDriver();
					window.frmLurkbot.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		TwitchClient twitchClient = TwitchClientBuilder.builder()
	            .withEnableHelix(true) // to talk to twitch API
	            .withEnableTMI(true) // to get chatters
	            .build();
		
		twitchClient.getChat().joinChannel("ChannelName");
	}

	/**
	 * Create the application.
	 */
	public BotDriver() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLurkbot = new JFrame();
		frmLurkbot.setResizable(false);
		frmLurkbot.setTitle("LurkBot");
		frmLurkbot.setBounds(100, 100, 750, 550);
		frmLurkbot.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmLurkbot.setJMenuBar(menuBar);
		
		JMenuItem FileMenuItem = new JMenuItem("File");
		menuBar.add(FileMenuItem);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0};
		gridBagLayout.rowHeights = new int[]{0};
		gridBagLayout.columnWeights = new double[]{Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{Double.MIN_VALUE};
		frmLurkbot.getContentPane().setLayout(gridBagLayout);
	}

}
