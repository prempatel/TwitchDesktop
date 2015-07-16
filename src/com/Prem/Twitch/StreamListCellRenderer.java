package com.Prem.Twitch;

import java.awt.Color;
import java.awt.Component;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 * Class for custom JList cell rendering
 * 
 * @author Prem
 * @since 2015-07-10
 *
 */
public class StreamListCellRenderer extends JLabel implements ListCellRenderer<Object> {

	private static final long serialVersionUID = 4436136733049953824L;
	
	private final Color PURPLE = new Color(100, 65, 165);
	private final ImageIcon offlineSelect = new ImageIcon("./lib/offlineselection.png");
	private final ImageIcon onlineSelect = new ImageIcon("./lib/onlineselection.png");
	private final Border emptyBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
	
	private StreamContainer streamInfo;
	private List<String> streamList;
	private Map<String, Boolean> streamStatus;
	
	public StreamListCellRenderer(){
		setOpaque(true);
		setHorizontalTextPosition(SwingConstants.LEADING);
		setBorder(emptyBorder);
		
		streamInfo = new StreamContainer();
		streamList = streamInfo.getStreamsList();
		streamStatus = streamInfo.getStreamStatusMap();
	}

	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		setText(value.toString());

		for(String stream : streamList){
			if(stream.equals(value.toString())){
				//checks if stream is online and will only render if list & map are equal size (or causes error)
				if ((streamStatus.size() == streamList.size()) && streamInfo.getStreamStatusMap().get(stream)) {
					if (isSelected) {
						setIcon(onlineSelect);
						setBackground(PURPLE);
						setForeground(Color.WHITE);
					} else {
						setIcon(null);
						setBackground(PURPLE);
						setForeground(Color.WHITE);
					}
				}
				else{
					if(isSelected){
						setIcon(offlineSelect);
						setBackground(Color.WHITE);
						setForeground(PURPLE);
					}
					else{
						setIcon(null);
						setBackground(Color.WHITE);
						setForeground(PURPLE);
					}
				}
			}
		}
		return this;
	}
}