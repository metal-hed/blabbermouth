package com.orlando.blabbermouth.net;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ViewArticleActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
     // Set the text view as the activity layout
        setContentView(R.layout.activity_view_article);
        
        // Get message from intent
        Intent intent = getIntent();
        String title = intent.getStringExtra(MainActivity.TITLE_MESSAGE);
        String article = intent.getStringExtra(MainActivity.ARTICLE_MESSAGE);
        String link = intent.getStringExtra(MainActivity.LINK_MESSAGE);
        
        // Get and update the text view
        TextView titleView = (TextView)findViewById(R.id.titleText);
        titleView.setTextSize(18);
        titleView.setText(title);
        
     
        TextView articleView = (TextView)findViewById(R.id.articleText);
        articleView.setTextSize(16);
        articleView.setText(article);
        
        TextView linkView = (TextView)findViewById(R.id.linkText);
        linkView.setTextSize(14);
        linkView.setClickable(true);
        linkView.setTextColor(Color.BLUE);
        linkView.setText(link);
        
    }
    
    public void onArticleClick(View view){
    	TextView linkView = (TextView)findViewById(R.id.linkText);
    	String link = linkView.getText().toString();
    	
    	Uri webpage = Uri.parse(link);
    	Intent webIntent = new Intent(Intent.ACTION_VIEW,webpage);
    	
    	if(isIntentAvailable(webIntent)){
        	Toast.makeText(getApplicationContext(), "Opening "+link, Toast.LENGTH_LONG).show();
        	startActivity(webIntent);
		}
    }
    
    // Check to see if there is an application available to handle the passed intent
    private boolean isIntentAvailable(Intent intent){
    	PackageManager packageManager = getPackageManager();
    	List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
    	return activities.size() > 0;
    }
}
