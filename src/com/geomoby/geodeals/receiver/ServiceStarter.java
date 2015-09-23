package com.geomoby.geodeals.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.geomoby.GeoMoby;
import com.geomoby.geodeals.DemoService;

public class ServiceStarter extends BroadcastReceiver {

	private static final String TAG = "GeoMoby ServiceStarter";
	private static final String PREF_NAME = "checked";
	
	private static SharedPreferences spref;

    @Override
    public void onReceive(Context context, Intent intent) {    	
    	spref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    	
    	// User privacy - Restart the service only if the service was activated before the device reboot
		if ( spref.getBoolean("check",false) == true){
			Log.d(TAG, "Restart the GeoMoby service...");
			
			// Initalise the GeoMoby tracking service
			GeoMoby.init(context);
			
			// Apply a new configuration if needed
			//setGMConfiguration();
			
			// Start GeoMoby Location Service
			GeoMoby.start(); 
		}
		
    	Log.d(TAG, "Reboot completed.");
    }

    /**
     * You can use the pre-existing configuration or restart with fresh values
     * 
     */
	private void setGMConfiguration() {
	
		/*
		 * Set your GeoMoby Api Key (required)
		 */
		GeoMoby.setApiKey(spref.getString("apiKey", ""));
		
		/*
		 *  Save the tags in the GeoMoby shared preferences in private mode for the user. These tags will be used
		 *  to segment your audience when creating your proximity alerts. Please make sure that they match with
		 *  the ones configured in your dashboard when you create an alert.
		 *  Ex: 'test' is the default tag so make sure that it is set up in your Account page
		 *
		 * Build the string of tags - empty for testing. Make sure that you create your first geofences with no tags in your dashboard.
		 * Add your own logic here: "male,vip,monday"...
		 */
		String tags = "";
		GeoMoby.setTags(tags);
		
		/*
		 *  Filter events based on users activity (still,walking,cycling,driving,tilting - default:walking - debug:tilting)
		 *  You can also filter several activities using '|' as a separator (tilting|walking)
		 */  
		String motion_filter = "walking|tilting";
		GeoMoby.setMotionFilter(motion_filter);
		
		/*
		 *  This setting corresponds to the minimum time interval between 2 GeoMoby service calls (in seconds) - Recommended 60s.
		 */
		String updateIntervalSeconds = "60";
		GeoMoby.setUpdateInterval(updateIntervalSeconds);

		/*
		 *  Silence Time is the time window when no notifications can be sent (24 hour)
		 */
		String silence_start = "00";
		String silence_stop = "06";
		GeoMoby.setSilenceTimeStart(silence_start);
		GeoMoby.setSilenceTimeStop(silence_stop);


		/* Turn development mode on and off (yes/no)
		* dev_mode=true consumes a bit more battery 
		* dev_mode=false is the production mode as it gets the most out of our optimised battery management
		*/
		String dev_mode = "true";
		GeoMoby.setDevMode(dev_mode);
		
		/*
		*  Set the GeoMoby Outdoor Location service - 5-10m accuracy outdoors and about 20m indoors (no iBeacons needed)
		*/
		if(Boolean.valueOf(spref.getString("outdoor", "false"))){
			GeoMoby.setOutdoorLocationService("true");
		}else{
			GeoMoby.setOutdoorLocationService("false");
		}
		
		
		/*
		*  Set the GeoMoby iBeacon Location service
		*
		*  You can set both indoor and outdoor to "true" for a end-to-end location monitoring experience
		*/
		if(Boolean.valueOf(spref.getString("indoor", "false"))){
			GeoMoby.setIndoorLocationService("true");
			GeoMoby.setUUID(spref.getString("uuid", ""));
		}else{
			GeoMoby.setIndoorLocationService("false");
		}
		
	}
}
