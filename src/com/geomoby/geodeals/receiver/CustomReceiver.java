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
package com.geomoby.geodeals.receiver;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.geomoby.async.GeoAlert;
import com.geomoby.geodeals.notification.CustomNotification;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class CustomReceiver extends BroadcastReceiver {

	// Sets an ID for the notification, so it can be updated
	private static int notifyID = 1;
	private static final String TAG = "CustomReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String message = intent.getExtras().getString("message");

		if(message != null){	
			generateNotification(context, message);
		}

		setResultCode(Activity.RESULT_OK);

	} 

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	@SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message) {
		if(message.length() > 0) {
			
			// Parse the GeoMoby message
			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			JsonArray Jarray = parser.parse(message).getAsJsonArray();
			ArrayList<GeoAlert> alerts = new ArrayList<GeoAlert>();
			for(JsonElement obj : Jarray ){
				GeoAlert gName = gson.fromJson(obj,GeoAlert.class);
				alerts.add(gName);
			}

			// Send Notification
			NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

			Intent i = new Intent(context, CustomNotification.class);
			i.putParcelableArrayListExtra("GeoAlert", (ArrayList<GeoAlert>) alerts);

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

				// Manage notifications differently according to Android version
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

					builder
					.setSmallIcon(icon)
					.setContentTitle("Coupon City Alert!")
					.setContentText("Click Now!")
					.setTicker("Coupon City Alert!")
					.setContentIntent(pendingIntent)
					.setAutoCancel(true);

					builder
					.setDefaults(Notification.DEFAULT_ALL); //Vibrate, Sound and Led

					// Because the ID remains unchanged, the existing notification is updated.
					notificationManager.notify(notifyID,builder.build());

				}else{
					Notification notification = new Notification(icon,"Coupon City Alert!",System.currentTimeMillis());

					//Setting Notification Flags
					notification.defaults |= Notification.DEFAULT_ALL;
					notification.flags |= Notification.FLAG_AUTO_CANCEL;

					//Set the Notification Info
					notification.setLatestEventInfo(context, "Coupon City Alert!", "Click Now !", pendingIntent);

					//Send the notification
					// Because the ID remains unchanged, the existing notification is updated.
					notificationManager.notify(notifyID, notification);
				}

			} catch (IOException e) {
				System.err.println("Failed to open geomoby property file");
				e.printStackTrace();
			}
		}
	}

}
