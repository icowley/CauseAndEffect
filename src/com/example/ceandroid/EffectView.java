package com.example.ceandroid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import CEapi.Effect;
import CEapi.rCause;
import CEapi.rEffect;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * EffectView Class displays the list of Effects
 * 
 * @author CEandroid SMU
 */
public class EffectView extends FragmentActivity {
	/**
	 * Elements array holds and empty array that is later used for cause
	 * information
	 */
	static String[] Elements = new String[] { " ", " ", " ", " ", " ", " ",
			" ", " ", " " };
	/**
	 * List View used to list out the Effects
	 */
	private ListView listView;
	/**
	 * Database Handler used to get the list of Effects
	 */
	private DatabaseHandler db;
	/**
	 * gPosition used to handle user actions on the list of Effects
	 */
	int gPosition = 0;
	/**
	 * The type of Effect that is selected
	 */
	int type = 0;
	/**
	 * List of Effect Objects
	 */
	private List<Effect> effects = new ArrayList<Effect>();
	/**
	 * Used to access global application variables
	 */
	private CEapp app;

	/**
	 * List of effects currently being used in the application This can be
	 * removed if strings are used for the switch statement
	 * 
	 * @author CEandroid SMU
	 */
	public enum eEnum {
		vibrate, toast, notification, sound, ringer
	}

	/**
	 * OnCreate gets the list of Effects from the database and adds them to the
	 * ListView
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new DatabaseHandler(this);

		setContentView(R.layout.item_list);

		// instantiate listView
		listView = (ListView) findViewById(R.id.fruitList);

		listView.setTextFilterEnabled(true);

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
	 * makeList() is what is used by the onCreate to create the list of Effects
	 */
	public void makeList() {
		effects = db.getAllEffects();
		Elements = new String[effects.size()];
		for (int i = 0; i < effects.size(); i++) {
			if (effects.get(i).getName().equals("Toast")) {
				Elements[i] = "Popup Text";
			} else {
				Elements[i] = effects.get(i).getName();
			}
		}
	}

