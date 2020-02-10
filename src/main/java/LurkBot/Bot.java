package LurkBot;

import java.awt.EventQueue;
//import java.awt.EventQueue;
import java.awt.event.ActionEvent;
//import java.util.Iterator;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.FlowLayout;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JPanel;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

public class Bot {

	private JFrame frmLurkbot;
	private TwitchClient twitchClient;
	private OAuth2Credential credential;
	private final String CHANNEL = "Vader";
	private final String OAUTH = "qmbevizwjp4vi8krqrfj5rp0jmesst";
	private CommandList commandList = new CommandList();
	private CensorList censorList = new CensorList();
	private String chatLogString = LocalDateTime.now() + "\n";
	private Date datetime = new Date();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
	private File chatLogFile = new File(dateFormat.format(datetime) + ".txt");
	
	private static JTextArea chatroomReceivedMessagesTextArea;
	private JTable commandsTable;
	
	/**
	 * Create the application.
	 */
	public Bot() {
		initialize();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frmLurkbot.setVisible(true);
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
		chatroomReceivedMessagesTextArea.append("\n= Entered chatroom =");
		
		// Read chat room of currently connected channel
		twitchClient.getChat().getEventManager().onEvent(ChannelMessageEvent.class).subscribe(event -> {
			//System.out.println(/* "[" + event.getChannel().getName() + "] " + */event.getUser().getName() + " > " + event.getMessage());
			chatroomReceivedMessagesTextArea.append("\n" + event.getUser().getName() + " > " + event.getMessage());
			chatLogString += event.getUser().getName() + ":" + event.getMessage() + "\n";
			
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
			
			// TODO compare words in message to list of censored words
			if(event.getMessage().contains("")) {
				event.timeout(event.getUser().getName(), Duration.ofHours(24), "Using banned word");
			}
			
			if(event.getMessage().charAt(0) == '!') {
				String commandString = event.getMessage().substring(1, event.getMessage().length());
				if(commandString == "deletecommand") {
					commandList.deleteCommand(""); // get command to be deleted
				}
				String response = commandList.getResponseString(commandString);
				
				twitchClient.getChat().sendMessage(CHANNEL, "@" + event.getUser().getName() + response);
			}
		});
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLurkbot = new JFrame();
		frmLurkbot.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				BufferedWriter out;
				try {
					out = new BufferedWriter(new FileWriter(chatLogFile, true));
					out.write(chatLogString);
					out.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				commandList.saveToFile();
				
				e.getWindow().dispose();
			}
		});
		frmLurkbot.setTitle("LurkBot");
		frmLurkbot.setBounds(100, 100, 922, 700);
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
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JLabel lblChatroom = new JLabel(CHANNEL + "'s Chatroom");
		lblChatroom.setBounds(5, 6, 250, 14);
		frmLurkbot.getContentPane().add(lblChatroom);
		
		JTextArea chatroomSendMessageTextArea = new JTextArea();
		chatroomSendMessageTextArea.setBounds(5, 501, 250, 70);
		frmLurkbot.getContentPane().add(chatroomSendMessageTextArea);
		chatroomSendMessageTextArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chatroomSendMessageTextArea.setText("");
			}
		});
		chatroomSendMessageTextArea.setText("Send a message...");
		
		JButton btnSendMessage = new JButton("Send");
		btnSendMessage.setBounds(68, 587, 125, 30);
		frmLurkbot.getContentPane().add(btnSendMessage);
		btnSendMessage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String message = chatroomSendMessageTextArea.getText();
				if(message != "Send a message...") {
					chatroomReceivedMessagesTextArea.append("\nLurkBot > " + message);
					twitchClient.getChat().sendMessage(CHANNEL, message);
					chatroomSendMessageTextArea.setText("Send a message...");
				}
			}
		});
		
		JTabbedPane chatroomTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		chatroomTabbedPane.setBounds(0, 20, 260, 478);
		frmLurkbot.getContentPane().add(chatroomTabbedPane);
		
		JPanel allChatroomPanel = new JPanel();
		chatroomTabbedPane.addTab("All", null, allChatroomPanel, null);
		allChatroomPanel.setLayout(null);
		
		JScrollPane chatScrollPane = new JScrollPane();
		chatScrollPane.setBounds(0, 0, 250, 446);
		allChatroomPanel.add(chatScrollPane);
		
		chatroomReceivedMessagesTextArea = new JTextArea();
		chatScrollPane.setViewportView(chatroomReceivedMessagesTextArea);
		chatroomReceivedMessagesTextArea.setWrapStyleWord(true);
		chatroomReceivedMessagesTextArea.setLineWrap(true);
		chatroomReceivedMessagesTextArea.setEditable(false);
		chatroomReceivedMessagesTextArea.setText("= Welcome to LurkBot. =");
		
		JPanel PMChatroomPanel = new JPanel();
		chatroomTabbedPane.addTab("Whispers", null, PMChatroomPanel, null);
		PMChatroomPanel.setLayout(null);
		
		JScrollPane whispersScrollPane = new JScrollPane();
		whispersScrollPane.setBounds(0, 0, 250, 446);
		PMChatroomPanel.add(whispersScrollPane);
		
		JTextArea whispersReceivedTextArea = new JTextArea();
		whispersScrollPane.setViewportView(whispersReceivedTextArea);
		DefaultCaret caret = (DefaultCaret)chatroomReceivedMessagesTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		
		tabbedPane.setBounds(270, 14, 629, 565);
		frmLurkbot.getContentPane().add(tabbedPane);
		
		// Commands Panel
		String[] columnNames = { "Command", "Response" };
		
		JPanel commandsPanel = new JPanel();
		tabbedPane.addTab("Commands", null, commandsPanel, null);
		commandsPanel.setLayout(null);
		
		JScrollPane commandsScrollPane = new JScrollPane();
		commandsScrollPane.setBounds(10, 11, 511, 484);
		commandsPanel.add(commandsScrollPane);
		commandsTable = new JTable(commandList.to2DStringArray(), columnNames);
		commandsScrollPane.setViewportView(commandsTable);
		commandsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		commandsTable.setCellSelectionEnabled(true);
		
		JButton btnAddNewCommand = new JButton("Create");
		btnAddNewCommand.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTextField commandField = new JTextField(20);
				JTextField responseField = new JTextField(20);
				JPanel newCommandPanel = new JPanel();
				newCommandPanel.add(new JLabel("Command:"));
				newCommandPanel.add(commandField);
				newCommandPanel.add(new JLabel("Response:"));
				newCommandPanel.add(responseField);
				
				int result = JOptionPane.showConfirmDialog(null,  newCommandPanel, "New Command", JOptionPane.OK_CANCEL_OPTION);
				if(result == JOptionPane.OK_OPTION) {
					commandList.addCommand(commandField.getText(), responseField.getText());
				}
			}
		});
		btnAddNewCommand.setBounds(10, 506, 100, 43);
		commandsPanel.add(btnAddNewCommand);
		
		JButton btnEditCommand = new JButton("Edit");
		btnEditCommand.setBounds(120, 506, 100, 43);
		commandsPanel.add(btnEditCommand);
		
		JButton btnDeleteCommand = new JButton("Delete");
		btnDeleteCommand.setBounds(230, 506, 100, 43);
		commandsPanel.add(btnDeleteCommand);
		
		JButton btnExportCommands = new JButton("Export Commands");
		btnExportCommands.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(commandList.saveToFile())
					JOptionPane.showMessageDialog(null, "Save Completed");
			}
		});
		btnExportCommands.setBounds(371, 506, 150, 43);
		commandsPanel.add(btnExportCommands);
		
		JPanel censorshipPanel = new JPanel();
		tabbedPane.addTab("Censors", null, censorshipPanel, null);
		censorshipPanel.setLayout(null);
		
		JScrollPane censorshipScrollPane = new JScrollPane();
		censorshipScrollPane.setBounds(10, 11, 383, 435);
		censorshipPanel.add(censorshipScrollPane);
	}
}