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
package com.geomoby.geodeals.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.geomoby.geodeals.DemoService;
import com.geomoby.geodeals.R;


@SuppressLint({ "NewApi", "ValidFragment" })
public class Preferences extends PreferenceActivity {

	private static final String PREF = "GeoMobyPrefs";
	private static final int RESULT_SETTINGS = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction().replace(android.R.id.content,new UserSettingActivity()).commit();
		//PreferenceManager.setDefaultValues(Preferences.this, R.xml.preferences, false);
	}

	public class UserSettingActivity extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
			
			Preference button = (Preference)findPreference("button");
			button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			                @Override
			                public boolean onPreferenceClick(Preference arg0) {  
			                	
			        			SharedPreferences settingsActivity = getSharedPreferences(PREF, MODE_PRIVATE);
			        			SharedPreferences.Editor prefEditor = settingsActivity.edit();
			        			prefEditor.putBoolean("firstTime", true);
			        			prefEditor.commit();
			        			
			                	Intent i = new Intent(getApplicationContext(), DemoService.class);
			        			startActivityForResult(i, RESULT_SETTINGS);
			        			
			                    finish();
			                    
			                    return true;
			                }
			            });
		}
	}
}
