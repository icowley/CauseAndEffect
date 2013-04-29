package com.example.ceandroid;

import java.util.ArrayList;

import CEapi.rCause;
import CEapi.rEffect;
import android.app.Activity;
import android.app.PendingIntent;
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
import android.widget.TextView;

/**
 * Page to push rules to another device via NFC
 * 
 * @author CEandroid SMU
 * 
 */
public class ShareNFC extends Activity {
	/** Used to get the current rule selected */
	private CEapp app;

	/** Tag for NFC */
	private static final String TAG = "ceandroid";

	/** Adapter for NFC usage. */
	NfcAdapter nAdapter;

	/** PendingIntent for NFC usage. */
	PendingIntent nPendingIntent;

	/** Filters for Ndef exchange. */
	IntentFilter[] nExchangeFilters;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		enableNdefExchangeMode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
		nAdapter.disableForegroundNdefPush(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sharenfc);

		// Turn on "up" navigation
		getActionBar().setDisplayHomeAsUpEnabled(true);

		app = (CEapp) getApplication();

		// NFC adapter initialization
		nAdapter = NfcAdapter.getDefaultAdapter(this);

		// this activity handles NFC intents
		nPendingIntent = PendingIntent.getActivity(this, 0, new Intent(
				getBaseContext(), ShareList.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);

		// intent filters for p2p exchange
		@SuppressWarnings("static-access")
		IntentFilter ndefDetected = new IntentFilter(
				nAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefDetected.addDataType("text/plain");
		} catch (MalformedMimeTypeException e) {
		}
		nExchangeFilters = new IntentFilter[] { ndefDetected };

		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText("Ready to send: \n\"" + app.currentRule.getName() + "\"");
	}

	/**
	 * Grabs Ndef messages as they are discovered.
	 * 
	 * @param intent
	 *            used to find when Ndefs are discovered
	 * @return array of Ndef messages
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

	/**
	 * Enables exchange of NFC data.
	 * 
	 */
	@SuppressWarnings("deprecation")
	private void enableNdefExchangeMode() {
		nAdapter.enableForegroundNdefPush(ShareNFC.this, getRuleAsNdef());
		nAdapter.enableForegroundDispatch(this, nPendingIntent,
				nExchangeFilters, null);
	}

	/**
	 * Converts a rule to an Ndef payload.
	 * 
	 * @return NdefMessage with rule string as its payload
	 */
	private NdefMessage getRuleAsNdef() {
		String rString = "";
		Rule r = app.currentRule;
		ArrayList<rCause> rCauses = r.getRCauses();
		ArrayList<rEffect> rEffects = r.getREffects();

		rString += r.getName() + '\n' + r.getTreeData() + '\n';

		// add number of rCauses for use in reading string
		rString += String.valueOf(rCauses.size()) + '\n';
		for (int i = 0; i < rCauses.size(); i++) {
			rCause cur = rCauses.get(i);
			if (cur.getParameters().equals(""))
				cur.setParameters(" ");
			rString += String.valueOf(cur.getCauseID()) + '\n'
					+ cur.getParameters() + '\n' + cur.getType() + '\n';
		}

		// do the same for rEffects
		rString += String.valueOf(rEffects.size()) + '\n';
		for (int i = 0; i < rEffects.size(); i++) {
			rEffect cur = rEffects.get(i);
			if (cur.getParameters().equals(""))
				cur.setParameters(" ");
			rString += String.valueOf(cur.getEffectID()) + '\n'
					+ cur.getParameters() + '\n' + cur.getType() + '\n';
		}

		byte[] rBytes = rString.getBytes();
		NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"text/plain".getBytes(), new byte[] {}, rBytes);
		return new NdefMessage(new NdefRecord[] { record });
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
			Intent parentActivityIntent = new Intent(this, ShareList.class);
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
