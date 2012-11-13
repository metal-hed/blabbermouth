package com.orlando.blabbermouth.net;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
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
        	    Toast.makeText(getApplicationContext(),
        	      "Click ListItem Number " + position, Toast.LENGTH_LONG)
        	      .show();
        	  }
        	});
        
                
        RSSFeedParser parser = new RSSFeedParser(url); 
        rssFeed = parser.parse();
        
        String [] values = getRSSTitles();
        
        // First paramenter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
          android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
        listView.setAdapter(adapter); 
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
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
}
