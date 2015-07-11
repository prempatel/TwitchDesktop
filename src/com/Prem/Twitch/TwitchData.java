package com.Prem.Twitch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

/**
 * The class which will do the most work and retrieve data from Twitch API.
 * 
 * @author Prem Patel
 * @since 2015-07-10
 *
 */
public class TwitchData {
	
	/**
	 * Retrieves JSON data for stream
	 * @param streamName Twitch stream/channel name
	 * @return	(StreamContainer) POJO to store JSON data 
	 */
	public StreamContainer getJsonDataFromApi(String streamName){
		StreamContainer streamData = null;
		if (!streamName.isEmpty()) {
			Gson gson = new Gson();
			StringBuffer responseData = openHttpAndGetData(streamName);
			if (responseData != null) {
				streamData = gson.fromJson(responseData.toString(), StreamContainer.class);
			}
		}
		return streamData;
	}
	
	/**
	 * Gets JSON data from URL and stores it in AccessToken class.
	 * @param channelName
	 * @return AccessToken object
	 */
	public AccessToken getAndStoreAccessToken(String channelName){
		String tokenUrl = ("https://api.twitch.tv/api/channels/" + channelName + "/access_token");
		Gson gson = new Gson();		
		StringBuffer tokenData = openAndGrabDataFromUrl(tokenUrl);

		AccessToken accessToken = gson.fromJson(tokenData.toString(), AccessToken.class);	
		return accessToken;
	}
	
	/**
	 * Gets a playlist file containing stream data.
	 * @param channelName Twitch stream/channel name
	 * @return String format of m3u8 file
	 */
	public String requestTwitchStreamPlaylist(String channelName){	
		AccessToken accessToken = getAndStoreAccessToken(channelName);
		String encodedToken = accessToken.getEncodedToken();
		String sigFromAccessToken = accessToken.getTokenSig();
		
		String requestUrlString = "http://usher.twitch.tv/api/channel/hls/" 
							+ channelName + ".m3u8?player=twitchweb&&token=" + encodedToken + "&sig=" + sigFromAccessToken 
							+ "&allow_source=false&type=any";
		
		StringBuffer linkData = openAndGrabDataFromUrl(requestUrlString);
		return linkData.toString();
	}
	
	/**
	 * Opens connection and inputstream to URL and returns data in a StringBuffer
	 * @param URL
	 * @return StringBuffer containing data from URL
	 */
	public StringBuffer openAndGrabDataFromUrl(String url){
		String tempData;
		StringBuffer data = null;
		try {
			data = new StringBuffer();
			URL dataUrl = new URL(url);
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(dataUrl.openStream()));
			
			while((tempData = buffReader.readLine()) != null){
				data.append(tempData);
			}
			if(buffReader != null){
				buffReader.close();
			}
		}catch (IOException e) {e.printStackTrace();}
		
