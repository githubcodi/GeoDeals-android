<a href="http://www.geomoby.com/"><img alt="GeoMoby logo" align="right" width="150" height="50" src="http://www.geomoby.com/images/geomoby/GeoMobySmallColor.png" /></a> GeoMoby Demo App - GeoDeals
=================

[GeoMoby SDK](http://www.geomoby.com/) is the all-in-one geofencing and user engagement platform that utilises all location services available on your mobile device from GPS, Wi-Fi... to Bluetooth Low Energy with only 1% battery drain!. You can create any geofences and microfences (iBeacons) you want: it is unlimited!

The GeoMoby Android SDK allows your Android app to detect when it is in proximity of a specific points of interests (park, mall, venue, concert...) and trigger a message to your app. Catch the message and offer your user a new experience: play a video, sound, display a picture, trigger a payment transaction...

Changelog
=========

**Android SDK 2.7**	
* Updated layout to show edit texts and checkboxes
* Added ability to show multiple notifications in status bar for testing
* Bug fixes


What does the sample app do?
============================

The app simply displays any data associated with that geofence/microfence based on criteria entered in your GeoMoby dashboard.

How to run the sample app
=========================

The sample app requires devices running Android 3.2 or newer.

 1. Import the project as explained in our <a href="http://geomoby.com/developers/tutorial/android/android-getting-started.php">Android Guide</a>

 2. Set up your project

   <pre>//Your GeoMoby API Key
    String api_key="8df4c81d79e332df65a963";
    GeoMoby.setApiKey(api_key);

   //Filter events based on users activity (still,walking,cycling,driving,tilting - default:walking - debug:tilting). 
   //You can also filter several activities using '|' as a separator (tilting|walking)
   String motion_filter = "walking|still";
   GeoMoby.setMotionFilter(motion_filter);

   //This setting corresponds to the minimum time interval between 2 GeoMoby service calls (in seconds) -    Recommended 60s.
   String updateIntervalSeconds = "60";
   GeoMoby.setUpdateInterval(updateIntervalSeconds);

  //Silence Time is the time window when no notifications can be sent (24 hour)
  String silence_start = "23";
  String silence_stop = "05";
  GeoMoby.setSilenceTimeStart(silence_start);
  GeoMoby.setSilenceTimeStop(silence_stop);

  //Turn development mode on and off (yes/no)
  //dev_mode=true consumes a bit more battery
  //dev_mode=false is the production mode as it gets the most out of our optimised battery management
  String dev_mode = "true";
  GeoMoby.setDevMode(dev_mode);

  //Set the GeoMoby Outdoor Location service - 5-10m accuracy outdoors and about 20m indoors (no iBeacons needed)
  GeoMoby.setOutdoorLocationService("false");

  //Set the GeoMoby iBeacon Location service.You can set both indoor and outdoor to "true" for a end-to-end monitoring experience
GeoMoby.setIndoorLocationService("true");		
GeoMoby.setUUID("e2c56db5-dffb-48d2-b060-d0f5a71096e0");
  </pre>

 3. Run the app!

Note: Ensure that using ["Android SDK Manager"](http://developer.android.com/tools/help/sdk-manager.html) you downloaded "Google Play Services" Rev.22 or later.

Android Guide
================
<a href="http://geomoby.com/developers/tutorial/android/android-getting-started.php"> Android Installation Guide</a>

Contribution
============

For bugs, feature requests, or other questions, [file an issue](https://github.com/geomoby/GeoDeals-android/issues).

License
=======

Copyright 2015 [GeoMoby Pty Ltd](http://www.geomoby.com/)

 
