package com.Prem.Twitch;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.google.gson.annotations.SerializedName;

public class AccessToken {
	@SerializedName("token")
	private String streamToken;
	
	@SerializedName("sig")
	private String tokenSig;
	
	public String getTokenSig() {
		return tokenSig;
	}
	
	public String getEncodedToken(){
		String encodedToken = null;
		try {
			encodedToken = URLEncoder.encode(streamToken, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodedToken;
	}
}