		return data;
	}
	
	/**
	 * Opens connection to Http URL, only if response is 200/OK, and gets JSON data
	 * @param apiUrlForStream 
	 * @return StringBuffer containing JSON from API
	 */
	public StringBuffer openHttpAndGetData(String streamName){
		StringBuffer responseData = null;
		try {
			URL urlApi = new URL("https://api.twitch.tv/kraken/streams/" + streamName);
			HttpURLConnection connUrl = (HttpURLConnection) urlApi.openConnection();
			if (connUrl.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connUrl.getInputStream()));
				responseData = new StringBuffer();
				String inputData;
				while ((inputData = reader.readLine()) != null) {
					responseData.append(inputData);
				}
				if (reader != null) {
					reader.close();
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return responseData;
	}
	
	/**
	 * Retrieves direct stream links from playlist file.
	 * @param playlist (String from requestTwitchStreamPlaylist() would be common)
	 * @return array of Twitch Stream links by quality
	 */
	public static String[] getStreamLinksFromPlaylist(String playlist){
		String[] streamsByQuality = new String[5];
		
		//Source Quality Link
		streamsByQuality[0] = playlist.substring(playlist.indexOf("http://"), 
												 playlist.indexOf("#EXT", playlist.indexOf("http://")));
		//High Quality Link
		streamsByQuality[1] = playlist.substring(playlist.indexOf("http://", playlist.indexOf("VIDEO=\"high\"")), 
												 playlist.indexOf("#EXT", 	 playlist.indexOf("VIDEO=\"high\"")));
		//Medium Quality Link
		streamsByQuality[2] = playlist.substring(playlist.indexOf("http://", playlist.indexOf("VIDEO=\"medium\"")), 
												 playlist.indexOf("#EXT", 	 playlist.indexOf("VIDEO=\"medium\"")));
		//Low Quality Link
		streamsByQuality[3] = playlist.substring(playlist.indexOf("http://", playlist.indexOf("VIDEO=\"low\"")), 
												 playlist.indexOf("#EXT", 	 playlist.indexOf("VIDEO=\"low\"")));
		//Mobile Quality Link
		streamsByQuality[4] = playlist.substring(playlist.lastIndexOf("http://"));

		return streamsByQuality;
	}
	
	/**
	 * Returns specific stream based on paramaters(stream quality).
	 * @param quality (Best, High, Medium, Low, Worst)
	 * @param channel (Twitch stream/channel name)
	 * @return direct stream url
	 */
	public String getStreamLinksFromPlaylist(String quality, String channel) {
		String streamLink = "Unsupported Quality";
		String playlist = requestTwitchStreamPlaylist(channel);
		
		switch (quality.toLowerCase()) {
		case "best":
			streamLink = playlist.substring(
						 playlist.indexOf("http://"),
						 playlist.indexOf("#EXT", playlist.indexOf("http://")));
			return streamLink;
			
		case "high":
			streamLink = playlist.substring(
						 playlist.indexOf("http://", playlist.indexOf("VIDEO=\"high\"")), 
						 playlist.indexOf("#EXT", playlist.indexOf("VIDEO=\"high\"")));
			return streamLink;
			
		case "medium":
			streamLink = playlist.substring(
						 playlist.indexOf("http://", playlist.indexOf("VIDEO=\"medium\"")),
						 playlist.indexOf("#EXT", playlist.indexOf("VIDEO=\"medium\"")));
			return streamLink;
			
		case "low":
			streamLink = playlist.substring(
						 playlist.indexOf("http://", playlist.indexOf("VIDEO=\"low\"")),
						 playlist.indexOf("#EXT", playlist.indexOf("VIDEO=\"low\"")));
			return streamLink;
			
		case "worst":
			streamLink = playlist.substring(playlist.lastIndexOf("http://"));
			return streamLink;
			
		}//end switch
		return streamLink;
	}
	
	/**
	 * Checks if it is a valid/real Twitch stream
	 * @param streamName Twitch stream/channel name
	 * @return True, if it is a valid stream, else False
	 */
	public boolean streamExists(String streamName){
		StreamContainer container = getJsonDataFromApi(streamName);
		return container != null;
	}
	
	/**
	 * Retrieves data of whether streams are online or not, and stores them in a map
	 * @param streamList List of streams
	 * @param streamStatusMap Map to store stream status data
	 */
	public void getStreamStatus(List<String> streamList, Map<String, Boolean> streamStatusMap){
		StreamContainer streamInfo;
		System.out.print("S" + streamStatusMap.size() + " ");
		for(String streamName : streamList){
			streamInfo = getJsonDataFromApi(streamName);
			if (streamStatusMap.containsKey(streamName)) {
				streamStatusMap.replace(streamName, streamInfo.isOnline());
			}
			else{
				streamStatusMap.put(streamName, streamInfo.isOnline());
			}
		}
		System.out.println("S" + streamStatusMap.size());
	}
}
