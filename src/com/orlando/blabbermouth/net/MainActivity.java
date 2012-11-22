package com.orlando.blabbermouth.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.orlando.blabbermouth.net.BlabbermouthParser.Item;

public class MainActivity extends Activity {
	
	public final static String TITLE_MESSAGE = "com.orlando.blabbermouth.net.TITLE";
	public final static String ARTICLE_MESSAGE = "com.orlando.blabbermouth.net.ARTICLE";
	public final static String LINK_MESSAGE = "com.orlando.blabbermouth.net.LINK";
	
	private List<Item> rssFeed;
	private final String url = "http://feeds.feedburner.com/blabbermouth?fmt=xml";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ListView listView = (ListView) findViewById(R.id.itemlist);

        listView.setOnItemClickListener(new OnItemClickListener() {
        	  public void onItemClick(AdapterView<?> parent, View view,
        	    int position, long id) {
        	   getArticle(position);
        	  }
        	});
        
        loadPage();
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
	         
	     try {
	         stream = downloadUrl(urlString);        
	         rssFeed = parser.parse(stream);
	     // Makes sure that the InputStream is closed after the app is
	     // finished using it.
	     } finally {
	         if (stream != null) {
	             stream.close();
	         } 
	      }
	     
	     return "200";
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
 
	 private String [] getTitles(List<Item> feed){
		 String [] titles = new String[feed.size()];
		
		 for(int i = 0; i < titles.length; i++){
			 titles[i] = feed.get(i).title;
		 }
		 
		 return titles;
	 }
 
	 public void getArticle(int position){
	 	Intent intent = new Intent(this, ViewArticleActivity.class);
	 	String title = rssFeed.get(position).title;
	 	String article = rssFeed.get(position).description;
	 	String link = rssFeed.get(position).guid;
	 	
	 	intent.putExtra(TITLE_MESSAGE, title);
	 	intent.putExtra(ARTICLE_MESSAGE, article);
	 	intent.putExtra(LINK_MESSAGE, link);
	 	startActivity(intent);
	 }
    
	 private void updateListView(){
		 ListView myListView = (ListView) findViewById(R.id.itemlist);
	     
	     // First paramenter - Context
		 // Second parameter - Layout for the row
		 // Third parameter - ID of the TextView to which the data is written
		 // Forth - the Array of data
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				 android.R.layout.simple_list_item_1, android.R.id.text1, getTitles(rssFeed));
	
		 // Assign adapter to ListView
	     myListView.setAdapter(adapter);
	 }
    private class DownloadXMLTask extends AsyncTask<String, Void, String>{
    	
    	@Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(url);
            } catch (IOException e) {
            	Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            	return null;
            } catch (XmlPullParserException e) {
            	Toast.makeText(getApplicationContext(), getResources().getString(R.string.xml_error), Toast.LENGTH_SHORT).show();
            	return null;
            }
        }
    	
    	@Override
        protected void onPostExecute(String result) {  
            setContentView(R.layout.activity_main);
            updateListView();
            //Toast.makeText(getApplicationContext(), getResources().getString(R.string.load_complete), Toast.LENGTH_SHORT).show();
        }
    	
    }
}
