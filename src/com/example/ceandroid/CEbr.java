package com.example.ceandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Time;

/**
 * CEbr is the BroadcastReceiver that is run on the CEservice to handle OS
 * triggers This class is the start of the entire Rules Engine flow
 * 
 * @author CEandroid SMU
 */
public class CEbr extends BroadcastReceiver {
	/**
	 * Application Context
	 */
	private Context mContext;
	/**
	 * SMS Received String
	 */
	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	/**
	 * Stores whether this is the first connection
	 */
	private static boolean firstConnect = false;

	/**
	 * The onReceive function handles all of the messages from the operating
	 * system. The rules engine is fired immediatley to minimize time in this
	 * section.
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 *      android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		String action = intent.getAction();
		String phoneState;
		Bundle b = intent.getExtras();
		if (b != null) {
			phoneState = b.getString(TelephonyManager.EXTRA_STATE);
		} else {
			phoneState = " ... ";
		}
		String message = "Broadcast intent detected " + intent.getAction();
		System.out.println(message);
		System.out.println("PhoneState is " + phoneState);
		int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
				WifiManager.WIFI_STATE_UNKNOWN);
		// The minute has ticked
		if (Intent.ACTION_TIME_TICK.equals(action)) {
			Time now = new Time();
			now.setToNow();
			String timeText = "" + now.hour + now.minute;
			RulesEngine r1 = new RulesEngine("time", mContext);
			System.out.println("time_tick" + timeText);
			r1.start();
		} else if (action.equals(SMS_RECEIVED)) {
			RulesEngine r1 = new RulesEngine("textMessage", mContext);
			r1.start();
		} else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			// need to check for first state change only to avoid wifi message
			// echoes
			if (firstConnect) {
				if (wifiState == WifiManager.WIFI_STATE_DISABLED
						|| wifiState == WifiManager.WIFI_STATE_ENABLED) {
					RulesEngine r1 = new RulesEngine("wifiStatus", mContext);
					r1.start();
					firstConnect = false;
				}
			} else {
				firstConnect = true;
			}
		} else if (action
				.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
			if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED,
					false)) {
				RulesEngine r1 = new RulesEngine("ssid", mContext);
				r1.start();
			}
		} else if (phoneState != null) {
			System.out.println("Ringing");
			if (phoneState.equals("RINGING")) {
				RulesEngine r1 = new RulesEngine("phoneCall", mContext);
				r1.start();
			}
		}
		locationUpdates();
	}

	/**
	 * locationUpdates requests updates based on the most accurate service that
	 * is currently on GPS, Network, and Passive Providers
	 */
	private void locationUpdates() {
		LocationManager locManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			/**
			 * Triggers when the phone receives an updated location
			 * 
			 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
			 */
			public void onLocationChanged(Location location) {
				LocationManager listLocManager = (LocationManager) mContext
						.getSystemService(Context.LOCATION_SERVICE);
				if (isBetterLocation(
						location,
						listLocManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER))) {
					RulesEngine r1 = new RulesEngine("location", mContext);
					r1.start();
				} else {
					if (isBetterLocation(
							location,
							listLocManager
									.getLastKnownLocation(LocationManager.NETWORK_PROVIDER))) {
						RulesEngine r1 = new RulesEngine("location", mContext);
						r1.start();
					} else {
						if (isBetterLocation(
								location,
								listLocManager
										.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER))) {
							RulesEngine r1 = new RulesEngine("location",
									mContext);
							r1.start();
						}
					}
				}
			}

			/**
			 * Triggers when the status is changed
			 * 
			 * @see android.location.LocationListener#onStatusChanged(java.lang.String,
			 *      int, android.os.Bundle)
			 */
			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}

			/**
			 * Triggers when a location provider is enabled
			 * 
			 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
			 */
			public void onProviderEnabled(String provider) {

			}

			/**
			 * Triggers when a location provider is disabled
			 * 
			 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
			 */
			public void onProviderDisabled(String provider) {

			}

			// Is not two minutes
			private static final int TWO_MINUTES = 1000 * 15;

			/**
			 * Determines whether one Location reading is better than the
			 * current Location fix
			 * 
			 * @param location
			 *            The new Location that you want to evaluate
			 * @param currentBestLocation
			 *            The current Location fix, to which you want to compare
			 *            the new one
			 */
			protected boolean isBetterLocation(Location location,
					Location currentBestLocation) {
				if (currentBestLocation == null) {
					// A new location is always better than no location
					return true;
				}

				// Check whether the new location fix is newer or older
				long timeDelta = location.getTime()
						- currentBestLocation.getTime();
				boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
				boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
				boolean isNewer = timeDelta > 0;

				// If it's been more than two minutes since the current
				// location, use the new location
				// because the user has likely moved
				if (isSignificantlyNewer) {
					return true;
				}
				// If the new location is more than two minutes older, it must
				// be worse
				else {
					if (isSignificantlyOlder) {
						return false;
					}
				}

				// Check whether the new location fix is more or less accurate
				int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
						.getAccuracy());
				boolean isLessAccurate = accuracyDelta > 0;
				boolean isMoreAccurate = accuracyDelta < 0;
				boolean isSignificantlyLessAccurate = accuracyDelta > 200;

				// Check if the old and new location are from the same provider
				boolean isFromSameProvider = isSameProvider(
						location.getProvider(),
						currentBestLocation.getProvider());

				// Determine location quality using a combination of timeliness
				// and accuracy
				if (isMoreAccurate) {
					return true;
				} else {
					if (isNewer && !isLessAccurate) {
						return true;
					} else {
						if (isNewer && !isSignificantlyLessAccurate
								&& isFromSameProvider) {
							return true;
						}
					}
					return false;
				}
			}

			/**
			 * isSameProvider checks to see if the providers are the same
			 * 
			 * @param provider1
			 * @param provider2
			 * @return True if the provider is the same
			 */
			private boolean isSameProvider(String provider1, String provider2) {
				if (provider1 == null) {
					return provider2 == null;
				}
				return provider1.equals(provider2);
			}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
				0, locationListener);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				locationListener);
		locManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0,
				0, locationListener);
	}
}