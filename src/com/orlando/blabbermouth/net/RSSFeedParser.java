package com.orlando.blabbermouth.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class RSSFeedParser {
  static final String TITLE = "title";
  static final String DESCRIPTION = "description";
  static final String CHANNEL = "channel";
  static final String LANGUAGE = "language";
  static final String COPYRIGHT = "copyright";
  static final String LINK = "link";
  static final String AUTHOR = "author";
  static final String ITEM = "item";
  static final String PUB_DATE = "lastBuildDate";
  static final String GUID = "guid";
  static final String RSS = "rss";

  final URL url;

  public static void main (String [] args){
	  RSSFeedParser r = new RSSFeedParser("http://feeds.feedburner.com/blabbermouth?fmt=xml");
	  RSSFeed f = r.parse();
	  List<RSSEntry> l = f.getEntries();
	  String [] tits = new String [l.size()];
	  	
	  	Iterator<RSSEntry> it = l.iterator();
	  	int count = 0;
	  	while(it.hasNext()){
	  		tits[count] = it.next().getTitle();
	  	}
	  	
	  	for(String s:tits){
	  		System.out.println(s);
	  	}
	  	
  }
   
  public RSSFeedParser(String feedUrl) {
    try {
      this.url = new URL(feedUrl);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public RSSFeed parse() {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      RSSFeed messages = new RSSFeed();
      try {
          DocumentBuilder builder = factory.newDocumentBuilder();
          Document dom = builder.parse(this.getInputStream());
          Element root = dom.getDocumentElement();
          
          messages.setTitle(root.getElementsByTagName(TITLE).item(0).getFirstChild().getNodeValue());
          messages.setDescription(root.getElementsByTagName(DESCRIPTION).item(0).getFirstChild().getNodeValue());
          messages.setLink(root.getElementsByTagName(LINK).item(0).getFirstChild().getNodeValue());
          messages.setDate(root.getElementsByTagName(PUB_DATE).item(0).getFirstChild().getNodeValue());
          
          NodeList items = root.getElementsByTagName(ITEM);
          
          for (int i=0;i<items.getLength();i++){
        	  RSSEntry message = new RSSEntry();
              Node item = items.item(i);
              NodeList properties = item.getChildNodes();
              
              for (int j=0;j<properties.getLength();j++){
                  Node property = properties.item(j);
                  String name = property.getNodeName();
                  if (name.equalsIgnoreCase(TITLE)){
                      message.setTitle(property.getFirstChild().getNodeValue());
                  } else if (name.equalsIgnoreCase(LINK)){
                      message.setLink(property.getFirstChild().getNodeValue());
                  } else if (name.equalsIgnoreCase(DESCRIPTION)){
                      StringBuilder text = new StringBuilder();
                      NodeList chars = property.getChildNodes();
                      for (int k=0;k<chars.getLength();k++){
                          text.append(chars.item(k).getNodeValue());
                      }
                      message.setDescription(text.toString());
                  } 
              }
              
              messages.add(message);
          }
          
      } catch (Exception e) {
          throw new RuntimeException(e);
      } 
      return messages;
  }

  private InputStream getInputStream() {
    try {
      return url.openConnection().getInputStream();
    } catch (IOException e) {
      throw new RuntimeException("An exception has occured: "+e.getMessage() + e);
    }
  }
} 
