package com.TwitchTest;

public class Preview {
	private String small;
	private String medium;
	private String large;
	private String template;

	public String getSmall() {
		return small;
	}

	public String getMedium() {
		return medium;
	}

	public String getLarge() {
		return large;
	}

	public String getTemplate() {
		String oldTemp = template;
		oldTemp = oldTemp.replace("{width}", "160");
		oldTemp = oldTemp.replace("{height}", "100");
		String newTemp = oldTemp;
		return newTemp;
	}

	public void setSmall(String small) {
		this.small = small;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}

	public void setLarge(String large) {
		this.large = large;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
}
