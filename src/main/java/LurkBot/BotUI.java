package LurkBot;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import javax.swing.DefaultListModel;
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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.JList;
import javax.swing.JPasswordField;

public class BotUI {

	private JFrame frmLurkbot;
	
	private CommandList commandList = new CommandList();
	private CensorList censorList = new CensorList();
	private TimedMessageList messageList = new TimedMessageList();
	
	private Bot bot;
	
	private static JTextArea chatroomReceivedMessagesTextArea;
	private static JTextArea whispersReceivedTextArea;
	private static JTextArea directMessagesReceivedTextArea;
	private static JTextArea modMessagesReceivedTextArea;
	DefaultListModel<String> modListModel;
	private JTextField textFieldSavedChannel;
	private JPasswordField passwordFieldOauth;
	private JTable commandsTable;
	private JTable censorsTable;
	private JTable censoredCensorsTable;
	
	private class ComboItem
	{
	    private String key;
	    private String value;

	    public ComboItem(String key, String value)
	    {
	        this.key = key;
	        this.value = value;
	    }

	    @Override
	    public String toString()
	    {
	        return key;
	    }

	    public String getKey()
	    {
	        return key;
	    }

	    public String getValue()
	    {
	        return value;
	    }
	}
	
	/**
	 * Create the application.
	 */
	public BotUI() {
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
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLurkbot = new JFrame();
		frmLurkbot.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) { // On window close
				if(bot != null) {
					bot.leaveChannel();
					bot.logChat();
				}
				
				commandList.saveToFile();
				
				e.getWindow().dispose();
			}
		});
		frmLurkbot.setTitle("LurkBot");
		frmLurkbot.setBounds(100, 100, 950, 700);
		frmLurkbot.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLurkbot.getContentPane().setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		frmLurkbot.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent event) {
	        	//bot.leaveChannel();
	        	System.exit(0);
	        }
	    });
		mnFile.add(mntmExit);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JLabel lblChatroom = new JLabel("Chatrooms");
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
					
					bot.sendMessageToChatroom(message);
					
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
		chatScrollPane.setBounds(0, 0, 258, 446);
		allChatroomPanel.add(chatScrollPane);
		
		chatroomReceivedMessagesTextArea = new JTextArea();
		chatScrollPane.setViewportView(chatroomReceivedMessagesTextArea);
		chatroomReceivedMessagesTextArea.setWrapStyleWord(true);
		chatroomReceivedMessagesTextArea.setLineWrap(true);
		chatroomReceivedMessagesTextArea.setEditable(false);
		chatroomReceivedMessagesTextArea.setText("= Welcome to LurkBot =");
		DefaultCaret caret = (DefaultCaret)chatroomReceivedMessagesTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JPanel PMChatroomPanel = new JPanel();
		chatroomTabbedPane.addTab("Whispers", null, PMChatroomPanel, null);
		PMChatroomPanel.setLayout(null);
		
		JScrollPane whispersScrollPane = new JScrollPane();
		whispersScrollPane.setBounds(0, 0, 258, 446);
		PMChatroomPanel.add(whispersScrollPane);
		
		whispersReceivedTextArea = new JTextArea();
		whispersReceivedTextArea.setWrapStyleWord(true);
		whispersReceivedTextArea.setLineWrap(true);
		whispersReceivedTextArea.setEditable(false);
		whispersScrollPane.setViewportView(whispersReceivedTextArea);
		
		JPanel DMChatroomPanel = new JPanel();
		chatroomTabbedPane.addTab("@Me", null, DMChatroomPanel, null);
		DMChatroomPanel.setLayout(null);
		
		JScrollPane dmsScrollPane = new JScrollPane();
		dmsScrollPane.setBounds(0, 0, 258, 446);
		DMChatroomPanel.add(dmsScrollPane);
		
		directMessagesReceivedTextArea = new JTextArea();
		directMessagesReceivedTextArea.setWrapStyleWord(true);
		directMessagesReceivedTextArea.setLineWrap(true);
		directMessagesReceivedTextArea.setEditable(false);
		dmsScrollPane.setViewportView(directMessagesReceivedTextArea);
		
		JPanel ModsChatroomPanel = new JPanel();
		chatroomTabbedPane.addTab("Mods", null, ModsChatroomPanel, null);
		ModsChatroomPanel.setLayout(null);
		
		JScrollPane modsScrollPane = new JScrollPane();
		modsScrollPane.setBounds(0, 0, 258, 446);
		ModsChatroomPanel.add(modsScrollPane);
		
		modMessagesReceivedTextArea = new JTextArea();
		modMessagesReceivedTextArea.setWrapStyleWord(true);
		modMessagesReceivedTextArea.setEditable(false);
		modMessagesReceivedTextArea.setLineWrap(true);
		modsScrollPane.setViewportView(modMessagesReceivedTextArea);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		
		tabbedPane.setBounds(270, 14, 650, 565);
		frmLurkbot.getContentPane().add(tabbedPane);
		
		// Commands Tab
		String[] columnNames = { "Command", "Response" };
		
		JPanel quickLinksPanel = new JPanel();
		tabbedPane.addTab("Quick Links", null, quickLinksPanel, null);
		quickLinksPanel.setLayout(null);
		
		JButton btnChatModeEmoteButton = new JButton("Emote-only");
		btnChatModeEmoteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bot.sendMessageToChatroom("/emoteonly");
			}
		});
		
		JLabel lblChatroomMode = new JLabel("Chatroom Mode");
		lblChatroomMode.setBounds(12, 12, 110, 16);
		quickLinksPanel.add(lblChatroomMode);
		
		JButton btnChatModeAllButton = new JButton("Normal");
		btnChatModeAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bot.sendMessageToChatroom("/slowoff");
				bot.sendMessageToChatroom("/followersoff");
				bot.sendMessageToChatroom("/subscribersoff");
				bot.sendMessageToChatroom("/Uniquechatoff");
				bot.sendMessageToChatroom("/emoteonlyoff");
			}
		});
		btnChatModeAllButton.setBounds(12, 30, 110, 26);
		quickLinksPanel.add(btnChatModeAllButton);
		
		JButton btnChatModeFollowerButton = new JButton("Follower-only");
		btnChatModeFollowerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bot.sendMessageToChatroom("/followers 30m");
			}
		});
		btnChatModeFollowerButton.setBounds(12, 60, 110, 26);
		quickLinksPanel.add(btnChatModeFollowerButton);
		
		JButton btnChatModeSubButton = new JButton("Sub-only");
		btnChatModeSubButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bot.sendMessageToChatroom("/subonly");
			}
		});
		btnChatModeSubButton.setBounds(12, 90, 110, 26);
		quickLinksPanel.add(btnChatModeSubButton);
		btnChatModeEmoteButton.setBounds(12, 119, 110, 26);
		quickLinksPanel.add(btnChatModeEmoteButton);
		
		JButton btnSlowModeButton = new JButton("Slow");
		btnSlowModeButton.setBounds(12, 148, 110, 26);
		quickLinksPanel.add(btnSlowModeButton);
		
		JButton btnTimeoutBanUserButton = new JButton("Timeout/Ban");
		btnTimeoutBanUserButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTextField userField = new JTextField(10);
				JTextField reasonField = new JTextField(20);
				JComboBox<ComboItem> timeoutDurationComboBox = new JComboBox<>();
				timeoutDurationComboBox.addItem(new ComboItem("Permanent", "-1"));
				timeoutDurationComboBox.addItem(new ComboItem("10 Seconds", "10"));
				timeoutDurationComboBox.addItem(new ComboItem("30 Seconds", "30"));
				timeoutDurationComboBox.addItem(new ComboItem("1 Minute", "60"));
				timeoutDurationComboBox.addItem(new ComboItem("5 Minutes", "300"));
				timeoutDurationComboBox.addItem(new ComboItem("10 Minutes", "600"));
				timeoutDurationComboBox.addItem(new ComboItem("30 Minutes", "1800"));
				timeoutDurationComboBox.addItem(new ComboItem("1 Hour", "3600"));
				timeoutDurationComboBox.addItem(new ComboItem("2 Hours", "7200"));
				timeoutDurationComboBox.addItem(new ComboItem("5 Hours", "18000"));
				timeoutDurationComboBox.addItem(new ComboItem("8 Hours", "28800"));
				timeoutDurationComboBox.addItem(new ComboItem("12 Hours", "43200"));
				timeoutDurationComboBox.addItem(new ComboItem("24 Hours", "86400"));
				JPanel timeoutPanel = new JPanel();
				timeoutPanel.add(new JLabel("User:"));
				timeoutPanel.add(userField);
				timeoutPanel.add(new JLabel("Duration:"));
				timeoutPanel.add(timeoutDurationComboBox);
				timeoutPanel.add(new JLabel("Reason:"));
				timeoutPanel.add(reasonField);
				
				Object timeoutDurItem = timeoutDurationComboBox.getSelectedItem();
				String timeoutDurValue = ((ComboItem)timeoutDurItem).getValue();
				
				int result = JOptionPane.showConfirmDialog(null,  timeoutPanel, "Timeout User", JOptionPane.OK_CANCEL_OPTION);
				if(result == JOptionPane.OK_OPTION && userField.getText() != null) {
					if(Integer.parseInt(timeoutDurValue) == -1)
						bot.sendMessageToChatroom("/timeout " + userField.getText() + " " + timeoutDurValue + " " + reasonField.getText());
					else
						bot.sendMessageToChatroom("/timeout " + userField.getText() + " " + timeoutDurValue + " " + reasonField.getText());
				}
			}
		});
		btnTimeoutBanUserButton.setBounds(12, 274, 110, 26);
		quickLinksPanel.add(btnTimeoutBanUserButton);
		
		JButton btnUnbanButton = new JButton("Unban");
		btnUnbanButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTextField userField = new JTextField(20);
				
				JPanel unbanPanel = new JPanel();
				unbanPanel.add(new JLabel("User:"));
				unbanPanel.add(userField);
				
				int result = JOptionPane.showConfirmDialog(null,  unbanPanel, "Unban User", JOptionPane.OK_CANCEL_OPTION);
				if(result == JOptionPane.OK_OPTION && userField.getText() != null) {
					bot.sendMessageToChatroom("/unban " + userField.getText());
				}
			}
		});
		btnUnbanButton.setBounds(12, 306, 110, 26);
		quickLinksPanel.add(btnUnbanButton);
		
		JButton btnHostChannelButton = new JButton("Host");
		btnHostChannelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnHostChannelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTextField userField = new JTextField(20);
				
				JPanel hostPanel = new JPanel();
				hostPanel.add(new JLabel("User:"));
				hostPanel.add(userField);
				
				int result = JOptionPane.showConfirmDialog(null,  hostPanel, "Host Channel", JOptionPane.OK_CANCEL_OPTION);
				if(result == JOptionPane.OK_OPTION && userField.getText() != null) {
					bot.sendMessageToChatroom("/host " + userField.getText());
				}
			}
		});
		btnHostChannelButton.setBounds(12, 408, 110, 26);
		quickLinksPanel.add(btnHostChannelButton);
		
		JButton btnRaidChannelButton = new JButton("Raid");
		btnRaidChannelButton.setLocation(12, 438);
		btnRaidChannelButton.setSize(110, 26);
		btnRaidChannelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTextField userField = new JTextField(20);
				
				JPanel raidPanel = new JPanel();
				raidPanel.add(new JLabel("User:"));
				raidPanel.add(userField);
				
				int result = JOptionPane.showConfirmDialog(null,  raidPanel, "Raid Channel", JOptionPane.OK_CANCEL_OPTION);
				if(result == JOptionPane.OK_OPTION && userField.getText() != null) {
					bot.sendMessageToChatroom("/raid " + userField.getText());
				}
			}
		});
		btnRaidChannelButton.setBounds(12, 440, 110, 26);
		quickLinksPanel.add(btnRaidChannelButton);
		
		JButton btnModUserButton = new JButton("Mod user");
		btnModUserButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTextField userField = new JTextField(20);
				
				JPanel modPanel = new JPanel();
				modPanel.add(new JLabel("User:"));
				modPanel.add(userField);
				
				int result = JOptionPane.showConfirmDialog(null,  modPanel, "Mod User", JOptionPane.OK_CANCEL_OPTION);
				if(result == JOptionPane.OK_OPTION && userField.getText() != null) {
					bot.sendMessageToChatroom("/mod " + userField.getText());
				}
			}
		});
		btnModUserButton.setBounds(12, 489, 110, 26);
		quickLinksPanel.add(btnModUserButton);
		
		JButton btnUnmodUserButton = new JButton("Unmod User");
		btnUnmodUserButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTextField userField = new JTextField(20);
				
				JPanel unModPanel = new JPanel();
				unModPanel.add(new JLabel("User:"));
				unModPanel.add(userField);
				
				int result = JOptionPane.showConfirmDialog(null,  unModPanel, "Un-mod User", JOptionPane.OK_CANCEL_OPTION);
				if(result == JOptionPane.OK_OPTION && userField.getText() != null) {
					bot.sendMessageToChatroom("/unmod " + userField.getText());
				}
			}
		});
		btnUnmodUserButton.setBounds(12, 520, 110, 26);
		quickLinksPanel.add(btnUnmodUserButton);
		
		JButton btnVipUserButton = new JButton("VIP user");
		btnVipUserButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTextField userField = new JTextField(20);
				
				JPanel vipPanel = new JPanel();
				vipPanel.add(new JLabel("User:"));
				vipPanel.add(userField);
				
				int result = JOptionPane.showConfirmDialog(null,  vipPanel, "VIP User", JOptionPane.OK_CANCEL_OPTION);
				if(result == JOptionPane.OK_OPTION && userField.getText() != null) {
					bot.sendMessageToChatroom("/vip " + userField.getText());
				}
			}
		});
		btnVipUserButton.setBounds(134, 489, 110, 26);
		quickLinksPanel.add(btnVipUserButton);
		
		JButton btnUnvipUser = new JButton("Un-VIP user");
		btnUnvipUser.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTextField userField = new JTextField(20);
				
				JPanel unVipPanel = new JPanel();
				unVipPanel.add(new JLabel("User:"));
				unVipPanel.add(userField);
				
				int result = JOptionPane.showConfirmDialog(null,  unVipPanel, "Un-VIP User", JOptionPane.OK_CANCEL_OPTION);
				if(result == JOptionPane.OK_OPTION && userField.getText() != null) {
					bot.sendMessageToChatroom("/unvip " + userField.getText());
				}
			}
		});
		btnUnvipUser.setBounds(134, 520, 110, 26);
		quickLinksPanel.add(btnUnvipUser);
		
		JLabel lblChannelMods = new JLabel("Mods");
		lblChannelMods.setBounds(170, 12, 55, 16);
		quickLinksPanel.add(lblChannelMods);
		modListModel = new DefaultListModel<>();
		JList<String> listMods = new JList<>(modListModel);
		listMods.setBounds(170, 33, 150, 150);
		quickLinksPanel.add(listMods);
		/*
		JCheckBox chckbxPreventLinksIn = new JCheckBox("Prevent links in chat");
		chckbxPreventLinksIn.setSelected(true);
		chckbxPreventLinksIn.setBounds(170, 218, 150, 24);
		quickLinksPanel.add(chckbxPreventLinksIn);
		*/
		JPanel generalSettingPanel = new JPanel();
		tabbedPane.addTab("General", null, generalSettingPanel, null);
		generalSettingPanel.setLayout(null);
		
		JLabel lblSavedChannel = new JLabel("Saved Channel");
		lblSavedChannel.setVerticalAlignment(SwingConstants.TOP);
		lblSavedChannel.setBounds(12, 12, 114, 20);
		generalSettingPanel.add(lblSavedChannel);
		
		textFieldSavedChannel = new JTextField();
		textFieldSavedChannel.setEditable(false);
		textFieldSavedChannel.setBounds(12, 32, 141, 20);
		generalSettingPanel.add(textFieldSavedChannel);
		textFieldSavedChannel.setColumns(10);
		
		JLabel lblOauth = new JLabel("Oauth");
		lblOauth.setBounds(165, 12, 55, 16);
		generalSettingPanel.add(lblOauth);
		
		passwordFieldOauth = new JPasswordField();
		passwordFieldOauth.setEditable(false);
		passwordFieldOauth.setBounds(165, 32, 250, 20);
		generalSettingPanel.add(passwordFieldOauth);
		/*
		JButton btnSave = new JButton("Save");
		btnSave.setBounds(427, 29, 98, 26);
		generalSettingPanel.add(btnSave);
		*/
		JPanel commandsPanel = new JPanel();
		tabbedPane.addTab("Commands", null, commandsPanel, null);
		commandsPanel.setLayout(null);
		
		JScrollPane commandsScrollPane = new JScrollPane();
		commandsScrollPane.setBounds(12, 67, 535, 479);
		commandsPanel.add(commandsScrollPane);
		commandsTable = new JTable(commandList.to2DStringArray(), columnNames);
		commandsScrollPane.setViewportView(commandsTable);
		commandsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JButton btnAddNewCommand = new JButton("Create");
		btnAddNewCommand.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTextField commandField = new JTextField(20);
				JTextField responseField = new JTextField(20);
				JTextField cooldownField = new JTextField(10);
				JCheckBox modCheckBox = new JCheckBox("Moderators");
				JCheckBox vipCheckBox = new JCheckBox("VIPs");
				JCheckBox subCheckBox = new JCheckBox("Subscribers");
				JCheckBox folCheckBox = new JCheckBox("Followers");
				JPanel newCommandPanel = new JPanel();
				newCommandPanel.add(new JLabel("Command:"));
				newCommandPanel.add(commandField);
				newCommandPanel.add(new JLabel("Response:"));
				newCommandPanel.add(responseField);
				newCommandPanel.add(new JLabel("Who can use this command:"));
				newCommandPanel.add(modCheckBox);
				newCommandPanel.add(vipCheckBox);
				newCommandPanel.add(subCheckBox);
				newCommandPanel.add(folCheckBox);
				
				int result = JOptionPane.showConfirmDialog(null,  newCommandPanel, "New Command", JOptionPane.OK_CANCEL_OPTION);
				if(result == JOptionPane.OK_OPTION) {
					String allowedUsers = "";
					if(modCheckBox.isSelected())
						allowedUsers += "mod";
					if(vipCheckBox.isSelected())
						allowedUsers += "vip";
					if(subCheckBox.isSelected())
						allowedUsers += "sub";
					if(folCheckBox.isSelected())
						allowedUsers += "fol";
					
					commandList.addCommand(commandField.getText(), responseField.getText(), Integer.parseInt(cooldownField.getText()), allowedUsers);
				}
			}
		});
		btnAddNewCommand.setBounds(12, 12, 100, 43);
		commandsPanel.add(btnAddNewCommand);
		
		JButton btnEditCommand = new JButton("Edit");
		btnEditCommand.setBounds(122, 12, 100, 43);
		commandsPanel.add(btnEditCommand);
		
		JButton btnDeleteCommand = new JButton("Delete");
		btnDeleteCommand.setBounds(232, 12, 100, 43);
		commandsPanel.add(btnDeleteCommand);
		
		JButton btnExportCommands = new JButton("Export Commands");
		btnExportCommands.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(commandList.saveToFile())
					JOptionPane.showMessageDialog(null, "Save Completed");
			}
		});
		btnExportCommands.setBounds(373, 12, 150, 43);
		commandsPanel.add(btnExportCommands);
		
		// Censorship Tab
		JPanel censorshipPanel = new JPanel();
		tabbedPane.addTab("Censors", null, censorshipPanel, null);
		censorshipPanel.setLayout(null);
		
		//String[] censorsColumnNames = { "Word", "Null"};
		
		JScrollPane censorshipScrollPane = new JScrollPane();
		censorshipScrollPane.setBounds(10, 40, 535, 455);
		censorshipPanel.add(censorshipScrollPane);
		
		//censorsTable = new JTable(censorList.toStringArray(), censorsColumnNames);
		censorsTable = new JTable(60, 2);
		censoredCensorsTable = new JTable(60, 2);
		for(int i = 0; i < 25; i++) {
			Object censorWord = censorList.toStringArray()[i];
			censorsTable.setValueAt(censorWord, i, 0);
			censorsTable.setValueAt("5 minutes", i, 1);
			
			Object censoredWord = censorWord.toString().charAt(0);
			for(int j = 0; j < censorWord.toString().length(); j++) {
				censoredWord += "*";
			}
			censoredCensorsTable.setValueAt(censoredWord, i, 0);
			censoredCensorsTable.setValueAt("5 minutes", i, 1);
		}
		censorshipScrollPane.setViewportView(censoredCensorsTable);
		censorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		censorsTable.setCellSelectionEnabled(true);
		
		JToggleButton tglbtnCensorsToggleButton = new JToggleButton("Show/Hide");
		tglbtnCensorsToggleButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(tglbtnCensorsToggleButton.isSelected()) {
					censorshipScrollPane.setViewportView(censorsTable);
				}
				else {
					censorshipScrollPane.setViewportView(censoredCensorsTable);
				}
			}
		});
		tglbtnCensorsToggleButton.setBounds(10, 12, 110, 26);
		censorshipPanel.add(tglbtnCensorsToggleButton);
		
		JButton btnAddNewCommand_1 = new JButton("Create");
		btnAddNewCommand_1.setBounds(10, 503, 100, 43);
		censorshipPanel.add(btnAddNewCommand_1);
		
		JButton btnEditCommand_1 = new JButton("Edit");
		btnEditCommand_1.setBounds(120, 503, 100, 43);
		censorshipPanel.add(btnEditCommand_1);
		
		JButton btnDeleteCommand_1 = new JButton("Delete");
		btnDeleteCommand_1.setBounds(230, 503, 100, 43);
		censorshipPanel.add(btnDeleteCommand_1);
		
		JButton btnExportCensors = new JButton("Export Censors");
		btnExportCensors.setBounds(371, 503, 150, 43);
		censorshipPanel.add(btnExportCensors);
		
		JPanel messagesPanel = new JPanel();
		//tabbedPane.addTab("Messages", null, messagesPanel, null);
	}
	
	public void connectBot(Bot bot) {
		this.bot = bot;
	}
	
	public void chatroomAllAppendMessage(String message) {
		chatroomReceivedMessagesTextArea.append("\n" + message);
	}
	
	public void chatroomWhispersAppendMessage(String whisperMessage) {
		whispersReceivedTextArea.append("\n" + whisperMessage);
	}
	
	public void chatroomDirectMessagesAppendMessage(String directMessage) {
		directMessagesReceivedTextArea.append("\n" + directMessage);
	}
	
	public void chatroomModMessagesAppendMessage(String modMessage) {
		modMessagesReceivedTextArea.append("\n" + modMessage);
	}
	
	public void setChannelName(String channelName) {
		textFieldSavedChannel.setText(channelName);
	}
	
	public void setChannelModsList(String[] channelMods) {
		for(String mod : channelMods) {
			modListModel.addElement(mod);
		}
	}
	
	public void setChannelOauth(String channelOauth) {
		passwordFieldOauth.setText(channelOauth);
	}
}