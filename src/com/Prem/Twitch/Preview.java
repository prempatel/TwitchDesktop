package com.Prem.Twitch;

public class Preview {
	private String small;
	private String medium;
	private String large;
	private String template;

	public String getSmall() { return small; }

	public String getMedium() { return medium; }

	public String getLarge() { return large; }

	//Generates custom preview image to fit in gui
	public String getTemplate() {
		String temp = template;
		temp = temp.replace("{width}", "160");
		temp = temp.replace("{height}", "100");
		return temp;
	}

	public void setSmall(String small) { this.small = small; }

	public void setMedium(String medium) { this.medium = medium; }

	public void setLarge(String large) { this.large = large; }

	public void setTemplate(String template) { this.template = template; }
}
