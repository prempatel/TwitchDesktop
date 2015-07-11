package com.TwitchTest;

public class Channel {
	private String display_name;
	private String status;
	private String url;
	private String created_at;
	private String updated_at;
	private int delay;
	private int followers;
	private int views;
	private String language;

	public String getDisplay_name() {
		return display_name;
	}

	public String getStatus() {
		return status;
	}

	public String getUrl() {
		return url;
	}

	public String getCreated_at() {
		return created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public int getDelay() {
		return delay;
	}

	public int getFollowers() {
		return followers;
	}
	
	public int getViews() {
		return views;
	}

	public String getLanguage() {
		return language;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public void setFollowers(int followers) {
		this.followers = followers;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
