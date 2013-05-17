package com.example.ceandroid;

import java.util.ArrayList;

import CEapi.rEffect;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class EffectFragment extends ListFragment {

	/**
	 * CEapp contains the globally accessible variables
	 */
	private CEapp app;

	/**
	 * Activity this view is associated with.
	 */
	private FragmentActivity mCallback;

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
	 * The list of rEffects
	 */
	ArrayList<String> eList = new ArrayList<String>();

	/**
	 * The current rEffect being updated
	 */
	ArrayAdapter<String> eA;

	/**
	 * The ListView containing all of the rEffects
	 */
	ListView eListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		eA = new ArrayAdapter<String>(mCallback,
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, eList);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback = (FragmentActivity) activity;
		app = (CEapp) mCallback.getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_effect,
				container, false);
		return root;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		eListView = getListView();
		eListView.setAdapter(eA);
		setListeners();
	}

	private void setListeners() {
		eListView.setOnItemClickListener(new OnItemClickListener() {
			/**
			 * Effect List Item Clicked Calls the EffectView page for this
			 * Effect
			 * 
			 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
			 *      android.view.View, int, long)
			 */
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int size = arg0.getAdapter().getCount();
				app.editType = false;
				if (size == (arg2 + 1)) {
					app.edit = false;
					Intent myIntent = new Intent(mCallback, EffectView.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(myIntent);
				} else {
					app.edit = true;
					app.editID = convertTypeToEEnum(app.currentRule
							.getREffects().get(arg2).getType());
					app.editedNumber = app.currentRule.getREffects().get(arg2)
							.getID();
					if (app.currentRule.getREffects().get(arg2).getType()
							.equals("vibrate")) {
						Toast.makeText(mCallback,
								"Cannot edit vibrate effects",
								Toast.LENGTH_LONG).show();
					} else {
						Intent myIntent = new Intent(mCallback,
								EffectView.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(myIntent);
					}
				}
			}
		});
		eListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			/**
			 * Called when a Effect has a long tap Delete dialog is called where
			 * appropriate
			 * 
			 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView,
			 *      android.view.View, int, long)
			 */
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				int size = arg0.getAdapter().getCount();
				if (arg2 < app.currentRule.getREffects().size()) {
					if (size == (arg2 + 1)) {
						// do nothing, '+' was selected
					} else {
						app.editType = false;
						app.editedNumber = app.currentRule.getREffects()
								.get(arg2).getID();
						DialogFragment newFragment = new DeleteDialogFragment();
						newFragment.show(mCallback.getSupportFragmentManager(),
								"delete");
					}
				}
				return true;
			}
		});

		populateEffects();
	}

	public void populateEffects() {
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
		eList.add("+");
	}

	/**
	 * Enum conversions for rEffects in the list
	 * 
	 * @param type
	 * @return int The type of rEffect
	 */
	public int convertTypeToEEnum(String type) {
		if (type.equals("notification"))
			return 0;
		if (type.equals("sound"))
			return 1;
		if (type.equals("ringer"))
			return 2;
		if (type.equals("toast"))
			return 3;
		if (type.equals("vibrate"))
			return 4;
		return -1;
	}

	public void dialogDelete(FragmentActivity activity, DialogFragment dialog) {
		mCallback = activity;
		app = (CEapp) mCallback.getApplication();
		DatabaseHandler db = new DatabaseHandler(mCallback);
		if (!app.editType) // effect
		{
			db.deleteREffect(db.getREffectByID(app.editedNumber));
		}
		app.currentRule.setREffects(db.getAllREffects(app.currentRule.getID()));
		db.close();
	}
}
