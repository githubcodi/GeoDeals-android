GeoDeals-android
================

GeoDeals demo app for Android.

Set Up
================

**Your GeoMoby API Key**

String api_key="8df4c81d79e332df65a963";

GeoMoby.setApiKey(api_key);

**Filter events based on users activity (still,walking,cycling,driving,tilting - default:walking - debug:tilting)**

**You can also filter several activities using '|' as a separator (tilting|walking)**

String motion_filter = "walking|still";

GeoMoby.setMotionFilter(motion_filter);

**This setting corresponds to the minimum time interval between 2 GeoMoby service calls (in seconds) - Recommended 60s.**

String updateIntervalSeconds = "60";

GeoMoby.setUpdateInterval(updateIntervalSeconds);


//  Silence Time is the time window when no notifications can be sent (24 hour)

String silence_start = "23";

String silence_stop = "05";

GeoMoby.setSilenceTimeStart(silence_start);

GeoMoby.setSilenceTimeStop(silence_stop);

// Turn development mode on and off (yes/no)
// dev_mode=true consumes a bit more battery 
// dev_mode=false is the production mode as it gets the most out of our optimised battery management
String dev_mode = "true";
GeoMoby.setDevMode(dev_mode);

//  Set the GeoMoby Outdoor Location service - 5-10m accuracy outdoors and about 20m indoors (no iBeacons needed)
GeoMoby.setOutdoorLocationService("false");


//  Set the GeoMoby iBeacon Location service
//  You can set both indoor and outdoor to "true" for a end-to-end monitoring experience
GeoMoby.setIndoorLocationService("true");		
GeoMoby.setUUID("e2c56db5-dffb-48d2-b060-d0f5a71096e0");

Android Guide
================
http://geomoby.com/developers/tutorial/android/android-getting-started.php
