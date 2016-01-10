package com.dreissig7.annicounter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.LocalDateTime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class Anni {
	public int numberOfAnni;
	public String title;
	public String oldDate;
	public String oldTime;
	public String oldTimeZone;
	public Boolean setTimeZoneToCurrent;
	public Boolean setTimeZoneManually;
	public String picPath;
	public String shareText;
	public Boolean setDefaultMessage;
	public Long oldDateTimeInMillis;
	public Map<String,LocalDateTime> sortedMap = new LinkedHashMap<String,LocalDateTime>();

	// preference keys
	public String KEY_TITLE;
	public String KEY_OLDDATE;
	public String KEY_OLDTIME;
	public String KEY_OLDTIMEZONELIST;
	public String KEY_SETTIMEZONETOCURRENT;
	public String KEY_SETTIMEZONEMANUALLY;
	public String KEY_PIC_PATH;
	public String KEY_SHARETEXT;
	public String KEY_SETDEFAULTMESSAGE;
	
	public static final String KEY_TNOA = "pref_total_number_of_annis";
	
//	private static final int[] years = {10,100,
//	11,111};
private static final int[] months = {11,111,1111};
private static final int[] weeks = {11,111,1111};
private static final int[] days = {111,1111,11111};
private static final int[] hours = {1111,11111,111111};
private static final int[] minutes = {111111,1111111,11111111};
private static final long[] seconds = {11111111,111111111,1111111111};
	
	public void setKeys() {
		String numberOfAnni_String = String.valueOf(numberOfAnni);
		KEY_TITLE = "pref_title" + numberOfAnni_String;
		KEY_OLDDATE = "pref_oldDate" + numberOfAnni_String;
		KEY_OLDTIME = "pref_oldTime" + numberOfAnni_String;
		KEY_OLDTIMEZONELIST = "pref_oldTimeZoneList" + numberOfAnni_String;
		KEY_SETTIMEZONETOCURRENT= "pref_setTimeZoneToCurrent" + numberOfAnni_String;
		KEY_SETTIMEZONEMANUALLY = "pref_setTimeZoneManually" + numberOfAnni_String;
		KEY_PIC_PATH = "pref_pic_path" + numberOfAnni_String;
		KEY_SHARETEXT = "pref_sharetext" + numberOfAnni_String;
		KEY_SETDEFAULTMESSAGE = "pref_setDefaultMessage" + numberOfAnni_String;
	}
	
	public void putDataToSharedPreferences(Context context, String name, int mode) {
		setKeys();
		
		SharedPreferences prefs = context.getSharedPreferences(name, mode);
		
		prefs.edit().putString(KEY_TITLE, title).commit();
		prefs.edit().putString(KEY_OLDDATE, oldDate).commit();
		prefs.edit().putString(KEY_OLDTIME, oldTime).commit();
		prefs.edit().putString(KEY_OLDTIMEZONELIST, oldTimeZone).commit();
		prefs.edit().putBoolean(KEY_SETTIMEZONETOCURRENT, setTimeZoneToCurrent).commit();
		prefs.edit().putBoolean(KEY_SETTIMEZONEMANUALLY, setTimeZoneManually).commit();
		prefs.edit().putString(KEY_PIC_PATH, picPath).commit();
		prefs.edit().putString(KEY_SHARETEXT, shareText).commit();
		prefs.edit().putBoolean(KEY_SETDEFAULTMESSAGE, setDefaultMessage).commit();
		
		oldDateToMillis(context);
		
	}
	
	public void getDataFromSharedPreferences(Context context, String name, int mode) {
		setKeys();
		
		SharedPreferences prefs = context.getSharedPreferences(name, mode);
		
		title = prefs.getString(KEY_TITLE, "");
		oldDate = prefs.getString(KEY_OLDDATE, "");
		oldTime = prefs.getString(KEY_OLDTIME, "");
		oldTimeZone = prefs.getString(KEY_OLDTIMEZONELIST, "");
		setTimeZoneToCurrent = prefs.getBoolean(KEY_SETTIMEZONETOCURRENT,false);
		setTimeZoneManually = prefs.getBoolean(KEY_SETTIMEZONEMANUALLY,false);
		picPath = prefs.getString(KEY_PIC_PATH, "");
		shareText = prefs.getString(KEY_SHARETEXT, "");
		setDefaultMessage = prefs.getBoolean(KEY_SETDEFAULTMESSAGE,false);
		
		oldDateToMillis(context);
	}
	
    @SuppressLint("SimpleDateFormat")
	public void oldDateToMillis(Context context) {
        if (oldDate==null){
        	oldDate = "01.01.2000";
        }     
        if (oldTime==null){
        	oldTime = "00:00";
        }
    	
    	Calendar cal = Calendar.getInstance();
    	long timeInMillis = 0;
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");

        Date convertedDate;
        TimeZone tz;
      
        try {
        tz = TimeZone.getTimeZone(oldTimeZone);
        dateFormat.setTimeZone(tz);
        convertedDate = dateFormat.parse(oldDate + ", " + oldTime +":00");
        cal.setTime(convertedDate);
        timeInMillis = cal.getTimeInMillis();

        } catch (ParseException e) {
        e.printStackTrace();
        }
        oldDateTimeInMillis = timeInMillis;
    }
    
    public void calcAnnis(Context context){
    	
    	sortedMap.clear();
    	   
    			LocalDateTime startA = new LocalDateTime(oldDateTimeInMillis);
    	        LocalDateTime startB = new LocalDateTime(System.currentTimeMillis());
    	        LocalDateTime enddate = startA.plusYears(140);
    	        
//    	        startB = startA; //show all Dates

    			LinkedHashMap<String,LocalDateTime> out= new LinkedHashMap<String,LocalDateTime>();
    			
    	// Format für Ausgabe der weiteren Jahrestage
    			
    	        DecimalFormat df =   new DecimalFormat  ( ",##0" );
    	        

    	// ab hier werden alle Jahrestage berechnet
    	        // weitere Jahrestage berechnen: Jahre
    	        String unit = context.getResources().getString(R.string.years);
//    			for (int i=0; i< years.length; i++){
    				for (int j=1; j<=140; j++) {
//    					int year = years[i] * j;
    					int year = j;
    					
    					// plus
    					LocalDateTime newdate = startA.plusYears(year);
    					String anniversary = df.format(year) + " " + unit;
    					if (newdate.isAfter(startB) && newdate.isBefore(enddate)) {
    						out.put(anniversary, newdate);
    					}
    					
    					// minus
    					newdate = startA.minusYears(year);
    				    anniversary = context.getResources().getString(R.string.noch)+" " + df.format(year) + " " + unit;
    					if (newdate.isAfter(startB) && newdate.isBefore(startA)) {
    						out.put(anniversary, newdate);
    					}
    				}
//    			}
    			
    			// weitere Jahrestage berechnen: Monate
    			unit = context.getResources().getString(R.string.months);
    			for (int i=0; i< months.length; i++){
    				for (int j=1; j<=9; j++) {
    					int month = months[i] * j;
    					
    					// plus
    					LocalDateTime newdate = startA.plusMonths(month);
    					if (newdate.isAfter(startB) && newdate.isBefore(enddate)) {
    						out.put(df.format(month) + " " + unit, newdate);
    					}
    					
    					// minus
    					newdate = startA.minusMonths(month);
    					if (newdate.isAfter(startB) && newdate.isBefore(startA)) {
    						out.put(context.getResources().getString(R.string.noch)+" " + df.format(month) + " " + unit, newdate);
    					}
    				}
    			}
    			
    			// weitere Jahrestage berechnen: Wochen
    			unit = context.getResources().getString(R.string.weeks);
    			for (int i=0; i< weeks.length; i++){
    				for (int j=1; j<=9; j++) {
    					int week = weeks[i] * j;
    					
    					// plus
    					LocalDateTime newdate = startA.plusWeeks(week);
    					if (newdate.isAfter(startB) && newdate.isBefore(enddate)) {
    						out.put(df.format(week) + " " + unit, newdate);
    					}
    					
    					// minus
    					newdate = startA.minusWeeks(week);
    					if (newdate.isAfter(startB) && newdate.isBefore(startA)) {
    						out.put(context.getResources().getString(R.string.noch)+" " + df.format(week) + " " + unit, newdate);
    					}
    				}
    			}
    			
    			// weitere Jahrestage berechnen: Tage
    			unit = context.getResources().getString(R.string.days);
    			for (int i=0; i< days.length; i++){
    				for (int j=1; j<=9; j++) {
    					int day = days[i] * j;
    					
    					// plus
    					LocalDateTime newdate = startA.plusDays(day);
    					if (newdate.isAfter(startB) && newdate.isBefore(enddate)) {
    						out.put(df.format(day) + " " + unit, newdate);
    					}
    					
    					// minus
    					newdate = startA.minusDays(day);
    					if (newdate.isAfter(startB) && newdate.isBefore(startA)) {
    						out.put(context.getResources().getString(R.string.noch)+" " + df.format(day) + " " + unit, newdate);
    					}
    				}
    			}
    			
    			// weitere Jahrestage berechnen: 
    			unit = context.getResources().getString(R.string.hours);
    			for (int i=0; i< hours.length; i++){
    				for (int j=1; j<=9; j++) {
    					int hour = hours[i] * j;
    					
    					// plus
    					LocalDateTime newdate = startA.plusHours(hour);
    					if (newdate.isAfter(startB) && newdate.isBefore(enddate)) {
    						out.put(df.format(hour) + " " + unit, newdate);
    					}
    					
    					// minus
    					newdate = startA.minusHours(hour);
    					if (newdate.isAfter(startB) && newdate.isBefore(startA)) {
    						out.put(context.getResources().getString(R.string.noch)+" " + df.format(hour) + " " + unit, newdate);
    					}
    				}
    			}
    			
    			unit = context.getResources().getString(R.string.minutes);
    			for (int i=0; i< minutes.length; i++){
    				for (int j=1; j<=9; j++) {
    					int minute = minutes[i] * j;
    					
    					// plus
    					LocalDateTime newdate = startA.plusMinutes(minute);
    					if (newdate.isAfter(startB) && newdate.isBefore(enddate)) {
    						out.put(df.format(minute) + " " + unit, newdate);
    					}
    					
    					// minus
    					newdate = startA.minusMinutes(minute);
    					if (newdate.isAfter(startB) && newdate.isBefore(startA)) {
    						out.put(context.getResources().getString(R.string.noch)+" " + df.format(minute) + " " + unit, newdate);
    					}
    				}
    			}
    			
    			unit = context.getResources().getString(R.string.seconds);
    			for (int i=0; i< seconds.length; i++){
    				for (int j=1; j<=9; j++) {
    					long second = seconds[i] * j;
    					
    					// plus
    					if (second <= Integer.MAX_VALUE) {
    						LocalDateTime newdate = startA.plusSeconds((int) second);
    						if (newdate.isAfter(startB) && newdate.isBefore(enddate)) {
    							out.put(df.format(second) + " " + unit, newdate);
    						}	
    					} else {
    						if (second < 2l * Integer.MAX_VALUE){
    							LocalDateTime newdate = startA.plusSeconds(Integer.MAX_VALUE);
    							newdate = newdate.plusSeconds((int) (second - Integer.MAX_VALUE));
    							if (newdate.isAfter(startB) && newdate.isBefore(enddate)) {
    								out.put(df.format(second) + " " + unit, newdate);
    							}
    						}
    						
    					}
    					
    					// minus
    					if (second <= Integer.MAX_VALUE) {
    						LocalDateTime newdate = startA.minusSeconds((int) second);
    						if (newdate.isAfter(startB) && newdate.isBefore(startA)) {
    							out.put(context.getResources().getString(R.string.noch)+" " + df.format(second) + " " + unit, newdate);
    						}	
    					} else {
    						if (second < 2l * Integer.MAX_VALUE){
    							LocalDateTime newdate = startA.minusSeconds(Integer.MAX_VALUE);
    							newdate = newdate.minusSeconds((int) (second - Integer.MAX_VALUE));
    							if (newdate.isAfter(startB) && newdate.isBefore(startA)) {
    								out.put(context.getResources().getString(R.string.noch)+" " + df.format(second) + " " + unit, newdate);
    							}
    						}
    						
    					}
    		
    				}	
    			}
    			
    			// LinkedHashMap nach value (Datum) sortieren
    			List<Map.Entry<String, LocalDateTime>> entries =
    					new ArrayList<Map.Entry<String,LocalDateTime>>(out.entrySet());
    			Collections.sort(entries, new Comparator<Map.Entry<String,LocalDateTime>>() {
    				public int compare(Map.Entry<String,LocalDateTime> a, Map.Entry<String,LocalDateTime> b){
    					return a.getValue().compareTo(b.getValue());
    				}
    			});
//    			Map<String,LocalDateTime> sortedMap = new LinkedHashMap<String,LocalDateTime>();
    			for (Map.Entry<String,LocalDateTime> entry : entries) {
    				sortedMap.put(entry.getKey(), entry.getValue());
    			}
    			
    }
}
