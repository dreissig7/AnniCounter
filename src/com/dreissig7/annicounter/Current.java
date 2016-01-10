package com.dreissig7.annicounter;

import java.io.File;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.dreissig7.annicounter.R;

@SuppressLint("SimpleDateFormat") 
public class Current extends Activity {

	public static int numberOfAnni;
    private Anni anni = new Anni();

    private Handler mHandler = new Handler();
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current);
		setupActionBar();
		
//		delay counter, prevents bitmap from being created too early
        count = 0;
        
//        get number of anni pressed in main activity's listview
        Intent intent = getIntent();
        numberOfAnni = intent.getIntExtra(MainActivity.EXTRA_ANNI, 0);
        anni.numberOfAnni = numberOfAnni;
        anni.getDataFromSharedPreferences(getApplicationContext(), MainActivity.PREFS, MainActivity.MODE);
        
//        if viewed first time, set time zone to current
        if(!anni.setTimeZoneManually){          
        	String tz = TimeZone.getDefault().getID();
        	anni.oldTimeZone = tz;
        	anni.setTimeZoneManually = true;
        	anni.putDataToSharedPreferences(getApplicationContext(), MainActivity.PREFS, MainActivity.MODE);
        }
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
  
    private Runnable updateTask = new Runnable () {
        public void run() {
        				count += 1;
        				if (count == 2){
        					LoadPic(100,100);
        				}
                        // run any code here...         
            			Refresh();
                        // queue the task to run again in 500 ms
                        mHandler.postDelayed(updateTask, 500);

        }
    };
    
    @Override
    protected void onPause(){
		super.onPause();
    	mHandler.removeCallbacks(updateTask);
    }
    
    @Override
    protected void onResume(){
    	super.onResume();

        // change title according to preferences
        anni.getDataFromSharedPreferences(getApplicationContext(), MainActivity.PREFS, MainActivity.MODE);
        this.setTitle(anni.title);
        
        LoadPic(100,100);
    	
    	updateTask.run();
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.current, menu);
        return true;
    }
       
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
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
        		sendIntent.putExtra(Intent.EXTRA_TEXT, Refresh());
        		sendIntent.setType("text/plain");
        		startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share)));
        		return true;
  
        	case R.id.nextAnnis:
        		startNextAnnis();
        		return true;
        		
            case R.id.action_settings:
            	Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
    	        intent.putExtra(MainActivity.EXTRA_ANNI, numberOfAnni);
    	        startActivity(intent);
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void startNextAnnis() {
        Intent intent = new Intent(this, NextAnnis.class);
        intent.putExtra(MainActivity.EXTRA_ANNI, numberOfAnni);
        startActivity(intent);
    }

    public static Map<Integer, Long> getDateTimeDiffMap(long timeInMillA) {

        
        Map<Integer,Long> out = new LinkedHashMap<Integer, Long>();

        long timeInMillB = System.currentTimeMillis();

        LocalDateTime startA = new LocalDateTime(timeInMillA);
        LocalDateTime startB = new LocalDateTime(timeInMillB);
        LocalDateTime startA2 = new LocalDateTime(0);

        Period difference = new Period(startA, startB, PeriodType.days());
        long day = difference.getDays();

        difference = new Period(startA, startB, PeriodType.months());
        long month = difference.getMonths();

        difference = new Period(startA, startB, PeriodType.years());
        long year = difference.getYears();

        difference = new Period(startA, startB, PeriodType.weeks());
        long week = difference.getWeeks();

        difference = new Period(startA, startB, PeriodType.hours());
        long hour = difference.getHours();

        difference = new Period(startA, startB, PeriodType.minutes());
        long min = difference.getMinutes();

        //difference = new Period(startA, startB, PeriodType.millis());
        long mili = timeInMillB - timeInMillA;
        
        long sec;
        if (mili < ((long)Integer.MAX_VALUE * 1000)) {
        	difference = new Period(startA, startB, PeriodType.seconds());
        	sec = difference.getSeconds();
        } else {
        	startA2 = startA.plusSeconds(Integer.MAX_VALUE);
        	difference = new Period(startA2, startB, PeriodType.seconds());
        	sec = difference.getSeconds() + (long)Integer.MAX_VALUE;
        }

        out.put(7, mili);
        out.put(6, sec);
        out.put(5, min);
        out.put(4, hour);
        out.put(3, day);
        out.put(2, week);
        out.put(1, month);
        out.put(0, year);      

        return out;
    }
    
    
    public String Refresh() {
        TextView years_out= (TextView) findViewById(R.id.years_out);
        TextView months_out= (TextView) findViewById(R.id.months_out);
        TextView weeks_out= (TextView) findViewById(R.id.weeks_out);
        TextView days_out= (TextView) findViewById(R.id.days_out);
        TextView hours_out= (TextView) findViewById(R.id.hours_out);
        TextView minutes_out= (TextView) findViewById(R.id.minutes_out);
        TextView seconds_out= (TextView) findViewById(R.id.seconds_out);
        
        long oldDateTime = anni.oldDateTimeInMillis;
        
        Map<Integer,Long> out = getDateTimeDiffMap(oldDateTime);
        
        DecimalFormat df =   new DecimalFormat  ( ",##0" );

        String years = df.format(out.get(0));
        String months = df.format(out.get(1));
        String weeks = df.format(out.get(2));
        String days = df.format(out.get(3));
        String hours = df.format(out.get(4));
        String minutes = df.format(out.get(5));
        String seconds = df.format(out.get(6));
        String title = anni.title;
        
        years_out.setText(years);
        months_out.setText(months);
        weeks_out.setText(weeks);
        days_out.setText(days);
        hours_out.setText(hours);
        minutes_out.setText(minutes);
        seconds_out.setText(seconds);
        
        String text = String.format(anni.shareText + "\n\n("+getResources().getString(R.string.pref_oldTimeZone_title)+": "+String.valueOf(TimeZone.getDefault().getID())+")"+getResources().getString(R.string.signature), title, years, months, days, hours, minutes, seconds);
        
        return text;

    }
    
	public void LoadPic(int imageHeight, int imageWidth) {
        // String picturePath contains the path of selected Image
        String picturePath = anni.picPath;
//        if (picturePath.contains("storage/emulated/0")){
//        	picturePath = picturePath.replace("storage/emulated/0", "storage/emulated/legacy");
//        }
        File file = new File(picturePath);
        if(file.exists()) {
        	ImageView pic = (ImageView) findViewById(R.id.imageView1);
        	int PimageHeight = pic.getHeight();
        	int PimageWidth = pic.getWidth();
       	
//            Log.i(TAG, "pic(W,H): (W "+ String.valueOf(PimageWidth) + " , H " + String.valueOf(PimageHeight) + "; Übergabe:" + String.valueOf(imageWidth) + String.valueOf(imageHeight));
            
            if (PimageHeight > 0 && PimageWidth > 0) {
            	imageHeight = PimageHeight;
            	imageWidth = PimageWidth;
            }
            
        	Bitmap bitmap = decodeSampledBitmapFromFile(picturePath, imageHeight, imageWidth);

               try {
                   ExifInterface exif = new ExifInterface(picturePath);
                   int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
//                   Log.d("EXIF", "Exif: " + orientation);
                   Matrix matrix = new Matrix();
                   if (orientation == 6) {
                       matrix.postRotate(90);
                   }
                   else if (orientation == 3) {
                       matrix.postRotate(180);
                   }
                   else if (orientation == 8) {
                       matrix.postRotate(270);
                   }
                   bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
               }
               catch (Exception e) {
            	   Log.e("Exception found ",e.getMessage());
               }
        	
        	
        	pic.setImageBitmap(bitmap);
        	pic.setScaleType(ImageView.ScaleType.FIT_CENTER);
        	
        }
        else{
        	Log.d("com.dreissig7.annicounter","File "+picturePath+ file.exists()+" , "+ Environment.getExternalStorageDirectory());
        }
        
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    	//Raw height and width of image
    	final int height = options.outHeight;
    	final int width = options.outWidth;
    	int inSampleSize = 1;

    	if (height > reqHeight || width > reqWidth) {

    		// Calculate ratios of height and width to requested height and width
    		final int heightRatio = Math.round((float) height / (float) reqHeight);
    		final int widthRatio = Math.round((float) width / (float) reqWidth);

    		// Choose the smallest ratio as inSampleSize value, this will guarantee
    		// a final image with both dimensions larger than or equal to the
    		// requested height and width.
    		inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    	}

    	return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
    	// First decode with inJustDecodeBounds=true to check dimensions
    	final BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inJustDecodeBounds = true;
    	BitmapFactory.decodeFile(path, options);

    	// Calculate inSampleSize
    	options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    	
    	// Decode bitmap with inSampleSize set
    	options.inJustDecodeBounds = false;
    	return BitmapFactory.decodeFile(path, options);
    }

    
}
