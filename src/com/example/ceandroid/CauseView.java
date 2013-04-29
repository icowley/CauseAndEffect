package com.example.ceandroid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.ceandroid.Causes.MapPicker;

import CEapi.Cause;
import CEapi.rCause;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * CauseView Class displays the list of Causes
 * 
 * @author CEandroid SMU
 */
public class CauseView extends FragmentActivity {
	/**
	 * Elements array holds and empty array that is later used for Cause
	 * information
	 */
	static String[] Elements = new String[] { " ", " ", " ", " ", " ", " ",
			" ", " ", " " };
	/**
	 * List View used to list out the Causes
	 */
	private ListView listView;
	/**
	 * Database Handler used to get the list of Causes
	 */
	private DatabaseHandler db;
	/**
	 * gPosition used to handle user actions on the list of Causes
	 */
	int gPosition = 0;
	/**
	 * The type of Cause that is selected
	 */
	int type = 0;
	/**
	 * List of Cause Objects
	 */
	private List<Cause> causes = new ArrayList<Cause>();
	/**
	 * Used to access global application variables
	 */
	private CEapp app;

	/**
	 * List of Causes currently being used in the application This can be
	 * removed if strings are used for the switch statement
	 * 
	 * @author CEandroid SMU
	 */
	public enum cEnum {
		time, phoneCall, textMessage, ssid, wifiStatus, location
	}

	/**
	 * OnCreate gets the list of Causes from the database and adds them to the
	 * ListView
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_list);
		listView = (ListView) findViewById(R.id.fruitList);
		listView.setTextFilterEnabled(true);
		db = new DatabaseHandler(this);

		// Set up the action bar
		final ActionBar actionbar = getActionBar();
		if (actionbar != null) {
			// Turn on "up" navigation
			actionbar.setDisplayHomeAsUpEnabled(true);
		}

		// Load the kind of list
		app = (CEapp) getApplication();
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

		if (app.edit) {
			if (app.editType) // cause
			{
				doSomething(app.editID, 2);
			} else // effect
			{
				doSomething(app.editID, 3);
			}
		}
		db.close();
	}

	/**
	 * makeList() is what is used by the onCreate to create the list of Causes
	 */
	public void makeList() {
		// DatabaseHandler db = new DatabaseHandler(this.this);
		causes = db.getAllCauses();
		// db.close();
		Elements = new String[causes.size()];
		for (int i = 0; i < causes.size(); i++) {
			Elements[i] = causes.get(i).getName();
		}
	}

