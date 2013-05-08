package com.example.ceandroid;

import java.util.ArrayList;

import CEapi.rCause;
import CEapi.rEffect;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;

/**
 * The EditRule class is the main Activity for rule creation This Activity is
 * used for creating, editing, and viewing rules The Rule creation constantly
 * comes back to this screen to show the user the progress on the rule creation
 * The rCauses, rCause boolean logic, rEffects, Rule Name, and Rule Active
 * Status are all displayed on this Activity
 * 
 * @author CEandroid SMU
 */
public class EditRule extends Activity implements
		DeleteDialogFragment.DeleteDialogListener {
	/**
	 * CEapp contains the globally accessible variables
	 */
	private CEapp app = (CEapp) this.getApplication();
	/**
	 * The type of rCause or rEffect used for Enums
	 */
	int type = 0;
	/**
	 * Generic size variable
	 */
	static int size = 0;
	/**
	 * The position of the rCause or rEffect that is to be deleted
	 */
	static int delPos = 0;
	/**
	 * The list of rCauses
	 */
	ArrayList<EditRuleNode> cList = new ArrayList<EditRuleNode>();
	/**
	 * The list of ANDs and ORs for rCauses
	 */
	ArrayList<EditRuleNode> bools = new ArrayList<EditRuleNode>();
	/**
	 * The list of rEffects
	 */
	ArrayList<String> eList = new ArrayList<String>();
	/**
	 * The current rCause being updated
	 */
	ArrayAdapter<EditRuleNode> cA;
	/**
	 * The current rEffect being updated
	 */
	ArrayAdapter<String> eA;
	/**
	 * The ListView containing all of the rCauses
	 */
	ListView cListView;
	/**
	 * The ListView containing all of the rEffects
	 */
	ListView eListView;
	/**
	 * TextWatcher updates the Rules named as it is changed
	 */
	TextWatcher textWatcher = new TextWatcher() {

		/**
		 * As the name is changing
		 * 
		 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence,
		 *      int, int, int)
		 */
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		/**
		 * Just before the name is changed
		 * 
		 * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence,
		 *      int, int, int)
		 */
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		/**
		 * After the name has been changed The rule name is updated at this time
		 * 
		 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
		 */
		public void afterTextChanged(Editable s) {
			app.currentRule.setName(s.toString());
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			db.updateName(app.currentRule.getID(), app.currentRule.getName());
			db.close();
		}
	};

	/**
	 * When the Activity is resumed, the textWatcher is added again
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		((EditText) this.findViewById(R.id.textView1))
				.addTextChangedListener(textWatcher);
	}

	/**
	 * Loads all rule information for a new rule or a specific rule in the
	 * database This include rCauses, rCause boolean logic, rEffects, Rule Name,
	 * and Rule Active Status
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_rule);

		// Turn on "up" navigation
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Reset Focus
		RelativeLayout myLayout = (RelativeLayout) this
				.findViewById(R.id.relativeEditRule);
		myLayout.requestFocus();

		// Create Lists
		cListView = (ListView) findViewById(R.id.causes);
		eListView = (ListView) findViewById(R.id.effects);
		cA = new ArrayAdapter<EditRuleNode>(this,
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, cList);
		eA = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, eList);
		cListView.setAdapter(cA);
		eListView.setAdapter(eA);

		// Load the kind of list
		app = (CEapp) this.getApplication();

		bools = app.currentRule.getBoolSequence();

		if (app.edit) // editing a cause or effect
		{
			DatabaseHandler db = new DatabaseHandler(this);
			if (app.editType) // Cause
			{
				size = app.currentRule.getRCauses().size();
				bools = app.currentRule.getBoolSequence();

				// added check - if > 0 then can't get negative index
				if (size > 0) {
					app.currentRule.getRCauses()
							.get(app.currentRule.getRCauses().size() - 1)
							.setID(app.editedNumber);
					db.updateRCause(
							app.currentRule.getRCauses().get(
									app.currentRule.getRCauses().size() - 1),
							app.currentRule);
					if (db.duplicateRCauses(app.currentRule)) {
						size = app.currentRule.getRCauses().size();
						db.deleteRCause(db.getRCauseByID(app.editedNumber));
					}
				}
			} else // Effect
			{
				int eSize = app.currentRule.getREffects().size();

				// added check - if > 0 then can't get negative index
				if (eSize > 0) {
					app.currentRule.getREffects()
							.get(app.currentRule.getREffects().size() - 1)
							.setID(app.editedNumber);
					db.updateREffect(
							app.currentRule.getREffects().get(
									app.currentRule.getREffects().size() - 1),
							app.currentRule);
					if (db.duplicateREffects(app.currentRule)) {
						db.deleteREffect(db.getREffectByID(app.editedNumber));
					}
				}
			}
			app.currentRule
					.setRCauses(db.getAllRCauses(app.currentRule.getID()));
			app.currentRule.setREffects(db.getAllREffects(app.currentRule
					.getID()));

			// remove bools until right amount in rule
			while (app.currentRule.getRCauses().size() - bools.size() != 1
					&& !bools.isEmpty()) {
				bools.remove(bools.size() - 1);
			}
			app.currentRule.updateTreeData(bools);
			db.updateRule(app.currentRule);
			bools = app.currentRule.getBoolSequence();
			db.close();
		}

		// Fill Out Form
		EditText title = (EditText) this.findViewById(R.id.textView1);
		title.setText(app.currentRule.getName());
		Switch active = (Switch) this.findViewById(R.id.switch1);
		active.setChecked(app.currentRule.Active);

		// toggle on/off with both clicks and swipes
		active.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
			/**
			 * Called when the Rule Active Status is changed The onSwitchClicked
			 * function is then called to update the value for the rule
			 * 
			 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton,
			 *      boolean)
			 */
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				onSwitchClicked(arg0);
			}
		});

		if (app.currentRule.edited) // general adding of causes or effects
		{
			DatabaseHandler db = new DatabaseHandler(this);
			bools = app.currentRule.getBoolSequence();
			if (db.updateRule(app.currentRule) > 0) {
				app.currentRule.setID(db.getAllRules()
						.get(db.getRulesCount() - 1).getID());
			}
			app.currentRule
					.setRCauses(db.getAllRCauses(app.currentRule.getID()));
			app.currentRule.setREffects(db.getAllREffects(app.currentRule
					.getID()));

			// add bools if rCause added
			if (app.addedRCause && !app.edit
					&& (size != app.currentRule.getRCauses().size())) {
				bools.add(new EditRuleNode("OR", false, true));
				size = app.currentRule.getRCauses().size();
				app.addedRCause = false;
			}

			app.currentRule.updateTreeData(bools);
			db.updateRule(app.currentRule);
			bools = app.currentRule.getBoolSequence();
			app.currentRule.edited = false;
			app.edit = false;
			db.close();
		}

		// clear bools if only 1 cause
		if (app.currentRule.getRCauses().size() <= 1) {
			bools.clear();
		}

		DatabaseHandler db = new DatabaseHandler(this);
		if (app.currentRule != null) {
			app.currentRule.updateTreeData(bools);
			db.updateRule(app.currentRule);
			bools = app.currentRule.getBoolSequence();
		}
		db.close();

		// add everything to lists
		for (int i = 0; i < app.currentRule.getRCauses().size(); i++) {
			rCause r = app.currentRule.getRCauses().get(i);

			if (!r.getType().equals("location")) {
				cList.add(new EditRuleNode(r.getName() + "\n"
						+ r.getParameters(), true, false));
			} else {
				String param = r.getParameters();
				String finalParam = "";
				char cur = ' ';
				int start = 0;
				while (cur != '\n') {
					cur = param.charAt(start);
					finalParam += cur;
					start++;
				}
				cList.add(new EditRuleNode(r.getName() + "\n" + finalParam,
						true, false));
			}

			if (i < bools.size() && !bools.isEmpty()) {
				cList.add(bools.get(i));
			}
		}

		for (int i = 0; i < app.currentRule.getREffects().size(); i++) {
			rEffect r = app.currentRule.getREffects().get(i);
			String finalParam = r.getParameters();
			if (r.getType().equals("sound")) {
				finalParam = finalParam.split("\\n")[1];
			}

			if (r.getType().equals("toast")) {
				eList.add("Popup Text" + "\n" + finalParam);
			} else {
				eList.add(r.getName() + "\n" + finalParam);
			}
		}

		// Add Adapters
		cList.add(new EditRuleNode("+", false, false));
		eList.add("+");
	}

	/**
	 * Creates the Menu for the EditRule Activity
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_edit_task, menu);
		return true;
	}

	/**
	 * Actions for the EditRule Menu
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
			Intent parentActivityIntent = new Intent(this, MyRules.class);
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			finish();
			return true;
		}
		default: {
			return super.onOptionsItemSelected(item);
		}
		}
	}

	/**
	 * Called when the Rule Activity status is changed This value is updated in
	 * the current rule and database
	 * 
	 * @param view
	 */
	public void onSwitchClicked(View view) {
		// Is the switch on?
		boolean on = ((Switch) view).isChecked();
		app.currentRule.Active = on;
		DatabaseHandler db = new DatabaseHandler(this);
		db.updateActive(app.currentRule.getID(), on);
		db.close();
	}

	/**
	 * This is called when a delete of a rCause or rEffect is confirmed
	 * 
	 * @see com.example.ceandroid.DeleteDialogFragment.DeleteDialogListener#onDialogPositiveClick(android.app.DialogFragment)
	 */
	public void onDialogPositiveClick(DialogFragment dialog) {
		// clicked to delete the cause or effect
		DatabaseHandler db = new DatabaseHandler(this);
		if (app.editType) // cause
		{
			db.deleteRCause(db.getRCauseByID(app.editedNumber));
			if (!bools.isEmpty() && bools.size() == delPos) {
				bools.remove(bools.size() - 1);
			} else if (!bools.isEmpty()) {
				bools.remove(delPos);
			}
		} else // effect
		{
			db.deleteREffect(db.getREffectByID(app.editedNumber));
		}
		app.currentRule.setRCauses(db.getAllRCauses(app.currentRule.getID()));
		app.currentRule.setREffects(db.getAllREffects(app.currentRule.getID()));
		app.currentRule.updateTreeData(bools);
		bools = app.currentRule.getBoolSequence();
		db.close();
		finish();
		startActivity(getIntent());
	}

	/**
	 * This is called when the delete of a rCause or rEffect is cancelled
	 * 
	 * @see com.example.ceandroid.DeleteDialogFragment.DeleteDialogListener#onDialogNegativeClick(android.app.DialogFragment)
	 */
	public void onDialogNegativeClick(DialogFragment dialog) {
		// clicked cancel, do not do anything
	}

	/**
	 * Prints the list of booleans used for the Cause Tree within the current
	 * rule Primarily used for debugging
	 * 
	 * @param bools
	 */
	public void print(ArrayList<EditRuleNode> bools) {
		for (int i = 0; i < bools.size(); i++) {
			System.out.println(bools.get(i).value);
		}
	}
}