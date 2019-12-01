package LurkBot;

import java.awt.EventQueue;
//import java.util.Iterator;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

public class BotDriver {

	private JFrame frmLurkbot;
	private static TwitchClient twitchClient;
	private static OAuth2Credential credential;
	private static final String CHANNEL = "";
	private static final String OAUTH = "twitchoauthexampleabcdef";
	
	private static JTextArea chatroomReceivedMessagesTextArea;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//final Logger logger = LogManager.getLogger("BotDriver");
		
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
		
		credential = new OAuth2Credential("twitch", OAUTH); // bot oauth
		
		twitchClient = TwitchClientBuilder.builder()
	            .withEnableHelix(true) // to talk to twitch API
	            //.withEnableTMI(true) // to get list of chatters
	            .withChatAccount(credential) // to connect to chatroom
	            .withEnableChat(true)
	            .build();
		
		twitchClient.getChat().joinChannel(CHANNEL);
		//twitchClient.getChat().sendMessage(CHANNEL, "has entered the room.");
		
		twitchClient.getChat().getEventManager().onEvent(ChannelMessageEvent.class).subscribe(event -> {
			//System.out.println(/* "[" + event.getChannel().getName() + "] " + */event.getUser().getName() + " > " + event.getMessage());
			chatroomReceivedMessagesTextArea.append("\n" + event.getUser().getName() + " > " + event.getMessage());
			
			if(event.getMessage().startsWith("!test")) {
				twitchClient.getChat().sendMessage(CHANNEL, "@" + event.getUser().getName() + " test command success!");
			}
			// TODO: implement command function call here
		});
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
		frmLurkbot.setBounds(100, 100, 300, 650);
		frmLurkbot.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLurkbot.getContentPane().setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		frmLurkbot.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Disconnect and Exit");
		mntmExit.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent event) {
	            twitchClient.getChat().leaveChannel(CHANNEL);
	        	System.exit(0);
	        }
	    });
		mnFile.add(mntmExit);
		
		JTextArea chatroomSendMessageTextArea = new JTextArea();
		chatroomSendMessageTextArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chatroomSendMessageTextArea.setText("");
			}
		});
		chatroomSendMessageTextArea.setText("Send a message...");
		chatroomSendMessageTextArea.setBounds(10, 468, 250, 70);
		frmLurkbot.getContentPane().add(chatroomSendMessageTextArea);
		
		JLabel lblChatroom = new JLabel("Chatroom");
		lblChatroom.setBounds(10, 0, 125, 14);
		frmLurkbot.getContentPane().add(lblChatroom);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 14, 250, 450);
		frmLurkbot.getContentPane().add(scrollPane);
		
		chatroomReceivedMessagesTextArea = new JTextArea();
		chatroomReceivedMessagesTextArea.setLineWrap(true);
		chatroomReceivedMessagesTextArea.setEditable(false);
		scrollPane.setViewportView(chatroomReceivedMessagesTextArea);
		chatroomReceivedMessagesTextArea.setText("Welcome to LurkBot.\n");
		DefaultCaret caret = (DefaultCaret)chatroomReceivedMessagesTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JButton btnSendMessage = new JButton("Send");
		btnSendMessage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String message = chatroomSendMessageTextArea.getText();
				if(message != "Send a message...") {
					chatroomReceivedMessagesTextArea.append("LurkBot > " + message + "\n");
					twitchClient.getChat().sendMessage(CHANNEL, message);
					chatroomSendMessageTextArea.setText("Send a message...");
				}
			}
		});
		btnSendMessage.setBounds(73, 549, 125, 30);
		frmLurkbot.getContentPane().add(btnSendMessage);
	}
}