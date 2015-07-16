package com.Prem.Twitch;

import com.sun.jna.NativeLibrary;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.DefaultAdaptiveRuntimeFullScreenStrategy;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Twitch Desktop
 * 
 * @author Prem Patel
 * @version 0.1
 * @since 2015-07-10
 *
 */
public class TwitchGui extends JFrame{
	private static final long serialVersionUID = -804313717028413743L;
	private static final String twitchUrl = "http://twitch.tv/";

    private static EmbeddedMediaPlayerComponent mediaPlayer;

	private JLabel textLabel, statusLabel;
	private JPanel mainPanel, infoPanel, statusPanel, enterPanel, listPanel, buttonPanel, mediaPlayerPanel;
	private JTextField channelText;
	private JButton addButton, removeButton;
	private JList<String> jStreams;
	private DefaultListModel<String> streamModel;
	private Preferences streamPrefs;
	private StreamContainer streamInfo = new StreamContainer();
	private TwitchData streamData = new TwitchData();
	
	/**
	 * Constructor to initialize components, load list, start thread executor
	 */
    private TwitchGui(){
		this.setTitle("TwitchChecker");
		this.setSize(640, 480);
		this.setMinimumSize(new Dimension(640, 480));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setMaximumSize(screenSize);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setIconImage(new ImageIcon("./lib/twitch.png").getImage());
		this.setLayout(new BorderLayout(5, 5));
		
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "./lib");
//		System.setProperty("jna.library.path", "./lib/");
		
