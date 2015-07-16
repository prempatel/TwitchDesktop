package com.Prem.Twitch;

public class Stream {
	private long _id;
	private String game;
	private int viewers;
	private String created_at;
	private int video_height;
	private double average_fps;
	private Preview preview;
	private Channel channel;

	public long get_id() { return _id; }

	public String getGame() { return game; }

	public int getViewers() { return viewers; }

	public String getCreated_at() { return created_at; }

	public int getVideo_height() { return video_height; }

	public double getAverage_fps() { return average_fps; }

	public Preview getPreview() { return preview; }
	
	public String getPreviewTemplate(){ return preview.getTemplate(); }

	public Channel getChannel() { return channel; }

	public void set_id(long _id) { this._id = _id; }

	public void setGame(String game) { this.game = game; }

	public void setViewers(int viewers) { this.viewers = viewers; }

	public void setCreated_at(String created_at) { this.created_at = created_at; }

	public void setVideo_height(int video_height) { this.video_height = video_height; }

	public void setAverage_fps(double average_fps) { this.average_fps = average_fps; }

	public void setPreview(Preview preview) { this.preview = preview; }

	public void setChannel(Channel channel) { this.channel = channel; }
}