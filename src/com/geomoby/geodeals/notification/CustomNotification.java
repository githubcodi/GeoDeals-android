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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.geomoby.async.ClickThroughAsyncTask;
import com.geomoby.async.GeoAlert;
import com.geomoby.geodeals.R;
import com.geomoby.logic.DisplayNotification;

public class CustomNotification extends Activity implements OnGestureListener, OnDoubleTapListener {

	private final String SETTING_LNG="longitude";
	private final String SETTING_LAT="latitude";
	//GestureDetector gd = null; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Hide Title Bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.offer);

		Intent intent = getIntent();

		ArrayList<GeoAlert> geoAlert = intent.getParcelableArrayListExtra("GeoAlert");

		String title = geoAlert.get(0).title;
		String link = geoAlert.get(0).link;
		String image_url = geoAlert.get(0).imageurl;
		String description = geoAlert.get(0).description;
		final double latitude = Double.valueOf(geoAlert.get(0).geofence_latitude);
		final double longitude = Double.valueOf(geoAlert.get(0).geofence_longitude);
		int notification_id = geoAlert.get(0).notification_id;


		Button btnClose = (Button) findViewById(R.id.close);
		btnClose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click   
				CustomNotification.this.finish();
			}
		});


		Button btnNearest = (Button) findViewById(R.id.nearest);
		btnNearest.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//final double latitude = -31.953679;
				//final double longitude = 115.858505;


				SharedPreferences settingsActivity = CustomNotification.this.getSharedPreferences("GeoMobyPrefs", MODE_PRIVATE);
				final double myLatitude = Double.valueOf(settingsActivity.getString(SETTING_LAT, ""));
				final double myLongitude = Double.valueOf(settingsActivity.getString(SETTING_LNG, ""));

				Context context = CustomNotification.this;
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?f=d&saddr="+myLatitude+","+myLongitude+"&daddr="+latitude+","+longitude+ "&dirflg=w"));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				//finish();
			}
		});

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
		new DownloadImageTask((ImageView) findViewById(R.id.image)).execute(image_url);

		//Notify GeoMoby server that user has opened the notification
		new ClickThroughAsyncTask(this).execute(notification_id);
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

