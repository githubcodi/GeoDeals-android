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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.geomoby.async.GeoMessage;
import com.geomoby.geodeals.notification.CustomNotification;

import com.geomoby.logic.GeomobyStartService;
import com.geomoby.logic.GeomobyStopService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import com.geomoby.logic.GeomobyNotificationsReceiver;
import com.geomoby.logic.GeomobyNotificationsReceiver.GMNotificationsListener;

public class DemoService extends Activity implements GMNotificationsListener {

	private static final String TAG = "** Demo Service **";
	private static final String PREF = "GeoMobyPrefs";
	private static final String SETTING_TAGS = "tags";
	private static final String PREF_NAME="checked";
	private static SharedPreferences spref;
	private static int notifyID = 1; 	// Sets an ID for the notification, so it can be updated
	
	boolean isCheckedStatus;
	CompoundButton mToggle;
	public Context mContext;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geomoby_main);
		mContext = this;

		mToggle = (CompoundButton) findViewById(R.id.togglebutton); 
		
		spref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		isCheckedStatus = spref.getBoolean("check", false);  //default is false

		/*
		 * Set up toggle status
		 */
		if (!isCheckedStatus) 
			mToggle.setChecked(false);
		else
			mToggle.setChecked(true);

		/*
		 *  Save the tags in the GeoMoby shared preferences in private mode for the user. These tags will be used
		 *  to segment your audience when creating your proximity alerts. Please make sure that they match with
		 *  the ones configured in your dashboard when you create an alert.
		 *  Ex: 'test' is the default tag so make sure that it is set up in your Account page
		 */
		SharedPreferences mySharedPreferences = getSharedPreferences(PREF, MODE_PRIVATE);

		// Build the string of tags - empty for testing. Make sure that you create your first geofences with no tags in your dashboard.
		// Add your own logic here: "male,vip,monday"...
		String tags = "";

		// Commit the string
		SharedPreferences.Editor prefEditor = mySharedPreferences.edit();
		prefEditor.putString(SETTING_TAGS, tags);
		prefEditor.commit();

		/*
		 *  Monitor the toggle - Our SDK will ensure that all services are running/stopping properly
		 */
		mToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked == false) {

					mToggle.setPressed(false);

					// Stop the GeoMoby tracking service
					startService(new Intent(DemoService.this, GeomobyStopService.class));

					SharedPreferences.Editor editor = spref.edit();
					editor.putBoolean("check", false);
					editor.commit();

				}else{

					mToggle.setPressed(true);

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
				}
			}
		});
		
		// Initialise GeoMoby Notification Listener
		GeomobyNotificationsReceiver receiver = new GeomobyNotificationsReceiver();
		receiver.setNotificationListener(this);
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
	
		@Override
		public void onNotificationReceived(String message) {
			Log.d(TAG,"Notification Received!");
			generateNotification(mContext, message);
		}
	
	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, String message) {
		if(message.length() > 0) {
			
			// Parse the GeoMoby message using the GeoMessage class
			try{
				Gson gson = new Gson();
				JsonParser parser = new JsonParser();
				JsonArray Jarray = parser.parse(message).getAsJsonArray();
				ArrayList<GeoMessage> alerts = new ArrayList<GeoMessage>();
				for(JsonElement obj : Jarray ){
					GeoMessage gName = gson.fromJson(obj,GeoMessage.class);
					alerts.add(gName);
				}

				// Prepare Notification and pass the GeoMessage to an Extra
				NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
				Intent i = new Intent(context, CustomNotification.class);
				i.putParcelableArrayListExtra("GeoMessage", (ArrayList<GeoMessage>) alerts);
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
	
				// Read from the /assets directory
				Resources resources = context.getResources();
				AssetManager assetManager = resources.getAssets();
				try {
					InputStream inputStream = assetManager.open("geomoby.properties");
					Properties properties = new Properties();
					properties.load(inputStream);
					String push_icon = properties.getProperty("push_icon");
					int icon = resources.getIdentifier(push_icon , "drawable", context.getPackageName());
					int not_title = resources.getIdentifier("notification_title" , "string", context.getPackageName());
					int not_text = resources.getIdentifier("notification_text" , "string", context.getPackageName());
					int not_ticker = resources.getIdentifier("notification_ticker" , "string", context.getPackageName());
	
					// Manage notifications differently according to Android version
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
	
						builder
						.setSmallIcon(icon)
						.setContentTitle(context.getResources().getText(not_title))
						.setContentText(context.getResources().getText(not_text))
						.setTicker(context.getResources().getText(not_ticker))
						.setContentIntent(pendingIntent)
						.setAutoCancel(true);
	
						builder
						.setDefaults(Notification.DEFAULT_ALL); //Vibrate, Sound and Led
	
						// Because the ID remains unchanged, the existing notification is updated.
						notificationManager.notify(notifyID,builder.build());
	
					}else{
						Notification notification = new Notification(icon,context.getResources().getText(not_text),System.currentTimeMillis());
	
						//Setting Notification Flags
						notification.defaults |= Notification.DEFAULT_ALL;
						notification.flags |= Notification.FLAG_AUTO_CANCEL;
	
						//Set the Notification Info
						notification.setLatestEventInfo(context, context.getResources().getText(not_text), context.getResources().getText(not_ticker), pendingIntent);
	
						//Send the notification
						// Because the ID remains unchanged, the existing notification is updated.
						notificationManager.notify(notifyID, notification);
					}
				} catch (IOException e) {
					System.err.println("Failed to open geomoby property file");
					e.printStackTrace();
				}
			}catch ( JsonParseException e) {
				Log.i(TAG,"This is not a GeoMoby notification");
				throw new RuntimeException(e);
			}
		}
	}
}
