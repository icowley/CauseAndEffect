package com.example.ceandroid;

import java.util.ArrayList;

import CEapi.Cause;
import CEapi.Effect;
import CEapi.rCause;
import CEapi.rEffect;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	/**
	 * CEapp contains the globally accessible variables
	 */
	private CEapp app;

	/**
	 * Creates the Main Menu Starts the Database and CEservice, if necessary
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		app = (CEapp) this.getApplication();
		if (app.getListType() == 0) {
			new Thread(new Runnable() {
				/**
				 * The database is created in a separate thread This keeps the
				 * application responsive
				 * 
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					testDatabase();
				}
			}).start();
		}
	}

	/**
	 * Occurs when the app is resumed Starts the service if it is not currently
	 * running
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		if ((!isMyServiceRunning()) && app.getListType() != 0) {
			Intent service = new Intent(this, CEservice.class);
			this.startService(service);
		}

		if (app.currentRule != null) {
			if (app.currentRule.getRCauses().isEmpty()
					&& app.currentRule.getREffects().isEmpty()) {
				DatabaseHandler db = new DatabaseHandler(this);
				db.deleteRule(app.currentRule);
				Toast.makeText(this, "Incomplete rule not saved.",
						Toast.LENGTH_LONG).show();
				app.currentRule = null;
				db.close();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
	}

	/**
	 * Used to evaluate the current state of the CEservice
	 * 
	 * @return boolean The service is either running or not running
	 */
	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (CEservice.class.getName()
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	// Generates test data for the database
	/**
	 * Used to create the database Creates Causes, Effects, and default Rules
	 * Once this function is completed, the service is started, and user actions
	 * are allowed
	 */
	@SuppressLint("SdCardPath")
	public void testDatabase() {
		DatabaseHandler db = new DatabaseHandler(this);

		try {
			db.importDatabase("/data/data/com.example.ceandroid/databases/database.db");
		} catch (Exception e) {
			db.onUpgrade(db.getWritableDatabase(), 1, 1);
			db.addCause(new Cause("time", "Time", "time reached", "time"));
			db.addCause(new Cause("phoneCall", "Phone Call",
					"incoming phone call", "phone"));
			db.addCause(new Cause("textMessage", "Text Message",
					"new text message", "message"));
			db.addCause(new Cause("ssid", "Wifi SSID", "ssid match", "wifi"));
			db.addCause(new Cause("wifiStatus", "Wifi Status", "wifi status",
					"wifi"));
			db.addCause(new Cause("location", "Arriving at a Location",
					"Triggers when you arrive at a location", "Location"));
			db.addCause(new Cause("location", "Departing a Location",
					"Triggers when you depart a location", "Location"));
			db.addEffect(new Effect("toast", "Toast",
					"Displays a toast message", "display"));
			db.addEffect(new Effect("vibrate", "Vibrate",
					"Activates the phone's vibration", "system"));
			db.addEffect(new Effect("notification", "Notification",
					"Adds a notification", "display"));
			db.addEffect(new Effect("sound", "Play Sound",
					"Activates the phone's vibration", "media"));
			db.addEffect(new Effect("ringer", "Ring Mode",
					"Silent, Vibrate, and Normal ring modes", "system"));

			ArrayList<rCause> ruleCauses = new ArrayList<rCause>();
			ruleCauses.add(new rCause(1, 1, "12:30", "time"));
			ArrayList<rEffect> ruleEffects = new ArrayList<rEffect>();
			ruleEffects.add(new rEffect(1, 3, "Wake up!'Go to school.'",
					"notification"));
			db.addRule(new Rule("1$", "Alarm", ruleCauses, ruleEffects));

			ruleCauses.clear();
			ruleEffects.clear();
			ruleCauses.add(new rCause(2, 1, "12:30", "time"));
			ruleCauses.add(new rCause(2, 2, "Nathan R Huntoon", "phone call"));
			ruleEffects.add(new rEffect(2, 2, "Tone Length:Long", "vibrate"));
			ruleEffects.add(new rEffect(2, 3,
					"Call from Nathan'Hangup Quick!'", "notification"));
			db.addRule(new Rule("2,3,+$", "Call me maybe?", ruleCauses,
					ruleEffects));

			ruleCauses.clear();
			ruleEffects.clear();
			ruleCauses.add(new rCause(3, 1, "12:30", "time"));
			ruleCauses.add(new rCause(3, 2, "Matt Rispoli", "phone call"));
			ruleEffects.add(new rEffect(3, 2, "Tone Length:Long", "vibrate"));
			ruleEffects
					.add(new rEffect(3, 3,
							"Call from Matt!'Probably about Physics.'",
							"notification"));
			db.addRule(new Rule("4,5,+$", "Called me.", ruleCauses, ruleEffects));

			ruleCauses.clear();
			ruleEffects.clear();
			ruleCauses.add(new rCause(4, 4, "PerunaNet", "ssid"));
			ruleCauses.add(new rCause(4, 1, "12:00", "time"));
			ruleEffects.add(new rEffect(4, 3, "I am connected to PerunaNet!",
					"notification"));
			db.addRule(new Rule("6,7,+$", "SSID test", ruleCauses, ruleEffects));
		}
		app.setListType(-1);
		db.close();

		Intent service = new Intent(this, CEservice.class);
		this.startService(service);
	}

	/**
	 * Called when the user tries to access the MyRules Activity Allows access
	 * unless the database and service aren't ready yet
	 * 
	 * @param v
	 */
	public void myRules(View v) {
		if (app.getListType() != 0) {
			Intent myIntent = new Intent(this, MyRules.class)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(myIntent);
		} else {
			loading();
		}
	}

	/**
	 * Called when the user tries to create a new rule (EditRule Activity)
	 * Allows access unless the database and service aren't ready yet
	 * 
	 * @param v
	 */
	public void newRule(View v) {
		if (app.getListType() != 0) {
			// New Rule
			CEapp app = (CEapp) this.getApplication();
			app.currentRule = new Rule();

			Intent myIntent = new Intent(this, EditRule.class)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(myIntent);
		} else {
			loading();
		}
	}

	/**
	 * Called when the user tries to access the ShareList Activity Allows access
	 * unless the database and service aren't ready yet
	 * 
	 * @param v
	 */
	public void share(View v) {
		if (app.getListType() != 0) {
			PackageManager p = this.getPackageManager();
			if (!p.hasSystemFeature("android.hardware.nfc")) {
				Toast.makeText(this, "NFC is not available on this device.",
						Toast.LENGTH_LONG).show();
			} else {
				NfcAdapter na = NfcAdapter.getDefaultAdapter(this);
				if (na.isEnabled()) {
					Intent myIntent = new Intent(this, ShareList.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(myIntent);
				} else {
					Toast.makeText(this,
							"Please enable NFC to use this feature.",
							Toast.LENGTH_LONG).show();
				}
			}
		} else {
			loading();
		}
	}

	/**
	 * Called when the user tries to access another screen before the database
	 * and service are ready
	 * 
	 * @param v
	 */
	public void loading() {
		Toast.makeText(this.getApplicationContext(), "Loading Test Database",
				Toast.LENGTH_SHORT).show();
	}
}