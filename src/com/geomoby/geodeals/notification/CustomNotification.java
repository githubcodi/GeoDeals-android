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

public class CustomNotification extends Activity implements OnGestureListener, OnDoubleTapListener {

	private final String SETTING_LNG="longitude";
	private final String SETTING_LAT="latitude";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Hide Title Bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.geomoby_offer);

		Intent intent = getIntent();

		ArrayList<GeoMessage> geoMessage = intent.getParcelableArrayListExtra("GeoMessage");
		
		String title = geoMessage.get(0).title;
		String link = geoMessage.get(0).siteURL;
		String image_url = geoMessage.get(0).imageURL;
		String description = geoMessage.get(0).message;
		final double latitude = Double.valueOf(geoMessage.get(0).latitude);
		final double longitude = Double.valueOf(geoMessage.get(0).longitude);
		String beaconName = geoMessage.get(0).micronodeName;
		int beaconProximity = geoMessage.get(0).micronodeProximity;
		int notification_id = geoMessage.get(0).id;

		Button btnClose = (Button) findViewById(R.id.close);
		btnClose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click   
				CustomNotification.this.finish();
			}
		});

		/*Button btnNearest = (Button) findViewById(R.id.nearest);
		btnNearest.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SharedPreferences settingsActivity = CustomNotification.this.getSharedPreferences("GeoMobyPrefs", MODE_PRIVATE);
				final double myLatitude = Double.valueOf(settingsActivity.getString(SETTING_LAT, ""));
				final double myLongitude = Double.valueOf(settingsActivity.getString(SETTING_LNG, ""));

				Context context = CustomNotification.this;
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?f=d&saddr="+myLatitude+","+myLongitude+"&daddr="+latitude+","+longitude+ "&dirflg=w"));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		});*/

		Typeface font = Typeface.createFromAsset(getAssets(), "Bitter-Bold.otf");

		TextView tvTitle = (TextView) findViewById(R.id.title);
		tvTitle.setTypeface(font);
		tvTitle.setText(title);

		TextView tvDesc = (TextView) findViewById(R.id.description);
		tvDesc.setTypeface(font);
		tvDesc.setText(description);


		TextView tvLink = (TextView) findViewById(R.id.link);
		tvLink.setTypeface(font);
		String desc = "<a href=\""+link+"\">Demo Link</a>";
		tvLink.setText(Html.fromHtml(desc));
		tvLink.setMovementMethod(LinkMovementMethod.getInstance());

		// Warning - Big bitmap images might create errors
		if(!image_url.equals(""))
			new DownloadImageTask((ImageView) findViewById(R.id.image)).execute(image_url);

		//Notify GeoMoby server that user has opened the notification
		//new ClickThroughAsyncTask(this).execute(notification_id);
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		CustomNotification.this.finish();
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		CustomNotification.this.finish();
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}



}

