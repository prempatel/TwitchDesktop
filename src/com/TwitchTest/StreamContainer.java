package com.TwitchTest;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Root class for stream storage
 * 
 * @author Prem Patel
 * @since 2015-07-10
 */
public class StreamContainer {
	
	private Stream stream;
	private static List<String> streamList = new ArrayList<String>();
	private static Map<String, Boolean> streamStatus = new ConcurrentHashMap<String, Boolean>();
	
	public Stream getStream() {
		return stream;
	}
	
	public void addStream(String stream){
		streamList.add(stream);
	}
	
	public List<String> getStreamsList(){
		return streamList;
	}
	
	public Map<String, Boolean> getStreamStatusMap(){
		return streamStatus;
	}
	
	/**
	 * Retrieves stream information and will display in gui
	 * 
	 * @return String of information for stream
	 */
	public String isOnlineString(){		
		return (stream!=null) ? "<html>" + "Playing " + stream.getGame() 
								+ " in " + stream.getVideo_height() + "P" 
								+ "<br>" + stream.getChannel().getStatus()
								+ "<br>Watch at " + stream.getChannel().getUrl() + "</html>"
								:"Stream is offline";
	}
	
	public boolean isOnline(){
		return stream != null;
	}

	/**
	 * Retrieves a preview image to display in gui, if stream is online
	 * 
	 * @return Preview ImageIcon
	 */
	public ImageIcon getPreview(){
		ImageIcon previewIcon = null;	
		if(isOnline()){
			BufferedImage previewImage = null;
			
			try {
				URL previewUrl = new URL(stream.getPreviewTemplate());
				previewImage = ImageIO.read(previewUrl);
			} catch (IOException ex) { ex.printStackTrace(); }
			
			previewIcon = new ImageIcon(previewImage);
		}
		return previewIcon;
	}
}
