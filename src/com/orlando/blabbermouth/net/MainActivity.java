package com.orlando.blabbermouth.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.orlando.blabbermouth.net.BlabbermouthParser.Item;

public class MainActivity extends Activity {
	
	public final static String TITLE_MESSAGE = "com.orlando.blabbermouth.net.TITLE";
	public final static String ARTICLE_MESSAGE = "com.orlando.blabbermouth.net.ARTICLE";
	public final static String LINK_MESSAGE = "com.orlando.blabbermouth.net.LINK";
	
	private RSSFeed rssFeed;
	private final String url = "http://feeds.feedburner.com/blabbermouth?fmt=xml";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadPage();
    
        
       /* ListView listView = (ListView) findViewById(R.id.itemlist);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
        	  public void onItemClick(AdapterView<?> parent, View view,
        	    int position, long id) {
        	   // Place click action here
        	  }
        	});*/
    }
   

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                // Refresh list
            	loadPage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void loadPage(){
    	if (isOnline()){
    		new DownloadXMLTask().execute(url);
    	}else{
    		Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_LONG).show();	
    	}
    }
        
    private boolean isOnline() {
        ConnectivityManager cm =
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
    
 // Uploads XML from stackoverflow.com, parses it, and combines it with
 // HTML markup. Returns HTML string.
 private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
     InputStream stream = null;
     // Instantiate the parser
     BlabbermouthParser parser = new BlabbermouthParser();
     List<Item> items = null;
     String title = null;
     String url = null;
     String summary = null;
     Calendar rightNow = Calendar.getInstance(); 
     DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");
         
         
     StringBuilder htmlString = new StringBuilder();
     /*htmlString.append("<h3>" + getResources().getString(R.string.page_title) + "</h3>");
     htmlString.append("<em>" + getResources().getString(R.string.updated) + " " + 
             formatter.format(rightNow.getTime()) + "</em>");*/
         
     try {
         stream = downloadUrl(urlString);        
         items = parser.parse(stream);
     // Makes sure that the InputStream is closed after the app is
     // finished using it.
     } finally {
         if (stream != null) {
             stream.close();
         } 
      }
     
     // BlabbermouthParser returns a List (called "items") of Item objects.
     // Each Entry object represents a single post in the XML feed.
     // This section processes the entries list to combine each entry with HTML markup.
     // Each entry is displayed in the UI as a link that optionally includes
     // a text summary.
     for (Item item : items) {       
         htmlString.append("<p><a href='");
         htmlString.append(item.guid);
         htmlString.append("'>" + item.title + "</a></p>");
         htmlString.append(item.description);
     }
     return htmlString.toString();
 }

 // Given a string representation of a URL, sets up a connection and gets
 // an input stream.
 private InputStream downloadUrl(String urlString) throws IOException {
     URL url = new URL(urlString);
     HttpURLConnection conn = (HttpURLConnection) url.openConnection();
     conn.setReadTimeout(10000 /* milliseconds */);
     conn.setConnectTimeout(15000 /* milliseconds */);
     conn.setRequestMethod("GET");
     conn.setDoInput(true);
     // Starts the query
     conn.connect();
     InputStream stream = conn.getInputStream();
     return stream;
 }
    
    private class DownloadXMLTask extends AsyncTask<String, Void, String>{//Change this for returning a list
    	
    	@Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return getResources().getString(R.string.no_connection);
            } catch (XmlPullParserException e) {
            	return getResources().getString(R.string.xml_error);
            }
        }
    	
    	@Override
        protected void onPostExecute(String result) {  
            setContentView(R.layout.activity_main);
            // Displays the HTML string in the UI via a WebView
            WebView myWebView = (WebView) findViewById(R.id.itemlist);
            myWebView.loadData(result, "text/html", null);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.load_complete), Toast.LENGTH_SHORT).show();
        }
    	
    }
}
