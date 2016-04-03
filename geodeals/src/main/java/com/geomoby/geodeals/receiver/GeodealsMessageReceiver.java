package com.geomoby.geodeals.receiver;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.geomoby.async.GeoMessage;
import com.geomoby.geodeals.notification.CustomNotification;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public  class GeodealsMessageReceiver extends BroadcastReceiver {

	private static final String TAG = "** GeoMoby Message Receiver **";
	private static int notifyID; 	// Sets an ID for the notification, so it can be updated
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String GMMessage = intent.getExtras().getString("geomoby");

		// Detect the GeoMoby GCM message
		if(GMMessage != null){
			Log.d(TAG,"Notification Received: "+ GMMessage);
			generateNotification(context, GMMessage);
		}
		setResultCode(Activity.RESULT_OK);
	}
	
	/**
	 * Display a notification to inform the user that server has sent a message.
	 */
	@SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message) {
		if(message.length() > 0) {
			
			notifyID = (int) System.currentTimeMillis();
			
			try{
				// Parse the GeoMoby message using our GeoMessage class
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
				Bundle b = new Bundle();
				b.putParcelableArrayList("GeoMessage", (ArrayList<GeoMessage>) alerts);
				i.putExtras(b);
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
				
				// Read from the /assets directory
				Resources resources = context.getResources();
				int icon = resources.getIdentifier("geomoby_icon" , "drawable", context.getPackageName());
				int not_title = resources.getIdentifier("notification_title" , "string", context.getPackageName());
				int not_ticker = resources.getIdentifier("notification_ticker" , "string", context.getPackageName());

				// Get the GeoMessage title
				String desc = alerts.get(0).title;
				
				// Manage notifications differently according to Android version
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

					builder
					.setSmallIcon(icon)
					.setContentTitle(context.getResources().getText(not_title))
					.setContentText(desc)
					.setTicker(context.getResources().getText(not_ticker))
					.setContentIntent(pendingIntent)
					.setAutoCancel(true);

					// Setting Notification Flags
					builder.setDefaults(Notification.DEFAULT_ALL); //Vibrate, Sound and Led
					//builder.setSound(alarmSound);

			        // Because the ID remains unchanged, the existing notification is updated.
					notificationManager.notify(notifyID,builder.build());

				}/*else{
					Notification notification = new Notification(icon,desc,System.currentTimeMillis());

					// Setting Notification Flags
					notification.defaults |= Notification.DEFAULT_VIBRATE;
					notification.flags |= Notification.FLAG_AUTO_CANCEL;

					// Set the Notification Info
					notification.setLatestEventInfo(context, desc, context.getResources().getText(not_ticker), pendingIntent);

					// Send the notification
					// If the ID remains unchanged, the existing notification is updated.
					notificationManager.notify(notifyID, notification);
				}*/
			}catch ( JsonParseException e) {
				Log.i(TAG,"This is not a GeoMoby notification");
				throw new RuntimeException(e);
			}
		}
	}

}
