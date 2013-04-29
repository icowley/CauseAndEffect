package com.example.ceandroid;

import java.util.ArrayList;
import java.util.List;

import CEapi.rCause;
import CEapi.rEffect;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Page for displaying available rules to share.
 * 
 * @author CEandroid SMU
 * 
 */
public class ShareList extends ListActivity {
	/** Array for holding rule names. Used for displaying in the list. */
	private String[] Elements;

	/** Handler for fetching rules. */
	private DatabaseHandler db;

	/** Holds rules to be used. */
	private List<Rule> rules = new ArrayList<Rule>();

	/** Used for setting the current rule for sharing. */
	private CEapp app;

	/** ListView for displaying rule names on screen. */
	private ListView lv;

	// NFC stuff
	/** Tag for NFC */
	private static final String TAG = "ceandroid";

	/** NfcAdapter for receiving Ndefs */
	NfcAdapter nAdapter;

	/** PendingIntent for NFC use */
	PendingIntent nPendingIntent;

	/** Intent filter for picking up Ndef messages */
	IntentFilter[] nExchangeFilters;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@SuppressWarnings("static-access")
	@Override
	public void onResume() {
		super.onResume();

		/** Detect Ndefs coming in and create a rule based on the payload */
		if (nAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			NdefMessage[] messages = getNdefMessages(getIntent());
			byte[] rPayload = messages[0].getRecords()[0].getPayload();
			String rString = new String(rPayload);

			replaceRuleFields(rString);

			setIntent(new Intent());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@SuppressWarnings("static-access")
	@Override
	protected void onNewIntent(Intent intent) {
		/** Detects Ndefs and creates a rule */
		if (nAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			NdefMessage[] messages = getNdefMessages(intent);
			String rString = new String(
					messages[0].getRecords()[0].getPayload());
			replaceRuleFields(rString);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (CEapp) getApplication();
		db = new DatabaseHandler(this);
		rules = db.getAllRules();
		Elements = new String[rules.size()];

		// Turn on "up" navigation
		getActionBar().setDisplayHomeAsUpEnabled(true);

		for (int i = 0; i < rules.size(); i++) {
			Elements[i] = rules.get(i).getName();
		}

		@SuppressWarnings("rawtypes")
		ArrayAdapter a = new ArrayAdapter<String>(this,
				R.layout.activity_sharelist, Elements);

		setListAdapter(a);
		a.notifyDataSetChanged();

		lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				app.currentRule = rules.get(position);
				app.currentRule.setRCauses(db.getAllRCauses(app.currentRule
						.getID()));
				app.currentRule.setREffects(db.getAllREffects(app.currentRule
						.getID()));

				Intent intent = new Intent(ShareList.this, ShareNFC.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		// Nfc adapter initialization
		nAdapter = NfcAdapter.getDefaultAdapter(this);

		// this activity handles NFC intents
		nPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// intent filters for p2p exchange
		@SuppressWarnings("static-access")
		IntentFilter ndefDetected = new IntentFilter(
				nAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefDetected.addDataType("text/plain");
		} catch (MalformedMimeTypeException e) {
		}
		nExchangeFilters = new IntentFilter[] { ndefDetected };
	}

	/**
	 * Takes in a C&E Ndef payload (plain-text string) and creates a rule in the
	 * database after parsing it.
	 * 
	 * @param rString
	 *            Plain-text representation of a rule object
	 */
	private void replaceRuleFields(String rString) {
		int charPtr = 0;
		DatabaseHandler db = new DatabaseHandler(this);
		ArrayList<rCause> rCauses = new ArrayList<rCause>();
		ArrayList<rEffect> rEffects = new ArrayList<rEffect>();
		int ceCount;
		String name = "";
		char cur = ' ';
		while (true) {
			cur = rString.charAt(charPtr);
			if (cur == '\n')
				break;
			name += cur;
			charPtr++;
		}

		charPtr++;
		System.out.println(name);

		// tree data, FIX ME!
		String tree = "";
		while (true) {
			cur = rString.charAt(charPtr);
			if (cur == '\n')
				break;
			tree += cur;
			charPtr++;
		}
		System.out.println(tree);

		charPtr++;

		String rCount = "";
		while (true) {
			cur = rString.charAt(charPtr);
			if (cur == '\n')
				break;
			rCount += cur;
			charPtr++;
		}
		System.out.println(rCount);

		charPtr++;
		Rule r = new Rule(tree, name, true);
		db.addRule(r);
		r.setID(db.getAllRules().get(db.getRulesCount() - 1).getID());
		ceCount = db.getRCausesCount();
		for (int i = 0; i < Integer.parseInt(rCount); i++) {
			ceCount++;
			String rid = "", rParam = "", rType = "";
			while (true) {
				cur = rString.charAt(charPtr);
				if (cur == '\n')
					break;
				rid += cur;
				charPtr++;
			}
			System.out.println(rid);
			charPtr++;

			while (true) {
				cur = rString.charAt(charPtr);
				if (cur == '\n')
					break;
				rParam += cur;
				charPtr++;
			}
			System.out.println(rParam);
			charPtr++;

			while (true) {
				cur = rString.charAt(charPtr);
				if (cur == '\n')
					break;
				rType += cur;
				charPtr++;
			}
			System.out.println(rType);
			charPtr++;

			rCauses.add(new rCause(ceCount, r.getID(), Integer.parseInt(rid),
					rParam, rType));
		}

		rCount = "";
		while (true) {
			cur = rString.charAt(charPtr);
			if (cur == '\n')
				break;
			rCount += cur;
			charPtr++;
		}
		System.out.println(rCount);
		charPtr++;

		ceCount = db.getREffectsCount();
		for (int i = 0; i < Integer.parseInt(rCount); i++) {
			ceCount++;
			String rid = "", rParam = "", rType = "";
			while (true) {
				cur = rString.charAt(charPtr);
				if (cur == '\n')
					break;
				rid += cur;
				charPtr++;
			}
			System.out.println(rid);
			charPtr++;

			while (true) {
				cur = rString.charAt(charPtr);
				if (cur == '\n')
					break;
				rParam += cur;
				charPtr++;
			}
			System.out.println(rParam);
			charPtr++;

			while (true) {
				cur = rString.charAt(charPtr);
				if (cur == '\n')
					break;
				rType += cur;
				charPtr++;
			}
			System.out.println(rType);
			charPtr++;

			rEffects.add(new rEffect(ceCount, r.getID(), Integer.parseInt(rid),
					rParam, rType));
		}

		r.setRCauses(rCauses);
		r.setREffects(rEffects);
		// r.updateTreeData();
		r.updateTreeData(r.getBoolSequence());
		db.updateRule(r);

		rules = db.getAllRules();
		Elements = new String[rules.size()];

		for (int i = 0; i < rules.size(); i++) {
			Elements[i] = rules.get(i).getName();
		}

		@SuppressWarnings("rawtypes")
		ArrayAdapter a = new ArrayAdapter<String>(this,
				R.layout.activity_sharelist, Elements);

		setListAdapter(a);
		a.notifyDataSetChanged();
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("NFC Sharing").setMessage("Rule received!")
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		adb.show();
		db.close();
	}

	/**
	 * Grabs Ndef messages from an NFC push from another Android device
	 * 
	 * @param intent
	 *            Used to check for an Ndef discovered action
	 * @return array of received Ndefs
	 */
	NdefMessage[] getNdefMessages(Intent intent) {
		// Parse the intent
		NdefMessage[] msgs = null;
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
		} else {
			Log.d(TAG, "Unknown intent.");
			finish();
		}
		return msgs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This is called when the Home (Up) button is pressed
			// in the Action Bar.
			Intent parentActivityIntent = new Intent(this, MainActivity.class);
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}