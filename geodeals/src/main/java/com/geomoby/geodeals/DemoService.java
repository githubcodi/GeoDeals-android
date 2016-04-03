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

import com.geomoby.GeoMoby;

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
import android.view.inputmethod.EditorInfo;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class DemoService extends Activity {

	private static final String TAG = "** GeoMoby Demo Service **";
	private static final String PREF_NAME="checked";
	private static SharedPreferences spref;
	public Context mContext;

	boolean isCheckedStatus;
	Switch mToggle;
	EditText mApiKey;
	EditText mUuid;
	CheckedTextView mOutdoor;
	CheckedTextView mIndoor;
	LinearLayout mUuidLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geomoby_main);
		mContext = this;

		// Initialise The GeoMoby SDK
		GeoMoby.init(this);

		// Get Android components
		mApiKey = (EditText) findViewById(R.id.et_apiKey);
		mApiKey.setImeOptions(EditorInfo.IME_ACTION_DONE);
		mOutdoor = (CheckedTextView) findViewById(R.id.ctv_outdoor);
		mIndoor = (CheckedTextView) findViewById(R.id.ctv_indoor);
		mToggle = (Switch) findViewById(R.id.togglebutton);
		mUuidLayout = (LinearLayout) findViewById(R.id.ll_uuid);
		mUuid = (EditText) findViewById(R.id.et_uuid);

		// Retrieve data stored in SharedPreferences or apply default values
		spref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		isCheckedStatus = spref.getBoolean("check", false);  //default is false
		mApiKey.setText(spref.getString("apiKey", ""));
		mOutdoor.setChecked(Boolean.valueOf(spref.getString("outdoor", "false")));
		mIndoor.setChecked(Boolean.valueOf(spref.getString("indoor", "false")));
		mUuid.setText(spref.getString("uuid", ""));

		// Set up an onClick listener on the outdoor checkbox
		mOutdoor.setOnClickListener(new View.OnClickListener() { 
			public void onClick(View v) { 
				mOutdoor.toggle(); 
			} 
		});

		// Set up an onClick listener on the indoor checkbox
		mIndoor.setOnClickListener(new View.OnClickListener() { 
			public void onClick(View v) 
			{ 
				mIndoor.toggle(); 
				if(mIndoor.isChecked()){
					mUuidLayout.setVisibility(View.VISIBLE);
				}else
					mUuidLayout.setVisibility(View.GONE);
			} 
		}); 

		/*
		 * Set up the toggle status
		 */
		 if (!isCheckedStatus){
			 mToggle.setChecked(false);
			 mApiKey.setEnabled(true);
			 mIndoor.setEnabled(true);
			 mOutdoor.setEnabled(true);
		 }else{
			 mToggle.setChecked(true);
			 mApiKey.setEnabled(false);
			 mIndoor.setEnabled(false);
			 mOutdoor.setEnabled(false);
		 }

		 /*
		  *  Monitor the toggle - Our SDK will ensure that all services are running/stopping properly
		  */
		 mToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				 if( isValidForm() ){

					 // Switching OFF the GeoMoby location service
					 if (isChecked == false) {
						 // Store the status into SharedPreferences
						 SharedPreferences.Editor editor = spref.edit();
						 editor.putBoolean("check", false);
						 editor.commit();

						// Set the Android components
						 mToggle.setChecked(false);
						 mApiKey.setEnabled(true);
						 mIndoor.setEnabled(true);
						 mOutdoor.setEnabled(true);

						 // Stop the GeoMoby tracking service
						 GeoMoby.stop();

					 // Switching ON the GeoMoby location service
					 }else{

						 if(mIndoor.isChecked()){
							 if (android.os.Build.VERSION.SDK_INT >= 18){
								 if(!checkBlueToothLEAvailability()) {
									 Log.w(TAG,"Bluetooth not activated!");
									 Toast.makeText(getApplicationContext(), "Please, activate your Bluetooth!",Toast.LENGTH_LONG).show();
									 mToggle.setChecked(false);
									 return;
								 }
							 }else{
								 Log.w(TAG,"Your phone is not compatible with Bluetooth LTE");
								 Toast.makeText(getApplicationContext(), "Sorry, your phone is not compatible with Bluetooth LTE...",Toast.LENGTH_LONG).show();
								 mToggle.setChecked(false);
								 return;
							 }
						 }

						 // Store into SharedPrefs
						 SharedPreferences.Editor editor = spref.edit();
						 editor.putBoolean("check", true);
						 editor.commit();
						 
						 // Set the Android components
						 mToggle.setChecked(true);
						 mApiKey.setEnabled(false);
						 mIndoor.setEnabled(false);
						 mOutdoor.setEnabled(false);

						 // Initialise the GeoMoby configuration
						 setGMConfiguration();

						 // Start the GeoMoby tracking service
						 GeoMoby.start();

						 /*
						  *  Create a Toast message when location service has started
						  */
						 LayoutInflater inflater = getLayoutInflater();
						 // Inflate the Layout
						 View layout = inflater.inflate(R.layout.geomoby_toast, (ViewGroup) findViewById(R.id.custom_toast_layout));
						 // Set the Text to show in TextView
						 TextView text = (TextView) layout.findViewById(R.id.textToShow);
						 text.setText("GREAT! YOU ARE READY TO RECEIVE REAL-TIME NOTIFICATIONS!");
						 Typeface face;
						 face = Typeface.createFromAsset(getAssets(), "Bitter-Bold.otf");
						 text.setTypeface(face);
						 // Display the Toast message
						 Toast toast = new Toast(getApplicationContext());
						 toast.setGravity(Gravity.BOTTOM, 0, 50);
						 toast.setDuration(Toast.LENGTH_LONG);
						 toast.setView(layout);
						 toast.show();
					 }
				 }else
					 mToggle.setChecked(false);
			 }

		 });
	}

	/**
	 * Set up the GeoMoby configuration
	 * 
	 */
	protected void setGMConfiguration() {
		/*
		 * Set your GeoMoby Api Key (required)
		 */
		String api_key= mApiKey.getText().toString();
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
		 *  Filter events based on users activity (still,walking,cycling,running,driving,tilting - default:walking - debug:still)
		 *  You can also filter several activities using '|' as a separator (tilting|walking)
		 */  
		String motion_filter = "walking|tilting|still|unknown";
		GeoMoby.setMotionFilter(motion_filter);

		/*
		 *  This setting corresponds to the average time interval between 2 GeoMoby service calls (in seconds) - Recommended 60s.
		 */
		String updateIntervalSeconds = "30";
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
		String outdoor;
		if(mOutdoor.isChecked()){
			GeoMoby.setOutdoorLocationService("true");
			outdoor = "true";
		}else{
			GeoMoby.setOutdoorLocationService("false");
			outdoor = "false";
		}

		/*
		 *  Set the GeoMoby iBeacon Location service
		 *  You can set both indoor and outdoor to "true" for a end-to-end monitoring experience
		 */
		String indoor;
		if(mIndoor.isChecked()){
			GeoMoby.setIndoorLocationService("true");
			indoor = "true";
			GeoMoby.setUUID(mUuid.getText().toString());
			//GeoMoby.setUUID("61687109-905F-4436-91F8-E602F514C96D");
		}else{
			GeoMoby.setIndoorLocationService("false");
			indoor = "false";
		}

		// Store the date entered in the form 
		SharedPreferences.Editor editor = spref.edit();
		editor.putString("apiKey", api_key);
		editor.putString("indoor", indoor);
		editor.putString("outdoor", outdoor);
		editor.putString("uuid", mUuid.getText().toString());
		editor.commit();

	}

	/**
	 * Validate the form
	 * 
	 * @return true if valid, false otherwise
	 */
	private boolean isValidForm() {
		if( mApiKey.getText().toString().length() != 22 ){
			mApiKey.setError("Api Key must be 22 characters!");
			return false;
		}else
			mApiKey.setError(null);

		if( !mIndoor.isChecked() && !mOutdoor.isChecked() ){
			Toast.makeText(this, "Choose at least one mode!",Toast.LENGTH_SHORT).show();
			return false;
		}else{
			//mIndoor.setError(null);
			//mOutdoor.setError(null);
		}

		if( mIndoor.isChecked() ){
			String pattern = "[A-Za-z0-9]{8}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{12}";
			if(!mUuid.getText().toString().matches(pattern)){
				mUuid.requestFocus();
				mUuid.setError("UUID format does not match!");
				return false; 
			}
		}

		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//onDestroy();
	}

	@Override
	public void onBackPressed() {
		onDestroy();
	}	

	/**
	 * Check if Bluetooth LE is supported by this Android device, and if so, make sure it is enabled.
	 * Throws a RuntimeException if Bluetooth LE is not supported.  (Note: The Android emulator will do this)
	 * 
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
