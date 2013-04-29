package com.example.ceandroid;

import android.app.ActionBar;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Preferences page - tabbed interface to get access to other pages
 * 
 * @author CEandroid SMU
 */
public class Preferences extends FragmentActivity {
	/**
	 * Selected item constant referring to which fragment is in the foreground
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	/**
	 * Creates the action bar and FragmentActivity Contains General Settings,
	 * Security Settings, and the About Page
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			// Set Navigation mode
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

			// Turn on "up" navigation
			actionBar.setDisplayHomeAsUpEnabled(true);

			// For each of the sections in the app, add a tab to the action bar.
			actionBar.addTab(actionBar
					.newTab()
					.setText("General")
					.setTabListener(
							new TabListener<General>(this, "general",
									General.class)));
			actionBar.addTab(actionBar
					.newTab()
					.setText("Security")
					.setTabListener(
							new TabListener<Security>(this, "security",
									Security.class)));
			actionBar
					.addTab(actionBar
							.newTab()
							.setText("About")
							.setTabListener(
									new TabListener<About>(this, "about",
											About.class)));
		}
	}

	/**
	 * Checks the current rule, clears if not null
	 * 
	 * @see android.app.Activity#onResume()
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
			}
		}
	}

	/**
	 * When the state is returned, the current tab selection is brought to the
	 * front
	 * 
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	/**
	 * Saves the instance state when the application is sent to the background
	 * 
	 * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	/**
	 * Preferenes Menu Creation
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_preferences, menu);
		return true;
	}

	/**
	 * Preferences Menu Actions
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case android.R.id.home: {
			// This is called when the Home (Up) button is pressed
			// in the Action Bar.
			Intent parentActivityIntent = new Intent(this, MainActivity.class);
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			finish();
			return true;
		}
		case R.id.menu_new_rule: {
			// New Rule
			CEapp app = (CEapp) this.getApplication();
			app.currentRule = new Rule();

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
		case R.id.menu_help: {
			Intent myIntent = new Intent(this, Help.class)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(myIntent);
			return true;
		}
		default: {
			return super.onOptionsItemSelected(item);
		}
		}
	}
}