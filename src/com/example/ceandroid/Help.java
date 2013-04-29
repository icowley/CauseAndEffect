package com.example.ceandroid;

import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * This Help Class provides an image link to our private tutorial on Wordpress
 * 
 * @author CEandroid SMU
 */
public class Help extends Activity {
	/**
	 * Creates the Help Page and adds the click listener
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		// Turn on "up" navigation
		getActionBar().setDisplayHomeAsUpEnabled(true);

		ImageButton hLink = (ImageButton) findViewById(R.id.helpLink);
		hLink.setOnClickListener(new OnClickListener() {
			/**
			 * Opens the Wordpress Website
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://ceandroid.wordpress.com/"));
				startActivity(i);
			}
		});
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
	 * Help Menu Creation
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_help, menu);
		return true;
	}

	/**
	 * Help Menu Actions
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
		case R.id.menu_settings: {
			Intent myIntent = new Intent(this, Preferences.class)
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