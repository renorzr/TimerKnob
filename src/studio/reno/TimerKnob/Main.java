package studio.reno.TimerKnob;

import studio.reno.TimerKnob.Knob.Action;
import studio.reno.TimerKnob.TimerKnob.OnRotateListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.admob.android.ads.AdListener;
import com.admob.android.ads.AdManager;
import com.admob.android.ads.AdView;

public class Main extends Activity {
	TextView indicator;
	TimerKnob knob;
	private int lastSeconds;
	Menu menu;
	boolean enableTick = true;
	private boolean preferencesInitialized = false;
	
	public Main(){
//		AdManager.setTestDevices( new String[] { AdManager.TEST_EMULATOR, "04F91B76A0E38C4499457AAF50238703" } );
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        knob = new TimerKnob(this);
        knob.addTo((ViewGroup) findViewById(R.id.main));
        knob.setOnRotateListener(new OnRotateListener(){
			@Override
			public void onRotating(Action action, float degrees, float value) {
				switch (action){
				case END:
					if (!preferencesInitialized) initPrefs();
					updateLastSeconds((int)(value*60));
					break;
				}
			}        	
        });

        AdView ad = ( AdView ) this.findViewById ( R.id.ad );
        if ( ad != null ) {
            ad.setVisibility ( View.VISIBLE );
//            ad.setAdListener ( new AdListener (){
//
//				@Override
//				public void onFailedToReceiveAd(AdView arg0) {
//					Log.d("AD", "failed to receive ad");
//				}
//
//				@Override
//				public void onFailedToReceiveRefreshedAd(AdView arg0) {
//					Log.d("AD", "failed to receive refreshed ad");
//				}
//
//				@Override
//				public void onReceiveAd(AdView arg0) {
//					Log.d("AD", "receive ad");
//				}
//
//				@Override
//				public void onReceiveRefreshedAd(AdView arg0) {
//					Log.d("AD", "receive refreshed ad");
//				}
//            	
//            });
        }
    }
    
    @Override
    public void onDestroy(){
    	knob.close();
    	super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        initPrefs();

		return true;
    }

	private void initPrefs() {
		preferencesInitialized = true;
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        enableTick(prefs.getBoolean("enableTick", true));
        updateLastSeconds(prefs.getInt("lastSeconds", 30*60));
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()){
    	case R.id.tick:
    		enableTick(!enableTick);
    		return true;
    	case R.id.last:
    		knob.setSeconds(lastSeconds);
    		return true;
    	case R.id.zero:
    		knob.setSeconds(0);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }

	private void enableTick(boolean enable) {
		enableTick = enable;
		knob.enableTick(enable);
		SharedPreferences.Editor edit = getPreferences(MODE_PRIVATE).edit();
		edit.putBoolean("enableTick", enable);
		edit.commit();
	}

	private void updateLastSeconds(int sec) {
		if (sec<=0) return;
		SharedPreferences.Editor edit = getPreferences(MODE_PRIVATE).edit();
		edit.putInt("lastSeconds", sec);
		edit.commit();

		lastSeconds = sec;
		
		if (menu!=null){
			if (sec>0) {
				menu.findItem(R.id.last).setTitle(toText(sec));
			} else {
				menu.findItem(R.id.last).setEnabled(false);
			}
		}
	}

	public static CharSequence toText(int leftSeconds) {
		int hr = leftSeconds/3600;
		int min = (leftSeconds-hr*3600)/60;
		int sec = leftSeconds-min*60-hr*3600;
		
		return hr<1 ? String.format("%02d:%02d", min, sec):String.format("%02d:%02d:%02d", hr, min, sec);
	}
}