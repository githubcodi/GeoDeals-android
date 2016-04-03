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

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.geomoby.async.GeoMessage;
import com.geomoby.geodeals.R;
import com.squareup.picasso.Picasso;

public class CustomNotification extends Activity implements OnGestureListener, OnDoubleTapListener {

	private static final String TAG = "** GeoMoby Custom Notification **";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide Title Bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Get the layout
		setContentView(R.layout.geomoby_offer);

		try{
			// Get the extras passed by the receiver
			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();
			
			// Parse the extra
			ArrayList<GeoMessage> geoMessage = bundle.getParcelableArrayList("GeoMessage");

			// Retrieve title and description
			String title = geoMessage.get(0).title;
			String description = geoMessage.get(0).message;

			/*
			 *  Below the other values returned by our server
			 */
			//String link = geoMessage.get(0).siteURL;
			String image_url = geoMessage.get(0).imageURL;
			//final double latitude = Double.valueOf(geoMessage.get(0).latitude);
			//final double longitude = Double.valueOf(geoMessage.get(0).longitude);
			//String beaconName = geoMessage.get(0).micronodeName;
			//int beaconProximity = geoMessage.get(0).micronodeProximity;
			//int notification_id = geoMessage.get(0).id;

			// Create a new font
			Typeface font = Typeface.createFromAsset(getAssets(), "Bitter-Bold.otf");

			// Populate title TextView
			TextView tvTitle = (TextView) findViewById(R.id.title);
			tvTitle.setTypeface(font);
			tvTitle.setText(title);

			// Populate description TextView
			TextView tvDesc = (TextView) findViewById(R.id.description);
			tvDesc.setTypeface(font);
			tvDesc.setText(description);

			ImageView tvImage = (ImageView) findViewById(R.id.image);
			Picasso.with(this).setLoggingEnabled(true);
			Picasso.with(this).load(image_url).into(tvImage);

			//Notify GeoMoby server that user has opened the notification - Not supported yet.
			//new ClickThroughAsyncTask(this).execute(notification_id);

		} catch (Throwable t){
			Log.d(TAG,"Parcelable Message: "+t.getMessage());

		}

		Button btnClose = (Button) findViewById(R.id.close);
		btnClose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Close the notification pop-up  
				CustomNotification.this.finish();
			}
		});
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// Close the notification pop-up
		CustomNotification.this.finish();
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// Close the notification pop-up
		CustomNotification.this.finish();
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {return false;}

	@Override
	public boolean onDown(MotionEvent e) {return false;}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {return false;}

	@Override
	public void onLongPress(MotionEvent e) {}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {return false;}

	@Override
	public void onShowPress(MotionEvent e) {}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {return false;}
}