	/**
	 * doSomething is called when the user selects a Cause from the list
	 * 
	 * @param position
	 * @param type
	 */
	public void doSomething(int position, int type) {
		// Enums Switch
		switch (cEnum.valueOf(causes.get(position).getType())) {
		case time: {
			DialogFragment newFragment = new TimePickerFragment();
			newFragment.show(getFragmentManager(), "timePicker");
		}
			break;
		case phoneCall: {
			Intent intent = new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, 1);
		}
			break;
		case textMessage: {
			Intent intent = new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, 2);
		}
			break;
		case ssid: {
			// Toast.makeText(this.getApplicationContext(),
			// "SSID not implemented", Toast.LENGTH_SHORT).show();
			final Dialog d = new Dialog(this);
			d.setContentView(R.layout.ce_dialog_ssid);
			d.setTitle("Add SSID here");
			d.setOnDismissListener(new OnDismissListener() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * android.content.DialogInterface.OnDismissListener#onDismiss
				 * (android.content.DialogInterface)
				 */
				public void onDismiss(DialogInterface arg0) {
					if (app.edit) {
						Intent myIntent = new Intent(getApplicationContext(),
								EditRule.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(myIntent);
					}
				}

			});
			Button button = (Button) d.findViewById(R.id.button1);
			button.setOnClickListener(new OnClickListener() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * android.view.View.OnClickListener#onClick(android.view.View)
				 */
				public void onClick(View v) {
					EditText edit2 = (EditText) d.findViewById(R.id.ssid);
					String text = edit2.getText().toString();
					d.dismiss();

					gPosition = 5;
					rCause rc = new rCause();
					rc.setName(causes.get(gPosition).getName());
					rc.setRuleID(app.currentRule.getID());
					rc.setType(causes.get(gPosition).getType());
					rc.setCauseID(4);
					String parameter = "" + text;
					rc.setParameters(parameter);
					app.currentRule.addRCause(rc);
					app.addedRCause = true;
					Intent myIntent = new Intent(getApplicationContext(),
							EditRule.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(myIntent);
				}
			});
			d.show();
		}
			break;
		case wifiStatus: {
			// Toast.makeText(this.getApplicationContext(),
			// "Wifi On/Off not implemented", Toast.LENGTH_SHORT).show();
			final Dialog d = new Dialog(this);
			d.setContentView(R.layout.ce_dialog_switch);
			d.setTitle("Check if Wifi is on or off");
			d.setOnDismissListener(new OnDismissListener() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * android.content.DialogInterface.OnDismissListener#onDismiss
				 * (android.content.DialogInterface)
				 */
				public void onDismiss(DialogInterface arg0) {
					if (app.edit) {
						Intent myIntent = new Intent(getApplicationContext(),
								EditRule.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(myIntent);
					}
				}

			});
			d.setOnDismissListener(new OnDismissListener() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * android.content.DialogInterface.OnDismissListener#onDismiss
				 * (android.content.DialogInterface)
				 */
				public void onDismiss(DialogInterface arg0) {
					if (app.edit) {
						Intent myIntent = new Intent(getApplicationContext(),
								EditRule.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(myIntent);
					}
				}

			});
			Button button = (Button) d.findViewById(R.id.button1);
			button.setOnClickListener(new OnClickListener() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * android.view.View.OnClickListener#onClick(android.view.View)
				 */
				public void onClick(View v) {
					Switch s1 = (Switch) d.findViewById(R.id.dialogSwitch);
					String text = "";
					if (s1.isChecked())
						text = "on";
					else
						text = "off";
					d.dismiss();

					gPosition = 6;
					rCause rc = new rCause();
					rc.setName(causes.get(gPosition).getName());
					rc.setRuleID(app.currentRule.getID());
					rc.setType(causes.get(gPosition).getType());
					rc.setCauseID(5);
					String parameter = text;
					rc.setParameters(parameter);
					app.currentRule.addRCause(rc);
					app.addedRCause = true;
					Intent myIntent = new Intent(getApplicationContext(),
							EditRule.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(myIntent);
				}
			});
			d.show();
		}
			break;
		case location: {
			Intent intent = new Intent(this.getApplicationContext(),
					MapPicker.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			app.setTemp(position);
			startActivityForResult(intent, 2);
		}
			break;
		default:
			break;
		}
		db.close();
	}

	// Time Picker Dialog
	/**
	 * TimePickerFragment is called when the user selects the Time Cause
	 * 
	 * @author CEandroid SMU
	 */
	@SuppressLint("ValidFragment")
	public class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {
		/**
		 * Creates the TimePickerFragment
		 * 
		 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		/**
		 * Called when the Time is confirmed by the user
		 * 
		 * @see android.app.TimePickerDialog.OnTimeSetListener#onTimeSet(android.widget.TimePicker,
		 *      int, int)
		 */
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			gPosition = 2;
			rCause rc = new rCause();
			rc.setName("Time");
			rc.setRuleID(app.currentRule.getID());
			rc.setType("time");
			rc.setCauseID(1);
			String parameter = "";
			if (hourOfDay < 10) {
				parameter = "0" + hourOfDay;
			} else {
				parameter = "" + hourOfDay;
			}
			parameter += ":";
			if (minute < 10) {
				rc.setParameters(parameter + "0" + minute);
			} else {
				rc.setParameters(parameter + minute);
			}
			app.currentRule.addRCause(rc);
			app.addedRCause = true;

			Intent myIntent = new Intent(getApplicationContext(),
					EditRule.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(myIntent);
		}

		/**
		 * Called when the user backs out or cancels the TimePickerFragment
		 * 
		 * @see android.app.DialogFragment#onDismiss(android.content.DialogInterface)
		 */
		@Override
		public void onDismiss(DialogInterface dialog) {
			if (app.edit) {
				Intent myIntent = new Intent(getApplicationContext(),
						EditRule.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent);
			}
		}
	}

	/**
	 * Called when the user is done setting parameters for a Cause and the
	 * picker is returned to CauseView Creates rCauses to be associated with the
	 * Rule based on the user's parameters
	 * 
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
	 *      android.content.Intent)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
		switch (reqCode) {
		case (1):
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c = this.managedQuery(contactData, null, null, null,
						null);
				if (c.moveToFirst()) {
					gPosition = 0;
					String name = c
							.getString(c
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					int hasNumber = Integer
							.parseInt(c.getString(c
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
					if (hasNumber != 0) {
						rCause rc = new rCause();
						rc.setName(causes.get(gPosition).getName());
						rc.setRuleID(app.currentRule.getID());
						rc.setType(causes.get(gPosition).getType());
						rc.setCauseID(2);
						String parameter = name;
						rc.setParameters(parameter);
						app.currentRule.addRCause(rc);
						app.addedRCause = true;

						Intent myIntent = new Intent(
								this.getApplicationContext(), EditRule.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(myIntent);
					} else {
						Toast.makeText(this.getApplicationContext(),
								"Please choose a contact with a valid number",
								Toast.LENGTH_LONG).show();
						Intent intent = new Intent(Intent.ACTION_PICK,
								ContactsContract.Contacts.CONTENT_URI)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivityForResult(intent, 1);
					}
				} else {
					if (app.edit) {
						Intent myIntent = new Intent(
								this.getApplicationContext(), EditRule.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(myIntent);
					}
				}
			}
			break;
		case (2):
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c = this.managedQuery(contactData, null, null, null,
						null);
				if (c.moveToFirst()) {
					gPosition = 1;
					String name = c
							.getString(c
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					int hasNumber = Integer
							.parseInt(c.getString(c
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
					if (hasNumber != 0) {
						rCause rc = new rCause();
						rc.setName(causes.get(gPosition).getName());
						rc.setRuleID(app.currentRule.getID());
						rc.setType(causes.get(gPosition).getType());
						rc.setCauseID(3);
						String parameter = name;
						rc.setParameters(parameter);
						app.currentRule.addRCause(rc);
						app.addedRCause = true;

						Intent myIntent = new Intent(
								this.getApplicationContext(), EditRule.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(myIntent);
					} else {
						Toast.makeText(this.getApplicationContext(),
								"Please choose a contact with a valid number",
								Toast.LENGTH_LONG).show();
						Intent intent = new Intent(Intent.ACTION_PICK,
								ContactsContract.Contacts.CONTENT_URI)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivityForResult(intent, 1);
					}
				} else {
					if (app.edit) {
						Intent myIntent = new Intent(
								this.getApplicationContext(), EditRule.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(myIntent);
					}
				}
			}
			break;
		default:
			Intent myIntent = new Intent(this.getApplicationContext(),
					EditRule.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(myIntent);
		}
		if (app.edit) {
			Intent myIntent = new Intent(this.getApplicationContext(),
					EditRule.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(myIntent);
		}
	}

	/**
	 * CauseView Menu Creation
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_edit_task, menu);
		return true;
	}

	/**
	 * CauseView Menu Actions
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
			Intent parentActivityIntent = new Intent(this, EditRule.class);
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			finish();
			return true;
		}
		case R.id.menu_help: {
			Intent myIntent = new Intent(this, Help.class)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(myIntent);
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