package com.dreissig7.annicounter;

import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import com.dreissig7.annicounter.R;

@SuppressLint("SimpleDateFormat")
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    public static int numberOfAnni;
    private Anni anni = new Anni();
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setupActionBar();
		
        Intent intent = getIntent();
        numberOfAnni = intent.getIntExtra(MainActivity.EXTRA_ANNI, 0);
        anni.numberOfAnni = numberOfAnni;
        anni.getDataFromSharedPreferences(getApplicationContext(), MainActivity.PREFS, MainActivity.MODE);
        setPreferenceScreen(createPreferenceHierarchy(anni));
        
        setupPicPrefOnClickListener();
        setupTimeZoneList();    
    }
    
    @SuppressWarnings("deprecation")
	public void setupTimeZoneList() {
//      Setup ListPreference with time zone id's
      String[] tzid = TimeZone.getAvailableIDs();
      TimeZone tz;
      int len = tzid.length;
      String[] tzid2 = new String[len];
      String[] tzval = new String[len];
      int offset;
      for (int i = 0; i < len; ++i) {
      	tz = TimeZone.getTimeZone(tzid[i]);
      	offset = tz.getRawOffset();  
      	tzval[i] = String.format("%+03d%02d", offset / 3600000, Math.abs((offset / 60000) % 60)); 
      	tzid2[i] = tzid[i]+" UTC"+tzval[i];
      }
      ListPreference lp = (ListPreference)findPreference(anni.KEY_OLDTIMEZONELIST);
      lp.setEntries(tzid2);
      lp.setEntryValues(tzid);
    }
    
    @SuppressWarnings("deprecation")
	public void setupPicPrefOnClickListener() {
    	Preference pref = (Preference) findPreference("pref_choose_pic"+String.valueOf(numberOfAnni));
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
          @Override
          public boolean onPreferenceClick(Preference preference) {
      		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
      		startActivityForResult(i, 1234);
            return true;
          }
        });
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
			
