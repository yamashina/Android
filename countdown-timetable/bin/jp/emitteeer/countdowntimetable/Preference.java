package jp.emitteeer.countdowntimetable;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.WindowManager;

public class Preference extends PreferenceActivity {
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);

        // Preference‚Ìæ“¾•”½‰f
        preferenceSetting();

		// Activity‚Ìƒ^ƒCƒgƒ‹‚ğİ’è‚·‚é
		this.setTitle("Preference");
	}

    @Override
    protected void onResume() {
    	super.onResume();
    	
        // Preference‚Ìæ“¾•”½‰f
        preferenceSetting();
    }
    
	public static boolean isAutoChangeScreenOrientation(Context c) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
		return pref.getBoolean("auto_change_screen_orientation", false);
	}
	
	public static boolean isKeepScreenOn(Context c) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
		return pref.getBoolean("keep_screen_on", false);
	}
    
    // Preference‚Ìæ“¾•”½‰f
    private void preferenceSetting() {
        boolean autoChangeScreen = Preference.isAutoChangeScreenOrientation(getBaseContext());
        if (autoChangeScreen) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        
        boolean keepScreen = Preference.isKeepScreenOn(getBaseContext());
        if (keepScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}