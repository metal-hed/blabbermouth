package com.orlando.blabbermouth.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class BlabbermouthParser {
	// We don't use namespaces
    private static final String ns = null;
    private static final String CHANNEL = "channel";
    private static final String ITEM = "item";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String GUID = "guid";
    private static final String PUBDATE = "pubDate";
    
    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, "UTF-8");
            parser.nextTag(); //rss
            parser.nextTag(); //channel
            return readFeed(parser);
        } finally {
            in.close();
        }
    }
    
    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List items = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, CHANNEL);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the item tag
            if (name.equals(ITEM)) {
                items.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }  
        return items;
    }
    
    // Parses the contents of an entry. If it encounters a title, description, guid(link), pubDate tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Item readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, ITEM);
        String title = null;
        String description = null;
        String guid = null;
        String pubDate = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TITLE)) {
                title = readTitle(parser);
            } else if (name.equals(DESCRIPTION)) {
            	description = readDescription(parser);
            } else if (name.equals(GUID)) {
            	guid = readGuid(parser);
            } else if (name.equals(PUBDATE)){
            	pubDate = readPubDate(parser);
            } else {
                skip(parser);
            }
        }
        return new Item(title, description, guid, pubDate);
    }
    
    private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException{
    	parser.require(XmlPullParser.START_TAG, ns, TITLE);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, TITLE);
        return title;
    }
    
    private String readDescription(XmlPullParser parser) throws XmlPullParserException, IOException{
    	parser.require(XmlPullParser.START_TAG, ns, DESCRIPTION);
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, DESCRIPTION);
        return description;
    }
    
    private String readGuid(XmlPullParser parser) throws XmlPullParserException, IOException{
    	parser.require(XmlPullParser.START_TAG, ns, GUID);
        String guid = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, GUID);
        return guid;
    }
    
    private String readPubDate(XmlPullParser parser) throws XmlPullParserException, IOException{
    	parser.require(XmlPullParser.START_TAG, ns, PUBDATE);
        String pubDate = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, PUBDATE);
        return pubDate;
    }
    
    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                depth--;
                break;
            case XmlPullParser.START_TAG:
                depth++;
                break;
            }
        }
     }
    
    public static class Item {
        public final String title;
        public final String guid;
        public final String description;
        public final String date;

        private Item(String title, String description, String guid, String date) {
            this.title = title;
            this.description = description;
            this.guid = guid;
            this.date = date;
        }
    }
    
}