//		case android.R.id.home:
//			// This ID represents the Home or Up button. In the case of this
//			// activity, the Up button is shown. Use NavUtils to allow users
//			// to navigate up one level in the application structure. For
//			// more details, see the Navigation pattern on Android Design:
//			//
//			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
//			//
//			NavUtils.navigateUpFromSameTask(this);
//			NavUtils.navigateUpTo(getParent(), getParentActivityIntent());
//			
//			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("deprecation")
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		String tz2;
        if (key.equals(anni.KEY_TITLE)) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
            anni.getDataFromSharedPreferences(getApplicationContext(), MainActivity.PREFS, MainActivity.MODE);
        }
        if (key.equals(anni.KEY_OLDDATE)) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
            anni.getDataFromSharedPreferences(getApplicationContext(), MainActivity.PREFS, MainActivity.MODE);
        }
        if (key.equals(anni.KEY_OLDTIME)) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
            anni.getDataFromSharedPreferences(getApplicationContext(), MainActivity.PREFS, MainActivity.MODE);
        }
        if (key.equals(anni.KEY_OLDTIMEZONELIST)) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
            anni.getDataFromSharedPreferences(getApplicationContext(), MainActivity.PREFS, MainActivity.MODE);
        }
        if (key.equals(anni.KEY_PIC_PATH)) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
            anni.getDataFromSharedPreferences(getApplicationContext(), MainActivity.PREFS, MainActivity.MODE);
        }
        if (key.equals(anni.KEY_SETTIMEZONETOCURRENT)) {
            if (sharedPreferences.getBoolean(key, false) == true){
            	tz2 = TimeZone.getDefault().getID();
            	anni.setTimeZoneToCurrent = false;
            	anni.oldTimeZone = tz2;
            	anni.putDataToSharedPreferences(getApplicationContext(), MainActivity.PREFS, MainActivity.MODE);
            }
        }
        if (key.equals(anni.KEY_SETDEFAULTMESSAGE)) {
            if (sharedPreferences.getBoolean(key, false) == true){
            	String defText = getResources().getString(R.string.pref_sharetext_default);
            	anni.setDefaultMessage = false;
            	anni.shareText = defText;
            	anni.putDataToSharedPreferences(getApplicationContext(), MainActivity.PREFS, MainActivity.MODE);
            }
        }
    }
	
    @SuppressWarnings("deprecation")
	@Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        
    }

    @SuppressWarnings("deprecation")
	@Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
    
    @SuppressWarnings("deprecation")
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         
         
        if (requestCode == 1234 &&resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
    
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
    
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            
            anni.picPath = picturePath;
            anni.putDataToSharedPreferences(getApplicationContext(), MainActivity.PREFS, MainActivity.MODE);
            
            Preference connectionPref = findPreference(anni.KEY_PIC_PATH);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(picturePath);
            
        	}
        }
    
    @SuppressWarnings("deprecation")
	private PreferenceScreen createPreferenceHierarchy(Anni anni) {
    	
    	String numberOfAnni_String = String.valueOf(anni.numberOfAnni);
    	
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        
//        //PreferenceScreen für Jahrestag
//        PreferenceScreen screenPref = getPreferenceManager().createPreferenceScreen(this);
//        screenPref.setKey("pref_date_settings");
//        screenPref.setTitle(getResources().getString(R.string.pref_date_title));
////        screenPref.setSummary(R.string.pref_date_summary);
//        root.addPreference(screenPref);
        
        // Edit text preference: Jahrestagtitel
        EditTextPreference title = new EditTextPreference(this);
        title.setDialogTitle(getResources().getString(R.string.pref_title_dialogTitle));
        title.setKey(anni.KEY_TITLE);
        title.setTitle(getResources().getString(R.string.pref_title_title));
//        title.setSummary(getResources().getString(R.string.pref_title_summary));
        title.setSummary(anni.title);
        title.setDefaultValue(getResources().getString(R.string.pref_title_default));
        root.addPreference(title);

        PreferenceCategory datePrefCat = new PreferenceCategory(this);
        datePrefCat.setTitle(getResources().getString(R.string.pref_date_title));
        datePrefCat.setKey("pref_date_settings_datetime"+numberOfAnni_String);
        root.addPreference(datePrefCat);
        
        com.dreissig7.annicounter.DatePreference datePref = new com.dreissig7.annicounter.DatePreference(this, null);
        datePref.setKey(anni.KEY_OLDDATE);
        datePref.setTitle(getResources().getString(R.string.pref_oldDate_title));
        datePref.setDialogTitle(getResources().getString(R.string.pref_oldDate_dialogTitle));
        datePref.setDefaultValue(getResources().getString(R.string.pref_oldDate_default));
//        datePref.setSummary(getResources().getString(R.string.pref_oldDate_summary));
        datePref.setSummary(anni.oldDate);
        datePrefCat.addPreference(datePref);
        
        com.dreissig7.annicounter.TimePreference timePref = new com.dreissig7.annicounter.TimePreference(this, null);
        timePref.setKey(anni.KEY_OLDTIME);
        timePref.setTitle(getResources().getString(R.string.pref_oldTime_title));
        timePref.setDialogTitle(getResources().getString(R.string.pref_oldTime_dialogTitle));
        timePref.setDefaultValue(getResources().getString(R.string.pref_oldTime_default));
//        timePref.setSummary(getResources().getString(R.string.pref_oldTime_summary));
        timePref.setSummary(anni.oldTime);
        datePrefCat.addPreference(timePref);
        
        // List preference Zeitzonen
        ListPreference listPref = new ListPreference(this);
        listPref.setEntries(R.array.pref_oldTimeZone_entries);
        listPref.setEntryValues(R.array.pref_oldTimeZone_values);
        listPref.setDialogTitle(getResources().getString(R.string.pref_oldTimeZone_dialogTitle));
        listPref.setKey(anni.KEY_OLDTIMEZONELIST);
        listPref.setTitle(getResources().getString(R.string.pref_oldTimeZone_title));
//        listPref.setSummary(getResources().getString(R.string.pref_oldTimeZone_summary));
        listPref.setSummary(anni.oldTimeZone);
        listPref.setDefaultValue(getResources().getString(R.string.pref_oldTimeZone_default));
        datePrefCat.addPreference(listPref);
        
        com.dreissig7.annicounter.OptionDialogPreference setTimeZoneToCurrentPref = new com.dreissig7.annicounter.OptionDialogPreference(this, null);
        setTimeZoneToCurrentPref.setKey(anni.KEY_SETTIMEZONETOCURRENT);
        setTimeZoneToCurrentPref.setDialogIcon(android.R.drawable.ic_dialog_alert);
        setTimeZoneToCurrentPref.setTitle(getResources().getString(R.string.pref_setTimeZoneToCurrent_title));
        setTimeZoneToCurrentPref.setSummary(getResources().getString(R.string.pref_setTimeZoneToCurrent_summary));
        setTimeZoneToCurrentPref.setDialogMessage(getResources().getString(R.string.pref_setTimeZoneToCurrent_message));
        setTimeZoneToCurrentPref.setPositiveButtonText(getResources().getString(R.string.pref_setTimeZoneToCurrent_positive));
        setTimeZoneToCurrentPref.setNegativeButtonText(getResources().getString(R.string.pref_setTimeZoneToCurrent_negative));
        datePrefCat.addPreference(setTimeZoneToCurrentPref);
        
    	CheckBoxPreference setTimeZoneManuallyPref = new CheckBoxPreference(this);
    	setTimeZoneManuallyPref.setKey(anni.KEY_SETTIMEZONEMANUALLY);
    	setTimeZoneManuallyPref.setEnabled(false);
    	setTimeZoneManuallyPref.setShouldDisableView(true);
    	setTimeZoneManuallyPref.setTitle(getResources().getString(R.string.pref_setTimeZoneManually_title));
    	setTimeZoneManuallyPref.setSummary(getResources().getString(R.string.pref_setTimeZoneManually_summary));
    	setTimeZoneManuallyPref.setDefaultValue("false");
    	datePrefCat.addPreference(setTimeZoneManuallyPref);
    	
        PreferenceCategory designPrefCat = new PreferenceCategory(this);
        designPrefCat.setTitle(getResources().getString(R.string.pref_date_design));
        designPrefCat.setKey("pref_date_settings_design"+numberOfAnni_String);
        root.addPreference(designPrefCat);
        
        // Intent preference
        PreferenceScreen choosePicPref = getPreferenceManager().createPreferenceScreen(this);
        choosePicPref.setIntent(new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("image/*"));
        choosePicPref.setTitle(getResources().getString(R.string.pref_choose_pic));
        choosePicPref.setKey("pref_choose_pic"+numberOfAnni_String);
//        choosePicPref.setSummary(R.string.pref_choose_pic_summary);
        designPrefCat.addPreference(choosePicPref);
        
        //PreferenceScreen für Erweiterte Einstellungen
        PreferenceScreen advancedScreenPref = getPreferenceManager().createPreferenceScreen(this);
        advancedScreenPref.setKey("pref_screen_advanced_settings"+numberOfAnni_String);
        advancedScreenPref.setTitle(R.string.pref_screen_advanced_settings_title);
        advancedScreenPref.setSummary(R.string.pref_screen_advanced_settings_summary);
        root.addPreference(advancedScreenPref);  
        
      //PreferenceScreen für Bild Pfad
      PreferenceScreen pathScreenPref = getPreferenceManager().createPreferenceScreen(this);
      pathScreenPref.setKey("pref_screen_path_settings"+numberOfAnni_String);
      pathScreenPref.setTitle(R.string.pref_screen_path_settings_title);
      pathScreenPref.setSummary(R.string.pref_screen_path_settings_summary);
      advancedScreenPref.addPreference(pathScreenPref);
        
        // Edit text preference: Bildpfad
        EditTextPreference pathPref = new EditTextPreference(this);
        pathPref.setDialogTitle(getResources().getString(R.string.pref_pic_path_dialogTitle));
        pathPref.setKey(anni.KEY_PIC_PATH);
        pathPref.setTitle(getResources().getString(R.string.pref_pic_path_title));
//        pathPref.setSummary(getResources().getString(R.string.pref_pic_path_summary));
        pathPref.setSummary(anni.picPath);
        pathPref.setDefaultValue(getResources().getString(R.string.pref_pic_path_default));
        pathPref.setShouldDisableView(false);
        pathPref.setEnabled(true);
        pathScreenPref.addPreference(pathPref);
        
        // Edit text preference: Sharetext
        EditTextPreference sharetextPref = new EditTextPreference(this);
        sharetextPref.setDialogTitle(getResources().getString(R.string.pref_sharetext_dialogTitle));
        sharetextPref.setKey(anni.KEY_SHARETEXT);
        sharetextPref.setTitle(getResources().getString(R.string.pref_sharetext_title));
        sharetextPref.setSummary(getResources().getString(R.string.pref_sharetext_summary));
        sharetextPref.setDefaultValue(getResources().getString(R.string.pref_sharetext_default));
//        sharetextPref.setShouldDisableView(true);
//        sharetextPref.setEnabled(false);
        advancedScreenPref.addPreference(sharetextPref);
        
        com.dreissig7.annicounter.OptionDialogPreference setDefaultMessagePref = new com.dreissig7.annicounter.OptionDialogPreference(this, null);
        setDefaultMessagePref.setKey(anni.KEY_SETDEFAULTMESSAGE);
        setDefaultMessagePref.setDialogIcon(android.R.drawable.ic_dialog_alert);
        setDefaultMessagePref.setTitle(getResources().getString(R.string.pref_setDefaultMessage_title));
        setDefaultMessagePref.setSummary(getResources().getString(R.string.pref_setDefaultMessage_summary));
        setDefaultMessagePref.setDialogMessage(getResources().getString(R.string.pref_setDefaultMessage_message));
        setDefaultMessagePref.setPositiveButtonText(getResources().getString(R.string.pref_setDefaultMessage_positive));
        setDefaultMessagePref.setNegativeButtonText(getResources().getString(R.string.pref_setDefaultMessage_negative));
//      sharetextPref.setShouldDisableView(true);
//      sharetextPref.setEnabled(false);
        advancedScreenPref.addPreference(setDefaultMessagePref);
        
        return root;
    }


}