		streamPrefs = Preferences.userNodeForPackage(this.getClass());
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we) {
				mediaPlayer.release();
				System.exit(0);
			}
		});

		textLabel = new JLabel("Enter a TwitchTV stream", SwingConstants.CENTER);
		textLabel.setAlignmentX(CENTER_ALIGNMENT);
		
		statusLabel = new JLabel("Stream info will display here.", SwingConstants.CENTER);
		
		channelText = new JTextField(20);
		channelText.setHorizontalAlignment(JTextField.CENTER);
		channelText.requestFocusInWindow();
		channelText.setToolTipText("Enter a stream to enable the buttons");
		channelText.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {}
			public void insertUpdate (DocumentEvent e) { checkText(); }
			public void removeUpdate (DocumentEvent e) { checkText(); }
		});
		
		channelText.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent ke) {
				if(ke.getKeyCode() == KeyEvent.VK_ENTER){ addButton.doClick(); }
			}
		});

		addButton = new JButton("Add Stream");
		addButton.setEnabled(false);
		addButton.setAlignmentX(CENTER_ALIGNMENT);
		addButton.addActionListener(actionEvent -> {
            addStream(channelText.getText().trim());
            channelText.setText(null);
        });

		removeButton = new JButton("Remove Stream");
		removeButton.setEnabled(false);
		removeButton.setAlignmentX(CENTER_ALIGNMENT);
		removeButton.addActionListener(actionEvent -> removeStream());
		
		jStreams = new JList<String>();
		jStreams.setCellRenderer(new StreamListCellRenderer());
		jStreams.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jStreams.setVisibleRowCount(10);
		jStreams.setFixedCellWidth(115);
		jStreams.addListSelectionListener(listSel -> {
            if (!listSel.getValueIsAdjusting()) {
                if (!jStreams.isSelectionEmpty()) {
                    setStream();
                    removeButton.setEnabled(true);
                }
                else {
                    removeButton.setEnabled(false);
                    statusLabel.setIcon(null);
                    statusLabel.setText("Stream info will display here.");
                    pack();
                }
            }
        });
		
		jStreams.addMouseListener(new MouseAdapter(){
			//Opens dialog to watch stream in mediaplayer
			public void mouseClicked(MouseEvent me) {
				if(me.getClickCount() == 2){
					if (!jStreams.isSelectionEmpty()) {
						checkAndPlayStream();
					}
				}
			}
			
			//Right click menu on list items
			public void mouseReleased(MouseEvent me) {
				if (me.isPopupTrigger()) {
					JPopupMenu popMenu = new JPopupMenu();
					JMenuItem openStream = new JMenuItem("Open Stream");
					openStream.addActionListener(actionEvent -> {
                        try {
                            if (!jStreams.isSelectionEmpty()) {
                                openStream(new URI(twitchUrl
                                            + jStreams.getSelectedValue()));
                            }
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    });
					JMenuItem openStreamPopout = new JMenuItem("Open Popout Stream");
					openStreamPopout.addActionListener(actionEvent -> {
                        try {
                            if (!jStreams.isSelectionEmpty()) {
                                openStream(new URI(twitchUrl
                                            + jStreams.getSelectedValue()
                                            + "/popout"));
                            }
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    });
					JMenuItem removeMenu = new JMenuItem("Remove Stream");
					removeMenu.addActionListener(actionEvent -> removeStream());
					popMenu.add(openStream);
					popMenu.add(openStreamPopout);
					popMenu.add(removeMenu);
					jStreams.setSelectedIndex(jStreams.locationToIndex(me.getPoint()));
					popMenu.show(jStreams, me.getX(), me.getY());
				}
			}
		});	
		
		loadStreamList();
		startStreamUpdateWorker();
		
		mediaPlayer = new EmbeddedMediaPlayerComponent();
		mediaPlayer.getMediaPlayer().setFullScreenStrategy(new DefaultAdaptiveRuntimeFullScreenStrategy(this));
		
		buttonPanel = new JPanel();
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		
		enterPanel = new JPanel();
		enterPanel.setLayout(new BoxLayout(enterPanel, BoxLayout.Y_AXIS));
		enterPanel.add(textLabel);
		enterPanel.add(channelText);
		enterPanel.add(buttonPanel);
		
		statusPanel = new JPanel(new BorderLayout());
		statusPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Stream Info"));
		statusPanel.add(statusLabel, BorderLayout.CENTER);
		
		listPanel = new JPanel(new BorderLayout());
		JScrollPane listScroll = new JScrollPane(jStreams, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		listPanel.add(listScroll, BorderLayout.CENTER);
		
		infoPanel = new JPanel(new BorderLayout(2, 2));
		infoPanel.add(enterPanel, BorderLayout.NORTH);
		infoPanel.add(statusPanel, BorderLayout.CENTER);
		
		mediaPlayerPanel = new JPanel(new BorderLayout());
		mediaPlayerPanel.add(mediaPlayer, BorderLayout.CENTER);

		mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		mainPanel.setLayout(new BorderLayout(5, 5));
		mainPanel.add(infoPanel, BorderLayout.NORTH);
		mainPanel.add(mediaPlayerPanel, BorderLayout.CENTER);

		this.add(mainPanel, BorderLayout.CENTER);
		this.add(listPanel, BorderLayout.WEST);
		
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

    public static TwitchGui setupGui() {
        return new TwitchGui();
    }

    /**
	 * Add stream to Preferences then update stream model
	 * 
	 * @param streamName Twitch stream/channel name
	 */
    private void addStream(String streamName){
		List<String> streamList = streamInfo.getStreamsList();
		if(streamData.streamExists(streamName)){
			if (streamList.contains(streamName.toLowerCase()) || streamList.contains(streamName.toUpperCase())) {
				JOptionPane.showMessageDialog(null, "Stream already in the list", null, JOptionPane.INFORMATION_MESSAGE);
			}
			else{
				streamPrefs.put(streamName, streamName);
				loadStreamList();
			}
		}
		else{
			JOptionPane.showMessageDialog(null, "Stream does not exist", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Remove stream from Preferences, then load/update stream model
	 */
    private void removeStream(){
		String selectedValue = jStreams.getSelectedValue();		
		if (selectedValue != null) {
			streamPrefs.remove(selectedValue);
			
			if(streamInfo.getStreamStatusMap().containsKey(selectedValue)){
				streamInfo.getStreamStatusMap().remove(selectedValue);
			}	
			loadStreamList();
		}
	}
	
	/**
	 * Retrieves streams from registry, adds to the list model, and updates stream statuses
	 */
    private void loadStreamList(){
		streamModel = new DefaultListModel<String>();
		List<String> streamList = streamInfo.getStreamsList();
		String selectedItem = jStreams.getSelectedValue();
		
		new SwingWorker<Void, String>(){	
			protected Void doInBackground() throws Exception {			
				String[] streamKeys = null;
				try {
					streamKeys = streamPrefs.keys();
				} catch (BackingStoreException e) {
					e.printStackTrace();
				}
				
				streamList.clear();
				
				//add each stream to the streamlist by getting value from Preferences keys
                assert streamKeys != null;
                for(String streamKeyValue : streamKeys){
					String streamValue = streamPrefs.get(streamKeyValue, "Unavailable");
					streamList.add(streamValue);
				}

                /* for(String stream : streamList){ publish(stream); } */
                streamList.forEach(this::publish);
				return null;
			}
			
			protected void process(List<String> publishedStreamList){
                publishedStreamList.forEach(streamModel::addElement);
			}
			
			protected void done(){
				//Run executor after loading list to check if streams are online
				ExecutorService streamDataWork = Executors.newSingleThreadExecutor();
				streamDataWork.submit(updateStreamStatus());
				streamDataWork.shutdown();
				
				jStreams.setModel(streamModel);
                jStreams.setSelectedValue(selectedItem, false);
                pack();
			}
		}.execute();
	}
	
	/**
	 * A Runnable worker to update stream statuses and list model to reflect any new changes
	 * 
	 * @return Runnable object to execute updating of stream status
	 */
    private Runnable updateStreamStatus(){
		return new Runnable() {
			public void run() {
				streamData.getStreamStatus(streamInfo.getStreamsList(),
									 streamInfo.getStreamStatusMap());
				updateStreamModel();
			}
		};
	}

	/**
	 * Reload streams to list model and update JList
	 */
    private void updateStreamModel(){
		streamModel = new DefaultListModel<String>();

        List<String> streamList = streamInfo.getStreamsList();
        streamList.forEach(streamModel::addElement);
		
		jStreams.setModel(streamModel);
		System.out.println("Updated Model");
	}
	
	/**
	 * Retrieve summary of stream info, if stream is online
	 * 
	 * @param streamName Twitch stream/channel name
	 */
    private String getStreamInfo(String streamName){
		streamInfo = streamData.getJsonDataFromApi(streamName);
		return streamInfo.isOnlineString();
	}
	
	/**
	 * Retrieves and sets stream information to display in gui
	 */
	private void setStream(){		
		new SwingWorker<Object[], Void>(){
			protected Object[] doInBackground() throws Exception {
				
				statusLabel.setText("Getting stream information");
				statusLabel.setIcon(null);
				pack();
				
				Object[] streamBox = new Object[2];
				streamBox[0] = getStreamInfo(jStreams.getSelectedValue());
				streamBox[1] = streamInfo.getPreview();
				return streamBox;
			}
			
			protected void done(){
				try {
					Object[] infoBox = get();
					statusLabel.setText((String) infoBox[0]);
					statusLabel.setIcon((ImageIcon) infoBox[1]);
					
				} catch (Exception e) {
					statusLabel.setText("Failed to get stream data");
				}
				pack();
			}
		}.execute();
	}
	
	/**
	 * Starts a scheduled executor to periodically update stream information
	 */
    private void startStreamUpdateWorker(){
		ScheduledExecutorService streamStatusExecutor = Executors.newSingleThreadScheduledExecutor();	
		streamStatusExecutor.scheduleAtFixedRate(updateStreamStatus(), 30, 30, TimeUnit.SECONDS);		
	}
	
	/**
	 * Opens stream in browser if desktop is supported
	 * 
	 * @param urlStream URL to Twitch stream
	 */
    private void openStream(URI urlStream){
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if(desktop != null){
			try {
				desktop.browse(urlStream);
			} catch (IOException e) { e.printStackTrace(); }
		}
	}

	/**
	 * Checks if stream is online, then displays a dialog to select stream quality or error message.
	 */
    private void checkAndPlayStream(){
		Map<String, Boolean> streamStatus = streamInfo.getStreamStatusMap();
		//Checks if current stream is online
		if(streamStatus.get(jStreams.getSelectedValue())){
			Object qualities[] = {"Best", "High", "Medium", "Low", "Worst"};
			
			//Display input dialog to select a stream quality
			String choice = (String) JOptionPane.showInputDialog(null, "Select a quality", null, 
									 							 JOptionPane.QUESTION_MESSAGE, null, 
									 							 qualities, qualities[0]);
			//Retrieve stream url based on choices
			String streamUrl = streamData.getStreamLinksFromPlaylist(choice, jStreams.getSelectedValue());
			mediaPlayer.getMediaPlayer().startMedia(streamUrl);
		}
		else{
			JOptionPane.showMessageDialog(null, jStreams.getSelectedValue() + " is offline.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void checkText(){
		if(channelText.getText().isEmpty()) { addButton.setEnabled(false); }
		else { addButton.setEnabled(true); }
	}
	
	public static void main(String[] args){
		SwingUtilities.invokeLater(TwitchGui::setupGui);
	}
}