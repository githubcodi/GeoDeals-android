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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.geomoby.logic.GeomobyStartService;
import com.geomoby.logic.GeomobyStopService;

public class DemoService extends Activity {

	public final static String TAG = "** Demo Service **";
	private static final String PREF = "GeoMobyPrefs";
	final String SETTING_TAGS = "tags";

	boolean isCheckedStatus;
	final String PREF_NAME="checked";
	public SharedPreferences spref;
	CompoundButton toggle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geomoby_main);

		toggle = (CompoundButton) findViewById(R.id.togglebutton); 
		
		spref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		isCheckedStatus = spref.getBoolean("check", false);  //default is false

		/*
		 * Set up toggle status
		 */
		if (!isCheckedStatus) 
			toggle.setChecked(false);
		else
			toggle.setChecked(true);

		/*
		 *  Save the tags in the GeoMoby shared preferences in private mode for the user. These tags will be used
		 *  to segment your audience when creating your proximity alerts. Please make sure that they match with
		 *  the ones configured in your dashboard when you create an alert.
		 *  Ex: 'test' is the default tag so make sure that it is set up in your Account page
		 */
		SharedPreferences mySharedPreferences = getSharedPreferences(PREF, MODE_PRIVATE);

		// Build the string of tags
		String tags = "test";

		// Commit the string
		SharedPreferences.Editor prefEditor = mySharedPreferences.edit();
		prefEditor.putString(SETTING_TAGS, tags);
		prefEditor.commit();

		/*
		 *  Monitor the toggle - Our SDK will ensure that all services are running/stopping properly
		 */
		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


				if (isChecked == false) {

					toggle.setPressed(false);

					//Log.d(TAG,"Stopping Notification Service");	
					// Stop the GeoMoby tracking service
					startService(new Intent(DemoService.this, GeomobyStopService.class));

					SharedPreferences.Editor editor = spref.edit();
					editor.putBoolean("check", false);
					editor.commit();

				}else{

					toggle.setPressed(true);

					// Start the GeoMoby tracking service
					startService(new Intent(DemoService.this, GeomobyStartService.class));

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

					//Toast.makeText(getApplicationContext(), "Great! You'll be receiving live offers soon!",Toast.LENGTH_LONG).show();

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
		onDestroy();
	}

	@Override
	public void onBackPressed() {
		onDestroy();
	}
}
