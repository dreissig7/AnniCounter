package com.dreissig7.annicounter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.backup.BackupManager;
import android.app.backup.RestoreObserver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	public static final String TAG = "AnniCounter";
    public final static String EXTRA_ANNI = "com.dreissig7.annicounter.ANNI";
    // A key to uniquely identify the set of backup data
    static final String PREFS_BACKUP_KEY = "anniCounterPrefs";
    // The name of the SharedPreferences file
    public static final String PREFS = "com.dreissig7.annicounter_preferences";
    public static final int MODE = MODE_PRIVATE;
    public SharedPreferences prefs;
	
	private static final int INVALID_POSITION = -1;
	
	ListView lv0;
	ArrayAdapter<String> adapter0;
	ArrayList<String> valueList0 = new ArrayList<String>();

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Show the Up button in the action bar.
		setupActionBar();
		lv0 = (ListView)findViewById(R.id.listView0);
		
		prefs = getSharedPreferences(PREFS, MODE);
	
		CreateAnniList();
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			adapter0 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_activated_1, valueList0);
		}else{
			adapter0 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_single_choice, valueList0);
		}
		
	    lv0.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	    lv0.setAdapter(adapter0);
	    
	    lv0.setOnItemClickListener(new OnItemClickListener()
	    {
	    	
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	    {
			Intent intent = new Intent(getApplicationContext(), Current.class);
	        int numberOfAnni = arg2;
	        intent.putExtra(EXTRA_ANNI, numberOfAnni);
	        startActivity(intent);

	    	return;  
	    }
		
	    });
	    
	    lv0.setOnItemLongClickListener(new OnItemLongClickListener()
	    {
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	    {
			if (lv0.getCheckedItemPosition() == arg2){
	    		lv0.setItemChecked(arg2, false);
	    	    setDeleteIcon(false);
	    	}else{
	    		lv0.setItemChecked(arg2, true);
	    		setDeleteIcon(true);
	    	}
	    	return true;
	    }
		
	    });
	    
	}
	
	public void requestBackup() {
		   BackupManager bm = new BackupManager(this);
		   bm.dataChanged();
		 }
	public void restoreBackup() {
		BackupManager bm = new BackupManager(this);
		bm.requestRestore(
	                new RestoreObserver() {
	                    public void restoreFinished(int error) {
	                        /** Done with the restore!  Now draw the new state of our data */
//	                        Log.v(TAG, "Restore finished, error = " + error);
	                    }
	                }
	        );
	    
		}
	
	public void CreateAnniList() {
//		populate listview with saved data
		valueList0.clear();
		int tnoa = prefs.getInt(Anni.KEY_TNOA, 0);
		if (tnoa == 0){
			AddAnni();
			tnoa = prefs.getInt(Anni.KEY_TNOA, 0);
		}
		for (int i=0; i < tnoa; i++) {
			Anni anni = new Anni();
			anni.numberOfAnni = i;
			anni.getDataFromSharedPreferences(getApplicationContext(), PREFS, MODE);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		        anni.calcAnnis(getApplicationContext());
				valueList0.add("\n"+anni.title + "\n" + FirstMapEntryToString(anni.sortedMap));
			}else{
				valueList0.add(anni.title);
			}
		}
		
		return;
	}
	
	public String FirstMapEntryToString(Map<String,LocalDateTime> sortedMap){
        DateTimeFormatter fmt = DateTimeFormat.mediumDateTime();
        DateTimeFormatter fmt_dayOfWeek = DateTimeFormat.forPattern("E");
		// sortedMap mit formatiertem Datum als String in Liste für ListView ausgeben
		String firstEntry = null;
		Iterator<String> iterator = sortedMap.keySet().iterator();
//		  while (iterator.hasNext()) {
		  Object key = iterator.next();
		  DateTime dt = ((LocalDateTime) sortedMap.get(key)).toDateTime();
		  firstEntry = (fmt_dayOfWeek.print(dt) + " " + fmt.print(dt) + "\n" + key);
//		  }
		return firstEntry;
	}
	
	public void AddAnni() {
		valueList0.add(getResources().getString(R.string.pref_title_default));
		if (adapter0!=null){
			adapter0.notifyDataSetChanged();
		}
		
		
//		PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt(Anni.KEY_TNOA, valueList0.size()).commit();
		prefs.edit().putInt(Anni.KEY_TNOA, valueList0.size()).commit();
		
		Anni anni = new Anni();
		anni.numberOfAnni = valueList0.size() - 1;
		anni.title = getResources().getString(R.string.pref_title_default);
		anni.oldDate = getResources().getString(R.string.pref_oldDate_default);
		anni.oldTime = getResources().getString(R.string.pref_oldTime_default);
		anni.oldTimeZone = getResources().getString(R.string.pref_oldTimeZone_default);
		anni.setTimeZoneToCurrent = false;
		anni.setTimeZoneManually = false;
		anni.picPath = getResources().getString(R.string.pref_pic_path_default);
		anni.shareText = getResources().getString(R.string.pref_sharetext_default);    
		anni.setDefaultMessage = false;
		anni.putDataToSharedPreferences(getApplicationContext(), PREFS, MODE);
		requestBackup();
	}

	@Override
    protected void onResume(){
    	super.onResume(); 	
        lv0.clearChoices();
        lv0.invalidateViews();
        CreateAnniList();
        adapter0.notifyDataSetChanged();
    }

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
        		
        	case  R.id.addAnni:
        		AddAnni();
        		return true;
        		
        	case  R.id.deleteAnni:
      
        		int pos = lv0.getCheckedItemPosition();
        		if (pos != INVALID_POSITION) {
        			
        			Anni delAnni = new Anni();
        			delAnni.numberOfAnni = pos;
        			delAnni.getDataFromSharedPreferences(getApplicationContext(), PREFS, MODE);
        			
        			for (int i=1; i<(valueList0.size()-pos);i++){
        				Anni anni_move = new Anni();
        				anni_move.numberOfAnni = pos+i;
        				anni_move.getDataFromSharedPreferences(getApplicationContext(), PREFS, MODE);
        				anni_move.numberOfAnni--;
        				anni_move.putDataToSharedPreferences(getApplicationContext(), PREFS, MODE);
        			}
        			
        			valueList0.remove(pos);
        			adapter0.notifyDataSetChanged();
//        			PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt(anni.KEY_TNOA, valueList0.size()).commit();
        			prefs.edit().putInt(Anni.KEY_TNOA, valueList0.size()).commit();
        			
        			lv0.clearChoices();
        			lv0.clearChildFocus(getCurrentFocus());
        			setDeleteIcon(false);
        			requestBackup();
        		}	
        		return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    void setDeleteIcon(boolean enabled){
		// hier könnte das delete icon je nach Markierung an/abgeschaltet werden
    }

}
