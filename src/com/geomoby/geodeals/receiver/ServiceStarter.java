package com.geomoby.geodeals.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.geomoby.GeoMoby;
import com.geomoby.geodeals.DemoService;

public class ServiceStarter extends BroadcastReceiver {

	final String PREF_NAME="checked";

    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	SharedPreferences spref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

    	Log.d("ServiceStarter", "Reboot completed.");
    	
    	// User privacy - Restart the service only if the service was activated before the device reboot
		if ( spref.getBoolean("check",false) == true){
			Log.d("ServiceStarter", "Restart the GeoMoby service...");
			// Start the GeoMoby tracking service
		
			GeoMoby.init(context);
			
			GeoMoby.start(); // Restart GeoMoby Location Service
		}
    }
}