	/**
	 * doSomething is called when the user selects an Effect from the list
	 * 
	 * @param position
	 * @param type
	 */
	public void doSomething(int position, int type) {
		// Enums Switch
		switch (eEnum.valueOf(effects.get(position).getType())) {
		case notification: {
			final Dialog d = new Dialog(this);
			d.setContentView(R.layout.ce_dialog_notification);
			d.setTitle("New Notification");
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

					EditText edit = (EditText) d.findViewById(R.id.title);
					String title = edit.getText().toString();
					EditText edit2 = (EditText) d.findViewById(R.id.text);
					String text = edit2.getText().toString();
					d.dismiss();

					rEffect re = new rEffect();
					re.setName("Notification");
					re.setRuleID(app.currentRule.getID());
					re.setType("notification");
					re.setEffectID(3);
					String parameter = title + "'" + text + "'";
					re.setParameters(parameter);
					app.currentRule.addREffect(re);
					Intent myIntent = new Intent(getApplicationContext(),
							EditRule.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(myIntent);
				}
			});
			d.show();
		}
			break;
		case toast: {
			final Dialog d = new Dialog(this);
			d.setContentView(R.layout.ce_dialog_toast);
			d.setTitle("New Popup Text");
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
					EditText edit2 = (EditText) d.findViewById(R.id.text);
					String text = edit2.getText().toString();
					d.dismiss();

					rEffect re = new rEffect();
					re.setName("Toast");
					re.setRuleID(app.currentRule.getID());
					re.setType("toast");
					re.setEffectID(1);
					String parameter = "Text:" + text;
					re.setParameters(parameter);
					app.currentRule.addREffect(re);
					Intent myIntent = new Intent(getApplicationContext(),
							EditRule.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(myIntent);
				}
			});
			d.show();
		}
			break;
		case vibrate: {
			rEffect re = new rEffect();
			re.setName("Vibrate");
			re.setRuleID(app.currentRule.getID());
			re.setType("vibrate");
			re.setEffectID(2);
			String parameter = "Long";
			re.setParameters("Tone Length:" + parameter);
			app.currentRule.addREffect(re);
			Intent myIntent = new Intent(this.getApplicationContext(),
					EditRule.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(myIntent);
		}
			break;
		case sound: {
			showFileChooser();
		}
			break;
		case ringer: {
			Dialog d = onCreateRingerDialog();
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
			d.show();
		}
			break;
		default:
			break;
		}
		db.close();
	}

	// Time Picker Dialog
	@SuppressLint("ValidFragment")
	public class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {
		/*
		 * (non-Javadoc)
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.app.TimePickerDialog.OnTimeSetListener#onTimeSet(android.
		 * widget.TimePicker, int, int)
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.app.DialogFragment#onDismiss(android.content.DialogInterface)
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
	 * Called when the user is done setting parameters for a Effect and the
	 * picker is returned to EffectView Creates rEffects to be associated with
	 * the Rule based on the user's parameters
	 * 
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
	 *      android.content.Intent)
	 */
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
		if (reqCode == 3) {
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				String fileName = "";
				String scheme = contactData.getScheme();

				if (scheme.equals("file")) {
					fileName = contactData.getLastPathSegment();
				} else if (scheme.equals("content")) {
					String[] proj = { MediaStore.Images.Media.TITLE };
					Cursor cursor = this.getContentResolver().query(
							contactData, proj, null, null, null);
					if (cursor != null && cursor.getCount() != 0) {
						int columnIndex = cursor
								.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
						cursor.moveToFirst();
						fileName = cursor.getString(columnIndex);
					}
				}

				rEffect re = new rEffect();
				re.setName("Play Sound");
				re.setRuleID(app.currentRule.getID());
				re.setType("sound");
				re.setEffectID(4);
				String parameter = contactData.toString() + "\n" + fileName;
				re.setParameters(parameter);
				app.currentRule.addREffect(re);
				Intent myIntent = new Intent(this.getApplicationContext(),
						EditRule.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent);
			} else {
				if (app.edit) {
					Intent myIntent = new Intent(this.getApplicationContext(),
							EditRule.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(myIntent);
				}
			}
		}
		if (app.edit) {
			Intent myIntent = new Intent(this.getApplicationContext(),
					EditRule.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(myIntent);
		}
	}

	/**
	 * Selects an audio file chooser to choose an audio file
	 */
	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select a File to Play"), 3);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(this.getApplicationContext(),
					"Please install a File Manager.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * Ring Mode Dialog Creator
	 * 
	 * @return Dialog Ring Mode Dialog
	 */
	public Dialog onCreateRingerDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Set the dialog title
		builder.setTitle("Ring Mode")
				.setItems(R.array.ring_mode,
						new android.content.DialogInterface.OnClickListener() {
							/*
							 * (non-Javadoc)
							 * 
							 * @see
							 * android.content.DialogInterface.OnClickListener
							 * #onClick(android.content.DialogInterface, int)
							 */
							public void onClick(DialogInterface dialog,
									int which) {
								rEffect re = new rEffect();
								re.setName("Ring Mode");
								re.setRuleID(app.currentRule.getID());
								re.setType("ringer");
								re.setEffectID(5);
								String parameter = "";
								switch (which) {
								case 0:
									parameter = "normal";
									break;
								case 1:
									parameter = "vibrate";
									break;
								case 2:
									parameter = "silent";
									break;
								default:
									parameter = "normal";
									break;
								}
								re.setParameters("Ring mode: " + parameter);
								app.currentRule.addREffect(re);
								Intent myIntent = new Intent(
										getApplicationContext(), EditRule.class)
										.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(myIntent);
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							/*
							 * (non-Javadoc)
							 * 
							 * @see
							 * android.content.DialogInterface.OnClickListener
							 * #onClick(android.content.DialogInterface, int)
							 */
							public void onClick(DialogInterface dialog, int id) {
								if (app.edit) {
									Intent myIntent = new Intent(
											getApplicationContext(),
											EditRule.class)
											.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(myIntent);
								}
							}
						});
		return builder.create();
	}

	/**
	 * EffectView Menu Creation
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_edit_task, menu);
		return true;
	}

	/**
	 * EffectView Menu Actions
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