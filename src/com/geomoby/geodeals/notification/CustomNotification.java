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
package com.geomoby.geodeals.notification;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.geomoby.async.ClickThroughAsyncTask;
import com.geomoby.async.GeoAlert;

public class CustomNotification extends Activity {
	
	private final String SETTING_LNG="longitude";
	private final String SETTING_LAT="latitude";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		
		ArrayList<GeoAlert> geoAlert = intent.getParcelableArrayListExtra("GeoAlert");
		
		String title = geoAlert.get(0).title;
		String link = geoAlert.get(0).link;
		String image_url = geoAlert.get(0).imageurl;
		String description = geoAlert.get(0).description;
		final double latitude = Double.valueOf(geoAlert.get(0).geofence_latitude);
		final double longitude = Double.valueOf(geoAlert.get(0).geofence_longitude);
		int notification_id = geoAlert.get(0).notification_id;
		
		// Example of how the message will be displayed in the Alert Dialog
		String message = "<head><style type='text/css'>body{margin:auto auto;text-align:center;} </style></head><body><b>" + description + "</b>";
		if(!"".equals(link) && link != null)
			message += "<br>Link: <a href='"+link+"'>Click for More</a>";
		if(!"".equals(image_url) && image_url != null)
			message += "<br><img src='"+image_url+"'/>";
		message+="</body>";
		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		
		// Setting Dialog Title
		alertDialog.setTitle(title);

		 WebView webView = new WebView(this);
		 webView.loadDataWithBaseURL(null, message, "text/html", "utf-8", null);
		 webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		// Setting Dialog WebView
		alertDialog.setView(webView);

		// on pressing OK button
		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				CustomNotification.this.finish();
			}
		});

		// Showing Alert Message
		alertDialog.show();

		//Notify GeoMoby server that user has opened the notification
		new ClickThroughAsyncTask(this).execute(notification_id);
	}

}

