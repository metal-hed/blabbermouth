package com.orlando.blabbermouth.net;

import java.util.ArrayList;
import java.util.List;

public class RSSFeed {
	private String title;
	private String link;
	private String description;
	private List<RSSEntry> entries = new ArrayList<RSSEntry>();
	private String date;
	
	public RSSFeed(String title, String link, String desc, String date){
		this.title = title;
		this.link = link;
		this.description = desc;
		this.date = date;
	}
	
	public RSSFeed() {}

	public String getTitle() {
		return title;
	}
	public String getLink() {
		return link;
	}
	public String getDescription() {
		return description;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<RSSEntry> getEntries() {
		return entries;
	}
	
	public void add(RSSEntry entry){
		entries.add(entry);
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
