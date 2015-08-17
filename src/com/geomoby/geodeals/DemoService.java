/**
 * GeoMoby Pty Ltd
 * http://www.geomoby.com
 * Copyright GeoMoby Pty Ltd 2013
 * 
 * @author Chris Baudia
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.geomoby.geodeals;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.geomoby.GeoMoby;

public class DemoService extends Activity {

	private static final String TAG = "** Demo Service **";
	private static final String PREF_NAME="checked";
	private static SharedPreferences spref;

	boolean isCheckedStatus;
	CompoundButton mToggle;
	public Context mContext;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geomoby_main);
		mContext = this;

		mToggle = (CompoundButton) findViewById(R.id.togglebutton); 

		GeoMoby.init(this);

		/*
		 * Set your GeoMoby Api Key (required)
		 */
		String api_key="your-api-key";
		GeoMoby.setApiKey(api_key);

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
		String motion_filter = "walking|still";
		GeoMoby.setMotionFilter(motion_filter);

		/*
		 *  This setting corresponds to the minimum time interval between 2 GeoMoby service calls (in seconds) - Recommended 60s.
		 */
		String updateIntervalSeconds = "60";
		GeoMoby.setUpdateInterval(updateIntervalSeconds);

		/*
		 *  Silence Time is the time window when no notifications can be sent (24 hour)
		 */
		String silence_start = "01";
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
		final String outdoor = "true";
		GeoMoby.setOutdoorLocationService(outdoor);

		/*
		 *  Set the GeoMoby iBeacon Location service
		 *
		 *  You can set both indoor and outdoor to "true" for a end-to-end monitoring experience
		 */
		final String indoor = "false";
		GeoMoby.setIndoorLocationService(indoor);
		GeoMoby.setUUID("12345678-905F-4436-91F8-E602F514C96D");


		spref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		isCheckedStatus = spref.getBoolean("check", false);  //default is false

		/*
		 * Set up toggle status
		 */
		if (!isCheckedStatus){
			mToggle.setChecked(false);
		}else{
			mToggle.setChecked(true);
		}

		/*
		 *  Monitor the toggle - Our SDK will ensure that all services are running/stopping properly
		 */
		mToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked == false) {

					mToggle.setPressed(false);

					// Stop the GeoMoby tracking service
					GeoMoby.stop();

					SharedPreferences.Editor editor = spref.edit();
					editor.putBoolean("check", false);
					editor.commit();

				}else{

					if(indoor.contains("true")){
						if (android.os.Build.VERSION.SDK_INT >= 18){
							if(!checkBlueToothLEAvailability()) {
								Log.w(TAG,"Bluetooth not activated!");
								Toast.makeText(getApplicationContext(), "Please, activate your Bluetooth",Toast.LENGTH_LONG).show();
								return;
							}
						}else{
							Log.w(TAG,"Your phone is not compatible with Bluetooth LTE");
							Toast.makeText(getApplicationContext(), "Sorry, your phone is not compatible with Bluetooth LTE",Toast.LENGTH_LONG).show();
							return;
						}
					}

					mToggle.setPressed(true);

					// Start the GeoMoby tracking service
					GeoMoby.start();

					SharedPreferences.Editor editor = spref.edit();
					editor.putBoolean("check", true);
					editor.commit();

					LayoutInflater inflater = getLayoutInflater();
					// Inflate the Layout
					View layout = inflater.inflate(R.layout.geomoby_toast, (ViewGroup) findViewById(R.id.custom_toast_layout));

					// Set the Text to show in TextView
					TextView text = (TextView) layout.findViewById(R.id.textToShow);
					text.setText("GREAT! YOU ARE READY TO RECEIVE REAL-TIME NOTIFICATIONS!");
					Typeface face;
					face = Typeface.createFromAsset(getAssets(), "Bitter-Bold.otf");
					text.setTypeface(face);

					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.BOTTOM, 0, 50);
					toast.setDuration(Toast.LENGTH_LONG);
					toast.setView(layout);
					toast.show();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		onDestroy();
	}	

	/**
	 * Check if Bluetooth LE is supported by this Android device, and if so, make sure it is enabled.
	 * Throws a RuntimeException if Bluetooth LE is not supported.  (Note: The Android emulator will do this)
	 * @return false if it is supported and not enabled
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public boolean checkBlueToothLEAvailability() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Log.e(TAG,"Bluetooth LE not supported by this device");  
		}		
		else {
			if (((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().isEnabled()){
				return true;
			}
		}	
		return false;
	}
}
