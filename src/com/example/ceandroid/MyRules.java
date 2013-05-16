package com.example.ceandroid;

import java.util.ArrayList;
import java.util.List;

import CEapi.Cause;
import CEapi.Effect;
import CEapi.rCause;
import CEapi.rEffect;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * MyRules Class displays the list of Rules
 * 
 * @author CEandroid SMU
 */
public class MyRules extends FragmentActivity implements
		DeleteDialogFragmentRule.DeleteDialogListenerRule {
	/**
	 * Elements array holds and empty array that is later used for Rule
	 * information
	 */
	static String[] Elements = new String[] { " ", " ", " ", " ", " ", " ",
			" ", " ", " " };
	/**
	 * List View used to list out the Rules
	 */
	private ListView listView;
	/**
	 * Database Handler used to get the list of Rules
	 */
	private DatabaseHandler db;
	/**
	 * gPosition used to handle user actions on the list of Rules
	 */
	int gPosition = 0;
	/**
	 * The type of Rule that is selected
	 */
	int type = 0;
	/**
	 * List of Rule Objects
	 */
	private List<Rule> rules = new ArrayList<Rule>();
	/**
	 * Used to access global application variables
	 */
	private CEapp app;

	/**
	 * Creates the MyRules Activity and loads the list of Rules from the
	 * database
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Load the kind of list
		app = (CEapp) getApplication();
		app.edit = false;
		
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
		
		setContentView(R.layout.item_list);
		@SuppressWarnings("unused")
		Resources res = getResources();
		listView = (ListView) findViewById(R.id.fruitList);
		listView.setTextFilterEnabled(true);
		db = new DatabaseHandler(this);



		if (app.currentRule != null) {
			if (app.currentRule.getRCauses().isEmpty()
					&& app.currentRule.getREffects().isEmpty()) {
				db.deleteRule(app.currentRule);
				Toast.makeText(this, "Incomplete rule not saved.",
						Toast.LENGTH_LONG).show();
				app.currentRule = null;
			}
		}

		makeList();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_activated_1, Elements);

		// set the adapter of each view with the same data
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.widget.AdapterView.OnItemClickListener#onItemClick(android
			 * .widget.AdapterView, android.view.View, int, long)
			 */
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				doSomething(position, type);
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.widget.AdapterView.OnItemLongClickListener#onItemLongClick
			 * (android.widget.AdapterView, android.view.View, int, long)
			 */
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				app.editedNumber = rules.get(arg2).getID();
				DialogFragment newFragment = new DeleteDialogFragmentRule();
				newFragment.show(getFragmentManager(), "delete");
				return true;
			}
		});
		db.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		CEapp app = (CEapp) this.getApplication();
		if (app.currentRule != null) {
			if (app.currentRule.getRCauses().isEmpty()
					&& app.currentRule.getREffects().isEmpty()) {
				DatabaseHandler db = new DatabaseHandler(this);
				db.deleteRule(app.currentRule);
				Toast.makeText(this, "Incomplete rule not saved.",
						Toast.LENGTH_LONG).show();
				app.currentRule = null;
				db.close();
			} else {
				makeList();
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_activated_1, Elements);
				// set the adapter of each view with the same data
				listView.setAdapter(adapter);
				listView.setAdapter(adapter);
			}
		}
	}

	/**
	 * makeList() is what is used by the onCreate to create the list of Rules
	 */
	public void makeList() {
		rules = db.getAllRules();
		Elements = new String[rules.size()];
		for (int i = 0; i < rules.size(); i++) {
			Elements[i] = rules.get(i)._name;
		}
	}

	/**
	 * doSomething is called when the user selects a Rule from the list
	 * 
	 * @param position
	 * @param type
	 */
	public void doSomething(int position, int type) {
		app.currentRule = rules.get(position);
		app.currentRule.setRCauses(db.getAllRCauses(app.currentRule.getID()));
		app.currentRule.setREffects(db.getAllREffects(app.currentRule.getID()));

		// Calls the Edit Text
		Intent myIntent = new Intent(getApplicationContext(), EditRulePager.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(myIntent);
		db.close();
	}

	/**
	 * MyRules Menu Creation
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_my_rules, menu);
		return true;
	}

	/**
	 * MyRules Menu Actions
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_new_rule: {
			// New Rule
			CEapp app = (CEapp) this.getApplication();
			app.currentRule = new Rule();
			/*
			 * DatabaseHandler db = new DatabaseHandler(this);
			 * app.currentRule.setID(db.getRulesCount()+1); db.close();
			 */

			Intent myIntent = new Intent(this, EditRulePager.class)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(myIntent);
			return true;
		}
		case R.id.menu_share: {
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
			return true;
		}
		default: {
			return super.onOptionsItemSelected(item);
		}
		}
	}

	/**
	 * onDialogPositiveClick is called when a Rule is confirmed to be deleted by
	 * the user
	 * 
	 * @see com.example.ceandroid.DeleteDialogFragmentRule.DeleteDialogListenerRule#onDialogPositiveClick(android.app.DialogFragment)
	 */
	public void onDialogPositiveClick(DialogFragment dialog) {
		// clicked to delete the cause or effect
		DatabaseHandler db = new DatabaseHandler(this);
		CEapp app = (CEapp) this.getApplication();
		db.deleteRule(db.getRule(app.editedNumber));
		db.close();
		Intent intent = getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		finish();
		startActivity(intent);
	}

	/**
	 * onDialogNegativeClick is called when a Rule delete is cancelled
	 * 
	 * @see com.example.ceandroid.DeleteDialogFragmentRule.DeleteDialogListenerRule#onDialogNegativeClick(android.app.DialogFragment)
	 */
	public void onDialogNegativeClick(DialogFragment dialog) {
		// clicked cancel, do not do anything
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
				db.importDatabase("/data/data/com.example.ceandroid/databases/CEdb.db");
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
			if (db.getAllCauses().size() < 1)
			{
				db.onUpgrade(db.getWritableDatabase(), 1, 1);
				db.addCause(new Cause("time", "Time", "time reached", "time"));
				db.addCause(new Cause("phoneCall", "Phone Call",
						"incoming phone call", "phone"));
				db.addCause(new Cause("textMessage", "Text Message",
						"new text message", "message"));
				db.addCause(new Cause("ssid", "SSID", "ssid match", "wifi"));
				db.addCause(new Cause("wifiStatus", "Status", "wifi status",
						"wifi"));
				db.addCause(new Cause("location", "Arriving",
						"Triggers when you arrive at a location", "Location"));
				db.addCause(new Cause("location", "Departing",
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
}
