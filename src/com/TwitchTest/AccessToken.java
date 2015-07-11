package com.TwitchTest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.google.gson.annotations.SerializedName;

public class AccessToken {
	@SerializedName("token")
	private String streamToken;
	
	@SerializedName("sig")
	private String tokenSig;
	
	public String getStreamToken() {
		return streamToken;
	}
	
	public String getTokenSig() {
		return tokenSig;
	}
	
	public String getEncodedToken(){
		String encodedToken = null;
		try {
			encodedToken = URLEncoder.encode(getStreamToken(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodedToken;
	}
}