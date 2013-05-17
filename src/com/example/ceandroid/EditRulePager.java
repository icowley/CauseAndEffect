package com.example.ceandroid;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class EditRulePager extends FragmentActivity implements
		DeleteDialogFragment.DeleteDialogListener {
	/**
	 * CEapp contains the globally accessible variables
	 */
	private CEapp app;

	/**
	 * The ViewPager for our Cause and Effect fragment.
	 */
	ViewPager mViewPager;

	/**
	 * The adapter for our ViewPager.
	 */
	TabsAdapter mTabsAdapter;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_rule_pager);
		app = (CEapp) getApplication();

		// Reset focus.
		findViewById(R.id.edit_rule_layout).requestFocus();

		// Create ViewPager and add our tabs.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		final ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		bar.setDisplayHomeAsUpEnabled(true);

		mTabsAdapter = new TabsAdapter(this, mViewPager);
		mTabsAdapter.addTab(bar.newTab().setText("Cause"), CauseFragment.class,
				null);
		mTabsAdapter.addTab(bar.newTab().setText("Effect"),
				EffectFragment.class, null);

		if (savedInstanceState != null) {
			bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
		}

		// Fill Out Form
		EditText title = (EditText) findViewById(R.id.rule_name);
		String s = app.currentRule.getName();
		Log.d("IC", s);
		if (s != null) {
			title.setText(app.currentRule.getName());
		}
		Switch active = (Switch) findViewById(R.id.rule_switch);
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
			public void onCheckedChanged(CompoundButton button, boolean arg1) {
				onSwitchClicked(button);
			}
		});
	}

	/**
	 * When the Activity is resumed, the textWatcher is added again
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		((EditText) this.findViewById(R.id.rule_name))
				.addTextChangedListener(textWatcher);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
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

	public void onDialogPositiveClick(DialogFragment dialog) {
		// clicked to delete the cause or effect
		finish();
		startActivity(getIntent());
	}

	public void onDialogNegativeClick(DialogFragment dialog) {
		// Do nothing, cancel was clicked.
	}
}
