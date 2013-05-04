package com.example.ceandroid;

import java.util.ArrayList;
import java.util.List;

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
		setContentView(R.layout.item_list);
		@SuppressWarnings("unused")
		Resources res = getResources();
		listView = (ListView) findViewById(R.id.fruitList);
		listView.setTextFilterEnabled(true);
		db = new DatabaseHandler(this);

		// Load the kind of list
		app = (CEapp) getApplication();
		app.edit = false;

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
		Intent myIntent = new Intent(getApplicationContext(), EditRule.class)
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

			Intent myIntent = new Intent(this, EditRule.class)
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
}
