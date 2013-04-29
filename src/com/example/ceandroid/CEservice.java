package com.example.ceandroid;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;

/**
 * Service to handle device state changes and evaluate rules accordingly
 * 
 * @author CEandroid SMU
 * 
 */
public class CEservice extends Service {
	/** Broadcast receiver to listen for state changes */
	CEbr br = new CEbr();

	/**
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_NOT_STICKY;
	}

	/**
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Registers the BroadcastReceiver (and the entire rules engine) from the
	 * service Uses IntentFilters to declare trigger types
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		IntentFilter filter = new IntentFilter();

		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.addAction(Intent.ACTION_TIME_TICK);
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		filter.addAction("android.intent.action.PHONE_STATE");
		filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
		filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);

		registerReceiver(br, filter);
	}

	/**
	 * Unregisters the CEbr
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(br);
	}
}
