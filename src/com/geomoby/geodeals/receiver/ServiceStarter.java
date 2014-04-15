package com.geomoby.geodeals.receiver;

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
		editor.putBoolean("check", false);
		editor.commit();
    }
}
