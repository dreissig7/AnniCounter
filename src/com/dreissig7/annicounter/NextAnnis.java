package com.dreissig7.annicounter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.NavUtils;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NextAnnis extends Activity {

    public static int numberOfAnni;   
    private Anni anni = new Anni();
    
	ListView lv;
	ListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_next_annis);
		
//		// Get the message from the intent
		Intent intent = getIntent();
        numberOfAnni = intent.getIntExtra(MainActivity.EXTRA_ANNI, 0);
        
		// Show the Up button in the action bar.
		setupActionBar();
		
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
	    	TextView text = (TextView) findViewById(R.id.nextAnnisText);
	    	text.setText(getResources().getString(R.string.nextAnnisClickToShare)+"\n"+getResources().getString(R.string.nextAnnisLongClick));
	    }
	    
        anni.numberOfAnni = numberOfAnni;
        anni.getDataFromSharedPreferences(getApplicationContext(), MainActivity.PREFS, MainActivity.MODE);       

		lv = (ListView)findViewById(R.id.ListView1);
	}
	
	@Override
    protected void onResume(){
    	super.onResume();
        // change title and datetime according to preferences
        anni.getDataFromSharedPreferences(getApplicationContext(), MainActivity.PREFS, MainActivity.MODE);
        this.setTitle(anni.title);
        anni.calcAnnis(getApplicationContext());
	    generateListView(MapToString(anni.sortedMap));
    }

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.next_annis, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
			
		case R.id.share:
    		
    		Intent sendIntent = new Intent();
    		sendIntent.setAction(Intent.ACTION_SEND);
    		sendIntent.putExtra(Intent.EXTRA_TEXT, createShareText());
    		sendIntent.setType("text/plain");
    		startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share)));
    		return true;
    		
        case R.id.action_settings:
        	Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
	        intent.putExtra(MainActivity.EXTRA_ANNI, numberOfAnni);
	        startActivity(intent);
            return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public List<String> MapToString(Map<String,LocalDateTime> sortedMap){
        DateTimeFormatter fmt = DateTimeFormat.mediumDateTime();
        DateTimeFormatter fmt_dayOfWeek = DateTimeFormat.forPattern("E");
		// sortedMap mit formatiertem Datum als String in Liste für ListView ausgeben
		List<String> sortedList = new ArrayList<String>();
		Iterator<String> iterator = sortedMap.keySet().iterator();
		  while (iterator.hasNext()) {
		  Object key = iterator.next();
		  DateTime dt = ((LocalDateTime) sortedMap.get(key)).toDateTime();
		  sortedList.add(fmt_dayOfWeek.print(dt) + " " + fmt.print(dt) + "\n" + key);
		  }
		return sortedList;
	}
	
	public void generateListView(List<String> valueList){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, valueList);
		}else{
			adapter = new ArrayAdapter<String>(getApplicationContext(), R.xml.small_simple_list_item_multiple_choice, valueList);
		}
	    lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	    lv.setAdapter(adapter);
	    
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

	    	lv.setOnItemLongClickListener(new OnItemLongClickListener()
		    {
			@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			@Override
		    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		    {
		    	String title = anni.title;
		    	String selectedItem = lv.getAdapter().getItem(arg2).toString();
		    	String[] selectedItemParts = selectedItem.split("\\n");
		    	String selectedDate = selectedItemParts[0].substring(selectedItemParts[0].indexOf(" ")+1);
		    	
		    	DateTimeFormatter fmt = DateTimeFormat.mediumDateTime();
	
		        DateTime beginDateTime = new DateTime();
		      
		        try {
		        beginDateTime = fmt.parseDateTime(selectedDate);
		        } catch (IllegalArgumentException e) {
		        e.printStackTrace();
		        }
		    	
		    	DateTime endDateTime = beginDateTime.plusHours(1);
		    	Intent intent = new Intent(Intent.ACTION_INSERT)
		    	        .setData(Events.CONTENT_URI)
		    	        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, 
		    	        		beginDateTime.getMillis())
		    	        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, 
		    	        		endDateTime.getMillis())
		    	        .putExtra(Events.TITLE, title + " " + selectedItemParts[1])
		    	        .putExtra(Events.DESCRIPTION, title + "\n" + selectedItemParts[0] + 
		    	        		"\n" + selectedItemParts[1] + "\n\n(" + 
		    	        		getResources().getString(R.string.pref_oldTimeZone_title) + 
		    	        		": "+String.valueOf(TimeZone.getDefault().getID())+")" + 
		    	        		"\n---\n"+title+" AnniCounter")
	//	    	        .putExtra(Events.EVENT_LOCATION, "The gym")
	//	    	        .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
	//	    	        .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com")
		    	        ;
//		    	startActivity(intent);
		    	startActivity(Intent.createChooser(intent, getResources().getText(R.string.createDate)));
				return false;  
		    }
		    });
	    }
	}
	
	public String createShareText(){
		SparseBooleanArray checked = lv.getCheckedItemPositions();
        ArrayList<String> selectedItems = new ArrayList<String>();
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            // Add date if it is checked i.e.) == TRUE!
            if (checked.valueAt(i))
                selectedItems.add((String) adapter.getItem(position));
        }
 
        String[] outputStrArr = new String[selectedItems.size()];
 
        for (int i = 0; i < selectedItems.size(); i++) {
            outputStrArr[i] = selectedItems.get(i);
        }
 
        String outputStr = anni.title;
        for (int i = 0; i < outputStrArr.length; i++) {
            outputStr = outputStr.concat("\n\n"+outputStrArr[i]);
        }
        
        outputStr = outputStr.concat("\n\n("+getResources().getString(R.string.pref_oldTimeZone_title)+
        		": "+String.valueOf(TimeZone.getDefault().getID())+")");
        outputStr = outputStr.concat(getResources().getString(R.string.signature));
        
		return outputStr;
		
	}

}
