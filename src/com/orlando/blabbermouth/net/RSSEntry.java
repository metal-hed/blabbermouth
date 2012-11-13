package com.orlando.blabbermouth.net;

public class RSSEntry {

	private String title;
	private String desc;
	private String link;
	
	public RSSEntry(String title, String desc, String origLink){
		this.setTitle(title);
		this.setDescription(desc);
		this.setLink(origLink);
	}
	
	public RSSEntry(){}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return desc;
	}

	public void setDescription(String desc) {
		this.desc = desc;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String origLink) {
		this.link = origLink;
	}
}
