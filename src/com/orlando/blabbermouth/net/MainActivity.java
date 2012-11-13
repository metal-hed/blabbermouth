package com.orlando.blabbermouth.net;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
        
        StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
        
        ListView listView = (ListView) findViewById(R.id.storylist);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
        	  public void onItemClick(AdapterView<?> parent, View view,
        	    int position, long id) {
        	    getArticle(position);
        	  }
        	});
        updateList();
    }
    
    public void updateList(){
    	ListView listView = (ListView) findViewById(R.id.storylist);
    	 String [] values = null;
         if(isOnline()){        
         	RSSFeedParser parser = new RSSFeedParser(url); 
         	rssFeed = parser.parse();
         	values = getRSSTitles();
         	
         	// First paramenter - Context
            // Second parameter - Layout for the row
            // Third parameter - ID of the TextView to which the data is written
            // Forth - the Array of data
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
              android.R.layout.simple_list_item_1, android.R.id.text1, values);

            // Assign adapter to ListView
            listView.setAdapter(adapter);
         }else{
         	Toast.makeText(getApplicationContext(), "No internet connection available", Toast.LENGTH_LONG).show();
         }
          
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
                updateList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
   
    private String [] getRSSTitles(){
    	
    	List<RSSEntry> l = rssFeed.getEntries();
    	String [] tits = new String [l.size()];
    	
    	Iterator<RSSEntry> it = l.iterator();
    	int count = 0;
    	while(it.hasNext()){
    		tits[count] = it.next().getTitle();
    		count++;
    	}
    	
    	return tits;
    }
    
    public void getArticle(int position){
    	Intent intent = new Intent(this, ViewArticleActivity.class);
    	String title = rssFeed.getEntries().get(position).getTitle();
    	String article = rssFeed.getEntries().get(position).getDescription();
    	String link = rssFeed.getEntries().get(position).getLink();
    	
    	intent.putExtra(TITLE_MESSAGE, title);
    	intent.putExtra(ARTICLE_MESSAGE, article);
    	intent.putExtra(LINK_MESSAGE, link);
    	startActivity(intent);
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
}
