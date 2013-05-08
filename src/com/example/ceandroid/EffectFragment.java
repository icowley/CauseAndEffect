package com.example.ceandroid;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class EffectFragment extends ListFragment {

	/**
	 * CEapp contains the globally accessible variables
	 */
	private CEapp app = (CEapp) getActivity().getApplication();
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		eA = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, eList);
		setListAdapter(eA);
		getListView().setOnItemClickListener(new OnItemClickListener() {
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
					Intent myIntent = new Intent(EffectFragment.this
							.getActivity(), EffectView.class)
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
						Toast.makeText(EffectFragment.this.getActivity(),
								"Cannot edit vibrate effects",
								Toast.LENGTH_LONG).show();
					} else {
						Intent myIntent = new Intent(EffectFragment.this
								.getActivity(), EffectView.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(myIntent);
					}
				}
			}
		});
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
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
				if (size == (arg2 + 1)) {
					// do nothing, '+' was selected
				} else {
					app.editType = false;
					app.editedNumber = app.currentRule.getREffects().get(arg2)
							.getID();
					DialogFragment newFragment = new DeleteDialogFragment();
					newFragment.show(getFragmentManager(), "delete");
				}
				return true;
			}
		});
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
}
