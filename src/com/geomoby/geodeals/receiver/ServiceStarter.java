package com.geomoby.geodeals.receiver;

import com.geomoby.logic.GeomobyStartService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class ServiceStarter extends BroadcastReceiver {

	final String PREF_NAME="checked";

    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	Log.d("ServiceStarter", "Resetting ON/OFF button...");
    	
    	SharedPreferences spref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = spref.edit();
		editor.putBoolean("check", true);
		editor.commit();
		
		// Start the GeoMoby tracking service
    	context.startService(new Intent(context, GeomobyStartService.class));
    }
}
